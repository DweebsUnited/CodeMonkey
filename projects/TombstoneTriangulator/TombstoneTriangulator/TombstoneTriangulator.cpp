#include <iostream>
#include <fstream>
#include <cstdio>

#include "json.hpp"

#include <CGAL/Exact_predicates_inexact_constructions_kernel.h>
#include <CGAL/Projection_traits_xy_3.h>
#include <CGAL/Delaunay_triangulation_2.h>
#include <CGAL/Triangulation_vertex_base_with_info_2.h>

typedef CGAL::Exact_predicates_inexact_constructions_kernel K;

typedef CGAL::Triangulation_vertex_base_with_info_2<int, K> Vb;
typedef CGAL::Triangulation_data_structure_2<Vb> Tds;

typedef CGAL::Delaunay_triangulation_2<K, Tds> Delaunay;

typedef K::Point_2   DTPoint;

void main( int argc, char ** argv ) {

    // Input validation

    // We expect to be given an input and output file
    if( argc != 3 ) {
        std::cerr << "Format: TombstoneTriangulator {input.json} {output.json}" << std::endl;
        // Command invoked cannot execute
        exit( 126 );
    }

    std::fstream fin, fout;
    fin.open( argv[ 1 ], std::ios::in );
    remove( argv[ 2 ] );
    fout.open( argv[ 2 ], std::ios::out );

    if( !fin.is_open( ) ) {
        std::cerr << "Cannot open input: " << argv[ 1 ] << std::endl;
        // Command invoked cannot execute
        exit( 126 );
    }
    if( !fout.is_open( ) ) {
        std::cerr << "Cannot open output: " << argv[ 1 ] << std::endl;
        // Command invoked cannot execute
        exit( 126 );
    }


    // JSON setup

    // Input format:
    //   [
    //     { id: int, x: float, y: float }
    //   ]

    nlohmann::json pts;
    try {
        fin >> pts;
    } catch( nlohmann::detail::parse_error e ) {
        std::cerr << e.what( ) << std::endl;
        exit( 1 );
    }

    if( pts.type( ) != nlohmann::json::value_t::array ) {
        std::cerr << "Input file does not contain single list!" << std::endl;
        exit( 1 );
    }


    // Delaunay setup

    Delaunay dt;
    Delaunay::Vertex_handle vh;


    // Delaunay construction

    for( nlohmann::json & pt : pts ) {

        nlohmann::json id = pt[ "id" ];
        nlohmann::json x = pt[ "x" ];
        nlohmann::json y = pt[ "y" ];

        // Handle bad object
        if( id == nullptr || x == nullptr || y == nullptr ) {
            std::cerr << "Object missing an attribute! Sorry this isn't more helpful." << std::endl;
            exit( 1 );
        }

        std::cout << "Point " << id.get<int>( ) << ": " << x.get<float>( ) << ", " << y.get<float>( ) << std::endl;

        // Add point to delaunay
        vh = dt.insert( DTPoint( x.get<float>( ), y.get<float>( ) ) );

        // Include point index
        vh->info( ) = id.get<int>( );

    }


    // Edges
    
    nlohmann::json edges = nlohmann::json::array( );

    for( Delaunay::Finite_edges_iterator pti = dt.finite_edges_begin( ); pti != dt.finite_edges_end( ); ++pti ) {

        Delaunay::Face& f = *( pti->first );
        int i = pti->second;
        Delaunay::Vertex_handle va = f.vertex( f.cw( i ) );
        Delaunay::Vertex_handle vb = f.vertex( f.ccw( i ) );

        nlohmann::json edge;
        edge[ "a" ] = va->info( );
        edge[ "b" ] = vb->info( );

        edges.push_back( edge );

    }


    // Faces

    nlohmann::json faces = nlohmann::json::array( );

    for( Delaunay::Finite_faces_iterator fti = dt.finite_faces_begin( ); fti != dt.finite_faces_end( ); ++fti ) {

        nlohmann::json face;
        face[ "a" ] = fti->vertex( 0 )->info( );
        face[ "b" ] = fti->vertex( 1 )->info( );
        face[ "c" ] = fti->vertex( 2 )->info( );

        faces.push_back( face );

    }

    
    // Output

    nlohmann::json output;

    output[ "points" ] = pts;
    output[ "edges" ] = edges;
    output[ "faces" ] = faces;

    fout << output << std::endl;

    exit( 0 );

}