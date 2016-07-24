SOURCEDIR = ./sources/
RESOURCEDIR = ./resources/
TESTSDIR = ./tests/
INCLUDEDIR = ./includes/
BUILDDIR = ./build/
TESTBUILDDIR = $(BUILDDIR)tests/
LIBBUILDDIR = $(BUILDDIR)lib/
PROJECTDIR = ./projects/

CC = gcc
CPP = g++
CCFLAGS = -c -O2 -Wall -Wextra -I$(INCLUDEDIR) -I$(RESOURCEDIR) $(GRAPHICSBUILDFLAGS)
CSTD = -std=c99
CPPSTD = -std=c++11
GRAPHICSLINKFLAGS = -lglew -lglfw3 -framework Cocoa -framework OpenGL -framework IOKit -framework CoreVideo -L/usr/local/lib
GRAPHICSBUILDFLAGS = -I$(GLFWDIR) -I$(GLEWDIR)

MODULES = engine shapes
MODULEOBJECTS = $(addprefix $(LIBBUILDDIR), $(addsuffix .o, $(MODULES)))

TESTS = main
TESTOBJECTS = $(addprefix $(TESTBUILDDIR), $(addsuffix .o, $(TESTS)))

all: simpleEngineTests.out

simpleEngineTests.out: ../../build/SimpleEngine.a
simpleEngineTests.out: $(TESTOBJECTS)
	$(CPP) $^ -o $@ $(GRAPHICSLINKFLAGS)

$(TESTBUILDDIR)%.o: $(TESTSDIR)%.cpp
	$(CPP) $< $(CPPSTD) $(CCFLAGS) -o $@


../../build/SimpleEngine.a: $(MODULEOBJECTS)
	ar rcs $@ $^

$(LIBBUILDDIR)%.o: $(SOURCEDIR)%.cpp ; $(CPP) $< $(CPPSTD) $(CCFLAGS) -o $@

.PHONY: clean
clean:
	rm *.out
	rm $(TESTBUILDDIR)*.o
	rm $(LIBBUILDDIR)*.o
	rm ../../build/SimpleEngine.a
