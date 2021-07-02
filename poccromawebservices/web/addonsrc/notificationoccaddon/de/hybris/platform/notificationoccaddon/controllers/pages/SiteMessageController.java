/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.notificationoccaddon.controllers.pages;

import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.notificationfacades.facades.SiteMessageFacade;
import de.hybris.platform.notificationoccaddon.dto.SiteMessageSearchPageWsDTO;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


/**
 * Web Services Controller to expose the site message functionality of the
 * {@link de.hybris.platform.notificationfacades.facades.NotificationPreferenceFacade}.
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/notifications/sitemessages")
@Api(tags = "Site Messages")
public class SiteMessageController
{

	private static final String MAX_PAGE_SIZE_KEY = "webservicescommons.pagination.maxPageSize";

	@Resource(name = "siteMessageFacade")
	private SiteMessageFacade siteMessageFacade;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "webPaginationUtils")
	private WebPaginationUtils webPaginationUtils;

	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation(value = "Get all site messages of current user", notes = "Returns the site messages of current user.")
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "currentPage", value = "the current result page requested", defaultValue = "0", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "the number of results returned per page", defaultValue = "10", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "sort", value = "sorting method applied to the return results", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "needsTotal", value = "whether to calculate the coupon totals", defaultValue = "true", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "baseSiteId", value = "base site identifier", required = true, dataType = "String", paramType = "path"),
			@ApiImplicitParam(name = "lang", value = "specifies the language", required = false, dataType = "String", paramType = "query") })
	public SiteMessageSearchPageWsDTO siteMessages(@RequestParam final Map<String, String> parameters)
	{
		final SearchPageData searchPageData = getWebPaginationUtils().buildSearchPageData(parameters);
		recalculatePageSize(searchPageData);

		return getDataMapper().map(getSiteMessageFacade().getPaginatedSiteMessages(searchPageData),
				SiteMessageSearchPageWsDTO.class);
	}

	protected void recalculatePageSize(final SearchPageData searchPageData)
	{
		int pageSize = searchPageData.getPagination().getPageSize();
		if (pageSize <= 0)
		{
			final int maxPageSize = Config.getInt(MAX_PAGE_SIZE_KEY, 1000);
			pageSize = getWebPaginationUtils().getDefaultPageSize();
			pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;
			searchPageData.getPagination().setPageSize(pageSize);
		}
	}

	protected SiteMessageFacade getSiteMessageFacade()
	{
		return siteMessageFacade;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}
}
