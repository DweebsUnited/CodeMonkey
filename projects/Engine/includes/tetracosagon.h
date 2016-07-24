/*************************************************************************************
* tetracosagon.h
* Declares the Tetracosagon class
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/
#ifndef _ENGINE_SHAPES_TETRACOSAGON_H
#define _ENGINE_SHAPES_TETRACOSAGON_H

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
* Tetracosagon Class
* Cubie made from 3 planes
*************************************************************************************/
class Tetracosagon : public Shape {
public:
    // Derive all other methods from Shape, there is no special processing here
    bool prepare( ) {                       /* Get the shape ready                  */

        // Shape points
        GLfloat points[ ] = { 0.5, 0.5, 0.0,
                            -0.5, 0.5, 0.0,
                            -0.5, -0.5, 0.0,
                            0.5, -0.5, 0.0,

                            0.0, 0.5, 0.5,
                            0.0, -0.5, 0.5,
                            0.0, -0.5, -0.5,
                            0.0, 0.5, -0.5,

                            0.5, 0.0, 0.5,
                            -0.5, 0.0, 0.5,
                            -0.5, 0.0, -0.5,
                            0.5, 0.0, -0.5,

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

        // Shape colors
        GLfloat colors[ ] = { 0.6, 0.5, 0.5,
                            0.6, 0.5, 0.5,
                            0.6, 0.5, 0.5,
                            0.6, 0.5, 0.5,
                            0.5, 0.6, 0.5,
                            0.5, 0.6, 0.5,
                            0.5, 0.6, 0.5,
                            0.5, 0.6, 0.5,
                            0.5, 0.5, 0.6,
                            0.5, 0.5, 0.6,
                            0.5, 0.5, 0.6,
                            0.5, 0.5, 0.6,

                            // Wireframe
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0 };

        // Shape index buffer
        GLuint index[ ] = {   0, 3, 1,
                                    1, 3, 2,
                                    0, 1, 3,
                                    1, 2, 3,

                                    4, 5, 7,
                                    7, 5, 6,
                                    4, 7, 5,
                                    7, 6, 5,

                                    8, 11, 9,
                                    9, 11, 10,
                                    8, 9, 11,
                                    9, 10, 11,

                                    // Wireframe
                                    12, 13,
                                    13, 14,
                                    14, 15,
                                    15, 12,
                                    16, 17,
                                    17, 18,
                                    18, 19,
                                    19, 16,
                                    12, 16,
                                    13, 17,
                                    14, 18,
                                    15, 19 };

        this->numVerts = 12;
        this->wfNumVerts = 8;
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
        glBufferData( GL_ARRAY_BUFFER, ( this->numVerts + this->wfNumVerts ) * 3 * sizeof( GLfloat ), points, GL_STATIC_DRAW );

        glVertexAttribPointer( 0, 3, GL_FLOAT, GL_FALSE, 0, NULL );

        // Colors VBO initialization
        this->vboIDColor = 0;
        glGenBuffers( 1, &this->vboIDColor );
        glBindBuffer( GL_ARRAY_BUFFER, this->vboIDColor );
        glBufferData( GL_ARRAY_BUFFER, ( this->numVerts + this->wfNumVerts ) * 3 * sizeof( GLfloat ), colors, GL_STATIC_DRAW );

        glVertexAttribPointer( 1, 3, GL_FLOAT, GL_FALSE, 0, NULL );

        // Index buffer initialization
        this->vboIDIndx = 0;
        glGenBuffers( 1, &this->vboIDIndx );
        glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, this->vboIDIndx );
        glBufferData( GL_ELEMENT_ARRAY_BUFFER, ( this->numIdx + this->wfNumIdx ) * sizeof( GLuint ), index, GL_STATIC_DRAW );

        return true;

    };

    void render( ) {                        /* Render this shape                    */

        if( glIsVertexArray( this->vaoID ) ) {
            // Bind VAO for drawing
            glBindVertexArray( this->vaoID );

            // Draw the shape
            glDrawElements( GL_TRIANGLES, this->numIdx, GL_UNSIGNED_INT, NULL );

            if( this->renderFrame ) {
                // Draw the shape
                glDrawElements( GL_LINES, this->wfNumIdx, GL_UNSIGNED_INT, (const GLvoid *) ( this->numIdx * sizeof( GLuint ) ) );
            }
        } else
            std::cerr << "Tetracosagon vao does not exist!" << std::endl;

    };

    bool renderFrame = true;                /* Whether to render a wireframe        */

private:
    GLuint              wfNumIdx;           /* Number of index elements to be drawn for wireframe */
    GLuint              wfNumVerts;         /* Number of vertices for the wireframe */

};

}; }; // End of namespaces

#endif
