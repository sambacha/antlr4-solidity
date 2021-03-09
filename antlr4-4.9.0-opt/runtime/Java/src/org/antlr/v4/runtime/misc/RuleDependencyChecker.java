/*
 * Copyright (c) 2012 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD-3-Clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime.misc;

import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleDependencies;
import org.antlr.v4.runtime.RuleDependency;
import org.antlr.v4.runtime.RuleVersion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sam Harwell
 */
public class RuleDependencyChecker {
	private static final Logger LOGGER = Logger.getLogger(RuleDependencyChecker.class.getName());

	private static final Set<Class<?>> checkedTypes = new HashSet<Class<?>>();

	public static void checkDependencies(Class<?> dependentClass) {
		if (isChecked(dependentClass)) {
			return;
		}

		List<Class<?>> typesToCheck = new ArrayList<Class<?>>();
		typesToCheck.add(dependentClass);
		Collections.addAll(typesToCheck, dependentClass.getDeclaredClasses());
		for (final Class<?> clazz : typesToCheck) {
			if (isChecked(clazz)) {
				continue;
			}

			List<Tuple2<RuleDependency, AnnotatedElement>> dependencies = getDependencies(clazz);
			if (dependencies.isEmpty()) {
				continue;
			}

			checkDependencies(dependencies, dependencies.get(0).getItem1().recognizer());
		}
	}

	private static boolean isChecked(Class<?> clazz) {
		synchronized (checkedTypes) {
			return checkedTypes.contains(clazz);
		}
	}

	private static void markChecked(Class<?> clazz) {
		synchronized (checkedTypes) {
			checkedTypes.add(clazz);
		}
	}

	private static void checkDependencies(List<Tuple2<RuleDependency, AnnotatedElement>> dependencies, Class<? extends Recognizer<?, ?>> recognizerClass) {
		String[] ruleNames = getRuleNames(recognizerClass);
		int[] ruleVersions = getRuleVersions(recognizerClass, ruleNames);

		StringBuilder incompatible = new StringBuilder();
		for (Tuple2<RuleDependency, AnnotatedElement> dependency : dependencies) {
			if (!recognizerClass.isAssignableFrom(dependency.getItem1().recognizer())) {
				continue;
			}

			if (dependency.getItem1().rule() < 0 || dependency.getItem1().rule() >= ruleVersions.length) {
				incompatible.append(String.format("Element %s dependent on unknown rule %d@%d in %s\n",
												  dependency.getItem2().toString(),
												  dependency.getItem1().rule(),
												  dependency.getItem1().version(),
												  dependency.getItem1().recognizer().getSimpleName()));
			}
			else if (ruleVersions[dependency.getItem1().rule()] != dependency.getItem1().version()) {
				incompatible.append(String.format("Element %s dependent on rule %s@%d (found @%d) in %s\n",
												  dependency.getItem2().toString(),
												  ruleNames[dependency.getItem1().rule()],
												  dependency.getItem1().version(),
												  ruleVersions[dependency.getItem1().rule()],
												  dependency.getItem1().recognizer().getSimpleName()));
			}
		}

		if (incompatible.length() != 0) {
			throw new IllegalStateException(incompatible.toString());
		}

		markChecked(recognizerClass);
	}

