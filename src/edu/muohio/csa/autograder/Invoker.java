package edu.muohio.csa.autograder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import edu.muohio.csa.autograder.framework.Graded;
import edu.muohio.csa.autograder.framework.GradingException;

public class Invoker implements Runnable {

	private TestResult result;
	private Method method;
	private Graded graded;
	
	private boolean passed = true;
	
	public Invoker( TestResult result, Method method, Graded graded ) {
		this.result = result;
		this.method = method;
		this.graded = graded;
	}
	
	public void run() {
		try {
			method.invoke( graded, (Object[])null );
		} catch ( Throwable t ) {
			Throwable workOn = t;
			if ( t instanceof InvocationTargetException ) {
				workOn = ((InvocationTargetException)t).getTargetException(); 
			}
			
			if ( workOn instanceof GradingException ) {
				result.setGradingException( (GradingException) workOn );
			} else {
				result.setGradingException( new GradingException(workOn) );
			}
			passed = false;
		}	
	}
	
	public boolean isPassed() {
		return passed;
	}
	
	
}
