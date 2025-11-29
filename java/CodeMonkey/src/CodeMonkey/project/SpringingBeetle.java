package CodeMonkey.project;

import CodeMonkey.transform.AxisTransform;
import CodeMonkey.transform.axis.*;
import CodeMonkey.transform.axis.context.ATContext;
import CodeMonkey.utility.Pair;
import processing.core.PApplet;

import java.util.Random;
import java.util.Vector;

public class SpringingBeetle extends ProjectBase {

    final int WIDTH = 720;
    final int HEIGHT = 640;

    final float NUM_WAVES = 8.0f;

    public static void main( String[ ] args ) {
        PApplet.main( "CodeMonkey.project.SpringingBeetle" );
    }

    @Override
    public void settings( ) {
        this.size( this.WIDTH, this.HEIGHT );
        this.setName( );
    }

    @Override
    public void setup( ) {
        this.background(0);
        this.noLoop();

        Random rng = new Random( );

        // These values are calculated based on the x coordinate each time wave.map() is called

        ATContext x_raw = new ATContext();
        ATContext x_norm = new ATContext();
        ATContext x_phase = new ATContext();

        // These values can be set per wave, as they are not overridden

        ATContext phase_offset = new ATContext();

        // These are for final conversion to screen space

        ATContext screen_amp = new ATContext();
        ATContext screen_off = new ATContext();

        // This is the wave pipeline definition!

        AxisTransform wave = new ATChain(
            // First set up variables
            // Save x_raw in case we need it later
            x_raw.set(new ATUnit()),
            // Convert from screen space to norm space [0.0, 1.0)
            x_norm.set(new ATLinear(1.0f / this.WIDTH, 0.0f)),
            // Convert from screen to phase space and set in context var
            x_phase.set(x_raw.chain(new ATLinear((NUM_WAVES * 2.0f * PI) / (this.WIDTH), 0.0f))),

            // We're going to randomly slide phase_offset each step
            phase_offset.set(
                phase_offset.chain(
                    new ATPLinear(
                        new ATConst(1.0f),
                        new ATPRandom(
                            rng,
                            new ATConst(0.0f),
                            x_norm.chain(new ATLinear(2.5f, 0.0000001f))
                        )
                    )
                )
            ),

            // Only need one phase offset, so use the one we are stepping randomly
            new ATChain(
                new ATConst(0.0f),
                new ATPSin( // Generate sin as [-1.0, 1.0]
                    new ATConst(1.0f),
                    new ATConst(1.0f),
                    phase_offset,
                    new ATConst(0.0f)
                )
            ),

            // Now convert y back to screen space
            new ATPLinear(screen_amp, screen_off)
        );

        // Generate waves!
        float wave_off = this.HEIGHT / 5.0f;
        for(Pair<Float> pair : new Pair[]{
            new Pair<>(0.15f * (this.HEIGHT) / 2.0f, wave_off * 1.0f),
            new Pair<>(0.15f * (this.HEIGHT) / 2.0f, wave_off * 2.0f),
            new Pair<>(0.15f * (this.HEIGHT) / 2.0f, wave_off * 3.0f),
            new Pair<>(0.15f * (this.HEIGHT) / 2.0f, wave_off * 4.0f),
        }) {

            screen_amp.set(pair.a);
            screen_off.set(pair.b);

            phase_offset.set(rng.nextFloat(0.0f, PI));

            Vector<Float> pts = new Vector<>();
            for(int x = 0; x < this.width; x++ ) {
                float y = wave.map(x);
                // System.out.printf("%d -- %f\n", x, y);
                pts.add(y);
            }

            stroke(255);
            strokeWeight(1.0f);
            for (int x = 0; x < this.width - 1; x++) {
                line(x, pts.get(x), x + 1, pts.get(x + 1));
            }
        }
    }

    @Override
    public void draw( ) {
    }
}
