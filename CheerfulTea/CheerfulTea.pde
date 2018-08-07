import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

Random rng = new Random( );


class Line {
  
  public float sx, sy, ex, ey;
  
  private float LINE_LEN = 0.05;
  
  public Line( Random rng ) {
    
    this.sx = rng.nextFloat( );
    this.sy = rng.nextFloat( );
    float ang = rng.nextFloat( ) * 2.0 * PI;
    
    this.ex = sx + this.LINE_LEN * cos( ang );
    this.ey = sy + this.LINE_LEN * sin( ang );
    
  }
  
  public void draw( PGraphics canvas ) {
    
    canvas.noFill( );
    canvas.stroke( 255 );
    
    canvas.line( this.sx * canvas.width, this.sy * canvas.height, this.ex * canvas.width, this.ey * canvas.height );
    
  }
  
}


class Genome {
  
    private List<Line> genome;
    
    private final int NUM_GENES = 64;
    private final float MUTA_CHANCE = 0.5;
    
    public int fitness;
    public boolean evaled;
    
    public Genome( Random rng ) {
      
      this.genome = new ArrayList<Line>( );
      this.fitness = 0;
      this.evaled = false;
      
      for( int gdx = 0; gdx < this.NUM_GENES; gdx++ )
        this.genome.add( new Line( rng ) );
      
    }
    
    public Genome( Random rng, Genome a, Genome b ) {
      
      this.genome = new ArrayList<Line>( );
      this.fitness = 0;
      this.evaled = false;
      
      for( int gdx = 0; gdx < this.NUM_GENES; ++gdx ) {
        
        if( rng.nextFloat( ) < this.MUTA_CHANCE )
          this.genome.add( new Line( rng ) );
        else {
          if( rng.nextFloat( ) < 0.5 )
            this.genome.add( a.get( gdx ) );
          else
            this.genome.add( b.get( gdx ) );
        }
        
      }
      
    }
    
    public void draw( PGraphics canvas ) {
      
      for( Line l : this.genome )
        l.draw( canvas );
      
    }
    
    public Line get( int gdx ) {
      return this.genome.get( gdx ); 
    }
  
}

class GenomeSorter implements Comparator<Genome> {
  
  public int compare( Genome a, Genome b ) {
    
    if( !a.evaled && !b.evaled )
      return 0;
    else if( a.evaled && !b.evaled )
      return 1;
    else if( !a.evaled && b.evaled )
      return -1;
    else
      return round( a.fitness - b.fitness );
      
  }
  
}


class Breeder {
  
  private List<Genome> population;
  
  private final int NUM_GENOMES = 64;
  
  private PImage target;
  private PGraphics champion;
  private PGraphics canvas;
  
  public Breeder( Random rng, PImage target ) {
    
    this.population = new ArrayList<Genome>( );
    this.target = target;
    this.champion = createGraphics( target.width, target.height );
    canvas = createGraphics( target.width, target.height );
    
    for( int gdx = 0; gdx < this.NUM_GENOMES; gdx++ )
      this.population.add( new Genome( rng ) );
    
  }
  
  public void draw( ) {
    
    image( this.champion, 0, 0, pixelWidth, pixelHeight );
    
  }
  
  public void nextGeneration( Random rng ) {
    
    // Evaluation
    this.evalPopulation( );
    
    // Selection
    // TODO: Stochasic Uniform
    this.sortPopulation( );
    this.population.subList( 8, this.NUM_GENOMES ).clear( );
    
    // Breeding
    this.breedPopulation( rng );
    
    // Sorting
    this.sortPopulation( );
    
    // Draw best
    this.drawChampion( );
    
  }
  
  private void evalPopulation( ) {

    for( int gdx = 0; gdx < this.NUM_GENOMES; gdx++ ) {
      
      Genome g = this.population.get( gdx );
      
      // Old champions don't need to be reevaluated
      if( g.evaled )
        continue;
      
      this.canvas.beginDraw( );
      this.canvas.background( 0 );
      g.draw( this.canvas );
      this.canvas.endDraw( );
      
      this.target.loadPixels( );
      this.canvas.loadPixels( );
      
      g.fitness = 0;
      for( int pdx = 0; pdx < this.canvas.width * this.canvas.height; ++pdx )
        // Forward support for feathered lines ^_^
        g.fitness += 255 - abs( ( ( this.canvas.pixels[ pdx ] & 0xFF ) - ( this.target.pixels[ pdx ] & 0xFF ) ) );
        
      g.evaled = true;
      
    }
    
  }
  
  private void sortPopulation( ) {
    this.population.sort( new GenomeSorter( ) );
  }
  
  private void breedPopulation( Random rng ) {
    
    int numChamps = this.population.size( );
    
    while( this.population.size( ) < this.NUM_GENOMES )
      this.population.add( new Genome( rng, this.population.get( rng.nextInt( numChamps ) ), this.population.get( rng.nextInt( numChamps ) ) ) );
    
  }
  
  private void drawChampion( ) {
    
    this.champion.beginDraw( );
    this.champion.background( 0 );
    this.population.get( 0 ).draw( this.champion );
    this.champion.endDraw( );
    
  }
  
}


PImage target;
Breeder b;
CannyEdgeDetector canny = new CannyEdgeDetector( );

void setup( ) {
  
  size( 720, 405 );
  target = loadImage( "target.png" );
  canny.setSourceImage( target );
  target = new PImage( target.width, target.height );
  canny.setEdgesImage( target );
  canny.process( );
  
  b = new Breeder( rng, target );
  b.nextGeneration( rng );
  
}

void draw( ) {
  
  b.draw( );
  
}

void keyPressed( ) {
  
  if( key == ' ' )
    b.nextGeneration( rng );
  else if( key == 'q' ) {
    
    for( int gen = 0; gen < 8; ++gen )
      b.nextGeneration( rng );
    
  }
  
}