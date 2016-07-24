#pragma once

#include <complex>
#include <vector>

#define PI 3.141592653589793f

namespace CodeMonkey {
namespace Fourier {

std::complex<float> cWave( float freq, float t, float phase = 0.0f ) {
    return std::polar( 1.0f, 2.0f * PI * freq * ( t + phase ) );
}

void window( std::vector<std::complex<float>>& data, float ( *windowFunc )( uint32_t, uint32_t ) ) {
    for( uint32_t n = 0; n < data.size( ); ++n ) {
        data[ n ] *= windowFunc( n, data.size( ) );
    }
};

float triangleWindow( uint32_t n, uint32_t N ) {
    return 1.0f - abs( ( n - ( N - 1 ) / 2.0f ) / ( N / 2.0f ) );
};

float welchWindow( uint32_t n, uint32_t N ) {
    float t = ( ( n - ( N - 1 ) / 2.0f ) / ( ( N - 1 ) / 2.0f ) );
    return 1.0f - t * t;
};

float hammingWindow( uint32_t n, uint32_t N ) {
    return 0.54f - 0.46f * cos( 2 * PI * n / ( N - 1 ) );
};

std::vector<std::complex<float>> DFT( std::vector<std::complex<float>>& xin ) {

    uint32_t N = (uint32_t)xin.size( );
    
    std::vector<std::complex<float>> xout;

    for( uint32_t k = 0; k < N; ++k ) {
        std::complex<float> accum;
        for( uint32_t n = 0; n < N; ++n ) {
            accum += xin[ n ] * std::exp( std::complex<float>( 0.0f, -2.0f * PI * k * n / N ) );
        }
        xout.push_back( 1.0f / N * accum );
    }

    return xout;

};

std::vector<std::complex<float>> iDFT( std::vector<std::complex<float>>& xin ) {

    uint32_t N = (uint32_t)xin.size( );

    std::vector<std::complex<float>> xout( N );

    for( uint32_t k = 0; k < N; ++k ) {
        std::complex<float> accum;
        for( uint32_t n = 0; n < N; ++n ) {
            accum += xin[ n ] * std::exp( std::complex<float>( 0.0f, 2.0f * PI * k * n / N ) );
        }
        xout.push_back( accum );
    }

    return xout;

};

void _FFT( std::complex<float> * xin, std::complex<float> * xout, uint32_t N, uint32_t s ) {

    if( s >= N )
        xout[ 0 ] = xin[ 0 ];
    else {

        _FFT( xin,     xout,     N, 2 * s );
        _FFT( xin + s, xout + s, N, 2 * s );

        for( uint32_t k = 0; k < N; k += 2 * s ) {

            std::complex<float> t = xout[ k + s ] * std::exp( std::complex<float>( 0.0f, -1.0f * PI * k / N ) );

            xin[ k / 2 ] = xout[ k ] + t;
            xin[ ( k + N ) / 2 ] = xout[ k ] - t;

        }

    }

};

std::vector<std::complex<float>> FFT( std::vector<std::complex<float>> xin ) {

    uint32_t N = xin.size( );

    std::vector<std::complex<float>> xout( xin );

    _FFT( &xin[ 0 ], &xout[ 0 ], N, 1 );

    return xout;

};

void _iFFT( std::complex<float> * xin, std::complex<float> * xout, uint32_t N, uint32_t s ) {

    if( s >= N )
        xout[ 0 ] = xin[ 0 ];
    else {

        _FFT( xin, xout, N, 2 * s );
        _FFT( xin + s, xout + s, N, 2 * s );

        for( uint32_t k = 0; k < N; k += 2 * s ) {

            std::complex<float> t = xout[ k + s ] * std::exp( std::complex<float>( 0.0f, PI * k / N ) );

            xin[ k / 2 ] = xout[ k ] + t;
            xin[ ( k + N ) / 2 ] = xout[ k ] - t;

        }

    }

};

std::vector<std::complex<float>> iFFT( std::vector<std::complex<float>> xin ) {

    uint32_t N = xin.size( );

    std::vector<std::complex<float>> xout( xin );

    _FFT( &xin[ 0 ], &xout[ 0 ], N, 1 );

    return xout;

};

};
};