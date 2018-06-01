class Element {
  
  private ArrayList<Element> children;
  
  private float angle;
  private float radius;
  
  private color col;
  
  private float angSpeed;
  
  private float SPD_MIN = 0 * 2 * PI / 180.0;
  private float SPD_MAX = 1 * 2 * PI / 180.0;
  private float SPD_RNG = SPD_MAX - SPD_MIN;
  
  private final float radFactor = 0.25; 
  
  private Random rng = new Random( );
  
  public Element( float radius, ColorFactoryIntf colFact, float spawnProb ) {
    
    this.col = colFact.make( );
    
    this.angle = this.rng.nextFloat( ) * 2 * PI;
    this.radius = radius;
    
    this.angSpeed = rng.nextFloat( ) * this.SPD_RNG + this.SPD_MIN;
    
    this.children = new ArrayList<Element>( );
    // Spawn?
    while( rng.nextFloat( ) < spawnProb ) {
      this.spawn( colFact, spawnProb * spawnProb );
      //System.out.println( "Spawned!" );
    }
    
  }
  
  public void spawn( ColorFactoryIntf colFact, float spawnProb ) {
    
    Element e = new Element( this.radius * this.radFactor, colFact, spawnProb );
    
    this.children.add( e );
    
  }
  
  private float modf( float n, float mod ) {
    
    while( n > mod )
      n -= mod;
      
    return n;
    
  }
  
  public void step( ) {
    
    this.angle = this.modf( this.angle + this.angSpeed, 2 * PI );
    
    for( Element e : this.children )
      e.step( );
    
  }
  
  public void draw( PVector parent, color parentC, PainterIntf paint ) {
    
    PVector pos = new PVector( 0, radius );
    pos.rotate( angle );
    pos.add( parent );
    
    paint.draw( parent, pos, parentC, this.col );
    
    for( Element e : this.children )
      e.draw( pos, this.col, paint );
    
  }
  
}