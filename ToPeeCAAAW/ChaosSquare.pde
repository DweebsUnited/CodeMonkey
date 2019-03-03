import java.util.Random;

class ChaosSquare {

  private List<PVector> anchors = new ArrayList<PVector>( );
  private ArrayList<PVector> points = new ArrayList<PVector>( );

  public PVector min, max;

  private float aRad;

  private int opa = 255;
  public boolean isBase;
  private color c;

  private Random rng = new Random( );

  public ChaosSquare( PVector min, PVector max ) {

    this( min, max, 16 );
    
  }

  public ChaosSquare( PVector min, PVector max, int nRounds ) {

    this( min, max, 1.0 / 32.0, nRounds );
    
  }

  public ChaosSquare( PVector min, PVector max, float aRad, int nRounds ) {

    this( min, max, aRad, 1.0 / 8.0, nRounds );
    
  }

  public ChaosSquare( PVector min, PVector max, float aRad, float frac, int nRounds ) {

    this.aRad = aRad;

    this.anchors.add( new PVector(       frac, frac ) );
    this.anchors.add( new PVector( 1.0 - frac, frac ) );
    this.anchors.add( new PVector( 1.0 - frac, 1.0 - frac ) );
    this.anchors.add( new PVector(       frac, 1.0 - frac ) );

    int adx;
    for( adx = 0; adx < 4; ++adx )
      this.anchors.get( adx ).add( this.sample( 2.0 ) );

    this.min = min.copy( );
    this.max = max.copy( );
    
    float rf = this.rng.nextFloat( );
    if( rf < 3.0 / 25 ) {
      
      //this.c = color( 137, 207, 240, this.opa );
      this.c = color( #F3B61F );
      this.isBase = false;
      
    } else if( rf < 6.0 / 25 ) {
      
      this.c = color( #8EC1BD );
      this.isBase = false;
      
    } else {
      
      this.c = color( #848484 );
      this.isBase = true;
      
    }
    
    // Generate points
    for( int sdx = 0; sdx < nRounds; ++sdx ) { // Rounds
      for( adx = 0; adx < 4; ++adx ) { // Anchors

        int nadx = ( adx + 1 ) % 4;
  
        PVector b = this.anchors.get( nadx ).copy( );
        b.add( this.sample( ) );
  
        // With very low probability, move anchor a large amount
        if ( this.rng.nextFloat( ) < 1.0 / 50 )
          this.anchors.get( nadx ).add( this.sample( 4.0 ) );
  
        points.add( b );
        
      }
    }

  }

  public void draw( PGraphics canvas ) {

    canvas.noFill( );
    canvas.stroke( this.c );
    canvas.strokeWeight( 5 );

    PVector cA = this.anchors.get( 0 );
    for( PVector pt : this.points ) {
      
      canvas.line(
        sX( cA.x ), sY( cA.y ), 
        sX( pt.x ), sY( pt.y )
        );

      cA = pt;
      
    }
    
  }
  
  public void write( BufferedWriter writer ) throws IOException {
    
    PVector origin = this.anchors.get( 0 );
    writer.append( "N," + Float.toString( sX( origin.x ) ) + "," + Float.toString( sY( origin.y ) ) + "\n" );
    
    for( PVector pt : this.points ) {
      
      writer.append( Float.toString( sX( pt.x ) ) + "," + Float.toString( sY( pt.y ) ) + "\n" );
      
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
