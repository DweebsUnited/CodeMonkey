SOURCEDIR = ./sources/
RESOURCEDIR = ./resources/
TESTSDIR = ./tests/
INCLUDEDIR = ./includes/
BUILDDIR = ./build/
PROJECTDIR = ./projects/

CC = gcc
CPP = g++
CCFLAGS = -c -O2 -Wall -Wextra -I$(INCLUDEDIR) -I$(RESOURCEDIR) -I../../resources
CSTD = -std=c99
CPPSTD = -std=c++11

MODULES = main
MODULEOBJECTS = $(addprefix $(BUILDDIR), $(addsuffix .o, $(MODULES)))

all: Fractal.out

Fractal.out: $(MODULEOBJECTS)
	$(CPP) $^ -o $@

$(BUILDDIR)%.o: $(SOURCEDIR)%.cpp $(INCLUDEDIR)%.h ; $(CPP) $< $(CPPSTD) $(CCFLAGS) -o $@

.PHONY: clean
clean:
	rm *.out
	rm $(BUILDDIR)*.o
