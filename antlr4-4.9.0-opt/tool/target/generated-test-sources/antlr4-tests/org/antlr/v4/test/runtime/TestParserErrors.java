package org.antlr.v4.test.runtime;

import org.antlr.v4.test.tool.BaseTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestParserErrors extends BaseTest {

	@Test
	public void testConjuringUpToken() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(70);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' x='b' {System.out.println(\"conjured=\" + $x);} 'c' ;");
		String grammar = grammarBuilder.toString();


		String input ="ac";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("conjured=[@-1,-1:-1='<missing 'b'>',<2>,1:1]\n", found);

		assertEquals("line 1:1 missing 'b' at 'c'\n", this.stderrDuringParse);

	}

	@Test
	public void testConjuringUpTokenFromSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(76);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' x=('b'|'c') {System.out.println(\"conjured=\" + $x);} 'd' ;");
		String grammar = grammarBuilder.toString();


		String input ="ad";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("conjured=[@-1,-1:-1='<missing 'b'>',<2>,1:1]\n", found);

		assertEquals("line 1:1 missing {'b', 'c'} at 'd'\n", this.stderrDuringParse);

	}

	@Test
	public void testContextListGetters() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(221);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("@parser::members{\n");
		grammarBuilder.append("void foo() {\n");
		grammarBuilder.append("	SContext s = null;\n");
		grammarBuilder.append("	List<? extends AContext> a = s.a();\n");
		grammarBuilder.append("	List<? extends BContext> b = s.b();\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("s : (a | b)+;\n");
		grammarBuilder.append("a : 'a' {System.out.print(\"a\");};\n");
		grammarBuilder.append("b : 'b' {System.out.print(\"b\");};");
		String grammar = grammarBuilder.toString();


		String input ="abab";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, true);
		assertEquals("abab\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDuplicatedLeftRecursiveCall_1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(63);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("start : expr EOF;\n");
		grammarBuilder.append("expr : 'x'\n");
		grammarBuilder.append("     | expr expr\n");
		grammarBuilder.append("     ;");
		String grammar = grammarBuilder.toString();


		String input ="x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, true);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDuplicatedLeftRecursiveCall_2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(63);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("start : expr EOF;\n");
		grammarBuilder.append("expr : 'x'\n");
		grammarBuilder.append("     | expr expr\n");
		grammarBuilder.append("     ;");
		String grammar = grammarBuilder.toString();


		String input ="xx";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, true);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDuplicatedLeftRecursiveCall_3() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(63);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("start : expr EOF;\n");
		grammarBuilder.append("expr : 'x'\n");
		grammarBuilder.append("     | expr expr\n");
		grammarBuilder.append("     ;");
		String grammar = grammarBuilder.toString();


		String input ="xxx";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, true);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDuplicatedLeftRecursiveCall_4() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(63);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("start : expr EOF;\n");
		grammarBuilder.append("expr : 'x'\n");
		grammarBuilder.append("     | expr expr\n");
		grammarBuilder.append("     ;");
		String grammar = grammarBuilder.toString();


		String input ="xxxx";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, true);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testExtraneousInput() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(67);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("member : 'a';\n");
		grammarBuilder.append("body : member*;\n");
		grammarBuilder.append("file : body EOF;\n");
		grammarBuilder.append("B : 'b';");
		String grammar = grammarBuilder.toString();


		String input ="baa";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "file", input, false);
		assertEquals("", found);

		assertEquals("line 1:0 mismatched input 'b' expecting {<EOF>, 'a'}\n", this.stderrDuringParse);

	}

	@Test
	public void testInvalidATNStateRemoval() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(98);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("start : ID ':' expr;\n");
		grammarBuilder.append("expr : primary expr? {} | expr '->' ID;\n");
		grammarBuilder.append("primary : ID;\n");
		grammarBuilder.append("ID : [a-z]+;");
		String grammar = grammarBuilder.toString();


		String input ="x:x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testInvalidEmptyInput() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(36);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("start : ID+;\n");
		grammarBuilder.append("ID : [a-z]+;");
		String grammar = grammarBuilder.toString();


		String input ="";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, true);
		assertEquals("", found);

		assertEquals("line 1:0 mismatched input '<EOF>' expecting ID\n", this.stderrDuringParse);

	}

	@Test
	public void testLL1ErrorInfo() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(306);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("start : animal (AND acClass)? service EOF;\n");
		grammarBuilder.append("animal : (DOG | CAT );\n");
		grammarBuilder.append("service : (HARDWARE | SOFTWARE) ;\n");
		grammarBuilder.append("AND : 'and';\n");
		grammarBuilder.append("DOG : 'dog';\n");
		grammarBuilder.append("CAT : 'cat';\n");
		grammarBuilder.append("HARDWARE: 'hardware';\n");
		grammarBuilder.append("SOFTWARE: 'software';\n");
		grammarBuilder.append("WS : ' ' -> skip ;\n");
		grammarBuilder.append("acClass\n");
		grammarBuilder.append("@init\n");
		grammarBuilder.append("{System.out.println(this.getExpectedTokens().toString(this.tokenNames));}\n");
		grammarBuilder.append("  : ;");
		String grammar = grammarBuilder.toString();


		String input ="dog and software";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals("{'hardware', 'software'}\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testLL2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(46);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' 'b'\n");
		grammarBuilder.append("  | 'a' 'c'\n");
		grammarBuilder.append(";\n");
		grammarBuilder.append("q : 'e' ;");
		String grammar = grammarBuilder.toString();


		String input ="ae";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals("line 1:1 no viable alternative at input 'ae'\n", this.stderrDuringParse);

	}

	@Test
	public void testLL3() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(55);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' 'b'* 'c'\n");
		grammarBuilder.append("  | 'a' 'b' 'd'\n");
		grammarBuilder.append(";\n");
		grammarBuilder.append("q : 'e' ;");
		String grammar = grammarBuilder.toString();


		String input ="abe";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals("line 1:2 no viable alternative at input 'abe'\n", this.stderrDuringParse);

	}

	@Test
	public void testLLStar() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(48);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a'+ 'b'\n");
		grammarBuilder.append("  | 'a'+ 'c'\n");
		grammarBuilder.append(";\n");
		grammarBuilder.append("q : 'e' ;");
		String grammar = grammarBuilder.toString();


		String input ="aaae";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals("line 1:3 no viable alternative at input 'aaae'\n", this.stderrDuringParse);

	}

	@Test
	public void testMultiTokenDeletionBeforeLoop() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(28);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' 'b'* 'c';");
		String grammar = grammarBuilder.toString();


		String input ="aacabc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals("line 1:1 extraneous input 'a' expecting {'b', 'c'}\n", this.stderrDuringParse);

	}

	@Test
	public void testMultiTokenDeletionBeforeLoop2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(36);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' ('b'|'z'{})* 'c';");
		String grammar = grammarBuilder.toString();


		String input ="aacabc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals("line 1:1 extraneous input 'a' expecting {'b', 'z', 'c'}\n", this.stderrDuringParse);

	}

	@Test
	public void testMultiTokenDeletionDuringLoop() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(29);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' 'b'* 'c' ;");
		String grammar = grammarBuilder.toString();


		String input ="abaaababc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals(
			"line 1:2 extraneous input 'a' expecting {'b', 'c'}\n" +
			"line 1:6 extraneous input 'a' expecting {'b', 'c'}\n", this.stderrDuringParse);

	}

	@Test
	public void testMultiTokenDeletionDuringLoop2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(37);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' ('b'|'z'{})* 'c' ;");
		String grammar = grammarBuilder.toString();


		String input ="abaaababc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals(
			"line 1:2 extraneous input 'a' expecting {'b', 'z', 'c'}\n" +
			"line 1:6 extraneous input 'a' expecting {'b', 'z', 'c'}\n", this.stderrDuringParse);

	}

	@Test
	public void testNoViableAltAvoidance() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(83);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("s : e '!' ;\n");
		grammarBuilder.append("e : 'a' 'b'\n");
		grammarBuilder.append("  | 'a'\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("DOT : '.' ;\n");
		grammarBuilder.append("WS : [ \\t\\r\\n]+ -> skip;");
		String grammar = grammarBuilder.toString();


		String input ="a.";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals("", found);

		assertEquals("line 1:1 mismatched input '.' expecting '!'\n", this.stderrDuringParse);

	}

	@Test
	public void testSingleSetInsertion() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(34);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' ('b'|'c') 'd' ;");
		String grammar = grammarBuilder.toString();


		String input ="ad";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals("line 1:1 missing {'b', 'c'} at 'd'\n", this.stderrDuringParse);

	}

	@Test
	public void testSingleSetInsertionConsumption() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(89);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("myset: ('b'|'c') ;\n");
		grammarBuilder.append("a: 'a' myset 'd' {System.out.println(\"\" + $myset.stop);} ; ");
		String grammar = grammarBuilder.toString();


		String input ="ad";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("[@0,0:0='a',<3>,1:0]\n", found);

		assertEquals("line 1:1 missing {'b', 'c'} at 'd'\n", this.stderrDuringParse);

	}

	@Test
	public void testSingleTokenDeletion() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(24);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' 'b' ;");
		String grammar = grammarBuilder.toString();


		String input ="aab";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals("line 1:1 extraneous input 'a' expecting 'b'\n", this.stderrDuringParse);

	}

	@Test
	public void testSingleTokenDeletionBeforeAlt() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(38);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : ('b' | 'c')\n");
		grammarBuilder.append(";\n");
		grammarBuilder.append("q : 'a'\n");
		grammarBuilder.append(";");
		String grammar = grammarBuilder.toString();


		String input ="ac";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals("line 1:0 extraneous input 'a' expecting {'b', 'c'}\n", this.stderrDuringParse);

	}

	@Test
	public void testSingleTokenDeletionBeforeLoop() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(29);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' 'b'* EOF ;");
		String grammar = grammarBuilder.toString();


		String input ="aabc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals(
			"line 1:1 extraneous input 'a' expecting {<EOF>, 'b'}\n" +
			"line 1:3 token recognition error at: 'c'\n", this.stderrDuringParse);

	}

	@Test
	public void testSingleTokenDeletionBeforeLoop2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(37);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' ('b'|'z'{})* EOF ;");
		String grammar = grammarBuilder.toString();


		String input ="aabc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals(
			"line 1:1 extraneous input 'a' expecting {<EOF>, 'b', 'z'}\n" +
			"line 1:3 token recognition error at: 'c'\n", this.stderrDuringParse);

	}

	@Test
	public void testSingleTokenDeletionBeforePredict() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(48);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a'+ 'b'\n");
		grammarBuilder.append("  | 'a'+ 'c'\n");
		grammarBuilder.append(";\n");
		grammarBuilder.append("q : 'e' ;");
		String grammar = grammarBuilder.toString();


		String input ="caaab";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals("line 1:0 extraneous input 'c' expecting 'a'\n", this.stderrDuringParse);

	}

	@Test
	public void testSingleTokenDeletionConsumption() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(89);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("myset: ('b'|'c') ;\n");
		grammarBuilder.append("a: 'a' myset 'd' {System.out.println(\"\" + $myset.stop);} ; ");
		String grammar = grammarBuilder.toString();


		String input ="aabd";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("[@2,2:2='b',<1>,1:2]\n", found);

		assertEquals("line 1:1 extraneous input 'a' expecting {'b', 'c'}\n", this.stderrDuringParse);

	}

	@Test
	public void testSingleTokenDeletionDuringLoop() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(29);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' 'b'* 'c' ;");
		String grammar = grammarBuilder.toString();


		String input ="ababbc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals("line 1:2 extraneous input 'a' expecting {'b', 'c'}\n", this.stderrDuringParse);

	}

	@Test
	public void testSingleTokenDeletionDuringLoop2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(37);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' ('b'|'z'{})* 'c' ;");
		String grammar = grammarBuilder.toString();


		String input ="ababbc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals("line 1:2 extraneous input 'a' expecting {'b', 'z', 'c'}\n", this.stderrDuringParse);

	}

	@Test
	public void testSingleTokenDeletionExpectingSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(30);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' ('b'|'c') ;");
		String grammar = grammarBuilder.toString();


		String input ="aab";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals("line 1:1 extraneous input 'a' expecting {'b', 'c'}\n", this.stderrDuringParse);

	}

	@Test
	public void testSingleTokenInsertion() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(28);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' 'b' 'c' ;");
		String grammar = grammarBuilder.toString();


		String input ="ac";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals("line 1:1 missing 'b' at 'c'\n", this.stderrDuringParse);

	}

	@Test
	public void testTokenMismatch() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(24);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : 'a' 'b' ;");
		String grammar = grammarBuilder.toString();


		String input ="aa";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);

		assertEquals("line 1:1 mismatched input 'a' expecting 'b'\n", this.stderrDuringParse);

	}

	@Test
	public void testTokenMismatch2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(165);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("stat:   ( '(' expr? ')' )? EOF ;\n");
		grammarBuilder.append("expr:   ID '=' STR ;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("ERR :   '~FORCE_ERROR~' ;\n");
		grammarBuilder.append("ID  :   [a-zA-Z]+ ;\n");
		grammarBuilder.append("STR :   '\"' ~[\"]* '\"' ;\n");
		grammarBuilder.append("WS  :   [ \\t\\r\\n]+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="( ~FORCE_ERROR~ ";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "stat", input, false);
		assertEquals("", found);

		assertEquals("line 1:2 mismatched input '~FORCE_ERROR~' expecting {')', ID}\n", this.stderrDuringParse);

	}

	@Test
	public void testTokenMismatch3() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(281);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("expression\n");
		grammarBuilder.append(":   value\n");
		grammarBuilder.append("|   expression op=AND expression\n");
		grammarBuilder.append("|   expression op=OR expression\n");
		grammarBuilder.append(";\n");
		grammarBuilder.append("value\n");
		grammarBuilder.append(":   BOOLEAN_LITERAL\n");
		grammarBuilder.append("|   ID\n");
		grammarBuilder.append("|   ID1\n");
		grammarBuilder.append("|   '(' expression ')'\n");
		grammarBuilder.append(";\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("AND : '&&';\n");
		grammarBuilder.append("OR  : '||';\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("BOOLEAN_LITERAL : 'true' | 'false';\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("ID  : [a-z]+;\n");
		grammarBuilder.append("ID1 : '$';\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("WS  : [ \\t\\r\\n]+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "expression", input, false);
		assertEquals("", found);

		assertEquals("line 1:0 mismatched input '<EOF>' expecting {'(', BOOLEAN_LITERAL, ID, '$'}\n", this.stderrDuringParse);

	}


}
