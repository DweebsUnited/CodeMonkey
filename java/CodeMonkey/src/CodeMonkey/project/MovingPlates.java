package CodeMonkey.project;

import processing.core.PApplet;
import processing.core.PConstants;
import wblut.hemesh.HEC_Dual;
import wblut.hemesh.HEC_Icosahedron;
import wblut.hemesh.HEM_Smooth;
import wblut.hemesh.HEM_Spherify;
import wblut.hemesh.HES_PlanarMidEdge;
import wblut.hemesh.HES_Subdividor;
import wblut.hemesh.HE_EdgeIterator;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Halfedge;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Selection;
import wblut.processing.WB_Render;


/**
 * Not-so-simple planet generator based on tectonic plates
 *
 * Pretty much a rip off of a very old blog post I found, but I will take it in
 * a different direction. Also first usage of the fantastic HE_Mesh library from
 * W:Blut.
 *
 * @see https://github.com/wblut/HE_Mesh
 * @see https://experilous.com/1/blog/post/procedural-planet-generation#irregularityEverywhere
 * @author DweebsUnited
 */
public class MovingPlates extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.MovingPlates" );

	}

	// Number of subdivision rounds to run
	private final int nSub = 3;
	// Probability to pick an edge
	private final double eProb = 0.05;
	// Number of edges to flip before relaxing
	private final int nFlip = 1;

	private HE_Mesh mesh, dual;
	private WB_Render render;


	/**
	 * Flip an edge in a triangle mesh
	 *
	 * @param e Either halfedge of the edge to flip
	 */
	public void flipEdge( HE_Halfedge e ) {

		// Step one: gather relevant objects
		HE_Halfedge e0 = e;
		HE_Halfedge e1 = e0.getPair( );
		HE_Halfedge e2 = e0.getNextInFace( );
		HE_Halfedge e3 = e2.getNextInFace( );
		HE_Halfedge e4 = e1.getNextInFace( );
		HE_Halfedge e5 = e4.getNextInFace( );

		HE_Face f0 = e0.getFace( );
		HE_Face f1 = e1.getFace( );

		e0.setNext( e3 );
		e0.setPrev( e4 );

		e1.setNext( e5 );
		e1.setPrev( e2 );

		e2.setNext( e1 );
		e2.setPrev( e5 );

		e3.setNext( e4 );
		e3.setPrev( e0 );

		e4.setNext( e0 );
		e4.setPrev( e3 );

		e5.setNext( e2 );
		e5.setPrev( e1 );

		e4.setFace( f0 );
		e2.setFace( f1 );

		f0.setHalfedge( e0 );
		f1.setHalfedge( e1 );

	}

	@Override
	public void settings( ) {

		this.size( 720, 640, PConstants.P3D );

	}

	@Override
	public void setup( ) {

		// Make a renderer
		this.render = new WB_Render( this );


		// Begin generation!

		// Make an icosahedron
		System.out.println( "Setting up to create Icosahedron" );
		HEC_Icosahedron creator = new HEC_Icosahedron( );
		creator.setRadius( 200 );

		// Then a mesh
		System.out.println( "Making a mesh" );
		this.mesh = new HE_Mesh( creator );

		// Subdivide a bunch, re-sphering
		System.out.println( "Setting up to subdivide" );
		HES_Subdividor subdividor = new HES_PlanarMidEdge( );
		HEM_Spherify spherMod = new HEM_Spherify( );
		spherMod.setRadius( 200 );
		spherMod.setCenter( 0, 0, 0 );
		System.out.print( "Subdividing" );
		for( int sdx = 0; sdx < this.nSub; ++sdx ) {

			System.out.print( "." );

			this.mesh = subdividor.apply( this.mesh );
			this.mesh.modify( spherMod );

		}
		System.out.println( );

		// Do some edge flippy shit
		System.out.println( "Setting up to perturb mesh" );
		HE_Selection toFlip = this.mesh.selectRandomEdges( this.eProb );
		HEM_Smooth smoothMod = new HEM_Smooth( );

		System.out.println(
				String.format( "Will flip %d / %d edges", toFlip.getNumberOfEdges( ), this.mesh.getNumberOfEdges( ) ) );

		int nFlipped = 0;
		HE_EdgeIterator iter = toFlip.eItr( );

		while( iter.hasNext( ) ) {

			HE_Halfedge e = iter.next( );

			// TODO: check if valid before flipping
			this.flipEdge( e );

			// Batch flip then smooth
			if( nFlipped % nFlip == 0 )
				this.mesh.modify( smoothMod );

		}

		// Make the dual - Pent/Hexa/Hepta - mesh
		HEC_Dual dualCre = new HEC_Dual( this.mesh );
		dualCre.setFixNonPlanarFaces( false );
		this.dual = new HE_Mesh( dualCre );

		// Now resphere the original mesh a little bigger for effect
		spherMod.setRadius( 215 );
		this.mesh.modify( spherMod );

	}

	@Override
	public void draw( ) {

		this.background( 55 );

		this.directionalLight( 255, 255, 255, 1, 1, -1 );
		this.directionalLight( 127, 127, 127, -1, -1, 1 );

		this.translate( this.width / 2, this.height / 2 );
		this.rotateY( this.mouseX * 1.0f / this.width * PConstants.TWO_PI );
		this.rotateX( this.mouseY * 1.0f / this.height * PConstants.TWO_PI );

		this.stroke( 0 );
		this.noFill( );
		this.render.drawEdges( this.mesh );
		this.render.drawEdges( this.dual );

		this.noStroke( );
		// this.fill( 255, 127 );
		// this.render.drawFaces( this.mesh );
		this.fill( 0, 255, 0, 255 );
		this.render.drawFaces( this.dual );

	}

}
