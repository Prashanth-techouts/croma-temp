/**
 *
 */
package de.techouts.poccromawebservices.v2.controller;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.pages.PageFacade;
import de.hybris.platform.cmsoccaddon.data.CMSPageWsDTO;
import de.hybris.platform.cmsoccaddon.data.ComponentListWsDTO;
import de.hybris.platform.cmsoccaddon.data.ComponentWsDTO;
import de.hybris.platform.cmsoccaddon.data.ContentSlotListWsDTO;
import de.hybris.platform.cmsoccaddon.data.ContentSlotWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;


/**
 * @author D1
 *
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/poccmssolr")
@ApiVersion("v2")
@Api(tags = "PocCmsSolr")
public class POCCmsSolrController extends BaseController
{


	@Resource(name = "cmsPageFacade")
	private PageFacade pageFacade;

	@Resource(name = "cmsDataMapper")
	protected DataMapper dataMapper;

	private static Logger LOGGER = LoggerFactory.getLogger(POCCmsSolrController.class);


	@RequestMapping(value = "/pages", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiBaseSiteIdParam
	public CMSPageWsDTO getPageAndSolrData(
			@ApiParam(value = "page type", allowableValues = "ContentPage, ProductPage, CategoryPage, CatalogPage") @RequestParam(required = true, defaultValue = ContentPageModel._TYPECODE) final String pageType,
			@ApiParam(value = "Page Label or Id") @RequestParam(required = false) final String pageLabelOrId,
			@ApiParam(value = "If pageType is ProductPage, code should be product code; if pageType is CategoryPage, code should be category code; "
					+ "if pageType is CatalogPage, code should be catalog code") @RequestParam(required = false) final String code,
			@ApiParam(value = "Response configuration (list of fields, which should be returned in response)", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = "DEFAULT") final String fields)
			throws CMSItemNotFoundException
	{

		try
		{
			final AbstractPageData pageData = getPageFacade().getPageData(pageType, pageLabelOrId, code);

			final ContentSlotListWsDTO slotList = new ContentSlotListWsDTO();
			slotList.setContentSlot(getContentSlotsWsDTOs(pageData, fields));

			final CMSPageWsDTO pageWsDTO = new CMSPageWsDTO();
			dataMapper.map(pageData, pageWsDTO, fields);
			pageWsDTO.setContentSlots(slotList);

			return pageWsDTO;
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}

	}

	protected List<ContentSlotWsDTO> getContentSlotsWsDTOs(final AbstractPageData pageData, final String fields)
	{
		return pageData.getContentSlots().stream().map(slot -> {
			final ContentSlotWsDTO contentSlotDTO = new ContentSlotWsDTO();
			dataMapper.map(slot, contentSlotDTO, fields);
			final ComponentListWsDTO componentList = new ComponentListWsDTO();
			componentList.setComponent(getComponentWsDTOs(slot.getComponents(), fields));
			contentSlotDTO.setComponents(componentList);
			return contentSlotDTO;
		}).collect(Collectors.toList());
	}

	/**
	 * Method who converts a list of CMSComponentData to ComponentWsDTO.
	 *
	 * @param components
	 *           List of CMSComponentData to convert to ComponentWsDTO.
	 * @param fields
	 *           Response configuration (list of fields, which should be returned in response)
	 * @return List of ContentSlotWsDTO
	 */
	protected List<ComponentWsDTO> getComponentWsDTOs(final List<AbstractCMSComponentData> components, final String fields)
	{
		return components.stream().map(componentData -> {
			final ComponentWsDTO componentDTO = new ComponentWsDTO();
			dataMapper.map(componentData, componentDTO, fields);
			return componentDTO;
		}).collect(Collectors.toList());
	}

	protected PageFacade getPageFacade()
	{
		return pageFacade;
	}

	public void setPageFacade(final PageFacade pageFacade)
	{
		this.pageFacade = pageFacade;
	}




}
