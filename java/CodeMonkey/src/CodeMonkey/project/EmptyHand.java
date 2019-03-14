package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.physics.PointMassAccum;
import processing.core.PApplet;
import processing.core.PVector;


public class EmptyHand extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.EmptyHand" );

	}

	final int cWidth = 720;
	final int cHeigh = 640;

	final int N_P = 128;
	final int N_L = 4;

	final float F_K = 10;
	final float F_CAP = 100;
	final float F_CEN = 1000;

	Random rng = new Random( );

	class Point {

		public PointMassAccum pm;

		public ArrayList< Point > links;

		public Point( ) {

			this.pm = new PointMassAccum(
					EmptyHand.this.cWidth / 2 + (float) ( EmptyHand.this.rng.nextGaussian( ) * 10 ),
					EmptyHand.this.cHeigh / 2 + (float) ( EmptyHand.this.rng.nextGaussian( ) * 10 ), 1 );
			this.links = new ArrayList< Point >( );

		}

	}

	ArrayList< Point > ps = new ArrayList< Point >( );

	@Override
	public void settings( ) {

		this.size( this.cWidth, this.cHeigh );

		this.setName( );

	}

	@Override
	public void setup( ) {

		for( int pdx = 0; pdx < this.N_P; ++pdx ) {

			Point p = new Point( );

			this.ps.add( p );

		}

		for( int pdx = 0; pdx < this.N_P; ++pdx ) {

			Point p = this.ps.get( pdx );

			PVector cF = new PVector( this.cWidth / 2 - p.pm.p.x, this.cHeigh / 2 - p.pm.p.y );
			cF.mult( this.F_CEN );

			p.pm.accum( cF );

			// Link to
			for( int ldx = 0; ldx < this.N_L; ++ldx ) {

				int lp = this.rng.nextInt( this.N_P );
				// Don't link to self
				while( lp == ldx )
					lp = this.rng.nextInt( this.N_P );

				Point ldp = this.ps.get( lp );

				p.links.add( ldp );
				ldp.links.add( p );

			}

		}

	}

	@Override
	public void draw( ) {

		// Accumulate all the forces
		for( int pdx = 0; pdx < this.N_P; ++pdx ) {

			Point p = this.ps.get( pdx );

			for( Point pt : this.ps ) {

				PVector fp = new PVector( pt.pm.p.x, pt.pm.p.y );
				fp.mult( this.F_K );

				if( fp.mag( ) > this.F_CAP ) {

					fp.setMag( this.F_CAP );

				}

				p.pm.accum( fp );

			}

		}

		// Update all the points
		for( int pdx = 0; pdx < this.N_P; ++pdx ) {

			Point p = this.ps.get( pdx );

			p.pm.verlet( 1f / this.frameRate );

		}

		// Now draw
		this.background( 0 );
		this.noStroke( );
		this.stroke( 255 );
		for( Point p : this.ps ) {

			this.ellipse( p.pm.p.x, p.pm.p.y, 3, 3 );

		}

	}

}
