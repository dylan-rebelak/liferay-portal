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

package com.liferay.portal.uad.aggregator.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;

import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.uad.constants.PortalUADConstants;
import com.liferay.portal.uad.test.PortletItemUADEntityTestHelper;

import com.liferay.user.associated.data.aggregator.UADAggregator;
import com.liferay.user.associated.data.test.util.BaseUADAggregatorTestCase;

import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;

import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@RunWith(Arquillian.class)
public class PortletItemUADAggregatorTest extends BaseUADAggregatorTestCase {
	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule = new LiferayIntegrationTestRule();

	@Override
	protected BaseModel<?> addBaseModel(long userId) throws Exception {
		PortletItem portletItem = _portletItemUADEntityTestHelper.addPortletItem(userId);

		_portletItems.add(portletItem);

		return portletItem;
	}

	@Override
	protected UADAggregator getUADAggregator() {
		return _uadAggregator;
	}

	@After
	public void tearDown() throws Exception {
		_portletItemUADEntityTestHelper.cleanUpDependencies(_portletItems);
	}

	@DeleteAfterTestRun
	private final List<PortletItem> _portletItems = new ArrayList<PortletItem>();
	@Inject
	private PortletItemUADEntityTestHelper _portletItemUADEntityTestHelper;
	@Inject(filter = "model.class.name=" +
	PortalUADConstants.CLASS_NAME_PORTLET_ITEM)
	private UADAggregator _uadAggregator;
}