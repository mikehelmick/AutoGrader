package edu.muohio.csa.autograder.style;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.dfa.report.ReportTree;
import edu.muohio.csa.autograder.TestResult;
import edu.muohio.csa.autograder.framework.GradingException;

public class AutoGradePMD {

	private RuleSets ruleSets;
	
	public AutoGradePMD()  {
		try {
			ruleSets = PMDChecker.getRuleSet();
		} catch ( RuleSetNotFoundException rsnfe ) {
			System.err.println("Unable to initialize PMD rules.");
			ruleSets = null;
		}
	}
	
	private static boolean isJava( File file ) {
		return file.getName().toLowerCase().endsWith(".java" );
	}
	
	private static String getContextName( String pkgName, File file ) {
		return pkgName + file.getName().substring( 0, file.getName().lastIndexOf('.') );
	}
	
	@SuppressWarnings("unchecked")
	public List<TestResult> checkJavaFiles( String path, String pkgName ) {
		List<TestResult> list = new ArrayList<TestResult>();
		
		File dir = new File( path + File.separator + pkgName );
		if ( dir.isDirectory() ) {
			
			File[] files = dir.listFiles();
			for( File file : files ) {
				if ( !file.isDirectory() && file.canWrite() && isJava( file ) ) {
					try {
						PMD pmd = new PMD();
						
						// open file
						InputStream is = new FileInputStream( file );
						RuleContext context = new RuleContext();
						context.setSourceCodeFilename( getContextName( pkgName, file ) );
						
						pmd.setJavaVersion( SourceType.JAVA_15 );
						pmd.processFile( is, "UTF-8", ruleSets, context );
						
						Report report = context.getReport();
						
						ReportTree tree = report.getViolationTree();
						
						int count = 0;
						Iterator<RuleViolation> iter =  (Iterator<RuleViolation>) tree.iterator();
						while( iter.hasNext() ) {
							RuleViolation rn = iter.next();
							
							StringBuffer buf = new StringBuffer();
							buf.append( rn.getClassName() + ": line " + rn.getBeginLine() + ", column " + rn.getBeginColumn() + "\n" );
							buf.append( "    " + rn.getRule().getName() + "\n " + rn.getRule().getDescription().trim() + "\n" );
							
							TestResult res = new TestResult( rn.getClassName(),
									                         rn.getRule().getName(), rn.getDescription().trim(), false );
							StyleException ex = new StyleException();
							ex.setStyleReport( buf.toString() );
							res.setGradingException( ex );
							
							list.add( res );
							
							count++;
						}
						
					} catch ( PMDException ex ) {
						TestResult res =  new TestResult( pkgName, "STYLE", "Error running PMD", false ) ;
						res.setGradingException( new GradingException( ex ) );
						list.add( res );
					} catch ( IOException ioex ) {
						TestResult res =  new TestResult( pkgName, "STYLE", "IOException", false ) ;
						res.setGradingException( new GradingException( ioex ) );
						list.add( res );
					}
				}
			}
			
		
		} else {
			list.add( new TestResult( pkgName, "STYLE", "Could not locate the directory : " +  path + File.separator + pkgName + " for style checking.", false ) );
		}
				
		return list;
	}
	
	
	
}
