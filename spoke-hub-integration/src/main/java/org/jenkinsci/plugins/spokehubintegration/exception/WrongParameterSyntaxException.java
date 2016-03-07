package org.jenkinsci.plugins.spokehubintegration.exception;

/**
 * Class that extends {@link Exception} class and models the insertion of a parameter
 * whose syntax is wrong.
 * 
 * @author Tommaso Montingelli
 *
 */
@SuppressWarnings("serial")
public class WrongParameterSyntaxException extends Exception {
	
	public WrongParameterSyntaxException() {
		super();
	}
	
	public WrongParameterSyntaxException(String message) {
		super(message);
	}

}
