package CodeMonkey.project;

import processing.core.PApplet;


public class DirtyLaundry extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.DirtyLaundry" );

	}

	int cWidth = 720;
	int cHeigh = 640;

	float[ ] height, waterh, energy, tw, te;

	boolean reset = true;

	@Override
	public void settings( ) {

		this.size( this.cWidth, 640 );

		this.setName( );

	}

	@Override
	public void setup( ) {

		this.height = new float[ this.cWidth ];
		this.waterh = new float[ this.cWidth ];
		this.energy = new float[ this.cWidth ];
		this.tw = new float[ this.cWidth ];
		this.te = new float[ this.cWidth ];

	}

	@Override
	public void draw( ) {

		if( this.reset ) {

			this.reset = false;

			for( int cdx = 0; cdx < this.cWidth; ++cdx ) {

				this.height[ cdx ] = this.noise( cdx * 0.05f, 0 ) * 0.45f * this.cHeigh;
				this.waterh[ cdx ] = ( cdx == 0 ) ? ( 0.75f * this.cHeigh )
						: ( 0.55f * this.cHeigh - this.height[ cdx ] );
				this.energy[ cdx ] = 0;

				this.tw[ cdx ] = 0;
				this.te[ cdx ] = 0;

			}

		}


		// Calculate deltas
		for( int cdx = 0; cdx < this.cWidth; ++cdx ) {

			// If we need to push to left
			if( cdx > 0 && ( this.height[ cdx ] + this.waterh[ cdx ] - this.energy[ cdx ] > this.height[ cdx - 1 ]
					+ this.waterh[ cdx - 1 ] + this.energy[ cdx - 1 ] ) ) {

				float flow = PApplet.min(
						this.waterh[ cdx ],
						this.height[ cdx ] + this.waterh[ cdx ] - this.energy[ cdx ] - this.height[ cdx - 1 ]
								- this.waterh[ cdx - 1 ] - this.energy[ cdx - 1 ] ) / 4.0f;

				this.tw[ cdx - 1 ] += flow;
				this.tw[ cdx ] -= flow;
				this.te[ cdx - 1 ] += -this.energy[ cdx - 1 ] / 2 - flow;

			}

			// If we need to push to right
			if( cdx < ( this.cWidth - 1 ) && ( this.height[ cdx ] + this.waterh[ cdx ]
					- this.energy[ cdx ] > this.height[ cdx + 1 ] + this.waterh[ cdx + 1 ] + this.energy[ cdx
							+ 1 ] ) ) {

				float flow = PApplet.min(
						this.waterh[ cdx ],
						this.height[ cdx ] + this.waterh[ cdx ] - this.energy[ cdx ] - this.height[ cdx + 1 ]
								- this.waterh[ cdx + 1 ] - this.energy[ cdx + 1 ] ) / 4.0f;

				this.tw[ cdx + 1 ] += flow;
				this.tw[ cdx ] -= flow;
				this.te[ cdx + 1 ] += -this.energy[ cdx + 1 ] / 2 - flow;

			}

		}

		// Update states
		for( int cdx = 0; cdx < this.cWidth; ++cdx ) {

			this.waterh[ cdx ] += this.tw[ cdx ];
			this.tw[ cdx ] = 0;

			this.energy[ cdx ] += this.te[ cdx ];
			this.te[ cdx ] = 0;

		}

		this.loadPixels( );

		int nWater = 0;
		for( int cdx = 0; cdx < this.cWidth; ++cdx ) {
			for( int rdx = 0; rdx < this.cHeigh; ++rdx ) {

				if( rdx < this.height[ cdx ] )
					this.pixels[ cdx + ( this.cHeigh - rdx - 1 ) * this.cWidth ] = this.color( 139, 69, 19 );
				else if( rdx < this.height[ cdx ] + this.waterh[ cdx ] ) {
					nWater += 1;
					this.pixels[ cdx + ( this.cHeigh - rdx - 1 ) * this.cWidth ] = this.color( 0, 128, 128 );
				}

			}
		}

		this.updatePixels( );

//		System.out.println( nWater );

	}

	@Override
	public void keyPressed( ) {

		if( this.key == ' ' )
			this.reset = true;

	}

}