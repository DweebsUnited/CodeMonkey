package CodeMonkey.spatial;

import processing.core.PVector;

public class Sphere implements Intersectable {

  private PVector o;
  private float radsq;

  public Sphere( PVector o, float rad ) {

    this.o = o.copy( );
    this.radsq = rad * rad;

  }

  @Override
  public float intersect( Ray r ) {

    // Forget proofs, I don't want to bother doing that here

    // det = ( l dot ( o - c ) )^2 - | o - c |^2 + r^2
    // if det < 0:
    //   No solution
    // if det == 0:
    //   int = it
    // if det > 0:
    //   int = it +- sqrt( det )

    PVector oc = r.o.copy( );
    oc.sub( this.o );
    float det = (float) Math.pow( r.d.dot( oc ), 2 ) - oc.magSq( ) + this.radsq;

    // it = -( l dot ( o - c ) )
    float it = -r.d.dot( oc );

    if( det < 0 ) {
      return Float.POSITIVE_INFINITY;
    } else if( det < Float.MIN_NORMAL ) {
      return it;
    } else {

      det = (float) Math.sqrt( det );

      // Return the closest positive intersection
      if( it - det < 0 )
        return it + det;
      else
        // if det > 0, min( it - det, it + det ) = it - det
        return it - det;

    }

  }

}
