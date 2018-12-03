package CodeMonkey.sort;

import processing.core.PImage;

public class HorizontalImageSorter {

  public static void sort( PImage img ) {

    img.loadPixels( );

    for( int edx = img.width - 1; edx > 0; --edx ) {

      for( int xdx = 0; xdx < edx; ++xdx ) {

        for( int ydx = 0; ydx < img.height; ++ydx ) {

          if( ( img.pixels[ xdx + ydx * img.width ] & 0xFFFFFF ) < ( img.pixels[ xdx + 1 + ydx * img.width ] & 0xFFFFFF ) ) {

            int t = img.pixels[ xdx + ydx * img.width ];
            img.pixels[ xdx + ydx * img.width ] = img.pixels[ xdx + 1 + ydx * img.width ];
            img.pixels[ xdx + 1 + ydx * img.width ] = t;

          }

        }

      }

    }

    img.updatePixels( );

  }

}
