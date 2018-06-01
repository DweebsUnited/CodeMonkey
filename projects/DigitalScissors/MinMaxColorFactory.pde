class MinMaxColorFactory implements ColorFactoryIntf {
  
    private int mr;
    private int Mr;
    private int mg;
    private int Mg;
    private int mb;
    private int Mb;
    private int o;
    
    private Random rng = new Random( );
  
    public MinMaxColorFactory( int mr, int Mr, int mg, int Mg, int mb, int Mb, int o ) {
      
        this.mr = mr;
        this.Mr = Mr;
        this.mg = mg;
        this.Mg = Mg;
        this.mb = mb;
        this.Mb = Mb;
        
        this.o = o;
        
    }
        
    public color make( ) {
      
        int r = this.Mr - this.mr;
        int g = this.Mg - this.mg;
        int b = this.Mb - this.mb;
        
        if( r != 0 )
            r = this.rng.nextInt( r );
        r += this.mr;
        
        if( g != 0 )
            g = this.rng.nextInt( g );
        g += this.mg;
        
        if( b != 0 )
            b = this.rng.nextInt( b );
        b += this.mb;
      
        return color( r, g, b, this.o );
        
    }
}