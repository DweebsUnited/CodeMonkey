#pragma once

void mandelbrotSetup( );
void mandelbrotTeardown( );
void mandelbrotKeyCallback( GLFWwindow* window, int key, int scancode, int action, int mods );
void mandelbrotMouseCallback( GLFWwindow* window, int button, int action, int mods );

extern Plugin mandelbrot;
