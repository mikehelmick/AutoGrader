/*
 * Created on Sep 19, 2005
 */
package edu.muohio.csa.autograder.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaName;

import edu.muohio.csa.autograder.StudentRecord;

/**
 * The idea was to spool repots directly to the printer. <br/>
 * 
 * This class is not functionalal - feel free to fix it and submit a patch.
 * 
 * @author mhelmick
 * @deprecated
 */
public class PrinterReporter extends Reporter {

	private void printHTML( String html ) {
		
		// Set the document type
		DocFlavor myFormat = DocFlavor.STRING.TEXT_PLAIN;
		// Create a Doc
		Doc myDoc = new SimpleDoc( html, myFormat, null); 
		
		// Build a set of attributes
		PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet(); 
		aset.add( new Copies(1) ); 
		aset.add( MediaName.NA_LETTER_WHITE );
		// discover the printers that can print the format according to the
		// instructions in the attribute set
		PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		// Create a print job from one of the print services
		if (service != null ) { 
			System.out.println( "Sending output to: " + service.getName() );
		    DocPrintJob job = service.createPrintJob(); 
		    try { 
		    		job.print(myDoc, aset); 
		    } catch (PrintException pe) {
		    		System.err.println( pe.getMessage() );
		        	pe.printStackTrace( System.err );
		    } 
		    
		} else {
			System.err.println("No default printer configured");
		}
	}
	
	public void a_writeReport( List<StudentRecord> students ) {
		
		for( StudentRecord student : students ) {
			OutputStream oStream = getStream( student.getStudentId() );
			HtmlWriter writer = new HtmlWriter( oStream );
			
			writer.startTag( "html" );
			 writer.startTag( "head" );
			  writer.startTag( "title" );
			   writer.writer().println( "AutoGrade Report for " + student.getStudentName() + " (" + student.getStudentId() + ")" );
			  writer.endTag();
			 writer.endTag();
			 
			 writer.startTag( "body" );
			  writer.startTag("H1");
			   writer.writer().println( student.getStudentName() + " (" + student.getStudentId() + ")" );
			  writer.endTag();
			 
			 writer.endTag();
			 
			writer.endTag();
			
			closeStream( oStream );
		}
	
	}
	
	@Override
	public void closeStream(OutputStream stream) {
		printHTML( stream.toString() );
		try {
			stream.close();
		} catch( IOException ex ) {
			System.err.println( "Printing Exception: " + ex.getMessage() );
			ex.printStackTrace( System.err );
		}
	}

	@Override
	public OutputStream getStream(String studentId) {
		return new ByteArrayOutputStream();
	}

	
	
}
