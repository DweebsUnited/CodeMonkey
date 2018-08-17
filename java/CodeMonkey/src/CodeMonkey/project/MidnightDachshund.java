package CodeMonkey.project;

import CodeMonkey.coordinate.Quaternion;
import processing.core.PApplet;
import processing.core.PVector;

public class MidnightDachshund extends PApplet {

  class Triangle {

    private float r = 1.0f;
    private PVector o = new PVector( );
    private float rot = 0;
    private PVector norm = new PVector( 0, 0, 1 );

    private final PVector up = new PVector( 0, 0, 1 );

    private float i, h;

    public PVector a = new PVector( );
    public PVector b = new PVector( );
    public PVector c = new PVector( );

    public Triangle( float r, PVector o, float rot, PVector norm ) {

      this.setRad( r );
      this.setO( o );
      this.setRot( rot );
      this.setNorm( norm );

      this.build( );

    }

    public void setRad( float r ) {

      this.r = r;

    }

    public void setO( PVector o ) {

      this.o.set( o );

    }

    public void setRot( float rot ) {

      this.rot = (float) Math.toRadians( rot );

    }

    public void setNorm( PVector norm ) {

      this.norm = norm.copy( );

      this.norm.normalize( );

    }

    private void build( ) {

      this.h = this.r * (float) Math.cos( Math.toRadians( 30 ) );
      this.i = this.r * (float) Math.sin( Math.toRadians( 30 ) );

      this.a.set(  0,       1,      0 );
      this.b.set( -this.i, -this.h, 0 );
      this.c.set(  this.i, -this.h, 0 );

      PVector axis = new PVector( );
      PVector.cross( this.up, this.norm, axis );

      Quaternion q = Quaternion.axisAngle( axis, PVector.angleBetween( this.up, this.norm ) );

      q = q.concat( Quaternion.axisAngle( this.norm, this.rot ) );

      this.a.set( q.rotate( this.a ) );
      this.b.set( q.rotate( this.b ) );
      this.c.set( q.rotate( this.c ) );

      this.a.add( this.o );
      this.b.add( this.o );
      this.c.add( this.o );

    }

  }

  private Triangle t;

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.MidnightDachshund" );

  }

  @Override
  public void settings( ) {

    this.size( 720, 640 );

  }

  @Override
  public void setup( ) {

    this.background( 255 );
    this.noFill( );
    this.stroke( 0 );

    this.t = new Triangle(
        45,
        new PVector( this.pixelWidth / 2, this.pixelHeight / 2, 0 ),
        45,
        new PVector( 0, 0, 1 ) );

  }

  @Override
  public void draw( ) {

    this.background( 255 );

    this.line( this.t.a.x, this.t.a.y, this.t.b.x, this.t.b.y );
    this.line( this.t.b.x, this.t.b.y, this.t.c.x, this.t.c.y );
    this.line( this.t.c.x, this.t.c.y, this.t.a.x, this.t.a.y );

  }

}
