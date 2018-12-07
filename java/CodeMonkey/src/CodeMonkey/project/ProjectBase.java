package CodeMonkey.project;

import processing.core.PApplet;
import processing.core.PGraphics;

public class ProjectBase extends PApplet {

  private static String CM = "/Users/ozzy/Documents/CodeMonkey/";
  private static String CMW = "C:\\Users\\ElysiumTech\\Documents\\Personal\\CodeMonkey\\";
  protected static String dataDir = CM + "data/";
  private String name = "DICKSYOUFORGOTTOsetName";

  protected void setName( ) {

    this.name = this.getClass( ).getSimpleName( );

    if( System.getProperty( "os.name" ).toLowerCase( ).contains( "windows" ) ) {
      ProjectBase.dataDir = CMW + "data/";
    }

  }

  protected void save( PGraphics canvas ) {

    this.save( canvas, ".png" );

  }

  protected void save( PGraphics canvas, int sqx ) {

    this.save( canvas, "_" + Integer.toString( sqx ) + ".png" );

  }

  protected void save( PGraphics canvas, String ext ) {

    canvas.save( dataDir + this.name + ext );

  }

}
