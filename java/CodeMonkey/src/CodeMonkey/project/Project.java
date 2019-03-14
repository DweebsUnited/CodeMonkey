package CodeMonkey.project;

public class Project {

	private static String CM = "/Users/ozzy/Documents/CodeMonkey/";
	private static String CMW = "C:\\Users\\ElysiumTech\\Documents\\Personal\\CodeMonkey\\";
	protected static String dataDir = Project.CM + "data/";

	protected static void setData( ) {

		if( System.getProperty( "os.name" ).toLowerCase( ).contains( "windows" ) ) {
			Project.dataDir = Project.CMW + "data/";
		}

	}

}
