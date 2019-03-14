package CodeMonkey.project;

import java.util.Random;

import CodeMonkey.transform.CoordinateTransform;
import CodeMonkey.transform.coordinate.CTLinear;
import processing.core.PApplet;
import processing.core.PVector;


public class DrinkingHorse extends PApplet {

	private static String CM = "/Users/ozzy/Documents/CodeMonkey/";
	private static String dataDir = DrinkingHorse.CM + "data/";

	private Random rng = new Random( );

	private int nAxis = 2;

	// The new basis vectors
	private PVector basis[];

	// Transform to draw points on screen
	private CoordinateTransform screen;

	// Transform to draw basis vectors on screen
	private CoordinateTransform screenBasis;

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.DrinkingHorse" );

	}

	@Override
	public void settings( ) {

		this.size( 720, 640 );

	}

	@Override
	public void setup( ) {

		// Make a transform for drawing the points on screen
		// TODO: Make this determined from the basis vectors
		this.screen = new CTLinear( new PVector( -this.nAxis, -this.nAxis ), new PVector( this.nAxis, this.nAxis ),
				new PVector( 0, 0 ), new PVector( this.pixelWidth, this.pixelHeight ) );

		// Set up the container

		this.basis = new PVector[ this.nAxis ];

		// Set up the basis drawing transform

		this.screenBasis = new CTLinear( -1, 1, 0, 75 );

		// Make a new set of basis vectors

		this.makeNewBasis( );

	}

	@Override
	public void draw( ) {

		PVector p = new PVector( 0, 0 );

		float c[] = new float[ this.nAxis ];
		float accum;

		for( int pdx = 0; pdx < 25; ++pdx ) {

			// Generate points on a unit sphere, draw them on the plane

			p.set( 0, 0 );
			accum = 0;

			for( int adx = 0; adx < this.nAxis - 1; ++adx ) {

				float r = this.rng.nextFloat( );
				r = 2 * r - 1;
				// r = (float) Math.pow( r, 0.5f ) * ( this.rng.nextFloat( ) > 0.5 ? 1 : -1 );
				c[ adx ] = r;
				accum += (float) Math.pow( r, 2 );

			}

			// sum( c^2 ) + l^2 = 1
			// l = sqrt( 1 - sum( c^2 ) )
			c[ this.nAxis - 1 ] = (float) Math.sqrt( 1 - accum ) * ( this.rng.nextFloat( ) > 0.5 ? 1 : -1 );

			System.out.println( String.format( "%f: %f", accum, c[ this.nAxis - 1 ] ) );

			for( int adx = 0; adx < this.nAxis; ++adx ) {

				PVector ac = this.basis[ adx ].copy( );
				ac.mult( c[ adx ] );
				p.add( ac );

			}

			// All basis vectors are coplanar, so z MUST equal 0
			// If it doesnt... god help us all

			p = this.screen.map( p );

			this.ellipse( p.x, p.y, 3, 3 );

		}

	}

	@Override
	public void keyPressed( ) {

		if( this.key == ' ' ) {
			this.makeNewBasis( );
		} else if( this.key == 'w' ) {
			this.save( DrinkingHorse.dataDir + "DrinkingHorse.png" );
		}

	}

	private void makeNewBasis( ) {

		// Clear screen
		this.background( 0 );

		this.stroke( 255 );
		this.noFill( );

		for( int adx = 0; adx < this.nAxis; ++adx ) {

			this.basis[ adx ] = new PVector( 2 * this.rng.nextFloat( ) - 1, 2 * this.rng.nextFloat( ) - 1, 0 );
			this.basis[ adx ].normalize( );

			// Draw basis in a square in the top corner

			// Color the last differently so we can detect visual differences
			if( adx == this.nAxis - 1 )
				this.stroke( 255, 0, 0 );
			PVector origin = this.screenBasis.map( new PVector( 0, 0 ) );
			PVector sBasis = this.screenBasis.map( this.basis[ adx ] );
			this.line( origin.x, origin.y, sBasis.x, sBasis.y );

		}

		// Set up to draw some points
		this.noStroke( );
		this.fill( 255, 5 );

	}

}
