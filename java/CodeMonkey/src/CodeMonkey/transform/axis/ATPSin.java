package CodeMonkey.transform.axis;

// y = a * sin( w x + p ) + o

import CodeMonkey.transform.AxisTransform;

public class ATPSin implements AxisTransform {
    private AxisTransform a, w, p, o;

    public ATPSin(AxisTransform a, AxisTransform w, AxisTransform p, AxisTransform o ) {
        this.a = a;
        this.w = w;
        this.p = p;
        this.o = o;
    }

    @Override
    public float map( float x ) {
        return this.a.map(x) * (float) Math.sin( this.w.map(x) * x + this.p.map(x) ) + this.o.map(x);
    }
}
