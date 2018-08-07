from random import randint, uniform
from copy import deepcopy

WIDE = 800
TALL = 600

P_SPD = 2
P_RNG = 16
P_SPRNG = 0.7

G_SIZE = 10

O_COLR = 100
O_CONN = 40
O_STRK = 40
O_FILL = 25

# The tpos point will move around with random speed each frame
# The pos point will act as though it is connected to tpos with a spring
class Point:
    speed = PVector( 0, P_SPD )
    
    def __init__( self, pos ):
        off = PVector( 0, 5 * P_SPD )
        off.rotate( uniform( 0, 2 * PI ) )
        
        self.tpos = deepcopy( pos )
        self.pos = self.tpos + off
        
        self.ppos = deepcopy( self.pos )
        
    def step( self ):
        # Update tpos
        tvel = PVector( uniform( -P_SPD, P_SPD ), uniform( -P_SPD, P_SPD ) )
        self.tpos += tvel
        
        # Update pos
        accel = self.tpos - self.pos
        
        # If out of range, spring it back in
        if accel.magSq( ) > P_RNG:
            accel.mult( P_SPRNG ) 
        else:
            accel.mult( 0 )
            
        # Verlet integrator
        self.ppos, self.pos = self.pos, 2 * self.pos - self.ppos + accel * 1.0 / ( 30 * 30 )
        
        # Clamp points to draw area
        if self.pos.x > WIDE:
            self.pos.x = WIDE
        if self.pos.x < 0:
            self.pos.x = 0

        if self.pos.y > TALL:
            self.pos.y = TALL
        if self.pos.y < 0:
            self.pos.y = 0
            
        if self.tpos.x > WIDE:
            self.tpos.x = WIDE
        if self.tpos.x < 0:
            self.tpos.x = 0

        if self.tpos.y > TALL:
            self.tpos.y = TALL
        if self.tpos.y < 0:
            self.tpos.y = 0

class Color:
    def __init__( self, mr, Mr, mg, Mg, mb, Mb, o ):
        self.mr = mr
        self.Mr = Mr
        self.mg = mg
        self.Mg = Mg
        self.mb = mb
        self.Mb = Mb
        
        self.o = o
        
        self.c = color( randint( self.mr, self.Mr ), randint( self.mg, self.Mg ), randint( self.mb, self.Mb ), self.o )
        
    def step( self ):
        self.c = color( randint( self.mr, self.Mr ), randint( self.mg, self.Mg ), randint( self.mb, self.Mb ), self.o )
        return self.c

ps = [ [ Point( PVector( WIDE / G_SIZE * gx + WIDE / G_SIZE / 2.0, TALL / G_SIZE * gy + WIDE / G_SIZE / 2.0 ) ) for gx in range( 0, G_SIZE ) ] for gy in range( 0, G_SIZE ) ]
drawing = True
ocol = Color( 255, 255, 140, 215, 0, 0, O_COLR )
rcol = Color( 128, 200, 0, 0, 0, 0, O_CONN )

def setup( ):
    size( WIDE, TALL )
    
    # fill( 0, 0, 0, 5 )
    background( 255, 255, 255 )
    
def draw( ):
    
    if drawing:
        noStroke( )
        fill( 255, O_FILL )
        rect( 0, 0, WIDE, TALL )
        
        for r in range( 0, G_SIZE ):
            for c in range( 0, G_SIZE ):
                
                # Need to draw links to the right and down
                if r + 1 < G_SIZE:
                    stroke( 0, O_STRK )
                    line( ps[ c ][ r ].tpos.x, ps[ c ][ r ].tpos.y, ps[ c ][ r + 1 ].tpos.x, ps[ c ][ r + 1 ].tpos.y )
                    stroke( ocol.step( ) )
                    line( ps[ c ][ r ].pos.x, ps[ c ][ r ].pos.y, ps[ c ][ r + 1 ].pos.x, ps[ c ][ r + 1 ].pos.y )
                    
                if c + 1 < G_SIZE:
                    stroke( 0, O_STRK )
                    line( ps[ c ][ r ].tpos.x, ps[ c ][ r ].tpos.y, ps[ c + 1 ][ r ].tpos.x, ps[ c + 1 ][ r ].tpos.y )
                    stroke( ocol.step( ) )
                    line( ps[ c ][ r ].pos.x, ps[ c ][ r ].pos.y, ps[ c + 1 ][ r ].pos.x, ps[ c + 1 ][ r ].pos.y )
                    
                stroke( rcol.step( ) )
                line( ps[ c ][ r ].tpos.x, ps[ c ][ r ].tpos.y, ps[ c ][ r ].pos.x, ps[ c ][ r ].pos.y )
                
                ps[ c ][ r ].step( )
                
        saveFrame( "frame-######.png" )
                
def keyReleased( ):
    global drawing
    
    if key == ' ':
        pass
        # Disable this for now...
        # drawing = not drawing