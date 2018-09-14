package CodeMonkey.utility;

public class OneShottr {

  private boolean shot = true;

  public OneShottr( ) {

  }

  public boolean reset( ) {

    this.shot = true;

    return this.shot;

  }

  public boolean step( float f ) {

    if( f > 1 && this.shot )
      this.shot = false;

    return this.shot;

  }

}
