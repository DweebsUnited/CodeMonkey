package CodeMonkey.spatial;

import CodeMonkey.transform.CoordinateTransform;
import CodeMonkey.transform.coordinate.CTLinear;
import processing.core.PImage;
import processing.core.PVector;


// This one uses an RGB texture as a normal map
public class TexturedMirror extends BillboardMirror {

	private CoordinateTransform texSpace;
	private PImage normTex;

	public TexturedMirror( float width, float height, PVector norm, PVector mirrCent, PImage normTex ) {

		super( width, height, norm, mirrCent );

		this.texSpace = new CTLinear( new PVector( -width / 2f, -height / 2f ), new PVector( width / 2f, height / 2f ),
				new PVector( 0, 0 ), new PVector( 1, 1 ) );

		this.normTex = normTex;

	}

	@Override
	public PVector normal( PVector p ) {

		// System.out.println( String.format( "IN: %f, %f", p.x, p.y ) );

		p = this.texSpace.map( p.copy( ) );

		// System.out.println( String.format( "TX: %f, %f", p.x, p.y ) );

		int col = this.normTex.pixels[ (int) ( Math.floor( this.normTex.width * p.x )
				+ this.normTex.width * Math.floor( this.normTex.height * p.y ) ) ];

		PVector t = new PVector( ( col >> 16 & 0xFF ) / 255f, ( col >> 8 & 0xFF ) / 255f, ( col & 0xFF ) / 255f );

		t.normalize( );

		return t;

	}

}
