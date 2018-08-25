package CodeMonkey.project;

import processing.core.PApplet;

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

  }

  @Override
  public void draw( ) {

    this.background( 0 );

    this.noStroke( );
    this.fill( 255 );

    int steps = 10;
    for (int i = 0; i <= steps; i++) {
      float t = i / (float) steps;
      float x = this.bezierPoint(85, 10, 90, 15, t);
      float y = this.bezierPoint(20, 10, 90, 80, t);
      this.ellipse(x, y, 5, 5);
    }

  }

  @Override
  public void keyPressed( ) {

    if( this.key == ' ' ) {
      this.save( "/Users/ozzy/Desktop/Test.png" );
    }

  }

}
