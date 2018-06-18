from sys import argv
from json import dump

if len( argv ) != 3:
    print "Format: CSVtoJSON.py {input.csv} {output.json}"
    exit( 126 )

pts = []

for ldx, line in enumerate( open( argv[ 1 ], "r" ).readlines( ) ):
    splits = line.split( ',' )
    pts.append( { 'id': ldx, 'x': float( splits[ 0 ] ), 'y': float( splits[ 1 ] ) } )

dump( pts, open( argv[ 2 ], "w" ) )
