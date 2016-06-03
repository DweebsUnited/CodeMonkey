#pragma once
/*********************************************************************************//**
* @file grid.h
* Useful for when you want a 2d array, but don't want to deal with memory management
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/

/*************************************************************************************
* Includes
*************************************************************************************/
#include <iostream>
#include <cstdint>


/*************************************************************************************
* Namespaces
*************************************************************************************/
namespace CodeMonkey {
namespace DataStructs {


/*************************************************************************************
* Classes
*************************************************************************************/
/*********************************************************************************//**
* Grid class with templated cells
*
* Provides basic memory management of a 2d array, mostly because I got tired of
*   rewriting the boilerplate code. This way I can also add some more complex
*   methods, since they will persist for all time. Implements the grid as a 1d
*   array of cells, and handles all the indexing therein.
*
* WARNING: If you use a class as the CellType, you must remember to init them,
*   and deinit when you are done. Use map to map an initializer over all cells.
*   Also do keep in mind if you are using pointers in anything tehy will only be
*   shallow copied. Use smart pointers instead if you need them.
*************************************************************************************/
template <class CellType>
class Grid {

    /** Helper typedef to keep internal formatting of classes consistent */
    typedef CellType _Cell;

public:
    /** Array of cells in the grid          */
    _Cell * cells;

    /** Number of rows in grid              */
    uint32_t nRows;
    /** Number of columns in grid           */
    uint32_t nCols;


    Grid( uint32_t nRows, uint32_t nCols ) : nRows( nRows ), nCols( nCols ) {
        this->cells = new _Cell[ nRows * nCols ];
    };

    ~Grid( ) { delete this->cells; };

    _Cell * operator[]( uint32_t i ) { return this->cells + i * this->nCols; };
    _Cell * operator()( ) { return this->cells; };

    void map( void( *mapFunc )( _Cell * cell, uint32_t i, uint32_t j ) ) {

        for( uint32_t i = 0; i < this->nRows; ++i )
            for( uint32_t j = 0; j < this->nCols; ++j )
                mapFunc( (*this)[ i ] + j, i, j );

    };

    // TODO: map on region of interest only

    void printOut( ) {

        for( uint32_t i = 0; i < this->nRows; ++i ) {

            for( uint32_t j = 0; j < this->nCols; ++j )
                std::cout << ( *this )[ i ][ j ] << "    ";

            std::cout << std::endl;

        }

    }

};

typedef Grid<uint32_t> IGrid;
typedef uint32_t ICell;

typedef Grid<bool> BoolGrid;
typedef bool BoolCell;

};
};
