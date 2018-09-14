package CodeMonkey.transform;

import processing.core.PVector;

public class BasisCoordinateTransform implements CoordinateTransform {

  private PVector basisX;
  private PVector basisY;
  private PVector basisZ;

  public BasisCoordinateTransform( PVector bx, PVector by, PVector bz ) {

    this.basisX = bx.copy( );
    this.basisY = by.copy( );
    this.basisZ = bz.copy( );

  }

  public BasisCoordinateTransform( PVector bx, PVector by ) {

    this( bx, by, new PVector( 0, 0, 0 ) );

  }

  @Override
  public PVector map( PVector p ) {

    PVector cx = this.basisX.copy( );
    cx.mult( p.x );
    PVector cy = this.basisY.copy( );
    cy.mult( p.y );
    PVector cz = this.basisZ.copy( );
    cz.mult( p.z );

    cz.add( cx );
    cz.add( cy );

    return cz.copy( );

  }

}
