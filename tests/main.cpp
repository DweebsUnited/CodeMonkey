#include "main.h"

int main( int argc, char ** argv ) {

    // Test that the basics of the graph are working
    std::cout << "Running graph test" << std::endl;
    graphTest( );
    std::cout << "Graph test done" << std::endl << std::endl;

    // Run djikstras algorithm on a graph
    // Returns a graph with data on shortest path from a given node
    std::cout << "Running Djikstra test" << std::endl;
    djikstraTest( );
    std::cout << "Djikstra test done" << std::endl;

    // Run Grid tests
    std::cout << "Running Genetic test" << std::endl;
    geneticTest( );
    std::cout << "Genetic test done" << std::endl;

#ifdef _WIN32
    getchar( );
#endif

};
