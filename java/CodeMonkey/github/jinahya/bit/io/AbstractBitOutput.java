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

import java.io.IOException;


/**
 * An abstract class for implementing {@link BitInput}.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 * @see AbstractBitInput
 */
public abstract class AbstractBitOutput implements BitOutput {

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * Writes given unsigned 8-bit integer.
	 *
	 * @param value the unsigned 8-bit integer to write
	 * @throws IOException if an I/O error occurs.
	 */
	protected abstract void write( int value ) throws IOException;

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * Writes an unsigned value whose size is, in maximum, {@value Byte#SIZE}.
	 *
	 * @param size  the number of lower bits to write; between {@code 1} and
	 *              {@value Byte#SIZE}, both inclusive.
	 * @param value the value to write
	 * @throws IOException if an I/O error occurs.
	 */
	protected void unsigned8( final int size, int value ) throws IOException {

		BitIoConstraints.requireValidSizeUnsigned8( size );
		final int required = size - this.available;
		if( required > 0 ) {
			this.unsigned8( size - required, value >> required );
			this.unsigned8( required, value );
			return;
		}
		this.octet <<= size;
		this.octet |= ( ( ( 1 << size ) - 1 ) & value );
		this.available -= size;
		if( this.available == 0 ) {
			this.write( this.octet );
			this.count++;
			this.octet = 0x00;
			this.available = Byte.SIZE;
		}
	}

	/**
	 * Writes an unsigned value whose size is max {@value Short#SIZE}.
	 *
	 * @param size  the number of lower bits to write; between {@code 1} and
	 *              {@value Short#SIZE}, both inclusive.
	 * @param value the value to write
	 * @throws IOException if an I/O error occurs
	 */
	protected void unsigned16( final int size, final int value ) throws IOException {

		BitIoConstraints.requireValidSizeUnsigned16( size );
		final int quotient = size / Byte.SIZE;
		final int remainder = size % Byte.SIZE;
		if( remainder > 0 ) {
			this.unsigned8( remainder, value >> ( quotient * Byte.SIZE ) );
		}
		for( int i = quotient - 1; i >= 0; i-- ) {
			this.unsigned8( Byte.SIZE, value >> ( Byte.SIZE * i ) );
		}
	}

	// -----------------------------------------------------------------------------------------------------------------
	@Override
	public void writeBoolean( final boolean value ) throws IOException {

		this.writeInt( true, 1, value ? 1 : 0 );
	}

	@Override
	public void writeByte( final boolean unsigned, final int size, final byte value ) throws IOException {

		BitIoConstraints.requireValidSizeByte( unsigned, size );
		this.writeInt( unsigned, size, value );
	}

	@Override
	public void writeShort( final boolean unsigned, final int size, final short value ) throws IOException {

		BitIoConstraints.requireValidSizeShort( unsigned, size );
		this.writeInt( unsigned, size, value );
	}

	@Override
	public void writeInt( final boolean unsigned, final int size, final int value ) throws IOException {

		BitIoConstraints.requireValidSizeInt( unsigned, size );
		final int quotient = size / Short.SIZE;
		final int remainder = size % Short.SIZE;
		if( remainder > 0 ) {
			this.unsigned16( remainder, value >> ( quotient * Short.SIZE ) );
		}
		for( int i = Short.SIZE * ( quotient - 1 ); i >= 0; i -= Short.SIZE ) {
			this.unsigned16( Short.SIZE, value >> i );
		}
	}

	@Override
	public void writeLong( final boolean unsigned, final int size, final long value ) throws IOException {

		BitIoConstraints.requireValidSizeLong( unsigned, size );
		final int quotient = size / Integer.SIZE;
		final int remainder = size % Integer.SIZE;
		if( remainder > 0 ) {
			this.writeInt( false, remainder, (int) ( value >> ( quotient * Integer.SIZE ) ) );
		}
		for( int i = Integer.SIZE * ( quotient - 1 ); i >= 0; i -= Integer.SIZE ) {
			this.writeInt( false, Integer.SIZE, (int) ( value >> i ) );
		}
	}

	@Override
	public void writeChar( final int size, final char value ) throws IOException {

		BitIoConstraints.requireValidSizeChar( size );
		this.writeInt( true, size, value );
	}

	@Override
	public long align( final int bytes ) throws IOException {

		if( bytes <= 0 ) {
			throw new IllegalArgumentException( "bytes(" + bytes + ") <= 0" );
		}
		long bits = 0;
		if( this.available < Byte.SIZE ) {
			bits += this.available;
			this.writeInt( true, this.available, 0x00 );
		}
		for( ; this.count % bytes > 0; bits += Byte.SIZE ) {
			this.writeInt( true, Byte.SIZE, 0x00 );
		}
		return bits;
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * The current value of written bits.
	 */
	private int octet;

	/**
	 * The number of bits available to write.
	 */
	private int available = Byte.SIZE;

	/**
	 * The number of bytes written so far.
	 */
	private long count = 0L;
}
