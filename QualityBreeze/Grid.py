import Maybe

class Grid:
    def __init__( self ):
        self.grid = [ [ Maybe.Nothing( ) for _ in range( 9 ) ] for _ in range( 9 ) ]

    def set( self, i, j, value ):
        self.grid[ i ][ j ] = Maybe.Just( value )

    def get( self, i, j ):
        return self.grid[ i ][ j ]

    def isSolved( self ):
        solved = True

        for row in range( 9 ):
            for col in range( 9 ):

                # If we find a nothing, it isn't solved yet
                if self.grid[ row ][ col ].isNothing( ):
                    solved = False

        return solved

    def step( self ):
        h = self.buildHints( )

        for row in range( 9 ):
            for col in range( 9 ):

                # If there is only one option, set it
                if h[ row ][ col ].isJust( ) and len( h[ row ][ col ].get( ) ) == 1:
                    self.set( row, col, h[ row ][ col ].get( )[ 0 ] )

    def buildHints( self ):
        # Build, then invert to return
        hints = [ [ [ ] for _ in range( 9 ) ] for _ in range( 9 ) ]

        # Check rows and cols
        for row in range( 9 ):
            for col in range( 9 ):

                # For each Just, add it's value to all hints in that row and col
                if self.grid[ row ][ col ].isJust( ):
                    v = self.grid[ row ][ col ].get( )

                    for r in range( 9 ):
                        hints[ r ][ col ].append( v )

                    for c in range( 9 ):
                        hints[ row ][ c ].append( v )

        # Check subgrids
        for subrow in range( 3 ):
            for subcol in range( 3 ):

                # Check cells in subgrid
                for row in range( 3 ):
                    for col in range( 3 ):

                        if self.grid[ row + 3 * subrow ][ col + 3 * subcol ].isJust( ):
                            v = self.grid[ row + 3 * subrow ][ col + 3 * subcol ].get( )

                            # Append to all other cells in subgrid
                            for r in range( 3 ):
                                for c in range( 3 ):
                                    hints[ r + 3 * subrow ][ c + 3 * subcol ].append( v )

        # Invert hints
        for row in range( 9 ):
            for col in range( 9 ):

                h = hints[ row ][ col ]
                nh = [ i for i in range( 1, 10 ) if i not in h ]

                hints[ row ][ col ] = Maybe.Just( nh ) if self.grid[ row ][ col ].isNothing( ) else Maybe.Nothing( )

        return hints
