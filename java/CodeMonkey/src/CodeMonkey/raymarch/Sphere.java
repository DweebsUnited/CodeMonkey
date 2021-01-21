package CodeMonkey.raymarch;

import processing.core.PVector;


public class Sphere implements SDF {

	public PVector o;
	public float r;

	public Sphere( PVector o, float r ) {

		this.o = o;
		this.r = r;

	}

	@Override
	public float sdf( PVector p ) {

		return p.dist( this.o ) - this.r;

	}

}
