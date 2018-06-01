import java.util.List;
import java.util.ArrayList;

ChaosSquare sq;

void setup( ) {
  
  size( 720, 640 );
  background( 255 );
  
  float mX = 1.0 / 16;
  float mY = 1.0 / 16;
  
  int gX = 5;
  int gY = 5;
  
  float sX = ( 1.0 - 2 * mX ) / gX;
  float sY = ( 1.0 - 2 * mY ) / gY;
  
  int nRounds = 12;
  
  float mRad = 1.0 / 32;
  float MRad = 1.0 / 8;
  
  for( int ydx = 0; ydx < gY; ++ydx ) {
    
    for( int xdx = 0; xdx < gX; ++xdx ) {
      
      float rad = ( MRad - mRad ) * ( ydx * gX + xdx ) / ( gX * gY ) + mRad;
      
      sq = new ChaosSquare( new PVector( ( mX + xdx * sX ) * pixelWidth, ( mY + ydx * sY ) * pixelHeight ), new PVector( ( mX + ( xdx + 1 ) * sX ) * pixelWidth, ( mY + ( ydx + 1 ) * sY ) * pixelHeight ), rad );
      for( int ddx = 0; ddx < nRounds; ++ddx )
        sq.draw( );
      
    }
    
  }
  
  noLoop( );
  
}

void keyPressed( ) {
  
  if( key == 'q' )
    saveFrame( "TOPECAAAAW.png" );
  
}