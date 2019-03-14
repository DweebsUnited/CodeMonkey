package CodeMonkey.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;


public class Trie {

	public static final String alphabet = "abcdefghijklmnopqrstuvwxyz-\'";
	public static final HashSet< Character > alphabetChars = new HashSet< Character >( );
	static {

		for( char c : Trie.alphabet.toCharArray( ) )
			Trie.alphabetChars.add( c );

	}

	public static boolean accept( String s ) {

		boolean works = true;

		for( char c : s.toCharArray( ) ) {
			if( !Trie.accept( c ) ) {
				works = false;
				break;
			}
		}

		return works;

	}

	public static boolean accept( char c ) {

		return Trie.alphabetChars.contains( c );

	}


	private String getStringRep( ArrayList< Character > list ) {

		StringBuilder builder = new StringBuilder( list.size( ) );
		for( Character ch : list )
			builder.append( ch );
		return builder.toString( );
	}


	private class Node {

		// Character this node represents
		public char c = ' ';
		// Whether this is the last character in an entry
		public boolean eos = false;

		// Next child of parent
		public Node next = null;
		// First child of us
		public Node child = null;

		// STATISTIIIIIIIICS!!!
		long usedCount = 0;
		long eosCount = 0;

		@Override
		public String toString( ) {

			return String.format( "%c:%d:%d:%c:%c\n", this.c, this.usedCount, this.eosCount,
					this.child == null ? ' ' : 'C', this.next == null ? ' ' : 'N' );

		}

	}

	private class QueryComparator implements Comparator< PairT< Long, String > > {

		@Override
		public int compare( PairT< Long, String > a, PairT< Long, String > b ) {

			return (int) ( b.a - a.a );

		}

	}

	private class NodeIterator implements Iterator< PairT< Integer, Node > > {

		private Stack< Node > stack;
		private Node curr;

		public NodeIterator( Node root ) {

			if( root.child == null )
				throw new RuntimeException( "Can't build an iterator without children!" );

			this.stack = new Stack< Node >( );
			this.curr = root.child;

		}

		@Override
		public boolean hasNext( ) {

			// If we still have a node, we are not done yet
			return this.curr != null;

		}

		@Override
		public PairT< Integer, Node > next( ) {

			int d = this.stack.size( );
			Node ret = this.curr;

			// If node has kids, push to stack, move down
			if( this.curr.child != null ) {

				this.stack.push( this.curr );
				this.curr = this.curr.child;

				// If not, move to next
			} else if( this.curr.next != null ) {

				this.curr = this.curr.next;

				// If neither, pop until next, move to
			} else if( !this.stack.empty( ) ) {

				do {

					this.curr = this.stack.pop( );

				} while( !this.stack.empty( ) && this.curr.next == null );

				// In base case: no next, set to null ==> ===
				this.curr = this.curr.next;

			}

			return new PairT< Integer, Node >( d, ret );

		}

	}


	private Node root;
	private int longest = 0;


	public Trie( ) {

		this.root = new Node( );

	}


	public long size( ) {

		return this.root.usedCount;
	}

	public int longest( ) {

		return this.longest;
	}


