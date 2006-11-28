/*
 * Created on Sep 13, 2005
 */
package edu.muohio.csa.autograder;

import edu.muohio.csa.autograder.framework.Graded;

/**
 * A GradingPackage is an object that holds a single Graded object (Graded objects are created by the user). <br/>
 * <br/>
 * The Grading package simply carries information used by the GradingSession
 * 
 * @author Mike Helmick
 *
 */
public class GradingPackage {
	
	private String gradingSetName;
	
	private String instanciatedClassName = "";
	
	private boolean run = false;
	private String status = "";
	private boolean failed = false;
	
	private Graded graded = null;
	
	public GradingPackage( Graded graded, String gradingSetName, String instanciatedClassName ) {
		this.graded = graded;
		this.gradingSetName = gradingSetName;
		this.instanciatedClassName = instanciatedClassName;
	}
	
	public String getGradingSetName() {
		return gradingSetName;
	}
	
	public Graded getGradedInstance() throws IllegalAccessException, InstantiationException {
		return graded;
	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isFailed() {
		return failed;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	public String getInstanciatedClassName() {
		return instanciatedClassName;
	}

	public void setInstanciatedClassName(String instanciatedClassName) {
		this.instanciatedClassName = instanciatedClassName;
	}

	
	
	
}
