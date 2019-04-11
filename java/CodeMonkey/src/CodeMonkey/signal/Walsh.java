package CodeMonkey.signal;

public class Walsh {

	// Walsh matrix generator
	// W(0) = [[1]]
	// W(n+1) =
	// W(n) W(n)
	// W(n) -W(n)

	public static float[ ][ ] getWalshCodes( int pow2 ) {

		// TODO: Do it right...

		if( pow2 != 3 )
			throw new RuntimeException( "" );

		return new float[ ][ ] { new float[ ] { 1, 1, 1, 1, 1, 1, 1, 1 }, new float[ ] { 1, -1, 1, -1, 1, -1, 1, -1 },
				new float[ ] { 1, 1, -1, -1, 1, 1, -1, -1 }, new float[ ] { 1, -1, -1, 1, 1, -1, -1, 1 },
				new float[ ] { 1, 1, 1, 1, -1, -1, -1, -1 }, new float[ ] { 1, -1, 1, -1, -1, 1, -1, 1 },
				new float[ ] { 1, 1, -1, -1, -1, -1, 1, 1 }, new float[ ] { 1, -1, -1, 1, -1, 1, 1, -1 } };

	}

}
