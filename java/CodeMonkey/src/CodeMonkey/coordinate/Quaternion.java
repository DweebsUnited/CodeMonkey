package CodeMonkey.coordinate;

import CodeMonkey.utility.PVectorFuncs;
import processing.core.PVector;

public class Quaternion {

  private float w;
  private PVector v;

  public static Quaternion axisAngle( PVector axis, float angle ) {

    angle /= 2;

    axis = PVectorFuncs.multRet( axis, (float) Math.sin( angle ) );
    angle = (float) Math.cos( angle );

    Quaternion q = new Quaternion( axis, angle );

    return q.normalize( );

  }

  public Quaternion( PVector imag, float real ) {

    this( );

    this.set( imag, real );

  }

  public Quaternion( Quaternion q ) {

    this( );

    this.set( q );

  }

  public Quaternion( ) {

    this.w = 0;
    this.v = new PVector( 0, 0, 0 );

  }

  public Quaternion copy( ) {

    return new Quaternion( this );

  }

  public Quaternion set( Quaternion q ) {

    this.w = q.w;
    this.v.set( q.v );

    return this;

  }

  public Quaternion set( PVector imag, float real ) {

    this.w = real;
    this.v.set( imag );

    return this;

  }

  // This makes it not a unit quaternion
  private Quaternion add( Quaternion q ) {

    this.w += q.w;
    this.v.add( q.v );

    return this;

  }

  public Quaternion mult( Quaternion q ) {

    // w w' - v . v'
    // v x v´ + wv´ + w´v

    float w = this.w * q.w - this.v.dot( q.v );

    PVector v = this.v.cross( q.v );

    PVector tv = new PVector( );

    tv.set( q.v );
    tv.mult( this.w );
    v.add( tv );

    tv.set( this.v );
    tv.mult( q.w );
    v.add( tv );

    return this.set( v, w );

  }

  public static Quaternion mult( Quaternion q1, Quaternion q2 ) {

    return q1.copy( ).mult( q2 );

  }

  // This makes it not a unit quaternion
  private Quaternion mult( float c ) {

    this.w *= c;
    this.v.mult( c );

    return this;

  }

  public float dot( Quaternion q ) {

    return q.v.dot( this.v ) + this.w * q.w;

  }

  // This *should* always return 1
  public float mag( ) {

    return (float) Math.sqrt( this.dot( this ) );

  }

  public Quaternion normalize( ) {

    return this.mult( 1.0f / this.mag( ) );

  }

  public Quaternion conjugate( ) {

    this.v.mult( -1.0f );
    return this;

  }

  // This assumes we are a unit quat
  public Quaternion inverse( ) {

    return this.conjugate( );

  }

  // This assumes two unit quats
  public Quaternion concat( Quaternion q2 ) {

    return Quaternion.mult( q2, this );

  }

  public PVector rotate( PVector vec ) {

    return Quaternion.rotate( this, vec );

  }

  public static PVector rotate( Quaternion q, PVector v ) {

    return Quaternion.mult( q, new Quaternion( v, 0 ) ).mult( q.copy( ).inverse( ) ).v;

  }

  public static PVector rotate( PVector axis, float angle, PVector v ) {

    return Quaternion.rotate( Quaternion.axisAngle( axis, angle ), v );

  }

  public static Quaternion slerp( Quaternion a, Quaternion b, float t ) {

    float o = (float) Math.acos( a.dot( b ) );

    b = b.copy( );
    if( a.dot( b ) < 0 )
      b.mult( -1.0f );

    a = a.copy( ).mult( (float) Math.sin( ( 1.0 - t ) * o ) / (float) Math.sin( o ) );
    return a.add( b.mult( (float) Math.sin( t * o ) / (float) Math.sin( o ) ) ).normalize( );

  }

}
