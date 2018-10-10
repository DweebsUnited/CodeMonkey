package CodeMonkey.transform.dualCoordinate;

import CodeMonkey.spatial.Quaternion;
import CodeMonkey.transform.CoordinateTransform;
import CodeMonkey.transform.DualCoordinateTransform;
import CodeMonkey.transform.coordinate.CTLinear;
import processing.core.PApplet;
import processing.core.PVector;

public class DCTNoiseRotate implements DualCoordinateTransform {

  private CoordinateTransform ohoneToNegpos = new CTLinear( new PVector( -1, -1, -1 ), new PVector( 2, 2, 2 ) );

  private PApplet context;

  public DCTNoiseRotate( PApplet context ) {

    this.context = context;

  }

  @Override
  public PVector map( PVector a, PVector b ) {

    float angle = (float)Math.PI * 2 * this.context.noise( b.x + 15, b.y + 15, b.z + 15 );

    return Quaternion.rotate( a, angle, b );

  }

}
