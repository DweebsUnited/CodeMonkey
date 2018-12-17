package CodeMonkey.genetic.breed;

import java.util.ArrayList;

import CodeMonkey.genetic.Gene;

public interface Breeder<G extends Gene> {

  public ArrayList<G> breed( ArrayList<G> a, ArrayList<G> b );

}
