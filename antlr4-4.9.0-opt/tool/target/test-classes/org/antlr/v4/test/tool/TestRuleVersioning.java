/*
 * Copyright (c) 2012 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD-3-Clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.misc.Predicate;
import org.antlr.v4.runtime.misc.Tuple;
import org.antlr.v4.runtime.misc.Tuple2;
import org.antlr.v4.runtime.misc.Utils;
import org.junit.Ignore;
import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Sam Harwell
 */
public class TestRuleVersioning extends BaseTest {

	private Properties _properties;

	@Test
	public void testPropertyEvaluation() throws Exception {
		Properties properties = getProperties();
		assertNotNull(properties);

		STGroup group = createGroup(properties);
		assertNotNull(group);

		Set<String> templateNames = group.getTemplateNames();
		String templateName0 = "/PropertyEval0-sample-template";
		assertTrue(templateNames.contains(templateName0));
		ST st = group.getInstanceOf(templateName0);
		assertNotNull(st);
		String result = st.render();
		assertEquals("foo", result);

		String templateName1 = "/PropertyEval1-sample-template";
		assertTrue(templateNames.contains(templateName1));
		ST st1 = group.getInstanceOf(templateName1);
		assertNotNull(st1);
		String result1 = st1.render();
		assertEquals("foo", result1);

		assertEquals("foo", resolveProperty(properties, group, "PropertyEval0", "sample"));
		assertEquals("foo", resolveProperty(properties, group, "PropertyEval1", "sample"));
		assertEquals("foo", resolveProperty(properties, group, "PropertyEval2", "sample"));
		assertEquals("foo", resolveProperty(properties, group, "PropertyEval3", "sample"));
		assertEquals("foo", resolveProperty(properties, group, "PropertyEval4", "sample"));
		assertEquals("foo", resolveProperty(properties, group, "PropertyEval5", "sample"));
	}

	@Test
	public void testSingleDependency() throws Exception {
		testBasicGrammar("SingleDependency", "class TParser$Dependent", true);
	}

	@Test
	public void testSingleDependencyFailed() throws Exception {
		testBasicGrammar("SingleDependencyFailed", "class TParser$Dependent", false);
	}

	@Test
	public void testMultipleDependencySingleGrammar() throws Exception {
		testBasicGrammar("MultipleDependencySingleGrammar", "", true);
	}

	@Test
	public void testMultipleDependencySingleGrammarFailed() throws Exception {
		testBasicGrammar("MultipleDependencySingleGrammarFailed", "class TParser$Dependent", false);
	}

	@Test
	public void testMultipleDependencySingleGrammarFailed2() throws Exception {
		testBasicGrammar("MultipleDependencySingleGrammarFailed2", "class TParser$Dependent", false);
		String secondLine = stderrDuringParse.split("\\n", 3)[1];
		String expected = "Element class TParser$Dependent dependent on rule b@0 (found @1) in TParser";
		assertEquals(expected, secondLine);
	}

	@Test
	@Ignore
	public void testMultipleDependencyMultipleGrammar() throws Exception {
		// This is intended to test a class with multiple dependencies on different grammars,
		// but is not yet implemented.
		testGrammar("MultipleDependencyMultipleGrammar");
	}

	@Test
	public void testClassDependency() throws Exception {
		testBasicGrammar("ClassDependency", "class TParser$Dependent", true);
	}

	@Test
	public void testClassDependencyFailed() throws Exception {
		testBasicGrammar("ClassDependencyFailed", "class TParser$Dependent", false);
	}

	@Test
	public void testMethodDependency() throws Exception {
		testBasicGrammar("MethodDependency", "public void TParser$Dependent.foo()", true);
	}

	@Test
	public void testMethodDependencyFailed() throws Exception {
		testBasicGrammar("MethodDependencyFailed", "public void TParser$Dependent.foo()", false);
	}

	@Test
	public void testConstructorDependency() throws Exception {
		testBasicGrammar("ConstructorDependency", "public TParser$Dependent()", true);
	}

	@Test
	public void testConstructorDependencyFailed() throws Exception {
		testBasicGrammar("ConstructorDependencyFailed", "public TParser$Dependent()", false);
	}

