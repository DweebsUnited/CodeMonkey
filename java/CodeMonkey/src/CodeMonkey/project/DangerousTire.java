package CodeMonkey.project;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

public class DangerousTire {

  private static Random rng = new Random( );

  private static String pickPoint( ) {

    return Float.toString( DangerousTire.rng.nextFloat( ) * 10 );

  }

  public static void main( String[ ] argv ) throws Exception {

    final int N_PTS = 64;

    FileWriter fstream = new FileWriter( "/Users/ozzy/Desktop/test.gcode" );
    BufferedWriter out = new BufferedWriter( fstream );

    // Home
    out.write( "G28\n" );
    // Random start point
    out.write( "G0 X" + DangerousTire.pickPoint( ) + " Y" + DangerousTire.pickPoint( ) + " Z" + DangerousTire.pickPoint( ) + " F500\n" );
    // Go go go
    for( int pdx = 0; pdx < N_PTS; ++pdx )
      out.write( "G0 X" + DangerousTire.pickPoint( ) + " Y" + DangerousTire.pickPoint( ) + " Z" + DangerousTire.pickPoint( ) + "\n" );

    out.close( );
    fstream.close( );

  }

}
