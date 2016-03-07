package org.jenkinsci.plugins.spokehubintegration.exception;

/**
 * Class that extends {@link Exception} class and models the insertion of a test scope
 * that does not exist.
 * 
 * @author Tommaso Montingelli
 *
 */
@SuppressWarnings("serial")
public class WrongTestScopeException extends Exception {
	
	public WrongTestScopeException() {
		super();
	}
	
	public WrongTestScopeException(String message) {
		super(message);
	}

}
