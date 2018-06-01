class Intersection {
  
  public boolean intersects;
  
  public float t0, t1;
  public PVector p0, p1;
  
}
  
Intersection intersect( PVector d, PVector o, PVector min, PVector max ) {
  
  if( ray.dir.x >= 0 ) { 
    txm = (min.x - r.orig.x) / ray.dir.x; 
    txM = (max.x - r.orig.x) / ray.dir.x; 
  } else { 
    txm = (max.x - r.orig.x) / ray.dir.x; 
    txM = (min.x - r.orig.x) / ray.dir.x; 
  } 
  
  float t0x = ( min.x - o.x ) / d.x; 
  float t1x = ( max.x - o.x ) / d.x;
  
  float t0y = ( min.y - o.y ) / d.y;
  float t1y = ( max.y - o.y ) / d.y;
  
}