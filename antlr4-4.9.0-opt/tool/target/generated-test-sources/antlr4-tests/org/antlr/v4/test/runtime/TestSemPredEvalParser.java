package org.antlr.v4.test.runtime;

import org.antlr.v4.test.tool.BaseTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestSemPredEvalParser extends BaseTest {

	@Test
	public void test2UnpredicatedAlts() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(317);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("s : {_interp.setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);} a ';' a; // do 2x: once in ATN, next in DFA\n");
		grammarBuilder.append("a : ID {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("  | ID {System.out.println(\"alt 2\");}\n");
		grammarBuilder.append("  | {false}? ID {System.out.println(\"alt 3\");}\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="x; y";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, true);
		assertEquals(
			"alt 1\n" +
			"alt 1\n", found);

		assertEquals(
			"line 1:0 reportAttemptingFullContext d=0 (a), input='x'\n" +
			"line 1:0 reportAmbiguity d=0 (a): ambigAlts={1, 2}, input='x'\n" +
			"line 1:3 reportAttemptingFullContext d=0 (a), input='y'\n" +
			"line 1:3 reportAmbiguity d=0 (a): ambigAlts={1, 2}, input='y'\n", this.stderrDuringParse);

	}

	@Test
	public void test2UnpredicatedAltsAndOneOrthogonalAlt() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(376);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("s : {_interp.setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);} a ';' a ';' a;\n");
		grammarBuilder.append("a : INT {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("  | ID {System.out.println(\"alt 2\");} // must pick this one for ID since pred is false\n");
		grammarBuilder.append("  | ID {System.out.println(\"alt 3\");}\n");
		grammarBuilder.append("  | {false}? ID {System.out.println(\"alt 4\");}\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="34; x; y";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, true);
		assertEquals(
			"alt 1\n" +
			"alt 2\n" +
			"alt 2\n", found);

		assertEquals(
			"line 1:4 reportAttemptingFullContext d=0 (a), input='x'\n" +
			"line 1:4 reportAmbiguity d=0 (a): ambigAlts={2, 3}, input='x'\n" +
			"line 1:7 reportAttemptingFullContext d=0 (a), input='y'\n" +
			"line 1:7 reportAmbiguity d=0 (a): ambigAlts={2, 3}, input='y'\n", this.stderrDuringParse);

	}

	@Test
	public void testActionHidesPreds() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(247);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("@parser::members {int i = 0;}\n");
		grammarBuilder.append("s : a+ ;\n");
		grammarBuilder.append("a : {this.i = 1;} ID {this.i == 1}? {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("  | {this.i = 2;} ID {this.i == 2}? {System.out.println(\"alt 2\");}\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="x x y";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"alt 1\n" +
			"alt 1\n" +
			"alt 1\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testActionsHidePredsInGlobalFOLLOW() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(299);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("@parser::members {\n");
		grammarBuilder.append("boolean pred(boolean v) {\n");
		grammarBuilder.append("	System.out.println(\"eval=\"+v);\n");
		grammarBuilder.append("	return v;\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("s : e {} {this.pred(true)}? {System.out.println(\"parse\");} '!' ;\n");
		grammarBuilder.append("t : e {} {this.pred(false)}? ID ;\n");
		grammarBuilder.append("e : ID | ; // non-LL(1) so we use ATN\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="a!";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"eval=true\n" +
			"parse\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testAtomWithClosureInTranslatedLRRule() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(94);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("start : e[0] EOF;\n");
		grammarBuilder.append("e[int _p]\n");
		grammarBuilder.append("    :   ( 'a' | 'b'+ ) ( {3 >= $_p}? '+' e[4] )*\n");
		grammarBuilder.append("    ;\n");
		String grammar = grammarBuilder.toString();


		String input ="a+b+a";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDepedentPredsInGlobalFOLLOW() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(322);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("@parser::members {\n");
		grammarBuilder.append("boolean pred(boolean v) {\n");
		grammarBuilder.append("	System.out.println(\"eval=\"+v);\n");
		grammarBuilder.append("	return v;\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("s : a[99] ;\n");
		grammarBuilder.append("a[int i] : e {this.pred($i==99)}? {System.out.println(\"parse\");} '!' ;\n");
		grammarBuilder.append("b[int i] : e {this.pred($i==99)}? ID ;\n");
		grammarBuilder.append("e : ID | ; // non-LL(1) so we use ATN\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="a!";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"eval=true\n" +
			"parse\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDependentPredNotInOuterCtxShouldBeIgnored() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(284);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("s : b[2] ';' |  b[2] '.' ; // decision in s drills down to ctx-dependent pred in a;\n");
		grammarBuilder.append("b[int i] : a[i] ;\n");
		grammarBuilder.append("a[int i]\n");
		grammarBuilder.append("  : {$i==1}? ID {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("    | {$i==2}? ID {System.out.println(\"alt 2\");}\n");
		grammarBuilder.append("    ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;\n");
		String grammar = grammarBuilder.toString();


		String input ="a;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals("alt 2\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDisabledAlternative() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(121);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("cppCompilationUnit : content+ EOF;\n");
		grammarBuilder.append("content: anything | {false}? .;\n");
		grammarBuilder.append("anything: ANY_CHAR;\n");
		grammarBuilder.append("ANY_CHAR: [_a-zA-Z0-9];");
		String grammar = grammarBuilder.toString();


		String input ="hello";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "cppCompilationUnit", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testIndependentPredNotPassedOuterCtxToAvoidCastException() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(197);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("s : b ';' |  b '.' ;\n");
		grammarBuilder.append("b : a ;\n");
		grammarBuilder.append("a\n");
		grammarBuilder.append("  : {false}? ID {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("  | {true}? ID {System.out.println(\"alt 2\");}\n");
		grammarBuilder.append(" ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="a;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals("alt 2\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testNoTruePredsThrowsNoViableAlt() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(185);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("s : a a;\n");
		grammarBuilder.append("a : {false}? ID INT {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("  | {false}? ID INT {System.out.println(\"alt 2\");}\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="y 3 x 4";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals("", found);

		assertEquals("line 1:0 no viable alternative at input 'y'\n", this.stderrDuringParse);

	}

	@Test
	public void testOrder() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(311);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("s : a {} a; // do 2x: once in ATN, next in DFA;\n");
		grammarBuilder.append("// action blocks lookahead from falling off of 'a'\n");
		grammarBuilder.append("// and looking into 2nd 'a' ref. !ctx dependent pred\n");
		grammarBuilder.append("a : ID {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("  | {true}?  ID {System.out.println(\"alt 2\");}\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="x y";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"alt 1\n" +
			"alt 1\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testPredFromAltTestedInLoopBack_1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(211);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("file_\n");
		grammarBuilder.append("@after {System.out.println($ctx.toStringTree(this));}\n");
		grammarBuilder.append("  : para para EOF ;\n");
		grammarBuilder.append("para: paraContent NL NL ;\n");
		grammarBuilder.append("paraContent : ('s'|'x'|{this._input.LA(2)!=TParser.NL}? NL)+ ;\n");
		grammarBuilder.append("NL : '\\n' ;\n");
		grammarBuilder.append("s : 's' ;\n");
		grammarBuilder.append("X : 'x' ;");
		String grammar = grammarBuilder.toString();


		String input =
			"s\n" +
			"\n" +
			"\n" +
			"x\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "file_", input, true);
		assertEquals("(file_ (para (paraContent s) \\n \\n) (para (paraContent \\n x \\n)) <EOF>)\n", found);

		assertEquals(
			"line 5:0 mismatched input '<EOF>' expecting {'s', '\n" +
			"', 'x'}\n", this.stderrDuringParse);

	}

	@Test
	public void testPredFromAltTestedInLoopBack_2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(211);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("file_\n");
		grammarBuilder.append("@after {System.out.println($ctx.toStringTree(this));}\n");
		grammarBuilder.append("  : para para EOF ;\n");
		grammarBuilder.append("para: paraContent NL NL ;\n");
		grammarBuilder.append("paraContent : ('s'|'x'|{this._input.LA(2)!=TParser.NL}? NL)+ ;\n");
		grammarBuilder.append("NL : '\\n' ;\n");
		grammarBuilder.append("s : 's' ;\n");
		grammarBuilder.append("X : 'x' ;");
		String grammar = grammarBuilder.toString();


		String input =
			"s\n" +
			"\n" +
			"\n" +
			"x\n" +
			"\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "file_", input, true);
		assertEquals("(file_ (para (paraContent s) \\n \\n) (para (paraContent \\n x) \\n \\n) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testPredTestedEvenWhenUnAmbig_1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(228);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("@parser::members {boolean enumKeyword = true;}\n");
		grammarBuilder.append("primary\n");
		grammarBuilder.append("    :   ID {System.out.println(\"ID \" + $ID.text);}\n");
		grammarBuilder.append("    |   {!this.enumKeyword}? 'enum' {System.out.println(\"enum\");}\n");
		grammarBuilder.append("    ;\n");
		grammarBuilder.append("ID : [a-z]+ ;\n");
		grammarBuilder.append("WS : [ \\t\\n\\r]+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="abc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "primary", input, false);
		assertEquals("ID abc\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testPredTestedEvenWhenUnAmbig_2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(228);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("@parser::members {boolean enumKeyword = true;}\n");
		grammarBuilder.append("primary\n");
		grammarBuilder.append("    :   ID {System.out.println(\"ID \" + $ID.text);}\n");
		grammarBuilder.append("    |   {!this.enumKeyword}? 'enum' {System.out.println(\"enum\");}\n");
		grammarBuilder.append("    ;\n");
		grammarBuilder.append("ID : [a-z]+ ;\n");
		grammarBuilder.append("WS : [ \\t\\n\\r]+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="enum";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "primary", input, false);
		assertEquals("", found);

		assertEquals("line 1:0 no viable alternative at input 'enum'\n", this.stderrDuringParse);

	}

	@Test
	public void testPredicateDependentOnArg() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(222);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("@parser::members {int i = 0;}\n");
		grammarBuilder.append("s : a[2] a[1];\n");
		grammarBuilder.append("a[int i]\n");
		grammarBuilder.append("  : {$i==1}? ID {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("  | {$i==2}? ID {System.out.println(\"alt 2\");}\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="a b";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"alt 2\n" +
			"alt 1\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testPredicateDependentOnArg2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(162);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("@parser::members {int i = 0;}\n");
		grammarBuilder.append("s : a[2] a[1];\n");
		grammarBuilder.append("a[int i]\n");
		grammarBuilder.append("  : {$i==1}? ID \n");
		grammarBuilder.append("  | {$i==2}? ID \n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="a b";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testPredsInGlobalFOLLOW() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(293);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("@parser::members {\n");
		grammarBuilder.append("boolean pred(boolean v) {\n");
		grammarBuilder.append("	System.out.println(\"eval=\"+v);\n");
		grammarBuilder.append("	return v;\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("s : e {this.pred(true)}? {System.out.println(\"parse\");} '!' ;\n");
		grammarBuilder.append("t : e {this.pred(false)}? ID ;\n");
		grammarBuilder.append("e : ID | ; // non-LL(1) so we use ATN\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="a!";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"eval=false\n" +
			"eval=true\n" +
			"parse\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testRewindBeforePredEval() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(253);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("s : a a;\n");
		grammarBuilder.append("a : {this._input.LT(1).getText().equals(\"x\")}? ID INT {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("  | {this._input.LT(1).getText().equals(\"y\")}? ID INT {System.out.println(\"alt 2\");}\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="y 3 x 4";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"alt 2\n" +
			"alt 1\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testSimple() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(277);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("s : a a a; // do 3x: once in ATN, next in DFA then INT in ATN\n");
		grammarBuilder.append("a : {false}? ID {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("  | {true}?  ID {System.out.println(\"alt 2\");}\n");
		grammarBuilder.append("  | INT         {System.out.println(\"alt 3\");}\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="x y 3";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"alt 2\n" +
			"alt 2\n" +
			"alt 3\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testSimpleValidate() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(178);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("s : a ;\n");
		grammarBuilder.append("a : {false}? ID  {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("  | {true}?  INT {System.out.println(\"alt 2\");}\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals("", found);

		assertEquals("line 1:0 no viable alternative at input 'x'\n", this.stderrDuringParse);

	}

	@Test
	public void testSimpleValidate2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(181);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("s : a a a;\n");
		grammarBuilder.append("a : {false}? ID  {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("  | {true}?  INT {System.out.println(\"alt 2\");}\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="3 4 x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"alt 2\n" +
			"alt 2\n", found);

		assertEquals("line 1:4 no viable alternative at input 'x'\n", this.stderrDuringParse);

	}

	@Test
	public void testToLeft() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(178);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("	s : a+ ;\n");
		grammarBuilder.append("a : {false}? ID {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("  | {true}?  ID {System.out.println(\"alt 2\");}\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="x x y";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"alt 2\n" +
			"alt 2\n" +
			"alt 2\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testToLeftWithVaryingPredicate() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(296);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("@parser::members {int i = 0;}\n");
		grammarBuilder.append("s : ({this.i += 1;\n");
		grammarBuilder.append("System.out.print(\"i=\");\n");
		grammarBuilder.append("System.out.println(this.i);} a)+ ;\n");
		grammarBuilder.append("a : {this.i % 2 == 0}? ID {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("  | {this.i % 2 != 0}? ID {System.out.println(\"alt 2\");}\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="x x y";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"i=1\n" +
			"alt 2\n" +
			"i=2\n" +
			"alt 1\n" +
			"i=3\n" +
			"alt 2\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testUnpredicatedPathsInAlt() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(197);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("s : a {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("  | b {System.out.println(\"alt 2\");}\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("a : {false}? ID INT\n");
		grammarBuilder.append("  | ID INT\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("b : ID ID\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="x 4";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals("alt 1\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testValidateInDFA() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(346);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("s : a ';' a;\n");
		grammarBuilder.append("// ';' helps us to resynchronize without consuming\n");
		grammarBuilder.append("// 2nd 'a' reference. We our testing that the DFA also\n");
		grammarBuilder.append("// throws an exception if the validating predicate fails\n");
		grammarBuilder.append("a : {false}? ID  {System.out.println(\"alt 1\");}\n");
		grammarBuilder.append("  | {true}?  INT {System.out.println(\"alt 2\");}\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="x ; y";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals("", found);

		assertEquals(
			"line 1:0 no viable alternative at input 'x'\n" +
			"line 1:4 no viable alternative at input 'y'\n", this.stderrDuringParse);

	}


}
