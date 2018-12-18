package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.genetic.Evaluator;
import CodeMonkey.genetic.Gene;
import CodeMonkey.genetic.GeneFactory;
import CodeMonkey.genetic.Genome;
import CodeMonkey.genetic.Population;
import CodeMonkey.genetic.breed.Breeder;
import CodeMonkey.genetic.champ.ChampionSelector;
import CodeMonkey.genetic.mutate.Mutator;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class PluggablePluto extends ProjectBase {

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.PluggablePluto" );

  }

  private static Random rng = new Random( );

  private static class Circle implements Gene {

    public int x, y;
    public int color;
    public int alpha;
    public int rad;

    public Circle( int x, int y, int color, int alpha, int rad ) {

      this.x = x;
      this.y = y;
      this.color = color;
      this.alpha = alpha;
      this.rad = rad;

    }

    public Circle( Circle c ) {

      this.x = c.x;
      this.y = c.y;
      this.color = c.color;
      this.alpha = c.alpha;
      this.rad = c.rad;

    }

  }

  private class CircleGenetics implements GeneFactory<Circle>, Evaluator<Circle>, ChampionSelector<Circle>, Breeder<Circle>, Mutator<Circle> {

    private Random rng = new Random( );
    private PGraphics canvas;
    private PImage target;
    private int nChamp;
    private float mChance;

    public CircleGenetics( PApplet context, PImage target, int nChamp, float mChance ) {

      this.canvas = context.createGraphics( target.width, target.height );
      this.target = target;
      this.nChamp = nChamp;
      this.mChance = mChance;

    }

    public int makeX( ) {
      return this.rng.nextInt( this.target.width );
    }
    public int makeY( ) {
      return this.rng.nextInt( this.target.height );
    }
    public int makeC( ) {
      return this.rng.nextInt( 255 );
    }
    public int makeA( ) {
      return this.rng.nextInt( 50 ) + 50;
    }
    public int makeR( ) {
      return this.rng.nextInt( 8 ) + 2;
    }

    @Override
    public Circle make( ) {

      return new Circle(
          this.makeX( ),
          this.makeY( ),
          this.makeC( ),
          this.makeA( ),
          this.makeR( ) );

    }

    @Override
    public float eval( ArrayList<Circle> genome ) {

      float f = 0;

      this.canvas.beginDraw( );

      this.canvas.background( 0 );

      for( Circle c : genome ) {

        this.canvas.color( c.color, c.alpha );
        this.canvas.ellipse( c.x, c.y, c.rad, c.rad );

      }

      this.canvas.loadPixels( );
      this.target.loadPixels( );

      for( int ydx = 0; ydx < this.canvas.height; ++ydx ) {
        for( int xdx = 0; xdx < this.canvas.width; ++ xdx ) {

          int pdx = xdx + ydx * this.canvas.width;

          f += 1.0f - Math.abs( ( this.target.pixels[ pdx ] & 0xFF ) - ( this.canvas.pixels[ pdx ] & 0xFF ) ) / 255f;

        }
      }

      this.canvas.endDraw( );

      return f / ( this.canvas.width * this.canvas.height );

    }

    @Override
    public ArrayList<Genome<Circle>> filter( ArrayList<Genome<Circle>> population ) {

      ArrayList<Genome<Circle>> champs = new ArrayList<Genome<Circle>>( );

      for( int cdx = 0; cdx < this.nChamp; ++cdx )
        champs.add( population.get( cdx ) );

      return champs;

    }

    @Override
    public ArrayList<Circle> breed( ArrayList<Circle> a, ArrayList<Circle> b ) {

      ArrayList<Circle> child = new ArrayList<Circle>( );

      for( int gdx = 0; gdx < a.size( ); ++gdx ) {

        if( this.rng.nextFloat( ) < 0.5 )
          child.add( new Circle( a.get( gdx ) ) );
        else
          child.add( new Circle( b.get( gdx ) ) );

      }

      return child;

    }

    @Override
    public void mutate( ArrayList<Circle> genome ) {

      for( Circle c : genome ) {

        if( this.rng.nextFloat( ) < this.mChance ) {

          switch( this.rng.nextInt( 5 ) ) {

            case 0:
              c.x = this.makeX( );
              break;
            case 1:
              c.y = this.makeY( );
              break;
            case 2:
              c.color = this.makeC( );
              break;
            case 3:
              c.alpha = this.makeA( );
              break;
            case 4:
              c.rad = this.makeR( );
              break;

          }

        }

      }

    }

  }


  private PGraphics canvas;
  private static int cWidth = 200;
  private static int cHeigh = 200;

  private PImage tgtImg;

  private CircleGenetics cg;

  private Population<Circle> genetic;

  @Override
  public void settings( ) {

    this.size( 720, 640 );

    this.setName( );

  }

  @Override
  public void setup( ) {


    this.canvas = this.createGraphics( cWidth, cHeigh );

    this.tgtImg = this.loadImage( dataDir + "Darwin.jpg" );
    this.tgtImg.resize( cWidth, cHeigh );
    this.tgtImg.filter( POSTERIZE, 8 );
    this.tgtImg.filter( GRAY );

    this.cg = new CircleGenetics( this, this.tgtImg, 5, 0.03f );

    this.genetic = new Population<Circle>( 16, 1024, this.cg );

    // DEBUG: Show target image
    //    this.canvas.beginDraw( );
    //    this.canvas.image( this.tgtImg, 0, 0, cWidth, cHeigh );
    //    this.canvas.endDraw( );

  }

  @Override
  public void draw( ) {

    // Evaluate all genomes, sort by best fitness
    this.genetic.eval( this.cg );

    // Pick champions and rebreed
    this.genetic.rebreed( this.cg, this.cg, this.cg );

    // Draw the best (Assumed 0-index)
    ArrayList<Circle> champion = this.genetic.get( 0 );
    this.canvas.beginDraw( );
    this.canvas.background( 0 );
    for( Circle c : champion ) {
      this.canvas.fill( c.color, c.alpha );
      this.canvas.noStroke( );
      this.canvas.ellipse( c.x, c.y, c.rad, c.rad );
    }
    this.canvas.endDraw( );

    this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );

  }

  @Override
  public void keyPressed( ) {

    if( this.key == 'w' )
      this.save( this.canvas );

  }

}
