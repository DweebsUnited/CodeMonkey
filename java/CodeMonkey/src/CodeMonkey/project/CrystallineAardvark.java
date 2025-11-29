package CodeMonkey.project;

import CodeMonkey.transform.CoordinateTransform;
import CodeMonkey.transform.coordinate.CTSkew2D;
import processing.core.PApplet;
import processing.core.PVector;

public class CrystallineAardvark extends ProjectBase {

    PVector bl, br, tl, tr;

    public static void main( String[ ] args ) {
        PApplet.main( "CodeMonkey.project.CrystallineAardvark" );
    }

    @Override
    public void settings( ) {
        this.size( 720, 640 );
        this.setName( );
    }

    @Override
    public void setup( ) {
        this.bl = new PVector( 0.25f, 0.75f, 0 );
        this.br = new PVector( 0.75f, 0.75f, 0 );
        this.tl = new PVector( 0.25f, 0.25f, 0 );
        this.tr = new PVector( 0.75f, 0.25f, 0 );
    }

    @Override
    public void draw( ) {
        this.background(0);

        // Draw four corners
        this.circle(this.bl.x * this.width, this.bl.y * this.height, 5);
        this.circle(this.br.x * this.width, this.br.y * this.height, 5);
        this.circle(this.tl.x * this.width, this.tl.y * this.height, 5);
        this.circle(this.tr.x * this.width, this.tr.y * this.height, 5);

        // Draw lines between corners
        //this.line(bl.x, bl.y,
    }

    @Override
    public void mouseDragged( ) {
        // Find closest corner, snap to mouse

    }
}