package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.genetic.Evaluator;
import CodeMonkey.genetic.Gene;
import CodeMonkey.genetic.GeneFactory;
import CodeMonkey.genetic.Genome;
import CodeMonkey.genetic.Population;
import CodeMonkey.genetic.breed.Breeder;
import CodeMonkey.genetic.champ.BestN;
import CodeMonkey.genetic.champ.ChampionSelector;
import CodeMonkey.genetic.mutate.Mutator;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class PluggablePluto extends ProjectBase {

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.PluggablePluto" );

  }

  private static class Circle implements Gene {

    public float x, y;
    public float color;
    public float alpha;
    public float rad;

    public Circle( float x, float y, float color, float alpha, float rad ) {

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

    private ChampionSelector<Circle> champSelect;

    public CircleGenetics( PApplet context, PImage target, int nChamp, float mChance ) {

      this.canvas = context.createGraphics( target.width, target.height );
      this.target = target;
      this.nChamp = nChamp;
      this.mChance = mChance;

      this.champSelect = new BestN<Circle>( nChamp );

    }

    public float makeX( ) {
      return this.rng.nextInt( this.target.width );
    }
    public float makeY( ) {
      return this.rng.nextInt( this.target.height );
    }
    public float makeC( ) {
      return this.rng.nextInt( 255 );
    }
    public float makeA( ) {
      return this.rng.nextInt( 50 ) + 50;
    }
    public float makeR( ) {
      return this.rng.nextInt( 8 ) + 2;
    }

    public float mutateX( float x ) {
      // Hard: New random coord
      if( this.rng.nextFloat( ) < 0.25f )
        return this.makeX( );
      // Medium: Move gaussian like
      x += 25f * (float)this.rng.nextGaussian( );
      if( x <= 0 )
        x = 0;
      if( x >= this.target.width - 1 )
        x = this.target.width - 1;
      return x;
    }
    public float mutateY( float y ) {
      // Hard: New random coord
      if( this.rng.nextFloat( ) < 0.25f )
        return this.makeY( );
      // Medium: Move gaussian like
      y += 25f * (float)this.rng.nextGaussian( );
      if( y < 0 )
        y = 0;
      if( y >= this.target.height - 1 )
        y = this.target.height - 1;
      return y;
    }
    public float mutateC( float c ) {
      // Hard: New random
      if( this.rng.nextFloat( ) < 0.25f )
        return this.makeC( );
      // Medium: Move yo gaussian hips baby
      c += 2f * (float)this.rng.nextGaussian( );
      if( c < 0 )
        c = 0;
      if( c > 255 )
        c = 255;
      return c;

    }
    public float mutateA( float a ) {
      return this.makeA( );
    }
    public float mutateR( float r ) {
      return this.makeR( );
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
      return this.champSelect.filter( population );
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

        if( this.rng.nextFloat( ) < this.mChance )
          c.x = this.mutateX( c.x );

        if( this.rng.nextFloat( ) < this.mChance )
          c.y = this.mutateY( c.y );

        if( this.rng.nextFloat( ) < this.mChance )
          c.color = this.mutateC( c.color );

        if( this.rng.nextFloat( ) < this.mChance )
          c.alpha = this.mutateA( c.alpha );

        if( this.rng.nextFloat( ) < this.mChance )
          c.rad = this.mutateR( c.rad );

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
    this.tgtImg.filter( GRAY );
    //    this.tgtImg.filter( POSTERIZE, 16 );

    this.cg = new CircleGenetics( this, this.tgtImg, 16, 0.03f );

    this.genetic = new Population<Circle>( 128, 2048, this.cg );

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

    System.out.println( String.format( "Gen: %d => %f", this.frameCount, this.genetic.bestFitness( ) ) );

    // DEBUG: Show target image
    //    this.canvas.beginDraw( );
    //    this.canvas.image( this.tgtImg, 0, 0, cWidth, cHeigh );
    //    this.canvas.endDraw( );
    //
    //    this.image( this.canvas, 0, 0, this.pixelWidth, this.pixelHeight );
    //
    //    this.noLoop( );

  }

  @Override
  public void keyPressed( ) {

    if( this.key == 'w' )
      this.save( this.canvas );

  }

}
