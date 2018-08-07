class Circle {
  
  public PVector pos;
  private PVector dir;
  
  public float rad;
  
  private int N_DRAW = 4;
  
  public color c[];
  
  public boolean dead = false;
  
  private FourseasonsColorFactory fcf;
  
  public boolean collide( Circle tgt ) {
   return this.pos.dist( tgt.pos ) < this.rad + tgt.rad;
  }
  
  public void update( ) {
    
    this.pos.add( this.dir );
    
  }
  
  public void draw( SandPainter p, Circle tgt ) {
    
    float alpha = this.pos.dist( tgt.pos ) * 2 / ( this.rad + tgt.rad );
    
    for( int n = 0; n < N_DRAW; ++n )
      p.pLine( this, tgt, this.fcf, alpha );
    
  }
  
}