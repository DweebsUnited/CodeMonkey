#include "main.h"

#include "genetic.h"

using namespace CodeMonkey;

// For the demo, fitness is sum of indices with nonzero genes
// Should optimize for most nonzero vals, preferring higher index genes
uint32_t fitnessFunc( std::vector<uint32_t> & genome ) {

    uint32_t fitness = 0;
    uint32_t index = 0;

    for( uint32_t gene : genome ) {

        if( gene ) {

            fitness += index;
        }

        ++index;

    }

    return fitness;

};

bool sortFunc( uint32_t & a, uint32_t & b ) {
    return a > b;
}

uint32_t mutateFunc( uint32_t & gene, uint32_t index ) {
    return !gene;
}

void geneticTest( ) {

    Genetic::Population<uint32_t> pop( 10, 2, 10 );

    pop.printOut( );

    for( uint32_t i = 0; i < 10; ++i ) {

        pop.nextGeneration(
            0.5,
            0.9,
            fitnessFunc,
            sortFunc,
            mutateFunc );

        pop.printOut( );

    }

};
