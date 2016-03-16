package org.jenkinsci.plugins.spokehubintegration;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Class that starts a new {@link Thread} that analyzes the request sent by Slack.
 * 
 * @author Tommaso Montingelli
 *
 */
public class CommandThread extends Thread {
	
	private static final Logger LOGGER = Logger.getLogger(CommandThread.class.getName());
	private SlashCommandGlobalConfiguration slashCommandConfiguration;
	private SlackData data;
	
	/**
	 * Initializes a {@link SlashCommandGlobalConfiguration} object and stores the data 
	 * sent by Slack.
	 * 
	 * @param slashCommandConfiguration slash command setup
	 * @param request request sent by Slack
	 */
	public CommandThread(SlashCommandGlobalConfiguration slashCommandConfiguration, StaplerRequest request) {
		this.slashCommandConfiguration = slashCommandConfiguration;
		this.data = new SlackData();
		request.bindParameters(this.data);
	}
	
	@Override
	public void run() {
		SlackMessage message = processRequest();
		if (message.getText() != null && message.getColor() != null) {
			JSONResponse response = new JSONResponse(message, StaplerResponse.SC_OK);
			response.sendResponse(this.data.getResponse_url());
		}
	}

	/**
	 * Processes the request received.
	 * 
	 * @return response to the request
	 */
	private SlackMessage processRequest() {
		String message;
		// checks if jenkins was restarted
		if (this.slashCommandConfiguration == null) {
			message = Messages.restartJenkins();
			LOGGER.log(Level.SEVERE, message);
            return new SlackMessage(message, Messages.danger());
		}
		
		String slackSlashCommandToken = this.slashCommandConfiguration.getSlackSlashCommandToken();
		// check if the token is set
		if (slackSlashCommandToken == null || slackSlashCommandToken.isEmpty()) {
			message = Messages.tokenNotSet();
			LOGGER.log(Level.SEVERE, message);
			return new SlackMessage(message, Messages.danger());
        }
		
		// check if the token is correct
		if (!slackSlashCommandToken.equals(this.data.getToken())) {
			message = Messages.invalidToken();
			LOGGER.log(Level.SEVERE, message);
			return new SlackMessage(message, Messages.danger());
		}
		
		Controller controller = CommandController.getInstance();
		return (SlackMessage) controller.handleData(this.data);
	}

}
