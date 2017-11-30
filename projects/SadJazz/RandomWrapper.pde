class RandomWrapper {
  
  private Random rng;
  
  public RandomWrapper( ) {
    this.rng = new Random( );
  }
  
  public int nextInt( int n ) {
    return this.rng.nextInt( n );
  }
  
  public float nextFloat( ) {
    return ( this.rng.nextFloat( ) * 2 ) - 1;
  }
  
  public float nextGaussian( ) {
    return (float) this.rng.nextGaussian( );
  }
  
  public boolean yesno( ) {
    return this.rng.nextBoolean( );
  }
  
  public int nextX( ) {
    return this.nextInt( pixelWidth ) + 1;
  }
  
  public int nextY( ) {
    return this.nextInt( pixelHeight ) + 1;
  }
  
  public PVector nextPos( ) {
    return new PVector( this.nextX( ), this.nextY( ) );
  }
  
  public PVector nextRota( float scale, float var ) {
    
    float ang = this.nextFloat( ) * 2 * PI;
    
    int s = min( pixelWidth, pixelHeight );
    float rad = s / scale + this.nextGaussian( ) / var;
    
    return new PVector( rad * cos( ang ) + pixelWidth / 2, rad * sin( ang ) + pixelHeight / 2 );
  
  }
  
  public float minMax( float rng, float min ) {
    return this.nextFloat( ) * rng + min;
  }
  
  public PVector minMaxVec( float rng, float min ) {
    return new PVector( this.nextFloat( ) * rng + min, this.nextFloat( ) * rng + min );
  }
  
}