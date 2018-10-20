package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.spatial.Ray;
import CodeMonkey.spatial.RefractingPlane;
import CodeMonkey.spatial.Sphere;
import CodeMonkey.utility.PVectorFuncs;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class ShatteredGlass extends PApplet {

  private PGraphics canvas;

  private Random rng = new Random( );

  private final float maxRefrac = 5.0f;
  private final float minRefrac = 1f / this.maxRefrac;

  private Sphere accum;

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.ShatteredGlass" );

  }

  @Override
  public void settings( ) {

    this.size( 720, 640 );

  }

  @Override
  public void setup( ) {

    this.canvas = this.createGraphics( 720, 640 );

    this.background( 0 );

    float rad = Math.min( this.pixelWidth, this.pixelHeight ) / 2;
    this.accum = new Sphere( new PVector( this.pixelWidth / 2, this.pixelHeight / 2 ), rad );

    // Make a bunch of random refracting planes
    // Scatter them with random 2d normals

    ArrayList<RefractingPlane> planes = new ArrayList<>( );

    for( int pdx = 0; pdx < 16; ++pdx ) {

      PVector n = PVector.random2D( );
      PVector o = new PVector(
          this.rng.nextInt( this.pixelWidth / 2 ) + this.pixelWidth / 4,
          this.rng.nextInt( this.pixelHeight / 2 ) + this.pixelHeight / 4 );

      RefractingPlane p = new RefractingPlane(
          n,
          o,
          this.rng.nextFloat( ) * ( this.maxRefrac - this.minRefrac ) + this.minRefrac );

      PVector r = p.getRight( );
      PVector u = p.getUp();

      this.stroke( 255 );
      this.line( o.x, o.y, o.x + n.x * 15, o.y + n.y * 15 );
      this.stroke( 255, 0, 0 );
      this.line( o.x - r.x * 15, o.y - r.y * 15, o.x + r.x * 15, o.y + r.y * 15 );
      this.line( o.x - u.x * 15, o.y - u.y * 15, o.x + u.x * 15, o.y + u.y * 15 );

      planes.add( p );

    }

    this.stroke( 255 );

    // Pick random point on circle, cast random interior direction, kill on nearest contact with circle or plane
    for( int rdx = 0; rdx < 128; ++rdx ) {

      // Origin
      PVector lo = new PVector( rad, 0 );
      lo.rotate( (float) ( this.rng.nextFloat( ) * 2 * Math.PI ) );
      lo.add( new PVector( this.pixelWidth / 2, this.pixelHeight / 2 ) );
      // Direction is center +- PI / 4
      PVector ld = new PVector( this.pixelWidth / 2, this.pixelHeight / 2 );
      ld.rotate( (float) ( this.rng.nextFloat( ) * Math.PI - Math.PI / 2 ) / 2f );

      // Now add a tiny amount of d to o otherwise we intersect the sphere immediately
      lo.add( PVectorFuncs.multRet( ld, 0.001f ) );

      // Make the ray object
      Ray r = new Ray( lo, ld );

      // Intersect with sphere
      float tcirc = this.accum.intersect( r );


      PVector ipt = r.atT( tcirc );

      this.line( lo.x, lo.y, ipt.x, ipt.y );

    }

  }

  @Override
  public void draw( ) {

    this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );

  }

}
