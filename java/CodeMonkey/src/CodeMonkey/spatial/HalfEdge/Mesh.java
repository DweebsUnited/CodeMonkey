package CodeMonkey.spatial.HalfEdge;

import java.util.ArrayList;

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

  public VertexData<VD> init( VD seed ) {

    VertexData<VD> vd = this.newV( seed );
    return vd;

  }

  public EdgeData<ED> init( VD seedA, VD seedB ) {

    return this.connect( this.newV( seedA ), this.newV( seedB ) );

  }

  public EdgeData<ED> connect( VertexData<VD> vsx, VertexData<VD> vtx ) {

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

    // Find free edge sequence to insert this into
    if( vsx.he != null ) {

      // Check connected HE around source
      HalfEdge<VD, ED, PD> he = vsx.he;
      HalfEdge<VD, ED, PD> first = he;
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

    // Connect source to HE
    vsx.he = f;

    // Return the new edge
    //    return e;

    // Check for polygon closure
    // Find 2-length edge sequence between the two to create a polygon
    if( vsx.he != null ) {

      // Check connected HE around source
      HalfEdge<VD, ED, PD> he = vsx.he;
      HalfEdge<VD, ED, PD> first = he;
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

}
