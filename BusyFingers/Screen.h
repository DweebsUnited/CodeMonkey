#pragma once

#define SCREEN_WIDTH 69
#define SCREEN_HEIGH 36
typedef char * screenBuf;

#ifdef DEBUG
/*

DEBUG functions

These will be compiled out at a later time, do not rely on them

*/

// DEBUG: Allocator
screenBuf screen_alloc( );

// DEBUG: Print using printf
void screen_print( screenBuf );

#endif


/*

Following are valid functions!

*/

// Set the whole screen to spaces
void screen_blank( screenBuf );
// Or just blank part of the screen
void screen_blank_rect( screenBuf, int top, int bot, int left, int rght );

// Draw a horizontal line
void screen_hline( screenBuf, char c, int rdx, int cdx, int len );

void screen_vline( screenBuf, char c, int rdx, int cdx, int len );

void screen_text( screenBuf, int rdx, int cdx, char * );
void screen_text_r( screenBuf, int rdx, int cdx, char * );
