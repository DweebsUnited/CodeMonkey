class PastelPalette implements ColorFactoryIntf {
  
  private color[] palette = {
    #0e0f0f,
    #74726b,
    #caa971,
    #f4e664,
    #fefde5 };
    
  private Random rng = new Random( );
  
  public color make( ) {
    
    return this.palette[ this.rng.nextInt( 5 ) ];
    
  }
  
}