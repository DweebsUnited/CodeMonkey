package CodeMonkey.coordinate;

import CodeMonkey.utility.PVectorFuncs;
import processing.core.PVector;

public class LinearTransform implements AxisTransform, CoordinateTransform {

  private PVector inm, inM, inR, outm, outM, outR;

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

  public LinearTransform( float inMin, float inMax, float outMin, float outMax ) {

    this(
        new PVector( inMin, inMin, inMin ),
        new PVector( inMax, inMax, inMax ),
        new PVector( outMin, outMin, outMin ),
        new PVector( outMax, outMax, outMax ) );

  }

  public LinearTransform( PVector off, PVector scale ) {

    this.inm = new PVector( 0, 0, 0 );
    this.inR = new PVector( 1, 1, 1 );
    this.outm = off.copy( );
    this.outR = scale.copy( );

  }

  public LinearTransform( float off, float scale ) {

    this(
        new PVector( off, off, off ),
        new PVector( scale, scale, scale ) );

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