	@Test
	public void testDependencyOnInvalidRule() throws Exception {
		testGrammar("DependencyOnInvalidRule");
		assertNotNullOrEmpty(stderrDuringParse);
		String firstLine = stderrDuringParse.split("\\n", 2)[0];
		String expected = "Exception in thread \"main\" java.lang.IllegalStateException: Element public TParser$Dependent() dependent on unknown rule 8@0 in TParser";
		assertEquals(expected, firstLine);
	}

	@Override
	protected boolean testInSameProcess() {
		return false;
	}

	private void testBasicGrammar(String name, String elementName, boolean expectSuccess) throws Exception {
		testGrammar(name);
		if (expectSuccess) {
			assertNull(stderrDuringParse);
		}
		else {
			assertNotNullOrEmpty(stderrDuringParse);
			String firstLine = stderrDuringParse.split("\\n", 2)[0];
			String expected = "Exception in thread \"main\" java.lang.IllegalStateException: Element " + elementName + " dependent on rule a@1 (found @0) in TParser";
			assertEquals(expected, firstLine);
		}
	}

	private void testGrammar(String name) throws Exception {
		Properties properties = getProperties();
		STGroup group = createGroup(properties);

		String grammarName = resolveProperty(properties, group, name, "gname");
		assertNotNullOrEmpty(grammarName);

		String grammar = resolveProperty(properties, group, name, "grammar");
		assertNotNullOrEmpty(grammar);

		String listener = resolveProperty(properties, group, name, "dependent");
		if (grammar.contains("##dependent##")) {
			assertNotNullOrEmpty(listener);
			grammar = grammar.replace("##dependent##", listener);
		}
		else {
			assertNull(listener);
		}

		String parserName = resolveProperty(properties, group, name, "parser");
		if (parserName == null) {
			parserName = grammarName + "Parser";
		}

		String lexerName = resolveProperty(properties, group, name, "lexer");
		if (lexerName == null) {
			lexerName = grammarName + "Lexer";
		}

		String startRuleName = "a";
		String input = "x";
		boolean debug = true;
		execParser(grammarName + ".g", grammar, parserName, lexerName, startRuleName, input, debug);
	}

	private int anonIndex = 0;

	private String resolveProperty(Properties properties, STGroup group, String basename, String extension) {
		String propertyName = basename;
		if (extension != null) {
			propertyName = basename + '.' + extension;
		}

		String templateName = "anon--" + anonIndex;
		anonIndex++;
		String templateText = "<" + propertyName + ">";
		group.defineTemplate(templateName, templateText);
		return group.getInstanceOf(templateName).render();
	}

	private Properties getProperties() throws IOException {
		if (_properties == null) {
			_properties = new Properties();
			String resourceName = TestRuleVersioning.class.getPackage().getName().replace('.', '/');
			resourceName = resourceName + "/" + "TestRuleVersioningGrammars.properties";
			_properties.load(TestRuleVersioning.class.getClassLoader().getResourceAsStream(resourceName));
		}

		return _properties;
	}

	private STGroup createGroup(Properties properties) {
		STGroup group = new STGroup();
		Map<String, Map<String, Object>> dictionaries =  new HashMap<String, Map<String, Object>>();
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String key = entry.getKey().toString();
			String[] elements = key.split("\\.");

			Map<String, Object> current = dictionaries.get(elements[0]);
			if (current == null) {
				current = new PropertiesWrapper(properties, elements[0], group);
				group.defineDictionary(elements[0], current);
				dictionaries.put(elements[0], current);
			}

			if (elements[elements.length - 1].equals("template")) {
				group.defineTemplate(key.replace('.', '-'), entry.getValue().toString());
			}
		}

