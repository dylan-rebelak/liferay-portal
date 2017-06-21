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

package com.liferay.portal.search.facet.faceted.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.search.facet.category.AssetCategoryIdsFacetFactory;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dylan Rebelak
 */
@RunWith(Arquillian.class)
@Sync
public class AssetCategoryIdsFacetedSearcherTest
	extends BaseFacetedSearcherTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		Registry registry = RegistryUtil.getRegistry();

		_assetCategoryIdsFacetFactory = registry.getService(
			AssetCategoryIdsFacetFactory.class);

		_group = GroupTestUtil.addGroup();

		_asserVocabulary = AssetVocabularyLocalServiceUtil.addDefaultVocabulary(
			_group.getGroupId());

		addCategory();
	}

	@Test
	public void testSearchByFacet() throws Exception {
		SearchContext searchContext = getSearchContext(
			_assetCategory.getTitleCurrentValue());

		searchContext.setCategoryIds(
			new long[] {_assetCategory.getCategoryId()});
		searchContext.setGroupIds(new long[] {_group.getGroupId()});

		Facet facet = _assetCategoryIdsFacetFactory.newInstance(searchContext);

		searchContext.addFacet(facet);

		addUser(_group, _assetCategory.getCategoryId());

		search(searchContext);

		Map<String, Integer> frequencies = Collections.singletonMap(
			Long.toString(_assetCategory.getCategoryId()), 1);

		assertFrequencies(facet.getFieldName(), searchContext, frequencies);
	}

	protected void addCategory() throws PortalException {
		String title = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		_assetCategory = AssetCategoryLocalServiceUtil.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(), title,
			_asserVocabulary.getVocabularyId(), serviceContext);
	}

	protected void addUser(Group group, long... categoryIds) throws Exception {
		_user = UserTestUtil.addUser(group.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAssetCategoryIds(categoryIds);

		UserTestUtil.updateUser(_user, serviceContext);
	}

	@DeleteAfterTestRun
	private AssetVocabulary _asserVocabulary;

	@DeleteAfterTestRun
	private AssetCategory _assetCategory;

	private AssetCategoryIdsFacetFactory _assetCategoryIdsFacetFactory;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _user;

}