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

        for( uint32_t i = 0; i < geneCount; ++i ) {
            this->genome.push_back( GeneType( ) );
        }

    };

};

template <class GeneType>
class Population {

public:

    std::vector<Genome<GeneType>> members;
    uint32_t memberCount;
    uint32_t numChamps;
    uint32_t geneCount;

    // Callback variables
    uint32_t( *fitnessCallback )( std::vector<GeneType> & ) = NULL;

    // Constructor
    Population(
        uint32_t memberCount,
        uint32_t numChamps,
        uint32_t geneCount ) :

        memberCount( memberCount ),
        numChamps( numChamps ),
        geneCount( geneCount ) {

        for( uint32_t i = 0; i < memberCount; ++i ) {
            this->members.push_back( Genome<GeneType>( geneCount ) );
        }

    };

    void nextGeneration(
        float crossoverChance, // Higher means more swaps between a and b
        float mutationChance, // Higher means more likely to mutate a GENE after breeding
        uint32_t( *fitnessCallback )( std::vector<GeneType> & ),
        bool( *sortCallback )( uint32_t &, uint32_t & ),
        GeneType( *mutateCallback )( GeneType &, uint32_t ) ) {

        if( fitnessCallback == NULL || mutateCallback == NULL )
            return;


        uint32_t sumFitness = 0;
        uint32_t stochasticFitness = 0;
        uint32_t i, j;
        bool crossSide;

        std::vector<uint32_t> fitnessPoints;
        std::vector<Genome<GeneType>> champions;

        std::default_random_engine gen;
        std::uniform_int_distribution<int> * distI;
        std::uniform_real_distribution<float> * distF = new std::uniform_real_distribution<float>( 0.0, 1.0 );


        // Compute fitness of each member
        std::for_each( this->members.begin( ), this->members.end( ), [ &sumFitness, fitnessCallback ]( Genome<GeneType> & member ) { member.fitness = fitnessCallback( member.genome ); sumFitness += member.fitness; } );


        // Sort according to fitness using callback
        // If not available, do nothing
        if( sortCallback != NULL ) {

            std::sort( this->members.begin( ), this->members.end( ), [ sortCallback ]( Genome<GeneType> & a, Genome<GeneType> & b ) { return sortCallback( a.fitness, b.fitness ); } );

        }


        // Use stochastic universal sampling to pick our champions
        distI = new std::uniform_int_distribution<int>( 0, sumFitness / this->numChamps );
        stochasticFitness = (*distI)( gen );

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


        // Breed champions at random until we have a new generation
        delete distI;
        distI = new std::uniform_int_distribution<int>( 0, this->numChamps );

        for( i = 0; i < numChamps; ++i ) {

            this->members[ i ] = champions[ i ];

        }

        for( ; i < this->memberCount; ++i ) {

            std::vector<GeneType> * champA = &( this->members[ (*distI)( gen ) ].genome );
            std::vector<GeneType> * champB = &( this->members[ (*distI)( gen ) ].genome );

            crossSide = (*distF)( gen ) > 0.5;

            for( j = 0; j < this->geneCount; ++j ) {

                if( crossSide ) {
                    this->members[ i ].genome[ j ] = (*champA)[ j ];
                } else {
                    this->members[ i ].genome[ j ] = (*champB)[ j ];
                }

                if( (*distF)( gen ) < mutationChance ) {
                    this->members[ i ].genome[ j ] = mutateCallback( this->members[ i ].genome[ j ], j );
                }

                if( (*distF)( gen ) < crossoverChance ) {
                    crossSide = !crossSide;
                }

            }

        }

    };

    void printOut( ) {
        std::cout << "Population:" << std::endl;
        for( Genome<GeneType> member : this->members ) {
            for( GeneType gene : member.genome ) {
                std::cout << gene << " ";
            }
            std::cout << std::endl;
        }
    }

};

};
};
