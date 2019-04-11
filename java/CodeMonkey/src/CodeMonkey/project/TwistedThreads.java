package CodeMonkey.project;

import CodeMonkey.signal.CDMA;
import CodeMonkey.signal.Walsh;
import processing.core.PApplet;


public class TwistedThreads extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.TwistedThreads" );

	}

	int sWidth = 720;
	int sHeigh = 640;

	int codebits = 8;

	CDMA spinner;
	float[ ][ ] codes;

	@Override
	public void settings( ) {

		this.size( this.sWidth, this.sHeigh );

	}

	@Override
	public void setup( ) {

		this.codes = Walsh.getWalshCodes( 3 );

		this.spinner = new CDMA( );

		// Get 8 streams, encode then decode

	}

	@Override
	public void draw( ) {


	}


}
