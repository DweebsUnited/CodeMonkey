package CodeMonkey.project;

import java.util.Random;

import CodeMonkey.transform.AxisTransform;
import CodeMonkey.transform.CoordinateTransform;
import CodeMonkey.transform.DualCoordinateTransform;
import CodeMonkey.transform.axis.ATLinear;
import CodeMonkey.transform.color.ColorCoordinateTransform;
import CodeMonkey.transform.color.CoordinateHue;
import CodeMonkey.transform.coordinate.CTExponential;
import CodeMonkey.transform.coordinate.CTLinear;
import CodeMonkey.transform.dualCoordinate.DCTMidpointDisplace;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class DopeyEevee extends PApplet {

  private static String CM = "/Users/ozzy/Documents/CodeMonkey/";
  private static String dataDir = CM + "data/";

  private static final int N_PIPE = 3;
  private static final int N_TRNS = 7;

  private class PipelineTransform {

    private CoordinateTransform     single;
    private DualCoordinateTransform dual;
    private int a, b;

    public PipelineTransform( CoordinateTransform transform, int a ) {

      this.single = transform;
      this.a = a;

    }

    public PipelineTransform( DualCoordinateTransform transform, int a, int b ) {

      this.dual = transform;
      this.a = a;
      this.b = b;

    }

    public void transform( PVector[] pipeline ) {

      if( this.single != null ) {

        pipeline[ this.a ].set( this.single.map( pipeline[ this.a ] ) );

      } else {

        pipeline[ this.a ].set( this.dual.map( pipeline[ this.a ], pipeline[ this.b ] ) );

      }

    }

  }

  private PipelineTransform makeTransform( Random rng ) {

    PipelineTransform t = null;
    int a, b, c;

    switch( rng.nextInt( 5 ) ) {

      case 0:
        a = rng.nextInt( N_PIPE );
        b = rng.nextInt( N_PIPE );
        c = rng.nextInt( N_PIPE );
        while( b == a )
          b = rng.nextInt( N_PIPE );

        t = new PipelineTransform(
            new CodeMonkey.transform.dualCoordinate.DCTAdd( ),
            a,
            b );

        System.out.println( String.format( "Add: %d += %d", a, b ) );
        break;
      case 1:
        a = rng.nextInt( N_PIPE );
        b = rng.nextInt( N_PIPE );
        while( b == a )
          b = rng.nextInt( N_PIPE );

        t = new PipelineTransform(
            new CodeMonkey.transform.dualCoordinate.DCTSub( ),
            a,
            b );

        System.out.println( String.format( "Sub: %d += %d", a, b ) );
        break;
      case 2:
        a = rng.nextInt( N_PIPE );

        t = new PipelineTransform(
            new CodeMonkey.transform.coordinate.CTNoiseRotate( this ),
            a
            );

        System.out.println( String.format( "NoiseRotate: %d", a ) );
        break;
      case 3:
        a = rng.nextInt( N_PIPE );
        b = rng.nextInt( N_PIPE );
        while( b == a )
          b = rng.nextInt( N_PIPE );

        t = new PipelineTransform(
            new DCTMidpointDisplace( rng ),
            a,
            b
            );

        System.out.println( String.format( "Midpoint displacement: %d = %d | %d", a, a, b ) );
        break;
      case 4:
        a = rng.nextInt( N_PIPE );

        float p = rng.nextFloat( );
        p = rng.nextFloat( ) > 0.5f ? p : 1.0f / p;

        t = new PipelineTransform(
            new CTExponential( 1, p, 0 ),
            a
            );

        System.out.println( String.format( "Exponential: %d^%f", a, p ) );
        break;

    }

    return t;

  }

  Random rng = new Random( );

  CoordinateTransform ohoneToNegpos = new CTLinear( new PVector( -1, -1, -1 ), new PVector( 2, 2, 2 ) );

  ColorCoordinateTransform colorTrans;

  PVector[][] buffer;
  PGraphics[] canvas;

  PipelineTransform[] pipeline;

  private void makeDraw( ) {

    this.noiseSeed( this.rng.nextLong( ) );
    this.rng.setSeed( this.rng.nextLong( ) );

    System.out.println( );
    System.out.println( "Making new pipeline" );
    this.pipeline = new PipelineTransform[ N_TRNS ];
    for( int tdx = 0; tdx < N_TRNS; ++tdx )
      this.pipeline[ tdx ] = this.makeTransform( this.rng );


    // Run each pixel through the pipeline
    for( int xdx = 0; xdx < this.pixelWidth; ++xdx ) {

      for( int ydx = 0; ydx < this.pixelHeight; ++ydx ) {

        float x = xdx / (float) this.pixelWidth;
        float y = ydx / (float) this.pixelHeight;
        float n = this.noise( x, y );

        // Make a new pipeline state
        // ( x * 3 ), ( y * 3 ), ( n * 3 )
        //        PVector[] pipelineState = {
        //            this.ohoneToNegpos.map( new PVector( x, x, x ) ),
        //            this.ohoneToNegpos.map( new PVector( y, y, y ) ),
        //            this.ohoneToNegpos.map( new PVector( n, n, n ) )
        //        };
        // ( x, y, n ) * 3
        PVector[] pipelineState = {
            this.ohoneToNegpos.map( new PVector( x, y, n ) ),
            this.ohoneToNegpos.map( new PVector( x, y, n ) ),
            this.ohoneToNegpos.map( new PVector( x, y, n ) )
        };

        // Run all the transforms
        for( int tdx = 0; tdx < N_TRNS; ++tdx )
          this.pipeline[ tdx ].transform( pipelineState );

        for( int pdx = 0; pdx < N_PIPE; ++pdx )
          this.buffer[ pdx ][ xdx + ydx * this.pixelWidth ].set( pipelineState[ pdx ] );

      }

    }


    // Set each buffer up
    // WTF, for what?!
    for( int bdx = 0; bdx < N_PIPE; ++bdx ) {

      // Calculate normalization factor
      float maxMag = 0;
      float m;
      for( int xdx = 0; xdx < this.pixelWidth; ++xdx ) {

        for( int ydx = 0; ydx < this.pixelHeight; ++ydx ) {

          m = this.buffer[ bdx ][ xdx + ydx * this.pixelWidth ].mag( );
          if( m > maxMag )
            maxMag = m;

        }

      }

      // Normalize
      maxMag = 1.0f / maxMag;

      for( int xdx = 0; xdx < this.pixelWidth; ++xdx ) {

        for( int ydx = 0; ydx < this.pixelHeight; ++ydx ) {

          this.buffer[ bdx ][ xdx + ydx * this.pixelWidth ].mult( maxMag );

        }

      }


      // Convert to colors and draw
      this.canvas[ bdx ].beginDraw( );
      this.canvas[ bdx ].loadPixels( );

      for( int xdx = 0; xdx < this.pixelWidth; ++xdx ) {

        for( int ydx = 0; ydx < this.pixelHeight; ++ydx ) {

          this.canvas[ bdx ].pixels[ xdx + ydx * this.pixelWidth ] = this.colorTrans.map( this, this.buffer[ bdx ][ xdx + ydx * this.pixelWidth ] );

        }

      }

      this.canvas[ bdx ].updatePixels( );
      this.canvas[ bdx ].endDraw( );

    }

    // Now composite the secret canvas
    this.canvas[ N_PIPE ].beginDraw( );
    this.canvas[ N_PIPE ].loadPixels( );

    float[] w = new float[ N_PIPE ];
    float wm = 0;
    for( int pdx = 0; pdx < N_PIPE; ++pdx ) {
      w[ pdx ] = this.rng.nextFloat( );
      wm += Math.pow( w[ pdx ], 2 );
    }
    for( int pdx = 0; pdx < N_PIPE; ++pdx )
      w[ pdx ] = w[ pdx ] / wm;

    // Set up to normalize each channel
    int rmin = 255;
    int gmin = 255;
    int bmin = 255;
    int rmax = 0;
    int gmax = 0;
    int bmax = 0;
    // BY MAGNITUDE FUCKERS
    // What does that even mean
    float mMax = Float.NEGATIVE_INFINITY;
    float mMin = Float.POSITIVE_INFINITY;

    for( int xdx = 0; xdx < this.pixelWidth; ++xdx ) {
      for( int ydx = 0; ydx < this.pixelHeight; ++ydx ) {

        float r = 0;
        float g = 0;
        float b = 0;

        for( int bdx = 0; bdx < N_PIPE; ++bdx ) {

          PVector bpt = this.buffer[ bdx ][ xdx + ydx * this.pixelWidth ];

          r += bpt.x * w[ bdx ];
          g += bpt.y * w[ bdx ];
          b += bpt.z * w[ bdx ];

        }

        r /= 3.0f;
        g /= 3.0f;
        b /= 3.0f;

        this.canvas[ N_PIPE ].pixels[ xdx + ydx * this.pixelWidth ] = this.colorTrans.map( this, new PVector( r, g, b ) );

        int red =   ( this.canvas[ N_PIPE ].pixels[ xdx + ydx * this.pixelWidth ] >> 0  ) & 0x000000FF;
        int green = ( this.canvas[ N_PIPE ].pixels[ xdx + ydx * this.pixelWidth ] >> 8  ) & 0x000000FF;
        int blue =  ( this.canvas[ N_PIPE ].pixels[ xdx + ydx * this.pixelWidth ] >> 16 ) & 0x000000FF;

        if( red > rmax )
          rmax = red;
        if( red < rmin )
          rmin = red;

        if( green > gmax )
          gmax = green;
        if( green < gmin )
          gmin = green;

        if( blue > bmax )
          bmax = blue;
        if( blue < bmin )
          bmin = blue;
        //        float m = (float) Math.sqrt( r * r + g * g + b * b );
        //        if( m > mMax )
          //          mMax = m;
        //        if( m < mMin )
        //          mMin = m;

      }
    }

    AxisTransform rTrans = new ATLinear( rmin, rmax - rmin, 0, 255 );
    AxisTransform gTrans = new ATLinear( gmin, gmax - gmin, 0, 255 );
    AxisTransform bTrans = new ATLinear( bmin, bmax - bmin, 0, 255 );
    //    CoordinateTransform bTrans = new ATLinear( mMin, mMax - mMin, 0, 1 );

    for( int xdx = 0; xdx < this.pixelWidth; ++xdx ) {
      for( int ydx = 0; ydx < this.pixelHeight; ++ydx ) {

        int p = this.canvas[ N_PIPE ].pixels[ xdx + ydx * this.pixelWidth ];

        int red   = ( p >> 0  ) & 0x000000FF;
        int green = ( p >> 8  ) & 0x000000FF;
        int blue  = ( p >> 16 ) & 0x000000FF;

        red   = round( rTrans.map( red ) )   & 0xFF;
        green = round( gTrans.map( green ) ) & 0xFF;
        blue  = round( bTrans.map( blue ) )  & 0xFF;

        p = red | ( green << 8 ) | ( blue << 16 ) | ( p & 0xFF000000 );

        this.canvas[ N_PIPE ].pixels[ xdx + ydx * this.pixelWidth ] = p;

      }
    }

    this.canvas[ N_PIPE ].updatePixels( );
    this.canvas[ N_PIPE ].endDraw( );

  }

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.DopeyEevee" );

  }

  @Override
  public void settings( ) {

    this.size( 720, 640 );

  }

  @Override
  public void setup( ) {

    this.colorTrans = new CoordinateHue( );

    // Make an extra one for secret reasons ;)
    this.canvas = new PGraphics[ N_PIPE + 1 ];
    for( int pdx = 0; pdx < N_PIPE + 1; ++pdx )
      this.canvas[ pdx ] = this.createGraphics( this.pixelWidth, this.pixelHeight );

    this.buffer = new PVector[ N_PIPE ][];
    for( int bdx = 0; bdx < N_PIPE; ++ bdx ) {
      this.buffer[ bdx ] = new PVector[ this.pixelWidth * this.pixelHeight ];
      for( int pdx = 0; pdx < this.pixelWidth * this.pixelHeight; ++pdx )
        this.buffer[ bdx ][ pdx ] = new PVector( );
    }


    this.makeDraw( );

  }

  @Override
  public void draw( ) {

    this.background( 0 );

    // TODO: This is hardcoded for 3 pipes...
    int hpw = Math.round( this.pixelWidth / 2.0f );
    int hph = Math.round( this.pixelHeight / 2.0f );
    this.image( this.canvas[ 0 ], 0, 0, hpw, hph );
    this.image( this.canvas[ 1 ], hpw, 0, hpw, hph );
    this.image( this.canvas[ 2 ], 0, hph, hpw, hph );
    this.image( this.canvas[ 3 ], hpw, hph, hpw, hph );

  }

  @Override
  public void keyPressed( ) {

    if( this.key == ' ' )
      this.makeDraw( );
    else if( this.key == 'w' )
      this.save( dataDir + "DopeyEevee.png" );
    else if( this.key == 'a' )
      this.canvas[ 0 ].save( dataDir + "DopeyEevee.png" );
    else if( this.key == 'r' )
      this.canvas[ 1 ].save( dataDir + "DopeyEevee.png" );
    else if( this.key == 'z' )
      this.canvas[ 2 ].save( dataDir + "DopeyEevee.png" );
    else if( this.key == 'x' )
      this.canvas[ 3 ].save( dataDir + "DopeyEevee.png" );

  }

}
