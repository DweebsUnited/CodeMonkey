// Note: All units except pixels are in meters

// Motors are at the corners
// Mapping pixels to coordinates:
final double xMax = 2.25; // Distance between the two motors

// Max length of either arm
final double maxArmLength = 2.25;

// yMax = height of isoceles triangle with xMax base and maxArmLength arms
// This positions the bottom of the screen to be at the bottom of the reachable area
final double yMax = Math.sqrt( maxArmLength * maxArmLength - 0.25 * xMax * xMax );

// Unit movement per step
final double unitMovement = 1.0 / 200.0 * PI * 0.016; // 1/200th of diameter, at 16mm radius
final double unitQMovement = unitMovement / 4.0;


// Basic datatype
class Pair {

  public double first, second;

  public Pair( double first, double second ) {
    
    this.first = first;
    this.second = second;
    
  }
  
  public Pair( Pair p ) {
    
    this.first = p.first;
    this.second = p.second;
    
  }
  
}


void setup( ) {
  
  System.out.print( "Max height reachable by the device: " );
  System.out.println( yMax );

  size( 1280, 720 );
  background( 255 );

  runEval( );
  
  System.out.println(
    String.format( "Largest rectangle print area = %f,%f x %f,%f -> %f x %f -> %f",
      xMax * largestRect.left / pixelWidth,
      yMax * largestRect.top / pixelHeight,
      
      xMax * ( largestRect.left + largestRect.width ) / pixelWidth,
      yMax * ( largestRect.top + largestRect.height ) / pixelHeight,
      
      xMax * largestRect.width / pixelWidth,
      yMax * largestRect.height / pixelHeight,
      
      largestRect.area ) );
      
  setupSim( );
  
}

void draw( ) {
  
  background( 0 );
  
  drawEval( );
  
  drawSim( );
  
  simStep( );
  
}

void mouseClicked( ) {
  
  setTarget( xMax * mouseX / pixelWidth, yMax * mouseY / pixelHeight );
  
}

void keyPressed( ) {
  
  if( key == 'h' ) {
    
    setTarget( originWall.first, originWall.second );
    
  }
  
}