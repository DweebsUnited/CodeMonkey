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

import java.io.DataInput;
import java.io.IOException;


/**
 * A {@link ByteInput} uses an instance of {@link DataInput} as its
 * {@link #source}.
 *
 * @param <T> data input type parameter.
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 * @see DataByteOutput
 */
public class DataByteInput< T extends DataInput > extends AbstractByteInput< T > {

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * Creates a new instance built on top of given {@code source}.
	 *
	 * @param source the byte source; {@code null} if it's supposed to be lazily
	 *               initialized and set
	 */
	public DataByteInput( final T source ) {

		super( source );
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * {@inheritDoc} The {@code read()} method of {@code DataByteInput} class
	 * invokes {@link DataInput#readUnsignedByte()}, on what {@link #getSource()}
	 * gives, and returns the result. Override this method if the {@link #source} is
	 * supposed to be lazily initialized and set.
	 *
	 * @return {@inheritDoc}
	 * @throws IOException {@inheritDoc}
	 */
	@Override
	public int read( ) throws IOException {

		return this.getSource( ).readUnsignedByte( );
	}

	// ----------------------------------------------------------------------------------------------------------
	// source

	/**
	 * {@inheritDoc}
	 *
	 * @param source {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	public DataByteInput< T > source( final T source ) {

		return (DataByteInput< T >) super.source( source );
	}
}
