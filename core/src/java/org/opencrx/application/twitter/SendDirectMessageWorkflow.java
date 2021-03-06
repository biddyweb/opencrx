/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: SendDirectMessageWorkflow
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2014, CRIXP Corp., Switzerland
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

import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.admin1.jmi1.ComponentConfiguration;
import org.opencrx.kernel.backend.Notifications;
import org.opencrx.kernel.backend.Workflows;
import org.opencrx.kernel.base.jmi1.WorkflowTarget;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.home1.cci2.TwitterAccountQuery;
import org.opencrx.kernel.home1.jmi1.TwitterAccount;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.home1.jmi1.WfProcessInstance;
import org.opencrx.kernel.utils.TinyUrlUtils;
import org.opencrx.kernel.utils.WorkflowHelper;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.ContextCapable;
import org.openmdx.kernel.log.SysLog;

import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * SendDirectMessageWorkflow
 *
 */
public class SendDirectMessageWorkflow extends Workflows.SynchronousWorkflow {

	/* (non-Javadoc)
	 * @see org.opencrx.kernel.backend.Workflows.SynchronousWorkflow#execute(org.opencrx.kernel.base.jmi1.WorkflowTarget, org.openmdx.base.jmi1.ContextCapable, org.openmdx.base.jmi1.ContextCapable, org.opencrx.kernel.home1.jmi1.WfProcessInstance)
	 */
	@Override
    public void execute(
        WorkflowTarget wfTarget,
        ContextCapable targetObject,
        WfProcessInstance wfProcessInstance
    ) throws ServiceException {        
    	PersistenceManager pm = JDOHelper.getPersistenceManager(wfProcessInstance);
    	PersistenceManager rootPm = pm.getPersistenceManagerFactory().getPersistenceManager(
            SecurityKeys.ROOT_PRINCIPAL,
            null
        );
        try {             
            UserHome userHome = (UserHome)pm.getObjectById(wfProcessInstance.refGetPath().getParent().getParent());
            String providerName = userHome.refGetPath().get(2);
            String segmentName = userHome.refGetPath().get(4);
            ComponentConfiguration configuration = TwitterUtils.getComponentConfiguration(
            	providerName, 
            	segmentName, 
            	rootPm
            );
            Map<String,Object> params = WorkflowHelper.getWorkflowParameters(wfProcessInstance);
            // Find default twitter account
            TwitterAccountQuery twitterAccountQuery = (TwitterAccountQuery)pm.newQuery(TwitterAccount.class);
            twitterAccountQuery.thereExistsIsDefault().isTrue();
            twitterAccountQuery.thereExistsIsActive().isTrue();
            List<TwitterAccount> twitterAccounts = userHome.getEMailAccount(twitterAccountQuery);     
            TwitterAccount twitterAccount = twitterAccounts.isEmpty() ?
            	null :
            		twitterAccounts.iterator().next();            
            String subject = null;
            String text = null;
            if(twitterAccount == null) {
                subject = "ERROR: " + Notifications.getInstance().getNotificationSubject(
                    pm,
                    targetObject,
                    wfProcessInstance.refGetPath(),
                    userHome,
                    params,
                    true // useSendMailSubjectPrefix
                );
                text = "ERROR: direct message not sent. No default twitter account\n";                 
            } else {
                // Send direct message
                subject = Notifications.getInstance().getNotificationSubject(
                    pm,
                    targetObject,
                    wfProcessInstance.refGetPath(),
                    userHome,
                    params,
                    true //this.useSendMailSubjectPrefix
                );
                String tinyUrl = TinyUrlUtils.getTinyUrl(
                	Notifications.getInstance().getAccessUrl(
                		targetObject.refGetPath(), 
                		userHome
                	)
                );
                if(tinyUrl != null) {
                    if(subject.length() > TwitterUtils.MESSAGE_SIZE - 30 - 4) {
                    	subject = subject.substring(0, TwitterUtils.MESSAGE_SIZE - 30 - 4) + "...";                    	
                    }
	                subject += " " + tinyUrl;
                } else {
                    if(subject.length() > TwitterUtils.MESSAGE_SIZE - 3) {
                    	subject = subject.substring(0, TwitterUtils.MESSAGE_SIZE - 3) + "...";
                    }
                }
            	TwitterFactory twitterFactory = new TwitterFactory(
            		
            	);
            	AccessToken accessToken = new AccessToken(
            		twitterAccount.getAccessToken(),
            		twitterAccount.getAccessTokenSecret()
            	);
            	Twitter twitter = twitterFactory.getInstance();
            	twitter.setOAuthConsumer(
            		TwitterUtils.getConsumerKey(twitterAccount, configuration), 
            		TwitterUtils.getConsumerSecret(twitterAccount, configuration)
            	);
            	twitter.setOAuthAccessToken(accessToken);
                SysLog.detail("Send direct message message");
            	DirectMessage message = twitter.sendDirectMessage(
            		twitterAccount.getName(),
            		subject
            	);
                SysLog.detail("Done", message);
            }
            WorkflowHelper.createLogEntry(
                wfProcessInstance,
                subject,
                text
            );
        } catch(Exception e) {
        	SysLog.warning("Can not send direct message");
            ServiceException e0 = new ServiceException(e);
            SysLog.detail(e0.getMessage(), e0.getCause());
            WorkflowHelper.createLogEntry(
                wfProcessInstance,
                "Can not send direct message: Exception",
                e.getMessage()
            );
            throw e0;        	
        } finally {
        	if(rootPm != null) {
        		rootPm.close();
        	}
        }
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    
}

//--- End of File -----------------------------------------------------------
