/*
 * Copyright 2013 <a href="mailto:onacit@gmail.com">Jin Kwon</a>.
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
 * A {@link ByteInput} reads bytes from an {@link InputStream}.
 *
 * @param <T> input stream type parameter
 * @author Jin Kwon &lt;onacit at gmail.com&gt;
 * @see StreamByteOutput
 */
public class StreamByteInput< T extends InputStream > extends AbstractByteInput< T > {

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * Creates a new instance built on top of the specified input stream.
	 *
	 * @param source the input stream; {@code null} if it's supposed to be lazily
	 *               initialized and set
	 */
	public StreamByteInput( final T source ) {

		super( source );
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * {@inheritDoc} The {@code read()} method of {@code StreamByteInput} class
	 * invokes {@link InputStream#read()}, on what {@link #getSource()} gives, and
	 * returns the result. Override this method if the {@link #source} is supposed
	 * to be lazily initialized and set.
	 *
	 * @return {@inheritDoc}
	 * @throws IOException {@inheritDoc}
	 * @see #getSource()
	 * @see InputStream#read()
	 */
	@Override
	public int read( ) throws IOException {

		final int value = this.getSource( ).read( );
		if( value == -1 ) {
			throw new EOFException( "reached to an end" );
		}
		return value;
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
	public StreamByteInput< T > source( final T source ) {

		return (StreamByteInput< T >) super.source( source );
	}
}
