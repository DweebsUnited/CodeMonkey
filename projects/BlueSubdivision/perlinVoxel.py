# On a grid, while a geometric true
from random import uniform
from noise import snoise2
from math import exp, pow

class Sigmoid:

  s = 0.001
  a = 0
  b = 0

  def __init__( self, s = 0.001 ):
    self.config( s )

  def config( self, s ):

    self.s = s;
    self.a = self.raw( 1 );
    self.b = self.raw( 0 );

    if( self.a < self.b ):
      t = self.a;
      self.a = self.b;
      self.b = t;

  def raw( self, x ):
    return 1.0 / ( 1.0 + exp( - self.s * ( x - 0.5 ) ) )

  def run( self, x ):
    return ( self.raw( x ) - self.b ) * 1.0 / ( self.a - self.b )


GRIDSIZE = 32
MAX_SIZE = 16
voxel = [ ]

level = -1

sig = Sigmoid( 2 )

while len( voxel ) < MAX_SIZE:
    newVoxels = 0
    selfLevel = level + 1

    voxel.append( [ [ False for _ in range( GRIDSIZE ) ] for _ in range( GRIDSIZE ) ] )

    for xdx in range( GRIDSIZE ):
        for ydx in range( 0, GRIDSIZE ):
            n = snoise2( xdx * 0.1, ydx * 0.1 )
            if ( voxel[ level ][ xdx ][ ydx ] or level < 0 ) and ( uniform( 0, 1 ) < pow( n if n > 0 else 0, 1.0 / 2.5 ) ):
                voxel[ selfLevel ][ xdx ][ ydx ] = True
                newVoxels += 1

    level = selfLevel

    if newVoxels == 0:
        break

coords = []
faces = []

def writeFaceX( ldx, xdx, ydx, dc ):
    global coords, faces
    newcoords = [
        ( xdx + 0.5 * dc, ydx + 0.5, ldx + 0.5 ),
        ( xdx + 0.5 * dc, ydx - 0.5, ldx + 0.5 ),
        ( xdx + 0.5 * dc, ydx - 0.5, ldx - 0.5 ),
        ( xdx + 0.5 * dc, ydx + 0.5, ldx - 0.5 ) ]

    lc = len( coords )
    coords += newcoords
    faces.append( ( lc, lc + 1, lc + 2, lc + 3 ) )

def writeFaceY( ldx, xdx, ydx, dc ):
    global coords, faces
    newcoords = [
        ( xdx + 0.5, ydx + 0.5 * dc, ldx + 0.5 ),
        ( xdx + 0.5, ydx + 0.5 * dc, ldx - 0.5 ),
        ( xdx - 0.5, ydx + 0.5 * dc, ldx - 0.5 ),
        ( xdx - 0.5, ydx + 0.5 * dc, ldx + 0.5 ) ]

    lc = len( coords )
    coords += newcoords
    faces.append( ( lc, lc + 1, lc + 2, lc + 3 ) )

def writeFaceL( ldx, xdx, ydx, dc ):
    global coords, faces
    newcoords = [
        ( xdx + 0.5, ydx + 0.5, ldx + 0.5 * dc ),
        ( xdx + 0.5, ydx - 0.5, ldx + 0.5 * dc ),
        ( xdx - 0.5, ydx - 0.5, ldx + 0.5 * dc ),
        ( xdx - 0.5, ydx + 0.5, ldx + 0.5 * dc ) ]

    lc = len( coords )
    coords += newcoords
    faces.append( ( lc, lc + 1, lc + 2, lc + 3 ) )

for ldx in range( len( voxel ) ):
    for xdx in range( GRIDSIZE ):
        for ydx in range( GRIDSIZE ):
            if voxel[ ldx ][ xdx ][ ydx ] is False:
                continue
            # If a neighbor is not filled, write out the face between them
            if ldx - 1 < 0 or not voxel[ ldx - 1 ][ xdx ][ ydx ]:
                writeFaceL( ldx, xdx, ydx, -1 )
            if ldx + 1 >= len( voxel ) or not voxel[ ldx + 1 ][ xdx ][ ydx ]:
                writeFaceL( ldx, xdx, ydx, 1 )

            if xdx - 1 < 0 or not voxel[ ldx ][ xdx - 1 ][ ydx ]:
                writeFaceX( ldx, xdx, ydx, -1 )
            if xdx + 1 >= GRIDSIZE or not voxel[ ldx ][ xdx + 1 ][ ydx ]:
                writeFaceX( ldx, xdx, ydx, 1 )

            if ydx - 1 < 0 or not voxel[ ldx ][ xdx ][ ydx - 1 ]:
                writeFaceY( ldx, xdx, ydx, -1 )
            if ydx + 1 >= GRIDSIZE or not voxel[ ldx ][ xdx ][ ydx + 1 ]:
                writeFaceY( ldx, xdx, ydx, 1 )

with open( "voxel.ply", "w" ) as out:
    out.write( "ply\n" )
    out.write( "format ascii 1.0\n" )
    out.write( "element vertex " + str( len( coords ) ) + "\n" )
    out.write( "property float x\n" )
    out.write( "property float y\n" )
    out.write( "property float z\n" )
    out.write( "element face " + str( len( faces ) ) + "\n" )
    out.write( "property list uchar int vertex_index\n" )
    out.write( "end_header\n" )

    for c in coords:
        out.write( str( c[ 0 ] ) + " " + str( c[ 1 ] ) + " " + str( c[ 2 ] ) + "\n" )

    for f in faces:
        out.write( "4 " + str( f[ 0 ] ) + " " + str( f[ 1 ] ) + " " + str( f[ 2 ] ) + " " + str( f[ 3 ] ) + "\n" )
