/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Notifications.java,v 1.13 2009/05/20 23:39:02 wfro Exp $
 * Description: UserHomes
 * Revision:    $Revision: 1.13 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/05/20 23:39:02 $
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

package org.opencrx.kernel.backend;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.application.dataprovider.cci.DataproviderOperations;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.ContextCapable;
import org.openmdx.base.naming.Path;
import org.openmdx.portal.servlet.Action;

public class Notifications extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new Notifications());
	}
	
    //-------------------------------------------------------------------------
	public static Notifications getInstance(
	) throws ServiceException {
		return getInstance(Notifications.class);
	}

	//-------------------------------------------------------------------------
	protected Notifications(
	) {
		
	}
	
    //-----------------------------------------------------------------------
    public String getNotificationText(
        PersistenceManager pm,
        ContextCapable target,
        Path wfProcessInstanceIdentity,
        UserHome userHome,
        Map<String,Object> params
    ) throws ServiceException {
    	SimpleDateFormat dateFormat = Utils.getLocalizedDateFormat(userHome);
        String text = "#ERR";
        String webAccessUrl = UserHomes.getInstance().getWebAccessUrl(userHome);
        Path targetIdentity = target == null ?
            null :
            target.refGetPath();
        Action selectTargetAction = targetIdentity == null ?
            null :
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
        Action selectWfProcessInstanceAction = wfProcessInstanceIdentity == null ? 
        	null : 
        	new Action(
                Action.EVENT_SELECT_OBJECT, 
                new Action.Parameter[]{
                    new Action.Parameter(Action.PARAMETER_OBJECTXRI, wfProcessInstanceIdentity.toXri())    
                },
                "",
                true
            );
        Action selectTriggeredBySubscriptionAction =  params.get("triggeredBySubscription") == null ? 
        	null : 
        	new Action(
                Action.EVENT_SELECT_OBJECT,
                new Action.Parameter[]{
                    new Action.Parameter(Action.PARAMETER_OBJECTXRI, ((Path)params.get("triggeredBySubscription")).toXri())
                },
                "",
                true
            );
        // Alert specific text
        if(target instanceof org.opencrx.kernel.home1.jmi1.Alert) {
            org.opencrx.kernel.home1.jmi1.Alert alert = (org.opencrx.kernel.home1.jmi1.Alert)target;
            ContextCapable referencedObj = null;
            try {
            	referencedObj = alert.getReference();
            } catch(Exception e) {}
            if(
                (referencedObj != null) && 
                !(referencedObj instanceof org.opencrx.kernel.home1.jmi1.UserHome)
            ) {
                text = this.getNotificationText(
                    pm,
                    referencedObj,
                    wfProcessInstanceIdentity,
                    userHome,
                    params
               );
            }
            else {
                text = "Alert:\n";
                if(selectTargetAction != null) {
                    text += "=======================================================================\n";
                    text += "\"" + webAccessUrl + "?event=" + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectTargetAction.getParameter() + "\"\n";
                    text += "=======================================================================\n";
                }
                text += "Name:\n";
                text += (alert.getName() == null ? "N/A" : alert.getName()) + "\n\n";
                text += "Description:\n";
                text += (alert.getDescription() == null ? "N/A" : alert.getDescription()) + "\n\n"; 
                text += "=======================================================================\n";
            }
        }
        // Activity specific text
        else if(
            (target instanceof org.opencrx.kernel.activity1.jmi1.Activity) ||
            (target instanceof org.opencrx.kernel.activity1.jmi1.ActivityFollowUp)
        ) {
            org.opencrx.kernel.activity1.jmi1.Activity1Package activityPkg = Utils.getActivityPackage(pm);
            org.opencrx.kernel.activity1.jmi1.Activity activity = target instanceof org.opencrx.kernel.activity1.jmi1.Activity ? 
            	(org.opencrx.kernel.activity1.jmi1.Activity)target : 
            	(org.opencrx.kernel.activity1.jmi1.Activity)pm.getObjectById(new Path(target.refMofId()).getParent().getParent());
            org.opencrx.kernel.account1.jmi1.Contact reportingContact = activity.getReportingContact();
            org.opencrx.kernel.account1.jmi1.Account reportingAccount = activity.getReportingAccount();
            org.opencrx.kernel.account1.jmi1.Contact assignedTo = activity.getAssignedTo();                
            org.opencrx.kernel.activity1.jmi1.ActivityProcessState activityState = activity.getProcessState();                
            org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition lastTransition = activity.getLastTransition();                
            text = "";
            if(selectTargetAction != null) {
                text += "=======================================================================\n";
                text += "\"" + webAccessUrl + "?event=" + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectTargetAction.getParameter() + "\"\n";
                text += "=======================================================================\n";
            }
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
                } 
                catch(Exception e) {}
                try {
                    text += "Reproducibility:            " + incident.getReproducibility() + "\n";
                } 
                catch(Exception e) {}
                try {
                    text += "Severity:                   " + incident.getSeverity() + "\n";
                } 
                catch(Exception e) {}
            }
            text += "Priority:                   " + activity.getPriority() + "\n";
            text += "Status:                     " + (activityState == null ? "N/A" : activityState.getName()) + "\n";
            text += "Last transition:            " + (lastTransition == null ? "N/A" : lastTransition.getName()) + "\n";
            text += "=======================================================================\n";
            text += "Scheduled start:            " + (activity.getScheduledStart() == null ? "N/A" : dateFormat.format(activity.getScheduledStart())) + "\n";
            text += "Scheduled end:              " + (activity.getScheduledEnd() == null ? "N/A" : dateFormat.format(activity.getScheduledEnd())) + "\n";
            text += "Due by:                     " + (activity.getDueBy() == null ? "N/A" : dateFormat.format(activity.getDueBy())) + "\n";
            text += "Actual start:               " + (activity.getActualStart() == null ? "N/A" : dateFormat.format(activity.getActualStart())) + "\n";
            text += "Actual end:                 " + (activity.getActualEnd() == null ? "N/A" : dateFormat.format(activity.getActualEnd())) + "\n";
            text += "=======================================================================\n";
            text += "Date Submitted:             " + activity.getCreatedAt() + "\n";
            text += "Last Modified:              " + activity.getModifiedAt() + "\n";
            text += "=======================================================================\n";
            String activityName = activity.getName();
            String activityDescription = activity.getDescription();
            String activityDetailedDescription = activity.getDetailedDescription();
            String messageBody = activity instanceof org.opencrx.kernel.activity1.jmi1.EMail ? 
            	((org.opencrx.kernel.activity1.jmi1.EMail)activity).getMessageBody() : 
            	null;
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
                if(selectTargetAction != null) {
                    text += "Object Invoked:  " + ("\"" + webAccessUrl + "?event=" + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectTargetAction.getParameter() + "\"\n");
                }
                text += "Workflow:        " + (selectWfProcessInstanceAction == null ? "N/A" : "\"" + webAccessUrl + "?event=" +  + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectWfProcessInstanceAction.getParameter()) + "\"\n";
                text += "Subscription:    " + (selectTriggeredBySubscriptionAction == null ? "N/A" : "\"" + webAccessUrl + "?event=" +  + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectTriggeredBySubscriptionAction.getParameter()) + "\"\n";
                text += "=======================================================================\n";
        }
        return text;
    }
            
    //-----------------------------------------------------------------------
    public String getNotificationSubject(
        PersistenceManager pm,
        ContextCapable target,
        UserHome userHome,  
        Map<String,Object> params
    ) throws ServiceException {
        Path userHomeIdentity = userHome.refGetPath();
        String title = null;
        String subscriptionId = (params.get("triggeredBySubscription") == null ? 
            "N/A" : 
            ((Path)params.get("triggeredBySubscription")).getBase());
        String sendMailSubjectPrefix = userHome.getSendMailSubjectPrefix() == null ? 
            "[" + userHome.refGetPath().get(2) + ":" + userHome.refGetPath().get(4) + "]" : 
            userHome.getSendMailSubjectPrefix();
        String webAccessUrl = UserHomes.getInstance().getWebAccessUrl(userHome);
        if(
            (target != null) && 
            ((params.get("confidential") == null) || !((Boolean)params.get("confidential")).booleanValue())
        ) {
            try {
                if(target instanceof org.opencrx.kernel.activity1.jmi1.EMail) {
                    org.opencrx.kernel.activity1.jmi1.EMail emailActivity = 
                        (org.opencrx.kernel.activity1.jmi1.EMail)target;
                    title = emailActivity.getMessageSubject();
                }
                else if(target instanceof org.opencrx.kernel.home1.jmi1.Alert) {
                    org.opencrx.kernel.home1.jmi1.Alert alert = 
                        (org.opencrx.kernel.home1.jmi1.Alert)target;
                    if(
                        (alert.getReference() != null) &&
                        !(alert.getReference() instanceof org.opencrx.kernel.home1.jmi1.UserHome)
                    ) {
                        title = this.getNotificationSubject(
                            pm, 
                            alert.getReference(), 
                            userHome, 
                            params
                        );
                    }
                    else {
                        title = alert.getName();
                    }
                }
                else {
                    if(title == null) {
                        try {
                            if(target.refGetValue("name") != null) {
                                title = sendMailSubjectPrefix + ": " + target.refGetValue("name");
                            }
                        } 
                        catch(Exception e) {}
                    }
                    if(title == null) {
                        try {
                            if(target.refGetValue("title") != null) {
                                title = sendMailSubjectPrefix + ": " + target.refGetValue("title");
                            }
                        } 
                        catch(Exception e) {}
                    }
                    if(title == null) {
                        try {
                            if(target.refGetValue("fullName") != null) {
                                title = sendMailSubjectPrefix + ": " + target.refGetValue("fullName");
                            }
                        } 
                        catch(Exception e) {}
                    }
                }
            }
            catch(Exception e) {}
        }
        if(title == null) {
            title = 
                sendMailSubjectPrefix + ": " + 
                "from=" + userHomeIdentity.get(2) + "/" + userHomeIdentity.get(4)+ "/" + userHomeIdentity.get(6) + "; " + 
                "trigger=" + subscriptionId + "; " +
                "access=" + webAccessUrl;                
        }   
        return title;
    }
        
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final short CHANGE_PASSWORD_OK = 0;
    public static final short MISSING_NEW_PASSWORD = 1;
    public static final short MISSING_NEW_PASSWORD_VERIFICATION = 2;
    public static final short PASSWORD_VERIFICATION_MISMATCH = 3;
    public static final short CAN_NOT_RETRIEVE_REQUESTED_PRINCIPAL = 4;
    public static final short CAN_NOT_CHANGE_PASSWORD = 5;
    public static final short MISSING_OLD_PASSWORD = 6;

}

//--- End of File -----------------------------------------------------------
