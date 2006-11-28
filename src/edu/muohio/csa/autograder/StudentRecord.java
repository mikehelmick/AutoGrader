/*
 * Created on Sep 13, 2005
 */
package edu.muohio.csa.autograder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing an individual student
 * @author mhelmick
 *
 */
public class StudentRecord {

	private String studentId;
	private String studentName;
	private Map<String,List<TestResult>> gradingResults = new LinkedHashMap<String,List<TestResult>>();
	
	private StudentRecord() {}
	
	/**
	 * 
	 * @param studentId - student ID - this is the package name used, so it must be the complete package name
	 * @param studentName - Text name of the student - for nice reporting :)
	 */
	public StudentRecord( String studentId, String studentName ) {
		this.studentId = studentId;
		this.studentName = studentName;
	}

	public String getStudentId() {
		return studentId;
	}

	public String getStudentName() {
		return studentName;
	}
	
	/**
	 * Returns a copy of the grading results for this student. <br/>
	 * The copy is a deep copy, and no changes to the returned map take effect here.
	 * @return
	 */
	public Map<String,List<TestResult>> getGradingResults() {
		Map<String,List<TestResult>> rtn = new LinkedHashMap<String,List<TestResult>>();
		
		for( String key : gradingResults.keySet() ) {
			List<TestResult> value = new ArrayList<TestResult>();
			for( TestResult tr : gradingResults.get(key) ) {
				value.add( new TestResult( tr ) );
			}
			rtn.put( key, value );
		}
		
		return rtn;
	}
	
	/**
	 * Package level, results are only set by the grading session
	 */
	void setTestResults( String test, List<TestResult> results ) {
		gradingResults.put( test, results );
	}
	
}
