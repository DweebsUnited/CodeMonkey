#include "bitmap_image.hpp"
#include <iostream>

#define IDX( x, y ) (( y * width + x ) * 3)

#define LIMIT 256
#define MAGLIMIT 4.0

int main()
{
	const int height = 2160;
	const int width = 3840;

	const int halfHigh = height / 2;
	const int halfWide = width / 2;

	const double xCenter = -1.37266;
	const double yCenter = -0.08333;

	const double scale = 0.025;

	const double yMin = yCenter - scale;
	const double yMax = yCenter + scale;
	const double xMin = xCenter - scale;
	const double xMax = xCenter + scale;

	const double pixH = ( yMax - yMin ) / height;
	const double pixW = ( xMax - xMin ) / width;

	bitmap_image img( width, height );
	if( !img ) {
		std::cout << "Couldn't open image" << std::endl;
		return -1;
	}

	double real, imag, treal, timag, treal2, timag2;

	unsigned char * data = img.data_;
	//bgr
	for( int i = 0; i < height; ++i ) {
		imag = yMin + i * pixH;
		for( int j = 0; j < width; ++j ) {
			real = xMin + j * pixW;

			treal = real;
			timag = imag;

			treal2 = treal * treal;
			timag2 = timag * timag;

			int cyclesCount = 0;
			for( cyclesCount = 0; ( cyclesCount < LIMIT ) && ( ( treal2 + timag2 ) < MAGLIMIT ); ++cyclesCount ) {
				timag = 2 * treal * timag + imag;
				treal = treal2 - timag2 + real;
				treal2 = treal * treal;
				timag2 = timag * timag;
			}

			if( cyclesCount == LIMIT )
				cyclesCount = 0;
			else
				cyclesCount = 255 - cyclesCount;

			data[ IDX( j, i ) + 0 ] = cyclesCount;
			data[ IDX( j, i ) + 1 ] = cyclesCount;
			data[ IDX( j, i ) + 2 ] = cyclesCount;
		}
	}

	img.save_image( "test.bmp" );
    return 0;
}
