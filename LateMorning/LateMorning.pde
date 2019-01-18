// Basic perlin noise viewer
// Base for other things

float C_SCALE = 0.01f;

void setup( ) {
  
  size( 720, 640 );
  
  System.out.println( String.format( "X Span: %f", pixelWidth * C_SCALE ) );
  System.out.println( String.format( "Y Span: %f", pixelHeight * C_SCALE ) );
  
  noiseSeed( System.currentTimeMillis( ) );
  noiseDetail( 12, 0.5 );
  
  loadPixels( );
  
  int mV = 255;
  int MV = 0;
  
  for( int idx = 0; idx < pixelHeight; ++idx ) {
    for( int jdx = 0; jdx < pixelWidth; ++jdx ) {
      
      int nV = int( noise( idx * C_SCALE, jdx * C_SCALE ) * 255 );
      
      if( nV > MV )
        MV = nV;
      if( nV < mV )
        mV = nV;
      
      pixels[ idx * pixelWidth + jdx ] = color( nV, nV, nV );
      
    }
  }
  
  for( int idx = 0; idx < pixelHeight; ++idx ) {
    for( int jdx = 0; jdx < pixelWidth; ++jdx ) {
      
      float nV = red( pixels[ idx * pixelWidth + jdx ] );
      
      nV = ( nV - mV ) / ( MV - mV ) * 255;
      
      pixels[ idx * pixelWidth + jdx ] = color( nV, nV, nV );
      
    }
  }
  
  updatePixels( );
  
  System.out.format( "Min: %d, Max: %d\n", mV, MV );
  
  noLoop( );
  
}

void draw( ) {
  
  
  
}
