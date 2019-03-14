package CodeMonkey.genetic.champ;

import java.util.ArrayList;

import CodeMonkey.genetic.Gene;
import CodeMonkey.genetic.Genome;


public class BestN< G extends Gene > implements ChampionSelector< G > {

	int nChamps;

	public BestN( int nChamps ) {

		this.nChamps = nChamps;

	}

	@Override
	public ArrayList< Genome< G > > filter( ArrayList< Genome< G > > population ) {

		// Simply remove all but top n
		ArrayList< Genome< G > > champs = new ArrayList< Genome< G > >( );

		for( int cdx = 0; cdx < this.nChamps; ++cdx )
			champs.add( population.get( cdx ) );

		return champs;

	}

}
