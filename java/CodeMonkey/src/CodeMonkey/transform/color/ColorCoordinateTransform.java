package CodeMonkey.transform.color;

import processing.core.PApplet;
import processing.core.PVector;


public interface ColorCoordinateTransform {

	public int map( PApplet context, PVector p );

}
