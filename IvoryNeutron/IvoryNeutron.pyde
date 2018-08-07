from collections import namedtuple
from operator import itemgetter
from pprint import pformat
from random import randint, uniform
import kdtree as KDTree

WIDE = 512
TALL = 512

N_PTS = 8
N_RND_PTS = 0

DRAW_PTS = False
DRAW_TREE = False

class Node:
    def __init__( self, x, y, data ):
        self.coords = ( x, y )
        self.data = data

    def __len__( self ):
        return len( self.coords )

    def __getitem__( self, i ):
        if i < len( self.coords ):
            return self.coords[ i ]
        else:
            return self.data

    def __repr__(self):
        return 'Node( {}, {}, {} )'.format( self.coords[ 0 ], self.coords[ 1 ], self.data )

def drawTree( tree ):
    stroke( 0, 0, 255 )

    for c, cdx in tree.children:
        line( tree[ 0 ], tree[ 1 ], c[ 0 ], c[ 1 ] )
        drawTree( c )


class NodeFactory:
    def __init__( self, coordFac, colorFac ):
        self.coordFac = coordFac
        self.colorFac = colorFac

    def make( self ):
        pts = [ ]

        for idx in range( self.coordFac.numPoints( ) ):

            x, y = self.coordFac.make( idx )
            col = self.colorFac.make( x, y )

            pts.append( Node( x, y, col ) )

        return pts


class CoordFactory( object ):
    def make( self, idx ):
        raise NotImplementedError( "Coordinate factory not implemented: make" )

    def numPoints( self ):
        raise NotImplementedError( "Coordinate factory not implemented: numPoints" )

class RandomCoordFactory( CoordFactory ):
    numpts = N_PTS

    def make( self, idx ):
        return ( randint( 0, WIDE ), randint( 0, TALL ) )

    def numPoints( self ):
        return self.numpts

class GridCoordFactory( CoordFactory ):
    def __init__( self, grid_wide, grid_tall ):
        self.grid_wide = grid_wide
        self.grid_tall = grid_tall

    def make( self, idx ):
        jdx = idx // self.grid_wide
        idx = idx % self.grid_wide

        r = PVector( max( WIDE, TALL ) / N_PTS / 2.0, 0 )
        r.rotate( uniform( 0, 2.0 * PI ) )

        x = idx * WIDE / N_PTS + WIDE / N_PTS / 2.0 + r.x
        y = jdx * TALL / N_PTS + TALL / N_PTS / 2.0 + r.y
        return ( x, y )

    def numPoints( self ):
        return self.grid_wide * self.grid_tall


class ColorFactory( object ):
    def make( self, x, y ):
        raise NotImplementedError( "Color factory not implemented: make" )

class RandomColorFactory( ColorFactory ):
    def make( self, x, y ):
        return color( randint( 0, 255 ), randint( 0, 255 ), randint( 0, 255 ) )

class FourPointColorFactory( ColorFactory ):
    def __init__( self, tlc, trc, blc, brc ):
        self.tlc = tlc
        self.trc = trc
        self.blc = blc
        self.brc = brc

    def make( self, x, y ):
        yf = y / float( TALL )
        xf = x / float( WIDE )

        # 4 points, lerp horizontally -> vertically
        # B       P
        #
        # G       Y

        tl = lerpColor( self.tlc, self.trc, xf )
        bl = lerpColor( self.blc, self.brc, xf )

        l = lerpColor( tl, bl, yf )

        return l

class FivePointColorFactory( ColorFactory ):
    def __init__( self, tlc, trc, blc, brc, cc ):
        self.tlc = tlc
        self.trc = trc
        self.blc = blc
        self.brc = brc
        self.cc = cc

    def make( self, x, y ):
        yf = y / float( TALL )
        xf = x / float( WIDE )

        # 5 points, lerp horizontally -> vertically -> to center
        # B       P
        #     O
        # G       Y

        tl = lerpColor( self.tlc, self.trc, xf )
        bl = lerpColor( self.blc, self.brc, xf )

        l = lerpColor( tl, bl, yf )

        # SLOOOOOOOOOOOOOOOOOW
        dx = x / float( WIDE ) - 0.5
        dy = y / float( TALL ) - 0.5
        centFac = sqrt( dx * dx + dy * dy ) * 2.0 / sqrt( 2.0 )
        centFac = pow( 1 - centFac, 3 )

        return lerpColor( l, self.cc, centFac )

# All Eyes On You color gradient
class AllEyesOnYouColorFactory( FourPointColorFactory ):
    brokenHeart = color( 92, 50, 62 )
    pinkEyesOnU = color( 168, 39, 67 )
    greenEyesOnU = color( 192, 210, 62 )
    yellowEyesOnU = color( 229, 240, 76 )

    # This is technically a 5 color palette, but I don't like the orange
    # orangeEyesOnU = color( 225, 94, 50 )

    def __init__( self ):
        super( AllEyesOnYouColorFactory, self ).__init__(
            AllEyesOnYouColorFactory.brokenHeart,
            AllEyesOnYouColorFactory.pinkEyesOnU,
            AllEyesOnYouColorFactory.greenEyesOnU,
            AllEyesOnYouColorFactory.yellowEyesOnU )


def setup( ):

    # Node factory
    nfac = NodeFactory( GridCoordFactory( N_PTS, N_PTS ), AllEyesOnYouColorFactory( ) )
    ps = nfac.make( )

    kdtree = KDTree.create( ps )

    size( WIDE, TALL )
    background( 255 )
    fill( 0 )
    noStroke( )

    # Draw voronoi cells
    loadPixels( )

    for xdx in range( WIDE ):
        for ydx in range( TALL ):
            c, dst = kdtree.search_nn( ( xdx, ydx ) )
            pixels[ ydx * WIDE + xdx ] = c[ 4 ]

    updatePixels( )

    # Draw all points
    if DRAW_PTS:
        for p in ps:
            ellipse( p[ 0 ], p[ 1 ], 5, 5 )

    # Draw the lines for the tree
    if DRAW_TREE:
        drawTree( kdtree )

    # Draw a bunch of points with nn
    fill( 0, 255, 0 )
    for _ in range( N_RND_PTS ):
        p = ( randint( 0, WIDE ), randint( 0, TALL ) )
        k, dst = kdtree.search_nn( p )

        noStroke( )
        ellipse( p[ 0 ], p[ 1 ], 5, 5 )

        # stroke( 255 - red( k[ 4 ] ), 255 - green( k[ 4 ] ), 255 - blue( k[ 4 ] ) )
        stroke( 255, 0, 0 )
        line( p[ 0 ], p[ 1 ], k[ 0 ], k[ 1 ] )

def draw( ):
    pass
