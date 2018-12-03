package CodeMonkey.lindenmayer;

import java.util.ArrayList;
import java.util.function.BiFunction;

public class RecursiveRewrite {

  private String alphabet, constants;
  private String[ ] rules;

  private ArrayList<ArrayList<Node>> nrules;

  private class Node {

    public char constant = 0;
    public ArrayList<Node> link = null;

    public Node( char c ) {

      this.constant = c;

    }

    public Node( ArrayList<Node> link ) {

      this.link = link;

    }

  }

  public RecursiveRewrite( String alphabet, String constants, String[ ] rules ) {

    this.alphabet = alphabet;
    this.constants = constants;
    this.rules = rules;

    // Set up rules
    this.nrules = new ArrayList<ArrayList<Node>>( );

    for( int rdx = 0; rdx < alphabet.length( ); ++rdx ) {

      this.nrules.add( new ArrayList<Node>( ) );

    }

    // For each character in each rule
    // If in alphabet, link to it
    // If constant add
    for( int rdx = 0; rdx < alphabet.length( ); ++rdx ) {

      ArrayList<Node> rule = this.nrules.get( rdx );

      for( int cdx = 0; cdx < rules[ rdx ].length( ); ++cdx ) {

        char c = rules[ rdx ].charAt( cdx );
        int rcdx = alphabet.indexOf( c );

        if( rcdx > -1 )

          rule.add( new Node( this.nrules.get( rcdx ) ) );

        else

          rule.add( new Node( c ) );

      }

    }

  }

  public <T> void generate( String axiom, int r, T s, ArrayList<BiFunction<T,Character,T>> callbacks ) {

    if( r < 0 )
      return;

    // for each character in axiom, if constant call the callback
    // If in alphabet recurse through rule

    for( int cdx = 0; cdx < axiom.length( ); ++cdx ) {

      char c = axiom.charAt( cdx );
      int rcdx = this.alphabet.indexOf( c );

      if( rcdx > -1 )
        this.recurse( this.nrules.get( rcdx ), r, s, callbacks );

    }

  }

  private <T> void recurse( ArrayList<Node> rule, int r, T s, ArrayList<BiFunction<T,Character,T>> callbacks ) {

    // At 0 follow no links
    for( Node n : rule ) {

      if( n.constant > 0 ) {
        s = callbacks.get( this.constants.indexOf( n.constant ) ).apply( s, n.constant );
      } else if( r > 0 )
        this.recurse( n.link, r - 1, s, callbacks );

    }

  }

}
