class PPBColorFactory implements ColorFactory {
  
  private color palette[] = { color( 190, 252, 255 ), color( 222, 255, 250 ), color( 255, 218, 245 ), color( 190, 252, 255 ), color( 230, 198, 255 ) };
  
  private RandomWrapper rng;
  
  public PPBColorFactory( RandomWrapper rng ) {
    this.rng = rng;
  }
  
  public color make( ) {
    return this.palette[ this.rng.nextInt( this.palette.length ) ];
  }
  
}