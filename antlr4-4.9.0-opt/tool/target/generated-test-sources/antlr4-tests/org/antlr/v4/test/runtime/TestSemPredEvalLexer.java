package org.antlr.v4.test.runtime;

import org.antlr.v4.test.tool.BaseTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestSemPredEvalLexer extends BaseTest {

	@Test
	public void testDisableRule() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(131);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("E1 : 'enum' { false }? ;\n");
		grammarBuilder.append("E2 : 'enum' { true }? ;  // winner not E1 or ID\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();

		String input ="enum abc";
		String found = execLexer("L.g4", grammar, "L", input, true);
		assertEquals(
			"[@0,0:3='enum',<2>,1:0]\n" +
			"[@1,5:7='abc',<3>,1:5]\n" +
			"[@2,8:7='<EOF>',<-1>,1:8]\n" +
			"s0-' '->:s5=>4\n" +
			"s0-'a'->:s6=>3\n" +
			"s0-'e'->:s1=>3\n" +
			":s1=>3-'n'->:s2=>3\n" +
			":s2=>3-'u'->:s3=>3\n" +
			":s6=>3-'b'->:s6=>3\n" +
			":s6=>3-'c'->:s6=>3\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testEnumNotID() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(108);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("ENUM : [a-z]+  { this.getText().equals(\"enum\") }? ;\n");
		grammarBuilder.append("ID : [a-z]+  ;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();

		String input ="enum abc enum";
		String found = execLexer("L.g4", grammar, "L", input, true);
		assertEquals(
			"[@0,0:3='enum',<1>,1:0]\n" +
			"[@1,5:7='abc',<2>,1:5]\n" +
			"[@2,9:12='enum',<1>,1:9]\n" +
			"[@3,13:12='<EOF>',<-1>,1:13]\n" +
			"s0-' '->:s3=>3\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testIDnotEnum() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(84);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("ENUM : [a-z]+  { false }? ;\n");
		grammarBuilder.append("ID : [a-z]+  ;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();

		String input ="enum abc enum";
		String found = execLexer("L.g4", grammar, "L", input, true);
		assertEquals(
			"[@0,0:3='enum',<2>,1:0]\n" +
			"[@1,5:7='abc',<2>,1:5]\n" +
			"[@2,9:12='enum',<2>,1:9]\n" +
			"[@3,13:12='<EOF>',<-1>,1:13]\n" +
			"s0-' '->:s2=>3\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testIDvsEnum() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(85);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("ENUM : 'enum' { false }? ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();

		String input ="enum abc enum";
		String found = execLexer("L.g4", grammar, "L", input, true);
		assertEquals(
			"[@0,0:3='enum',<2>,1:0]\n" +
			"[@1,5:7='abc',<2>,1:5]\n" +
			"[@2,9:12='enum',<2>,1:9]\n" +
			"[@3,13:12='<EOF>',<-1>,1:13]\n" +
			"s0-' '->:s5=>3\n" +
			"s0-'a'->:s4=>2\n" +
			"s0-'e'->:s1=>2\n" +
			":s1=>2-'n'->:s2=>2\n" +
			":s2=>2-'u'->:s3=>2\n" +
			":s4=>2-'b'->:s4=>2\n" +
			":s4=>2-'c'->:s4=>2\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testIndent() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(161);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("ID : [a-z]+  ;\n");
		grammarBuilder.append("INDENT : [ \\t]+ { this._tokenStartCharPositionInLine==0 }?\n");
		grammarBuilder.append("         { System.out.println(\"INDENT\"); }  ;\n");
		grammarBuilder.append("NL : '\\n';\n");
		grammarBuilder.append("WS : [ \\t]+ ;");
		String grammar = grammarBuilder.toString();

		String input =
			"abc\n" +
			"  def  \n";
		String found = execLexer("L.g4", grammar, "L", input, true);
		assertEquals(
			"INDENT\n" +
			"[@0,0:2='abc',<1>,1:0]\n" +
			"[@1,3:3='\\n',<3>,1:3]\n" +
			"[@2,4:5='  ',<2>,2:0]\n" +
			"[@3,6:8='def',<1>,2:2]\n" +
			"[@4,9:10='  ',<4>,2:5]\n" +
			"[@5,11:11='\\n',<3>,2:7]\n" +
			"[@6,12:11='<EOF>',<-1>,3:0]\n" +
			"s0-'\n" +
			"'->:s2=>3\n" +
			"s0-'a'->:s1=>1\n" +
			"s0-'d'->:s1=>1\n" +
			":s1=>1-'b'->:s1=>1\n" +
			":s1=>1-'c'->:s1=>1\n" +
			":s1=>1-'e'->:s1=>1\n" +
			":s1=>1-'f'->:s1=>1\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testLexerInputPositionSensitivePredicates() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(278);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("WORD1 : ID1+ { System.out.println(this.getText()); } ;\n");
		grammarBuilder.append("WORD2 : ID2+ { System.out.println(this.getText()); } ;\n");
		grammarBuilder.append("fragment ID1 : { this.getCharPositionInLine() < 2 }? [a-zA-Z];\n");
		grammarBuilder.append("fragment ID2 : { this.getCharPositionInLine() >= 2 }? [a-zA-Z];\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();

		String input =
			"a cde\n" +
			"abcde\n";
		String found = execLexer("L.g4", grammar, "L", input, true);
		assertEquals(
			"a\n" +
			"cde\n" +
			"ab\n" +
			"cde\n" +
			"[@0,0:0='a',<1>,1:0]\n" +
			"[@1,2:4='cde',<2>,1:2]\n" +
			"[@2,6:7='ab',<1>,2:0]\n" +
			"[@3,8:10='cde',<2>,2:2]\n" +
			"[@4,12:11='<EOF>',<-1>,3:0]\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testPredicatedKeywords() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(187);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("ENUM : [a-z]+ { this.getText().equals(\"enum\") }? { System.out.println(\"enum!\"); } ;\n");
		grammarBuilder.append("ID   : [a-z]+ { System.out.println(\"ID \" + this.getText()); } ;\n");
		grammarBuilder.append("WS   : [ \\n] -> skip ;");
		String grammar = grammarBuilder.toString();

		String input ="enum enu a";
		String found = execLexer("L.g4", grammar, "L", input, false);
		assertEquals(
			"enum!\n" +
			"ID enu\n" +
			"ID a\n" +
			"[@0,0:3='enum',<1>,1:0]\n" +
			"[@1,5:7='enu',<2>,1:5]\n" +
			"[@2,9:9='a',<2>,1:9]\n" +
			"[@3,10:9='<EOF>',<-1>,1:10]\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testRuleSempredFunction() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(34);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("T : 'a' {true}? ;");
		String grammar = grammarBuilder.toString();

		String input ="aaa";
		String found = execLexer("L.g4", grammar, "L", input, false);
		assertEquals(
			"[@0,0:0='a',<1>,1:0]\n" +
			"[@1,1:1='a',<1>,1:1]\n" +
			"[@2,2:2='a',<1>,1:2]\n" +
			"[@3,3:2='<EOF>',<-1>,1:3]\n", found);
		assertNull(this.stderrDuringParse);

	}


}
