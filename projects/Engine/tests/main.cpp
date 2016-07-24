/*************************************************************************************
* main.cpp
* Draw a simple 3d simplex, let it move around
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/

#include "main.h"

/*
    A   >   B
      \   /
    v       v

    C   <   D
*/

#define NUMNODES 1005

std::mt19937 gen;
std::uniform_real_distribution<> dist( 0.0, 1.0 );

std::vector<SimpleEngine::Shapes::Tetracosagon *> nodes;

SimpleEngine::Camera::Camera cam;

SimpleEngine::Engine e( 1280, 800, "Simplex v1.0" );

int fcount = 0;

bool logicCallback( float dX, float dY, float dT, void * eng ) {
    return true;
}

int main( ) {

    e.startGraphics( );

    std::cout << "Graphics started!" << std::endl;

    for( int i = 0; i < NUMNODES - 5; ++i ) {
        nodes.push_back( new SimpleEngine::Shapes::Tetracosagon( ) );
        nodes[ i ]->prepare( );
        nodes[ i ]->setScale( 0.1, 0.1, 0.1 );
        nodes[ i ]->setLocation( dist( gen ), dist( gen ), dist( gen ) );
        e.addShape( nodes[ i ] );
    }

    e.runEngine( logicCallback, &cam );

    //std::cerr << "Graphics stop [ OK ]" << std::endl;

    return 0;

}
