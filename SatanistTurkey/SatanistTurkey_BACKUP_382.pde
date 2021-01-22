import java.util.*;

// Clipping Rejection sampling:
PerlinSampler clipRej;
// Rejection Sampling rejection
UniformSampler rejRej;

// Clipping Box location
UniformSampler clipLocX, clipLocY;
// Clipping Box axis rotation
PerlinSampler clipSamp;
// Clipping Box length and width
UniformSampler lenSamp, widSamp;

// Crosshatch angle
PerlinSampler angSamp;
// Crosshatch angle noise
GaussianSampler angNoise;
// Crosshatch spacing
UniformSampler spaceSamp;
// Crosshatch offset
UniformSampler offSamp;

// Clip space
PVector clipm = new PVector( -1, -1 );
PVector clipM = new PVector( 1, 1 );

// Count tracking
int tCnt = 200;
int cCnt = 0;


// For GCode generation we need to sort the boxes to be drawn
class PairPV {
  PVector a, b;
  
  public PairPV( PVector a, PVector b ) { this.a = a; this.b = b; }
}

class Box {
  
  PVector o;
  float sortFactor; // Distance to the origin? True travelling salesman solution?
  ArrayList<PairPV> lines;
  
  public Box( ) { this.lines = new ArrayList<PairPV>( ); }
  
}

class BoxComparator implements Comparator<Box> {
  
  int compare( Box a, Box b ) {
    
    float f = b.sortFactor - a.sortFactor;
    
    return ( f > 0 ) ? ( 1 ) : ( ( f < 0 ) ? ( -1 ) : ( 0 ) );
    
  }
  
}

ArrayList<Box> boxes = new ArrayList<Box>( );

void writeGCode( ) {
  
  GCodeWriter gcw = new GCodeWriter( );
<<<<<<< HEAD
  gcw.open( "lines.gcode", 7, 5 );
=======
  gcw.open( "D:\\Projects\\CodeMonkey\\SatanistTurkey\\lines.gcode", 7, 5 );
>>>>>>> Various updates
  
  Collections.sort( boxes, new BoxComparator( ) );
  
  for( Box b : boxes ) {
    
    for( PairPV p : b.lines ) {
      
      gcw.line( p.a, p.b );
      
    }
    
  }
  
  gcw.close( );
  
}

void clipScreen(
  PVector pos,
  PVector clipCenter,
  float clipRot,
  float clipLen,
  float clipWid ) {
  
  // translate( clipCenter.x * pixelWidth, clipCenter.y * pixelHeight );
  // rotate( clipRot );
  // scale( clipLen, clipWid ); 
  // scale( 0.5, 0.5 );
  
  pos.mult( 0.5 );
  pos.x *= clipWid;
  pos.y *= clipLen;
  
  pos.rotate( clipRot );
  
  pos.add( new PVector( clipCenter.x * pixelWidth, clipCenter.y * pixelHeight ) );
  
}

void keyPressed( ) {
  
  if( key == ' ' ) {
    
    tCnt += 10;
    
    System.out.println( String.format( "Target: %d", tCnt ) ); 
    
  } else if( key == 'z' ) {
    
    saveFrame( "SatanistTurkey.png" );
    
    writeGCode( );
    
  }
  
}

void setup( ) {
  
  size( 1280, 900 );
  //blendMode( SUBTRACT );
  rectMode( CENTER );
  background( 0 );
  
  // DEBUG: For drawing Clip rectangle..
  // noStroke( );
  // fill( 255, 0, 0 );
  
  stroke( 255 );
  noFill( );
  strokeWeight( 0.05 );
  
  // Rejection sampling based on Perlin
  clipRej = new PerlinSampler( 0.0, 1.0, 0.0, 2.0 );
  // Small chance to ignore Rejection
  rejRej = new UniformSampler( 0.0, 1.0 );
  
  // Clip Location is anywhere onscreen
  clipLocX = new UniformSampler( 0, 1 );
  clipLocY = new UniformSampler( 0, 1 );
    
  // Clip Direction is a PerlinSampler
  clipSamp = new PerlinSampler( 0.0, PI, 1.0, 2.0 );
  
  // Length and Width are Uniform
  lenSamp = new UniformSampler( 24, 48 );
  widSamp = new UniformSampler( 16, 24 );
  
  // Crosshatch angle is Perlin
  angSamp = new PerlinSampler( 0.0, PI, 2.0, 2.0 );
  
  // Noise is a very small angle Gaussian
  angNoise = new GaussianSampler( 0, PI / 64.0 );
  
  // Crosshatch spacing is Uniform
<<<<<<< HEAD
  spaceSamp = new UniformSampler( 0.25, 0.7 );
=======
  spaceSamp = new UniformSampler( 0.4, 0.7 );
>>>>>>> Various updates
  
  // Crosshatch starting offset is Uniform
  offSamp = new UniformSampler( -0.95, 0.95 );
  
}

