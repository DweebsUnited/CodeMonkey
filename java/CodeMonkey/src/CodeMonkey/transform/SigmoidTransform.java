package CodeMonkey.transform;

import processing.core.PVector;

public class SigmoidTransform implements AxisTransform, CoordinateTransform {

  PVector s, o;

  public SigmoidTransform( float s, float o ) {

    this.s = new PVector( s, s, s );
    this.o = new PVector( o, o, o );

  }

  public SigmoidTransform( PVector s, PVector o  ) {

    this.s = s.copy( );
    this.o = o.copy( );

  }

  private float map( float c, float s, float o ) {

    return (float) ( 1.0f / ( 1.0f + Math.exp( -s * ( c - o ) ) ) );

  }

  @Override
  public float map( float c ) {

    return this.map( c, this.s.x, this.o.x );

  }

  @Override
  public PVector map( PVector p ) {

    return new PVector(
        this.map( p.x, this.s.x, this.o.x ),
        this.map( p.y, this.s.y, this.o.y ),
        this.map( p.z, this.s.z, this.o.z )
        );

  }

}
