class HistoryPainter implements PainterIntf {
  
  // Length of history
  private int hLen;
  
  // Circular buffer
  private int wp = 0;
  private int rc = 0;
  
  // Buffer arrays
  private PVector[] ss;
  private PVector[] fs;
  private color[] scs;
  private color[] fcs;
  
  private PainterIntf painter;
  
  public HistoryPainter( int hLen, PainterIntf painter ) {
   
    this.hLen = hLen;
    
    this.painter = painter;
    
    this.ss = new PVector[ hLen ];
    this.fs = new PVector[ hLen ];
    this.scs = new color[ hLen ];
    this.fcs = new color[ hLen ];
    
    for( int i = 0; i < hLen; ++i ) {
      
      this.ss[ i ] = new PVector( );
      this.fs[ i ] = new PVector( );
      
    }
    
  }
  
  public void draw( PVector s, PVector f, color sc, color fc ) {
    
    this.ss[ this.wp ].set( s );
    this.fs[ this.wp ].set( f );
    this.scs[ this.wp ] = sc;
    this.fcs[ this.wp ] = fc;
    
    this.wp = ( this.wp + 1 ) % this.hLen;
    this.rc = min( this.rc + 1, this.hLen );
    
  }
  
  public void drawFin( ) {
  
    int si = this.wp - this.rc;
    
    if( si < 0 )
      si += hLen;
      
    for( int i = 0; i < this.rc; ++i, si = ( si + 1 ) % this.hLen ) {
      
      this.painter.draw( this.ss[ si ], this.fs[ si ], this.scs[ si ], this.fcs[ si ] );
      
    }
    
    this.painter.drawFin( );
    
  };
  
}