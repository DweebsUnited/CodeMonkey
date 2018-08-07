// TODO: 
// FUNDAMENTAL ASSUMPTION: Every axis is between 0 and 1
//   This means intersecting each axis with the viewport, and setting its pixel scale individually

final int axis = 2;

import java.util.Random;
final Random rng = new Random( );

class Actor {
  
  private ArrayList<Float> pos;
  private ArrayList<Float> vel;
  private ArrayList<Float> acc;
  
  private ArrayList<Boolean> using;
  
  private boolean normalizeAcc = false;
  
  final private int F_UP = 5;
  
  public Actor( ) {
    
    this.pos = new ArrayList<Float>( );
    this.vel = new ArrayList<Float>( );
    this.acc = new ArrayList<Float>( );
    
    for( int i = 0; i < axis; ++i ) {
      
      this.pos.add( rng.nextFloat( ) );
      this.vel.add( 0.0 );
      this.acc.add( 0.0 );
      
      this.using.add( true );
      
    }
    
    this.using.set( rng.nextInt( axis ), false );
    
  }
  
  public void step( ) {

    for( int adx = 0; adx < axis; ++adx ) {
      
      float p = this.pos.get( adx );
      float v = this.vel.get( adx );
      float a = this.vel.get( adx );
      
      this.vel.set( adx, v + a );
      this.pos.set( adx, p + v );
      
      if( frameCount % F_UP == 0 ) {
        
        this.acc.set( adx, rng.nextFloat( ) );
        
        this.normalizeAcc = true;
        
      }
    
    }
    
    this.normalizeAccIfNeeded( );
    
  }
  
  public void normalize( float mag ) {
    
    float accum = 0.0;
    
    for( int adx = 0; adx < axis; ++adx ) {
      
      if( this.using.get( adx ) == false )
        continue;
      
      accum += pow( this.vel.get( adx ), 2 );
      
    }
    
    accum = sqrt( accum );
    
    for( int adx = 0; adx < axis; ++adx ) {
      
      if( this.using.get( adx ) == false )
        continue;
      
      this.vel.set( adx, this.vel.get( adx ) / accum );
      
    }
    
  }
  
  public void normalizeAccIfNeeded( ) {
    
    float accum = 0.0;
    
    for( int adx = 0; adx < axis; ++adx ) { }
    // TODO: Durrrr
    
  }
  
}

ArrayList<Actor> actors = new ArrayList<Actor>( );
final int nActors = 16;

void setup( ) {
  
  for( int adx = 0; adx < nActors; ++adx ) {
    
    actors.add( new Actor( ) );
    
  }
  
}

void draw( ) {
  
  
  
}
