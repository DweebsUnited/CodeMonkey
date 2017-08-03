final float P_SPD = 5.0;

class Element extends PVector {
  
  public PVector pos;
  public PVector ppos;
  public PVector accel;
  
  public Element( PVector pos ) {
    
    this.pos = new PVector( );
    this.ppos = new PVector( );
    this.accel = new PVector( );
    
    PVector off = new PVector( 0, 5 * P_SPD );
    off.rotate( rng.nextFloat( ) * 2 * PI );
    
    this.pos.set( pos );
    this.pos.add( off );
    
    this.ppos.set( this.pos );
    
    this.set( this.pos );
    
  }
  
  public void addAccel( PVector a ) {
    
    this.accel.add( a );
    
  }
  
  public void step( ) {
        
    // Verlet integrator
    PVector temp = new PVector( );
    temp.set( this.pos );
    
    this.pos.mult( 2 );
    this.pos.sub( this.ppos );
    this.accel.mult( T_STEP );
    this.pos.add( this.accel );
    this.ppos = temp;
    
    // Clamp points to draw area
    this.clamp( );
    
    // Super method
    this.set( this.pos );
    
    // Reset acceleration
    this.accel.set( 0, 0 );
    
  }
  
  private void clamp( ) {
      
    if( this.pos.x > WIDE )
      this.pos.x = WIDE;
    else if( this.pos.x < 0 )
      this.pos.x = 0;

    if( this.pos.y > TALL )
      this.pos.y = TALL;
    else if( this.pos.y < 0 )
      this.pos.y = 0;
      
  }
  
  public void draw( ) {
    
    fill( 255, 255, 255, 5 );
    noStroke( );
    
    ellipse( this.pos.x, this.pos.y, 5, 5 );
    
  }
  
}