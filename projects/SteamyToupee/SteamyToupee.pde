import java.util.Random;
import java.util.List;
import java.util.ArrayList;
Random rng = new Random( System.currentTimeMillis( ) );

float aoff = rng.nextFloat( );
float boff = rng.nextFloat( );
float coff = rng.nextFloat( );
float doff = rng.nextFloat( );

class Sigmoid {

  private float s, a, b;

  public Sigmoid( ) {

    this.config( 0.001 );

  }

  public Sigmoid( float s ) {

    this.config( s );

  }

  public void config( float s ) {

    this.s = s;
    this.a = this.raw( 1 );
    this.b = this.raw( 0 );

    if( this.a < this.b ) {

      float t = this.a;
      this.a = this.b;
      this.b = t;

    }

  }

  private float raw( float x ) {

    return 1.0 / ( 1.0 + exp( - this.s * ( x - 0.5 ) ) );

  }

  public float run( float x ) {

    return ( this.raw( x ) - this.b ) / ( this.a - this.b );

  }

}

Sigmoid contrast = new Sigmoid( 16 );

class Pair {

  public int x, y;
  private int lx, ly;

  public Pair( ) {
    this.gen( );
  }

  public void gen( ) {
    float fc = frameCount / 30.0 / 5.0;
    this.lx = x;
    this.ly = y;
    this.x = round( contrast.run( noise( fc + rng.nextFloat( ) + aoff, fc + rng.nextFloat( ) + boff ) ) * pixelWidth  );
    this.y = round( contrast.run( noise( fc + rng.nextFloat( ) + coff, fc + rng.nextFloat( ) + doff ) ) * pixelHeight );
  }

  public void draw( ) {

    stroke( 0, 25 );
    strokeWeight( 3 );
    noFill( );
    ellipse( this.x, this.y, 8, 8 );

  }

}

List<Pair> circles = new ArrayList<Pair>( );
int cdx = 0;

void reset( ) {

  noiseSeed( System.currentTimeMillis( ) );

  circles.clear( );
  for( cdx = 0; cdx < 1024; ++cdx ) {
    circles.add( new Pair( ) );
  }
  cdx = 0;

}

void setup( ) {

  size( 1280, 720 );
  background( 255 );

  reset( );

}

void draw( ) {

  for( int udx = 0; udx < 16; ++udx ) {
    circles.get( cdx ).gen( );

    cdx = ( cdx + 1 ) % circles.size( );
  }

  background( 255 );
  for( Pair p : circles )
    p.draw( );

}

void keyPressed( ) {

  if( key == ' ' )
    saveFrame( );
  else if( key == 'q' )
    reset( );

}
