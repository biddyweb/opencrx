/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: TwitterCreateAccessTokenWizardController
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2013, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.portal.wizard;

import javax.jdo.PersistenceManager;

import org.opencrx.application.twitter.TwitterUtils;
import org.opencrx.kernel.home1.jmi1.TwitterAccount;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.portal.servlet.AbstractWizardController;
import org.openmdx.portal.servlet.ObjectReference;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * TwitterCreateAccessTokenWizardController
 *
 */
public class TwitterCreateAccessTokenWizardController extends AbstractWizardController {

	/**
	 * Constructor.
	 * 
	 */
	public TwitterCreateAccessTokenWizardController(
	) {
		super();
	}
	
	/**
	 * Cancel action.
	 * 
	 */
	public void doCancel(
	) {
		this.setExitAction(
			 new ObjectReference(this.getObject(), this.getApp()).getSelectObjectAction()
	    );
	}
	
	public void doRefresh(
		@RequestParameter(name = "PIN") String pin
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		javax.jdo.PersistenceManager rootPm = pm.getPersistenceManagerFactory().getPersistenceManager(
			org.opencrx.kernel.generic.SecurityKeys.ROOT_PRINCIPAL,
			null
	    );
		if(this.getObject() instanceof TwitterAccount) {
			try {
				this.twitterAccount = (TwitterAccount)this.getObject();		
				org.opencrx.kernel.admin1.jmi1.ComponentConfiguration configuration = 
					org.opencrx.application.twitter.TwitterUtils.getComponentConfiguration(
						this.getProviderName(), 
						this.getSegmentName(), 
						rootPm
					);
			    this.twitter = new TwitterFactory().getInstance();
			    this.twitter.setOAuthConsumer(
			    	TwitterUtils.getConsumerKey(this.twitterAccount, configuration),
			    	TwitterUtils.getConsumerSecret(this.twitterAccount, configuration)    	
			    );
				this.requestToken = this.twitter.getOAuthRequestToken();
			} catch(Exception e) {
				this.message = e.getMessage();
			}
		}
	}

	public void doOK(
		@RequestParameter(name = "PIN") String pin
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		this.doRefresh(pin);
	    RequestToken requestToken = (RequestToken)this.getSession().getAttribute(REQUEST_TOKEN_KEY);
	    if(requestToken != null && this.twitter != null) {
		    AccessToken accessToken = null;
		    try {
		    	accessToken = this.twitter.getOAuthAccessToken(requestToken, pin);
		    	pm.currentTransaction().begin();
		    	this.twitterAccount.setAccessToken(accessToken.getToken());
		    	this.twitterAccount.setAccessTokenSecret(accessToken.getTokenSecret());
		    	pm.currentTransaction().commit();
				this.getSession().removeAttribute(REQUEST_TOKEN_KEY);
		    	this.forward(
		    		"Cancel",
		    		this.getRequest().getParameterMap()
		    	);
			} catch(Exception e) {
		    	new ServiceException(e).log();
		    	this.message = e.getMessage();
			}
	    }
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the requestToken
	 */
	public RequestToken getRequestToken() {
		return requestToken;
	}

	//-----------------------------------------------------------------------
	// Members
	//-----------------------------------------------------------------------
	public static final String REQUEST_TOKEN_KEY = RequestToken.class.getName();
	
	private Twitter twitter;
	private TwitterAccount twitterAccount;
	private RequestToken requestToken;	
	private String message;
}
