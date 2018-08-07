package CodeMonkey.spatial;

import processing.core.PVector;

public class Ray {

  public PVector o;
  public PVector d;

  public Ray( ) {

    this( new PVector( 0, 0, 0 ), new PVector( 1, 0, 0 ) );

  }

  public Ray( Ray r ) {

    this( r.o, r.d );

  }

  public Ray( PVector o, PVector d ) {

    this.o = o.copy( );
    this.d = d.copy( );

  }

  public Ray copy( ) {

    return new Ray( this );

  }

  public static Ray fromTwoPoints( PVector src, PVector tgt ) {

    tgt = tgt.copy( );
    tgt.sub( src );
    tgt.normalize( );
    return new Ray( src, tgt );

  }

  public PVector atT( float t ) {

    PVector res = this.d.copy( );
    res.mult( t );
    res.add( this.o );

    return res;

  }

  public Ray normalize( ) {

    this.d.normalize( );
    return this;

  }

}
