/*
 * Created on Sep 19, 2005
 */
package edu.muohio.csa.autograder.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileReporter extends Reporter {

	private String dir = "/";
	
	private static String SEPARATOR = null;
	public FileReporter( String dir ) throws IOException {
		if ( SEPARATOR == null ) {
			SEPARATOR = System.getProperty( "file.seperator" );
			if ( SEPARATOR == null ) {
				SEPARATOR = "/";
			}
		}
		
		this.dir = dir;
		if ( ! this.dir.endsWith( SEPARATOR ) ) {
			this.dir = this.dir + SEPARATOR;
		}
		
		File file = new File( this.dir );
		if ( !(file.exists() && file.isDirectory() ) ){
			throw new IOException("Not a directory");
		}
	}
	
	@Override
	public OutputStream getStream(String studentId) throws IOException {
		String fileName = dir + studentId + ".report";
		
		File file = new File( fileName );
		
		file.createNewFile();
		
		FileOutputStream oStream = new FileOutputStream( file );
		
		return oStream;
	}

	@Override
	public void closeStream(OutputStream stream) {
		try {
			stream.close();
		} catch ( IOException ex ) {
			System.err.println( ex.getMessage() );
			ex.printStackTrace( System.err );
		}
	}

}
