/*
 * Copyright (c) 2012 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD-3-Clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.parse;

import org.antlr.runtime.IntStream;
import org.antlr.runtime.RecognitionException;

/** */
public class v4ParserException extends RecognitionException {
	private static final long serialVersionUID = -7954962343881278338L;

	public String msg;
	/** Used for remote debugger deserialization */
	public v4ParserException() {}

	public v4ParserException(String msg, IntStream input) {
		super(input);
		this.msg = msg;
	}

}
