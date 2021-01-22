public interface Activation {
  public boolean a( Matrix input, Matrix output );
  public double grad( Matrix I, int n );
}

public abstract class ElementWiseActivation implements Activation {
  
  public abstract double func( double v );
  
  public boolean a( Matrix input, Matrix output ) {
    if( input.rows != output.rows || input.cols != output.cols || input.rows != 1 )
      return false;
      
    for( int r = 0; r < input.rows; ++r ) {
      for( int c = 0; c < input.cols; ++c ) {
        output.data[ r ][ c ] = this.func( input.data[ r ][ c ] );
      }
    }
    
    return true;
  }
  
}

public class Linear extends ElementWiseActivation {
  public double func( double v ) {
    return v;
  }
  
  public double grad( Matrix I, int n ) {
    // df/dx = 1
    return 1;
  }
}

public class Sigmoid extends ElementWiseActivation {
  public double func( double v ) {
    return 1.0 / ( 1.0 + Math.exp( -v ) );
  }
  
  public double grad( Matrix I, int n ) {
    // df/dx = f * ( 1 - f )
    double f = this.func( I.data[ 0 ][ n ] );
    return f * ( 1.0 - f );
  }
}

public class ReLu6 extends ElementWiseActivation {
  public double func( double v ) {
    return Math.max( 0, Math.min( v, 6 ) );
  }
  
  public double grad( Matrix I, int n ) {
    // df/dx = { x < 0: 0, x > 6: 0, 1 }
    if( I.data[ 0 ][ n ] < 0 )
      return 0;
    else if( I.data[ 0 ][ n ] > 6 )
      return 0;
    else
      return 1;
  }
}

public class SoftMax implements Activation {
  
  public boolean a( Matrix input, Matrix output ) {
    
    if( input.rows != output.rows || input.cols != output.cols || input.rows != 1 )
      return false;
    
    // S( yi ) = e^( yi ) / sum( e^( y ) )
    
    double s = 0;
    
    for( int j = 0; j < output.cols; ++j ) {
      
      output.data[ 0 ][ j ] = Math.exp( input.data[ 0 ][ j ] );
      
      s += output.data[ 0 ][ j ];
      
    }
    
    for( int j = 0; j < output.cols; ++j ) {
      
      output.data[ 0 ][ j ] = output.data[ 0 ][ j ] / s;
      
    }
    
    return true;
    
  }
  
  public double grad( Matrix I, int n ) {
    // dO/dIn = ( e^In * sum( e^Im ) ) / sum( e^I )^2
    double ns = 0;
    double s = 0;
    for( int ndx = 0; ndx < I.cols; ++ndx ) {
      s += Math.pow( Math.exp( I.data[ 0 ][ ndx ] ), 2 );
      if( ndx != n ) ns += Math.exp( I.data[ 0 ][ ndx ] );
    }
    
    return ( Math.exp( I.data[ 0 ][ n ] ) * ns ) / s;
    
  }
  
}
