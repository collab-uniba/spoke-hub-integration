package org.jenkinsci.plugins.spokehubintegration.command;

import org.jenkinsci.plugins.spokehubintegration.SlackData;
import org.jenkinsci.plugins.spokehubintegration.SlackMessage;

/**
 * Command interface for command execution.
 * 
 * @author Tommaso Montingelli
 *
 */
@FunctionalInterface
public interface Command {
	
	/**
	 * Executes the command.
	 * 
	 * @param data data sent by Slack
	 * @return response to the requested command
	 */
	SlackMessage execute(SlackData data);

}
