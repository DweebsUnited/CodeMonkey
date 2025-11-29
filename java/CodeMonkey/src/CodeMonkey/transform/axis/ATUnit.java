package CodeMonkey.transform.axis;

import CodeMonkey.transform.AxisTransform;

// y = x

public class ATUnit implements AxisTransform {
    public ATUnit() {
    }

    @Override
    public float map( float x ) {
        return x;
    }
}
