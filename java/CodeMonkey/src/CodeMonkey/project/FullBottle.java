package CodeMonkey.project;

import CodeMonkey.physics.PointMassAccum;
import CodeMonkey.spatial.HalfEdge.EdgeData;
import CodeMonkey.spatial.HalfEdge.HalfEdge;
import CodeMonkey.spatial.HalfEdge.Mesh;
import CodeMonkey.spatial.HalfEdge.PolygonData;
import CodeMonkey.spatial.HalfEdge.VertexData;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;


public class FullBottle extends ProjectBase {

	public static void main( String[ ] args ) {

		PApplet.main( "CodeMonkey.project.FullBottle" );

	}

	private class VertAppData {

		PointMassAccum pm;

	}

	public PVector getBary( PolygonData< Object > ptx ) {

		PVector b = new PVector( 0, 0 );

		HalfEdge< PointMassAccum, Object, Object > he = ptx.he;
		HalfEdge< PointMassAccum, Object, Object > fhe = he;

		do {

			b.add( he.vertexData.data.p );

			he = he.next;

		} while( he != fhe );

		b.mult( 0.33f );

		return b;

	}

	public PVector getHalf( EdgeData< Object > e ) {

		PVector b = new PVector( 0, 0 );
		b.add( ( (PointMassAccum) e.he.vertexData.data ).p );
		b.add( ( (PointMassAccum) e.he.pair.vertexData.data ).p );
		b.mult( 0.5f );

		return b;

	}

	private Mesh< PointMassAccum, Object, Object > mesh;

	private EdgeData< Object > etx;

	private PVector iA;
	private boolean initd = false;

	@Override
	public void settings( ) {

		this.size( 720, 640 );
		this.setName( );

	}

	@Override
	public void setup( ) {

		this.mesh = new Mesh< PointMassAccum, Object, Object >( );

	}

	@Override
	public void draw( ) {

		this.background( 255 );

		if( this.initd ) {

			VertexData< PointMassAccum > vsx, vtx;
			HalfEdge< PointMassAccum, Object, Object > he, nhe, phe;
			EdgeData< Object > pe, ne;


			// Draw all the vertices
			this.noFill( );
			this.stroke( 0 );
			for( VertexData< PointMassAccum > vt : this.mesh.vds ) {

				this.ellipse( vt.data.p.x, vt.data.p.y, 5, 5 );

			}

			// Draw all the edges
			this.noFill( );
			this.stroke( 0 );
			for( EdgeData< Object > etx : this.mesh.eds ) {

				vsx = etx.he.vertexData;
				vtx = etx.he.pair.vertexData;

				this.line( vsx.data.p.x, vsx.data.p.y, vtx.data.p.x, vtx.data.p.y );

			}

			// Draw lines from barycenter to each polygon vertex
			this.stroke( 0, 255, 0 );
			for( PolygonData< Object > ptx : this.mesh.pds ) {

				PVector bary = this.getBary( ptx );

				he = ptx.he;
				HalfEdge< PointMassAccum, Object, Object > first = he;

				do {

					this.line( bary.x, bary.y, he.vertexData.data.p.x, he.vertexData.data.p.y );

					he = he.next;

				} while( he != first );

			}


			// Draw splines on last edge added
			if( this.etx != null ) {
				PVector h, ho;
				PVector eh = this.getHalf( this.etx );
				PVector eho = eh.copy( );
				eho.cross( new PVector( 0, 0, 1 ) );
				eho.mult( 0.25f );


				this.stroke( 0, 255, 0 );
				this.line( eh.x + eho.x, eh.y + eho.y, eh.x - eho.x, eh.y - eho.y );


				// HE
				this.stroke( 0, 0, 255 );

				he = this.etx.he;
				phe = he.prev;
				pe = phe.edgeData;
				nhe = he.next;
				ne = nhe.edgeData;
				this.beginShape( );

				h = this.getHalf( pe );
				ho = h.copy( );
				ho.cross( new PVector( 0, 0, 1 ) );
				ho.mult( 0.25f );
				if( pe.he == phe )
					h.add( ho );
				else
					h.sub( ho );

				this.curveVertex( h.x, h.y );
				this.curveVertex( h.x, h.y );

				this.curveVertex( eh.x + eho.x, eh.y + eho.y );

				h = this.getHalf( ne );
				if( ne.he == nhe )
					h.add( ho );
				else
					h.sub( ho );

				this.curveVertex( h.x, h.y );
				this.curveVertex( h.x, h.y );

				this.endShape( );


				// Pair
				this.stroke( 255, 0, 0 );

				he = this.etx.he.pair;
				phe = he.prev;
				pe = phe.edgeData;
				nhe = he.next;
				ne = nhe.edgeData;
				this.beginShape( );

				h = this.getHalf( pe );
				ho = h.copy( );
				ho.cross( new PVector( 0, 0, 1 ) );
				ho.mult( 0.25f );
				if( pe.he == phe )
					h.add( ho );
				else
					h.sub( ho );

				this.curveVertex( h.x, h.y );
				this.curveVertex( h.x, h.y );

				this.curveVertex( eh.x - eho.x, eh.y - eho.y );

				h = this.getHalf( ne );
				ho = h.copy( );
				ho.cross( new PVector( 0, 0, 1 ) );
				ho.mult( 0.25f );
				if( ne.he == nhe )
					h.add( ho );
				else
					h.sub( ho );

				this.curveVertex( h.x, h.y );
				this.curveVertex( h.x, h.y );

				this.endShape( );

			}

		} else {

			this.stroke( 0 );
			this.fill( 255, 0, 0 );
			if( this.iA != null )
				this.ellipse( this.iA.x, this.iA.y, 5, 5 );

		}

	}

	@Override
	public void mouseClicked( ) {

		PVector m = new PVector( this.mouseX, this.mouseY );

		if( this.mouseButton == PConstants.LEFT ) {

			// Only if not initd
			if( !this.initd ) {

				if( this.iA == null ) {
					this.iA = m;
				} else {
					this.mesh.init( new PointMassAccum( this.iA, 1 ), new PointMassAccum( m, 1 ) );
					this.initd = true;
				}

			} else {

				// Find closest Edge
				EdgeData< Object > closest = null;
				float d = Float.POSITIVE_INFINITY;

				for( EdgeData< Object > etx : this.mesh.eds ) {

					PVector epv = new PVector( );
					epv.set( ( (PointMassAccum) etx.he.vertexData.data ).p );
					epv.add( ( (PointMassAccum) etx.he.pair.vertexData.data ).p );
					epv.mult( 0.5f );
					float dd = PVector.dist( this.getHalf( etx ), m );

					if( dd < d ) {

						d = dd;
						closest = etx;

					}

				}

				this.mesh.growEdge( closest, new PointMassAccum( m, 1 ) );

			}

		}

	}

}
