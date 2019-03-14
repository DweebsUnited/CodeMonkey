package CodeMonkey.transform.coordinate;

import CodeMonkey.transform.CoordinateTransform;
import processing.core.PVector;


public class CTSigmoid implements CoordinateTransform {

	PVector s, o;

	public CTSigmoid( PVector s, PVector o ) {

		this.s = s.copy( );
		this.o = o.copy( );

	}

	private float map( float c, float s, float o ) {

		return (float) ( 1.0f / ( 1.0f + Math.exp( -s * ( c - o ) ) ) );

	}

	@Override
	public PVector map( PVector p ) {

		return new PVector( this.map( p.x, this.s.x, this.o.x ), this.map( p.y, this.s.y, this.o.y ),
				this.map( p.z, this.s.z, this.o.z ) );

	}

}
