package CodeMonkey.utility;

public class Varargs {

	public static float min( float... args ) {

		float mv = Float.POSITIVE_INFINITY;

		for( float a : args ) {

			if( a < mv ) {

				mv = a;

			}

		}

		return mv;

	}

	public static float max( float... args ) {

		float mv = Float.NEGATIVE_INFINITY;

		for( float a : args ) {

			if( a > mv ) {

				mv = a;

			}

		}

		return mv;

	}

}
