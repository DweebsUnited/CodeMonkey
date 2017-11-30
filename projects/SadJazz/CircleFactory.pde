class CircleFactory {
  
  float spd_min;
  float spd_rng;
  
  float rad_min;
  float rad_rng;
  
  public CircleFactory( float spd_min, float spd_rng, float rad_min, float rad_rng ) {
    
    this.spd_min = spd_min;
    this.spd_rng = spd_rng;
  
    this.rad_min = rad_min;
    this.rad_rng = rad_rng;
   
    
  }
   
  
  public Circle make( RandomWrapper rng, PositionFactory pf ) {
    
    Circle c = new Circle( );
    
    c.pos = pf.make( );
    c.dir = rng.minMaxVec( this.spd_rng, this.spd_min );
    c.rad = rng.minMax( this.rad_min, this.rad_min );
    
    return c;
    
  }
  
}