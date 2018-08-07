// Max factor allowed before declaring loss of resolution
final double unitMovementFactorAllowable = 1.5;

Pair pointFromLengs( double a, double b ) {
  double x = ( xMax * xMax - b * b + a * a ) / ( 2.0 * xMax );
  return new Pair(
    x,
    Math.sqrt( a * a - x * x ) );
}

Pair evalPixelPrecisionPair( double x, double y ) {
  
  Pair dist = evalDistancePair( x, y );
  Pair baseCoords = new Pair( x, y );
  Pair aMovesCoords = pointFromLengs( dist.first + unitMovement, dist.second );
  Pair bMovesCoords = pointFromLengs( dist.first, dist.second + unitMovement );
 
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