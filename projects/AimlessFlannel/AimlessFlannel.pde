// Note: All units except pixels are in meters

// Motors are at the corners
// Mapping pixels to coordinates:
final double xMax = 2.25; // Distance between the two motors

// Max length of either arm
final double maxArmLength = 2.25;

// yMax = height of isoceles triangle with xMax base and maxArmLength arms
// This positions the bottom of the screen to be at the bottom of the reachable area
final double yMax = Math.sqrt( maxArmLength * maxArmLength - 0.25 * xMax * xMax );

// Max and min tensions in each line, as a function of gantry mass ( assumed to be 1 )
final double minTension = 0.4;
final double maxTension = 1.0 + ( 1.0 - minTension );

// Unit movement per step, and max factor allowed before declaring loss of resolution
final double unitMovement = 1.0 / 200.0 * PI * 2 * 0.0065; // 1/200th of diameter, at 6.5mm radius
final double unitMovementFactorAllowable = 1.5;

// Target aspect ratio ( x / y )
final double desiredAspectRatio = 4.0 / 3.0;


// Basic datatypes
class Pair {

  public double first, second;

  public Pair( double first, double second ) {
    
    this.first = first;
    this.second = second;
    
  }
  
}

class Quad {
  
  public int top = 0;
  public int left = 0;
  public int height = 1;
  public int width = pixelWidth;
  public double area;
  
}

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


Pair evalPixelDistancePair( double x, double y ) {

  double distA = Math.sqrt( x * x + y * y );
  double distB = Math.sqrt( ( xMax - x ) * ( xMax - x ) + y * y );

  return new Pair( distA, distB );
  
}

PixelStatus evalPixelDistance( double x, double y ) {

  Pair dist = evalPixelDistancePair( x, y );

  if ( dist.first > maxArmLength && dist.second < maxArmLength )
    return PixelStatus.FarFromLeft;
  else if ( dist.first < maxArmLength && dist.second > maxArmLength )
    return PixelStatus.FarFromRight;
  else if ( dist.first > maxArmLength && dist.second > maxArmLength )
    return PixelStatus.FarFromBoth;
  else
    return PixelStatus.Good;
    
}


Pair evalPixelTensionPair( double x, double y ) {

  double aA = Math.atan2( y, x );
  double aB = Math.atan2( y, xMax - x );

  double cAA = Math.cos( aA );
  double sAA = Math.sin( aA );

  double cAB = Math.cos( aB );
  double sAB = Math.sin( aB );

  double denom = cAA * sAB + sAA * cAB;
  double tA = cAB / denom;
  double tB = cAA / denom;

  return new Pair( tA, tB );
  
}

PixelStatus evalPixelTension( double x, double y ) {

  Pair T = evalPixelTensionPair( x, y );

  if ( T.first < minTension )
    return PixelStatus.LooseLeft;
  else if ( T.second < minTension )
    return PixelStatus.LooseRight;
  else if ( T.first > maxTension )
    return PixelStatus.TightLeft;
  else if ( T.second > maxTension )
    return PixelStatus.TightRight;
  else
    return PixelStatus.Good;
    
}


double distBetween( Pair a, Pair b ) {
  
  double dx = b.first - a.first;
  double dy = b.second - a.second;
  
  return Math.sqrt( dx * dx + dy * dy );
  
}

// Base c = distance between motors = xMax
Pair calcPointLawCos( double a, double b ) {
  double alpha = Math.acos( ( b * b + xMax * xMax - a * a ) / ( 2 * b * xMax ) );
  return new Pair( b * Math.cos( alpha ) + xMax, b * Math.sin( alpha ) );
}

Pair evalPixelPrecisionPair( double x, double y ) {
  
  Pair dist = evalPixelDistancePair( x, y );
  Pair baseCoords = calcPointLawCos( dist.first, dist.second );
  Pair aMovesCoords = calcPointLawCos( dist.first + unitMovement, dist.second );
  Pair bMovesCoords = calcPointLawCos( dist.first, dist.second + unitMovement );
 
  return new Pair( distBetween( baseCoords, aMovesCoords ), distBetween( baseCoords, bMovesCoords ) );
  
}

