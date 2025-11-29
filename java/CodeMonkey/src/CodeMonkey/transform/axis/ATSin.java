package CodeMonkey.transform.axis;

// y = a * sin( w x + p ) + o

import CodeMonkey.transform.AxisTransform;

public class ATSin implements AxisTransform {
    private float a, w, p, o;

    public ATSin( float a, float w, float p, float o ) {
        this.a = a;
        this.w = w;
        this.p = p;
        this.o = o;
    }

    @Override
    public float map( float x ) {
        return this.a * (float) Math.sin( this.w * x + this.p ) + this.o;
    }
}
