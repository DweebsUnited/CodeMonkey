color[] palette = {
color( 22, 71, 233 ),color( 69, 252, 32 ),color( 255, 11, 31 ),color( 235, 4, 255 ),color( 254, 235, 114 ),color( 45, 161, 132 ),color( 138, 41, 132 ),color( 128, 37, 244 ),color( 138, 153, 173 ),color( 162, 131, 31 ),color( 152, 128, 143 ),color( 161, 243, 73 ),color( 245, 7, 143 ),color( 254, 123, 72 ),color( 244, 119, 184 ),color( 115, 111, 98 ),color( 108, 109, 173 ),color( 115, 186, 126 ),color( 170, 28, 173 ),color( 177, 105, 126 ),color( 170, 103, 200 ),color( 186, 89, 106 ),color( 192, 166, 59 ),color( 186, 163, 133 ),color( 248, 83, 133 ),color( 145, 84, 137 ),color( 150, 142, 102 ),color( 145, 140, 158 ),color( 191, 80, 158 ),color( 203, 125, 108 ),color( 167, 114, 133 )

};

void setup( ) {

  size( 32, 1 );
  background( 0 );

  loadPixels( );

  for( int idx = 0; idx < palette.length; idx++ ) {

    pixels[ idx ] = palette[ idx ];

  }

  updatePixels( );

  saveFrame( "herp.png" );

  noLoop( );

}

void draw( ) {



}
