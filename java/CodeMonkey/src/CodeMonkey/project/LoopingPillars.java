package CodeMonkey.project;

import java.util.Random;

import CodeMonkey.neuron.RNN;
import processing.core.PApplet;


public class LoopingPillars extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.LoopingPillars" );

	}

	private Random rng = new Random( );

	private int sWidth = 720;
	private int sHeigh = 320;

	private int rnnLenH = this.sHeigh; // TODO: Vary this....
	private RNN gen;

	int wCol = 1;
	float[ ] bufA, bufB;

	@Override
	public void settings( ) {

		this.size( this.sWidth, this.sHeigh * 2 );

		this.setName( );

	}

	@Override
	public void setup( ) {

		this.background( 0 );

		this.gen = new RNN( this.sHeigh, this.rnnLenH, this.sHeigh, this.rng );

		this.bufA = new float[ this.sHeigh ];
		this.bufB = new float[ this.sHeigh ];

		for( int cdx = 0; cdx < this.sHeigh; ++cdx )
			this.bufA[ cdx ] = this.rng.nextFloat( );

	}

	@Override
	public void draw( ) {

		this.gen.step( this.bufA, this.bufB );

		// Get range ( softmax equiv? )
		float m = Float.POSITIVE_INFINITY;
		float M = Float.NEGATIVE_INFINITY;
		for( int ydx = 0; ydx < this.sHeigh; ++ydx ) {

			if( this.bufB[ ydx ] < m )
				m = this.bufB[ ydx ];
			if( this.bufB[ ydx ] > M )
				M = this.bufB[ ydx ];

		}

		// Make M the range
		M -= m;

		m = -1;
		M = 2;

		// Write a column
		this.loadPixels( );
		for( int ydx = 0; ydx < this.sHeigh; ++ydx ) {

			this.pixels[ this.wCol + ( ydx * 2 ) * this.sWidth ] = this.color( ( this.bufB[ ydx ] - m ) / M * 255 );
			this.pixels[ this.wCol + ( ydx * 2 + 1 ) * this.sWidth ] = this.color( ( this.bufB[ ydx ] - m ) / M * 255 );

		}
		this.updatePixels( );

		this.wCol = ( this.wCol + 1 ) % this.sWidth;

		// ENTROPY
		for( int ydx = 0; ydx < this.sHeigh; ++ydx )
			if( this.rng.nextFloat( ) < 0.25 )
				this.bufA[ ydx ] += this.rng.nextFloat( ) - 0.5;

	}

	@Override
	public void keyPressed( ) {

		if( this.key == ' ' ) {

			this.gen.randWeight( this.rng );

			for( int cdx = 0; cdx < this.sHeigh; ++cdx )
				this.bufA[ cdx ] = this.rng.nextFloat( );

		}

	}

}
