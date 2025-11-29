package CodeMonkey.transform.coordinate;

import CodeMonkey.transform.CoordinateTransform;
import processing.core.PVector;

// Skew transform applies a 2d coordinate to a square defined by the 4 corners

public class CTSkew2D implements CoordinateTransform {

    PVector to, tb, bo, bb;

    public CTSkew2D( PVector bl, PVector br, PVector tl, PVector tr) {
        this.tb = tr.copy(); // We store as top basis L->R
        this.tb.sub(tl);
        this.to = tl.copy(); // And top offset

        this.bb = br.copy(); // Bottom basis
        this.bb.sub(bl);
        this.bo = bl.copy(); // And offset
    }

    @Override
    // Expects a 2D vector from [0,1]
    public PVector map(PVector p) {
        // Map two coords from L to R along T and B applied to incoming x coord
        PVector tmap = this.tb.copy();
        tmap.mult(p.x);
        tmap.add(this.to);
        PVector bmap = this.bb.copy();
        bmap.mult(p.x);
        bmap.add(this.bo);
        // Then map from Bmap to Tmap applied to y
        PVector fb = tmap.copy();
        fb.sub(bmap);
        fb.mult(p.y);
        fb.add(bmap);

        return fb;
    }
}
