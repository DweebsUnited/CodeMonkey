import java.util.Random;
Random rng = new Random( );

PVector a, b, c;
float da, db, dc;

final int N_LINE = 128;
final int N_ELLIPSE = 256;
final int E_RAD = 1;

final int MIN_OPA = 5;
final int MAX_OPA = 55;

void setup( ) {
  
  size( 720, 720 );
  
  a = new PVector( rng.nextInt( pixelWidth ) + 1, rng.nextInt( pixelHeight ) + 1 );
  b = new PVector( rng.nextInt( pixelWidth ) + 1, rng.nextInt( pixelHeight ) + 1 );
  c = new PVector( rng.nextInt( pixelWidth ) + 1, rng.nextInt( pixelHeight ) + 1 );
  
  da = rng.nextFloat( ) * ( MAX_OPA - MIN_OPA ) + MIN_OPA;
  db = rng.nextFloat( ) * ( MAX_OPA - MIN_OPA ) + MIN_OPA;
  dc = rng.nextFloat( ) * ( MAX_OPA - MIN_OPA ) + MIN_OPA;
  
  background( 0 );
  noStroke( );
  
  for( int adx = 0; adx < N_LINE; ++adx ) {
    
    PVector x = new PVector( a.x, a.y );
    PVector y = new PVector( b.x, b.y );
    x.lerp( b, rng.nextFloat( ) );
    y.lerp( c, rng.nextFloat( ) );
    
    for( int bdx = 0; bdx < N_ELLIPSE; ++bdx ) {
    
      PVector z = new PVector( x.x, x.y );
      z.lerp( y, rng.nextFloat( ) );
      ellipse( z.x, z.y, E_RAD, E_RAD );
      
    }
    
  }
  
  for( int adx = 0; adx < N_LINE; ++adx ) {
    
    PVector x = new PVector( b.x, b.y );
    PVector y = new PVector( c.x, c.y );
    x.lerp( c, rng.nextFloat( ) );
    y.lerp( a, rng.nextFloat( ) );
    
    for( int bdx = 0; bdx < N_ELLIPSE; ++bdx ) {
    
      PVector z = new PVector( x.x, x.y );
      z.lerp( y, rng.nextFloat( ) );
      ellipse( z.x, z.y, E_RAD, E_RAD );
      
    }
    
  }
  
  for( int adx = 0; adx < N_LINE; ++adx ) {
    
    PVector x = new PVector( c.x, c.y );
    PVector y = new PVector( a.x, a.y );
    x.lerp( a, rng.nextFloat( ) );
    y.lerp( b, rng.nextFloat( ) );
    
    for( int bdx = 0; bdx < N_ELLIPSE; ++bdx ) {
    
      PVector z = new PVector( x.x, x.y );
      z.lerp( y, rng.nextFloat( ) );
      ellipse( z.x, z.y, E_RAD, E_RAD );
      
    }
    
  }
  
  noLoop( );
  
}