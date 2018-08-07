RandomWrapper rng = new RandomWrapper( );

class Arm {
  
  public PVector pos;
  public float len;
  public PVector arm;
  public float angSpd;
  
  public Arm( PVector pos, float len, float angSpd ) {
    
    this.pos = pos;
    this.len = len;
    this.arm = new PVector( len, 0 );
    this.angSpd = angSpd;
    
  }
  
  public PVector armTip( ) {
    
    PVector tip = new PVector( this.pos.x, this.pos.y );
    tip.add( this.arm );
    return tip;
    
  }
  
  public void update( ) {
    
    this.arm.rotate( this.angSpd );
    
  }
  
  void draw( ) {
    
    noFill( );
    stroke( 255, 0, 0 );
    PVector tip = this.armTip( );
    line( this.pos.x, this.pos.y, tip.x, tip.y );
    
  }
  
}

int cdx = 1;
color colors[] = { color( 0 ), color( 255 ) };

Arm a, b;

SandPainter sp;
Uniform01Distribution dist = new Uniform01Distribution( );

void setup( ) {
  
  size( 1280, 720 );
  background( 0 );
  
  a = new Arm(
    new PVector( pixelWidth / 2.0 - pixelWidth / 8.0, pixelHeight / 2.0 ),
    pixelWidth / 4.0,
    2.0 * PI / 360.111 );
  
  b = new Arm(
    new PVector( pixelWidth / 2.0 + pixelWidth / 8.0, pixelHeight / 2.0 ),
    pixelWidth / 4.0,
    - 2.0 * PI / 360.111 );
    
  sp = new SandPainter( rng, pixelWidth / 2 );
  
}

void draw( ) {
  
  if( rng.yesno( ) )
    cdx = ( cdx + 1 ) % colors.length;
  
  sp.prepare( );
  sp.pLine( a.armTip( ), b.armTip( ), colors[ cdx ], 0.05, dist );
  sp.save( );
  
  a.update( );
  b.update( );
  
}