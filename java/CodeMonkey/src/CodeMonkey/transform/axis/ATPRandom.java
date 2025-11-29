package CodeMonkey.transform.axis;

import CodeMonkey.transform.AxisTransform;

import java.util.Random;

// Random returns a random float in the configured range, defaulting to [0.0. 1.0)

public class ATPRandom implements AxisTransform {

    Random rng;

    AxisTransform origin, bound;

    public ATPRandom(Random rng) {
        this.rng = rng;
        this.origin = new ATConst(0.0f);
        this.bound = new ATConst(1.0f);
    }

    public ATPRandom(Random rng, AxisTransform origin, AxisTransform bound) {
        this.rng = rng;
        this.origin = origin;
        this.bound = bound;
    }

    @Override
    public float map(float x) {
        return rng.nextFloat(this.origin.map(x), this.bound.map(x));
    }
}
