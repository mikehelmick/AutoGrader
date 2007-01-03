package edu.muohio.csa.cscourseware;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.dfa.report.ReportTree;
import edu.muohio.csa.autograder.style.PMDChecker;

/**
 * The checkstyle program, take the parameter file names to check
 * 
 * Write report to the console
 * 
 * @author mhelmick
 */
public class CheckStyle {
	
	private static String getContextName( String fileName ) {
		if ( fileName.indexOf('/') > 0 ) {
			return fileName.substring( fileName.lastIndexOf('/') + 1 );
		} 
		return fileName;
	}

	@SuppressWarnings("unchecked")
	public static void main( String[] args ) {
		if ( args.length == 0 ) {
			System.out.println("error: no files given.");
			System.exit(0);
		}
		
		StringBuffer finalResults = new StringBuffer();
		
		
		PMD pmd = new PMD();
		
		RuleSets ruleSets = null;
		try {
			ruleSets = PMDChecker.getRuleSet();
		} catch ( Exception ex ) {
			System.out.println("error: Could not initialize PMD ruleset." );
			System.exit(0);
			
		}
			
		for( int i = 0; i < args.length; i++ ) {
			
			if ( args[i].endsWith( ".java" ) ) {
			
				try {
					InputStream is = new FileInputStream( args[i] );
					RuleContext context = new RuleContext();
					context.setSourceCodeFilename( getContextName( args[i] ) );
					
					pmd.setJavaVersion( SourceType.JAVA_15 );
					pmd.processFile( is, "UTF-8", ruleSets, context );
					
					Report report = context.getReport();
					
					ReportTree tree = report.getViolationTree();
					
					int count = 0;
					Iterator<RuleViolation> iter =  (Iterator<RuleViolation>) tree.iterator();
					while( iter.hasNext() ) {
						RuleViolation rn = iter.next();
						
						finalResults.append("violation" + count + ":\n");
						finalResults.append("  message: " + rn.getRule().getMessage() + "\n" );
						finalResults.append("  abs_path: " + args[i] + "\n" );
						finalResults.append("  filename: " + rn.getFilename() + "\n" );
						finalResults.append("  begin_line: " + rn.getBeginLine() + "\n" );
						finalResults.append("  begin_column: " + rn.getBeginColumn() + "\n" );
						finalResults.append("  end_line: " + rn.getEndLine() + "\n" );
						finalResults.append("  end_column: " + rn.getEndColumn() + "\n");
						finalResults.append("  package: " + rn.getPackageName() + "\n" );
						finalResults.append("  class: " + rn.getClassName() + "\n" );
						finalResults.append("  rule_name: " + rn.getRule().getName() + "\n");
						finalResults.append("  rule_description: " + rn.getRule().getDescription().trim().replaceAll("\n", "<br/>" ).replaceAll(" ", "&nbsp;" ) + "\n" );
						finalResults.append("  example: " + rn.getRule().getExample().trim().replaceAll("\n", "<br/>" ).replaceAll(" ", "&nbsp;" ) + "\n" );
						finalResults.append("\n");
						
						count++;
					}
					
					
				} catch ( Exception ex ) {
					finalResults.append("pmdexception" + i + ": " + ex.getMessage() );
				}
		
			}
		}
		
		
		
		
		
		System.out.println( finalResults.toString() );
	}
	
}
