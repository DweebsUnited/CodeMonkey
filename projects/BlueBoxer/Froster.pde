class Froster {
  
  private final int CR_DENS = 5;
  private final int CR_MIN = CR_DENS * 2;
  private final int CR_MAX = CR_MIN * 4;
  private final int CR_RNG = CR_MAX - CR_MIN;
  
  public PImage frost( Random rng, PImage img ) {
    
    PGraphics canvas = createGraphics( img.width, img.height );
    
    canvas.beginDraw( );
    canvas.image( img, 0, 0, canvas.width, canvas.height );
    
    EllipseAverager avgr = new EllipseAverager( new LinearAccumulatorFactory( ) );
    
    ArrayList<PVector> poisson = poissonDiskSampling( CR_DENS, canvas.width, canvas.height, 30 );
    
    for( PVector c : poisson ) {
      
      int rad = rng.nextInt( this.CR_RNG ) + this.CR_MIN;
      
      canvas.noStroke( );
      canvas.fill( avgr.getAverageColor( round( c.x ), round( c.y ), rad, canvas ) );
      
      canvas.ellipse( c.x, c.y, rad, rad );
      
    }
    
    canvas.endDraw( );
    
    canvas.filter( BLUR, 1 );
    canvas.filter( BLUR, 3 );
    
    return canvas;
    
  }
  
}
