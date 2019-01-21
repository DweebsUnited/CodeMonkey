package CodeMonkey.spatial.HalfEdge;

public class HalfEdge<VD, ED, PD> {

  public HalfEdge<VD, ED, PD> next;
  public HalfEdge<VD, ED, PD> prev;
  public HalfEdge<VD, ED, PD> pair;

  public VertexData<VD> vertexData;
  public EdgeData<ED> edgeData;
  public PolygonData<PD> polygonData;

  public boolean free( ) {

    return this.polygonData == null;

  }

}
