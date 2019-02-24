package CodeMonkey.neuron;

import processing.core.PVector;

public class Responsive extends Conducting {

  private float dLen;

  public Responsive( ) {
    super( );
  }

  public Responsive( PVector sp ) {
    super( sp );
  }

  @Override
  public int c( ) {
    return 0xFF00FF00;
  }

  @Override
  public void update( ) {

    super.update( );

    this.dLen = - 1000000000000000000f * this.v * this.dMag;

  }

  @Override
  protected float driveLen( ) {

    return this.dLen;

  }

}
