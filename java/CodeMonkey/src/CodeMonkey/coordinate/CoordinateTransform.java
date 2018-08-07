package CodeMonkey.coordinate;

import processing.core.PVector;

public interface CoordinateTransform {

  PVector map( PVector p );

}
