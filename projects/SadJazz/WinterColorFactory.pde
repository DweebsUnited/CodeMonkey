    class WinterColorFactory implements ColorFactory {
  
  private color palette[] = { color( #9bdbff ), color( #27c5ef ), color( #00a4b7 ), color( #4b69c8 ), color( #664a97 ) };
  
  private RandomWrapper rng;
  
  public WinterColorFactory( RandomWrapper rng ) {
    this.rng = rng;
  }
  
  public color make( ) {
    return this.palette[ this.rng.nextInt( this.palette.length ) ];
  }
  
}