/*
 * Created on Sep 13, 2005
 */
package edu.muohio.csa.autograder;

import edu.muohio.csa.autograder.framework.GradingException;

public class TestResult {

	private String studentId = "";
	private String className = "";
	private String testName = "";
	private boolean passed = false;
	
	private long startTime = 0;
	private long endTime = 0;
	
	private GradingException gradingException = null;
	
	public TestResult( String studentId, String className, String testName, boolean passed ) {
		this.studentId = studentId;
		this.className = className;
		this.testName = testName;
		this.passed = passed;
	}
	
	public void start() {
		startTime = System.currentTimeMillis();
		endTime = startTime;
	}
	
	public void end() {
		endTime = System.currentTimeMillis();
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public GradingException getGradingException() {
		return gradingException;
	}

	public void setGradingException(GradingException gradingException) {
		this.gradingException = gradingException;
		setPassed( false );
	}
	
	public String getElapsedTime() {
		long seconds = (endTime - startTime) / 1000;
		long ms = (endTime - startTime) % 1000;
		String elapsed = seconds + "." + ms + " seconds";
		return elapsed;
	}
	
	
	
}
