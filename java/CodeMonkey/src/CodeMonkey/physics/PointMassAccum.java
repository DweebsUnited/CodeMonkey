package CodeMonkey.physics;

import processing.core.PVector;

public class PointMassAccum {

  private PointMass m;

  private PVector F;
  public PVector p;

  public PointMassAccum( PVector p, float m ) {

    this( p.x, p.y, m );

  }

  public PointMassAccum( float x, float y, float m ) {

    this.m = new PointMass( x, y, m );

    this.F = new PVector( 0, 0, 0 );
    this.p = new PVector( x, y );

  }

  public void accum( PVector F ) {

    this.F.add( F );

  }

  public void verlet( float dt ) {

    this.m.verlet( this.F, dt );
    this.F.set( 0, 0, 0 );
    this.p.set( this.m.c );

  }

  public void set( PVector p ) {

    this.m.c.set( p );
    this.m.co.set( p );

  }

}
