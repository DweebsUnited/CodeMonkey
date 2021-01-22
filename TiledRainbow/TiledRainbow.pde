class quadf {
  
  float a, b, c, d;
  
  public quadf( ) { }
  public quadf( float a, float b, float c, float d ) { this.a = a; this.b = b; this.c = c; this.d = d; }
  
}

float cartDist( float sx, float sy, float tx, float ty ) {
  
  float dx = tx - sx;
  float dy = ty - sy;
  
  return ( dx * dx ) + ( dy * dy );
  
}

void func( quadf in, quadf out ) {
  
  out.a = cos( 2 * in.a / in.c + in.b );
  out.b = cos( 1 * in.c / in.a + in.b );
  out.c = cos( 4 * in.b / in.b + in.a );
  out.d = 0;
  
}

color palette[];

void setup( ) {
  
  size( 720, 640 );
  
  palette = new color[ 27 ];
  int pdx = 0;
  for( int rx = 0; rx < 3; ++rx ) {
    for( int gx = 0; gx < 3; ++gx ) {
      for( int bx = 0; bx < 3; ++bx ) {
        palette[ pdx++ ] = color( 255.0 / 3 * rx, 255.0 / 3 * gx, 255.0 / 3 * bx );
      }
    }
  }
  
  quadf cord = new quadf( );
  quadf vals = new quadf( );
  
  float cx, cy;
  color c;
  
  loadPixels( );
  for( int dx = 0; dx < pixelWidth; ++dx ) {
    
    for( int dy = 0; dy < pixelHeight; ++dy ) {
      
      cx = 1.0 * dx / pixelWidth;
      cy = 1.0 * dy / pixelHeight;
      
      cord.a = cartDist( 0, 0, cx, cy );
      cord.b = cartDist( 0, 1, cx, cy );
      cord.c = cartDist( 1, 0, cx, cy );
      
      func( cord, vals );
      
      if( vals.a > vals.b && vals.b > vals.c && vals.c > vals.d ) {
        c = palette[ 0 ];
      } else if( vals.a > vals.b && vals.b > vals.d && vals.d > vals.c ) {
        c = palette[ 1 ];
      } else if( vals.a > vals.c && vals.c > vals.b && vals.b > vals.d ) {
        c = palette[ 2 ];
      } else if( vals.a > vals.c && vals.c > vals.d && vals.d > vals.b ) {
        c = palette[ 3 ];
      } else if( vals.a > vals.d && vals.d > vals.b && vals.b > vals.c ) {
        c = palette[ 4 ];
      } else if( vals.a > vals.d && vals.d > vals.c && vals.c > vals.b ) {
        c = palette[ 5 ];
      } else if( vals.b > vals.a && vals.a > vals.c && vals.c > vals.d ) {
        c = palette[ 6 ];
      } else if( vals.b > vals.a && vals.a > vals.d && vals.d > vals.c ) {
        c = palette[ 7 ];
      } else if( vals.b > vals.c && vals.c > vals.a && vals.a > vals.d ) {
        c = palette[ 8 ];
      } else if( vals.b > vals.c && vals.c > vals.d && vals.d > vals.a ) {
        c = palette[ 9 ];
      } else if( vals.b > vals.d && vals.d > vals.a && vals.a > vals.c ) {
        c = palette[ 10 ];
      } else if( vals.b > vals.d && vals.d > vals.c && vals.c > vals.a ) {
        c = palette[ 11 ];
      } else if( vals.c > vals.a && vals.a > vals.b && vals.b > vals.d ) {
        c = palette[ 12 ];
      } else if( vals.c > vals.a && vals.a > vals.d && vals.d > vals.b ) {
        c = palette[ 13 ];
      } else if( vals.c > vals.b && vals.b > vals.a && vals.a > vals.d ) {
        c = palette[ 14 ];
      } else if( vals.c > vals.b && vals.b > vals.d && vals.d > vals.a ) {
        c = palette[ 15 ];
      } else if( vals.c > vals.d && vals.d > vals.a && vals.a > vals.b ) {
        c = palette[ 16 ];
      } else if( vals.c > vals.d && vals.d > vals.b && vals.b > vals.a ) {
        c = palette[ 17 ];
      } else if( vals.d > vals.a && vals.a > vals.b && vals.b > vals.c ) {
        c = palette[ 18 ];
      } else if( vals.d > vals.a && vals.a > vals.c && vals.c > vals.b ) {
        c = palette[ 19 ];
      } else if( vals.d > vals.b && vals.b > vals.a && vals.a > vals.c ) {
        c = palette[ 20 ];
      } else if( vals.d > vals.b && vals.b > vals.c && vals.c > vals.a ) {
        c = palette[ 21 ];
      } else if( vals.d > vals.c && vals.c > vals.a && vals.a > vals.b ) {
        c = palette[ 22 ];
      } else {
        c = palette[ 23 ];
      }
      
      pixels[ dy * pixelWidth + dx ] = c;
      
    }
    
  }
  updatePixels( );
  
  noLoop( );
  
}

void draw( ) {
  
  
  
}
