void setup( ) {
  
  size( 512, 512 );
  background( 255 );
  
  noStroke( );
  fill( 0, 255 );
  
  String [] pLines = loadStrings( "voronoiPoints.csv" );
  
  boolean first = true;
  for (String line : pLines) {
    
    if( first ) {
      
      first = false;
      continue;
      
    }
    
    String[] pieces = split(line, ',');
    
    ellipse( Float.valueOf( pieces[ 0 ] ) * pixelWidth / 2.0 + pixelWidth / 2.0, Float.valueOf( pieces[ 1 ] ) * pixelHeight / 2.0 + pixelHeight / 2.0, 2, 2 );
    
  }
  
  stroke( 0, 255 );
  noFill( );
  
  String [] eLines = loadStrings( "voronoiEdges.csv" );
  
  for (String line : eLines) {
    
    String[] pieces = split(line, ',');
    
    line(
      Float.valueOf( pieces[ 0 ] ) * pixelWidth / 2.0 + pixelWidth / 2.0,
      Float.valueOf( pieces[ 1 ] ) * pixelHeight / 2.0 + pixelHeight / 2.0,
      Float.valueOf( pieces[ 2 ] ) * pixelWidth / 2.0 + pixelWidth / 2.0,
      Float.valueOf( pieces[ 3 ] ) * pixelHeight / 2.0 + pixelHeight / 2.0 );
    
  }
  
  noLoop( );
  
}