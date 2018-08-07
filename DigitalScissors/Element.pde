class Element {
  
  private ArrayList<Element> neigh;
  
  // The element will be pulled towards all connected neighbors
  public PVector pos;
  private PVector ppos;
  
  private final static float I_SPD_MIN = 1;
  private final static float I_SPD_MAX = 5;
  private final static float I_SPD_RNG = Element.I_SPD_MAX - Element.I_SPD_MIN;
  
  // Range for spring action
  private final static float P_RNG = 16;
  
  // Gravity constants
  private final static float SPRNG = 0.05;
  private final static float RPRNG = 0.75;
  
  // Color
  public color col;
  
  // Set up an element
  public Element( PVector pos, ColorFactoryIntf colFact ) {
    
    this.pos = new PVector( );
    this.ppos = new PVector( );
    
    this.pos.set( pos );
    this.ppos.set( this.pos );
    
    PVector offset = PVector.random2D( );
    offset.setMag( rng.nextFloat( ) * Element.I_SPD_RNG + Element.I_SPD_MIN );
    this.ppos.add( offset );
    
    this.neigh = new ArrayList<Element>( );
    
    this.col = colFact.make( );
    
  }
  
  public void remake( PVector pos, ColorFactoryIntf colFact ) {
  
    this.pos = new PVector( );
    this.ppos = new PVector( );
    
    this.pos.set( pos );
    this.ppos.set( this.pos );
    
    PVector offset = PVector.random2D( );
    offset.setMag( rng.nextFloat( ) * Element.I_SPD_RNG + Element.I_SPD_MIN );
    this.ppos.add( offset );
    
    this.col = colFact.make( );
    
  }
  
  // Elements are attracted gravitationally to each link they have
  public void step( ) {
        
    // Update pos
    PVector accel = new PVector( );
    
    for( Element n : this.neigh ) {
      
      PVector off = new PVector ( );
      off.set( n.pos );
      off.sub( this.pos );
  
      if( off.magSq( ) > Element.P_RNG ) {
        off.mult( Element.SPRNG );
      } else {
        off.mult( - Element.RPRNG );
      }
        
      accel.add( off );
      
    }
        
    // Verlet integrator
    PVector temp = new PVector( );
    temp.set( this.pos );
    
    this.pos.mult( 2 );
    this.pos.sub( this.ppos );
    accel.mult( T_STEP );
    this.pos.add( accel );
    this.ppos = temp;
    
    // Clamp points to draw area
    this.clamp( );
      
  }
    
  // Clamp position to drawing space
  // This fucks with physics a LOT
  private void clamp( ) {
    
    if( this.pos.x > pixelWidth ) {
      this.pos.x = pixelWidth;
    } else if( this.pos.x < 0 ) {
      this.pos.x = 0;
    }

    if( this.pos.y > pixelHeight ) {
      this.pos.y = pixelHeight;
    } else if( this.pos.y < 0 ) {
      this.pos.y = 0;
    }
      
  }
  
  // Draw using a given painter
  public void draw( PainterIntf sp ) {
    
    for( Element n : this.neigh ) {
      
      sp.draw( this.pos, n.pos, this.col, n.col );
      
    }
    
  }
  
  // Link to another element
  public void link( Element n ) {
    this.neigh.add( n );
  }
  
  // Clear all the links
  public void clearLinks( ) {
    this.neigh.clear( );
  }
  
}

void keyReleased( ) {
    
    if( key == 'w' ) {
      
        saveFrame( );
      
    }
    
}