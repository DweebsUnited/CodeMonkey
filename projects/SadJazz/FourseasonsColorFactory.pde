class FourseasonsColorFactory {
  
  private SummerColorFactory summer;
  private AutumnColorFactory autumn;
  private WinterColorFactory winter;
  private SpringColorFactory spring;
  
  final private PVector orig;
  final private int cOff;
  
  public FourseasonsColorFactory( RandomWrapper rng ) {
    
    this.summer = new SummerColorFactory( rng );
    this.autumn = new AutumnColorFactory( rng );
    this.winter = new WinterColorFactory( rng );
    this.spring = new SpringColorFactory( rng );
    
    this.orig = rng.nextRota( 3, 1 );
    this.cOff = rng.nextInt( 4 );
    
  }
  
  public color[] make( ) {
    
    color[] c = new color[ 4 ];
    c[ 0 ] = this.summer.make( );
    c[ 1 ] = this.autumn.make( );
    c[ 2 ] = this.winter.make( );
    c[ 3 ] = this.spring.make( );
    
    return c;
    
  }
  
  public color getCol( Circle c ) {
    
    float ang = atan2( c.pos.y - this.orig.y, c.pos.x - this.orig.x ) + PI;
    
    int idx;
    
    if( ang < PI / 2 )
      idx = 0;
    else if( ang < PI )
      idx = 1;
    else if( ang < 3 * PI / 2 )
      idx = 2;
    else
      idx = 3;
      
    return c.c[ ( idx + this.cOff ) % 4 ];
    
  }
  
}