package CodeMonkey.spatial;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.utility.Grid3D;
import CodeMonkey.utility.RandomQueue;
import processing.core.PVector;


public class PoissonSampler3D {

	private static Random rng = new Random( );

	private Grid3D< PVector > grid;

	private RandomQueue< PVector > queue;
	public ArrayList< PVector > sample;

	public float width;
	public float height;
	public float depth;

	private float mDist;
	private float cellSize;
	private int nCount;

	public PoissonSampler3D( float width, float height, float depth, float mDist, int nCount ) {

		this.mDist = mDist;
		this.cellSize = this.mDist / (float) Math.sqrt( 2 );
		this.nCount = nCount;

		this.grid = new Grid3D< PVector >( (int) Math.ceil( width / this.cellSize ),
				(int) Math.ceil( height / this.cellSize ), (int) Math.ceil( depth / this.cellSize ) );

		this.width = this.grid.width * this.cellSize;
		this.height = this.grid.height * this.cellSize;
		this.depth = this.grid.depth * this.cellSize;

		this.queue = new RandomQueue< PVector >( );
		this.sample = new ArrayList< PVector >( );

		// Make and add the first point randomly

		PVector point = new PVector( PoissonSampler3D.rng.nextFloat( ) * this.width,
				PoissonSampler3D.rng.nextFloat( ) * this.height, PoissonSampler3D.rng.nextFloat( ) * this.depth );

		this.queue.add( point );
		this.sample.add( point );

		this.grid.set( this.toGridX( point ), this.toGridY( point ), this.toGridZ( point ), point );

		while( this.step( ) )
			;

	}

	public PoissonSampler3D( float width, float height, float depth, float mDist ) {

		this( width, height, depth, mDist, 30 );

	}

	public boolean step( ) {

		if( this.queue.size( ) > 0 ) {

			// Get a point from the queue

			PVector p = this.queue.peek( );
			boolean toRemove = true;

			// As many tries as we have

			for( int pdx = 0; pdx < this.nCount; ++pdx ) {

				// Generate new point around

				float r1 = PoissonSampler3D.rng.nextFloat( );
				float r2 = PoissonSampler3D.rng.nextFloat( );
				float r3 = PoissonSampler3D.rng.nextFloat( );

				float rad = this.mDist * ( r1 + 1 );
				float ang = 2 * (float) Math.PI * r2;
				float elev = (float) Math.PI * r3;


				float nX = p.x + rad * (float) Math.sin( elev ) * (float) Math.cos( ang );
				float nY = p.y + rad * (float) Math.sin( elev ) * (float) Math.sin( ang );
				float nZ = p.z + rad * (float) Math.cos( elev );

				PVector point = new PVector( nX, nY, nZ );

				if( this.inGrid( point ) && !this.inNeighbourhood( point, this.mDist ) ) {

					this.queue.add( point );
					this.sample.add( point );

					this.grid.set( this.toGridX( point ), this.toGridY( point ), this.toGridZ( point ), point );

					toRemove = false;

				}

			}

			if( toRemove )
				this.queue.remove( p );

			return true;

		}

		return false;

	}

	private int toGridX( PVector p ) {

		return (int) ( p.x / this.cellSize );
	}

	private int toGridY( PVector p ) {

		return (int) ( p.y / this.cellSize );
	}

	private int toGridZ( PVector p ) {

		return (int) ( p.z / this.cellSize );
	}

	private boolean inGrid( PVector p ) {

		int gX = this.toGridX( p );
		int gY = this.toGridY( p );
		int gZ = this.toGridZ( p );

		return this.inGrid( gX, gY, gZ );

	}

	private boolean inGrid( int x, int y, int z ) {

		return x > 0 && x < this.grid.width && y > 0 && y < this.grid.height && z > 0 && z < this.grid.depth;

	}

	private boolean inNeighbourhood( PVector p, float mDist ) {
		// True if there is a point too close

		int gX = this.toGridX( p );
		int gY = this.toGridY( p );
		int gZ = this.toGridZ( p );

		for( int dx = -2; dx < 3; ++dx ) {

			for( int dy = -2; dy < 3; ++dy ) {

				for( int dz = -2; dz < 3; ++dz ) {

					if( !this.inGrid( gX + dx, gY + dy, gZ + dz ) )
						continue;

					PVector n = this.grid.get( gX + dx, gY + dy, gZ + dz );

					if( n != null && p.dist( n ) < mDist )
						return true;

				}

			}

		}

		return false;

	}

}
