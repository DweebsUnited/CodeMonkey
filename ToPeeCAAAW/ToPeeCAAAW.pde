import java.util.List;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;

ChaosSquare sq;

PGraphics canvas;

final float mX = 1.0 / 16;
final float mY = 1.0 / 16;

final int gX = 5;
final int gY = 5;

final float sX = ( 1.0 - 2 * mX ) / gX;
final float sY = ( 1.0 - 2 * mY ) / gY;

final int nRounds = 5;

final float mRad = 1.0 / 32;
final float MRad = 1.0 / 8;

// GCode settings
// This is approx 1 inch in mm
final float zUp = 26;
final float zDown = 7;
// mm/s -> We want to draw fast, this is a typical high-speed movement for printing
//   For plotting, it might be possible to move faster 0.o
final float spd = 1000;
final float upSpd = 4000;

BufferedWriter baseWriter;
BufferedWriter acctWriter;

void create( ) throws IOException {
  
  baseWriter = new BufferedWriter( new FileWriter( "/Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/ToPeeCAAAW_base.pointlist" ) );
  acctWriter = new BufferedWriter( new FileWriter( "/Users/ozzy/Documents/CodeMonkey/ToPeeCAAAW/ToPeeCAAAW_acct.pointlist" ) );
  
  canvas.beginDraw( );
  canvas.background( 255 );
  
  for( int ydx = 0; ydx < gY; ++ydx ) {
    
    for( int xdx = 0; xdx < gX; ++xdx ) {
      
      float rad = ( MRad - mRad ) * ( ydx * gX + xdx ) / ( gX * gY ) + mRad;
      
      sq = new ChaosSquare(
        new PVector( ( mX + xdx * sX ) * canvas.pixelWidth, ( mY + ydx * sY ) * canvas.pixelHeight ),
        new PVector( ( mX + ( xdx + 1 ) * sX ) * canvas.pixelWidth, ( mY + ( ydx + 1 ) * sY ) * canvas.pixelHeight ),
        rad );
        
      BufferedWriter writer = sq.isBase ? baseWriter : acctWriter;
        
      // Move to square start
      writer.append( "N," + Float.toString( ( sq.max.x - sq.min.x ) * sq.cA.x + sq.min.x ) + "," + Float.toString( ( sq.max.y - sq.min.y ) * sq.cA.y + sq.min.y ) + "\n" );
        
      for( int ddx = 0; ddx < nRounds; ++ddx )
        sq.draw( canvas, writer );
    
    }
    
  }
  
  canvas.endDraw( );
  
  baseWriter.close( );
  acctWriter.close( );
  
  canvas.save( "TOPECAAAAW.png" );
  
}

void setup( ) {
  
  size( 1024, 683 );
  background( 255 );
  
  canvas = createGraphics( 4096, 2731 );
  
  try {
    create( );
  } catch( Exception e ) {
    System.out.println( "Failure?" );
  }
  
}

void draw( ) {
  
  image( canvas, 0, 0, pixelWidth, pixelHeight );
  
}

void keyPressed( ) {
  
  if( key == ' ' ) {
    
    try {
      create( );
    } catch( Exception e ) {
      System.out.println( "Failure?" );
    }
    
  }
  
}
