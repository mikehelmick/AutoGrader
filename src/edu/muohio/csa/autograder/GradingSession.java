/*
 * Created on Sep 13, 2005
 */
package edu.muohio.csa.autograder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import edu.muohio.csa.autograder.classpath.DynamicLoader;
import edu.muohio.csa.autograder.framework.Graded;
import edu.muohio.csa.autograder.framework.GradingException;
import edu.muohio.csa.autograder.report.ConsoleReporter;
import edu.muohio.csa.autograder.report.FileReporter;
import edu.muohio.csa.autograder.report.Reporter;
import edu.muohio.csa.autograder.style.AutoGradePMD;
import edu.muohio.csa.autograder.style.StyleException;
import edu.muohio.csa.autograder.ui.TextUI;
import edu.muohio.csa.autograder.ui.UI;

/**
 * MAnages a complete grading session.   In order to test student code, you
 * will create a grading session, add grading pacakges to it, and execute the session
 * as shown below.
 * 
 * <code>
 * GradingSession session = new GradingSession();
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
	</code>
 * 
 * @author Mike Helmick
 *
 */
public class GradingSession extends Observable implements Runnable {
	
	private List<StudentRecord> students = new ArrayList<StudentRecord>();
	private List<GradingPackage> packages = new ArrayList<GradingPackage>();
	
	private Set<Observer> observers = new HashSet<Observer>();
	private List<Reporter> reporters = new ArrayList<Reporter>();
	
	public boolean running = false;
	public int studentRecord = 0;
	public int packageIndex = 0;
	
	private int threadTime = 60;
	
	private String pmdPath = null;
	
	public GradingSession() {	
	}
	
	/**
	 * Set the time limit for student method invocations.   This limit has a default value of 60 seconds.
	 * @param threadTime - Execution time to allow in seconds, must be 1 or higher.   
	 */
	public void setThreadTime( int threadTime ) {
		if ( threadTime >= 1 ) {
			this.threadTime = threadTime;
		} else {
			throw new IllegalArgumentException("Time must be greater than or equal to 1 second");
		}
	}
	
	/**
	 * Used to add items to the classpath if necessary.
	 * @param dirPath
	 * @throws IOException
	 */
	public void addStudentClassPath( String dirPath ) throws IOException {
		DynamicLoader.addFile( dirPath );
	}
	
	
	private boolean observersContains( Class clazz ) {
		boolean contains = false;
		for( Observer ob : observers ) {
			if ( clazz.isInstance( ob ) ) {
				contains = true;
				break;
			}
		}
		
		return contains;
	}
	
	/**
	 * Add a text user interface to the grading session.  <br/>
	 * <i>If you do not add this, you won't see any output.</i>
	 */
	public void addTextUI() {
		if ( ! observersContains( TextUI.class ) ) {
			this.addObserver( new TextUI() );
		}
	}
	
	/**
	 * Add a console reporter - provides up to date information to the console.
	 */
	public void addConsoleReporter() {
		reporters.add( new ConsoleReporter() );
	}
	
	/**
	 * Diasable the console repoter.
	 *
	 */
	public void removeConsoleReporter() {
		removeInstanceOf( ConsoleReporter.class );
	}
	
	/**
	 * Has no effect
	 * @deprecated
	 */
	public void addPrinterReporter() {
		//reporters.add( new PrinterReporter() );
	}

	/**
	 * Has no effect
	 * @deprecated
	 */
	public void removePrinterReporter() {
		//removeInstanceOf( PrinterReporter.class );
	}
	
	/**
	 * Adds a file reporter - this will write a report for each student to the directory specified.</br>
	 * The directory must exist.
	 * @param dir
	 * @throws IOException if the directory string passed in does not resolve to a directory
	 */
	public void addFileReporter( String dir ) throws IOException {
		reporters.add( new FileReporter( dir ) );
	}
	
	/**
	 * Disable the file reporter 
	 */
	public void removeFileReporter() {
		removeInstanceOf( FileReporter.class );
	}
	
	public void removeInstanceOf( Class clazz ) {
		Iterator<Reporter> iter = reporters.iterator();
		while( iter.hasNext() ) {
			Reporter rpt = iter.next();
			if ( clazz.isInstance( rpt ) ) {
				iter.remove();
			}
		}
	}
		
	/**
	 * These pass-through methods are around just so we can pass
	 * the observers on to the graded instaces as they are instanciated
	 */
	@Override
	public synchronized void addObserver(Observer arg0) {
		super.addObserver(arg0);		
		observers.add( arg0 );
	}

	@Override
	public synchronized void deleteObserver(Observer arg0) {
		super.deleteObserver(arg0);
		observers.remove( arg0 );
	}


