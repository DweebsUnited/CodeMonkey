#include "main.h"

#include "graph.h"

using namespace CodeMonkey;

void graphTest( ) {

    DataStructs::WeightedGraph g;

    g.makeNewNodes( 7 );

    g.addLink( 0, 1, 25 );
    g.addLink( 1, 5, 10 );
    g.addLink( 1, 2, 10 );
    g.addLink( 5, 2, 10 );
    g.addLink( 5, 4, 10 );
    g.addLink( 2, 3, 10 );
    g.addLink( 4, 3, 10 );
    g.addLink( 4, 6, 10 );

    g.printOut( );

    g.remove( 0 );

    g.printOut( );

    g.removeLink( 4, 5, DataStructs::WeightedGraph::LinkDirection::FOREWARD );

    g.printOut( );

};
