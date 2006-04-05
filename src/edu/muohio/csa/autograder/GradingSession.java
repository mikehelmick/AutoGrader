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
import edu.muohio.csa.autograder.report.PrinterReporter;
import edu.muohio.csa.autograder.report.Reporter;
import edu.muohio.csa.autograder.ui.TextUI;
import edu.muohio.csa.autograder.ui.UI;

public class GradingSession extends Observable implements Runnable {
	
	private List<StudentRecord> students = new ArrayList<StudentRecord>();
	private List<GradingPackage> packages = new ArrayList<GradingPackage>();
	
	private Set<Observer> observers = new HashSet<Observer>();
	private List<Reporter> reporters = new ArrayList<Reporter>();
	
	public boolean running = false;
	public int studentRecord = 0;
	public int packageIndex = 0;
	
	public GradingSession() {	
	}
	
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
	
	public void addTextUI() {
		if ( ! observersContains( TextUI.class ) ) {
			this.addObserver( new TextUI() );
		}
	}
	
	public void addConsoleReporter() {
		reporters.add( new ConsoleReporter() );
	}
	
	public void removeConsoleReporter() {
		removeInstanceOf( ConsoleReporter.class );
	}
	
	public void addPrinterReporter() {
		reporters.add( new PrinterReporter() );
	}
	
	public void removePrinterReporter() {
		removeInstanceOf( PrinterReporter.class );
	}
	
	/**
	 * 
	 * @param dir
	 * @throws IOException if the directory string passed in does not resolve to a directory
	 */
	public void addFileReporter( String dir ) throws IOException {
		reporters.add( new FileReporter( dir ) );
	}
	
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
								
								int secondsRemaining = 60;
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
	
	public void addStudent( StudentRecord student ) {
		students.add( student );
	}
	
	public void addPackage( GradingPackage pkg ) {
		packages.add( pkg );
	}

	public List<GradingPackage> getGradingPackages() {
		return packages;
	}
	
	public List<StudentRecord> getStudents() {
		return students;
	}

	public int getPackageIndex() {
		return packageIndex;
	}

	public int getStudentRecord() {
		return studentRecord;
	}
	
	
	
}
