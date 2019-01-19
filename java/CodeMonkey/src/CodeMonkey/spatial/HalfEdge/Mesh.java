package CodeMonkey.spatial.HalfEdge;

import java.util.ArrayList;

import CodeMonkey.utility.Factory;
import CodeMonkey.utility.Pair;
import CodeMonkey.utility.Triple;

public class Mesh<VD, ED, PD> {

  ArrayList<VertexData<VD>> vertices;
  ArrayList<EdgeData<ED>> edges;
  ArrayList<PolygonData<PD>> polygons;

  Factory<ED> ef;

  public Mesh( Factory<ED> ef ) {

    this.vertices = new ArrayList<VertexData<VD>>( );
    this.edges    = new ArrayList<EdgeData<ED>>( );
    this.polygons = new ArrayList<PolygonData<PD>>( );

    this.ef = ef;

  }

  public int init( VD seed ) {

    int adx = this.newVertex( new VertexData<VD>( seed ) );

    return adx;

  }

  public Triple<Integer> init( VD seedA, VD seedB ) {

    int adx = this.newVertex( new VertexData<VD>( seedA ) );
    Pair<Integer> rdx = this.exVertex( seedB );

    return new Triple<Integer>( adx, rdx.a, rdx.b );

  }

  private int newVertex( VertexData<VD> vtx ) {

    this.vertices.add( vtx );
    return this.vertices.size( ) - 1;

  }

  public void exVertex( int vdx, VD vtx ) {

    int ndx = this.newVertex( new VertexData( vtx ) );

    // We make one new edge - two new
    HalfEdge f = new HalfEdge( );
    HalfEdge b = new HalfEdge( );

    // Connect the half edges
    f.pair = b;
    b.pair = f;

    f.next = b;
    b.prev = f;

    VertexData<VD> orig = this.vertices.get( vdx );

    HalfEdge origN = orig.he;

    // Insert in loop
    f.prev = origN.prev;
    origN.prev.next = f;
    b.next = origN;
    origN.prev = b;

    //    throw new RuntimeException( "Not yet implemented" );

  }

}
