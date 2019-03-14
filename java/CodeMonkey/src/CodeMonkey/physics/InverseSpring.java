package CodeMonkey.physics;

import processing.core.PVector;


public class InverseSpring {

	private float k, x, p;

	public InverseSpring( float k ) {

		this( k, 0, 2 );

	}

	public InverseSpring( float k, float x ) {

		this( k, x, 2 );

	}

	public InverseSpring( float k, float x, float p ) {

		this.k = k;
		this.x = x;
		this.p = p;

	}

	public void spring( PVector a, PVector b, PVector fa, PVector fb ) {

		if( fa != null ) {
			fa.set( b );
			fa.sub( a );
			fa.setMag( this.k / (float) Math.pow( fa.mag( ) - this.x, this.p ) );
		}

		if( fb != null ) {
			fb.set( a );
			fb.sub( b );
			fb.setMag( this.k / (float) Math.pow( fb.mag( ) - this.x, this.p ) );
		}

	}

}