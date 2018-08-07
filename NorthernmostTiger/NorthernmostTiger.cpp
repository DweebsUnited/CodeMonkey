#include <iostream>
#include <fstream>
#include <ctime>
#include <utility>
#include <random>
#include <vector>

// Here's the noise library
#include "../../resources/FastNoise-master/FastNoise.h"

// Includes for defining the Voronoi diagram adaptor
#include <CGAL/Exact_predicates_inexact_constructions_kernel.h>
#include <CGAL/Delaunay_triangulation_2.h>
#include <CGAL/Voronoi_diagram_2.h>
#include <CGAL/Delaunay_triangulation_adaptation_traits_2.h>
#include <CGAL/Delaunay_triangulation_adaptation_policies_2.h>

// Voronoi typedefs
typedef CGAL::Exact_predicates_inexact_constructions_kernel                  K;
typedef CGAL::Delaunay_triangulation_2<K>                                    DT;
typedef CGAL::Delaunay_triangulation_adaptation_traits_2<DT>                 AT;
typedef CGAL::Delaunay_triangulation_caching_degeneracy_removal_policy_2<DT> AP;
typedef CGAL::Voronoi_diagram_2<DT,AT,AP>                                    VD;

typedef AT::Site_2                    Site_2;
typedef VD::Bounded_faces_iterator    Bounded_faces_iterator;
typedef VD::Ccb_halfedge_circulator   Ccb_halfedge_circulator;

// Why is this not a standard definition?
#define PI 3.1415926535

// It's about to get loud baby!
FastNoise fn( time( NULL ) );

// RNG setup
std::default_random_engine eng;
std::uniform_real_distribution<float> dist( 0.0, 1.0 );
auto getRand = std::bind( dist, eng );

// Constants for point generation
const int N_PTS = 64;
const float A_SCALE = 256;


bool validateFace( Bounded_faces_iterator fIter ) {

    Ccb_halfedge_circulator he_start = fIter->ccb( );
    Ccb_halfedge_circulator he = he_start;

    do {

        float sx = he->source( )->point( ).x( );
        float sy = he->source( )->point( ).y( );

        float tx = he->target( )->point( ).x( );
        float ty = he->target( )->point( ).y( );


        if( ( sx * sx + sy * sy > 2.25 ) ||
            ( tx * tx + ty * ty > 2.25 ) )
            return false;

    } while( ++he != he_start );

    return true;

}

bool writeFace( std::ofstream & of, Bounded_faces_iterator fIter ) {

    Ccb_halfedge_circulator he_start = fIter->ccb( );
    Ccb_halfedge_circulator he = he_start;

    do {

        of << he->source( )->point( ).x( ) << "," << he->source( )->point( ).y( ) << "," << he->target( )->point( ).x( ) << "," << he->target( )->point( ).y( ) << std::endl;

    } while( ++he != he_start );

    return true;

}


