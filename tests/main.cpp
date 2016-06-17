#include "main.h"

#include "bitmap_image.hpp"

#include <random>
#include <chrono>

void scratchBMP( );

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
    threadsafeTest( );
    std::cout << "Threadsafe test completed" << std::endl << std::endl;

#ifdef _WIN32
    getchar( );
#endif

};

void scratchBMP( ) {

    bitmap_image bmp( 250, 250 );
    bmp.set_all_channels( 255 );

    //plasma( bmp ... );

    image_drawer draw( bmp );

    std::default_random_engine gen;
    // TODO: Add guards on drawing out of bounds
    std::uniform_int_distribution<uint16_t> dist( 0, 250 );
    std::uniform_int_distribution<uint16_t> color( 0, 255 );
    std::uniform_real_distribution<float> a( 0.0, 0.15 );

    draw.pen_width( 1 );

    gen.seed( std::chrono::high_resolution_clock::now( ).time_since_epoch( ).count( ) );

    std::chrono::high_resolution_clock::time_point t1 = std::chrono::high_resolution_clock::now( );
    for( uint32_t i = 0; i < 100000; ++i ) {
        draw.pen_color( color( gen ), color( gen ), color( gen ) );
        draw.line_segment_transparent( dist( gen ), dist( gen ), dist( gen ), dist( gen ), a( gen ) );
    }
    double timetook = std::chrono::duration_cast<std::chrono::duration<double>>( std::chrono::high_resolution_clock::now( ) - t1 ).count( );

    std::cout << "It took: " << timetook << " seconds to draw 100000 transparent lines." << std::endl;

    bmp.save_image( "SingleLine.bmp" );

};
