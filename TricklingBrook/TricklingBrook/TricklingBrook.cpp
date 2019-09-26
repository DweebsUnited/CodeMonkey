// TricklingBrook.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

// Sample period:	ps	= var		= 1 / fs	= 1 s
// Sample rate:		fs	= var		= 1 / ps	= 1 Hz
// Block size:		N	= var
//
// Frame size:		T	= N ps
//
// Bandwidth:		fM	= fs / 2				= 0.5 Hz	= 2 s period
// Effective bins:	SL = N / 2
// Freq res:		df	= fM / SL	= fs / N

#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <vector>
#include <filesystem>

#include "Windows.h"
#include "bitmap_image.hpp"

const double PI = 3.141592653589793238460;

int winLens[] = { 16, 32, 64, 128, 256, 512, 1024, 2048 };

std::vector<std::shared_ptr<windows::Window>> winFunc {
	std::shared_ptr<windows::Window>( new windows::rect ),
	std::shared_ptr<windows::Window>( new windows::bartlett ),
	std::shared_ptr<windows::Window>( new windows::hanning ),
	std::shared_ptr<windows::Window>( new windows::hamming ),
	std::shared_ptr<windows::Window>( new windows::blackman ),
	std::shared_ptr<windows::Window>( new windows::eblackman )
};

int main( ) {

	// Load data into bigass matrix
	// "(PDH-CSV 4.0) (W. Europe Daylight Time)(-120)"
	// "\\SELBY\LogicalDisk(E:)\% Disk Time"
	// "\\SELBY\Memory\Available Bytes"
	// "\\SELBY\Memory\Cache Bytes"
	// "\\SELBY\Memory\Cache Faults/sec"
	// "\\SELBY\Memory\Page Faults/sec"
	// "\\SELBY\PhysicalDisk(1 E:)\% Disk Time"
	// "\\SELBY\Processor(_Total)\% Processor Time"
	// "\\SELBY\Processor Performance(PPM_Processor_0)\% of Maximum Frequency"
	// "\\SELBY\Processor Performance(PPM_Processor_1)\% of Maximum Frequency"
	// "\\SELBY\Processor Performance(PPM_Processor_2)\% of Maximum Frequency"
	// "\\SELBY\Processor Performance(PPM_Processor_3)\% of Maximum Frequency"
	// "\\SELBY\Processor Performance(PPM_Processor_4)\% of Maximum Frequency"
	// "\\SELBY\Processor Performance(PPM_Processor_5)\% of Maximum Frequency"
	// "\\SELBY\Processor Performance(PPM_Processor_6)\% of Maximum Frequency"
	// "\\SELBY\Processor Performance(PPM_Processor_7)\% of Maximum Frequency"
	// "\\SELBY\Processor Performance(PPM_Processor_8)\% of Maximum Frequency"
	// "\\SELBY\Processor Performance(PPM_Processor_9)\% of Maximum Frequency"
	// "\\SELBY\Processor Performance(PPM_Processor_10)\% of Maximum Frequency"
	// "\\SELBY\Processor Performance(PPM_Processor_11)\% of Maximum Frequency"
	std::filesystem::path dataSrc( "C:\\Users\\eosburn\\source\\repos\\TricklingBrook\\TricklingBrook\\DataCollector01.csv" );
	// std::filesystem::path dataSrc( "C:\\Users\\eosburn\\source\\repos\\TricklingBrook\\TricklingBrook\\DataCollector02.csv" );

	std::ifstream dfin( dataSrc );

	std::vector<std::string> dataTit {
		"%LogicalDiskTime",
		"MemoryAvailable",
		"MemoryCache",
		"MemoryCacheFaultRate",
		"MemoryPageFaultRate",
		"%PhysicalDiskTime",
		"%ProcessorTime",
		"0%MaxFreq",
		"1%MaxFreq",
		"2%MaxFreq",
		"3%MaxFreq",
		"4%MaxFreq",
		"5%MaxFreq",
		"6%MaxFreq",
		"7%MaxFreq",
		"8%MaxFreq",
		"9%MaxFreq",
		"10%MaxFreq",
		"11%MaxFreq",
		"12%MaxFreq"
	};
	std::vector<std::vector<double>> dataMat;
	for( std::string _ : dataTit )
		dataMat.push_back( std::vector<double>( ) );

	std::string line;
	// Strip header line
	std::getline( dfin, line );


	while( std::getline( dfin, line ) ) {

		// Tokenize, pull relevant columns
		std::istringstream tokenizer( line );
		std::string token;
		// Pull off time
		std::getline( tokenizer, token, ',' );

		int cdx = 0;
		while( std::getline( tokenizer, token, ',' ) ) {
			try {
				dataMat[ cdx ].push_back( std::stod( token.substr( 1, token.length( ) - 2 ) ) );
			} catch( const std::exception& e ) {
				std::cout << e.what( ) << std::endl;
				std::cout << cdx << ": " << token << " : " << line << std::endl;
				throw;
			}
			++cdx;

		}

	}


	// Set up folders
	std::filesystem::path outdir( "C:\\Users\\eosburn\\source\\repos\\TricklingBrook\\TricklingBrook\\output" );
	for( std::shared_ptr<windows::Window> func : winFunc )
		std::filesystem::create_directories( outdir / func->name( ) );


	// Static DFT attributes
	double ps = 1;
	double fs = 1 / ps;
	double fM = 0.5;
    

	// For each window length
	for( int N : winLens ) {

		// Precompute window functions
		std::vector<std::vector<double>> windows;
		for( std::shared_ptr<windows::Window> window : winFunc ) {

			std::vector<double> winData;
			for( int ndx = 0; ndx < N; ++ndx )
				winData.push_back( window->func( ndx, N - 1 ) );

			windows.push_back( std::move( winData ) );

		}


		// Compute DFT attributes
		int SL = N / 2;
		double df = fs / N;
		int nWin = dataMat[ 0 ].size( ) - ( N - 1 );

		std::cout << "Block size: " << N << std::endl;
		std::cout << "  Bandwidth  : " << fM << std::endl;
		std::cout << "  Freq res   : " << df << std::endl;
		std::cout << "  Spec lines : " << SL << std::endl;
		std::cout << "  Num windows: " << nWin << std::endl;


		// Set up target arrays
		// windowFuncs x cols x nWin
		std::vector<std::vector<std::vector<double>>> dftTarget;
		
		for( int wdx = 0; wdx < winFunc.size( ); ++wdx ) {

			std::vector<std::vector<double>> col;

			for( int cdx = 0; cdx < dataMat.size( ); ++cdx )
				col.push_back( std::vector<double>( ) );

			dftTarget.push_back( col );
		
		}


		// For windows
		for( int sdx = 0; sdx < nWin; ++sdx ) {

			// For each column
			for( int cdx = 0; cdx < dataMat.size( ); ++cdx ) {

				// Slice
				// Window
				// DFT

			}

		}


		// Draw some images

		for( std::shared_ptr<windows::Window> window : winFunc ) {

			bitmap_image img( nWin, SL );
			image_drawer imgD( img );
			
			imgD.pen_width( 1 );

			// TODO

		}

	}

}



