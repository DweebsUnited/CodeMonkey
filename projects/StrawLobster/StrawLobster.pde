import java.util.ArrayList;
import java.util.Comparator;

int cx( float x ) {
  return Math.round( ( x + 1.0 ) / 2.0 * pixelWidth );
}
int cy( float y ) {
  return Math.round( ( y + 1.0 ) / 2.0 * pixelHeight );
}

class Point {

  public PVector coord;
  public int id;
  public double dens;

  public Point( PVector coord, int id ) {

    this.coord = coord;
    this.id = id;
  }

  public void prepare( ) {
    this.dens = 1.0 / ( dens + 0.001 );
  }
}
class PointComparator implements Comparator<Point> {
  public int compare( Point a, Point b ) {
    return a.id - b.id;
  }
}

double triArea( Point a, Point b, Point c ) {
  return 0.5 * Math.abs( ( b.coord.x - a.coord.x ) * ( c.coord.y - a.coord.y ) - ( c.coord.x - a.coord.x ) * ( b.coord.y - a.coord.y ) );
}

void drawTriangle( Point a, Point b, Point c, double minDens, double maxDens ) {

  double fillDens = ( a.dens + b.dens + c.dens ) / 3.0;
  fillDens = Math.pow( ( fillDens - minDens ) / ( maxDens - minDens ), 1 );

  noStroke( );
  fill( Math.round( ( 1.0 - fillDens ) * 255 ) );
  triangle( cx( a.coord.x ), cy( a.coord.y ), cx( b.coord.x ), cy( b.coord.y ), cx( c.coord.x ), cy( c.coord.y ) );
}

void setup( ) {

  size( 4096, 4096 );
  background( 255 );

  JSONObject triangulation = loadJSONObject( "triangulation.json" );
  JSONArray points = triangulation.getJSONArray( "points" );
  JSONArray faces = triangulation.getJSONArray( "faces" );

  ArrayList<Point> pts = new ArrayList<Point>( );

  for ( int pdx = 0; pdx < points.size( ); ++pdx ) {

    JSONObject pt = points.getJSONObject( pdx );
    pts.add( new Point( new PVector( pt.getFloat( "x" ), pt.getFloat( "y" ) ), pt.getInt( "id" ) ) );
  }

  pts.sort( new PointComparator( ) );

  for ( int fdx = 0; fdx < faces.size( ); ++fdx ) {

    JSONObject face = faces.getJSONObject( fdx );
    Point a = pts.get( face.getInt( "a" ) );
    Point b = pts.get( face.getInt( "b" ) );
    Point c = pts.get( face.getInt( "c" ) );

    double area = triArea( a, b, c );

    a.dens += area;
    b.dens += area;
    c.dens += area;
    
  }

  double rawMax = 0.0;
  double rawMin = Double.MAX_VALUE;
  double maxDens = 0.0;
  double minDens = Double.MAX_VALUE;
  for ( Point p : pts ) {

    if ( p.dens > rawMax )
      rawMax = p.dens;
      
    if ( p.dens < rawMin )
      rawMin = p.dens;
    
    p.prepare( );

    if ( p.dens > maxDens )
      maxDens = p.dens;
      
    if ( p.dens < minDens )
      minDens = p.dens;
      
  }
  
  System.out.println( String.format( "Raw min: %f", rawMin ) );
  System.out.println( String.format( "Raw max: %f", rawMax ) );
  System.out.println( String.format( "Min density: %f", minDens ) );
  System.out.println( String.format( "Max density: %f", maxDens ) );

  for ( int fdx = 0; fdx < faces.size( ); ++fdx ) {

    JSONObject face = faces.getJSONObject( fdx );
    Point a = pts.get( face.getInt( "a" ) );
    Point b = pts.get( face.getInt( "b" ) );
    Point c = pts.get( face.getInt( "c" ) );

    drawTriangle( a, b, c, minDens, maxDens );
  }
  
  noLoop( );
  saveFrame( "StrawLobster.png" );
  
}