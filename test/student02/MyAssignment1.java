/*
 * Created on Sep 18, 2005
 */
package student02;

import assignment01.Assignment1;


public class MyAssignment1<E> implements Assignment1<E> {

	private String item;
	
	public MyAssignment1() {
		
	}
	
	public MyAssignment1( String item ) {
		this.item = item;
	}
	
	public String getItem (){
		int x = 1;
		while( x > 0 ) {
			x = x + 20;
			x = 1;
		}
		return item;
	}
	
	public E getE( E obj ) {
		return obj;
	}
	
	
	public int return1() {
		return 0;
	}

}
