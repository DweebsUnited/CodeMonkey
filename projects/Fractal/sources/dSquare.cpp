#include "includes.h"
#include "interface.h"

#include "dSquare.h"

void dSquare( ) {



    updateTexture( );

}

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
void dSquareKeyCallback( GLFWwindow* window, int key, int scancode, int action, int mods ) {

    // If the Esc key, close the window
    if( action == GLFW_PRESS ) {

        if( key == GLFW_KEY_ESCAPE )

            glfwSetWindowShouldClose( window, GL_TRUE );

    }

}
#pragma clang diagnostic pop
