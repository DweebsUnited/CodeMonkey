class Element {

    // The tpos point will move around with random speed set every few frames
    private PVector targetPos;
    private PVector targetVel;

    // The pos point will act as though it is connected to tpos with a spring
    private PVector pos;
    private PVector ppos;
    
    private ArrayList<Element> neigh;
    
    // The ColorFactoryIntf factories
    private ColorFactoryIntf targetColor;
    private ColorFactoryIntf posColor;
    private ColorFactoryIntf linkColor;
    
    public Element( PVector pos, ColorFactoryIntf targetColorFactory, ColorFactoryIntf posColorFactory, ColorFactoryIntf linkColorFactory ) {
      
        this.targetPos = new PVector( );
        this.targetVel = newTvel( );
        
        this.pos = new PVector( );
        this.ppos = new PVector( );
      
        PVector off = new PVector( 0, 5 * P_SPD );
        off.rotate( rng.nextFloat( ) * 2 * PI );
        
        this.targetPos.set( pos );
        this.pos.set( this.targetPos );
        this.pos.add( off );
        
        this.ppos.set( this.pos );
        
        this.neigh = new ArrayList<Element>( );
        
        this.targetColor = targetColorFactory;
        this.posColor = posColorFactory;
        this.linkColor = linkColorFactory;
        
    }
        
    public void step( ) {
      
        // Update tvel every few frames
        if( frameCount % T_UP == 0 ) {
            this.targetVel = this.newTvel( );
        }
        
        // Update tpos
        this.targetPos.add( targetVel );
        
        // Update pos
        PVector accel = new PVector( );
        accel.set( this.targetPos );
        accel.sub( this.pos );
        
        // If out of range, spring it back in
        if( accel.magSq( ) > P_RNG ) {
            accel.mult( P_SPRNG );
        } else {
            accel.mult( - P_RSPRG );
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
    
    private PVector newTvel( ) {
        return new PVector( rng.nextFloat( ) * 2 * P_SPD - P_SPD, rng.nextFloat( ) * 2 * P_SPD - P_SPD );
    }
    
    private void clamp( ) {
      
        if( this.pos.x > WIDE ) {
            this.pos.x = WIDE;
        } else if( this.pos.x < 0 ) {
            this.pos.x = 0;
        }

        if( this.pos.y > TALL ) {
            this.pos.y = TALL;
        } else if( this.pos.y < 0 ) {
            this.pos.y = 0;
        }
            
        if( this.targetPos.x > WIDE ) {
            this.targetPos.x = WIDE;
        } else if( this.targetPos.x < 0 ) {
            this.targetPos.x = 0;
        }

        if( this.targetPos.y > TALL ) {
            this.targetPos.y = TALL;
        } else if( this.targetPos.y < 0 ) {
            this.targetPos.y = 0;
        }
        
    }
    
    public void link( Element n ) {
        this.neigh.add( n );
    }
    
    public void draw( ) {
      
        for( Element n : this.neigh ){
          
            stroke( this.targetColor.make( ) );
            line( this.targetPos.x, this.targetPos.y, n.targetPos.x, n.targetPos.y );
            
            stroke( this.posColor.make( ) );
            line( this.pos.x, this.pos.y, n.pos.x, n.pos.y );
            
        }
        
        stroke( this.linkColor.make( ) );
        line( this.targetPos.x, this.targetPos.y, this.pos.x, this.pos.y );
        
    }
    
}