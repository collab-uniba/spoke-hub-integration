package org.jenkinsci.plugins.spokehubintegration;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.interceptor.RequirePOST;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;

/**
 * Class that implements {@link UnprotectedRootAction} interface, receives the 
 * HTTP POST requests sent by Slack and forwards the content to the 
 * {@link CommandController}.
 * 
 * @author Tommaso Montingelli
 *
 */
@Extension
public class SlackWebhookEndpoint implements UnprotectedRootAction {
	
	private static final Logger LOGGER = Logger.getLogger(SlackWebhookEndpoint.class.getName());
	private SlashCommandGlobalConfig slashCommandConfiguration;
    
	/**
	 * Initializes a {@link SlashCommandGlobalConfig} object.
	 */
    public SlackWebhookEndpoint() {
    	this.slashCommandConfiguration = SlashCommandGlobalConfig.getInstance();
	}

	@Override
	public String getIconFileName() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public String getUrlName() {
		return Messages.urlName();
	}
	
	/**
	 * Receives the HTTP POST requests sent by Slack.
	 * 
	 * @param request - request
	 * @return response to the request
	 */
	@RequirePOST
    public HttpResponse doIndex(StaplerRequest request) {
		String message;
		// checks if jenkins was restarted
		if (this.slashCommandConfiguration == null) {
			message = Messages.restartJenkins();
			LOGGER.log(Level.INFO, message);
            return new JSONResponse(new SlackMessage(message, Messages.danger()), 
            		StaplerResponse.SC_OK);
		}
		
		String slackSlashCommandToken = this.slashCommandConfiguration.getSlackSlashCommandToken();
		// check if the token is set
		if (slackSlashCommandToken == null || slackSlashCommandToken.isEmpty()) {
			message = Messages.tokenNotSet();
			LOGGER.log(Level.INFO, message);
            return new JSONResponse(new SlackMessage(message, Messages.danger()), 
            		StaplerResponse.SC_OK);
        }
		
		SlackData data = new SlackData();
		request.bindParameters(data);
		// check if the token is correct
		if (!slackSlashCommandToken.equals(data.getToken())) {
			message = Messages.invalidToken();
			LOGGER.log(Level.INFO, message);
            return new JSONResponse(new SlackMessage(message, Messages.danger()), 
            		StaplerResponse.SC_OK);
		}
		
		Controller controller = CommandController.getInstance();
		return (JSONResponse) controller.handleData(data);
	}

}
