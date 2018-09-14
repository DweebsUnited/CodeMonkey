package CodeMonkey.project;

import CodeMonkey.transform.AxisTransform;
import CodeMonkey.transform.NormalAxisTransform;
import CodeMonkey.transform.SigmoidTransform;
import processing.core.PApplet;

public class Test extends PApplet {

  private AxisTransform trans = new NormalAxisTransform( new SigmoidTransform( 6.0f, 0.5f ), 1, 0 );

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

    for (int i = 0; i <= this.pixelWidth; i++) {

      float x = i / (float) this.pixelWidth;

      float y = this.trans.map( x );

      this.ellipse( x * this.pixelWidth, ( 1 - y ) * this.pixelHeight, 3, 3 );

    }

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
