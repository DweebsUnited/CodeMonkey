package CodeMonkey.project;

import java.util.Random;

import CodeMonkey.transform.AxisTransform;
import CodeMonkey.transform.axis.ATLinear;
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
  //     Marching cubes on cells dense enough?
  //     One model per frame? *shudder*

  // A' = A + ( Da * GradA - A B^2 + f ( 1 - A ) ) dt
  // B' = B + ( Db * GradB + A B^2 - ( k + f ) B ) dt

  private final int N_STEPS = 25;
  private final int N_FRAMES = 30 * 30;

  private PGraphics canvas;
  private int cWidth = 512;
  private int cHeigh = 512;
  private int nPx = this.cWidth * this.cHeigh;

  private float[] AField, BField;
  private float[] APField, BPField;

  private float ODa, ODb, Of, Ok;
  private float[] Da, Db;
  private float[] f, k;

  private float gDM = 0.01f;
  private float[] gDx, gDy;

  private float[] grad = {
      0.05f, 0.20f, 0.05f,
      0.20f,     0, 0.20f,
      0.05f, 0.20f, 0.05f
  };

  private void step( ) {

    // Run one: Calculate prime fields
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

            float g = this.grad[ gx + gy * 3 + 4 ];
            g += gx * this.gDx[ pdx + gx + gy * this.cWidth ] + gy * this.gDy[ pdx + gx + gy * this.cWidth ];

            G  += g;
            GA += g * this.AField[ pdx + gx + gy * this.cWidth ];
            GB += g * this.BField[ pdx + gx + gy * this.cWidth ];

          }
        }

        GA -= G * A;
        GB -= G * B;

        // A' = A + ( Da * GradA - A B^2 + f ( 1 - A ) ) dt
        // B' = B + ( Db * GradB + A B^2 - ( k + f ) B ) dt

        float AB2 = A * B * B;

        this.APField[ pdx ] = A + this.Da[ pdx ] * GA - AB2 + this.f[ pdx ] * ( 1 - A );
        this.BPField[ pdx ] = B + this.Db[ pdx ] * GB + AB2 - ( this.k[ pdx ] + this.f[ pdx ] ) * B;

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

    this.canvas = this.createGraphics( this.cWidth, this.cHeigh );

    this.AField  = new float[ this.nPx ];
    this.BField  = new float[ this.nPx ];
    this.APField = new float[ this.nPx ];
    this.BPField = new float[ this.nPx ];
    this.Da      = new float[ this.nPx ];
    this.Db      = new float[ this.nPx ];
    this.f       = new float[ this.nPx ];
    this.k       = new float[ this.nPx ];
    this.gDx     = new float[ this.nPx ];
    this.gDy     = new float[ this.nPx ];

    this.ODa = 1.0f;
    this.ODb = 0.5f;
    this.Of = 0.055f;
    this.Ok = 0.062f;

    AxisTransform toNegPos = new ATLinear( 0, 1, -1, 1 );

    float AAvg = 0, BAvg = 0;
    float DaAvg = 0, DbAvg = 0;
    float fAvg = 0, kAvg = 0;

    this.noiseSeed( this.rng.nextLong( ) );
    for( int dy = 0; dy < this.cHeigh; ++dy ) {
      for( int dx = 0; dx < this.cWidth; ++dx ) {

        int pdx = dx + dy * this.cWidth;

        this.AField[ pdx ] = 1;
        this.BField[ pdx ] = ( Math.abs( dx - this.cWidth / 2 ) < 10 && Math.abs( dy - this.cHeigh / 2 ) < 10 ) ? 1 : 0;

        AAvg += this.AField[ pdx ];
        BAvg += this.BField[ pdx ];

      }
    }

    this.noiseSeed( this.rng.nextLong( ) );
    for( int dy = 0; dy < this.cHeigh; ++dy ) {
      for( int dx = 0; dx < this.cWidth; ++dx ) {

        int pdx = dx + dy * this.cWidth;

        this.Da[ pdx ] = this.ODa + toNegPos.map( this.noise( dx * 0.005f, dy * 0.005f ) ) * 0.2f;
        this.Db[ pdx ] = this.ODb + toNegPos.map( this.noise( dx * 0.005f, dy * 0.005f ) ) * 0.2f;

        DaAvg += this.Da[ pdx ];
        DbAvg += this.Db[ pdx ];

      }
    }

    this.noiseSeed( this.rng.nextLong( ) );
    for( int dy = 0; dy < this.cHeigh; ++dy ) {
      for( int dx = 0; dx < this.cWidth; ++dx ) {

        int pdx = dx + dy * this.cWidth;

        this.f[ pdx ] = this.Of + toNegPos.map( this.noise( dx * 0.005f, dy * 0.005f ) ) * 0.01f;
        this.k[ pdx ] = this.Ok + toNegPos.map( this.noise( dx * 0.005f, dy * 0.005f ) ) * 0.01f;

        fAvg += this.f[ pdx ];
        kAvg += this.k[ pdx ];

      }
    }

    this.noiseSeed( this.rng.nextLong( ) );
    for( int dy = 0; dy < this.cHeigh; ++dy ) {
      for( int dx = 0; dx < this.cWidth; ++dx ) {

        int pdx = dx + dy * this.cWidth;

        float gx = toNegPos.map( this.noise( dx * 0.01f, dy * 0.01f ) );
        float gy = toNegPos.map( this.noise( dx * 0.01f, dy * 0.01f ) );
        float gm = (float)Math.sqrt( gx * gx + gy * gy );

        this.gDx[ pdx ] = gx * this.gDM / gm;
        this.gDy[ pdx ] = gy * this.gDM / gm;

      }
    }

    System.out.println( String.format( "A: %f, B: %f, Da: %f, Db: %f, f: %f, k: %f", AAvg / this.nPx, BAvg / this.nPx, DaAvg / this.nPx, DbAvg / this.nPx, fAvg / this.nPx, kAvg / this.nPx ) );

  }

  @Override
  public void draw( ) {

    if( this.frameCount > this.N_FRAMES )
      this.exit( );

    for( int sdx = 0; sdx < this.N_STEPS; ++sdx )
      this.step( );

    this.writeToCanvas( );

    this.save( this.canvas, this.frameCount );

    this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );

    System.out.println( this.frameRate );

  }

}
