import java.util.Random;

Random rng = new Random( );

void shuffleI( int arr[] ) {
  
  int sdx, swap;
  for( int adx = arr.length; adx > 0; --adx ) {
    
    sdx = rng.nextInt( adx + 1 );
    swap = arr[ sdx ];
    arr[ sdx ] = arr[ adx ];
    arr[ adx ] = swap;
    
  }
  
}

final int sensorSize = 1;
float sensor[][] = new float[ 2 * sensorSize + 1 ][ 2 * sensorSize + 1 ];
float sensorScale;
final float sensorAngle = PI / 8;

class Actor {
  
  PVector pos;
  float heading;
  
}

class Indirect {
  
  int[][] arr;
  int[] adx;
  
  public Indirect( int[] size ) {
    
    this.arr = new int[ size.length ][];
    this.adx = new int[ size.length ];
    
    for( int ldx = 0; ldx < size.length; ++ldx ) {
      
      this.arr[ ldx ] = new int[ size[ ldx ] ];
      this.adx[ ldx ] = size[ ldx ]; // Force re-sort on first access
      
      for( int sdx = 0; sdx < size[ ldx ]; ++sdx ) {
        
        this.arr[ ldx ][ sdx ] = sdx;
        
      }
      
    }
    
  }
  
  public int next( ) {
    
    int dx = 0;
    int lfac = 1;
    for( int ldx = 0; ldx < arr.length; ++ldx ) {
      
      // If adx off the end, shuffle and reset
      if( adx[ ldx ] >= arr[ ldx ].length ) {
        
        // If top layer getting shuffled, announce new round
        if( ldx == 0 )
          System.out.println( "New round of indirections" );
        
        shuffleI( arr[ ldx ] );
        adx[ ldx ] = 0;
        
      }
      
      dx += arr[ ldx ][ adx[ ldx ]++ ] * lfac;
      lfac *= arr[ ldx ].length;
      
    }
    
    return dx;
    
  }
  
}

final int nActors = 1024 * 1024;
Actor actors[] = new Actor[ nActors ];
Indirect adx = new Indirect( new int[] { 1024, 1024 } );

void setup( ) {
  
  // Set up sensor
  sensorScale = 0;
  float v = 0;
  for( int dx = -sensorSize; dx < sensorSize; ++dx ) {
    for( int dy = -sensorSize; dy < sensorSize; ++dy ) {
    
      v = 1.0 / sqrt( dx * dx + dy * dy );
      sensor[ sensorSize + dy ][ sensorSize + dx ] = v;
      sensorScale = v; 
    
    }
  }
  
  // Set up actors
  for( int asdx = 0; asdx < nActors; ++asdx ) {
    
    
    
  }
  
}
