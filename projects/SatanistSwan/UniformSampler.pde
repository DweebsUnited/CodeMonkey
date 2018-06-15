class UniformSampler implements FloatSampler {
  
  private Random rng;
  
  private float min, max;
  
  public UniformSampler( float min, float max ) {
    
    this.rng = new Random( );
    
    this.min = min;
    this.max = max;
    
  }
  
  public float sample( float x, float y ) {
    
    return rng.nextFloat( ) * ( this.max - this.min ) + this.min;
    
  }
  
}
