import java.util.Random;

// Number of points to draw
final int N_PTS = 15;
// Cosmic background radiation points to draw
final int N_CBR = 5;
// Point opacity
final int P_OPA_MIN = 15;
final int P_OPA_MAX = 35;
final int P_OPA_RNG = P_OPA_MAX - P_OPA_MIN;
// Radius scaling factor
final float G_FAC = 175;
// Point size
final int P_SZ_MIN = 2;
final int P_SZ_MAX = 6;
final int P_SZ_RNG = P_SZ_MAX - P_SZ_MIN;
// Frame interval to decay
final int F_FAC = 50;
final int F_OPA = 5;
// Power to raise acceptance test to
final int A_POW = 4;

Random rng = new Random( System.currentTimeMillis( ) );

float[][] acceptance;
boolean acceptable( PVector pos ) {
  
  int x = int( pos.x );
  int y = int( pos.y );
  
  if( x > pixelWidth - 1 || x < 0 || y > pixelHeight - 1 || y < 0 )
    return false;
  
  return rng.nextFloat( ) < acceptance[ int( pos.y ) ][ int( pos.x ) ];
  
}

void setup( ) {
  size( 1280, 720 );
  background( 0 );
  
  // Open your mind to others
  
  noiseSeed( System.currentTimeMillis( ) );
  noiseDetail( 8, 1.0 );
  
  acceptance = new float[ pixelHeight ][ pixelWidth ];
  
  float mV = 1.0;
  float MV = 0.0;
  
  for( int idx = 0; idx < pixelHeight; ++idx ) {
    for( int jdx = 0; jdx < pixelWidth; ++jdx ) {
      
      float nV = noise( idx / (float)pixelHeight, jdx / (float)pixelWidth );
      
      if( nV > MV )
        MV = nV;
      if( nV < mV )
        mV = nV;
      
      acceptance[ idx ][ jdx ] = nV;
      
    }
  }
  
  for( int idx = 0; idx < pixelHeight; ++idx ) {
    for( int jdx = 0; jdx < pixelWidth; ++jdx ) {
      
      float nV = acceptance[ idx ][ jdx ];
      
      nV = ( nV - mV ) / ( MV - mV );
      
      acceptance[ idx ][ jdx ] = pow( nV, A_POW );
      
    }
  }
  
  noStroke( );
  
}

void draw( ) {
  
  // Decay if its that time again
  
  if( frameCount % F_FAC == 0 ) {
    
    fill( 0, F_OPA );
    rect( 0, 0, pixelWidth, pixelHeight );
    
  }
  
  // Anchor
  PVector anchor = new PVector( );
  
  // Cosmic background radiation ;)
  PVector p = new PVector( );
    
  for( int idx = 0; idx < N_CBR; ++idx ) {
  
    do {
      p.set( rng.nextInt( pixelWidth ), rng.nextInt( pixelHeight ) );
    } while( !acceptable( p ) );
    
    float s = rng.nextInt( P_SZ_RNG ) + P_SZ_MIN;
    
    fill( 255, rng.nextInt( P_OPA_RNG ) + P_OPA_MIN );
    ellipse( p.x, p.y, s, s );
    
  }
    
  // Now we draw around an anchor
  
  do {
      anchor.set( rng.nextInt( pixelWidth ), rng.nextInt( pixelHeight ) );
    } while( !acceptable( anchor ) );
  
  // Draw appropriate amount of dots
  
  for( int idx = 0; idx < N_PTS; ++idx ) {
      
    p.setMag( (float)rng.nextGaussian( ) * G_FAC );
    p.rotate( rng.nextFloat( ) * 2 * PI );
  
    p.add( anchor );
      
    if( !acceptable( p ) ) {
      --idx;
      continue;
    }
    
    float s = rng.nextInt( P_SZ_RNG ) + P_SZ_MIN;
    
    fill( 255, rng.nextInt( P_OPA_RNG ) + P_OPA_MIN );
    ellipse( p.x, p.y, s, s );

  }

}

void keyReleased( ) {
    
  if( key == 'w' ) {
    
    saveFrame( "#####.png" );
    
  } else if( key == 'f' ) {
    
    background( 0 );
    
  }
  
}