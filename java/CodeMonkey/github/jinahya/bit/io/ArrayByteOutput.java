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

import java.io.IOException;
import java.io.OutputStream;


/**
 * A byte output writes byte to an array of bytes.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public class ArrayByteOutput extends AbstractByteOutput< byte[ ] > {

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * Creates a new instance of byte output which writes bytes to given output
	 * stream using an array of bytes whose {@code length} equals to specified.
	 *
	 * @param length the length of byte array; must be positive.
	 * @param stream the output stream to which bytes are written; must be not
	 *               {@code null}.
	 * @return an instance byte output.
	 */
	public static ArrayByteOutput of( final int length, final OutputStream stream ) {

		if( length <= 0 ) {
			throw new IllegalArgumentException( "length(" + length + ") <= 0" );
		}
		if( stream == null ) {
			throw new NullPointerException( "stream is null" );
		}
		return new ArrayByteOutput( null, -1, -1 ) {

			@Override
			public void write( final int value ) throws IOException {

				if( this.target == null ) {
					this.target = new byte[ length ];
					this.index = 0;
					this.limit = this.target.length;
				}
				super.write( value );
				if( this.index == this.limit ) {
					stream.write( this.target );
					this.index = 0;
				}
			}
		};
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * Creates a new instance with given parameters.
	 *
	 * @param target a byte array
	 * @param index  the index in array to write
	 * @param limit  the index in array that {@link #index} can exceed
	 */
	public ArrayByteOutput( final byte[ ] target, final int index, final int limit ) {

		super( target );
		this.index = index;
		this.limit = limit;
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * {@inheritDoc} The {@code write(int)} method of {@code ArrayByteOutput} class
	 * sets {@code getTarget[getIndex()]} with given value and increments the
	 * {@link #index} using {@link #setIndex(int)} method. Override this method if
	 * either {@link #target}, {@link #index}, or {@link #limit} needs to be lazily
	 * initialized and/or adjusted.
	 *
	 * @param value {@inheritDoc}
	 * @throws IOException {@inheritDoc}
	 */
	@Override
	public void write( final int value ) throws IOException {

		final byte[ ] t = this.getTarget( );
		if( t == null ) {
			throw new IllegalStateException( "target is currently null" );
		}
		final int i = this.getIndex( );
		if( i < 0 ) {
			throw new IllegalStateException( "index(" + i + ") < 0" );
		}
		if( i >= t.length ) {
			throw new IllegalStateException( "index(" + i + ") >= target.length(" + t.length + ")" );
		}
		final int l = this.getLimit( );
		if( l <= i ) {
			throw new IllegalStateException( "limit(" + l + ") <= index(" + i + ")" );
		}
		if( l > t.length ) {
			throw new IllegalStateException( "limit(" + l + ") > target.length(" + t.length + ")" );
		}
		t[ i ] = (byte) value;
		this.setIndex( i + 1 );
	}

	// ----------------------------------------------------------------------------------------------------------
	// target

	/**
	 * Replaces the {@link #target} with given and returns self.
	 *
	 * @param target new value for {@link #target}
	 * @return this instance.
	 */
	@Override
	public ArrayByteOutput target( final byte[ ] target ) {

		return (ArrayByteOutput) super.target( target );
	}

	// -----------------------------------------------------------------------------------------------------------
	// index

	/**
	 * Returns the current value of {@link #index}
	 *
	 * @return the current value of {@link #index}.
	 */
	public int getIndex( ) {

		return this.index;
	}

	/**
	 * Replaces the {@link #index} with given.
	 *
	 * @param index new value for {@link #index}
	 */
	public void setIndex( final int index ) {

		this.index = index;
	}

	/**
	 * Replaces the {@link #index} with given and returns self.
	 *
	 * @param index new value for {@link #index}
	 * @return this instance.
	 */
	public ArrayByteOutput index( final int index ) {

		this.setIndex( index );
		return this;
	}

	// -----------------------------------------------------------------------------------------------------------
	// limit

	/**
	 * Returns the current value of {@link #limit}.
	 *
	 * @return the current value of {@link #limit}
	 */
	public int getLimit( ) {

		return this.limit;
	}

	/**
	 * Replaces the current value of {@link #limit} with given.
	 *
	 * @param limit new value for {@link #limit}
	 */
	public void setLimit( int limit ) {

		this.limit = limit;
	}

	/**
	 * Replaces the current value of {@link #limit} with given and returns this
	 * instance.
	 *
	 * @param limit new value for {@link #limit}
	 * @return this instance.
	 */
	public ArrayByteOutput limit( final int limit ) {

		this.setLimit( limit );
		return this;
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * The index in the {@link #target} to write.
	 */
	protected int index;

	/**
	 * The index in the {@link #target} that {@link #index} can't exceed.
	 */
	protected int limit;
}
