package CodeMonkey.transform.coordinate;

import CodeMonkey.spatial.Quaternion;
import CodeMonkey.transform.CoordinateTransform;
import processing.core.PApplet;
import processing.core.PVector;

public class CTNoiseRotate implements CoordinateTransform {

  private CoordinateTransform ohoneToNegpos = new CTLinear( new PVector( -1, -1, -1 ), new PVector( 2, 2, 2 ) );

  private PApplet context;

  public CTNoiseRotate( PApplet context ) {

    this.context = context;

  }

  @Override
  public PVector map( PVector p ) {

    PVector axis = this.ohoneToNegpos.map( new PVector(
        this.context.noise( p.x + 0,  p.y + 0,  p.z + 0 ),
        this.context.noise( p.x + 5,  p.y + 5,  p.z + 5 ),
        this.context.noise( p.x + 10, p.y + 10, p.z + 10 ) ) );
    float angle = (float)Math.PI * 2 * this.context.noise( p.x + 15, p.y + 15, p.z + 15 );

    return Quaternion.rotate( axis, angle, p );

  }

}
