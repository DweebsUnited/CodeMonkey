package CodeMonkey.transform.axis;

import CodeMonkey.transform.AxisTransform;

import java.util.Random;

// Random returns a random float in the configured range, defaulting to [0.0. 1.0)

public class ATRandom implements AxisTransform {

    Random rng;

    float origin, bound;

    public ATRandom(Random rng) {
        this.rng = rng;
        this.origin = 0.0f;
        this.bound = 1.0f;
    }

    public ATRandom(Random rng, float origin, float bound) {
        this.rng = rng;
        this.origin = origin;
        this.bound = bound;
    }

    @Override
    public float map(float x) {
        return rng.nextFloat(this.origin, this.bound);
    }
}
