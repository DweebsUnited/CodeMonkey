SOURCEDIR = ./sources/
TESTSDIR = ./tests/
INCLUDEDIR = ./includes/
BUILDDIR = ./build/

CC = gcc
CPP = g++
CCFLAGS = -c -O2 -Wall -Wextra -I$(INCLUDEDIR)
CSTD = -std=c99
CPPSTD = -std=c++11

TESTS = main graph djikstra
TESTOBJECTS = $(addprefix $(BUILDDIR), $(addsuffix .o, $(TESTS)))

PROJECTS = Engine RocketShip
# Run make in all project subdirs

all: codeMonkeyTests.out

codeMonkeyTests.out: $(TESTOBJECTS)
	$(CPP) $^ -o $@

$(BUILDDIR)%.o: $(TESTSDIR)%.cpp ; $(CPP) $^ $(CPPSTD) $(CCFLAGS) -o $@
$(BUILDDIR)%.o: $(INCLUDEDIR)%.h ;

.PHONY: clean
clean:
	rm *.out
	rm $(BUILDDIR)*.o
