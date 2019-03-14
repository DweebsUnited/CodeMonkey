package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.draw.LineDrawer;
import CodeMonkey.draw.SandPainter;
import CodeMonkey.spatial.Ray;
import CodeMonkey.spatial.RefractingPlane;
import CodeMonkey.spatial.Sphere;
import CodeMonkey.utility.PVectorFuncs;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;


public class ShatteredGlass extends ProjectBase {

	private PGraphics canvas;
	private final int cWidth = 1920;
	private final int cHeight = 1080;
	private LineDrawer painter;

	private Random rng = new Random( );

	private final float maxRefrac = 5.0f;
	private final float minRefrac = 1f / this.maxRefrac;

	private Sphere boundSphere;
	private ArrayList< RefractingPlane > planes;

	private boolean adding = true;

	private final int N_PLANES = 8;
	private final int N_RAYS = 64;
	private float rad;

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.ShatteredGlass" );

	}

	@Override
	public void settings( ) {

		this.size( 720, 640 );

		this.setName( );

	}

	@Override
	public void setup( ) {

		this.canvas = this.createGraphics( 1920, 1080 );
		this.canvas.beginDraw( );
		this.canvas.background( 0 );
		this.canvas.endDraw( );
		this.painter = new SandPainter( this.rng, 0.5f, 3, this.color( 255, 2 ), ( double arg ) -> {
			return Math.pow( 2 * arg - 1, 2 );
		} );

		this.background( 0 );

		this.rad = Math.min( this.cWidth, this.cHeight ) / 2;
		this.boundSphere = new Sphere( new PVector( this.cWidth / 2, this.cHeight / 2 ), this.rad );

		// Make a bunch of random refracting planes
		// Scatter them with random 2d normals

		this.planes = new ArrayList<>( );

		for( int pdx = 0; pdx < this.N_PLANES; ++pdx ) {

			PVector n = PVector.random2D( );
			PVector o = new PVector( this.rng.nextInt( this.cWidth / 2 ) + this.cWidth / 4,
					this.rng.nextInt( this.cHeight / 2 ) + this.cHeight / 4 );

			RefractingPlane p = new RefractingPlane( n, o,
					this.rng.nextFloat( ) * ( this.maxRefrac - this.minRefrac ) + this.minRefrac );

			PVector r = p.getRight( );
			PVector u = p.getUp( );

			// this.stroke( 255 );
			// this.line( o.x, o.y, o.x + n.x * 15, o.y + n.y * 15 );
			// this.stroke( 255, 0, 0 );
			// this.line( o.x - r.x * 15, o.y - r.y * 15, o.x + r.x * 15, o.y + r.y * 15 );
			// this.line( o.x - u.x * 15, o.y - u.y * 15, o.x + u.x * 15, o.y + u.y * 15 );

			this.planes.add( p );

		}

	}

	@Override
	public void draw( ) {

		if( this.adding )
			this.addRays( );

		this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );

	}

	private void addRays( ) {

		this.canvas.beginDraw( );

		// Pick random point on circle, cast random interior direction, kill on nearest
		// contact with circle or plane
		for( int rdx = 0; rdx < this.N_RAYS; ++rdx ) {

			// Origin
			PVector lo = new PVector( this.rad, 0 );
			lo.rotate( (float) ( this.rng.nextFloat( ) * 2 * Math.PI ) );
			lo.add( new PVector( this.cWidth / 2, this.cHeight / 2 ) );
			// Direction is center +- PI / 4
			PVector ld = new PVector( this.cWidth / 2, this.cHeight / 2 );
			ld.sub( lo );
			ld.rotate( (float) ( this.rng.nextFloat( ) * Math.PI - Math.PI / 2 ) / 2f );
			ld.normalize( );

			// Now add a tiny amount of d to o otherwise we intersect the sphere immediately
			lo.add( PVectorFuncs.multRet( ld, 0.0001f ) );

			// Make the ray object
			Ray r = new Ray( lo, ld );

			// Intersect with sphere
			float tmin;
			int tminP;
			// Infi-loop catcher
			int cnt = 0;

			do {

				tmin = Float.POSITIVE_INFINITY;
				tminP = -1;

				float tsph = this.boundSphere.intersect( r );
				if( tsph > 0 ) {
					tmin = tsph;
				}

				// Intersect with each plane
				for( int rpdx = 0; rpdx < this.planes.size( ); ++rpdx ) {

					RefractingPlane rp = this.planes.get( rpdx );
					float it = rp.intersect( r );

					if( it > 0 && it < tmin ) {

						tmin = it;
						tminP = rpdx;

					}

				}

				// Draw ray to intersection point
				PVector ipt = r.atT( tmin );

				// this.canvas.stroke( 255, 5 );
				// this.canvas.line( r.o.x, r.o.y, ipt.x, ipt.y );
				this.painter.line( this.canvas, r.o, ipt );

				if( tminP > -1 ) {

					// Refract
					r = this.planes.get( tminP ).refract( r );
					// Offset so we don't immediately intersect with the plane
					r.o.add( PVectorFuncs.multRet( r.d, 0.0001f ) );

				}

				++cnt;

			} while( tminP != -1 && cnt < this.N_PLANES * 2 );

		}

		this.canvas.endDraw( );

	}

	@Override
	public void keyPressed( ) {

		if( this.key == ' ' )
			this.adding = !this.adding;
		else if( this.key == 'w' )
			this.save( this.canvas );

	}

}
