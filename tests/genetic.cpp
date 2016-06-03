#include "main.h"

#include "genetic.h"

using namespace CodeMonkey;

// For the demo, fitness is sum of indices with nonzero genes
// Should optimize for most nonzero vals, preferring higher index genes
uint32_t fitnessFunc( std::vector<uint8_t> & genome ) {

    uint32_t fitness = 0;
    uint32_t index = 0;

    for( uint8_t gene : genome ) {

        if( gene ) {

            fitness += index;
        }

        ++index;

    }

    return fitness;

};

void geneticTest( ) {

    Genetic::Population<uint8_t> pop( 100, 10, 32, fitnessFunc );

    pop.nextGeneration( );

};
