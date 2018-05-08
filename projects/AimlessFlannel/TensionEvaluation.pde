// Max and min tensions in each line, as a function of gantry mass ( assumed to be 1 )
final double minTension = 0.4;
final double maxTension = 1.0 + ( 1.0 - minTension );

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