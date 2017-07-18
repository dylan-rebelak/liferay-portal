/*
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

package com.liferay.portal.search.elasticsearch.internal.connection;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.search.elasticsearch.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch.settings.ElasticsearchConnectionSettings;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import java.util.Map;

/**
 * @author Dylan Rebelak
 */
@Component(
	configurationPid = "com.liferay.portal.search.elasticsearch.configuration.ElasticsearchConfiguration",
	immediate = true, service = ElasticsearchConnectionSettings.class
)
public class ElasticsearchConnectionSettingsImpl
	implements ElasticsearchConnectionSettings {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		elasticsearchConfiguration = ConfigurableUtil.createConfigurable(
			ElasticsearchConfiguration.class, properties);
	}

	@Override
	public String getClusterName() {
		return elasticsearchConfiguration.clusterName();
	}

	@Override
	public String[] getTransportAddresses() {
		return elasticsearchConfiguration.transportAddresses();
	}

	@Override
	public boolean getClientTransportSniff() {
		return elasticsearchConfiguration.clientTransportSniff();
	}

	@Override
	public boolean getClientTransportIgnoreClusterName() {
		return elasticsearchConfiguration.clientTransportIgnoreClusterName();
	}

	@Override
	public String getClientTransportNodesSamplerInterval() {
		return elasticsearchConfiguration.clientTransportNodesSamplerInterval();
	}

	@Override
	public boolean getHttpEnabled() {
		return elasticsearchConfiguration.httpEnabled();
	}

	protected volatile ElasticsearchConfiguration elasticsearchConfiguration;
}
