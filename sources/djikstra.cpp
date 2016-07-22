/*********************************************************************************//**
* @file djikstra.cpp
* Implementations of the various djikstra methods
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/

/*************************************************************************************
* Includes
*************************************************************************************/
#include "djikstra.h"


/*************************************************************************************
* Run Djikstra's algorithm on a Graph with a given starting node
*************************************************************************************/
void CodeMonkey::Algorithm::djikstra( CodeMonkey::Algorithm::DjikstraGraph & g, uint32_t startID ) {

    // Init all node data to default

    // This is done a little backwards
    // We need a pointer later, so we just use it here as well, rather than clutter the stack
    CodeMonkey::Algorithm::djikstraData * dData = new CodeMonkey::Algorithm::djikstraData { false, true, 0 };

    for( CodeMonkey::Algorithm::DjikstraNode * n : g.nodes ) {
        n->payload = ( *dData ); // This is the weird part about doing it this way
    }

    // Never forget
    delete dData;


    // Proper Djikstras algorithm begin

    // List of nodes we have encountered, but not finished with
    std::queue<CodeMonkey::Algorithm::DjikstraNode *> checkList;
    // Seeded with starting node
    checkList.push( g.findNodeByID( startID ) );

    // Distance to current node, saved here rather than remade each loop
    // Not sure if compiler will do this optimization automatically, so I do it explicitly
    uint32_t currDist;

    // Node currently being checked
    CodeMonkey::Algorithm::DjikstraNode * n;

    // While we still have nodes to visit
    while( checkList.size( ) > 0 ) {

        // Get the next node from the queue
        n = checkList.front( );
        checkList.pop( );

        // Get the distance to the current node
        currDist = n->payload.distance;

        for( CodeMonkey::Algorithm::DjikstraNode::Link & l : n->links ) {
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

/*************************************************************************************
* Print the results of a run of Djikstra's algorithm
*************************************************************************************/
void CodeMonkey::Algorithm::djikstraPrint( CodeMonkey::Algorithm::DjikstraGraph & g ) {
    std::cout << "Number of nodes: " << g.nodes.size( ) << std::endl;

    for( CodeMonkey::Algorithm::DjikstraNode * n : g.nodes )
        std::cout << "Node ID: " << n->id << std::endl << "    Distance from starting node: " << n->payload.distance << std::endl;
}
