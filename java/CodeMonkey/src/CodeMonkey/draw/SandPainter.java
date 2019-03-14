package CodeMonkey.draw;

import java.util.Random;
import java.util.function.DoubleUnaryOperator;

import processing.core.PGraphics;
import processing.core.PVector;


public class SandPainter implements LineDrawer {

	private Random rng;
	private float density;
	private float rad;
	private int color;
	private DoubleUnaryOperator dist;

	public SandPainter( Random rng, float density, float rad, int color, DoubleUnaryOperator dist ) {

		this.rng = rng;
		this.density = density;
		this.rad = rad;
		this.color = color;
		this.dist = dist;

	}

	public SandPainter( Random rng, float density, float rad, int color ) {

		this( rng, density, rad, color, ( double arg ) -> {
			return arg;
		} );

	}

	@Override
	public void line( PGraphics canvas, float ox, float oy, float dx, float dy ) {

		this.line( canvas, new PVector( ox, oy ), new PVector( dx, dy ) );

	}

	@Override
	public void line( PGraphics canvas, PVector o, PVector d ) {

		float dist = o.dist( d );
		int nPoints = (int) Math.ceil( this.density * dist );

		// canvas.beginDraw( );
		canvas.noStroke( );
		canvas.fill( this.color );
		for( int pdx = 0; pdx < nPoints; ++pdx ) {

			float t = (float) this.dist.applyAsDouble( this.rng.nextFloat( ) );
			PVector p = PVector.lerp( o, d, t );
			canvas.ellipse( p.x, p.y, this.rad, this.rad );

		}
		// canvas.endDraw( );

	}

}
