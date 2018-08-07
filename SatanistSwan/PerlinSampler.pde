class PerlinSampler implements FloatSampler {
  
  private float min, max;
  private float xoff, yoff;
  private float scale;
  
  public PerlinSampler( float min, float max ) {
    
    this( min, max, 0.0 );
    
  }
  
  public PerlinSampler( float min, float max, float off ) {
    
    this( min, max, off, off, 1.0 );
    
  }
  
  public PerlinSampler( float min, float max, float off, float scale ) {
    
    this( min, max, off, off, scale );
    
  }
  
  public PerlinSampler( float min, float max, float xoff, float yoff, float scale ) {
    
    this.min = min;
    this.max = max;
    
    this.xoff = xoff;
    this.yoff = yoff;
    
    this.scale = scale;
    
  }
  
  public float sample( float x, float y ) {
    
    return noise( x * this.scale + this.xoff, y * this.scale + this.yoff ) * ( this.max - this.min ) + this.min;
    
  }
  
}
