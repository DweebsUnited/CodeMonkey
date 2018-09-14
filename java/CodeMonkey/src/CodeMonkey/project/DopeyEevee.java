package CodeMonkey.project;

import java.util.ArrayList;

import CodeMonkey.transform.CoordinateTransform;
import processing.core.PApplet;
import processing.core.PVector;

public class DopeyEevee extends PApplet {

  private static final int N_PIPE = 3;

  private class PipelineTransform {

    private CoordinateTransform transform;
    private int a, b;

    public PipelineTransform( int a, int b ) {

      this.a = a;
      this.b = b;

    }

    public void transform( PVector[] pipeline ) {

      if( this.b < 0 ) {

        pipeline[ this.a ].set( this.transform.map( pipeline[ this.a ] ) );

      } else {



      }

    }

  }

  ArrayList<CoordinateTransform> pipeline

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.DopeyEevee" );

  }

  @Override
  public void settings( ) {

    this.size( 720, 640 );

  }

  @Override
  public void setup( ) {



  }

  @Override
  public void draw( ) {



  }

}
