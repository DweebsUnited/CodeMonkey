package CodeMonkey.transform.color;

import processing.core.PApplet;
import processing.core.PVector;


public class XYZRGB implements ColorCoordinateTransform {

	@Override
	public int map( PApplet context, PVector p ) {

		return context.color( p.x * 255, p.y * 255, p.z * 255 );

	}

}
