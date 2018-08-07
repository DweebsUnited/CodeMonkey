const int NUM_PTS = 256;

#include "json.hpp"

#include "AllClasses.h"
#include "PLYExporter.h"

#include <cstdio>

void generateModel( nlohmann::json & edges, std::vector<Point *> & points, int frame ) {

    // Generate a model
    PLYExporter ply;

    // For each edge
    for( nlohmann::json & e : edges ) {

        nlohmann::json ea = e[ "a" ];
        nlohmann::json eb = e[ "b" ];

        Point * a = points[ ea.get<int>( ) ];
        Point * b = points[ eb.get<int>( ) ];

        // Need a "right" vector = globalUp x across
        float ux = 0;
        float uy = 0;
        float uz = 1.0;

        // Vector from a to b
        float ax = b->x - a->x;
        float ay = b->y - a->y;
        float az = b->z - a->z;

        float amag = std::sqrt( ax * ax + ay * ay + az * az );
        ax /= amag;
        ay /= amag;
        az /= amag;

        // Cross product, computed "at" a
        float rx = -uz * ay;
        float ry = uz * ax;

        float rmag = std::sqrt( rx * rx + ry * ry );
        rx *= 0.01f / rmag;
        ry *= 0.01f / rmag;

        // Need 4 vertices and 2 faces
        int fV = ply.addVertex( a->x + rx, a->y + ry, a->z, 255, 0, 0 );
        ply.addVertex( a->x - rx, a->y - ry, a->z, 255, 0, 0 );
        ply.addVertex( b->x + rx, b->y + ry, b->z, 255, 0, 0 );
        ply.addVertex( b->x - rx, b->y - ry, b->z, 255, 0, 0 );

        ply.addFace( fV + 0, fV + 1, fV + 2 );
        ply.addFace( fV + 1, fV + 3, fV + 2 );

    }

    ply.exportFile( std::to_string( frame ) + std::string( ".ply" ) );

}

void generateFaceDump( nlohmann::json triangulation, std::vector<Point *> points ) {

    // Generate a model
    PLYExporter ply;

    for( Point * p : points ) {

        ply.addVertex( p->x, p->y, p->z, 0, 0, 0 );

    }

    for( nlohmann::json & face : triangulation[ "faces" ] ) {

        ply.addFace( face[ "a" ].get<int>( ), face[ "b" ].get<int>( ), face[ "c" ].get<int>( ) );
    }

    ply.exportFile( "faceDump.ply" );

}

void main( ) {

    PointFactory * pf = new PointFactory( );

    std::vector<Point *> points;

    {
        nlohmann::json jsonPts = nlohmann::json::array( );

        Point * p;

        /*
        while( pf->hasRim( ) ) {

            // Add point to points list
            p = pf->makeRim( );
            points.push_back( p );

            nlohmann::json pt;
            pt[ "id" ] = idx++;
            pt[ "x" ] = p->x;
            pt[ "y" ] = p->y;

            jsonPts.push_back( pt );

        }
        */

        for( int pdx = 0; pdx < NUM_PTS; pdx++ ) {

            // Add point to points list
            p = pf->make( );
            points.push_back( p );

            nlohmann::json pt;
            pt[ "id" ] = p->id;
            pt[ "x" ] = p->x;
            pt[ "y" ] = p->y;
            pt[ "z" ] = p->z;

            jsonPts.push_back( pt );

        }

        remove( "pts.json" );
        std::fstream out;
        out.open( "pts.json", std::fstream::out );

        if( !out.is_open( ) ) {
            std::cerr << "Couldn't open pts.json to pass to triangulator" << std::endl;
            exit( 1 );
        }

        out << jsonPts << std::endl;

    }

    if( system( "TombstoneTriangulator.exe t pts.json triangulation.json" ) != 0 ) {
        std::cerr << "Couldn't run triangulation" << std::endl;
        exit( 1 );
    }

    std::fstream fin;
    fin.open( "triangulation.json", std::ios::in );

    if( !fin.is_open( ) ) {
        std::cerr << "Couldn't open triangulation output" << std::endl;
        exit( 1 );
    }

    nlohmann::json triangulation;
    fin >> triangulation;
    fin.close( );

    // Go through and link points
    // TODO

    // Generate a model
    generateFaceDump( triangulation, points );

}