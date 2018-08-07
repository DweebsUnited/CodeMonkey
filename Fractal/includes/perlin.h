#pragma once

void perlinSetup( );
void perlinTeardown( );
void perlinKeyCallback( GLFWwindow* window, int key, int scancode, int action, int mods );
void perlinMouseCallback( GLFWwindow* window, int button, int action, int mods );

extern Plugin perlin;
