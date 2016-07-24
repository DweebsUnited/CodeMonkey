/*************************************************************************************
* main.h
* All tests should add their main test method here
*
* Copyright (C) 2014 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/

#include "engine.h"

#include <string>
#include <sstream>
#include <iomanip>
#include <random>
#include <iostream>

class DynamicTetracosagon {
public:
    SimpleEngine::Shapes::Tetracosagon * t = NULL;

    glm::vec3 transOld = glm::vec3( 0, 0, 0 );

    DynamicTetracosagon( ) { };

    ~DynamicTetracosagon( ) { if( this->t ) delete( t ); }

    glm::vec3 getPos( ) { return this->t->translation; };
    void setPos( glm::vec3 pos ) { this->t->translation = pos; };

    void setScale( float x, float y, float z ) { this->t->setScale( x, y, z ); };

    SimpleEngine::Shapes::Tetracosagon * getShape( ) { return this->t; };

    bool prepare( std::mt19937 & gen, std::uniform_real_distribution<> & dist ) {
        if( this->t )
            delete( t );

        this->t = new SimpleEngine::Shapes::Tetracosagon( );
        this->t->prepare( );

        this->setPos( glm::vec3( glm::rotate( glm::angleAxis( glm::radians( (float)dist( gen ) * 360.0f ), glm::vec3( 0.0, 1.0, 0.0 ) ), glm::rotate( glm::angleAxis( glm::radians( (float)dist( gen ) * 22.5f ), glm::vec3( 1.0, 0.0, 0.0 ) ), glm::vec4( 0.0, 1.0, 0.0, 0.0 ) ) ) ) * (float)dist( gen ) * 2.0f * 1.0f/60.0f );
        return true;
    };

    void update( float dT ) {
        // Vertlet update method
        // I actually really like this
        glm::vec3 temp = 2.0f * this->getPos( ) - this->transOld + glm::vec3( 0.0, -1.0, 0.0 ) * dT * dT;
        this->transOld = this->getPos( );
        this->setPos( temp );
    };

    std::string getPosStr( ) {
        std::stringstream ret;
        ret << std::fixed << std::setprecision( 3 ) << this->t->translation.x << ", " << this->t->translation.y << ", " << this->t->translation.z;
        return ret.str( );
    };

};
