from itertools import product

class Dice:
    faces = [ ]
    mod = 0

    def __init__( self, faces, mod ):
        self.faces = [ f for f in range( 1, faces + 1 ) ]
        self.mod = mod

    def outcomes( self ):
        for f in self.faces:
            yield( f + self.mod )

class AddDice:
    dice = [ ]

    def __init__( self, *dice ):
        self.dice = dice

    def outcomes( self ):
        for faces in product( *map( lambda d: d.outcomes( ), self.dice ) ):
            yield( sum( faces ) )

def compare( a, b ):
    less  = 0
    equal = 0
    great = 0

    for oa in a.outcomes( ):
        for ob in b.outcomes( ):
            if oa < ob:
                less  += 1
            elif oa == ob:
                equal += 1
            else:
                great += 1

    return ( less, equal, great )

def explain( cnts ):
    s = float( sum( cnts ) )
    print( "p lose, A < B: " + str( cnts[ 0 ] / s ) )
    print( "p tie,  A = B: " + str( cnts[ 1 ] / s ) )
    print( "p win,  A > B: " + str( cnts[ 2 ] / s ) )

print( "2d6 + 2 vs 2d6" )
explain(
    compare(
        AddDice(
            Dice( 6, 1 ),
            Dice( 6, 1 ) ),
        AddDice(
            Dice( 6, 0 ),
            Dice( 6, 0 ) ) ) )

print( "2d6 + 2 vs 2d6" )
explain(
    compare(
        AddDice(
            Dice( 6, 1 ),
            Dice( 6, 1 ) ),
        AddDice(
            Dice( 6, 0 ),
            Dice( 6, 0 ) ) ) )
