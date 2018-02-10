// http://www.rlfbckr.org/work/random-access-memory/

// Vibrating table to get an initial state
//   Mill grid in?

// Some way of mapping a set of stones to an action and a movement
//   x  o  o      o  x  o
//   o  _  x  ->  o  o  x + RIGHT 1
//   x  o  x      o  x  o
// Ignoring the middle stone gives 255 possible states
//   Some bs about only changing based on the neighbors
// Movement of 1 space means there are only 8 next step possibilities
//   Only 3 new stones come into view
// Movement of 2 spaces means there are 32 possible next steps
//   5 new stones
// Move 1 diagonally means 32 next steps

// Seems like it could be encoded into a genome
//   What is fitness? Length of runtime before terminating?
// How should the rules be built?
//   Randomly with a random grid?
//   Based off the initial state somehow?

// Alternately, do it in a radial system
//   How could an increasing density be applied? Ternary+ system, making sure only valid rules are used?
// Move commands would be CW, CCW, IN, OUT instead of 8 cardinal