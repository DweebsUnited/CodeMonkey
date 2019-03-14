package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import CodeMonkey.spatial.ICoord;
import processing.core.PApplet;
import processing.core.PGraphics;


public class BulbousBottle extends ProjectBase {

	private float DBFSConst = 0.5f;

	Random rng = new Random( );

	private PGraphics canvas;
	private int cWidth = 1920;
	private int cHeigh = 1080;

	private boolean[ ] used;

	class RecurseState {

		public int x, y;
		public ArrayList< Integer > kids;

		public RecurseState( int x, int y ) {

			this.x = x;
			this.y = y;

			this.kids = new ArrayList< Integer >( );
			for( int kdx = 0; kdx < BulbousBottle.this.kidC.size( ); ++kdx )
				this.kids.add( kdx );

			Collections.shuffle( this.kids );

		}

	}

	private LinkedList< RecurseState > stack;

	private float[ ] sortVal;
	private ArrayList< ICoord > kidC;

	private int r = this.rng.nextInt( 256 );
	private int g = this.rng.nextInt( 256 );
	private int b = this.rng.nextInt( 256 );
	private int COL_OFF = 1;

	private void addToStack( int nx, int ny ) {

		RecurseState ns = new RecurseState( nx, ny );
		this.stack.addLast( ns );

		// Set color as we add it to the stack
		this.canvas.pixels[ nx + ny * this.cWidth ] = this.color( this.r, this.g, this.b );
		this.r += (int) Math.round( this.rng.nextGaussian( ) * this.COL_OFF );
		if( this.r > 255 )
			this.r = 255;
		if( this.r < 0 )
			this.r = 0;
		this.g += (int) Math.round( this.rng.nextGaussian( ) * this.COL_OFF );
		if( this.g > 255 )
			this.g = 255;
		if( this.g < 0 )
			this.g = 0;
		this.b += (int) Math.round( this.rng.nextGaussian( ) * this.COL_OFF );
		if( this.b > 255 )
			this.b = 255;
		if( this.b < 0 )
			this.b = 0;

		this.used[ nx + ny * this.cWidth ] = true;

	}

	private void recurse( ) {

		if( this.stack.size( ) == 0 )
			return;

		// Get state from stack
		int getDx = this.rng.nextFloat( ) < this.DBFSConst ? this.stack.size( ) - 1 : 0;
		RecurseState s = this.stack.get( getDx );
		this.DBFSConst = this.noise( s.x, s.y );

		// If last child is best child
		if( s.kids.size( ) == 1 )
			this.stack.remove( getDx );

		// Check next child, add to stack if need be
		int kdx = s.kids.get( s.kids.size( ) - 1 );
		s.kids.remove( s.kids.size( ) - 1 );

		// If in grid, recurse on it
		ICoord k = this.kidC.get( kdx );
		int nx = s.x + k.x;
		int ny = s.y + k.y;

		if( nx >= 0 && nx < this.cWidth && ny >= 0 && ny < this.cHeigh ) {

			if( !this.used[ nx + ny * this.cWidth ] ) {

				this.addToStack( nx, ny );

			}

		}

	}

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.BulbousBottle" );

	}

	@Override
	public void settings( ) {

		this.size( 720, 640 );

		this.setName( );

	}

	@Override
	public void setup( ) {

		this.canvas = this.createGraphics( this.cWidth, this.cHeigh );

		this.stack = new LinkedList< RecurseState >( );

		this.used = new boolean[ this.cWidth * this.cHeigh ];
		for( int pdx = 0; pdx < this.cWidth * this.cHeigh; ++pdx )
			this.used[ pdx ] = false;

		this.sortVal = new float[ this.cWidth * this.cHeigh ];
		for( int ydx = 0; ydx < this.cHeigh; ++ydx ) {
			for( int xdx = 0; xdx < this.cWidth; ++xdx ) {

				float y = ydx - this.cHeigh / 2f;
				float x = xdx - this.cWidth / 2f;

				this.sortVal[ xdx + ydx * this.cWidth ] = y * y + x * x;

			}
		}

		// Octagonal directions
		this.kidC = new ArrayList< ICoord >(
				Arrays.asList( new ICoord( -1, -1 ), new ICoord( 0, -1 ), new ICoord( 1, -1 ), new ICoord( -1, 0 ),
						new ICoord( 1, 0 ), new ICoord( -1, 1 ), new ICoord( 0, 1 ), new ICoord( 1, 1 ) ) );

		// Add the first pixel (midscreen)

		this.canvas.beginDraw( );
		this.canvas.loadPixels( );

		this.addToStack( this.cWidth / 2, this.cHeigh / 2 );

		this.canvas.updatePixels( );
		this.canvas.endDraw( );

	}

	@Override
	public void draw( ) {

		if( this.stack.size( ) == 0 ) {
			this.save( this.canvas );
			this.noLoop( );
		}

		this.canvas.beginDraw( );
		this.canvas.loadPixels( );

		for( int sdx = 0; sdx < 4096; ++sdx )
			this.recurse( );

		this.canvas.updatePixels( );
		this.canvas.endDraw( );

		this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );

	}

}
