/*************************************************************************************
* shapes.cpp
* Defines the Shape class, and several primitives
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/

/*************************************************************************************
* Includes
*************************************************************************************/
#include "shapes.h"

/*************************************************************************************
* Shape class constructor
*************************************************************************************/
SimpleEngine::Shapes::Shape::Shape( ) {

    // Set up basic parameters for modelMat
    this->translation = glm::vec3( 0.25, 0.0, 0.0 );
    this->scale = glm::vec3( 1.0, 1.0, 1.0 );
    this->rotAxis = glm::vec3( 0.70711, 0.70711, 0.0 );
    this->rotAngle = 0;

};

/*************************************************************************************
* Shape class constructor
*************************************************************************************/
SimpleEngine::Shapes::Shape::Shape( const Shape& s ) {

    // Save all the data from the old shape
    this->vaoID = s.vaoID;
    this->vboIDVert = s.vboIDVert;
    this->vboIDColor = s.vboIDColor;
    this->vboIDIndx = s.vboIDIndx;

    this->numVerts = s.numVerts;
    this->numIdx = s.numIdx;

    this->translation = s.translation;
    this->scale = s.scale;
    this->rotAxis = s.rotAxis;
    this->rotAngle = s.rotAngle;

};

/*************************************************************************************
* Shape class destructor
* Deletes all created arrays
*************************************************************************************/
SimpleEngine::Shapes::Shape::~Shape( ) {

    // Bind our VAO if it exists
    if( glIsVertexArray( this->vaoID ) ) {
        glBindVertexArray( this->vaoID );

        // Delete our VBOs
        if( glIsBuffer( this->vboIDVert ) )
            glDeleteBuffers( 1, &( this->vboIDVert ) );

        if( glIsBuffer( this->vboIDColor ) )
            glDeleteBuffers( 1, &( this->vboIDColor ) );

        if( glIsBuffer( this->vboIDIndx ) )
            glDeleteBuffers( 1, &( this->vboIDIndx ) );

        // Unbind and delete our VAO
        glBindVertexArray( 0 );
        glDeleteVertexArrays( 1, &( this->vaoID ) );
    }
};

/*************************************************************************************
* Shape computation of model matrix, necessary for rendering
*************************************************************************************/
glm::mat4 SimpleEngine::Shapes::Shape::computeModelMat( ) {

    glm::mat4 transMat = glm::translate( this->translation );

    glm::mat4 scaleMat = glm::scale( this->scale );

    glm::mat4 rotaMat = glm::mat4_cast( glm::angleAxis( this->rotAngle, this->rotAxis ) );

    // Calculate model matrix
    return transMat * rotaMat * scaleMat;

};
