#include "main.h"

// Shader sources
const GLchar* vertexShader =
    "#version 410\n"
    "layout( location = 0 ) in vec2 iPos;"
    "layout( location = 1 ) in vec2 iTexcoord;"
    "layout( location = 2 ) in vec3 iColor;"
    "layout( location = 0 ) out vec3 color;"
    "layout( location = 1 ) out vec2 texcoord;"
    "void main(){"
    "    color = iColor;"
    "    texcoord = iTexcoord;"
    "    gl_Position = vec4(iPos, 0.0, 1.0);"
    "}";
const GLchar* fragmentShader =
    "#version 410\n"
    "layout( location = 0 ) in vec3 color;"
    "layout( location = 1 ) in vec2 texcoord;"
    "layout( location = 0 ) out vec4 oColor;"
    "uniform sampler2D texID;"
    "void main(){"
    "    vec4 tex = texture( texID, texcoord );"
    "    oColor = vec4( tex.r, tex.r, tex.r, 1.0 );"
    "}";

// Graphics interface globals
const int width = 1024;
const int height = 1024;

static std::atomic<float *> data;
static std::atomic<bool> dirty( false );

void updateTexture( float * image ) {

    data = image;
    dirty = true;

}


int main( int argc, char ** argv ) {

    if( argc != 2 ) {
        std::cerr << "Invalid usage. Please give a mode." << std::endl << "Options: mandelbrot dSquare deJong" << std::endl << std::endl;
        exit( 1 );
    }

    // Begin graphics

    // Start glfw
    glfwInit( );

    // Make a window
    glfwWindowHint( GLFW_RESIZABLE, GL_FALSE );
    glfwWindowHint( GLFW_CONTEXT_VERSION_MAJOR, 4 );
    glfwWindowHint( GLFW_CONTEXT_VERSION_MINOR, 1 );
    glfwWindowHint( GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE );
    glfwWindowHint( GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE );

    GLFWwindow * window = glfwCreateWindow( width, height, "Mandelbrot", NULL, NULL );

    glfwMakeContextCurrent( window );

    // Set callbacks
    glfwSetErrorCallback( errorCallback );

    // Start up GLEW
    glewExperimental = GL_TRUE;
    glewInit( );

    // Background default color
    glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );

    // Shaders

    // Clear errors
    glGetError( );

    // Vertex
    GLuint vertexShaderId = glCreateShader( GL_VERTEX_SHADER );
    glShaderSource( vertexShaderId, 1, &vertexShader, NULL );
    glCompileShader( vertexShaderId );

    // Fragment
    GLuint fragmentShaderId = glCreateShader( GL_FRAGMENT_SHADER );
    glShaderSource( fragmentShaderId, 1, &fragmentShader, NULL );
    glCompileShader( fragmentShaderId );

    { // Error check the compilations

        // Result of gl status calls
        GLint compStatus;

        // Vertex shader
        // Query compilation status, output it
        glGetShaderiv( vertexShaderId, GL_COMPILE_STATUS, &compStatus );

        if( compStatus != GL_TRUE ) {

            std::cerr << "Vertex compilation status: " << compStatus << std::endl;

            // Get the length of the shader log, output it
            glGetShaderiv( vertexShaderId, GL_INFO_LOG_LENGTH, &compStatus );
            GLchar shadeLogV[ compStatus ]; shadeLogV[ 0 ] = 0;
            glGetShaderInfoLog( vertexShaderId, compStatus, NULL, shadeLogV );
            std::cerr << "Shader Log:" << std::endl << shadeLogV << std::endl << std::endl;

        }

        // Do it all again for the fragment shader
        glGetShaderiv( fragmentShaderId, GL_COMPILE_STATUS, &compStatus );

        if( compStatus != GL_TRUE ) {

            std::cerr << "Fragment compilation status: " << compStatus << std::endl;
            glGetShaderiv( fragmentShaderId, GL_INFO_LOG_LENGTH, &compStatus );
            GLchar shadeLogF[ compStatus ]; shadeLogF[ 0 ] = 0;
            glGetShaderInfoLog( fragmentShaderId, compStatus, NULL, shadeLogF );
            std::cerr << "Shader Log:" << std::endl << shadeLogF << std::endl << std::endl;

        }

    }

    GLuint shaderProgram = glCreateProgram( );
    glAttachShader( shaderProgram, vertexShaderId );
    glAttachShader( shaderProgram, fragmentShaderId );
    glLinkProgram( shaderProgram );
    glUseProgram( shaderProgram );

    { // Get some info on link status

        // Result of gl status calls
        GLint compStatus;

        // Vertex shader
        // Query compilation status, output it
        glGetProgramiv( shaderProgram, GL_LINK_STATUS, &compStatus );

        if( compStatus != GL_TRUE ) {

            std::cerr << "Program link status: " << compStatus << std::endl;

            // Get the length of the program log, output it
            glGetProgramiv( shaderProgram, GL_INFO_LOG_LENGTH, &compStatus );
            GLchar progLog[ compStatus ]; progLog[ 0 ] = 0;
            glGetProgramInfoLog( shaderProgram, compStatus, NULL, progLog );
            std::cerr << "Program Log:" << std::endl << progLog << std::endl << std::endl;

        }

    }

    if( glGetError( ) != GL_NO_ERROR ) {

        // Print error message
        std::cerr << "ERROR: Could not create the shaders" << std::endl;
        exit( 1 );

    }

    // Set up data

    // Create Vertex Array Object
    GLuint vao;
    glGenVertexArrays( 1, &vao );
    glBindVertexArray( vao );

    // Create a Vertex Buffer Object and copy the vertex data to it
    GLuint vbo;
    glGenBuffers( 1, &vbo );

    GLfloat vertices[ ] = {
    //  Position      Texcoords   Color
        -1.0f,  1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, // Top-left
         1.0f,  1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, // Top-right
         1.0f, -1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, // Bottom-right
        -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f  // Bottom-left
    };

    glBindBuffer( GL_ARRAY_BUFFER, vbo );
    glBufferData( GL_ARRAY_BUFFER, 7 * 4 * sizeof( GLfloat ), vertices, GL_STATIC_DRAW );

    // Create an element array
    GLuint ebo;
    glGenBuffers( 1, &ebo );

    GLuint elements[] = {
        0, 3, 2,
        2, 1, 0
    };

    glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, ebo );
    glBufferData( GL_ELEMENT_ARRAY_BUFFER, 3 * 2 * sizeof( GLuint ), elements, GL_STATIC_DRAW );

    // Layout data
    // Position
    glEnableVertexAttribArray( 0 );
    glVertexAttribPointer( 0, 2, GL_FLOAT, GL_FALSE, 7 * sizeof( GLfloat ), 0 );

    // Texture coordinates
    glEnableVertexAttribArray( 1 );
    glVertexAttribPointer( 1, 2, GL_FLOAT, GL_FALSE, 7 * sizeof( GLfloat ), (void *)( 2 * sizeof( GLfloat ) ) );

    // Color
    glEnableVertexAttribArray( 2 );
    glVertexAttribPointer( 2, 3, GL_FLOAT, GL_FALSE, 7 * sizeof( GLfloat ), (void *)( 4 * sizeof( GLfloat ) ) );

    // Textures
    glActiveTexture( GL_TEXTURE0 );

    GLuint texture;
    glGenTextures( 1, &texture );
    glBindTexture( GL_TEXTURE_2D, texture );

    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE );
    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE );
    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR );
    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );

    glUniform1i( glGetUniformLocation( shaderProgram, "texID" ), 0 );


    // Set up things specific to each mode
    std::string mode = argv[ 1 ];
    Plugin * plug;
    if( mode == "mandelbrot" ) {
        plug = &mandelbrot;
    } else if( mode == "dSquare" ) {
        plug = &dSquare;
    } else if( mode == "deJong" ) {
        plug = &peterDeJong;
    } else {
        std::cerr << "Invalid mode!" << std::endl << "Options: mandelbrot dSquare deJong" << std::endl << std::endl;
        exit( 1 );
    }

    glfwSetKeyCallback( window, plug->keyCbk );
    glfwSetMouseButtonCallback( window, plug->mouseCbk );
    if( plug->setup )
        plug->setup( );


    // Main render loop
    if( plug->renderloop )
        plug->renderloop( );
    else {

        while( 1 ) {

            // if the window should close, do it
            if( glfwWindowShouldClose( window ) ) {

                if( plug->teardown )
                    plug->teardown( );

                if( window )
                    glfwDestroyWindow( window );

                break;

            }

            // Clear background
            glClear( GL_COLOR_BUFFER_BIT );

            // Check mouse/keyboard events
            glfwPollEvents( );

            glDrawElements( GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0 );

            // Swap buffers and draw to screen
            glfwSwapBuffers( window );

            // Check if we have an update
            if( dirty ) {
                glTexImage2D( GL_TEXTURE_2D, 0, GL_RED, width, height, 0, GL_RED, GL_FLOAT, data );
                dirty = false;
            }

        }

    }

    return 0;

}

void errorCallback( int error, const char * descrip ) {

    std::cerr << error << ": " << descrip;
    exit( -1 );

}
