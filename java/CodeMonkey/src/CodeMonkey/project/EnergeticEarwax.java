package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.physics.InverseSpring;
import CodeMonkey.physics.PointMassAccum;
import CodeMonkey.physics.Spring;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class EnergeticEarwax extends ProjectBase {

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.EnergeticEarwax" );

  }

  private static class Point {

    public int id;

    public PointMassAccum m;

    public Point F, B;

  }

  private Random rng = new Random( );

  private final int sWidth = 720;
  private final int sHeigh = 640;
  private final int cWidth = 1920;
  private final int cHeigh = 1080;

  private final float dt = 1 / 60f;

  private final int N_SEED = 128;
  private final int S_RAD = 128;

  private final float P_MASS = 1;

  private final float K_EDGE = 10;
  private final float D_EDGE = 3;
  private final float K_NEIGH = -100;
  private final float P_NEIGH = 1;
  private final float R_DIST = 25;

  private final float P_SPLIT = 0.01f;
  //  private final float D_SPLIT = 7;
  private final float N_SCALE = 6.5f / this.cWidth;

  private PGraphics canvas;

  Point loop;
  int pdxID = 0;
  ArrayList<Point> pool;

  Spring sE;
  InverseSpring sN;

  @Override
  public void settings( ) {

    this.size( this.sWidth, this.sHeigh );

    this.setName( );

  }

  @Override
  public void setup( ) {

    this.canvas = this.createGraphics( this.cWidth, this.cHeigh );

    this.canvas.beginDraw( );
    this.canvas.background( 0 );
    this.canvas.endDraw( );

    this.pool = new ArrayList<Point>( );

    Point p = new Point( );
    this.loop = p;

    for( int cdx = 0; cdx < this.N_SEED; ++cdx ) {

      p.id = this.pdxID++;
      p.m = new PointMassAccum(
          (float)Math.cos( 2f * PI * cdx / this.N_SEED ) * this.S_RAD + this.cWidth / 2,
          (float)Math.sin( 2f * PI * cdx / this.N_SEED ) * this.S_RAD + this.cHeigh / 2,
          this.P_MASS );

      p.F = new Point( );
      p.F.B = p;

      this.pool.add( p );

      p = p.F;

    }

    p.B.F = this.loop;
    this.loop.B = p.B;

    this.sE = new Spring( this.K_EDGE, this.D_EDGE );
    this.sN = new InverseSpring( this.K_NEIGH, 0, this.P_NEIGH );

  }

  @Override
  public void draw( ) {

    // Randomly split a few edges
    for( int pdx = 0; pdx < this.pool.size( ); ++pdx ) {
      Point p = this.pool.get( pdx );

      if( this.rng.nextFloat( ) < this.P_SPLIT * Math.pow( this.noise( p.m.get( ).x * this.N_SCALE, p.m.get( ).y * this.N_SCALE ), 2 ) ) {

        // Check if edge long enough to split

        //        if( PVector.dist( p.m.get( ), p.F.m.get( ) ) < this.D_SPLIT )
        //          continue;

        // Split the edge going forwards
        PVector h = p.m.get( ).copy( );
        h.add( p.F.m.get( ) );
        h.mult( 0.5f );

        Point s = new Point( );
        s.id = this.pdxID++;
        s.m = new PointMassAccum( h.x, h.y, this.P_MASS );

        s.B = p;
        s.F = p.F;
        p.F.B = s;
        p.F = s;

        this.pool.add( s );

      }

    }

    PVector sFs = new PVector( );
    PVector sFt = new PVector( );

    // First run round: Physics accumulation
    for( int pdx = 0; pdx < this.pool.size( ); ++pdx ) {
      Point p = this.pool.get( pdx );

      this.sE.spring( p.m.get( ), p.B.m.get( ), sFs, null );
      p.m.accum( sFs );
      this.sE.spring( p.m.get( ), p.F.m.get( ), sFs, null );
      p.m.accum( sFs );

      for( int ppdx = pdx + 1; ppdx < this.pool.size( ); ++ppdx ) {
        Point t = this.pool.get( ppdx );

        // Repulsion from all others
        // TODO: Use spatial grid

        if( PVector.dist( p.m.get( ), t.m.get( ) ) > this.R_DIST )
          continue;

        this.sN.spring( p.m.get( ), t.m.get( ), sFs, sFt );
        p.m.accum( sFs );
        t.m.accum( sFt );

      }
    }


    // Second: Physics update
    for( Point p : this.pool ) {

      p.m.verlet( this.dt );

      // TODO: Update position in spatial grid

    }


    // Last run round: Drawing
    this.canvas.beginDraw( );
    //    this.canvas.background( 0 );
    //    this.canvas.noFill( );
    //    this.canvas.stroke( 255, 5 );
    this.canvas.noStroke( );
    this.canvas.fill( 255, 5 );

    //    this.canvas.beginShape( );
    //    this.canvas.curveVertex( this.loop.m.get( ).x, this.loop.m.get( ).y );
    Point p = this.loop;
    while( p.F != this.loop ) {

      // Hull line, jagged
      //      this.canvas.line( p.m.get( ).x, p.m.get( ).y, p.F.m.get( ).x, p.F.m.get( ).y );

      // Points - actually kinda cool
      this.canvas.ellipse( p.m.get( ).x, p.m.get( ).y, 3, 3 );

      // Smooth hull
      //      this.canvas.curveVertex( p.m.get( ).x, p.m.get( ).y );

      p = p.F;

    }
    //    this.canvas.curveVertex( this.loop.m.get( ).x, this.loop.m.get( ).y );
    //    this.canvas.curveVertex( this.loop.m.get( ).x, this.loop.m.get( ).y );
    //    this.canvas.endShape( );

    this.canvas.endDraw( );

    this.image( this.canvas, 0, 0, this.sWidth, this.sHeigh );

  }

  @Override
  public void keyPressed( ) {

    if( this.key == 'w' )
      this.save( this.canvas );

  }

}
