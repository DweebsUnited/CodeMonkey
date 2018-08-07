package CodeMonkey.coordinate;

import processing.core.PVector;

public class PVectorFuncs {

  public static PVector elemMul( PVector a, PVector b ) {

    return new PVector( a.x * b.x, a.y * b.y, a.z * b.z );

  }

  public static PVector elemDiv( PVector a, PVector b ) {

    return new PVector( a.x / b.x, a.y / b.y, a.z / b.z );

  }

  public static PVector multRet( PVector v, float c ) {

    PVector r = v.copy( );
    r.mult( c );
    return r;

  }

  public static PVector addRet( PVector a, PVector b ) {

    PVector r = a.copy( );
    r.add( b );
    return r;

  }

}
