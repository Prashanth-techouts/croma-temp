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
package de.hybris.platform.customerinterestsoccaddon.controllers.customerinterests;

import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestRelationData;
import de.hybris.platform.customerinterestsfacades.productinterest.ProductInterestFacade;
import de.hybris.platform.customerinterestsoccaddon.dto.CustomerInterestsSearchPageWsDTO;
import de.hybris.platform.customerinterestsoccaddon.validation.CustomerInterestsValidator;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import de.hybris.platform.util.Config;



/**
 * Controller for customer interests.
 */
@Controller
@RequestMapping("/{baseSiteId}/my-account")
@Api(tags = "Customer Interests")
public class CustomerInterestsController
{
	private static final String DEFAULT_FIELD_SET = "DEFAULT";
	private static final String MAX_PAGE_SIZE_KEY = "webservicescommons.pagination.maxPageSize";

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "productInterestFacade")
	private ProductInterestFacade productInterestFacade;

	@Resource(name = "webPaginationUtils")
	private WebPaginationUtils webPaginationUtils;

	@Resource(name = "customerInterestsValidator")
	private CustomerInterestsValidator customerInterestsValidator;

	@RequestMapping(value = "/productinterests", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@ApiOperation(value = "Removes all product interests", notes = "Removes all product interests by product code.")
	@ApiBaseSiteIdParam
	public void removeAllProductInterests(
			@ApiParam(value = "product identifier", required = true) @RequestParam(required = true) final String productCode,
			final HttpServletRequest request, final HttpServletResponse response, final Model model)
	{
		customerInterestsValidator.checkIfProductExist(productCode);
		customerInterestsValidator.checkIfProductInterestsExist(productCode);
		getProductInterestFacade().removeAllProductInterests(productCode);
	}


	@ResponseBody
	@RequestMapping(value = "/productinterests", method = RequestMethod.GET)
	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@ApiOperation(value="Gets product interests",notes="Gets all product interests of current customer.")
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "needsTotal", value = "the flag for indicating if total number of results is needed or not", allowableValues = "true,false", required = false, dataType = "string", paramType = "query"),
	@ApiImplicitParam(name="pageSize",value="the number of results returned per page",required=false,dataType="string",paramType="query"),
	@ApiImplicitParam(name="currentPage",value="the current result page requested",required=false,dataType="string",paramType="query"),
	@ApiImplicitParam(name="sort",value="the sorting method applied to the return results",required=false,dataType="string",paramType="query"),
	@ApiImplicitParam(name="lang",value="specifies the language",required=false,dataType="String",paramType="query"),
	@ApiImplicitParam(name="curr",value="specifies the currency",required=false,dataType="String",paramType="query"),
	@ApiImplicitParam(name="baseSiteId",value="Base site identifier",required=true,dataType="String",paramType="path")}
	)
	public CustomerInterestsSearchPageWsDTO getCustomerInterests(@RequestParam(required = false) final Map<String, String> params)
	{

		final SearchPageData<Object> searchPageData = webPaginationUtils.buildSearchPageData(params);
		recalculatePageSize(searchPageData);
		customerInterestsValidator.checkIfPageSizeCorrect(searchPageData.getPagination().getPageSize());
		final SearchPageData<ProductInterestRelationData> paginatedData = productInterestFacade
				.getPaginatedProductInterestsByCustomer(searchPageData);
		return dataMapper.map(paginatedData, CustomerInterestsSearchPageWsDTO.class, DEFAULT_FIELD_SET);

	}

	protected void recalculatePageSize(final SearchPageData searchPageData)
	{
		int pageSize = searchPageData.getPagination().getPageSize();
		if (pageSize <= 0)
		{
			final int maxPageSize = Config.getInt(MAX_PAGE_SIZE_KEY, 1000);
			pageSize = webPaginationUtils.getDefaultPageSize();
			pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;
			searchPageData.getPagination().setPageSize(pageSize);
		}
	}

	protected ProductInterestFacade getProductInterestFacade()
	{
		return productInterestFacade;
	}
}
