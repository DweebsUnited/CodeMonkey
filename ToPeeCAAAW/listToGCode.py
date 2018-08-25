import argparse
import os

UPSPD = "4000"
DNSPD = "2000"

ZUP = "26"
ZDN = "7"

parser = argparse.ArgumentParser( description='Convert a pointlist to a gcode file' )

parser.add_argument( "infilename" )

args = parser.parse_args( )

f = open( args.infilename )

with open( os.path.join( os.path.dirname( args.infilename ), "pointlist.gcode" ), "w" ) as out:
    # Header
    # Temps to 0 -> Keep heaters off
    out.write( "M104 S0\n" )
    out.write( "M140 S0\n" )
    # Fans off -> Not needed
    out.write( "M107\n" )
    # Home -> Important to 0 everything after reboot, here just safety
    out.write( "G28\n" )
    # Use absolute
    out.write( "G90\n" )
    # Set speed
    out.write( "G0 F" + UPSPD + "\n" )
    # Move to draw origin, pen down so we can set pen
    out.write( "G0 X0 Y0 Z" + ZDN + "\n" )
    # ULTIMAKER ONLY -> Wait for input from user
    out.write( "M0\n" )

    # Begin

    for line in f.readlines( ):

        line = line.rstrip( )

        # New square
        if line[ 0 ] == "N":
            coords = line.split( ',' )
            x = coords[ 1 ]
            y = coords[ 2 ]

            # Pen up, move, pen down
            out.write( "G0 Z" + ZUP + " F" + UPSPD + "\n" )
            out.write( "G0 X" + x + " Y" + y + "\n" )
            out.write( "G0 Z" + ZDN + " F" + DNSPD + "\n" )

        # Coordinates
        else:
            coords = line.split( ',' )
            x = coords[ 0 ]
            y = coords[ 1 ]

            out.write( "G0 X" + x + " Y" + y + "\n" )

    out.write( "G0 Z" + ZUP + " F" + UPSPD + "\n" )
