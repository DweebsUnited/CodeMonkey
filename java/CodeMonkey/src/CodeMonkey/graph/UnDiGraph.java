package CodeMonkey.graph;

import java.util.ArrayList;
import java.util.function.ToDoubleBiFunction;

public class UnDiGraph<T> extends DiGraph<T> {

  private static final int PATH_CUTOFF = 50;

  public UnDiGraph( ) {

    super( );

  }

  @Override
  public boolean link( int a, int b ) {

    Node<T> na = this.getNode( a );
    Node<T> nb = this.getNode( b );

    if( na == null || nb == null )
      return false;

    na.link( nb );
    nb.link( na );

    return true;

  }

  public ArrayList<Integer> greedyPath( int a, int b, ToDoubleBiFunction<T, T> dist ) {

    ArrayList<Integer> path = new ArrayList<Integer>( );

    path.add( a );

    Node<T> src  = this.getNode( a );
    Node<T> dest = this.getNode( b );

    int curr = a;
    Node<T> currN = src;

    while( curr != b ) {

      // Bail if we get too long -> greedy can be trapped

      if( path.size( ) > PATH_CUTOFF )
        return null;

      // Find neighbor of curr closest to b
      //   TODO: Don't allow using same node twice
      double closen = Float.POSITIVE_INFINITY;

      for( Node<T> neigh : currN.links ) {

        double neighd = dist.applyAsDouble( dest.val, neigh.val );

        if( neighd < closen ) {

          closen = neighd;

          curr = neigh.ID;
          currN = neigh;

        }

      }

      path.add( curr );

    }

    return path;

  }

}
