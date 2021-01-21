import java.util.Random;

void setup( ) {
  
  size( 1920, 1080 );
  //blendMode( SUBTRACT );
  
  SquareDrawer sqdA = new SquareDrawer(
    120, 68,
    new PerlinSampler( 0.0, PI, 0.0, 2.0 ),
    new UniformSampler( 0.125, 0.75 ),
    new PerlinSampler( 0.0, 1.0, 0.0, 2.0 ) );
    
  SquareDrawer sqdB = new SquareDrawer(
    120, 68,
    new PerlinSampler( 0.0, PI, 1.0, 2.0 ),
    new UniformSampler( 0.125, 0.75 ),
    new PerlinSampler( 0.0, 1.0, 1.0, 2.0 ) );
    
  SquareDrawer sqdC = new SquareDrawer(
    120, 68,
    new PerlinSampler( 0.0, PI, 2.0, 2.0 ),
    new UniformSampler( 0.125, 0.75 ),
    new PerlinSampler( 0.0, 1.0, 2.0, 2.0 ) );
    
  PGraphics canvas = createGraphics( pixelWidth, pixelHeight );
  canvas.beginDraw( );
  canvas.blendMode( MULTIPLY );
  canvas.background( 255 );
  canvas.endDraw( );

  sqdA.draw( canvas, color( 255,   0,   0, 85 ), 3 );
  sqdB.draw( canvas, color(   0,   0, 255, 85 ), 3 );
  sqdC.draw( canvas, color(   0, 255,   0, 85 ), 3 );
  
  image( canvas, 0, 0, pixelWidth, pixelHeight );
  
  saveFrame( "SatanistSwan.png" );
  noLoop( );
  
}
