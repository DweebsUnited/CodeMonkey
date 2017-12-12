class SpringColorFactory implements ColorFactory {
  
  private color palette[] = { color( #229fa9 ), color( #49e5aa ), color( #f4649e ), color( #d2ebfb ), color( #f0d14f ) };
  
  private RandomWrapper rng;
  
  public SpringColorFactory( RandomWrapper rng ) {
    this.rng = rng;
  }
  
  public color make( ) {
    return this.palette[ this.rng.nextInt( this.palette.length ) ];
  }
  
}