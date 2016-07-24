/*************************************************************************************
* tetrahedron.h
* Declares the Tetrahedron class
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/
#ifndef _ENGINE_SHAPES_TETRAHEDRON_H
#define _ENGINE_SHAPES_TETRAHEDRON_H

/*************************************************************************************
* Includes
*************************************************************************************/
#include "includes.h"
#include "shapes.h"

/*************************************************************************************
* Namespace declaration
*   The one time I won't indent
*************************************************************************************/
namespace SimpleEngine {
namespace Shapes {

/*************************************************************************************
* Tetrahedron Class
* Tetrahedron with side length 2
*************************************************************************************/
class Tetrahedron : public Shape {
public:
    // Derive all other methods from Shape, there is no special processing here
    bool prepare( ) {                       /* Get the shape ready                  */

        // Points
        GLfloat points[ ] = { 1.0, 0.0, -0.707,
                            -1.0, 0.0, -0.707,
                            0.0, 1.0, 0.707,
                            0.0, -1.0, 0.707,

                            // Wireframe
                            1.01, 0.0, -0.708,
                            -1.01, 0.0, -0.708,
                            0.0, 1.01, 0.708,
                            0.0, -1.01, 0.708
                        };

        // Colors
        GLfloat colors[ ] = { 1.0, 0.0, 0.0,
                            0.0, 1.0, 0.0,
                            0.0, 0.0, 1.0,
                            0.0, 1.0, 0.0,

                            // Wireframe
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0 };

        // Index buffer
        GLuint index[ ] = {   2, 0, 1,
                                    1, 0, 3,
                                    2, 1, 3,
                                    0, 2, 3,

                                    // Wireframe
                                    6, 5,
                                    5, 7,
                                    7, 4,
                                    4, 6,
                                    4, 5,
                                    6, 7
                                     };

        this->numVerts = 4;
        this->numIdx = 12;
        this->wfNumIdx = 12;

        // VAO initialization
        this->vaoID = 0;
        glGenVertexArrays( 1, &this->vaoID );
        glBindVertexArray( this->vaoID );
        glEnableVertexAttribArray( 0 );
        glEnableVertexAttribArray( 1 );

        // Points VBO initialization
        this->vboIDVert = 0;
        glGenBuffers( 1, &this->vboIDVert );
        glBindBuffer( GL_ARRAY_BUFFER, this->vboIDVert );
        glBufferData( GL_ARRAY_BUFFER, ( this->numVerts * 2 ) * 3 * sizeof( GLfloat ), points, GL_STATIC_DRAW );

        glVertexAttribPointer( 0, 3, GL_FLOAT, GL_FALSE, 0, NULL );

        // Colors VBO initialization
        this->vboIDColor = 0;
        glGenBuffers( 1, &this->vboIDColor );
        glBindBuffer( GL_ARRAY_BUFFER, this->vboIDColor );
        glBufferData( GL_ARRAY_BUFFER, ( this->numVerts * 2 ) * 3 * sizeof( GLfloat ), colors, GL_STATIC_DRAW );

        glVertexAttribPointer( 1, 3, GL_FLOAT, GL_FALSE, 0, NULL );

        // Index buffer initialization
        this->vboIDIndx = 0;
        glGenBuffers( 1, &this->vboIDIndx );
        glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, this->vboIDIndx );
        glBufferData( GL_ELEMENT_ARRAY_BUFFER, ( this->numIdx + this->wfNumIdx ) * sizeof( GLuint ), index, GL_STATIC_DRAW );

        return true;

    };

    void render( ) {                        /* Render this shape                    */

        // Bind VAO for drawing
        glBindVertexArray( this->vaoID );
        // Draw the shape
        glDrawElements( GL_TRIANGLES, this->numIdx, GL_UNSIGNED_INT, NULL );

        if( this->renderFrame ) {
            // Draw the shape
            glDrawElements( GL_LINES, this->wfNumIdx, GL_UNSIGNED_INT, (const GLvoid *) ( this->numIdx * sizeof( GLuint ) ) );
        }

    };

    bool renderFrame = true;                /* Whether to render a wireframe        */

private:
    GLuint              wfNumIdx;           /* Number of index elements to be drawn for wireframe */

};

}; }; // End of namespaces

#endif
