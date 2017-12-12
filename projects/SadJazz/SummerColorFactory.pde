class SummerColorFactory implements ColorFactory {
  
  private color palette[] = { color( #ff598f ), color( #fd8a5e ), color( #e0e300 ), color( #01dddd ), color( #00bfaf ) };
  
  private RandomWrapper rng;
  
  public SummerColorFactory( RandomWrapper rng ) {
    this.rng = rng;
  }
  
  public color make( ) {
    return this.palette[ this.rng.nextInt( this.palette.length ) ];
  }
  
}