	private static int[] getRuleVersions(Class<? extends Recognizer<?, ?>> recognizerClass, String[] ruleNames) {
		int[] versions = new int[ruleNames.length];

		Field[] fields = recognizerClass.getFields();
		for (Field field : fields) {
			boolean isStatic = (field.getModifiers() & Modifier.STATIC) != 0;
			boolean isInteger = field.getType() == Integer.TYPE;
			if (isStatic && isInteger && field.getName().startsWith("RULE_")) {
				try {
					String name = field.getName().substring("RULE_".length());
					if (name.isEmpty() || !Character.isLowerCase(name.charAt(0))) {
						continue;
					}

					int index = field.getInt(null);
					if (index < 0 || index >= versions.length) {
						Object[] params = { index, field.getName(), recognizerClass.getSimpleName() };
						LOGGER.log(Level.WARNING, "Rule index {0} for rule ''{1}'' out of bounds for recognizer {2}.", params);
						continue;
					}

					Method ruleMethod = getRuleMethod(recognizerClass, name);
					if (ruleMethod == null) {
						Object[] params = { name, recognizerClass.getSimpleName() };
						LOGGER.log(Level.WARNING, "Could not find rule method for rule ''{0}'' in recognizer {1}.", params);
						continue;
					}

					RuleVersion ruleVersion = ruleMethod.getAnnotation(RuleVersion.class);
					int version = ruleVersion != null ? ruleVersion.value() : 0;
					versions[index] = version;
				} catch (IllegalArgumentException ex) {
					LOGGER.log(Level.WARNING, null, ex);
				} catch (IllegalAccessException ex) {
					LOGGER.log(Level.WARNING, null, ex);
				}
			}
		}

		return versions;
	}

	private static Method getRuleMethod(Class<? extends Recognizer<?, ?>> recognizerClass, String name) {
		Method[] declaredMethods = recognizerClass.getMethods();
		for (Method method : declaredMethods) {
			if (method.getName().equals(name) && method.isAnnotationPresent(RuleVersion.class)) {
				return method;
			}
		}

		return null;
	}

	private static String[] getRuleNames(Class<? extends Recognizer<?, ?>> recognizerClass) {
		try {
			Field ruleNames = recognizerClass.getField("ruleNames");
			return (String[])ruleNames.get(null);
		} catch (NoSuchFieldException ex) {
			LOGGER.log(Level.WARNING, null, ex);
		} catch (SecurityException ex) {
			LOGGER.log(Level.WARNING, null, ex);
		} catch (IllegalArgumentException ex) {
			LOGGER.log(Level.WARNING, null, ex);
		} catch (IllegalAccessException ex) {
			LOGGER.log(Level.WARNING, null, ex);
		}

		return new String[0];
	}

	public static List<Tuple2<RuleDependency, AnnotatedElement>> getDependencies(Class<?> clazz) {
		List<Tuple2<RuleDependency, AnnotatedElement>> result = new ArrayList<Tuple2<RuleDependency, AnnotatedElement>>();
		List<ElementType> supportedTarget = Arrays.asList(RuleDependency.class.getAnnotation(Target.class).value());
		for (ElementType target : supportedTarget) {
			switch (target) {
			case TYPE:
				if (!clazz.isAnnotation()) {
					getElementDependencies(clazz, result);
				}
				break;
			case ANNOTATION_TYPE:
				if (!clazz.isAnnotation()) {
					getElementDependencies(clazz, result);
				}
				break;
			case CONSTRUCTOR:
				for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
					getElementDependencies(ctor, result);
				}
				break;
			case FIELD:
				for (Field field : clazz.getDeclaredFields()) {
					getElementDependencies(field, result);
				}
				break;
			case LOCAL_VARIABLE:
				System.err.println("Runtime rule dependency checking is not supported for local variables.");
				break;
			case METHOD:
				for (Method method : clazz.getDeclaredMethods()) {
					getElementDependencies(method, result);
				}
				break;
			case PACKAGE:
				// package is not a subset of class, so nothing to do here
				break;
			case PARAMETER:
				System.err.println("Runtime rule dependency checking is not supported for parameters.");
				break;
			}
		}

		return result;
	}

	private static void getElementDependencies(AnnotatedElement annotatedElement, List<Tuple2<RuleDependency, AnnotatedElement>> result) {
		RuleDependency dependency = annotatedElement.getAnnotation(RuleDependency.class);
		if (dependency != null) {
			result.add(Tuple.create(dependency, annotatedElement));
		}

		RuleDependencies dependencies = annotatedElement.getAnnotation(RuleDependencies.class);
		if (dependencies != null) {
			for (RuleDependency d : dependencies.value()) {
				if (d != null) {
					result.add(Tuple.create(d, annotatedElement));
				}
			}
		}
	}

	private RuleDependencyChecker() {
	}
}
