/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Workflows.java,v 1.34 2010/11/28 15:43:35 wfro Exp $
 * Description: Workflows
 * Revision:    $Revision: 1.34 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/11/28 15:43:35 $
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.application.mail.exporter.ExportMailWorkflow;
import org.opencrx.application.mail.exporter.SendMailNotificationWorkflow;
import org.opencrx.application.mail.exporter.SendMailWorkflow;
import org.opencrx.application.twitter.SendDirectMessageWorkflow;
import org.opencrx.kernel.base.jmi1.BooleanProperty;
import org.opencrx.kernel.base.jmi1.DecimalProperty;
import org.opencrx.kernel.base.jmi1.IntegerProperty;
import org.opencrx.kernel.base.jmi1.Property;
import org.opencrx.kernel.base.jmi1.StringProperty;
import org.opencrx.kernel.base.jmi1.UriProperty;
import org.opencrx.kernel.base.jmi1.WorkflowTarget;
import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.home1.jmi1.WfActionLogEntry;
import org.opencrx.kernel.home1.jmi1.WfProcessInstance;
import org.opencrx.kernel.utils.Utils;
import org.opencrx.kernel.workflow.PrintConsole;
import org.opencrx.kernel.workflow.SendAlert;
import org.opencrx.kernel.workflow1.jmi1.WfProcess;
import org.opencrx.kernel.workflow1.jmi1.Workflow1Package;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasicObject;
import org.openmdx.base.jmi1.ContextCapable;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;
import org.openmdx.kernel.loading.Classes;
import org.openmdx.kernel.log.SysLog;

