package CodeMonkey.turing;

import java.util.Random;

import CodeMonkey.utility.PairT;


public interface TuringMachine {

	void setup( int symbols, int moveDirs, int states );

	void randomize( Random rng );

	PairT< Integer, Integer > step( int read );

	void print( );

}
