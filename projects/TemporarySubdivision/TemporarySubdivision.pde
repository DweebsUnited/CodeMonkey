// Midpoint displacement mountain range

// TODO: Color palette
// TODO: Crosshatching

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


final int nRanges = 5;

final int nDivisions = 5;
final float offsetRange = 175.0;
final float offsetPersistance = 0.5;

Random rng = new Random( );
List<List<Float>> ranges;

void Subdivide( List<Float> r ) {
  
  if( r.size( ) != 2 )
    throw new RuntimeException( "Cannot subdivide list with size != 2" );
  
  // 0: 0 1
  // 1: 0 2 1
  // 2: 0 3 2 4 1
  // 3: 0 5 3 6 2 7 4 8 1
  
  float oRange = offsetRange;
  
  for( int ddx = 0; ddx < nDivisions; ++ddx ) {
    
    for( int idx = r.size( ); idx > 1; --idx ) {
      
      // Divide pairs of elements
      // Calculate indices from EOL, so that inserting doesn't mess us up

      Float mid = ( r.get( r.size( ) - idx ) + r.get( r.size( ) - ( idx - 1 ) ) ) / 2;
      mid += (float)rng.nextGaussian( ) / 3.0 * oRange;
      
      r.add( r.size( ) - ( idx - 1 ), mid );
      
    }
    
    oRange *= offsetPersistance;

  }
  
}

void ConstructRanges( ) {
  
  ranges = new ArrayList<List<Float>>( );
  
  for( int rdx = 0; rdx < nRanges; ++rdx ) {
    
    List<Float> range = new ArrayList<Float>( );
    
    range.add( 0.0 );
    range.add( 0.0 );
    
    Subdivide( range );
    
    ranges.add( range );
    
  }
  
}

void setup( ) {
  
  // Set up drawing
  
  size( 720, 640 );
  background( 255 );
  
  // Construct mountain ranges
  
  ConstructRanges( );
  
  // Draw ranges...
  
  for( int rdx = ranges.size( ) - 1; rdx >= 0; --rdx ) {
    
    float rOff = pixelHeight - ( pixelHeight * 3.0 / 4.0 ) / ( ranges.size( ) + 1 ) * ( rdx + 1 );
    
    List<Float> range = ranges.get( rdx );
    
    float px = pixelWidth / ( range.size( ) - 1.0 );
    
    for( int pdx = 0; pdx < range.size( ) - 1; ++pdx ) {
      
      // pt -> screen
      // 0 -> 0
      // max -> pixelWidth
      
      //stroke( 0 );
      //strokeWeight( 1 );
      //noFill( );
      //line( pdx * px, range.get( pdx ) + rOff, ( pdx + 1 ) * px, range.get( pdx + 1 ) + rOff );
      
      noStroke( );
      fill( lerpColor( #B15B60, #572663, rdx / ( ranges.size( ) - 1.0 ) ) );
      quad(
        pdx * px, range.get( pdx ) + rOff,
        pdx * px, pixelHeight,
        ( pdx + 1 ) * px + 1, pixelHeight,
        ( pdx + 1 ) * px + 1, range.get( pdx + 1 ) + rOff
      );
      
    }
    
  }
  
  // No need for this currently
  
  noLoop( );
  
}
