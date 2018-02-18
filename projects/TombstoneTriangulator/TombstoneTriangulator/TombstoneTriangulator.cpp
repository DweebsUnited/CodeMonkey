#include <iostream>
#include <fstream>
#include <cstdio>

#include "json.hpp"

#include <CGAL/Exact_predicates_inexact_constructions_kernel.h>

#include <CGAL/Projection_traits_xy_3.h>
#include <CGAL/Projection_traits_yz_3.h>
#include <CGAL/Projection_traits_xz_3.h>

#include <CGAL/Triangulation_vertex_base_with_info_2.h>
#include <CGAL/Triangulation_vertex_base_with_info_3.h>

#include <CGAL/Delaunay_triangulation_2.h>
#include <CGAL/Delaunay_triangulation_3.h>


typedef CGAL::Exact_predicates_inexact_constructions_kernel K;

typedef CGAL::Projection_traits_xy_3<K>  Pxy;
typedef CGAL::Projection_traits_yz_3<K>  Pyz;
typedef CGAL::Projection_traits_xz_3<K>  Pzx;

typedef CGAL::Triangulation_vertex_base_with_info_2<int, Pxy> Vbi2xy;
typedef CGAL::Triangulation_vertex_base_with_info_2<int, Pyz> Vbi2yz;
typedef CGAL::Triangulation_vertex_base_with_info_2<int, Pzx> Vbi2zx;

typedef CGAL::Triangulation_data_structure_2<Vbi2xy> Tds2xy;
typedef CGAL::Triangulation_data_structure_2<Vbi2yz> Tds2yz;
typedef CGAL::Triangulation_data_structure_2<Vbi2zx> Tds2zx;

typedef CGAL::Delaunay_triangulation_2<Pxy, Tds2xy> Delaunay2xy;
typedef CGAL::Delaunay_triangulation_2<Pyz, Tds2yz> Delaunay2yz;
typedef CGAL::Delaunay_triangulation_2<Pzx, Tds2zx> Delaunay2zx;

typedef CGAL::Triangulation_vertex_base_with_info_3<int, K> Vbi3;
typedef CGAL::Triangulation_data_structure_3<Vbi3> Tds3;
typedef CGAL::Delaunay_triangulation_3<K, Tds3> Delaunay3;

typedef K::Point_3   DTPoint;
typedef K::Vector_3  Vector;

// What projection method to use

enum class DelaunayMethod {

    XY,
    YZ,
    ZX,
    ThreeD

};


// Data classes

class Point {
public:

    int id;
    double x, y, z;
    bool hasZ;

    Point( ) { };
    Point( int id, double x, double y, double z, bool hasZ ) : id( id ), x( x ), y( y ), z( z ), hasZ( hasZ ) { };

};

class Edge {
public:

    int a, b;

    Edge( ) { };
    Edge( int a, int b ) : a( a ), b( b ) { };

};

class Face {
public:

    int a, b, c;
    double area;

    Face( ) { };
    Face( int a, int b, int c, double area ) : a( a ), b( b ), c( c ), area( area ) { };

};


// Data class json conversions

// Point

void to_json( nlohmann::json& j, const Point& p ) {

    j = nlohmann::json { { "id", p.id }, { "x", p.x }, { "y", p.y } };
    
    if( p.hasZ )
        j[ "z" ] = p.z;

}

// Edge

void to_json( nlohmann::json& j, const Edge& e ) {
    j = nlohmann::json { { "a", e.a }, { "b", e.b } };
}

// Face

void to_json( nlohmann::json& j, const Face& f ) {
    j = nlohmann::json { { "a", f.a }, { "b", f.b }, { "c", f.c }, { "area", f.area } };
}


// Load points from a file into points

