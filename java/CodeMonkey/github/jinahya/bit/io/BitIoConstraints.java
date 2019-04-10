/*
 *  Copyright 2010 Jin Kwon.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package jinahya.bit.io;

/**
 * A class for constraints.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
final class BitIoConstraints {

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * Checks whether given size is valid for unsigned 8 bit integer. An
	 * {@code IllegalArgumentException} will be thrown if given value is not valid.
	 *
	 * @param size the size to check; must between {@code 1} and {@value Byte#SIZE},
	 *             both inclusive.
	 * @return given size.
	 */
	static int requireValidSizeUnsigned8( final int size ) {

		if( size < 1 ) {
			throw new IllegalArgumentException( "size(" + size + ") < 1" );
		}
		if( size > Byte.SIZE ) {
			throw new IllegalArgumentException( "size(" + size + ") > " + Byte.SIZE );
		}
		return size;
	}

	/**
	 * Checks whether given size is valid for unsigned 16 bit integer. An
	 * {@code IllegalArgumentException} will be thrown if given value is not valid.
	 *
	 * @param size the size to check; must between {@code 1} and
	 *             {@value Short#SIZE}, both inclusive.
	 * @return given size.
	 */
	static int requireValidSizeUnsigned16( final int size ) {

		if( size < 1 ) {
			throw new IllegalArgumentException( "size(" + size + ") < 1" );
		}
		if( size > Short.SIZE ) {
			throw new IllegalArgumentException( "size(" + size + ") > " + Short.SIZE );
		}
		return size;
	}

	// --------------------------------------------------------------------------------------------------------
	// exponent
	static final int MIN_EXPONENT = 3;

	static final int MAX_EXPONENT = 6;

	/**
	 * Validates given exponent.
	 *
	 * @param exponent the exponent to validate.
	 * @return given exponent.
	 */
	static int requireValidExponent( final int exponent ) {

		if( exponent < BitIoConstraints.MIN_EXPONENT ) {
			throw new IllegalArgumentException( "exponent(" + exponent + ") < " + BitIoConstraints.MIN_EXPONENT );
		}
		if( exponent > BitIoConstraints.MAX_EXPONENT ) {
			throw new IllegalArgumentException( "exponent(" + exponent + ") > " + BitIoConstraints.MAX_EXPONENT );
		}
		return exponent;
	}

	// ------------------------------------------------------------------------------------------------------------
	// size
	static final int MIN_SIZE = 1;

	private static final int[ ] MAX_SIZES = new int[ BitIoConstraints.MAX_EXPONENT - BitIoConstraints.MIN_EXPONENT
			+ 1 ];

	static {
		BitIoConstraints.MAX_SIZES[ 0 ] = (int) Math.pow( 2, BitIoConstraints.MIN_EXPONENT );
		for( int i = 1; i < BitIoConstraints.MAX_SIZES.length; i++ ) {
			BitIoConstraints.MAX_SIZES[ i ] = BitIoConstraints.MAX_SIZES[ i - 1 ] * 2;
		}
	}

	/**
	 * Returns the maximum size for given arguments.
	 *
	 * @param unsigned the value for unsigned.
	 * @param exponent the value for exponent.
	 * @return the maximum size.
	 */
	static int maxSize( final boolean unsigned, final int exponent ) {

		return BitIoConstraints.MAX_SIZES[ BitIoConstraints.requireValidExponent( exponent )
				- BitIoConstraints.MIN_EXPONENT ] - ( unsigned ? 1 : 0 );
	}

	static int requireValidSize( final boolean unsigned, final int exponent, final int size ) {

		if( size < BitIoConstraints.MIN_SIZE ) {
			throw new IllegalArgumentException( "size(" + size + ") < " + BitIoConstraints.MIN_SIZE );
		}
		final int maxSize = BitIoConstraints.maxSize( unsigned, exponent );
		if( size > maxSize ) {
			throw new IllegalArgumentException(
					"size(" + size + ") > " + maxSize + "; unsigned=" + unsigned + "; exponent=" + exponent );
		}
		return size;
	}

	static int requireValidSizeByte( final boolean unsigned, final int size ) {

		return BitIoConstraints.requireValidSize( unsigned, 3, size );
	}

	static int requireValidSizeShort( final boolean unsigned, final int size ) {

		return BitIoConstraints.requireValidSize( unsigned, 4, size );
	}

	static int requireValidSizeInt( final boolean unsigned, final int size ) {

		return BitIoConstraints.requireValidSize( unsigned, 5, size );
	}

	static int requireValidSizeLong( final boolean unsigned, final int size ) {

		return BitIoConstraints.requireValidSize( unsigned, 6, size );
	}

	static int requireValidSizeChar( final int size ) {

		return BitIoConstraints.requireValidSizeUnsigned16( size );
	}

	// -----------------------------------------------------------------------------------------------------------------
	private BitIoConstraints( ) {

		super( );
	}
}
