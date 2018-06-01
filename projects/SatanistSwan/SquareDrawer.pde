class SquareDrawer {
  
  private int xGrid, yGrid;
  
  private FloatSampler angleSamp, spaceSamp;
  
  private Random rng;
  
  private final float SQ_2 = sqrt( 2.0 );
  
  // Random is pretty obvious
  // Grids are number of boxes in each dimension
  // angleSamp should return a radian line angle from 0-PI
  public SquareDrawer( Random rng, int xGrid, int yGrid, FloatSampler angleSamp, FloatSampler spaceSamp ) {
    
    this.rng = rng;
    
    this.xGrid = xGrid;
    this.yGrid = yGrid;
    
    this.angleSamp = angleSamp;
    this.spaceSamp = spaceSamp;
    
  }
  
  public void draw( PImage canvas ) {
    
    float pxw = canvas.pixelWidth;
    float pxh = canvas.pixelHeight;
    
    for( int xdx = 0; xdx < this.xGrid; ++xdx ) {
      
      for( int ydx = 0; ydx < this.yGrid; ++ydx ) {
        
        // These are in screen space
        PVector min = new PVector( xdx         * pxw / this.xGrid, ydx         * pxh / this.yGrid );
        PVector max = new PVector( ( xdx + 1 ) * pxw / this.xGrid, ( ydx + 1 ) * pxh / this.yGrid );
        PVector mid = new PVector( ( min.x + max.x ) / 2, ( min.y + max.y ) / 2 );
        
        // We will do line construction in "line space"
        // Clip box is 0,0 -> 1,1
        
        // Construct ray
        // Pick direction from angleSampler
        PVector d = new PVector( 1.0, 0.0 );
        d.rotate( tan( this.angleSamp.sample( mid.x, mid.y ) ) );
        d.normalize( );
        
        // Pick origin, random offset for first line ( guarantees at least one hits the box )
        PVector o = new PVector( 0.0, rng.nextFloat( ) );
        
        // Make normal
        PVector hat = d.copy( );
        hat.rotate( PI / 2.0 );
        hat.mult( this.spaceSamp.sample( mid.x, mid.y ) );
        
        // Work up and down
        boolean addGood = true;
        PVector addO = o.copy( );
        boolean subGood = true;
        PVector subO = o.copy( );
        
        // Run first intersection with o
        
        // To do crosshatching:
        //   +3 +2 +1 o0 -1 -2 -3
        //   Append one side to the reversed other side
        //   Iterate from S->E->E->S->S ...
        
        
        
        for( int odx = 0; addGood == true && subGood == true; ++odx ) {
          
          // Check add direction
          if( addGood ) {
            
            addO.add( hat );
            
            Intersection i = intersect( d, addO, min, max ) 
            
            if( i.intersects ) {
              
              // TODO: Line
              
            } else {
              addGood = false;
            
          }
          
          // Check sub direction 
          if( subGood ) {
            
            subO.sub( hat );
            
            Intersection i = intersect( d, subO, min, max ) 
            
            if( i.intersects ) {
              
              // TODO: Line
              
            } else {
              subGood = false;
            
          }
          
        }
        
      }
      
    }
    
  }
  
}