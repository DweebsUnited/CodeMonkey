package CodeMonkey.project;

import java.util.Random;

import com.hamoid.VideoExport;

import processing.core.PApplet;
import processing.core.PGraphics;

public class FrozenPlanetoid extends ProjectBase {

  Random rng = new Random( );

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.FrozenPlanetoid" );

  }

  // TODO:
  //   Directional bias on the grads
  //   Noise for the parameters
  //   3d in some way...
  //     Marching cubes cells dense enough?
  //     One model per frame? *shudder*

  // A' = A + ( Da * GradA - A B^2 + f ( 1 - A ) ) dt
  // B' = B + ( Db * GradB + A B^2 - ( k + f ) B ) dt

  private final int N_STEPS = 55;
  private final int N_FRAMES = 30 * 30;

  private final float Da = 1.0f;
  private final float Db = 0.5f;

  private final float f = 0.05f;
  private final float k = 0.06f;

  private PGraphics canvas;
  private int cWidth = 720;
  private int cHeigh = 640;
  private int nPx = this.cWidth * this.cHeigh;

  private float[] AField;
  private float[] BField;

  private float[] APField;
  private float[] BPField;

  private float[] grad = {
      0.05f, 0.20f, 0.05f,
      0.20f,     0, 0.20f,
      0.05f, 0.20f, 0.05f
  };

  private VideoExport exp;

  private void step( ) {

    // Run one: Calculate primes
    for( int dy = 0; dy < this.cHeigh; ++dy ) {
      for( int dx = 0; dx < this.cWidth; ++dx ) {

        int pdx = dx + dy * this.cWidth;

        float A = this.AField[ pdx ];
        float B = this.BField[ pdx ];

        float GA = 0;
        float GB = 0;
        float G = 0;

        for( int gy = -1; gy < 2; ++gy ) {

          if( dy + gy < 0 || dy + gy >= this.cHeigh )
            continue;

          for( int gx = -1; gx < 2; ++gx ) {

            if( dx + gx < 0 || dx + gx >= this.cWidth )
              continue;

            G  += this.grad[ gx + gy * 3 + 4 ];
            GA += this.grad[ gx + gy * 3 + 4 ] * this.AField[ pdx + gx + gy * this.cWidth ];
            GB += this.grad[ gx + gy * 3 + 4 ] * this.BField[ pdx + gx + gy * this.cWidth ];

          }
        }

        GA -= G * A;
        GB -= G * B;

        // A' = A + ( Da * GradA - A B^2 + f ( 1 - A ) ) dt
        // B' = B + ( Db * GradB + A B^2 - ( k + f ) B ) dt

        float AB2 = A * B * B;

        this.APField[ pdx ] = A + this.Da * GA - AB2 + this.f * ( 1 - A );
        this.BPField[ pdx ] = B + this.Db * GB + AB2 - ( this.k + this.f ) * B;

      }
    }

    // Swap
    float[] t = this.APField;
    this.APField = this.AField;
    this.AField = t;

    t = this.BPField;
    this.BPField = this.BField;
    this.BField = t;

  }

  private void writeToCanvas( ) {

    // Run two: Calculate pixels
    this.canvas.beginDraw( );
    this.canvas.loadPixels( );
    for( int dy = 0; dy < this.cHeigh; ++dy ) {
      for( int dx = 0; dx < this.cWidth; ++dx ) {

        int pdx = dx + dy * this.cWidth;

        this.canvas.pixels[ pdx ] = this.color( Math.abs( this.AField[ pdx ] - this.BField[ pdx ] ) * 255 );

      }
    }
    this.canvas.updatePixels( );
    this.canvas.endDraw( );

  }

  @Override
  public void settings( ) {

    this.size( 720, 640 );

    this.setName( );

  }

  @Override
  public void setup( ) {

    this.exp = new VideoExport( this, dataDir + "FrozenPlanetoid.mp4", this.canvas );
    this.exp.forgetFfmpegPath( );
    this.exp.startMovie( );

    this.noiseSeed( this.rng.nextLong( ) );

    this.canvas = this.createGraphics( this.cWidth, this.cHeigh );

    this.AField  = new float[ this.nPx ];
    this.BField  = new float[ this.nPx ];
    this.APField = new float[ this.nPx ];
    this.BPField = new float[ this.nPx ];

    for( int dy = 0; dy < this.cHeigh; ++dy ) {
      for( int dx = 0; dx < this.cWidth; ++dx ) {

        int pdx = dx + dy * this.cWidth;

        this.AField[ pdx ] = (float)Math.pow( this.noise( dx * 0.005f, dy * 0.005f, 0 ), 1f / 10 );
        this.BField[ pdx ] = (float)Math.pow( this.noise( dx * 0.005f, dy * 0.005f, 5 ), 10f );

      }
    }

    for( int fdx = 0; fdx < this.N_FRAMES; ++fdx ) {

      System.out.println( String.format( "%d / %d", fdx, this.N_FRAMES ) );

      for( int sdx = 0; sdx < this.N_STEPS; ++sdx )
        this.step( );

      this.writeToCanvas( );

      this.exp.saveFrame( );

    }

    this.exp.endMovie( );

    this.exit( );

  }

  @Override
  public void draw( ) {

    for( int sdx = 0; sdx < this.N_STEPS; ++sdx )
      this.step( );

    this.writeToCanvas( );

    this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );

    System.out.println( this.frameRate );

  }

}
