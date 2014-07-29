/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: SendMessageWorkflow
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
package org.opencrx.application.jabber;

import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.opencrx.kernel.backend.Notifications;
import org.opencrx.kernel.backend.Workflows;
import org.opencrx.kernel.base.jmi1.WorkflowTarget;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.home1.cci2.JabberAccountQuery;
import org.opencrx.kernel.home1.jmi1.JabberAccount;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.home1.jmi1.WfProcessInstance;
import org.opencrx.kernel.utils.TinyUrlUtils;
import org.opencrx.kernel.utils.WorkflowHelper;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.ContextCapable;
import org.openmdx.kernel.log.SysLog;

/**
 * SendMessageWorkflow
 *
 */
public class SendMessageWorkflow extends Workflows.SynchronousWorkflow {

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
        	Map<String,Object> params = WorkflowHelper.getWorkflowParameters(wfProcessInstance);
            UserHome userHome = (UserHome)pm.getObjectById(wfProcessInstance.refGetPath().getParent().getParent());
            // Find default Jabber account
            JabberAccountQuery jabberAccountQuery = (JabberAccountQuery)pm.newQuery(JabberAccount.class);
            jabberAccountQuery.thereExistsIsDefault().isTrue();
            jabberAccountQuery.thereExistsIsActive().isTrue();
            List<JabberAccount> jabberAccounts = userHome.getEMailAccount(jabberAccountQuery);     
            JabberAccount jabberAccount = jabberAccounts.isEmpty() ?
            	null :
            		jabberAccounts.iterator().next();
            String subject = null;
            String text = null;
            if(jabberAccount == null) {
                subject = "ERROR: " + Notifications.getInstance().getNotificationSubject(
                    pm,
                    targetObject,
                    wfProcessInstance.refGetPath(),
                    userHome,
                    params,
                    true // useSendMailSubjectPrefix
                );
                text = "ERROR: direct message not sent. No default jabber account\n";                 
            } else {
                // Send message
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
                    if(subject.length() > MESSAGE_SIZE - 30 - 4) {
                    	subject = subject.substring(0, MESSAGE_SIZE - 30 - 4) + "...";                    	
                    }
	                subject += " " + tinyUrl;
                } else {
                    if(subject.length() > MESSAGE_SIZE - 3) {
                    	subject = subject.substring(0, MESSAGE_SIZE - 3) + "...";
                    }
                }
                // Send message                
                String hostname = jabberAccount.getHostname();
                int port = 5222;
                if(hostname != null && hostname.indexOf(":") > 0) {
                	hostname = hostname.substring(0, hostname.indexOf(":"));
                	port = Integer.valueOf(hostname.substring(hostname.indexOf(":") + 1));                	
                }
                Connection connection = hostname == null || hostname.isEmpty() ?
                	new XMPPConnection(jabberAccount.getServicename()) :
                		new XMPPConnection(new ConnectionConfiguration(hostname, port, jabberAccount.getServicename()));
    		    SASLAuthentication.supportSASLMechanism("PLAIN", 0);                	
		    	connection.connect();
		    	connection.login(jabberAccount.getUsername(), jabberAccount.getPassword());
		    	Chat chat = connection.getChatManager().createChat(
		    		jabberAccount.getName(), 
		    		new MessageListener() {	
		    			public void processMessage(Chat chat, Message message) {}
		    		}
		    	);
		    	chat.sendMessage(subject);
		    	connection.disconnect();
            }
            WorkflowHelper.createLogEntry(
                wfProcessInstance,
                subject,
                text
            );
        } catch(Exception e) {
        	SysLog.warning("Can not send message");
            ServiceException e0 = new ServiceException(e);
            SysLog.detail(e0.getMessage(), e0.getCause());
            WorkflowHelper.createLogEntry(
                wfProcessInstance,
                "Can not send message: Exception",
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
    public static final int MESSAGE_SIZE = 140;
    
}

//--- End of File -----------------------------------------------------------
