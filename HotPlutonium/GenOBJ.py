from sys import argv
from json import load

if len( argv ) != 3:
    print "Format: GenSTL.py {input.json} {output.obj}"
    exit( )

triangulation = load( open( argv[ 1 ], "r" ) )

points = triangulation[ "points" ]
dens = [ 0 for _ in points ]
edges = triangulation[ "edges" ]
faces = triangulation[ "faces" ]

points.sort( key=lambda p: p[ "id" ] )

for f in faces:
    dens[ f[ "a" ] ] += f[ "area" ]
    dens[ f[ "b" ] ] += f[ "area" ]
    dens[ f[ "c" ] ] += f[ "area" ]

dens = list( map( lambda d: ( 1.0 / d ) if ( d > 0.0 ) else ( 0.0 ), dens ) )

densMin = min( dens )
densMax = max( dens )

xMin = min( map( lambda p: p[ "x" ], points ) )
xMax = max( map( lambda p: p[ "x" ], points ) )
yMin = min( map( lambda p: p[ "y" ], points ) )
yMax = max( map( lambda p: p[ "y" ], points ) )

print "Mins and maxes: {}, {}, {} : {}, {}, {}".format( xMin, xMax, xMax - xMin, yMin, yMax, yMax - yMin )

dScale = 100

with open( argv[ 2 ], "w" ) as fout:
    for p, d in zip( points, dens ):
        d = pow( ( d - densMin ) / ( densMax - densMin ), 0.75 ) * dScale
        fout.write( "v " + str( p[ "x" ] - xMin ) + " " + str( p[ "y" ] - yMin ) + " " + str( d ) + "\n" )

    fout.write( "\n" )

    for f in faces:
        fout.write( "f " + str( f[ "a" ] + 1 ) + " " + str( f[ "b" ] + 1 ) + " " + str( f[ "c" ] + 1 ) + "\n" )
