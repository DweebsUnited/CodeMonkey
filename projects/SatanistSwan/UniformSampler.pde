class UniformSampler implements FloatSampler {
  
  private Random rng;
  
  private float min, max;
  
  public UniformSampler( Random rng, float min, float max ) {
    
    this.rng = rng;
    
  }
  
  public float sample( float x, float y ) {
    
    return rng.nextFloat( ) * ( this.max - this.min ) + this.min;
    
  }
  
}