class Circle {
  
  public PVector pos;
  private PVector dir;
  
  public float rad;
  
  private int N_DRAW = 4;
  
  public boolean collide( Circle tgt ) {
   return this.pos.dist( tgt.pos ) < this.rad + tgt.rad;
  }
  
  // TODO: Prob dist between circles should be skewed towards the higher strength
  private float strength;
  
  public void update( ) {
    
    this.pos.add( this.dir );
    
  }
  
  public void draw( Painter p, Circle tgt ) {
    
    float alpha = this.pos.dist( tgt.pos ) * 2 / ( this.rad + tgt.rad );
    
    for( int n = 0; n < N_DRAW; ++n )
      p.pLine( this.pos, tgt.pos, alpha );
    
  }
  
}