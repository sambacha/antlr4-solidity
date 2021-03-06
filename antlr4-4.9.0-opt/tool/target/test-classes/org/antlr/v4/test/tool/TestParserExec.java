/*
 * Copyright (c) 2012 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD-3-Clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** Test parser execution.
 *
 *  For the non-greedy stuff, the rule is that .* or any other non-greedy loop
 *  (any + or * loop that has an alternative with '.' in it is automatically
 *  non-greedy) never sees past the end of the rule containing that loop.
 *  There is no automatic way to detect when the exit branch of a non-greedy
 *  loop has seen enough input to determine how much the loop should consume
 *  yet still allow matching the entire input. Of course, this is extremely
 *  inefficient, particularly for things like
 *
 *     block : '{' (block|.)* '}' ;
 *
 *  that need only see one symbol to know when it hits a '}'. So, I
 *  came up with a practical solution.  During prediction, the ATN
 *  simulator never fall off the end of a rule to compute the global
 *  FOLLOW. Instead, we terminate the loop, choosing the exit branch.
 *  Otherwise, we predict to reenter the loop.  For example, input
 *  "{ foo }" will allow the loop to match foo, but that's it. During
 *  prediction, the ATN simulator will see that '}' reaches the end of a
 *  rule that contains a non-greedy loop and stop prediction. It will choose
 *  the exit branch of the inner loop. So, the way in which you construct
 *  the rule containing a non-greedy loop dictates how far it will scan ahead.
 *  Include everything after the non-greedy loop that you know it must scan
 *  in order to properly make a prediction decision. these beasts are tricky,
 *  so be careful. don't liberally sprinkle them around your code.
 *
 *  To simulate filter mode, use ( .* (pattern1|pattern2|...) )*
 *
 *  Nongreedy loops match as much input as possible while still allowing
 *  the remaining input to match.
 */
