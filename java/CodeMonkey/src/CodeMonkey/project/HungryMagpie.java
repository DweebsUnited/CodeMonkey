package CodeMonkey.project;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;


public class HungryMagpie extends ProjectBase {

	// Starting with squares of certain size, subdivide if range of colors too big

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.HungryMagpie" );

	}

	private static PApplet context;

	private static final int SQ_W_M = 4;
	private static final float SQ_DIFFERENCE = 175 * 175 * 3;
	private static final float H_DIFFERENCE = 160f;

	// private static Random rng = new Random( );

	private final int cWidth = 640;

	private static class Stats {

		public long r = 0, g = 0, b = 0;
		public int nPx;

		public float Mhue = Float.NEGATIVE_INFINITY;
		public float mhue = Float.POSITIVE_INFINITY;

		public boolean tooBig = false;

	}

	private static Stats getStats( PImage img, int x, int y, int w, int h ) {

		System.out.println( String.format( "Stats: %d, %d", w, h ) );

		Stats s = new Stats( );

		int cdx, ccdx, c, cc, r, cr, g, cg, b, cb, d;

		float hue, chue, hd;

		s.nPx = w * h;

		s.tooBig = false;

		for( int ydx = 0; ydx < h; ++ydx ) {
			for( int xdx = 0; xdx < w; ++xdx ) {

				cdx = ( x + xdx ) + ( y + ydx ) * img.width;

				c = img.pixels[ cdx ];

				r = ( c >> 16 ) & 0xFF;
				g = ( c >> 8 ) & 0xFF;
				b = ( c >> 0 ) & 0xFF;

				hue = HungryMagpie.context.hue( c );

				if( hue > s.Mhue )
					s.Mhue = hue;
				if( hue < s.mhue )
					s.mhue = hue;

				for( int yydx = ydx; yydx < h && s.tooBig == false; ++yydx ) {
					for( int xxdx = xdx + 1; xxdx < w && s.tooBig == false; ++xxdx ) {

						ccdx = ( x + xxdx ) + ( y + yydx ) * img.width;

						cc = img.pixels[ ccdx ];

						// cr = r - ( ( cc >> 16 ) & 0xFF );
						// cg = g - ( ( cc >> 8 ) & 0xFF );
						// cb = b - ( ( cc >> 0 ) & 0xFF );

						hd = Math.abs( hue - HungryMagpie.context.hue( cc ) );
						hd = Math.min( hd, 360 - hd );

						// d = cr * cr + cg * cg + cb * cb;
						// if( d > SQ_DIFFERENCE )
						if( hd > HungryMagpie.H_DIFFERENCE )
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

		public Rect( PImage img, int x, int y, int w, int h, int SQ_W, Stats s ) {

			System.out.println( String.format( "Starting square with width and height: %d, %d", w, h ) );

			this.tlx = x;
			this.tly = y;
			this.wx = w;
			this.wy = h;

			// Half width and height
			int hw = w / 2;
			int hh = h / 2;

			// Calculate the stats
			Stats TLS = HungryMagpie.getStats( img, x, y, hw, hh );
			Stats TRS = HungryMagpie.getStats( img, x + hw, y, w - hw, hh );
			Stats BLS = HungryMagpie.getStats( img, x, y + hh, hw, h - hh );
			Stats BRS = HungryMagpie.getStats( img, x + hw, y + hh, w - hw, h - hh );

			// Our color
			this.color = HungryMagpie.this.color( s.r, s.g, s.b );

			// Split conditions
			// Minimum size block, don't split past
			if( this.wx < SQ_W || this.wy < SQ_W )
				return;

			// Split valdity per quadrant by biggest color difference
			if( TLS.tooBig )
				this.TL = new Rect( img, x, y, hw, hh, SQ_W, TLS );
			if( TRS.tooBig )
				this.TR = new Rect( img, x + hw, y, w - hw, hh, SQ_W, TRS );
			if( BLS.tooBig )
				this.BL = new Rect( img, x, y + hh, hw, h - hh, SQ_W, BLS );
			if( BRS.tooBig )
				this.BR = new Rect( img, x + hw, y + hh, w - hw, h - hh, SQ_W, BRS );

		}

		public Rect( PImage img, int x, int y, int w, int h, int SQ_W ) {

			this( img, x, y, w, h, SQ_W, HungryMagpie.getStats( img, x, y, w, h ) );

		}

		public void draw( PGraphics canvas ) {

			// Expects startdraw to have been called
			canvas.noStroke( );
			canvas.fill( this.color );

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

	private static PGraphics canvas;
	private PImage img;
	private Rect rimg;

	@Override
	public void settings( ) {

		this.size( 720, 640 );

		this.setName( );

		HungryMagpie.context = this;

	}

	@Override
	public void setup( ) {

		// Load target image
		this.img = this.loadImage( ProjectBase.dataDir + "DSC_0088-3.jpg" );
		// this.img.resize( this.cWidth, (int)Math.floor( this.img.height /
		// (float)this.img.width * this.cWidth ) );
		this.img.loadPixels( );

		System.out.println( "Loaded target image" );

		// Set up canvas for drawing when done processing
		HungryMagpie.canvas = this.createGraphics( this.img.width, this.img.height );

		System.out.println( "Created a canvas" );

		this.rimg = new Rect( this.img, 0, 0, this.img.width, this.img.height, HungryMagpie.SQ_W_M );

		// Draw the rect
		HungryMagpie.canvas.beginDraw( );

		HungryMagpie.canvas.background( 0 );

		this.rimg.draw( HungryMagpie.canvas );

		HungryMagpie.canvas.endDraw( );

		System.out.println( "Done filling in the image" );

		this.save( HungryMagpie.canvas );

	}

	@Override
	public void draw( ) {

		this.image( HungryMagpie.canvas, 0, 0, this.pixelWidth, this.pixelHeight );

	}

}
