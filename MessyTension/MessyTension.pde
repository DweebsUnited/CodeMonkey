import java.util.Random;

class Mass {
  
  ArrayList<Spring> conns;
  
  float m;
  PVector p;
  PVector pp;
  
  PVector F;
  
}

class Spring {
  
  Mass m1, m2;
  
  // Rest length
  float rl;
  // Spring constant
  float k;
  
}

Random rng = new Random( );

ArrayList<Mass> actors;
final int nActors = 32;
final float mU = 7.5;
final float mS = 5;
final float svU = 0;
final float svS = 0.05;
ArrayList<Spring> springs;
final int nSprings = 32;
final float rlU = 0.35;
final float rlS = 0.25;
final float kU = 3;
final float kS = 1;

final float windScale = 0.75;
final float windStrength = 2.5;
final float windStep = 0.003;
final float keepStrength = 3;
final float neighDist = 0.05;
final float neighForce = 5;
float mousek = 0.25;
final float timeStep = 1.0 / 30 ;
final float velDamping = 0.0075;

final float mPixels = 2;

void setup( ) {
  
  size( 720, 640 );
  
  // Allocate
  actors = new ArrayList<Mass>( nActors );
  springs = new ArrayList<Spring>( nSprings );
  
  // Set up actors
  for( int adx = 0; adx < nActors; ++adx ) {
    
    Mass a = new Mass( );
    a.m = max( 0.01, mS * (float) rng.nextGaussian( ) / 3.0 + mU );
    a.p = new PVector( 2 * rng.nextFloat( ) - 1, 2 * rng.nextFloat( ) - 1 );
    a.pp = new PVector( svS * (float) rng.nextGaussian( ) / 3.0 + svU, 0 );
    a.pp.rotate( rng.nextFloat( ) * PI );
    a.pp.add( a.p );
    a.F = new PVector( 0, 0 );
    
    actors.add( a );
    
  }
  
  // Set up springs
  for( int sdx = 0; sdx < nSprings; ++sdx ) {
    
    int sm = rng.nextInt( nActors );
    int tm = sm;
    while( tm == sm )
      tm = rng.nextInt( nActors );
      
    Spring s = new Spring( );
    s.m1 = actors.get( sm );
    s.m2 = actors.get( tm );
    s.rl = rlS * (float) rng.nextGaussian( ) / 3.0 + rlU;
    s.k = max( 0.01, kS * (float) rng.nextGaussian( ) / 3.0 + kU );
    
    springs.add( s );
    
  }
  
  // Target frame rate
  frameRate( 30 );
  
}

int drawMode = 0;

