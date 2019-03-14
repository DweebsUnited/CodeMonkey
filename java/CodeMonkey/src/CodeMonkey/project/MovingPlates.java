package CodeMonkey.project;

import processing.core.PApplet;
import processing.core.PConstants;
import wblut.hemesh.HEC_Dual;
import wblut.hemesh.HEC_Icosahedron;
import wblut.hemesh.HEM_Smooth;
import wblut.hemesh.HEM_Spherify;
import wblut.hemesh.HES_PlanarMidEdge;
import wblut.hemesh.HES_Subdividor;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Selection;
import wblut.processing.WB_Render;


public class MovingPlates extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.MovingPlates" );

	}

	private final int nSub = 3;
	private final double eProb = 0.05;

	private HE_Mesh mesh, dual;
	private WB_Render render;

	@Override
	public void settings( ) {

		this.size( 720, 640, PConstants.P3D );

	}

	@Override
	public void setup( ) {

		// Make a renderer
		this.render = new WB_Render( this );


		// Begin!

		// Make an icosahedron
		HEC_Icosahedron creator = new HEC_Icosahedron( );
		creator.setRadius( 200 );

		// Then a mesh
		this.mesh = new HE_Mesh( creator );

		// Subdivide a bunch
		HES_Subdividor subdividor = new HES_PlanarMidEdge( );
		for( int sdx = 0; sdx < this.nSub; ++sdx )
			this.mesh = subdividor.apply( this.mesh );

		// Do some edge flippy shit
		HE_Selection toFlip = this.mesh.selectRandomEdges( this.eProb );
		System.out.println( String.format( "#Edge: %d", toFlip.getNumberOfEdges( ) ) );
		// TODO: rotateEdge
		// TODO:

		// Smooth it out
		HEM_Smooth smoothMod = new HEM_Smooth( );
		this.mesh.modify( smoothMod );

		// Then re-sphere
		HEM_Spherify spherMod = new HEM_Spherify( );
		spherMod.setRadius( 200 );
		spherMod.setCenter( 0, 0, 0 );
		this.mesh.modify( spherMod );

		// Make the dual
		HEC_Dual dualCre = new HEC_Dual( this.mesh );
		dualCre.setFixNonPlanarFaces( false );
		this.dual = new HE_Mesh( dualCre );

		// Now resphere the mesh a little bigger for effect
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
