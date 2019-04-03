package CodeMonkey.project;

import java.util.Random;

import CodeMonkey.spatial.Octree;
import CodeMonkey.spatial.RayCamera;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;


public class FuzzyNormals extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.FuzzyNormals" );

	}

	Random rng = new Random( );

	private final int sWidth = 1080;
	private final int sHeigh = Math.round( this.sWidth * 9f / 16f );

	private RayCamera cam;

	private Octree oct = new Octree( );

	private float buf[];

	private float cAng = 0;
	private PVector cPos, cTgt, cRgt;

	@Override
	public void settings( ) {

		this.size( this.sWidth, this.sHeigh );

		this.setName( );

	}

	public void setupOct( Octree oct, int l ) {

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

		this.cPos = new PVector( 10, 0, 0 );
		this.cPos.rotate( this.cAng );
		this.cTgt = new PVector( 0.5f, 0.5f, 0.5f );
		this.cRgt = this.cPos.copy( );
		this.cRgt.setMag( 1 );
		this.cRgt.rotate( PConstants.PI / 2 );
		this.cPos.z = 5;

		this.cam = new RayCamera( this.cPos, this.cTgt, this.cRgt, 1f, this.sWidth, this.sHeigh );

	}

	@Override
	public void draw( ) {

		this.background( 0 );

		this.cam.reset( );

		this.loadPixels( );

		float m = Float.POSITIVE_INFINITY;
		float M = 0;
		for( int py = 0; py < this.sHeigh; ++py ) {
			for( int px = 0; px < this.sWidth; ++px ) {

				float d = this.oct.intersect( this.cam.next( ) );

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

	}

	@Override
	public void keyPressed( ) {

		boolean doMove = false;

		if( this.key == 'a' ) {

			this.cAng += PConstants.PI / 8;

			doMove = true;

		} else if( this.key == 's' ) {

			this.cAng -= PConstants.PI / 8;

			doMove = true;
		}

		if( doMove ) {

			this.cPos = new PVector( 10, 0, 0 );
			this.cPos.rotate( this.cAng );
			this.cTgt = new PVector( 0.5f, 0.5f, 0.5f );
			this.cRgt = this.cPos.copy( );
			this.cRgt.setMag( 1 );
			this.cRgt.rotate( PConstants.PI / 2 );
			this.cPos.z = 5;

			this.cam.move( this.cPos, this.cTgt, this.cRgt );

		}

	}

	@Override
	public void mouseClicked( ) {

		// Send single ray in, highlighting all boxes checked

	}

}
