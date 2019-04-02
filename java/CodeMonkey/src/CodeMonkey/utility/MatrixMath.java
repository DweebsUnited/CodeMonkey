package CodeMonkey.utility;

import java.util.function.DoubleUnaryOperator;


public class MatrixMath {

	public static void mult( float[ ] a, float[ ] b, int ar, int ac, int bc, float[ ] res ) {

		if( a.length != ar * ac )
			throw new RuntimeException( "Matrix A dimensions do not match" );

		if( b.length != ac * bc )
			throw new RuntimeException( "Matrix B dimensions do not match" );

		if( res.length != ar * bc )
			throw new RuntimeException( "Matrix RES dimensions do not match" );

		if( res == a || res == b )
			throw new RuntimeException( "Matrix mult requires unique output object" );

		// Set res to 0, multAdd
		for( int cdx = 0; cdx < ar * bc; ++cdx )
			res[ cdx ] = 0;

		MatrixMath.multAdd( a, b, ar, ac, bc, res );

	}

	public static void multAdd( float[ ] a, float[ ] b, int ar, int ac, int bc, float[ ] res ) {

		if( a.length != ar * ac )
			throw new RuntimeException( "Matrix A dimensions do not match" );

		if( b.length != ac * bc )
			throw new RuntimeException( "Matrix B dimensions do not match" );

		if( res.length != ar * bc )
			throw new RuntimeException( "Matrix RES dimensions do not match" );

		if( res == a || res == b )
			throw new RuntimeException( "Matrix mult requires unique output object" );

		// ar x ac * br=ac x bc -> ar x bc
		for( int ardx = 0; ardx < ar; ++ardx ) {
			for( int acdx = 0; acdx < ac; ++acdx ) {
				for( int brdx = 0; brdx < ac; ++brdx ) {
					for( int bcdx = 0; bcdx < bc; ++bcdx ) {

						res[ bcdx + ardx * bc ] += a[ acdx + ardx * ac ] * b[ bcdx + brdx * bc ];

					}
				}
			}
		}

	}

	public static void map( float[ ] m, DoubleUnaryOperator f ) {

		for( int cdx = 0; cdx < m.length; ++cdx )
			m[ cdx ] = (float) f.applyAsDouble( m[ cdx ] );

	}

}
