package CodeMonkey.transform.axis.context;

import CodeMonkey.transform.AxisTransform;

public class ATContextChainer implements AxisTransform {

    ATContext var;
    AxisTransform[] ts;

    public ATContextChainer(ATContext var, AxisTransform... ts) {
        this.var = var;
        this.ts = ts;
    }

    @Override
    public float map(float x) {
        x = this.var.map(x);

        for(AxisTransform t : ts) {
            x = t.map(x);
        }
        return x;
    }
}
