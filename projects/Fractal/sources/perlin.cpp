#include "includes.h"
#include "interface.h"

#include "perlin.h"

#include <thread>
#include <random>

Plugin perlin {
    perlinSetup,
    perlinTeardown,
    perlinKeyCallback,
    perlinMouseCallback,
    nullptr
};

static float * image = (float *) malloc( width * height * sizeof( float ) );

static std::thread updater;
static std::atomic<bool> shouldRun( false );

static std::default_random_engine dist;
static std::uniform_real_distribution<float> range( -1.0, 1.0 );

void perlinFun( ) {

    // TODO

    updateTexture( image );

}

void perlinSetup( ) {

    shouldRun = false;
    if( updater.joinable( ) )
        updater.join( );

    for( int i = 0; i < height * width; ++i )
        image[ i ] = 0.0;

    shouldRun = true;
    updater = std::thread( perlinFun );

}

void perlinTeardown( ) {

    shouldRun = false;
    if( updater.joinable( ) )
        updater.join( );

}

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
void perlinKeyCallback( GLFWwindow* window, int key, int scancode, int action, int mods ) {

    // If the Esc key, close the window
    if( action == GLFW_PRESS ) {

        if( key == GLFW_KEY_ESCAPE )
            glfwSetWindowShouldClose( window, GL_TRUE );

        else if( key == GLFW_KEY_SPACE )
            perlinSetup( );

    }

}
#pragma clang diagnostic pop

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
void perlinMouseCallback( GLFWwindow* window, int button, int action, int mods ) {

}
#pragma clang diagnostic pop
