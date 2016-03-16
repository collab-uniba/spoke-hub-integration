package org.jenkinsci.plugins.spokehubintegration.command;

import org.jenkinsci.plugins.spokehubintegration.SlackData;
import org.jenkinsci.plugins.spokehubintegration.SlackMessage;

/**
 * Class that implements {@link Command} interface and identifies the command 'help'.
 * 
 * @author Tommaso Montingelli
 *
 */
public class HelpCommand implements Command {
	
	private JenkinsReceiver receiver;

	/**
	 * Initializes a newly created {@link JenkinsReceiver} object.
	 * 
	 * @param receiver object that performs the execution of the commands
	 */
	public HelpCommand(JenkinsReceiver receiver) {
		this.receiver = receiver;
	}

	@Override
	public SlackMessage execute(SlackData data) {
		return this.receiver.help(data);
	}

}
