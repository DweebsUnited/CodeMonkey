package CodeMonkey.transform.axis;

import CodeMonkey.transform.AxisTransform;

public class ATLinear implements AxisTransform {

  private float inm, inM, inR, outm, outM, outR;

  public ATLinear( float inMin, float inMax, float outMin, float outMax ) {

    this.inm = inMin;
    this.inM = inMax;
    this.inR = inMax - inMin;
    this.outm = outMin;
    this.outM = outMax;
    this.outR = outMax - outMin;

  }

  public ATLinear( float off, float scale ) {

    this.inm = 0;
    this.inR = 1;
    this.outm = off;
    this.outR = scale;

  }

  @Override
  public float map( float c ) {

    return ( c - this.inm ) / this.inR * this.outR + this.outm;

  }

}
