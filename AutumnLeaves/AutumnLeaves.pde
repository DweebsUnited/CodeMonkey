// Idea: Have simple NN trained on an input image
//   Split 2^n image until you get to single pixels
//   Run NN at each level on: centerX, centerY, depthFactor, parentColor
// Color outputs: 1D / 2D Colormap, HSV, RGB

// Idea: Reverse-convolution
//   Each layer generates x children

import java.util.Random;

Random rng = new Random( );

double map( double v, double i, double I, double o, double O ) {
  return ( v - i ) / ( I - i ) * ( O - o ) + o;
}

PImage sn;

PGraphics canvas;
double[][][] canvasData;

Matrix input;
NN nn;
Matrix output;
Matrix truth;

// T-B inclusive ( 0-1023 for first level, 0-511|512-1023 for level 2 )
public void segmenter( double[][][] data, float dim, int T, int B, int L, int R, int depth, double[] parentData ) {
  
  // Centers always have a 0.5 -> boundaries round/floor
  float cY = ( T + B ) / 2f;
  float cX = ( L + R ) / 2f;
  
  input.data[ 0 ][ 0 ] = cX / dim;
  input.data[ 0 ][ 1 ] = cY / dim;
  input.data[ 0 ][ 2 ] = depth;
  input.data[ 0 ][ 3 ] = parentData[ 0 ];
  input.data[ 0 ][ 4 ] = parentData[ 1 ];
  input.data[ 0 ][ 5 ] = parentData[ 2 ];
  
  nn.apply( input, output ); 
  
  double[] ourData = output.data[ 0 ].clone( );
  
  if( R == L ) {
    
    // Base case: Apply color
    data[ T ][ R ] = ourData;
    
  } else {

    // 4x segments!
    // TL
    segmenter( data, dim, T, floor( cY ), L, floor( cX ), depth + 1, ourData );
    // TR
    segmenter( data, dim, T, floor( cY ), ceil( cX ), R, depth + 1, ourData );
    // BL
    segmenter( data, dim, ceil( cY ), B, L, floor( cX ), depth + 1, ourData );
    // BR
    segmenter( data, dim, ceil( cY ), B, ceil( cX ), R, depth + 1, ourData );
    
  }
  
}

public void segmentertrain( double[][][] data, Cost c, double r, float dim, int T, int B, int L, int R, int depth, double[] parentData ) {
  
  // Centers always have a 0.5 -> boundaries round/floor
  float cY = ( T + B ) / 2f;
  float cX = ( L + R ) / 2f;
  
  input.data[ 0 ][ 0 ] = cX / dim;
  input.data[ 0 ][ 1 ] = cY / dim;
  input.data[ 0 ][ 2 ] = depth;
  input.data[ 0 ][ 3 ] = parentData[ 0 ];
  input.data[ 0 ][ 4 ] = parentData[ 1 ];
  input.data[ 0 ][ 5 ] = parentData[ 2 ];
  
  if( R == L ) {
    truth.data[ 0 ][ 0 ] = data[ T ][ R ][ 0 ];
    truth.data[ 0 ][ 1 ] = data[ T ][ R ][ 1 ];
    truth.data[ 0 ][ 2 ] = data[ T ][ R ][ 2 ];
  } else {
    truth.data[ 0 ][ 0 ] = (
      canvasData[ floor( cX ) ][ floor( cY ) ][ 0 ] +
      canvasData[ ceil( cX ) ][ floor( cY ) ][ 0 ] +
      canvasData[ floor( cX ) ][ ceil( cY ) ][ 0 ] +
      canvasData[ ceil( cX ) ][ ceil( cY ) ][ 0 ] ) / 4.0;
    truth.data[ 0 ][ 1 ] = (
      canvasData[ floor( cX ) ][ floor( cY ) ][ 1 ] +
      canvasData[ ceil( cX ) ][ floor( cY ) ][ 1 ] +
      canvasData[ floor( cX ) ][ ceil( cY ) ][ 1 ] +
      canvasData[ ceil( cX ) ][ ceil( cY ) ][ 1 ] ) / 4.0;
    truth.data[ 0 ][ 2 ] = (
      canvasData[ floor( cX ) ][ floor( cY ) ][ 2 ] +
      canvasData[ ceil( cX ) ][ floor( cY ) ][ 2 ] +
      canvasData[ floor( cX ) ][ ceil( cY ) ][ 2 ] +
      canvasData[ ceil( cX ) ][ ceil( cY ) ][ 2 ] ) / 4.0;
  }
  
  nn.train( input, output, truth, c, r );
  
  double[] ourData = output.data[ 0 ].clone( );
  
  if( R == L ) {
    
    // Base case for training: write last training result?
    data[ T ][ R ] = ourData;
    
  } else {

    // 4x segments!
    // TL
    segmentertrain( data, c, r, dim, T, floor( cY ), L, floor( cX ), depth + 1, ourData );
    // TR
    segmentertrain( data, c, r, dim, T, floor( cY ), ceil( cX ), R, depth + 1, ourData );
    // BL
    segmentertrain( data, c, r, dim, ceil( cY ), B, L, floor( cX ), depth + 1, ourData );
    // BR
    segmentertrain( data, c, r, dim, ceil( cY ), B, ceil( cX ), R, depth + 1, ourData );
    
  }
  
}

