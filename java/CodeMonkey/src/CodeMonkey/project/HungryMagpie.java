package CodeMonkey.project;

import java.util.Random;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class HungryMagpie extends ProjectBase {

  // Starting with squares of certain size, subdivide if range of colors too big

  public static void main( String[] args ) {

    PApplet.main( "CodeMonkey.project.HungryMagpie" );

  }

  //  private static final int SQ_W_m = 4;
  private static final int SQ_W_M = 8;

  private static final int LAYER_OPA_M = 255;
  //  private static final int LAYER_OPA_m = 255;

  //  private static final int NUM_LAYERS = 1;

  //  private static final float N_SCALE = 0.001f;
  //  private static final float N_CUTOFF = 0.5f;
  //
  //  private static final int RANGE_OVERLAP = 25;

  private static final int SQ_DIFFERENCE = 3 * 195 * 195;
  //  private static final float H_DIFFERENCE = 320;

  //  private static final float GRAD_T = 1;
  //  private static final float GRAD_B = 0f;

  private static Random rng = new Random( );

  private static class Stats {

    public long r = 0, g = 0, b = 0;
    public int nPx;

    public boolean tooBig = false;

  }

  private static Stats getStats( PImage img, int x, int y, int w, int h ) {

    Stats s = new Stats( );

    int cdx, ccdx, c, cc, r, cr, g, cg, b, cb, d;

    s.nPx = w * h;

    s.tooBig = false;

    for( int ydx = 0; ydx < h; ++ydx ) {
      for( int xdx = 0; xdx < w; ++xdx ) {

        cdx = ( x + xdx ) + ( y + ydx ) * img.width;

        c = img.pixels[ cdx ];

        r = ( c >> 16 ) & 0xFF;
        g = ( c >> 8  ) & 0xFF;
        b = ( c >> 0  ) & 0xFF;

        for( int yydx = ydx; yydx < h && s.tooBig == false; ++ yydx ) {
          for( int xxdx = xdx + 1; xxdx < w && s.tooBig == false; ++xxdx ) {

            ccdx = ( x + xxdx ) + ( y + yydx ) * img.width;

            cc = img.pixels[ ccdx ];

            cr = r - ( ( cc >> 16 ) & 0xFF );
            cg = g - ( ( cc >> 8  ) & 0xFF );
            cb = b - ( ( cc >> 0  ) & 0xFF );

            d = cr * cr + cg * cg + cb * cb;
            if( d > SQ_DIFFERENCE )
              s.tooBig = true;

          }
        }

        s.r += r * r;
        s.g += g * g;
        s.b += b * b;

      }
    }

    s.r = Math.round( Math.sqrt( s.r / s.nPx ) );
    s.g = Math.round( Math.sqrt( s.g / s.nPx ) );
    s.b = Math.round( Math.sqrt( s.b / s.nPx ) );

    return s;

  }

  private class Rect {

    public int tlx, tly;
    public int wx, wy;

    public int color;

    public Rect TL, TR, BL, BR;

    public Rect( PImage img, int x, int y, int w, int h, int a, int SQ_W, Stats s ) {

      this.tlx = x;
      this.tly = y;
      this.wx = w;
      this.wy = h;

      // Half width and height
      int hw = w / 2;
      int hh = h / 2;

      // Calculate the stats
      Stats TLS = getStats( img, x,      y,      hw,     hh     );
      Stats TRS = getStats( img, x + hw, y,      w - hw, hh     );
      Stats BLS = getStats( img, x,      y + hh, hw,     h - hh );
      Stats BRS = getStats( img, x + hw, y + hh, w - hw, h - hh );

      // Our color
      this.color = HungryMagpie.this.color( s.r, s.g, s.b, a );

      // Split conditions
      // Minimum size block, don't split past
      if( this.wx < SQ_W || this.wy < SQ_W )
        return;

      // Split validity by difference of colors
      //      if( ( Math.pow( TLS.r - TRS.r, 2 ) + Math.pow( TLS.g - TRS.g, 2 ) + Math.pow( TLS.b - TRS.b, 2 ) < SQ_DIFFERENCE ) &&
      //          ( Math.pow( BLS.r - BRS.r, 2 ) + Math.pow( BLS.g - BRS.g, 2 ) + Math.pow( BLS.b - BRS.b, 2 ) < SQ_DIFFERENCE ) &&
      //          ( Math.pow( TLS.r - BLS.r, 2 ) + Math.pow( TLS.g - BLS.g, 2 ) + Math.pow( TLS.b - BLS.b, 2 ) < SQ_DIFFERENCE ) &&
      //          ( Math.pow( TRS.r - BRS.r, 2 ) + Math.pow( TRS.g - BRS.g, 2 ) + Math.pow( TRS.b - BRS.b, 2 ) < SQ_DIFFERENCE ) &&
      //          ( Math.pow( TLS.r - BRS.r, 2 ) + Math.pow( TLS.g - BRS.g, 2 ) + Math.pow( TLS.b - BRS.b, 2 ) < SQ_DIFFERENCE ) &&
      //          ( Math.pow( TRS.r - BLS.r, 2 ) + Math.pow( TRS.g - BLS.g, 2 ) + Math.pow( TRS.b - BLS.b, 2 ) < SQ_DIFFERENCE ) )
      //        return;

      // Skip split validity every now and then
      //      if( rng.nextFloat( ) < 0.97f ) {
      //
      //        float f = rng.nextFloat( );
      //        if( f < 0.33f ) {
      //          // Split by r
      //          if( Math.max(
      //              Math.max( Math.abs( TLS.r - TRS.r ), Math.abs( BLS.r - BRS.r ) ),
      //              Math.max( Math.abs( TLS.r - BLS.r ), Math.abs( TRS.r - BRS.r ) )
      //              ) < SQ_D )
      //            return;
      //        } else if( f < 0.66f ) {
      //          // Split by g
      //          if( Math.max(
      //              Math.max( Math.abs( TLS.g - TRS.g ), Math.abs( BLS.g - BRS.g ) ),
      //              Math.max( Math.abs( TLS.g - BLS.g ), Math.abs( TRS.g - BRS.g ) )
      //              ) < SQ_D )
      //            return;
      //        } else {
      //          // Split by b
      //          if( Math.max(
      //              Math.max( Math.abs( TLS.b - TRS.b ), Math.abs( BLS.b - BRS.b ) ),
      //              Math.max( Math.abs( TLS.b - BLS.b ), Math.abs( TRS.b - BRS.b ) )
      //              ) < SQ_D )
      //            return;
      //        }
      //      }

      // Split validity by noise at corners and center
      //      if( ( HungryMagpie.this.noise( ( x      ) * N_SCALE, ( y      ) * N_SCALE ) < N_CUTOFF ) ||
      //          ( HungryMagpie.this.noise( ( x + w  ) * N_SCALE, ( y      ) * N_SCALE ) < N_CUTOFF ) ||
      //          ( HungryMagpie.this.noise( ( x      ) * N_SCALE, ( y + h  ) * N_SCALE ) < N_CUTOFF ) ||
      //          ( HungryMagpie.this.noise( ( x + w  ) * N_SCALE, ( y + h  ) * N_SCALE ) < N_CUTOFF ) ||
      //          ( HungryMagpie.this.noise( ( x + hw ) * N_SCALE, ( y + hh ) * N_SCALE ) < N_CUTOFF ) ) {

      // Split validity by difference of ranges
      // TL TR
      //      int M1, M2, m1, m2;
      //      M1 = Math.max( TLS.rM, TRS.rM );
      //      M2 = Math.min( TLS.rM, TRS.rM );
      //      m1 = Math.min( TLS.rm, TRS.rm );
      //      m2 = Math.max( TLS.rm, TRS.rm );
      //      if( ( M1 - m1 ) - Math.abs( TLS.rM - TRS.rM ) - Math.abs( TLS.rm - TRS.rm ) > RANGE_OVERLAP )
      //        return;

      // Split valdity per quadrant by biggest color difference
      if( TLS.tooBig )
        this.TL = new Rect( img, x,      y,      hw,     hh,     a, SQ_W, TLS );
      if( TRS.tooBig )
        this.TR = new Rect( img, x + hw, y,      w - hw, hh,     a, SQ_W, TRS );
      if( BLS.tooBig )
        this.BL = new Rect( img, x,      y + hh, hw,     h - hh, a, SQ_W, BLS );
      if( BRS.tooBig )
        this.BR = new Rect( img, x + hw, y + hh, w - hw, h - hh, a, SQ_W, BRS );

      //      }

    }

    public Rect( PImage img, int x, int y, int w, int h, int a, int SQ_W ) {

      this( img, x, y, w, h, a, SQ_W, getStats( img, x, y, w, h ) );

    }

    public void draw( PGraphics canvas ) {

      // Expects startdraw to have been called
      canvas.noStroke( );
      canvas.fill( this.color );
      //      canvas.stroke( this.color );
      //      canvas.fill( 0, 0 );

      //      int cdx, c;
      //
      //      for( int ydx = 0; ydx < this.wy; ++ydx ) {
      //        for( int xdx = 0; xdx < this.wx; ++xdx ) {
      //
      //          cdx = ( this.tlx + xdx ) + ( this.tly + ydx ) * canvas.width;
      //
      //          float f = ydx / (float)this.wy * ( GRAD_B - GRAD_T ) + GRAD_T;

      //          canvas.pixels[ cdx ] = HungryMagpie.this.lerpColor( canvas.pixels[ cdx ], this.color, (float)Math.pow( f, 2 ) );
      //          canvas.pixels[ cdx ] = this.color;

      //        }
      //      }
      canvas.rect( this.tlx, this.tly, this.wx, this.wy );

      if( this.TL != null )
        this.TL.draw( canvas );
      if( this.TR != null )
        this.TR.draw( canvas );
      if( this.BL != null )
        this.BL.draw( canvas );
      if( this.BR != null )
        this.BR.draw( canvas );

    }

  }

  private PGraphics canvas;
  private PImage img;
  private Rect rimg;

  @Override
  public void settings( ) {

    this.size( 720, 640 );

    this.setName( );

  }

  @Override
  public void setup( ) {

    // Load target image
    this.img = this.loadImage( dataDir + "StarryNight.jpg" );
    this.img.loadPixels( );

    System.out.println( "Loaded target image" );

    // Set up canvas for drawing when done processing
    this.canvas = this.createGraphics( this.img.width, this.img.height );

    System.out.println( "Created a canvas" );

    // Draw the rect
    this.canvas.beginDraw( );

    this.canvas.background( 0 );

    //    this.canvas.loadPixels( );

    //    for( int ldx = 0; ldx < NUM_LAYERS; ++ldx ) {

    // Create Rect
    //      this.noiseSeed( rng.nextLong( ) );
    //    this.rimg = new Rect(
    //        this.img,
    //        0, 0,
    //        this.img.width, this.img.height,
    //        Math.round( ( ldx / (float) NUM_LAYERS * ( LAYER_OPA_M - LAYER_OPA_m ) + LAYER_OPA_m ) ),
    //        rng.nextInt( SQ_W_M - SQ_W_m ) + SQ_W_m );

    this.rimg = new Rect(
        this.img,
        0, 0,
        this.img.width, this.img.height,
        LAYER_OPA_M,
        SQ_W_M );

    this.rimg.draw( this.canvas );

    //    }

    //    this.canvas.updatePixels( );

    this.canvas.endDraw( );

    System.out.println( "Done filling in the image" );

    this.save( this.canvas );

  }

  @Override
  public void draw( ) {

    this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );

  }

}
