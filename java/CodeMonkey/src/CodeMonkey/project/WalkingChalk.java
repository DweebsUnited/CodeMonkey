package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.turing.Turing2D;
import CodeMonkey.turing.TuringMachine;
import CodeMonkey.utility.Pair;
import CodeMonkey.utility.PairT;
import processing.core.PApplet;


public class WalkingChalk extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.WalkingChalk" );

	}

	Random rng = new Random( );

	int sWidth = 720;
	int sHeigh = 640;

	int nTMs = 2048;
	ArrayList< TuringMachine > tms;
	ArrayList< Pair< Integer > > tcs;

	int[ ] sbuf;

	int nCol = 8;

	@Override
	public void settings( ) {

		this.size( this.sWidth, this.sHeigh );

		this.setName( );

	}

	public int c( int s ) {

		return this.color( s * 256 / this.nCol );

	}

	@Override
	public void setup( ) {

		this.tms = new ArrayList< TuringMachine >( );
		this.tcs = new ArrayList< Pair< Integer > >( );
		for( int tdx = 0; tdx < this.nTMs; ++tdx ) {

			TuringMachine tm = new Turing2D( );
			// Symbols, moveDirs, States
			tm.setup( this.nCol + 1, 5, 128 );
			tm.randomize( this.rng );

			Pair< Integer > tc = new Pair< Integer >( this.rng.nextInt( this.sWidth ),
					this.rng.nextInt( this.sHeigh ) );

			this.tms.add( tm );
			this.tcs.add( tc );

		}

		this.sbuf = new int[ this.sWidth * this.sHeigh ];

		this.loadPixels( );
		for( int pdx = 0; pdx < this.sWidth * this.sHeigh; ++pdx ) {

			this.sbuf[ pdx ] = 0;

			this.pixels[ pdx ] = this.c( 0 );

		}
		this.updatePixels( );

	}

	@Override
	public void draw( ) {

		for( int tdx = 0; tdx < this.nTMs; ++tdx ) {

			TuringMachine tm = this.tms.get( tdx );
			Pair< Integer > cs = this.tcs.get( tdx );

			int pdx = cs.a + cs.b * this.sWidth;

			PairT< Integer, Integer > trans = tm.step( this.sbuf[ pdx ] );

			this.sbuf[ pdx ] = trans.a;

			// Apply
			this.loadPixels( );
			this.sbuf[ pdx ] = trans.a;
			this.pixels[ pdx ] = this.c( trans.a );
			this.updatePixels( );

			// Now handle move
			switch( trans.b ) {

				// No move
				case 0:
					// Doopty
					break;

				case 1:
					// UP
					// Java % means remainder, not modulus... :(
					// If it goes negative, it will remain negative, so we have to add the modulus
					// to keep things always positive
					cs.b = ( cs.b - 1 + this.sHeigh ) % this.sHeigh;
					break;
				case 2:
					// DOWN
					cs.b = ( cs.b + 1 ) % this.sHeigh;
					break;
				case 3:
					// LEFT
					cs.a = ( cs.a - 1 + this.sWidth ) % this.sWidth;
					break;
				case 4:
					// RIGHT
					cs.a = ( cs.a + 1 ) % this.sWidth;
					break;

			}

			//			if( this.frameCount % 2048 == 0 )
			//				tm.randomize( this.rng );

		}

	}

	@Override
	public void keyPressed( ) {

		if( this.key == ' ' )
			this.save( );

	}

}
