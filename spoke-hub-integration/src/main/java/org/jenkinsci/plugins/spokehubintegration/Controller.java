package org.jenkinsci.plugins.spokehubintegration;


/**
 * Receives requests from {@link SlackWebhookEndpoint} and delegates the execution of 
 * commands to the classes to which they are associated.
 * 
 * @author Tommaso Montingelli
 *
 */
@FunctionalInterface
public interface Controller {

	/**
	 * Performs a specific request.
	 * 
	 * @param data - content of the HTTP POST request
	 * @return response to the requested command
	 */
	Object handleData(SlackData data);

}