import java.util.Random;
import java.util.List;
import java.util.ArrayList;

final Random rng = new Random( System.currentTimeMillis( ) );

class Particle {
  
  public PVector pos;
  public PVector vel;
  public int life;
  
  public Particle( ) {
    
    this.reset( );
    
  }
  
  public void reset( ) {
    
    this.pos = new PVector( rng.nextFloat( ) * pixelWidth, rng.nextFloat( ) * pixelHeight );
    
    float nX = this.pos.x * 0.01;
    float nY = this.pos.y * 0.01;
    this.vel = new PVector( noise( nX, nY, 0.0 ) * 2.0 - 1.0, noise( nX, nY, 1.0 ) * 2.0 - 1.0 );
    this.vel.setMag( 1 );
    
    this.life = (int)Math.round( Math.sqrt( rng.nextFloat( ) ) * 250 );
    
  }
  
  public boolean shouldLive( ) {
    
    return this.pos.x < pixelWidth && this.pos.x > 0 && this.pos.y < pixelHeight && this.pos.y > 0 && this.life > 0;
    
  }
  
  public void update( ) {
    
    float nX = this.pos.x * 0.01;
    float nY = this.pos.y * 0.01;
    
    PVector acc = new PVector( noise( nX, nY, 0.0 ) * 2.0 - 1.0, noise( nX, nY, 1.0 ) * 2.0 - 1.0 );
    
    this.vel.add( acc );
    this.vel.setMag( 1 );
    
    this.pos.add( vel );
    
    //this.pos.set( Math.round( this.pos.x ), Math.round( this.pos.y ) );
    
    this.life -= noise( nX, nY, 2.0 ) * 5;
    
    if( ! this.shouldLive( ) )
      this.reset( );
    
  }
  
  public void draw( ) {
    
    if( this.life < 0 )
      return;
    
    noStroke( );
    fill( 255, 25 );
    ellipse( this.pos.x, this.pos.y, 1, 1 );
    
  }
  
}

List<Particle> particles;

void setup( ) {
  
  size( 1280, 720 );
  background( 0 );
  
  particles = new ArrayList<Particle>( );
  for( int i = 0; i < 128; ++i )
    particles.add( new Particle( ) );
  
}

void draw( ) {
  
  //noStroke( );
  //fill( 0, 5 );
  //rect( 0, 0, pixelWidth, pixelHeight );
  
  for( Particle p : particles ) {
    
    p.draw( );
    p.update( );
    
  }
  
}

void keyPressed( ) {
  
  if( key == 'q' )
    saveFrame( "OliveSummer.png" );
  
}