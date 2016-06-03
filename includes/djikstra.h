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
void djikstra( DjikstraGraph & g, uint32_t startID ) {

    // Init all node data to default

    // This is done a little backwards
    // We need a pointer later, so we just use it here as well, rather than clutter the stack
    djikstraData * dData = new djikstraData { false, true, 0 };

    for( DjikstraNode * n : g.nodes ) {
        n->payload = ( *dData ); // This is the weird part about doing it this way
    }

    // Never forget
    delete dData;


    // Proper Djikstras algorithm begin

    // List of nodes we have encountered, but not finished with
    std::queue<DjikstraNode *> checkList;
    // Seeded with starting node
    checkList.push( g.findNodeByID( startID ) );

    // Distance to current node, saved here rather than remade each loop
    // Not sure if compiler will do this optimization automatically, so I do it explicitly
    uint32_t currDist;

    // Node currently being checked
    DjikstraNode * n;

    // While we still have nodes to visit
    while( checkList.size( ) > 0 ) {

        // Get the next node from the queue
        n = checkList.front( );
        checkList.pop( );

        // Get the distance to the current node
        currDist = n->payload.distance;

        for( DjikstraNode::Link & l : n->links ) {
            dData = &l.target->payload;

            // Node has already been checked fully, go to next link
            if( dData->used )
                continue;
            else {
                // Add target to the check queue
                checkList.push( l.target );

                // If target has not been seen (isInf) or the distance to it from current node is less than its previous best
                if( l.target->payload.isInf || currDist + l.payload < dData->distance ) {

                    // If it merely hadn't been seen before, save it as seen
                    // TODO: Redo this with an Infinity value, instead of another variable
                    if( dData->isInf )
                        dData->isInf = false;
                    // Update the distance
                    dData->distance = currDist + l.payload;

                }

            }

        }

        // Mark the current node as done
        n->payload.used = true;

    }

};

/*********************************************************************************//**
* Print the results of a run of Djikstra's algorithm
*
* @param    [in]    g       Graph that Djikstra was run on
*************************************************************************************/
void djikstraPrint( DjikstraGraph & g ) {
    std::cout << "Number of nodes: " << g.nodes.size( ) << std::endl;

    for( DjikstraNode * n : g.nodes )
        std::cout << "Node ID: " << n->id << std::endl << "    Distance from starting node: " << n->payload.distance << std::endl;
};

};
};