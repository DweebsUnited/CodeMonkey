class Vertex<D> {
  
  D payload;
  
}

class Edge<D> {
  
  D payload;
  
}

class Face<D> {
  
  D payload;
  
}

class HalfEdge<V, E, F> {
  
  HalfEdge n, p, twin;
  
  Vertex<V> o;
  Edge<E> e;
  Face<F> f;
  
  public HalfEdge nextAroundVertex( ) {
    
    return this.twin.n;
    
  }
  
}
