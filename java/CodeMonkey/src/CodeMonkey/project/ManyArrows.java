package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.spatial.nD;
import processing.core.PApplet;

public class ManyArrows extends ProjectBase {

  public static void main( String[] args ) {

    PApplet.main( "CodeMonkey.project.ManyArrows" );

  }

  public void drawE( nD p ) {

    float[] c = p.coord( );

    this.noStroke( );
    this.fill( 255 * c[ 2 ], 255 * c[ 3 ], 255 * c[ 4 ], 100 );

    this.ellipse( c[ 0 ] * this.pixelWidth, c[ 1 ] * this.pixelHeight, 1, 1 );

  }

  public void sand( nD a, nD b ) {

    for( int pdx = 0; pdx < this.nSand; ++pdx ) {

      nD t = nD.lerp( a, b, this.rng.nextFloat( ) );

      this.drawE( t );

    }

  }

  private Random rng = new Random( );

  private final int nPs = 32;
  private final float vMag = 0.01f;
  private final float iMag2 = 0.02f;
  private final int nSand = 32;

  private ArrayList<nD> ps;
  private ArrayList<nD> vs;

  @Override
  public void settings( ) {

    this.size( 720, 640 );

    this.setName( );

  }

  @Override
  public void setup( ) {

    this.ps = new ArrayList<nD>( );
    this.vs = new ArrayList<nD>( );

    for( int pdx = 0; pdx < this.nPs; ++pdx ) {

      float[] d = { this.rng.nextFloat( ), this.rng.nextFloat( ), this.rng.nextFloat( ), this.rng.nextFloat( ), this.rng.nextFloat( ) };
      float[] v = { this.rng.nextFloat( ), this.rng.nextFloat( ), this.rng.nextFloat( ), this.rng.nextFloat( ), this.rng.nextFloat( ) };
      nD vv = new nD( v );
      vv.normalize( );

      this.ps.add( new nD( d ) );
      this.vs.add( vv );

    }

    this.background( 255 );

  }

  @Override
  public void draw( ) {

    for( int pdx = 0; pdx < this.nPs; ++pdx ) {

      nD p = this.ps.get( pdx );
      nD v = this.vs.get( pdx );

      p.add( v );

      boolean inBounds = true;
      for( float d : p.coord( ) ) {

        if( d < 0 || d > 1 )
          inBounds = false;

      }
      if( ! inBounds ) {

        p.set( new float[]{
            this.rng.nextFloat( ),
            this.rng.nextFloat( ),
            this.rng.nextFloat( ),
            this.rng.nextFloat( ),
            this.rng.nextFloat( )
        } );
        v.set( new float[]{
            2 * this.rng.nextFloat( ) - 1,
            2 * this.rng.nextFloat( ) - 1,
            2 * this.rng.nextFloat( ) - 1,
            2 * this.rng.nextFloat( ) - 1,
            2 * this.rng.nextFloat( ) - 1
        } );
        v.normalize( );
        v.mult( this.vMag );

      }

    }

    for( int pdx = 0; pdx < this.nPs; ++pdx ) {

      nD pa = this.ps.get( pdx );
      float[] ca = pa.coord( );

      for( int tdx = pdx + 1; tdx < this.nPs; ++tdx ) {

        nD pb = this.ps.get( tdx );
        float[] cb = pb.coord( );

        float d = 0;

        for( int ddx = 0; ddx < 2; ++ddx ) {

          d += ( ca[ ddx ] - cb[ ddx ] ) * ( ca[ ddx ] - cb[ ddx ] );

        }

        if( d < this.iMag2 )
          this.sand( pa, pb );

      }

    }

  }

}
