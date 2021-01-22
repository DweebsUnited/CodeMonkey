public interface Cost {
  
  public double cost( Matrix output, Matrix truth );
  public boolean grad( Matrix output, Matrix truth, Matrix result );
  
}

public class MSE implements Cost {
  
  public double cost( Matrix output, Matrix truth ) {
    
    if( output.rows != truth.rows || output.cols != truth.cols || output.rows != 1 )
      throw new Error( String.format( "cost dimensions are bad: %d x %d vs %d x %d", output.rows, output.cols, truth.rows, truth.cols ) );
    
    // C = 1/2 sum( truth - output )^2
    double c = 0;
    for( int col = 0; col < output.cols; ++col )
      c += Math.pow( truth.data[ 0 ][ col ] - output.data[ 0 ][ col ], 2 );
      
    return c * 0.5;
    
  }
  
  public boolean grad( Matrix output, Matrix truth, Matrix result ) {
    
    if( output.rows != truth.rows || output.cols != truth.cols || output.rows != result.rows || output.cols != result.cols || output.rows != 1 )
      throw new Error( String.format( "cost.grad dimensions are bad: %d x %d vs %d x %d -> %d x %d", output.rows, output.cols, truth.rows, truth.cols, result.rows, result.cols ) );
    
    // dC/da = output - truth
    result.set( output );
    result.sub( truth, result );
    return true;
    
  }
  
}
