package CodeMonkey.utility;

import java.util.ArrayList;

public class Grid2D<T> {

  private ArrayList<ArrayList<T>> grid;
  public int width, height;

  public Grid2D( int width, int height ) {

    this.width = width;
    this.height = height;

    this.grid = new ArrayList<ArrayList<T>>( );

    for( int hdx = 0; hdx < height; ++hdx ) {

      ArrayList<T> row = new ArrayList<T>( );

      this.grid.add( row );

      for( int wdx = 0; wdx < width; ++wdx ) {

        row.add( null );

      }

    }

  }

  public void set( int x, int y, T val ) {

    this.grid.get( y ).set( x, val );

  }

  public T get( int x, int y ) {

    try {
      return this.grid.get( y ).get( x );
    } catch( Exception e ) {

      System.out.println( String.format( "Error getting: %d, %d in size %d, %d", x, y, this.grid.get( 0 ).size( ), this.grid.size( ) ) );
      return null;

    }

  }

}
