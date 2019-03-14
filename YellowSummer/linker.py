from subprocess import check_output, Popen, PIPE
from generator import Genetic
from mixes import makeMixes

g = Genetic( )

g.runGens( )

palette = g.champ( )

with open( "palette.csv", "wb" ) as out:
    for c in palette:
        out.write( "{}, {}, {}\n".format( c[ 0 ], c[ 1 ], c[ 2 ] ) )

makeMixes( palette )
