/*
 * Copyright (c) 2012 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD-3-Clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

/** The Tokens rule start state linking to each lexer rule start state */
public final class TokensStartState extends DecisionState {

	@Override
	public int getStateType() {
		return TOKEN_START;
	}
}
