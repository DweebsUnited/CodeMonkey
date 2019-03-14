package CodeMonkey.project;

import CodeMonkey.spatial.PoissonSampler;
import processing.core.PApplet;
import processing.core.PVector;

public class Test extends PApplet {

  public static void main( String [ ] args ) {

    PApplet.main( "CodeMonkey.project.Test" );

  }

  int pdx = 0;

  PoissonSampler ps;

  float cellSize;

  @Override
  public void settings( ) {

    this.size( 106, 106 );

  }

  @Override
  public void setup( ) {

    this.cellSize = 10 / (float) Math.sqrt( 2 );

    this.background( 0 );

    this.stroke( 255, 0, 0 );
    this.noFill( );

    for( int cdx = 0; this.cellSize * cdx < this.pixelWidth; ++cdx ) {

      this.line( 0, cdx * this.cellSize, this.pixelWidth, cdx * this.cellSize );
      this.line( cdx * this.cellSize, 0, cdx * this.cellSize, this.pixelWidth );

    }

    this.noStroke( );
    this.fill( 255 );

    this.ps = new PoissonSampler( this.pixelWidth, this.pixelWidth, 10 );

    System.out.println( String.format( "%d -> %f", this.pixelWidth, this.ps.width ) );

    this.frameRate( 10 );

  }

  @Override
  public void draw( ) {

    PVector p = this.ps.sample.get( this.pdx ++ );

    this.ellipse( p.x, p.y, 5, 5 );

  }

  @Override
  public void keyPressed( ) {

  }

}
