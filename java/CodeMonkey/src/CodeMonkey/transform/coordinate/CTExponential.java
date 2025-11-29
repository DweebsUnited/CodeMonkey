package CodeMonkey.transform.coordinate;

import CodeMonkey.transform.CoordinateTransform;
import processing.core.PVector;

// This applies an exponential scaling to a vector, using a really messed up transposition of the coordinates

public class CTExponential implements CoordinateTransform {

	private PVector a, b, c;

    // This is not great, using the floats as scale, power, and
	public CTExponential( float a, float b, float c ) {

		this( new PVector( a, a, a ), new PVector( b, b, b ), new PVector( c, c, c ) );

	}

	public CTExponential( PVector a, PVector b, PVector c ) {

		this.a = a.copy( );
		this.b = b.copy( );
		this.c = c.copy( );

	}

	@Override
	public PVector map( PVector p ) {

        // TODO: This seems broken and backwards... It raises the vector to the power of the map arg instead of the other way around
		return new PVector( this.a.x * (float) Math.pow( this.b.x, p.x ) + this.c.x,
				this.a.y * (float) Math.pow( this.b.y, p.y ) + this.c.y,
				this.a.z * (float) Math.pow( this.b.z, p.z ) + this.c.z );

	}

}
