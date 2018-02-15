import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

int cc( float x, float max, float min ) {
  return Math.round( ( x - min ) / ( max - min ) * pixelWidth );
}

class Point {

  public PVector coord;
  public int dens;
  public int idx;

  public Point( PVector coord, int dens ) {

    this.coord = coord;
    this.dens = dens;
    
  }
  
}
class PointXComparator implements Comparator<Point> {
  public int compare( Point a, Point b ) {
    if( a.coord.x - b.coord.x < 0.00000000001 )
      return 0;
    else if( a.coord.x > b.coord.x )
      return 1;
    else
      return -1;
  }
}
class PointYComparator implements Comparator<Point> {
  public int compare( Point a, Point b ) {
    if( a.coord.y - b.coord.y < 0.00000000001 )
      return 0;
    else if( a.coord.y > b.coord.y )
      return 1;
    else
      return -1;
  }
}

List<Point> pts;
Pair closest = null;

class Pair {
  
  public int a, b;
  public double dist;
  
  public Pair( int a, int b, double dist ) {
    this.a = a;
    this.b = b;
    this.dist = dist;
  }
  
}

void findClosest( ) {
  
  for( int pdx = 0; pdx < pts.size( ); ++pdx ) {
    
    pts.get( pdx ).idx = pdx;
    
  }
  
  List<Point> sortedX = new ArrayList<Point>( pts );
  sortedX.sort( new PointXComparator( ) );
  List<Point> sortedY = new ArrayList<Point>( pts );
  sortedY.sort( new PointYComparator( ) );
  
  closest = findClosestRecurse( sortedX, sortedY );
  
}

Pair findClosestRecurse( List<Point> sortedX, List<Point> sortedY ) {
  
  int numPoints = sortedX.size( );
  if( numPoints <= 3 )
    return bruteForceClosest( sortedX );
 
  int halfIdx = numPoints >>> 1;
  List<Point> leftHalf = sortedX.subList( 0, halfIdx );
  List<Point> rightHalf = sortedX.subList( halfIdx, numPoints );
 
  List<Point> tempList = new ArrayList<Point>( leftHalf );
  tempList.sort( new PointYComparator( ) );
  Pair closestPair = findClosestRecurse( leftHalf, tempList );
 
  tempList.clear( );
  tempList.addAll( rightHalf );
  tempList.sort( new PointYComparator( ) );
  Pair closestPairRight = findClosestRecurse( rightHalf, tempList );
 
  if( closestPairRight.dist < closestPair.dist )
    closestPair = closestPairRight;
 
  tempList.clear( );
  double shortestDistance = closestPair.dist;
  double centerX = rightHalf.get( 0 ).coord.x;
  
  for( Point point : sortedY )
  if( Math.abs( centerX - point.coord.x ) < shortestDistance )
    tempList.add( point );
 
  for( int i = 0; i < tempList.size( ) - 1; i++ ) {
    
    Point point1 = tempList.get( i );
    
    for( int j = i + 1; j < tempList.size( ); j++ ) {
      
      Point point2 = tempList.get( j );
      
      if( ( point2.coord.y - point1.coord.y ) >= shortestDistance )
        break;
        
      double dist = PVector.dist( point1.coord, point2.coord );
      
      if( dist < closestPair.dist ) {
        
        closestPair = new Pair( point1.idx, point2.idx, dist );
        shortestDistance = dist;
        
      }
    }
  }
  
  return closestPair;
  
}

Pair bruteForceClosest( List<Point> points ) {
  
  closest = new Pair( 0, 0, Double.MAX_VALUE );
  
  for( int i = 0; i < points.size( ); ++i ) {
    
    for( int j = i + 1; j < points.size( ); ++j ) {
      
      double dist = PVector.dist( points.get( i ).coord, points.get( j ).coord );
      if( dist < closest.dist )
        closest = new Pair( points.get( i ).idx, points.get( j ).idx, dist );
      
    }
    
  }
  
  return closest;
  
}

