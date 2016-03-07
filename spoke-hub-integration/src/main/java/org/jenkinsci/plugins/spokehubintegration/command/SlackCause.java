package org.jenkinsci.plugins.spokehubintegration.command;

import hudson.model.Cause;

/**
 * This class is used to keep track of why a given build was started.
 * 
 * @author Tommaso Montingelli
 *
 */
public class SlackCause extends Cause {

	private String username;

	/**
	 * Stores the username of the Slack user who made the request.
	 * 
	 * @param username - username of the Slack user
	 */
    public SlackCause(String username) {
        this.username = username;
    }

    @Override
    public String getShortDescription() {
        return Messages.slackCause(this.username);
    }

}
