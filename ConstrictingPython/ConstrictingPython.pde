import java.util.*;

Random rng = new Random( );

class Pair<A> {
  A a, b;
  public Pair( A a, A b ) { this.a = a; this.b = b; }
}

class Triple<A> {
  A a, b, c;
  public Triple( A a, A b, A c ) { this.a = a; this.b = b; this.c = c; }
}

class Vertex {
  
  PVector pos;
  
  Edge conflict;
  
  public Vertex( ) { }
  public Vertex( PVector pos ) { this( ); this.pos = pos; }
  
}

class Edge {
  
  // End vertices
  Vertex a, b;
  
  // Next and prev Edges
  Edge n, p;
  
  PVector norm;
  PVector mid;
  
  // Conflicting vertices
  ArrayList<Vertex> conflict;
  
  public Edge( ) { this.conflict = new ArrayList<Vertex>( ); this.n = this; this.p = this; }
  public Edge( Vertex a, Vertex b ) { this( ); this.a = a; this.b = b; this.mid = b.pos.copy( ); this.mid.add( a.pos ); this.mid.mult( 0.5 ); }
  public Edge( Vertex a, Vertex b, PVector norm ) { this( a, b ); this.norm = norm; }
  public Edge( Vertex a, Vertex b, Vertex in ) { this( a, b, norm( a.pos, b.pos, in.pos ) ); }
  
  // Determine if v is on positive ( norm ) side of edge
  public boolean outside( Vertex v ) {
    
    PVector p = v.pos.copy( );
    p.sub( this.mid );
    
    return p.dot( this.norm ) > 0;
    
  }
  
  public boolean concave( ) {
    
    return this.outside( this.n.b );
    
  }
}

// Norm from a point to line
PVector norm( Edge e, Vertex vIn ) {
  
  // Sanity check, cant use a v on e
  if( e.a == vIn || e.b == vIn )
    throw new RuntimeException( "Can't calculate a norm with co-linear points!" );
    
  return norm( e.a.pos, e.b.pos, vIn.pos );
  
}
PVector norm( PVector l1, PVector l2, PVector v ) {
  
  // ( V - c . S ) . S = 0
  // V . S - c . S . S = 0
  // V . S = c . S . S
  // V . S / c . S . S = 1
  // V . S / S . S = c
  
  // p = c S
  // p = ( V . S / S . S ) * S
  
  // n = p - V
  // n = ( V . S / S . S ) * S - V
  
  // Origin will be e.a
  // WARN: l1 must not be modified :S
  PVector a1 = l1;
  
  PVector b = v.copy( );
  b.sub( a1 );
  PVector a2 = l2.copy( );
  a2.sub( a1 );
  
  // Start with S = l2 - l1 = a2
  PVector s = a2;
  
  // V . S
  float vs = s.dot( b );
  // S . S
  float ss = s.dot( s );
  
  // ( V . S / S . S ) * S
  s.mult( vs / ss );
  
  // ( V . S / S . S ) * S - V
  s.sub( b );
  
  // That gives us n!
  // INFO: Do not normalize here, leave that up to usage to determine if necessary
  return s;
  
}

Pair<Edge> extrude( Edge e, Vertex v ) {
  
  // Make new edges f g
  Edge f = new Edge( e.a, v, e.b );
  edges.add( f );
  Edge g = new Edge( v, e.b, e.a );
  edges.add( g );
  
  // Link new edges
  f.n = g;
  f.p = e.p;
  
  g.n = e.n;
  g.p = f;
  
  // Unlink old edge
  f.p.n = f;
  g.n.p = g;
  
  // Remove e from edges
  edges.remove( e );
  
  // null v conflict now that it is done
  v.conflict = null;
  e.conflict.remove( v );
  
  // Update conflicts of old edge to use new
  for( Vertex cv : e.conflict ) {
    
    if( f.outside( cv ) ) {
      cv.conflict = f;
      f.conflict.add( cv );
    } else if( g.outside( cv ) ) {
      cv.conflict = g;
      g.conflict.add( cv );
    } else {
      // Now inside the hull, null conflict
      cv.conflict = null;
    }
    
  }
  
  return new Pair<Edge>( f, g );
  
}

