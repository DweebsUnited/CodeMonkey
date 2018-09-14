package CodeMonkey.transform;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class AxisHueTransform implements ColorCoordinateTransform {

  @Override
  public int map( PApplet context, PVector p ) {

    float ang = PVector.angleBetween( p, new PVector( 1, 0, 0 ) );

    context.colorMode( PConstants.HSB, 360, 100, 100 );

    float m = p.mag( );
    int col = context.color( ang * 360.0f / (float) Math.PI, 100 * m, 100 * m );

    context.colorMode( PConstants.RGB, 255, 255, 255 );

    return col;

  }

}
