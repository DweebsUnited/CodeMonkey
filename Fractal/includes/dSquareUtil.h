#pragma once

#include <random>

static std::default_random_engine dist;
static std::uniform_real_distribution<float> range( -1.0, 1.0 );

static double persistance;

float sample( float * data, int j, int i ) {

    return data[ ( j & ( width - 1 ) ) + ( i & ( height - 1 ) ) * width ];

}

void setSample( float * data, int j, int i, float value ) {

    data[ ( j & ( width - 1 ) ) + ( i & ( height - 1 ) ) * width ] = value;

}

void sampleSquare( float * data, int j, int i, int size, float value ) {

    int hs = size / 2;

    // a     b
    //
    //    x
    //
    // c     d

    float a = sample( data, j - hs, i - hs );
    float b = sample( data, j + hs, i - hs );
    float c = sample( data, j - hs, i + hs );
    float d = sample( data, j + hs, i + hs );

    setSample( data, j, i, ( ( a + b + c + d ) / 4.0 ) + value );

}

void sampleDiamond( float * data, int j, int i, int size, float value ) {

    int hs = size / 2;

    //   c
    //
    //a  x  b
    //
    //   d

    double a = sample( data, j - hs, i );
    double b = sample( data, j + hs, i );
    double c = sample( data, j, i - hs );
    double d = sample( data, j, i + hs );

    setSample( data, j, i, ( ( a + b + c + d ) / 4.0 ) + value );

}

void DiamondSquareIter( float * data, int stepsize, double scale ) {

    int halfstep = stepsize / 2;

    for( int i = halfstep; i < height + halfstep; i += stepsize )
        for( int j = halfstep; j < width + halfstep; j += stepsize )
            sampleSquare( data, j, i, stepsize, range( dist ) * scale );

    for( int i = 0; i < height; i += stepsize ) {
        for( int j = 0; j < width; j += stepsize ) {

            sampleDiamond( data, j + halfstep, i, stepsize, range( dist ) * scale);
            sampleDiamond( data, j, i + halfstep, stepsize, range( dist ) * scale);

        }
    }

}

void DiamondSquare( float * data, int startStep, double startScale, double iPersistance ) {

    persistance = iPersistance;

    while( startStep > 1 ) {

        DiamondSquareIter( data, startStep, startScale );

        startStep /= 2;
        startScale *= persistance;

    }

}
