class SandPainter {
  
  final int N_PTS;
  
  final float P_RAD;
  
  RandomWrapper rng;
  
  public SandPainter( RandomWrapper rng, int n_pts, float p_rad ) {
  
    this.rng = rng;
    this.N_PTS = n_pts;
    this.P_RAD = p_rad;
  
  }
  
  public void pLine( Circle p1, Circle p2, FourseasonsColorFactory fcf, float alpha ) {
    
    alpha /= N_PTS * 2;
    
    noStroke( );
    
    for( int i = 0; i < this.N_PTS; ++i ) {
      
      float a = this.rng.nextFloat( );
      while( a / 2 + 0.25 < this.rng.nextFloat( ) )
        a = this.rng.nextFloat( );
      
      color c = lerpColor( fcf.getCol( p1 ), fcf.getCol( p2 ), a );
      
      fill( c, alpha * 255 );
          
      PVector p = PVector.lerp( p1.pos, p2.pos, a );
      ellipse( p.x, p.y, this.P_RAD, this.P_RAD );
    
    }
    
  }
  
}