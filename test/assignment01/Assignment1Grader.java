/*
 * Created on Sep 14, 2005
 */
package assignment01;

import edu.muohio.csa.autograder.framework.Graded;
import edu.muohio.csa.autograder.framework.GradingException;

public class Assignment1Grader extends Graded {

	@SuppressWarnings("unchecked")
	public void grade_Test_Return1() throws GradingException {
		
		Assignment1<String> testClass = (Assignment1<String>) this.getInstanceOfObject( Assignment1.class );
		
		assertEquals("String", testClass.getE( "String" ) );
		
		assertEquals( 1, testClass.return1(), "return 1 didn't return 1" );
	}
	
	public void grade_Test_Return1_sleep() throws GradingException {
		
		//Assignment1 testClass = 
		this.getInstanceOfObject( Assignment1.class );
		
	}
	
	@SuppressWarnings("unchecked")
	public void grade_with_param_constructor() throws GradingException {
		
		Assignment1<String> testClass = (Assignment1<String>) this.getInstanceOfObject( Assignment1.class, "text" );
		
		assertEquals( testClass.getItem(), "text" );
		
	}
	
}
