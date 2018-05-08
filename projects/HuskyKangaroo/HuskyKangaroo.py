cfrom random import uniform

SPLIT_PERC = 0.75
FILL_PERC = 0.5

class PLY:
    def __init__( self ):
        self.coords = []
        self.faces = []
        self.faceOff = 0

    def appendCoord( self, x, y, z ):
        self.coords.append( ( x, y, z ) )

    def startFaceSet( self ):
        self.faceOff = len( self.coords )

    def appendFace( self, a, b, c, d ):
        self.faces.append( ( self.faceOff + a, self.faceOff + b, self.faceOff + c, self.faceOff + d ) )

    def export( self ):
        with open( "HuskyKangaroo.ply", "w" ) as out:
            out.write( "ply\n" )
            out.write( "format ascii 1.0\n" )
            out.write( "element vertex " + str( len( self.coords ) ) + "\n" )
            out.write( "property float x\n" )
            out.write( "property float y\n" )
            out.write( "property float z\n" )
            out.write( "element face " + str( len( self.faces ) ) + "\n" )
            out.write( "property list uchar int vertex_index\n" )
            out.write( "end_header\n" )

            for c in self.coords:
                out.write( str( c[ 0 ] ) + " " + str( c[ 1 ] ) + " " + str( c[ 2 ] ) + "\n" )

            for f in self.faces:
                out.write( "4 " + str( f[ 0 ] ) + " " + str( f[ 1 ] ) + " " + str( f[ 2 ] ) + " " + str( f[ 3 ] ) + "\n" )

class Octree:

    isLeaf = False
    isFilled = True

    # xyz, Xyz, xYz, XYz, xyZ, XyZ, xYZ, XYZ
    children = None

    def static( depthLeft, totalDepth = -1 ):
        if totalDepth < 0:
            totalDepth = depthLeft

        o = Octree( )

        # Decide randomly if we should split
        if depthLeft > 0 and ( depthLeft == totalDepth or uniform( 0.0, 1.0 ) < SPLIT_PERC ):

            o.children = [ ]

            for cdx in range( 8 ):
                o.children.append( Octree.static( depthLeft - 1, totalDepth ) )

        else:
            # We are a leaf, choose whether we are filled
            o.isLeaf = True
            o.isFilled = uniform( 0.0, 1.0 ) < FILL_PERC

        return o

    # Assume passed not a leaf
    def copyChildren( self ):
        return [ oc.copy( ) for oc in self.children ]

    def copy( self ):
        o = Octree( )
        if self.isLeaf:
            o.isLeaf = True
            o.isFilled = self.isFilled
        else:
            o.children = self.copyChildren( )

        return o

    def expandTree( self, maxRecur, root ):
        if maxRecur > 0:

            if self.isLeaf:
                if self.isFilled:
                    self.isLeaf = False
                    self.children = root.copyChildren( )

            if not self.isLeaf:
                for c in self.children:
                    c.expandTree( maxRecur - 1, root )

    def render( self, ply,
        xMin, xMax,
        yMin, yMax,
        zMin, zMax ):

        xHalf = ( xMax + xMin ) / 2.0
        yHalf = ( yMax + yMin ) / 2.0
        zHalf = ( zMax + zMin ) / 2.0

        # If not leaf, recurse
        if self.isLeaf:
            if self.isFilled:
                # How the hell is a cube rendered?!
                ply.startFaceSet( )

                ply.appendCoord( xMin, yMin, zMin ) # 0
                ply.appendCoord( xMax, yMin, zMin ) # 1
                ply.appendCoord( xMin, yMax, zMin ) # 2
                ply.appendCoord( xMax, yMax, zMin ) # 3
                ply.appendCoord( xMin, yMin, zMax ) # 4
                ply.appendCoord( xMax, yMin, zMax ) # 5
                ply.appendCoord( xMin, yMax, zMax ) # 6
                ply.appendCoord( xMax, yMax, zMax ) # 7

                ply.appendFace( 0, 1, 3, 2 ) # Bottom
                ply.appendFace( 4, 6, 7, 5 ) # Top
                ply.appendFace( 0, 2, 6, 4 ) # LFfront
                ply.appendFace( 1, 5, 7, 3 ) # RBack
                ply.appendFace( 0, 4, 5, 1 ) # RFront
                ply.appendFace( 2, 3, 7, 6 ) # LBack

        else:
            for cdx, c in enumerate( self.children ):
                c.render( ply,
                xHalf if ( ( cdx & 0x01 ) > 0 ) else xMin, xMax if ( ( cdx & 0x01 ) > 0 ) else xHalf,
                yHalf if ( ( cdx & 0x02 ) > 0 ) else yMin, yMax if ( ( cdx & 0x02 ) > 0 ) else yHalf,
                zHalf if ( ( cdx & 0x04 ) > 0 ) else zMin, zMax if ( ( cdx & 0x04 ) > 0 ) else zHalf )

if __name__=="__main__":

        root = Octree.static( 1 )

        tree = root.copy( )

        tree.expandTree( 6, root )

        ply = PLY( )

        tree.render( ply,
            0.0, 4.0,
            0.0, 4.0,
            0.0, 4.0 )

        ply.export( )