void loadPoints( std::string fname, std::vector<Point> & points, DelaunayMethod method ) {

    // Input format:
    //   [
    //     { id: int, x: double, y: double [, z: double ] }
    //   ]


    std::cout << "Loading points from " << fname << std::endl;


    // File setup and verification

    std::cout << "Opening file: ";

    std::fstream fin;
    fin.open( fname, std::ios::in );

    if( !fin.is_open( ) ) {
        std::cerr << "Cannot open input: " << fname << std::endl;
        // Command invoked cannot execute
        exit( 126 );
    }

    std::cout << "[ GOOD ]" << std::endl;


    // Load file, catch parsing errors

    std::cout << "Parsing file: ";

    nlohmann::json pts;
    try {
        fin >> pts;
    } catch( nlohmann::detail::parse_error e ) {
        std::cerr << e.what( ) << std::endl;
        exit( 1 );
    }

    std::cout << "[ GOOD ]" << std::endl;


    // Check data format

    std::cout << "Checking data format: ";

    if( pts.type( ) != nlohmann::json::value_t::array ) {
        std::cerr << "Input file does not contain single list!" << std::endl;
        exit( 1 );
    }

    std::cout << "[ GOOD ]" << std::endl;


    // Convert array to vector of points

    std::cout << "Converting points from file";

    for( nlohmann::json & pt : pts ) {

        
        // Pull attributes from the point

        nlohmann::json id = pt[ "id" ];
        nlohmann::json x = pt[ "x" ];
        nlohmann::json y = pt[ "y" ];
        nlohmann::json z = pt[ "z" ];


        // Handle bad object

        if( id == nullptr || x == nullptr || y == nullptr || ( method != DelaunayMethod::XY && z == nullptr ) ) {
            
            std::cerr << "Object missing an attribute!" << std::endl;
            
            std::cerr << "ID: " << ( id != nullptr ? std::to_string( id.get<int>( )   ) : "MISSING" ) << std::endl;
            std::cerr << "X:  " << (  x != nullptr ? std::to_string( x.get<double>( ) ) : "MISSING" ) << std::endl;
            std::cerr << "Y:  " << (  y != nullptr ? std::to_string( y.get<double>( ) ) : "MISSING" ) << std::endl;
            std::cerr << "Z:  " << (  z != nullptr ? std::to_string( z.get<double>( ) ) :
                ( method != DelaunayMethod::XY ? "MISSING" : "" ) )
                << std::endl;

            exit( 1 );
        
        }


        // Create a point

        Point p;
        p.id = id.get<int>( );
        p.x  = x.get<double>( );
        p.y  = y.get<double>( );

        // Guaranteed to have a z if the method requires it, so we don't need to check what method we have
        if( z != nullptr ) {
            p.hasZ = true;
            p.z = z.get<double>( );
        } else {
            p.hasZ = false;
            p.z = 0; // So the XY projection doesn't get screwed up
        }


        // Add it to the list

        points.push_back( p );


        // I love these progress bars, even though they don't tell you much

        if( points.size( ) % 10 == 0 )
            std::cout << '.';

    }


    // Final status print

    std::cout << std::endl;
    std::cout << "Loaded " << points.size( ) << " points" << std::endl;

}


// Write results of a triangulation out to a file

void writeTriangulation( std::string fname, std::vector<Point> & points, std::vector<Edge> & edges, std::vector<Face> & faces ) {


    // File setup and verification

    std::cout << "Opening file: ";

    remove( fname.c_str( ) );

    std::fstream fout;
    fout.open( fname, std::ios::out );

    if( !fout.is_open( ) ) {
        std::cerr << "Cannot open output: " << fname << std::endl;
        // Command invoked cannot execute
        exit( 126 );
    }

    std::cout << "[ GOOD ]" << std::endl;


    // Set up json objects for output

    std::cout << "Converting objects to json: ";

    nlohmann::json output;

    output[ "points" ] = points;
    output[ "edges" ] = edges;
    output[ "faces" ] = faces;

    std::cout << "[ GOOD ]" << std::endl;


    // Write to file

    std::cout << "Writing json to file: ";

    fout << output << std::endl;

    std::cout << "[ GOOD ]" << std::endl;

}


// 2d Delaunay dispatcher

template <typename Delaunay>
void delaunay( std::vector<Point> & points, std::vector<Edge> & edges, std::vector<Face> & faces ) {


    // Variables!

    Delaunay dt;
    Delaunay::Vertex_handle vh;


    // Construct the triangulation

    std::cout << "Constructing triangulation";
    int pCnt = 0;

    for( Point & pt : points ) {

        // Add point to delaunay
        vh = dt.insert( DTPoint( pt.x, pt.y, pt.z ) );

        // Include point index
        vh->info( ) = pt.id;

        if( ++pCnt % 10 == 0 )
            std::cout << '.';

    }

    std::cout << std::endl;


    // Save edges

    std::cout << "Reading edges: ";

    for( Delaunay::Finite_edges_iterator pti = dt.finite_edges_begin( ); pti != dt.finite_edges_end( ); ++pti ) {

        Delaunay::Face& f = *( pti->first );
        int i = pti->second;
        Delaunay::Vertex_handle va = f.vertex( f.cw( i ) );
        Delaunay::Vertex_handle vb = f.vertex( f.ccw( i ) );

        edges.push_back( Edge( va->info( ), vb->info( ) ) );

    }

    std::cout << "[ GOOD ]" << std::endl;


    // Save faces

    std::cout << "Reading faces: ";

    for( Delaunay::Finite_faces_iterator fti = dt.finite_faces_begin( ); fti != dt.finite_faces_end( ); ++fti ) {

        Face f;

        f.a = fti->vertex( 0 )->info( );
        f.b = fti->vertex( fti->ccw( 0 ) )->info( );
        f.c = fti->vertex( fti->cw( 0 ) )->info( );

        Vector a = fti->vertex( fti->ccw( 0 ) )->point( ) - fti->vertex( 0 )->point( );
        Vector b = fti->vertex( fti->cw( 0 ) )->point( ) - fti->vertex( 0 )->point( );
        Vector cross = CGAL::cross_product( a, b );

        f.area = 0.5 * std::sqrt( cross.squared_length( ) );

        faces.push_back( f );

    }

    std::cout << "[ GOOD ]" << std::endl;

}


