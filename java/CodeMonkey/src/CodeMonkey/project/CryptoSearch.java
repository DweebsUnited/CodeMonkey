package CodeMonkey.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import CodeMonkey.utility.Pair;
import CodeMonkey.utility.PairT;
import CodeMonkey.utility.Trie;


public class CryptoSearch extends Project {

	private static void loadFile( String fname, Trie trie ) {

		// Insert each line of file
		BufferedReader reader;
		try {

			reader = new BufferedReader( new FileReader( fname ) );
			String line = reader.readLine( );

			while( line != null ) {

				// Clean the line - remove all whitespace
				line.replaceAll( "\\s+", "" );

				// Lowercase it
				line = line.toLowerCase( );

				// Only short and only letters
				if( Trie.accept( line ) ) {

					// System.out.println( String.format( "Inserting: %s:%d", line, line.length( ) )
					// );

					trie.insert( line );

				} else {

					System.out.println( String.format( "Rejecting: %s", line ) );

				}

				// Read next line
				line = reader.readLine( );

			}

			reader.close( );

		} catch( IOException e ) {

			e.printStackTrace( );

		}

	}

	private static void writeStats( String sname, String pname, Trie trie ) {

		BufferedWriter writer;
		ArrayList< Pair< Long > > stats = trie.countStats( );
		ArrayList< ArrayList< Pair< Long > > > pstats = trie.posStats( );

		try {

			writer = new BufferedWriter( new FileWriter( sname ) );
			writer.write( String.format( "c,usedCount,eosCount\n" ) );

			for( char cdx = 0; cdx < Trie.alphabet.length( ); ++cdx ) {

				Pair< Long > s = stats.get( cdx );

				writer.write( String.format( "%c,%d,%d\n", Trie.alphabet.charAt( cdx ), s.a, s.b ) );

			}

			writer.close( );

		} catch( IOException e ) {

			System.err.println( "Couldn't write stats" );
			e.printStackTrace( );

		}

		try {

			for( char cdx = 0; cdx < Trie.alphabet.length( ); ++cdx ) {

				writer = new BufferedWriter( new FileWriter( pname + Trie.alphabet.charAt( cdx ) + ".csv" ) );

				writer.write( String.format( "%c\n", Trie.alphabet.charAt( cdx ) ) );
				writer.write( String.format( "pdx,usedCount,eosCount\n" ) );

				// System.out.println( String.format( "Writing: %c", Trie.alphabet.charAt( cdx )
				// ) );

				for( int pdx = 0; pdx < trie.longest( ); ++pdx ) {

					Pair< Long > s = pstats.get( cdx ).get( pdx );

					writer.write( String.format( "%d,%d,%d\n", pdx, s.a, s.b ) );

				}

				writer.close( );

			}

		} catch( IOException e ) {

			System.err.println( "Couldn't write pos stats" );
			e.printStackTrace( );

		}

	}

	public static void main( String[ ] args ) throws IOException {

		Project.setData( );

		Trie trie = new Trie( );

		// Load files

		// loadFile( Project.dataDir + "popular.txt", trie );
		// loadFile( "C:\\Users\\ElysiumTech\\Downloads\\OANC\\wordlist.txt", trie );
		// trie.save( Project.dataDir + "OANC.trie" );

		trie.load( Project.dataDir + "Wikipedia.trie" );

		// trie.print( );
		System.out.println( String.format( "Entries added: %d", trie.size( ) ) );
		System.out.println( String.format( "Longest entry: %d", trie.longest( ) ) );

		// Now for the queries
		System.out.print( ">" );
		Scanner scan = new Scanner( System.in );
		while( true ) {

			String line = scan.nextLine( );

			// Clean the line - remove all whitespace
			line.replaceAll( "\\s+", "" );

			// Lowercase it
			line = line.toLowerCase( );

			if( line.equals( "exit" ) || line.equals( "quit" ) )
				break;
			else if( line.equals( "p" ) )
				trie.print( );
			else if( line.equals( "s" ) ) {

				ArrayList< Pair< Long > > stats = trie.countStats( );

				for( char cdx = 0; cdx < Trie.alphabet.length( ); ++cdx ) {

					Pair< Long > s = stats.get( cdx );

					System.out.println( String.format( "%c, %d:%d", Trie.alphabet.charAt( cdx ), s.a, s.b ) );

				}

			} else if( line.equals( "sp" ) ) {

				ArrayList< ArrayList< Pair< Long > > > stats = trie.posStats( );

				for( char cdx = 0; cdx < Trie.alphabet.length( ); ++cdx ) {

					System.out.println( String.format( "%c", Trie.alphabet.charAt( cdx ) ) );

					for( int pdx = 0; pdx < trie.longest( ); ++pdx ) {

						Pair< Long > s = stats.get( cdx ).get( pdx );

						System.out.println( String.format( "  %d:%d", s.a, s.b ) );

					}

				}

			} else if( line.equals( "ws" ) ) {

				CryptoSearch.writeStats( Project.dataDir + "stats.csv", Project.dataDir + "posstats/", trie );

			}

			System.out.println( String.format( "Searching: %s", line ) );

			ArrayList< PairT< Long, String > > res = trie.query( line );

			// Max, used for filtering
			long maxC = res.get( 0 ).a;

			// Num per line
			int nLine = (int) Math.floor( 80f / ( line.length( ) + 2 + 4 ) );

			for( int rdx = 0; rdx < res.size( ); ++rdx ) {

				// Guaranteed top 100, then only top 95%
				if( res.get( rdx ).a < maxC * 0.05f && rdx > 100 )
					break;

				System.out.print( String.format( "%04d:%s ", res.get( rdx ).a, res.get( rdx ).b ) );

				if( rdx % nLine == nLine - 1 )
					System.out.println( );

			}

			System.out.println( );
			System.out.print( ">" );

		}

		scan.close( );

	}

}
