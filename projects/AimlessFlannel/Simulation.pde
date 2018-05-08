// Setup to simulate the plotter
// These are quartersteps from the origin
int aQSteps;
int bQSteps;

int aTargetQSteps;
int bTargetQSteps;

// Origin point in wall space
Pair originWall;
// Current position in wall space
Pair currentPos;
// Target position
Pair targetPos;
// Coefficients for the target line
double targetLineA;
double targetLineB;
double targetLineC;
double targetLineD;
// For drawing the line that it should be following
Pair targetTrail;

// We will compute offsets to these from the number of steps
Pair originLength;

boolean alerted = true;

void setTarget( double x, double y ) {
  
  alerted = false;
  
  targetPos = new Pair( x, y );
    
  targetTrail = new Pair( currentPos );

  Pair targetLeng = evalDistancePair( targetPos.first, targetPos.second );
  
  aTargetQSteps = (int)Math.round( ( targetLeng.first  - originLength.first  ) / unitQMovement );
  bTargetQSteps = (int)Math.round( ( targetLeng.second - originLength.second ) / unitQMovement );
  
  targetLineA = targetPos.second - currentPos.second;
  targetLineB = targetPos.first - currentPos.first;
  targetLineC = targetPos.first * currentPos.second - targetPos.second * currentPos.first;
  targetLineD = Math.sqrt( targetLineA * targetLineA + targetLineB * targetLineB );
  
  System.out.println( String.format( "%f,%f -> %f,%f : %d,%d -> %d,%d", currentPos.first, currentPos.second, targetPos.first, targetPos.second, aQSteps, bQSteps, aTargetQSteps, bTargetQSteps ) );
  
}

double distToTargetLine( Pair p ) {
  
  // Now this is podracing
  return Math.abs( targetLineA * p.first - targetLineB * p.second + targetLineC ) / targetLineD;
  
}

void simStep( ) {
  
  if( aQSteps != aTargetQSteps || bQSteps != bTargetQSteps ) {
  
    int aStep = ( aQSteps == aTargetQSteps ? 0 : ( aTargetQSteps > aQSteps ? 1 : -1 ) );
    int bStep = ( bQSteps == bTargetQSteps ? 0 : ( bTargetQSteps > bQSteps ? 1 : -1 ) );
    
    // For each step possibility ( a, b, ab )
    //   Calculate arm lengths to the point
    //   Calculate coords
    //   Calculate dist to target
    //   Move whichever gets us closest
    // Calculate it this way to avoid incremental error
    
    Pair aMovesCoords  = pointFromLengs( originLength.first + unitQMovement * ( aQSteps + aStep ), originLength.second + unitQMovement * bQSteps             );
    Pair bMovesCoords  = pointFromLengs( originLength.first + unitQMovement * aQSteps,             originLength.second + unitQMovement * ( bQSteps + bStep ) );
    Pair abMovesCoords = pointFromLengs( originLength.first + unitQMovement * ( aQSteps + aStep ), originLength.second + unitQMovement * ( bQSteps + bStep ) );
    double aDist  = distToTargetLine( aMovesCoords  );
    double bDist  = distToTargetLine( bMovesCoords  );
    double abDist = distToTargetLine( abMovesCoords );
    
    System.out.println( String.format( "%f, %f, %f", aDist, bDist, abDist ) );
    
    if( aStep != 0 && aDist < bDist && aDist < abDist ) {
      
      // Step a
      aQSteps += aStep;
      currentPos = new Pair( aMovesCoords );
      
    } else if( bStep != 0 && bDist < aDist && bDist < abDist ) {
      
      // Step b
      bQSteps += bStep;
      currentPos = new Pair( bMovesCoords );
      
    } else if( aStep != 0 || bStep != 0 ) {
      
      // Step both
      aQSteps += aStep;
      bQSteps += bStep;
      currentPos = new Pair( abMovesCoords );
      
    } else {
      System.out.println( "Should never get here..." );
    }
  
  } else if( !alerted ) {
    System.out.println( "Arrived, QSteps are equal" );
    alerted = true;
  }
  
}

void setupSim( ) {
  
  // Set up arm simulation
  // These are quartersteps from the origin
  aQSteps = 0;
  bQSteps = 0;
  
  aTargetQSteps = 0;
  bTargetQSteps = 0;
  
  // Origin point in wall space
  originWall = new Pair(
    xMax / 2.0,
    yMax * ( largestRect.top + largestRect.height / 2 ) / pixelHeight );
  
  currentPos = new Pair( originWall );
  targetPos = new Pair( currentPos );
  targetTrail = new Pair( currentPos );
  
  // We will compute offsets to these from the number of steps
  originLength = new Pair(
    Math.sqrt( originWall.first * originWall.first                       + originWall.second * originWall.second ),
    Math.sqrt( ( xMax - originWall.first ) * ( xMax - originWall.first ) + originWall.second * originWall.second ) );
  
}

void drawSim( ) {
  
  noFill( );
  stroke( 0, 0, 255 );
  line( (float)( targetTrail.first / xMax * pixelWidth ), (float)( targetTrail.second / yMax * pixelHeight ), (float)( targetPos.first / xMax * pixelWidth ), (float)( targetPos.second / yMax * pixelHeight ) );
  stroke( 255, 0, 0 );
  line( 0.0, 0.0, (float)( targetPos.first / xMax * pixelWidth ), (float)( targetPos.second / yMax * pixelHeight ) );
  line( pixelWidth, 0.0, (float)( targetPos.first / xMax * pixelWidth ), (float)( targetPos.second / yMax * pixelHeight ) );
  stroke( 0, 255, 0 );
  line( 0.0, 0.0, (float)( currentPos.first / xMax * pixelWidth ), (float)( currentPos.second / yMax * pixelHeight ) );
  line( pixelWidth, 0.0, (float)( currentPos.first / xMax * pixelWidth ), (float)( currentPos.second / yMax * pixelHeight ) );
  
}