/*************************************************************************************
* engine.cpp
* Defines the core of the engine
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/

/*************************************************************************************
* Includes
*************************************************************************************/
#include "engine.h"

/*************************************************************************************
* Engine class constructor
* Sets up graphics
*************************************************************************************/
SimpleEngine::Engine::Engine( int width, int height, const char * title ) : width( width ), height( height ), title( title ) { };

/*************************************************************************************
* Engine class destructor
* Cleans up graphics
*************************************************************************************/
SimpleEngine::Engine::~Engine( ) {

    //std::cerr << "Unbind shader program [ ";
    glUseProgram( 0 );
    //std::cerr << "OK ]" << std::endl;

    //std::cerr << "Detach shaders [ ";
    glDetachShader( this->programId, this->vertexShaderId );
    glDetachShader( this->programId, this->fragmentShaderId );
    //std::cerr << "OK ]" << std::endl;

    //std::cerr << "Delete shaders [ ";
    glDeleteShader( this->fragmentShaderId );
    glDeleteShader( this->vertexShaderId );
    //std::cerr << "OK ]" << std::endl;

    //std::cerr << "Delete program [ ";
    glDeleteProgram( this->programId );
    //std::cerr << "OK ]" << std::endl;

    //std::cerr << "Destroy window [ ";
    // Close window if open
    glfwDestroyWindow( this->window );
    //std::cerr << "OK ]" << std::endl;

    //std::cerr << "Terminate GLFW [ ";
    // Clean up glfw
    glfwTerminate( );
    //std::cerr << "OK ]" << std::endl;

};

