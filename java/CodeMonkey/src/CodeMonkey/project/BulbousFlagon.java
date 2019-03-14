package CodeMonkey.project;

import java.util.LinkedList;
import java.util.Random;

import CodeMonkey.spatial.ICoord;
import processing.core.PApplet;
import processing.core.PGraphics;


public class BulbousFlagon extends ProjectBase {

	private Random rng = new Random( );

	private PGraphics canvas;
	private int cWidth = 1920;
	private int cHeigh = 1080;

	private final int OFFSCALE = 5;

	private LinkedList< ICoord > frontier;

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.BulbousFlagon" );

	}

	private void recurse( ) {

		if( this.frontier.size( ) == 0 )
			return;

		// Pick random from list
		int gdx = this.rng.nextInt( this.frontier.size( ) );

		ICoord c = this.frontier.remove( gdx );

		// Color at c, needed in inner loop - query once
		int cc = this.canvas.pixels[ c.x + c.y * this.cWidth ];
		int r = ( cc & 0x0000FF ) >> 0x00;
		int g = ( cc & 0x00FF00 ) >> 0x08;
		int b = ( cc & 0xFF0000 ) >> 0x10;

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

				// Neighbor is not black - already used, skip
				if( ( this.canvas.pixels[ nx + ny * this.cWidth ] & 0xFFFFFF ) != 0 )
					continue;

				// Offsets for
				int ro = r + (int) Math.round( this.rng.nextGaussian( ) * this.OFFSCALE );
				if( ro < 0 )
					ro = 0;
				int go = g + (int) Math.round( this.rng.nextGaussian( ) * this.OFFSCALE );
				if( go < 0 )
					go = 0;
				int bo = b + (int) Math.round( this.rng.nextGaussian( ) * this.OFFSCALE );
				if( bo < 0 )
					bo = 0;

				this.canvas.pixels[ nx + ny * this.cWidth ] = 0xFF000000 | ( ( ro & 0xFF ) << 0x00 )
						| ( ( go & 0xFF ) << 0x08 ) | ( ( bo & 0xFF ) << 0x10 );

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

		this.canvas = this.createGraphics( this.cWidth, this.cHeigh );

		this.canvas.beginDraw( );
		this.canvas.background( 0 );
		this.canvas.loadPixels( );
		this.canvas.pixels[ this.cWidth / 2 + this.cHeigh / 2 * this.cWidth ] = this.color( this.rng.nextInt( 256 ),
				this.rng.nextInt( 256 ), this.rng.nextInt( 256 ) );
		this.canvas.updatePixels( );
		this.canvas.endDraw( );

		this.frontier = new LinkedList< ICoord >( );
		this.frontier.add( new ICoord( this.cWidth / 2, this.cHeigh / 2 ) );

	}

	@Override
	public void draw( ) {

		if( this.frontier.size( ) == 0 ) {
			this.canvas.save( "C:\\Users\\ElysiumTech\\Desktop\\Temp.png" );
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
