#include "main.h"

#include "bitmap_image.hpp"

#include <random>
#include <chrono>

int main( int argc, char ** argv ) {

    // Test that the basics of the graph are working
    std::cout << "Running graph test" << std::endl;
    //graphTest( );
    std::cout << "Graph test done" << std::endl << std::endl;

    // Run djikstras algorithm on a graph
    // Returns a graph with data on shortest path from a given node
    std::cout << "Running Djikstra test" << std::endl;
    //djikstraTest( );
    std::cout << "Djikstra test done" << std::endl << std::endl;

    // Run Genetic tests
    std::cout << "Running Genetic test" << std::endl;
    //geneticTest( );
    std::cout << "Genetic test done" << std::endl << std::endl;

    // Run Grid tests
    std::cout << "Running Grid test" << std::endl;
    //gridTest( );
    std::cout << "Grid test done" << std::endl << std::endl;

    // Test some of the threadsafe stuff
    std::cout << "Testing some threadsafe shtuff" << std::endl;
    //threadsafeTest( );
    std::cout << "Threadsafe test completed" << std::endl << std::endl;

    // Test the logger
    std::cout << "Testing logging. This might take a while" << std::endl;
    logTest( );
    std::cout << "Logger test completed" << std::endl << std::endl;

#ifdef _WIN32
    getchar( );
#endif

};
