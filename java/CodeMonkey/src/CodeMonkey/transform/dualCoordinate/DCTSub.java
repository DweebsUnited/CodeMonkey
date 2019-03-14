package CodeMonkey.transform.dualCoordinate;

import CodeMonkey.transform.DualCoordinateTransform;
import processing.core.PVector;


public class DCTSub implements DualCoordinateTransform {

	public DCTSub( ) {


	}

	@Override
	public PVector map( PVector a, PVector b ) {

		PVector ret = a.copy( );
		ret.sub( b );
		return ret;

	}

}
