class Camera {
  
  private PVector eye;
  private PVector look;
  private PVector up;
  private PVector right;
  
  private float ar = 1.0;
  private float eyeOff = 1.0;
  private float persp = 0.0;
  
  public Camera( ) { }
  
  public Camera( float ar, float eyeOff, float persp ) {
    
    this.ar = ar;
    this.eyeOff = eyeOff;
    this.persp = persp;
    
  }
  
  public void lookAt( PVector eye, PVector center, PVector up ) {
    
    this.eye = eye.copy( );       // Eye is unchanged for now
    
    this.look = center.copy( );
    this.look.sub( this.eye );    // Make look a vector
    this.look.setMag( this.eyeOff );
                                  // Normalize look vector
    
    this.up = up.copy( );         // May not be in camera space
    this.right = this.up.cross( this.look );
                                  // This is now left x)
    this.right.mult( - 1.0 / this.ar / this.right.mag( ) );
                                  // Flip and normalize to get the right vector

    this.up = this.right.cross( this.up );
                                  // Remake up in camera space
    this.up.normalize( );         // Normalize up
    
    // Move eye back to correct perspective
    PVector tLook = this.look.copy( );
    tLook.mult( this.eyeOff );
    this.eye.sub( tLook );
    
  }
  
  private color zGrad( float d ) {
    
    final float dMin = 0.0;
    final float dMax = 20.0;
    
    if( Float.isInfinite( d ) )
      return color( 0 );
    else
      //return color( 255 );
      return color( 255 * ( 1.0 - ( d - dMin ) / ( dMax - dMin ) ) );
    
  }
  
  // For now do a basic z-buffer render
  public void cast( PImage canvas, List<Intersectable> scene ) {
    
    canvas.loadPixels( );
    
    // For each pixel
    for( int ydx = 0; ydx < canvas.height; ++ydx ) {
      
      float scY = ( ydx ) / ( canvas.height - 1.0 ) * 2.0 - 1.0;
      PVector camY = this.up.copy( );
      camY.mult( scY );
      
      for( int xdx = 0; xdx < canvas.width; ++xdx ) {
      
        float scX = ( xdx ) / ( canvas.width - 1.0 ) * 2.0 - 1.0;
        PVector camX = this.right.copy( );
        camX.mult( scX );
        
        PVector pixDir = this.eye.copy( );
        pixDir.add( look );
        pixDir.add( camY );
        pixDir.add( camX );
        
        PVector pixEye = camY.copy( );
        pixEye.add( camX );
        pixEye.mult( this.persp );
        pixEye.add( this.eye );
        
        pixDir.sub( pixEye );
        pixDir.normalize( );
        
        float minDist = Float.POSITIVE_INFINITY;
        
        for( Intersectable i : scene ) {
          
          float d = i.intersect( new Ray( pixEye, pixDir ) );
          if( d < minDist )
            minDist = d;
          
        }
        
        canvas.pixels[ ydx * canvas.width + xdx ] = this.zGrad( minDist );
      
      }
      
    }
    
    canvas.updatePixels( );
    
  }
  
}