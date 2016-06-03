#pragma once
/*********************************************************************************//**
* @file graph.h
* Graph classes, and some handy typedefs for using them
*
* TODO: Extend Graph to have several variations, much like Node
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/

/*************************************************************************************
* Includes
*************************************************************************************/
#include "Node.h"


/*************************************************************************************
* Namespaces
*************************************************************************************/
namespace CodeMonkey {
namespace DataStructs {


/*************************************************************************************
* Classes
*************************************************************************************/
/*********************************************************************************//**
* Graph class with templated link and node payloads
*
* Provides basic memory management of nodes and links, but no more. Algorithms that
*   use it can add meaningful data to each. The payloads are just member variables.
*   To understand, see: Node
*************************************************************************************/
template <class LinkPayloadType, class NodePayloadType>
class Graph {
    /** Helper typedef to save a TON of typing */
    typedef Node<LinkPayloadType, NodePayloadType> _Node;

    /** Helper typedef to save a TON of typing */
    typedef Node<LinkPayloadType, NodePayloadType> _Node;

public:
    /*****************************************************************************//**
    * Directional controls for links
    *********************************************************************************/
    enum class LinkDirection : uint8_t {
        /** Link from A to B                */
        FOREWARD = 0x01,
        /** Link from B to A                */
        BACKWARD = 0x02,
        /** Link both directions            */
        BOTH = FOREWARD | BACKWARD
    };

private:
    /** Binary AND for scoped enum LinkDirection */
    inline friend uint8_t operator&( const LinkDirection & lhs, const LinkDirection & rhs ) { return static_cast<uint8_t>( lhs ) & static_cast<uint8_t>( rhs ); };

    /** Node ID generator, incremented after each access, NEVER decrement */
    uint32_t nodeIDGen = 0;

public:
    /** List of nodes for which this Graph is responsible */
    std::vector<_Node *> nodes;


    /*****************************************************************************//**
    * Generates new nodes in the Graph
    *
    * This keeps ownership of the nodes within the Graph object. Mainly because I
    *   don't want to deal with the user controlling nodes, and scoping, etc. Will
    *   return the ID of the first new node generated.
    *
    * @param    [in]    num Number of new nodes to generate
    * @return               ID of first newly generated node
    *********************************************************************************/
    uint32_t makeNewNodes( uint16_t num ) {

        // Save first new ID for returning later
        uint32_t ret = this->nodeIDGen;

        // Use num destructively, because "HEY! It's on the stack!"
        for( ; num > 0; --num )
            // Push back new nodes
            this->nodes.push_back( new _Node( this->nodeIDGen++ ) );

        // Return saved value from earlier
        return ret;

    };

    /*****************************************************************************//**
    * Deletes nodes from the Graph
    *
    * If we're going to add them, we'll want to delete them. No error checking
    *   performed. This means if the node doesn't exist, it fails quietly.
    *
    * @param    [in]    idRem   ID of the node to delete
    * @return                   True if a node was deleted, False otherwise
    *********************************************************************************/
    bool remove( uint32_t idRem ) {

        _Node * nRem = findNodeByID( idRem );

        // For any node linking to that ID, remove that link
        std::for_each( this->nodes.begin( ), this->nodes.end( ), [ nRem ]( _Node * n ) { n->removeLink( nRem ); } );

        // Use remove_if to remove node with ID in question, deallocate if we find it
        typename std::vector<_Node *>::iterator newEnd =
            std::remove_if( this->nodes.begin( ), this->nodes.end( ), [ idRem ]( _Node * n ) { if( n->id == idRem ) { delete n; return true; } else return false; } );

        // Save return value, because once we erase we can't tell if anything was actually removed
        bool ret = newEnd != this->nodes.end( );

        // Do the erasure
        if( ret )
            this->nodes.erase( newEnd, this->nodes.end( ) );

        // And return
        return ret;

    };

