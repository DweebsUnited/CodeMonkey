package CodeMonkey.transform.axis;

// y = x * a + o

import CodeMonkey.transform.AxisTransform;

public class ATPLinear implements AxisTransform {
    AxisTransform a, o;

    public ATPLinear(AxisTransform a, AxisTransform o) {
        this.a = a;
        this.o = o;
    }

    @Override
    public float map( float x ) {
        return x * this.a.map(x) + this.o.map(x);
    }

}
