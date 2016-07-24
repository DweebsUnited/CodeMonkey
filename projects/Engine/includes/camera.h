/*************************************************************************************
* camera.h
* Declares the Camera class, and several derivatives thereof.
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/
#ifndef _ENGINE_CAMERA_H
#define _ENGINE_CAMERA_H

/*************************************************************************************
* Includes
*************************************************************************************/
#include "includes.h"

/*************************************************************************************
* Namespace declaration
*   The one time I won't indent
*************************************************************************************/
namespace SimpleEngine {
namespace Camera {

/*************************************************************************************
* Camera Class
* Handles all the camera BS that needs to happen.
*************************************************************************************/
class Camera {
public:
    virtual glm::mat4 computeViewMatrix( ) { return glm::lookAt( camLoc, camLoc + camFore, camUp ); };
                                            /* Compute the view matrix              */

    void setLocation( GLfloat x, GLfloat y, GLfloat z ) { this->camLoc = glm::vec3( x, y, z ); };
    void setLocation( glm::vec3 loc ) { this->camLoc = loc; };

    glm::vec3 getLocation( ) { return this->camLoc; };

    void setForewards( GLfloat x, GLfloat y, GLfloat z ) { this->camFore = glm::vec3( x, y, z ); };
    void setForewards( glm::vec3 fore ) { this->camFore = fore; };

    glm::vec3 getForeward( ) { return this->camFore; };

    void setLookAt( GLfloat x, GLfloat y, GLfloat z ) { this->camFore = glm::vec3( x, y, z ) - camLoc; }
    void setLookAt( glm::vec3 la ) { this->camFore = la - camLoc; }

    void setUp( GLfloat x, GLfloat y, GLfloat z ) { this->camUp = glm::vec3( x, y, z ); };
    void setUp( glm::vec3 up ) { this->camUp = up; };

    glm::vec3 getUp( ) { return this->camUp; };

    glm::vec3           camLoc;             /* Camera location                      */
    glm::vec3           camFore;            /* Camera foreward direction            */
    glm::vec3           camUp;              /* Camera up direction                  */

};

/*************************************************************************************
* SphereCamera Class
* Orbiting camera
*************************************************************************************/
class SphereCamera : public Camera {
public:

    glm::mat4 computeViewMatrix( ) {

        glm::quat vRot = glm::angleAxis( glm::radians( this->phi ), glm::vec3( 1.0, 0.0, 0.0 ) );
        glm::quat hRot = glm::angleAxis( glm::radians( this->theta ), glm::vec3( 0.0, 1.0, 0.0 ) );

        this->camLoc = glm::vec3( glm::rotate( hRot, glm::rotate( vRot, glm::vec4( 0.0, 0.0, -1.0, 0.0 ) ) ) ) * this->distance;
        this->camUp = glm::vec3( glm::rotate( hRot, glm::rotate( vRot, glm::vec4( 0.0, 1.0, 0.0, 0.0 ) ) ) );
        this->camFore = this->origin - this->camLoc;

        return glm::lookAt( this->camLoc, this->origin, this->camUp );

    };

    void setDistance( GLfloat dist ) { this->distance = dist; };
    GLfloat getDistance( ) { return this->distance; };

    void setAngles( GLfloat theta, GLfloat phi ) { this->theta = theta; this->phi = phi; };
    GLfloat getTheta( ) { return this->theta; };
    GLfloat getPhi( ) { return this->phi; };

    GLfloat theta = 0.0;                    /* Horizontal angle                     */
    GLfloat phi = 0.0;                      /* Vertical angle                       */
    GLfloat distance = 1.0;                 /* Distance from origin                 */

    glm::vec3 origin = glm::vec3( 0, 0, 0 ); // NOT USED YET
                                            /* Origin for camera to orbit           */

};

}; }; // End of namespaces

#endif
