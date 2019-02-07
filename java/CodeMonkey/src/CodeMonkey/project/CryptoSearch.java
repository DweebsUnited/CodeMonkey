package CodeMonkey.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import CodeMonkey.utility.Pair;
import CodeMonkey.utility.Trie;

public class CryptoSearch extends Project {

  private static boolean isOnlyLetters( String name ) {
    return name.matches( "[a-zA-Z]+" );
  }

  public static void main( String[ ] args ) {

    Project.setData( );

    Trie trie = new Trie( );

    // Insert each line of file
    BufferedReader reader;
    try {

      reader = new BufferedReader( new FileReader( Project.dataDir + "popular.txt" ) );
      String line = reader.readLine( );

      while( line != null ) {

        // Clean the line - remove all whitespace
        line.replaceAll( "\\s+", "" );

        // Lowercase it
        line = line.toLowerCase( );

        // Only short and only letters
        if( isOnlyLetters( line ) ) {

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

      e.printStackTrace();

    }

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
