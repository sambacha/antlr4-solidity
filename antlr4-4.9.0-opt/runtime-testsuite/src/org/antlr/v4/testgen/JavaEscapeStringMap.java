/*
 * Copyright (c) 2012 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD-3-Clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.testgen;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Sam Harwell
 */
public class JavaEscapeStringMap extends AbstractMap<String, Object> {

	@Override
	public Object get(Object key) {
		if (key instanceof String) {
			String str = (String)key;
			StringBuilder builder = new StringBuilder(str.length());
			for (int i = 0; i < str.length(); i++) {
				switch (str.charAt(i)) {
				case '\\':
					builder.append("\\\\");
					break;

				case '\r':
					// normalize \r\n to just \n
					break;

				case '\n':
					builder.append("\\n");
					break;

				case '"':
					builder.append("\\\"");
					break;

				default:
					builder.append(str.charAt(i));
					break;
				}
			}

			return builder.toString();
		}

		return super.get(key);
	}

	@Override
	public boolean containsKey(Object key) {
		return key instanceof String;
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return Collections.emptySet();
	}

}
