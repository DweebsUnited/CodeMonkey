/*************************************************************************************
* cube.h
* Declares the Cube class
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/
#ifndef _ENGINE_SHAPES_CUBE_H
#define _ENGINE_SHAPES_CUBE_H

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
* Cube Class
* It's a cube. What more can I say?
*************************************************************************************/
class Cube : public Shape {
public:
    // Derive all other methods from Shape, there is no special processing here
    bool prepare( ) {                       /* Get the cube ready                   */

        // Cube points
        GLfloat points[ ] = { 0.5, 0.5, 0.5, // FTR
                            -0.5, 0.5, 0.5, // FTL
                            -0.5, -0.5, 0.5, // FBL
                            0.5, -0.5, 0.5, // FBR
                            0.5, 0.5, -0.5, // BTR
                            -0.5, 0.5, -0.5, // BTL
                            -0.5, -0.5, -0.5, // BBL
                            0.5, -0.5, -0.5, // BBR

                            // Wireframe
                            0.51, 0.51, 0.51, // FTR
                            -0.51, 0.51, 0.51, // FTL
                            -0.51, -0.51, 0.51, // FBL
                            0.51, -0.51, 0.51, // FBR
                            0.51, 0.51, -0.51, // BTR
                            -0.51, 0.51, -0.51, // BTL
                            -0.51, -0.51, -0.51, // BBL
                            0.51, -0.51, -0.51 // BBR
                        };

        // Cube colors
        GLfloat colors[ ] = { 1.0, 0.0, 0.0,
                            0.0, 1.0, 0.0,
                            0.0, 0.0, 1.0,
                            1.0, 0.0, 0.0,
                            0.0, 1.0, 0.0,
                            0.0, 0.0, 1.0,
                            1.0, 0.0, 0.0,
                            0.0, 1.0, 0.0,

                            // Wireframe
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0 };

        // Cube index buffer
        GLuint index[ ] = {   1, 0, 3,
                                    1, 3, 2,
                                    5, 4, 0,
                                    5, 0, 1,
                                    2, 3, 7,
                                    2, 7, 6,
                                    5, 1, 2,
                                    5, 2, 6,
                                    0, 4, 7,
                                    0, 7, 3,
                                    6, 7, 4,
                                    6, 4, 5,

                                    // Wireframe
                                    8, 9,
                                    9, 10,
                                    10, 11,
                                    11, 8,
                                    12, 13,
                                    13, 14,
                                    14, 15,
                                    15, 12,
                                    8, 12,
                                    9, 13,
                                    10, 14,
                                    11, 15 };

        this->numVerts = 8;
        this->numIdx = 36;
        this->wfNumIdx = 24;

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

    void render( ) {                        /* Render this cube                     */

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
