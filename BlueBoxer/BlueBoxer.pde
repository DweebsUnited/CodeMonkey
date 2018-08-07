import java.util.Random;
import java.util.Iterator;

import com.hamoid.VideoExport;

Random rng = new Random( );

class Actor {
  
  private int w, h;
  
  public PVector pos;
  
  private PVector vel;
  private PVector acc;
  
  private float rad;
  private float spd;
  
  private final float spdMax = 15;
  private final float spdRng = 5;
  private final float radMin = 15;
  private final float radRng = 25;
  
  public Actor( int w, int h ) {
    
    this.w = w;
    this.h = h;
    
    this.pos = new PVector( rng.nextInt( this.w ), - this.rad );
    this.vel = new PVector( );
    this.acc = new PVector( );
    
    float fac = rng.nextFloat( );
    
    this.rad = fac * this.radRng + this.radMin;
    this.spd = this.spdMax - fac * this.spdRng;
    
  }
  
  public void step( PGraphics canvas ) {
    
    this.acc.set( 0, this.spd / 2.0 );
    this.acc.rotate( ( rng.nextFloat( ) * 90 - 45 ) * PI / 180 );
    
    // Cheap physics cause who cares x)
    this.vel.add( this.acc );
    this.vel.setMag( this.spd );
    
    this.pos.add( this.vel );
    
    // Now we draw
    canvas.beginDraw( );
    canvas.noStroke( );
    canvas.fill( 255, 0, 0 );
    canvas.ellipse( this.pos.x, this.pos.y, this.rad, this.rad );
    canvas.endDraw( );
    
  }
  
  public boolean shouldKill( ) {
    
    return this.pos.y > this.h + this.rad;
    
  }
  
}

PImage sn;
PImage frosted;

PGraphics mask;
PGraphics canvas;

PShader maskShader;
PShader decayShader;

final float poissonLambda = 1.0 / 30;
int nSpawnCounter = 0;

ArrayList<Actor> actors = new ArrayList<Actor>( );

VideoExport videoExport;

void setup( ) {
  
  size( 720, 640, P2D );
  background( 0 );
  
  sn = loadImage( "StarryNight.jpg" );
  
  // Don't do this if we don't have to
  frosted = loadImage( "StarryNightFrosted.jpg" );
    
  if( frosted == null ) {
    
    frosted = new Froster( ).frost( rng, sn );
    frosted.save( "StarryNightFrosted.jpg" );
    
  }
  
  // Make mask
  mask = createGraphics( sn.width, sn.height, P2D );
  mask.noSmooth( );
  
  mask.beginDraw( );
  mask.background( 0 );
  mask.endDraw( );
  
  // Create canvas to use a masking shader
  canvas = createGraphics( sn.width, sn.height, P2D );
  
  maskShader = loadShader( "maskShader.glsl" );
  decayShader = loadShader( "decayShader.glsl" );
  
  videoExport = new VideoExport( this );
  videoExport.startMovie( );
  
}

void draw( ) {
  
  // Remove dead actors
  Iterator<Actor> actIter = actors.iterator( );
  while( actIter.hasNext( ) ) {
    
    Actor a = actIter.next( );
    
    if( a.shouldKill( ) )
      actIter.remove( );
    else
      a.step( mask );
      
  }
  
  // Poisson spawning
  if( nSpawnCounter == 0 ) {
    
    actors.add( new Actor( sn.width, sn.height ) );
    
    nSpawnCounter = round( - log( 1.0 - rng.nextFloat( ) ) / poissonLambda );
    
  } else
    --nSpawnCounter;
  
  canvas.beginDraw( );
  canvas.background( 0, 255, 0 );
  canvas.endDraw( );
  
  maskShader.set( "base", sn );
  maskShader.set( "mask", mask );
  maskShader.set( "frost", frosted );
  
  canvas.filter( maskShader );
  mask.filter( decayShader );
  
  image( canvas, 0, 0, pixelWidth, pixelHeight );
  
  videoExport.saveFrame( );
  
}

void keyPressed( ) {
  
  if( key == 'q' ) {
    
    videoExport.endMovie( );
    exit( );
    
  }
  
}
