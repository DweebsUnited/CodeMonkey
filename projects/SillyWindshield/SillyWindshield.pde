void setup( ) {
  
  size( 1280, 720 );
  background( 255 );
  
  loadPixels( );
  
  for( int ydx = 0; ydx < pixelHeight; ++ydx ) {
    
    for( int xdx = 0; xdx < pixelWidth; ++xdx ) {
      
      float nX = xdx * 0.001;
      float nY = ydx * 0.001;
      
      PVector nVec = new PVector( noise( nX, nY, 0.0 ), noise( nX, nY, 1.0 ) );
      
      float nHeight = nVec.x;
      int nMag = Math.round( nHeight / 10 );
      
      if( Math.abs( nHeight - nMag * 10 ) < 0.000001 )
        pixels[ ydx * pixelWidth + xdx ] = color( 0 );
      
    }
    
  }
  
  updatePixels( );
  
  noLoop( );
  
}