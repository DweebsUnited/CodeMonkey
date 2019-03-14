package CodeMonkey.spatial.HalfEdge;

import java.util.ArrayList;

import CodeMonkey.utility.TripleT;


public class Mesh< VD, ED, PD > {

	public ArrayList< VertexData< VD > > vds;
	public ArrayList< EdgeData< ED > > eds;
	public ArrayList< PolygonData< PD > > pds;

	private boolean initd = false;

	public Mesh( ) {

		this.vds = new ArrayList< VertexData< VD > >( );
		this.eds = new ArrayList< EdgeData< ED > >( );
		this.pds = new ArrayList< PolygonData< PD > >( );

	}

	private VertexData< VD > newV( ) {

		VertexData< VD > v = new VertexData< VD >( );
		this.vds.add( v );
		return v;

	}

	public VertexData< VD > newV( VD data ) {

		VertexData< VD > v = new VertexData< VD >( data );
		this.vds.add( v );
		return v;

	}

	public EdgeData< ED > newE( VertexData< VD > vsx, VertexData< VD > vtx ) {

		EdgeData< ED > e = new EdgeData< ED >( );
		this.eds.add( e );

		HalfEdge< VD, ED, PD > f = new HalfEdge< VD, ED, PD >( );
		HalfEdge< VD, ED, PD > b = new HalfEdge< VD, ED, PD >( );

		e.he = f;

		if( !vsx.isolated( ) )
			vsx.he = f;
		if( !vtx.isolated( ) )
			vtx.he = b;

		f.next = b;
		f.prev = b;
		f.pair = b;
		f.vertexData = vsx;
		f.edgeData = e;

		b.next = f;
		b.prev = f;
		b.pair = f;
		b.vertexData = vtx;
		b.edgeData = e;

		return e;

	}

	public PolygonData< PD > newP( ) {

		PolygonData< PD > p = new PolygonData< PD >( );
		this.pds.add( p );
		return p;

	}

	public EdgeData< ED > init( VD vxa, VD vxb ) {

		VertexData< VD > vsx = this.newV( vxa );
		VertexData< VD > vtx = this.newV( vxb );

		this.initd = true;
		return this.newE( vsx, vtx );

	}

	public TripleT< EdgeData< ED >, EdgeData< ED >, PolygonData< PD > > growEdge( EdgeData< ED > e, VD v ) {

		if( !this.initd )
			throw new RuntimeException( "Must init mesh first" );

		return this.growEdge( e, this.newV( v ) );

	}

	public TripleT< EdgeData< ED >, EdgeData< ED >, PolygonData< PD > > growEdge( EdgeData< ED > e,
			VertexData< VD > v ) {

		if( !this.initd )
			throw new RuntimeException( "Must init mesh first" );

		EdgeData< ED > ea, eb;

		// Find free edge
		HalfEdge< VD, ED, PD > he = e.he;
		if( he.free( ) ) {

			// F requires these edges
			ea = this.newE( e.he.pair.vertexData, v );
			eb = this.newE( v, e.he.vertexData );

		} else {

			he = he.pair;
			if( !he.free( ) )
				throw new RuntimeException( "Can't grow non-free edge" );

			// B requires these
			ea = this.newE( e.he.vertexData, v );
			eb = this.newE( v, e.he.pair.vertexData );

		}

		// Link he together
		ea.he.next = eb.he;
		eb.he.prev = ea.he;

		ea.he.pair.prev = eb.he.pair;
		eb.he.pair.next = ea.he.pair;

		he.next = ea.he;
		ea.he.prev = he;

		he.prev = eb.he;
		eb.he.next = he;

		// Now the polygon
		PolygonData< PD > p = this.newP( );

		he.polygonData = p;
		ea.he.polygonData = p;
		eb.he.polygonData = p;

		p.he = he;

		return new TripleT< EdgeData< ED >, EdgeData< ED >, PolygonData< PD > >( ea, eb, p );

	}

}
