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
package com.hybris.yprofile.profiletagaddon.interceptors.beforeview;

import com.hybris.yprofile.services.ProfileConfigurationService;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.storefront.data.JavaScriptVariableData;
import de.hybris.platform.addonsupport.config.javascript.BeforeViewJsPropsHandlerAdaptee;
import de.hybris.platform.addonsupport.config.javascript.JavaScriptVariableDataFactory;
import de.hybris.platform.servicelayer.session.impl.DefaultSessionTokenService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import java.util.Optional;

import static java.util.Optional.ofNullable;


public class ProfileTagBeforeViewHandler extends BeforeViewJsPropsHandlerAdaptee
{
	@Resource
	private ProfileConfigurationService profileConfigurationService;

	@Resource
	private BaseSiteService baseSiteService;

	@Override
	public String beforeViewJsProps(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
			final String viewName)
	{

		attachCustomJSVariablesToModel(model, request);
		return viewName;
	}

	/**
	 * Provides a combination of setting attributes as JS variables with base.js.properties or traditional way of setting
	 * the value in model object.
	 *
	 * @param model
	 * @param request
	 */
	protected void attachCustomJSVariablesToModel(final ModelMap model, final HttpServletRequest request) // NOSONAR
	{
		if (model != null)
		{
			final Map<String, String> jsPropsMap = new HashMap<>();
			final List<JavaScriptVariableData> jsPropList = JavaScriptVariableDataFactory.createFromMap(jsPropsMap);
			Map<String, List<JavaScriptVariableData>> jsVariables = (Map<String, List<JavaScriptVariableData>>) model
					.get(detectJsModelName());

			if (jsVariables == null)
			{
				jsVariables = new HashMap<String, List<JavaScriptVariableData>>();
				model.addAttribute(detectJsModelName(), jsVariables);
			}

			//Loads the key-values from base.js.properties and available as JS variables in the storefront
			List<JavaScriptVariableData> jsVariablesList = jsVariables.get(getMessageSource().getAddOnName());
			if (jsVariablesList != null && !jsVariablesList.isEmpty())
			{
				jsVariablesList.addAll(jsPropList);
			}
			else
			{
				jsVariablesList = jsPropList;
			}
			jsVariables.put(getMessageSource().getAddOnName(), jsVariablesList);

			model.addAttribute("SITE_ID", getSiteId());
			model.addAttribute("PROFILETAG_URL", profileConfigurationService.getYaaSProfileTagUrl());
			model.addAttribute("PROFILETAG_CONFIG_URL", profileConfigurationService.getYaaSProfileTagConfigUrl());
			model.addAttribute("TENANT", profileConfigurationService.getYaaSTenant(getSiteId()));
			model.addAttribute("CLIENT_ID", profileConfigurationService.getYaaSClientId(getSiteId()));
		}
	}


	protected String getTenant() {
		return profileConfigurationService.getYaaSTenant(getSiteId());
	}

	protected String getSiteId(){
		return getCurrentBaseSiteModel().isPresent() ? getCurrentBaseSiteModel().get().getUid() : StringUtils.EMPTY;
	}

	protected Optional<BaseSiteModel> getCurrentBaseSiteModel() {
		return ofNullable(baseSiteService.getCurrentBaseSite());
	}


}