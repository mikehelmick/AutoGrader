/*
 * Created on Sep 13, 2005
 */
package edu.muohio.csa.autograder.framework;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import edu.muohio.csa.autograder.StudentRecord;



/**
 * Gradable classes must extend this class.
 * 
 * any void method shose name starts with "grade" will be graded
 * 
 * project: AutoGrader
 * package: edu.muohio.csa.autograder.framework
 * 
 * @author mhelmick
 * @version $Id$
 */
public abstract class Graded extends Observable {
	
	public static final String METHOD_PREFIX = "grade";
	
	private String underTest = null;
	
	private List<Method> gradeMethods = null;
	
	private int testsRun = 0;
	private int testsPassed = 0;
	
	private StudentRecord studentRecord = null;
	
	public Graded() {
	}
	
	public List<Method> getMethodsUnderTest() {
		initializeObject();
		return gradeMethods;
	}
	
	private void initializeObject() {
		if ( gradeMethods == null ) {
			gradeMethods = new ArrayList<Method>();
			Method[] methods = this.getClass().getMethods();
			for( int i = 0; i < methods.length; i++ ) {
				if ( ! methods[i].getName().equalsIgnoreCase( "gradeObject" ) &&
					 methods[i].getParameterTypes().length == 0 &&
					 (methods[i].getName().length() > METHOD_PREFIX.length() &&
					  methods[i].getName().substring(0,METHOD_PREFIX.length()).equalsIgnoreCase( METHOD_PREFIX ))  ) {
					gradeMethods.add( methods[i] );
				}
			}
		}
	}

	
	@SuppressWarnings("unchecked")
	protected final <T> T getInstanceOfObject( Class<? extends T> tClass ) throws GradingException {
		T rtnObj = null;
		
		try {
			Class clazz = Class.forName( underTest );
			
			rtnObj = (T) clazz.newInstance();
			
		} catch( ClassNotFoundException ex ) {
			throw new GradingException( this.underTest + " could not be instantiated.", ex );
		} catch( ClassCastException ex ) {
			throw new GradingException( this.underTest + " coult not be upcast to the correct type: " + tClass.getName(), ex );
		} catch (InstantiationException ex ) {
			throw new GradingException( this.underTest + " could not be instantiated.", ex );
		} catch (IllegalAccessException ex ) {
			throw new GradingException( this.underTest + " could not be instantiated.", ex );
		}
		
		return rtnObj;
	}
	
	public final Method getSetupMethod() throws NoSuchMethodException {
		return this.getClass().getMethod( "setUp", (Class[])null );
	}
	
	public final Method getTearDownMethod() throws NoSuchMethodException {
		return this.getClass().getMethod( "tearDown", (Class[])null );
	}
	
	public final String getUnderTest() {
		return underTest;
	}
	
	public void setUp() {
	}
	
	public void tearDown() {
	}
	
	public void methodRun( boolean passed ) {
		this.testsRun++;
		if ( passed ) {
			this.testsPassed++;
		}
		setChanged();
		notifyObservers();
	}
	
	private void throwGradingException( String got, String expected, String message ) throws GradingException {
		throw new GradingException( message + "  got " + got + " expected " + expected );
	}
	
	protected void assertTrue( boolean bool, String message ) throws GradingException {
		if ( !bool ) {
			throwGradingException( new Boolean( bool ).toString(), Boolean.TRUE.toString(), message );
		}
	}
	
	protected void assertFalse( boolean bool, String message ) throws GradingException {
		if ( bool ) {
			throwGradingException( new Boolean( bool ).toString(), Boolean.FALSE.toString(), message );
		}
	}
	
	protected void assertEquals( String value, String expected ) throws GradingException {
		assertEquals( value, expected, "" );
	}
	
	protected void assertEquals( String value, String expected, String message ) throws GradingException {
		if ( ! expected.equals( value ) ) {
			throwGradingException( value, expected, message );
		}
	}
	
	protected void assertEquals( Long value, Long expected ) throws GradingException {
		assertEquals( value, expected, "" );
	}
	
	protected void assertEquals( Long value, Long expected, String message ) throws GradingException {
		if ( ! expected.equals( value ) ) {
			throwGradingException( value.toString(), expected.toString(), message );
		}
	}
	
	protected void assertEquals( Double value, Double expected, Double variance ) throws GradingException {
		assertEquals( value, expected, variance, "" );
	}
	
	protected void assertEquals( Double value, Double expected, Double variance, String message ) throws GradingException {
		if ( ! ( value < expected + variance && value > expected - variance ) ) {
			throwGradingException( value.toString(), expected.toString(), message );
		}
	}
	
	protected void assertEquals( Comparable value, Comparable expected ) throws GradingException {
		assertEquals( value, expected, "" );
	}
	
	protected void assertEquals( Object value, Object expected, String message ) throws GradingException {
		if ( ! expected.equals( value ) ) {
			throwGradingException( value.toString(), expected.toString(), message );
		}
	}
	
	protected void fail() throws GradingException {
		throw new GradingException("unspecified failure");
	}
	
	protected void fail( String message ) throws GradingException {
		throw new GradingException( message );
	}
	
	public void setStudentRecord( StudentRecord studentRecord ) {
		setChanged();
		notifyObservers( Boolean.TRUE ); // send a synchronous notify event
		
		this.studentRecord = studentRecord;
		testsRun = 0;
		testsPassed = 0;
		
		setChanged();
		notifyObservers();
	}

	public StudentRecord getStudentRecord() {
		return studentRecord;
	}

	public int getTestsPassed() {
		return testsPassed;
	}

	public int getTestsRun() {
		return testsRun;
	}

	public int getTotalMethods() {
		initializeObject();
		return gradeMethods.size();
	}

	public void setUnderTest(String underTest) {
		this.underTest = underTest;
	}
	
	

}
