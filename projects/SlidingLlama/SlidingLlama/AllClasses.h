#pragma once

#define _USE_MATH_DEFINES
#include <cmath>

#include <fstream>
#include <iostream>

#include <vector>
#include <random>
#include <functional>
#include <ctime>
#include <complex>

class Point {
private:
    std::complex<float> coord = std::complex<float>( 0, 0 );
    float z = 0;

    int id;

    std::vector<Point *> links;

public:
    Point( );

    void setID( int id );
    void addLink( Point * p );

    void set( float x, float y );
    void set( float z );

    // Get Z
    float get( );
    // Get X
    float getX( );
    // Get Y
    float getY( );

    static float triArea( Point * a, Point * b, Point * c );

    float springForce( );
};

class PointFactory {
private:
    int guid = 0;

    std::default_random_engine gen;
    std::uniform_real_distribution<float> radDist;
    std::uniform_real_distribution<float> magDist;

    const int NUM_RIM = 64;
    int rimGenned = 0;

public:
    PointFactory( );
    Point * make( );
    Point * makeRim( );
    bool hasRim( );
};