/*
 * Created on Sep 13, 2005
 */
package edu.muohio.csa.autograder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentRecord {

	private String studentId;
	private String studentName;
	private Map<String,List<TestResult>> gradingResults = new HashMap<String,List<TestResult>>();
	
	public StudentRecord( String studentId, String studentName ) {
		this.studentId = studentId;
		this.studentName = studentName;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	
	public Map<String,List<TestResult>> getGradingResults() {
		return gradingResults;
	}
	
	public void setTestResults( String test, List<TestResult> results ) {
		gradingResults.put( test, results );
	}
	
}
