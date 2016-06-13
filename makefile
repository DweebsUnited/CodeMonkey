SOURCEDIR = ./sources/
TESTSDIR = ./tests/
INCLUDEDIR = ./includes/
BUILDDIR = ./build/
PROJECTDIR = ./projects/

CC = gcc
CPP = g++
CCFLAGS = -c -O2 -Wall -Wextra -I$(INCLUDEDIR)
CSTD = -std=c99
CPPSTD = -std=c++11

TESTS = main grid graph djikstra genetic
TESTOBJECTS = $(addprefix $(BUILDDIR), $(addsuffix .o, $(TESTS)))


all: codeMonkeyTests.out

codeMonkeyTests.out: $(TESTOBJECTS)
	$(CPP) $^ -o $@

$(BUILDDIR)%.o: $(TESTSDIR)%.cpp $(INCLUDEDIR)%.h ; $(CPP) $< $(CPPSTD) $(CCFLAGS) -o $@

.PHONY: clean
clean:
	rm *.out
	rm $(BUILDDIR)*.o