int main( ) {

    // Generate n points in the space
    std::cout << "Making " << N_PTS << " points" << std::endl;

    std::vector<Site_2> points;
    for( int idx = 0; idx < N_PTS; ++idx ) {

        // std::cout << "Making point: " << idx << std::endl;

        Site_2 p;
        float acceptance, t, r;

        do {

            t = getRand( ) * 2 * PI;
            r = getRand( );

            p = Site_2( r * std::cos( t ), r * std::sin( t ) );

            // This is rough, since the noise technically has infinite domain...
            acceptance = std::abs( ( fn.GetValue( p.x( ) * A_SCALE, p.y( ) * A_SCALE ) + 1.0 ) / 2.0 );

        } while( getRand( ) < acceptance && getRand( ) > r * r );

        // std::cout << "Good!" << std::endl;

        points.push_back( p );

    }

    std::cout << "Writing out points list" << std::endl;

    {
        std::ofstream of( "voronoiPoints.csv" );
        of << "X,Y" << std::endl;
        for( int idx = 0; idx < N_PTS; ++idx )
            of << points[ idx ].x( ) << "," << points[ idx ].y( ) << std::endl;
        of.close( );
    }

    // Compute voronoi diagram of points
    std::cout << "Computing voronoi cells" << std::endl;

    VD vd;
    for( int idx = 0; idx < N_PTS; ++idx )
        vd.insert( points[ idx ] );

    // For debugging, output a csv with all the voronoi cell edges
    // Not the cleanest way, but it's easy enough to draw in a processing script

    // For each Face
    //   For each halfedge incident
    //     Output
    {
        std::ofstream of( "voronoiEdges.csv" );

        for( Bounded_faces_iterator fIter = vd.bounded_faces_begin( );
            fIter != vd.bounded_faces_end( );
            ++fIter ) {

            if( validateFace( fIter ) )
                writeFace( of, fIter );

        }

        of.close( );
    }

    // Generate heights for each cell
    // Low density areas are taller
    // High density is shorter
    // Pick high spot or two, fall off normally with distance?
    // Randomly offset all anyways


    // Generate obj model

    // Write out vertices like so:
    // Vtop, Vbot, V1top, V2top, V3top, ..., V1bot, V2bot, ...

    std::ofstream of( "voronoi.obj" );
    int vcoord = 1;

    for( Bounded_faces_iterator fIter = vd.bounded_faces_begin( );
        fIter != vd.bounded_faces_end( );
        ++fIter ) {

        // Validate face for sanity reasons
        if( ! validateFace( fIter ) )
            continue;

        // Height comes from the noise field?
        float h = ( fn.GetValue( fIter->dual( )->point( ).x( ) * A_SCALE, fIter->dual( )->point( ).y( ) * A_SCALE ) + 1.0 ) / 2.0;
        h = h * h * h;

        // Top and bottom vertex index, write em
        int tbV = vcoord;
        vcoord += 2;
        of << "v " << fIter->dual( )->point( ).x( ) << " " << fIter->dual( )->point( ).y( ) << " " << h << std::endl;
        of << "v " << fIter->dual( )->point( ).x( ) << " " << fIter->dual( )->point( ).y( ) << " " << "0.0" << std::endl;

        // First vertex coordinate
        int fV = vcoord;

        // We need to wak the edges
        Ccb_halfedge_circulator he_start = fIter->ccb( );
        Ccb_halfedge_circulator he = he_start;

        // Write them top then bottom
        do {

            of << "v "
                << he->source( )->point( ).x( ) << " "
                << he->source( )->point( ).y( ) << " "
                << h <<
                std::endl;

            of << "v "
                << he->source( )->point( ).x( ) << " "
                << he->source( )->point( ).y( ) << " "
                << "0.0"
                << std::endl;

            // Don't forget this
            vcoord += 2;

        } while( ++he != he_start );

        // Number of halfedge vertices
        int nV = ( vcoord - fV ) / 2;

        // Now we have to write the faces
        for( int i = 0; i < nV; ++i ) {

            // We need two side faces, a top tri, and a bottom tri

            // Handle wrapping on the last point
            int n = ( ( i + 1 ) == nV ? 0 : i + 1 );

            // Top face
            of << "f "
                << fV + 2 * n << " "        // i+1 top
                << tbV        << " "        // top
                << fV + 2 * i               // i top
                << std::endl;

            // Bottom face
            of << "f "
                << fV + 2 * i + 1 << " "    // i+1 bottom
                << tbV + 1        << " "    // bottom
                << fV + 2 * n + 1           // i bottom
                << std::endl;

            // Horizontal faces
            of << "f "
                << fV + 2 * n     << " "    // i+1 top
                << fV + 2 * i     << " "    // i top
                << fV + 2 * i + 1           // i bottom
                << std::endl;

            of << "f "
                << fV + 2 * n     << " "    // i+1 top
                << fV + 2 * i + 1 << " "    // i bottom
                << fV + 2 * n + 1           // i+1 bottom
                << std::endl;

        }

    }

    of.close( );

}
