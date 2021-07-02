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
package de.techouts.pocchroma.core.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.techouts.pocchroma.core.constants.PocchromaCoreConstants;
import de.techouts.pocchroma.core.setup.CoreSystemSetup;


/**
 * Do not use, please use {@link CoreSystemSetup} instead.
 * 
 */
public class PocchromaCoreManager extends GeneratedPocchromaCoreManager
{
	public static final PocchromaCoreManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (PocchromaCoreManager) em.getExtension(PocchromaCoreConstants.EXTENSIONNAME);
	}
}
