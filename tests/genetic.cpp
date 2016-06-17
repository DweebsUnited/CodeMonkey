#include "main.h"

#include "genetic.h"

using namespace CodeMonkey;

// For the demo, fitness is sum of indices with nonzero genes
// Should optimize for most nonzero vals, preferring higher index genes

class ProjectPopulation : public Genetic::Population<> {
public:
    ProjectPopulation( uint32_t a, uint32_t b, uint32_t c ) :
        Genetic::Population<>( a, b, c ) {

        this->initMembers( );

    };

    Gene newGene( ) {
        static std::default_random_engine gen;
        static std::uniform_int_distribution<Gene> distI( 0, std::numeric_limits<Gene>::max( ) / 4 );

        return distI( gen );
    };

    void mutateGene( Gene & gene ) {
        gene = this->newGene( );
    };

    void fitnessCalculate( Genome & genome ) {
        genome.fitness = 0;
        for( Gene gene : genome.genome )
            genome.fitness += gene;
    };
};

void geneticTest( ) {

    ProjectPopulation pop( 10, 10, 2 );

    pop.printOut( );

    for( uint32_t i = 0; i < 10; ++i ) {

        pop.nextGeneration( );

        pop.printOut( );

    }

};
