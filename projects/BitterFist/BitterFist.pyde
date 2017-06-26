from random import randint, uniform
from copy import deepcopy

WIDE = 800
TALL = 600

NUM_PTS = 45

P_SPD = 2
C_SIZE = 5
G_SIZE = 15

class Point:
    speed = PVector( 0, 1 )
    
    def __init__( self, pos ):
        self.pos = deepcopy( pos )
        
        self.vel = deepcopy( Point.speed )
        
    def step( self ):
        self.pos.add( self.vel )

        if self.pos.x > WIDE:
            self.pos.x = WIDE
        if self.pos.x < 0:
            self.pos.x = 0

        if self.pos.y > TALL:
            self.pos.y = TALL
        if self.pos.y < 0:
            self.pos.y = 0
        
        self.vel = PVector( uniform( -P_SPD, P_SPD ), uniform( -P_SPD, P_SPD ) )

ps = [ [ Point( PVector( WIDE / G_SIZE * gx + WIDE / G_SIZE / 2.0, TALL / G_SIZE * gy + WIDE / G_SIZE / 2.0 ) ) for gx in range( 0, G_SIZE ) ] for gy in range( 0, G_SIZE ) ]

def setup( ):
    size( WIDE, TALL )
    
    stroke( 0, 0, 0, 5 )
    fill( 0, 0, 0, 5 )
    background( 255, 255, 255 )
    
def draw( ):
    # Draw all links
    for r in range( 0, G_SIZE ):
        for c in range( 0, G_SIZE ):
            
            # Need to draw link to the right and down
            if r + 1 < G_SIZE:
                line( ps[ c ][ r ].pos.x, ps[ c ][ r ].pos.y, ps[ c ][ r + 1 ].pos.x, ps[ c ][ r + 1 ].pos.y )
                
            if c + 1 < G_SIZE:
                line( ps[ c ][ r ].pos.x, ps[ c ][ r ].pos.y, ps[ c + 1 ][ r ].pos.x, ps[ c + 1 ][ r ].pos.y )
                
            ps[ c ][ r ].step( )