package CodeMonkey.project;

import java.util.Random;

import CodeMonkey.spatial.Mirror;
import CodeMonkey.spatial.PerlinMirror;
import CodeMonkey.spatial.Ray;
import CodeMonkey.utility.PVectorFuncs;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class TitaniumSheepdog extends PApplet {

  private final int NUM_RAYS = 16;
  private final int NUM_TGT = 2;
  private final float TGT_SD = 0.005f;
  private final float ANG_REJECT = (float) Math.PI / 10f;

  private Random rng = new Random( );
  private Mirror mirr;

  private PVector light;

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

    this.size( 720, 640 );

  }

  @Override
  public void setup( ) {

    this.colorMode( HSB, 360, 100, 100, 100 );

    this.a = this.color(  97, 57, 47, 25 );
    this.b = this.color( 244, 52, 35, 25 );

    this.canvas = this.createGraphics( 1920, 1080 );

    this.canvas.beginDraw( );
    this.canvas.noStroke( );
    this.canvas.background( 255 );
    this.canvas.endDraw( );


    this.mirr = new PerlinMirror( this, this.rng );

    this.light = new PVector( 0.5f, 2.5f, 2.5f );

  }

  @Override
  public void draw( ) {

    if( ! this.drawing )
      return;

    this.canvas.beginDraw( );

    for( int rdx = 0; rdx < this.NUM_RAYS; ++rdx ) {

      PVector tgtCent = new PVector( this.rng.nextFloat( ), this.rng.nextFloat( ) / 2, 0 );

      for( int tdx = 0; tdx < this.NUM_TGT; ++tdx ) {

        PVector tgt = PVectorFuncs.addRet( tgtCent, new PVector( (float) this.rng.nextGaussian( ) * this.TGT_SD, (float) this.rng.nextGaussian( ) * this.TGT_SD ) );

        Ray r = Ray.fromTwoPoints( this.light, tgt );
        r.normalize( );

        Ray ref = this.mirr.bounce( r );

        if( ref == null )
          continue;

        PVector norm = this.mirr.normal( ref.o );

        // Reject norms that are too close to Z, ie too flat
        if( PVector.angleBetween( norm, new PVector( 0, 0, 1 ) ) < this.ANG_REJECT )
          continue;

        // Where the reflection hits the screen ( y = 0 )
        PVector p = ref.atT( -ref.o.y / ref.d.y );

        // Compensate for terrain height
        p.add( new PVector( 0, 0, 0.25f ) );

        if( p.x < 0 || p.x > 1 || p.z < 0 || p.z > 1 )
          continue;

        int col;

        // Per Mom,
        //        float ah = this.noise( p.x, p.z, 0.7439652f );
        //      float bh = this.noise( p.x, p.z, 0.4729439f );

        // per Dad
        //      float ah = this.noise( p.x, p.z, 0.7365218f );
        //      float bh = this.noise( p.x, p.z, 0.9362519f );

        // Try choosing based on greatest noise( intersection point )
        //      if( ah > bh ) {
        //        col = this.a;
        //      } else {
        //        col = this.b;
        //      }

        // Try choosing based on greatest norm axis
        if( norm.x > norm.y ) {
          col = this.a;
        } else {
          col = this.b;
        }

        // Try choosing based on prob of a
        //        if( this.rng.nextFloat( ) < ah ) {
        //          col = this.a;
        //        } else {
        //          col = this.b;
        //        }

        this.canvas.fill( col );

        this.canvas.ellipse( p.x * this.canvas.pixelWidth, ( 1 - p.z ) * this.canvas.pixelHeight, 5, 5 );

      }

    }

    this.canvas.endDraw( );

    this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );

  }

  @Override
  public void keyPressed( ) {

    if( this.key == 'w' ) {

      this.canvas.save( "/Users/ozzy/Desktop/TitaniumSheepdog.png" );

    } else if( this.key == ' ' ) {

      this.drawing = !this.drawing;

    } else if( this.key == 'q' ) {

      this.canvas.beginDraw( );
      this.canvas.background( 255 );
      this.canvas.endDraw( );

      this.noiseSeed( this.rng.nextInt( ) );
      this.randomSeed( this.rng.nextInt( ) );
      this.rng = new Random( this.rng.nextInt( ) );

      this.mirr.reset( );

    } else if( this.key == 'g' ) {
      this.mFac *= 2;
      System.out.println( String.format( "Move size: %f", this.mFac ) );
    } else if( this.key == 'd' ) {
      this.mFac /= 2;
      System.out.println( String.format( "Move size: %f", this.mFac ) );
    } else if( this.key == 'j' ) {
      this.light.add( new PVector( 0, 0, this.mFac ) );
      System.out.println( String.format( "Light: %f, %f, %f", this.light.x, this.light.y, this.light.z ) );
    } else if( this.key == 'h' ) {
      this.light.sub( new PVector( 0, 0, this.mFac ) );
      System.out.println( String.format( "Light: %f, %f, %f", this.light.x, this.light.y, this.light.z ) );
    } else if( this.key == 'l' ) {
      this.light.add( new PVector( this.mFac, 0, 0 ) );
      System.out.println( String.format( "Light: %f, %f, %f", this.light.x, this.light.y, this.light.z ) );
    } else if( this.key == 'n' ) {
      this.light.sub( new PVector( this.mFac, 0, 0 ) );
      System.out.println( String.format( "Light: %f, %f, %f", this.light.x, this.light.y, this.light.z ) );
    } else if( this.key == 'u' ) {
      this.light.add( new PVector( 0, this.mFac, 0 ) );
      System.out.println( String.format( "Light: %f, %f, %f", this.light.x, this.light.y, this.light.z ) );
    } else if( this.key == 'e' ) {
      this.light.sub( new PVector( 0, this.mFac, 0 ) );
      System.out.println( String.format( "Light: %f, %f, %f", this.light.x, this.light.y, this.light.z ) );
    }

  }

}
