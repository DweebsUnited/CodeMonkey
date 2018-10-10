package CodeMonkey.transform.axis;

import CodeMonkey.transform.AxisTransform;

public class ATSigmoid implements AxisTransform {

  float s, o;

  public ATSigmoid( float s, float o ) {

    this.s = s;
    this.o = o;

  }

  private float map( float c, float s, float o ) {

    return (float) ( 1.0f / ( 1.0f + Math.exp( -s * ( c - o ) ) ) );

  }

  @Override
  public float map( float c ) {

    return this.map( c, this.s, this.o );

  }

}

