class BlackYellowPalette implements ColorFactoryIntf {
  
  private color[] palette = {
    #eae3d9,
    #f4c760,
    #e89c68,
    #df877b,
    #d9637d };
    
  private Random rng = new Random( );
  
  public color make( ) {
    
    return this.palette[ this.rng.nextInt( 5 ) ];
    
  }
  
}