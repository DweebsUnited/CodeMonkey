package CodeMonkey.transform;

import processing.core.PVector;


public interface CoordinateTransform {

	PVector map( PVector p );

}
