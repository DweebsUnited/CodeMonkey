package CodeMonkey.draw;

import CodeMonkey.sort.HorizontalImageSorter;
import CodeMonkey.sort.VerticalImageSorter;
import CodeMonkey.spatial.ICoord;
import processing.core.PApplet;
import processing.core.PImage;

public class ImageTexture {

  private void rot( int n, ICoord c, int rx, int ry ) {

    if( ry == 0 ) {

      if( rx == 1 ) {

        c.x = n - 1 - c.x;
        c.y = n - 1 - c.y;

      }

      int t = c.x;
      c.x = c.y;
      c.y = t;

    }

  }

  private void d2xy( int n, int d, ICoord c ) {

    int rx, ry, s, t = d;

    c.x = 0;
    c.y = 0;

    for( s = 1; s < n; s *= 2 ) {

      rx = 1 & ( t / 2 );
      ry = 1 & ( t ^ rx );
      this.rot( s, c, rx, ry );
      c.x += s * rx;
      c.y += s * ry;
      t /= 4;

    }

  }

  public PImage sn;
  public final int sz;
  public final int sz2;

  public ImageTexture( PApplet context, String img ) {

    // Load image
    this.sn = context.loadImage( img );

    HorizontalImageSorter.sort( this.sn );
    VerticalImageSorter.sort( this.sn );

    //System.out.println( String.format( "Original size: %d x %d", sn.width, sn.height ) );

    // Get next smallest power of 2 that fits in
    int tsz = Math.min( this.sn.width, this.sn.height );
    tsz = (int)Math.floor( Math.log( tsz ) / Math.log( 2 ) );
    //System.out.println( String.format( "Next smallest = 2^%d", sz ) );

    // Take 2 to it
    this.sz = 0x01 << tsz;
    //System.out.println( String.format( "New size: %d x %d", sz, sz ) );

    // Resize the image
    this.sn.resize( this.sz, this.sz );

    // Num pixels
    this.sz2 = this.sz * this.sz - 1;

    // Load the pixels array
    this.sn.loadPixels( );

  }

  // Get color at t in [ 0, 1 )
  public int colorAt( float t ) {

    ICoord c = new ICoord( 0, 0 );

    this.d2xy( this.sz2, Math.round( t * this.sz2 ), c );

    return this.sn.pixels[ c.x + c.y * this.sz ];

  }

  //Get color at t in [ 0, sz2 ]
  public int colorAt( int t ) {

    ICoord c = new ICoord( 0, 0 );

    this.d2xy( this.sz2, t, c );

    return this.sn.pixels[ c.x + c.y * this.sz ];

  }

}

