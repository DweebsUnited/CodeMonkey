package CodeMonkey.spatial.HalfEdge;

import java.util.ArrayList;

import CodeMonkey.utility.PairT;
import CodeMonkey.utility.TripleT;

public class Mesh<VD, ED, PD> {

  public ArrayList<VertexData<VD>> vds;
  public ArrayList<EdgeData<ED>> eds;
  public ArrayList<PolygonData<PD>> pds;

  public Mesh( ) {

    this.vds = new ArrayList<VertexData<VD>>( );
    this.eds = new ArrayList<EdgeData<ED>>( );
    this.pds = new ArrayList<PolygonData<PD>>( );

  }

  private VertexData<VD> newV( ) {

    VertexData<VD> v = new VertexData<VD>( );
    this.vds.add( v );
    return v;

  }
  private VertexData<VD> newV( VD data ) {

    VertexData<VD> v = new VertexData<VD>( data );
    this.vds.add( v );
    return v;

  }
  private EdgeData<ED> newE( ) {

    EdgeData<ED> e = new EdgeData<ED>( );
    this.eds.add( e );
    return e;

  }
  private EdgeData<ED> newE( ED data ) {

    EdgeData<ED> e = new EdgeData<ED>( data );
    this.eds.add( e );
    return e;

  }
  private PolygonData<PD> newP( ) {

    PolygonData<PD> p = new PolygonData<PD>( );
    this.pds.add( p );
    return p;

  }
  private PolygonData<PD> newP( PD data ) {

    PolygonData<PD> p = new PolygonData<PD>( data );
    this.pds.add( p );
    return p;

  }

  public VertexData<VD> init( VD seed ) {

    VertexData<VD> vd = this.newV( seed );
    return vd;

  }

  public EdgeData<ED> init( VD seedA, VD seedB ) {

    return this.connect( this.newV( seedA ), this.newV( seedB ) ).a;

  }

  public TripleT<VertexData<VD>, EdgeData<ED>, PolygonData<PD>> connect( VertexData<VD> vsx, VD vdx ) {

    VertexData<VD> vtx = this.newV( vdx );
    PairT<EdgeData<ED>, PolygonData<PD>> inter = this.connect( vsx, vtx );

    return new TripleT<VertexData<VD>, EdgeData<ED>, PolygonData<PD>>( vtx, inter.a, inter.b );

  }

  public PairT<EdgeData<ED>, PolygonData<PD>> connect( VertexData<VD> vsx, VertexData<VD> vtx ) {

    // We make one new edge - two new HE
    HalfEdge<VD, ED, PD> f = new HalfEdge<VD, ED, PD>( );
    HalfEdge<VD, ED, PD> b = new HalfEdge<VD, ED, PD>( );

    // Connect the HE
    f.pair = b;
    b.pair = f;

    f.next = b;
    f.prev = b;
    b.next = f;
    b.prev = f;

    f.vertexData = vsx;
    b.vertexData = vtx;
    vtx.he = b;

    // Create the edge, connect it to all the HE
    EdgeData<ED> e = this.newE( );
    e.he = f;
    f.edgeData = e;
    b.edgeData = e;


    // There are two ways to connect this edge: Such that it makes a polygon, and with a floating target
    // We check for polygon first, then fall back to edge if none can be made
    PolygonData<PD> p = null;

    // But we can do nothing if it is the first edge
    if( vsx.he != null ) {

      // Check for polygon closure

      // Limited to triangle closure - Find two free edges linking ST
      // Check connected HE around source
      HalfEdge<VD, ED, PD> he = vsx.he;
      HalfEdge<VD, ED, PD> first = he;
      do {

        // Check forwards
        if( he.free( ) ) {

          HalfEdge<VD, ED, PD> fhe = he.next;
          HalfEdge<VD, ED, PD> ffirst = fhe;
          do {

            // Polygon conditions
            if( fhe.free( ) && fhe.next.vertexData == vtx ) {

              // fhe -> he -> f is a polygon
              p = this.newP( );

              // Link fb in
              f.next = fhe;
              f.prev = he;
              b.next = he.next;
              b.prev = fhe.prev;

              // Cut links
              he.next.prev = b;
              he.next = f;
              fhe.prev.next = b;
              fhe.prev = f;

              // Link all three to p
              p.he = f;
              b.polygonData = p;
              he.polygonData = p;
              fhe.polygonData = p;

            }

            fhe = fhe.pair.next;

          } while( p == null && fhe != ffirst );

        }

        // We found a forewards, break early
        if( p != null )
          break;

        // To check backwards, start with he pair
        he = he.pair;

        // Check backwards
        if( he.free( ) ) {

          HalfEdge<VD, ED, PD> fhe = he.prev;
          HalfEdge<VD, ED, PD> ffirst = fhe;
          do {

            // Polygon conditions
            if( fhe.free( ) && fhe.vertexData == vtx ) {

              // he -> fhe -> b is a polygon
              p = this.newP( );

              // Link fb in
              b.next = he;
              b.prev = fhe;
              f.next = fhe.next;
              f.prev = he.prev;

              // Cut links
              he.prev.next = f;
              he.prev = b;
              fhe.next.prev = f;
              fhe.next = b;

              // Link all three to p
              p.he = b;
              b.polygonData = p;
              he.polygonData = p;
              fhe.polygonData = p;

            }

            // Different cycle method due to looking backwards
            fhe = fhe.pair.prev;

          } while( p == null && fhe != ffirst );

        }

        // Undo this so the cycling is not broken
        he = he.pair;

        // Cycle around source
        he = he.prev.pair;

      } while( p == null && he != first );


      // We only look for edge insertion if no polygon was found
      // Find free edge sequence to insert this into
      if( p == null ) {

        // Check connected HE around source
        he = vsx.he;
        first = he;
        do {

          // If both free
          if( he.free( ) && he.prev.free( ) ) {

            // Insert newest loop
            f.prev = he.prev;
            he.prev.next = f;
            b.next = he;
            he.prev = b;

            break;

          }

          // Cycle around source
          he = he.prev.pair;

        } while( he != first );

      }

    }

    // Connect source to HE
    vsx.he = f;

    return new PairT<EdgeData<ED>, PolygonData<PD>>( e, p );

  }

}
