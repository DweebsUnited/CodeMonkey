#define _USE_MATH_DEFINES
#include "PLYExporter.h"

#include <cmath>

// For remove
#include <cstdio>


PLYExporter::PLYExporter( ) {

    this->coords = std::vector<float>( );

}


int PLYExporter::addVertex( float x, float y, float z, unsigned char red, unsigned char green, unsigned char blue ) {

    // std::cout << "Vertex " << this->vertID << ": " << x << ", " << y << ", " << z << std::endl;

    this->coords.push_back( x );
    this->coords.push_back( y );
    this->coords.push_back( z );

    this->colors.push_back( red );
    this->colors.push_back( green );
    this->colors.push_back( blue );

    return this->vertID++;

}

void PLYExporter::addFace( int v0, int v1, int v2 ) {

    this->faces.push_back( v0 );
    this->faces.push_back( v1 );
    this->faces.push_back( v2 );

}


bool PLYExporter::exportFile( std::string fname ) {

    if( !this->begin( fname ) )
        return false;

    this->writeHeader( );

    this->writeVerts( );

    this->writeFaces( );

    this->end( );

    return true;

}

bool PLYExporter::begin( std::string fname ) {

    remove( fname.c_str( ) );
    this->out.open( fname, std::ios::out );

    return this->out.is_open( );

}

void PLYExporter::end( ) {

    this->out.close( );

}

void PLYExporter::writeHeader( ) {

    this->out << "ply" << std::endl
        << "format ascii 1.0" << std::endl
        << "element vertex " << this->coords.size( ) / 3 << std::endl
        << "property float x" << std::endl
        << "property float y" << std::endl
        << "property float z" << std::endl
        << "property uchar red" << std::endl
        << "property uchar green" << std::endl
        << "property uchar blue" << std::endl
        << "element face " << this->faces.size( ) / 3 << std::endl
        << "property list uchar int vertex_indices" << std::endl
        << "end_header" << std::endl;

}

void PLYExporter::writeVerts( ) {

    for( int i = 0; i < this->coords.size( ) / 3; ++i ) {

        this->out << this->coords[ i * 3 + 0 ] << " "
            << this->coords[ i * 3 + 1 ] << " "
            << this->coords[ i * 3 + 2 ] << " "
            << (int)this->colors[ i * 3 + 0 ] << " "
            << (int)this->colors[ i * 3 + 1 ] << " "
            << (int)this->colors[ i * 3 + 2 ] << std::endl;

    }

    this->vertID = 0;
    this->coords.empty( );
    this->colors.empty( );

}

void PLYExporter::writeFaces( ) {

    for( int i = 0; i < this->faces.size( ) / 3; ++i ) {

        this->out << "3 "
            << this->faces[ i * 3 + 0 ] << " "
            << this->faces[ i * 3 + 1 ] << " "
            << this->faces[ i * 3 + 2 ] << std::endl;

    }

    this->faces.empty( );

}