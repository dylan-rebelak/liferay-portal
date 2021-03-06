/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.source.formatter.checks;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.checks.util.YMLSourceUtil;

import java.io.IOException;

import java.util.Collections;
import java.util.List;

/**
 * @author Hugo Huijser
 */
public class YMLWhitespaceCheck extends WhitespaceCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		content = StringUtil.replace(
			content, CharPool.TAB, StringPool.FOUR_SPACES);

		content = _formatDefinitions(content, StringPool.BLANK, 0);

		return super.doProcess(fileName, absolutePath, content);
	}

	private String _formatDefinition(
		String definition, String indent, int level,
		boolean hasNestedDefinitions) {

		String expectedIndent = StringPool.BLANK;

		for (int j = 0; j < level; j++) {
			expectedIndent = expectedIndent + StringPool.FOUR_SPACES;
		}

		String newDefinition = definition;

		if (!expectedIndent.equals(indent)) {
			newDefinition = expectedIndent + StringUtil.trimLeading(definition);
		}

		if (hasNestedDefinitions) {
			return newDefinition;
		}

		String[] lines = StringUtil.splitLines(newDefinition);

		if (lines.length <= 1) {
			return newDefinition;
		}

		String previousIndent = StringPool.BLANK;

		for (int j = 1; j < lines.length; j++) {
			String line = lines[j];

			if (Validator.isNull(line)) {
				continue;
			}

			String curIndent = line.replaceAll("^(\\s+).+", "$1");

			if (curIndent.equals(line)) {
				curIndent = StringPool.BLANK;
			}

			if (curIndent.length() > previousIndent.length()) {
				expectedIndent = expectedIndent + StringPool.FOUR_SPACES;
			}
			else if (curIndent.length() < previousIndent.length()) {
				expectedIndent = StringUtil.replaceFirst(
					expectedIndent, StringPool.FOUR_SPACES, StringPool.BLANK);
			}

			if (!curIndent.equals(expectedIndent)) {
				String s = expectedIndent + StringUtil.trimLeading(line);

				newDefinition = StringUtil.replaceFirst(newDefinition, line, s);
			}

			previousIndent = curIndent;
		}

		return newDefinition;
	}

	private String _formatDefinitions(
		String content, String indent, int level) {

		List<String> definitions = YMLSourceUtil.getDefinitions(
			content, indent);

		for (String definition : definitions) {
			String nestedDefinitionIndent =
				YMLSourceUtil.getNestedDefinitionIndent(definition);

			List<String> nestedDefinitions = Collections.emptyList();

			if (!nestedDefinitionIndent.equals(StringPool.BLANK)) {
				nestedDefinitions = YMLSourceUtil.getDefinitions(
					content, nestedDefinitionIndent);

				String newDefinition = _formatDefinitions(
					definition, nestedDefinitionIndent, level + 1);

				if (!newDefinition.equals(definition)) {
					content = StringUtil.replaceFirst(
						content, definition, newDefinition);
				}
			}

			String newDefinition = _formatDefinition(
				definition, nestedDefinitionIndent, level,
				!nestedDefinitions.isEmpty());

			if (!newDefinition.equals(definition)) {
				content = StringUtil.replaceFirst(
					content, definition, newDefinition);
			}
		}

		return content;
	}

}