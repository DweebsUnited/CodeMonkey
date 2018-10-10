package CodeMonkey.transform;

public class ATNormalize implements AxisTransform {

  private float mVal, MVal;
  private float rng;

  private AxisTransform trans;

  public ATNormalize( AxisTransform trans, float minCoor, float maxCoor ) {

    this.trans = trans;

    this.mVal = trans.map( minCoor );
    this.MVal = trans.map( maxCoor );

    this.rng = this.MVal - this.mVal;

  }

  @Override
  public float map( float p ) {

    return ( this.trans.map( p ) - this.mVal ) / this.rng;

  }

}
