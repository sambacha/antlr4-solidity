package org.antlr.v4.test.runtime;

import org.antlr.v4.test.tool.BaseTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestVisitors extends BaseTest {

	@Test
	public void testCalculatorVisitor() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(1360);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("@parser::members {\n");
		grammarBuilder.append("	public static class LeafVisitor extends TBaseVisitor<Integer> {\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		public Integer visitS(SContext ctx) {\n");
		grammarBuilder.append("			return visit(ctx.expr());\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		public Integer visitNumber(NumberContext ctx) {\n");
		grammarBuilder.append("			return Integer.valueOf(ctx.INT().getText());\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		public Integer visitMultiply(MultiplyContext ctx) {\n");
		grammarBuilder.append("			Integer left = visit(ctx.expr(0));\n");
		grammarBuilder.append("			Integer right = visit(ctx.expr(1));\n");
		grammarBuilder.append("			if (ctx.MUL() != null) {\n");
		grammarBuilder.append("				return left * right;\n");
		grammarBuilder.append("			}\n");
		grammarBuilder.append("			else {\n");
		grammarBuilder.append("				return left / right;\n");
		grammarBuilder.append("			}\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		public Integer visitAdd(AddContext ctx) {\n");
		grammarBuilder.append("			Integer left = visit(ctx.expr(0));\n");
		grammarBuilder.append("			Integer right = visit(ctx.expr(1));\n");
		grammarBuilder.append("			if (ctx.ADD() != null) {\n");
		grammarBuilder.append("				return left + right;\n");
		grammarBuilder.append("			}\n");
		grammarBuilder.append("			else {\n");
		grammarBuilder.append("				return left - right;\n");
		grammarBuilder.append("			}\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		protected Integer defaultResult() {\n");
		grammarBuilder.append("			throw new RuntimeException(\"Should not be reachable\");\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		protected Integer aggregateResult(Integer aggregate, Integer nextResult) {\n");
		grammarBuilder.append("			throw new RuntimeException(\"Should not be reachable\");\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("	}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("s\n");
		grammarBuilder.append("@after {\n");
		grammarBuilder.append("System.out.println($ctx.toStringTree(this));\n");
		grammarBuilder.append("System.out.println(new LeafVisitor().visit($ctx));\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("	:	expr EOF\n");
		grammarBuilder.append("	;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("expr\n");
		grammarBuilder.append("	:	INT						# number\n");
		grammarBuilder.append("	|	expr (MUL | DIV) expr	# multiply\n");
		grammarBuilder.append("	|	expr (ADD | SUB) expr	# add\n");
		grammarBuilder.append("	;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("INT : [0-9]+;\n");
		grammarBuilder.append("MUL : '*';\n");
		grammarBuilder.append("DIV : '/';\n");
		grammarBuilder.append("ADD : '+';\n");
		grammarBuilder.append("SUB : '-';\n");
		grammarBuilder.append("WS : [ \\t]+ -> channel(HIDDEN);");
		String grammar = grammarBuilder.toString();


		String input ="2 + 8 / 2";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"(s (expr (expr 2) + (expr (expr 8) / (expr 2))) <EOF>)\n" +
			"6\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testShouldNotVisitEOF() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(493);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("@parser::members {\n");
		grammarBuilder.append("	public static class LeafVisitor extends TBaseVisitor<String> {\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		public String visitTerminal(TerminalNode node) {\n");
		grammarBuilder.append("			return node.getSymbol().toString() + \"\\n\";\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		protected boolean shouldVisitNextChild(RuleNode node, String currentResult) {\n");
		grammarBuilder.append("			return currentResult == null || currentResult.isEmpty();\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("	}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("s\n");
		grammarBuilder.append("@after {\n");
		grammarBuilder.append("System.out.println($ctx.toStringTree(this));\n");
		grammarBuilder.append("System.out.println(new LeafVisitor().visit($ctx));\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("	:	'A' EOF\n");
		grammarBuilder.append("	;");
		String grammar = grammarBuilder.toString();


		String input ="A";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"(s A <EOF>)\n" +
			"[@0,0:0='A',<1>,1:0]\n" +
			"\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testShouldNotVisitTerminal() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(544);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("@parser::members {\n");
		grammarBuilder.append("	public static class LeafVisitor extends TBaseVisitor<String> {\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		public String visitTerminal(TerminalNode node) {\n");
		grammarBuilder.append("			throw new RuntimeException(\"Should not be reachable\");\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		protected String defaultResult() {\n");
		grammarBuilder.append("			return \"default result\";\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		protected boolean shouldVisitNextChild(RuleNode node, String currentResult) {\n");
		grammarBuilder.append("			return false;\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("	}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("s\n");
		grammarBuilder.append("@after {\n");
		grammarBuilder.append("System.out.println($ctx.toStringTree(this));\n");
		grammarBuilder.append("System.out.println(new LeafVisitor().visit($ctx));\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("	:	'A' EOF\n");
		grammarBuilder.append("	;");
		String grammar = grammarBuilder.toString();


		String input ="A";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"(s A <EOF>)\n" +
			"default result\n", found);
		assertNull(this.stderrDuringParse);

	}

	@Test
	public void testVisitErrorNode() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(533);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("@parser::members {\n");
		grammarBuilder.append("	public static class LeafVisitor extends TBaseVisitor<String> {\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		public String visitErrorNode(ErrorNode node) {\n");
		grammarBuilder.append("			return \"Error encountered: \" + node.getSymbol();\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		protected String defaultResult() {\n");
		grammarBuilder.append("			return \"\";\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		protected String aggregateResult(String aggregate, String nextResult) {\n");
		grammarBuilder.append("			return aggregate + nextResult;\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("	}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("s\n");
		grammarBuilder.append("@after {\n");
		grammarBuilder.append("System.out.println($ctx.toStringTree(this));\n");
		grammarBuilder.append("System.out.println(new LeafVisitor().visit($ctx));\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("	:	'A' EOF\n");
		grammarBuilder.append("	;");
		String grammar = grammarBuilder.toString();


		String input ="";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"(s <missing 'A'> <EOF>)\n" +
			"Error encountered: [@-1,-1:-1='<missing 'A'>',<1>,1:0]\n", found);

		assertEquals("line 1:0 missing 'A' at '<EOF>'\n", this.stderrDuringParse);

	}

	@Test
	public void testVisitTerminalNode() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(529);
		grammarBuilder.append("grammar T;\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("@parser::members {\n");
		grammarBuilder.append("	public static class LeafVisitor extends TBaseVisitor<String> {\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		public String visitTerminal(TerminalNode node) {\n");
		grammarBuilder.append("			return node.getSymbol().toString() + \"\\n\";\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		protected String defaultResult() {\n");
		grammarBuilder.append("			return \"\";\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("		@Override\n");
		grammarBuilder.append("		protected String aggregateResult(String aggregate, String nextResult) {\n");
		grammarBuilder.append("			return aggregate + nextResult;\n");
		grammarBuilder.append("		}\n");
		grammarBuilder.append("	}\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("\n");
		grammarBuilder.append("s\n");
		grammarBuilder.append("@after {\n");
		grammarBuilder.append("System.out.println($ctx.toStringTree(this));\n");
		grammarBuilder.append("System.out.println(new LeafVisitor().visit($ctx));\n");
		grammarBuilder.append("}\n");
		grammarBuilder.append("	:	'A' EOF\n");
		grammarBuilder.append("	;");
		String grammar = grammarBuilder.toString();


		String input ="A";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
		assertEquals(
			"(s A <EOF>)\n" +
			"[@0,0:0='A',<1>,1:0]\n" +
			"[@1,1:0='<EOF>',<-1>,1:1]\n" +
			"\n", found);
		assertNull(this.stderrDuringParse);

	}


}
