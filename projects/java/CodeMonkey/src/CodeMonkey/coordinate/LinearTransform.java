package CodeMonkey.coordinate;

import processing.core.PVector;

public class LinearTransform implements AxisTransform, CoordinateTransform {

  private PVector inm, inM, inR, outm, outM, outR;

  public LinearTransform( float inMin, float inMax, float outMin, float outMax ) {

    this(
        new PVector( inMin, inMin, inMin ),
        new PVector( inMax, inMax, inMax ),
        new PVector( outMin, outMin, outMin ),
        new PVector( outMax, outMax, outMax ) );

  }

  public LinearTransform( PVector inMin, PVector inMax, PVector outMin, PVector outMax ) {

    this.inm = inMin.copy( );
    this.inM = inMax.copy( );
    this.inR = this.inM.copy( );
    this.inR.sub( this.inm );

    this.outm = outMin.copy( );
    this.outM = outMax.copy( );
    this.outR = this.outM.copy( );
    this.outR.sub( this.outm );

  }

  @Override
  public float map( float c ) {

    return ( c - this.inm.x ) / this.inR.x * this.outR.x + this.outm.x;

  }

  @Override
  public PVector map( PVector p ) {

    p = p.copy( );
    p.sub( this.inm );
    p = PVectorFuncs.elemDiv( p, this.inR );
    p = PVectorFuncs.elemMul( p, this.outR );
    p.add( this.outm );

    return p;

  }

}
