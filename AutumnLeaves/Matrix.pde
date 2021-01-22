public class Matrix {
  public double[][] data = null;
  public int rows = 0, cols = 0;

  public Matrix(int rows, int cols) {
    data = new double[rows][cols];
    this.rows = rows;
    this.cols = cols;
  }
  
  public Matrix(Matrix m) {
    this(m.data);
  }

  public Matrix(double[][] data) {
    this.data = data.clone();
    rows = this.data.length;
    cols = this.data[0].length;
  }
  
  public void set( Matrix m ) {
    if( this.rows != m.rows || this.cols != m.cols )
      throw new Error( String.format( "Set dimensions are bad: %d x %d + %d x %d", this.rows, this.cols, m.rows, m.cols ) );
    for( int row = 0; row < this.rows; ++row )
      for( int col = 0; col < this.cols; ++col )
        this.data[ row ][ col ] = m.data[ row ][ col ];
  }

  public boolean isSquare() {
    return rows == cols;
  }

  public void display() {
    System.out.print("[");
    for (int row = 0; row < rows; ++row) {
      if (row != 0) {
        System.out.print(" ");
      }

      System.out.print("[");

      for (int col = 0; col < cols; ++col) {
        System.out.printf("%8.3f", data[row][col]);

        if (col != cols - 1) {
          System.out.print(" ");
        }
      }

      System.out.print("]");

      if (row == rows - 1) {
        System.out.print("]");
      }

      System.out.println();
    }
  }

  public Matrix transpose() {
    Matrix result = new Matrix(cols, rows);

    for (int row = 0; row < rows; ++row) {
      for (int col = 0; col < cols; ++col) {
        result.data[col][row] = data[row][col];
      }
    }

    return result;
  }

  // Note: exclude_row and exclude_col starts from 1
  public Matrix subMatrix(int exclude_row, int exclude_col) {
    Matrix result = new Matrix(this.rows - 1, this.cols - 1);

    for (int row = 0, p = 0; row < this.rows; ++row) {
      if (row != exclude_row - 1) {
        for (int col = 0, q = 0; col < this.cols; ++col) {
          if (col != exclude_col - 1) {
            result.data[p][q] = this.data[row][col];

            ++q;
          }
        }

        ++p;
      }
    }

    return result;
  }

  public double determinant() {
    if (rows != cols) {
      return Double.NaN;
    }
    else {
      return _determinant(this);
    }
  }

  private double _determinant(Matrix matrix) {
    if (matrix.cols == 1) {
      return matrix.data[0][0];
    }
    else if (matrix.cols == 2) {
      return (matrix.data[0][0] * matrix.data[1][1] -
          matrix.data[0][1] * matrix.data[1][0]);
    }
    else {
      double result = 0.0;

      for (int col = 0; col < matrix.cols; ++col) {
        Matrix sub = matrix.subMatrix(1, col + 1);

        result += (Math.pow(-1, 1 + col + 1) *
               matrix.data[0][col] * _determinant(sub));
      }

      return result;
    }
  }

  public Matrix inverse() {
    double det = determinant();

    if (rows != cols || det == 0.0) {
      return null;
    }
    else {
      Matrix result = new Matrix(rows, cols);

      for (int row = 0; row < rows; ++row) {
        for (int col = 0; col < cols; ++col) {
          Matrix sub = this.subMatrix(row + 1, col + 1);

          result.data[col][row] = (1.0 / det *
                       Math.pow(-1, row + col) *
                       _determinant(sub));
        }
      }

      return result;
      
    }
  }
  
  public void add( Matrix B, Matrix result ) {
    
    if( this.rows != B.rows || this.cols != B.cols || this.rows != result.rows || this.cols != result.cols )
      throw new Error( String.format( "Add dimensions are bad: %d x %d + %d x %d = %d x %d", this.rows, this.cols, B.rows, B.cols, result.rows, result.cols ) );
      
    for( int row = 0; row < this.rows; ++row ) {
      for( int col = 0; col < B.cols; ++col ) {
        result.data[ row ][ col ] = this.data[ row ][ col ] + B.data[ row ][ col ];
      }
    }
    
  }
  
  public Matrix add( Matrix B ) {
    
    if( this.rows != B.rows || this.cols != B.cols )
      throw new Error( String.format( "Add dimensions are bad: %d x %d + %d x %d", this.rows, this.cols, B.rows, B.cols ) );
    
    Matrix result = new Matrix( this.rows, this.cols );
    
    this.add( B, result );
    return result;
    
  }
  
  public void sub( Matrix B, Matrix result ) {
    
    if( this.rows != B.rows || this.cols != B.cols || this.rows != result.rows || this.cols != result.cols )
      throw new Error( String.format( "Sub dimensions are bad: %d x %d + %d x %d = %d x %d", this.rows, this.cols, B.rows, B.cols, result.rows, result.cols ) );
      
    for( int row = 0; row < this.rows; ++row ) {
      for( int col = 0; col < B.cols; ++col ) {
        result.data[ row ][ col ] = this.data[ row ][ col ] - B.data[ row ][ col ];
      }
    }
    
  }
  
  public Matrix sub( Matrix B ) {
    
    if( this.rows != B.rows || this.cols != B.cols )
      throw new Error( String.format( "Sub dimensions are bad: %d x %d + %d x %d", this.rows, this.cols, B.rows, B.cols ) );
    
    Matrix result = new Matrix( this.rows, this.cols );
    
    this.add( B, result );
    return result;
    
  }
  
  public void mult( double v, Matrix result ) {
    
    if( this.rows != result.rows || this.cols != result.cols )
      throw new Error( String.format( "Mult dimensions are bad: v * %d x %d = %d x %d", this.rows, this.cols, result.rows, result.cols ) );
    
    for( int row = 0; row < this.rows; ++row ) {
      for( int col = 0; col < this.cols; ++col ) {
        result.data[ row ][ col ] = this.data[ row ][ col ] * v;
      }
    }
    
  }
  
  public Matrix mult( double v ) {
    
    Matrix result = new Matrix( this.rows, this.cols );
    
    this.mult( v, result );
    return result;
    
  }
  
  public void mult( Matrix B, Matrix result ) {
    
    if( this.cols != B.rows || result.rows != this.rows || result.cols != B.cols )
      throw new Error( String.format( "Mult dimensions are bad: %d x %d + %d x %d = %d x %d", this.rows, this.cols, B.rows, B.cols, result.rows, result.cols ) );
    
    for( int row = 0; row < this.rows; ++row ) {
      for( int col = 0; col < B.cols; ++col ) {
        double accum = 0;
        for( int i = 0; i < this.cols; ++i ) {
          accum += this.data[ row ][ i ] * B.data[ i ][ col ];
        }
        result.data[ row ][ col ] = accum;
      }
    }
    
  }
  
  public Matrix mult( Matrix B ) {
    
    // rows, i * i, cols = rows, cols
    
    if( this.cols != B.rows )
      throw new Error( String.format( "Mult dimensions are bad: %d x %d + %d x %d", this.rows, this.cols, B.rows, B.cols ) );
    
    Matrix result = new Matrix( this.rows, B.cols );
    
    this.mult( B, result );
    return result;
    
  }
  
  public void hadamard( Matrix B, Matrix result ) {
    
    if( this.rows != B.rows || this.cols != B.cols || this.rows != result.rows || this.cols != result.cols )
      throw new Error( String.format( "Hadamard dimensions are bad: %d x %d + %d x %d = %d x %d", this.rows, this.cols, B.rows, B.cols, result.rows, result.cols ) );
      
    for( int row = 0; row < this.rows; ++row ) {
      for( int col = 0; col < B.cols; ++col ) {
        result.data[ row ][ col ] = this.data[ row ][ col ] * B.data[ row ][ col ];
      }
    }
    
  }
  
  public Matrix hadamard( Matrix B ) {
    
    if( this.rows != B.rows || this.cols != B.cols )
      throw new Error( String.format( "Hadamard dimensions are bad: %d x %d + %d x %d", this.rows, this.cols, B.rows, B.cols ) );
    
    Matrix result = new Matrix( this.rows, this.cols );
    
    this.add( B, result );
    return result;
    
  }
  
}
