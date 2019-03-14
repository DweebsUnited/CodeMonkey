package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.physics.PointMass;
import CodeMonkey.physics.Spring;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;


public class PhoneticMeow extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.PhoneticMeow" );

	}

	private final int GRID_X = 9;
	private final int GRID_Y = 9;

	private final float dt = 1 / 30f;

	private final float Knom = 0.01f;

	private Random rng = new Random( );

	private int cWidth = 720;
	private int cHeigh = 640;

	private PVector pa, pb;

	// Invalid points: 0,0; 0,w; h,0; h,w
	// Anchored points: 0,1 - 0,w-1; etc
	// Just loop from 1 -> x-1 for physics updates
	private ArrayList< ArrayList< PointMass > > gridPoints;
	private ArrayList< ArrayList< PVector > > gridAnchors;
	private ArrayList< ArrayList< Float > > gridK;
	private ArrayList< ArrayList< PVector > > gridF;

	private boolean clear = true;

	private float fx, fy;

	@Override
	public void settings( ) {

		this.size( this.cWidth, this.cHeigh );
		this.setName( );

	}

	private void reset( ) {

		for( int gdy = 1; gdy < this.GRID_Y - 1; ++gdy ) {

			ArrayList< PointMass > rowP = this.gridPoints.get( gdy );
			ArrayList< PVector > rowA = this.gridAnchors.get( gdy );

			for( int gdx = 0; gdx < this.GRID_X; ++gdx ) {

				PointMass p = rowP.get( gdx );
				PVector pP = rowA.get( gdx );

				p.c.set( pP );
				p.co.set( pP );

			}
		}

		this.pa = null;

		this.fx = this.rng.nextInt( 240 ) + 120;
		this.fy = this.rng.nextInt( 240 ) + 120;

	}

	@Override
	public void setup( ) {

		this.gridPoints = new ArrayList< ArrayList< PointMass > >( );
		this.gridAnchors = new ArrayList< ArrayList< PVector > >( );
		this.gridK = new ArrayList< ArrayList< Float > >( );
		this.gridF = new ArrayList< ArrayList< PVector > >( );

		for( int gdy = 0; gdy < this.GRID_Y; ++gdy ) {

			ArrayList< PointMass > rowP = new ArrayList< PointMass >( );
			ArrayList< PVector > rowA = new ArrayList< PVector >( );
			ArrayList< Float > rowK = new ArrayList< Float >( );
			ArrayList< PVector > rowF = new ArrayList< PVector >( );

			this.gridPoints.add( rowP );
			this.gridAnchors.add( rowA );
			this.gridK.add( rowK );
			this.gridF.add( rowF );

			for( int gdx = 0; gdx < this.GRID_X; ++gdx ) {

				PVector p = new PVector( this.cWidth / ( this.GRID_X - 1 ) * gdx,
						this.cHeigh / ( this.GRID_Y - 1 ) * gdy );

				rowP.add( new PointMass( p.x, p.y, 1 ) );
				rowA.add( p );

				rowK.add( 0f );
				rowF.add( new PVector( 0, 0 ) );

			}

		}

		this.reset( );

	}

	@Override
	public void draw( ) {

		// Several passes to update grid:
		// Calculate k from masses
		// Accumulate Fs
		// Update

		// Reset values and calculate k at each point
		for( int gdy = 0; gdy < this.GRID_Y; ++gdy ) {

			ArrayList< PointMass > rowP = this.gridPoints.get( gdy );
			// ArrayList<PVector> rowA = this.gridAnchors.get( gdy );
			ArrayList< Float > rowK = this.gridK.get( gdy );
			// ArrayList<PVector> rowF = this.gridF.get( gdy );

			for( int gdx = 0; gdx < this.GRID_X; ++gdx ) {

				PointMass p = rowP.get( gdx );
				float kA = 0;

				if( this.pa != null ) {
					float dx = p.c.x - this.pa.x;
					float dy = p.c.y - this.pa.y;
					kA += this.Knom / ( dx * dx + dy * dy );
				}

				rowK.set( gdx, 1f + kA );

			}
		}

		// Calculate and accumulate Fs
		// Probably some duplication to be eliminated here...
		for( int gdy = 1; gdy < this.GRID_Y - 1; ++gdy ) {

			ArrayList< PointMass > rowP = this.gridPoints.get( gdy );
			// ArrayList<PVector> rowA = this.gridAnchors.get( gdy );
			ArrayList< Float > rowK = this.gridK.get( gdy );
			ArrayList< PVector > rowF = this.gridF.get( gdy );

			for( int gdx = 1; gdx < this.GRID_X - 1; ++gdx ) {

				PointMass p = rowP.get( gdx );
				PVector F = rowF.get( gdx );

				PVector Ft = new PVector( );

				Float k = rowK.get( gdx );

				Spring.spring( ( k + rowK.get( gdx - 1 ) ) / 2, p.c, rowP.get( gdx - 1 ).c, Ft, null );
				F.add( Ft );
				Spring.spring( ( k + rowK.get( gdx + 1 ) ) / 2, p.c, rowP.get( gdx + 1 ).c, Ft, null );
				F.add( Ft );
				Spring.spring( ( k + this.gridK.get( gdy - 1 ).get( gdx ) ) / 2, p.c,
						this.gridPoints.get( gdy - 1 ).get( gdx ).c, Ft, null );
				F.add( Ft );
				Spring.spring( ( k + this.gridK.get( gdy + 1 ).get( gdx ) ) / 2, p.c,
						this.gridPoints.get( gdy + 1 ).get( gdx ).c, Ft, null );
				F.add( Ft );

			}
		}

		// Update points with their forces
		for( int gdy = 1; gdy < this.GRID_Y - 1; ++gdy ) {

			ArrayList< PointMass > rowP = this.gridPoints.get( gdy );
			// ArrayList<PVector> rowA = this.gridAnchors.get( gdy );
			// ArrayList<Float> rowK = this.gridK.get( gdy );
			ArrayList< PVector > rowF = this.gridF.get( gdy );

			for( int gdx = 1; gdx < this.GRID_X - 1; ++gdx ) {

				PointMass p = rowP.get( gdx );
				p.verlet( rowF.get( gdx ), this.dt );

				// float dx = p.c.x - a.x;
				// float dy = p.c.y - a.y;
				// if( dx * dx + dy * dy > 0.01f )
				// System.out.println( ( dx * dx + dy * dy ) );

			}
		}

		// Mass update
		float cx = ( this.GRID_X - 3 ) * ( (float) Math.sin( this.frameCount / this.fx * 2 * PConstants.PI ) + 1 ) / 2f;
		float cy = ( this.GRID_Y - 3 ) * ( (float) Math.sin( this.frameCount / this.fy * 2 * PConstants.PI ) + 1 ) / 2f;

		int gx = (int) Math.floor( cx );
		float tx = cx - gx;
		int gy = (int) Math.floor( cy );
		float ty = cy - gy;

		PointMass TL = this.gridPoints.get( gy + 1 ).get( gx + 1 );
		PointMass TR = this.gridPoints.get( gy + 1 ).get( gx + 2 );
		PointMass BL = this.gridPoints.get( gy + 2 ).get( gx + 1 );
		PointMass BR = this.gridPoints.get( gy + 2 ).get( gx + 2 );

		PVector tl = PVector.lerp( TL.c, TR.c, tx );
		PVector bl = PVector.lerp( BL.c, BR.c, tx );

		this.pb = PVector.lerp( tl, bl, ty );

		// Background clear
		if( this.clear ) {
			this.background( 255 );
			this.clear = false;
		}

		// For now do it every time
		this.background( 255 );

		// Decay to background
		this.noStroke( );
		// this.fill( 255, 25 );
		// this.rect( 0, 0, this.cWidth, this.cHeigh );

		// Skip the outer anchors, we can always +-1 on each axis
		for( int gdy = 0; gdy < this.GRID_Y; ++gdy ) {

			ArrayList< PointMass > rowP = this.gridPoints.get( gdy );
			ArrayList< PVector > rowA = this.gridAnchors.get( gdy );

			for( int gdx = 0; gdx < this.GRID_X; ++gdx ) {

				PVector pP = rowP.get( gdx ).c;
				PVector pA = rowA.get( gdx );

				this.fill( 0, 255, 0 );
				this.ellipse( pA.x, pA.y, 3, 3 );
				this.fill( 0 );
				this.ellipse( pP.x, pP.y, 3, 3 );

			}
		}

		// Draw mass
		this.fill( 255, 0, 0 );
		if( this.pa != null && this.pb != null ) {
			this.line( this.pa.x, this.pa.y, this.pb.x, this.pb.y );
		}

		this.pa = this.pb;

	}

	@Override
	public void keyPressed( ) {

		if( this.key == 'z' )
			this.clear = true;
		else if( this.key == 'w' )
			this.save( );
		else if( this.key == 'x' )
			this.reset( );

	}

}
