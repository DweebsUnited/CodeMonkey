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
CCFLAGS = -c -O2 -Wall -Wextra -I$(INCLUDEDIR) -I$(RESOURCEDIR)
CSTD = -std=c99
CPPSTD = -std=c++11

MODULES = logger djikstra
MODULEOBJECTS = $(addprefix $(LIBBUILDDIR), $(addsuffix .o, $(MODULES)))

TESTS = main grid graph djikstra genetic threadsafe logger
TESTOBJECTS = $(addprefix $(TESTBUILDDIR), $(addsuffix .o, $(TESTS)))

all: codeMonkeyTests.out

codeMonkeyTests.out: $(BUILDDIR)CodeMonkey.a
codeMonkeyTests.out: $(TESTOBJECTS)
	$(CPP) $^ -o $@

$(TESTBUILDDIR)%.o: $(TESTSDIR)%.cpp
	$(CPP) $< $(CPPSTD) $(CCFLAGS) -o $@


$(BUILDDIR)CodeMonkey.a: $(MODULEOBJECTS)
	ar rcs $@ $^

$(LIBBUILDDIR)%.o: $(SOURCEDIR)%.cpp $(INCLUDEDIR)%.h ; $(CPP) $< $(CPPSTD) $(CCFLAGS) -o $@

.PHONY: clean
clean:
	rm *.out
	rm $(TESTBUILDDIR)*.o
	rm $(LIBBUILDDIR)*.o

.PHONY: Fractal
Fractal:
	make -C $(PROJECTDIR)$@
