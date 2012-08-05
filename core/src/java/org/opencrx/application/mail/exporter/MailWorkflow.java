/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: MailWorkflow.java,v 1.10 2009/05/14 09:01:57 wfro Exp $
 * Description: Mail workflow
 * Revision:    $Revision: 1.10 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/05/14 09:01:57 $
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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
import org.opencrx.kernel.base.jmi1.BooleanProperty;
import org.opencrx.kernel.base.jmi1.IntegerProperty;
import org.opencrx.kernel.base.jmi1.Property;
import org.opencrx.kernel.base.jmi1.StringProperty;
import org.opencrx.kernel.base.jmi1.UriProperty;
import org.opencrx.kernel.home1.jmi1.EMailAccount;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.home1.jmi1.WfActionLogEntry;
import org.opencrx.kernel.home1.jmi1.WfProcessInstance;
import org.opencrx.kernel.utils.Utils;
import org.opencrx.kernel.workflow.ASynchWorkflow_1_0;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.ContextCapable;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.id.UUIDs;

public abstract class MailWorkflow 
    implements ASynchWorkflow_1_0 {
    
    //-----------------------------------------------------------------------
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
        EMailAccount eMailAccount
    ) throws ServiceException {
        Address[] recipients = null;
        try {
            // from
            message.setFrom(
                new InternetAddress(
                    eMailAccount.getReplyEMailAddress() == null
                        ? "noreply@localhost"
                        :  eMailAccount.getReplyEMailAddress()
                )
            );
            // recipients
            recipients = InternetAddress.parse(
                eMailAccount.getEMailAddress() == null
                    ? "noreply@localhost"
                    : eMailAccount.getEMailAddress()
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
    private void createLogEntry(        
        WfProcessInstance wfProcessInstance,
        String name,
        String description,
        PersistenceManager pm
    ) throws ServiceException  {
        if(wfProcessInstance == null) return;
        org.opencrx.kernel.home1.jmi1.Home1Package home1Pkg = Utils.getHomePackage(pm);
        WfActionLogEntry logEntry = home1Pkg.getWfActionLogEntry().createWfActionLogEntry();
        try {
            pm.currentTransaction().begin();
            wfProcessInstance.addActionLog(
                false,
                UUIDs.getGenerator().next().toString(),
                logEntry
            );
            logEntry.setName(name);
            logEntry.setDescription(description);
            pm.currentTransaction().commit();
        }
        catch(Exception e) {
            new ServiceException(e).log();
            try {
                pm.currentTransaction().rollback();                
            } catch(Exception e0) {}
        }
    }
    
    //-----------------------------------------------------------------------
    public void execute(
        WfProcessInstance wfProcessInstance
    ) throws ServiceException {        
    	PersistenceManager pm = JDOHelper.getPersistenceManager(wfProcessInstance);
        Address[] recipients = null;
        Transport transport = null;
        try {             
            Path wfProcessInstanceIdentity = new Path(wfProcessInstance.refMofId());
            
            // Parameters
            Map<String,Object> params = new HashMap<String,Object>();
            Collection<Property> properties = wfProcessInstance.getProperty();
            for(Property p: properties) {
                if(p instanceof StringProperty) {
                    params.put(
                        p.getName(),
                        ((StringProperty)p).getStringValue()
                    );                    
                }
                else if(p instanceof IntegerProperty) {
                    params.put(
                        p.getName(),
                        new Integer(((IntegerProperty)p).getIntegerValue())
                    );                    
                }
                else if(p instanceof UriProperty) {
                    params.put(
                        p.getName(),
                        new Path(((UriProperty)p).getUriValue())
                    );                                        
                }                    
                else if(p instanceof BooleanProperty) {
                    params.put(
                        p.getName(),
                        new Boolean(((BooleanProperty)p).isBooleanValue())
                    );                                        
                }                    
            }
            
            // User homes
            UserHome userHome = (UserHome)pm.getObjectById(new Path(wfProcessInstance.refMofId()).getParent().getParent());
            
            // Target object
            Path targetIdentity = new Path(wfProcessInstance.getTargetObject());            
            ContextCapable target = null;
            try {
                target = (ContextCapable)pm.getObjectById(targetIdentity);
            } catch(Exception e) {}
            
            // Find default email account
            Collection<EMailAccount> eMailAccounts = userHome.getEMailAccount();
            EMailAccount eMailAccountUser = null;
            for(EMailAccount obj: eMailAccounts) {
                if((obj.isDefault() != null) && obj.isDefault().booleanValue()) {
                   eMailAccountUser = obj;
                   break;
                }
            }
            String subject = null;
            String text = null;
            
            // can not send
            if(eMailAccountUser == null) {
                subject = "ERROR: " + Notifications.getInstance().getNotificationSubject(
                    pm,
                    target,
                    userHome,
                    params
                );
                text = "ERROR: email not sent. No default email account\n" + text; 
            }
            // send mail
            else {
                // Try to get mail service name from user's email account
                String mailServiceName = eMailAccountUser.getOutgoingMailServiceName();                
                // Try to get mail service name from workflow configuration
                if(
                    ((mailServiceName == null) || (mailServiceName.length() == 0)) &&
                    wfProcessInstance.getProcess() != null
                ) {
                	properties = wfProcessInstance.getProcess().getProperty();
                    for(Property property: properties) {
                        if((MailWorkflow.OPTION_MAIL_SERVICE_NAME).equals(property.getName())) {
                            mailServiceName = ((org.opencrx.kernel.base.jmi1.StringProperty)property).getStringValue();
                            break;
                        }
                    }
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
                        AppLog.detail("Mail service not found", mailServiceName);
                        // Fallback to mail/provider/<provider>
                        mailServiceName = "/mail/provider/" + wfProcessInstanceIdentity.get(2);
                        AppLog.detail("Fall back to mail service", mailServiceName);
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
                        eMailAccountUser
                    );
                    if(recipients.length > 0) {
                        // subject
                        message.setSubject(
                            subject = Notifications.getInstance().getNotificationSubject(
                                pm,
                                target,
                                userHome,
                                params
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
                        AppLog.detail("Send message");
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
                        AppLog.detail("Done");
                    }
                    // No recipients. Can not send message
                    else {
                        this.createLogEntry(
                            wfProcessInstance,
                            "Can not send mail: No recipients",
                            "#recipients must be > 0",
                            pm
                        );
                    }
                } 
                catch(NamingException e) {
                    AppLog.detail("Can not get mail session", mailServiceName);
                    ServiceException e0 = new ServiceException(e);
                    AppLog.detail(e0.getMessage(), e0.getCause());
                    text = "ERROR: email not sent. Can not get mail session " + mailServiceName + ":\n" + e0.getMessage();
                }                
            }
            this.createLogEntry(
                wfProcessInstance,
                subject,
                text,
                pm
            );
        }        
        catch(AuthenticationFailedException e) {
            AppLog.warning("Can not send message to recipients (reason=AuthenticationFailedException)", Arrays.asList(recipients));
            ServiceException e0 = new ServiceException(e);
            AppLog.detail(e0.getMessage(), e0.getCause());
            this.createLogEntry(
                wfProcessInstance,
                "Can not send mail: AuthenticationFailedException",
                e.getMessage(),
                pm
            );
            throw e0;
        }
        catch(AddressException e) {
            AppLog.warning("Can not send message to recipients (reason=AddressException)", Arrays.asList(recipients));
            ServiceException e0 = new ServiceException(e);
            AppLog.detail(e0.getMessage(), e0.getCause());
            this.createLogEntry(
                wfProcessInstance,
                "Can not send mail: AddressException",
                e.getMessage(),
                pm
            );
            throw e0;
        }
        catch(MessagingException e) {
            AppLog.warning("Can not send message to recipients (reason=MessagingException)", Arrays.asList(recipients));
            ServiceException e0 = new ServiceException(e);
            AppLog.detail(e0.getMessage(), e0.getCause());
            this.createLogEntry(
                wfProcessInstance,
                "Can not send mail: MessagingException",
                e.getMessage(),
                pm
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
