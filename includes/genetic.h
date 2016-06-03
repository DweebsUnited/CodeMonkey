#pragma once

#include <iostream>
#include <vector>

namespace CodeMonkey {
namespace Genetic {

// TODO: Parameterize fitness type

template <class GeneType>
class Genome {

    std::vector<GeneType> genome;

    uint32_t fitness;

    Genome( uint32_t geneCount ) {
        
        this->genome.reserve( geneCount );

    };

};

template <class GeneType>
class Population {

    std::vector<Genome<GeneType>> members;

    // Callback variables
    uint32_t( *fitnessCallback )( std::vector<GeneType> &, uint32_t );
    bool( *sortCallback )( uint32_t, uint32_t ) = { ;

    Population( uint32_t memberCount, uint32_t( *fitnessCallback )( std::vector<GeneType> &, uint32_t ) ) : fitnessCallback( fitnessCallback ) {

        this->members.reserve( memberCount );

    };

    // Default callback implementations?

    void breed( ) {

        // Compute fitness of each member
        member.fitness = this->fitnessCallback( member.genome )

        // Sort according to fitness using callback
        // If not available, do nothing

        // Use stochastic universal sampling, and pick random samples to breed population back to full

        // Mutate at random percentage

    };

};

};
};