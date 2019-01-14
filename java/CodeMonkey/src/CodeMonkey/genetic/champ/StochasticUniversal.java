package CodeMonkey.genetic.champ;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.genetic.Gene;
import CodeMonkey.genetic.Genome;

public class StochasticUniversal<G extends Gene> implements ChampionSelector<G> {

  private Random rng = new Random( );

  private int nChamps;

  public StochasticUniversal( int nChamps ) {

    this.nChamps = nChamps;

  }

  @Override
  public ArrayList<Genome<G>> filter( ArrayList<Genome<G>> population ) {

    float F = 0;

    for( Genome<G> genome : population )
      F += genome.fitness;

    float FN = F / this.nChamps;

    ArrayList<Genome<G>> champs = new ArrayList<Genome<G>>( );

    float r = this.rng.nextFloat( ) * FN;
    F = FN;

    for( Genome<G> genome : population ) {

      r += genome.fitness;

      if( r > F ) {

        champs.add( genome );

        if( champs.size( ) == this.nChamps )
          break;

        F = FN * ( champs.size( ) + 1 );

      }

    }

    return champs;

  }

}
