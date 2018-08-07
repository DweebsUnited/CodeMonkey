import java.util.Random;

// Frame interval to relink nodes
final int NEIGH_INT = 150;

// Number of elements to use
final int N_ELEMS = 8;
final int N_LINKS = 4;

// Framerate for physics
final float F_RATE = 60;
final float T_STEP = 1.0 / ( F_RATE * F_RATE );

// This is the list of elements
Element[] elems = new Element[ N_ELEMS ];

// We need some randomness
Random rng = new Random( );

// Color and painting setup
// We're using an earthtone palette
ColorFactoryIntf pf = new EarthtonePalette( );
// And a SandPainter
PainterIntf sp = new SandPainter( 8, 16, 1, 25 );

void setup( ) {
  
  size( 1280, 720 );
  background( 255 );
  frameRate( F_RATE );
  
  PVector pos = new PVector( );
  
  for( int i = 0; i < N_ELEMS; ++i ) {
    
    pos.set( rng.nextInt( pixelWidth ), rng.nextInt( pixelHeight ) );
    
    elems[ i ] = new Element( pos, pf );
    
  }
  
  for( int i = 0; i < N_ELEMS; ++i ) {
    
    elems[ i ].clearLinks( );
    elems[ i ].link( elems[ rng.nextInt( N_ELEMS ) ] );
    
  }
  
}

void draw( ) {
  
  //background( 255 );
  
  // Reset links every so often just for fun
  if( frameCount % NEIGH_INT == 0 ) {
    
    for( int i = 0; i < N_ELEMS; ++i ) {
      
      if( rng.nextFloat( ) < 0.01 ) {
        PVector pos = new PVector( );
        pos.set( rng.nextInt( pixelWidth ), rng.nextInt( pixelHeight ) );
        elems[ i ].remake( pos, pf );
      }
    
      elems[ i ].clearLinks( );
      elems[ i ].link( elems[ rng.nextInt( N_ELEMS ) ] );
      
    }
    
  }
  
  for( Element e : elems ) {
    
    e.draw( sp );
    e.step( );
    
  }
  
  sp.drawFin( );
  
}