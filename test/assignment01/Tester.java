/*
 * Created on Sep 14, 2005
 */
package assignment01;

import edu.muohio.csa.autograder.GradingPackage;
import edu.muohio.csa.autograder.GradingSession;
import edu.muohio.csa.autograder.StudentRecord;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		GradingSession session = new GradingSession();
		session.addTextUI();
		session.addConsoleReporter();
		try {
			session.addFileReporter( "/Users/mhelmick/Desktop/GRADES" );
		} catch ( Exception ex ) {}
			
		GradingPackage asgn1Pkg = new GradingPackage( new Assignment1Grader(), "assignment1", "MyAssignment1" );
		session.addPackage( asgn1Pkg );
		
		session.addStudent( new StudentRecord( "student01", "Test Student" ) );
		session.addStudent( new StudentRecord( "student02", "J.D." ) );
		session.addStudent( new StudentRecord( "student03", "Slacker" ) );
		 
		session.run();
		
		System.out.println("DONE");
	}

}
