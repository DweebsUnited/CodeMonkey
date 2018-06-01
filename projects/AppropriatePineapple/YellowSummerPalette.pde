class YellowSummerPalette implements ColorFactoryIntf {
  
  private color[] palette = {
    color( 244, 17, 164 ),
    color( 147, 248, 22 ),
    color( 254, 17, 139 ),
    color( 205, 253, 136 ),
    color( 0, 137, 206 ),
    color( 95, 61, 59 ),
    color( 188, 131, 237 ),
    color( 90, 182, 81 ),
    color( 254, 122, 60 ),
    color( 85, 168, 138 ) };
    
  private Random rng = new Random( );
  
  public color make( ) {
    
    return this.palette[ this.rng.nextInt( 10 ) ];
    
  }
  
}