void randomizenn( ) {
  
  for( Layer l : nn.layers ) {
    for( int row = 0; row < l.w.rows; ++row ) {
      for( int col = 0; col < l.w.cols; ++col ) {
        l.w.data[ row ][ col ] = 2 * ( rng.nextFloat( ) - 0.5 );
      }
    }
  }
  
}

void trainnn( ) {
  
  System.out.println( "Starting train" );
  segmentertrain( canvasData, new MSE( ), 0.0005, 1023, 0, 1023, 0, 1023, 0, new double[] { 180, 50, 50 } );
  System.out.println( "Train done" );
  
  drawCanvas( );
  
}

void kickSegmenter( ) {
  
  segmenter( canvasData, 1023, 0, 1023, 0, 1023, 0, new double[] { 180, 50, 50 } );
  
  drawCanvas( );
  
}

void drawCanvas( ) {
  
  double Hmin = Float.POSITIVE_INFINITY;
  double Hmax = Float.NEGATIVE_INFINITY;
  double Smin = Float.POSITIVE_INFINITY;
  double Smax = Float.NEGATIVE_INFINITY;
  double Bmin = Float.POSITIVE_INFINITY;
  double Bmax = Float.NEGATIVE_INFINITY;
  for( int rdx = 0; rdx < 1024; ++rdx ) {
    for( int cdx = 0; cdx < 1024; ++cdx ) {
      
      if( canvasData[ rdx ][ cdx ][ 0 ] < Hmin )
        Hmin = canvasData[ rdx ][ cdx ][ 0 ];
      if( canvasData[ rdx ][ cdx ][ 0 ] > Hmax )
        Hmax = canvasData[ rdx ][ cdx ][ 0 ];
        
      if( canvasData[ rdx ][ cdx ][ 1 ] < Smin )
        Smin = canvasData[ rdx ][ cdx ][ 1 ];
      if( canvasData[ rdx ][ cdx ][ 1 ] > Smax )
        Smax = canvasData[ rdx ][ cdx ][ 1 ];
        
      if( canvasData[ rdx ][ cdx ][ 2 ] < Bmin )
        Bmin = canvasData[ rdx ][ cdx ][ 2 ];
      if( canvasData[ rdx ][ cdx ][ 2 ] > Bmax )
        Bmax = canvasData[ rdx ][ cdx ][ 2 ];
      
    }
  }
  
  canvas.beginDraw( );
  
  canvas.fill( color( 127 ) );
  
  canvas.loadPixels( );
  colorMode( HSB, 360, 100, 100 );
  
  for( int rdx = 0; rdx < 1024; ++rdx ) {
    for( int cdx = 0; cdx < 1024; ++cdx ) {
      
      canvas.pixels[ cdx + rdx * 1024 ] = color(
        (int) map( canvasData[ rdx ][ cdx ][ 0 ], Hmin, Hmax, 0, 360 ),
        (int) map( canvasData[ rdx ][ cdx ][ 1 ], Smin, Smax, 0, 100 ),
        (int) map( canvasData[ rdx ][ cdx ][ 2 ], Bmin, Bmax, 0, 100 )
        );
      
    }
  }
  
  canvas.updatePixels( );
  canvas.endDraw( );
  
}

void setup( ) {
  
  size( 1024, 1024 );
  
  canvas = createGraphics( 1024, 1024 );
  canvasData = new double[ 1024 ][ 1024 ][ 3 ];
  
  sn = loadImage( "StarryNight.jpg" );
  sn.resize( 1024, 1024 );
  sn.filter( POSTERIZE, 4 );
  sn.loadPixels( );
  for( int rdx = 0; rdx < 1024; ++rdx ) {
    for( int cdx = 0; cdx < 1024; ++cdx ) {
      
      canvasData[ rdx ][ cdx ][ 0 ] = hue( sn.pixels[ cdx + rdx * 1024 ] ) / 360;
      canvasData[ rdx ][ cdx ][ 1 ] = saturation( sn.pixels[ cdx + rdx * 1024 ] ) / 100;
      canvasData[ rdx ][ cdx ][ 2 ] = brightness( sn.pixels[ cdx + rdx * 1024 ] ) / 100;
      
    }
  }
  
  input = new Matrix( 1, 6 );
  nn = new NN( new int[] { 6, 64, 8, 16, 3 }, new Activation[] { new ReLu6( ), new ReLu6( ), new ReLu6( ), new Linear( ) } );
  output = new Matrix( 1, 3 );
  truth = new Matrix( 1, 3 );
  
  randomizenn( );
  
  kickSegmenter( );
  
  frameRate( 1 );
  
}

void draw( ) {
  
  background( 127 );
  image( canvas, 0, 0, pixelWidth, pixelHeight );
  
}

void keyPressed( ) {
  
  if( key == ' ' ) {
    
    randomizenn( );
  
    kickSegmenter( );
    
  } else if( key == 'q' ) {
    
    trainnn( );
    
    kickSegmenter( );
    
  } else if( key == 'z' ) {
    
    canvas.save( "AutumnLeaves.png" );
    
  }
  
}
