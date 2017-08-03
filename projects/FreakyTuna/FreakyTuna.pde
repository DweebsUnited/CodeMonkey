import java.util.Random;
import com.hamoid.*;

VideoExport exporter;

final int WIDE = 1280;
final int TALL = 720;

// Number of points active
final int N_PTS = 64;
// Time step for physics
final float T_STEP = 1.0 / 60.0;
// Drag coefficient
final float P_DRAG = 0.99;
// Perterbation gaussian scaling factor
final float P_PFAC = 12.5;

// Number of launch centers
final int N_LCH = 2;
// Launch angle gaussian scaling factor
final float LA_FAC = 6.0;
// Launch speed
final float P_SPD = 0.02;
// Min launch distance
final float L_MIN = 0.9;
// Launch belt thickness
final float L_DIF = 0.05;

// Point opacity
final int P_OPA = 1;

final Random rng = new Random( );

// Drop points randomly, then let them spiral in towards the center
// Like shooting atoms near a black hole

class Point {
  
  private PVector pos;
  private PVector ppos;
  private PVector opos;
  
  public Point( PVector pos, PVector vel ) {
    
    this.pos = new PVector( );
    this.ppos = new PVector( );
    this.opos = new PVector( );
    
    this.pos.set( pos );
    this.ppos.set( pos );
    this.opos.set( pos );
    
    this.pos.add( vel );
    
  }
  
  private float gravMag( float d ) {
    
    return 0.01 / pow( d, 0.25 );
    
  }
  
  public void step( ) {
    
    PVector accel = new PVector( 0, 0 );
    accel.sub( this.pos );
    float d = accel.magSq( );
    accel.setMag( this.gravMag( d ) );
    
    // Verlet integrator
    PVector temp = new PVector( );
    temp.set( this.pos );
    
    this.pos.mult( 2 );
    this.pos.sub( this.ppos );
    accel.mult( T_STEP );
    this.pos.add( accel );
    
    this.ppos = temp;
    
    // Little bit of drag
    this.pos.sub( this.ppos );
    this.pos.mult( P_DRAG );
    this.pos.rotate( (float)rng.nextGaussian( ) / P_PFAC ); // Little perturbations ;)
    this.pos.add( this.ppos );
    
  }
  
  public void draw( ) {
    
    //if( frameCount % 2 == 0 ) {
    
      float x = ( this.pos.x + 1.0 ) / 2.0 * TALL + ( WIDE - TALL ) / 2.0;
      float y = ( this.pos.y + 1.0 ) / 2.0 * TALL;
      float px = ( this.ppos.x + 1.0 ) / 2.0 * TALL + ( WIDE - TALL ) / 2.0;
      float py = ( this.ppos.y + 1.0 ) / 2.0 * TALL;
      
      noFill( );
      stroke( 255, 255, 255, P_OPA );
      line( x, y, px, py );
      
    //} else
    //  this.opos.set( this.pos );
    
  }
  
  public float mag( ) {
    return this.pos.mag( );
  }
  
}

class PointFactory {
  
  public float[] launchPts;
  
  public PointFactory( ) {
    
    this.launchPts = new float[ N_LCH ];
    for( int idx = 0; idx < N_LCH; ++idx )
      this.launchPts[ idx ] = rng.nextFloat( ) * 2 * PI;
    
  }
  
  Point make( ) {
  
    // Angle of unit circle to start from
    float sAng = this.launchPts[ rng.nextInt( N_LCH ) ] + (float)rng.nextGaussian( ) / LA_FAC;
    
    // Start location
    PVector s = new PVector( rng.nextFloat( ) * L_DIF + L_MIN, 0.0 );
    s.rotate( sAng );
    
    // Set up for velocity
    PVector v = new PVector( 0.0, 0.0 );
    v.sub( s );
    v.normalize( );
    v.mult( P_SPD );
    
    // Launch angle [ 45, 95 ] deg
    float lAng = rng.nextFloat( ) * -50.0 + 95.0;
    lAng *= PI / 180.0;
    
    if( rng.nextInt( 100 ) > 50 )
      lAng *= -1.0;
      
    v.rotate( lAng );
    
    return new Point( s, v );
  
  }
  
}

PointFactory pf;
Point[] points = new Point[ N_PTS ];
boolean drawing = false;

void setup( ) {
  
  pf = new PointFactory( );
  
  size( 1280, 720 );
  background( 0 );
  
  for( int idx = 0; idx < N_PTS; ++idx ) {
    points[ idx ] = pf.make( );
  }
  
}

void draw( ) {
  
  for( int idx = 0; idx < N_PTS; ++idx ) {
    
    points[ idx ].step( );
    if( points[ idx ].mag( ) < 0.075 || points[ idx ].mag( ) > 1.0 )
      points[ idx ] = pf.make( );
    else
      points[ idx ].draw( );
    
  }
  
  if( drawing )
    exporter.saveFrame( );
  
}

void keyReleased( ) {
    
  if( key == 'q' ) {
    
    if( !drawing ) {
      
      exporter = new VideoExport( this, "FreakyTuna.mp4" );
      exporter.setFrameRate( 30.0 );
      
      exporter.startMovie( );
      
    } else {
    
      exporter.endMovie( );
    
    }
    
    drawing = ! drawing;
    
  } else if( key == 'w' ) {
    
    saveFrame( "#####.png" );
    
  } else if( key == 'f' ) {
    
    background( 0 );
    
    pf = new PointFactory( );
    
    for( int idx = 0; idx < N_PTS; ++idx ) {
      points[ idx ] = pf.make( );
    }
    
  }
  
}