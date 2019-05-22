package CodeMonkey.project;

import processing.core.PApplet;


public class PoppingKernel extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.PoppingKernel" );

	}

	private final int sWidth = 720;
	private final int sHeigh = 640;

	private final float N_FAC = 1.0f / PApplet.max( this.sWidth, this.sHeigh );

	// TODO: We can't do even kernels yet...
	private int k = 1;
	// Identity
	private float[ ] kernel = { 0, 1, 0, 1, 1, 1, 0, 1, 0 };
	private float kernNorm = 1.0f / 5f;

	// private float[ ] kernel = { 0, -1, 0, -1, 4, -1, 0, -1, 0 };
	// private float kernNorm = 1;

	private float[ ] bufA, bufB;

	private void setupBuf( ) {

		this.loadPixels( );
		for( int dy = 0; dy < this.sHeigh; ++dy ) {
			for( int dx = 0; dx < this.sWidth; ++dx ) {

				int pdx = dy * this.sWidth + dx;
				this.bufA[ pdx ] = this.noise( dx * this.N_FAC, dy * this.N_FAC );
				this.bufB[ pdx ] = this.bufA[ pdx ];
				this.pixels[ pdx ] = this.color( 255 * this.bufA[ pdx ] );


			}
		}
		this.updatePixels( );

	}

	@Override
	public void settings( ) {

		this.size( this.sWidth, this.sHeigh );

		this.setName( );

	}

	@Override
	public void setup( ) {

		this.bufA = new float[ this.sWidth * this.sHeigh ];
		this.bufB = new float[ this.sWidth * this.sHeigh ];

		this.setupBuf( );

		this.frameRate( 1 );

	}

	@Override
	public void draw( ) {

		// Apply KEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEERNEL
		for( int dy = 0; dy < this.sHeigh; ++dy ) {
			for( int dx = 0; dx < this.sWidth; ++dx ) {

				int pdx = dy * this.sWidth + dx;

				float accum = 0;

				int kdx = 0;
				for( int oy = -this.k; oy <= this.k; ++oy ) {
					for( int ox = -this.k; ox <= this.k; ++ox ) {

						// We will compute the offset for the kernel by reflecting out of bounds
						// This is a neat way of handling OOB, but will inevitably be slower
						// TODO: Experiment with other methods of OOB handling
						int odx = 0;
						if( dy + oy < 0 )
							//odx += ( PApplet.abs( oy ) - 1 ) * this.sWidth;
							odx += 0;

						else if( dy + oy >= this.sHeigh )
							// dy + oy = sHeigh + e
							// e = ( dy + oy ) - sHeigh
							// Want: sHeigh - 1 - e
							//odx += ( this.sHeigh - 1 - dy + oy - this.sHeigh ) * this.sWidth;
							odx += 0;

						else
							odx += oy * this.sWidth;

						// Again for x, but don't mult by the width of buf
						if( dx + ox < 0 )
							//odx += PApplet.abs( ox ) - 1;
							odx += 0;

						else if( dx + ox >= this.sWidth )
							//odx += this.sWidth - 1 - dx + ox - this.sWidth;
							odx += 0;

						else
							odx += ox;

						// Accum the offset using the kdx
						accum += this.bufA[ pdx + odx ] * this.kernel[ kdx ];

						kdx += 1;

					}
				}

				this.bufB[ pdx ] = accum * this.kernNorm;

			}
		}

		float[ ] t = this.bufA;
		this.bufA = this.bufB;
		this.bufB = t;

		this.loadPixels( );
		for( int pdx = 0; pdx < this.sWidth * this.sHeigh; ++pdx ) {

			this.pixels[ pdx ] = this.color( 255 * this.bufA[ pdx ] );

		}
		this.updatePixels( );

	}

}
