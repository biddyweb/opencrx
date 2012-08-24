/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: Mail workflow
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2008, CRIXP Corp., Switzerland
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
package org.opencrx.application.mail.exporter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.opencrx.kernel.backend.Notifications;
import org.opencrx.kernel.backend.Workflows;
import org.opencrx.kernel.home1.cci2.EMailAccountQuery;
import org.opencrx.kernel.home1.jmi1.EMailAccount;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.home1.jmi1.WfProcessInstance;
import org.opencrx.kernel.utils.WorkflowHelper;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.ContextCapable;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.log.SysLog;

public abstract class MailWorkflow extends Workflows.AsynchronousWorkflow {
    
    /**
     * Set message content.
     * @param message
     * @param session
     * @param pm
     * @param targetIdentity
     * @param wfProcessInstanceIdentity
     * @param userHome
     * @param params
     * @return
     * @throws ServiceException
     */
    protected String setContent(
        Message message,
        Session session,
        PersistenceManager pm,
        Path targetIdentity,
        Path wfProcessInstanceIdentity,
        UserHome userHome,
        Map<String,Object> params
    ) throws ServiceException {
        ContextCapable targetObject = null;
        try {
            targetObject = (ContextCapable)pm.getObjectById(targetIdentity);
        } 
        catch(Exception e) {}
        String text = null;
        try {
            text = Notifications.getInstance().getNotificationText(
                pm,
                targetObject,
                wfProcessInstanceIdentity,
                userHome,
                params
            );
            message.setText(text);
        }
        catch(MessagingException e) {
            throw new ServiceException(e);
        }
        return text;        
    }
    
    //-----------------------------------------------------------------------
    protected Address[] setRecipients(
        Message message,
        PersistenceManager pm,
        Path targetIdentity,
        EMailAccount eMailAccount,
        String defaultReplyEMailAddress
    ) throws ServiceException {
        Address[] recipients = null;
        try {
            // from
            message.setFrom(
                new InternetAddress(
                    eMailAccount.getReplyEMailAddress() == null ? 
                    	defaultReplyEMailAddress == null ?
                    		"noreply@localhost" :
                    			defaultReplyEMailAddress :  
                    				eMailAccount.getReplyEMailAddress()
                )
            );
            // recipients
            recipients = InternetAddress.parse(
                eMailAccount.getName() == null ? 
                	defaultReplyEMailAddress == null ?
                		"noreply@localhost" :
                			defaultReplyEMailAddress : 
                				eMailAccount.getName()
            );
            message.setRecipients(
                Message.RecipientType.TO,
                recipients
            );
        }
        catch(AddressException e) {
            throw new ServiceException(e);
        }
        catch(MessagingException e) {
            throw new ServiceException(e);
        }
        return recipients;
    }
    
    //-----------------------------------------------------------------------
    abstract boolean useSendMailSubjectPrefix(
    );

