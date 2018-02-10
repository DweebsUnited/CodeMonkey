class CircularBuffer {
  
  private float[] data;
  
  private int capacity;
  private int wHead;
  
  public CircularBuffer( int capacity ) {
    
    this.capacity = capacity;
    this.wHead = 0;
    this.data = new float[ capacity ];
    
  }
  
  public void put( float value ) {
    
    data[ this.wHead++ ] = value;
    
    if( this.wHead == this.capacity )
      this.wHead = 0;
    
  }
  
  public float get( int idx ) {
    
    return data[ ( this.wHead + 1 + idx ) % this.capacity ];
    
  }
  
}