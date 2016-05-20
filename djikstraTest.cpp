#include "main.h"

#include "djikstra.h"

void djikstraTest( ) {

    CodeMonkey::Algorithm::DjikstraGraph g;

    g.makeNewNodes( 6 );

    g.addLink( 0, 1, 7 );
    g.addLink( 0, 2, 9 );
    g.addLink( 0, 5, 14 );

    g.addLink( 1, 2, 10 );
    g.addLink( 1, 3, 15 );

    g.addLink( 2, 3, 11 );
    g.addLink( 2, 5, 2 );
    
    g.addLink( 3, 4, 6 );
    
    g.addLink( 4, 5, 9 );

    //g.printOut( );

    CodeMonkey::Algorithm::djikstra( g, 0 );

    CodeMonkey::Algorithm::djikstraPrint( g );

}