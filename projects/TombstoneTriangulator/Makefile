std = -std=c++11
includes = -I ../../resources/ -I /opt/local/include
libs = -L /opt/local/lib/ -lCGAL -lgmp

all: TombstoneTriangulator

TombstoneTriangulator: TombstoneTriangulator.cpp
	g++ TombstoneTriangulator.cpp -o TombstoneTriangulator $(std) $(includes) $(libs)

PHONY clean:
	rm TombstoneTriangulator
