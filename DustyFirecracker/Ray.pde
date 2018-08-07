class Ray {
  
  public PVector o;
  public PVector d;
  
  public Ray( PVector o, PVector d ) {
    
    this.o = o.copy( );
    this.d = d.copy( );
    
  }
  
}