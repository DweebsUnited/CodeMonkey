import Grid

g = Grid.Grid( )

#       | 9   2 |     1
# 2 4   | 1   6 | 7   8
#       |       | 6 2 3
# ---------------------
#       | 3 2 1 |   4
#       |       |
#   7   | 6 5 8 |
# ---------------------
# 4 3 6 |       |
# 8   7 | 5   9 |   1 4
# 1     | 4   3 |

g.set( 0, 3, 9 )
g.set( 0, 5, 2 )
g.set( 0, 8, 1 )

g.set( 1, 0, 2 )
g.set( 1, 1, 4 )
g.set( 1, 3, 1 )
g.set( 1, 5, 6 )
g.set( 1, 6, 7 )
g.set( 1, 8, 8 )

g.set( 2, 6, 6 )
g.set( 2, 7, 2 )
g.set( 2, 8, 3 )

g.set( 3, 3, 3 )
g.set( 3, 4, 2 )
g.set( 3, 5, 1 )
g.set( 3, 7, 4 )

g.set( 5, 1, 7 )
g.set( 5, 3, 6 )
g.set( 5, 4, 5 )
g.set( 5, 5, 8 )

g.set( 6, 0, 4 )
g.set( 6, 1, 3 )
g.set( 6, 2, 6 )

g.set( 7, 0, 8 )
g.set( 7, 2, 7 )
g.set( 7, 3, 5 )
g.set( 7, 5, 9 )
g.set( 7, 7, 1 )
g.set( 7, 8, 4 )

g.set( 8, 0, 1 )
g.set( 8, 3, 4 )
g.set( 8, 5, 3 )

# Timeout 100 steps
s = 0
while not g.isSolved( ) and s < 100:
    # for idx, r in enumerate( g.buildHints( ) ):
    #     print str( idx ) + " --> "
    #     for jdx, c in enumerate( r ):
    #         print str( jdx ) + ": ", repr( c )
    #     print

    g.step( )

    s += 1

print "Solved in", s, "steps!"

for i in range( 9 ):
    for j in range( 9 ):
        print g.get( i, j ),
    print
