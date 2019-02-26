package CodeMonkey.neuron;

import processing.core.PVector;

public class Fixed extends Insulating {
  
  public Fixed( ) {
    super( );
  }
  public Fixed( PVector sp ) {
    super( sp );
  }
  
  @Override
  public void update( ) { }
  
  @Override
  public int c( ) { return 0x00000000; }

}
