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

import java.io.IOException;


/**
 * An interface for writing bytes.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
//@FunctionalInterface
public interface ByteOutput {

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * Writes given unsigned 8-bit value.
	 *
	 * @param value an unsigned 8-bit value between {@code 0} and {@code 255}, both
	 *              inclusive.
	 * @throws IOException if an I/O error occurs.
	 */
	void write( int value ) throws IOException;
}
