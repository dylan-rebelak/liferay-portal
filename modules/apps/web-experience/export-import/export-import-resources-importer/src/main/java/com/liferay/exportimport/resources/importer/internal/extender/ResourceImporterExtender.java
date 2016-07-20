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

package com.liferay.exportimport.resources.importer.internal.extender;

import com.liferay.exportimport.resources.importer.provider.ResourceImporterBundleProvider;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael C. Han
 */
@Component(immediate = true)
public class ResourceImporterExtender {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTracker = ServiceTrackerFactory.create(
			bundleContext, ResourceImporterBundleProvider.class,
			new ResourceImporterBundleProviderServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations.values()) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();

		_serviceTracker.close();

		_serviceTracker = null;

		_bundleContext = null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourceImporterExtender.class);

	private BundleContext _bundleContext;

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

	private final Map<String, ServiceRegistration<ServletContext>>
		_serviceRegistrations = new ConcurrentHashMap<>();
	private ServiceTracker
		<ResourceImporterBundleProvider, ResourceImporterBundleProvider>
			_serviceTracker;

	private class ResourceImporterBundleProviderServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ResourceImporterBundleProvider, ResourceImporterBundleProvider> {

		@Override
		public ResourceImporterBundleProvider addingService(
			ServiceReference<ResourceImporterBundleProvider> serviceReference) {

			if (!_clusterMasterExecutor.isMaster()) {
				return null;
			}

			ResourceImporterBundleProvider resourceImporterBundleProvider =
				_bundleContext.getService(serviceReference);

			Bundle bundle = resourceImporterBundleProvider.getBundle();

			ServletContext servletContext = new BundleServletContextAdapter(
				bundle);

			ServiceRegistration<ServletContext> serviceRegistration =
				_bundleContext.registerService(
					ServletContext.class, servletContext,
					new HashMapDictionary<String, Object>());

			String bundleSymbolicName = bundle.getSymbolicName();

			_serviceRegistrations.put(bundleSymbolicName, serviceRegistration);

			return null;
		}

		@Override
		public void modifiedService(
			ServiceReference<ResourceImporterBundleProvider> serviceReference,
			ResourceImporterBundleProvider resourceImporterBundleProvider) {
		}

		@Override
		public void removedService(
			ServiceReference<ResourceImporterBundleProvider> serviceReference,
			ResourceImporterBundleProvider resourceImporterBundleProvider) {

			Bundle bundle = resourceImporterBundleProvider.getBundle();

			String bundleSymbolicName = bundle.getSymbolicName();

			ServiceRegistration<ServletContext> serviceRegistration =
				_serviceRegistrations.remove(bundleSymbolicName);

			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		}

	}

}