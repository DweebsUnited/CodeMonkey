package CodeMonkey.draw;

import processing.core.PGraphics;
import processing.core.PVector;


public interface LineDrawer {

	public void line( PGraphics canvas, float ox, float oy, float dx, float dy );

	public void line( PGraphics canvas, PVector o, PVector d );

}