void test18( ) {
	{
		bitmap_image image( 1000, 180 );
		image_drawer draw( image );
		const rgb_t* colormap[ 9 ] = {
									 autumn_colormap,
									 copper_colormap,
									   gray_colormap,
										hot_colormap,
										hsv_colormap,
										jet_colormap,
									  prism_colormap,
										vga_colormap,
									   yarg_colormap
		};

		for( unsigned int i = 0; i < image.width( ); ++i ) {
			for( unsigned int j = 0; j < 9; ++j ) {
				draw.pen_color( colormap[ j ][ i ].red, colormap[ j ][ i ].green, colormap[ j ][ i ].blue );
				draw.vertical_line_segment( j * 20, ( j + 1 ) * 20, i );
			}
		}

		image.save_image( "test18_color_maps.bmp" );
	}

	{
		bitmap_image image( 1000, 500 );
		image_drawer draw( image );

		std::size_t palette_colormap_size = sizeof( palette_colormap ) / sizeof( rgb_t );
		std::size_t bar_width = image.width( ) / palette_colormap_size;

		for( std::size_t i = 0; i < palette_colormap_size; ++i ) {
			for( std::size_t j = 0; j < bar_width; ++j ) {
				draw.pen_color( palette_colormap[ i ].red, palette_colormap[ i ].green, palette_colormap[ i ].blue );
				draw.vertical_line_segment( 0, image.height( ), static_cast<int>( i * bar_width + j ) );
			}
		}

		image.save_image( "test18_palette_colormap.bmp" );
	}
}