package CodeMonkey.neuron;

import java.util.Random;

import CodeMonkey.utility.MatrixMath;


public class RNN {

	private int lenX, lenH, lenY;
	public float[ ] t, h, whh, wxh, why;

	public RNN( int lenX, int lenH, int lenY, Random rng ) {

		this.lenX = lenX;
		this.lenH = lenH;
		this.lenY = lenY;

		this.t = new float[ lenH ];
		this.h = new float[ lenH ];
		this.whh = new float[ lenH * lenH ];
		this.wxh = new float[ lenH * lenX ];
		this.why = new float[ lenY * lenH ];

		this.randWeight( rng );

	}

	public void randWeight( Random rng ) {

		// 0 starting state
		for( int cdx = 0; cdx < this.lenH; ++cdx )
			this.h[ cdx ] = 0;

		// Random starting weights
		for( int cdx = 0; cdx < this.lenH * this.lenH; ++cdx )
			this.whh[ cdx ] = ( 0.05f * rng.nextFloat( ) ) - 0.025f;

		for( int cdx = 0; cdx < this.lenX * this.lenH; ++cdx )
			this.wxh[ cdx ] = ( 0.05f * rng.nextFloat( ) ) - 0.025f;

		for( int cdx = 0; cdx < this.lenH * this.lenY; ++cdx )
			this.why[ cdx ] = ( 0.05f * rng.nextFloat( ) ) - 0.025f;

	}

	public void step( float[ ] x, float[ ] y ) {

		if( x.length != this.lenX )
			throw new RuntimeException( "Matrix X dimensions do not match" );

		if( y.length != this.lenY )
			throw new RuntimeException( "Matrix Y dimensions do not match" );

		// h' = tanh( whh * h + wxh * x )
		// y = why * h'

		// Construct h'
		// h x h * h x 1 -> h x 1
		MatrixMath.mult( this.whh, this.h, this.lenH, this.lenH, 1, this.t );
		// h x X * X x 1 -> h x 1
		MatrixMath.multAdd( this.wxh, x, this.lenH, this.lenX, 1, this.t );
		MatrixMath.map( this.t, ( double e ) -> {
			return Math.tanh( e );
		} );

		// Swap h' to h
		float[ ] swp = this.t;
		this.t = this.h;
		this.h = swp;

//		float mD = Float.POSITIVE_INFINITY;
//		float MD = Float.NEGATIVE_INFINITY;
//		for( int cdx = 0; cdx < this.lenH; ++cdx ) {
//
//			float tmp = this.h[ cdx ] - this.t[ cdx ];
//
//			if( tmp < mD )
//				mD = tmp;
//			if( tmp > MD )
//				MD = tmp;
//
//		}
//
//		System.out.println( String.format( "%f -> %f", mD, MD ) );

		// Now construct y
		// y x h * h x 1 -> y x 1
		MatrixMath.mult( this.why, this.h, this.lenY, this.lenH, 1, y );

	}

}
