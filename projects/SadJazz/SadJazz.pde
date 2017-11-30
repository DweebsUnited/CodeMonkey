// Many circles, moving with constant speed in constant random direction
// If two circles intersect, draw a line between their centers

import java.util.Random;
RandomWrapper rng = new RandomWrapper( );

final int N_CIRC = 128;

final float SPD_MIN = 0.25;
final float SPD_MAX = 1;
final float SPD_RNG = SPD_MAX - SPD_MIN;

final float RAD_MIN = 32;
final float RAD_MAX = 2 * RAD_MIN;
final float RAD_RNG = RAD_MAX - RAD_MIN;

Circle[] cs = new Circle[ N_CIRC ];

Painter p = new SandPainter( );

void setup( ) {

  p.setRNG( rng );

  // Draw setup
  size( 1280, 720 );
  background( 0 );

  PositionFactory pf = new CirclePositionFactory( rng );
  CircleFactory cf = new CircleFactory( SPD_MIN, SPD_RNG, RAD_MIN, RAD_RNG );

  for ( int i = 0; i < N_CIRC; ++i ) {

    cs[ i ] = cf.make( rng, pf );
  }
}

void draw( ) {

  for ( int i = 0; i < N_CIRC; ++i ) {

    for ( int j = i + 1; j < N_CIRC; ++j ) {

      if ( cs[ i ].collide( cs[ j ] ) )
        cs[ i ].draw( p, cs[ j ] );
    }

    // p.pPoint( cs[ i ].pos );
  }

  for ( Circle c : cs ) {

    c.update( );
  }
}