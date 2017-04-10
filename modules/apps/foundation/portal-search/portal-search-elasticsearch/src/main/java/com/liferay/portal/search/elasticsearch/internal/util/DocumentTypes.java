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

package com.liferay.portal.search.elasticsearch.internal.util;

import com.liferay.portal.kernel.search.suggest.SuggestionConstants;

/**
 * @author Michael C. Han
 */
public class DocumentTypes {

	public static final String KEYWORD_QUERY = SuggestionConstants.TYPE_QUERY_SUGGESTION;

	public static final String LIFERAY = "LiferayDocumentType";

	public static final String SPELL_CHECK = SuggestionConstants.TYPE_SPELL_CHECKER;

}