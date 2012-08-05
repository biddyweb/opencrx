/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: TwitterUtils.java,v 1.4 2010/10/15 09:39:04 wfro Exp $
 * Description: TwitterUtils
 * Revision:    $Revision: 1.4 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/10/15 09:39:04 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2007, CRIXP Corp., Switzerland
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 * 
 * * Neither the name of CRIXP Corp. nor the names of the contributors
 * to openCRX may be used to endorse or promote products derived
 * from this software without specific prior written permission
 * 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * ------------------
 * 
 * This product includes software developed by the Apache Software
 * Foundation (http://www.apache.org/).
 * 
 * This product includes software developed by contributors to
 * openMDX (http://www.openmdx.org/)
 */
package org.opencrx.application.twitter;


import javax.jdo.PersistenceManager;

import org.opencrx.kernel.admin1.jmi1.ComponentConfiguration;
import org.opencrx.kernel.home1.jmi1.TwitterAccount;
import org.opencrx.kernel.utils.ComponentConfigHelper;
import org.openmdx.base.exception.ServiceException;

public abstract class TwitterUtils {

    //-----------------------------------------------------------------------
	public static ComponentConfiguration getComponentConfiguration(
		String providerName,
		String segmentName,
		PersistenceManager rootPm
	) throws ServiceException {
	    return ComponentConfigHelper.getComponentConfiguration(
			TwitterUtils.CONFIGURATION_ID, 
			providerName, 
			rootPm, 
			true, // autoCreate 
			new String[][]{
				{providerName + "." + TwitterUtils.PROPERTY_OAUTH_CONSUMER_KEY, ""},
				{providerName + "." + TwitterUtils.PROPERTY_OAUTH_CONSUMER_SECRET, ""},    				
			}
		);
	}
	
    //-----------------------------------------------------------------------
	public static String getConsumerKey(
		TwitterAccount twitterAccount,
		ComponentConfiguration configuration
	) {
		String providerName = twitterAccount.refGetPath().get(2);
		String segmentName = twitterAccount.refGetPath().get(4);
		String consumerKey = twitterAccount.getConsumerKey();
		if(consumerKey == null || consumerKey.length() == 0) {
			// Get from component configuration: segment-specific consumerKey
			org.opencrx.kernel.base.jmi1.StringProperty p = ComponentConfigHelper.getComponentConfigProperty(
				providerName + "." + segmentName + "." + PROPERTY_OAUTH_CONSUMER_KEY, 
				configuration
			);
			if(p == null) {
				// Get from component-configuration: provider-specific consumerKey
				p = ComponentConfigHelper.getComponentConfigProperty(
					providerName + "." + PROPERTY_OAUTH_CONSUMER_KEY, 
					configuration
				);
			}
			if(p != null) {
				consumerKey = p.getStringValue();
			}
		}
		return consumerKey;
	}
	
    //-----------------------------------------------------------------------
	public static String getConsumerSecret(
		TwitterAccount twitterAccount,
		ComponentConfiguration configuration
	) {
		String providerName = twitterAccount.refGetPath().get(2);
		String segmentName = twitterAccount.refGetPath().get(4);
		String consumerKey = twitterAccount.getConsumerKey();
		String consumerSecret = twitterAccount.getConsumerSecret();
		// Ignore secret if key is empty
		if(consumerKey == null || consumerKey.length() == 0) {
			// Get from component configuration: segment-specific consumerKey
			org.opencrx.kernel.base.jmi1.StringProperty p = ComponentConfigHelper.getComponentConfigProperty(
				providerName + "." + segmentName + "." + PROPERTY_OAUTH_CONSUMER_SECRET, 
				configuration
			);
			if(p == null) {
				// Get from component-configuration: provider-specific consumerKey
				p = ComponentConfigHelper.getComponentConfigProperty(
					providerName + "." + PROPERTY_OAUTH_CONSUMER_SECRET, 
					configuration
				);
			}
			if(p != null) {
				consumerSecret = p.getStringValue();
			}
		}
		return consumerSecret;		
	}
	
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    public static final String CONFIGURATION_ID = "Twitter";
    public static final int MESSAGE_SIZE = 140;
    public static final String PROPERTY_OAUTH_CONSUMER_KEY = "OAuth.ConsumerKey";
    public static final String PROPERTY_OAUTH_CONSUMER_SECRET = "OAuth.ConsumerSecret";
	
}