void joinClosest( ) {
  
  if( closest == null )
    findClosest( );
  
  Point a = pts.get( closest.a );
  Point b = pts.get( closest.b );
  
  float xavg = ( a.coord.x * a.dens + b.coord.x * b.dens ) / ( a.dens + b.dens );
  float yavg = ( a.coord.y * a.dens + b.coord.y * b.dens ) / ( a.dens + b.dens );
  
  Point npt = new Point( new PVector( xavg, yavg ), a.dens + b.dens );
  
  if( a.idx < b.idx ) {
    pts.remove( b.idx );
    pts.remove( a.idx );
  } else {
    pts.remove( a.idx );
    pts.remove( b.idx );
  }
   
  pts.add( npt );
  
}

void writePoints( ) {
  
  JSONArray points = new JSONArray( );
  for( int pdx = 0; pdx < pts.size( ); ++pdx ) {
    
    JSONObject pt = new JSONObject( );
    pt.setFloat( "x", pts.get( pdx ).coord.x );
    pt.setFloat( "y", pts.get( pdx ).coord.y );
    pt.setInt( "id", pdx );
    
    points.append( pt );
    
  }
  saveJSONArray( points, "newPoints.json" );
  
}

void setup( ) {

  size( 720, 720 );

  JSONObject triangulation = loadJSONObject( "Starbucks.json" );
  JSONArray points = triangulation.getJSONArray( "points" );

  pts = new ArrayList<Point>( );

  for ( int pdx = 0; pdx < points.size( ); ++pdx ) {

    JSONObject pt = points.getJSONObject( pdx );
    pts.add( new Point( new PVector( pt.getFloat( "x" ), pt.getFloat( "y" ) ), 1 ) );
  
  }

  findClosest( );
  
  noLoop( );
  
}

void draw( ) {
  
  float xmin = Float.MAX_VALUE;
  float ymin = Float.MAX_VALUE;
  float xmax = -Float.MAX_VALUE;
  float ymax = -Float.MAX_VALUE;
  for( Point p : pts ) {
    
    if( p.coord.x > xmax )
      xmax = p.coord.x;
      
    if( p.coord.y > ymax )
      ymax = p.coord.y;
      
    if( p.coord.x < xmin )
      xmin = p.coord.x;
    
    if( p.coord.y < ymin )
      ymin = p.coord.y;
    
  }
  
  noStroke( );
  fill( 255 );
  rect( 0, 0, pixelWidth, pixelHeight );
  fill( 0 );
  
  for( Point p : pts ) {
    
    ellipse( cc( p.coord.x, xmax, xmin ), cc( p.coord.y, ymax, ymin ), 3, 3 );
    
  }
  
}

void keyPressed( ) {
  
  if( key == ' ' ) {
    writePoints( );
  } else if( key == 'q' ) {
    System.out.println( "Clipping to 0.00001" );
    while( closest.dist < 0.00001 ) {
      joinClosest( );
      findClosest( );
    }
  } else if( key == 'w' ) {
    System.out.println( "Clipping to 0.0001" );
    while( closest.dist < 0.0001 ) {
      joinClosest( );
      findClosest( );
    }
  } else if( key == 'f' ) {
    System.out.println( "Clipping to 0.001" );
    while( closest.dist < 0.001 ) {
      joinClosest( );
      findClosest( );
    }
  } else if( key == 'p' ) {
    System.out.println( "Clipping to 0.01" );
    while( closest.dist < 0.01 ) {
      joinClosest( );
      findClosest( );
    }
  } else if( key == 'g' ) {
    System.out.println( "Clipping to 0.1" );
    while( closest.dist < 0.1 ) {
      joinClosest( );
      findClosest( );
    }
  } else if( key == 'j' ) {
    System.out.println( "Clipping to 1.0" );
    while( closest.dist < 1.0 ) {
      joinClosest( );
      findClosest( );
    }
  }else if( key == 'l' ) {
    System.out.println( "Clipping to 10.0" );
    while( closest.dist < 10.0 ) {
      joinClosest( );
      findClosest( );
    }
  } else {
    return;
  }
  
  System.out.println( String.format( "Closest distance of %d points: %f", pts.size( ), closest.dist ) );
  redraw( );
  
}