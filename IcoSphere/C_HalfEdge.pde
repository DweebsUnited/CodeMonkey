import java.util.Iterator;

class HalfEdge<P_V, P_HE, P_E, P_F> {
  
  private HalfEdge<P_V, P_HE, P_E, P_F> _next;
  private HalfEdge<P_V, P_HE, P_E, P_F> _prev;
  private HalfEdge<P_V, P_HE, P_E, P_F> _pair;
  
  private Vertex<P_V, P_HE, P_E, P_F> _orig;
  private Edge<P_V, P_HE, P_E, P_F> _edge;
  private Face<P_V, P_HE, P_E, P_F> _left;
  
  public HalfEdge<P_V, P_HE, P_E, P_F> next( ) { return this._next; }
  public HalfEdge<P_V, P_HE, P_E, P_F> prev( ) { return this._prev; }
  public HalfEdge<P_V, P_HE, P_E, P_F> pair( ) { return this._pair; }
  public Vertex<P_V, P_HE, P_E, P_F> orig( ) { return this._orig; }
  public Edge<P_V, P_HE, P_E, P_F> edge( ) { return this._edge; }
  public Face<P_V, P_HE, P_E, P_F> left( ) { return this._left; }
  
  public HalfEdge<P_V, P_HE, P_E, P_F> nextAroundVertex( ) { return this.pair( ).next( ); }
  public HalfEdge<P_V, P_HE, P_E, P_F> prevAroundVertex( ) { return this.prev( ).pair( ); }
  
  public boolean free( ) { return this.left( ) == null; }
  
}
