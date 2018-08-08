package CodeMonkey.project;

import java.util.Random;

import CodeMonkey.spatial.Mirror;
import CodeMonkey.spatial.PerlinMirror;
import CodeMonkey.spatial.Ray;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class TitaniumSheepdog extends PApplet {

  private final int NUM_RAYS = 256;
  private final int NUM_TGT = 256;
  private final int TGT_SD = 256;

  private Random rng = new Random( );
  private Mirror mirr;

  private PVector lend;
  private PVector rend;

  private boolean drawing = true;
  private PGraphics canvas;

  private int a;
  private int b;
  private int c;

  // Some 3D debugging
  //  private ArrayList<Segment> lines = new ArrayList<Segment>( );
  //  private ArrayList<Segment> bounces = new ArrayList<Segment>( );

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.TitaniumSheepdog" );

  }

  @Override
  public void settings( ) {

    this.size( 720, 640 );

  }

  @Override
  public void setup( ) {

    this.colorMode( HSB, 360, 100, 100, 100 );

    this.a = this.color(  97, 57, 47, 10 );
    this.b = this.color( 244, 52, 35, 10 );
    this.c = this.color(  56, 57, 54, 10 );

    this.canvas = this.createGraphics( 1920, 1080 );

    this.canvas.beginDraw( );
    this.canvas.noStroke( );
    this.canvas.background( 255 );
    this.canvas.endDraw( );

    // 3D debugging
    //    this.camera( 2, 2, 5, 0.5f, 0.25f, 0, 0, 0, 1 );
    //    this.ortho( -1, 1, -1, 1, 0, 100 );


    this.mirr = new PerlinMirror( this, this.rng );

    this.lend = new PVector( 0.45f, 2.5f, 2.5f );
    this.rend = new PVector( 0.55f, 2.5f, 2.5f );


    // Launch rays from a line above the mirror
    //    for( int rdx = 0; rdx < this.NUM_RAYS; ++rdx ) {
    //
    //      PVector o = PVector.lerp( this.lend, this.rend, this.rng.nextFloat( ) );
    //      Ray r = Ray.fromTwoPoints( o, new PVector( o.x, this.rng.nextFloat( ) / 2.0f, 0 ) );
    //      r.normalize( );
    //
    //      Ray ref = this.mirr.bounce( r );
    //
    //      if( ref == null )
    //        continue;
    //
    //      // 3D debugging
    //            this.lines.add( new Segment( o, ref.o ) );
    //
    //      // Where the reflection hits the screen ( y = 0 )
    //      PVector p = ref.atT( -ref.o.y / ref.d.y );
    //
    //      if( p.x < 0 || p.x > 1 || p.z < 0 || p.z > 1 )
    //        continue;
    //
    //      //      System.out.println( String.format( "%f, %f, %f -> %f, %f, %f -> %f, %f, %f",
    //      //          r.o.x, r.o.y, r.o.z,
    //      //          ref.o.x, ref.o.y, ref.o.z,
    //      //          p.x, p.y, p.z ) );
    //
    //      // 3D debugging
    //            this.bounces.add( new Segment( ref.o, p ) );
    //
    //    }

  }

  @Override
  public void draw( ) {

    if( ! this.drawing )
      return;

    this.canvas.beginDraw( );

    for( int rdx = 0; rdx < this.NUM_RAYS; ++rdx ) {

      PVector o = PVector.lerp( this.lend, this.rend, this.rng.nextFloat( ) );
      PVector tgtCent = new PVector( this.rng.nextFloat( ), this.rng.nextFloat( ) / 2.0f, 0 );
      for( int tdx = 0; tdx < this.NUM_TGT; ++tdx ) {

        // TODO: gaussian dist tgt around cent

        Ray r = Ray.fromTwoPoints( o, tgt );
        r.normalize( );

        Ray ref = this.mirr.bounce( r );

        if( ref == null )
          continue;

        PVector norm = this.mirr.normal( ref.o );

        // Where the reflection hits the screen ( y = 0 )
        PVector p = ref.atT( -ref.o.y / ref.d.y );

        if( p.x < 0 || p.x > 1 || p.z < 0 || p.z > 1 )
          continue;

        int col;
        float mh;

        // Per Mom,
        //      float ah = this.noise( p.x, p.z, 0.7439652f );
        //      float bh = this.noise( p.x, p.z, 0.4729439f );
        //      float ch = this.noise( p.x, p.z, 0.6627156f );

        // per Dad
        //      float ah = this.noise( p.x, p.z, 0.7365218f );
        //      float bh = this.noise( p.x, p.z, 0.9362519f );
        //      float ch = this.noise( p.x, p.z, 0.0298198f );

        // Try choosing based on greatest noise( intersection point )
        //      if( ah > bh ) {
        //        col = this.a;
        //        mh = ah;
        //      } else {
        //        col = this.b;
        //        mh = bh;
        //      }
        //      if( ch > mh ) {
        //        col = this.c;
        //      }

        // Try choosing based on greatest norm axis
        if( norm.x > norm.y ) {
          col = this.a;
          mh = norm.x;
        } else {
          col = this.b;
          mh = norm.y;
        }
        //      if( norm.z > mh ) {
        //        col = this.c;
        //      }

        this.canvas.fill( col );

        this.canvas.ellipse( p.x * this.canvas.pixelWidth, ( 1 - p.z ) * this.canvas.pixelHeight, 5, 5 );

      }

    }

    this.canvas.endDraw( );

    this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );

    //    this.background( 0 );
    //
    //    // 3D debugging
    //    this.noStroke( );
    //    this.fill( 127 );
    //
    //    this.beginShape( TRIANGLE_STRIP );
    //    this.vertex( 1, 0, 0 );
    //    this.vertex( 0, 0, 0 );
    //    this.vertex( 1, 0.5f, 0 );
    //    this.vertex( 0, 0.5f, 0 );
    //    this.endShape( );
    //
    //    this.noStroke( );
    //    this.fill( 200 );
    //
    //    this.beginShape( TRIANGLE_STRIP );
    //    this.vertex( 1, 0, 0 );
    //    this.vertex( 0, 0, 0 );
    //    this.vertex( 1, 0, 1 );
    //    this.vertex( 0, 0, 1 );
    //    this.endShape( );
    //
    //    this.stroke( 0, 255, 0 );
    //    this.noFill( );
    //
    //    for( Segment s : this.lines ) {
    //
    //      this.line( s.a.x, s.a.y, s.a.z, s.b.x, s.b.y, s.b.z );
    //
    //    }
    //
    //    this.stroke( 0, 0, 255 );
    //    this.noFill( );
    //
    //    for( Segment s : this.bounces ) {
    //
    //      this.line( s.a.x, s.a.y, s.a.z, s.b.x, s.b.y, s.b.z );
    //
    //    }

  }

  @Override
  public void keyPressed( ) {

    if( this.key == 'w' ) {

      this.saveFrame( "/Users/ozzy/Desktop/TitaniumSheepdog.png" );

    } else if( this.key == ' ' ) {

      this.drawing = !this.drawing;

    } else if( this.key == 'q' ) {

      this.canvas.beginDraw( );
      this.canvas.background( 255 );
      this.canvas.endDraw( );

      this.noiseSeed( this.rng.nextInt( ) );
      this.randomSeed( this.rng.nextInt( ) );
      this.rng = new Random( this.rng.nextInt( ) );

    } else if( this.key == 'a' ) {

      this.mirr.reset( );

    }

  }

}
