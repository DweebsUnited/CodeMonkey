package CodeMonkey.project;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;


public class BigAnvil extends ProjectBase {

	private PImage img;
	private PGraphics canvas;
	private PImage thresh;
	private int cWidth, cHeigh;

	private boolean drawt = false;
	private boolean sortV = false;

	private class Pixel {

		public int col;
		public int dx, dy;
		public float sortVal;
	}

	private Pixel[ ] sortField;

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.BigAnvil" );

	}

	@Override
	public void settings( ) {

		this.size( 720, 640 );

		this.setName( );

	}

	@Override
	public void setup( ) {

		this.img = this.loadImage( ProjectBase.dataDir + "StarryNight.jpg" );
		this.cWidth = this.img.width;
		this.cHeigh = this.img.height;
		this.img.loadPixels( );

		this.thresh = this.createImage( this.cWidth, this.cHeigh, PConstants.RGB );
		this.thresh.copy( this.img, 0, 0, this.cWidth, this.cHeigh, 0, 0, this.cWidth, this.cHeigh );
		this.thresh.loadPixels( );
		// Dark yellow from sun
		// float dc = this.color( 201, 175, 56 );
		// Cream from the middle somewhere
		// int dc = this.color( 154, 147, 141 );
		// Blue from the sky
		int dc = this.color( 54, 74, 135 );
		float dh = this.hue( dc );
		float ds = this.saturation( dc );
		float db = this.brightness( dc );
		float th = 125;
		float ts = 100;
		float tb = 50;

		this.canvas = this.createGraphics( this.cWidth, this.cHeigh );
		this.canvas.beginDraw( );

		this.sortField = new Pixel[ this.cWidth * this.cHeigh ];
		for( int dy = 0; dy < this.cHeigh; ++dy ) {
			for( int dx = 0; dx < this.cWidth; ++dx ) {

				int dxdx = dx + dy * this.cWidth;

				// First set up the sortField pixels

				Pixel px = new Pixel( );

				px.col = this.img.pixels[ dxdx ];
				px.dx = dx;
				px.dy = dy;

				// TODO: Crucial: How we sort
				int cr = ( px.col >> 0x10 ) & 0xFF;
				int cg = ( px.col >> 0x08 ) & 0xFF;
				int cb = ( px.col >> 0x00 ) & 0xFF;
				float h = this.hue( px.col );
				float s = this.saturation( px.col );
				float b = this.brightness( px.col );

				px.sortVal = cr * cr + cg * cg + cb * cb;

				this.sortField[ dxdx ] = px;


				// Now the threshold field
				// This is here because it needs cr cg cb too :/
				// EFFICIENCY

				if( Math.abs( h - dh ) < th && Math.abs( s - ds ) < ts && Math.abs( b - db ) < tb )
					this.thresh.pixels[ dxdx ] = this.color( 255 );
				else
					this.thresh.pixels[ dxdx ] = this.color( 0 );

			}
		}

		this.thresh.updatePixels( );
		this.thresh.filter( PConstants.BLUR, 2 );

	}

	private boolean checkSwap( int a, int b ) {

		if( this.sortField[ a ].sortVal < this.sortField[ b ].sortVal ) {

			Pixel t = this.sortField[ a ];
			this.sortField[ a ] = this.sortField[ b ];
			this.sortField[ b ] = t;

			return true;

		}

		return false;

	}

	private boolean runSortV( ) {

		boolean didSwap = false;

		int dx = 0, dy = 0;
		while( dx < this.cWidth ) {

			for( dy = 0; dy < this.cHeigh && ( this.thresh.pixels[ dx + dy * this.cWidth ] & 0xFF ) <= 127; ++dy )
				;

			while( dy < this.cHeigh ) {
				int dxdx = dx + dy * this.cWidth;
				int ny;

				for( ny = dy + 1; ny < this.cHeigh
						&& ( this.thresh.pixels[ dx + ny * this.cWidth ] & 0xFF ) <= 127; ++ny )
					;

				if( ny == this.cHeigh )
					break;

				if( ny - dy < 5 )
					didSwap = didSwap | this.checkSwap( dx + ny * this.cWidth, dxdx );

				dy = ny;

			}

			dx += 1;

		}

		return didSwap;

	}

	private boolean runSortH( ) {

		boolean didSwap = false;

		int dx = 0, dy = 0;
		while( dy < this.cHeigh ) {

			int oy = dy * this.cWidth;

			for( dx = 0; dx < this.cWidth && ( this.thresh.pixels[ dx + oy ] & 0xFF ) <= 127; ++dx )
				;

			while( dx < this.cWidth ) {

				int dxdx = dx + oy;
				int nx;

				for( nx = dx + 1; nx < this.cWidth && ( this.thresh.pixels[ nx + oy ] & 0xFF ) <= 127; ++nx )
					;

				if( nx == this.cWidth )
					break;

				if( nx - dx < 5 )
					didSwap = didSwap | this.checkSwap( nx + oy, dxdx );

				dx = nx;

			}

			dy += 1;

		}

		return didSwap;

	}

	@Override
	public void draw( ) {

		if( this.sortV ) {
			if( !this.runSortV( ) )
				this.sortV = !this.sortV;
		} else {
			if( !this.runSortH( ) )
				this.sortV = !this.sortV;
		}

		this.canvas.loadPixels( );
		for( int dy = 0; dy < this.cHeigh; ++dy ) {
			for( int dx = 0; dx < this.cWidth - 1; ++dx ) {
				int dxdx = dx + dy * this.cWidth;

				this.canvas.pixels[ dxdx ] = this.sortField[ dxdx ].col;

			}
		}
		this.canvas.updatePixels( );

		if( this.drawt )
			this.image( this.thresh, 0, 0, this.pixelWidth, this.pixelHeight );
		else
			this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );

	}

	@Override
	public void keyPressed( ) {

		if( this.key == 'w' )
			this.save( this.canvas );
		else if( this.key == ' ' )
			this.drawt = !this.drawt;
		else if( this.key == 'a' )
			this.sortV = !this.sortV;

	}

	@Override
	public void mouseClicked( ) {

		int dx = (int) Math.floor( this.mouseX / (float) this.pixelWidth * this.cWidth );
		int dy = (int) Math.floor( this.mouseY / (float) this.pixelHeight * this.cHeigh );

		int px = this.img.pixels[ dx + dy * this.cWidth ];
		int cr = ( px >> 0x10 ) & 0xFF;
		int cg = ( px >> 0x08 ) & 0xFF;
		int cb = ( px >> 0x00 ) & 0xFF;

		System.out.println( String.format( "%d,%d -> %d,%d,%d", this.mouseX, this.mouseY, cr, cg, cb ) );

	}

}
