package edu.muohio.csa.autograder.style;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.dfa.report.ReportTree;

public class PMDChecker {

	public static RuleSets getRuleSet() throws RuleSetNotFoundException  {
		RuleSetFactory rsf = new RuleSetFactory();
		
		RuleSets set = new RuleSets();
		
		
		Iterator iter = rsf.getRegisteredRuleSets();
		while( iter.hasNext() ) {
			RuleSet rs = (RuleSet) iter.next();
		
			if ( rs.getName().equals("Basic Rules") ||
				 rs.getName().equals("Braces Rules") ||
				 rs.getName().equals("Finalizer Rules") ||
				 rs.getName().equals("Unused Code Rules") ||
				 rs.getName().equals("Coupling Rules") ||
				 rs.getName().equals("Optimization Rules") ||
				 rs.getName().equals("Design Rules") ||
				 rs.getName().equals("Strict Exception Rules") ||
				 rs.getName().equals("String and StringBuffer Rules") ||
				 rs.getName().equals("Code Size Rules") ||
				 rs.getName().equals("Naming Rules") ||
				 rs.getName().equals("Braces Rules") ) {
				set.addRuleSet( rs ); 
			}
		
		}
		
		/* for printing
		Set rules = set.getAllRules();
		Iterator it = rules.iterator(); 
		while( it.hasNext() ) {
			Rule o = (Rule) it.next();
			/*System.out.println("    StyleCheck.create :name => '" + o.getName() + "'," +
					           " :description => '" + o.getDescription().trim().replaceAll("\n", "\\\\n" ).replaceAll("'", "\\\\'" )  + "', " +
					           " :example => '" + o.getExample().trim().replaceAll("\n", "\\\\n" ).replaceAll(" ", "&nbsp;" ).replaceAll("'", "\\\\'" ) + "'");
		} 
		*/
		
		return set;
	}
	
	public static void main( String[] args ) throws Exception {
		
		PMD pmd = new PMD();
		
		RuleSets rs = getRuleSet();
		RuleContext context = new RuleContext();
		context.setSourceCodeFilename("MyAssignment1.java");
		
		
		InputStream is = new FileInputStream( "/Users/mhelmick/src/workspace/AutoGrader/test/style/MyAssignment1.java" );
		
		try {
			pmd.processFile( is, "UTF-8", rs, context );
			pmd.setJavaVersion( SourceType.JAVA_15 );
			
			Report report = context.getReport();
			
			ReportTree tree = report.getViolationTree();
			
			Iterator<RuleViolation> iter =  (Iterator<RuleViolation>) tree.iterator();
			while( iter.hasNext() ) {
				RuleViolation rn = iter.next();
				System.out.println( rn.getClassName() + ":" + rn.getBeginLine() + "," + rn.getBeginColumn() + "-"+ rn.getEndLine() +"," + rn.getEndColumn() );
				System.out.println( "    " + rn.getRule().getName() + " (" + rn.getRule().getDescription().trim() + ")" );
				System.out.println( "    " + rn.getRule().getExample() );
				System.out.println("-------------------------------------------");
			}
			
			
		} catch ( PMDException ex ) {
			System.err.println("EX: " + ex.getMessage() );
			ex.printStackTrace( System.err );
		}
		
	}
	
}
