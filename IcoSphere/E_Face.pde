class Face<P_V, P_HE, P_E, P_F> implements Iterable<HalfEdge<P_V, P_HE, P_E, P_F>> {
  
  private HalfEdge<P_V, P_HE, P_E, P_F> _half;
  public HalfEdge<P_V, P_HE, P_E, P_F> half( ) { return this._half; }
  
  public AroundFaceIter aroundFaceIterator( ) { return new Face.AroundFaceIter( this.half( ) ); }
  public class AroundFaceIter implements Iterator<HalfEdge<P_V, P_HE, P_E, P_F>> {
      
    HalfEdge<P_V, P_HE, P_E, P_F> he_s, he_t, he_n;
    
    public AroundFaceIter( HalfEdge<P_V, P_HE, P_E, P_F> he ) {
      
      he_s = he;
      he_t = he_s;
      he_n = he_t.next( );
      
    }
  
    public boolean hasNext( ) {
      
      return this.he_s != this.he_n;
      
    }
    
    public HalfEdge<P_V, P_HE, P_E, P_F> next( ) {
      
      this.he_t = this.he_n;
      this.he_n = this.he_t.next( );
      return this.he_t;
      
    }
    
  }
  public aroundFaceIter iterator( ) { return this.aroundFaceIterator( ); }
  
}
