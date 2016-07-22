# CodeMonkey

A repository of useful libraries I never want to rewrite. Includes projects on different topics I have learned in my academic explorations, as well as many one time use scripts, and several large undertakings.

Has full ( okay not quite ) Doxygen support, with a config file.

## Highlights

* The best folder hierarchy you ever SEEN son.
* Data structure implementations from simple to templated behemoths.
  * Everything I learned in CS 251: Data Structures and Algorithms
* Basic DSP routines
  * DFT, FFT, and inverses for both

In fieri:
* Reading answer messages off iClickers with an SDR
* Fixed point math ( because I learn best by implementation )
* A complete rendering engine:
  * Basic shape library with file loading.
  * Extendable parts, from camera controls to shaders.
  * Will incorporate graphic optimizations as I take the time to learn them.
* RocketShip: Genetic generation of github user icons
* Lindon: Genetic approximation of images using colored lines
* Fractal generators
  * Diamond square
  * Perlin noise
  * Curl noise
* Cloud generation and simulation

## Installation

Download it. The solutions should be good for VS 2015 Community, and the makefiles for any gcc/g++ compiler ( barring weird platform specific errors ).

## Usage

Assuming no weird errors ( I've gotten new compilation errors on every platform I've compiled this on :/ ) this should be ready to go. There are makefiles for the library and for each project, which are a good starting point if you want to use it for something. The library makefile will build CodeMonkey.a in build, which you can link like any other static library. Some modules are still header only, so you may not need to link with it if you only use those.

## Contributing

1. Fork it!
2. Do work on a feature branch based off master
3. Push it somewhere
5. Submit a pull request :D

## History

This was started when I was rooting around and found hundreds of old one-off scripts from when I was learning to program, and realized I never bothered to write reusable code. I'm now taking the time to build new projects in a very reusable way: as a library. From now on as I come across new algorithms I want to try implementing, or have new project ideas, they just get added to this. If something doesn't work, or I give up, I'll comment out functionality but leave the structure and documentation, in case I ever want to revisit it.

## Credits

My name is Eric S. Osburn, my handle is DweebsUnited, and my nickname is Ozzy. I'm a CS undergrad at Purdue, who doesn't know what he wants to do with his life. Rock climbing, Gaellic hurling, my hobbies are best described as eccentric. My foremost passion however is coding. I hope you get some good use out of this, drop me an email if you make something cool that uses it.

## License

Copyright (C) 2016 by Eric Osburn. The redistribution terms are provided in the LICENSE file that must be distributed with this source code.
