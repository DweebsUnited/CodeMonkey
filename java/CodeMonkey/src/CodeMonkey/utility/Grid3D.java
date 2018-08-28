package CodeMonkey.utility;

import java.util.ArrayList;

public class Grid3D<T> {

  private ArrayList<ArrayList<ArrayList<T>>> grid;
  public int width, height, depth;

  public Grid3D( int width, int height, int depth ) {

    this.width = width;
    this.height = height;
    this.depth = depth;

    this.grid = new ArrayList<ArrayList<ArrayList<T>>>( );

    for( int hdx = 0; hdx < height; ++hdx ) {

      ArrayList<ArrayList<T>> row = new ArrayList<ArrayList<T>>( );

      this.grid.add( row );

      for( int wdx = 0; wdx < width; ++wdx ) {

        ArrayList<T> col = new ArrayList<T>( );

        row.add( col );

        for( int ddx = 0; ddx < depth; ++ddx ) {

          col.add( null );

        }

      }

    }

  }

  public void set( int x, int y, int z, T val ) {

    this.grid.get( y ).get( x ).set( z, val );

  }

  public T get( int x, int y, int z ) {

    try {
      return this.grid.get( y ).get( x ).get( z );
    } catch( Exception e ) {

      System.out.println( String.format( "Error getting: %d, %d in size %d, %d", x, y, this.grid.get( 0 ).size( ), this.grid.size( ) ) );
      return null;

    }

  }

}
