package CodeMonkey.spatial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import CodeMonkey.utility.Pair;
import CodeMonkey.utility.PairT;
import CodeMonkey.utility.TripleT;
import processing.core.PVector;


public class Octree implements Intersectable {

	// xyz, Xyz, xYz, XYz, xyZ, XyZ, xYZ, XYZ
	public ArrayList< Octree > children;

	public boolean filled = false;

	// Filled -> ! Children
	// Children -> ! Filled

	private PVector min, max;

	public static int RECURSE_DEPTH = 0;

//	private void printbb( ) {
//
//		System.out.print( String.format( "%f,%f,%f->%f,%f,%f: ", this.min.x, this.min.y, this.min.z, this.max.x,
//				this.max.y, this.max.z ) );
//
//	}

	public Octree( PVector min, PVector max ) {

		this.min = min.copy( );
		this.max = max.copy( );

		this.filled = false;

	}

	public Octree( ) {

		this( new PVector( 0, 0, 0 ), new PVector( 1, 1, 1 ) );

	}


	// Create all child objects, ensure consistent
	public void split( ) {

		this.filled = false;
		this.children = new ArrayList< Octree >( );

		PVector h = this.min.copy( );
		h.add( this.max );
		h.div( 2 );

		for( int kdx = 0; kdx < 8; ++kdx ) {

			PVector cMin = new PVector( ( ( ( kdx & 0x01 ) > 0 ) ? h : this.min ).x,
					( ( ( kdx & 0x02 ) > 0 ) ? h : this.min ).y, ( ( ( kdx & 0x04 ) > 0 ) ? h : this.min ).z );
			PVector cMax = new PVector( ( ( ( kdx & 0x01 ) > 0 ) ? this.max : h ).x,
					( ( ( kdx & 0x02 ) > 0 ) ? this.max : h ).y, ( ( ( kdx & 0x04 ) > 0 ) ? this.max : h ).z );

			// xyz, Xyz, xYz, XYz, xyZ, XyZ, xYZ, XYZ
			this.children.add( new Octree( cMin, cMax ) );

		}

	}

	public float shallowIntersect( Ray r, PVector min, PVector max ) {

		return AABB3D.intersect( r, min, max );

	}

	public PairT< Float, Pair< PVector > > intersect( Ray r, float t, PVector min, PVector max, Octree root, int nR ) {

		// Make the half
		PVector h = this.min.copy( );
		h.add( this.max );
		h.div( 2 );

		// If we are filled, stop! Hey presto!
		if( this.filled ) {

			// Recursion check!
			if( nR >= Octree.RECURSE_DEPTH ) {
				// Don't go deeper, consider it a hit
				return new PairT< Float, Pair< PVector > >( t, new Pair< PVector >( min, max ) );
			} else {
				// Recurse!
				return root.intersect( r, t, min, max, root, nR + 1 );
			}

		} else if( this.children == null ) { // If we have no kids and not filled, its a miss

			return new PairT< Float, Pair< PVector > >( Float.POSITIVE_INFINITY, new Pair< PVector >( min, max ) );

		} else if( t >= Float.POSITIVE_INFINITY ) { // If a miss, don't bother with kids ( This will happen when ray
													 // misses )

			return new PairT< Float, Pair< PVector > >( Float.POSITIVE_INFINITY, new Pair< PVector >( min, max ) );

		}


		// Heap sort valid
		PriorityQueue< TripleT< Float, Octree, Pair< PVector > > > heap = new PriorityQueue< TripleT< Float, Octree, Pair< PVector > > >(
				new Comparator< TripleT< Float, Octree, Pair< PVector > > >( ) {

					@Override
					public int compare( TripleT< Float, Octree, Pair< PVector > > o1,
							TripleT< Float, Octree, Pair< PVector > > o2 ) {

						return o1.a < o2.a ? -1 : 1;

					}

				} );

		for( int odx = 0; odx < this.children.size( ); ++odx ) {

			Octree o = this.children.get( odx );

			PVector cMin = new PVector( ( ( ( odx & 0x01 ) > 0 ) ? h : min ).x, ( ( ( odx & 0x02 ) > 0 ) ? h : min ).y,
					( ( ( odx & 0x04 ) > 0 ) ? h : min ).z );
			PVector cMax = new PVector( ( ( ( odx & 0x01 ) > 0 ) ? max : h ).x, ( ( ( odx & 0x02 ) > 0 ) ? max : h ).y,
					( ( ( odx & 0x04 ) > 0 ) ? max : h ).z );

			t = o.shallowIntersect( r, cMin, cMax );

			// Primitive check -> No point if miss
			if( t < Float.POSITIVE_INFINITY ) {

				heap.add( new TripleT< Float, Octree, Pair< PVector > >( t, o, new Pair< PVector >( cMin, cMax ) ) );

			}

		}

		// Go through valid, recurse until get a good one
		PairT< Float, Pair< PVector > > bK = new PairT< Float, Pair< PVector > >( Float.POSITIVE_INFINITY,
				new Pair< PVector >( min, max ) );

		for( TripleT< Float, Octree, Pair< PVector > > vol : heap ) {

			// Run the full intersection on the child
			PairT< Float, Pair< PVector > > c = vol.b.intersect( r, vol.a, vol.c.a, vol.c.b, root, nR );

			// If closer than best, save
			if( c.a < Float.POSITIVE_INFINITY )
				if( c.a < bK.a )
					bK = c;

		}

		// Maybe nothing, maybe a kid
		return bK;

	}

	@Override
	public float intersect( Ray r ) {

		// Now for the fun part... Recursion :o
		PairT< Float, Pair< PVector > > col = this.intersect( r, this.shallowIntersect( r, this.min, this.max ),
				this.min, this.max, this, 0 );

		return col.a;

	}

}
