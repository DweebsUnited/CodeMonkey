#include "includes.h"
#include "interface.h"

#include "dSquare.h"

#include <thread>
#include <random>

Plugin dSquare {
    dSquareSetup,
    dSquareTeardown,
    dSquareKeyCallback,
    nullptr
};

// Seeding feature size
static const int featuresize = 512;

static float * image = (float *) malloc( width * height * sizeof( float ) );

static std::thread updater;
static std::atomic<bool> shouldRun( false );

static std::default_random_engine dist;
static std::uniform_real_distribution<float> range( -1.0, 1.0 );

static const float persistance = 0.4;

float sample( int j, int i ) {

    return image[ ( j & ( width - 1 ) ) + ( i & ( height - 1 ) ) * width ];

}

void setSample( int j, int i, float value ) {

    image[ ( j & ( width - 1 ) ) + ( i & ( height - 1 ) ) * width ] = value;

}

void sampleSquare( int j, int i, int size, float value ) {

    int hs = size / 2;

    // a     b
    //
    //    x
    //
    // c     d

    float a = sample( j - hs, i - hs );
    float b = sample( j + hs, i - hs );
    float c = sample( j - hs, i + hs );
    float d = sample( j + hs, i + hs );

    setSample( j, i, ( ( a + b + c + d ) / 4.0 ) + value );

}

void sampleDiamond( int j, int i, int size, float value ) {

    int hs = size / 2;

    //   c
    //
    //a  x  b
    //
    //   d

    double a = sample( j - hs, i );
    double b = sample( j + hs, i );
    double c = sample( j, i - hs );
    double d = sample( j, i + hs );

    setSample( j, i, ( ( a + b + c + d ) / 4.0 ) + value );

}

void DiamondSquare( int stepsize, double scale ) {

    int halfstep = stepsize / 2;

    for( int i = halfstep; i < height + halfstep; i += stepsize )
        for( int j = halfstep; j < width + halfstep; j += stepsize )
            sampleSquare( j, i, stepsize, range( dist ) * scale );

    for( int i = 0; i < height; i += stepsize ) {
        for( int j = 0; j < width; j += stepsize ) {

            sampleDiamond( j + halfstep, i, stepsize, range( dist ) * scale);
            sampleDiamond( j, i + halfstep, stepsize, range( dist ) * scale);

        }
    }

}

void dSquareFun( ) {

    int samplesize = featuresize;

    double scale = 1.0;

    while( samplesize > 1) {

        DiamondSquare( samplesize, scale );

        samplesize /= 2;
        scale *= persistance;

    }

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
            setSample( j, i, range( dist ) );

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
