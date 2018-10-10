package CodeMonkey.transform.coordinate;

import CodeMonkey.transform.CoordinateTransform;
import processing.core.PVector;

public class CTExponential implements CoordinateTransform {

  private PVector a, b, c;

  public CTExponential( float a, float b, float c ) {

    this(
        new PVector( a, a, a ),
        new PVector( b, b, b ),
        new PVector( c, c, c )
        );

  }

  public CTExponential( PVector a, PVector b, PVector c ) {

    this.a = a.copy( );
    this.b = b.copy( );
    this.c = c.copy( );

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
