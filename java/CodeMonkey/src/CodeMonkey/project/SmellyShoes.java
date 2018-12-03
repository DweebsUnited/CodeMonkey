package CodeMonkey.project;

import java.util.ArrayList;
import java.util.function.BiFunction;

import CodeMonkey.lindenmayer.RecursiveRewrite;
import processing.core.PApplet;
import processing.core.PGraphics;

public class SmellyShoes extends PApplet {

  private static String CM = "/Users/ozzy/Documents/CodeMonkey/";
  private static String dataDir = CM + "data/";

  private PGraphics canvas;

  private RecursiveRewrite curve;

  private class State {

    public PGraphics canvas;
    public int dx, dy;
    public int facing;

    public float hue;

  }
  private State s;

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.SmellyShoes" );

  }

  // Recursive Rewrite system:
  // Hilbert curve
  //   Alphabet: A, B
  //   Constants: F, +, - (Turtle: Forward, Right, Left)
  //   Axiom: A
  //   Rules:
  //     A -> - B F + A F A + F B -
  //     B -> + A F - B F B - F A +

  @Override
  public void settings( ) {

    this.size( 720, 640 );

  }

  @Override
  public void setup( ) {

    this.canvas = this.createGraphics( 2048, 2048 );
    this.canvas.beginDraw( );
    this.canvas.background( 0 );
    this.canvas.endDraw( );

    this.colorMode( HSB, 100, 100, 100 );

    this.s = new State( );
    this.s.canvas = this.canvas;
    this.s.dx = this.canvas.width - 1;
    this.s.dy = 0;
    this.s.facing = 2; // Facing left

    this.curve = new RecursiveRewrite(
        "AB",
        "F+-",
        new String[] {
            "-BF+AFA+FB-",
            "+AF-BFB-FA+"
        }
        );

    ArrayList<BiFunction<State,Character,State>> clist = new ArrayList<BiFunction<State,Character,State>>( );
    clist.add( ( s, c ) -> { // F

      switch( s.facing ) {
        case 0:
          s.dx += 1;
          break;
        case 1:
          s.dy -= 1;
          break;
        case 2:
          s.dx -= 1;
          break;
        case 3:
          s.dy += 1;
          break;
      }

      if( s.dx >= 0 && s.dx < s.canvas.width && s.dy >= 0 && s.dy < s.canvas.height )
        s.canvas.pixels[ s.dx + s.dy * s.canvas.width ] = this.color( s.hue, 100, 100 );

      s.hue += 100f / ( s.canvas.width * s.canvas.height );

      return s;

    } );
    clist.add( ( s, c ) -> { // +
      s.facing = ( s.facing - 1 + 4 ) % 4;
      return s;
    } );
    clist.add( ( s, c ) -> { // -
      s.facing = ( s.facing + 1 ) % 4;
      return s;
    } );

    this.canvas.beginDraw( );
    this.canvas.loadPixels( );

    this.curve.generate(
        "A",
        10,
        this.s,
        clist );

    this.canvas.updatePixels( );
    this.canvas.endDraw( );

    this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );
    this.noLoop( );

    this.canvas.save( dataDir += "SmellyShoes.png" );

  }

  @Override
  public void draw( ) {

  }

}
