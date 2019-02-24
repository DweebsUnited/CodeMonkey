package CodeMonkey.physics;

import processing.core.PVector;

public class Spring {

  private float k;
  private float x;

  public Spring( float k ) {

    this( k, 0 );

  }

  public Spring( float k, float x ) {

    this.k = k;
    this.x = x;

  }

  public void spring( PVector a, PVector b, PVector fa, PVector fb ) {

    if( fa != null ) {
      fa.set( b );
      fa.sub( a );
      fa.setMag( ( fa.mag( ) - this.x ) * this.k );
    }

    if( fb != null ) {
      fb.set( a );
      fb.sub( b );
      fb.setMag( ( fb.mag( ) - this.x ) * this.k );
    }

  }

  public static void spring( float k, PVector a, PVector b, PVector fa, PVector fb ) {

    if( fa != null ) {
      fa.set( b );
      fa.sub( a );
      fa.setMag( fa.mag( ) * k );
    }

    if( fb != null ) {
      fb.set( a );
      fb.sub( b );
      fb.setMag( fb.mag( ) * k );
    }

  }

  public static void spring( float k, float x, PVector a, PVector b, PVector fa, PVector fb ) {

    if( fa != null ) {
      fa.set( b );
      fa.sub( a );
      fa.setMag( ( fa.mag( ) - x ) * k );
    }

    if( fb != null ) {
      fb.set( a );
      fb.sub( b );
      fb.setMag( ( fb.mag( ) - x ) * k );
    }

  }

}