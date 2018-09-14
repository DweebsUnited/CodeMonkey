package CodeMonkey.transform;

import processing.core.PVector;

public interface DualCoordinateTransform {

  public PVector map( PVector a, PVector b );

}
