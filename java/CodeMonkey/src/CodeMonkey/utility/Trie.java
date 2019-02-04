package CodeMonkey.utility;

import java.util.ArrayList;

public class Trie {

  private String getStringRep( ArrayList<Character> list ) {
    StringBuilder builder = new StringBuilder( list.size( ) );
    for( Character ch: list ) {
      builder.append( ch );
    }
    return builder.toString( );
  }

  private class Node {
    // Character this node represents
    public char c;
    // Whether this is the last character in an entry
    public boolean eos;

    // Next child of parent
    public Node next;
    // First child of us
    public Node child;

  }

  private Node root;
  private int nEntry = 0;

  public Trie( ) {

    this.root = new Node( );

  }

  public int size( ) {
    return this.nEntry;
  }

  public void insert( String s ) {

    this.nEntry += 1;
    this.insert( s, this.root );

  }

  public ArrayList<String> query( String q ) {

    return this.query( q, this.root );

  }

  public void print( ) {

    this.rprint( this.root, new ArrayList<Character>( ) );

  }

  private void rprint( Node trie, ArrayList<Character> progress ) {

    if( trie.eos )
      System.out.println( String.format( ":%s", this.getStringRep( progress ) ) );

    // Recursive print all children
    Node child = trie.child;

    while( child != null ) {

      progress.add( child.c );
      this.rprint( child, progress );
      progress.remove( progress.size( ) - 1 );

      child = child.next;

    }

  }

  private void rquery( String q, Node trie, int cdx, ArrayList<Character> progress, ArrayList<String> res ) {

    // End of recursion: Past end of query
    if( cdx >= q.length( ) ) {

      // But only output if end of string marker
      if( trie.eos )
        res.add( this.getStringRep( progress ) );

      return;

    }

    char c = q.charAt( cdx );

    // Check this first, then it falls through to single character lookup later
    if( Character.isDigit( c ) ) {

      //      System.out.print( String.format( "%d: Digit %c", cdx, c ) );

      // Get the lookback character
      c = progress.get( c - 0x30 );

      //      System.out.println( String.format( "%c", c ) );

    }

    // If c is space, wildcard
    if( c == ' ' ) {

      //      System.out.println( String.format( "%d: Wildcard", cdx ) );

      Node child = trie.child;

      // Make sure there is a child list, otherwise no match
      if( child == null )
        return;

      do {

        //        System.out.println( String.format( "%d: Try: %c", cdx, child.c ) );

        progress.add( child.c );
        this.rquery( q, child, cdx + 1, progress, res );
        progress.remove( progress.size( ) - 1 );

        child = child.next;

      } while( child != null );

      // If c is char, look for specific
    } else if( Character.isLetter( c ) ) {

      //      System.out.println( String.format( "%d: Target: %c", cdx, c ) );

      Node child = trie.child;

      // Make sure there is a child list, otherwise no match
      if( child == null )
        return;

      // Go till we can't, or we arrive
      while( child.next != null && child.c < c )
        child = child.next;

      // No match
      if( child == null )
        ;
      // Match!
      else if( child.c == c ) {

        //        System.out.println( String.format( "%d: Match", cdx ) );

        progress.add( c );
        this.rquery( q, child, cdx + 1, progress, res );
        progress.remove( progress.size( ) - 1 );

      }
      // Else no match

      // Anything else unacceptable
    } else {
      throw new RuntimeException( "Invalid character in query" );
    }

  }

  private ArrayList<String> query( String q, Node trie ) {

    if( trie == null )
      throw new RuntimeException( "Querying a null trie.. Naughty, naughty" );

    // Kick off the recursion
    ArrayList<Character> temp = new ArrayList<Character>( );
    ArrayList<String> res = new ArrayList<String>( );

    this.rquery( q, trie, 0, temp, res );

    return res;

  }

  //  private void rinsert( String s, Node trie, char cdx ) {
  //
  //    // End of recursion: Past end of query
  //    if( cdx >= s.length( ) ) {
  //
  //      // But only output if end of string marker
  //      trie.eos = true;
  //
  //      return;
  //
  //    }
  //
  //    char c = s.charAt( cdx );
  //
  //    //std::cout << cdx << ": Target: " << c << std::endl;
  //
  //    Node child = trie.child;
  //
  //    // Make sure there is a child list, otherwise no match
  //    if( child == null )
  //      return;
  //
  //    // Go till we can't, or we arrive
  //    while( child.next != null && child.c < c )
  //      child = child.next;
  //
  //    // No match
  //    if( child == null )
  //      ;
  //    // Match!
  //    else if( child.c == c ) {
  //
  //      //std::cout << cdx << ": Match" << std::endl;
  //
  //      progress.add( c );
  //      this.rquery( q, child, cdx + 1, progress, res );
  //      progress.remove( progress.size( ) - 1 );
  //
  //    }
  //    // Else no match
  //
  //  }

  // For each character, move along tree until next is null or past
  //   Basically a singly linked list insertion, with a few edge cases
  //   If last, mark
  private void insert( String s, Node trie ) {

    if( trie == null )
      throw new RuntimeException( "Inserting into a null trie.. Naughty, naughty" );

    Node child;

    // Until end of string
    for( int cdx = 0; cdx < s.length( ); ++cdx ) {

      char c = s.charAt( cdx );

      child = trie.child;

      // No children -> Insert new child
      if( child == null ) {

        //        System.out.println( String.format( "New %c", c ) );

        // Set up new child
        Node newChild = new Node( );
        newChild.c = c;

        // Link in to parent
        trie.child = newChild;

        // Prepare for next level
        child = newChild;

      } else {

        // newChild is beginning of list
        if( child.c == c ) {
          // NOP, but need to avoid the else

          //          System.out.println( String.format( "Match %c", c ) );

          // newChild goes at beginning of list
        } else if( child.c > c ) {

          //          System.out.println( String.format( "Head %c", c ) );

          // Set up new child
          Node newChild = new Node( );
          newChild.c = c;

          // Link in to parent
          newChild.next = trie.child;
          trie.child = newChild;

          // Prepare for next level
          child = newChild;

        } else {

          while( child.next != null && child.next.c <= c )
            child = child.next;

          // Current child match, move on
          if( child.c == c ) {

            //            System.out.println( String.format( "Match %c", c ) );

            // Insert after child
          } else {

            //            System.out.println( String.format( "Middln %c", c ) );

            // Set up new child
            Node newChild = new Node( );
            newChild.c = c;

            // Link in after current kid
            newChild.next = child.next;
            child.next = newChild;

            // Prepare for next level
            child = newChild;

          }

        }

      }

      // Move down a level
      trie = child;

    }

    // Mark end of string
    trie.eos = true;

  }

}
