/*
 * Created on Sep 13, 2005
 */
package edu.muohio.csa.autograder.framework;

/**
 * Application level exception
 * @author mhelmick
 *
 */
public class GradingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3901097520908353515L;

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