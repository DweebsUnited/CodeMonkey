CFLAGS = -std=c11

all: Screen.o Directory.o

test: Screen.o Directory.o test.c
	gcc $(CFLAGS) Screen.o Directory.o test.c -o test

Screen.o: Screen.c Screen.h
	gcc $(CFLAGS) -c Screen.c -o Screen.o

Directory.o: Directory.c Directory.h Screen.o
	gcc $(CFLAGS) -c Directory.c -o Directory.o

clean:
	rm *.o
	rm test
