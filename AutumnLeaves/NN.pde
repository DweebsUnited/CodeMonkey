public class NN {
  
  public int in, out;
  public int nLayers;
  public int[] lSize;
  public Layer[] layers;
  public Matrix[] hidden;
  
  public NN( int[] lSize, Activation[] as ) {
    
    // hSize:
    //   in0 / in
    //   out0 / in1 / h0
    //   out1 / in2 / h1
    //   ...
    //   outn / out
    // as:
    //   a0
    //   a1
    //   ...
    // layers:
    //   l0 -> in x h0
    //   l1 -> h0 x h1
    //   ...
    //   ln -> hn x out
    // hidden:
    //   h0 / input
    //   h1
    //   ...
    //   hn / output
    
    if( lSize.length != ( as.length + 1 ) )
      throw new Error( "Must have one more Activation than Layer sizes..." );
    
    this.in = lSize[ 0 ];
    this.out = lSize[ lSize.length - 1 ];
    
    this.lSize = lSize;
    
    this.nLayers = lSize.length - 1;
    this.layers = new Layer[ nLayers ];
    this.hidden = new Matrix[ lSize.length ];

    for( int ldx = 0; ldx < nLayers; ++ldx ) {
      
      this.layers[ ldx ] = new Layer( lSize[ ldx ], lSize[ ldx + 1 ], as[ ldx ] );
      
    }
    
    // Skip first and last
    for( int hdx = 1; hdx < lSize.length - 1; ++hdx ) {
      
      this.hidden[ hdx ] = new Matrix( 1, lSize[ hdx ] );
      
    }
    
  }
  
  public void apply( Matrix input, Matrix output ) {
    
    if( input.rows != 1 || input.cols != this.in || output.rows != 1 || output.cols != this.out )
      throw new Error( String.format( "Apply dimensions are bad: %d x %d -> %d x %d", input.rows, input.cols, output.rows, output.cols ) );
    
    // Add input and output to hidden array
    this.hidden[ 0 ] = input;
    this.hidden[ lSize.length - 1 ] = output;
    
    // Loop layers
    for( int ldx = 0; ldx < nLayers; ++ldx ) {
      
      this.layers[ ldx ].apply( this.hidden[ ldx ], this.hidden[ ldx + 1 ] );
      
    }
    
  }
  
  public void train( Matrix input, Matrix output, Matrix truth, Cost c, double r ) {
    
    // Run forewards so layers cache correctly
    this.apply( input, output );
    
    // Cost
    double C = c.cost( output, truth );
    
    // dC/da = initial error
    Matrix E = new Matrix( 1, this.out );
    
    c.grad( output, truth, E );
    
    // For each layer backwards propagate the errors
    for( int ldx = nLayers - 1; ldx >= 0; --ldx ) {
      
      E = this.layers[ ldx ].backprop( E, r );
      
    }
    
  }
  
}
