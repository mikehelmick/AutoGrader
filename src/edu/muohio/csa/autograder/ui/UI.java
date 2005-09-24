/*
 * Created on Sep 14, 2005
 */
package edu.muohio.csa.autograder.ui;

import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.muohio.csa.autograder.GradingSession;
import edu.muohio.csa.autograder.framework.Graded;

public abstract class UI implements Observer {
	
	protected Graded graded = null;
	protected GradingSession session = null;
	
	protected Queue<Event> events = (Queue<Event>) new ConcurrentLinkedQueue<Event>();

	private Thread dispatcherThread = null;
	
	public final void update(Observable observable, Object arg1) {
		if ( observable instanceof GradingSession ) {
			GradingSession gradingSession = (GradingSession) observable;
			events.add( new Event( gradingSession ) );
			
		} else if ( observable instanceof Graded )  {		
			Graded graded = (Graded) observable;
			events.add( new Event( graded ) );
		}
		
		if ( arg1 != null && arg1 instanceof Boolean && Boolean.TRUE.equals( arg1 ) ) {
			// in this case delivery is meant to be synchronous
			// wait until the queue is empty
			while( ! events.isEmpty() ) {
				try {
					Thread.sleep( 100 );
				} catch( InterruptedException  iex ) {}
			}
		}
	}
	
	public final void shutdown() {
		events.add( new ShutdownEvent() );
	}
	
	public UI() {
		Dispatcher dispatcher = new Dispatcher();
		dispatcherThread = new Thread( dispatcher, "UI Dispatcher" );
		dispatcherThread.start();
	}
	
	protected abstract void gradingSessionChanged();
	
	protected abstract void gradedChanged();
	
	private class Dispatcher implements Runnable { 
		
		public void run() {
			
			Event event = null;
			
			while( true ) {
				event = events.poll();
				
				if ( event != null ) {
					if ( event instanceof ShutdownEvent ) {
						break;
					}
					
					if ( event.getGraded() != null ) {
						graded = event.getGraded();
						gradedChanged();
					} else if ( event.getGradingSession() != null ) {
						session = event.getGradingSession();
						gradingSessionChanged();
					}
				}
				
				if ( events.isEmpty() ) {
					try {
						Thread.sleep( 250 );
					} catch( InterruptedException ex ) {}
				}
			}
			
		}
		
	}
	
	protected static class Event {
		
		private GradingSession gradingSession = null;
		private Graded graded = null;
		
		public Event( GradingSession gradingSession ) {
			this.gradingSession = gradingSession;
		}
		
		public Event( Graded graded ) {
			this.graded = graded;
		}

		public Graded getGraded() {
			return graded;
		}

		public GradingSession getGradingSession() {
			return gradingSession;
		}
	
	}
	
	protected static class ShutdownEvent extends Event {
		public ShutdownEvent() {
			super( (Graded)null );
		}
	}

}
