import java.util.Random;

final int N_ARCS = 64;

final int N_WAVE = 3;

final float MAG_OFF = 100;
final float SCALE_A = 75;
final float SCALE_B = 1.0 / ( 2 * PI );
final float SCALE_C = 2 * PI;
final float SCALE_T = 2;

float[] sA = new float[ N_WAVE ];
float[] sB = new float[ N_WAVE ];
float[] sC = new float[ N_WAVE ];

CircularBuffer arcs;

Random rng = new Random( System.currentTimeMillis( ) );

PVector anchor;

void setup( ) {
  
  size( 800, 600 );
  frameRate( 30 );
  
  background( 255 );
  
  noFill( );
  stroke( 0, 25 );
  
  for( int wdx = 0; wdx < N_WAVE; ++wdx ) {
    
    sA[ wdx ] = rng.nextFloat( ) * SCALE_A;
    sB[ wdx ] = rng.nextFloat( ) * SCALE_B;
    sC[ wdx ] = rng.nextFloat( ) * SCALE_C;
    
  }
  
  anchor = new PVector( pixelWidth / 2.0, pixelHeight / 2.0 );
  
  arcs = new CircularBuffer( N_ARCS );
  
}

void draw( ) {
  
  noStroke( );
  fill( 255, 25 );
  rect( 0, 0, pixelWidth, pixelHeight );
  noFill( );
  stroke( 0, 75 );

  PVector a = new PVector( 1, 0 );
    
  float mag = 0;
  for( int wdx = 0; wdx < N_WAVE; ++wdx ) {
    
    mag += sA[ wdx ] * sin( sB[ wdx ] * ( frameCount * SCALE_T ) / (float)frameRate + sC[ wdx ] );
    
    if( rng.nextFloat( ) > rng.nextFloat( ) )
      sA[ wdx ] = rng.nextFloat( ) * SCALE_A;
    if( rng.nextFloat( ) > rng.nextFloat( ) )
      sB[ wdx ] = rng.nextFloat( ) * SCALE_B;
    if( rng.nextFloat( ) > rng.nextFloat( ) )
      sC[ wdx ] = rng.nextFloat( ) * SCALE_C;
    
  }
  arcs.put( mag + MAG_OFF );
  
  PVector n = new PVector( 1, 0 );
  
  n.setMag( arcs.get( 0 ) );
  
  for( int adx = 0; adx < N_ARCS; ++adx ) {
    
    a.set( n );
    n.rotate( 2 * PI / N_ARCS );
    
    n.setMag( arcs.get( adx + 1 ) );
    
    line( anchor.x + a.x, anchor.y + a.y, anchor.x + n.x, anchor.y + n.y ); 
    
  }
  
}