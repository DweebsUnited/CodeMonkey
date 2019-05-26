package CodeMonkey.project;

import CodeMonkey.signal.CDMA;
import CodeMonkey.signal.Walsh;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;


public class TwistedThreads extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.TwistedThreads" );

	}

	int sWidth = 720;
	int sHeigh = 640;

	int codebits = 8;

	CDMA spinner;
	float[ ][ ] codes;

	PImage sn;
	PGraphics obuf;

	int drawBuf = 0;

	@Override
	public void settings( ) {

		this.size( this.sWidth, this.sHeigh );

		this.setName( );

	}

	@Override
	public void setup( ) {

		this.codes = Walsh.getWalshCodes( 3 );

		this.spinner = new CDMA( );

		this.sn = this.loadImage( ProjectBase.dataDir + "StarryNight.jpg" );
		this.sn.loadPixels( );

		// Output size is going to be:
		// align4( inputbits / codesize * codesize * ( codebits + 2 ) )
		// Need to split input buffer into codebits streams
		// So, osize: ( wpx * hpx * bytes/px * bits/byte ) * obit/ibit / bit/byte =
		// wpx * hpx * 3 * 8 * 10 / 8 = wpx * hpx * 3 * 10
		int obytes = this.sn.width * this.sn.height * 30;
		//int ow = ;
		//this.obuf = this.createGraphics( );

		// Get 8 streams, encode then decode

	}

	@Override
	public void draw( ) {

		switch( this.drawBuf ) {

			case 0:
				this.image( this.sn, 0, 0, this.sWidth, this.sHeigh );
				break;

			case 1:
				this.image( this.obuf, 0, 0, this.sWidth, this.sHeigh );

		}

	}

	@Override
	public void keyPressed( ) {

		if( this.key == ' ' )
			this.drawBuf = ( this.drawBuf + 1 ) % 2;

	}

}
