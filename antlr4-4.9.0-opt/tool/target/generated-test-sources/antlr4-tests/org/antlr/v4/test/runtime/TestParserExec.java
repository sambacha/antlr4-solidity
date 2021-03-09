package org.antlr.v4.test.runtime;

import org.antlr.v4.test.tool.BaseTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestParserExec extends BaseTest {

	@Test
	public void testAPlus() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(91);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : ID+ {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();


		String input ="a b c";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("abc\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testAStar_1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(91);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : ID* {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();


		String input ="";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testAStar_2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(91);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : ID* {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();


		String input ="a b c";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("abc\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testAorAPlus() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(96);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : (ID|ID)+ {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();


		String input ="a b c";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("abc\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testAorAStar_1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(96);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : (ID|ID)* {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();


		String input ="";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testAorAStar_2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(96);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : (ID|ID)* {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();


		String input ="a b c";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("abc\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testAorB() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(150);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : ID {\n");
		grammarBuilder.append("System.out.println(\"alt 1\");\n");
		grammarBuilder.append("} | INT {\n");
		grammarBuilder.append("System.out.println(\"alt 2\");\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="34";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("alt 2\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testAorBPlus() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(119);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : (ID|INT{\n");
		grammarBuilder.append("})+ {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="a 34 c";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("a34c\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testAorBStar_1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(119);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : (ID|INT{\n");
		grammarBuilder.append("})* {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testAorBStar_2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(119);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : (ID|INT{\n");
		grammarBuilder.append("})* {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="a 34 c";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("a34c\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testBasic() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(112);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : ID INT {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();


		String input ="abc 34";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("abc34\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testEOFInClosure() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(53);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("prog : stat EOF;\n");
		grammarBuilder.append("stat : 'x' ('y' | EOF)*?;");
		String grammar = grammarBuilder.toString();


		String input ="x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "prog", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testIfIfElseGreedyBinding1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(200);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("start : statement+ ;\n");
		grammarBuilder.append("statement : 'x' | ifStatement;\n");
		grammarBuilder.append("ifStatement : 'if' 'y' statement ('else' statement)? {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> channel(HIDDEN);");
		String grammar = grammarBuilder.toString();


		String input ="if y if y x else x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals(
			"if y x else x\n" +
			"if y if y x else x\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testIfIfElseGreedyBinding2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(200);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("start : statement+ ;\n");
		grammarBuilder.append("statement : 'x' | ifStatement;\n");
		grammarBuilder.append("ifStatement : 'if' 'y' statement ('else' statement|) {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> channel(HIDDEN);");
		String grammar = grammarBuilder.toString();


		String input ="if y if y x else x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals(
			"if y x else x\n" +
			"if y if y x else x\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testIfIfElseNonGreedyBinding1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(201);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("start : statement+ ;\n");
		grammarBuilder.append("statement : 'x' | ifStatement;\n");
		grammarBuilder.append("ifStatement : 'if' 'y' statement ('else' statement)?? {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> channel(HIDDEN);");
		String grammar = grammarBuilder.toString();


		String input ="if y if y x else x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals(
			"if y x\n" +
			"if y if y x else x\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testIfIfElseNonGreedyBinding2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(200);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("start : statement+ ;\n");
		grammarBuilder.append("statement : 'x' | ifStatement;\n");
		grammarBuilder.append("ifStatement : 'if' 'y' statement (|'else' statement) {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> channel(HIDDEN);");
		String grammar = grammarBuilder.toString();


		String input ="if y if y x else x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals(
			"if y x\n" +
			"if y if y x else x\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testLL1OptionalBlock_1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(117);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : (ID|{}INT)? {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+;\n");
		grammarBuilder.append("INT : '0'..'9'+ ;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();


		String input ="";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testLL1OptionalBlock_2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(117);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : (ID|{}INT)? {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("ID : 'a'..'z'+;\n");
		grammarBuilder.append("INT : '0'..'9'+ ;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();


		String input ="a";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("a\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testLabelAliasingAcrossLabeledAlternatives() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(185);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("start : a* EOF;\n");
		grammarBuilder.append("a\n");
		grammarBuilder.append("  : label=subrule {System.out.println($label.text);} #One\n");
		grammarBuilder.append("  | label='y' {System.out.println($label.text);} #Two\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("subrule : 'x';\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="xy";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "start", input, false);
		assertEquals(
			"x\n" +
			"y\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testLabels() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(118);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : b1=b b2+=b* b3+=';' ;\n");
		grammarBuilder.append("b : id_=ID val+=INT*;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="abc 34;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testListLabelForClosureContext() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(462);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("ifStatement\n");
		grammarBuilder.append("@after {\n");
		grammarBuilder.append("List<?> __ttt__ = $ctx.elseIfStatement();\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("    : 'if' expression\n");
		grammarBuilder.append("      ( ( 'then'\n");
		grammarBuilder.append("          executableStatement*\n");
		grammarBuilder.append("          elseIfStatement*  // <--- problem is here; should yield a list not node\n");
		grammarBuilder.append("          elseStatement?\n");
		grammarBuilder.append("          'end' 'if'\n");
		grammarBuilder.append("        ) | executableStatement )\n");
		grammarBuilder.append("    ;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("elseIfStatement\n");
		grammarBuilder.append("    : 'else' 'if' expression 'then' executableStatement*\n");
		grammarBuilder.append("    ;\n");
		grammarBuilder.append("expression : 'a' ;\n");
		grammarBuilder.append("executableStatement : 'a' ;\n");
		grammarBuilder.append("elseStatement : 'a' ;");
		String grammar = grammarBuilder.toString();


		String input ="a";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "expression", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testListLabelsOnSet() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(140);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : b b* ';' ;\n");
		grammarBuilder.append("b : ID val+=(INT | FLOAT)*;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("FLOAT : [0-9]+ '.' [0-9]+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="abc 34;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testMultipleEOFHandling() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(42);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("prog : ('x' | 'x' 'y') EOF EOF;");
		String grammar = grammarBuilder.toString();


		String input ="x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "prog", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testOpenDeviceStatement_1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(237);
		grammarBuilder.append("grammar OpenDeviceStatement;\n");
		grammarBuilder.append("program : statement+ '.' ;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("statement : 'OPEN' ( 'DEVICE' (  OPT1  |  OPT2  |  OPT3  )? )+ {System.out.println($text);} ;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("OPT1 : 'OPT-1';\n");
		grammarBuilder.append("OPT2 : 'OPT-2';\n");
		grammarBuilder.append("OPT3 : 'OPT-3';\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("WS : (' '|'\\n')+ -> channel(HIDDEN);");
		String grammar = grammarBuilder.toString();


		String input ="OPEN DEVICE DEVICE";
		String found = execParser("OpenDeviceStatement.g4", grammar, "OpenDeviceStatementParser", "OpenDeviceStatementLexer", "statement", input, false);
		assertEquals("OPEN DEVICE DEVICE\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testOpenDeviceStatement_2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(239);
		grammarBuilder.append("grammar OpenDeviceStatement;\n");
		grammarBuilder.append("program : statement+ '.' ;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("statement : 'OPEN' ( 'DEVICE' (  (OPT1)  |  OPT2  |  OPT3  )? )+ {System.out.println($text);} ;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("OPT1 : 'OPT-1';\n");
		grammarBuilder.append("OPT2 : 'OPT-2';\n");
		grammarBuilder.append("OPT3 : 'OPT-3';\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("WS : (' '|'\\n')+ -> channel(HIDDEN);");
		String grammar = grammarBuilder.toString();


		String input ="OPEN DEVICE DEVICE";
		String found = execParser("OpenDeviceStatement.g4", grammar, "OpenDeviceStatementParser", "OpenDeviceStatementLexer", "statement", input, false);
		assertEquals("OPEN DEVICE DEVICE\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testOpenDeviceStatement_3() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(239);
		grammarBuilder.append("grammar OpenDeviceStatement;\n");
		grammarBuilder.append("program : statement+ '.' ;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("statement : 'OPEN' ( 'DEVICE' (  (OPT1)  |  OPT2  |  OPT3  )? )+ {System.out.println($text);} ;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("OPT1 : 'OPT-1';\n");
		grammarBuilder.append("OPT2 : 'OPT-2';\n");
		grammarBuilder.append("OPT3 : 'OPT-3';\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("WS : (' '|'\\n')+ -> channel(HIDDEN);");
		String grammar = grammarBuilder.toString();


		String input ="OPEN DEVICE DEVICE .";
		String found = execParser("OpenDeviceStatement.g4", grammar, "OpenDeviceStatementParser", "OpenDeviceStatementLexer", "statement", input, false);
		assertEquals("OPEN DEVICE DEVICE\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testOptional_1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(90);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("stat : ifstat | 'x';\n");
		grammarBuilder.append("ifstat : 'if' stat ('else' stat)?;\n");
		grammarBuilder.append("WS : [ \\n\\t]+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "stat", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testOptional_2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(90);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("stat : ifstat | 'x';\n");
		grammarBuilder.append("ifstat : 'if' stat ('else' stat)?;\n");
		grammarBuilder.append("WS : [ \\n\\t]+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="if x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "stat", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testOptional_3() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(90);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("stat : ifstat | 'x';\n");
		grammarBuilder.append("ifstat : 'if' stat ('else' stat)?;\n");
		grammarBuilder.append("WS : [ \\n\\t]+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="if x else x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "stat", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testOptional_4() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(90);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("stat : ifstat | 'x';\n");
		grammarBuilder.append("ifstat : 'if' stat ('else' stat)?;\n");
		grammarBuilder.append("WS : [ \\n\\t]+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="if if x else x";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "stat", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testOrderingPredicates() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(164);
		grammarBuilder.append("grammar Issue2301;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("constant\n");
		grammarBuilder.append("	: 'DUMMY'\n");
		grammarBuilder.append("	;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("expr\n");
		grammarBuilder.append("	: ID constant?\n");
		grammarBuilder.append("	| expr AT X\n");
		grammarBuilder.append("	| expr AT Y\n");
		grammarBuilder.append("	;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("SPACES: [ \\t\\r\\n]+ -> skip;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("AT: 'AT';\n");
		grammarBuilder.append("X : 'X';\n");
		grammarBuilder.append("Y : 'Y';\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("ID: [A-Z]+;");
		String grammar = grammarBuilder.toString();


		String input ="POINT AT X";
		String found = execParser("Issue2301.g4", grammar, "Issue2301Parser", "Issue2301Lexer", "expr", input, false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testParserProperty() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(168);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("@members {\n");
		grammarBuilder.append("boolean Property() {\n");
		grammarBuilder.append("  return true;\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("a : {$parser.Property()}? ID {System.out.println(\"valid\");}\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="abc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("valid\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testPredicatedIfIfElse() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(175);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("s : stmt EOF ;\n");
		grammarBuilder.append("stmt : ifStmt | ID;\n");
		grammarBuilder.append("ifStmt : 'if' ID stmt ('else' stmt | { this._input.LA(1)!=TParser.ELSE }?);\n");
		grammarBuilder.append("ELSE : 'else';\n");
		grammarBuilder.append("ID : [a-zA-Z]+;\n");
		grammarBuilder.append("WS : [ \\n\\t]+ -> skip;");
		String grammar = grammarBuilder.toString();


		String input ="if x if x a else b";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, true);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testPredictionIssue334() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(257);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("file_ @init{\n");
		grammarBuilder.append("setErrorHandler(new BailErrorStrategy());\n");
		grammarBuilder.append("} \n");
		grammarBuilder.append("@after {\n");
		grammarBuilder.append("System.out.println($ctx.toStringTree(this));\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("  :   item (SEMICOLON item)* SEMICOLON? EOF ;\n");
		grammarBuilder.append("item : A B?;\n");
		grammarBuilder.append("SEMICOLON: ';';\n");
		grammarBuilder.append("A : 'a'|'A';\n");
		grammarBuilder.append("B : 'b'|'B';\n");
		grammarBuilder.append("WS      : [ \\r\\t\\n]+ -> skip;");
		String grammar = grammarBuilder.toString();


		String input ="a";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "file_", input, false);
		assertEquals("(file_ (item a) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testReferenceToATN_1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(120);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : (ID|ATN)* ATN? {System.out.println($text);} ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("ATN : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testReferenceToATN_2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(120);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : (ID|ATN)* ATN? {System.out.println($text);} ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("ATN : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="a 34 c";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals("a34c\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testTokenOffset() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(401);
		grammarBuilder.append("grammar L;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("a : ('1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'|'10'|'11'|'12'|'13'|'14'|'15'|'16'\n");
		grammarBuilder.append("|'17'|'18'|'19'|'20'|'21'|'22'|'23'|'24'|'25'|'26'|'27'|'28'|'29'|'30'|'31'|'32'\n");
		grammarBuilder.append("|'33'|'34'|'35'|'36'|'37'|'38'|'39'|'40'|'41'|'42'|'43'|'44'|'45'|'46'|'47'|'48'\n");
		grammarBuilder.append("|'49'|'50'|'51'|'52'|'53'|'54'|'55'|'56'|'57'|'58'|'59'|'60'|'61'|'62'|'63'|'64'\n");
		grammarBuilder.append("|'65'|'66')+ {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("};\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;\n");
		String grammar = grammarBuilder.toString();


		String input ="12 34 56 66";
		String found = execParser("L.g4", grammar, "LParser", "LLexer", "a", input, false);
		assertEquals("12345666\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testWildcard() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(148);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("a : (assign|.)+ EOF ;\n");
		grammarBuilder.append("assign : ID '=' INT ';' {\n");
		grammarBuilder.append("System.out.println($text);\n");
		grammarBuilder.append("} ;\n");
		grammarBuilder.append("ID : 'a'..'z'+ ;\n");
		grammarBuilder.append("INT : '0'..'9'+;\n");
		grammarBuilder.append("WS : (' '|'\\n') -> skip;");
		String grammar = grammarBuilder.toString();


		String input ="x=10; abc;;;; y=99;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
		assertEquals(
			"x=10;\n" +
			"y=99;\n", found);
		assertNull(this.stderrDuringParse);

	}


}
