interface Painter {
  
  public void setRNG( RandomWrapper rng );
  
  public void pLine( PVector p1, PVector p2, float alpha );
  public void pPoint( PVector p );
  
}