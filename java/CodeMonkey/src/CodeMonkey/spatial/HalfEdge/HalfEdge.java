package CodeMonkey.spatial.HalfEdge;

public class HalfEdge<VD, ED, PD> {

  HalfEdge next;
  HalfEdge prev;
  HalfEdge pair;

  VertexData<VD> vertexData;
  EdgeData<ED> edgeData;
  PolygonData<PD> polygonData;

  public boolean free( ) {

    return this.pair.polygonData == null;

  }

}
