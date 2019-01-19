package CodeMonkey.spatial.HalfEdge;

public class VertexData<VD> {

  public VD data;
  public HalfEdge he;

  public VertexData( ) { }

  public VertexData( VD data ) {

    this.data = data;

  }

}
