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

  boolean[] bufA, bufB;
  int[] bufN;

  @Override
  public void settings( ) {

    this.size( this.sWidth, this.sHeigh );
    this.setName( );

  }

  @Override
  public void setup( ) {

    this.bufA = new boolean[ this.sWidth * this.sHeigh ];
    this.bufB = new boolean[ this.sWidth * this.sHeigh ];
    this.bufN = new int[ this.sWidth * this.sHeigh ];

    for( int cdx = 0; cdx < this.sWidth * this.sHeigh; ++cdx ) {

      this.bufA[ cdx ] = this.rng.nextFloat( ) > 0.5f;

    }

  }

  @Override
  public void draw( ) {

    if( this.frameCount % 10 == 0 ) {

      for( int ydx = 0; ydx < this.sHeigh; ++ydx ) {
        for( int xdx = 0; xdx < this.sWidth; ++xdx ) {

          this.bufN[ ydx * this.sWidth + xdx ] = 0;

        }
      }

      for( int ydx = 0; ydx < this.sHeigh; ++ydx ) {
        for( int xdx = 0; xdx < this.sWidth; ++xdx ) {

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

              this.bufN[ cdx ] +=
                  this.bufA[ ydx * this.sWidth + xdx ] ? 1 : 0;

            }
          }

        }
      }

      for( int ydx = 0; ydx < this.sHeigh; ++ydx ) {
        for( int xdx = 0; xdx < this.sWidth; ++xdx ) {

          if( this.bufA[ ydx * this.sWidth + xdx ] )
            this.bufB[ ydx * this.sWidth + xdx ] = this.bufN[ ydx * this.sWidth + xdx ] == 2 || this.bufN[ ydx * this.sWidth + xdx ] == 3;
          else
            this.bufB[ ydx * this.sWidth + xdx ] = this.bufN[ ydx * this.sWidth + xdx ] == 3;

        }
      }

      boolean[] swap = this.bufA;
      this.bufA = this.bufB;
      this.bufB = swap;

    }

    this.loadPixels( );
    for( int ydx = 0; ydx < this.sHeigh; ++ydx ) {
      for( int xdx = 0; xdx < this.sWidth; ++xdx ) {

        this.pixels[ ydx * this.sWidth + xdx ] = this.bufA[ ydx * this.sWidth + xdx ] ? this.color( 255 ) : this.color( 0 );

      }
    }
    this.updatePixels( );

  }

}