public class TestParserExec extends BaseTest {
	@Test public void testLabels() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : b1=b b2+=b* b3+=';' ;\n" +
			"b : id=ID val+=INT*;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "abc 34;", false);
		assertEquals("", found);
		assertEquals(null, stderrDuringParse);
	}

	/**
	 * This is a regression test for #270 "Fix operator += applied to a set of
	 * tokens".
	 * https://github.com/antlr/antlr4/issues/270
	 */
	@Test public void testListLabelOnSet() {
		String grammar =
			"grammar T;\n" +
			"a : b b* ';' ;\n" +
			"b : ID val+=(INT | FLOAT)*;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"FLOAT : [0-9]+ '.' [0-9]+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "abc 34;", false);
		assertEquals("", found);
		assertEquals(null, stderrDuringParse);
	}

	@Test public void testBasic() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID INT {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "abc 34", false);
		assertEquals("abc34\n", found);
	}

	@Test public void testAorB() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID {System.out.println(\" alt 1\");}" +
			"  | INT {System.out.println(\"alt 2\");}" +
				";\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "34", false);
		assertEquals("alt 2\n", found);
	}

	@Test public void testAPlus() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID+ {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a b c", false);
		assertEquals("abc\n", found);
	}

	// force complex decision
	@Test public void testAorAPlus() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : (ID|ID)+ {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a b c", false);
		assertEquals("abc\n", found);
	}

	private static final String ifIfElseGrammarFormat =
		"grammar T;\n" +
		"start : statement+ ;\n" +
		"statement : 'x' | ifStatement;\n" +
		"ifStatement : 'if' 'y' statement %s {System.out.println($text);};\n" +
		"ID : 'a'..'z'+ ;\n" +
		"WS : (' '|'\\n') -> channel(HIDDEN);\n";

	@Test public void testIfIfElseGreedyBinding1() throws Exception {
		final String input = "if y if y x else x";
		final String expectedInnerBound = "if y x else x\nif y if y x else x\n";

		String grammar = String.format(ifIfElseGrammarFormat, "('else' statement)?");
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals(expectedInnerBound, found);

	}

	@Test public void testIfIfElseGreedyBinding2() throws Exception {
		final String input = "if y if y x else x";
		final String expectedInnerBound = "if y x else x\nif y if y x else x\n";

		String grammar = String.format(ifIfElseGrammarFormat, "('else' statement|)");
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals(expectedInnerBound, found);
	}

	@Test public void testIfIfElseNonGreedyBinding() throws Exception {
		final String input = "if y if y x else x";
		final String expectedOuterBound = "if y x\nif y if y x else x\n";

		String grammar = String.format(ifIfElseGrammarFormat, "('else' statement)??");
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals(expectedOuterBound, found);

		grammar = String.format(ifIfElseGrammarFormat, "(|'else' statement)");
		found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals(expectedOuterBound, found);
	}

	@Test public void testAStar() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ID* {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "", false);
		assertEquals("\n", found);
		found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a b c", false);
		assertEquals("abc\n", found);
	}

	@Test public void testLL1OptionalBlock() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : (ID|{}INT)? {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "", false);
		assertEquals("\n", found);
		found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a", false);
		assertEquals("a\n", found);
	}

	// force complex decision
	@Test public void testAorAStar() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : (ID|ID)* {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "", false);
		assertEquals("\n", found);
		found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a b c", false);
		assertEquals("abc\n", found);
	}

	@Test public void testAorBPlus() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : (ID|INT{;})+ {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a 34 c", false);
		assertEquals("a34c\n", found);
	}

	@Test public void testAorBStar() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : (ID|INT{;})* {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "", false);
		assertEquals("\n", found);
		found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a 34 c", false);
		assertEquals("a34c\n", found);
	}


	/**
	 * This test is meant to detect regressions of bug antlr/antlr4#41.
	 * https://github.com/antlr/antlr4/issues/41
	 */
	@Test
	public void testOptional1() throws Exception {
		String grammar =
			"grammar T;\n" +
			"stat : ifstat | 'x';\n" +
			"ifstat : 'if' stat ('else' stat)?;\n" +
			"WS : [ \\n\\t]+ -> skip ;"
			;

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "stat", "x", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testOptional2() throws Exception {
		String grammar =
				"grammar T;\n" +
				"stat : ifstat | 'x';\n" +
				"ifstat : 'if' stat ('else' stat)?;\n" +
				"WS : [ \\n\\t]+ -> skip ;"
				;

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "stat", "if x else x", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testOptional3() throws Exception {
		String grammar =
				"grammar T;\n" +
				"stat : ifstat | 'x';\n" +
				"ifstat : 'if' stat ('else' stat)?;\n" +
				"WS : [ \\n\\t]+ -> skip ;"
				;

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "stat", "if x", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testOptional4() throws Exception {
		String grammar =
				"grammar T;\n" +
				"stat : ifstat | 'x';\n" +
				"ifstat : 'if' stat ('else' stat)?;\n" +
				"WS : [ \\n\\t]+ -> skip ;"
				;

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "stat", "if if x else x", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/**
	 * This test is meant to test the expected solution to antlr/antlr4#42.
	 * https://github.com/antlr/antlr4/issues/42
	 */
	@Test
	public void testPredicatedIfIfElse() throws Exception {
		String grammar =
			"grammar T;\n" +
			"stmt : ifStmt | ID;\n" +
			"ifStmt : 'if' ID stmt (options{sll=true;} : 'else' stmt | );\n" +
			"ELSE : 'else';\n" +
			"ID : [a-zA-Z]+;\n" +
			"WS : [ \\n\\t]+ -> skip;\n"
			;

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "stmt",
								  "if x if x a else b", true);
		String expecting = "";
		assertEquals(expecting, found);
		assertNull(this.stderrDuringParse);
	}

	/**
	 * This is a regression test for antlr/antlr4#118.
	 * https://github.com/antlr/antlr4/issues/118
	 */
	@Ignore("Performance impact of passing this test may not be worthwhile")
	@Test public void testStartRuleWithoutEOF() {
		String grammar =
			"grammar T;\n"+
			"s @after {dumpDFA();}\n" +
			"  : ID | ID INT ID ;\n" +
			"ID : 'a'..'z'+ ;\n"+
			"INT : '0'..'9'+ ;\n"+
			"WS : (' '|'\\t'|'\\n')+ -> skip ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "s",
								   "abc 34", true);
		String expecting =
			"Decision 0:\n" +
			"s0-ID->s1\n" +
			"s1-INT->s2\n" +
			"s2-EOF->:s3=>1\n"; // Must point at accept state
		assertEquals(expecting, result);
		assertNull(this.stderrDuringParse);
	}

	/**
	 * This is a regression test for antlr/antlr4#195 "label 'label' type
	 * mismatch with previous definition: TOKEN_LABEL!=RULE_LABEL"
	 * https://github.com/antlr/antlr4/issues/195
	 */
	@Test public void testLabelAliasingAcrossLabeledAlternatives() throws Exception {
		String grammar =
			"grammar T;\n" +
			"start : a* EOF;\n" +
			"a\n" +
			"  : label=subrule {System.out.println($label.text);} #One\n" +
			"  | label='y' {System.out.println($label.text);} #Two\n" +
			"  ;\n" +
			"subrule : 'x';\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start",
								  "xy", false);
		assertEquals("x\ny\n", found);
	}

	/**
	 * This is a regression test for antlr/antlr4#334 "BailErrorStrategy: bails
	 * out on proper input".
	 * https://github.com/antlr/antlr4/issues/334
	 */
	@Test public void testPredictionIssue334() {
		String grammar =
			"grammar T;\n" +
			"\n" +
			"file @init{setErrorHandler(new BailErrorStrategy());} \n" +
			"@after {System.out.println($ctx.toStringTree(this));}\n" +
			"  :   item (SEMICOLON item)* SEMICOLON? EOF ;\n" +
			"item : A B?;\n" +
			"\n" +
			"\n" +
			"\n" +
			"SEMICOLON: ';';\n" +
			"\n" +
			"A : 'a'|'A';\n" +
			"B : 'b'|'B';\n" +
			"\n" +
			"WS      : [ \\r\\t\\n]+ -> skip;\n";

		String input = "a";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "file", input, false);
		assertEquals("(file (item a) <EOF>)\n", found);
		assertNull(stderrDuringParse);
	}

	/**
	 * This is a regressino test for antlr/antlr4#299 "Repeating subtree not
	 * accessible in visitor".
	 * https://github.com/antlr/antlr4/issues/299
	 */
	@Test public void testListLabelForClosureContext() throws Exception {
		String grammar =
			"grammar T;\n" +
			"ifStatement\n" +
			"@after { List<? extends ElseIfStatementContext> items = $ctx.elseIfStatement(); }\n" +
			"    : 'if' expression\n" +
			"      ( ( 'then'\n" +
			"          executableStatement*\n" +
			"          elseIfStatement*  // <--- problem is here\n" +
			"          elseStatement?\n" +
			"          'end' 'if'\n" +
			"        ) | executableStatement )\n" +
			"    ;\n" +
			"\n" +
			"elseIfStatement\n" +
			"    : 'else' 'if' expression 'then' executableStatement*\n" +
			"    ;\n"
			+ "expression : 'a' ;\n"
			+ "executableStatement : 'a' ;\n"
			+ "elseStatement : 'a' ;\n";
		String input = "a";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "expression", input, false);
		assertEquals("", found);
		assertNull(stderrDuringParse);
	}

	/**
	 * This test ensures that {@link ParserATNSimulator} produces a correct
	 * result when the grammar contains multiple explicit references to
	 * {@code EOF} inside of parser rules.
	 */
	@Test
	public void testMultipleEOFHandling() throws Exception {
		String grammar =
			"grammar T;\n" +
			"prog : ('x' | 'x' 'y') EOF EOF;\n";
		String input = "x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "prog", input, false);
		assertEquals("", found);
		assertNull(stderrDuringParse);
	}

	/**
	 * This test ensures that {@link ParserATNSimulator} does not produce a
	 * {@link StackOverflowError} when it encounters an {@code EOF} transition
	 * inside a closure.
	 */
	@Test
	public void testEOFInClosure() throws Exception {
		String grammar =
			"grammar T;\n" +
			"prog : stat EOF;\n" +
			"stat : 'x' ('y' | EOF)*?;\n";
		String input = "x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "prog", input, false);
		assertEquals("", found);
		assertNull(stderrDuringParse);
	}

	/**
	 * This is a regression test for antlr/antlr4#561 "Issue with parser
	 * generation in 4.2.2"
	 * https://github.com/antlr/antlr4/issues/561
	 */
	@Test public void testReferenceToATN() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : (ID|ATN)* ATN? {System.out.println($text);} ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"ATN : '0'..'9'+;\n" +
			"WS : (' '|'\\n') -> skip ;\n";

		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "", false);
		assertEquals("\n", found);
		found = execParser("T.g4", grammar, "TParser", "TLexer", "a",
								  "a 34 c", false);
		assertEquals("a34c\n", found);
	}

	/**
	 * This is a regression test for antlr/antlr4#588 "ClassCastException during
	 * semantic predicate handling".
	 * https://github.com/antlr/antlr4/issues/588
	 */
	@Test public void testFailedPredicateExceptionState() throws Exception {
		String grammar = load("Psl.g4", "UTF-8");
		String found = execParser("Psl.g4", grammar, "PslParser", "PslLexer", "floating_constant", " . 234", false);
		assertEquals("", found);
		assertEquals("line 1:6 rule floating_constant DEC:A floating-point constant cannot have internal white space\n", stderrDuringParse);
	}

	/**
	 * This is a regression test for antlr/antlr4#563 "Inconsistent token
	 * handling in ANTLR4".
	 * https://github.com/antlr/antlr4/issues/563
	 */
	@Test public void testAlternateQuotes() throws Exception {
		String lexerGrammar =
			"lexer grammar ModeTagsLexer;\n" +
			"\n" +
			"// Default mode rules (the SEA)\n" +
			"OPEN  : '??'     -> mode(ISLAND) ;       // switch to ISLAND mode\n" +
			"TEXT  : ~'??'+ ;                         // clump all text together\n" +
			"\n" +
			"mode ISLAND;\n" +
			"CLOSE : '??'     -> mode(DEFAULT_MODE) ; // back to SEA mode \n" +
			"SLASH : '/' ;\n" +
			"ID    : [a-zA-Z]+ ;                     // match/send ID in tag to parser\n";
		String parserGrammar =
			"parser grammar ModeTagsParser;\n" +
			"\n" +
			"options { tokenVocab=ModeTagsLexer; } // use tokens from ModeTagsLexer.g4\n" +
			"\n" +
			"file: (tag | TEXT)* ;\n" +
			"\n" +
			"tag : '??' ID '??'\n" +
			"    | '??' '/' ID '??'\n" +
			"    ;";

		boolean success = rawGenerateAndBuildRecognizer("ModeTagsLexer.g4",
														lexerGrammar,
														null,
														"ModeTagsLexer");
		assertTrue(success);

		String found = execParser("ModeTagsParser.g4", parserGrammar, "ModeTagsParser", "ModeTagsLexer", "file", "", false);
		assertEquals("", found);
		assertNull(stderrDuringParse);
	}

	/**
	 * This is a regression test for tunnelvisionlabs/antlr4cs#71 "Erroneous
	 * extraneous input detected in C# (but not in Java)".
	 * https://github.com/tunnelvisionlabs/antlr4cs/issues/71
	 */
	@Test
	public void testCSharpIssue71() {
		String grammar =
			"grammar Expr;\n" +
			"\n" +
			"root\n" +
			"	:	assignment EOF\n" +
			"	;\n" +
			"\n" +
			"assignment\n" +
			"	:	LOCAL_VARIABLE '=' expression\n" +
			"	;\n" +
			"\n" +
			"expression\n" +
			"	:	logical_and_expression\n" +
			"	;\n" +
			"\n" +
			"logical_and_expression\n" +
			"	:	relational_expression ('AND' relational_expression)*\n" +
			"	;\n" +
			"\n" +
			"relational_expression\n" +
			"	:	primary_expression (('<'|'>') primary_expression)*\n" +
			"	;\n" +
			"\n" +
			"primary_expression\n" +
			"	:	'(' + expression + ')'\n" +
			"	|	UNSIGNED_INT\n" +
			"	|	LOCAL_VARIABLE\n" +
			"	;\n" +
			"\n" +
			"LOCAL_VARIABLE\n" +
			"	:	[_a-z][_a-zA-Z0-9]*\n" +
			"	;\n" +
			"\n" +
			"UNSIGNED_INT\n" +
			"	:	('0'|'1'..'9''0'..'9'*)\n" +
			"	;\n" +
			"\n" +
			"WS\n" +
			"	:	[ \\t\\r\\n]+ -> channel(HIDDEN)\n" +
			"	;\n";

		String input = "b = (((a > 10)) AND ((a < 15)))";
		String found = execParser("Expr.g4", grammar, "ExprParser", "ExprLexer", "root",
								  input, false);
		assertEquals("", found);
		assertNull(stderrDuringParse);
	}

	/**
	 * This is a regression test for antlr/antlr4#672 "Initialization failed in
	 * locals".
	 * https://github.com/antlr/antlr4/issues/672
	 */
	@Test public void testAttributeValueInitialization() throws Exception {
		String grammar =
			"grammar Data; \n" +
			"\n" +
			"file : group+ EOF; \n" +
			"\n" +
			"group: INT sequence {System.out.println($sequence.values.size());} ; \n" +
			"\n" +
			"sequence returns [List<Integer> values = new ArrayList<Integer>()] \n" +
			"  locals[List<Integer> localValues = new ArrayList<Integer>()]\n" +
			"         : (INT {$localValues.add($INT.int);})* {$values.addAll($localValues);}\n" +
			"; \n" +
			"\n" +
			"INT : [0-9]+ ; // match integers \n" +
			"WS : [ \\t\\n\\r]+ -> skip ; // toss out all whitespace\n";

		String input = "2 9 10 3 1 2 3";
		String found = execParser("Data.g4", grammar, "DataParser", "DataLexer", "file", input, false);
		assertEquals("6\n", found);
		assertNull(stderrDuringParse);
	}

	@Test
	public void testParserProperty() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {\n" +
			"boolean Property() {\n" +
			"	return true;\n" +
			"}\n" +
			"}\n" +
			"a : {$parser.Property()}? ID {System.out.println(\"valid\");}\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "abc", false);
		assertEquals("valid\n", found);
		assertNull(this.stderrDuringParse);
	}
}
