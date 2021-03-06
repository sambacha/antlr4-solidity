package org.antlr.v4.test.runtime;

import org.antlr.v4.test.tool.BaseTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestListeners extends BaseTest {

	@Test
	public void testBasic() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(453);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("@parser::members {\n");
		grammarBuilder.append("public static class LeafListener extends TBaseListener {\n");
		grammarBuilder.append("	public void visitTerminal(TerminalNode node) {\n");
		grammarBuilder.append("		System.out.println(node.getSymbol().getText());\n");
		grammarBuilder.append("	}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("s\n");
		grammarBuilder.append("@after {\n");
		grammarBuilder.append("System.out.println($ctx.r.toStringTree(this));\n");
		grammarBuilder.append("ParseTreeWalker walker = new ParseTreeWalker();\n");
		grammarBuilder.append("walker.walk(new LeafListener(), $ctx.r);\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("  : r=a ;\n");
		grammarBuilder.append("a : INT INT\n");
		grammarBuilder.append("  | ID\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("MULT: '*' ;\n");
		grammarBuilder.append("ADD : '+' ;\n");
		grammarBuilder.append("INT : [0-9]+ ;\n");
		grammarBuilder.append("ID  : [a-z]+ ;\n");
		grammarBuilder.append("WS : [ \\t\\n]+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="1 2";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"(a 1 2)\n" +
			"1\n" +
			"2\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testLR() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(633);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("@parser::members {\n");
		grammarBuilder.append("public static class LeafListener extends TBaseListener {\n");
		grammarBuilder.append("	public void exitE(TParser.EContext ctx) {\n");
		grammarBuilder.append("		if (ctx.getChildCount()==3) {\n");
		grammarBuilder.append("			System.out.printf(\"%s %s %s\\n\",ctx.e(0).start.getText(),\n");
		grammarBuilder.append("				ctx.e(1).start.getText(), ctx.e().get(0).start.getText());\n");
		grammarBuilder.append("		} else\n");
		grammarBuilder.append("			System.out.println(ctx.INT().getSymbol().getText());\n");
		grammarBuilder.append("	}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("s\n");
		grammarBuilder.append("@after {\n");
		grammarBuilder.append("System.out.println($ctx.r.toStringTree(this));\n");
		grammarBuilder.append("ParseTreeWalker walker = new ParseTreeWalker();\n");
		grammarBuilder.append("walker.walk(new LeafListener(), $ctx.r);\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("	: r=e ;\n");
		grammarBuilder.append("e : e op='*' e\n");
		grammarBuilder.append("	| e op='+' e\n");
		grammarBuilder.append("	| INT\n");
		grammarBuilder.append("	;\n");
		grammarBuilder.append("MULT: '*' ;\n");
		grammarBuilder.append("ADD : '+' ;\n");
		grammarBuilder.append("INT : [0-9]+ ;\n");
		grammarBuilder.append("ID  : [a-z]+ ;\n");
		grammarBuilder.append("WS : [ \\t\\n]+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="1+2*3";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"(e (e 1) + (e (e 2) * (e 3)))\n" +
			"1\n" +
			"2\n" +
			"3\n" +
			"2 3 2\n" +
			"1 2 1\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testLRWithLabels() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(630);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("@parser::members {\n");
		grammarBuilder.append("public static class LeafListener extends TBaseListener {\n");
		grammarBuilder.append("	public void exitCall(TParser.CallContext ctx) {\n");
		grammarBuilder.append("		System.out.printf(\"%s %s\",ctx.e().start.getText(),ctx.eList());\n");
		grammarBuilder.append("	}\n");
		grammarBuilder.append("	public void exitInt(TParser.IntContext ctx) {\n");
		grammarBuilder.append("		System.out.println(ctx.INT().getSymbol().getText());\n");
		grammarBuilder.append("	}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("s\n");
		grammarBuilder.append("@after {\n");
		grammarBuilder.append("System.out.println($ctx.r.toStringTree(this));\n");
		grammarBuilder.append("ParseTreeWalker walker = new ParseTreeWalker();\n");
		grammarBuilder.append("walker.walk(new LeafListener(), $ctx.r);\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("  : r=e ;\n");
		grammarBuilder.append("e : e '(' eList ')' # Call\n");
		grammarBuilder.append("  | INT             # Int\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("eList : e (',' e)* ;\n");
		grammarBuilder.append("MULT: '*' ;\n");
		grammarBuilder.append("ADD : '+' ;\n");
		grammarBuilder.append("INT : [0-9]+ ;\n");
		grammarBuilder.append("ID  : [a-z]+ ;\n");
		grammarBuilder.append("WS : [ \\t\\n]+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="1(2,3)";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"(e (e 1) ( (eList (e 2) , (e 3)) ))\n" +
			"1\n" +
			"2\n" +
			"3\n" +
			"1 [13 6]\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testRuleGetters_1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(650);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("@parser::members {\n");
		grammarBuilder.append("public static class LeafListener extends TBaseListener {\n");
		grammarBuilder.append("	public void exitA(TParser.AContext ctx) {\n");
		grammarBuilder.append("		if (ctx.getChildCount()==2) {\n");
		grammarBuilder.append("			System.out.printf(\"%s %s %s\",ctx.b(0).start.getText(),\n");
		grammarBuilder.append("				ctx.b(1).start.getText(),ctx.b().get(0).start.getText());\n");
		grammarBuilder.append("		} else\n");
		grammarBuilder.append("			System.out.println(ctx.b(0).start.getText());\n");
		grammarBuilder.append("	}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("s\n");
		grammarBuilder.append("@after {\n");
		grammarBuilder.append("System.out.println($ctx.r.toStringTree(this));\n");
		grammarBuilder.append("ParseTreeWalker walker = new ParseTreeWalker();\n");
		grammarBuilder.append("walker.walk(new LeafListener(), $ctx.r);\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("  : r=a ;\n");
		grammarBuilder.append("a : b b		// forces list\n");
		grammarBuilder.append("  | b		// a list still\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("b : ID | INT;\n");
		grammarBuilder.append("MULT: '*' ;\n");
		grammarBuilder.append("ADD : '+' ;\n");
		grammarBuilder.append("INT : [0-9]+ ;\n");
		grammarBuilder.append("ID  : [a-z]+ ;\n");
		grammarBuilder.append("WS : [ \\t\\n]+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="1 2";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"(a (b 1) (b 2))\n" +
			"1 2 1\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testRuleGetters_2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(650);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("@parser::members {\n");
		grammarBuilder.append("public static class LeafListener extends TBaseListener {\n");
		grammarBuilder.append("	public void exitA(TParser.AContext ctx) {\n");
		grammarBuilder.append("		if (ctx.getChildCount()==2) {\n");
		grammarBuilder.append("			System.out.printf(\"%s %s %s\",ctx.b(0).start.getText(),\n");
		grammarBuilder.append("				ctx.b(1).start.getText(),ctx.b().get(0).start.getText());\n");
		grammarBuilder.append("		} else\n");
		grammarBuilder.append("			System.out.println(ctx.b(0).start.getText());\n");
		grammarBuilder.append("	}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("s\n");
		grammarBuilder.append("@after {\n");
		grammarBuilder.append("System.out.println($ctx.r.toStringTree(this));\n");
		grammarBuilder.append("ParseTreeWalker walker = new ParseTreeWalker();\n");
		grammarBuilder.append("walker.walk(new LeafListener(), $ctx.r);\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("  : r=a ;\n");
		grammarBuilder.append("a : b b		// forces list\n");
		grammarBuilder.append("  | b		// a list still\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("b : ID | INT;\n");
		grammarBuilder.append("MULT: '*' ;\n");
		grammarBuilder.append("ADD : '+' ;\n");
		grammarBuilder.append("INT : [0-9]+ ;\n");
		grammarBuilder.append("ID  : [a-z]+ ;\n");
		grammarBuilder.append("WS : [ \\t\\n]+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="abc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"(a (b abc))\n" +
			"abc\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testTokenGetters_1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(595);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("@parser::members {\n");
		grammarBuilder.append("public static class LeafListener extends TBaseListener {\n");
		grammarBuilder.append("	public void exitA(TParser.AContext ctx) {\n");
		grammarBuilder.append("		if (ctx.getChildCount()==2)\n");
		grammarBuilder.append("			System.out.printf(\"%s %s %s\",ctx.INT(0).getSymbol().getText(),\n");
		grammarBuilder.append("				ctx.INT(1).getSymbol().getText(),ctx.INT());\n");
		grammarBuilder.append("		else\n");
		grammarBuilder.append("			System.out.println(ctx.ID().getSymbol());\n");
		grammarBuilder.append("	}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("s\n");
		grammarBuilder.append("@after {\n");
		grammarBuilder.append("System.out.println($ctx.r.toStringTree(this));\n");
		grammarBuilder.append("ParseTreeWalker walker = new ParseTreeWalker();\n");
		grammarBuilder.append("walker.walk(new LeafListener(), $ctx.r);\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("  : r=a ;\n");
		grammarBuilder.append("a : INT INT\n");
		grammarBuilder.append("  | ID\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("MULT: '*' ;\n");
		grammarBuilder.append("ADD : '+' ;\n");
		grammarBuilder.append("INT : [0-9]+ ;\n");
		grammarBuilder.append("ID  : [a-z]+ ;\n");
		grammarBuilder.append("WS : [ \\t\\n]+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="1 2";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"(a 1 2)\n" +
			"1 2 [1, 2]\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testTokenGetters_2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(595);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("@parser::members {\n");
		grammarBuilder.append("public static class LeafListener extends TBaseListener {\n");
		grammarBuilder.append("	public void exitA(TParser.AContext ctx) {\n");
		grammarBuilder.append("		if (ctx.getChildCount()==2)\n");
		grammarBuilder.append("			System.out.printf(\"%s %s %s\",ctx.INT(0).getSymbol().getText(),\n");
		grammarBuilder.append("				ctx.INT(1).getSymbol().getText(),ctx.INT());\n");
		grammarBuilder.append("		else\n");
		grammarBuilder.append("			System.out.println(ctx.ID().getSymbol());\n");
		grammarBuilder.append("	}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("s\n");
		grammarBuilder.append("@after {\n");
		grammarBuilder.append("System.out.println($ctx.r.toStringTree(this));\n");
		grammarBuilder.append("ParseTreeWalker walker = new ParseTreeWalker();\n");
		grammarBuilder.append("walker.walk(new LeafListener(), $ctx.r);\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("  : r=a ;\n");
		grammarBuilder.append("a : INT INT\n");
		grammarBuilder.append("  | ID\n");
		grammarBuilder.append("  ;\n");
		grammarBuilder.append("MULT: '*' ;\n");
		grammarBuilder.append("ADD : '+' ;\n");
		grammarBuilder.append("INT : [0-9]+ ;\n");
		grammarBuilder.append("ID  : [a-z]+ ;\n");
		grammarBuilder.append("WS : [ \\t\\n]+ -> skip ;");
		String grammar = grammarBuilder.toString();


		String input ="abc";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"(a abc)\n" +
			"[@0,0:2='abc',<4>,1:0]\n", found);
		assertNull(this.stderrDuringParse);

	}


}
