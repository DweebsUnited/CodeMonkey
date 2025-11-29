package CodeMonkey.transform.axis.context;

// ATContext is a little special, it's map function returns whatever value was set by an ATContextSet
// This allows setting variables that can be referred back to in a pipeline

import CodeMonkey.transform.AxisTransform;
import CodeMonkey.transform.axis.ATConst;

public class ATContext implements AxisTransform {
    float c;

    public ATContext() {
    }

    public ATContext(float c) {
        this.c = c;
    }

    public void set(float c) {
        this.c = c;
    }

    public ATContextSetter set(AxisTransform t) {
        return new ATContextSetter(this, t);
    }

    public ATContextChainer chain(AxisTransform... ts) {
        return new ATContextChainer(this, ts);
    }

    @Override
    public float map(float c) {
        return this.c;
    }
}
