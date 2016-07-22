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

    /** Give inherited classes a nudge to keep the terminology straight */
    typedef GeneType Gene;

    /*****************************************************************************//**
    * A genome is just a list of genes, but we save its fitness with it to save
    *   some cycles later on
    *
    * This is all a real genome is. Cool.
    *********************************************************************************/
    struct Genome {
        Fitness fitness;
        std::vector<GeneType> genome;
    };

    /** Yes, a population does contain members */
    std::vector<Genome> members;
    /** Saving this instead of reserving saves some function calls to size( ) later on */
    uint32_t memberCount;
    /** This we do need to save though      */
    uint32_t geneCount;

    /** How many of each generation survive */
    uint32_t numChamps;

    /** The sum of the current generation's fitness */
    Fitness sumFitness;

    /** TODO This should be private         */
    std::default_random_engine gen;
    /** TODO This should be private         */
    std::uniform_real_distribution<float> distF;
    /** TODO This should be private         */
    std::uniform_int_distribution<int> distI;

    /*****************************************************************************//**
    * Constructor doesn't do anything, but taught me a lot about virtual functions
    *   and function hiding in C++
    *
    * @param    [in]    memberCount     Number of members in population
    * @param    [in]    geneCount       Number of genes in a genome
    * @param    [in]    numChamps       Number of champions to keep each generation
    *********************************************************************************/
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

    /*****************************************************************************//**
    * Initialize each member and it's genome, using the virtual newGene( )
    *
    * Don't call this in the constructor because newGene is not defined in this
    *   class. Weird virtual rules that make complete sense if you learn more
    *   about how virtuals actually work.
    *********************************************************************************/
    virtual void initMembers( ) {
        // Make a default genome
        Genome g;

        // For each member
        for( uint32_t i = 0; i < this->memberCount; ++i ) {

            // Push back the genome
            this->members.push_back( g );

            // With enough genes to make a genome
            for( uint32_t j = 0; j < this->geneCount; ++j )

                // Hey! Make a genome
                this->members[ i ].genome.push_back( this->newGene( ) );

        }

    };

    /*****************************************************************************//**
    * This builds a new gene to be put in a genome
    *
    * Usually the result of breeding. Just like biology. Cool.
    *
    * @param    [out]   GeneType        Number of members in population
    *********************************************************************************/
    virtual GeneType newGene( ) = 0;

    /*****************************************************************************//**
    * Do filthy nasty things to that gene
    *
    * Radiation damage?
    *
    * @param    [out]   GeneType        Number of members in population
    *********************************************************************************/
    virtual void mutateGene( GeneType & gene ) = 0;

    /*****************************************************************************//**
    * We must test you, and then, break you.
    *
    * @param    [out]   GeneType        Number of members in population
    *********************************************************************************/
    virtual void fitnessCalculate( Genome & genome ) = 0;

    /*****************************************************************************//**
    * Test their mental and physical might
    *
    * @param    [out]   Fitness         How well they all scored on the hero test
    *********************************************************************************/
    virtual Fitness fitnessCompute( ) {

        // I love functional programming
        std::for_each( this->members.begin( ), this->members.end( ), [ this ]( Genome & genome ) { this->fitnessCalculate( genome ); } );

        // Use member var as accumulator
        this->sumFitness = 0;

        // Functional reduce idiom
        for( Genome & genome : this->members )
            this->sumFitness += genome.fitness;

        // And return the saved result
        return this->sumFitness;

    };

    /*****************************************************************************//**
    * Pick the finest among them
    *
    * SIDE EFFECTS: Sorts members HtoL
    *
    * @param    [out]   std::vector<Genome>     Champions of this generation
    *********************************************************************************/
    virtual std::vector<Genome> pickChampions( ) {

        // Use stochastic universal sampling to pick our champions

        // Counter
        uint32_t i;
        // Accumulator
        Fitness accum;

        // Ranged random distribution
        std::uniform_int_distribution<int> dist( 0, this->sumFitness / this->numChamps );
        Fitness stochasticFitness = dist( this->gen );

        // We construct this list first, not inline with the next
        std::vector<uint32_t> fitnessPoints;
        // Champions selected
        std::vector<Genome> champions;

        // This sets us up for stochastic sampling
        std::sort( this->members.begin( ), this->members.end( ), [ ]( Genome & a, Genome & b ) { return a.fitness > b.fitness; } );

        // Set up counter
        i = 0;

        // Set up accumulator
        accum = this->sumFitness;

        // Do while
        do {

            // Each point is a constant size from the previous
            do {

                // Pass by another member
                ++i;
                accum -= this->members[ i ].fitness;

            // While we have not passed a sample point
            } while( accum > stochasticFitness + i * this->sumFitness / this->numChamps );

            // If we make it here, point is inside current champion
            champions.push_back( this->members[ i ] );

        // i guaranteed to not exceed numChamps
        } while( i < this->numChamps );

        // This was what it all built up to
        return champions;

    };

    /*****************************************************************************//**
    * Whether or not this gene in this member should be a crossover point
    *
    * @param    [out]   bool            Whether we should cross to the opposite parent
    *********************************************************************************/
    virtual bool doCrossOver( uint32_t geneIdx, uint32_t memberIdx ) {

        // It's as simple as this
        return this->distF( this->gen ) < 0.5;

    };

    /*****************************************************************************//**
    * Whether this gene should mutate
    *
    * Takes these parems so later members can have a higher mutation rate. This
    *   introduces some extreme randomness to hopefully not hit a local minima.
    *
    * @param    [out]   bool            Whether we should mutate this gene
    *********************************************************************************/
    virtual bool doMutate( uint32_t geneIdx, uint32_t memberIdx ) {

        // In this example though, it's just a flat check
        return this->distF( this->gen ) < 0.1;

    };

    /*****************************************************************************//**
    * The meat, compute the next generation of this population
    *
    * All the virtual functions above mean this will use the most derived version
    *   of each method. Making it totally extensible.
    *********************************************************************************/
    void nextGeneration( ) {

        // Counters
        uint32_t i, j;

        // Which parent we are taking genes from
        bool crossSide;

        // Calculate fitness values for all genes
        this->fitnessCompute( );

        // Pick our champions for breeding
        std::vector<Genome> champions = this->pickChampions( );


        // Breed champions at random until we have a new generation

        // Copy champions into population
        for( i = 0; i < numChamps; ++i )
            this->members[ i ] = champions[ i ];

        // Overwrite the rest of the old members in the population
        for( ; i < this->memberCount; ++i ) {

            // Pick two champions as parents
            Genome * champA = &( this->members[ this->distI( this->gen ) ] );
            Genome * champB = &( this->members[ this->distI( this->gen ) ] );

            // Pick a parent to start with
            crossSide = this->distF( this->gen ) > 0.5;

            // Begin copying genes
            for( j = 0; j < this->geneCount; ++j ) {

                // Pull gene from appropriate parent
                if( crossSide ) {
                    this->members[ i ].genome[ j ] = champA->genome[ j ];
                } else {
                    this->members[ i ].genome[ j ] = champB->genome[ j ];
                }

                // Check for mutation
                if( this->doMutate( j, i ) )
                    // Mutate if need be
                    this->mutateGene( this->members[ i ].genome[ j ] );

                // Check for crossover
                if( this->doCrossOver( j, i ) )
                    // Start pulling genes from the other parent
                    crossSide = !crossSide;

            }

        }

    };

    /*****************************************************************************//**
    * Print out a generation, making a very unsafe typecast
    *********************************************************************************/
    void printOut( ) {

        // Header
        std::cout << "Population:" << std::endl;

        // Print all the members
        for( Genome member : this->members ) {

            // Fitness first
            std::cout << member.fitness << ": ";

            // Then each gene as a uint32
            for( GeneType gene : member.genome )

                std::cout << (uint32_t) gene << " ";

            // And some closing newlines
            std::cout << std::endl << std::endl;

        }

    };

};

};
};
