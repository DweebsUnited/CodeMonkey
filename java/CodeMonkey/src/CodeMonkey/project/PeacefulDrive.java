package CodeMonkey.project;

import processing.core.PApplet;
import processing.core.PConstants;


public class PeacefulDrive extends PApplet {

	final float minAng = PApplet.radians( 89.9f );
	final float maxAng = PApplet.radians( 0f );

	final float maxRefrac = 5.0f;
	final float minRefrac = 1f / this.maxRefrac;

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.PeacefulDrive" );

	}

	@Override
	public void settings( ) {

		this.size( 720, 640 );

	}

	@Override
	public void setup( ) {

		this.background( 255 );

		this.noStroke( );
		this.noFill( );

		this.colorMode( PConstants.HSB, 360, 100, 100 );

		this.loadPixels( );

		for( int adx = 0; adx < this.pixelHeight; ++adx ) {

			float a = ( this.maxAng - this.minAng ) * adx / ( this.pixelHeight - 1 ) + this.minAng;

			for( int rdx = 0; rdx < this.pixelWidth; ++rdx ) {

				float r = ( this.maxRefrac - this.minRefrac ) * rdx / ( this.pixelWidth - 1 ) + this.minRefrac;

				// sin( a ) = sin( b ) * nb
				// asin( sin( a ) / nb ) = b
				float ra = (float) Math.asin( Math.sin( a ) / r );

				this.pixels[ rdx + adx * this.pixelWidth ] = this.color( PApplet.degrees( ra * 4 ), 100, 100 );

			}
		}

		this.updatePixels( );

		this.noLoop( );

	}

	@Override
	public void draw( ) {


	}

}
