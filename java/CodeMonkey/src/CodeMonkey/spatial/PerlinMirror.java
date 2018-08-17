package CodeMonkey.spatial;

import java.util.Random;

import CodeMonkey.coordinate.AxisTransform;
import CodeMonkey.coordinate.CoordinateTransform;
import CodeMonkey.coordinate.LinearTransform;
import CodeMonkey.coordinate.Quaternion;
import processing.core.PApplet;
import processing.core.PVector;

public class PerlinMirror implements Mirror {

  private PApplet context;
  private Random rng;

  private CoordinateTransform cTrans;
  private AxisTransform nTrans;

  private AABB3D aabb;

  private final float STEP = 0.001f;
  private final float DERIV_STEP = 0.0001f;

  public PerlinMirror( PApplet context, Random rng ) {

    this.rng = rng;

    this.context = context;

    // TODO: This is hardcoded
    this.aabb = new AABB3D( new PVector( 0, 0, 0 ), new PVector( 1.0f, 0.5f, 1.0f ) );

    this.reset( );

  }

  @Override
  public void reset( ) {

    // TODO: This scale is wrooooooooooong
    //  I don't want a rescaling, I want an offset
    //    this.cTrans = new LinearTransform(
    //        new PVector( this.rng.nextFloat( ) * 25, this.rng.nextFloat( ) * 25, this.rng.nextFloat( ) * 25 ),
    //        new PVector( 2, 2, 2 ) );
    // List of good ones:
    //   1.2, 21.8
    //   2.3, 0.7
    //   6.2, 0.3
    PVector temp = new PVector( 2.5f, 10.0f, 0 );
    this.cTrans = new LinearTransform(
        new PVector( 0, 0, 0 ),
        new PVector( 1, 1, 1 ),
        new PVector( 0, 0, 0 ),
        temp );

    System.out.println( temp );

    this.nTrans = new LinearTransform( 0, 1, 0, 0.25f );

  }

  private float sample( PVector p ) {

    p = this.cTrans.map( p );

    return this.nTrans.map( this.context.noise( p.x, p.y ) );

  }

  @Override
  public float intersect( Ray r ) {

    r = r.copy( );

    // First check: Do we even hit the box?
    float t = this.aabb.intersect( r );

    if( t == Float.POSITIVE_INFINITY )
      return t;

    // Make sure we are not ON the box ( next int = 0 )
    t += this.STEP;

    float tAccum = t;

    r.o.set( r.atT( t ) );
    float h = this.sample( r.o );

    boolean onTop = r.o.z > h;

    // TODO: Caution, rays can bounce off bottom too

    do {

      // Update t

      t = this.aabb.intersect( r );
      if( t < Float.POSITIVE_INFINITY ) {
        t = Math.min( t, this.STEP );
      } else {
        return Float.POSITIVE_INFINITY;
      }

      tAccum += t;

      r.o.set( r.atT( t ) );
      h = this.sample( r.o );

      // We exit when we pass through the surface
      if( r.o.z > h != onTop ) {

        break;

      }

    } while( t < Float.POSITIVE_INFINITY );

    // Collided!
    return tAccum;

  }

  @Override
  public PVector normal( PVector p ) {

    // Cross product two tangent vectors

    p = p.copy( );
    PVector px = new PVector( p.x + this.DERIV_STEP, p.y );
    PVector py = new PVector( p.x, p.y + this.DERIV_STEP );

    p.z = this.sample( p );
    px.z = this.sample( px );
    py.z = this.sample( py );

    px.sub( p );
    py.sub( p );

    px.normalize( p );
    py.normalize( p );

    PVector.cross( px, py, p );

    p.normalize( );

    return p;

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
