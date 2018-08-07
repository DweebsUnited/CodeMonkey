class SandPainter implements PainterIntf {
  
  private int ptsPer;
  private float ptsUnit;
  
  private int rad;
  private int opa;
  
  private Random rng = new Random( );
  
  public SandPainter( int pts, float unit, int rad, int opa ) {
    
    this.ptsPer = pts;
    this.ptsUnit = unit;
    
    this.rad = rad;
    this.opa = opa;
    
  }
  
  public void draw( PVector s, PVector f, color sc, color fc ) {
    
    noStroke( );
    
    float dist = s.dist( f );
    int numPts = int( dist / this.ptsUnit * this.ptsPer );
    
    for( int p = 0; p < numPts; ++p ) {
      
      float t = rng.nextFloat( );
      // 0.8 - pow( 1.5 * t - 0.75, 2 )
      // 2 * abs( 0.5 - t )
      while( rng.nextFloat( ) > 2 * abs( 0.5 - t ) ) { t = rng.nextFloat( ); };
      
      color col = ( lerpColor( sc, fc, t ) &0xffffff ) | ( this.opa << 24 );
      fill( col );
      
      ellipse( lerp( s.x, f.x, t ), lerp( s.y, f.y, t ), this.rad, this.rad );
      
    }
    
  }
  
  public void drawFin( ) { };
  
}