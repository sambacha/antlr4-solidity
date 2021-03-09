/*
 * Copyright (c) 2012 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD-3-Clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.testgen;

import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;

/**
 *
 * @author sam
 */
public class STGroupModelAdaptor implements ModelAdaptor<STGroup> {

	@Override
	public Object getProperty(Interpreter interp, ST self, STGroup o, Object property, String propertyName) throws STNoSuchPropertyException {
		STGroup group = o;
		if (group.isDictionary(propertyName)) {
			return group.rawGetDictionary(propertyName);
		}

		ST template = group.getInstanceOf(propertyName);
		if (template != null) {
			return template;
		}

		if ("name".equalsIgnoreCase(propertyName)) {
			return group.getName();
		}

		for (STGroup importedGroup : group.getImportedGroups()) {
			Object result = getProperty(interp, self, importedGroup, property, propertyName);
			if (result != null) {
				return result;
			}
		}

		return null;
	}

}
