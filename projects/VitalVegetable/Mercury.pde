import java.util.Random;

class RandomWrapper {
  
  private Random rng;
  
  public RandomWrapper( ) {
    this.rng = new Random( );
  }
  
  public int nextInt( int n ) {
    return this.rng.nextInt( n );
  }
  
  public float nextFloat01( ) {
    return this.rng.nextFloat( );
  }
  
  public float nextFloat( ) {
    return ( this.rng.nextFloat( ) * 2 ) - 1;
  }
  
  public float nextGaussian( ) {
    return (float) this.rng.nextGaussian( );
  }
  
  public boolean yesno( ) {
    return this.rng.nextBoolean( );
  }
  
  public PVector pickAlong( PVector a, PVector b, Distribution d ) {
    
    return PVector.lerp( a, b, d.next( this ) );
    
  }
  
  public int nextX( ) {
    return this.nextInt( pixelWidth ) + 1;
  }
  
  public int nextY( ) {
    return this.nextInt( pixelHeight ) + 1;
  }
  
  public PVector nextPos( ) {
    return new PVector( this.nextX( ), this.nextY( ) );
  }
  
  public PVector nextRota( float scale, float var ) {
    
    scale *= 2;
    
    float ang = this.nextFloat( ) * 2 * PI;
    
    int s = min( pixelWidth, pixelHeight );
    float rad = s / scale + this.nextGaussian( ) / var;
    
    return new PVector( rad * cos( ang ) + pixelWidth / 2, rad * sin( ang ) + pixelHeight / 2 );
  
  }
  
  public PVector nextPAtAng( float ang, float scale, float var ) {
    
    scale *= 2;
    
    int s = min( pixelWidth, pixelHeight );
    float rad = s / scale + this.nextGaussian( ) / var;
    
    return new PVector( rad * cos( ang ) + pixelWidth / 2, rad * sin( ang ) + pixelHeight / 2 );
    
  }
  
  public float minMax( float rng, float min ) {
    return ( this.nextFloat( ) * 2 - 1 ) * rng + min;
  }
  
  public PVector minMaxVec( float rng, float min ) {
    return new PVector( ( this.nextFloat( ) * 2 - 1 ) * rng + min, ( this.nextFloat( ) * 2 - 1 ) * rng + min );
  }
  
}

interface Distribution {
  public float next( RandomWrapper rng );
}

class Uniform01Distribution implements Distribution {
  
  public Uniform01Distribution( ) { }
  
  public float next( RandomWrapper rng ) {
  
    return rng.nextFloat01( );
  
  }
  
}

class UniformDistribution implements Distribution {
  
  float min;
  float rng;
  
  public UniformDistribution( float min, float max ) {
    
    this.min = min;
    this.rng = max - min;
    
  }
  
  public float next( RandomWrapper rng ) {
  
    return rng.nextFloat01( ) * this.rng + this.min;
  
  }
  
}

class Gaussian01Distribution implements Distribution {
  
  private float var;
  
  public Gaussian01Distribution( float var ) {
    
    this.var = 3.0 / var;
    
  }
  
  public float next( RandomWrapper rng ) {
  
    return (float) ( rng.nextGaussian( ) / this.var + 0.5 );
  
  }
  
}

class SandPainter {
  
  final int N_PTS;
  
  RandomWrapper rng;
  
  public SandPainter( RandomWrapper rng, int n_pts ) {
  
    this.rng = rng;
    this.N_PTS = n_pts;
  
  }
  
  public void prepare( ) {
    loadPixels( );
  }
  
  public void pLine( PVector p1, PVector p2, color c, float alpha, Distribution d ) {
    
    noStroke( );
    noFill( );
    
    for( int idx = 0; idx < this.N_PTS; ++idx ) {
      
      float a = d.next( this.rng );
      
      PVector p = PVector.lerp( p1, p2, a );
      
      int i = int( p.y ) * pixelWidth + int( p.x );
      
      if( i < 0 ) {
        -- idx;
        continue;
      } if( i >= pixelWidth * pixelHeight ) {
        -- idx;
        continue;
      }
      
      pixels[ i ] = lerpColor( pixels[ i ], c, alpha );
    
    }
    
  }
  
  public void save( ) {
    updatePixels( );
  }
  
}