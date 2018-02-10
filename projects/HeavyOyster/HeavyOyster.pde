// Ring of actuators connected by elastic
// Input on one makes it react
// Each tries to reach lowest pressure on self (tend to settle towards center and average of neighbors)
// Also throw some randomness in, because why not ;)

import java.util.Random;

final int N_ARM = 6;

final float F_FAC_MAX = 0.45;
final float F_FAC_MIN = 0.05;
final float F_FAC_RNG = F_FAC_MAX - F_FAC_MIN;

final float G_RAD = 25;
final float G_FAC = 0.01;

float[] rads = new float[ N_ARM ];
float[] frcs = new float[ N_ARM ];

PVector origin;

Random rng = new Random( System.currentTimeMillis( ) );

void setup( ) {
  
  size( 800, 600 );
  
  background( 255 );
  noFill( );
  
  origin = new PVector( pixelWidth / 2, pixelHeight / 2 );
  
  for( int idx = 0; idx < N_ARM; ++idx ) {
    rads[ idx ] = abs( (float)rng.nextGaussian( ) ) * pixelHeight / 4;
  }
  
  frameRate( 2 );
  
}

void draw( ) {
  
  for( int idx = 0; idx < N_ARM; ++idx ) {
    
    float nRad = rads[ ( idx + 1 ) == N_ARM ? 0 : ( idx + 1 ) ];
    float pRad = rads[ ( idx - 1 ) < 0 ? ( N_ARM - 1 ) : ( idx - 1 ) ];
    
    frcs[ idx ] = ( nRad + pRad ) / 2.0;
    
    if( rads[ idx ] > G_RAD )
      frcs[ idx ] += ( G_RAD - rads[ idx ] ) * G_FAC;
    
  }
  
  background( 255 );
  
  PVector a = new PVector( 1, 0 );
  PVector b = new PVector( 1, 0 );
  
  for( int idx = 0; idx < N_ARM; ++idx ) {
    
    rads[ idx ] = frcs[ idx ];
    frcs[ idx ] = 0;
    
    a.set( b );
    b.rotate( 2 * PI / N_ARM );
    
    a.setMag( rads[ idx ] );
    b.setMag( rads[ ( idx + 1 ) == N_ARM ? 0 : ( idx + 1 ) ] );
    
    stroke( 0 );
    line( origin.x, origin.y, origin.x + a.x, origin.y + a.y );
    
    stroke( 255, 0, 0 );
    line( origin.x + a.x, origin.y + a.y, origin.x + b.x, origin.y + b.y );
    
  }
  
}

void mouseClicked( ) {
  
  float x = mouseX - pixelWidth / 2;
  float y = mouseY - pixelHeight / 2;
  
  int cIdx = round( ( atan2( y, x ) + PI ) / ( 2.0 * PI / N_ARM ) ) % N_ARM;
  
  rads[ cIdx ] = sqrt( x * x + y * y );
  
}