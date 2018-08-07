class Quad {
  
  public int top;
  public int left;
  public int height;
  public int width;
  public double area;
  
}

PImage reachableMask;
PImage tensionMask;
PImage precisionMask;
Quad largestRect;

public enum PixelStatus {

  // Pixel is good
  Good, 

  // Pixel is unreachable due to string length 
  FarFromLeft, 
  FarFromRight, 
  FarFromBoth, 

  // Tension imbalances
  TightLeft, 
  TightRight, 
  LooseLeft, 
  LooseRight, 

  // Precision too low
  NEPrecisionLeft,
  NEPrecisionRight,
  NEPrecisionBoth,
  
}

Quad findLargestRect( ) {
  
  Quad qArea = new Quad( );
  
  int midX = pixelWidth / 2;
  int minY;
  for(
    minY = 0;
    reachableMask.pixels[ midX + minY * pixelWidth ] != color( 0 ) || tensionMask.pixels[ midX + minY * pixelWidth ] != color( 0 ) || precisionMask.pixels[ midX + minY * pixelWidth ] != color( 0 );
    ++minY );
  
  qArea.top = minY;
  
  double maxArea = 0;
  
  noFill( );
  stroke( 127, 255 );
  
  for( int maxY = minY + 1; maxY < pixelHeight; ++maxY ) {
    
    int minX;
    for(
      minX = midX;
      minX > 0 &&
        ( reachableMask.pixels[ minX + maxY * pixelWidth ] == color( 0 ) &&
        tensionMask.pixels[ minX + maxY * pixelWidth ] == color( 0 ) &&
        precisionMask.pixels[ minX + maxY * pixelWidth ] == color( 0 ) );
      --minX );
    
    int maxX = pixelWidth - minX;
    
    line( minX, minY, maxX, maxY );
    
    double dx = xMax * ( ( pixelWidth - minX ) - minX ) / (double)pixelWidth;
    double dy = yMax * ( maxY - minY )                  / (double)pixelHeight;
    
    double area = dx * dy;
    
    if( area > maxArea ) {
      
      qArea.height = maxY - minY;
      qArea.left = minX;
      qArea.width = ( pixelWidth - minX ) - minX;
      qArea.area = area;
      
      maxArea = area;
      
    }
    
  }
  
  return qArea;
  
}

void runEval( ) {
  
  reachableMask = createImage( pixelWidth, pixelHeight, ALPHA );
  tensionMask = createImage( pixelWidth, pixelHeight, ALPHA );
  precisionMask = createImage( pixelWidth, pixelHeight, ALPHA );

  reachableMask.loadPixels( );
  tensionMask.loadPixels( );
  precisionMask.loadPixels( );

  for ( int ydx = 0; ydx < pixelHeight; ++ydx ) {

    for ( int xdx = 0; xdx < pixelWidth; ++xdx ) {

      double x = xMax * (double)xdx / pixelWidth;
      double y = yMax * (double)ydx / pixelHeight;

      // First check, can we reach the pixel from either arm
      PixelStatus sD = evalPixelDistance( x, y );

      // Second check, is one of the strings too tight or too loose
      PixelStatus sT = evalPixelTension( x, y );
      
      // Final check, will movement by one step on either motor be within tolerances?
      PixelStatus sP = evalPixelPrecision( x, y );

      color reachableColor = color( 0 );
      color tensionColor = color( 0 );
      color precisionColor = color( 0 );

      if ( sD != PixelStatus.Good ) {
        reachableColor = color( 255 );
      }
      if ( sT != PixelStatus.Good ) {
        tensionColor = color( 255 );
      }
      if ( sP != PixelStatus.Good ) {
        precisionColor = color( 255 );
      }

      reachableMask.pixels[ xdx + ydx * pixelWidth ] = reachableColor;
      tensionMask.pixels[   xdx + ydx * pixelWidth ] = tensionColor;
      precisionMask.pixels[ xdx + ydx * pixelWidth ] = precisionColor;
      
    }
    
  }

  reachableMask.updatePixels( );
  tensionMask.updatePixels( );
  precisionMask.updatePixels( );
  
  largestRect = findLargestRect( );
  
}

void drawEval( ) {
  
  tint( 255 );
  image( precisionMask, 0, 0 );
  
  tint( 255 );
  image( tensionMask, 0, 0 );
  
  tint( 255 );
  image( reachableMask, 0, 0 );
  
  noStroke( );
  fill( 127, 127, 127 );
  rect( (float)largestRect.left, (float)largestRect.top, (float)largestRect.width, (float)largestRect.height );
  
}