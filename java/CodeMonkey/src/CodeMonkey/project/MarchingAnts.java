package CodeMonkey.project;

import java.util.Random;

import CodeMonkey.draw.SandPainter;
import CodeMonkey.raymarch.OpUnion;
import CodeMonkey.raymarch.SDF;
import CodeMonkey.raymarch.Sphere;
import CodeMonkey.spatial.Ray;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;


public class MarchingAnts extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.MarchingAnts" );

	}

	final int cWidth = 720;
	final int cHeigh = 640;

	PGraphics canvas;

	boolean inBounds( Ray r ) {

		return r.o.x >= 0 && r.o.y >= 0 && r.o.x < this.cWidth && r.o.y < this.cHeigh;

	}

	Random rng;

	SDF scene;
	final float stopD = 0.01f;

	SandPainter sp;

	@Override
	public void settings( ) {

		this.size( this.cWidth, this.cHeigh );

	}

	@Override
	public void setup( ) {

		this.rng = new Random( );

		this.canvas = this.createGraphics( this.cWidth, this.cHeigh );
		this.canvas.beginDraw( );
		this.canvas.background( 0 );
		this.canvas.endDraw( );

		this.scene = new OpUnion(
				new Sphere(
						new PVector( this.rng.nextFloat( ) * this.cWidth, this.rng.nextFloat( ) * this.cHeigh ),
						this.rng.nextFloat( ) * 60 + 60 ),
				new Sphere(
						new PVector( this.rng.nextFloat( ) * this.cWidth, this.rng.nextFloat( ) * this.cHeigh ),
						this.rng.nextFloat( ) * 60 + 60 ) );

		this.sp = new SandPainter( this.rng, 0.001f, 1, this.color( 255, 4 ) );

	}

	@Override
	public void draw( ) {

		this.canvas.beginDraw( );
		// this.canvas.background( 0 );

		PVector o = new PVector( this.cWidth * 3f / 4, this.cHeigh / 2f );

		for( int s = 0; s < 1024; ++s ) {

			float t = 2f * PConstants.PI / s * ( 1024 + this.rng.nextFloat( ) );

			PVector dir = new PVector( 1, 0 );
			dir.rotate( t );
			Ray r = new Ray( o.copy( ), dir );

			float md = this.scene.sdf( r.o );
			PVector step;
			PVector nro;

			while( this.inBounds( r ) && md > this.stopD ) {

				step = r.d.copy( );
				step.mult( md );
				nro = r.o.copy( );
				nro.add( step );

				this.sp.line( this.canvas, r.o, nro );

				r.o.set( nro );
				md = this.scene.sdf( r.o );

			}

		}

		this.canvas.endDraw( );
		this.image( this.canvas, 0, 0 );

	}

}
