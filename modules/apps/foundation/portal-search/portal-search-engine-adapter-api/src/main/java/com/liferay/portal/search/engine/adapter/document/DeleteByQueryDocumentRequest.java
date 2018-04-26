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

package com.liferay.portal.search.engine.adapter.document;

import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.search.engine.adapter.DocumentRequest;
import com.liferay.portal.search.engine.adapter.DocumentRequestExecutor;

/**
 * @author Michael C. Han
 */
public class DeleteByQueryDocumentRequest implements DocumentRequest {

	public DeleteByQueryDocumentRequest(String indexName, Query query) {
		_indexName = indexName;
		_query = query;
	}

	@Override
	public <T> T accept(
		DocumentRequestExecutor<T> documentRequestExecutor) {
		return documentRequestExecutor.executeDocumentRequest(this);
	}

	@Override
	public String getIndexName() {
		return _indexName;
	}

	public Query getQuery() {
		return _query;
	}

	public void setRefresh(boolean refresh) {
		_refresh = refresh;
	}

	public void setWaitForCompletion(boolean waitForCompletion) {
		_waitForCompletion = waitForCompletion;
	}

	public boolean isRefresh() {
		return _refresh;
	}

	public boolean isWaitForCompletion() {
		return _waitForCompletion;
	}

	private boolean _refresh;
	private boolean _waitForCompletion;
	private final String _indexName;
	private final Query _query;

}
