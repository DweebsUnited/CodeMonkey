package CodeMonkey.spatial;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.utility.Grid2D;
import CodeMonkey.utility.RandomQueue;
import processing.core.PVector;


public class PoissonSampler {

	private static Random rng = new Random( 3 );

	private Grid2D< PVector > grid;

	private RandomQueue< PVector > queue;
	public ArrayList< PVector > sample;

	public float width;
	public float height;

	public float owidth;
	public float oheight;

	private float mDist;
	private float cellSize;
	private int nCount;

	public PoissonSampler( float width, float height, float mDist, int nCount ) {

		this.mDist = mDist;
		this.cellSize = this.mDist / (float) Math.sqrt( 2 );
		this.nCount = nCount;

		this.grid = new Grid2D< PVector >( (int) Math.ceil( width / this.cellSize ),
				(int) Math.ceil( height / this.cellSize ) );

		this.width = this.grid.width * this.cellSize;
		this.height = this.grid.height * this.cellSize;

		this.owidth = width;
		this.oheight = height;

		this.queue = new RandomQueue< PVector >( );
		this.sample = new ArrayList< PVector >( );

		// Make and add the first point randomly

		PVector point = new PVector( PoissonSampler.rng.nextFloat( ) * this.width,
				PoissonSampler.rng.nextFloat( ) * this.height );

		this.queue.add( point );
		this.sample.add( point );

		this.grid.set( this.toGridX( point ), this.toGridY( point ), point );

		while( this.step( ) )
			;

	}

	public PoissonSampler( float width, float height, float mDist ) {

		this( width, height, mDist, 30 );

	}

	public boolean step( ) {

		if( this.queue.size( ) > 0 ) {

			// Get a point from the queue

			PVector p = this.queue.peek( );
			// boolean toRemove = true;

			// As many tries as we have

			for( int pdx = 0; pdx < this.nCount; ++pdx ) {

				// Generate new point around

				float r1 = PoissonSampler.rng.nextFloat( );
				float r2 = PoissonSampler.rng.nextFloat( );

				float rad = this.mDist * ( r1 + 1 );
				float ang = 2 * (float) Math.PI * r2;


				float nX = p.x + rad * (float) Math.cos( ang );
				float nY = p.y + rad * (float) Math.sin( ang );

				PVector point = new PVector( nX, nY );

				if( this.inGrid( point ) && !this.inNeighbourhood( point, this.mDist ) ) {

					this.queue.add( point );
					this.sample.add( point );

					this.grid.set( this.toGridX( point ), this.toGridY( point ), point );

					// toRemove = false;

				}

			}

			// if( toRemove )
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

	private boolean inGrid( PVector p ) {

		if( p.x >= this.owidth || p.y >= this.oheight )
			return false;

		int gX = this.toGridX( p );
		int gY = this.toGridY( p );

		return gX >= 0 && gX < this.grid.width && gY >= 0 && gY < this.grid.height;

	}

	private boolean inGrid( int x, int y ) {

		if( x >= this.owidth || y >= this.oheight )
			return false;

		return x >= 0 && x < this.grid.width && y >= 0 && y < this.grid.height;

	}

	private boolean inNeighbourhood( PVector p, float mDist ) {

		for( PVector n : this.sample ) {

			if( n.dist( p ) < mDist )
				System.out.println( "Should say no" );

		}

		int gX = this.toGridX( p );
		int gY = this.toGridY( p );

		for( int dx = -2; dx < 3; ++dx ) {

			for( int dy = -2; dy < 3; ++dy ) {

				if( !this.inGrid( gX + dx, gY + dy ) )
					continue;

				PVector n = this.grid.get( gX + dx, gY + dy );

				if( n != null && p.dist( n ) < mDist )
					return true;

			}

		}

		for( PVector s : this.sample ) {

			if( s.dist( p ) < mDist )
				System.out.println( "Broken!" );

			for( int dx = -2; dx < 3; ++dx ) {

				for( int dy = -2; dy < 3; ++dy ) {

					if( !this.inGrid( gX + dx, gY + dy ) )
						continue;

					PVector n = this.grid.get( gX + dx, gY + dy );

					if( n != null && p.dist( n ) < mDist )
						return true;

				}

			}

		}

		return false;

	}

}
