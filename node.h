#pragma once
/*********************************************************************************//**
* @file node.h
* Several types of Node, all serving a different purpose
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/

#include <iostream>
#include <cstdint>
#include <vector>
#include <algorithm>
#include <memory>

#include "stdlib.h"

namespace CodeMonkey {
namespace DataStructs {

template <class LinkPayloadType, class NodePayloadType>
class Node {

    typedef Node<LinkPayloadType, NodePayloadType> _Node;

public:
    uint32_t id;

    class Link {
    public:
        Link( _Node * target, LinkPayloadType & payload ) : target( target ), payload( payload ) { };
        _Node * target;
        LinkPayloadType payload;
    };

    Node( uint32_t id ) : id( id ) { };
    Node( uint32_t id, NodePayloadType & payload ) : id( id ), payload( payload ) { };

    NodePayloadType payload;

    std::vector<Link> links; // List of links

    void printOut( ) {

        std::cout << "Node id: " << this->id << std::endl;
        std::cout << "  Payload: " << this->payload << std::endl;

        for( Link & l : this->links ) {
            std::cout << "    Link to id: " << l.target->id << std::endl;
            std::cout << "      Payload: " << l.payload << std::endl;
        }

    };

    void addLink( _Node * target, LinkPayloadType & payload ) {
        if( std::find_if( this->links.begin( ), this->links.end( ), [ target ]( Link & l ) { return target == l.target; } ) == this->links.end( ) )
            this->links.push_back( Link( target, payload ) );
    };

    void removeLink( _Node * target ) {
        this->links.erase( std::remove_if( this->links.begin( ), this->links.end( ), [ target ]( Link & l ) { return target == l.target; } ), this->links.end( ) );
    };

    void removeAllLinks( ) {
        for( Link & l : this->links ) {
            l.target->removeLink( this );
        }
    }

};

class BareEmptyNode {

    typedef BareEmptyNode _Node;

public:
    uint32_t id;

    BareEmptyNode( uint32_t id ) : id( id ) { };

    std::vector<_Node *> links; // List of links

    void printOut( ) {

        std::cout << "Node id: " << this->id << std::endl;

        for( _Node * l : this->links ) {
            std::cout << "    Link to id: " << l->id << std::endl;
        }

    };

    void addLink( _Node * target ) {
        if( std::find_if( this->links.begin( ), this->links.end( ), [ target ]( _Node * l ) { return target == l; } ) == this->links.end( ) )
            this->links.push_back( target );
    };

    void removeLink( _Node * target ) {
        this->links.erase( std::remove_if( this->links.begin( ), this->links.end( ), [ target ]( _Node * l ) { return target == l; } ), this->links.end( ) );
    };

};

};
};