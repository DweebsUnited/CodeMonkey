package CodeMonkey.spatial;

import processing.core.PVector;

public class BillboardMirror implements Mirror {

  private float width, height;

  private PVector norm;

  private Plane p;

  public BillboardMirror( float width, float height, PVector norm, PVector mirrCent ) {

    this.width = width;
    this.height = height;

    this.norm = norm.copy( );

    this.p = new Plane( norm, mirrCent );

  }

  @Override
  public float intersect( Ray r ) {

    return this.p.intersect( r );

  }

  @Override
  public PVector normal( PVector p ) {

    // Billboards don't change normal
    return this.norm;

  }

  // Returns Null if ray does not intersect
  @Override
  public Ray bounce( Ray r ) {

    r = r.copy( );

    float tInt = this.intersect( r );

    if( tInt == Float.POSITIVE_INFINITY )
      return null;

    PVector pInt = r.atT( tInt );

    PVector planeSpace = this.p.map( pInt );

    if(
        planeSpace.x >=  this.width  / 2.0f ||
        planeSpace.x < -this.width  / 2.0f ||
        planeSpace.y >=  this.height / 2.0f ||
        planeSpace.y < -this.height / 2.0f ) {
      //      System.out.println( "Out of bounds" );
      return null;
    }

    PVector pNorm = this.normal( planeSpace );

    PVector bd = Quaternion.rotate( pNorm, (float) Math.PI, r.d );
    bd.mult( -1 );

    r.d.set( bd );
    r.o.set( pInt );

    return r;

  }

}

