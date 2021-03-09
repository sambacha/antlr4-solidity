/*
 * Copyright (c) 2012 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD-3-Clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializationOptions;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ATNSerializer;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TestATNDeserialization extends BaseTest {
	@Test public void testSimpleNoBlock() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A B ;");
		checkDeserializationIsStable(g);
	}

	@Test public void testEOF() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : EOF ;");
		checkDeserializationIsStable(g);
	}

	@Test public void testEOFInSet() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : (EOF|A) ;");
		checkDeserializationIsStable(g);
	}

	@Test public void testNot() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"tokens {A, B, C}\n" +
			"a : ~A ;");
		checkDeserializationIsStable(g);
	}

	@Test public void testWildcard() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"tokens {A, B, C}\n" +
			"a : . ;");
		checkDeserializationIsStable(g);
	}

	@Test public void testPEGAchillesHeel() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A | A B ;");
		checkDeserializationIsStable(g);
	}

	@Test public void test3Alts() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A | A B | A B C ;");
		checkDeserializationIsStable(g);
	}

	@Test public void testSimpleLoop() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A+ B ;");
		checkDeserializationIsStable(g);
	}

	@Test public void testRuleRef() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : e ;\n" +
			"e : E ;\n");
		checkDeserializationIsStable(g);
	}

	@Test public void testLexerTwoRules() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' ;\n" +
			"B : 'b' ;\n");
		checkDeserializationIsStable(lg);
	}

	@Test public void testLexerEOF() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' EOF ;\n");
		checkDeserializationIsStable(lg);
	}

	@Test public void testLexerEOFInSet() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' (EOF|'\\n') ;\n");
		checkDeserializationIsStable(lg);
	}

	@Test public void testLexerRange() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"INT : '0'..'9' ;\n");
		checkDeserializationIsStable(lg);
	}

	@Test public void testLexerLoops() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"INT : '0'..'9'+ ;\n");
		checkDeserializationIsStable(lg);
	}

	@Test public void testLexerNotSet() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b')\n ;");
		checkDeserializationIsStable(lg);
	}

	@Test public void testLexerNotSetWithRange() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b'|'e'|'p'..'t')\n ;");
		checkDeserializationIsStable(lg);
	}

	@Test public void testLexerNotSetWithRange2() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b') ~('e'|'p'..'t')\n ;");
		checkDeserializationIsStable(lg);
	}

	@Test public void test2ModesInLexer() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a'\n ;\n" +
			"mode M;\n" +
			"B : 'b';\n" +
			"mode M2;\n" +
			"C : 'c';\n");
		checkDeserializationIsStable(lg);
	}

	protected void checkDeserializationIsStable(Grammar g) {
		ATN atn = createATN(g, false);
		char[] data = Utils.toCharArray(ATNSerializer.getSerialized(atn, Arrays.asList(g.getRuleNames())));
		String atnData = ATNSerializer.getDecoded(atn, Arrays.asList(g.getRuleNames()), Arrays.asList(g.getTokenNames()));
		ATNDeserializationOptions options = new ATNDeserializationOptions();
		options.setOptimize(false);
		ATN atn2 = new ATNDeserializer(options).deserialize(data);
		String atn2Data = ATNSerializer.getDecoded(atn2, Arrays.asList(g.getRuleNames()), Arrays.asList(g.getTokenNames()));

		assertEquals(atnData, atn2Data);
	}
}
