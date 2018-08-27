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

package com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.index;

import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexResponse;

import org.elasticsearch.action.admin.indices.get.GetIndexAction;
import org.elasticsearch.action.admin.indices.get.GetIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.client.Client;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = GetIndexIndexRequestExecutor.class)
public class GetIndexIndexRequestExecutorImpl
	implements GetIndexIndexRequestExecutor {

	@Override
	public GetIndexIndexResponse execute(
		GetIndexIndexRequest getIndexIndexRequest) {

		GetIndexRequestBuilder getIndexRequestBuilder =
			createGetIndexRequestBuilder(getIndexIndexRequest);

		GetIndexResponse getIndexResponse = getIndexRequestBuilder.get();

		GetIndexIndexResponse getIndexIndexResponse =
			new GetIndexIndexResponse();
		
		return getIndexIndexResponse;
	}

	protected GetIndexRequestBuilder createGetIndexRequestBuilder(
		GetIndexIndexRequest getIndexIndexRequest) {

		Client client = elasticsearchConnectionManager.getClient();

		GetIndexRequestBuilder getIndexRequestBuilder =
			GetIndexAction.INSTANCE.newRequestBuilder(client);

		getIndexRequestBuilder.setIndices(getIndexIndexRequest.getIndexNames());

		return getIndexRequestBuilder;
	}

	@Reference
	protected ElasticsearchConnectionManager elasticsearchConnectionManager;

}