#include "includes.h"
#include "interface.h"

#include "peterDeJong.h"

#include <thread>

Plugin peterDeJong {
    peterDeJongSetup,
    peterDeJongTeardown,
    peterDeJongKeyCallback,
    peterDeJongMouseCallback
};

static float a = 1.641;
static float b = 1.902;
static float c = 0.316;
static float d = 1.525;

static float x = 0.0;
static float y = 0.0;

static float * image = (float *) malloc( width * height * sizeof( float ) );

static std::thread updater;
static std::atomic<bool> shouldRun( false );

void peterDeJongUpdateFunc( ) {

    while( shouldRun ) {

        for( uint64_t idx = 0; idx < 10000ul; ++idx ) {

            float nx = std::sin( a * y ) - std::cos( b * x );
            float ny = std::sin( c * x ) - std::cos( d * y );

            int i = ( ny / 4 + 0.5 ) * height;
            int j = ( nx / 4 + 0.5 ) * width;

            if( image[ i * width + j ] < 1.0 )
                image[ i * width + j ] += 0.005;

            x = nx;
            y = ny;

        }

        updateTexture( image );

    }

}

void peterDeJongSetup( ) {

    shouldRun = false;
    if( updater.joinable( ) )
        updater.join( );

    for( int i = 0; i < height * width; ++i )
        image[ i ] = 0.0;

    shouldRun = true;
    updater = std::thread( peterDeJongUpdateFunc );

}

void peterDeJongTeardown( ) {

    shouldRun = false;
    if( updater.joinable( ) )
        updater.join( );

}

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
void peterDeJongKeyCallback( GLFWwindow* window, int key, int scancode, int action, int mods ) {

    // If the Esc key, close the window
    if( action == GLFW_PRESS ) {

        if( key == GLFW_KEY_ESCAPE )

            glfwSetWindowShouldClose( window, GL_TRUE );

    }

}
#pragma clang diagnostic pop

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
void peterDeJongMouseCallback( GLFWwindow* window, int button, int action, int mods ) {

    double mx, my;
    glfwGetCursorPos( window, &mx, &my );

    x = mx * 2 / width - 1;
    y = my * 2 / height - 1;

    peterDeJongSetup( );

}
#pragma clang diagnostic pop