public class Workflows extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new Workflows());
	}
	
    //-------------------------------------------------------------------------
	public static Workflows getInstance(
	) throws ServiceException {
		return getInstance(Workflows.class);
	}

	//-------------------------------------------------------------------------
	protected Workflows(
	) {
		
	}
	
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.workflow1.jmi1.Segment getWorkflowSegment(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        return (org.opencrx.kernel.workflow1.jmi1.Segment)pm.getObjectById(
            new Path("xri://@openmdx*org.opencrx.kernel.workflow1/provider/" + providerName + "/segment/" + segmentName)
        );
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.workflow1.jmi1.Topic findTopic(
        String name,
        org.opencrx.kernel.workflow1.jmi1.Segment segment,
        javax.jdo.PersistenceManager pm
    ) {
        org.opencrx.kernel.workflow1.jmi1.Workflow1Package workflowPkg = org.opencrx.kernel.utils.Utils.getWorkflowPackage(pm);
        org.opencrx.kernel.workflow1.cci2.TopicQuery topicQuery = workflowPkg.createTopicQuery();
        topicQuery.name().equalTo(name);
        List<org.opencrx.kernel.workflow1.jmi1.Topic> topics = segment.getTopic(topicQuery);
        return topics.isEmpty()
            ? null
            : topics.iterator().next();
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.workflow1.jmi1.WfProcess findWfProcess(
        String name,
        org.opencrx.kernel.workflow1.jmi1.Segment segment,
        javax.jdo.PersistenceManager pm
    ) {
        org.opencrx.kernel.workflow1.jmi1.Workflow1Package workflowPkg = org.opencrx.kernel.utils.Utils.getWorkflowPackage(pm);
        org.opencrx.kernel.workflow1.cci2.WfProcessQuery wfProcessQuery = workflowPkg.createWfProcessQuery();
        wfProcessQuery.name().equalTo(name);
        List<org.opencrx.kernel.workflow1.jmi1.WfProcess> wfProcesses = segment.getWfProcess(wfProcessQuery);
        return wfProcesses.isEmpty()
            ? null
            : wfProcesses.iterator().next();
    }
        
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.workflow1.jmi1.Topic initTopic(
        PersistenceManager pm,
        Workflow1Package workflowPackage,
        org.opencrx.kernel.workflow1.jmi1.Segment workflowSegment,
        String id,
        String name,
        String description,
        String topicPathPattern,
        WfProcess[] actions
    ) {
        org.opencrx.kernel.workflow1.jmi1.Topic topic = null;
        try {
            topic = workflowSegment.getTopic(id);
        } 
        catch(Exception e) {}
        if(topic == null) {
            pm.currentTransaction().begin();
            topic = workflowPackage.getTopic().createTopic();
            topic.setName(name);
            topic.setDescription(description);
            topic.setTopicPathPattern(topicPathPattern);
            topic.getPerformAction().addAll(
                Arrays.asList(actions)
            );
            topic.getOwningGroup().addAll(
                workflowSegment.getOwningGroup()
            );
            workflowSegment.addTopic(
                false,                     
                id,
                topic
            );
            pm.currentTransaction().commit();
        }         
        return topic;
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.workflow1.jmi1.WfProcess initWorkflow(
        PersistenceManager pm,
        Workflow1Package workflowPackage,
        org.opencrx.kernel.workflow1.jmi1.Segment workflowSegment,
        String id,
        String name,
        String description,
        Boolean isSynchronous,
        org.opencrx.kernel.base.jmi1.Property[] properties 
    ) {
        org.opencrx.kernel.workflow1.jmi1.WfProcess wfProcess = null;
        try {
            wfProcess = (org.opencrx.kernel.workflow1.jmi1.WfProcess)workflowSegment.getWfProcess(id);
        }
        catch(Exception e) {}
        if(wfProcess == null) {
            // Add process
            pm.currentTransaction().begin();
            wfProcess = workflowPackage.getWfProcess().createWfProcess();
            wfProcess.setName(name);
            wfProcess.setDescription(description);
            wfProcess.setSynchronous(isSynchronous);
            wfProcess.setPriority((short)0);
            wfProcess.getOwningGroup().addAll(
                workflowSegment.getOwningGroup()
            );
            workflowSegment.addWfProcess(
                false,                     
                id,
                wfProcess
            );
            pm.currentTransaction().commit();
            // Add properties
            if(properties != null) {
                pm.currentTransaction().begin();
                UUIDGenerator uuids = UUIDs.getGenerator();
                for(int i = 0; i < properties.length; i++) {
                    wfProcess.addProperty(
                        false,                             
                        uuids.next().toString(),
                        properties[i]
                    );
                }
                pm.currentTransaction().commit();
            }
        }         
        return wfProcess;
    }
    
    //-----------------------------------------------------------------------
    public void initWorkflows(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) throws ServiceException {
        Workflow1Package workflowPackage = Utils.getWorkflowPackage(pm);
        org.opencrx.kernel.workflow1.jmi1.Segment workflowSegment = this.getWorkflowSegment(
            pm, 
            providerName, 
            segmentName
        );
        // ExportMailWorkflow 
        this.initWorkflow(
            pm,
            workflowPackage,
            workflowSegment,
            WORKFLOW_EXPORT_MAIL,
            ExportMailWorkflow.class.getName(),
            "Export mails",
            Boolean.FALSE,
            null
        );        
        // SendMailWorkflow
        this.initWorkflow(
            pm,
            workflowPackage,
            workflowSegment,
            WORKFLOW_SEND_MAIL,
            SendMailWorkflow.class.getName(),
            "Send mails",
            Boolean.FALSE,
            null
        );        
        // SendMailNotificationWorkflow
        WfProcess sendMailNotificationWorkflow = this.initWorkflow(
            pm,
            workflowPackage,
            workflowSegment,
            WORKFLOW_SEND_MAIL_NOTIFICATION,
            SendMailNotificationWorkflow.class.getName(),
            "Send mail notifications",
            Boolean.FALSE,
            null
        );        
        // SendAlert
        WfProcess sendAlertWorkflow = this.initWorkflow(
            pm,
            workflowPackage,
            workflowSegment,
            WORKFLOW_SEND_ALERT,
            org.opencrx.kernel.workflow.SendAlert.class.getName(),
            "Send alert",
            Boolean.TRUE,
            null
        );        
        // PrintConsole
        this.initWorkflow(
            pm,
            workflowPackage,
            workflowSegment,
            WORKFLOW_PRINT_CONSOLE,
            org.opencrx.kernel.workflow.PrintConsole.class.getName(),
            "Print to console",
            Boolean.TRUE,
            null
        );
        // SendDirectMessage
        WfProcess sendDirectMessageWorkflow = this.initWorkflow(
            pm,
            workflowPackage,
            workflowSegment,
            WORKFLOW_SEND_DIRECT_MESSAGE_TWITTER,
            org.opencrx.application.twitter.SendDirectMessageWorkflow.class.getName(),
            "Send direct message to Twitter",
            Boolean.TRUE,
            null
        );
        WfProcess[] sendAlertActions = new WfProcess[]{
            sendAlertWorkflow
        };
        WfProcess[] sendMailNotificationsActions = new WfProcess[]{
            sendMailNotificationWorkflow
        };
        WfProcess[] sendDirectMessageToTwitterActions = new WfProcess[]{
            sendDirectMessageWorkflow
        };
        this.initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "AccountModifications",            
            TOPIC_NAME_ACCOUNT_MODIFICATIONS,
            "Send alert for modified accounts",
            "xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*",
            sendAlertActions
        );
        this.initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "ActivityFollowUpModifications",            
            TOPIC_NAME_ACTIVITY_FOLLOWUP_MODIFICATIONS,
            "Send alert for modified activity follow ups",
            "xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/followUp/:*",
            sendAlertActions
        );
        this.initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "ActivityModifications",            
            TOPIC_NAME_ACTIVITY_MODIFICATIONS,
            "Send alert for modified activities",
            "xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*",
            sendAlertActions
        );
        this.initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "BookingModifications",            
            TOPIC_NAME_BOOKING_MODIFICATIONS,
            "Send alert for modified bookings",
            "xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/booking/:*",
            sendAlertActions
        );
        this.initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "Competitor Modifications",            
            TOPIC_NAME_COMPETITOR_MODIFICATIONS,
            "Send alert for modified competitors",
            "xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/competitor/:*",
            sendAlertActions
        );
        this.initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "CompoundBookingModifications",            
            TOPIC_NAME_COMPOUND_BOOKING_MODIFICATIONS,
            "Send alert for modified compound bookings",
            "xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/cb/:*",
            sendAlertActions
        );
        this.initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "InvoiceModifications",            
            TOPIC_NAME_INVOICE_MODIFICATIONS,
            "Send alert for modified invoices",
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*",
            sendAlertActions
        );
        this.initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "LeadModifications",            
            TOPIC_NAME_LEAD_MODIFICATIONS,
            "Send alert for modified leads",
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*",
            sendAlertActions
        );
        this.initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "OpportunityModifications",            
            TOPIC_NAME_OPPORTUNITY_MODIFICATIONS,
            "Send alert for modified opportunities",
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*",
            sendAlertActions
        );
        this.initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "OrganizationModifications",            
            TOPIC_NAME_ORGANIZATION_MODIFICATIONS,
            "Send alert for modified organizations",
            "xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*",
            sendAlertActions
        );
        this.initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "ProductModifications",            
            TOPIC_NAME_PRODUCT_MODIFICATIONS,
            "Send alert for modified products",
            "xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*",
            sendAlertActions
        );
        this.initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "QuoteModifications",            
            TOPIC_NAME_QUOTE_MODIFICATIONS,
            "Send alert for modified quotes",
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*",
            sendAlertActions
        );
        this.initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "SalesOrderModifications",            
            TOPIC_NAME_SALES_ORDER_MODIFICATIONS,
            "Send alert for modified sales orders",
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*",
            sendAlertActions
        );
        this.initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "AlertModifications",            
            TOPIC_NAME_ALERT_MODIFICATIONS_EMAIL,
            "Send mail for new alerts",
            "xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/alert/:*",
            sendMailNotificationsActions
        );
        this.initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "AlertModificationsTwitter",            
            TOPIC_NAME_ALERT_MODIFICATIONS_TWITTER,
            "Send direct message for new alerts to Twitter",
            "xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/alert/:*",
            sendDirectMessageToTwitterActions
        );
    }

    //-------------------------------------------------------------------------
    public WfProcessInstance executeWorkflow(
        WorkflowTarget wfTarget,
        WfProcess wfProcess,
        ContextCapable targetObject,
        String triggeredByEventId,
        org.opencrx.kernel.base.jmi1.Subscription triggeredBySubscription,
        Integer triggeredByEventType
    ) throws ServiceException {
        if(wfProcess == null) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.WORKFLOW_MISSING_WORKFLOW,
                "Missing workflow"
            );                                                                
        }
    	PersistenceManager pm = JDOHelper.getPersistenceManager(wfTarget);        
        boolean isSynchronous = wfProcess.isSynchronous() == null ?
        	false :
        	wfProcess.isSynchronous().booleanValue();
        // Target
        if(targetObject == null) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.WORKFLOW_MISSING_TARGET,
                "Missing target object"
            );                                                                
        }
        Path targetObjectIdentity = targetObject.refGetPath();
        // Create workflow instance
        Path wfInstanceIdentity = 
            wfTarget.refGetPath().getDescendant(
                new String[]{
                    "wfProcessInstance", 
                    triggeredByEventId == null ? 
                    	this.getUidAsString() : 
                    	triggeredByEventId
                }
            );            
        WfProcessInstance wfInstance = null;
        // Try to execute workflow in context of existing workflow instance
        try {
        	wfInstance = (WfProcessInstance)pm.getObjectById(wfInstanceIdentity);
        }
        catch(Exception e) {}
        if(wfInstance == null) {
        	wfInstance = pm.newInstance(WfProcessInstance.class);
        	wfInstance.refInitialize(false, false);
            wfInstance.setStepCounter(new Integer(0));
            wfInstance.setProcess(wfProcess);
            wfInstance.setTargetObject(targetObjectIdentity.toXri());            
            wfInstance.setFailed(Boolean.FALSE);
            if(wfTarget instanceof UserHome) {
	            ((UserHome)wfTarget).addWfProcessInstance(
	            	false, 
	            	this.getUidAsString(),
	            	wfInstance
	            );
            }
        }
        // Add parameters of executeWorkflow() operation to property set of WfProcessInstance
        if(triggeredBySubscription != null) {
            UriProperty property = pm.newInstance(UriProperty.class);
            property.refInitialize(false, false);
            property.setName("triggeredBySubscription");
            property.setUriValue(triggeredBySubscription.refGetPath().toXri());
            wfInstance.addProperty(
            	false,
            	this.getUidAsString(),
            	property
            );
        }
        if(triggeredByEventType != null) {
            IntegerProperty property = pm.newInstance(IntegerProperty.class);
            property.refInitialize(false, false);
            property.setName("triggeredByEventType");
            property.setIntegerValue(triggeredByEventType.intValue());
            wfInstance.addProperty(
            	false,
            	this.getUidAsString(),
            	property
            );
        }                
        // Execute workflow if synchronous  
        if(isSynchronous) {
            SynchWorkflow_2_0 workflow = null;            
            Class<?> workflowClass = null;
            try {
                workflowClass = Classes.getApplicationClass(
                    wfProcess.getName()
                );
            }
            catch(ClassNotFoundException e) {
                new ServiceException(e).log();
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.WORKFLOW_NO_IMPLEMENTATION,
                    "implementation not found",
                    new BasicException.Parameter("param0", wfProcess.getName()),
                    new BasicException.Parameter("param1", e.getMessage())
                );                                                                                        
            }
            // Look up constructor
            Constructor<?> workflowConstructor = null;
            try {
                workflowConstructor = workflowClass.getConstructor(new Class[]{});
            }
            catch(NoSuchMethodException e) {
                new ServiceException(e).log();
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.WORKFLOW_MISSING_CONSTRUCTOR,
                    "missing constructor",
                    new BasicException.Parameter("param0", wfProcess.getName()),
                    new BasicException.Parameter("param1", e.getMessage())
                );                                                                                        
            }
            // Instantiate workflow
            try {
                workflow = (SynchWorkflow_2_0)workflowConstructor.newInstance(new Object[]{});
            }
            catch(InstantiationException e) {
                new ServiceException(e).log();
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.WORKFLOW_CAN_NOT_INSTANTIATE,
                    "can not instantiate",
                    new BasicException.Parameter("param0", wfProcess.getName()),
                    new BasicException.Parameter("param1", e.getMessage())
                );                                                                                        
            }
            catch(IllegalAccessException e) {
                new ServiceException(e).log();
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.WORKFLOW_ILLEGAL_ACCESS,
                    "illegal access",
                    new BasicException.Parameter("param0", wfProcess.getName()),
                    new BasicException.Parameter("param1", e.getMessage())
                );                                                                            
            }
            catch(IllegalArgumentException e) {
                new ServiceException(e).log();
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.WORKFLOW_ILLEGAL_ARGUMENT,
                    "illegal argument",
                    new BasicException.Parameter("param0", wfProcess.getName()),
                    new BasicException.Parameter("param1", e.getMessage())
                );                                                                                        
            }
            catch(InvocationTargetException e) {
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.WORKFLOW_CAN_NOT_INVOKE,
                    "can not invoke",
                    new BasicException.Parameter("param0", wfProcess.getName()),
                    new BasicException.Parameter("param1", e.getTargetException().getMessage())
                );                                                                                        
            }
            // Get workflow parameters
            Collection<Property> parameters = wfProcess.getProperty();
            Map<String,Object> params = new HashMap<String,Object>();
            // Add parameters of executeWorkflow operation to params
            if(triggeredBySubscription != null) {
                params.put("triggeredBySubscription", triggeredBySubscription);
            }
            if(triggeredByEventType != null) {
                params.put("triggeredByEventType", triggeredByEventType);            
            }
            for(Property parameter: parameters) {
                Object val = null;
                if(parameter instanceof BooleanProperty) {
                    val = ((BooleanProperty)parameter).isBooleanValue();
                }
                else if(parameter instanceof IntegerProperty) {
                    val = ((IntegerProperty)parameter).getIntegerValue();
                }
                else if(parameter instanceof DecimalProperty) {
                    val = ((DecimalProperty)parameter).getDecimalValue();
                }
                else if(parameter instanceof UriProperty) {
                    val = ((UriProperty)parameter).getUriValue();                
                }
                else if(parameter instanceof StringProperty) {
                    val = ((StringProperty)parameter).getStringValue();
                }
                params.put(
                    parameter.getName(),
                    val
                );
            }
            // Execute workflow
            try {
            	if(targetObject != null) {
	                workflow.execute(
	                    wfTarget,
	                    targetObject,
	                    params,
	                    wfInstance
	                );
            	}
            	else {
            		SysLog.detail("Workflow not executed on null target. Workflow instance is", wfInstance == null ? null : wfInstance.refGetPath());
            	}
                // Update workflow instance after successful execution
                wfInstance.setStartedOn(new Date());
                wfInstance.setLastActivityOn(new Date());
                wfInstance.setFailed(Boolean.FALSE);
            }
            catch(Exception e) {
                ServiceException e0 = new ServiceException(e);
                SysLog.warning(e0.getMessage(), e0.getCause());
                /**
                  * Exceptions are catched in case of synchronous workflows. This prevents a 
                  * transaction rollback. This behaviour is e.g. required in case of activity
                  * process executions. A failed workflow would result in a rollback of activity
                  * status transitions. 
                  * Instead, the status of the workflow execution is updated and a log entry is 
                  * created. This provides enough information by an external workflow handler to 
                  * re-execute the workflow. The workflow is never re-executed if no retries are 
                  * allowed. In this case the failed status is set immediately. 
                  * A workflow implementation must start its own new transaction if the work in 
                  * case of an exception has to be rolled back.
                  */   
                // Update workflow instance. Set timestamp for last activity
                wfInstance.setLastActivityOn(new Date());
                Number maxRetries = wfProcess.getMaxRetries();
                if(
                    (maxRetries == null) ||
                    (maxRetries.intValue() == 0)
                ) {
                    wfInstance.setStartedOn(new Date());
                    wfInstance.setFailed(Boolean.TRUE);
                }
                // Increment stepCounter
                Number stepCounter = wfInstance.getStepCounter();
                if(stepCounter == null) {
                    stepCounter = new Integer(0);
                }
                wfInstance.setStepCounter(
                    new Integer(stepCounter.intValue() + 1)
                );
                // Create log entry
                WfActionLogEntry logEntry = pm.newInstance(WfActionLogEntry.class);
                logEntry.refInitialize(false, false);
                logEntry.setName(e0.getMessage());
                logEntry.setCorrelation(
                	targetObject instanceof BasicObject ?
                		(BasicObject)targetObject :
                		null
                );
                wfInstance.addActionLog(
                	false,
                	this.getUidAsString(),
                	logEntry
                );
            }
        }
        return wfInstance;
    }

    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final short STATUS_OK = 0;
    public static final short STATUS_FAILED = 1;

    public static final String WORKFLOW_EXPORT_MAIL = "ExportMail";
    public static final String WORKFLOW_SEND_MAIL = "SendMail";
    public static final String WORKFLOW_SEND_MAIL_NOTIFICATION = "SendMailNotification";
    public static final String WORKFLOW_SEND_ALERT = "SendAlert";
    public static final String WORKFLOW_PRINT_CONSOLE = "PrintConsole";
    public static final String WORKFLOW_SEND_DIRECT_MESSAGE_TWITTER = "SendDirectMessage";

    public static final String TOPIC_NAME_ACCOUNT_MODIFICATIONS = "Account Modifications";
    public static final String TOPIC_NAME_ACTIVITY_FOLLOWUP_MODIFICATIONS = "Activity Follow Up Modifications";
    public static final String TOPIC_NAME_ACTIVITY_MODIFICATIONS = "Activity Modifications";
    public static final String TOPIC_NAME_ALERT_MODIFICATIONS_EMAIL = "Alert Modifications";
    public static final String TOPIC_NAME_ALERT_MODIFICATIONS_TWITTER = "Alert Modifications (Twitter)";
    public static final String TOPIC_NAME_BOOKING_MODIFICATIONS = "Booking Modifications";
    public static final String TOPIC_NAME_COMPETITOR_MODIFICATIONS = "Competitor Modifications";
    public static final String TOPIC_NAME_COMPOUND_BOOKING_MODIFICATIONS = "Compound Booking Modifications";
    public static final String TOPIC_NAME_INVOICE_MODIFICATIONS = "Invoice Modifications";
    public static final String TOPIC_NAME_LEAD_MODIFICATIONS = "Lead Modifications";
    public static final String TOPIC_NAME_OPPORTUNITY_MODIFICATIONS = "Opportunity Modifications";
    public static final String TOPIC_NAME_ORGANIZATION_MODIFICATIONS = "Organization Modifications";
    public static final String TOPIC_NAME_PRODUCT_MODIFICATIONS = "Product Modifications";
    public static final String TOPIC_NAME_QUOTE_MODIFICATIONS = "Quote Modifications";
    public static final String TOPIC_NAME_SALES_ORDER_MODIFICATIONS = "SalesOrder Modifications";
    
    public static final String WORKFLOW_NAME_PRINT_CONSOLE = PrintConsole.class.getName();
    public static final String WORKFLOW_NAME_SEND_ALERT = SendAlert.class.getName();
    public static final String WORKFLOW_NAME_EXPORT_MAIL = ExportMailWorkflow.class.getName();
    public static final String WORKFLOW_NAME_SEND_MAIL_NOTIFICATION = SendMailNotificationWorkflow.class.getName();
    public static final String WORKFLOW_NAME_SEND_MAIL = SendMailWorkflow.class.getName();
    public static final String WORKFLOW_NAME_SEND_DIRECT_MESSAGE_TWITTER = SendDirectMessageWorkflow.class.getName();
        
}

//--- End of File -----------------------------------------------------------