/*************************************************************************************
* Engine startGraphics
* Starts a window with all the current arguments
* If one is already open, it closes and reopens it
*************************************************************************************/
void SimpleEngine::Engine::startGraphics( ) {

    // Attempt to start glfw
    if ( !glfwInit( ) ) {
        std::cerr << "Could not initialise glfw!" << std::endl;
        exit( 1 );
    }

    // Set error callback
    glfwSetErrorCallback( SimpleEngine::EngineUtils::errorCallback );

    std::cout << "Graphics object made: " << title << std::endl;

    // Close window if already open
    if( this->window ) {
        glfwDestroyWindow( this->window );
    }

    // Set up the hints for the window
    // Non-resizable, highest OpenGL
    glfwWindowHint( GLFW_RESIZABLE, GL_FALSE );
    glfwWindowHint( GLFW_CONTEXT_VERSION_MAJOR, 4 );
    glfwWindowHint( GLFW_CONTEXT_VERSION_MINOR, 1 );
    glfwWindowHint( GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE );
    glfwWindowHint( GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE );

    // Create a window!
    this->window = glfwCreateWindow( this->width, this->height, this->title, NULL, NULL );

    // Error check it
    if ( !this->window ) {
        glfwTerminate( );
        exit( 1 );
    }

    // DEBUG: Output the OpenGL version we got
    std::cout << "\nUsing OpenGL version " << glfwGetWindowAttrib( this->window, GLFW_CONTEXT_VERSION_MAJOR ) << '.' << glfwGetWindowAttrib( this->window, GLFW_CONTEXT_VERSION_MINOR ) << "\n\n";

    // Set the window as the current OpenGL context
    glfwMakeContextCurrent( this->window );

    // Start up GLEW so we have VAOs
    glewExperimental = GL_TRUE;
    GLenum err = glewInit( );

    // Reset error buffer because GLEW is stupid
    glGetError( );

    // Set the viewport size callback
    glfwSetFramebufferSizeCallback( this->window, EngineUtils::fbSizeCallback );

    // Set the key input callback
    glfwSetKeyCallback( this->window, EngineUtils::keyCallback );
    glfwSetInputMode( this->window, GLFW_CURSOR, GLFW_CURSOR_DISABLED );

    // Background default color
    glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );

    // Shader setup
    std::cout << "Loading Shaders...\n";

    // Create, setup, and compile the shaders
    this->loadShader( EngineUtils::vertexShader, ENGINE_VERTEX_SHADER );
    this->loadShader( EngineUtils::fragmentShader, ENGINE_FRAGMENT_SHADER );

    std::cout << "Compiling Shaders...\n";

    this->vertexShaderId = glCreateShader( GL_VERTEX_SHADER );
    glShaderSource( this->vertexShaderId, 1, &( this->vertexShader ), NULL );
    glCompileShader( this->vertexShaderId );
    this->unLoadShader( ENGINE_VERTEX_SHADER );

    this->fragmentShaderId = glCreateShader( GL_FRAGMENT_SHADER );
    glShaderSource( this->fragmentShaderId, 1, &( this->fragmentShader ), NULL );
    glCompileShader( this->fragmentShaderId );
    this->unLoadShader( ENGINE_FRAGMENT_SHADER );

    { // Error check the compilations

        // Result of gl status calls
        GLint compStatus;

        // Vertex shader
        // Query compilation status, output it
        glGetShaderiv( this->vertexShaderId, GL_COMPILE_STATUS, &compStatus );

        if( compStatus != GL_TRUE ) {

            std::cout << "Vertex compilation status: " << compStatus << '\n';

            // Get the length of the shader log, output it
            glGetShaderiv( this->vertexShaderId, GL_INFO_LOG_LENGTH, &compStatus );
            GLchar shadeLogV[ compStatus ]; shadeLogV[ 0 ] = 0;
            glGetShaderInfoLog( this->vertexShaderId, compStatus, NULL, shadeLogV );
            std::cout << "Shader Log:\n" << shadeLogV << "\n\n";

        }

        // Do it all again for the fragment shader
        glGetShaderiv( this->fragmentShaderId, GL_COMPILE_STATUS, &compStatus );

        if( compStatus != GL_TRUE ) {

            std::cout << "Fragment compilation status: " << compStatus << '\n';
            glGetShaderiv( this->fragmentShaderId, GL_INFO_LOG_LENGTH, &compStatus );
            GLchar shadeLogF[ compStatus ]; shadeLogF[ 0 ] = 0;
            glGetShaderInfoLog( this->fragmentShaderId, compStatus, NULL, shadeLogF );
            std::cout << "Shader Log:\n" << shadeLogF << "\n\n";

        }

    }

    std::cout << "Linking Shaders...\n";

    this->programId = glCreateProgram( );
    glAttachShader( this->programId, this->vertexShaderId );
    glAttachShader( this->programId, this->fragmentShaderId );
    glLinkProgram( this->programId );
    glUseProgram( this->programId );

    { // Get some info on link status

        // Result of gl status calls
        GLint compStatus;

        // Vertex shader
        // Query compilation status, output it
        glGetProgramiv( this->programId, GL_LINK_STATUS, &compStatus );

        if( compStatus != GL_TRUE ) {

            std::cout << "Program link status: " << compStatus << '\n';

            // Get the length of the program log, output it
            glGetProgramiv( this->programId, GL_INFO_LOG_LENGTH, &compStatus );
            GLchar progLog[ compStatus ]; progLog[ 0 ] = 0;
            glGetProgramInfoLog( this->programId, compStatus, NULL, progLog );
            std::cout << "Program Log:\n" << progLog << "\n\n";

        }

    }

    // Check for errors
    err = glGetError( );
    if( err != GL_NO_ERROR ) {

        // Print error message
        std::cout << "ERROR: Could not create the shaders: " << err << '\n';
        exit( 1 );

    }

    // Get the uniform locations
    this->mvpUniform = glGetUniformLocation( this->programId, "mvpMat" );

    // Enable back face culling, wound CW
    glEnable( GL_CULL_FACE );
    glCullFace( GL_BACK );
    glFrontFace( GL_CW );

    // Enable depth
    glEnable( GL_DEPTH_TEST );
    glDepthFunc( GL_LESS );

    // Give the window a little antialiasing so it looks nice
    glfwWindowHint (GLFW_SAMPLES, 4);

    std::cout << std::endl;

};

