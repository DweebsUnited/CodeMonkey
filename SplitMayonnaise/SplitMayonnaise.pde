import java.util.*;
import java.io.*;
import java.awt.Color;

// [ 0, 1 ]
float[] rgbComp = new float[ 4 ];
float[] HSBtoRGB( float h, float s, float b ) {
  
  Color c = Color.getHSBColor( h, s, b );
  c.getRGBComponents( rgbComp );
  return rgbComp;
  
}

class Quad {
  
  float T, B, L, R;
  int depth;
  
  public Quad TL, TR, BL, BR;
  
  float rbar, gbar, bbar;
  float rvar, gvar, bvar;
  float vara;
  
  public Quad( int depth, float T, float B, float L, float R ) {
    
    this.depth = depth;
    this.T = T;
    this.B = B;
    this.L = L;
    this.R = R;
    
    rbar = 0; gbar = 0; bbar = 0;
    rvar = 0; gvar = 0; bvar = 0;
    vara = 0;
    
  }
  
}

public void mapLocal( PVector l, PVector s, float T, float B, float L, float R ) {
  l.x = s.x * ( R - L ) + L;
  l.y = s.y * ( T - B ) + B;
}

public Quad buildQuad( int depth, PImage src, PVector[] sPoints, float varcut, float T, float B, float L, float R ) {
  
  if( depth < 0 )
    return null;
    
  Quad q = new Quad( depth, T, B, L, R );
  
  PVector locals = new PVector( );
  color px;
  float r, g, b;
  
  for( PVector s : sPoints ) {
    
    // Must transform s to bring in to our box
    mapLocal( locals, s, T, B, L, R );
    
    px = src.get( int( locals.x * src.pixelWidth ), int( locals.y * src.pixelHeight ) );
    r = hue( px );
    g = saturation( px );
    b = brightness( px );
    
    q.rbar += r;
    q.gbar += g;
    q.bbar += b;
    
    q.rvar += ( r * r );
    q.gvar += ( g * g );
    q.bvar += ( b * b );
    
  }
  
  q.rbar /= sPoints.length;
  q.gbar /= sPoints.length;
  q.bbar /= sPoints.length;
  
  q.rvar = q.rvar / sPoints.length - ( q.rbar * q.rbar );
  q.gvar = q.gvar / sPoints.length - ( q.gbar * q.gbar );
  q.bvar = q.bvar / sPoints.length - ( q.bbar * q.bbar );
  
  // Average variances
  q.vara = ( q.rvar + q.gvar + q.bvar ) / 3.0;
  // Take max variance
  //q.vara = max( q.rvar, max( q.gvar, q.bvar ) );
  System.out.println( q.vara );
  
  if( q.vara > varcut ) {
    float hh = ( q.T + q.B ) / 2.0;
    float hw = ( q.L + q.R ) / 2.0;
    
    q.TL = buildQuad( q.depth - 1, src, sPoints, varcut, q.T, hh, q.L, hw );
    q.TR = buildQuad( q.depth - 1, src, sPoints, varcut, q.T, hh, hw, q.R );
    q.BL = buildQuad( q.depth - 1, src, sPoints, varcut, hh, q.B, q.L, hw );
    q.BR = buildQuad( q.depth - 1, src, sPoints, varcut, hh, q.B, hw, q.R );
  }
  
  return q;
  
}

void drawQuad( Quad q ) {
  
  // Leaf, draw
  if( q.TL == null ) {
    
    fill( color( q.rbar, q.gbar, q.bbar ) );
    rect( q.L * pixelWidth, q.B * pixelHeight, ( q.R - q.L ) * pixelWidth, ( q.T - q.B ) * pixelHeight );
    
  } else {
    
    drawQuad( q.TL );
    drawQuad( q.TR );
    drawQuad( q.BL );
    drawQuad( q.BR );
    
  }
  
}

class TripleInt {
  int a, b, c;
  public TripleInt( int a, int b, int c ) { this.a = a; this.b = b; this.c = c; }
}

class VertData {
  PVector pos;
  int r, g, b;
  public VertData( PVector pos, int r, int g, int b ) { this.pos = pos.copy( ); this.r = r; this.g = g; this.b = b; }
}

