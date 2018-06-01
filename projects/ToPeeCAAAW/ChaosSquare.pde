import java.util.Random;

class ChaosSquare {
  
  private List<PVector> anchors = new ArrayList<PVector>( );
  private int adx;
  private PVector cA;
  
  private PVector min, max;
  
  private float aRad;
  
  private int opa = 255;
  private color c;
  
  private Random rng = new Random( );
  
  public ChaosSquare( PVector min, PVector max ) {
    
    this( min, max, 1.0 / 32.0, 1.0 / 8.0 );
    
  }
  
  public ChaosSquare( PVector min, PVector max, float aRad ) {
    
    this( min, max, aRad, 1.0 / 8.0 );
    
  }
  
  public ChaosSquare( PVector min, PVector max, float aRad, float frac ) {
    
    this.aRad = aRad;
    
    this.anchors.add( new PVector(       frac,       frac ) );
    this.anchors.add( new PVector( 1.0 - frac,       frac ) );
    this.anchors.add( new PVector( 1.0 - frac, 1.0 - frac ) );
    this.anchors.add( new PVector(       frac, 1.0 - frac ) );
    
    for( this.adx = 0; this.adx < 4; ++this.adx )
      this.anchors.get( adx ).add( this.sample( 2.0 ) );
    
    this.adx = 0;
    this.cA = this.anchors.get( adx );
    
    this.min = min.copy( );
    this.max = max.copy( );
    
    this.c = ( this.rng.nextFloat( ) < 2.0 / 25 ) ? color( 255, 0, 0, this.opa ) : color( 0, this.opa );
    
  }
  
  public void draw( ) {
    
    noFill( );
    stroke( this.c );
    
    for( int sdx = 0; sdx < 4; ++sdx ) {
    
      int nadx = ( adx + 1 ) % 4;
      
      PVector b = this.anchors.get( nadx ).copy( );
      b.add( this.sample( ) );
      
      // With very low probability, move anchor a large amount
      if( this.rng.nextFloat( ) < 1.0 / 50 )
        this.anchors.get( nadx ).add( this.sample( 4.0 ) );
      
      line(
        sX( this.cA.x ), sY( this.cA.y ),
        sX( b.x ), sY( b.y )
      );
      
      adx = nadx;
      this.cA = b;
    
    }
    
  }
  
  private float sX( float x ) {
    return ( this.max.x - this.min.x ) * x + this.min.x;
  }
  
  private float sY( float y ) {
    return ( this.max.y - this.min.y ) * y + this.min.y;
  }
  
  private PVector sample( ) {
    return new PVector( (float)( this.rng.nextGaussian( ) / 3.0 * aRad ), (float)( this.rng.nextGaussian( ) / 3.0 * aRad ) );
  }
  
  private PVector sample( float scale ) {
    PVector s = this.sample( );
    s.mult( scale );
    return s;
  }
  
}