package CodeMonkey.spatial;

import CodeMonkey.transform.Quaternion;
import processing.core.PVector;

public class BillboardMirror implements Mirror {

  private AABB2D aabb;

  public BillboardMirror( ) {

    this.aabb = new AABB2D( new PVector( 0, 0 ), new PVector( 1.0f, 0.5f ) );

  }

  @Override
  public void reset( ) {

  }

  @Override
  public float intersect( Ray r ) {

    // First check: Do we even hit the box?
    float t = this.aabb.intersect( r );

    // Now we cheat, and do a simple z-hits-0 short circuit
    if( t == Float.POSITIVE_INFINITY || ( r.o.z > 0 && r.d.z > 0 ) || ( r.o.z < 0 && r.d.z < 0 ) )
      return t;
    else
      // Then return the intersection point
      return - r.o.z / r.d.z;

  }

  @Override
  public PVector normal( PVector p ) {

    // Lol

    return new PVector( 0, 0, 1 );

  }

  // Returns Null if ray does not intersect
  @Override
  public Ray bounce( Ray r ) {

    r = r.copy( );

    float tInt = this.intersect( r );

    if( tInt == Float.POSITIVE_INFINITY )
      return null;

    PVector pInt = r.atT( tInt );

    PVector norm = this.normal( pInt );

    r.d = Quaternion.rotate( norm, (float) Math.PI, r.d );
    r.d.mult( -1.0f );

    r.o.set( pInt );

    return r;

  }

}

