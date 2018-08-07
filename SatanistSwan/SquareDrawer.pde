class SquareDrawer {

  private int xGrid, yGrid;

  private FloatSampler angleSamp, spaceSamp, drawSamp;

  private Random rng;

  private final PVector cMin = new PVector( 0.0, 0.0 );
  private final PVector cMax = new PVector( 1.0, 1.0 );

  // Random is pretty obvious
  // Grids are number of boxes in each dimension
  // angleSamp should return a radian line angle in 0-PI
  // spaceSamp should return a float offset in 0-1
  //   Both samplers will receive coordinates in 0-1
  public SquareDrawer( int xGrid, int yGrid, FloatSampler angleSamp, FloatSampler spaceSamp, FloatSampler drawSamp ) {

    this.rng = new Random( );

    this.xGrid = xGrid;
    this.yGrid = yGrid;

    this.angleSamp = angleSamp;
    this.spaceSamp = spaceSamp;
    this.drawSamp  = drawSamp;

  }

  public void draw( PGraphics canvas, color c, int stroke ) {

    canvas.beginDraw( );
    canvas.noFill( );
    canvas.stroke( c );
    canvas.strokeWeight( stroke );

    float pxw = canvas.pixelWidth;
    float pxh = canvas.pixelHeight;

    float xSize = pxw / this.xGrid;
    float ySize = pxh / this.yGrid;

    PVector min = new PVector( ), mid = new PVector( ), max = new PVector( );

    for ( int xdx = 0; xdx < this.xGrid; ++xdx ) {

      //System.out.println( String.format( "X: %d", xdx ) );

      for ( int ydx = 0; ydx < this.yGrid; ++ydx ) {

        //System.out.println( String.format( "Y: %d", ydx ) );

        // These are in screen space
        min.set( xdx         * xSize, ydx         * ySize );
        max.set( ( xdx + 1 ) * xSize, ( ydx + 1 ) * ySize );
        mid.set( ( min.x + max.x ) / 2, ( min.y + max.y ) / 2 );
        
        // Should we even draw this box?
        if( this.drawSamp.sample( mid.x / pxw, mid.y / pxh ) > 0.5 )
          continue;

        // We will do line construction in "line space"
        // Clip box is 0,0 -> 1,1

        // Construct ray
        // Pick direction from angleSampler
        PVector d = new PVector( 1.0, 0.0 );
        d.rotate( tan( this.angleSamp.sample( mid.x / pxw, mid.y / pxh ) ) );
        d.normalize( );

        // Pick origin, random offset for first line ( guarantees first one hits the box )
        PVector o = new PVector( 0.0, rng.nextFloat( ) );
        o.sub( d );

        // Make normal
        PVector hat = d.copy( );
        hat.rotate( PI / 2.0 );
        hat.mult( this.spaceSamp.sample( mid.x / pxw, mid.y / pxh ) );

        // Work up and down
        boolean addGood = true;
        PVector addO = o.copy( );
        boolean subGood = true;
        PVector subO = o.copy( );

        // Run first intersection with o
        {

          Intersection i = intersect( d, o, cMin, cMax );

          //System.out.println( String.format( "%f,%f -> %f,%f = %d", o.x, o.y, d.x, d.y, ( i.intersects ? 1 : 0 ) ) );

          if ( i.intersects == false ) {

            System.out.println( "Something went very wrong..." );
          } else {

            canvas.line( i.pm.x * xSize + min.x, i.pm.y * ySize + min.y, i.pM.x * xSize + min.x, i.pM.y * ySize + min.y );
          }
        }

        // To do crosshatching:
        //   +3 +2 +1 o0 -1 -2 -3
        //   Append one side to the reversed other side: ( [ +1, +2, +3 ], o0, [ -1, -2, -3 ] ) -> [ +3, +2, +1, o0, -1, -2, -3 ]
        //   Iterate from S->E->E->S->S ...

        while ( addGood == true || subGood == true ) {

          // Check add direction
          if ( addGood ) {

            addO.add( hat );

            Intersection i = intersect( d, addO, cMin, cMax );

            if ( i.intersects ) {

              canvas.line( i.pm.x * xSize + min.x, i.pm.y * ySize + min.y, i.pM.x * xSize + min.x, i.pM.y * ySize + min.y );
            } else
              addGood = false;
          }

          // Check sub direction 
          if ( subGood ) {

            subO.sub( hat );

            Intersection i = intersect( d, subO, cMin, cMax );

            if ( i.intersects ) {

              canvas.line( i.pm.x * xSize + min.x, i.pm.y * ySize + min.y, i.pM.x * xSize + min.x, i.pM.y * ySize + min.y );
            } else
              subGood = false;
          }
        }
      }
    }

    canvas.endDraw( );
  }
}