void draw( ) {
  
  if( tCnt > cCnt ) {
  
    // Draw a box:
    Box b = new Box( );
    boxes.add( b );
    
    // Random location
    PVector clipCenter = new PVector( clipLocX.sample( ), clipLocY.sample( ) );
    
    while( clipRej.sample( clipCenter.x, clipCenter.y ) > 0.5 && rejRej.sample( ) > 0.05 )
      clipCenter.set( clipLocX.sample( ), clipLocY.sample( ) );
    
    // Clipping Box axis direction
    float clipRot = clipSamp.sample( clipCenter.x, clipCenter.y );
    
    // Random Clip dimensions
    float clipLen = lenSamp.sample( );
    float clipWid = widSamp.sample( );
    
    // Transform to clip space so we can Hatch consistently
    pushMatrix( );
    
    translate( clipCenter.x * pixelWidth, clipCenter.y * pixelHeight );
    rotate( clipRot );
    scale( clipLen, clipWid ); // 
    scale( 0.5, 0.5 ); // width 2 to width 1
    
    // Draw the clip boundaries
    //rect( 0, 0, 2, 2 );
    line( -1, -1, -1,  1 );
    line( -1,  1,  1,  1 );
    line(  1,  1,  1, -1 );
    line(  1, -1, -1, -1 );
    
    PVector TL = new PVector( -1,  1 );
    clipScreen( TL, clipCenter, clipRot, clipLen, clipWid );
    PVector TR = new PVector(  1,  1 );
    clipScreen( TR, clipCenter, clipRot, clipLen, clipWid );
    PVector BL = new PVector( -1, -1 );
    clipScreen( BL, clipCenter, clipRot, clipLen, clipWid );
    PVector BR = new PVector(  1, -1 );
    clipScreen( BR, clipCenter, clipRot, clipLen, clipWid );
    
    b.o = clipCenter;
    
    b.lines.add( new PairPV( TL, TR ) );
    b.lines.add( new PairPV( TR, BR ) );
    b.lines.add( new PairPV( BR, BL ) );
    b.lines.add( new PairPV( BL, TL ) );
    
    // We can now draw in Clip space: ( -1, 1 )
    
    // Random Crosshatch spacing
    float spcCross = spaceSamp.sample( );
    
    // Crosshatch origin is random point in clip space
    PVector o = new PVector( offSamp.sample( ), offSamp.sample( ) );
    
    // Crosshatch angle is Perlin'd
    float angCross = angSamp.sample( o.x, o.y );
    
    // Crosshatch Vector
    PVector d = new PVector( 1, 0 );
    d.rotate( angCross );
    
    // L/R step Vector
    PVector step = d.copy( );
    step.rotate( PI / 2 );
    step.mult( spcCross );
    
    // L/R origins start at o
    PVector l = o.copy( );
    PVector ld = d.copy( );
    PVector r = o.copy( );
    PVector rd = d.copy( );
    
    // Do first intersection with origin
    Intersection i = intersect( d, o, clipm, clipM );
    
    // Draw the origin line!
    line( i.pm.x, i.pm.y, i.pM.x, i.pM.y );
    
    clipScreen( i.pm, clipCenter, clipRot, clipLen, clipWid );
    clipScreen( i.pM, clipCenter, clipRot, clipLen, clipWid );
    b.lines.add( new PairPV( i.pm, i.pM ) );
    
    // Iterate sides until neither intersects
    boolean lint = true, rint = true;
    
    while ( lint == true || rint == true ) {
      
      if( lint ) {
        
        l.sub( step );
        // Tweak direction slightly
        ld.set( d );
        ld.rotate( angNoise.sample( ) );
        
        i = intersect( ld, l, clipm, clipM );
        
        if( i.intersects ) {
          line( i.pm.x, i.pm.y, i.pM.x, i.pM.y );
          
          clipScreen( i.pm, clipCenter, clipRot, clipLen, clipWid );
          clipScreen( i.pM, clipCenter, clipRot, clipLen, clipWid );
          b.lines.add( new PairPV( i.pm, i.pM ) );
        } else
          lint = false;
        
      }
      
      if( rint ) {
        
        r.add( step );
        // Tweak direction slightly
        rd.set( d );
        rd.rotate( angNoise.sample( ) );
        
        i = intersect( d, r, clipm, clipM );
        
        if( i.intersects ) {
          line( i.pm.x, i.pm.y, i.pM.x, i.pM.y );
          
          clipScreen( i.pm, clipCenter, clipRot, clipLen, clipWid );
          clipScreen( i.pM, clipCenter, clipRot, clipLen, clipWid );
          b.lines.add( new PairPV( i.pm, i.pM ) );
        } else
          rint = false;
        
      }
      
    }
    
    popMatrix( );
    cCnt += 1;
    
  }
  
}
