package CodeMonkey.spatial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import CodeMonkey.utility.PairT;
import processing.core.PVector;


public class Octree implements Intersectable {

	// xyz, Xyz, xYz, XYz, xyZ, XyZ, xYZ, XYZ
	public ArrayList< Octree > children;

	public boolean filled = false;

	// Filled -> ! Children
	// Children -> ! Filled

	private PVector min, max;

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

	public float shallowIntersect( Ray r ) {

		return ( this.children != null || this.filled ) ? AABB3D.intersect( r, this.min, this.max )
				: Float.POSITIVE_INFINITY;

	}

	private static class OComp implements Comparator< PairT< Float, Octree > > {

		@Override
		public int compare( PairT< Float, Octree > o1, PairT< Float, Octree > o2 ) {

			return o1.a < o2.a ? -1 : 1;

		}

	}

	public PairT< Float, Integer > intersect( Ray r, float t, int l ) {

		// If we are filled, stop recursion: Hey presto!
		if( this.filled )
			return new PairT< Float, Integer >( t, l );
		// If we have no kids, clearly a miss
		else if( this.children == null )
			return new PairT< Float, Integer >( Float.POSITIVE_INFINITY, l );
		// If a miss, don't bother with kids ( This will happen when ray misses )
		else if( t >= Float.POSITIVE_INFINITY )
			return new PairT< Float, Integer >( Float.POSITIVE_INFINITY, l );

		// Heap sort valid
		PriorityQueue< PairT< Float, Octree > > heap = new PriorityQueue< PairT< Float, Octree > >( new OComp( ) );

		for( Octree o : this.children ) {

			t = o.shallowIntersect( r );

			// Primitive check -> No point if miss
			if( t < Float.POSITIVE_INFINITY )
				heap.add( new PairT< Float, Octree >( t, o ) );

		}

		// Go through valid, recurse until get a good one
		for( PairT< Float, Octree > vol : heap ) {

			// Run the full intersection on the child
			PairT< Float, Integer > c = vol.b.intersect( r, vol.a, l + 1 );

			// If closer than best, save
			if( vol.a < Float.POSITIVE_INFINITY )
				return c;

		}

		// If still nothing, oh well, no collision
		return new PairT< Float, Integer >( Float.POSITIVE_INFINITY, l );

	}

	@Override
	public float intersect( Ray r ) {

		// Now for the fun part... Recursion :o
		PairT< Float, Integer > col = this.intersect( r, this.shallowIntersect( r ), 0 );

		return col.a;

	}

}
