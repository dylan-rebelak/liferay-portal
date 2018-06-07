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

package com.liferay.portal.instances.service.impl;

import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.instances.service.DefaultSiteInitializerCreator;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.proxy.ProxyModeThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.security.permission.DoAsUserThread;
import com.liferay.site.initializer.SiteInitializer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dylan Rebelak
 */
@Component(immediate = true)
public class DefaultSiteInitializerCreatorImpl
	implements DefaultSiteInitializerCreator {

	@Activate
	@Modified
	public void activate() throws Exception {
		Group guestGroup = _groupLocalService.getGroup(
			_portal.getDefaultCompanyId(), "Guest");

		addSite(_portal.getDefaultCompanyId(), guestGroup.getGroupId());
	}

	@Override
	public void addSite(long companyId, long groupId) throws Exception {
		User user = _userLocalService.getUserByScreenName(companyId, "test");

		RunSiteInitializerThread runSiteInitializerThread =
			new RunSiteInitializerThread(user.getUserId(), groupId);

		runSiteInitializerThread.start();
		runSiteInitializerThread.join();
	}

	@Reference(unbind = "-")
	protected void setGroupLocalService(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	@Reference(unbind = "-")
	protected void setPortal(Portal portal) {
		_portal = portal;
	}

	@Reference(target = "(default.site.initializer=true)", unbind = "-")
	protected void setSiteInitializer(SiteInitializer siteInitializer) {
		_siteInitializer = siteInitializer;
	}

	@Reference(unbind = "-")
	protected void setUserLocalService(UserLocalService userLocalService) {
		_userLocalService = userLocalService;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultSiteInitializerCreatorImpl.class);

	private GroupLocalService _groupLocalService;
	private Portal _portal;
	private SiteInitializer _siteInitializer;
	private UserLocalService _userLocalService;

	private class RunSiteInitializerThread extends DoAsUserThread {

		public RunSiteInitializerThread(long userId, long groupId) {
			super(userId);

			_groupId = groupId;
		}

		@Override
		protected void doRun() throws Exception {
			ProxyModeThreadLocal.setForceSync(true);

			try {
				if (StartupHelperUtil.isDBNew()) {
					_siteInitializer.initialize(_groupId);
				}
			}
			catch (Exception e) {
				_log.error("Unable to run initializer", e);
			}
		}

		private final long _groupId;

	}

}