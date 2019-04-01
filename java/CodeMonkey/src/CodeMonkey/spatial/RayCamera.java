package CodeMonkey.spatial;

import java.util.ArrayList;

import processing.core.PVector;


public class RayCamera {

	// Pixel density
	public int sWidth, sHeigh;
	// Ray origin
	public PVector so, sx, sy;
	// Frame definition
	public PVector fo, fx, fy;

	public RayCamera( PVector o, PVector tgt, PVector screenRight, float ratio, int sWidth, int sHeigh ) {

		this.sWidth = sWidth;
		this.sHeigh = sHeigh;

		// Make normal dir
		PVector dir = tgt.copy( );
		dir.sub( o );
		dir.normalize( );

		// Frame axis
		this.fo = o.copy( );

		this.fx = screenRight.copy( );
		this.fo.sub( this.fx );
		this.fx.mult( 2 );

		this.fy = this.fx.cross( dir );
		this.fy.setMag( this.fx.mag( ) * sHeigh / sWidth );
		this.fo.sub( this.fy );
		this.fy.mult( 2 );

		// Screen axis, must move o
		this.so = o.copy( );
		this.so.sub( dir );

		this.sx = screenRight.copy( );
		this.sx.mult( ratio );
		this.so.sub( this.sx );
		this.sx.mult( 2 );

		this.sy = this.sx.cross( dir );
		this.sy.setMag( this.sx.mag( ) * sHeigh / sWidth );
		this.sy.mult( ratio );
		this.so.sub( this.sy );
		this.sy.mult( 2 );

	}

	public ArrayList< Ray > getPixelRays( ) {

		ArrayList< Ray > rays = new ArrayList< Ray >( );

		for( int py = 0; py < this.sHeigh; ++py ) {

			PVector rsy = this.sy.copy( );
			rsy.mult( ( py + 0.5f ) / this.sHeigh );
			rsy.add( this.so );

			PVector rty = this.fy.copy( );
			rty.mult( ( py + 0.5f ) / this.sHeigh );
			rty.add( this.fo );

			for( int px = 0; px < this.sWidth; ++px ) {

				PVector rs = this.sx.copy( );
				rs.mult( ( px + 0.5f ) / this.sWidth );
				rs.add( rsy );

				PVector rt = this.fx.copy( );
				rt.mult( ( px + 0.5f ) / this.sWidth );
				rt.add( rty );

				rays.add( Ray.fromTwoPoints( rs, rt ) );

			}

		}

		return rays;

	}

}
