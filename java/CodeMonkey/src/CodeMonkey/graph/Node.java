package CodeMonkey.graph;

import java.util.ArrayList;


public class Node< T > {

	public int ID;
	public T val;
	public ArrayList< Node< T > > links;

	public Node( int ID, T val ) {

		this.ID = ID;
		this.val = val;
		this.links = new ArrayList< Node< T > >( );

	}

	public boolean link( Node< T > tgt ) {

		return this.links.add( tgt );

	}

	public boolean unlink( Node< T > tgt ) {

		return this.links.remove( tgt );

	}

}
