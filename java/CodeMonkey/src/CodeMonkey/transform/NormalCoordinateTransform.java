package CodeMonkey.transform;

import processing.core.PVector;

public class NormalCoordinateTransform implements CoordinateTransform {

  private PVector mVal, MVal;
  private PVector rng;

  private CoordinateTransform trans;

  public NormalCoordinateTransform( CoordinateTransform trans, PVector minCoor, PVector maxCoor ) {

    this.trans = trans;

    this.mVal = trans.map( minCoor );
    this.MVal = trans.map( maxCoor );

    this.rng = this.MVal.copy( );
    this.rng.sub( this.mVal );

  }

  @Override
  public PVector map( PVector p ) {

    PVector c = this.trans.map( p );

    c.x = ( c.x - this.mVal.x ) / this.rng.x;
    c.x = ( c.x - this.mVal.y ) / this.rng.y;
    c.x = ( c.x - this.mVal.z ) / this.rng.z;

    return c;

  }

}
