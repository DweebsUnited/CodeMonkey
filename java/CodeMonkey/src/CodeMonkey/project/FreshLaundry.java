package CodeMonkey.project;

import java.util.Random;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;


public class FreshLaundry extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.FreshLaundry" );

	}

	Random rng = new Random( );

	final int cWidth = 720;
	final int cHeigh = 640;

	final int G_POINT = 16;
	final int G_SQUARE = this.G_POINT - 1;

	final float G_SCALE = 5;
	final float G_HEIGH = 2.0f;

	final float C_HEIGH = 5 * this.G_HEIGH;
	final PVector C_RAD = new PVector( 2 * this.G_SCALE, 0 );

	PVector c = new PVector( );
	float cAng = PConstants.PI / 8;
	float cAngStep = PConstants.PI / 64;
	float cAngUp = 0;

	float[ ] gData, wData, t;

	@Override
	public void settings( ) {

		this.size( this.cWidth, this.cHeigh, PConstants.P3D );

		this.setName( );

	}

	@Override
	public void setup( ) {

		this.gData = new float[ this.G_POINT * this.G_POINT ];

		for( int rdx = 0; rdx < this.G_POINT; ++rdx ) {
			for( int cdx = 0; cdx < this.G_POINT; ++cdx ) {

				this.gData[ rdx * this.G_POINT + cdx ] = this.noise( rdx * 0.25f, cdx * 0.25f );

			}
		}

	}

	@Override
	public void draw( ) {

		// Physics

		// Clear
		this.background( 127 );

		// Camera
		this.cAng = this.cAng + this.cAngUp;
		this.c.set( this.C_RAD );
		this.c.rotate( this.cAng );
		this.c.set( this.c.x, this.c.y, this.C_HEIGH );

		this.camera( this.c.x, this.c.y, this.c.z, 0, 0, 0, 0, 0, -1 );
		this.perspective( PConstants.PI / 3.0f, this.cWidth / (float) this.cHeigh, 0.001f, 1000f );

		// Grid
		this.fill( 255 );
		this.stroke( 0 );
//		this.box( this.G_SCALE );

		this.beginShape( PConstants.QUADS );
		for( int rdx = 0; rdx < this.G_SQUARE; ++rdx ) {
			for( int cdx = 0; cdx < this.G_SQUARE; ++cdx ) {

				this.vertex(
						PApplet.map( cdx, 0, this.G_SQUARE - 1, -this.G_SCALE, this.G_SCALE ),
						PApplet.map( rdx, 0, this.G_SQUARE - 1, -this.G_SCALE, this.G_SCALE ),
						this.gData[ rdx * this.G_POINT + cdx ] * this.G_HEIGH );
				this.vertex(
						PApplet.map( cdx + 1, 0, this.G_SQUARE - 1, -this.G_SCALE, this.G_SCALE ),
						PApplet.map( rdx, 0, this.G_SQUARE - 1, -this.G_SCALE, this.G_SCALE ),
						this.gData[ rdx * this.G_POINT + ( cdx + 1 ) ] * this.G_HEIGH );
				this.vertex(
						PApplet.map( cdx + 1, 0, this.G_SQUARE - 1, -this.G_SCALE, this.G_SCALE ),
						PApplet.map( rdx + 1, 0, this.G_SQUARE - 1, -this.G_SCALE, this.G_SCALE ),
						this.gData[ ( rdx + 1 ) * this.G_POINT + ( cdx + 1 ) ] * this.G_HEIGH );
				this.vertex(
						PApplet.map( cdx, 0, this.G_SQUARE - 1, -this.G_SCALE, this.G_SCALE ),
						PApplet.map( rdx + 1, 0, this.G_SQUARE - 1, -this.G_SCALE, this.G_SCALE ),
						this.gData[ ( rdx + 1 ) * this.G_POINT + cdx ] * this.G_HEIGH );

			}
		}
		this.endShape( );

	}

	@Override
	public void keyPressed( ) {

		if( this.key == 'f' )
			this.cAngUp = this.cAngStep;
		else if( this.key == 'p' )
			this.cAngUp = -this.cAngStep;

	}

	@Override
	public void keyReleased( ) {

		if( this.key == 'f' || this.key == 'p' )
			this.cAngUp = 0;

	}

}
