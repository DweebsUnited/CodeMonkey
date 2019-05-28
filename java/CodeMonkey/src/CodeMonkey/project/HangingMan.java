package CodeMonkey.project;

import CodeMonkey.physics.PointMassAccum;
import CodeMonkey.physics.XRRope;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;


public class HangingMan extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.HangingMan" );

	}

	private int cWidth = 720;
	private int cHeigh = 640;

	private PGraphics canvas;

	// Physics tick rate
	private final float PHY_TICK = 1f / 1000; // ss / tick


	// Kran dimensions, all in meters
	private float kWidth = 20;
	private float hkWidth = this.kWidth / 2;
	private float kLengt = 40;
	private float hkLengt = this.kLengt / 2;
	private float kHeigh = 20;
	private float hkHeigh = this.kHeigh / 2;

	private float camDist = (float) Math.sqrt(
			this.hkWidth * this.hkWidth + this.hkHeigh * this.hkHeigh + this.hkLengt * this.hkLengt ) * 1.5f;
	private float camRota = 0;
	private float camFOV = 70;

	private final float CAMERA_ROT = PConstants.PI / 32;
	private final float CAMERA_DIT = this.camDist * 0.05f;

	private final float AXIS_LEN = 5;


	// Finally Kran parts
	// Drive head
	private PVector Dp;

	private final float DRIVE_SIZE = 2;

	// Kran Drive Control
	private PVector D = new PVector( 0, 0, 0 );

	private final float SPEED_X = 0.0254f * this.PHY_TICK; // m / s
	private final float SPEED_Y = 0.0254f * this.PHY_TICK; // m / s

	// Light parts
	private float Lm = 0.5f; // kg

	private PointMassAccum L;

	// Now the Rope
	private final float ROPE_K = 10000;
	private final int ROPE_SEG = 32;
	private final float ROPE_MASS = 0.25f; // kg / m

	private float RL = this.hkHeigh;

	private XRRope R;

	private final float LIGHT_SIZE = 0.5f;
	private final float SPEED_Z = 0.0254f * this.PHY_TICK;


	@Override
	public void settings( ) {

		this.size( this.cWidth, this.cHeigh, PConstants.P2D );

		this.setName( );

	}

	@Override
	public void setup( ) {

		this.canvas = this.createGraphics( this.cWidth, this.cHeigh, PConstants.P3D );

		this.canvas.beginDraw( );
		this.canvas.perspective( this.camFOV, 16f / 9, 0.01f, 1000f );
		this.canvas.endDraw( );

		this.Dp = new PVector( this.hkWidth, this.hkLengt, this.kHeigh );
		this.L = new PointMassAccum( this.hkWidth, this.hkLengt + this.RL, this.kHeigh, this.Lm );
		this.R = new XRRope( this.Dp, this.L, this.RL, this.ROPE_K, this.ROPE_SEG, this.ROPE_MASS );


	}

	@Override
	public void draw( ) {

		int phyStep = (int) Math.floor( 1f / this.PHY_TICK / this.frameRate );
		for( int sdx = 0; sdx < phyStep; ++sdx ) {

			// State update
			// X
			// Stop if it would take us out of bounds
			if( this.Dp.x + this.D.x > this.kWidth || this.Dp.x + this.D.x < 0 )
				this.D.x = 0;
			else
				this.Dp.x += this.D.x;

			// Y
			// Stop if it would take us out of bounds
			if( this.Dp.y + this.D.y > this.kLengt || this.Dp.y + this.D.y < 0 )
				this.D.y = 0;
			else
				this.Dp.y += this.D.y;

			// Z
			// Stop if it would take us out of bounds
			if( this.RL + this.D.z > this.kHeigh || this.RL + this.D.z < 0.1f )
				this.D.z = 0;
			else
				this.RL += this.D.z;

			// Now physics
			// Update Rope, this also accums to the Light
			this.R.accum( this.Dp, this.L, this.RL );

			// Verlet
			this.R.verlet( this.PHY_TICK, true );
			this.L.verlet( this.PHY_TICK, true );

		}


		// Now some drawing

		PVector eye = new PVector( this.camDist, 0, 0 );
		eye.rotate( this.camRota );
		eye.add( new PVector( this.hkWidth, this.hkLengt, this.hkHeigh ) );

		this.canvas.beginDraw( );

		this.canvas.background( 255 );

		this.canvas.camera( eye.x, eye.y, eye.z, this.hkWidth, this.hkLengt, this.hkHeigh, 0, 0, -1 );

		// Draw BB
		this.canvas.noFill( );
		this.canvas.stroke( 0 );

		this.canvas.pushMatrix( );
		this.canvas.translate( this.hkWidth, this.hkLengt, this.hkHeigh );
		this.canvas.box( this.kWidth, this.kLengt, this.kHeigh );
		this.canvas.popMatrix( );

		// Draw Drive - box at top
		this.canvas.fill( 14, 177, 210 );
		this.canvas.stroke( 0 );
		this.canvas.pushMatrix( );
		this.canvas.translate( this.Dp.x, this.Dp.y, this.Dp.z );
		this.canvas.translate( 0, 0, this.DRIVE_SIZE / 2 );
		this.canvas.box( this.DRIVE_SIZE, this.DRIVE_SIZE, this.DRIVE_SIZE );
		this.canvas.popMatrix( );

		// Draw Light
		this.canvas.fill( 220, 96, 46 );
		this.canvas.stroke( 0 );
		this.canvas.pushMatrix( );
		this.canvas.translate( this.L.p.x, this.L.p.y, this.L.p.z );
		this.canvas.box( this.LIGHT_SIZE, this.LIGHT_SIZE, this.LIGHT_SIZE );
		this.canvas.popMatrix( );

		// Draw Rope
		this.R.draw( this.canvas );

		// Draw origin axis
		this.canvas.noFill( );
		this.canvas.stroke( 255, 0, 0 );
		this.canvas.line( 0, 0, 0, this.AXIS_LEN, 0, 0 );
		this.canvas.stroke( 0, 255, 0 );
		this.canvas.line( 0, 0, 0, 0, this.AXIS_LEN, 0 );
		this.canvas.stroke( 0, 0, 255 );
		this.canvas.line( 0, 0, 0, 0, 0, this.AXIS_LEN );

		this.canvas.endDraw( );

		this.image( this.canvas, 0, 0, this.cWidth, this.cHeigh );

	}

	@Override
	public void keyPressed( ) {

		// Camera movement
		if( this.key == 'd' ) {

			// Rotate camera-right
			this.camRota = ( this.camRota - this.CAMERA_ROT + 2 * PConstants.PI ) % ( 2 * PConstants.PI );


		} else if( this.key == 't' ) {

			// Rotate camera-left
			this.camRota = ( this.camRota + this.CAMERA_ROT ) % ( 2 * PConstants.PI );

		} else if( this.key == 'g' ) {

			// Camera out
			this.camDist -= this.CAMERA_DIT;

		} else if( this.key == 'p' ) {

			// Camera in
			this.camDist += this.CAMERA_DIT;

		}

		// Some control signals now
		// X
		else if( this.key == 'q' ) {

			// GO Pos
			this.D.x = this.SPEED_X;

		} else if( this.key == 'a' ) {

			// STOP
			this.D.x = 0;

		} else if( this.key == 'z' ) {

			// GO Neg
			this.D.x = -this.SPEED_X;

		}

		// Y
		else if( this.key == 'w' ) {

			// GO Pos
			this.D.y = this.SPEED_Y;

		} else if( this.key == 'r' ) {

			// STOP
			this.D.y = 0;

		} else if( this.key == 'x' ) {

			// GO Neg
			this.D.y = -this.SPEED_Y;

		}

		// Z: Rope
		else if( this.key == 'f' ) {

			// GO Pos
			this.D.z = this.SPEED_Z;

		} else if( this.key == 's' ) {

			// STOP
			this.D.z = 0;

		} else if( this.key == 'c' ) {

			// GO Neg
			this.D.z = -this.SPEED_Z;

		}

	}

}
