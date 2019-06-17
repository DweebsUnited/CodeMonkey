package CodeMonkey.physics;

import processing.core.PVector;


public class PointMass {

	private final static float G = -9.8f;

	private float m;

	public PVector c;
	public PVector co;

	public PointMass( float x, float y, float m ) {

		this( x, y, 0, m );

	}

	public PointMass( float x, float y, float z, float m ) {

		this.m = m;

		this.c = new PVector( x, y, z );
		this.co = new PVector( x, y, z );

	}

	// TODO: Gravity flag
	public void verlet( PVector F, float dt ) {

		this.verlet( F, dt, false );

	}

	public void verlet( PVector F, float dt, boolean GRAVITY ) {

		// X+1 = 2 X - X-1 + A dt dt

		F = F.copy( );

		// Add gravity if need be
		if( GRAVITY ) {

			F.add( new PVector( 0, 0, PointMass.G * this.m ) );

		}

		PVector p = new PVector( );
		p.set( this.c );
		p.mult( 2 );
		p.sub( this.co );

		F.mult( dt * dt / this.m );

		p.add( F );

		// To velocity
		p.sub( this.co );

		// Dampen by %
		p.mult( 0.9999f );
		// Damping by 1/x

		// Back to position
		p.add( this.co );

		this.co.set( this.c );
		this.c.set( p );

	}

	public void set( PVector p ) {

		this.set( p, p );

	}

	public void set( PVector pp, PVector p ) {

		this.c.set( p );
		this.co.set( pp );

	}

	public void setM( float m ) {

		this.m = m;

	}

}
