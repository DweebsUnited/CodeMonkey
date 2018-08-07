class EllipseAverager {
  
  private Accumulator ar, ag, ab;
  
  public EllipseAverager( AccumulatorFactory accumFac ) {
    
    this.ar = accumFac.make( );
    this.ag = accumFac.make( );
    this.ab = accumFac.make( );
    
  }

  private int getRed( color c ) { return c >> 0x10 & 0xFF; }
  private int getGrn( color c ) { return c >> 0x08 & 0xFF; }
  private int getBlu( color c ) { return c >> 0x00 & 0xFF; }
  
  public color getAverageColor( int cx, int cy, int rad, PImage img ) {
    
    img.loadPixels( );
    
    this.ar.clear( );
    this.ag.clear( );
    this.ab.clear( );
    
    for( int pdy = max( 0, cy - rad ); pdy < min( img.height, cy + rad ); ++pdy ) {
      
      for( int pdx = max( 0, cx - rad ); pdx < min( img.width, cx + rad ); ++pdx ) {
      
        // Circle containment check
        
        int ox = abs( cx - pdx );
        int oy = abs( cy - pdy );
        
        if( ox * ox + oy * oy > rad * rad )
          continue;
          
        this.ar.add( getRed( img.pixels[ pdx + pdy * img.width ] ) );
        this.ag.add( getGrn( img.pixels[ pdx + pdy * img.width ] ) );
        this.ab.add( getBlu( img.pixels[ pdx + pdy * img.width ] ) );
      
      }
      
    }
    
    return color( this.ar.get( ), this.ag.get( ), this.ab.get( ) );
    
  }

}
