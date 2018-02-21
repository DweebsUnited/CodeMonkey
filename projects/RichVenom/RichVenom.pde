import java.util.List;
import java.util.ArrayList;
import java.util.Random;

final float minX = -2;
final float maxX = 2;
final float minY = -2;
final float maxY = 2;

float cx( float x ) { return ( x - minX ) / ( maxX - minX ) * pixelWidth; }
float cy( float y ) { return ( y - minY ) / ( maxY - minY ) * pixelHeight; }

final int numArclets = 250;
final double unitRadius = 0.001;
final double unitArcAng = 2 * PI / numArclets;

final double noiseMag = 0.005;

final double maxVel = 0.05;
final double minVel = -0.05;

final double smoothFactor = 0.75;

final double velOff = 0.0025;

final double minRadius = 0.001;

class Arclet {
  
  public double angle;
  public double radius;
  public double velocity;
   
  public Arclet( ) { }
  
  public Arclet( Arclet a ) {
    this.angle = a.angle;
    this.radius = a.radius;
    this.velocity = a.velocity;
  }
  
}

List<Arclet> arclets = new ArrayList<Arclet>( );
boolean updating = true;
boolean resetting = true;

Random rng = new Random( System.currentTimeMillis( ) );

void setupArclets( ) {
  
  arclets.clear( );
  
  for( int adx = 0; adx < numArclets; ++adx ) {
    
    Arclet a = new Arclet( );
    a.angle = adx * unitArcAng;
    a.radius = unitRadius;
    a.velocity = 0.01;
    
    arclets.add( a );
    
  }
  
}

void calcNextCircle( ) {
  
  if( ! updating )
    return;
  
  int deadArclets = 0;
  
  // Update all the points
  for( int adx = 0; adx < arclets.size( ); ++adx ) {
    
    Arclet a = arclets.get( adx );
    
    a.radius += a.velocity + velOff;
    
    if( a.radius * a.radius > maxX * maxX + maxY * maxY )
      ++deadArclets;
    
    a.velocity += ( rng.nextFloat( ) * 2 - 1 ) * noiseMag;
    
    if( a.velocity > maxVel )
      a.velocity = maxVel;
    else if( a.velocity < minVel )
      a.velocity = minVel;
    
    if( a.radius < minRadius )
      a.radius = minRadius;
    
  }
  
  List<Arclet> newArclets = new ArrayList<Arclet>( );
  
  // Smooth all the points
  for( int adx = 0; adx < arclets.size( ); ++adx ) {
    
    // Update this one so we don't go backwards off the list
    int bdx = ( adx + 1 ) % arclets.size( );
    int cdx = ( bdx + 1 ) % arclets.size( );
    
    Arclet a = new Arclet( arclets.get( bdx ) );
    a.radius = arclets.get( bdx ).radius * ( 1 - smoothFactor ) + ( arclets.get( adx ).radius + arclets.get( cdx ).radius ) / 2.0 * smoothFactor;
    
    newArclets.add( a );
    
  }
  
  arclets = newArclets;
  
  if( deadArclets == arclets.size( ) )
    updating = false;
  
}

void drawArclets( ) {
  
  for( int adx = 0; adx < arclets.size( ); ++adx ) {
  
    Arclet a = arclets.get( adx );
    Arclet b = arclets.get( ( adx + 1 ) % arclets.size( ) );
    
    PVector aPos = new PVector( (float)Math.cos( a.angle ), (float)Math.sin( a.angle ) );
    aPos.mult( (float)a.radius );
    
    PVector bPos = new PVector( (float)Math.cos( b.angle ), (float)Math.sin( b.angle ) );
    bPos.mult( (float)b.radius );
    
    noFill( );
    stroke( 0 );
    
    line( cx( aPos.x ), cy( aPos.y ), cx( bPos.x ), cy( bPos.y ) );
  
  }
  
}

void setup( ) {
  
  size( 900, 900 );
  background( 255 );
  
}

void draw( ) {
  
  if( resetting ) {
    
    background( 255 );
    
    setupArclets( );
    drawArclets( );
    
    resetting = false;
    updating = true;
    
  }
  
  calcNextCircle( );
  calcNextCircle( );
  drawArclets( );
}

void keyPressed( ) {
  
  if( key == ' ' ) {
    updating = !updating;
  } else if( key == 'q' ) {
    saveFrame( "RichVenom.png" );
  } else if( key =='a' ) {
    resetting = true;
  }
  
}