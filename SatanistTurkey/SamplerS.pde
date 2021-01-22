class PerlinSampler {
  
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

class GaussianSampler {
  
  private Random rng;
  
  private float o, u;
  
  public GaussianSampler( float o, float u ) {
    
    this.rng = new Random( );
    
    this.o = o;
    this.u = u;
    
  }
  
  public float sample( ) {
    
    return (float)rng.nextGaussian( ) * this.u + this.o;
    
  }
  
}

class UniformSampler {
  
  private Random rng;
  
  private float min, max;
  
  public UniformSampler( float min, float max ) {
    
    this.rng = new Random( );
    
    this.min = min;
    this.max = max;
    
  }
  
  public float sample( ) {
    
    return rng.nextFloat( ) * ( this.max - this.min ) + this.min;
    
  }
  
}
