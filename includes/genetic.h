#pragma once

#include <iostream>
#include <vector>
#include <algorithm>
#include <random>

namespace CodeMonkey {
namespace Genetic {

// TODO: Parameterize fitness type

template <class GeneType>
class Genome {

public:

    std::vector<GeneType> genome;

    uint32_t fitness;

    Genome( uint32_t geneCount ) {

        this->genome.reserve( geneCount );

    };

};

template <class GeneType>
class Population {

public:

    std::vector<Genome<GeneType>> members;
    uint32_t memberCount;

    uint32_t numChamps;

    // Callback variables
    uint32_t( *fitnessCallback )( std::vector<GeneType> & ) = NULL;
    bool( *sortCallback )( uint32_t &, uint32_t & ) = NULL;
    //Genome<GeneType>( *breedCallback )(  );

    // Constructor
    Population( uint32_t memberCount, uint32_t numChamps, uint32_t geneCount, uint32_t( *fitnessCallback )( std::vector<GeneType> & ) ) :
        memberCount( memberCount ),
        numChamps( numChamps ),
        fitnessCallback( fitnessCallback ) {

        for( uint32_t i = 0; i < memberCount; ++i ) {
            this->members.push_back( Genome<GeneType>( geneCount ) );
        }

    };

    void nextGeneration( ) {

        uint32_t sumFitness = 0;
        uint32_t stochasticFitness = 0;
        uint32_t i;

        std::vector<uint32_t> fitnessPoints;
        std::vector<Genome<GeneType>> champions;

        std::default_random_engine generator;
        std::uniform_int_distribution<int> * distribution;

        // Compute fitness of each member
        std::for_each( this->members.begin( ), this->members.end( ), [ this, &sumFitness ]( Genome<GeneType> & member ) { member.fitness = this->fitnessCallback( member.genome ); sumFitness += member.fitness; } );

        // Sort according to fitness using callback
        // If not available, do nothing
        if( sortCallback != NULL ) {

            std::sort( this->members.begin( ), this->members.end( ), [ this ]( Genome<GeneType> & a, Genome<GeneType> & b ) { return this->sortCallback( a.fitness, b.fitness ); } );

        }

        // Use stochastic universal sampling, and pick random samples to breed population back to full
        distribution = new std::uniform_int_distribution<int>( 0, sumFitness / this->numChamps );
        stochasticFitness = (*distribution)( generator );

        for( i = 0; i < this->numChamps; ++i ) {

            fitnessPoints.push_back( sumFitness - ( stochasticFitness + i * sumFitness / this->numChamps ) );

        }

        i = 0;
        sumFitness -= this->members[ i ].fitness;
        for( uint32_t point : fitnessPoints ) {

            while( sumFitness > point ) {

                ++i;
                sumFitness -= this->members[ i ].fitness;

            }

            champions.push_back( this->members[ i ] );

        }

        // Sanity check that we have enough champions
        // std::cout << champions.size( ) << " " << numChamps << std::endl;

        // Breed champions at random until we are at full capacity
        delete distribution;
        distribution = new std::uniform_int_distribution<int>( 0, this->numChamps );

        for( i = 0; i < this->memberCount; ++i ) {

            this->memberCount[ i ] = this->breedCallback( champions[ distribution( generator ) ], champions[ distribution( generator ) ] );

        }

        // Mutate


    };

};

};
};
