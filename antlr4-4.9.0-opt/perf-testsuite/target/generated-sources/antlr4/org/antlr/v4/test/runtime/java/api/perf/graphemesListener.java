// Generated from org/antlr/v4/test/runtime/java/api/perf/graphemes.g4 by ANTLR 4.9.0
package org.antlr.v4.test.runtime.java.api.perf;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link graphemesParser}.
 */
public interface graphemesListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link graphemesParser#emoji_sequence}.
	 * @param ctx the parse tree
	 */
	void enterEmoji_sequence(@NotNull graphemesParser.Emoji_sequenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link graphemesParser#emoji_sequence}.
	 * @param ctx the parse tree
	 */
	void exitEmoji_sequence(@NotNull graphemesParser.Emoji_sequenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link graphemesParser#grapheme_cluster}.
	 * @param ctx the parse tree
	 */
	void enterGrapheme_cluster(@NotNull graphemesParser.Grapheme_clusterContext ctx);
	/**
	 * Exit a parse tree produced by {@link graphemesParser#grapheme_cluster}.
	 * @param ctx the parse tree
	 */
	void exitGrapheme_cluster(@NotNull graphemesParser.Grapheme_clusterContext ctx);
	/**
	 * Enter a parse tree produced by {@link graphemesParser#graphemes}.
	 * @param ctx the parse tree
	 */
	void enterGraphemes(@NotNull graphemesParser.GraphemesContext ctx);
	/**
	 * Exit a parse tree produced by {@link graphemesParser#graphemes}.
	 * @param ctx the parse tree
	 */
	void exitGraphemes(@NotNull graphemesParser.GraphemesContext ctx);
}