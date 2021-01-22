package CodeMonkey.spatial;

import processing.core.PVector;


public class AABB2D implements Intersectable {

	private PVector min;
	private PVector max;
	public PVector size;

	public AABB2D( PVector min, PVector max ) {

		this.min = min.copy( );
		this.max = max.copy( );

		this.size = this.max.copy( );
		this.size.sub( this.min );

	}

	public AABB2D( PVector o, float halfSize ) {

		this( new PVector( o.x - halfSize, o.y - halfSize ), new PVector( o.x + halfSize, o.y + halfSize ) );

	}

	@Override
	// Always returns next face in direction of ray
	// IE, if in box it returns the back face collision
	public float intersect( Ray r ) {

		PVector invDir = new PVector( 1.0f / r.d.x, 1.0f / r.d.y );

		float x1 = ( this.min.x - r.o.x ) * invDir.x;
		float x2 = ( this.max.x - r.o.x ) * invDir.x;

		float y1 = ( this.min.y - r.o.y ) * invDir.y;
		float y2 = ( this.max.y - r.o.y ) * invDir.y;

		float tmin = Math.max( Math.min( x1, x2 ), Math.min( y1, y2 ) );
		float tmax = Math.min( Math.max( x1, x2 ), Math.max( y1, y2 ) );

		if( tmax <= 0 ) {

			// Intersects, but whole box is behind us

			return Float.POSITIVE_INFINITY;

		} else if( tmin > tmax ) {

			// No intersection

			return Float.POSITIVE_INFINITY;

		} else if( tmin <= 0 ) {

			// Inside box

			return tmax;

		} else {

			// Clean intersection

			return tmin;

		}

	}

}