    /*****************************************************************************//**
    * Return a pointer to a node (if it exists), given its ID
    *
    * This is the heart of much of the flexibility of the Graph. Because nodes can
    *   be removed, the ID's likely will not map to indices directly. Instead this
    *   function allows us to have a fragmented node list, with minimal impact on
    *   the rest of the functionality. Returns NULL if node not found.
    *
    * @param    [in]    id  ID of node to find
    * @return               Pointer to the node if it exists, NULL otherwise
    *********************************************************************************/
    _Node * findNodeByID( uint32_t id ) {

        // I dislike cheating with auto, but I also don't feel like making this line any longer than it is already
        auto ret = std::find_if( this->nodes.begin( ), this->nodes.end( ), [ id ]( _Node * n ) { return n->id == id; } );

        // There was no node found
        if( ret == this->nodes.end( ) )
            return NULL;

        // Deref and return (iterator -> pointer)
        return *ret;

    };

    /*****************************************************************************//**
    * Link two nodes together by IDs
    *
    * This has undergone the most revision in this whole class. Given two IDs, and
    *   a directional enum, add the appropriate links to the nodes. Makes great use
    *   of findNodeByID, and indeed was the reason it was written.
    *
    * @param    [in]    idA ID of node A to link
    * @param    [in]    idB ID of node B to link
    * @param    [in]    pay Link payload
    * @param    [in]    dir Directional control enum telling how to link nodes
    * @return               True if nodes were linked, False otherwise
    *********************************************************************************/
    // TODO: Carry class list of links and their directions, would make deleting a node much easier
    //   Right now we search every single link in the graph on removal
    //   That would allow us to edit only the nodes with known links
    //   Use map of id->Node* instead for even better speed?

    //   Assume no desync ever? Can I make it fault tolerant somehow?
    //     If a node gets changed, can I update the routing table to reflect that?
    //     I'm smelling networking ;)
    bool addLink( uint32_t idA, uint32_t idB, LinkPayloadType pay, LinkDirection dir = LinkDirection::BOTH ) {

        // Get pointers to nodes
        _Node * aPtr = findNodeByID( idA );
        _Node * bPtr = findNodeByID( idB );

        // If either was bad, we must fail
        if( aPtr != NULL && bPtr != NULL ) {

            // Add links, return
            if( dir & LinkDirection::FOREWARD ) aPtr->addLink( bPtr, pay );
            if( dir & LinkDirection::BACKWARD ) bPtr->addLink( aPtr, pay );
            return true;

        } else
            return false;

    };

    /*****************************************************************************//**
    * Unlink two nodes given their IDs
    *
    * The opposite of addLink, has exactly opposite functionality as well. Again,
    *   only limited error checking.
    *
    * @param    [in]    idA ID of node A to unlink
    * @param    [in]    idB ID of node B to unlink
    * @param    [in]    dir Directional control enum telling how to unlink nodes
    * @return               True if nodes were linked, False otherwise
    *********************************************************************************/
    bool removeLink( uint32_t idA, uint32_t idB, LinkDirection dir = LinkDirection::BOTH ) {

        // Get pointer to nodes
        _Node * aPtr = findNodeByID( idA );
        _Node * bPtr = findNodeByID( idB );

        // If either was bad, we must fail as well
        if( aPtr != NULL && bPtr != NULL ) {

            // Add links, return
            if( dir & LinkDirection::FOREWARD ) aPtr->removeLink( bPtr );
            if( dir & LinkDirection::BACKWARD ) aPtr->removeLink( bPtr );
            return true;

        } else
            return false;

    };

    /*****************************************************************************//**
    * Print a Graph
    *
    * It's a basic print, but it's a print. Lists all nodes, their payloads, all
    *   their links, and all their links' payloads. Be wary of using this for any
    *   but a basic graph, the output pipes all the payloads directly to std::cout.
    *   This may cause errors, it may only result in wonky output, I don't know
    *   which.
    *********************************************************************************/
    void printOut( ) {

        std::cout << "Number of nodes: " << nodes.size( ) << std::endl;

        for( _Node * n : nodes )
            n->printOut( );

    };

};


/*************************************************************************************
* Extras
*************************************************************************************/
/** Provide a basic Weighted Graph type */
typedef Graph<uint32_t, uint8_t> WeightedGraph;

};
};
