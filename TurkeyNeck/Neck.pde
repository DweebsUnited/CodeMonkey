final int N_STRING = 6;

class Neck {
  
  private ArrayList<Integer>[] strings;
  
  public Neck( ) {
    
    this.strings = (ArrayList<Integer>[])new ArrayList[ N_STRING ];
    
    for( int sdx = 0; sdx < N_STRING; ++sdx ) {
      
      this.strings[ sdx ] = new ArrayList<Integer>( );
      
    }
    
  }
  
}