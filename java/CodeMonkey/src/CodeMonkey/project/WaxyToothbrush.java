package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.transform.AxisTransform;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class WaxyToothbrush extends ProjectBase { // NN Line thickness

  private final static int cWidth = 1920;
  private final static int cHeigh = 1080;
  private final static int sWidth = 960;
  private final static float D_NEIGH_MAX = max( cWidth, cHeigh ) / 10f;
  private final static float D_NEIGH_MIN = max( cWidth, cHeigh ) / 25f;
  private final static float D_NEIGH_RNG = D_NEIGH_MAX - D_NEIGH_MIN;
  private final static int D_SCALE_K = 3;
  private final static float S_LAMBDA = 1.0f / 5;
  private final static int M_CIRCS = 128;
  private final static int P_DECAY = 4;
  private final static int P_PULSE = 64;

  private Random rng = new Random( );

  private PGraphics canvas;

  private class Circle {
    PVector pos;
    PVector vel;
    public Circle( PVector pos, PVector vel ) { this.pos = pos.copy( ); this.vel = vel.copy( ); };
  }
  private ArrayList<Circle> circs;

  private class DS implements AxisTransform {
    @Override
    public float map( float c ) {
      // ( min, max ) -> ( 0, 1 ) -> ( 1 - x )^k -> ( 0, 255 )
      c = ( c - D_NEIGH_MIN ) / D_NEIGH_RNG;
      c = (float)Math.pow( 1 - c, D_SCALE_K );
      c = c * 255;
      return c;
    }
  }
  private AxisTransform distStroke = new DS( );

  private int nSpawnCounter = 0;

  private int pulse = 0;

  public static void main( String[ ] args ) {
    // TODO Auto-generated method stub

    PApplet.main( "CodeMonkey.project.WaxyToothbrush" );

  }

  @Override
  public void settings( ) {

    this.size( sWidth, Math.round( cHeigh * (float)sWidth / cWidth ) );

    this.setName( );

  }

  @Override
  public void setup( ) {

    this.canvas = this.createGraphics( cWidth, cHeigh );

    this.circs = new ArrayList<Circle>( );

  }

  Circle makeCirc( ) {

    float r = min( cWidth, cHeigh ) * 0.85f * 0.5f;
    float t = (float)this.rng.nextGaussian( ) * PI; // Amazing... Normal dist is already perfect

    PVector p = new PVector(
        r * (float) Math.cos( t ) + cWidth / 2,
        r * (float) Math.sin( t ) + cHeigh / 2
        );

    PVector dir = new PVector( cWidth / 2, cHeigh / 2 );
    dir.sub( p );
    dir.setMag( 1 );
    dir.rotate( t );

    return new Circle(
        p,
        PVector.random2D( ) );

  }

  @Override
  public void draw( ) {

    // Poisson spawning
    if( this.nSpawnCounter <= 0 ) {

      if( this.circs.size( ) < M_CIRCS )
        this.circs.add( this.makeCirc( ) );

      this.nSpawnCounter = (int)Math.round( - Math.log( 1.0 - this.rng.nextFloat( ) ) / S_LAMBDA );

    } else
      --this.nSpawnCounter;

    this.canvas.beginDraw( );
    this.canvas.background( 0 );

    // n
    // Update points, reset if off the screen
    ArrayList<Circle> temp = new ArrayList<Circle>( );
    for( Circle c : this.circs ) {

      c.pos.add( c.vel );

      if( c.pos.x >= cWidth || c.pos.y < 0 || c.pos.y >= cHeigh || c.pos.y < 0 ) {
      } else {

        temp.add( c );

        this.canvas.noStroke( );
        this.canvas.fill( 255 );
        this.canvas.ellipse( c.pos.x, c.pos.y, 3, 3 );

      }

    }
    this.circs = temp;

    // n^2
    // Find neighbors md < |d| < Md
    this.canvas.noFill( );

    for( int pdx = 0; pdx < this.circs.size( ); ++pdx ) {

      Circle c = this.circs.get( pdx );

      for( int ptdx = pdx + 1; ptdx < this.circs.size( ); ++ptdx ) {

        Circle ct = this.circs.get( ptdx );

        float d = PVector.dist( c.pos, ct.pos );

        if( d < D_NEIGH_MAX ) {

          this.canvas.stroke( this.distStroke.map( d ) + this.pulse );

          this.canvas.line( c.pos.x, c.pos.y, ct.pos.x, ct.pos.y );

        }

      }

    }

    this.canvas.endDraw( );

    this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );

    if( this.pulse > 0 )
      this.pulse -= P_DECAY;

  }

  @Override
  public void keyPressed( ) {

    if( this.key == ' ' )
      this.pulse += P_PULSE;

  }

}
