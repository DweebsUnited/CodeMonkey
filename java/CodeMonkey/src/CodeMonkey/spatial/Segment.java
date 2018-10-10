package CodeMonkey.spatial;

import processing.core.PVector;

public class Segment {

  public PVector a;
  public PVector b;

  public Segment( PVector a, PVector b ) {

    this.a = a.copy( );
    this.b = b.copy( );

  }

}