/*************************************************************************************
* Engine computeMVPMatrix
* Compute the MVP matrix with a given shape, save it to the uniform
*************************************************************************************/
void SimpleEngine::Engine::computeMVPMatrix( SimpleEngine::Shapes::Shape * s ) {

    // Calculate MVP matrix
    glm::mat4 mvpMat = this->projMat * this->viewMat * s->computeModelMat( );

    // Add MVP matrix to uniform
    glUniformMatrix4fv( this->mvpUniform, 1, GL_FALSE, &mvpMat[ 0 ][ 0 ] );

};

/*************************************************************************************
* Engine addShape
* Add a shape to the render list
*************************************************************************************/
void SimpleEngine::Engine::addShape( SimpleEngine::Shapes::Shape * s ) {

    this->shapeList.push_back( s );

};

/*************************************************************************************
* Engine remShape
* Remove a shape from the renderlist
*************************************************************************************/
void SimpleEngine::Engine::remShape( SimpleEngine::Shapes::Shape * s ) {

    this->shapeList.erase( std::remove( this->shapeList.begin( ), this->shapeList.end( ), s ), this->shapeList.end( ) );

};

/*************************************************************************************
* Engine runEngine
* The heart of the engine
*************************************************************************************/
void SimpleEngine::Engine::runEngine( bool (*userLogic)( float, float, float, void * thisPtr ), SimpleEngine::Camera::Camera * cam ) {

    if( !this->window ) {
        std::cerr << "Cannot run with no window!" << std::endl;
        exit( -2 );
    }

    // Frame limiter, and counter
    double ntime;
    double ftime = glfwGetTime( );
    int fcount = 0;
    struct timespec ts;
    ts.tv_sec = 0;

    // Set up mouse grabbing so user can have some dynamics
    float mousTime = glfwGetTime( );
    double mousX, mousY;

    while( 1 ) {
        // if the window should close, do it
        if( glfwWindowShouldClose( this->window ) ) {

            //std::cerr << "Begin shutdown" << std::endl;
            //std::cerr << "Window shutdown [ ";

            if( this->window )
                glfwDestroyWindow( this->window );

            //std::cerr << "OK ]" << std::endl;
            break;

        }

        // Clear background
        glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );

        // Call user logic function, give some basic info for now
        glfwGetCursorPos( this->window, &mousX, &mousY );
        glfwSetCursorPos( this->window, 0.0, 0.0 );
        userLogic( mousX, mousY, glfwGetTime( ) - mousTime, this );
        mousTime = glfwGetTime( );

        // Render all shapes

        // Calculate view matrix
        this->viewMat = cam->computeViewMatrix( );
        // Calculate projection matrix
        this->projMat = glm::perspective( 90.0f, (float) this->width / this->height, 0.1f, 10.0f );

        int index = 0;
        for( auto it = this->shapeList.begin( ); it != this->shapeList.end( ); ++it ) {
            this->computeMVPMatrix( *it );

            ( *it )->render( );

            ++index;
        }


        // Check mouse/keyboard events
        glfwPollEvents( );

        // Swap buffers and draw to screen
        glfwSwapBuffers( this->window );

        // Get current time
        ntime = glfwGetTime( );

        // FPS counter, only update once per second
        if( ntime - ftime > 1.0 ) {
            //std::cout << "FPS: " << fcount << std::endl; TODO: Add this back in, but make it a little less annoying :/
            fcount = 0;
            ftime = ntime;
        } else
            ++fcount;
    }

};


