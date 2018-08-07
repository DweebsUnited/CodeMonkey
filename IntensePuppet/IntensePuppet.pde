// Loom like generator, draws links between points

import java.util.Random;

final int N_PTS = 32;

PVector[] pts = new PVector[ N_PTS ];

Random rng = new Random( System.currentTimeMillis( ) );

void setup( ) {
  
  size( 1280, 720 );
  background( 0 );
  
  PVector anchor = new PVector( pixelWidth / 2.0, pixelHeight / 2.0 );
  
  for( int idx = 0; idx < N_PTS; ++idx ) {
    
    pts[ idx ] = new PVector( pixelHeight / 2, 0.0 );
    pts[ idx ].rotate( 2.0 * PI * idx / N_PTS );
    pts[ idx ].add( anchor );
    
  }
  
  noFill( );
  stroke( 255, 255, 255, 25 );
  
}

int pP = 0;

void draw( ) {
  
  int nP = rng.nextInt( N_PTS );
  
  PVector a = pts[ pP ];
  PVector b = pts[ nP ];
  
  pP = nP;

  line( a.x, a.y, b.x, b.y );
  
}

void keyReleased( ) {
  
  if( key == 'w' )
    saveFrame( "#####.png" );
    
}