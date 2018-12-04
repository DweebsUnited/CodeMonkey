package CodeMonkey.draw;

import processing.core.PApplet;
import processing.core.PImage;

public class HorizontalImageSorter {

  public static void sort( PApplet context, PImage img ) {

    img.loadPixels( );

    float[] sortField = new float[ img.width * img.height ];
    for( int pdx = 0; pdx < img.width * img.height; ++pdx )
      sortField[ pdx ] = context.red( img.pixels[ pdx ] ) + context.green( img.pixels[ pdx ] ) + context.blue( img.pixels[ pdx ] );

    for( int edx = img.width - 1; edx > 0; --edx ) {

      for( int xdx = 0; xdx < edx; ++xdx ) {

        for( int ydx = 0; ydx < img.height; ++ydx ) {

          int pdx = xdx + ydx * img.width;
          int ndx = xdx + 1 + ydx * img.width;

          if( sortField[ pdx ] < sortField[ ndx ] ) {

            int t = img.pixels[ pdx ];
            img.pixels[ pdx ] = img.pixels[ ndx ];
            img.pixels[ ndx ] = t;

            float tf = sortField[ pdx ];
            sortField[ pdx ] = sortField[ ndx ];
            sortField[ ndx ] = tf;

          }

        }

      }

    }

    img.updatePixels( );

  }

}
