import java.util.Random;

int cDist( color a, color b ) {
  
  int red = ( a >> 16 & 0xFF ) - ( b >> 16 & 0xFF );
  int green = ( a >> 8 & 0xFF ) - ( b >> 8 & 0xFF );
  int blue = ( a & 0xFF ) - ( b & 0xFF );
  return red * red + green * green + blue * blue;
  
}

void kMeansStep( PImage img, int k, color centroids[] ) {
  
  // K means 
  // Find all assignments
  // Update each centroid
  
  int rAvg[] = new int[ k ];
  int gAvg[] = new int[ k ];
  int bAvg[] = new int[ k ];
  
  int cCount[] = new int[ k ];
  
  for( color px : img.pixels ) {
    
    int minDist = cDist( centroids[ 0 ], px );
    int minCent = 0;
    for( int cIdx = 1; cIdx < k; ++cIdx ) {
      
      int d = cDist( centroids[ cIdx ], px );
      if( d < minDist ) {
        
        minDist = d;
        minCent = cIdx;
        
      }
      
    }
    
    cCount[ minCent ] += 1;
    rAvg[ minCent ] += ( px >> 16 & 0xFF );
    gAvg[ minCent ] += ( px >> 8 & 0xFF );
    bAvg[ minCent ] += ( px & 0xFF );
    
  }
  
  for( int cIdx = 0; cIdx < k; ++cIdx ) {
    
    centroids[ cIdx ] = color(
      (int)( (float)rAvg[ cIdx ] / cCount[ cIdx ] ),
      (int)( (float)gAvg[ cIdx ] / cCount[ cIdx ] ),
      (int)( (float)bAvg[ cIdx ] / cCount[ cIdx ] ) );
    
  }
  
}

void setup( ) {
  
  size( 1280, 1014 );
  
  Random rng = new Random( System.currentTimeMillis( ) );
  
  PImage img = loadImage( "D:\\Downloads\\StarryNight.jpg" );
  img.loadPixels( );
  
  final int k = 4;
  color centroids[] = new color[ k ];
  
  for( int cIdx = 0; cIdx < k; ++cIdx ) {
    
    centroids[ cIdx ] = color( rng.nextInt( 255 ), rng.nextInt( 255 ), rng.nextInt( 255 ) );
    
  }
  
  for( int kIdx = 0; kIdx < 500; ++kIdx ) {
    
    kMeansStep( img, k, centroids );
    
  }
  
  for( int pxIdx = 0; pxIdx < pixelWidth * pixelHeight; ++pxIdx ) {
    
    int minDist = cDist( centroids[ 0 ], img.pixels[ pxIdx ] );
    int minCent = 0;
    for( int cIdx = 1; cIdx < k; ++cIdx ) {
      
      int d = cDist( centroids[ cIdx ], img.pixels[ pxIdx ] );
      if( d < minDist ) {
        
        minDist = d;
        minCent = cIdx;
        
      }
      
    }
    
    img.pixels[ pxIdx ] = centroids[ minCent ];
    
  }
  
  img.updatePixels( );
  
  image( img, 0, 0 );
  
}