PixelStatus evalPixelPrecision( double x, double y ) {
  
  Pair precisions = evalPixelPrecisionPair( x, y );
  
  if( precisions.first > unitMovement * unitMovementFactorAllowable && precisions.second < unitMovement * unitMovementFactorAllowable )
    return PixelStatus.NEPrecisionLeft;
  else if( precisions.first < unitMovement * unitMovementFactorAllowable && precisions.second > unitMovement * unitMovementFactorAllowable )
    return PixelStatus.NEPrecisionRight;
  else if( precisions.first > unitMovement * unitMovementFactorAllowable && precisions.second > unitMovement * unitMovementFactorAllowable )
    return PixelStatus.NEPrecisionBoth;
  else
    return PixelStatus.Good;
  
}


Quad findLargestRect( color pixels[] ) {
  
  Quad qArea = new Quad( );
  Quad qAspectRatio = new Quad( );
  
  int midX = pixelWidth / 2;
  int minY;
  for( minY = 0; pixels[ midX + minY * pixelWidth ] != color( 255 ); ++minY );
  
  qArea.top = minY;
  qAspectRatio.top = minY;
  
  double maxArea = 0;
  double closestAspectRatio = Double.MAX_VALUE;
  
  noFill( );
  stroke( 127, 255 );
  
  for( int maxY = minY + 1; maxY < pixelHeight; ++maxY ) {
    
    int minX;
    for( minX = midX; minX < pixelWidth && pixels[ minX + maxY * pixelWidth ] == color( 255 ); --minX );
    
    int maxX = pixelWidth - minX;
    
    line( minX, minY, maxX, maxY );
    
    double dx = xMax * ( ( pixelWidth - minX ) - minX ) / (double)pixelWidth;
    double dy = yMax * ( maxY - minY )                  / (double)pixelHeight;
    
    double area = dx * dy;
    double aspectRatio = dx / dy;
    
    if( area > maxArea ) {
      
      qArea.height = maxY - minY;
      qArea.left = minX;
      qArea.width = ( pixelWidth - minX ) - minX;
      qArea.area = area;
      
      maxArea = area;
      
    }
    
    if( Math.abs( aspectRatio - desiredAspectRatio ) < Math.abs( closestAspectRatio - desiredAspectRatio ) ) {
      
      qAspectRatio.height = maxY - minY;
      qAspectRatio.left = minX;
      qAspectRatio.width = ( pixelWidth - minX ) - minX;
      qAspectRatio.area = area;
      
      closestAspectRatio = aspectRatio;
      
    }
    
  }
  
  return qArea;
  
}


PImage reachableMask;
PImage tensionMask;
PImage precisionMask;
Quad largestRect;

void setup( ) {
  
  System.out.print( "Max height reachable by the device: " );
  System.out.println( yMax );

  size( 1280, 720 );
  background( 255 );

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
  
  tint( 0, 255, 0 );
  image( precisionMask, 0, 0 );
  
  tint( 255, 0, 0 );
  image( tensionMask, 0, 0 );
  
  tint( 0, 0, 255 );
  image( reachableMask, 0, 0 );
  
  updatePixels( );
  loadPixels( );
  largestRect = findLargestRect( pixels );
  updatePixels( );
  
  noStroke( );
  fill( 127, 127, 127, 255 );
  rect( (float)largestRect.left, (float)largestRect.top, (float)largestRect.width, (float)largestRect.height );
  
  System.out.println(
    String.format( "Largest rectangle print area = %f,%f x %f,%f -> %f x %f -> %f",
      xMax * largestRect.left / pixelWidth,
      yMax * largestRect.top / pixelHeight,
      
      xMax * ( largestRect.left + largestRect.width ) / pixelWidth,
      yMax * ( largestRect.top + largestRect.height ) / pixelHeight,
      
      xMax * largestRect.width / pixelWidth,
      yMax * largestRect.height / pixelHeight,
      
      largestRect.area ) );
  
  saveFrame( "AimlessFlannel.png" );
      
  noLoop( );
  
}