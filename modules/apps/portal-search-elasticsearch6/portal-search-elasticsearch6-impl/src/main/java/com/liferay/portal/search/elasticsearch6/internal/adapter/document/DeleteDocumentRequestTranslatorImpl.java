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

package com.liferay.portal.search.elasticsearch6.internal.adapter.document;

import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentResponse;

import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.rest.RestStatus;

/**
 * @author Dylan Rebelak
 */
public class DeleteDocumentRequestTranslatorImpl
	implements DeleteDocumentRequestTranslator {

	@Override
	public DeleteDocumentResponse execute(
		DeleteDocumentRequest deleteDocumentRequest,
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		DeleteRequestBuilder deleteRequestBuilder = createBuilder(
			deleteDocumentRequest, elasticsearchConnectionManager);

		DeleteResponse deleteResponse = deleteRequestBuilder.get();

		RestStatus restStatus = deleteResponse.status();

		return new DeleteDocumentResponse(restStatus.getStatus());
	}

	protected DeleteRequestBuilder createBuilder(
		DeleteDocumentRequest deleteDocumentRequest,
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		Client client = elasticsearchConnectionManager.getClient();

		DeleteRequestBuilder deleteRequestBuilder = client.prepareDelete();

		deleteRequestBuilder.setIndex(deleteDocumentRequest.getIndexName());
		deleteRequestBuilder.setType(deleteDocumentRequest.getType());
		deleteRequestBuilder.setId(deleteDocumentRequest.getUid());

		return deleteRequestBuilder;
	}

}