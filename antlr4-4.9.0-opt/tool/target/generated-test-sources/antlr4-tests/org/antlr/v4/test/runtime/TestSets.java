package org.antlr.v4.test.runtime;

import org.antlr.v4.test.tool.BaseTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestSets extends BaseTest {

	@Test
	public void testCharSetLiteral() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(92);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : (A {System.out.println($A.text);})+ ;\n");
		grammarBuilder.append("A : [AaBb] ;\n");
		grammarBuilder.append("WS : (' '|'\\n')+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="A a B b";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals(
			"A\n" +
			"a\n" +
			"B\n" +
			"b\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testComplementSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(51);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("parse : ~NEW_LINE;\n");
		grammarBuilder.append("NEW_LINE: '\\r'? '\\n';");
		String grammar = grammarBuilder.toString();


		String input ="a";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "parse", input, false);
		assertEquals("", found);

		assertEquals(
			"line 1:0 token recognition error at: 'a'\n" +
			"line 1:1 missing {} at '<EOF>'\n", this.stderrDuringParse);

	}

	@Test
	public void testLexerOptionalSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(84);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : A {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("A : ('a'|'b')? 'c' ;");
		String grammar = grammarBuilder.toString();


		String input ="ac";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("ac\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testLexerPlusSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(84);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : A {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("A : ('a'|'b')+ 'c' ;");
		String grammar = grammarBuilder.toString();


		String input ="abaac";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testLexerStarSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(84);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : A {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("A : ('a'|'b')* 'c' ;");
		String grammar = grammarBuilder.toString();


		String input ="abaac";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testNotChar() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(60);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : A {System.out.println($A.text);} ;\n");
		grammarBuilder.append("A : ~'b' ;");
		String grammar = grammarBuilder.toString();


		String input ="x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testNotCharSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(66);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : A {System.out.println($A.text);} ;\n");
		grammarBuilder.append("A : ~('b'|'c') ;");
		String grammar = grammarBuilder.toString();


		String input ="x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testNotCharSetWithLabel() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(68);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : A {System.out.println($A.text);} ;\n");
		grammarBuilder.append("A : h=~('b'|'c') ;");
		String grammar = grammarBuilder.toString();


		String input ="x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testNotCharSetWithRuleRef3() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(132);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : A {System.out.println($A.text);} ;\n");
		grammarBuilder.append("A : ('a'|B) ;  // this doesn't collapse to set but works\n");
		grammarBuilder.append("fragment\n");
		grammarBuilder.append("B : ~('a'|'c') ;");
		String grammar = grammarBuilder.toString();


		String input ="x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testOptionalLexerSingleElement() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(78);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : A {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("A : 'b'? 'c' ;");
		String grammar = grammarBuilder.toString();


		String input ="bc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("bc\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testOptionalSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(76);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : ('a'|'b')? 'c' {System.out.println(this._input.getText());} ;");
		String grammar = grammarBuilder.toString();


		String input ="ac";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("ac\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testOptionalSingleElement() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(78);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : A? 'c' {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("A : 'b' ;");
		String grammar = grammarBuilder.toString();


		String input ="bc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("bc\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testParserNotSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(64);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : t=~('x'|'y') 'z' {System.out.println($t.text);} ;");
		String grammar = grammarBuilder.toString();


		String input ="zz";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("z\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testParserNotToken() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(70);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : ~'x' 'z' {System.out.println(this._input.getText());} ;");
		String grammar = grammarBuilder.toString();


		String input ="zz";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("zz\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testParserNotTokenWithLabel() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(58);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : t=~'x' 'z' {System.out.println($t.text);} ;");
		String grammar = grammarBuilder.toString();


		String input ="zz";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("z\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testParserSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(59);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : t=('x'|'y') {System.out.println($t.text);} ;");
		String grammar = grammarBuilder.toString();


		String input ="x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testPlusLexerSingleElement() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(78);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : A {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("A : 'b'+ 'c' ;");
		String grammar = grammarBuilder.toString();


		String input ="bbbbc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("bbbbc\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testPlusSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(76);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : ('a'|'b')+ 'c' {System.out.println(this._input.getText());} ;");
		String grammar = grammarBuilder.toString();


		String input ="abaac";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testRuleAsSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(83);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a @after {System.out.println(this._input.getText());} : 'a' | 'b' |'c' ;");
		String grammar = grammarBuilder.toString();


		String input ="b";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("b\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testSeqDoesNotBecomeSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(120);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : C {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("fragment A : '1' | '2';\n");
		grammarBuilder.append("fragment B : '3' '4';\n");
		grammarBuilder.append("C : A | B;");
		String grammar = grammarBuilder.toString();


		String input ="34";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("34\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testStarLexerSingleElement_1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(78);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : A {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("A : 'b'* 'c' ;");
		String grammar = grammarBuilder.toString();


		String input ="bbbbc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("bbbbc\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testStarLexerSingleElement_2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(78);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : A {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("A : 'b'* 'c' ;");
		String grammar = grammarBuilder.toString();


		String input ="c";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("c\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testStarSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(76);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : ('a'|'b')* 'c' {System.out.println(this._input.getText());} ;");
		String grammar = grammarBuilder.toString();


		String input ="abaac";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testUnicodeEscapedBMPRangeSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(205);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : LETTERS* 'd' {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("// Note the double-backslash to avoid Java passing\n");
		grammarBuilder.append("// unescaped values as part of the grammar.\n");
		grammarBuilder.append("LETTERS : ('a'|'\\u00E0'..'\\u00E5');");
		String grammar = grammarBuilder.toString();


		String input ="aáäáâåd";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("aáäáâåd\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testUnicodeEscapedBMPSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(213);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : LETTERS {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("// Note the double-backslash to avoid Java passing\n");
		grammarBuilder.append("// unescaped values as part of the grammar.\n");
		grammarBuilder.append("LETTERS : ('a'|'\\u00E4'|'\\u4E9C'|'\\u3042')* 'c';");
		String grammar = grammarBuilder.toString();


		String input ="aäあ亜c";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("aäあ亜c\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testUnicodeEscapedSMPRangeSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(211);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : LETTERS* 'd' {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("// Note the double-backslash to avoid Java passing\n");
		grammarBuilder.append("// unescaped values as part of the grammar.\n");
		grammarBuilder.append("LETTERS : ('a'|'\\u{1F600}'..'\\u{1F943}');");
		String grammar = grammarBuilder.toString();


		String input ="a😉🥂🜀d";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("a😉🥂🜀d\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testUnicodeEscapedSMPRangeSetMismatch() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(211);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : LETTERS* 'd' {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("// Note the double-backslash to avoid Java passing\n");
		grammarBuilder.append("// unescaped values as part of the grammar.\n");
		grammarBuilder.append("LETTERS : ('a'|'\\u{1F600}'..'\\u{1F943}');");
		String grammar = grammarBuilder.toString();


		String input ="a🗿🥄d";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("ad\n", found);

		assertEquals(
			"line 1:1 token recognition error at: '🗿'\n" +
			"line 1:2 token recognition error at: '🥄'\n", this.stderrDuringParse);

	}

	@Test
	public void testUnicodeEscapedSMPSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(247);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : LETTERS  {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("// Note the double-backslash to avoid Java passing\n");
		grammarBuilder.append("// unescaped values as part of the grammar.\n");
		grammarBuilder.append("LETTERS : ('a'|'\\u{1D5BA}'|'\\u{1D5BE}'|'\\u{1D5C2}'|'\\u{1D5C8}'|'\\u{1D5CE}')* 'c';");
		String grammar = grammarBuilder.toString();


		String input ="a𝗂𝗎𝖺c";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("a𝗂𝗎𝖺c\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testUnicodeNegatedBMPSetIncludesSMPCodePoints() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(96);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : LETTERS {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("LETTERS : 'a' ~('b')+ 'c';");
		String grammar = grammarBuilder.toString();


		String input ="a😳😡😝🤓c";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("a😳😡😝🤓c\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testUnicodeNegatedSMPSetIncludesBMPCodePoints() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(117);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : LETTERS {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("LETTERS : 'a' ~('\\u{1F600}'..'\\u{1F943}')+ 'c';");
		String grammar = grammarBuilder.toString();


		String input ="abc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("abc\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testUnicodeUnescapedBMPRangeSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(100);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : LETTERS* 'd' {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("LETTERS : ('a'|'à'..'å');");
		String grammar = grammarBuilder.toString();


		String input ="aáäáâåd";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("aáäáâåd\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testUnicodeUnescapedBMPSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(103);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : LETTERS {System.out.println(this._input.getText());} ;\n");
		grammarBuilder.append("LETTERS : ('a'|'ä'|'亜'|'あ')* 'c';");
		String grammar = grammarBuilder.toString();


		String input ="aäあ亜c";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("aäあ亜c\n", found);
		assertNull(this.stderrDuringParse);

	}


}
