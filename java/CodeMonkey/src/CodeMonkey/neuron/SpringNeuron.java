package CodeMonkey.neuron;

import java.util.ArrayList;

import CodeMonkey.physics.PointMassAccum;
import CodeMonkey.physics.Spring;
import processing.core.PVector;


// Base class for neurons with physics
abstract public class SpringNeuron {

	final float SPRING_K = 25;

	// These are needed to draw
	// Stroke color
	abstract public int c( );

	// Position
	public PVector p;
	// Electrical value
	public float v;

	// Points linked to
	private ArrayList< SpringNeuron > links;
	private float linkDist = 15;
	// private ArrayList<Float> linkDist;

	// Physics!
	// Pointmass
	private PointMassAccum pm;
	// V accumulator
	protected float VAccum;

	// Springs
	// Nominal length
	// Driven length
	abstract protected float driveLen( );


	// Constants for child usage
	protected final float dMag = 25;


	private void verlet( ) {

		this.pm.verlet( 1 / 30f );
		this.p = this.pm.p.copy( );

	}

	public void link( SpringNeuron n ) {

		this.links.add( n );

		// this.linkDist.add( PVector.dist( this.p, n.p ) );

	}

	// Send values and forces to links
	public void transmit( ) {

		PVector fa = new PVector( 0, 0 );
		PVector fb = new PVector( 0, 0 );

		for( int ldx = 0; ldx < this.links.size( ); ++ldx ) {

			SpringNeuron tp = this.links.get( ldx );
			// float nomLen = this.linkDist.get( ldx );

			Spring.spring( this.SPRING_K, this.linkDist + this.driveLen( ), this.p, tp.p, fa, fb );

			this.receive( fa );
			tp.receive( this.v );
			tp.receive( fb );

		}

	}

	// Update self with forces, reset accums
	public void update( ) {

		this.verlet( );

	}

	// Receive values
	public void receive( float v ) {

		this.VAccum += v;

	}

	public void receive( PVector F ) {

		this.pm.accum( F );

	}

	// Base constructor, create members
	public SpringNeuron( ) {

		this.p = new PVector( );
		this.v = 0;
		this.links = new ArrayList< SpringNeuron >( );
		// this.linkDist = new ArrayList<Float>( );
		this.pm = new PointMassAccum( 0, 0, 2.5f );

	}

	// With starting position -> dont break physics
	public SpringNeuron( PVector sp ) {

		// Setup objects
		this( );

		// Now set position
		this.pm.set( sp );
		this.p = sp.copy( );

	}

}
