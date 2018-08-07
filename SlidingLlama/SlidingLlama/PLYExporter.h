#pragma once
#include <fstream>
#include <iostream>

#include <string>
#include <vector>

class PLYExporter {
private:

    std::fstream out;

    int vertID = 0;

    std::vector<float> coords;
    std::vector<unsigned char> colors;

    std::vector<int> faces;

public:
    PLYExporter( );

    int addVertex( float x, float y, float z, unsigned char red, unsigned char green, unsigned char blue );
    void addFace( int v0, int v1, int v2 );

    bool exportFile( std::string fname );

private:
    bool begin( std::string fname );
    void end( );

    void writeHeader( );
    void writeVerts( );
    void writeFaces( );

};