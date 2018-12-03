package CodeMonkey.sort;

import processing.core.PImage;

public class VerticalImageSorter {

  public static void sort( PImage img ) {

    img.loadPixels( );

    for( int edx = img.height - 1; edx > 0; --edx ) {

      for( int ydx = 0; ydx < edx; ++ydx ) {

        for( int xdx = 0; xdx < img.width; ++xdx ) {

          if( ( img.pixels[ xdx + ydx * img.width ] & 0xFFFFFF ) < ( img.pixels[ xdx + ( 1 + ydx ) * img.width ] & 0xFFFFFF ) ) {

            int t = img.pixels[ xdx + ydx * img.width ];
            img.pixels[ xdx + ydx * img.width ] = img.pixels[ xdx + ( 1 + ydx ) * img.width ];
            img.pixels[ xdx + ( 1 + ydx ) * img.width ] = t;

          }

        }

      }

    }

    img.updatePixels( );

  }

}
