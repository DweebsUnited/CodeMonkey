#pragma once
/*********************************************************************************//**
* @file node.h
* Several types of Nodes, getting progressively more complicated
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/

/*************************************************************************************
* Includes
*************************************************************************************/
#include <iostream>
#include <cstdint>
#include <vector>
#include <algorithm>
#include <memory>

#include "stdlib.h"


/*************************************************************************************
* Namespaces
*************************************************************************************/
namespace CodeMonkey {
namespace DataStructs {


/*************************************************************************************
* Classes
*************************************************************************************/
/*********************************************************************************//**
* Node with templated link payload, and node payload
*
* The payloads are really just templated member variables of the respective
*   classes. I find it most useful to use structs, but I'm fairly certain classes
*   would pass just fine as well. For usage examples, see: Graph Algorithm::djikstra
*************************************************************************************/
template <class LinkPayloadType, class NodePayloadType>
class Node {

    /** Typedefs are wonderful, they save a ton of typing */
    typedef Node<LinkPayloadType, NodePayloadType> _Node;

public:
    /*****************************************************************************//**
    * Class to represent a link between two nodes
    *
    * Templates come in handy here, giving us a user definable member variable. Be
    *   wary, using too big a data structure as a payload could cause issues if you
    *   are creating and removing many links.
    *
    * TODO: Make this a struct and generator function instead of a class.
    *********************************************************************************/
    class Link {

    public:
        /*************************************************************************//**
        * Constructor doesn't do anything
        *
        * @param    [in]    target  Pointer to target node
        * @param    [in]    payload Link payload to save off
        *****************************************************************************/
        Link( _Node * target, LinkPayloadType & payload ) : target( target ), payload( payload ) { };

        /** Target node this points at      */
        _Node * target;

        /** User typed data variable        */
        LinkPayloadType payload;

    };

    /** This node's id                      */
    uint32_t id;

    /** Payload, user typed data variable   */
    NodePayloadType payload;

    /** List of all outgoing links this node has */
    std::vector<Link> links;


    /*****************************************************************************//**
    * Constructor doesn't do anything
    *********************************************************************************/
    Node( ) { };

    /*****************************************************************************//**
    * Constructor still doesn't do anything
    *
    * @param    [in]    id  ID of this node
    *********************************************************************************/
    Node( uint32_t id ) : id( id ) { };

    /*****************************************************************************//**
    * OH WAIT! Nope, still does nothing
    *
    * @param    [in]    id      ID of the node
    * @param    [in]    payload Node data to save
    *********************************************************************************/
    Node( uint32_t id, NodePayloadType & payload ) : id( id ), payload( payload ) { };

    /*****************************************************************************//**
    * Print out data, by piping payload right to std::cout
    *********************************************************************************/
    void printOut( ) {

        // Print id and payload
        std::cout << "Node id: " << this->id << std::endl;
        std::cout << "  Payload: " << this->payload << std::endl;

        // Then print all the links
        for( Link & l : this->links ) {
            std::cout << "    Link to id: " << l.target->id << std::endl;
            std::cout << "      Payload: " << l.payload << std::endl;
        }

    };

    /*****************************************************************************//**
    * Add a new outward pointing link to this node, target may not know we exist
    *
    * @param    [in]    target
    * @param    [in]    payload Node data to save
    *********************************************************************************/
    // TODO: take shared ptr to link payload instead
    //   Would be better for large payloads, overkill for just ints
    void addLink( _Node * target, LinkPayloadType & payload ) {

        // If we are not already linked to that target
        if( std::find_if( this->links.begin( ), this->links.end( ), [ target ]( Link & l ) { return target == l.target; } ) == this->links.end( ) )
            // Add a link with the given payload
            this->links.push_back( Link( target, payload ) );
        // TODO: else update the link's payload

    };

    /*****************************************************************************//**
    * Remove a link from this node, unlinking it from the target
    *
    * @param    [in]    target  Target to remove a link to
    *********************************************************************************/
    void removeLink( _Node * target ) {

        // Remove and erase idiom
        this->links.erase( std::remove_if( this->links.begin( ), this->links.end( ), [ target ]( Link & l ) { return target == l.target; } ), this->links.end( ) );

    };

};

//TODO: BareNode, EmptyNode, SingleTargetNode, SingleTargetBare, ...

/*********************************************************************************//**
* The most basic of nodes
*
* This is the most simple a node can be, linking to other nodes, but carrying no
*   data about anything. Bare means it has no link payloads, Empty that it has no
*   node payload either.
*************************************************************************************/
class BareEmptyNode {

    /** This doesn't do much here, but it keeps the formatting consistent */
    typedef BareEmptyNode _Node;

public:

    /** This node's id                      */
    uint32_t id;

    /** List of all outgoing links this node has */
    std::vector<_Node *> links;


    /*****************************************************************************//**
    * Constructor doesn't do anything
    *********************************************************************************/
    BareEmptyNode( ) { };

    /*****************************************************************************//**
    * Constructor still doesn't do anything
    *
    * @param    [in]    id  ID of this node
    *********************************************************************************/
    BareEmptyNode( uint32_t id ) : id( id ) { };

    /*****************************************************************************//**
    * Print out data to std::cout
    *********************************************************************************/
    void printOut( ) {

        std::cout << "Node id: " << this->id << std::endl;

        for( _Node * l : this->links ) {
            std::cout << "    Link to id: " << l->id << std::endl;
        }

    };

    /*****************************************************************************//**
    * Add a new outward pointing link to this node, target may not know we exist
    *
    * @param    [in]    target  Target to point at
    *********************************************************************************/
    void addLink( _Node * target ) {

        // If it doesnt exist already
        if( std::find_if( this->links.begin( ), this->links.end( ), [ target ]( _Node * l ) { return target == l; } ) == this->links.end( ) )
            // Add it to the list of links
            this->links.push_back( target );

    };

    /*****************************************************************************//**
    * Remove a link to another node
    *
    * @param    [in]    target  Target to no longer point at
    *********************************************************************************/
    void removeLink( _Node * target ) {

        // Remove erase idiom
        this->links.erase( std::remove_if( this->links.begin( ), this->links.end( ), [ target ]( _Node * l ) { return target == l; } ), this->links.end( ) );

    };

};

};
};
