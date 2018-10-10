package CodeMonkey.transform.axis;

import CodeMonkey.transform.AxisTransform;

public class ATExponential implements AxisTransform {

  private float a, b, c;

  public ATExponential( float a, float b, float c ) {

    this.a = a;
    this.b = b;
    this.c = c;

  }

  @Override
  public float map( float c ) {

    return this.a * (float) Math.pow( c, this.b ) + this.c;

  }

}
