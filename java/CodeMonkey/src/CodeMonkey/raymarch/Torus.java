package CodeMonkey.raymarch;

import processing.core.PVector;


public class Torus implements SDF {

	public PVector o;
	public float rad;
	public float thick;

	public Torus( PVector o, float rad, float thick ) {

		this.o = o;
		this.rad = rad;
		this.thick = thick;

	}

	@Override
	public float sdf( PVector p ) {

		PVector q = new PVector( p.x, 0, p.z );
		q.set( q.mag( ) - this.rad, p.y );
		return q.mag( ) - this.thick;

	}

}
