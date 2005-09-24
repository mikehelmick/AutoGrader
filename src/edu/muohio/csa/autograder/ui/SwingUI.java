/*
 * Created on Sep 24, 2005
 */
package edu.muohio.csa.autograder.ui;

import java.io.IOException;

import javax.swing.JFrame;

import edu.muohio.csa.autograder.AutoGrader;
import edu.muohio.csa.autograder.GradingSession;

public class SwingUI extends UI{

	private GradingSession session = new GradingSession();
	
	public SwingUI() {
		super();
	}

	@Override
	protected void gradedChanged() {
		
	}

	@Override
	protected void gradingSessionChanged() {
		
	}
	
	private void resetSession() {
		session = new GradingSession();
	}
	
	private void activateConsoleReporter() {
		session.addConsoleReporter();
	}
	
	private void activatePrinterReporter() {
		session.addPrinterReporter();
	}
	
	private void activateFileReporter( String directory ) throws IOException {
		session.addFileReporter( directory );
	}
	
	/**
	 * inner class to actually draw the UI window
	 * project: AutoGrader
	 * package: edu.muohio.csa.autograder.ui
	 * 
	 * @author mhelmick
	 * @version $Id$
	 */
	private class Display extends JFrame {
		
		public Display() {
			super( AutoGrader.SYS_NAME + " v" + AutoGrader.VER_NAME );
			init();
		}
		
		/** draw the window */
		private void init() {
			
		}
		
	}
	
	
}
