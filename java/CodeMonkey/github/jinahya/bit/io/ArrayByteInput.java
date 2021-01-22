/*
 * Copyright 2015 Jin Kwon &lt;jinahya_at_gmail.com&gt;.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jinahya.bit.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;


/**
 * A byte input reading bytes from an array of bytes.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public class ArrayByteInput extends AbstractByteInput< byte[ ] > {

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * Creates a new instance of {@link ArrayByteInput} which reads bytes from given
	 * input stream using an array of bytes whose {@code length} equals to
	 * specified.
	 *
	 * @param length the length of the byte array; must be positive.
	 * @param stream the input stream from which bytes are read; must be not
	 *               {@code null}.
	 * @return a new instance of {@link ArrayByteInput}.
	 */
	public static ArrayByteInput of( final int length, final InputStream stream ) {

		if( length <= 0 ) {
			throw new IllegalArgumentException( "length(" + length + ") <= 0" );
		}
		if( stream == null ) {
			throw new NullPointerException( "stream is null" );
		}
		return new ArrayByteInput( null, -1, -1 ) {

			@Override
			public int read( ) throws IOException {

				if( this.source == null ) {
					this.source = new byte[ length ];
					this.index = this.source.length;
					this.limit = this.source.length;
				}
				if( this.index == this.limit ) {
					this.limit = stream.read( this.source );
					if( this.limit == -1 ) {
						throw new EOFException( "reached to an end; " + stream );
					}
					assert this.limit > 0;
					this.index = 0;
				}
				return super.read( );
			}
		};
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * Creates a new instance with given arguments.
	 *
	 * @param source a byte array; {@code null} if it's supposed to be lazily
	 *               initialized an set.
	 * @param index  array index to read
	 * @param limit  array index to limit
	 */
	public ArrayByteInput( final byte[ ] source, final int index, final int limit ) {

		super( source );
		this.index = index;
		this.limit = limit;
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * {@inheritDoc} The {@code read()} method of {@code ArrayByteInput} class
	 * returns {@code source[index]} as an unsigned 8-bit value and increments the
	 * {@link #index}. Override this method if either {@link #source},
	 * {@link #index}, or {@link #limit} needs to be lazily initialized or adjusted.
	 *
	 * @return {@inheritDoc}
	 * @throws IOException {@inheritDoc}
	 */
	@Override
	public int read( ) throws IOException {

		final byte[ ] s = this.getSource( );
		if( s == null ) {
			throw new IllegalStateException( "source is currently null" );
		}
		final int i = this.getIndex( );
		if( i < 0 ) {
			throw new IllegalStateException( "index(" + i + ") < 0" );
		}
		if( i >= s.length ) {
			throw new IllegalStateException( "index(" + i + ") >= source.length(" + s.length + ")" );
		}
		final int l = this.getLimit( );
		if( l <= i ) {
			throw new IllegalStateException( "limit(" + l + ") <= index(" + i + ")" );
		}
		if( l > s.length ) {
			throw new IllegalStateException( "limit(" + l + ") > source.length(" + s.length + ")" );
		}
		final int result = s[ i ] & 0xFF;
		this.setIndex( i + 1 );
		return result;
	}

	// ----------------------------------------------------------------------------------------------------------
	// source
	@Override
	public ArrayByteInput source( final byte[ ] target ) {

		return (ArrayByteInput) super.source( target );
	}

	// -----------------------------------------------------------------------------------------------------------
	// index

	/**
	 * Returns the current value of {@link #index}.
	 *
	 * @return current value of {@link #index}.
	 */
	public int getIndex( ) {

		return this.index;
	}

	/**
	 * Replaces the current value of {@link #index} with given.
	 *
	 * @param index new value for {@link #index}
	 */
	public void setIndex( final int index ) {

		this.index = index;
	}

	/**
	 * Replaces the current value of {@link #index} with given and returns this
	 * instance.
	 *
	 * @param index new value for {@link #index}
	 * @return this instance.
	 * @see #setIndex(int)
	 */
	public ArrayByteInput index( final int index ) {

		this.setIndex( index );
		return this;
	}

	// -----------------------------------------------------------------------------------------------------------
	// limit

	/**
	 * Returns the value of {@link #limit}
	 *
	 * @return the value of {@link #limit}
	 */
	public int getLimit( ) {

		return this.limit;
	}

	/**
	 * Replaces the value of {@link #limit} with given.
	 *
	 * @param limit new value of {@link #limit}
	 */
	public void setLimit( final int limit ) {

		this.limit = limit;
	}

	/**
	 * Replaces the value of {@link #limit} with given and returns this instance.
	 *
	 * @param limit new value of {@link #limit}
	 * @return this instance
	 * @see #setLimit(int)
	 */
	public ArrayByteInput limit( final int limit ) {

		this.limit = limit;
		return this;
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * The index in the {@link #source} that {@link #index} can't exceeds.
	 */
	protected int limit;

	/**
	 * The index in the {@link #source} to read.
	 */
	protected int index;
}
