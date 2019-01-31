package CodeMonkey.genetic.mutate;

import java.util.ArrayList;

import CodeMonkey.genetic.Gene;

public interface Mutator<G extends Gene> {

  public void mutate( ArrayList<G> genome, int nGenChamp );

}
