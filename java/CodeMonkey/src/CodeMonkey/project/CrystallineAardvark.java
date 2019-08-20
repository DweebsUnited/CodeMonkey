package CodeMonkey.project;

public class CrystallineAardvark extends ProjectBase {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	void makeRand( ) {
	  
	  for( int dx = 0; dx < nRand; ++dx ) {
	    
	    rarry1[ dx ] = (float)rng.nextGaussian( ) * 0.1 + 1;
	    rarry2[ dx ] = rng.nextFloat( ) * 2.0 * PI;
	    rarry3[ dx ] = rng.nextFloat( ) * 0.2 - 0.1;
	    rarry4[ dx ] = (float)rng.nextGaussian( ) * 0.1 + 0.5;
	    
	  }
	  
	  shdr.set( "rand1", rarry1 );
	  shdr.set( "rand2", rarry2 );
	  shdr.set( "rand3", rarry3 );
	  shdr.set( "rand4", rarry4 );
	  
	}
	
	float sig( float s, float c, float x ) {
	
	  float a = 1.0f / ( 1.0f + exp( - s * ( 0.0f - c ) ) );
	  float b = 1.0f / ( 1.0f + exp( - s * ( 1.0f - c ) ) );
	  float t;
	
	  if( a > b ) {
	    t = a;
	    a = b;
	    b = t;
	  }
	
	  t = 1.0f / ( 1.0f + exp( - s * ( x - c ) ) );
	
	  return ( t - a ) * 1.0f / ( b - a );
	
	}
	
	float fmap( float x, float in_min, float in_max, float out_min, float out_max ) {
	  return ( x - in_min ) * ( out_max - out_min ) / ( in_max - in_min ) + out_min;
	}

}