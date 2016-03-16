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
	
	/*
	 * NOTE: @Extension annotation must be placed to let Jenkins know about this extension
	 * 
	 * NOTE: slashCommandConfiguration variable must be instantiated in this class
	 */
	
	private static final Logger LOGGER = Logger.getLogger(SlackWebhookEndpoint.class.getName());
	private SlashCommandGlobalConfiguration slashCommandConfiguration;
    
	/**
	 * Initializes a {@link SlashCommandGlobalConfiguration} object.
	 */
    public SlackWebhookEndpoint() {
    	this.slashCommandConfiguration = SlashCommandGlobalConfiguration.getInstance();
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
	 * @param request request
	 * @return response to the request
	 */
	@RequirePOST
    public HttpResponse doIndex(StaplerRequest request) {
		CommandThread thread = new CommandThread(this.slashCommandConfiguration, request);
		thread.start();
		
		String message = Messages.requestReceived();
		LOGGER.log(Level.INFO, message);
		return new JSONResponse(new SlackMessage(message, Messages.good()), 
				StaplerResponse.SC_OK);
	}

}
