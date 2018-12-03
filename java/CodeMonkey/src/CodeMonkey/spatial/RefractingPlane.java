package CodeMonkey.spatial;

import processing.core.PVector;

public class RefractingPlane extends Plane {

  private float iRefrac;

  public RefractingPlane( PVector norm, PVector o, float iRefrac ) {

    super( norm, o );

    this.iRefrac = iRefrac;

  }

  public Ray refract( Ray raaaaaaaaaaaaaaaaaay ) {

    // Copy so we can return
    Ray ref = raaaaaaaaaaaaaaaaaay.copy( );

    // Origin is where ray hits plane
    ref.o = this.intersectPoint( ref );

    // alpha = ang( norm, ray ) must be < pi / 2
    PVector n = this.norm.copy( );
    float alpha = PVector.angleBetween( ref.d, n );
    float r = this.iRefrac;

    // If not, flip norm and refrac index
    if( alpha > Math.PI / 2 ) {

      n.mult( -1 );
      r = 1.0f / r;

      alpha = PVector.angleBetween( ref.d, n );

    }

    // Flip refrac if norm flipped
    PVector axis = ref.d.cross( n );
    ref.d.set( Quaternion.rotate( axis, alpha - (float) Math.asin( Math.sin( alpha ) * r ), ref.d ) );

    return ref;

  }

}
