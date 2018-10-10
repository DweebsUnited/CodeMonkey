package CodeMonkey.transform.color;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

// WARN: This class is 2d specific!

public class CoordinateHue implements ColorCoordinateTransform {

  PVector xaxis = new PVector( 1, 0, 0 );

  @Override
  public int map( PApplet context, PVector p ) {

    PVector c = new PVector( );
    PVector.cross( this.xaxis, p, c );

    float ang = PVector.angleBetween( p, this.xaxis );
    ang = ( c.z <= 0 ) ? ( 2 * (float) Math.PI - ang ) : ang;

    context.colorMode( PConstants.HSB, 360, 100, 100 );

    float m = p.mag( );
    int col = context.color( ang * 180.0f / (float) Math.PI, 100, 100 * (float) Math.sqrt( m ) );

    context.colorMode( PConstants.RGB, 255, 255, 255 );

    return col;

  }

}
