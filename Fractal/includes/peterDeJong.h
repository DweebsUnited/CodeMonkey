#pragma once

void peterDeJongSetup( );
void peterDeJongTeardown( );
void peterDeJongKeyCallback( GLFWwindow* window, int key, int scancode, int action, int mods );
void peterDeJongMouseCallback( GLFWwindow* window, int button, int action, int mods );

extern Plugin peterDeJong;
