/*************************************************************************************
* shapes.h
* Declares the Shape class, and several Shape primitives.
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/
#ifndef _ENGINE_SHAPES_H
#define _ENGINE_SHAPES_H

/*************************************************************************************
* Includes
*************************************************************************************/
#include "includes.h"

/*************************************************************************************
* Namespace declaration
*   The one time I won't indent
*************************************************************************************/
namespace SimpleEngine {
namespace Shapes {

/*************************************************************************************
* Shape Class
* Base renderable class, everything stems from here
*************************************************************************************/
class Shape {
public:
    Shape( );
    Shape( const Shape& s );

    virtual ~Shape( );

    /* Constructors and destructors         */

    virtual bool prepare( ) = 0;
    /* Get the shape ready                  */

    glm::mat4 computeModelMat( );
    /* Compute the model matrix for this shape */

    virtual void render( ) = 0;
    /* Render the shape                     */

    void setLocation( float x, float y, float z ) { this->translation = glm::vec3( x, y, z ); };
    void setLocation( glm::vec3 loc ) { this->translation = loc; };

    void setScale( float x, float y, float z ) { this->scale = glm::vec3( x, y, z ); };
    void setScale( glm::vec3 scale ) { this->scale = scale; };

    glm::vec3           translation;        /* Translation of the object            */
    glm::vec3           scale;              /* Scales of the object                 */
    glm::vec3           rotAxis;            /* Axis of rotation                     */
    GLfloat             rotAngle;           /* Angle of rotation                    */

protected:
    ~Shape( );
    GLuint              vaoID;              /* ID of the shapes VAO                 */
    GLuint              vboIDVert;          /* ID of the shapes vertex VBO          */
    GLuint              vboIDColor;         /* ID of the shapes color VBO           */
    GLuint              vboIDIndx;          /* ID of the shapes vert index VBO      */

    GLuint              numVerts;           /* Number of vertices                   */
    GLuint              numIdx;             /* Number of index elements to be drawn */

};

}; }; // End of namespaces

#include "cube.h"
#include "tetrahedron.h"
#include "tetracosagon.h"

#endif
