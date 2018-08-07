class AABB implements Intersectable {
  
  private PVector min;
  private PVector max;
  
  public AABB( PVector min, PVector max ) {
    
    this.min = min.copy( );
    this.max = max.copy( );
    
  }
  
  public AABB( PVector o, float size ) {
    
    size /= 2;
    
    this.min = new PVector( o.x - size, o.y - size, o.z - size );
    this.max = new PVector( o.x + size, o.y + size, o.z + size );
    
  }
  
  public float intersect( Ray r ) {
    
    // Check xy collision
    float x1, x2;
    if( r.d.x > 0 ) {
      x1 = ( this.min.x - r.o.x ) / r.d.x;
      x2 = ( this.max.x - r.o.x ) / r.d.x;
    } else {
      x1 = ( this.max.x - r.o.x ) / r.d.x;
      x2 = ( this.min.x - r.o.x ) / r.d.x;
    }
    
    float y1, y2;
    if( r.d.y > 0 ) {
      y1 = ( this.min.y - r.o.y ) / r.d.y;
      y2 = ( this.max.y - r.o.y ) / r.d.y;
    } else {
      y1 = ( this.max.y - r.o.y ) / r.d.y;
      y2 = ( this.min.y - r.o.y ) / r.d.y;
    }
    
    // Early bail, missed collision on xy plane
    if( x1 > y2 || y1 > x2 )
      return Float.POSITIVE_INFINITY;
      
    // We have collision, set up for z check
    float t1 = max( x1, y1 );
    float t2 = min( x2, y2 );
    
    float z1, z2;
    if( r.d.z > 0 ) {
      z1 = ( this.min.z - r.o.z ) / r.d.z;
      z2 = ( this.max.z - r.o.z ) / r.d.z;
    } else {
      z1 = ( this.max.z - r.o.z ) / r.d.z;
      z2 = ( this.min.z - r.o.z ) / r.d.z;
    }
    
    // Check for bail on z check
    if( t1 > z2 || z1 > t2 )
      return Float.POSITIVE_INFINITY;
      
    // We have a collision!
    return max( t1, z1 );
    
  }
  
}