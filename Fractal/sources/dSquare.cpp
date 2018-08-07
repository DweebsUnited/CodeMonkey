#include "includes.h"
#include "interface.h"

#include "dSquare.h"
#include "dSquareUtil.h"

#include <thread>
#include <random>

Plugin dSquare {
    dSquareSetup,
    dSquareTeardown,
    dSquareKeyCallback,
    nullptr,
    nullptr
};

// Seeding feature size
static const int featuresize = 512;

static float * image = (float *) malloc( width * height * sizeof( float ) );

static std::thread updater;
static std::atomic<bool> shouldRun( false );

void dSquareFun( ) {

    DiamondSquare( image, featuresize, 1.0, 0.4 );

    float maxVal = -std::numeric_limits<float>::infinity( );
    float minVal = std::numeric_limits<float>::infinity( );
    for( int i = 0; i < height; ++i ) {
        for( int j = 0; j < width; ++j ) {

            if( image[ j + i * width ] < minVal )
                minVal = image[ j + i * width ];
            if( image[ j + i * width ] > maxVal )
                maxVal = image[ j + i * width ];

        }
    }
    maxVal -= minVal;
    for( int i = 0; i < height; ++i ) {
        for( int j = 0; j < width; ++j ) {

            image[ j + i * width ] = ( image[ j + i * width ] - minVal ) / maxVal;

        }
    }


    updateTexture( image );

}

void dSquareSetup( ) {

    shouldRun = false;
    if( updater.joinable( ) )
        updater.join( );

    for( int i = 0; i < height; i += featuresize )
        for( int j = 0; j < width; j += featuresize )
            image[ j + i * width ] = range( dist );

    shouldRun = true;
    updater = std::thread( dSquareFun );

}

void dSquareTeardown( ) {

    shouldRun = false;
    if( updater.joinable( ) )
        updater.join( );

}

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
void dSquareKeyCallback( GLFWwindow* window, int key, int scancode, int action, int mods ) {

    // If the Esc key, close the window
    if( action == GLFW_PRESS ) {

        if( key == GLFW_KEY_ESCAPE )
            glfwSetWindowShouldClose( window, GL_TRUE );

        else if( key == GLFW_KEY_SPACE )
            dSquareSetup( );

    }

}
#pragma clang diagnostic pop
