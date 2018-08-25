package CodeMonkey.graph;

import java.util.ArrayList;

public class DiGraph<T> {

  private static int IDGEN = 0;

  public ArrayList<Node<T>> nodes;

  public DiGraph( ) {

    this.nodes = new ArrayList<Node<T>>( );

  }

  public int newNode( T val ) {

    int id = DiGraph.IDGEN++;

    this.nodes.add( new Node<T>( id, val ) );

    return id;

  }

  public Node<T> getNode( int ID ) {

    // The ID of a node is its index
    if( ID >= this.nodes.size( ) || ID < 0 )
      return null;

    return this.nodes.get( ID );

  }

  public boolean link( int a, int b ) {

    Node<T> na = this.getNode( a );
    Node<T> nb = this.getNode( b );

    if( na == null || nb == null )
      return false;

    na.link( nb );

    return true;

  }

}
