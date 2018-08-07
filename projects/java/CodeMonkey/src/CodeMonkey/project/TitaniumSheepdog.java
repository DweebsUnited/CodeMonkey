package CodeMonkey.project;

import java.util.Random;

import CodeMonkey.spatial.Mirror;
import CodeMonkey.spatial.PerlinMirror;
import CodeMonkey.spatial.Ray;
import processing.core.PApplet;
import processing.core.PVector;

public class TitaniumSheepdog extends PApplet {

  private final int NUM_RAYS = 1024;

  private Random rng = new Random( );
  private Mirror mirr;

  private PVector lend;
  private PVector rend;

  private boolean drawing = true;

  // Some 3D debugging
  //  private ArrayList<Segment> lines = new ArrayList<Segment>( );
  //  private ArrayList<Segment> bounces = new ArrayList<Segment>( );

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.TitaniumSheepdog" );

  }

  @Override
  public void settings( ) {

    this.size( 1920, 1080 );

  }

  @Override
  public void setup( ) {

    this.background( 0 );

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

    for( int rdx = 0; rdx < this.NUM_RAYS; ++rdx ) {

      PVector o = PVector.lerp( this.lend, this.rend, this.rng.nextFloat( ) );
      Ray r = Ray.fromTwoPoints( o, new PVector( this.rng.nextFloat( ), this.rng.nextFloat( ) / 2.0f, 0 ) );
      r.normalize( );

      Ray ref = this.mirr.bounce( r );

      if( ref == null )
        continue;

      // Where the reflection hits the screen ( y = 0 )
      PVector p = ref.atT( -ref.o.y / ref.d.y );

      if( p.x < 0 || p.x > 1 || p.z < 0 || p.z > 1 )
        continue;

      this.noStroke( );


      this.fill( 255, 5 );


      this.ellipse( p.x * this.pixelWidth, ( 1 - p.z ) * this.pixelHeight, 5, 5 );

    }

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

    }

  }

}
