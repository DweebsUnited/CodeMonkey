import java.util.Random;

final float F_RATE = 60;

ColorFactoryIntf colors = new BlackYellowPalette( );
PVector origin;
color originCol;
Element root;
PainterIntf paint = new SandPainter( 8, 16, 1, 55 );

void setup( ) {
  
  size( 1280, 720 );
  background( 255 );
  frameRate( F_RATE );
  
  root = new Element( 360, colors, 0.75 );
  
  origin = new PVector( pixelWidth / 2, pixelHeight / 2 );
  originCol = colors.make( );
  
}

void draw( ) {
  
  root.step( );
  root.draw( origin, originCol, paint );
  
}

void keyReleased( ) {
    
    if( key == 'w' ) {
      
        saveFrame( );
      
    } else if( key == ' ' ) {
      
      background( 255 );
      root = new Element( 360, colors, 0.75 );
      originCol = colors.make( );
      
    }
}