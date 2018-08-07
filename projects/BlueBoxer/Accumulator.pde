interface Accumulator {
  
  public void add( int v );
  public float get( );
  public void clear( );
  
}

interface AccumulatorFactory {
  
  public Accumulator make( );
  
}

class LinearAccumulator implements Accumulator {
  
  private int a;
  private int ac;
  
  public void add( int v ) {
    
    this.a += v;
    this.ac += 1;
    
  }
  
  public float get( ) {
    
    return (float) this.a / this.ac;
    
  }
  
  public void clear( ) {
    
    this.a = 0;
    this.ac = 0;
    
  }
  
}

class LinearAccumulatorFactory implements AccumulatorFactory {
  
  public Accumulator make( ) {
    
    return new LinearAccumulator( );
    
  }
  
}

class SQAvgAccumulator implements Accumulator {
  
  private int a;
  private int ac;
  
  public void add( int v ) {
    
    this.a += v * v;
    this.ac += 1;
    
  }
  
  public float get( ) {
    
    return sqrt( (float) this.a / this.ac );
    
  }
  
  public void clear( ) {
    
    this.a = 0;
    this.ac = 0;
    
  }
  
}

class SQAvgAccumulatorFactory implements AccumulatorFactory {
  
  public Accumulator make( ) {
    
    return new SQAvgAccumulator( );
    
  }
  
}
