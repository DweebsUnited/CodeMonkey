class CirclePositionFactory implements PositionFactory {
  
  private RandomWrapper rng;
  
  public CirclePositionFactory( RandomWrapper rng ) {
    
    this.rng = rng;
    
  }
  
  public PVector make( ) {
    
    return this.rng.nextRota( 1, 1 );
    
  }
  
}