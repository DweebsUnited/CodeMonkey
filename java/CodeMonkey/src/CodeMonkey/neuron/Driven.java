package CodeMonkey.neuron;

import processing.core.PVector;


public class Driven extends Insulating {

	private int uCount = 0;

	private float dLen = 0;

	public Driven( ) {

		super( );
	}

	public Driven( PVector sp ) {

		super( sp );
	}

	@Override
	public int c( ) {

		return 0xFFFF0000;
	}

	@Override
	public void update( ) {

		super.update( );

		this.v = (float) Math.sin( this.uCount++ * Math.PI / ( 30 * 2 ) ); // TODO: Hardcoded to 30 fps

		this.dLen = this.v * this.dMag;

	}

	@Override
	protected float driveLen( ) {

		return this.dLen;

	}

}