// 3d Delaunay
// Type names are different for 3d delaunay ( facet instead of face :S )

void delaunay3d( std::vector<Point> & points, std::vector<Edge> & edges, std::vector<Face> & faces ) {


    // Variables!

    Delaunay3 dt;
    Delaunay3::Vertex_handle vh;


    // Construct the triangulation

    std::cout << "Constructing triangulation";
    int pCnt = 0;

    for( Point & pt : points ) {

        // Add point to delaunay
        vh = dt.insert( DTPoint( pt.x, pt.y, pt.z ) );

        // Include point index
        vh->info( ) = pt.id;

        if( ++pCnt % 10 == 0 )
            std::cout << '.';

    }

    std::cout << std::endl;


    // Save edges

    std::cout << "Reading edges: ";

    for( Delaunay3::Finite_edges_iterator pti = dt.finite_edges_begin( ); pti != dt.finite_edges_end( ); ++pti ) {

        Delaunay3::Cell & c = *( pti->first );
        Delaunay3::Vertex_handle va = c.vertex( pti->second );
        Delaunay3::Vertex_handle vb = c.vertex( pti->third );

        edges.push_back( Edge( va->info( ), vb->info( ) ) );

    }

    std::cout << "[ GOOD ]" << std::endl;


    // Save faces

    std::cout << "Reading faces: ";

    for( Delaunay3::Finite_facets_iterator fti = dt.finite_facets_begin( ); fti != dt.finite_facets_end( ); ++fti ) {

        Delaunay3::Cell & c = *( fti->first );

        Face f;

        // Wrap clockwise with normal facing out from the cell this facet came from
        f.a = c.vertex( ( fti->second + 2 ) % 4 )->info( );
        f.b = c.vertex( ( fti->second + 1 ) % 4 )->info( );
        f.c = c.vertex( ( fti->second + 3 ) % 4 )->info( );

        Vector a = c.vertex( ( fti->second + 1 ) % 4 )->point( ) - c.vertex( ( fti->second + 2 ) % 4 )->point( );
        Vector b = c.vertex( ( fti->second + 3 ) % 4 )->point( ) - c.vertex( ( fti->second + 2 ) % 4 )->point( );
        Vector cross = CGAL::cross_product( a, b );

        f.area = 0.5 * std::sqrt( cross.squared_length( ) );

        faces.push_back( f );

    }

    std::cout << "[ GOOD ]" << std::endl;

}


// Main

void main( int argc, char ** argv ) {


    // Variables!

    std::vector<Point> points;
    std::vector<Edge> edges;
    std::vector<Face> faces;

    DelaunayMethod method;


    // Input validation

    // We expect to be given an input and output file
    if( argc < 3 || argc > 4 ) {
        std::cerr << "Format: TombstoneTriangulator [xyzt] {input.json} {output.json}" << std::endl;
        // Command invoked cannot execute
        exit( 126 );
    }

    // Read the flag if present

    if( argc == 4 ) {

        switch( argv[ 1 ][ 0 ] ) {

            case 'x':
                method = DelaunayMethod::YZ;
                std::cout << "Using YZ projection method" << std::endl;
                break;
            case 'y':
                method = DelaunayMethod::ZX;
                std::cout << "Using ZX projection method" << std::endl;
                break;
            case 'z':
                method = DelaunayMethod::XY;
                std::cout << "Using XY projection method" << std::endl;
                break;
            case 't':
                method = DelaunayMethod::ThreeD;
                std::cout << "Using ThreeD method" << std::endl;
                break;

        }

    } else // Default is to project along Z
        method = DelaunayMethod::XY;


    // Load the points from the input file

    loadPoints( argv[ argc - 2 ], points, method );


    // Delaunay

    switch( method ) {

        case DelaunayMethod::XY:
            delaunay<Delaunay2xy>( points, edges, faces );
            break;
        case DelaunayMethod::YZ:
            delaunay<Delaunay2yz>( points, edges, faces );
            break;
        case DelaunayMethod::ZX:
            delaunay<Delaunay2zx>( points, edges, faces );
            break;
        case DelaunayMethod::ThreeD:
            delaunay3d( points, edges, faces );
            break;

    }


    // Write out triangulation

    writeTriangulation( argv[ argc - 1 ], points, edges, faces );

    
    // Done!

    std::cout << "Done!" << std::endl;

    exit( 0 );

}