		return group;
	}

	@Override
	public List<String> getCompileOptions() {
		List<String> result = new ArrayList<String>(super.getCompileOptions());
		result.add(0, "-proc:none");
		return result;
	}

	private static class PropertiesWrapper extends AbstractMap<String, Object> {
		private final Properties properties;
		private final List<Tuple2<String, Integer>> prefixes;
		private final STGroup group;

		public PropertiesWrapper(Properties properties, String prefix, STGroup group) {
			this(properties, Collections.singletonList(Tuple.create(prefix, 0)), group);
		}

		public PropertiesWrapper(Properties properties, List<Tuple2<String, Integer>> prefixes, STGroup group) {
			this.properties = properties;
			this.prefixes = prefixes;
			this.group = group;
		}

		@Override
		public Set<Entry<String, Object>> entrySet() {
			List<Entry<Object, Object>> entries = new ArrayList<Entry<Object, Object>>(properties.entrySet());
			Utils.removeAll(entries, new Predicate<Entry<Object, Object>>() {

				@Override
				public boolean eval(Entry<Object, Object> arg) {
					String key = arg.getKey().toString();
					for (Tuple2<String, Integer> prefix : prefixes) {
						if (key.startsWith(prefix.getItem1() + '.') || key.equals(prefix.getItem1())) {
							// don't remove items matching the prefix
							return false;
						}
					}
					// remove items without a match
					return true;
				}
			});

			final Map<String, Object> subkeyValues = new HashMap<String, Object>();
			final Map<String, List<Tuple2<String, Integer>>> subkeyPrefixes = new HashMap<String, List<Tuple2<String, Integer>>>();
			for (int i = 0; i < prefixes.size(); i++) {
				Tuple2<String, Integer> prefix = prefixes.get(i);
				for (Entry<Object, Object> entry : entries) {
					String key = entry.getKey().toString();
					if (key.equals(prefix.getItem1()) || key.equals(prefix.getItem1() + ".template") || key.equals(prefix.getItem1() + ".inherit")) {
						// what to do with these items?
						throw new UnsupportedOperationException();
					}
					else if (key.startsWith(prefix.getItem1() + '.')) {
						String subkey = key.substring(prefix.getItem1().length() + 1);
						int dot = subkey.indexOf('.');
						boolean template = subkey.substring(dot + 1).equals("template");

						if (dot >= 0 && !template) {
							boolean inherit = subkey.substring(dot + 1).equals("inherit");

							assert !subkeyValues.containsKey(subkey);
							subkey = subkey.substring(0, dot);
							List<Tuple2<String, Integer>> prefixList = subkeyPrefixes.get(subkey);
							if (prefixList == null) {
								prefixList = new ArrayList<Tuple2<String, Integer>>();
								subkeyPrefixes.put(subkey, prefixList);
							}

							String nextPrefix = inherit ? entry.getValue().toString() : prefix.getItem1() + '.' + subkey;
							int priority = prefix.getItem2() + (inherit ? 1 : 0);
							prefixList.add(Tuple.create(nextPrefix, priority));
						}
						else {
							assert !subkeyPrefixes.containsKey(subkey);
							// earlier prefixes take priority (later are due to further inheritance
							if (!subkeyValues.containsKey(subkey)) {
								Object value;
								if (template) {
									subkey = subkey.substring(0, dot);
									String templateName = key.replace('.', '-');
									value = group.getInstanceOf(templateName);
								}
								else {
									value = entry.getValue().toString();
								}

								subkeyValues.put(subkey, value);
							}
						}
					}
				}
			}

			// sort prefix lists by priority
			for (List<Tuple2<String, Integer>> list : subkeyPrefixes.values()) {
				Collections.sort(list, new Comparator<Tuple2<String, Integer>>() {
					@Override
					public int compare(Tuple2<String, Integer> o1, Tuple2<String, Integer> o2) {
						return o1.getItem2() - o2.getItem2();
					}
				});
			}

			return new AbstractSet<Entry<String, Object>>() {
				@Override
				public Iterator<Entry<String, Object>> iterator() {
					return new Iterator<Entry<String, Object>>() {
						Iterator<Entry<String, Object>> valueIterator = subkeyValues.entrySet().iterator();
						Iterator<Entry<String, List<Tuple2<String, Integer>>>> prefixIterator = subkeyPrefixes.entrySet().iterator();

						@Override
						public boolean hasNext() {
							return valueIterator.hasNext() || prefixIterator.hasNext();
						}

						@Override
						public Entry<String, Object> next() {
							if (valueIterator.hasNext()) {
								return valueIterator.next();
							}
							else {
								final Entry<String, List<Tuple2<String, Integer>>> next = prefixIterator.next();
								return new Entry<String, Object>() {
									@Override
									public String getKey() {
										return next.getKey();
									}

									@Override
									public Object getValue() {
										return new PropertiesWrapper(properties, next.getValue(), group);
									}

									@Override
									public Object setValue(Object value) {
										throw new UnsupportedOperationException("Not supported.");
									}
								};
							}
						}

						@Override
						public void remove() {
							throw new UnsupportedOperationException("Not supported.");
						}
					};
				}

				@Override
				public int size() {
					throw new UnsupportedOperationException("Not supported yet.");
				}
			};
		}

	}
}