	public void insert( String s ) {

		if( !Trie.accept( s ) )
			throw new RuntimeException( "Invalid characters in: " + s );

		if( s.length( ) > this.longest ) {
			this.longest = s.length( );
			// System.out.println( String.format( "New longest: %s:%d", s, s.length( ) ) );
		}

		Node trie = this.root;
		Node child;

		// Until end of string
		for( int cdx = 0; cdx < s.length( ); ++cdx ) {

			char c = s.charAt( cdx );

			child = trie.child;

			// No children -> Insert new child
			if( child == null ) {

				// System.out.println( String.format( "New %c", c ) );

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

					// System.out.println( String.format( "Match %c", c ) );

					// newChild goes at beginning of list
				} else if( child.c > c ) {

					// System.out.println( String.format( "Head %c", c ) );

					// Set up new child
					Node newChild = new Node( );
					newChild.c = c;

					// Link in to parent
					newChild.next = trie.child;
					trie.child = newChild;

					// Prepare for next level
					child = newChild;

				} else {

					// Move ahead as far as we can
					while( child.next != null && child.next.c <= c )
						child = child.next;

					// Current child match, move on
					if( child.c == c ) {

						// System.out.println( String.format( "Match %c", c ) );

						// Insert after child
					} else {

						// System.out.println( String.format( "Middln %c", c ) );

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
			trie.usedCount += 1;
			trie = child;

		}

		// Mark end of string
		trie.usedCount += 1;
		trie.eosCount += 1;
		trie.eos = true;

	}

	public ArrayList< PairT< Long, String > > query( String q ) {

		// Kick off the recursion
		ArrayList< Character > temp = new ArrayList< Character >( );
		ArrayList< PairT< Long, String > > qres = new ArrayList< PairT< Long, String > >( );

		// Run the query
		this.rquery( q, this.root, 0, temp, qres );

		// Sort by frequency (count)
		Collections.sort( qres, new QueryComparator( ) );

		// Return
		return qres;

	}


	public void print( ) {

		this.rprint( this.root, new ArrayList< Character >( ) );

	}

	public void save( String fname ) throws IOException {

		BufferedWriter writer = new BufferedWriter( new FileWriter( fname ) );
		NodeIterator iter = new NodeIterator( this.root );

		while( iter.hasNext( ) ) {

			Node n = iter.next( ).b;

			writer.write( n.toString( ) );

		}

		writer.close( );

	}

	public void load( String fname ) throws IOException {

		if( this.root.child != null )
			throw new RuntimeException( "TODO: Merge not yet implemented" );

		this.root = new Node( );
		Stack< TripleT< Node, Boolean, Boolean > > stack = new Stack< TripleT< Node, Boolean, Boolean > >( );
		Node curr = this.root;
		boolean hasChild = true;
		boolean hasNext = false;

		BufferedReader reader = new BufferedReader( new FileReader( fname ) );
		String line = reader.readLine( );
		int ldx = 0;

		while( line != null ) {

			ldx += 1;

			if( line.length( ) == 0 ) {
				line = reader.readLine( );
				continue;
			}

			if( curr == null ) {
				reader.close( );
				throw new RuntimeException( "Malformed file, tree finished before EOF: " + ldx );
			}

			String[ ] seg = line.split( ":" );

			if( seg.length != 5 ) {
				reader.close( );
				throw new RuntimeException( "Malformed line: " + ldx );
			}

			Node newChild = new Node( );
			newChild.c = seg[ 0 ].charAt( 0 );
			newChild.usedCount = Integer.parseInt( seg[ 1 ] );
			newChild.eosCount = Integer.parseInt( seg[ 2 ] );
			newChild.eos = newChild.eosCount > 0;

			boolean cHasChild = seg[ 3 ].equals( "C" );
			boolean cHasNext = seg[ 4 ].equals( "N" );

			// For stats, add eosCount to root used
			this.root.usedCount += newChild.eosCount;

			// If node has kids, push to stack, link in below
			if( hasChild ) {

				stack.push( new TripleT< Node, Boolean, Boolean >( curr, new Boolean( hasChild ),
						new Boolean( hasNext ) ) );

				// For stats, increment longest as needed
				if( stack.size( ) > this.longest )
					this.longest = stack.size( );

				// Link in, move to
				curr.child = newChild;
				curr = newChild;

				hasChild = cHasChild;
				hasNext = cHasNext;

				// If node has next, link next to, slide over to it
			} else if( hasNext ) {

				curr.next = newChild;
				curr = newChild;

				hasChild = cHasChild;
				hasNext = cHasNext;

				// If neither, pop until next, move to
			} else if( !stack.empty( ) ) {

				do {

					TripleT< Node, Boolean, Boolean > state = stack.pop( );

					curr = state.a;
					hasChild = state.b;
					hasNext = state.c;

				} while( !stack.empty( ) && hasNext == false );

				// Link as next
				if( !hasNext ) {
					reader.close( );
					throw new RuntimeException( "Malformed file, ran out of stack before EOF: " + ldx );
				}

				curr.next = newChild;
				curr = newChild;

				hasChild = cHasChild;
				hasNext = cHasNext;

			}

			line = reader.readLine( );

		}

		// Consume rest of stack w/o next for error checking
		do {

			TripleT< Node, Boolean, Boolean > state = stack.pop( );
			hasNext = state.c;

		} while( !stack.empty( ) && hasNext == false );

		// Final error check
		if( stack.size( ) != 0 ) {
			reader.close( );
			throw new RuntimeException( "Malformed file, stack not empty when done parsing file" );
		}

		reader.close( );

	}


	public ArrayList< Pair< Long > > countStats( ) {

		ArrayList< Pair< Long > > ret = new ArrayList< Pair< Long > >( );

		for( char cdx = 0; cdx < Trie.alphabet.length( ); ++cdx ) {

			ret.add( new Pair< Long >( new Long( 0 ), new Long( 0 ) ) );

		}


		NodeIterator iter = new NodeIterator( this.root );

		while( iter.hasNext( ) ) {

			PairT< Integer, Node > n = iter.next( );

			// System.out.println( "Visiting " + n.b.c + " @ " + n.a.toString( ) );

			int cdx = Trie.alphabet.indexOf( n.b.c );
			if( cdx < 0 )
				throw new RuntimeException( "Encountered unknown character while iterating: " + n.b.c );

			Pair< Long > s = ret.get( cdx );

			s.a += n.b.usedCount;
			s.b += n.b.eosCount;

		}

		return ret;

	}

	public ArrayList< ArrayList< Pair< Long > > > posStats( ) {

		ArrayList< ArrayList< Pair< Long > > > ret = new ArrayList< ArrayList< Pair< Long > > >( );

		for( char cdx = 0; cdx < Trie.alphabet.length( ); ++cdx ) {

			ArrayList< Pair< Long > > pList = new ArrayList< Pair< Long > >( );

			for( int pdx = 0; pdx < this.longest; ++pdx ) {

				Pair< Long > p = new Pair< Long >( new Long( 0 ), new Long( 0 ) );

				pList.add( p );

			}

			ret.add( pList );

		}


		NodeIterator iter = new NodeIterator( this.root );

		while( iter.hasNext( ) ) {

			PairT< Integer, Node > n = iter.next( );

			int cdx = Trie.alphabet.indexOf( n.b.c );
			if( cdx < 0 )
				throw new RuntimeException( "Encountered unknown character while iterating: " + n.b.c );

			ArrayList< Pair< Long > > ls = ret.get( cdx );
			Pair< Long > s = ls.get( n.a );
			s.a += n.b.usedCount;
			s.b += n.b.eosCount;

		}

		return ret;

	}


	private void rprint( Node trie, ArrayList< Character > progress ) {

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

	private void rquery( String q, Node trie, int cdx, ArrayList< Character > progress,
			ArrayList< PairT< Long, String > > res ) {

		// End of recursion: Past end of query
		if( cdx >= q.length( ) ) {

			// But only output if end of string marker
			if( trie.eos )
				res.add( new PairT< Long, String >( trie.eosCount, this.getStringRep( progress ) ) );

			return;

		}

		char c = q.charAt( cdx );

		// Check this first, then it falls through to single character lookup later
		if( Character.isDigit( c ) ) {

			// System.out.print( String.format( "%d: Digit %c", cdx, c ) );

			// Get the lookback character
			c = progress.get( c - 0x30 );

			// System.out.println( String.format( "%c", c ) );

		}

		// If c is space, wildcard
		if( c == ' ' ) {

			// System.out.println( String.format( "%d: Wildcard", cdx ) );

			Node child = trie.child;

			// Make sure there is a child list, otherwise no match
			if( child == null )
				return;

			do {

				// System.out.println( String.format( "%d: Try: %c", cdx, child.c ) );

				progress.add( child.c );
				this.rquery( q, child, cdx + 1, progress, res );
				progress.remove( progress.size( ) - 1 );

				child = child.next;

			} while( child != null );

			// If c is char, look for specific
		} else if( Trie.accept( c ) ) {

			// System.out.println( String.format( "%d: Target: %c", cdx, c ) );

			Node child = trie.child;

			// Make sure there is a child list, otherwise no match
			if( child == null )
				return;

			// Go till we can't, or we arrive
			while( child.next != null && child.c < c )
				child = child.next;

			// Match!
			if( child.c == c ) {

				// System.out.println( String.format( "%d: Match", cdx ) );

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

}
