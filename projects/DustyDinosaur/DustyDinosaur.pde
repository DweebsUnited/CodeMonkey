import java.util.Random;

final int WIDE = 1280;
final int TALL = 720;

final int N_PTS = 64;
final float T_STEP = 1.0 / 30.0;

Element[] elems = new Element[ N_PTS ];

final Random rng = new Random( );

void setup( ) {
  
  for( int idx = 0; idx < N_PTS; ++idx ) {
    
    elems[ idx ] = new Element( new PVector( rng.nextInt( WIDE ), rng.nextInt( TALL ) ) );
    
  }
  
}

void draw( ) {
  
  DelaunayTriangulator dt = new DelaunayTriangulator( );
  dt.points = elems;
  dt.triangles = dt.Calculate( );
  
  // Each element has these forces:
  //   Pulled slightly towards its connections
  //   Pushed away from center of all triangles
  // Best guess: This will tend to make a very uniform graph
  
  
  
}