/*************************************************************************************
* Engine loadShader
* Load a shader from a file
*************************************************************************************/
void SimpleEngine::Engine::loadShader( const char * filename, uint shaderToUse ) {

    uint                len;                // File length
    uint                i = 0;              // Counter
    std::ifstream       file;               // File stream

    // Open the file as ASCII
    file.open( filename, std::ios::in );

    // If file not good, say and quit
    if( !file.good( ) ) {

        std::cout << "Could not load shader file: " << filename << '\n';
        exit( 1 );

    }

    // Seek to the end, get the pos, and reset the stream
    file.seekg( 0, std::ios::end );
    len = file.tellg( );
    file.seekg( std::ios::beg );

    // If the file is empty, or the read failed
    if( len == 0 ) {

        std::cout << "Could not load shader file: " << filename << '\n';
        exit( 1 );

    }

    // Get memory for the shader, fail if needed, then read in from file
    if( shaderToUse == ENGINE_VERTEX_SHADER ) {

        this->vertexShader = ( GLchar * ) new char[ len + 1 ];

        if( this->vertexShader == 0 ) {

            std::cout << "Could not reserve memory for shader!\n";
            exit( 1 );

        }

        // Get all chars from file
        while ( file.good( ) ) {

            this->vertexShader[ i ] = file.get( );

            if ( !file.eof( ) )
                i++;

        }

        // Ensure theres a trailing 0
        this->vertexShader[ i ] = 0;

    } else if( shaderToUse == ENGINE_FRAGMENT_SHADER ) {

        this->fragmentShader = ( GLchar* ) new char[ len + 1 ];

        if( this->fragmentShader == 0 ) {

            std::cout << "Could not reserve memory for shader!\n";
            exit( 1 );

        }

        // Get all chars from file
        while ( file.good( ) ) {

            this->fragmentShader[ i ] = file.get( );

            if ( !file.eof( ) )
                i++;

        }

        // Ensure theres a trailing 0
        this->fragmentShader[ i ] = 0;

    }

    // Close the file
    file.close( );

};

/*************************************************************************************
* Engine unLoadShader
* Unload a shader file
*************************************************************************************/
void SimpleEngine::Engine::unLoadShader( int shaderToUse ) {

    if( shaderToUse == 0 ) {

        if( this->vertexShader != 0 ){

            delete[ ] this->vertexShader;
            this->vertexShader = 0;

        }

    } else {

        if( this->fragmentShader != 0 ){

            delete[ ] this->fragmentShader;
            this->fragmentShader = 0;

        }

    }

};

/*************************************************************************************
* Error handler for glfw
* Print out any errors that occur
*************************************************************************************/
void SimpleEngine::EngineUtils::errorCallback( int error, const char * descrip ) {

    std::cerr << error << ": " << descrip;
    exit( -1 );

};

/*************************************************************************************
* FrameBufferSize handler for glfw
* Resize viewport if window size changes
*************************************************************************************/
// The callback has more args than are used, so suppress the warning
// This needs a better solution...
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"

void SimpleEngine::EngineUtils::fbSizeCallback( GLFWwindow* window, int width, int height ) {

    glViewport( 0, 0, width, height );

};

#pragma clang diagnostic pop

/*************************************************************************************
* Input handler for glfw
* Close window if key is Esc
*************************************************************************************/
// The callback has more args than are used, so suppress the warning
// This needs a better solution...
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"

void SimpleEngine::EngineUtils::keyCallback( GLFWwindow* window, int key, int scancode, int action, int mods ) {

    // If the Esc key, close the window
    if ( key == GLFW_KEY_ESCAPE && action == GLFW_PRESS )
        glfwSetWindowShouldClose( window, GL_TRUE );

};

#pragma clang diagnostic pop

/*************************************************************************************
* Shader default filenames
*************************************************************************************/
const char* SimpleEngine::EngineUtils::vertexShader = "resources/default.vertexshader";
const char* SimpleEngine::EngineUtils::fragmentShader = "resources/default.fragmentshader";
