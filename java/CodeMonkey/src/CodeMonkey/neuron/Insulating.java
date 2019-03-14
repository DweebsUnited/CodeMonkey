package CodeMonkey.neuron;

import processing.core.PVector;


public class Insulating extends SpringNeuron {

	public Insulating( ) {

		super( );
	}

	public Insulating( PVector sp ) {

		super( sp );
	}

	@Override
	public int c( ) {

		return 0xFFFFFFFF;
	}

	@Override
	public void receive( float v ) {

		// Insulators dont care

	}

	@Override
	protected float driveLen( ) {

		return 0;
	}

}