	/**
	 * Execute the grading session.  The last step after it has been set up.
	 */
	@SuppressWarnings({"unchecked","deprecation"})
	public void run() {
		setChanged();
		notifyObservers();
		
		// lets us grade multiple packages in one session
		for( GradingPackage thisPkg : packages ) {
			
			try {
				// instanciate the tester class
				Graded graded = thisPkg.getGradedInstance();
				for( Observer observer : observers ) {
					graded.addObserver( observer );
				}
				
				// for each student to grade
				for( StudentRecord student : students ) {
					// set our current student
					graded.setStudentRecord( student );
					
					List<TestResult> studentResults = new ArrayList<TestResult>();
					TestResult curTestResult = null;
					try {
						
						// get the object to test
						String className = student.getStudentId() + "." + thisPkg.getInstanciatedClassName();
						graded.setUnderTest( className );
						
						List<Method> testMethods = graded.getMethodsUnderTest();;
						// for each methods under test
						for( Method method : testMethods ) {
							curTestResult = new TestResult( student.getStudentId(), 
									                        thisPkg.getInstanciatedClassName(), 
									                        method.getName(), 
									                        true );
							
							boolean passed = true;
							try {
								// invoke the test method
								curTestResult.start();
								graded.getSetupMethod().invoke( graded, (Object[]) null );
								
								Invoker invoker = new Invoker( curTestResult, method, graded );
								Thread invokerThread = new Thread(invoker, "invoker");
								invokerThread.start();
								
								try { Thread.sleep(100); } catch ( Exception ex ) {}
								
								int secondsRemaining = threadTime;
								while( secondsRemaining > 0 && invokerThread.isAlive() ) {
									try {
										Thread.sleep(1000);
									} catch ( Exception ex ) {}
									secondsRemaining --;
									if ( secondsRemaining > 20 && secondsRemaining % 10 == 0 ) {
										System.out.print( secondsRemaining + " " );
									} else if ( secondsRemaining <= 20 ) {
										System.out.print( secondsRemaining + " " );
									}
									
								}
								
								if ( invokerThread.isAlive() ) {
									// need to kill
									int count = 3;
									while( invokerThread.isAlive() && count > 0 ) {
										System.out.println("\n ** INTERRUPTING METHOD EXECUTION **");
										try {
											invokerThread.interrupt();
											Thread.sleep(100);
										} catch ( Exception ex ) {
										}
										count--;
									}
									try {
										if ( invokerThread.isAlive() ) {
											invokerThread.stop();
										}
									} catch ( Exception ex ) {
										System.err.println("ERROR _ CAN NOT STOP RUNAWAY METHOD, leaving it..");
									}
								}
								
								passed = invoker.isPassed();
								
								graded.getTearDownMethod().invoke( graded, (Object[])null );
							} catch ( Throwable t ) {
								Throwable workOn = t;
								if ( t instanceof InvocationTargetException ) {
									workOn = ((InvocationTargetException)t).getTargetException(); 
								}
								
								if ( workOn instanceof GradingException ) {
									curTestResult.setGradingException( (GradingException) workOn );
								} else {
									curTestResult.setGradingException( new GradingException(workOn) );
								}
								passed = false;
								
							} finally {
								curTestResult.end();
								studentResults.add( curTestResult );
								
								graded.methodRun( passed );
							}
							
						}
						
						
					} catch ( Throwable t ) {
						// if we get here then there was something majorly wrong
						if ( curTestResult != null ) {
							curTestResult.setPassed( false );
							curTestResult.setGradingException( new GradingException( t ) );
						} else {
							curTestResult = new TestResult( student.getStudentId(), 
			                        					       thisPkg.getInstanciatedClassName(), 
			                        					       "GENERAL FAILURE", 
			                        					       false );
			                 curTestResult.setGradingException( new GradingException( t ) );
						}
					} finally {
						student.setTestResults( thisPkg.getGradingSetName(), studentResults );
						
						setChanged();
						notifyObservers();
					}
					
					studentRecord++;
					setChanged();
					notifyObservers( Boolean.TRUE );
				} // end for each student loop
				
			} catch ( Exception ex ) {
				thisPkg.setRun( false );
				thisPkg.setFailed( true );
				thisPkg.setStatus( ex.getMessage() );
			}
			
			packageIndex++;
			studentRecord = 0;
			setChanged();
			notifyObservers( Boolean.TRUE );
		}
		
		// PMD
		if ( pmdPath != null ) {
			AutoGradePMD agPMD = new AutoGradePMD();
			for( StudentRecord student : students ) {
				// set our current student
				List<TestResult> trResults  = agPMD.checkJavaFiles( pmdPath, student.getStudentId() );
				student.setTestResults("PMD-STYLE", trResults );
			}
		}
		
		// exiting
		for( Observer ob : observers ) {
			if ( ob instanceof UI ) {
				((UI)ob).shutdown(); 
			}
		}
		
		// report
		for( Reporter reporter : reporters ) {
			reporter.writeReport( students );
		}
	}
	
	/**
	 * Add a student to be graded
	 * @param student
	 */
	public void addStudent( StudentRecord student ) {
		students.add( student );
	}
	
	/**
	 * Add a grading package<br/>
	 * The same package can be added multiple times.  We do this when student implement the same interfaces several times.
	 * @param pkg
	 */
	public void addPackage( GradingPackage pkg ) {
		packages.add( pkg );
	}

	/**
	 * Get a list of the already added grading packages
	 * @return
	 */
	public List<GradingPackage> getGradingPackages() {
		return packages;
	}
	
	/**
	 * Get a list of student records added
	 * @return
	 */
	public List<StudentRecord> getStudents() {
		return students;
	}

	public int getPackageIndex() {
		return packageIndex;
	}

	public int getStudentRecord() {
		return studentRecord;
	}


	public void addPMD(String srcPath ) {
		this.pmdPath = srcPath;
	}
	
	
	
}
