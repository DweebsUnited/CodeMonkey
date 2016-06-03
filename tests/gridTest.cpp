#include "main.h"

#include "grid.h"

// Make it a checkerboard
void mapFunc( uint32_t * cell, uint32_t i, uint32_t j ) {
    (*cell) = ( i + j ) % 2;
}

void gridTest( ) {

    CodeMonkey::DataStructs::IGrid g( 5, 5 );

    g.map( mapFunc );

    g.printOut( );

}