void writePlyFile( String fname, ArrayList<VertData> verts, ArrayList<TripleInt> faces ) {
  
  File f = new File( fname );
  FileWriter fr = null;
  BufferedWriter br = null;
  
  try {
    fr = new FileWriter( f );
    br = new BufferedWriter( fr );
    
    // Header
    br.write( "ply\n" );
    br.write( "format ascii 1.0\n" );
    br.write( "comment SplitMayonnaise output .ply\n" );
    br.write( String.format( "element vertex %d\n", verts.size( ) ) );
    br.write( "property float x\n" );
    br.write( "property float y\n" );
    br.write( "property float z\n" );
    br.write( "property uchar red\n" );
    br.write( "property uchar green\n" );
    br.write( "property uchar blue\n" );
    br.write( String.format( "element face %d\n", faces.size( ) ) );
    br.write( "property list uchar int vertex_index\n" );
    br.write( "end_header\n" );
    
    // Vertices:
    for( VertData vd : verts )
      br.write( String.format( "%f %f %f %d %d %d\n", vd.pos.x, vd.pos.y, vd.pos.z, vd.r, vd.g, vd.b ) );
    
    // Faces:
    for( TripleInt ti : faces )
      br.write( String.format( "3 %d %d %d\n", ti.a, ti.b, ti.c ) );
    
  } catch( Exception e ) {
    
    System.out.println( "Could not write .ply:" );
    System.out.println( e );
    
  } finally {
    
    try {
      br.close( );
      fr.close( );
    } catch( Exception e ) {
      System.out.println( "Could not close file? :" );
      System.out.println( e );
    }
    
  }
  
}

void addQuadPly( Quad q, int scale, ArrayList<VertData> verts, ArrayList<TripleInt> faces ) {
  
  HSBtoRGB( q.rbar / 100.0, q.gbar / 100.0, q.bbar / 100.0 );
  
  float r = rgbComp[ 0 ] * 255;
  float g = rgbComp[ 1 ] * 255;
  float b = rgbComp[ 2 ] * 255;
  
  // Add a little randomness to the height
  float h = dlim - q.depth + 1 + (float)rng.nextGaussian( ) * 0.1;
  
  //z
  //|
  //|
  //|
  //TL----TR
  // `.
  //   `B
  // Normals: UP, DN, R, L, T, B
  //
  //4------5.
  //|`.    | `.
  //|  `7--+---6
  //|   |  |   |
  //0---+--1.  |
  // `. |    `.|
  //   `3------2
  // Verts: TL, TR, BR, BL, TLz, TRz, BRz, BLz
  // Faces:
  //   DN: 013, 312
  //   UP: 475, 576
  //   T : 041, 145
  //   B : 327, 726
  //   L : 034, 437
  //   R : 216, 615
  
  // Vertices: TL, TR, BR, BL, TLz, TRz, BRz, BLz
  int vroot = verts.size( );
  verts.add( new VertData( new PVector( q.L * scale, q.T * scale, 0.0 ), int( r + (float)rng.nextGaussian( ) ), int( g + (float)rng.nextGaussian( ) ), int( b + (float)rng.nextGaussian( ) ) ) );
  verts.add( new VertData( new PVector( q.R * scale, q.T * scale, 0.0 ), int( r + (float)rng.nextGaussian( ) ), int( g + (float)rng.nextGaussian( ) ), int( b + (float)rng.nextGaussian( ) ) ) );
  verts.add( new VertData( new PVector( q.R * scale, q.B * scale, 0.0 ), int( r + (float)rng.nextGaussian( ) ), int( g + (float)rng.nextGaussian( ) ), int( b + (float)rng.nextGaussian( ) ) ) );
  verts.add( new VertData( new PVector( q.L * scale, q.B * scale, 0.0 ), int( r + (float)rng.nextGaussian( ) ), int( g + (float)rng.nextGaussian( ) ), int( b + (float)rng.nextGaussian( ) ) ) );
  
  verts.add( new VertData( new PVector( q.L * scale, q.T * scale, h   ), int( r + (float)rng.nextGaussian( ) ), int( g + (float)rng.nextGaussian( ) ), int( b + (float)rng.nextGaussian( ) ) ) );
  verts.add( new VertData( new PVector( q.R * scale, q.T * scale, h   ), int( r + (float)rng.nextGaussian( ) ), int( g + (float)rng.nextGaussian( ) ), int( b + (float)rng.nextGaussian( ) ) ) );
  verts.add( new VertData( new PVector( q.R * scale, q.B * scale, h   ), int( r + (float)rng.nextGaussian( ) ), int( g + (float)rng.nextGaussian( ) ), int( b + (float)rng.nextGaussian( ) ) ) );
  verts.add( new VertData( new PVector( q.L * scale, q.B * scale, h   ), int( r + (float)rng.nextGaussian( ) ), int( g + (float)rng.nextGaussian( ) ), int( b + (float)rng.nextGaussian( ) ) ) );
  
  // Faces:
  //   DN: 013, 312
  //   UP: 475, 576
  //   T : 041, 145
  //   B : 327, 726
  //   L : 034, 437
  //   R : 216, 615
  faces.add( new TripleInt( vroot + 0, vroot + 1, vroot + 3 ) );
  faces.add( new TripleInt( vroot + 3, vroot + 1, vroot + 2 ) );
  faces.add( new TripleInt( vroot + 4, vroot + 7, vroot + 5 ) );
  faces.add( new TripleInt( vroot + 5, vroot + 7, vroot + 6 ) );
  faces.add( new TripleInt( vroot + 0, vroot + 4, vroot + 1 ) );
  faces.add( new TripleInt( vroot + 1, vroot + 4, vroot + 5 ) );
  faces.add( new TripleInt( vroot + 3, vroot + 2, vroot + 7 ) );
  faces.add( new TripleInt( vroot + 7, vroot + 2, vroot + 6 ) );
  faces.add( new TripleInt( vroot + 0, vroot + 3, vroot + 4 ) );
  faces.add( new TripleInt( vroot + 4, vroot + 3, vroot + 7 ) );
  faces.add( new TripleInt( vroot + 2, vroot + 1, vroot + 6 ) );
  faces.add( new TripleInt( vroot + 6, vroot + 1, vroot + 5 ) );
  
}

