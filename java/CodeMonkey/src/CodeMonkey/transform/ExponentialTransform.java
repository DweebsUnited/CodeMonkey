package CodeMonkey.transform;

import processing.core.PVector;

public class ExponentialTransform implements AxisTransform, CoordinateTransform {

  private PVector a;
  private PVector b;
  private PVector c;

  public ExponentialTransform( float a, float b, float c ) {

    this.a = new PVector( a, a, a );
    this.b = new PVector( b, b, b );
    this.c = new PVector( c, c, c );

  }

  public ExponentialTransform( PVector a, PVector b, PVector c ) {

    this.a = a.copy( );
    this.b = b.copy( );
    this.c = c.copy( );

  }

  @Override
  public float map( float c ) {

    return this.a.x * (float) Math.pow( c, this.b.x ) + this.c.x;

  }

  @Override
  public PVector map( PVector p ) {

    return new PVector(
        this.a.x * (float) Math.pow( this.b.x, p.x ) + this.c.x,
        this.a.y * (float) Math.pow( this.b.y, p.y ) + this.c.y,
        this.a.z * (float) Math.pow( this.b.z, p.z ) + this.c.z
        );

  }

}
