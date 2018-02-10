class AutumnColorFactory implements ColorFactory {
  
  private color palette[] = { color( #96cd39 ), color( #f5ff65 ), color( #ffba47 ), color( #ff5b44 ) };
  
  private RandomWrapper rng;
  
  public AutumnColorFactory( RandomWrapper rng ) {
    this.rng = rng;
  }
  
  public color make( ) {
    return this.palette[ this.rng.nextInt( this.palette.length ) ];
  }
  
}