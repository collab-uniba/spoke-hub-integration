package org.jenkinsci.plugins.spokehubintegration.command;

import org.jenkinsci.plugins.spokehubintegration.SlackData;
import org.jenkinsci.plugins.spokehubintegration.SlackMessage;

/**
 * Class that implements {@link Command} interface and identifies the command 'test'.
 * 
 * @author Tommaso Montingelli
 *
 */
public class TestCommand implements Command {
	
	private JenkinsReceiver receiver;

	/**
	 * Initializes a newly created {@link JenkinsReceiver} object.
	 * 
	 * @param receiver object that performs the execution of the commands
	 */
	public TestCommand(JenkinsReceiver receiver) {
		this.receiver = receiver;
	}

	@Override
	public SlackMessage execute(SlackData data) {
		return this.receiver.test(data);
	}

}
