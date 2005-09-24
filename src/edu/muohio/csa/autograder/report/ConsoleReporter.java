/*
 * Created on Sep 19, 2005
 */
package edu.muohio.csa.autograder.report;

import java.io.OutputStream;

public class ConsoleReporter extends Reporter {

	@Override
	public void closeStream(OutputStream stream) {
		System.out.println("=======================================" );
	}

	@Override
	public OutputStream getStream(String studentId) {
		System.out.println( "---- STUDENT ID = " + studentId + " ----" );
		return System.out;
	}


}