void draw( ) {
  
  background( 0 );
  
  // Apply all springs
  PVector fDir = new PVector( );
  float mag;
  float mSm = Float.POSITIVE_INFINITY;
  float MSm = Float.NEGATIVE_INFINITY;
  for( Spring s : springs ) {
    
    // fDir = m1 -> m2
    fDir.set( s.m2.p );
    fDir.sub( s.m1.p );
    
    mag = fDir.mag( );
    
    // F = k x
    fDir.mult( s.k * ( mag - s.rl ) );
    
    // Add to force accumulators
    s.m1.F.add( fDir );
    fDir.mult( -1 );
    s.m2.F.add( fDir );
    
    if( mag > MSm )
      MSm = mag;
    if( mag < mSm )
      mSm = mag;
    
  }
  
  // Remaining forces are all based on actors alone
  Mass m, n;
  PVector np = new PVector( );
  PVector v = new PVector( );
  for( int mdx = 0; mdx < nActors; ++mdx ) {
    
    m = actors.get( mdx );
    
    // Apply noisy wind
    fDir.set( 2 * noise( windScale * 0.5 * ( m.p.x + 1 ), windScale * m.p.y, 0 + frameCount * windStep ) - 1, 2 * noise( windScale * 0.5 * ( m.p.x + 1 ), windScale * m.p.y, 3 + frameCount * windStep ) - 1 );
    fDir.div( sqrt( 2 ) ); // Make polar normal
    fDir.mult( windStrength );
    m.F.add( fDir );
    
    // Apply keeper
    fDir.set( 0, 0 );
    fDir.sub( m.p );
    mag = fDir.mag( );
    if( mag > 0.8 ) {
      
      // keeperf_raw = 10 * ( x - 0.8 ) ^ 2
      fDir.mult( keepStrength * min( 10 * pow( mag - 0.8, 2 ), 1.6 ) );
      m.F.add( fDir );
      
    }
    
    // Repel neighbors
    for( int ndx = 0; ndx < nActors; ++ndx ) {
      if( ndx == mdx )
        continue;
    
      n = actors.get( ndx );
      
      fDir.set( n.p );
      fDir.sub( m.p );
      
      mag = fDir.mag( );
      
      if( mag < neighDist ) {
        
        // Repellant_raw = ( -nF ) / ( nD ) x + nF
        fDir.mult( m.m * ( ( 1.0 - neighForce ) / neighDist * mag + neighForce ) );
        n.F.add( fDir );
        
      }
      
    }
    
    // Attract to mouse
    fDir.set( 2.0 * mouseX / pixelWidth - 1, 2.0 * mouseY / pixelHeight - 1 );
    fDir.sub( m.p );
    fDir.mult( mousek );
    m.F.add( fDir );
    
  }
  
  // Update loop
  for( int mdx = 0; mdx < nActors; ++mdx ) {
    
    m = actors.get( mdx );
    
    // Verlet integrate
    // np = 2 * p - pp + a * t^2
    np.set( m.p );
    np.mult( 2 );
    np.sub( m.pp );
    m.F.mult( timeStep * timeStep / m.m );
    np.add( m.F );
    
    // Calculate velocity
    v.set( np );
    v.sub( m.p );
    
    // Dampen
    v.mult( 1.0 - velDamping );
    
    // Update object
    m.pp.set( m.p );
    m.p.add( v );
    m.F.set( 0, 0 );
    
  }
  
  // Springs in 0, 3
  if( drawMode == 0 || drawMode == 3 ) {
    
    // Draw settings for springs
    noFill( );
    strokeWeight( 1.5 );
    
    // Draw springs
    for( Spring s : springs ) {
      
      fDir.set( s.m2.p );
      fDir.sub( s.m1.p );
      
      mag = fDir.mag( );
      
      stroke( lerpColor( color( 0, 0, 255 ), color( 255, 0, 0 ), ( mag - mSm ) / ( MSm - mSm ) ) );
      line(
        0.5 * ( s.m1.p.x + 1 ) * pixelWidth, 0.5 * ( s.m1.p.y + 1 ) * pixelHeight,
        0.5 * ( s.m2.p.x + 1 ) * pixelWidth, 0.5 * ( s.m2.p.y + 1 ) * pixelHeight
      );
      
    }
    
  }
  
  // Masses drawn in modes 0, 2, 3
  if( drawMode == 0 || drawMode == 2 || drawMode == 3 ) {
    
    // Draw settings for masses
    fill( 255 );
    noStroke( );
    
    // Draw masses
    for( int mdx = 0; mdx < nActors; ++mdx ) {
    
      m = actors.get( mdx );
      
      // Draw masses
      ellipse( 0.5 * ( m.p.x + 1 ) * pixelWidth, 0.5 * ( m.p.y + 1 ) * pixelHeight, m.m * mPixels, m.m * mPixels );
      
    }
    
  }
  
  // DrawMode 3 does something special
  if( drawMode == 3 ) {
    
  }
  
  // DrawMode 1 is the x wind field
  if( drawMode == 1 ) {
    
    loadPixels( );
    
    int pdx;
    for( int dx = 0; dx < pixelWidth; ++dx ) {
      for( int dy = 0; dy < pixelHeight; ++dy ) {
        
        pdx = dx + dy * pixelWidth;
        
        pixels[ pdx ] = color( 255 * noise( windScale * dx / pixelWidth, windScale * dy / pixelHeight, 0 + frameCount * windStep ) );
        
      }
    }
    
    updatePixels( );
    
  }
  
}

void keyPressed( ) {
  
  if( key == '1' )
    drawMode = 0;
  else if( key == '2' )
    drawMode = 1;
  else if( key == '3' )
    drawMode = 2;
  else if( key == '4' )
    drawMode = 3;
  
}

void mouseWheel( MouseEvent e ) {
  
  mousek -= 0.01 * e.getCount( );
  
  System.out.println( mousek );
  
}
