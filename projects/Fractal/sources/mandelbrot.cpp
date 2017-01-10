#include "includes.h"
#include "interface.h"

#include "mandelbrot.h"

#include <thread>

Plugin mandelbrot {
    mandelbrotSetup,
    mandelbrotTeardown,
    mandelbrotKeyCallback,
    mandelbrotMouseCallback,
    nullptr
};

// Mandelbrot globals
static double x = -0.25;
static double y = 0.0;
static double scale = 1.0;

static const uint16_t iterLimit = 254;
static const float magLimit = 4.0;

static float * image = (float *) malloc( width * height * sizeof( float ) );

static std::thread updater;
static std::atomic<bool> shouldRun( false );

void mandelbrotFun( ) {

    double yMin = y - scale;
    double yMax = y + scale;
    double xMin = x - scale;
    double xMax = x + scale;

    double pixH = ( yMax - yMin ) / height;
    double pixW = ( xMax - xMin ) / width;

    // bitmap_image img( width, height );
    // if( !img ) {
    //     std::cerr << "Couldn't open image" << std::endl;
    //     return;
    // }

    double real, imag, treal, timag, treal2, timag2;

    // unsigned char * data = img.data_;
    //bgr
    for( int i = 0; i < height; ++i ) {

        imag = yMin + i * pixH;

        for( int j = 0; j < width; ++j ) {

            real = xMin + j * pixW;

            treal = real;
            timag = imag;

            treal2 = treal * treal;
            timag2 = timag * timag;

            int cyclesCount;
            for( cyclesCount = 0; ( cyclesCount < iterLimit ) && ( ( treal2 + timag2 ) < magLimit ); ++cyclesCount ) {
                timag = 2 * treal * timag + imag;
                treal = treal2 - timag2 + real;

                treal2 = treal * treal;
                timag2 = timag * timag;
            }

            image[ i * width + j ] = ( ( iterLimit + 1 ) - cyclesCount ) / (float) ( iterLimit + 1 );

            // img.data_[ ( i * width + j ) * 3 + 0 ] = cyclesCount;
            // img.data_[ ( i * width + j ) * 3 + 1 ] = cyclesCount;
            // img.data_[ ( i * width + j ) * 3 + 2 ] = cyclesCount;

        }

    }

    // img.save_image( "test.bmp" );

    updateTexture( image );

}

void mandelbrotSetup( ) {

    shouldRun = false;
    if( updater.joinable( ) )
        updater.join( );

    for( int i = 0; i < height * width; ++i )
        image[ i ] = 0.0;

    shouldRun = true;
    updater = std::thread( mandelbrotFun );

}

void mandelbrotTeardown( ) {

    shouldRun = false;
    if( updater.joinable( ) )
        updater.join( );

}

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
void mandelbrotKeyCallback( GLFWwindow* window, int key, int scancode, int action, int mods ) {

    // If the Esc key, close the window
    if( action == GLFW_PRESS ) {

        if( key == GLFW_KEY_ESCAPE )

            glfwSetWindowShouldClose( window, GL_TRUE );

        else if( key == GLFW_KEY_UP ) {

            y += scale / 4.0;
            mandelbrotSetup( );

        } else if( key == GLFW_KEY_DOWN ) {

            y -= scale / 4.0;
            mandelbrotSetup( );

        } else if( key == GLFW_KEY_LEFT ) {

            x -= scale / 4.0;
            mandelbrotSetup( );

        } else if( key == GLFW_KEY_RIGHT ) {

            x += scale / 4.0;
            mandelbrotSetup( );

        } else if( key == GLFW_KEY_I ) {

            scale /= 2.0;
            mandelbrotSetup( );

        } else if( key == GLFW_KEY_O ) {

            scale *= 2.0;
            mandelbrotSetup( );

        } else if( key == GLFW_KEY_R ) {

            x = -0.25;
            y = 0.0;
            scale = 1.0;
            mandelbrotSetup( );

        }

    }

}
#pragma clang diagnostic pop

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
void mandelbrotMouseCallback( GLFWwindow* window, int button, int action, int mods ) {

    if( action == GLFW_PRESS ) {

        if( button == GLFW_MOUSE_BUTTON_LEFT ) {

            double mx, my;
            glfwGetCursorPos( window, &mx, &my );
            // Relative to upper left in subpixel

            y += ( 1.0 - my * 2.0 / height ) * scale;
            x += ( mx * 2.0 / width  - 1.0 ) * scale;

            scale /= 2.0;

            mandelbrotSetup( );

        } else {

            scale *= 2.0;

            mandelbrotSetup( );

        }

    }

}
#pragma clang diagnostic pop
