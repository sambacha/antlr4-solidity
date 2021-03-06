package org.antlr.v4.test.runtime;

import org.antlr.v4.test.tool.BaseTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import org.antlr.v4.test.tool.ErrorQueue;
import org.antlr.v4.tool.Grammar;

public class TestCompositeParsers extends BaseTest {

	@Test
	public void testBringInLiteralsFromDelegate() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"parser grammar S;\n" +
			"a : '=' 'a' {System.out.print(\"S.a\");};";
		writeFile(tmpdir, "S.g4", slave_S);

		StringBuilder grammarBuilder = new StringBuilder(54);
		grammarBuilder.append("grammar M;\n");
		grammarBuilder.append("import S;\n");
		grammarBuilder.append("s : a ;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="=a";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", input, false);
		assertEquals("S.a\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testCombinedImportsCombined() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"parser grammar S;\n" +
			"tokens { A, B, C }\n" +
			"x : 'x' INT {System.out.println(\"S.x\");};\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;";
		writeFile(tmpdir, "S.g4", slave_S);

		StringBuilder grammarBuilder = new StringBuilder(31);
		grammarBuilder.append("grammar M;\n");
		grammarBuilder.append("import S;\n");
		grammarBuilder.append("s : x INT;");
		String grammar = grammarBuilder.toString();

		writeFile(tmpdir, "M.g4", grammar);
		ErrorQueue equeue = new ErrorQueue();
		new Grammar(tmpdir+"/M.g4", grammar, equeue);
		assertEquals("unexpected errors: " + equeue, 0, equeue.errors.size());


		String input ="x 34 9";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", input, false);
		assertEquals("S.x\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDelegatesSeeSameTokenType() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"parser grammar S;\n" +
			"tokens { A, B, C }\n" +
			"x : A {System.out.println(\"S.x\");};";
		writeFile(tmpdir, "S.g4", slave_S);

		String slave_T =
			"parser grammar T;\n" +
			"tokens { C, B, A } // reverse order\n" +
			"y : A {System.out.println(\"T.y\");};";
		writeFile(tmpdir, "T.g4", slave_T);

		StringBuilder grammarBuilder = new StringBuilder(598);
		grammarBuilder.append("// The lexer will create rules to match letters a, b, c.\n");
		grammarBuilder.append("// The associated token types A, B, C must have the same value\n");
		grammarBuilder.append("// and all import'd parsers.  Since ANTLR regenerates all imports\n");
		grammarBuilder.append("// for use with the delegator M, it can generate the same token type\n");
		grammarBuilder.append("// mapping in each parser:\n");
		grammarBuilder.append("// public static final int C=6;\n");
		grammarBuilder.append("// public static final int EOF=-1;\n");
		grammarBuilder.append("// public static final int B=5;\n");
		grammarBuilder.append("// public static final int WS=7;\n");
		grammarBuilder.append("// public static final int A=4;\n");
		grammarBuilder.append("grammar M;\n");
		grammarBuilder.append("import S,T;\n");
		grammarBuilder.append("s : x y ; // matches AA, which should be 'aa'\n");
		grammarBuilder.append("B : 'b' ; // another order: B, A, C\n");
		grammarBuilder.append("A : 'a' ; \n");
		grammarBuilder.append("C : 'c' ; \n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();

		writeFile(tmpdir, "M.g4", grammar);
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(tmpdir+"/M.g4", grammar, equeue);
		String expectedTokenIDToTypeMap = "{EOF=-1, B=1, A=2, C=3, WS=4}";
		String expectedStringLiteralToTypeMap = "{'a'=2, 'b'=1, 'c'=3}";
		String expectedTypeToTokenList = "[B, A, C, WS]";
		assertEquals(expectedTokenIDToTypeMap, g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, sort(g.stringLiteralToTypeMap).toString());
		assertEquals(expectedTypeToTokenList, realElements(g.typeToTokenList).toString());
		assertEquals("unexpected errors: "+equeue, 0, equeue.errors.size());


		String input ="aa";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", input, false);
		assertEquals(
			"S.x\n" +
			"T.y\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDelegatorAccessesDelegateMembers() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"parser grammar S;\n" +
			"@parser::members {\n" +
			"	public void foo() {System.out.println(\"foo\");}\n" +
			"}\n" +
			"a : B;";
		writeFile(tmpdir, "S.g4", slave_S);

		StringBuilder grammarBuilder = new StringBuilder(122);
		grammarBuilder.append("grammar M; // uses no rules from the import\n");
		grammarBuilder.append("import S;\n");
		grammarBuilder.append("s : 'b' {foo();} ; // gS is import pointer\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="b";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", input, false);
		assertEquals("foo\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDelegatorInvokesDelegateRule() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"parser grammar S;\n" +
			"a : B {System.out.println(\"S.a\");};";
		writeFile(tmpdir, "S.g4", slave_S);

		StringBuilder grammarBuilder = new StringBuilder(104);
		grammarBuilder.append("grammar M;\n");
		grammarBuilder.append("import S;\n");
		grammarBuilder.append("s : a ;\n");
		grammarBuilder.append("B : 'b' ; // defines B from inherited token space\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="b";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", input, false);
		assertEquals("S.a\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDelegatorInvokesDelegateRuleWithArgs() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"parser grammar S;\n" +
			"a[int x] returns [int y] : B {System.out.print(\"S.a\");} {$y=1000;} ;";
		writeFile(tmpdir, "S.g4", slave_S);

		StringBuilder grammarBuilder = new StringBuilder(145);
		grammarBuilder.append("grammar M;\n");
		grammarBuilder.append("import S;\n");
		grammarBuilder.append("s : label=a[3] {System.out.println($label.y);} ;\n");
		grammarBuilder.append("B : 'b' ; // defines B from inherited token space\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="b";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", input, false);
		assertEquals("S.a1000\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDelegatorInvokesDelegateRuleWithReturnStruct() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"parser grammar S;\n" +
			"a : B {System.out.print(\"S.a\");} ;";
		writeFile(tmpdir, "S.g4", slave_S);

		StringBuilder grammarBuilder = new StringBuilder(133);
		grammarBuilder.append("grammar M;\n");
		grammarBuilder.append("import S;\n");
		grammarBuilder.append("s : a {System.out.print($a.text);} ;\n");
		grammarBuilder.append("B : 'b' ; // defines B from inherited token space\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="b";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", input, false);
		assertEquals("S.ab\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDelegatorInvokesFirstVersionOfDelegateRule() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"parser grammar S;\n" +
			"a : b {System.out.println(\"S.a\");};\n" +
			"b : B;";
		writeFile(tmpdir, "S.g4", slave_S);

		String slave_T =
			"parser grammar T;\n" +
			"a : B {System.out.println(\"T.a\");};";
		writeFile(tmpdir, "T.g4", slave_T);

		StringBuilder grammarBuilder = new StringBuilder(106);
		grammarBuilder.append("grammar M;\n");
		grammarBuilder.append("import S,T;\n");
		grammarBuilder.append("s : a ;\n");
		grammarBuilder.append("B : 'b' ; // defines B from inherited token space\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="b";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", input, false);
		assertEquals("S.a\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDelegatorRuleOverridesDelegate() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"parser grammar S;\n" +
			"a : b {System.out.print(\"S.a\");};\n" +
			"b : B ;";
		writeFile(tmpdir, "S.g4", slave_S);

		StringBuilder grammarBuilder = new StringBuilder(59);
		grammarBuilder.append("grammar M;\n");
		grammarBuilder.append("import S;\n");
		grammarBuilder.append("b : 'b'|'c';\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="c";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "a", input, false);
		assertEquals("S.a\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDelegatorRuleOverridesDelegates() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"parser grammar S;\n" +
			"a : b {System.out.println(\"S.a\");};\n" +
			"b : 'b' ;";
		writeFile(tmpdir, "S.g4", slave_S);

		String slave_T =
			"parser grammar T;\n" +
			"tokens { A }\n" +
			"b : 'b' {System.out.println(\"T.b\");};";
		writeFile(tmpdir, "T.g4", slave_T);

		StringBuilder grammarBuilder = new StringBuilder(95);
		grammarBuilder.append("grammar M;\n");
		grammarBuilder.append("import S, T;\n");
		grammarBuilder.append("b : 'b'|'c' {System.out.println(\"M.b\");}|B|A;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="c";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "a", input, false);
		assertEquals(
			"M.b\n" +
			"S.a\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testDelegatorRuleOverridesLookaheadInDelegate() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"parser grammar S;\n" +
			"type_ : 'int' ;\n" +
			"decl : type_ ID ';'\n" +
			"	| type_ ID init_ ';' {System.out.println(\"JavaDecl: \" + $text);};\n" +
			"init_ : '=' INT;";
		writeFile(tmpdir, "S.g4", slave_S);

		StringBuilder grammarBuilder = new StringBuilder(121);
		grammarBuilder.append("grammar M;\n");
		grammarBuilder.append("import S;\n");
		grammarBuilder.append("prog : decl ;\n");
		grammarBuilder.append("type_ : 'int' | 'float' ;\n");
		grammarBuilder.append("ID  : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+ ;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();


		String input ="float x = 3;";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "prog", input, false);
		assertEquals("JavaDecl: floatx=3;\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testImportLexerWithOnlyFragmentRules() throws Exception {
		mkdir(tmpdir);

		String slave_Unicode =
			"lexer grammar Unicode;\n" +
			"\n" +
			"fragment\n" +
			"UNICODE_CLASS_Zs    : '\\u0020' | '\\u00A0' | '\\u1680' | '\\u180E'\n" +
			"                    | '\\u2000'..'\\u200A'\n" +
			"                    | '\\u202F' | '\\u205F' | '\\u3000'\n" +
			"                    ;\n";
		writeFile(tmpdir, "Unicode.g4", slave_Unicode);

		StringBuilder grammarBuilder = new StringBuilder(91);
		grammarBuilder.append("grammar Test;\n");
		grammarBuilder.append("import Unicode;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("program : 'test' 'test';\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("WS : (UNICODE_CLASS_Zs)+ -> skip;\n");
		String grammar = grammarBuilder.toString();


		String input ="test test";
		String found = execParser("Test.g4", grammar, "TestParser", "TestLexer", "program", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testImportedGrammarWithEmptyOptions() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"parser grammar S;\n" +
			"options {}\n" +
			"a : B ;";
		writeFile(tmpdir, "S.g4", slave_S);

		StringBuilder grammarBuilder = new StringBuilder(64);
		grammarBuilder.append("grammar M;\n");
		grammarBuilder.append("import S;\n");
		grammarBuilder.append("s : a ;\n");
		grammarBuilder.append("B : 'b' ;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="b";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testImportedRuleWithAction() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"parser grammar S;\n" +
			"a @after {int x = 0;} : B;";
		writeFile(tmpdir, "S.g4", slave_S);

		StringBuilder grammarBuilder = new StringBuilder(62);
		grammarBuilder.append("grammar M;\n");
		grammarBuilder.append("import S;\n");
		grammarBuilder.append("s : a;\n");
		grammarBuilder.append("B : 'b';\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="b";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testKeywordVSIDOrder() throws Exception {
		mkdir(tmpdir);

		String slave_S =
			"lexer grammar S;\n" +
			"ID : 'a'..'z'+;";
		writeFile(tmpdir, "S.g4", slave_S);

		StringBuilder grammarBuilder = new StringBuilder(129);
		grammarBuilder.append("grammar M;\n");
		grammarBuilder.append("import S;\n");
		grammarBuilder.append("a : A {System.out.println(\"M.a: \" + $A);};\n");
		grammarBuilder.append("A : 'abc' {System.out.println(\"M.A\");};\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="abc";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "a", input, false);
		assertEquals(
			"M.A\n" +
			"M.a: [@0,0:2='abc',<1>,1:0]\n", found);
		assertNull(this.stderrDuringParse);

	}


}
