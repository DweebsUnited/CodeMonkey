import java.util.List;
import java.util.ArrayList;

ChaosSquare sq;

PGraphics canvas;

final float mX = 1.0 / 16;
final float mY = 1.0 / 16;

final int gX = 5;
final int gY = 5;

final float sX = ( 1.0 - 2 * mX ) / gX;
final float sY = ( 1.0 - 2 * mY ) / gY;

final int nRounds = 12;

final float mRad = 1.0 / 32;
final float MRad = 1.0 / 8;

void create( ) {
  
  canvas.beginDraw( );
  canvas.background( 255 );
  
  for( int ydx = 0; ydx < gY; ++ydx ) {
    
    for( int xdx = 0; xdx < gX; ++xdx ) {
      
      float rad = ( MRad - mRad ) * ( ydx * gX + xdx ) / ( gX * gY ) + mRad;
      
      sq = new ChaosSquare(
        new PVector( ( mX + xdx * sX ) * canvas.pixelWidth, ( mY + ydx * sY ) * canvas.pixelHeight ),
        new PVector( ( mX + ( xdx + 1 ) * sX ) * canvas.pixelWidth, ( mY + ( ydx + 1 ) * sY ) * canvas.pixelHeight ),
        rad );
        
      for( int ddx = 0; ddx < nRounds; ++ddx )
        sq.draw( canvas );
      
    }
    
  }
  
  canvas.endDraw( );
  
}

void setup( ) {
  
  size( 1024, 683 );
  background( 255 );
  
  canvas = createGraphics( 4096, 2731 );
  
  create( );
  
}

void draw( ) {
  
  image( canvas, 0, 0, pixelWidth, pixelHeight );
  
}

void keyPressed( ) {
  
  if( key == 'q' )
    canvas.save( "TOPECAAAAW.png" );
  else if( key == ' ' )
    create( );
  
}
