#pragma once

// Things that all modules need to know about
void updateTexture( float * image );
extern const int width;
extern const int height;

struct Plugin {
    void (*setup)( );
    void (*teardown)( );
    GLFWkeyfun keyCbk;
    GLFWmousebuttonfun mouseCbk;
    void (*renderloop)( );
};
