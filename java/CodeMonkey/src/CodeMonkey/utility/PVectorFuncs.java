package CodeMonkey.utility;

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

  public static PVector projectVector( PVector v, PVector onto ) {

    float t = v.dot( onto ) / onto.magSq( );
    return multRet( onto, t );

  }

  public static float projectScalar( PVector v, PVector onto ) {

    return v.dot( onto ) / onto.mag( );

  }

}
