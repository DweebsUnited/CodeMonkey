package CodeMonkey.transform.dualCoordinate;

import CodeMonkey.transform.DualCoordinateTransform;
import processing.core.PVector;

public class DCTAdd implements DualCoordinateTransform {

  public DCTAdd( ) {



  }

  @Override
  public PVector map( PVector a, PVector b ) {

    PVector ret = a.copy( );
    ret.add( b );
    return ret;

  }

}
