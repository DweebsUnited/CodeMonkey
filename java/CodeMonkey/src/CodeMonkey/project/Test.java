package CodeMonkey.project;

import CodeMonkey.transform.CoordinateTransform;
import CodeMonkey.transform.color.ColorCoordinateTransform;
import CodeMonkey.transform.color.CoordinateHue;
import CodeMonkey.transform.coordinate.CTLinear;
import processing.core.PApplet;
import processing.core.PVector;

public class Test extends PApplet {

  private ColorCoordinateTransform trans = new CoordinateHue( );

  CoordinateTransform ohoneToNegpos = new CTLinear( new PVector( -1, -1, -1 ), new PVector( 2, 2, 2 ) );

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

    for( int xdx = 0; xdx < this.pixelWidth; ++xdx ) {

      for( int ydx = 0; ydx < this.pixelHeight; ++ydx ) {

        PVector pos = this.ohoneToNegpos.map( new PVector(
            xdx / (float) this.pixelWidth,
            ydx / (float) this.pixelHeight,
            0.5f
            ) );

        //        System.out.println( String.format( "%f, %f, %f", pos.x, pos.y, pos.z ) );

        this.pixels[ xdx + ydx * this.pixelWidth ] = this.trans.map(
            this,
            pos );

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
