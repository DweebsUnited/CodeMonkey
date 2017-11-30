class RandomPositionFactory implements PositionFactory {
  
  private RandomWrapper rng;
  
  public RandomPositionFactory( RandomWrapper rng ) {
    
    this.rng = rng;
    
  }
  
  public PVector make( ) {
    
    return this.rng.nextPos( );
    
  }
  
}