package CodeMonkey.project;

import java.util.LinkedList;
import java.util.Random;

import CodeMonkey.draw.ImageTexture;
import CodeMonkey.spatial.ICoord;
import processing.core.PApplet;
import processing.core.PGraphics;


public class BulbousRamen extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.BulbousRamen" );

	}

	private final int N_SEED = 16;

	private Random rng = new Random( );

	private PGraphics canvas;
	private int cWidth = 1920;
	private int cHeigh = 1080;
	private int nPx = this.cWidth * this.cHeigh;

	private final int OFFSCALE = 16;

	private LinkedList< ICoord > frontier;

	boolean used[];

	ImageTexture tex;
	int[ ] texU;

	private void recurse( ) {

		if( this.frontier.size( ) == 0 )
			return;

		// Pick random from list
		int gdx = this.rng.nextInt( this.frontier.size( ) );

		ICoord c = this.frontier.remove( gdx );

		// Color at c, needed in inner loop - query once
		int h = this.texU[ c.x + c.y * this.cWidth ];

		// System.out.println( String.format( "%d, %d, %d", r, g, b ) );

		// Displace its color to every non-black neighbor
		for( int dx = -1; dx <= 1; ++dx ) {
			for( int dy = -1; dy <= 1; ++dy ) {

				// Ourself - already known to be colored, early termination
				if( dx == 0 && dy == 0 )
					continue;

				// Neighbor ICoords
				int nx = c.x + dx;
				int ny = c.y + dy;

				// Neighbor out of bounds - can't use
				if( nx < 0 || nx >= this.cWidth || ny < 0 || ny >= this.cHeigh )
					continue;

				// Neighbor is already used, skip
				if( this.used[ nx + ny * this.cWidth ] )
					continue;

				// Offset color
				int ho = h + (int) Math.round( this.rng.nextGaussian( ) * this.OFFSCALE );
				if( ho < 0 )
					ho += this.tex.sz2;
				if( ho > this.tex.sz2 )
					ho -= this.tex.sz2;

				this.texU[ nx + ny * this.cWidth ] = ho;
				this.used[ nx + ny * this.cWidth ] = true;
				this.canvas.pixels[ nx + ny * this.cWidth ] = this.tex.colorAt( ho );

				this.frontier.add( new ICoord( nx, ny ) );

			}
		}

	}

	@Override
	public void settings( ) {

		this.size( 720, 640 );

		this.setName( );

	}

	@Override
	public void setup( ) {

		this.tex = new ImageTexture( this, this.loadImage( ProjectBase.dataDir + "StarryNight.jpg" ), 128 );
		this.texU = new int[ this.nPx ];

		this.used = new boolean[ this.nPx ];

		for( int pdx = 0; pdx < this.nPx; ++pdx ) {
			this.texU[ pdx ] = 0;
			this.used[ pdx ] = false;
		}

		this.canvas = this.createGraphics( this.cWidth, this.cHeigh );

		this.frontier = new LinkedList< ICoord >( );

		this.canvas.beginDraw( );
		this.canvas.background( 0 );
		this.canvas.loadPixels( );

		for( int spdx = 0; spdx < this.N_SEED; ++spdx ) {

			int sx = this.rng.nextInt( this.cWidth );
			int sy = this.rng.nextInt( this.cHeigh );
			int sdx = sx + sy * this.cWidth;

			int hStart = this.rng.nextInt( this.tex.sz2 );

			this.texU[ sdx ] = hStart;
			this.used[ sdx ] = true;

			this.canvas.pixels[ sdx ] = this.tex.colorAt( hStart );

			this.frontier.add( new ICoord( sx, sy ) );
		}

		this.canvas.updatePixels( );
		this.canvas.endDraw( );

	}

	@Override
	public void draw( ) {

		if( this.frontier.size( ) == 0 ) {
			this.save( this.canvas );
			this.noLoop( );
		}

		this.canvas.beginDraw( );
		this.canvas.loadPixels( );

		for( int sdx = 0; sdx < 2048; ++sdx )
			this.recurse( );

		this.canvas.updatePixels( );
		this.canvas.endDraw( );

		this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );

	}

}
