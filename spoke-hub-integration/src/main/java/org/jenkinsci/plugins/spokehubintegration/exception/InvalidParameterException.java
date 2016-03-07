package org.jenkinsci.plugins.spokehubintegration.exception;

/**
 * Class that extends {@link Exception} class and models the insertion of a parameter
 * that does not exist.
 * 
 * @author Tommaso Montingelli
 *
 */
@SuppressWarnings("serial")
public class InvalidParameterException extends Exception {
	
	public InvalidParameterException() {
		super();
	}
	
	public InvalidParameterException(String message) {
		super(message);
	}

}
