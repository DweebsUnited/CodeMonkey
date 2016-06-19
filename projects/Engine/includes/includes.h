/*************************************************************************************
* includes.h
* This is a central point for all graphics includes.
* It was too much of a headache tracking it all otherwise.
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/
#ifndef _ENGINE_INCLUDES_H
#define _ENGINE_INCLUDES_H

/*************************************************************************************
* Includes
*************************************************************************************/
#include <GL/glew.h>

#define GLFW_INCLUDE_GLU
#include <GLFW/glfw3.h>

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-W#warnings"
#include <OpenGL/gl3.h>
#pragma clang diagnostic pop

#include <glm/glm.hpp>

#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtx/transform.hpp>

#include <glm/gtc/quaternion.hpp>
#include <glm/gtx/quaternion.hpp>

#include <iostream>
#include <fstream>
#include <ctime>
#include <vector>

#endif
