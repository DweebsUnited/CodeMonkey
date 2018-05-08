double distBetween( Pair a, Pair b ) {
  
  double dx = b.first - a.first;
  double dy = b.second - a.second;
  
  return Math.sqrt( dx * dx + dy * dy );
  
}

Pair evalDistancePair( double x, double y ) {

  double distA = Math.sqrt( x * x + y * y );
  double distB = Math.sqrt( ( xMax - x ) * ( xMax - x ) + y * y );

  return new Pair( distA, distB );
  
}

PixelStatus evalPixelDistance( double x, double y ) {

  Pair dist = evalDistancePair( x, y );

  if ( dist.first > maxArmLength && dist.second < maxArmLength )
    return PixelStatus.FarFromLeft;
  else if ( dist.first < maxArmLength && dist.second > maxArmLength )
    return PixelStatus.FarFromRight;
  else if ( dist.first > maxArmLength && dist.second > maxArmLength )
    return PixelStatus.FarFromBoth;
  else
    return PixelStatus.Good;
    
}