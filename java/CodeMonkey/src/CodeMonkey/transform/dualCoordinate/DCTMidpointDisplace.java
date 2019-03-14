package CodeMonkey.transform.dualCoordinate;

import java.util.Random;

import CodeMonkey.transform.DualCoordinateTransform;
import processing.core.PVector;


public class DCTMidpointDisplace implements DualCoordinateTransform {

	private Random rng;

	public DCTMidpointDisplace( Random rng ) {

		this.rng = rng;

	}

	@Override
	public PVector map( PVector a, PVector b ) {

		return PVector.lerp( a, b, 0.5f + (float) this.rng.nextGaussian( ) / 3.0f * 0.25f );

	}

}
