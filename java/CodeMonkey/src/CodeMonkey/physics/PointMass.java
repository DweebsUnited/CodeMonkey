package CodeMonkey.physics;

import processing.core.PVector;


public class PointMass {

	private float m;

	public PVector c;
	public PVector co;

	public PointMass( float x, float y, float m ) {

		this.m = m;

		this.c = new PVector( x, y );

		this.co = new PVector( );
		this.co.set( this.c );

	}

	public void verlet( PVector F, float dt ) {

		// X+1 = 2 X - X-1 + A dt dt

		F = F.copy( );

		PVector p = new PVector( );
		p.set( this.c );
		p.mult( 2 );
		p.sub( this.co );

		F.mult( dt * dt / this.m );

		p.add( F );

		// To velocity
		p.sub( this.co );
		// Dampen by %
		p.mult( 0.97f );
		// Back to position
		p.add( this.co );

		this.co.set( this.c );
		this.c.set( p );

	}

}
