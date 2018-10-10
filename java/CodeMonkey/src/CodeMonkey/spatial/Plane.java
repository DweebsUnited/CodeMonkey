package CodeMonkey.spatial;

import CodeMonkey.transform.CoordinateTransform;
import CodeMonkey.utility.PVectorFuncs;
import processing.core.PVector;

public class Plane implements Intersectable, CoordinateTransform {

  PVector norm, up, right, orig;

  public Plane( PVector norm, PVector orig, PVector up, PVector right ) {

    // Norm is perp to up and right ;)

    this.norm = norm.copy( );
    this.up = up.copy( );
    this.right = right.copy( );
    this.orig = orig.copy( );

  }

  public Plane( PVector orig, PVector up, PVector right ) {

    // Norm is perp to up and right ;)

    this( right.cross( up ), orig, up, right );

  }

  private float projX( float y, float z ) {

    return ( - this.norm.y * ( y - this.orig.y ) - this.norm.z * ( z - this.orig.z ) ) / this.norm.x + this.orig.x;

  }

  private float projY( float x, float z ) {

    return ( - this.norm.x * ( x - this.orig.x ) - this.norm.z * ( z - this.orig.z ) ) / this.norm.y + this.orig.y;

  }

  private float projZ( float x, float y ) {

    return ( - this.norm.x * ( x - this.orig.x ) - this.norm.y * ( y - this.orig.y ) ) / this.norm.z + this.orig.z;

  }

  public Plane( PVector norm, PVector o ) {

    this.norm = norm.copy( );
    this.orig = o.copy( );

    // Project along axis closest to norm

    float angX = PVector.angleBetween( norm, new PVector( 1, 0, 0 ) );
    float angY = PVector.angleBetween( norm, new PVector( 0, 1, 0 ) );
    float angZ = PVector.angleBetween( norm, new PVector( 0, 0, 1 ) );

    // No Y isnt right handed, sue me

    if( angX <= angY && angX <= angZ ) {

      //      System.out.println( "Using X" );

      // Project along X
      this.up    = new PVector( this.projX( o.y + 1, o.z ), o.y + 1, o.z     );
      this.right = new PVector( this.projX( o.y, o.z + 1 ), o.y,     o.z + 1 );

    } else if( angY <= angZ ) {

      //      System.out.println( "Using Y" );

      // Project along Y
      this.up    = new PVector( o.x,     this.projY( o.x, o.z + 1 ), o.z + 1 );
      this.right = new PVector( o.x + 1, this.projY( o.x + 1, o.z ), o.z     );

    } else {

      //      System.out.println( "Using Z" );

      // Project along Z
      this.up    = new PVector( o.x,     o.y + 1, this.projZ( o.x, o.y + 1 ) );
      this.right = new PVector( o.x + 1, o.y,     this.projZ( o.x + 1, o.y ) );

    }

    this.up.sub( this.orig );
    this.right.sub( this.orig );

    this.up.normalize( );
    this.right.normalize( );

    //    System.out.println( String.format( "N: %f, %f, %f", norm.x, norm.y, norm.z ) );
    //    System.out.println( String.format( "O: %f, %f, %f", this.orig.x, this.orig.y, this.orig.z ) );
    //    System.out.println( String.format( "U: %f, %f, %f", this.up.x, this.up.y, this.up.z ) );
    //    System.out.println( String.format( "R: %f, %f, %f", this.right.x, this.right.y, this.right.z ) );


  }

  @Override
  public float intersect( Ray r ) {

    // t = dot( op - ol, n ) / dot( l, n )

    PVector t1 = this.orig.copy( );
    t1.sub( r.o );

    float t = r.d.dot( this.norm );

    if( Math.abs( t ) <= Float.MIN_NORMAL )
      return Float.POSITIVE_INFINITY;

    t = t1.dot( this.norm ) / t;

    return t;

  }

  @Override
  public PVector map( PVector p ) {

    //    System.out.println( String.format( "In: %f, %f, %f", p.x, p.y, p.z ) );

    p = p.copy( );

    // This projects the point onto the plane using the normal as a direction
    Ray t = new Ray( p, this.norm );
    float tt = this.intersect( t );

    p.set( t.atT( tt ) );

    p.sub( this.orig );

    // Now we get its projection onto our right and up basis vectors
    float uProj = PVectorFuncs.projectScalar( p, this.up    );
    float rProj = PVectorFuncs.projectScalar( p, this.right );

    PVector ret = new PVector( rProj, uProj, 0 );

    //    System.out.println( String.format( "Out: %f, %f", ret.x, ret.y ) );

    return ret;

  }

}
