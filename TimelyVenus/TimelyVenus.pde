import java.util.Random;

// Framework for drawing different areas in different ways
// Objects like this:
//   Radial stroke
//     See desktop for example of this one
//     Center
//     Arc start ang
//     Arc end ang
//     Density radially
//     Length of stroke

final Random rng = new Random( );

final float A_LEN = 5.0;

class DenseArc {
  
  private float sAng;
  private float eAng;
  
  private PVector anchor;
  
  private float density;
  
  public DenseArc( float sAng, float eAng, PVector anchor, float density ) {
    
    this.anchor = new PVector( );
    this.anchor.set( anchor );
    
    this.sAng = sAng;
    this.eAng = eAng;
    
    this.density = density;
    
  }
  
  public void draw( ) {
    
    noFill( );
    stroke( 0, 0, 0, 255 );
    
    for( int idx = 0; idx < density * ( eAng - sAng ); ++idx ) {
      
      float rad = rng.nextFloat( ) * pixelHeight / 2.0;
      float ang = rng.nextFloat( ) * ( this.eAng - this.sAng - A_LEN ) + this.sAng;
      
      PVector axis = new PVector( rad, 0.0 );
      axis.rotate( ang );
      
      PVector arm = new PVector( );
      arm.set( axis );
      arm.rotate( PI / 2.0 );
      arm.setMag( A_LEN / 2.0 );
      
      PVector lArm = new PVector( );
      lArm.set( axis );
      lArm.add( arm );
      PVector rArm = new PVector( );
      lArm.set( axis );
      lArm.sub( arm );
      
      line( ax, ay, bx, by );
      
    }
    
  }
  
}

DenseArc d;

void setup( ) {
  
  size( 1280, 720 );
  background( 255 );
  
  d = new DenseArc( 0.785, 3.927, new PVector( pixelWidth / 2.0, pixelHeight / 2.0 ), 10 );
  
  d.draw( );
  
  noLoop( );
  
}

void draw( ) {
  
  
  
}