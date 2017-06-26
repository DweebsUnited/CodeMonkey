from collections import namedtuple
from operator import itemgetter
from pprint import pformat
from random import randint, uniform
import kdtree as KDTree

WIDE = 800
TALL = 600

N_PTS = 15
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

def makeNode( idx, jdx, colorFunc ):
    r = PVector( max( WIDE, TALL ) / N_PTS / 2, 0 )
    r.rotate( uniform( 0, 2 * PI ) )
    
    x = idx * WIDE / N_PTS + WIDE / N_PTS / 2 + r.x
    y = jdx * TALL / N_PTS + TALL / N_PTS / 2 + r.y
    return Node( x, y, colorFunc( x, y ) )

def AllEyesOnYou( x, y ):
    brokenHeart = color( 92, 50, 62 )
    pinkEyesOnU = color( 168, 39, 67 )
    orangeEyesOnU = color( 225, 94, 50 )
    greenEyesOnU = color( 192, 210, 62 )
    yellowEyesOnU = color( 229, 240, 76 )
    
    # 4 quadrants, 5 points
    # B       P
    #     O   
    # G       Y
    
    tl = lerpColor( brokenHeart, pinkEyesOnU, x / float( WIDE ) )
    bl = lerpColor( greenEyesOnU, yellowEyesOnU, x / float( WIDE ) )
    
    return lerpColor( tl, bl, y / float( TALL ) )
    # TODO: Include the orange in the middle

def setup( ): 
            
    # Node color is a lerp between the top and bottom lerps
    # Different effects can be created by 
    ps = [ makeNode( idx, jdx, AllEyesOnYou ) for idx in range( N_PTS ) for jdx in range( N_PTS ) ]
    
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
        stroke( 255, 0, 0 )
        line( p[ 0 ], p[ 1 ], k[ 0 ], k[ 1 ] )
    
def draw( ):
    pass