    /* (non-Javadoc)
     * @see org.opencrx.kernel.backend.Workflows.AsynchronousWorkflow#execute(org.opencrx.kernel.home1.jmi1.WfProcessInstance)
     */
    @Override
    public void execute(
        WfProcessInstance wfProcessInstance
    ) throws ServiceException {        
    	PersistenceManager pm = JDOHelper.getPersistenceManager(wfProcessInstance);
        Address[] recipients = null;
        Transport transport = null;
        try {             
            Path wfProcessInstanceIdentity = new Path(wfProcessInstance.refMofId());            
            Map<String,Object> params = WorkflowHelper.getWorkflowParameters(wfProcessInstance); 
            UserHome userHome = (UserHome)pm.getObjectById(new Path(wfProcessInstance.refMofId()).getParent().getParent());            
            // Target object
            Path targetIdentity = new Path(wfProcessInstance.getTargetObject());            
            ContextCapable target = null;
            try {
                target = (ContextCapable)pm.getObjectById(targetIdentity);
            } catch(Exception e) {}            
            // Find default email account
            EMailAccountQuery emailAccountQuery = (EMailAccountQuery)pm.newQuery(EMailAccount.class);
            emailAccountQuery.thereExistsIsDefault().isTrue();
            emailAccountQuery.thereExistsIsActive().isTrue();
            List<EMailAccount> eMailAccounts = userHome.getEMailAccount(emailAccountQuery);
            EMailAccount eMailAccountUser = eMailAccounts.isEmpty() ?
            	null :
            		eMailAccounts.iterator().next();
            String subject = null;
            String text = null;            
            // Can not send if no email account is configured
            if(eMailAccountUser == null) {
                subject = "ERROR: " + Notifications.getInstance().getNotificationSubject(
                    pm,
                    target,
                    userHome,
                    params,
                    this.useSendMailSubjectPrefix()
                );
                text = "ERROR: email not sent. No default email account\n" + text; 
            }
            // send mail
            else {
                // Try to get mail service name from user's email account
                String mailServiceName = eMailAccountUser.getOutgoingMailServiceName();                
                // Try to get mail service name from workflow configuration
                if(
                    ((mailServiceName == null) || mailServiceName.isEmpty()) &&
                    wfProcessInstance.getProcess() != null
                ) {
                	mailServiceName = (String)params.get(MailWorkflow.OPTION_MAIL_SERVICE_NAME);
                }
                // If not configured take mail/provider/<provider>/segment/<segment> as default
                if(mailServiceName == null) {
                    mailServiceName = "/mail/provider/" + wfProcessInstanceIdentity.get(2) + "/segment/" + wfProcessInstanceIdentity.get(4);
                }
                try {
                    Context initialContext = new InitialContext();
                    Session session = null;
                    try {
                        session = (Session)initialContext.lookup("java:comp/env" + mailServiceName);
                    }
                    catch(Exception e) {    
                    	SysLog.detail("Mail service not found", mailServiceName);
                        // Fallback to mail/provider/<provider>
                        mailServiceName = "/mail/provider/" + wfProcessInstanceIdentity.get(2);
                        SysLog.detail("Fall back to mail service", mailServiceName);
                        session = (Session)initialContext.lookup("java:comp/env" + mailServiceName);
                    }
                    // message
                    Message message = new MimeMessage(session);
                    // date
                    message.setSentDate(
                        new Date()
                    );
                    // header
                    message.setHeader(
                        "X-Mailer", 
                        "openCRX SendMail"
                    );
                    recipients = this.setRecipients(
                        message,
                        pm,
                        targetIdentity,
                        eMailAccountUser,
                        session.getProperty("mail.from")
                    );
                    if(recipients.length > 0) {
                        // subject
                        message.setSubject(
                            subject = Notifications.getInstance().getNotificationSubject(
                                pm,
                                target,
                                userHome,
                                params,
                                this.useSendMailSubjectPrefix()
                            )                
                        );    
                        // content
                        text = this.setContent(
                            message,
                            session,
                            pm,
                            targetIdentity,
                            wfProcessInstanceIdentity,
                            userHome,
                            params
                        );
                        message.saveChanges();
                        SysLog.detail("Send message");
                        transport = session.getTransport();
                        String protocol = transport.getURLName().getProtocol();
                        String port = session.getProperty("mail." + protocol + ".port");
                        transport.connect(
                            session.getProperty("mail." + protocol + ".host"), 
                            port == null ? -1 : Integer.valueOf(port).intValue(),                           
                            session.getProperty("mail." + protocol + ".user"), 
                            session.getProperty("mail." + protocol + ".password")
                        );
                        transport.sendMessage(
                            message,
                            message.getAllRecipients()
                        );           
                        SysLog.detail("Done");
                    }
                    // No recipients. Can not send message
                    else {
                        WorkflowHelper.createLogEntry(
                            wfProcessInstance,
                            "Can not send mail: No recipients",
                            "#recipients must be > 0"
                        );
                    }
                } 
                catch(NamingException e) {
                	SysLog.detail("Can not get mail session", mailServiceName);
                    ServiceException e0 = new ServiceException(e);
                    SysLog.detail(e0.getMessage(), e0.getCause());
                    text = "ERROR: email not sent. Can not get mail session " + mailServiceName + ":\n" + e0.getMessage();
                }                
            }
            WorkflowHelper.createLogEntry(
                wfProcessInstance,
                subject,
                text
            );
        }
        catch(AuthenticationFailedException e) {
        	SysLog.warning("Can not send message to recipients (reason=AuthenticationFailedException)", recipients == null ? null : Arrays.asList(recipients));
            ServiceException e0 = new ServiceException(e);
            SysLog.detail(e0.getMessage(), e0.getCause());
            WorkflowHelper.createLogEntry(
                wfProcessInstance,
                "Can not send mail: AuthenticationFailedException",
                e.getMessage()
            );
            throw e0;
        }
        catch(AddressException e) {
        	SysLog.warning("Can not send message to recipients (reason=AddressException)", recipients == null ? null : Arrays.asList(recipients));
            ServiceException e0 = new ServiceException(e);
            SysLog.detail(e0.getMessage(), e0.getCause());
            WorkflowHelper.createLogEntry(
                wfProcessInstance,
                "Can not send mail: AddressException",
                e.getMessage()
            );
            throw e0;
        }
        catch(MessagingException e) {
        	SysLog.warning("Can not send message to recipients (reason=MessagingException)", recipients == null ? null : Arrays.asList(recipients));
            ServiceException e0 = new ServiceException(e);
            SysLog.detail(e0.getMessage(), e0.getCause());
            WorkflowHelper.createLogEntry(
                wfProcessInstance,
                "Can not send mail: MessagingException",
                e.getMessage()
            );
            throw e0;
        }
        catch(Exception e) {
        	SysLog.warning("Can not send message to recipients (reason=Exception)", recipients == null ? null : Arrays.asList(recipients));
            ServiceException e0 = new ServiceException(e);
            SysLog.detail(e0.getMessage(), e0.getCause());
            WorkflowHelper.createLogEntry(
                wfProcessInstance,
                "Can not send mail: Exception",
                e.getMessage()
            );
            throw e0;        	
        }
        finally {
            if(transport != null) {
                try {
                    transport.close();
                } catch(Exception e) {}
            }
        }
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final String OPTION_MAIL_SERVICE_NAME = "mailServiceName";
        
}


//--- End of File -----------------------------------------------------------
