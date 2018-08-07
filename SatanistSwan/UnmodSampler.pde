class UnmodSampler implements PVectorSampler {
  
  public UnmodSampler( ) { }
  
  public PVector sample( PVector c ) {
    
    return c.copy( );
    
  }
  
}