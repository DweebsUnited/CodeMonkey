class SandPainter implements Painter {
  
  final int N_PTS = 32;
  
  final float P_RAD = 1;
  
  RandomWrapper rng;
  
  public void setRNG( RandomWrapper rng ) {
  
    this.rng = rng;
  
  }
  
  public void pLine( PVector p1, PVector p2, float alpha ) {
    
    alpha /= N_PTS;
    
    // Set up to draw a sand line
    noStroke( );
    fill( 255, alpha * 255 );
    
    for( int i = 0; i < this.N_PTS; ++i ) {
      
      float a = this.rng.nextFloat( );
          
      PVector p = PVector.lerp( p1, p2, a );
      ellipse( p.x, p.y, this.P_RAD, this.P_RAD );
    
    }
    
  }
  
  public void pPoint( PVector p ) {
    
    
    
  }
  
}