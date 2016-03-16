package org.jenkinsci.plugins.spokehubintegration.command;

import org.jenkinsci.plugins.spokehubintegration.SlackData;
import org.jenkinsci.plugins.spokehubintegration.SlackMessage;

/**
 * Class that requires the execution of a command.
 * 
 * @author Tommaso Montingelli
 *
 */
public class JenkinsInvoker {
	
	private Command command;
	
	/**
	 * Instantiates the requested command.
	 * 
	 * @param command requested command
	 */
	public JenkinsInvoker(Command command) {
		this.command = command;
	}
	
	/**
	 * Starts a build for any type of job.
	 * 
	 * @param data data sent by Slack
	 * @return response to the requested command
	 */
	public SlackMessage build(SlackData data) {
		return this.command.execute(data);
	}
	
	/**
	 * Performs tests for a maven job.
	 * 
	 * @param data data sent by Slack
	 * @return response to the requested command
	 */
	public SlackMessage test(SlackData data) {
		return this.command.execute(data);
	}
	
	/**
	 * Lists all jobs.
	 * 
	 * @param data data sent by Slack
	 * @return response to the requested command
	 */
	public SlackMessage listJobs(SlackData data) {
		return this.command.execute(data);
	}
	
	/**
	 * Lists all the available commands or a detailed description of single command.
	 * 
	 * @param data data sent by Slack
	 * @return response to the requested command
	 */
	public SlackMessage help(SlackData data) {
		return this.command.execute(data);
	}

}
