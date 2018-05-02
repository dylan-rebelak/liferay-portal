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

package com.liferay.portal.search.elasticsearch6.internal.adapter.cluster;

import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.engine.adapter.cluster.StateClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.StateClusterResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.cluster.ClusterState;

/**
 * @author Dylan Rebelak
 */
public class StateClusterRequestTranslatorImpl implements StateClusterRequestTranslator{
	@Override
	public StateClusterResponse execute(
		StateClusterRequest stateClusterRequest,
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		ClusterStateRequestBuilder clusterStateRequestBuilder =
			createBuilder(stateClusterRequest, elasticsearchConnectionManager);

		ClusterStateResponse clusterStateResponse =
			clusterStateRequestBuilder.get();

		ClusterState clusterState = clusterStateResponse.getState();

		return new StateClusterResponse(clusterState.toString());
	}

	protected ClusterStateRequestBuilder createBuilder(
		StateClusterRequest stateClusterRequest,
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		ClusterAdminClient clusterAdminClient =
			elasticsearchConnectionManager.getClusterAdminClient();

		ClusterStateRequestBuilder clusterStateRequestBuilder =
			clusterAdminClient.prepareState();

		clusterStateRequestBuilder.setIndices(
			stateClusterRequest.getIndexName());

		return clusterStateRequestBuilder;
	}
}
