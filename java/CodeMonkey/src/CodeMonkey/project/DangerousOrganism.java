package CodeMonkey.project;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class DangerousOrganism extends ProjectBase {

  public static void main( String[] args ) {

    PApplet.main( "CodeMonkey.project.DangerousOrganism" );

  }

  final float S_STEP = 1f / this.frameRate;
  // Grid size ( Square )
  final int N_GRID = 16;

  PGraphics canvas;
  int cWidth = 720;
  int cHeigh = 640;

  float squeeze = 0f;

  int mSz, gPxSize;
  int xMar, yMar;
  int gPxCell, gPxCellHlf;
  float gCornerLen;
  PVector[][] g;

  @Override
  public void settings( ) {

    this.size( 720, 640 );

    this.setName( );

  }

  @Override
  public void setup( ) {

    this.canvas = this.createGraphics( this.cWidth, this.cHeigh );

    // Grid size is going to be 90% min canvas size
    this.mSz = min( this.cWidth, this.cHeigh );
    this.gPxSize = (int)Math.floor( 0.9f * this.mSz );

    // Margins
    this.xMar = ( this.cWidth - this.gPxSize ) / 2;
    this.yMar = ( this.cHeigh - this.gPxSize ) / 2;

    // Pixels per cell and halfcell
    this.gPxCell = this.gPxSize / this.N_GRID;
    this.gPxCellHlf = this.gPxCell / 2;

    // Hypotenuuuuuuuuuuuuuuse
    this.gCornerLen = (float)Math.sqrt( 2f ) * this.gPxCellHlf;

    // Now create the grid
    this.g = new PVector[ this.N_GRID ][ this.N_GRID ];
    for( int ydx = 0; ydx < this.N_GRID; ++ydx ) {
      for( int xdx = 0; xdx < this.N_GRID; ++xdx ) {

        this.g[ ydx ][ xdx ] = new PVector(
            this.xMar + this.gPxCellHlf + this.gPxCell * xdx,
            this.yMar + this.gPxCellHlf + this.gPxCell * ydx );

      }
    }

  }

  @Override
  public void draw( ) {

    // Draw some griddos
    this.canvas.beginDraw( );
    this.canvas.background( 0 );
    this.canvas.noFill( );
    this.canvas.stroke( 255 );

    for( int ydx = 0; ydx < this.N_GRID; ++ydx ) {
      for( int xdx = 0; xdx < this.N_GRID; ++xdx ) {

        // 4 corners
        // We're just going to unwrap the loop.. Because I give 0 shits now
        PVector TR = new PVector( this.gCornerLen, 0 ).rotate( 1 * PI / 4 ).add( this.g[ ydx ][ xdx ] );
        PVector TL = new PVector( this.gCornerLen, 0 ).rotate( 3 * PI / 4 ).add( this.g[ ydx ][ xdx ] );
        PVector BL = new PVector( this.gCornerLen, 0 ).rotate( 5 * PI / 4 ).add( this.g[ ydx ][ xdx ] );
        PVector BR = new PVector( this.gCornerLen, 0 ).rotate( 7 * PI / 4 ).add( this.g[ ydx ][ xdx ] );

        this.canvas.line( TR.x, TR.y, TL.x, TL.y );
        this.canvas.line( TL.x, TL.y, BL.x, BL.y );
        this.canvas.line( BL.x, BL.y, BR.x, BR.y );
        this.canvas.line( BR.x, BR.y, TR.x, TR.y );

      }
    }

    this.canvas.endDraw( );

    // Draw the canvas
    this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );

    // Restrict the squeeeeeeeeeeeeeeeeeeeeeeze
    this.squeeze -= this.S_STEP;

  }

  @Override
  public void mouseClicked( ) {

  }

}
