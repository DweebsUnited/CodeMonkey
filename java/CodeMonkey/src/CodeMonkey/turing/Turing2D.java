package CodeMonkey.turing;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.utility.PairT;


public class Turing2D implements TuringMachine {

	private class Transition {

		int writeSym;
		int moveDir;
		int nextState;

	};

	int nSym, nMve, nStt;
	private ArrayList< ArrayList< Transition > > trans;

	int cState;

	@Override
	public void setup( int symbols, int moveDirs, int states ) {

		this.nSym = symbols;
		this.nMve = moveDirs;
		this.nStt = states;

		this.trans = new ArrayList< ArrayList< Transition > >( );

		for( int sdx = 0; sdx < symbols; ++sdx ) {

			ArrayList< Transition > row = new ArrayList< Transition >( );

			for( int tdx = 0; tdx < states; ++tdx ) {

				row.add( new Transition( ) );

			}

			this.trans.add( row );

		}

		this.cState = 0;

	}

	@Override
	public void randomize( Random rng ) {

		// Must be called after setup
		if( this.trans == null )
			throw new RuntimeException( "Must set up turing machine before randomizing" );

		for( int rdx = 0; rdx < this.nSym; ++rdx ) {

			ArrayList< Transition > readSym = this.trans.get( rdx );

			for( int sdx = 0; sdx < this.nStt; ++sdx ) {

				Transition t = readSym.get( sdx );

				// Restrict so that we never enter a halt state ;)
				// This is overrestricted actually, but whatevs

				// Halt <--> nState == state && write == read && move == None
				// Here --> write != read && nState != state

				t.writeSym = rdx;
				while( t.writeSym == rdx )
					t.writeSym = rng.nextInt( this.nSym );

				t.moveDir = rng.nextInt( this.nMve );

				t.nextState = sdx;
				while( t.nextState == sdx )
					t.nextState = rng.nextInt( this.nStt );


			}

		}

		this.cState = rng.nextInt( this.nStt );

	}

	@Override
	public PairT< Integer, Integer > step( int read ) {

		ArrayList< Transition > r = this.trans.get( read );
		Transition t = r.get( this.cState );

		this.cState = t.nextState;
		return new PairT< Integer, Integer >( t.writeSym, t.moveDir );

	}

	@Override
	public void print( ) {

		// Print headers * m states
		// For each of n symbols
		//   Print wt, mv, ns
		System.out.print( "         " );
		for( int mdx = 0; mdx < this.nStt; ++mdx )
			System.out.print( String.format( "%-26s ", mdx ) );
		System.out.println( );

		System.out.print( "Read     " );
		for( int mdx = 0; mdx < this.nStt; ++mdx )
			System.out.print( "Write    Move     NxtState " );
		System.out.println( );

		for( int sdx = 0; sdx < this.nSym; ++sdx ) {

			System.out.print( String.format( "%-8d ", sdx ) );
			ArrayList< Transition > r = this.trans.get( sdx );

			for( int mdx = 0; mdx < this.nStt; ++mdx ) {

				Transition t = r.get( mdx );
				System.out.print( String.format( "%-8s %-8s %-8s ", t.writeSym, t.moveDir, t.nextState ) );

			}
			System.out.println( );

		}

	}


}
