import java.util.Random;
import com.hamoid.*;

// Load image
// Each step, compare pixel with random neighbor
//   TODO: Bias in one direction or another
// If pixel loses, copy winners color

Random rng = new Random( );

PImage base, next, temp;


class Pair {
  
  public int x, y;
  
  public Pair( ) {
    this( 0, 0 );
  }
  
  public Pair( int x, int y ) {
    this.x = x;
    this.y = y;
  }
  
}


Pair getNeighbor( Pair coord, int w, int h ) {
  
  int dx = 0, dy = 0;
  
  switch( rng.nextInt( 8 ) ) {
    
    case 0:
      dx = -1;
      dy = -1;
      break;
    case 1:
      dx = 0;
      dy = -1;
      break;
    case 2:
      dx = 1;
      dy = -1;
      break;
    
    case 3:
      dx = -1;
      dy = 0;
      break;
    case 4:
      dx = 1;
      dy = 0;
      break;
    
    case 5:
      dx = -1;
      dy = 1;
      break;
    case 6:
      dx = 0;
      dy = 1;
      break;
    case 7:
      dx = 1;
      dy = 1;
      break;
    
  }
  
  int x = coord.x + dx;
  int y = coord.y + dy;
  
  if( x == -1 )
    x += w;
  if( x == w )
    x -= w;
    
  if( y == -1 )
    y += h;
  if( y == h )
    y -= h;
  
  return new Pair( x, y );
  
}

int winners[] = new int[] { 1, 2, 0 };

color fight( color a, color b ) {
  
  // Red:   c >> 16 & 0xFF
  // Green: c >> 8 & 0xFF
  // Blue:  c >> 0 & 0xFF
  
  int aidx = 0;
  int bidx = 0;
  
  int aval = a & 0xFF;
  int bval = b & 0xFF;
  
  {
    
    int va = a >> ( 8 * 1 ) & 0xFF;
    int vb = b >> ( 8 * 1 ) & 0xFF;
    
    if( va > aval ) {
      aval = va;
      aidx = 1;
    }
    if( vb > bval ) {
      bval = vb;
      bidx = 1;
    }
    
  }
  {
    
    int va = a >> ( 8 * 2 ) & 0xFF;
    int vb = b >> ( 8 * 2 ) & 0xFF;
    
    if( va > aval ) {
      aval = va;
      aidx = 2;
    }
    if( vb > bval ) {
      bval = vb;
      bidx = 2;
    }
    
  }
  
  return ( aidx == winners[ bidx ] ) ? b : a;
  
}

VideoExport videoExport;

void setup( ) {
  
  size( 1280, 1014 );
  
  base = loadImage( "D:\\Downloads\\StarryNight.jpg" );
  next = createImage( base.width, base.height, RGB );
  
  videoExport = new VideoExport( this, "RustyNeptune.mp4" );
  videoExport.startMovie( );
  
}

void draw( ) {
  
  base.loadPixels( );
  next.loadPixels( );
  
  Pair coords = new Pair( );
  
  for( int ydx = 0; ydx < base.height; ++ydx ) {
    
    coords.y = ydx;
    
    for( int xdx = 0; xdx < base.width; ++xdx ) {
      
      coords.x = xdx;
      
      Pair n = getNeighbor( coords, base.width, base.height );
      
      next.pixels[ coords.y * base.width + coords.x ] = fight( base.pixels[ coords.y * base.width + coords.x ], base.pixels[ n.y * base.width + n.x ] );
      
    }
    
  }
  
  next.updatePixels( );
  
  temp = base;
  base = next;
  next = temp;
  
  image( base, 0, 0, pixelWidth, pixelHeight );
  videoExport.saveFrame( );
  
}

void keyPressed( ) {
  
  if( key == 'q' ) {
    videoExport.endMovie( );
    exit( );
  }
  
}