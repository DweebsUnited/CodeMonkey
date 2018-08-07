__all__ = ['fromMaybe', 'Maybe', 'Just', 'Nothing']

def fromMaybe( default, m ):
    if( m.isJust( ) ):
        return m.get( )
    else:
        return default

class Maybe:
    def __init__( self, m ):
        self.isjust = m.isJust( )
        self.value = fromMaybe( 0, m )

    def get( self ):
        return self.value


    def isJust( self ):
        return self.isjust

    def isNothing( self ):
        return not self.isjust


    def __repr__( self ):
        return 'Nothing' if self.isNothing( ) else 'Just( %s )' % repr( self.get( ) )

    def __str__( self ):
        return '' if self.isNothing( ) else repr( self.get( ) )

    def __unicode__( self ):
        return u'' if self.isNothing( ) else unicode( self.get( ) )

    def __nonzero__( self ):
        return False if self.isNothing( ) else bool( self.get( ) )

    def __call__( self, *args, **kwargs ):
        return Nothing( ) if self.isNothing( ) \
            else Just( self.get( )( *args, **kwargs ) )

    def __getattr__( self, name ):
        try:
            return Nothing( ) if self.isNothing( ) \
                else Just( getattr( self.get( ), name ) )
        except:
            return Nothing( )

    def __getitem__( self, key_or_slice ):
        try:
            return Nothing( ) if self.isNothing( ) \
                else Just( self.get( ).__getitem__( key_or_slice ) )
        except:
            return Nothing( )

    def __rshift__( self, fn ):
        return Nothing( ) if self.isNothing( ) \
            else Maybe( fn( self.get( ) ) )

    def __or__( self, other ):
        return other if self.isNothing( ) \
            else self.get( )

class Just( Maybe ):
    def __init__( self, value ):
        self.isjust = True
        self.value = value

class Nothing( Maybe ):
    def __init__( self ):
        self.isjust = False
        self.value = 0
