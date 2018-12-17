package CodeMonkey.genetic;

import java.util.ArrayList;

public interface Evaluator<G extends Gene> {

  public float eval( ArrayList<G> genome );

}
