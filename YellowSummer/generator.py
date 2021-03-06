import random
from functools import reduce
from itertools import chain, combinations
import cProfile

def powerset( fromSet ):
    fromSet = list( fromSet )
    return chain.from_iterable( combinations( fromSet, r ) for r in range( len( fromSet ) + 1 ) )

# TODO: MUTATIONS

# Genome length
G_LEN = 5

# Population size
P_LEN = 64

# How many champs to keep
C_KEEP = 8

# Chance for a mutation to occur
C_MUTA = 0.03

# How many generations
N_GEN = 512

# Simple storage and crossover for a 3-color gene
class Gene:
    def __init__( self, red=None, blue=None, green=None, gList=None ):

        if gList is None or len( gList ) == 0:
            self.red   = red   if red   is not None else random.randint( 0, 255 )
            self.blue  = blue  if blue  is not None else random.randint( 0, 255 )
            self.green = green if green is not None else random.randint( 0, 255 )
        else:
            self.red   = 0
            self.blue  = 0
            self.green = 0

            for g in gList:
                self.red   += g.red
                self.blue  += g.blue
                self.green += g.green

            self.red   /= len( gList )
            self.blue  /= len( gList )
            self.green /= len( gList )

    def cross( A, B ):
        nR = A.red   if random.uniform( 0.0, 1.0 ) > 0.5 else B.red 
        nB = A.blue  if random.uniform( 0.0, 1.0 ) > 0.5 else B.blue 
        nG = A.green if random.uniform( 0.0, 1.0 ) > 0.5 else B.green 

        # nR = nR if random.uniform( 0.0, 1.0 ) < C_MUTA else int( nR + random.gauss( 0, 2 ) )
        # nB = nR if random.uniform( 0.0, 1.0 ) < C_MUTA else int( nB + random.gauss( 0, 2 ) )
        # nG = nR if random.uniform( 0.0, 1.0 ) < C_MUTA else int( nG + random.gauss( 0, 2 ) )

        # if( nR > 255 ):
        #     nR = 255
        # elif( nR < 0 ):
        #     nR = 0

        # if( nG > 255 ):
        #     nG = 255
        # elif( nG < 0 ):
        #     nG = 0

        # if( nB > 255 ):
        #     nB = 255
        # elif( nB < 0 ):
        #     nB = 0

        nR = nR if random.uniform( 0.0, 1.0 ) < C_MUTA else random.randint( 0, 255 )
        nB = nR if random.uniform( 0.0, 1.0 ) < C_MUTA else random.randint( 0, 255 )
        nG = nR if random.uniform( 0.0, 1.0 ) < C_MUTA else random.randint( 0, 255 )
    
        return Gene( nR, nB, nG )

    def diff( A, B ):
        return abs( A.red - B.red ) + abs( A.blue - B.blue ) + abs( A.green - B.green )

    def asTuple( self ):
        return ( self.red, self.blue, self.green )

    def __repr__( self ):
        return "({},{},{})".format( self.red, self.blue, self.green )

# List of genes, and a crossover method
class Genome:
    def __init__( self, make = True ):
        self.genes = [ Gene( ) for _ in range( G_LEN ) ] if make == True else [ ]
        self.fitness = 0

    def cross( A, B ):
        C = Genome( False )

        for gA, gB in zip( A.genes, B.genes ):
            C.genes.append( Gene.cross( gA, gB ) )

        return C

    def toList( self ):
        return [ g.asTuple( ) for g in self.genes ]

    def __repr__( self ):
        return repr( self.genes )

# Controller for the genetic process
class Genetic:
    def __init__( self ):
        self.genomes = [ Genome( ) for _ in range( P_LEN ) ]

    def sortPop( self ):
        self.genomes = sorted( self.genomes, key=lambda g: g.fitness, reverse=True )

    # Determine fitness of all genomes
    # O( 2^( 2 N ) N^2 ) --> RIP
    def eval( self ):
        for genome in self.genomes:
            genome.fitness = 0

            # Oh god why
            for subsetA in powerset( genome.genes ):
                for subsetB in powerset( genome.genes ):

                    gA = Gene( gList=subsetA )
                    gB = Gene( gList=subsetB )

                    genome.fitness += Gene.diff( gA, gB )

    def champSelect( self ):
        self.sortPop( )

        champs = self.genomes[ : C_KEEP ]

        return champs

    # Rebreed from given set of champs
    def rebreed( self ):
        self.genomes = self.champSelect( )

        while len( self.genomes ) < P_LEN:
            self.genomes.append( Genome.cross( self.genomes[ random.randint( 0, C_KEEP - 1 ) ], self.genomes[ random.randint( 0, C_KEEP - 1 ) ] ) )

    def runGens( self ):
        for gen in range( N_GEN ):
            self.eval( )
            self.rebreed( )

        return self.champ( )

    def champ( self ):
        self.sortPop( )
        return self.genomes[ 0 ].toList( )

    def __repr__( self ):
        ret = ""
        for g in self.genomes:
            ret += repr( g ) + ": {}".format( g.fitness ) + "\n"

        return ret
