package CodeMonkey.physics;

import processing.core.PVector;

public class InverseSpring {

  private float k, s, x;

  public InverseSpring( float k, float s ) {

    this( k, s, 0 );

  }

  public InverseSpring( float k, float s, float x ) {

    this.k = k;
    this.s = s;
    this.x = x;

  }

  public void spring( PVector a, PVector b, PVector fa, PVector fb ) {

    if( fa != null ) {
      fa.set( b );
      fa.sub( a );
      fa.setMag( this.k / (float) Math.pow( this.s * ( fa.mag( ) - this.x ), 2 ) );
    }

    if( fb != null ) {
      fb.set( a );
      fb.sub( b );
      fb.setMag( this.k / (float) Math.pow( this.s * ( fb.mag( ) - this.x ), 2 ) );
    }

  }

}