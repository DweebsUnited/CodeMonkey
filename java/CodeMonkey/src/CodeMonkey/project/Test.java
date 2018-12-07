package CodeMonkey.project;

import processing.core.PApplet;
import processing.core.PVector;

public class Test extends PApplet {

  public static void main( String [ ] args ) {

    PApplet.main( "CodeMonkey.project.Test" );

  }

  @Override
  public void settings( ) {

    this.size( 720, 640 );

  }

  @Override
  public void setup( ) {

    this.background( 0 );

    this.noStroke( );
    this.fill( 255 );

    this.loadPixels( );

    float hW = this.pixelWidth / 2.0f;
    float hH = this.pixelHeight / 2.0f;

    PVector axis = new PVector( 1, 0 ).rotate( PI / 4 );

    for( int xdx = 0; xdx < this.pixelWidth; ++xdx ) {

      for( int ydx = 0; ydx < this.pixelHeight; ++ydx ) {

        PVector pos = new PVector( ( xdx - hW ) / hW, ( ydx - hH ) / hH );

        float cMag = ( pos.dot( axis ) + 1 ) / 2;
        this.pixels[ xdx + ydx * this.pixelWidth ] = this.color( cMag * 255 );

      }

    }

    this.updatePixels( );

    this.noLoop( );

  }

  @Override
  public void draw( ) {



  }

  @Override
  public void keyPressed( ) {

    if( this.key == ' ' ) {
      this.save( "/Users/ozzy/Desktop/Test.png" );
    }

  }

}
