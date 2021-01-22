import java.io.*;

class GCodeWriter {
  
  BufferedWriter bw;
  float up, down;
  
  public boolean open( String fname, float up, float down ) {
    
    this.up = up;
    this.down = down;
    
    try {
      
      this.bw = new BufferedWriter( new FileWriter( fname ) );
      
      // Write header
      // Auto home
      this.bw.write( "G28\n" );
      // Absolute Positioning
      this.bw.write( "G90\n" );
      // Metric units
      this.bw.write( "G21\n" );
      // Move to starting point
      this.bw.write( String.format( "G0 Z%f\n", this.up ) );
      // Parsing marker
      this.bw.write( String.format( "; Size: %f x %f\n", pixelWidth, pixelHeight ) );
      this.bw.write( "; BEGINPARSE\n" );
      
      return true;
  
    } catch( Exception e ) {
      return false;
    }
    
  }
  
  public int line( PVector start, PVector end ) {
    
    try {
      // Move to start
      this.bw.write( String.format( "G0 X%f Y%f\n", start.x, start.y ) );
    } catch( Exception e ) {
      return 0;
    }
    
    try {
      // Pen down
      this.bw.write( String.format( "G0 Z%f\n", this.down ) );
    } catch( Exception e ) {
      return 1;
    }
    
    try {
      // Move to end
      this.bw.write( String.format( "G0 X%f Y%f\n", end.x, end.y ) );
    } catch( Exception e ) {
      return 2;
    }
    
    try {
      // Pen up
      this.bw.write( String.format( "G0 Z%f\n", this.up ) );
    } catch( Exception e ) {
      return 3;
    }
    
    return 4;
    
  }
  
  public boolean close( ) {
    
    try {
      this.bw.flush( );
      this.bw.close( );
      return true;
    } catch( Exception e ) {
      return false;
    }
    
  }
  
}
