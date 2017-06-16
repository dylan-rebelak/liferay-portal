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

package com.liferay.document.library.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.service.test.ServiceTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.documentlibrary.util.DLFileEntryIndexer;

import java.io.File;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.Assert;
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
public class DLFileEntryIndexerLocalizedContentTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceTestUtil.setUser(TestPropsValues.getUser());

		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());

		_indexer = new DLFileEntryIndexer();
	}

	@Test
	public void testJapaneseContent() throws Exception {
		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), null, LocaleUtil.JAPAN);

		String firstDoc = "ContentSearch.txt";

		addFileEntry(firstDoc);

		List<String> contentStrings = new ArrayList<>(
			Collections.singletonList("content_ja_JP"));

		String word1 = "新規";
		String word2 = "作成";
		String prefix1 = "新";
		String prefix2 = "作";

		Stream<String> searchTerms = Stream.of(word1, word2, prefix1, prefix2);

		searchTerms.forEach(
			searchTerm -> {
				Document document = _search(searchTerm, LocaleUtil.JAPAN);

				assertLocalization(contentStrings, document);
			});
	}

	@Test
	public void testJapaneseContentFullWordOnly() throws Exception {
		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), null, LocaleUtil.JAPAN);

		String firstDoc = "Japanese1.txt";
		String secondDoc = "Japanese2.txt";
		String thirdDoc = "Japanese3.txt";

		addFileEntry(firstDoc);
		addFileEntry(secondDoc);
		addFileEntry(thirdDoc);

		List<String> contentStrings = new ArrayList<>(
			Collections.singletonList("content_ja_JP"));

		String word1 = "新規";
		String word2 = "作成";

		Stream<String> searchTerms = Stream.of(word1, word2);

		searchTerms.forEach(
			searchTerm -> {
				Document document = _search(searchTerm, LocaleUtil.JAPAN);

				assertLocalization(contentStrings, document);
			});
	}

	@Test
	public void testSiteLocale() throws Exception {
		Group testGroup = GroupTestUtil.addGroup();

		String firstDoc = "LocaleJa.txt";
		String secondDoc = "LocaleEn.txt";

		List<String> contentStringsJa = new ArrayList<>(
			Collections.singletonList("content_ja_JP"));
		List<String> contentStringsEn = new ArrayList<>(
			Collections.singletonList("content_en_US"));

		try {
			GroupTestUtil.updateDisplaySettings(
				_group.getGroupId(), null, LocaleUtil.JAPAN);
			GroupTestUtil.updateDisplaySettings(
				testGroup.getGroupId(), null, LocaleUtil.US);

			addFileEntry(firstDoc, _group.getGroupId());
			addFileEntry(secondDoc, testGroup.getGroupId());

			String searchTerm = "新規";

			Document documentJa = _search(
				searchTerm, LocaleUtil.JAPAN, _group.getGroupId());

			assertLocalization(contentStringsJa, documentJa);

			searchTerm = "Locale Test";

			Document documentEn = _search(
				searchTerm, LocaleUtil.ENGLISH, testGroup.getGroupId());

			assertLocalization(contentStringsEn, documentEn);
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(testGroup);
		}
	}

	protected FileEntry addFileEntry(String fileName) throws Exception {
		return addFileEntry(fileName, _group.getGroupId());
	}

	protected FileEntry addFileEntry(String fileName, Long groupId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		File file = null;
		FileEntry fileEntry;

		try (InputStream inputStream =
				DLFileEntrySearchTest.class.getResourceAsStream(
					"dependencies/" + fileName)) {

			String mimeType = MimeTypesUtil.getContentType(file, fileName);

			file = FileUtil.createTempFile(inputStream);

			fileEntry = DLAppLocalServiceUtil.addFileEntry(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName, mimeType,
				fileName, StringPool.BLANK, StringPool.BLANK, file,
				serviceContext);
		}
		finally {
			FileUtil.delete(file);
		}

		return fileEntry;
	}

	protected void assertLocalization(
		List<String> contentStrings, Document document) {

		List<String> fields = _getFieldValues("content", document);

		Assert.assertEquals(contentStrings.toString(), fields.toString());
	}

	private static List<String> _getFieldValues(
		String prefix, Document document) {

		List<String> fields = new ArrayList<>();

		for (String field : document.getFields().keySet()) {
			if (field.contains(prefix)) {
				fields.add(field);
			}
		}

		return fields;
	}

	private SearchContext _getSearchContext(
			String searchTerm, Locale locale, Long groupId)
		throws Exception {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			groupId);

		searchContext.setKeywords(searchTerm);

		searchContext.setLocale(locale);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(StringPool.STAR);

		return searchContext;
	}

	private Document _getSingleDocument(String searchTerm, Hits hits) {
		List<Document> documents = hits.toList();

		if (documents.size() == 1) {
			return documents.get(0);
		}

		throw new AssertionError(searchTerm + "->" + documents);
	}

	private Document _search(String searchTerm, Locale locale) {
		return _search(searchTerm, locale, _group.getGroupId());
	}

	private Document _search(String searchTerm, Locale locale, Long groupId) {
		try {
			SearchContext searchContext = _getSearchContext(
				searchTerm, locale, groupId);

			Hits hits = _indexer.search(searchContext);

			return _getSingleDocument(searchTerm, hits);
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	private Indexer<?> _indexer;

}