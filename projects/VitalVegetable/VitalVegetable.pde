import java.util.Iterator;

final RandomWrapper rng = new RandomWrapper( );

final float SPD_MAG = 5.0;
final int N_SPAWN = 25;
final float P_SPAWN = 0.05;

final int MAX_LIFE = 250;
final int MIN_LIFE = 25;
final int RNG_LIFE = MAX_LIFE - MIN_LIFE;

final float N_SCALE = 0.007;
final int N_L_SCALE = 15;

class Spawn {
  
  PVector pos, spd;
  
  public Spawn( PVector pos, PVector spd ) {
    
    this.pos = new PVector( pos.x, pos.y );
    this.spd = new PVector( spd.x, spd.y );
    this.spd.setMag( 2 );
    
  }
  
  public void update( ) {
    
    this.pos.add( this.spd );
    
  }
  
  public boolean shouldDie( ) {
    
    return this.pos.x > pixelWidth ||
      this.pos.x < 0 ||
      this.pos.y > pixelHeight ||
      this.pos.y < 0 ||
      rng.nextFloat01( ) > Math.tanh( 3 * noise( this.pos.x * N_SCALE, this.pos.y * N_SCALE ) );
    
  }
  
  public void draw( ) {
    
    noStroke( );
    fill( 255, 10 );
    
    ellipse( this.pos.x, this.pos.y, 2, 2 );
    
  }
  
}

class Spawner {
  
  PVector pos, speed;
  
  ArrayList<Spawn> spawned;
  
  public Spawner( ) {
    
    this.pos = rng.nextPos( );
    this.speed = PVector.random2D( );
    this.speed.setMag( SPD_MAG );
    
    this.spawned = new ArrayList<Spawn>( );
    
  }
  
  public int count( ) {
    
    return this.spawned.size( );
    
  }
  
  public void update( ) {
    
    this.pos.add( this.speed );
    
    if( this.pos.x > pixelWidth || this.pos.x < 0 || this.pos.y > pixelHeight || this.pos.y < 0 )
      this.pos = rng.nextPos( );
    
    PVector up = PVector.random2D( );
    this.speed.add( up );
    this.speed.setMag( SPD_MAG );
    
    if( rng.nextFloat01( ) < P_SPAWN )
      this.spawned.add( new Spawn( this.pos, this.speed ) );
    
  }
  
  public void draw( ) {
    
    Iterator<Spawn> spawn = this.spawned.iterator( );
    while( spawn.hasNext( ) ) {
      
      Spawn s = spawn.next( );
      s.update( );
      s.draw( );
      
      if( s.shouldDie( ) )
        spawn.remove( );
      
    }
    
  }
  
}

ArrayList<Spawner> spawns = new ArrayList<Spawner>( );

void setup( ) {
  
  size( 720, 720 );
  background( 0 );
  
  for( int i = 0; i < N_SPAWN; ++i ) {
    
    spawns.add( new Spawner( ) );
    
  }
  
}

void draw( ) {
  
  int count = 0;
  
  for( Spawner s : spawns ) {
    s.update( );
    s.draw( );
    
    count += s.count( );
  }
  
  if( frameCount % 60 == 0 )
    System.out.println( count );
  
}

void keyPressed( ) {
  
  if( key == 'w' ) {
    
    saveFrame( );
    
  }
  
}