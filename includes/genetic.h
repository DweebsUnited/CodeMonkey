#pragma once
/*********************************************************************************//**
* @file genetic.h
* Genetic algorithm class, designed to be extended with project specific methods
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/

/*************************************************************************************
* Includes
*************************************************************************************/
#include <iostream>
#include <vector>
#include <algorithm>
#include <random>
#include <limits>


/*************************************************************************************
* Namespaces
*************************************************************************************/
namespace CodeMonkey {
namespace Genetic {

/*************************************************************************************
* Utilities
*************************************************************************************/
typedef uint32_t Fitness;


/*************************************************************************************
* Classes
*************************************************************************************/
/*********************************************************************************//**
* Population class with templated gene type
*
* Implements a basic Genetic algorithm, using stochastic random sampling for
*   champion selection. All methods that deal with genes are pure virtual, as
*   those are always project specific.
*************************************************************************************/
template <class GeneType = uint16_t>
class Population {
public:

    typedef GeneType Gene;

    struct Genome {
        Fitness fitness;
        std::vector<GeneType> genome;
    };


    std::vector<Genome> members;
    uint32_t memberCount;
    uint32_t geneCount;

    uint32_t numChamps;

    Fitness sumFitness;

    std::default_random_engine gen;
    std::uniform_real_distribution<float> distF;
    std::uniform_int_distribution<int> distI;

    // Constructor
    Population(
        uint32_t memberCount,
        uint32_t geneCount,
        uint32_t numChamps ) :
        memberCount( memberCount ),
        geneCount( geneCount ),
        numChamps( numChamps ),
        gen( ),
        distF( 0.0, 1.0 ),
        distI( 0, this->numChamps ) { };

    // Don't call this here because newGene is not defined in this class
    // Weird virtual rules that make complete sense if you learn a little more about
    //   how virtuals actually work
    virtual void initMembers( ) {
        Genome g;
        for( uint32_t i = 0; i < this->memberCount; ++i ) {
            this->members.push_back( g );
            for( uint32_t j = 0; j < this->geneCount; ++j )
                this->members[ i ].genome.push_back( this->newGene( ) );
        }
    };

    virtual GeneType newGene( ) = 0;

    virtual void mutateGene( GeneType & gene ) = 0;

    virtual void fitnessCalculate( Genome & genome ) = 0;

    virtual Fitness fitnessCompute( ) {

        std::for_each( this->members.begin( ), this->members.end( ), [ this ]( Genome & genome ) { this->fitnessCalculate( genome ); } );

        std::sort( this->members.begin( ), this->members.end( ), [ ]( Genome & a, Genome & b ) { return a.fitness > b.fitness; } );

        this->sumFitness = 0;
        for( Genome & genome : this->members )
            this->sumFitness += genome.fitness;

        return this->sumFitness;
    };

    virtual std::vector<Genome> pickChampions( ) {
        // Use stochastic universal sampling to pick our champions

        uint32_t i;

        Fitness stochasticFitness = this->distI( this->gen );

        std::vector<uint32_t> fitnessPoints;
        std::vector<Genome> champions;

        for( i = 0; i < this->numChamps; ++i ) {

            fitnessPoints.push_back( this->sumFitness - ( stochasticFitness + i * this->sumFitness / this->numChamps ) );

        }

        i = 0;
        stochasticFitness = this->sumFitness - this->members[ i ].fitness;
        for( uint32_t point : fitnessPoints ) {

            while( stochasticFitness > point ) {

                ++i;
                stochasticFitness -= this->members[ i ].fitness;

            }

            champions.push_back( this->members[ i ] );

        }

        return champions;
    };

    virtual bool doCrossOver( uint32_t geneIdx, uint32_t memberIdx ) {
        return this->distF( this->gen ) < 0.5;
    };

    virtual bool doMutate( uint32_t geneIdx, uint32_t memberIdx ) {
        return this->distF( this->gen ) < 0.1;
    };

    void nextGeneration( ) {
        uint32_t i, j;
        bool crossSide;

        // Calculate fitness values for all genes
        this->fitnessCompute( );

        // Pick our champions for breeding
        std::vector<Genome> champions = this->pickChampions( );


        // Breed champions at random until we have a new generation
        for( i = 0; i < numChamps; ++i )
            this->members[ i ] = champions[ i ];

        for( ; i < this->memberCount; ++i ) {

            Genome * champA = &( this->members[ this->distI( this->gen ) ] );
            Genome * champB = &( this->members[ this->distI( this->gen ) ] );

            crossSide = this->distF( this->gen ) > 0.5;

            for( j = 0; j < this->geneCount; ++j ) {

                // Get genes from appropriate parent
                if( crossSide ) {
                    this->members[ i ].genome[ j ] = champA->genome[ j ];
                } else {
                    this->members[ i ].genome[ j ] = champB->genome[ j ];
                }

                // Check for mutation
                if( this->doMutate( j, i ) )
                    this->mutateGene( this->members[ i ].genome[ j ] );

                // Check for crossover
                if( this->doCrossOver( j, i ) )
                    crossSide = !crossSide;

            }

        }

    };

    void printOut( ) {
        std::cout << "Population:" << std::endl;
        for( Genome member : this->members ) {
            std::cout << member.fitness << ": ";
            for( GeneType gene : member.genome )
                std::cout << (uint32_t) gene << " ";
            std::cout << std::endl;
        }
    };

};

};
};
