import java.util.Random;

final int G_SIZE = 5;

final float E_RAD = 5;

float[][] pos = new float[ G_SIZE ][ G_SIZE ];
float[][] ppos = new float[ G_SIZE ][ G_SIZE ];

PVector axis = new PVector( 1.0, 1.0 );

Random rng = new Random( );

void setup( ) {
  
  size( 800, 600 );
  background( 255 );
  noStroke( );
  fill( 0, 5 );
  
  axis.setMag( 1.0 );
  
}

void draw( ) {
  
  for( int idx = 0; idx < G_SIZE; ++idx ) {
    for( int jdx = 0; jdx < G_SIZE; ++jdx ) {
      
      float x = pixelWidth * ( idx + 0.5 ) / G_SIZE + pos[ idx ][ jdx ] * axis.x;
      float y = pixelHeight * ( jdx + 0.5 ) / G_SIZE + pos[ idx ][ jdx ] * axis.y;
      
      noStroke( );
      fill( 0, 5 );
      ellipse( x, y, E_RAD, E_RAD );
      
      stroke( 0, 5 );
      noFill( );
      
      float nx, ny;
      
      if( idx != G_SIZE - 1 ) {
        
        nx = pixelWidth * ( idx + 1.5 ) / G_SIZE + pos[ idx + 1 ][ jdx ] * axis.x;
        ny = pixelHeight * ( jdx + 0.5 ) / G_SIZE + pos[ idx + 1 ][ jdx ] * axis.y;
        line( x, y, nx, ny );
        
      }
      
      if( jdx != G_SIZE - 1 ) {
        
        nx = pixelWidth * ( idx + 0.5 ) / G_SIZE + pos[ idx ][ jdx + 1 ] * axis.x;
        ny = pixelHeight * ( jdx + 1.5 ) / G_SIZE + pos[ idx ][ jdx + 1 ] * axis.y;
        line( x, y, nx, ny );
        
      }
      
      pos[ idx ][ jdx ] += (float)rng.nextGaussian( );
      
    }
  }
  
}