package org.antlr.v4.test.runtime;

import org.antlr.v4.test.tool.BaseTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestCompositeLexers extends BaseTest {

	@Test
	public void testLexerDelegatorInvokesDelegateRule() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"lexer grammar S;\n" +
			"A : 'a' {System.out.println(\"S.A\");};\n" +
			"C : 'c' ;";
		writeFile(tmpdir, "S.g4", slave_S);

		StringBuilder grammarBuilder = new StringBuilder(61);
		grammarBuilder.append("lexer grammar M;\n");
		grammarBuilder.append("import S;\n");
		grammarBuilder.append("B : 'b';\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();

		String input ="abc";
		String found = execLexer("M.g4", grammar, "M", input, false);
		assertEquals(
			"S.A\n" +
			"[@0,0:0='a',<3>,1:0]\n" +
			"[@1,1:1='b',<1>,1:1]\n" +
			"[@2,2:2='c',<4>,1:2]\n" +
			"[@3,3:2='<EOF>',<-1>,1:3]\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testLexerDelegatorRuleOverridesDelegate() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"lexer grammar S;\n" +
			"A : 'a' {System.out.println(\"S.A\");} ;\n" +
			"B : 'b' {System.out.println(\"S.B\");} ;";
		writeFile(tmpdir, "S.g4", slave_S);

		StringBuilder grammarBuilder = new StringBuilder(93);
		grammarBuilder.append("lexer grammar M;\n");
		grammarBuilder.append("import S;\n");
		grammarBuilder.append("A : 'a' B {System.out.println(\"M.A\");} ;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();

		String input ="ab";
		String found = execLexer("M.g4", grammar, "M", input, false);
		assertEquals(
			"M.A\n" +
			"[@0,0:1='ab',<1>,1:0]\n" +
			"[@1,2:1='<EOF>',<-1>,1:2]\n", found);
		assertNull(this.stderrDuringParse);

	}


}
