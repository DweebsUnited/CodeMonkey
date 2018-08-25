package CodeMonkey.utility;

import java.util.ArrayList;

public class CircularBuffer<T> {

  public ArrayList<T> buffer;

  private int wH = 0;

  public CircularBuffer( int size ) {

    this.buffer = new ArrayList<T>( );

    for( int odx = 0; odx < size; ++odx )
      this.buffer.add( null );

  }

  public void add( T val ) {

    this.buffer.set( this.wH++, val );

    this.wH = ( this.wH >= this.buffer.size( ) ) ? 0 : this.wH;

  }

}
