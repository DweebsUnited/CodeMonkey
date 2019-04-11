package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.physics.PointMassAccum;
import CodeMonkey.physics.Spring;
import processing.core.PApplet;
import processing.core.PVector;


public class GroupingDingbats extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.GroupingDingbats" );

	}

	Random rng = new Random( );

	int sWidth = 720;
	int sHeigh = 640;

	final int N_ACT = PApplet.min( this.sWidth, this.sHeigh );
	final float N_SPRNG = 0.1f;
	final float N_DIST = 5f;
	final float N_SCALE = 0.03f / PApplet.max( this.sWidth, this.sHeigh );
	final float CUTOFF = 50 * 50;

	ArrayList< PointMassAccum > actors;
	Spring s;

	@Override
	public void settings( ) {

		this.size( this.sWidth, this.sHeigh );

		this.setName( );

	}

	@Override
	public void setup( ) {

		this.s = new Spring( this.N_SPRNG, this.N_DIST );

		this.actors = new ArrayList< PointMassAccum >( );

		for( int adx = 0; adx < this.N_ACT; ++adx ) {

			this.actors.add(
					new PointMassAccum( this.rng.nextFloat( ) * this.sWidth, this.rng.nextFloat( ) * this.sHeigh, 5 ) );

		}

	}

	@Override
	public void draw( ) {

		PVector fa = new PVector( );

		for( int adx = 0; adx < this.N_ACT; ++adx ) {

			PointMassAccum src = this.actors.get( adx );

			// Force field based on Perlin
			PVector f = new PVector( this.noise( src.p.x * this.N_SCALE, src.p.y * this.N_SCALE ),
					this.noise( src.p.x * this.N_SCALE, src.p.y * this.N_SCALE ) );
//			f.mult( 5 );
//			src.accum( f );

			// And based on flocking-ish
			for( int ndx = adx + 1; ndx < this.N_ACT; ++ndx ) {

				PVector target = this.actors.get( ndx ).p.copy( );
				target.sub( src.p );

				float d = PVector.dist( src.p, target );

				if( d > 25 * 25 )
					continue;

				this.s.spring( src.p, target, f, null );
				src.accum( f );


			}

		}

		for( PointMassAccum a : this.actors ) {

			a.verlet( 1f / 30 );

		}

		this.background( 0 );

		this.noStroke( );
		this.fill( 255 );
		for( PointMassAccum a : this.actors ) {

			this.ellipse( a.p.x, a.p.y, 3, 3 );

		}

	}

}
