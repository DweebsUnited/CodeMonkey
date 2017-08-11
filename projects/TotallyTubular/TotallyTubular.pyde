# /*******************************************************************************//**
# * @file TotallyTubular.pyde
# * A hyphae-like simulator for py.processing
# *
# * Copyright (C) 2017 by Eric Osburn.
# * The redistribution terms are provided in the LICENSE file that must
# * be distributed with this source code.
# ***********************************************************************************/

from random import uniform, randint
from copy import deepcopy

# TODO:
#   Variable thickness
#   Branching
#   Scale rotation by thickness (smaller more wandering)
#   Collision avoidance

WIDE = 1280
HIGH = 1024
NUM_BS = 25
B_FACT = 0.01
F_RUN = 1000

class Branch:
    speed = PVector( 2, 0 )
    thick = 5
    
    col = randint( 100, 255 );

    def __init__( self, pos ):
        self.pos = pos
        self.vel = deepcopy( Branch.speed )
        self.vel.rotate( uniform( -PI, PI ) )

        self.alive = True

    def step( self ):
        self.pos += self.vel
        self.vel.rotate( uniform( -PI / 8.0, PI / 8.0 ) )

    def kill( self ):
        self.alive = False

    def split( self ):
        nb = deepcopy( self )
        self.vel.rotate( uniform( PI / 4.0, PI ) )
        nb.vel.rotate( uniform( -PI, -PI / 4.0 ) )
        nb.col = randint( 100, 255 )
        return nb

bs = [ Branch( PVector( randint( 0, WIDE ), randint( 0, HIGH ) ) ) for _ in range( NUM_BS ) ]
fcnt = 0

def setup():
    size( WIDE, HIGH )
    background( 255, 255, 255 )
    
    noStroke( )

def draw():
    # Don't forget this
    global bs, fcnt

    for b in bs:
        fill( b.col )
        ellipse( b.pos.x, b.pos.y, Branch.thick, Branch.thick )
        b.step( )

        if b.pos.x > WIDE or b.pos.x < 0 or b.pos.y > HIGH or b.pos.y < 0:
            b.kill( )
        elif uniform( 0, 1 ) < B_FACT:
            bs.append( b.split( ) )

    bs = [ b for b in bs if b.alive ]

    fcnt += 1

    if fcnt > F_RUN:
        noLoop( )
    # else:
        # saveFrame( "hyphae-######.png" )