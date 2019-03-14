package CodeMonkey.neuron;

import processing.core.PVector;


public class Conducting extends SpringNeuron {

	private static float[ ] resp;
	private static final int respStep = 32;
	private static final float Q = -0.05f;
	private static final float R = 1.0f;
	private static final float S = -0.15f;

	private static final float R_THRESH = 0.95f;

	static {

		// Set up the response curve
		Conducting.resp = new float[ Conducting.respStep ];

		int QStep = Conducting.respStep / 8;
		int RStep = 3 * QStep;

		for( int sdx = 0; sdx < QStep; ++sdx )
			Conducting.resp[ sdx ] = Conducting.Q / QStep * ( sdx + 1 );

		for( int sdx = 0; sdx < RStep; ++sdx )
			Conducting.resp[ sdx + QStep ] = ( Conducting.R - Conducting.Q ) / RStep * ( sdx + 1 ) + Conducting.Q;

		for( int sdx = 0; sdx < RStep; ++sdx )
			Conducting.resp[ sdx + QStep + RStep ] = ( Conducting.S - Conducting.R ) / RStep * ( sdx + 1 ) + Conducting.R;

		for( int sdx = 0; sdx < QStep; ++sdx )
			Conducting.resp[ sdx + QStep + RStep + RStep ] = ( 0 - Conducting.S ) / QStep * ( sdx + 1 ) + Conducting.S;

	}

	private int rStep = -1;

	public Conducting( ) {

		super( );

	}

	public Conducting( PVector sp ) {

		super( sp );

	}

	@Override
	public int c( ) {

		return 0xFF0000FF;
	}

	@Override
	public void update( ) {

		super.update( );

		// Not driving, and enough pressure to respond
		if( this.rStep < 0 && this.VAccum > Conducting.R_THRESH )
			this.rStep = 0;

		// Reset pressure
		this.VAccum = 0;

		if( this.rStep >= 0 ) {

			this.v = Conducting.resp[ this.rStep ];
			this.rStep += 1;
			if( this.rStep == Conducting.respStep )
				this.rStep = -1;

		} else
			this.v = 0;

	}

	@Override
	protected float driveLen( ) {

		return 0;
	}

}
