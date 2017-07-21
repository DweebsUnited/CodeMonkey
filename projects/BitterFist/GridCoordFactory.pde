class GridCoordFactory {
  
    private int gridWide;
    private int gridTall;
    private int pixWide;
    private int pixTall;
  
    GridCoordFactory( int gridWide, int gridTall, int pixWide, int pixTall ) {
      
        this.gridWide = gridWide;
        this.gridTall = gridTall;
        
        this.pixWide = pixWide;
        this.pixTall = pixTall;
      
    }
  
    PVector make( int idx, int jdx ) {
      
        return new PVector( this.pixWide / this.gridWide * ( idx + 0.5 ), this.pixTall / this.gridTall * ( jdx + 0.5 ) );
      
    }
  
}