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

import static com.github.jinahya.bit.io.BitIoConstraints.requireValidSizeByte;
import static com.github.jinahya.bit.io.BitIoConstraints.requireValidSizeChar;
import static com.github.jinahya.bit.io.BitIoConstraints.requireValidSizeInt;
import static com.github.jinahya.bit.io.BitIoConstraints.requireValidSizeLong;
import static com.github.jinahya.bit.io.BitIoConstraints.requireValidSizeShort;
import static com.github.jinahya.bit.io.BitIoConstraints.requireValidSizeUnsigned16;
import static com.github.jinahya.bit.io.BitIoConstraints.requireValidSizeUnsigned8;

import java.io.IOException;


/**
 * An abstract class for implementing {@link BitInput}.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 * @see AbstractBitOutput
 */
public abstract class AbstractBitInput implements BitInput {

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * Reads an unsigned 8-bit integer.
	 *
	 * @return an unsigned 8-bit integer.
	 * @throws IOException if an I/O error occurs.
	 */
	protected abstract int read( ) throws IOException;

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * Reads an unsigned value whose maximum size is {@value Byte#SIZE}.
	 *
	 * @param size the number of bits for the value; between {@code 1} and
	 *             {@value Byte#SIZE}, both inclusive.
	 * @return an unsigned byte value.
	 * @throws IOException if an I/O error occurs.
	 */
	protected int unsigned8( final int size ) throws IOException {

		requireValidSizeUnsigned8( size );
		if( this.available == 0 ) {
			this.octet = this.read( );
			this.count++;
			this.available = Byte.SIZE;
		}
		final int required = size - this.available;
		if( required > 0 ) {
			return ( this.unsigned8( this.available ) << required ) | this.unsigned8( required );
		}
		final int value = ( this.octet >> ( this.available - size ) ) & ( ( 1 << size ) - 1 );
		this.available -= size;
		return value;
	}

	/**
	 * Reads an unsigned value whose maximum size is {@value Short#SIZE}.
	 *
	 * @param size the number of bits for the value; between {@code 1} and
	 *             {@value Short#SIZE}, both inclusive.
	 * @return an unsigned short value.
	 * @throws IOException if an I/O error occurs.
	 */
	protected int unsigned16( final int size ) throws IOException {

		requireValidSizeUnsigned16( size );
		int value = 0x00;
		final int quotient = size / Byte.SIZE;
		final int remainder = size % Byte.SIZE;
		for( int i = 0; i < quotient; i++ ) {
			value <<= Byte.SIZE;
			value |= this.unsigned8( Byte.SIZE );
		}
		if( remainder > 0 ) {
			value <<= remainder;
			value |= this.unsigned8( remainder );
		}
		return value;
	}

	// -----------------------------------------------------------------------------------------------------------------
	@Override
	public boolean readBoolean( ) throws IOException {

		return this.readInt( true, 1 ) == 1;
	}

	// -----------------------------------------------------------------------------------------------------------------
	@Override
	public byte readByte( final boolean unsigned, final int size ) throws IOException {

		requireValidSizeByte( unsigned, size );
		return (byte) this.readInt( unsigned, size );
	}

	@Override
	public short readShort( final boolean unsigned, final int size ) throws IOException {

		requireValidSizeShort( unsigned, size );
		return (short) this.readInt( unsigned, size );
	}

	@Override
	public int readInt( final boolean unsigned, final int size ) throws IOException {

		requireValidSizeInt( unsigned, size );
		if( !unsigned ) {
			int value = 0 - this.readInt( true, 1 );
			final int usize = size - 1;
			if( usize > 0 ) {
				value <<= usize;
				value |= this.readInt( true, usize );
			}
			return value;
		}
		int value = 0x00;
		final int quotient = size / Short.SIZE;
		for( int i = 0; i < quotient; i++ ) {
			value <<= Short.SIZE;
			value |= this.unsigned16( Short.SIZE );
		}
		final int remainder = size % Short.SIZE;
		if( remainder > 0 ) {
			value <<= remainder;
			value |= this.unsigned16( remainder );
		}
		return value;
	}

	@Override
	public long readLong( final boolean unsigned, final int size ) throws IOException {

		requireValidSizeLong( unsigned, size );
		if( !unsigned ) {
			long value = 0L - this.readLong( true, 1 );
			final int usize = size - 1;
			if( usize > 0 ) {
				value <<= usize;
				value |= this.readLong( true, usize );
			}
			return value;
		}
		long value = 0x00L;
		final int quotient = size / 31;
		for( int i = 0; i < quotient; i++ ) {
			value <<= 31;
			value |= this.readInt( true, 31 );
		}
		final int remainder = size % 31;
		if( remainder > 0 ) {
			value <<= remainder;
			value |= this.readInt( true, remainder );
		}
		return value;
	}

	// -----------------------------------------------------------------------------------------------------------------
	@Override
	public char readChar( final int size ) throws IOException {

		requireValidSizeChar( size );
		return (char) this.readInt( true, size );
	}

	// -----------------------------------------------------------------------------------------------------------------
	@Override
	public long align( final int bytes ) throws IOException {

		if( bytes <= 0 ) {
			throw new IllegalArgumentException( "bytes(" + bytes + ") <= 0" );
		}
		long bits = 0; // number of bits to be discarded
		if( this.available > 0 ) {
			bits += this.available;
			this.readInt( true, this.available );
		}
		for( ; this.count % bytes > 0; bits += Byte.SIZE ) {
			this.readInt( true, Byte.SIZE );
		}
		return bits;
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * The current octet.
	 */
	private int octet;

	/**
	 * The number of available bits in {@link #octet}.
	 */
	private int available = 0;

	/**
	 * The number of bytes read so far.
	 */
	private long count = 0L;
}
