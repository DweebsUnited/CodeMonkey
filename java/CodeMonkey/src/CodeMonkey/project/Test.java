package CodeMonkey.project;

import processing.core.PApplet;

public class Test extends PApplet {

  public static void main( String [ ] args ) {

    PApplet.main( "CodeMonkey.project.Test" );

  }

  @Override
  public void settings( ) {

    this.size( 720, 640, P3D );

  }

  @Override
  public void setup( ) {

    this.camera( 1, 1, 1, 0, 0, 0, 0, 0, 1 );
    this.ortho( -2, 2, -2, 2, 0.1f, 100 );

  }

  @Override
  public void draw( ) {

    this.beginShape( TRIANGLE );
    this.vertex( 1, 0, 0 );
    this.vertex( 0, 0, 0 );
    this.vertex( 1, 1, 0 );
    this.endShape( );

  }

}
