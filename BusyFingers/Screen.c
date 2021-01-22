#include "Screen.h"

#include "assert.h"

#include "stdlib.h"
#include "string.h"
#include "stdio.h"

/*

DEBUG functions

These will be compiled out at a later time, do not rely on them

*/
screenBuf screen_alloc( ) {

	screenBuf sbuf = (screenBuf)calloc( SCREEN_WIDTH * SCREEN_HEIGH, sizeof( char ) );

	return sbuf;

}

void screen_print( screenBuf sbuf ) {

	printf( "\n" );

	for( int cdx = 0; cdx < SCREEN_WIDTH + 2; ++cdx ) {

		printf( "*" );

	}

	printf( "\n" );

	for( int rdx = 0; rdx < SCREEN_HEIGH; ++rdx ) {

		printf( "*" );

		for( int cdx = 0; cdx < SCREEN_WIDTH; ++cdx ) {

			printf( "%c", sbuf[ rdx * SCREEN_WIDTH + cdx ] );

		}

		printf( "*\n" );

	}

	for( int cdx = 0; cdx < SCREEN_WIDTH + 2; ++cdx ) {

		printf( "*" );

	}

	printf( "\n" );

}


/*

Following are valid functions!

*/

void screen_blank( screenBuf sbuf ) {

	// Blank buffer
	memset( sbuf, ' ', SCREEN_WIDTH * SCREEN_HEIGH );

}

void screen_blank_rect( screenBuf sbuf, int top, int bot, int left, int rght ) {

	// Go down the rows, blanking
	for( int rdx = top; rdx <= bot; ++rdx ) {

		memset( sbuf + rdx * SCREEN_WIDTH + top, ' ', rght - left );

	}

}

void screen_hline( screenBuf sbuf, char c, int rdx, int cdx, int len ) {

	// Bounary conditions
	assert( rdx >= 0 );
	assert( rdx < SCREEN_HEIGH );
	assert( cdx >= 0 );
	assert( cdx < SCREEN_WIDTH );
	assert( cdx + len <= SCREEN_WIDTH );

	for( int pdx = 0; pdx < len; ++pdx ) {

		sbuf[ rdx * SCREEN_WIDTH + cdx + pdx ] = c;

	}

}

void screen_vline( screenBuf sbuf, char c, int rdx, int cdx, int len ) {

	// Bounary conditions
	assert( rdx >= 0 );
	assert( rdx < SCREEN_HEIGH );
	assert( cdx >= 0 );
	assert( cdx < SCREEN_WIDTH );
	assert( rdx + len <= SCREEN_HEIGH );

	for( int pdx = 0; pdx < len; ++pdx ) {

		sbuf[ ( rdx + pdx ) * SCREEN_WIDTH + cdx ] = c;

	}

}

void screen_text( screenBuf sbuf, int rdx, int cdx, char * str ) {

	// Boundary conditions!
	assert( rdx >= 0 );
	assert( rdx < SCREEN_HEIGH );
	assert( cdx >= 0 );
	assert( cdx < SCREEN_WIDTH );
	assert( strlen( str ) > 0 );
	assert( cdx + ( strlen( str ) - 1 ) < SCREEN_WIDTH );

	int off = 0;
	while( *str != 0 ) {

		sbuf[ rdx * SCREEN_WIDTH + cdx + ( off++ ) ] = *( str++ );

	}

}

void screen_text_r( screenBuf sbuf, int rdx, int cdx, char * str ) {

	// Boundary conditions!
	assert( rdx >= 0 );
	assert( rdx < SCREEN_HEIGH );
	assert( cdx >= 0 );
	assert( cdx < SCREEN_WIDTH );
	assert( strlen( str ) > 0 );
	assert( cdx - ( strlen( str ) - 1 ) >= 0 );

	cdx -= strlen( str ) - 1;

	int off = 0;
	while( *str != 0 ) {

		sbuf[ rdx * SCREEN_WIDTH + cdx + ( off++ ) ] = *( str++ );

	}

}
