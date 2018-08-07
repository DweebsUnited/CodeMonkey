import java.util.Random;

final int N_LIN = 7;
int[] LIN = new int[ N_LIN ];
color[] col = new color[ N_LIN ];

final int N_PTS = 256;
final int P_OPA = 10;
final int P_MAG = 32;

final int N_WAVE = 23;

Random rng = new Random( );

void setup( ) {
  
  size( 1280, 720 );
  background( 0 );
  noSmooth( );
  
  for( int l = 0; l < N_LIN; ++l ) {
    
    LIN[ l ] = rng.nextInt( pixelHeight );
    col[ l ] = getColor( );
    
    stroke( 255, P_OPA );
    line( 0, LIN[ l ], pixelWidth, LIN[ l ] );
    
    float mag = rng.nextFloat( ) * P_MAG;
    
    for( int x = 0; x < pixelWidth; ++x ) {
      
      mag += ( rng.nextFloat( ) * 2 - 1.0 ) * 3.0;
      
      if( mag > P_MAG )
        mag = P_MAG;
      else if( mag < 0 )
        mag = 0;
      
      //stroke( 225,191,146, max( 255 - mag * 3, 25 ) );
      //line( x, LIN[ l ].y - mag, x, LIN[ l ].y + mag ); 
      
      for( int p = 0; p < N_PTS; ++p ) {
        
        float y = -1;
        
        while( !( y > 0 && y < pixelHeight ) )
          y = (float)rng.nextGaussian( ) * mag * 2 - mag + LIN[ l ];
          
        fill( col[ l ], P_OPA );
        noStroke( );
        ellipse( x, y, 2, 2 );
        
      }
      
    }
    
  }
  
}

void draw( ) {
  
}