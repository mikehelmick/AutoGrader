/*
 * Created on Sep 14, 2005
 */
package edu.muohio.csa.autograder.ui;

import java.util.List;
import java.util.Map;

import edu.muohio.csa.autograder.StudentRecord;
import edu.muohio.csa.autograder.TestResult;



public class TextUI extends UI {

	public TextUI() {
		super();
	}

	@Override
	protected void gradedChanged() {
		System.out.println("Graded object updated:");
		System.out.println( "\t" + graded.getTestsPassed() + "/" +
				                   graded.getTestsRun() +  
				                   " (" + graded.getTotalMethods() + " total)" );
		
		StudentRecord sr = graded.getStudentRecord();
		if ( sr != null ) {
			System.out.println( "\tstudentId=" + sr.getStudentId() + " " +
					            "name=" + sr.getStudentName() );
			System.out.println( "\ttestig class=" + graded.getUnderTest() );
	
			Map<String,List<TestResult> > resultMap = sr.getGradingResults();
			String pkgName = session.getGradingPackages().get( session.getPackageIndex() ).getGradingSetName();
			List<TestResult> results = resultMap.get( pkgName );
			if ( results != null ) {
				for( TestResult res : results ) {
					System.out.println( "\t" + res.getTestName() + "  passed=" + res.isPassed() );
					if ( ! res.isPassed() ) {
						System.out.println( "\t\tex=" + res.getGradingException().getMessage() );
					}
					System.out.println( "\t\ttime=" + res.getElapsedTime() );
				}
			}
		}
	
	}

	@Override
	protected void gradingSessionChanged() {
		System.out.println("Grading session changed:");
		
		//GradingPackage curPkg = gradingSession.getGradingPackages().get( gradingSession.getPackageIndex() );
		//System.out.println( "\tGrading set: " + curPkg.getGradingSetName() );
		
		//StudentRecord student = gradingSession.getStudents().get( gradingSession.getStudentRecord() );
		
		//System.out.println( "\tLast test result:" );
		//List<TestResult> results = student.getGradingResults().get( curPkg.getGradingSetName() );
		//TestResult result = results.get( graded.getTestsRun() );
		//System.out.println( "\t\t " + result.getTestName() + " passed=" + result.isPassed() + " time=" + result.getElapsedTime() );
		
	}

	
	
}
