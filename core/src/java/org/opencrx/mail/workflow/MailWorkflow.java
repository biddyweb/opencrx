/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: MailWorkflow.java,v 1.24 2008/07/02 09:04:02 wfro Exp $
 * Description: Mail workflow
 * Revision:    $Revision: 1.24 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/07/02 09:04:02 $
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
package org.opencrx.mail.workflow;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jmi.reflect.RefObject;
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

import org.opencrx.kernel.base.jmi1.BooleanProperty;
import org.opencrx.kernel.base.jmi1.IntegerProperty;
import org.opencrx.kernel.base.jmi1.Property;
import org.opencrx.kernel.base.jmi1.StringProperty;
import org.opencrx.kernel.base.jmi1.UriProperty;
import org.opencrx.kernel.home1.jmi1.EmailAccount;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.home1.jmi1.WfActionLogEntry;
import org.opencrx.kernel.home1.jmi1.WfProcessInstance;
import org.opencrx.kernel.utils.Utils;
import org.opencrx.kernel.workflow.ASynchWorkflow_1_0;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderOperations;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.portal.servlet.Action;
import org.openmdx.portal.servlet.WebKeys;

public abstract class MailWorkflow 
    implements ASynchWorkflow_1_0 {
    
    //-----------------------------------------------------------------------
    protected String getWebAccessUrl(
        UserHome userHome
    ) {
        Path userHomeIdentity = userHome.refGetPath();
        return userHome.getWebAccessUrl() == null
            ? "http://localhost/opencrx-core-" + userHomeIdentity.get(2) + "/" + WebKeys.SERVLET_NAME
            : userHome.getWebAccessUrl() + "/" + WebKeys.SERVLET_NAME;        
    }
    
    //-----------------------------------------------------------------------
    protected String getText(
        Message message,
        PersistenceManager pm,
        Path targetIdentity,
        RefObject targetObject,
        Path wfProcessInstanceIdentity,
        UserHome userHome,
        Map params
    ) throws ServiceException {
        String text = "#ERR";
        try {
            String webAccessUrl = this.getWebAccessUrl(userHome);
            Action selectTargetAction = 
                new Action(
                    Action.EVENT_SELECT_OBJECT, 
                    new Action.Parameter[]{
                        new Action.Parameter(Action.PARAMETER_OBJECTXRI, targetIdentity.toXri())
                    },
                    "",
                    true
                );
            String subscriptionId = 
                (params.get("triggeredBySubscription") == null ? "N/A" : ((Path)params.get("triggeredBySubscription")).getBase());            
            Action selectWfProcessInstanceAction = wfProcessInstanceIdentity == null
                ? null
                : new Action(
                    Action.EVENT_SELECT_OBJECT, 
                    new Action.Parameter[]{
                        new Action.Parameter(Action.PARAMETER_OBJECTXRI, wfProcessInstanceIdentity.toXri())    
                    },
                    "",
                    true
                );
            Action selectTriggeredBySubscriptionAction =  params.get("triggeredBySubscription") == null
                ? null
                : new Action(
                    Action.EVENT_SELECT_OBJECT,
                    new Action.Parameter[]{
                        new Action.Parameter(Action.PARAMETER_OBJECTXRI, ((Path)params.get("triggeredBySubscription")).toXri())
                    },
                    "",
                    true
                );
            // Alert specific text
            if(targetObject instanceof org.opencrx.kernel.home1.jmi1.Alert) {
                org.opencrx.kernel.home1.jmi1.Alert alert = (org.opencrx.kernel.home1.jmi1.Alert)targetObject;
                text = "Alert:\n";
                text += "=======================================================================\n";
                text += "\"" + webAccessUrl + "?event=" + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectTargetAction.getParameter() + "\"\n";
                text += "=======================================================================\n";
                RefObject_1_0 referencedObject = null;
                try {
                    referencedObject = alert.getReference();
                } catch(Exception e) {}
                if(referencedObject != null) {
                    text += "\nAdditional information:\n";
                    text += this.getText(
                        message, 
                        pm, 
                        referencedObject.refGetPath(),
                        referencedObject, 
                        null, 
                        userHome, 
                        params
                    );
                }
                else {
                    text += "Event:           " + (params.get("triggeredByEventType") == null ? "N/A" : DataproviderOperations.toString(((Number)params.get("triggeredByEventType")).intValue())) + "\n";
                    text += "Subscription Id: " + subscriptionId + "\n";
                    text += "Workflow:        " + (selectWfProcessInstanceAction == null ? "N/A" : "\"" + webAccessUrl + "?event=" +  + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectWfProcessInstanceAction.getParameter() + "\"") + "\n";
                    text += "Subscription:    " + (selectTriggeredBySubscriptionAction == null ? "N/A" : "\"" + webAccessUrl + "?event=" +  + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectTriggeredBySubscriptionAction.getParameter() + "\"") + "\n";
                    text += "=======================================================================\n";                    
                }    
            }
            // Activity specific text
            else if(
                (targetObject instanceof org.opencrx.kernel.activity1.jmi1.Activity) ||
                (targetObject instanceof org.opencrx.kernel.activity1.jmi1.ActivityFollowUp)
            ) {
                org.opencrx.kernel.activity1.jmi1.Activity1Package activityPkg = Utils.getActivityPackage(pm);
                org.opencrx.kernel.activity1.jmi1.Activity activity = targetObject instanceof org.opencrx.kernel.activity1.jmi1.Activity
                    ? (org.opencrx.kernel.activity1.jmi1.Activity)targetObject
                    : (org.opencrx.kernel.activity1.jmi1.Activity)pm.getObjectById(new Path(targetObject.refMofId()).getParent().getParent());
                org.opencrx.kernel.account1.jmi1.Contact reportingContact = activity.getReportingContact();
                org.opencrx.kernel.account1.jmi1.Account reportingAccount = activity.getReportingAccount();
                org.opencrx.kernel.account1.jmi1.Contact assignedTo = activity.getAssignedTo();                
                org.opencrx.kernel.activity1.jmi1.ActivityProcessState activityState = activity.getProcessState();                
                org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition lastTransition = activity.getLastTransition();                
                text = "";
                text += "=======================================================================\n";
                text += "\"" + webAccessUrl + "?event=" + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectTargetAction.getParameter() + "\"\n";
                text += "=======================================================================\n";
                text += "Reporting Contact:          " + (reportingContact == null ? "N/A" : reportingContact.getFullName()) + "\n";
                text += "Reporting Account:          " + (reportingAccount == null ? "N/A" : reportingAccount.getFullName()) + "\n";
                text += "Handler:                    " + (assignedTo == null ? "N/A" : assignedTo.getFullName()) + "\n";
                text += "=======================================================================\n";
                int ii = 0;
                Collection<org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment> assignedGroups = activity.getAssignedGroup();
                for(org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment assignedGroup: assignedGroups) {
                    org.opencrx.kernel.activity1.jmi1.ActivityGroup group = assignedGroup.getActivityGroup();
                    if(group != null) {
                        if(ii == 0) {
                            text += "Activity Group:             " + group.getName() + "\n";
                        }
                        else {
                            text += "                            " + group.getName() + "\n";                        
                        }
                    }
                }
                text += "Activity#:                  " + activity.getActivityNumber() + "\n";
                if(activity instanceof org.opencrx.kernel.activity1.jmi1.Incident) {
                    org.opencrx.kernel.activity1.jmi1.Incident incident = (org.opencrx.kernel.activity1.jmi1.Incident)activity;
                    try {
                        text += "Category:                   " + incident.getCategory() + "\n";
                    } catch(Exception e) {}
                    try {
                        text += "Reproducibility:            " + incident.getReproducibility() + "\n";
                    } catch(Exception e) {}
                    try {
                        text += "Severity:                   " + incident.getSeverity() + "\n";
                    } catch(Exception e) {}
                }
                text += "Priority:                   " + activity.getPriority() + "\n";
                text += "Status:                     " + (activityState == null ? "N/A" : activityState.getName()) + "\n";
                text += "Last transition:            " + (lastTransition == null ? "N/A" : lastTransition.getName()) + "\n";
                text += "=======================================================================\n";
                text += "Date Submitted:             " + activity.getCreatedAt() + "\n";
                text += "Last Modified:              " + activity.getModifiedAt() + "\n";
                text += "=======================================================================\n";
                String activityName = activity.getName();
                String activityDescription = activity.getDescription();
                String activityDetailedDescription = activity.getDetailedDescription();
                String messageBody = activity instanceof org.opencrx.kernel.activity1.jmi1.Email
                    ? ((org.opencrx.kernel.activity1.jmi1.Email)activity).getMessageBody()
                    : null;
                text += "Summary:\n";
                text += (activityName == null ? "N/A" : activityName) + "\n\n";
                text += "Description:\n";
                text += (activityDescription == null ? "N/A" : activityDescription) + "\n\n"; 
                text += "Details:\n";
                text += (activityDetailedDescription == null ? "N/A" : activityDetailedDescription) + "\n\n";
                if(messageBody != null) {
                    text += "Message Body:\n";
                    text += messageBody + "\n\n";                     
                }
                text += "=======================================================================\n";
                text += "\n";
                org.opencrx.kernel.activity1.cci2.ActivityFollowUpQuery filter = activityPkg.createActivityFollowUpQuery();
                filter.orderByCreatedAt().ascending();
                Collection<org.opencrx.kernel.activity1.jmi1.ActivityFollowUp> followUps = activity.getFollowUp(filter);
                for(org.opencrx.kernel.activity1.jmi1.ActivityFollowUp followUp: followUps) {
                    org.opencrx.kernel.account1.jmi1.Contact followUpAssignedTo = followUp.getAssignedTo();
                    org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition followUpTransition = followUp.getTransition();                
                    text += "-----------------------------------------------------------------------\n";
                    text += "Submitted by: " + (followUpAssignedTo == null ? "N/A" : followUpAssignedTo.getFullName()) + "\n";
                    text += "Submitted at: " + followUp.getCreatedAt() + "\n";
                    text += "Transition  : " + (followUpTransition == null ? "N/A" : followUpTransition.getName()) + "\n";
                    text += "Title       : " + ((followUp == null) || (followUp.getTitle() == null) ? "N/A" : followUp.getTitle()) + "\n";
                    text += "-----------------------------------------------------------------------\n";
                    String followUpText = followUp.getText();
                    text += (followUpText == null ? "N/A" : followUpText) + "\n\n\n";
                }
            }
            // Generic text
            else {
                text = "";
                text = 
                    text += "=======================================================================\n";
                    text += "Event:           " + (params.get("triggeredByEventType") == null ? "N/A" : DataproviderOperations.toString(((Number)params.get("triggeredByEventType")).intValue())) + "\n";
                    text += "Subscription Id: " + subscriptionId + "\n";
                    text += "Object Invoked:  " + ("\"" + webAccessUrl + "?event=" + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectTargetAction.getParameter() + "\"\n");
                    text += "Workflow:        " + (selectWfProcessInstanceAction == null ? "N/A" : "\"" + webAccessUrl + "?event=" +  + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectWfProcessInstanceAction.getParameter()) + "\"\n";
                    text += "Subscription:    " + (selectTriggeredBySubscriptionAction == null ? "N/A" : "\"" + webAccessUrl + "?event=" +  + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectTriggeredBySubscriptionAction.getParameter()) + "\"\n";
                    text += "=======================================================================\n";
            }
            message.setText(text);
        }
        catch(MessagingException e) {
            throw new ServiceException(e);
        }
        return text;
    }
    
    //-----------------------------------------------------------------------
    protected String getSubject(
        PersistenceManager pm,
        Path targetIdentity,
        UserHome userHome,  
        Map params
    ) throws ServiceException {
        Path userHomeIdentity = userHome.refGetPath();
        String subject = null;
        String subscriptionId = (params.get("triggeredBySubscription") == null ? "N/A" : ((Path)params.get("triggeredBySubscription")).getBase());
        String sendMailSubjectPrefix = userHome.getSendMailSubjectPrefix() == null
            ? "openCRX SendMail"
            : userHome.getSendMailSubjectPrefix();
        String webAccessUrl = this.getWebAccessUrl(userHome);
        if((params.get("confidential") == null) || !((Boolean)params.get("confidential")).booleanValue()) {
            try {
                RefObject targetObject = (RefObject)pm.getObjectById(targetIdentity);
                if(subject == null) {
                    try {
                        if(targetObject.refGetValue("name") != null) {
                            subject = sendMailSubjectPrefix + ": " + targetObject.refGetValue("name");
                        }
                    } catch(Exception e) {}
                }
                if(subject == null) {
                    try {
                        if(targetObject.refGetValue("title") != null) {
                            subject = sendMailSubjectPrefix + ": " + targetObject.refGetValue("title");
                        }
                    } catch(Exception e) {}
                }
                if(subject == null) {
                    try {
                        if(targetObject.refGetValue("fullName") != null) {
                            subject = sendMailSubjectPrefix + ": " + targetObject.refGetValue("fullName");
                        }
                    } catch(Exception e) {}
                }
            }
            catch(Exception e) {}
        }
        if(subject == null) {
            subject = 
                sendMailSubjectPrefix + ": " + 
                "from=" + userHomeIdentity.get(2) + "/" + userHomeIdentity.get(4)+ "/" + userHomeIdentity.get(6) + "; " + 
                "trigger=" + subscriptionId + "; " +
                "access=" + webAccessUrl;                
        }   
        return subject;
    }
        
    //-----------------------------------------------------------------------
    protected String setContent(
        Message message,
        Session session,
        PersistenceManager pm,
        Path targetIdentity,
        Path wfProcessInstanceIdentity,
        UserHome userHome,
        Map params
    ) throws ServiceException {
        RefObject targetObject = null;
        try {
            targetObject = (RefObject)pm.getObjectById(targetIdentity);
        } catch(Exception e) {}
        String text = null;
        try {
            text = this.getText(
                message,
                pm,
                targetIdentity,
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
        EmailAccount eMailAccount
    ) throws ServiceException {
        Address[] recipients = null;
        try {
            // from
            message.setFrom(
                new InternetAddress(
                    eMailAccount.getReplyEmailAddress() == null
                        ? "noreply@localhost"
                        :  eMailAccount.getReplyEmailAddress()
                )
            );
            // recipients
            recipients = InternetAddress.parse(
                eMailAccount.getEmailAddress() == null
                    ? "noreply@localhost"
                    : eMailAccount.getEmailAddress()
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
        WfProcessInstance wfProcessInstance,
        PersistenceManager pm
    ) throws ServiceException {
        
        Address[] recipients = null;
        Transport transport = null;
        try {             
            Path wfProcessInstanceIdentity = new Path(wfProcessInstance.refMofId());
            
            // Parameters
            Map params = new HashMap();
            for(
                Iterator i = wfProcessInstance.getProperty().iterator();
                i.hasNext();
            ) {
                Property p = (Property)i.next();
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
            
            // Find default email account
            Collection eMailAccounts = userHome.getEmailAccount();
            EmailAccount eMailAccountUser = null;
            for(
                Iterator i = eMailAccounts.iterator();
                i.hasNext();
            ) {
                EmailAccount obj = (EmailAccount)i.next();
                if((obj.isDefault() != null) && obj.isDefault().booleanValue()) {
                   eMailAccountUser = obj;
                   break;
                }
            }
            String subject = null;
            String text = null;
            
            // can not send
            if(eMailAccountUser == null) {
                subject = "ERROR: " + this.getSubject(
                    pm,
                    targetIdentity,
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
                    for(
                        Iterator i = wfProcessInstance.getProcess().getProperty().iterator(); 
                        i.hasNext(); 
                    ) {
                        org.opencrx.kernel.base.jmi1.Property property = (org.opencrx.kernel.base.jmi1.Property)i.next();
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
                            subject = this.getSubject(
                                pm,
                                targetIdentity,
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
