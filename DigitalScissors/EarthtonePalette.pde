class EarthtonePalette implements ColorFactoryIntf {
  
  private color[] palette = {
    color( 36, 25, 20 ),
    color( 102, 89, 77 ),
    color( 161, 152, 125 ),
    color( 61, 23, 8 ),
    color( 108, 64, 13 ),
    color( 194, 163, 122 ),
    color( 115, 25, 0 ),
    color( 230, 103, 0 ),
    color( 242, 230, 121 ),
    color( 34, 57, 11 ),
    color( 96, 182, 38 ),
    color( 182, 242, 145 ),
    color( 57, 89, 102 ),
    color( 125, 152, 161 ),
    color( 184, 207, 207 ) };
    
  private Random rng = new Random( );
  
  public color make( ) {
    
    return this.palette[ this.rng.nextInt( 15 ) ];
    
  }
  
}