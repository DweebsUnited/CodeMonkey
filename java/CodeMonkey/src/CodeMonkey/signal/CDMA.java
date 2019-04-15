package CodeMonkey.signal;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

import jinahya.bit.io.BitInput;
import jinahya.bit.io.BitOutput;


public class CDMA {

	// TODO: DNE ( Do Not Encode ) mask, to not mux certain channels
	// Encoding: Given codebits parallel bitstreams, create one bitstream
	public static void encode( ArrayList< BitInput > channels, float[ ][ ] codes, BitOutput output )
			throws IOException {

		// For each bit in input:
		// Map data: 0,1 -> -1,1
		//
		// Calculate codebits symbols:
		// For a bit from each channel:
		// For each cbit in code:
		// sum( bit * cbit )
		//
		// For each calculated symbol, write out as codebits + 2 bits

		int codebits = codes.length;

		if( channels.size( ) != codebits )
			throw new RuntimeException( "Incorrect number of input channels (Use null if not in use)" );
		for( int codedx = 0; codedx < codebits; ++codedx )
			if( codes[ codedx ].length != codebits )
				throw new RuntimeException( String.format( "Code %d is not the proper size" ) );


		// Carry on my
		boolean waywardSon;

		while( true ) {

			// This is where we will construct the symbols
			int[ ] symbols = new int[ codebits ];
			for( int sdx = 0; sdx < codebits; ++sdx )
				symbols[ sdx ] = 0;

			// Now we try to get an input bit from each channel
			// If no channels have data remaining, we will exit
			waywardSon = false;
			for( int chandx = 0; chandx < codebits; ++chandx ) {

				int ib;
				try {
					ib = channels.get( chandx ).readBoolean( ) ? 1 : -1;
				} catch( EOFException e ) {
					continue;
				}

				// Carry on my
				waywardSon = true;
				for( int codedx = 0; codedx < codebits; ++codedx )
					symbols[ codedx ] += codes[ chandx ][ codedx ] * ib;

			}

			// All have EOFd, processing is done
			if( !waywardSon )
				break;

			// Now we process the coded data
			for( int codedx = 0; codedx < codebits; ++codedx )
				output.writeInt( false, codebits + 2, symbols[ codedx ] );

		}

		// Pad with null data to not have writers freak out
		output.align( 4 );

	}

	// Decoding: Given one bytestream( codebits )
	// Dot product with each code
	// Map result by element: -,0,+ -> 0,DNE,1
	// DNE means nothing was sent on this channel

}
