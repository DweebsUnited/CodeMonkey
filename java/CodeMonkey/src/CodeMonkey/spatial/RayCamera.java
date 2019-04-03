package CodeMonkey.spatial;

import java.util.Iterator;

import processing.core.PVector;


public class RayCamera implements Iterator< Ray > {

	// Pixel density
	public int sWidth, sHeigh;
	public float ratio;
	// Ray origin
	public PVector so, sx, sy;
	// Frame definition
	public PVector fo, fx, fy;

	// Ray Iterator
	int py, px;
	PVector rsy, rty, rs, rt;

	public RayCamera( PVector o, PVector tgt, PVector screenRight, float ratio, int sWidth, int sHeigh ) {

		this.sWidth = sWidth;
		this.sHeigh = sHeigh;
		this.ratio = ratio;

		this.so = new PVector( );
		this.sx = new PVector( );
		this.sy = new PVector( );
		this.fo = new PVector( );
		this.fx = new PVector( );
		this.fy = new PVector( );

		this.move( o, tgt, screenRight );

	}

	public void move( PVector o, PVector tgt, PVector screenRight ) {

		// Make normal dir
		PVector dir = tgt.copy( );
		dir.sub( o );
		dir.normalize( );

		// Frame axis
		this.fo.set( o );

		this.fx.set( screenRight );
		this.fo.sub( this.fx );
		this.fx.mult( 2 );

		this.fx.cross( dir, this.fy );
		this.fy.setMag( this.fx.mag( ) * this.sHeigh / this.sWidth );
		this.fo.sub( this.fy );
		this.fy.mult( 2 );

		// Screen axis, must move o
		this.so.set( o );
		this.so.sub( dir );

		this.sx.set( screenRight );
		this.sx.mult( this.ratio );
		this.so.sub( this.sx );
		this.sx.mult( 2 );

		this.sx.cross( dir, this.sy );
		this.sy.setMag( this.sx.mag( ) * this.sHeigh / this.sWidth );
		this.sy.mult( this.ratio );
		this.so.sub( this.sy );
		this.sy.mult( 2 );

		this.reset( );

	}


	// Ray Iterator
	public void reset( ) {

		this.py = -1;
		this.px = this.sWidth;

	}

	@Override
	public boolean hasNext( ) {

		// Last one is x,y = sWidth-1,sHeigh-1
		return this.py < this.sHeigh - 1 && this.px < this.sWidth - 1;

	}

	@Override
	public Ray next( ) {

		this.px += 1;
		if( this.px >= this.sWidth ) {

			this.px = 0;
			this.py += 1;

			// If no more
			if( this.py >= this.sHeigh )
				throw new RuntimeException( "No more pixels! Reset and try again" );

			// Y moved, recompute
			this.rsy = this.sy.copy( );
			this.rsy.mult( ( this.py + 0.5f ) / this.sHeigh );
			this.rsy.add( this.so );

			this.rty = this.fy.copy( );
			this.rty.mult( ( this.py + 0.5f ) / this.sHeigh );
			this.rty.add( this.fo );

		}

		// X moved, compute
		this.rs = this.sx.copy( );
		this.rs.mult( ( this.px + 0.5f ) / this.sWidth );
		this.rs.add( this.rsy );

		this.rt = this.fx.copy( );
		this.rt.mult( ( this.px + 0.5f ) / this.sWidth );
		this.rt.add( this.rty );

		return Ray.fromTwoPoints( this.rs, this.rt );


	}

}
