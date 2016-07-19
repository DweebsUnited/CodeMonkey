#pragma once
/*********************************************************************************//**
* @file djikstra.h
* An implementation of Djiksta's algorithm, run on a Graph
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/

/*************************************************************************************
* Includes
*************************************************************************************/
#include <queue>

#include "Graph.h"


/*************************************************************************************
* Namespaces
*************************************************************************************/
namespace CodeMonkey {
namespace Algorithm {


/*************************************************************************************
* Data structures
*************************************************************************************/
/** A structure to hold data while running the algorithm */
struct djikstraData {
    /** Whether this node has been fully dealt with */
    bool used;
    /** Whether this node has an infite distance */
    bool isInf;
    /** Distance from start node to here along the shortest path */
    uint32_t distance;
};


/*************************************************************************************
* Typedefs
*************************************************************************************/
/** Clean up the Graph type for this algorithm */
typedef DataStructs::Graph<uint32_t, djikstraData> DjikstraGraph;
/** Clean up the Node type for this algorithm */
typedef DataStructs::Node<uint32_t, djikstraData> DjikstraNode;


/*************************************************************************************
* Functions
*************************************************************************************/
/*********************************************************************************//**
* Run Djikstra's algorithm on a Graph with a given starting node
*
* Djikstra's finds the shortest distance to each node from a given starting point.
*   While it does not save the shortest path (that's a different algorithm), knowing
*   How far two nodes are from each other along the graph is nonetheless useful.
*
* @param    [in]    g       Graph to run Djikstra on
* @param    [in]    startID ID of the node to start the algorithm with
*************************************************************************************/
void djikstra( DjikstraGraph & g, uint32_t startID );

/*********************************************************************************//**
* Print the results of a run of Djikstra's algorithm
*
* @param    [in]    g       Graph that Djikstra was run on
*************************************************************************************/
void djikstraPrint( DjikstraGraph & g );

};
};
