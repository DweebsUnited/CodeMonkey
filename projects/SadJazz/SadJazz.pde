// Many circles, moving with constant speed in constant random direction
// If two circles intersect, draw a line between their centers

// TODO: Make CircleFactory work on saved data

import java.util.Random;
RandomWrapper rng = new RandomWrapper( );

final int N_CIRC = 256;
int dCount = 0;

// 4k settings
//final float SPD_MIN = 1;
//final float SPD_MAX = 3;

//final float RAD_MIN = 256;
//final float RAD_MAX = 4 * RAD_MIN;

//final int P_LINE = 128;

// 720p settings
final float SPD_MIN = 0.25;
final float SPD_MAX = 1;

final float RAD_MIN = 32;
final float RAD_MAX = 2 * RAD_MIN;

final int P_LINE = 16;

final float SPD_RNG = SPD_MAX - SPD_MIN;
final float RAD_RNG = RAD_MAX - RAD_MIN;

Circle[] cs = new Circle[ N_CIRC ];

SandPainter p = new SandPainter( rng, P_LINE, 1 );

void setup( ) {

  // Draw setup
  // 4k
  //size( 3840, 2160 );
  // 720p
  size( 1280, 720 );
  background( 0 );

  PositionFactory pf = new CirclePositionFactory( rng );
  CircleFactory cf = new CircleFactory( SPD_MIN, SPD_RNG, RAD_MIN, RAD_RNG );
  FourseasonsColorFactory colf = new FourseasonsColorFactory( rng );

  for ( int i = 0; i < N_CIRC; ++i ) {

    cs[ i ] = cf.make( rng, pf, colf );
    
  }
  
}

void draw( ) {

  for ( int i = 0; i < N_CIRC; ++i ) {

    for ( int j = i + 1; j < N_CIRC; ++j ) {

      if ( cs[ i ].collide( cs[ j ] ) )
        cs[ i ].draw( p, cs[ j ] );
    }
    
  }

  for ( Circle c : cs ) {

    c.update( );
    
    if( ( c.pos.x <= 0 || c.pos.x > pixelWidth || c.pos.y <= 0 || c.pos.y > pixelHeight ) && !c.dead ) {
      System.out.print( dCount );
      System.out.print( " / " );
      System.out.println( N_CIRC );
      dCount += 1;
      c.dead = true;
    }
    
  }

  if ( dCount == N_CIRC ) {
    saveFrame( );
    noLoop( );
    System.out.println( "Done!" );
  }
  
}