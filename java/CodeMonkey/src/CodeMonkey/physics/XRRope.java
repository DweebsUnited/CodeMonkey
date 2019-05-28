package CodeMonkey.physics;

import java.util.ArrayList;

import processing.core.PGraphics;
import processing.core.PVector;


public class XRRope {

	// One fixed end, one physics end
	private PVector lF, lP;
	private ArrayList< PointMassAccum > RS;

	private float RL;
	private float k;
	private int nRS;
	private float mLen; // kg / m

	public XRRope( PVector fix_i, PointMassAccum phy_i, float RL, float ROPE_K, int ROPE_SEG, float ROPE_MASS ) {

		if( ROPE_SEG < 1 )
			throw new RuntimeException( "A Rope with no segments is a spring" );

		this.RL = RL;
		this.nRS = ROPE_SEG;
		this.k = ROPE_K;
		this.mLen = ROPE_MASS;

		this.lF = fix_i.copy( );
		this.lP = phy_i.p.copy( );

		this.RS = new ArrayList< PointMassAccum >( this.nRS );
		for( int rsdx = 0; rsdx < this.nRS; ++rsdx ) {

			float rsF = ( rsdx + 0.5f ) / this.nRS;
			this.RS.add(
					new PointMassAccum(
							this.lP.x * rsF + this.lF.x * ( 1f - rsF ),
							this.lP.y * rsF + this.lF.y * ( 1f - rsF ),
							this.lP.z * rsF + this.lF.z * ( 1f - rsF ),
							this.RL * this.mLen / this.nRS ) );

		}

	}

	public void accum( PVector p_f, PointMassAccum p_p ) {

		this.accum( p_f, p_p, this.RL );

	}

	public void accum( PVector p_f, PointMassAccum p_p, float RL ) {

		this.lF.set( p_f );
		this.lP.set( p_p.p );

		this.RL = RL;

		PVector fa = new PVector( );
		PVector fb = new PVector( );

		PointMassAccum pma = null;
		PointMassAccum pmb = this.RS.get( 0 );

		PVector pa = p_f.copy( );
		PVector pb = pmb.p.copy( );

		// Treat the first spring special, has half the length
		Spring.spring( this.k, ( RL / this.nRS ) / 2, pa, pb, fa, fb );

		// And we don't force the driver
		pmb.accum( fb );

		// Do update mass though
		pmb.setM( RL * this.mLen / this.nRS );

		for( int rsdx = 1; rsdx < this.nRS; ++rsdx ) {

			// Rotate in next segment
			pma = pmb;
			pmb = this.RS.get( rsdx );
			pa.set( pb );
			pb.set( pmb.p );

			Spring.spring( this.k, RL / this.nRS, pa, pb, fa, fb );

			// Use forces
			pma.accum( fa );
			pmb.accum( fb );

			// Update segment mass
			pmb.setM( RL * this.mLen / this.nRS );

		}

		// Last spring, connecting to Physics object has to be treated specially
		pmb = this.RS.get( this.nRS - 1 );
		pa.set( pb );
		pb.set( p_p.p );

		Spring.spring( this.k, ( RL / this.nRS ) / 2, pa, pb, fa, fb );

		// Use forces
		pmb.accum( fa );
		p_p.accum( fb );

		// Update segment mass
		pmb.setM( RL * this.mLen / this.nRS );

	}

	public void verlet( float dt ) {

		this.verlet( dt, false );

	}

	public void verlet( float dt, boolean GRAVITY ) {

		for( PointMassAccum pm : this.RS )
			pm.verlet( dt, GRAVITY );

	}

	public void draw( PGraphics canvas ) {

		canvas.noFill( );
		canvas.stroke( 3, 71, 72 );

		int[ ] cols = { canvas.color( 255, 0, 0 ), canvas.color( 0, 255, 0 ) };

		PointMassAccum pmb = this.RS.get( 0 );

		PVector pa = this.lF.copy( );
		PVector pb = pmb.p.copy( );

		canvas.line( pa.x, pa.y, pa.z, pb.x, pb.y, pb.z );

		for( int rsdx = 1; rsdx < this.nRS; ++rsdx ) {

			canvas.stroke( cols[ rsdx % 2 ] );

			// Rotate in next segment
			pmb = this.RS.get( rsdx );
			pa.set( pb );
			pb.set( pmb.p.copy( ) );

			canvas.line( pa.x, pa.y, pa.z, pb.x, pb.y, pb.z );

		}

		pa.set( pb );
		pb.set( this.lP.copy( ) );

		canvas.stroke( 3, 71, 72 );
		canvas.line( pa.x, pa.y, pa.z, pb.x, pb.y, pb.z );

	}

}
