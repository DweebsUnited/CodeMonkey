package CodeMonkey.genetic;

import java.util.ArrayList;

public class Genome<G extends Gene> {

  public ArrayList<G> genome;

  public float fitness = 0;

  public Genome( int size, GeneFactory<G> factory ) {

    this.genome = new ArrayList<G>( );

    for( int gdx = 0; gdx < size; ++gdx ) {

      this.genome.add( factory.make( ) );

    }

  }

  public Genome( ArrayList<G> genome ) {

    this.genome = genome;

  }

}
