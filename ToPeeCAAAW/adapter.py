import argparse
import sys
import os

mSpeed = None

inoffx = 0
inoffy = 0
inoffz = 0
inscalex = 1
inscaley = 1
inscalez = 1

outoffx = 0
outoffy = 0
outoffz = 0
outscalex = 1
outscaley = 1
outscalez = 1

parser = argparse.ArgumentParser( description='Rescale and offset a gcode file' )
parser.add_argument( "infilename" )
parser.add_argument( "-ox", type=float )    # Offset x
parser.add_argument( "-oy", type=float )    # Offset y
parser.add_argument( "-oz", type=float )    # Offset z
parser.add_argument( "-sx", type=float )    # Scale x
parser.add_argument( "-sy", type=float )    # Scale y
parser.add_argument( "-sz", type=float )    # Scale z

parser.add_argument( "-iox", type=float )    # Offset x
parser.add_argument( "-ioy", type=float )    # Offset y
parser.add_argument( "-ioz", type=float )    # Offset z
parser.add_argument( "-isx", type=float )    # Scale x
parser.add_argument( "-isy", type=float )    # Scale y
parser.add_argument( "-isz", type=float )    # Scale z

parser.add_argument( "-r", "--round", action='store_true' )  # Should we round to integer coords?

args = parser.parse_args( )

# Read all lines, capture min, max

fminx = sys.float_info.max
fminy = sys.float_info.max
fminz = sys.float_info.max

fmaxx = sys.float_info.min
fmaxy = sys.float_info.min
fmaxz = sys.float_info.min

f = open( args.infilename )
for line in f.readlines( ):
    # Split on space, look for x, y, z
    for part in line.split( ' ' ):
        if part[ 0 ] == 'F':
            mSpeed = float( part[ 1: ] )
        elif part[ 0 ] == 'X':
            c = float( part[ 1: ] )
            if c < fminx:
                fminx = c
            if c > fmaxx:
                fmaxx = c
        elif part[ 0 ] == 'Y':
            c = float( part[ 1: ] )
            if c < fminy:
                fminy = c
            if c > fmaxy:
                fmaxy = c
        elif part[ 0 ] == 'Z':
            c = float( part[ 1: ] )
            if c < fminz:
                fminz = c
            if c > fmaxz:
                fmaxz = c

f.seek( 0 )

inoffx = fminx if args.iox is None else args.iox
inoffy = fminy if args.ioy is None else args.ioy
inoffz = fminz if args.ioz is None else args.ioz
inscalex = ( fmaxx - fminx ) if args.isx is None else args.isx
inscaley = ( fmaxy - fminy ) if args.isy is None else args.isy
inscalez = ( fmaxz - fminz ) if args.isz is None else args.isz

print( "Input offset:" )
print( "  X : " + str( inoffx ) )
print( "  Y : " + str( inoffy ) )
print( "  Z : " + str( inoffz ) )
print( "Input scale:" )
print( "  X : " + str( inscalex ) )
print( "  Y : " + str( inscaley ) )
print( "  Z : " + str( inscalez ) )
print( "" )

if( not any( [ args.ox, args.oy, args.oz, args.sx, args.sy, args.sz, args.round ] ) ):
    f.close( )
    exit( 0 )

outoffx = inoffx if args.ox is None else args.ox
outoffy = inoffy if args.oy is None else args.oy
outoffz = inoffz if args.oz is None else args.oz
outscalex = inscalex if args.sx is None else args.sx
outscaley = inscaley if args.sy is None else args.sy
outscalez = inscalez if args.sz is None else args.sz

print( "Output offset:" )
print( "  X : " + str( outoffx ) )
print( "  Y : " + str( outoffy ) )
print( "  Z : " + str( outoffz ) )
print( "Output scale:" )
print( "  X : " + str( outscalex ) )
print( "  Y : " + str( outscaley ) )
print( "  Z : " + str( outscalez ) )
print( "" )

# Given args, calculate min, max, and rewrite
with open( os.path.join( os.path.dirname( args.infilename ), "rescaled.gcode" ), "w" ) as out:
    for line in f.readlines( ):
        line = line.rstrip( )

        parts = line.split( ' ' )
        for pdx, part in enumerate( parts ):
            if part[ 0 ] == 'G':
                if part[ 1 ] == '0':
                    out.write( part )
                else:
                    out.write( line.rstrip( ) )
                    break

            elif part[ 0 ] == 'F':
                c = float( part[ 1: ] )

                if args.round:
                    c = int( round( c ) )

                out.write( 'F' + str( c ) )

            elif part[ 0 ] == 'X':
                c = float( part[ 1: ] )
                c = ( c - inoffx ) * outscalex / inscalex + outoffx

                if args.round:
                    c = int( round( c ) )

                out.write( 'X' + str( c ) )

            elif part[ 0 ] == 'Y':
                c = float( part[ 1: ] )
                c = ( c - inoffy ) * outscaley / inscaley + outoffy

                if args.round:
                    c = int( round( c ) )

                out.write( 'Y' + str( c ) )

            elif part[ 0 ] == 'Z':
                c = float( part[ 1: ] )
                c = ( c - inoffz ) * outscalez / inscalez + outoffz

                if args.round:
                    c = int( round( c ) )

                out.write( 'Z' + str( c ) )
            else:
                out.write( part.rstrip( ) )

            if pdx < len( parts ) - 1:
                out.write( ' ' )

        out.write( "\n" )

f.close( )
exit( 0 )
