/*************************************************************************************
* engine.h
* Declares the Engine class, and some helpful utilities.
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/
#ifndef _ENGINE_H
#define _ENGINE_H

/*************************************************************************************
* Literal Definitions
*************************************************************************************/
#define ENGINE_VERTEX_SHADER        0       /* Vertex shader enum                   */
#define ENGINE_FRAGMENT_SHADER      1       /* Fragment shader enum                 */

/*************************************************************************************
* Includes
*************************************************************************************/
#include "includes.h"

#include "camera.h"

#include "shapes.h"

/*************************************************************************************
* Namespace declaration
*   The one time I won't indent
*************************************************************************************/
namespace SimpleEngine {

/*************************************************************************************
* Engine Class
* Handles all the backend for programs using it.
*************************************************************************************/
class Engine {
public:
    Engine( int width, int height, const char * title );
    ~Engine( );
    /* Constructors and destructors         */
    void startGraphics( );
    /* Start function                       */
    void computeMVPMatrix( Shapes::Shape * );
    /* Compute the full MVP matrix for a given shape */
    void addShape( Shapes::Shape * );
    /* Add a shape to the render list       */
    void remShape( Shapes::Shape * );
    /* Remove a shape from render list      */
    void runEngine( bool (*userLogic)( float, float, float, void * ), Camera::Camera * );
    /* Kick off the render loop             */

private:
    GLFWwindow*         window = NULL;      /* Window for graphics                  */
    int                 width;              /* Window default width                 */
    int                 height;             /* Window default height                */
    const char*         title;              /* Window default title                 */

    GLuint              vertexShaderId;     /* Vertex shader ID                     */
    GLuint              fragmentShaderId;   /* Fragment shader ID                   */
    GLuint              programId;          /* Program shader ID                    */
    GLchar*             vertexShader;       /* Vertex shader code                   */
    GLchar*             fragmentShader;     /* Fragment shader code                 */

    GLuint              mvpUniform;         /* Uniform loc of the MVP matrix        */

    glm::mat4           viewMat;
    glm::mat4           projMat;

    std::vector< Shapes::Shape * >
                        shapeList;          /* List of all shapes to render         */

    void loadShader( const char *, uint );
    /* Load a shader file                   */
    void unLoadShader( int );
    /* Unload a shader file                 */

};

/*************************************************************************************
* EngineUtil Namespace
* Extra engine utilities
*************************************************************************************/
namespace EngineUtils {

    /*********************************************************************************
    * Default callback handlers
    *********************************************************************************/
    void errorCallback( int, const char * );
    void fbSizeCallback( GLFWwindow*, int, int );
    void keyCallback( GLFWwindow*, int, int, int, int );

    /*********************************************************************************
    * Default shader filenames
    *********************************************************************************/
    extern const char*  vertexShader;
    extern const char*  fragmentShader;

};

}; // End of SimpleEngine namespace

#endif
