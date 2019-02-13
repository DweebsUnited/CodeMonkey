package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.spatial.Mirror;
import CodeMonkey.spatial.Ray;
import CodeMonkey.spatial.Segment;
import CodeMonkey.spatial.TexturedMirror;
import CodeMonkey.transform.AxisTransform;
import CodeMonkey.transform.CoordinateTransform;
import CodeMonkey.transform.axis.ATLinear;
import CodeMonkey.transform.coordinate.CTLinear;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class TitaniumSheepdog extends PApplet {

  private static String CM = "/Users/ozzy/Documents/CodeMonkey/";
  private static String dataDir = CM + "data/";

  //Some 3D debugging
  private boolean is2D = true;
  private ArrayList<Segment> lines = new ArrayList<Segment>( );
  private ArrayList<Segment> bounces = new ArrayList<Segment>( );

  private final int NUM_RAYS_2D = 1024;
  private final int NUM_RAYS_3D = 1;

  private final int TEX_WIDE = 1024;
  private final int TEX_TALL = 1024;
  private final float DERIV_STEP = 0.01f;
  private CoordinateTransform nTrans;
  private AxisTransform nScale;
  private PImage normMap;

  private Random rng = new Random( );

  private Mirror mirr;

  private PVector lend;
  private PVector rend;

  private boolean drawing = true;
  private PGraphics canvas;

  private int a;
  private int b;

  private float mFac = 0.1f;

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.TitaniumSheepdog" );

  }

  @Override
  public void settings( ) {

    if( this.is2D )
      this.size( 720, 640 );
    else
      this.size( 720, 640, P3D );

  }

  private void makeNormMap( ) {

    this.colorMode( RGB, 255 );

    PVector nc = new PVector( );

    this.normMap = this.createImage( this.TEX_WIDE, this.TEX_TALL, RGB );

    float minAng = Float.POSITIVE_INFINITY;
    float maxAng = Float.NEGATIVE_INFINITY;
    float avgAng = 0;

    this.normMap.loadPixels( );
    for( int dy = 0; dy < this.TEX_TALL; ++dy ) {
      for( int dx = 0; dx < this.TEX_WIDE; ++dx ) {

        nc.set( dx / (float)this.TEX_WIDE, dy / (float)this.TEX_TALL );
        nc = this.nTrans.map( nc );

        float nx = this.nScale.map( this.noise( nc.x - this.DERIV_STEP, nc.y                   ) );
        float ny = this.nScale.map( this.noise( nc.x,                   nc.y - this.DERIV_STEP ) );
        float px = this.nScale.map( this.noise( nc.x + this.DERIV_STEP, nc.y                   ) );
        float py = this.nScale.map( this.noise( nc.x,                   nc.y + this.DERIV_STEP ) );

        PVector sx = new PVector( 2 * this.DERIV_STEP, 0, px - nx );
        sx.normalize( );
        PVector sy = new PVector( 0, 2 * this.DERIV_STEP, py - ny );
        sy.normalize( );

        PVector n = sx.cross( sy );
        n.normalize( );

        float a = PVector.angleBetween( n, new PVector( 0, 0, 1 ) );

        if( a > maxAng )
          maxAng = a;
        if( a < minAng )
          minAng = a;
        avgAng += a;

        this.normMap.pixels[ dx + dy * this.TEX_WIDE ] = this.color( n.x * 255, n.y * 255, n.z * 255 );

      }
    }
    this.normMap.updatePixels( );

    avgAng /= this.TEX_WIDE * this.TEX_TALL;

    //    System.out.println( String.format( "Min: %f", minAng ) );
    //    System.out.println( String.format( "Max: %f", maxAng ) );
    //    System.out.println( String.format( "Avg: %f", avgAng ) );

  }

  @Override
  public void setup( ) {

    if( ! this.is2D ) {
      this.camera( 2, 2, 5, 0.5f, 0.25f, 0, 0, 0, -1 );
      this.ortho( -1, 1, -1, 1, 0, 100 );
    }

    this.colorMode( HSB, 360, 100, 100, 100 );
    this.b = this.color(  97, 57, 47, 5 );
    this.a = this.color( 244, 52, 35, 5 );

    //    this.b = this.color( 0, 25 );
    //    this.a = this.color( 0, 25 );

    this.canvas = this.createGraphics( 3840, 2160 );

    this.canvas.beginDraw( );
    this.canvas.noStroke( );
    this.canvas.background( 255 );
    this.canvas.endDraw( );


    this.nTrans = new CTLinear(
        new PVector( this.rng.nextFloat( ) * 10, this.rng.nextFloat( ) * 10 ),
        new PVector( this.rng.nextFloat( ) * 4, this.rng.nextFloat( ) * 4 ) );
    this.nScale = new ATLinear( 0, 0.25f );
    this.makeNormMap( );

    this.mirr = new TexturedMirror(
        1f, 0.5f,
        new PVector( 0, 0, 1 ),
        new PVector( 0.5f, 0.25f, 0 ),
        this.normMap );

    this.lend = new PVector( 0.05f, 2.5f, 2.0f );
    this.rend = new PVector( 0.95f, 2.5f, 2.0f );

  }

  @Override
  public void draw( ) {

    if( ! this.drawing && this.is2D )
      return;

    this.canvas.beginDraw( );

    int c3d = 0;

    for( int rdx = 0; rdx < this.NUM_RAYS_2D; ++rdx ) {

      PVector o = PVector.lerp( this.lend, this.rend, this.rng.nextFloat( ) );
      PVector tgtCent = new PVector( this.rng.nextFloat( ), this.rng.nextFloat( ) / 2.0f, 0 );

      Ray r = Ray.fromTwoPoints( o, tgtCent );

      Ray ref = this.mirr.bounce( r );

      if( ref == null )
        continue;

      // Where the reflection hits the screen ( y = 0 )
      PVector p = ref.atT( -ref.o.y / ref.d.y );

      // Compensate for terrain height
      p.add( new PVector( 0, 0, 0.25f ) );

      if( p.x < 0 || p.x > 1 || p.z < 0 || p.z > 1 ) {
        //        System.out.println( "Bounceout" );
        continue;
      }

      int col;

      // Per Mom,
      float ah = this.noise( p.x, p.z, 0.7439652f );
      float bh = this.noise( p.x, p.z, 0.4729439f );

      // per Dad
      //      float ah = this.noise( p.x, p.z, 0.7365218f );
      //      float bh = this.noise( p.x, p.z, 0.9362519f );

      // Try choosing based on greatest noise( intersection point )
      //      if( ah > bh ) {
      //        col = this.a;
      //      } else {
      //        col = this.b;
      //      }

      // Normalize probabilities, pick by p( A )
      ah = ( ah + bh ) / 2;
      col = this.rng.nextFloat( ) > ah ? this.a : this.b;

      this.canvas.fill( col );
      this.canvas.ellipse( p.x * this.canvas.pixelWidth, ( 1 - p.z ) * this.canvas.pixelHeight, 5, 2 );
      //      System.out.println( String.format( "Hit: %f, %f", p.x, p.z ) );

      if( ! this.is2D && c3d++ < this.NUM_RAYS_3D ) {
        this.lines.add(   new Segment( o,     tgtCent ) );
        this.bounces.add( new Segment( ref.o, p       ) );
      }

      //      }

    }

    this.canvas.endDraw( );

    if( this.is2D )
      this.draw2D( );
    else
      this.draw3D( );

  }

  private void draw2D( ) {

    this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );
    //    this.image( this.normMap, 0, 0, this.pixelWidth, this.pixelHeight );

  }

  private void draw3D( ) {

    this.background( 0 );

    // 3D debugging
    this.noStroke( );
    this.fill( 127 );

    // Mirror
    this.beginShape( TRIANGLE_STRIP );
    this.texture( this.normMap );
    this.vertex( 1, 0, 0,    1, 0 );
    this.vertex( 0, 0, 0,    0, 0 );
    this.vertex( 1, 0.5f, 0, 1, 1 );
    this.vertex( 0, 0.5f, 0, 0, 1 );
    this.endShape( );

    this.noStroke( );
    this.fill( 200 );

    this.beginShape( TRIANGLE_STRIP );
    this.texture( this.canvas );
    this.vertex( 1, 0, 0, 1, 0 );
    this.vertex( 0, 0, 0, 0, 0 );
    this.vertex( 1, 0, 1, 1, 1 );
    this.vertex( 0, 0, 1, 0, 1 );
    this.endShape( );

    if( this.drawing ) {

      this.stroke( 0, 255, 0 );
      this.noFill( );

      for( Segment s : this.lines ) {

        this.line( s.a.x, s.a.y, s.a.z, s.b.x, s.b.y, s.b.z );

      }

      this.stroke( 0, 0, 255 );
      this.noFill( );

      for( Segment s : this.bounces ) {

        this.line( s.a.x, s.a.y, s.a.z, s.b.x, s.b.y, s.b.z );

      }

    }

  }

  @Override
  public void keyPressed( ) {

    if( this.key == 'w' ) {

      this.canvas.save( dataDir + "TitaniumSheepdog.png" );

    } else if( this.key == ' ' ) {

      this.drawing = !this.drawing;

    } else if( this.key == 'q' ) {

      this.canvas.beginDraw( );
      this.canvas.background( 255 );
      this.canvas.endDraw( );

      this.noiseSeed( this.rng.nextInt( ) );
      this.randomSeed( this.rng.nextInt( ) );
      this.rng = new Random( this.rng.nextInt( ) );

      this.makeNormMap( );

    } else if( this.key == 'g' ) {
      this.mFac *= 2;
      System.out.println( String.format( "Move size: %f", this.mFac ) );
    } else if( this.key == 'd' ) {
      this.mFac /= 2;
      System.out.println( String.format( "Move size: %f", this.mFac ) );
    }
    //    else if( this.key == 'j' ) {
    //      this.light.add( new PVector( 0, 0, this.mFac ) );
    //      System.out.println( String.format( "Light: %f, %f, %f", this.light.x, this.light.y, this.light.z ) );
    //    } else if( this.key == 'h' ) {
    //      this.light.sub( new PVector( 0, 0, this.mFac ) );
    //      System.out.println( String.format( "Light: %f, %f, %f", this.light.x, this.light.y, this.light.z ) );
    //    } else if( this.key == 'l' ) {
    //      this.light.add( new PVector( this.mFac, 0, 0 ) );
    //      System.out.println( String.format( "Light: %f, %f, %f", this.light.x, this.light.y, this.light.z ) );
    //    } else if( this.key == 'n' ) {
    //      this.light.sub( new PVector( this.mFac, 0, 0 ) );
    //      System.out.println( String.format( "Light: %f, %f, %f", this.light.x, this.light.y, this.light.z ) );
    //    } else if( this.key == 'u' ) {
    //      this.light.add( new PVector( 0, this.mFac, 0 ) );
    //      System.out.println( String.format( "Light: %f, %f, %f", this.light.x, this.light.y, this.light.z ) );
    //    } else if( this.key == 'e' ) {
    //      this.light.sub( new PVector( 0, this.mFac, 0 ) );
    //      System.out.println( String.format( "Light: %f, %f, %f", this.light.x, this.light.y, this.light.z ) );
    //    }

  }

}
