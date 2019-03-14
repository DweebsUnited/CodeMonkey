package CodeMonkey.project;

import java.util.Random;

import processing.core.PApplet;

public class LivingWave extends ProjectBase {

  public static void main( String[] args ) {

    PApplet.main( "CodeMonkey.project.LivingWave" );

  }

  private Random rng = new Random( );

  final int sWidth = 720;
  final int sHeigh = 640;

  float[] bufA, bufB;

  float[] calcP( float[] n ) {

    float[] res = new float[ 9 ];

    for( int rdx = 0; rdx < 9; ++rdx )
      res[ rdx ] = 0;

    for( int cdx = 0; cdx < 256; ++cdx ) {

      float accum = 1;
      int bCnt = 0;

      for( int bdx = 0; bdx < 8; ++bdx ) {

        if( ( ( cdx >> bdx ) & 0x01 ) > 0 ) {

          bCnt += 1;
          accum *= n[ bdx ];

        } else {

          accum *= ( 1 - n[ bdx ] );

        }

      }

      res[ bCnt ] += accum;

    }

    return res;

  }

  @Override
  public void settings( ) {

    this.size( this.sWidth, this.sHeigh );
    this.setName( );

  }

  @Override
  public void setup( ) {

    this.bufA = new float[ this.sWidth * this.sHeigh ];
    this.bufB = new float[ this.sWidth * this.sHeigh ];

    for( int cdx = 0; cdx < this.sWidth * this.sHeigh; ++cdx ) {

      this.bufA[ cdx ] = this.rng.nextFloat( );
      this.bufB[ cdx ] = this.bufA[ cdx ];

    }

  }

  @Override
  public void draw( ) {

    if( this.frameCount % 10 == 0 ) {

      for( int ydx = 0; ydx < this.sHeigh; ++ydx ) {
        for( int xdx = 0; xdx < this.sWidth; ++xdx ) {

          // Construct array of 8 neighbors, run probability calculator
          float[] neigh = new float[ 8 ];
          int ndx = 0;

          int ox = 0, oy = 0;
          for( int dy = -1; dy < 2; ++dy ) {

            if( ydx + dy < 0 )
              oy = 1;
            else if( ydx + dy >= this.sHeigh )
              oy = -1;
            else
              oy = 0;

            for( int dx = -1; dx < 2; ++dx ) {

              if( dy == 0 && dx == 0 )
                continue;

              // I think this will actually work...
              if( xdx + dx < 0 )
                ox = 1;
              else if( xdx + dx >= this.sWidth )
                ox = -1;
              else
                ox = 0;

              int cdx = ( ydx + dy + oy * this.sHeigh ) * this.sWidth + ( xdx + dx + ox * this.sWidth );

              neigh[ ndx++ ] = this.bufA[ cdx ];

            }
          }

          float[] ps = this.calcP( neigh );
          float pA =
              ( 1 - this.bufA[ ydx * this.sWidth + xdx ] ) * ps[ 3 ] +
              this.bufA[ ydx * this.sWidth + xdx ] * ( ps[ 2 ] + ps[ 3 ] );

          float pD =
              ( 1 - this.bufA[ ydx * this.sWidth + xdx ] ) * ( 1 - ps[ 3 ] ) +
              this.bufA[ ydx * this.sWidth + xdx ] * ( 1 - ( ps[ 2 ] + ps[ 3 ] ) );

          pA = pA / (float)Math.sqrt( pA * pA + pD * pD );

          this.bufB[ ydx * this.sWidth + xdx ] = pA;

        }
      }

      float[] swap = this.bufA;
      this.bufA = this.bufB;
      this.bufB = swap;

    }

    this.loadPixels( );
    for( int ydx = 0; ydx < this.sHeigh; ++ydx ) {
      for( int xdx = 0; xdx < this.sWidth; ++xdx ) {

        this.pixels[ ydx * this.sWidth + xdx ] = this.color( 255 * this.bufA[ ydx * this.sWidth + xdx ] );

      }
    }
    this.updatePixels( );

  }

}
