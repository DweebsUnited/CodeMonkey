package CodeMonkey.transform.axis;

import CodeMonkey.transform.AxisTransform;

// y = c

public class ATConst implements AxisTransform {

    float c;
    public ATConst(float c) {
        this.c = c;
    }

    @Override
    public float map(float x) {
        return this.c;
    }
}
