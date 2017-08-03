import java.util.Random;

final int WIDE = 1280;
final int TALL = 720;

final int N_PTS = 64;
final float T_STEP = 1.0 / 30.0;

Element[] elems = new Element[ N_PTS ];

final Random rng = new Random( );

void setup( ) {
  
  size( 1280, 720 );
  background( 0, 0, 0 );
  
  for( int idx = 0; idx < N_PTS; ++idx ) {
    
    elems[ idx ] = new Element( new PVector( rng.nextInt( WIDE ), rng.nextInt( TALL ) ) );
    
  }
  
}

void draw( ) {
  
  DelaunayTriangulator dt = new DelaunayTriangulator( );
  dt.points = elems;
  
  // Each element has these forces:
  //   Pulled slightly towards its connections
  //   Pushed away from center of all triangles
  // Best guess: This will tend to make a very uniform graph
  
  for( Triangle t : dt.Calculate( ) ) {
    
    for( int idx = 0; idx < 3; ++idx ) {
      
      int n = ( idx == 2 ) ? 0 : idx + 1;
      
      noFill( );
      stroke( 255, 0, 0, 5 );
      
      line( t.points[ idx ].x, t.points[ idx ].y, t.points[ n ].x, t.points[ n ].y );
      
    }
    
  }
  
  for( Element e : elems )
    e.draw( );
  
}