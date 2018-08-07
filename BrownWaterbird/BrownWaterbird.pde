// The most basic random line drawer imaginable

import java.util.Random;

Random rng = new Random( System.currentTimeMillis( ) );

void setup( ) {
  
  size( 1280, 720 );
  background( 0 );
  
  noFill( );
  stroke( 255, 255, 255, 25 );
  
}

void draw( ) {
  
  PVector nP = new PVector( rng.nextInt( pixelWidth ), rng.nextInt( pixelHeight ) );
  PVector pP = new PVector( rng.nextInt( pixelWidth ), rng.nextInt( pixelHeight ) );

  line( nP.x, nP.y, pP.x, pP.y );
  
}

void keyReleased( ) {
  
  if( key == 'w' )
    saveFrame( "#####.png" );
    
}