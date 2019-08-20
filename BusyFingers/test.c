#define DEBUG

#include "Screen.h"

screenBuf sbuf;

int main( ) {

	sbuf = screen_alloc( );

	screen_blank( sbuf );

	screen_hline( sbuf, '=', 1,                0, SCREEN_WIDTH );
	screen_hline( sbuf, '=', SCREEN_HEIGH - 2, 0, SCREEN_WIDTH );

	screen_text( sbuf, 0, 1, "Note OS v4.20" );
	screen_text_r( sbuf, 0, SCREEN_WIDTH - 2, "Directory" );

	screen_print( sbuf );

}
