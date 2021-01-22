public class Layer {
  
  private int in, out;
  
  private Matrix bI;
  private Matrix w;
  private Matrix wI;
  
  private Activation a;
  
  public Layer( int in, int out, Activation a ) {
    
    this.in = in;
    this.out = out;
    
    // Add 1 input for bias term
    this.bI = new Matrix( 1, in + 1 );
    this.w = new Matrix( in + 1, out );
    this.wI = new Matrix( 1, out );
    
    this.a = a;
    
  }
  
  public boolean apply( Matrix input, Matrix output ) {
    
    // Input must be row vector: 1, this.in
    // Output must be row vector: 1, this.out
    
    if( input.rows != 1 || input.cols != this.in || output.rows != 1 || output.cols != this.out )
      return false;
    
    // Output: a( input * w + b )
    
    // Add bias term to input
    for( int col = 0; col < this.in; ++col )
      bI.data[ 0 ][ col ] = input.data[ 0 ][ col ];
    bI.data[ 0 ][ this.in ] = 1.0;
    
    this.bI.mult( this.w, wI );
    
    this.a.a( wI, output );
    
    return true;
    
  }
  
  public Matrix apply( Matrix input ) {
    
    // Input must be row vector: 1, this.in
    
    if( input.rows != 1 || input.cols != this.in )
      return null;
    
    Matrix result = new Matrix( 1, this.out );
    
    if( this.apply( input, result ) )
      return result;
    else
      return null;
    
  }
  
  public Matrix backprop( Matrix E, double r ) {
    
    if( this.out != E.cols || E.rows != 1 )
      throw new Error( String.format( "backprop E dimensions are bad: %d x %d -> %d", E.rows, E.cols, this.out ) );
    
    // Calculate dz/da = derivative of activation at wI
    Matrix dzda = new Matrix( 1, this.out );
    for( int col = 0; col < this.out; ++col ) {
      
      // dOn / dEn
      dzda.data[ 0 ][ col ] = this.a.grad( this.wI, col );
      
    }
    
    // This layer's error is: E had dz/da
    E.hadamard( dzda, E );
    
    // First layer will get E: 1 x output -> call it 1 x 3
    // With two inputs we will have the following:
    // I0 I1
    //       W00 W01 W02
    //       W10 W11 W12
    //                   O0 O1 O2
    // Error in weights: dC/dw = alast * Eout = bIk * Ej
    // ->    bI0E0 bI0E1 bI0E2
    //       bI1E0 bI1E1 bI1E2
    Matrix dW = new Matrix( this.w.rows, this.w.cols );
    for( int row = 0; row < dW.rows; ++row ) {
      for( int col = 0; col < dW.cols; ++col ) {
        
        dW.data[ row ][ col ] = this.bI.data[ 0 ][ row ] * E.data[ 0 ][ col ];
        
      }
    }
    
    // Apply learning rate
    dW.mult( r, dW );
    
    // Propagating error: E * W_T
    // Only way for it to make sense... 1x3 * 2x3
    Matrix W_T = this.w.transpose( );
    E = E.mult( W_T );
    E.cols -= 1; // Its 1 am, I dont care anymore
    
    // Now we can update our w and continue
    this.w.sub( dW, w );
    
    return E;
    
  }
  
}
