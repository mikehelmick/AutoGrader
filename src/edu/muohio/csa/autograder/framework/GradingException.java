/*
 * Created on Sep 13, 2005
 */
package edu.muohio.csa.autograder.framework;

public class GradingException extends Exception {

	public GradingException() {
		super();
	}

	public GradingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public GradingException(String arg0) {
		super(arg0);
	}

	public GradingException(Throwable arg0) {
		super(arg0);
	}
	
}