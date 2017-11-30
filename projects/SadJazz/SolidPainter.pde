class SolidPainter implements Painter {
  
  public void setRNG( RandomWrapper rng ) { }
  
  public void pLine( PVector p1, PVector p2, float alpha ) {
    
    noFill( );
    stroke( 255, alpha * 255 );
    line( p1.x, p1.y, p2.x, p2.y );
    
  }
  
  public void pPoint( PVector p ) {
    
    fill( 255 );
    noStroke( );
    ellipse( p.x, p.y, 1, 1 );
    
  }
  
}