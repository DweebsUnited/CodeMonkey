package CodeMonkey.genetic.champ;

import java.util.ArrayList;

import CodeMonkey.genetic.Gene;
import CodeMonkey.genetic.Genome;

public interface ChampionSelector<G extends Gene> {

  public ArrayList<Genome<G>> filter( ArrayList<Genome<G>> population );

}
