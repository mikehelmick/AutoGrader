/*
 * Created on Sep 19, 2005
 */
package edu.muohio.csa.autograder.report;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Stack;

public class HtmlWriter {

	private OutputStream oStream = null;
	private PrintWriter writer = null;
	
	private Stack<String> tags = new Stack<String>();
	
	public HtmlWriter( OutputStream oStream ) {
		this.oStream = oStream;
		this.writer = new PrintWriter( this.oStream, true );
	}
	
	public PrintWriter writer() {
		return writer;
	}
	
	private void indent() {
		int i = tags.size();
		for( ; i > 0; i-- ) {
			writer.print(" ");
		}
	}
	
	public void startTag( String tag ) {
		tags.push( tag );
		indent();
		writer.println( "<" + tag + ">" );
	}
	
	public void endTag() {
		if ( ! tags.empty() ) {
			indent();
			String tag = tags.pop();
			writer.println( "</" + tag + ">" );
		}
	}
	
}
