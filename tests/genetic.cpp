#include "main.h"

#include "genetic.h"

using namespace CodeMonkey;

// For the demo, fitness is sum of indices with nonzero genes
// Should optimize for most nonzero vals, preferring higher index genes

void geneticTest( ) {

    Genetic::Population<uint32_t> pop( 10, 10, 2 );
    pop.initMembers( );

    pop.printOut( );

    for( uint32_t i = 0; i < 10; ++i ) {

        pop.nextGeneration( );

        pop.printOut( );

    }

};
