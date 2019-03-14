package CodeMonkey.utility;

public class FlipFlopper {

	private boolean fliporflop = false;

	public FlipFlopper( ) {

	}

	public boolean step( float f ) {

		if( f > 1 ) {

			this.fliporflop = !this.fliporflop;

		}

		return this.fliporflop;

	}

}
