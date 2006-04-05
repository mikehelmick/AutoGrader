/*
 * Created on Sep 14, 2005
 */
package student01;

import assignment01.Assignment1;

public class MyAssignment1<E> implements Assignment1<E> {
	
	private String item;
	
	public MyAssignment1() {
	
	}
	
	public MyAssignment1( String item ) {
		this.item = item;
	}
	
	public String getItem (){
		return item;
	}

	public E getE( E obj ) {
		return obj;
	}
	
	public int return1() {
		return 1;
	}

}
