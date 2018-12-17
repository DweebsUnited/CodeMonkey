package CodeMonkey.genetic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import CodeMonkey.genetic.breed.Breeder;
import CodeMonkey.genetic.champ.ChampionSelector;
import CodeMonkey.genetic.mutate.Mutator;

public class Population<G extends Gene> {

  private class GenomeComparator implements Comparator<Genome<G>> {

    @Override
    public int compare( Genome<G> arg0, Genome<G> arg1 ) {

      if( arg0.fitness >= arg1.fitness )
        return -1;
      else
        return 1;

    }

  }
  private GenomeComparator gComp = new GenomeComparator( );

  private ArrayList<Genome<G>> population;
  private int size;

  public Population( int size, int genomeSize, GeneFactory<G> factory ) {

    this.size = size;

    this.population = new ArrayList<Genome<G>>( );

    for( int gdx = 0; gdx < size; ++gdx ) {

      this.population.add( new Genome<G>( genomeSize, factory ) );

    }

  }

  public ArrayList<G> get( int mdx ) {

    return this.population.get( mdx ).genome;

  }

  public void eval( Evaluator<G> e ) {

    for( Genome<G> genome : this.population )
      genome.fitness = e.eval( genome.genome );

    this.population.sort( this.gComp );

  }

  public void rebreed( ChampionSelector<G> selection, Breeder<G> breeder, Mutator<G> mutator ) {

    Random rng = new Random( );

    ArrayList<Genome<G>> champs = selection.filter( this.population );
    int nChamps = champs.size( );

    this.population = champs;

    while( this.population.size( ) < this.size ) {

      Genome<G> offspring = new Genome<G>(
          breeder.breed(
              this.population.get( rng.nextInt( nChamps ) ).genome,
              this.population.get( rng.nextInt( nChamps ) ).genome ) );

      mutator.mutate( offspring.genome );

      this.population.add( offspring );

    }

  }

}
