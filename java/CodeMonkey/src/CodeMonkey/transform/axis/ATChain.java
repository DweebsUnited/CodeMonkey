package CodeMonkey.transform.axis;

import CodeMonkey.transform.AxisTransform;

// Compose axis transforms by applying the incoming coordinate to each given transform in order

// For example: ATChain(ATLinear(), ATSin()).map(x) -> ATSin(ATLinear(x))

public class ATChain implements AxisTransform {
    AxisTransform[] ts;

    public ATChain(AxisTransform... ts) {
        this.ts = ts;
    }

    @Override
    public float map(float x) {
        for(AxisTransform t : ts) {
            x = t.map(x);
        }
        return x;
    }
}
