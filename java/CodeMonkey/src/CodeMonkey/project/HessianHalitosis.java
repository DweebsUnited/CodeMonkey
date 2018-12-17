package CodeMonkey.project;

import java.util.Random;

import CodeMonkey.transform.AxisTransform;
import CodeMonkey.transform.axis.ATLinear;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class HessianHalitosis extends ProjectBase {

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.HessianHalitosis" );

  }

  final int pxScale = 25;

  private Random rng = new Random( );

  private PGraphics canvas;
  private int cWidth = 1920;
  private int cHeigh = 1080;

  private AxisTransform toNegPos = new ATLinear( 0, 1, -1, 1 );

  private int[] palette;

  @Override
  public void settings( ) {

    this.size( 720, 640 );

    this.setName( );

  }

  @Override
  public void setup( ) {

    this.canvas = this.createGraphics( this.cWidth, this.cHeigh );

    this.canvas.beginDraw( );
    this.canvas.background( 0 );
    this.canvas.endDraw( );

    this.palette = new int[] {
        this.color( 72, 40, 47 ),
        this.color( 68, 82, 99 ),
        this.color( 48, 56, 60 ),
        this.color( 93, 92, 113 ),
        this.color( 224, 188, 85 ),
    };

  }

  @Override
  public void draw( ) {

    this.canvas.beginDraw( );

    // Pick random point
    PVector x = new PVector( this.rng.nextFloat( ) * this.cWidth, this.rng.nextFloat( ) * this.cHeigh );
    // Find flow dir
    float angle = (float)Math.atan2(
        this.toNegPos.map( this.noise( x.x * 0.0075f, x.y * 0.0075f, 0 ) ),
        this.toNegPos.map( this.noise( x.x * 0.0075f, x.y * 0.0075f, 5 ) ) );
    // Draw a rectangle longer in the length directional than it is wide in the widthagonal directional
    float l = (float)Math.pow( this.rng.nextFloat( ), 1.0f / 4 ) * this.pxScale;
    float w = (float)Math.pow( this.rng.nextFloat( ), 4.0f / 1 ) * this.pxScale;

    this.canvas.noStroke( );

    this.canvas.fill( this.palette[ this.rng.nextInt( this.palette.length ) ] );

    this.canvas.pushMatrix( );

    this.canvas.rotate( angle );
    this.canvas.translate( x.x, x.y );

    this.canvas.rect( 0, 0, l, w );

    this.canvas.popMatrix( );

    //    this.canvas.noFill( );
    //    this.canvas.stroke( 0 );

    //    this.canvas.line(
    //        x.x + f.x + s.x, x.y + f.y + s.y,
    //        x.x + f.x + s.x, x.y + f.y - s.y );
    //    this.canvas.line(
    //        x.x + f.x + s.x, x.y + f.y - s.y,
    //        x.x + f.x - s.x, x.y + f.y - s.y );
    //    this.canvas.line(
    //        x.x + f.x - s.x, x.y + f.y - s.y,
    //        x.x + f.x - s.x, x.y + f.y + s.y );
    //    this.canvas.line(
    //        x.x + f.x - s.x, x.y + f.y + s.y,
    //        x.x + f.x + s.x, x.y + f.y + s.y );

    //    this.canvas.stroke( 255, 0, 0 );
    //
    //    this.canvas.line( x.x, x.y, x.x + f.x * 25, x.y + f.y * 25 );
    //
    //    this.canvas.stroke( 0, 255, 0 );
    //
    //    this.canvas.line( x.x, x.y, x.x + s.x * 25, x.y + s.y * 25 );

    this.canvas.endDraw( );

    this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );

  }

  @Override
  public void keyPressed( ) {

    if( this.key == 'w' )
      this.save( this.canvas );

  }

}
