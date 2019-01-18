package CodeMonkey.transform.axis;

import CodeMonkey.transform.AxisTransform;

public class ATSigmoidContrast implements AxisTransform {

  float thresh, contr, s, t;

  public ATSigmoidContrast( float thresh, float contr ) {

    this( thresh, contr, false );

  }

  public ATSigmoidContrast( float thresh, float contr, boolean flip ) {

    this.thresh = thresh;
    this.contr = contr;
    this.s = flip ? 1 : 0;

    this.t = 1f / ( 1 + (float)Math.exp( thresh * contr ) );

  }

  @Override
  public float map( float c ) {

    return this.s - (float)( ( 1f / ( 1 + Math.exp( this.contr * ( this.thresh - c ) ) ) - this.t ) / ( 1f / ( 1 + Math.exp( this.contr * ( this.thresh - 1 ) ) ) - this.t ) );

  }

}