Edge collapse( Edge e ) {
  
  // Collapse e and e.n
  
  // Make new edge f
  Edge f = new Edge( e.a, e.n.b, e.b );
  edges.add( f );
  
  // Link in
  f.p = e.p;
  f.n = e.n.n;
  
  f.p.n = f;
  f.n.p = f;
  
  // Remove old edges
  edges.remove( e.n );
  edges.remove( e );
  
  // Update conflicts of old edges
  for( Vertex cv : e.conflict ) {
    
    if( f.outside( cv ) ) {
      cv.conflict = f;
      f.conflict.add( cv );
    } else {
      // Now inside the hull, null conflict
      cv.conflict = null;
    }
    
  }
  
  for( Vertex cv : e.n.conflict ) {
    
    if( f.outside( cv ) ) {
      cv.conflict = f;
      f.conflict.add( cv );
    } else {
      // Now inside the hull, null conflict
      cv.conflict = null;
    }
    
  }
  
  return f;
  
}

void drawLine( color c, PVector a, PVector b ) {
  
  noFill( );
  stroke( c );
  line( a.x * pixelWidth, a.y * pixelHeight, b.x * pixelWidth, b.y * pixelHeight );
  
}

void drawPoint( color c, int r, PVector a ) {
  
  fill( c );
  noStroke( );
  ellipse( a.x * pixelWidth, a.y * pixelHeight, r, r );
  
}

final int nPoints = 64;
ArrayList<Vertex> verts = new ArrayList<Vertex>( );
ArrayList<Edge> edges = new ArrayList<Edge>( );

void setup( ) {
  
  size( 1080, 720 );
  
  // Generate some random points
  for( int pdx = 0; pdx < nPoints; ++ pdx ) {
    
    verts.add( new Vertex( new PVector( rng.nextFloat( ), rng.nextFloat( ) ) ) );
    
  }
  
  // Init!
  // Pick 3 random ( idx 0, 1, 2 ), make Edge e 0->1
  Vertex a = verts.get( 0 );
  Vertex b = verts.get( 1 );
  Vertex c = verts.get( 2 );
  
  // For first extrusion e needs to be backwards
  Edge e = new Edge( b, a, c );
  edges.add( e );

  // "Extrude" e
  extrude( e, c );
  // Swap e vertices back
  e.a = a;
  e.b = b;
  // Re-add e to edges
  edges.add( e );
  
  // For all other points ( idx > 2 )
  //   Conflict Edge = first Edge with positive Norm projection
  //   If none, point inside hull, not a problem
  for( int vdx = 3; vdx < verts.size( ); ++vdx ) {
    
    Vertex v = verts.get( vdx );
    
    for( Edge ce : edges ) {
      
      // If on positive side, we have a conflict
      if( ce.outside( v ) ) {
        v.conflict = ce;
        ce.conflict.add( v );
        break;
      }
      
    }
    
  }
  
  frameRate( 5 );
  
}

int vdx = 3;

void hullStep( ) {
  
  // Add a vertex to the hull
  if( vdx < verts.size( ) ) {
    
    Vertex v = verts.get( vdx );
    
    while( v.conflict == null && vdx < verts.size( ) ) {
      vdx += 1;
      v = verts.get( vdx );
    }
    
    if( v.conflict != null ) {
      
      // Extrude conflicting edge with two more
      Pair<Edge> er = extrude( v.conflict, v );
      
      // Re-convex the hull
      Edge ce = er.a.p;
      while( ce.concave( ) ) {
        ce = collapse( ce ).p;
      }
      
      ce = er.b; 
      while( ce.concave( ) ) {
        ce = collapse( ce );
      }
      
    }
    
    vdx += 1;
    
  }
  
}

void draw( ) {
  
  hullStep( );
  
  // Drawing!
  background( 0 );
  
  color white = color( 255 );
  color red = color( 255, 0, 0 );
  color blue = color( 0, 0, 255 );
  
  for( Vertex vd : verts ) {
    drawPoint( white, 5, vd.pos );
    if( vd.conflict != null )
      drawLine( blue, vd.pos, vd.conflict.mid );
  }
  for( Edge ed : edges ) {
    drawLine( white, ed.a.pos, ed.b.pos );
    
    PVector ndest = ed.norm.copy( );
    ndest.normalize( );
    ndest.mult( 0.05 );
    ndest.add( ed.mid );
    drawLine( red, ed.mid, ndest );
    
    drawLine( red, ed.mid, ed.b.pos );
  }
  
  //drawLine( red, e.a.pos, e.b.pos );
  //drawLine( red, f.a.pos, f.b.pos );
  //drawLine( red, g.a.pos, g.b.pos );
  
  //drawPoint( red, 5, a.pos );
  //drawPoint( red, 5, b.pos );
  //drawPoint( red, 5, c.pos );

}

void keyPressed( ) {
  
  if( key == ' ' ) {
    
  }
  
}
