package CodeMonkey.spatial;

import processing.core.PVector;

public interface Mirror extends Intersectable {

  public PVector normal( PVector p );
  public Ray bounce( Ray r );

}
