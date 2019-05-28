package CodeMonkey.physics;

import processing.core.PVector;


public class PointMassAccum {

	private PointMass m;

	private PVector F;
	public PVector p;

	public PointMassAccum( PVector p, float m ) {

		this( p.x, p.y, p.z, m );

	}

	public PointMassAccum( float x, float y, float m ) {

		this( x, y, 0, m );

	}

	public PointMassAccum( float x, float y, float z, float m ) {

		this.m = new PointMass( x, y, z, m );

		this.F = new PVector( 0, 0, 0 );
		this.p = this.m.c.copy( );

	}

	public void accum( PVector F ) {

		this.F.add( F );

	}

	public void verlet( float dt ) {

		this.verlet( dt, false );

	}

	public void verlet( float dt, boolean GRAVITY ) {

		this.m.verlet( this.F, dt, GRAVITY );
		this.F.set( 0, 0, 0 );
		this.p.set( this.m.c );

	}

	public void set( PVector p ) {

		this.m.set( p );
		this.p.set( p );

	}

	public void set( PVector pp, PVector p ) {

		this.m.set( pp, p );
		this.p.set( p );

	}

	public void setM( float m ) {

		this.m.setM( m );

	}

}
