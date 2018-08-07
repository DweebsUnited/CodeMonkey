from sys import argv
from subprocess import call

if len( argv ) != 2:
    print "Format: RunAnalyse.py {input.csv}"

fname = argv[ 1 ].split( '.' )
fname, fext = fname[ 0 ], fname[ 1 ]

fcsv = fname + ".csv"
fjson = fname + ".json"
ftri = fname + "Tri.json"
fobj = fname + ".obj"

if fext == ".csv":
    print "Running CSV to JSON"

    call( [ 'python', 'CSVtoJSON.py', fcsv, fjson ] )

print "Running triangulator"

call( [ "../TombstoneTriangulator/TombstoneTriangulator", fjson, ftri  ] )

print "Copying to StrawLobster"

call( [ "cp", ftri, "../StrawLobster/data/triangulation.json" ] )

print "Generating OBJ model"

call( [ "python", "GenOBJ.py", ftri, fobj ] )
