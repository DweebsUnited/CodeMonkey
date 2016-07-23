#include "main.h"

#include "fourier.h"

#include <vector>
#include <algorithm>
#include <fstream>

void dftTest( ) {

    uint32_t N = 1024;
    uint32_t sps = 2400000;

    const uint32_t nBins = 64;

    std::vector<std::complex<float>> tData;
    for( uint32_t t = 0; t < N; ++t )
        tData.push_back( 10.0f * CodeMonkey::Fourier::cWave( 1800000, 1.0f / sps * t ) + 5.0f * CodeMonkey::Fourier::cWave( 1200000, 1.0f / sps * t ) + 5.0f * CodeMonkey::Fourier::cWave( 600000, 1.0f / sps * t ) );

    std::vector<std::complex<float>> dData = CodeMonkey::Fourier::DFT( tData );

    // Downsum to 64 bins, normalize
    // Print out stars for each bin

    std::vector<float> bins( nBins );

    for( uint32_t i = 0; i < nBins; ++i ) {
        std::complex<float> accum;
        for( uint32_t j = 0; j < N / nBins; ++j )
            accum += dData[ i * N / nBins + j ];
        bins[ i ] = std::norm( accum );
    }

    float max = *std::max_element( bins.begin( ), bins.end( ) );

    std::for_each( bins.begin( ), bins.end( ), [ max ]( float& elem ) { elem /= max; } );

    for( uint32_t m = 0; m < 10; ++m ) {
        for( uint32_t i = 0; i < nBins; ++i ) {
            if( bins[ i ] > ( 9 - m ) / 10.0f )
                std::cout << "*";
            else
                std::cout << " ";
        }
        std::cout << std::endl;
    }

};

void fftTest( ) {

    uint32_t N = 1024;
    uint32_t sps = 2400000;

    const uint32_t nBins = 64;

    std::vector<std::complex<float>> tData;
    for( uint32_t t = 0; t < N; ++t )
        tData.push_back( 10.0f * CodeMonkey::Fourier::cWave( 1800000, 1.0f / sps * t ) + 5.0f * CodeMonkey::Fourier::cWave( 1200000, 1.0f / sps * t ) + 5.0f * CodeMonkey::Fourier::cWave( 600000, 1.0f / sps * t ) );

    std::vector<std::complex<float>> dData = CodeMonkey::Fourier::FFT( tData );

    // Downsum to 64 bins, normalize
    // Print out stars for each bin

    std::vector<float> bins( nBins );

    for( uint32_t i = 0; i < nBins; ++i ) {
        std::complex<float> accum;
        for( uint32_t j = 0; j < N / nBins; ++j )
            accum += dData[ i * N / nBins + j ];
        bins[ i ] = std::norm( accum );
    }

    float max = *std::max_element( bins.begin( ), bins.end( ) );

    std::for_each( bins.begin( ), bins.end( ), [ max ]( float& elem ) { elem /= max; } );

    for( uint32_t m = 0; m < 10; ++m ) {
        for( uint32_t i = 0; i < nBins; ++i ) {
            if( bins[ i ] >( 9 - m ) / 10.0f )
                std::cout << "*";
            else
                std::cout << " ";
        }
        std::cout << std::endl;
    }

};