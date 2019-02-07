package CodeMonkey.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import CodeMonkey.utility.Pair;
import CodeMonkey.utility.Trie;

public class CryptoSearch extends Project {

  private static boolean accept( String name ) {
    return name.matches( "[a-z]+" );
  }

  private static void loadFile( String fname, Trie trie ) {

    //Insert each line of file
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
        if( accept( line ) ) {

          // System.out.println( String.format( "Inserting: %s:%d", line, line.length( ) ) );

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
    ArrayList<Pair<Long>> stats = trie.countStats( );
    ArrayList<ArrayList<Pair<Long>>> pstats = trie.posStats( );

    try {

      writer = new BufferedWriter( new FileWriter( sname ) );

      for( char cdx = 'a'; cdx <= 'z'; ++cdx ) {

        Pair<Long> s = stats.get( cdx - 'a' );

        System.out.println( String.format( "%c, %d:%d", cdx, s.a, s.b ) );

      }

      writer.close( );

    } catch( IOException e ) {

      System.err.println( "Couldn't write stats" );
      e.printStackTrace( );

    }

    try {

      writer = new BufferedWriter( new FileWriter( pname ) );

      for( char cdx = 'a'; cdx <= 'z'; ++cdx ) {

        System.out.println( String.format( "%c", cdx ) );

        for( int pdx = 0; pdx < trie.longest( ); ++pdx ) {

          Pair<Long> s = pstats.get( cdx - 'a' ).get( pdx );

          System.out.println( String.format( "  %d:%d", s.a, s.b ) );

        }

      }

    } catch( IOException e ) {

      System.err.println( "Couldn't write pos stats" );
      e.printStackTrace( );

    }

  }

  public static void main( String[ ] args ) {

    Project.setData( );

    Trie trie = new Trie( );

    // Load files
    loadFile( Project.dataDir + "popular.txt", trie );
    loadFile( Project.dataDir + "5000.txt", trie );

    //    trie.print( );
    System.out.println( String.format( "Entries added: %d", trie.size( ) ) );

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

        ArrayList<Pair<Long>> stats = trie.countStats( );

        for( char cdx = 'a'; cdx <= 'z'; ++cdx ) {

          Pair<Long> s = stats.get( cdx - 'a' );

          System.out.println( String.format( "%c, %d:%d", cdx, s.a, s.b ) );

        }

      } else if( line.equals( "sp" ) ) {

        ArrayList<ArrayList<Pair<Long>>> stats = trie.posStats( );

        for( char cdx = 'a'; cdx <= 'z'; ++cdx ) {

          System.out.println( String.format( "%c", cdx ) );

          for( int pdx = 0; pdx < trie.longest( ); ++pdx ) {

            Pair<Long> s = stats.get( cdx - 'a' ).get( pdx );

            System.out.println( String.format( "  %d:%d", s.a, s.b ) );

          }

        }

      } else if( line.equals( "ws" ) ) {

        writeStats( Project.dataDir + "stats.csv", Project.dataDir + "posstats.csv", trie );

      }

      System.out.println( String.format( "Searching: %s", line ) );

      ArrayList<String> res = trie.query( line );

      // Num per line
      int nLine = (int)Math.floor( 80f / ( line.length( ) + 2 ) );

      for( int rdx = 0; rdx < res.size( ); ++rdx ) {

        System.out.print( String.format( ":%s ", res.get( rdx ) ) );

        if( rdx % nLine == nLine - 1 )
          System.out.println( );

      }

      System.out.println( );
      System.out.print( ">" );

    }

  }

}
