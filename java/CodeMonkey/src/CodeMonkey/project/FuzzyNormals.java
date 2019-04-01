package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.spatial.Octree;
import CodeMonkey.spatial.Ray;
import CodeMonkey.spatial.RayCamera;
import processing.core.PApplet;
import processing.core.PVector;


public class FuzzyNormals extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.FuzzyNormals" );

	}

	Random rng = new Random( );

	final int sWidth = 1080;
	final int sHeigh = Math.round( this.sWidth * 9f / 16f );

	private RayCamera cam = new RayCamera( new PVector( -5, -5, 5 ), new PVector( 0.5f, 0.5f, 0.5f ),
			new PVector( 0.707f, -0.707f, 0 ), 1f, this.sWidth, this.sHeigh );

	private ArrayList< Ray > rays;

	private Octree oct = new Octree( );

	float buf[];

	@Override
	public void settings( ) {

		this.size( this.sWidth, this.sHeigh );

		this.setName( );

	}

	public void setupOct( Octree oct, int l ) {

//		oct.filled = false;
//		oct.split( );
//		oct.children.get( 0 ).filled = true;
//		oct.children.get( 1 ).filled = true;
//		oct.children.get( 2 ).filled = true;
//		oct.children.get( 3 ).filled = true;

		// Max recurse depth
		if( l >= 4 ) {
			if( this.rng.nextFloat( ) > 0.5 )
				oct.filled = true;
			return;
		}

		if( l < 2 || this.rng.nextFloat( ) > 0.5 ) {

			oct.split( );

			for( Octree c : oct.children )
				this.setupOct( c, l + 1 );

		} else if( this.rng.nextFloat( ) > 0.5 )
			oct.filled = true;

	}

	@Override
	public void setup( ) {

		this.buf = new float[ this.sWidth * this.sHeigh ];

		// Gotta split some kiddos
		this.setupOct( this.oct, 0 );

		this.rays = this.cam.getPixelRays( );

		this.loadPixels( );

		float m = Float.POSITIVE_INFINITY;
		float M = 0;
		for( int py = 0; py < this.sHeigh; ++py ) {
			for( int px = 0; px < this.sWidth; ++px ) {

				float d = this.oct.intersect( this.rays.get( py * this.sWidth + px ) );

				this.buf[ py * this.sWidth + px ] = d;

				if( d < Float.POSITIVE_INFINITY ) {

					if( d < m )
						m = d;
					if( d > M )
						M = d;

				}

			}
		}

		M -= m;
		for( int py = 0; py < this.sHeigh; ++py ) {
			for( int px = 0; px < this.sWidth; ++px ) {

				int c = Math.round( ( ( 1 - ( this.buf[ py * this.sWidth + px ] - m ) / M ) * 0.8f + 0.1f ) * 255 );

				this.pixels[ ( this.sHeigh - py - 1 ) * this.sWidth + px ] = this.color( c );

			}
		}

		this.updatePixels( );

		this.save( );

		this.noLoop( );

	}

	@Override
	public void draw( ) {


	}

}
