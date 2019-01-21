package CodeMonkey.spatial.HalfEdge;

public class EdgeData<ED> {

  public ED data;
  public HalfEdge he;

  public EdgeData( ) { }

  public EdgeData( ED data ) {

    this.data = data;

  }

}
