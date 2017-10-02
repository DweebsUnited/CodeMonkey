from itertools import chain, combinations

def powerset( fromSet ):
    fromSet = list( fromSet )
    return chain.from_iterable( combinations( fromSet, r ) for r in range( len( fromSet ) + 1 ) )

def makeMixes( palette ):
    with open( "cols.txt", "w" ) as out:
        for pList in powerset( palette ):
            if len( pList ) == 0:
                continue

            red   = 0
            blue  = 0
            green = 0

            for g in pList:
                red   += g[ 0 ]
                blue  += g[ 1 ]
                green += g[ 2 ]

            red   /= len( pList )
            blue  /= len( pList )
            green /= len( pList )

            print ( red, green, blue )
            out.write( "color( {}, {}, {} ), ".format( red, green, blue ) )
