package CodeMonkey.raymarch;

import processing.core.PVector;


public class OpUnion implements SDF {

	SDF a, b;

	public OpUnion( SDF a, SDF b ) {

		this.a = a;
		this.b = b;

	}

	@Override
	public float sdf( PVector r ) {

		return Math.min( this.a.sdf( r ), this.b.sdf( r ) );

	}

}
