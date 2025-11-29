package CodeMonkey.transform.axis.context;

import CodeMonkey.transform.AxisTransform;

public class ATContextSetter implements AxisTransform {

    ATContext var;
    AxisTransform t;

    public ATContextSetter(ATContext var, AxisTransform t) {
        this.var = var;
        this.t = t;
    }

    @Override
    public float map(float x) {
        this.var.set(this.t.map(x));
        return x;
    }
}
