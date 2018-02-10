final int N_PTS = 128;
final int N_LNS = 128;
final int N_TRI = 4;
final float alpha = 0.05;
final int D_LIM = 2;

final color white = color( 255 );
final color black = color( 0 );

RandomWrapper rng = new RandomWrapper( );

SandPainter p = new SandPainter( rng, N_PTS );

UniformDistribution ud = new UniformDistribution( 0, 1 );
UniformDistribution pd = new UniformDistribution( -1, 1 );
Gaussian01Distribution gd = new Gaussian01Distribution( 1 );

void paintTri( PVector ma, PVector mb, PVector mc, int depth ) { //<>//
  
  if( depth >= D_LIM )
    return;
  
  for( int ldx = 0; ldx < N_LNS; ++ldx ) {
      
      PVector ab = rng.pickAlong( ma, mb, ud );
      PVector bc = rng.pickAlong( mb, mc, ud );
      PVector ca = rng.pickAlong( mc, ma, ud );
      
      p.pLine( ab, bc, white, alpha, pd );
      p.pLine( bc, ab, white, alpha, pd );
      p.pLine( bc, ca, white, alpha, pd );
      p.pLine( ca, bc, white, alpha, pd );
      p.pLine( ca, ab, white, alpha, pd );
      p.pLine( ab, ca, white, alpha, pd );
        
    }
  
  for( int tdx = 0; tdx < N_TRI; ++tdx )
    paintTri( rng.pickAlong( ma, mb, gd ), rng.pickAlong( mb, mc, gd ), rng.pickAlong( mc, ma, gd ), depth + 1 );
  
}

void setup( ) {
  
  size( 1280, 720 );
  background( black );
  
  float maa = 360 * rng.nextFloat01( );
   
  PVector mb = rng.nextPAtAng( radians( maa + 130 + rng.nextGaussian(  ) * 10 ), 1, 3 );
  PVector mc = rng.nextPAtAng( radians( maa - 130 + rng.nextGaussian(  ) * 10 ), 1, 3 );
  
  maa = radians( maa );
  PVector ma = rng.nextPAtAng( maa, 1, 3 );
  
  p.prepare( );
  paintTri( ma, mb, mc, 0 );
  p.save( );
  
  System.out.println( "Done" );
  
  saveFrame( );
  
  noLoop( );
  
}