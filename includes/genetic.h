#pragma once

#include <iostream>

namespace CodeMonkey {
namespace Genetic {

template <class GeneType>
class Genome {

    GeneType * genome;
    uint32_t geneCount;

    Genome( uint32_t geneCount ) : geneCount( geneCount ) {
        
        this->genome = malloc( sizeof( GeneType ) * geneCount );

        if( this->genome == NULL ) {

            std::cerr << "Failed to allocate room for a genome." << std::endl;
            exit( 1 );

        }

    };

};

template <class GeneType>
class Population {

    Genome<GeneType> * members;
    uint32_t memberCount;

    Genome( uint32_t memberCount ) : memberCount( memberCount ) {

        this->members = malloc( sizeof( Genome<GeneType> ) * memberCount );

        if( this->genome == NULL ) {

            std::cerr << "Failed to allocate room for a population." << std::endl;
            exit( 1 );

        }

    };

};

};
};