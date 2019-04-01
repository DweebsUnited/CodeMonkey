package CodeMonkey.project;

import processing.core.PApplet;
import processing.core.PGraphics;


public class ProjectBase extends PApplet {

	protected static String CM = "/Users/ozzy/Documents/CodeMonkey/";
	private static String CMW = "C:\\Users\\eosburn\\Documents\\Personal\\CodeMonkey\\java\\CodeMonkey\\";
	protected static String dataDir = ProjectBase.CM + "data/";
	private String name = "DICKSYOUFORGOTTOsetName";

	protected void setName( ) {

		this.name = this.getClass( ).getSimpleName( );

		if( System.getProperty( "os.name" ).toLowerCase( ).contains( "windows" ) ) {
			ProjectBase.CM = ProjectBase.CMW;
			ProjectBase.dataDir = ProjectBase.CM + "data/";
		}

	}

	public void save( ) {

		this.save( ".png" );

	}

	public void save( int sqx ) {

		this.save( "_" + Integer.toString( sqx ) + ".png" );

	}

	@Override
	public void save( String ext ) {

		this.saveFrame( ProjectBase.dataDir + this.name + ext );

	}

	public void save( PGraphics canvas ) {

		this.save( canvas, ".png" );

	}

	public void save( PGraphics canvas, int sqx ) {

		this.save( canvas, "_" + Integer.toString( sqx ) + ".png" );

	}

	public void save( PGraphics canvas, String ext ) {

		canvas.save( ProjectBase.dataDir + this.name + ext );

	}

}
