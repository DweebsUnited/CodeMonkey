package CodeMonkey.transform.coordinate;

import CodeMonkey.transform.CoordinateTransform;
import CodeMonkey.utility.PVectorFuncs;
import processing.core.PVector;


public class CTLinear implements CoordinateTransform {

	private PVector inm, inM, inR, outm, outM, outR;

	public CTLinear( float inMin, float inMax, float outMin, float outMax ) {

		this( new PVector( inMin, inMin, inMin ), new PVector( inMax, inMax, inMax ),
				new PVector( outMin, outMin, outMin ), new PVector( outMax, outMax, outMax ) );

	}

	public CTLinear( PVector inMin, PVector inMax, PVector outMin, PVector outMax ) {

		this.inm = inMin.copy( );
		this.inM = inMax.copy( );
		this.inR = this.inM.copy( );
		this.inR.sub( this.inm );

		this.outm = outMin.copy( );
		this.outM = outMax.copy( );
		this.outR = this.outM.copy( );
		this.outR.sub( this.outm );

	}

	public CTLinear( PVector off, PVector scale ) {

		this.inm = new PVector( 0, 0, 0 );
		this.inR = new PVector( 1, 1, 1 );
		this.outm = off.copy( );
		this.outR = scale.copy( );

	}

	@Override
	public PVector map( PVector p ) {

		p = p.copy( );
		p.sub( this.inm );
		p = PVectorFuncs.elemDiv( p, this.inR );
		p = PVectorFuncs.elemMul( p, this.outR );
		p.add( this.outm );

		return p;

	}

}
