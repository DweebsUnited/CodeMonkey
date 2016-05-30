#pragma once
/*********************************************************************************//**
* @file codeMonkey.h
* @brief Namespace definitions aplenty, no real code here
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/

/*********************************************************************************//**
* The base namespace for the whole library
*
* Everything fits inside here, just to make it easier to include and use in a project
*************************************************************************************/
namespace CodeMonkey {

/*********************************************************************************//**
* This is the home of all the data structures that have been implemented
*************************************************************************************/
namespace DataStructs { };

/*********************************************************************************//**
* Contains all the algorithms on data structures, and a collection of other
*   random but useful scripts.
*************************************************************************************/
namespace Algorithm { };

/*********************************************************************************//**
* The largest namespace, containing a complete rendering engine
*************************************************************************************/
namespace Engine { };

/*********************************************************************************//**
* Geometric objects, useful outside the Engine, but primarily used therein
*************************************************************************************/
namespace Shape { };

}