class Intersection {
  
  public boolean intersects;
  
  public float tm, tM;
  public PVector pm, pM;
  
}

// AABB intersection is pretty straightforward
Intersection intersect( PVector d, PVector o, PVector min, PVector max ) {
  
  Intersection i = new Intersection( );
  
  float x1, x2;
  if( d.x > 0 ) {
    x1 = ( min.x - o.x ) / d.x;
    x2 = ( max.x - o.x ) / d.x;
  } else {
    x1 = ( max.x - o.x ) / d.x;
    x2 = ( min.x - o.x ) / d.x;
  }
  
  float y1, y2;
  if( d.y > 0 ) {
    y1 = ( min.y - o.y ) / d.y;
    y2 = ( max.y - o.y ) / d.y;
  } else {
    y1 = ( max.y - o.y ) / d.y;
    y2 = ( min.y - o.y ) / d.y;
  }
  
  // Missed collision on xy plane
  if( x1 > y2 || y1 > x2 ) {
    
    i.intersects = false;
    
  } else {
    
    // We have intersection!
    
    i.intersects = true;
    
    i.tm = max( x1, y1 );
    i.tM = min( x2, y2 );
    
    i.pm = d.copy( );
    i.pm.mult( i.tm );
    i.pm.add( o );
    
    i.pM = d.copy( );
    i.pM.mult( i.tM );
    i.pM.add( o );
  
  }
  
  // Return results
  return i;
  
}
