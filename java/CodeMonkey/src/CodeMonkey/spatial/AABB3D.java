package CodeMonkey.spatial;

import processing.core.PVector;


public class AABB3D implements Intersectable {

	private PVector min;
	private PVector max;

	public AABB3D( PVector min, PVector max ) {

		this.min = min.copy( );
		this.max = max.copy( );

	}

	public AABB3D( PVector o, float size ) {

		size /= 2;

		this.min = new PVector( o.x - size, o.y - size, o.z - size );
		this.max = new PVector( o.x + size, o.y + size, o.z + size );

	}

	@Override
	public float intersect( Ray r ) {

		PVector invDir = new PVector( 1.0f / r.d.x, 1.0f / r.d.y, 1.0f / r.d.z );

		float x1 = ( this.min.x - r.o.x ) * invDir.x;
		float x2 = ( this.max.x - r.o.x ) * invDir.x;

		float y1 = ( this.min.y - r.o.y ) * invDir.y;
		float y2 = ( this.max.y - r.o.y ) * invDir.y;

		float z1 = ( this.min.z - r.o.z ) * invDir.z;
		float z2 = ( this.max.z - r.o.z ) * invDir.z;

		float tmin = Math.max( Math.max( Math.min( x1, x2 ), Math.min( y1, y2 ) ), Math.min( z1, z2 ) );
		float tmax = Math.min( Math.min( Math.max( x1, x2 ), Math.max( y1, y2 ) ), Math.max( z1, z2 ) );

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

	public static float intersect( Ray r, PVector min, PVector max ) {

		PVector invDir = new PVector( 1.0f / r.d.x, 1.0f / r.d.y, 1.0f / r.d.z );

		float x1 = ( min.x - r.o.x ) * invDir.x;
		float x2 = ( max.x - r.o.x ) * invDir.x;

		float y1 = ( min.y - r.o.y ) * invDir.y;
		float y2 = ( max.y - r.o.y ) * invDir.y;

		float z1 = ( min.z - r.o.z ) * invDir.z;
		float z2 = ( max.z - r.o.z ) * invDir.z;

		float tmin = Math.max( Math.max( Math.min( x1, x2 ), Math.min( y1, y2 ) ), Math.min( z1, z2 ) );
		float tmax = Math.min( Math.min( Math.max( x1, x2 ), Math.max( y1, y2 ) ), Math.max( z1, z2 ) );

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