void writeQuadPly( Quad q, int scale, ArrayList<VertData> verts, ArrayList<TripleInt> faces ) {
  
  if( q.TL == null ) {
    addQuadPly( q, scale, verts, faces );
  } else {
    writeQuadPly( q.TL, scale, verts, faces );
    writeQuadPly( q.TR, scale, verts, faces );
    writeQuadPly( q.BL, scale, verts, faces );
    writeQuadPly( q.BR, scale, verts, faces );
  }
  
}

void writeTree( Quad q, int scale, String fname ) {
  
  ArrayList<VertData> verts = new ArrayList<VertData>( );
  ArrayList<TripleInt> faces = new ArrayList<TripleInt>( );
  
  writeQuadPly( q, scale, verts, faces );
  
  writePlyFile( fname, verts, faces );
  
}

final int dlim = 8;

void buildTree( ) {
  
  int nSamp = 16;
  PVector[] sPoints = new PVector[ nSamp ];
  for( int sdx = 0; sdx < nSamp; ++ sdx )
    sPoints[ sdx ] = new PVector( rng.nextFloat( ), rng.nextFloat( ) );
  
  tree = buildQuad( dlim, im, sPoints, 85, 1, 0, 0, 1 );
  
}

Random rng = new Random( );
PImage im;
Quad tree;

void setup( ) {
  
  size( 1024, 1024 );
  
  colorMode( HSB, 100, 100, 100 );
  
  im = loadImage( "Sunflowers.jpg" );
  
  buildTree( );
  
}

void draw( ) {
  
  drawQuad( tree );
  
}

void keyPressed( ) {
  
  if( key == ' ' ) {
    buildTree( );
  } else if( key == 'z' ) {
    save( "SplitMayonnaise.png" );
  } else if( key == 'q' ) {
    writeTree( tree, 128, "D:\\Documents\\Processing\\sketches\\SplitMayonnaise\\ply\\SplitMayonnaise.ply" );
  }
  
}
