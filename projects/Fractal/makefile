SOURCEDIR = ./sources/
TESTSDIR = ./tests/
INCLUDEDIR = ./includes/
BUILDDIR = ./build/
PROJECTDIR = ./projects/

CC = gcc
CPP = g++
CCFLAGS = -c -O2 -Wall -Wextra -I$(INCLUDEDIR) -I../../includes/
CSTD = -std=c99
CPPSTD = -std=c++11

all: Fractal.out

Fractal.out: $(BUILDDIR)/main.o
	$(CPP) $< -o $@

$(BUILDDIR)%.o: $(SOURCEDIR)%.cpp $(INCLUDEDIR)%.h ; $(CPP) $< $(CPPSTD) $(CCFLAGS) -o $@

.PHONY: clean
clean:
	rm *.out
	rm $(BUILDDIR)*.o
