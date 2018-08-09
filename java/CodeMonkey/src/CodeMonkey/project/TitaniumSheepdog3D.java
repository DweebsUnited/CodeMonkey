package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.coordinate.Segment;
import CodeMonkey.spatial.Mirror;
import CodeMonkey.spatial.PerlinMirror;
import CodeMonkey.spatial.Ray;
import CodeMonkey.utility.PVectorFuncs;
import processing.core.PApplet;
import processing.core.PVector;

public class TitaniumSheepdog3D extends PApplet {

  private final int NUM_RAYS = 4;
  private final int NUM_TGT = 4;
  private final float TGT_SD = 0.005f;
  private final float ANG_REJECT = (float) Math.PI / 7.5f;

  private Random rng = new Random( );
  private Mirror mirr;

  private PVector lend;
  private PVector rend;

  // Some 3D debugging
  private ArrayList<Segment> lines = new ArrayList<Segment>( );
  private ArrayList<Segment> bounces = new ArrayList<Segment>( );

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.TitaniumSheepdog3D" );

  }

  @Override
  public void settings( ) {

    this.size( 720, 640, P3D );

  }

  @Override
  public void setup( ) {

    // 3D debugging
    this.camera( 2, 2, 5, 0.5f, 0.25f, 0, 0, 0, -1 );
    this.ortho( -1, 1, -1, 1, 0, 100 );


    this.mirr = new PerlinMirror( this, this.rng );

    this.lend = new PVector( 0.45f, 2.5f, 2.5f );
    this.rend = new PVector( 0.55f, 2.5f, 2.5f );

  }

  @Override
  public void draw( ) {

    for( int rdx = 0; rdx < this.NUM_RAYS; ++rdx ) {

      PVector o = PVector.lerp( this.lend, this.rend, this.rng.nextFloat( ) );
      PVector tgtCent = new PVector( this.rng.nextFloat( ), this.rng.nextFloat( ) / 2.0f, 0 );

      for( int tdx = 0; tdx < this.NUM_TGT; ++tdx ) {

        // TODO: gaussian dist tgt around cent

        PVector tgt = PVectorFuncs.addRet( tgtCent, new PVector( (float) this.rng.nextGaussian( ) * this.TGT_SD, (float) this.rng.nextGaussian( ) * this.TGT_SD ) );

        Ray r = Ray.fromTwoPoints( o, tgt );
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

        this.lines.add( new Segment( o, ref.o ) );

        if( p.x < 0 || p.x > 1 || p.z < 0 || p.z > 1 )
          continue;

        this.bounces.add( new Segment( ref.o, p ) );

      }

    }

    this.background( 0 );

    // 3D debugging
    this.noStroke( );
    this.fill( 127 );

    this.beginShape( TRIANGLE_STRIP );
    this.vertex( 1, 0, 0 );
    this.vertex( 0, 0, 0 );
    this.vertex( 1, 0.5f, 0 );
    this.vertex( 0, 0.5f, 0 );
    this.endShape( );

    this.noStroke( );
    this.fill( 200 );

    this.beginShape( TRIANGLE_STRIP );
    this.vertex( 1, 0, 0 );
    this.vertex( 0, 0, 0 );
    this.vertex( 1, 0, 1 );
    this.vertex( 0, 0, 1 );
    this.endShape( );

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

  @Override
  public void keyPressed( ) {

    if( this.key == 'w' ) {

      this.saveFrame( "/Users/ozzy/Desktop/TitaniumSheepdog3D.png" );

    } else if( this.key == 'q' ) {

      this.noiseSeed( this.rng.nextInt( ) );
      this.randomSeed( this.rng.nextInt( ) );
      this.rng = new Random( this.rng.nextInt( ) );

    } else if( this.key == 'a' ) {

      this.mirr.reset( );

    }

  }

}
