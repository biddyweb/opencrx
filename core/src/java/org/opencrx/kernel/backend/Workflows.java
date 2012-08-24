/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Description: Workflows
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
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
import java.util.Date;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.application.mail.exporter.ExportMailWorkflow;
import org.opencrx.application.mail.exporter.SendMailNotificationWorkflow;
import org.opencrx.application.mail.exporter.SendMailWorkflow;
import org.opencrx.kernel.base.jmi1.IntegerProperty;
import org.opencrx.kernel.base.jmi1.UriProperty;
import org.opencrx.kernel.base.jmi1.WorkflowTarget;
import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.home1.jmi1.WfActionLogEntry;
import org.opencrx.kernel.home1.jmi1.WfProcessInstance;
import org.opencrx.kernel.workflow.PrintConsole;
import org.opencrx.kernel.workflow.SendAlert;
import org.opencrx.kernel.workflow1.cci2.TopicQuery;
import org.opencrx.kernel.workflow1.cci2.WfProcessQuery;
import org.opencrx.kernel.workflow1.jmi1.Topic;
import org.opencrx.kernel.workflow1.jmi1.WfProcess;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasicObject;
import org.openmdx.base.jmi1.ContextCapable;
import org.openmdx.base.naming.Path;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.loading.Classes;
import org.openmdx.kernel.log.SysLog;

public class Workflows extends AbstractImpl {

	/**
	 * Register backend class
	 */
	public static void register(
	) {
		registerImpl(new Workflows());
	}

	/**
	 * Get instance of registered backend class
	 */
	public static Workflows getInstance(
	) throws ServiceException {
		return getInstance(Workflows.class);
	}

	/**
	 * Constructor
	 */
	protected Workflows(
	) {
		
	}

	/**
	 * Abstract class for synchronous workflows.
	 */
	public static abstract class SynchronousWorkflow {
	    
	    /**
	     * Executes the workflow for the specified target object. The list of processed 
	     * (modified, created, removed) objects must be updated if the objects are subject 
	     * to topics, subscriptions and workflows. 
	     * 
	     * @param userHome the user's home for which the workflow is executed.
	     * @param targetObject workflow target object.
	     * @param triggeredBy object which triggered workflow.
	     * @param wfProcessInstance workflow process instance.
	     * @param pm persistence manager
	     * @throws ServiceException
	     */
	    public abstract void execute(
	        WorkflowTarget wfTarget,
	        ContextCapable targetObject,
	        WfProcessInstance wfProcessInstance
	    ) throws ServiceException;

	}

	/**
	 * Abstract class for asynchronous workflows
	 */
	public static abstract class AsynchronousWorkflow {
	    
	    /**
	     * Execute the workflow specified by wfProcessInstance. wfProcessInstance may be
	     * modified by execute.
	     * @throws ServiceException
	     */
	    public abstract void execute(
	        WfProcessInstance wfProcessInstance
	    ) throws ServiceException;

	}
	
    /**
     * Get workflow segment
     * @param pm
     * @param providerName
     * @param segmentName
     * @return
     */
    public org.opencrx.kernel.workflow1.jmi1.Segment getWorkflowSegment(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        return (org.opencrx.kernel.workflow1.jmi1.Segment)pm.getObjectById(
            new Path("xri://@openmdx*org.opencrx.kernel.workflow1").getDescendant("provider", providerName, "segment", segmentName)
        );
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.workflow1.jmi1.Topic findTopic(
        String name,
        org.opencrx.kernel.workflow1.jmi1.Segment segment,
        javax.jdo.PersistenceManager pm
    ) {
        TopicQuery topicQuery = (TopicQuery)pm.newQuery(Topic.class);
        topicQuery.name().equalTo(name);
        List<Topic> topics = segment.getTopic(topicQuery);
        return topics.isEmpty() ? 
        	null : 
        		topics.iterator().next();
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.workflow1.jmi1.WfProcess findWfProcess(
        String name,
        org.opencrx.kernel.workflow1.jmi1.Segment segment,
        javax.jdo.PersistenceManager pm
    ) {
        WfProcessQuery wfProcessQuery = (WfProcessQuery)pm.newQuery(WfProcess.class);
        wfProcessQuery.name().equalTo(name);
        List<WfProcess> wfProcesses = segment.getWfProcess(wfProcessQuery);
        return wfProcesses.isEmpty() ? 
        	null :
        		wfProcesses.iterator().next();
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.workflow1.jmi1.Topic initTopic(
        org.opencrx.kernel.workflow1.jmi1.Segment workflowSegment,
        String id,
        String name,
        String description,
        String topicPathPattern,
        WfProcess[] actions
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(workflowSegment);
        Topic topic = null;
        try {
            topic = workflowSegment.getTopic(id);
        } 
        catch(Exception e) {}
        // Do not touch existing topics
        if(topic == null) {
            pm.currentTransaction().begin();
            topic = pm.newInstance(Topic.class);
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
                id,
                topic
            );
            pm.currentTransaction().commit();
        }
        return topic;
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.workflow1.jmi1.WfProcess initWorkflow(
        org.opencrx.kernel.workflow1.jmi1.Segment workflowSegment,
        String id,
        String name,
        String description,
        Boolean isSynchronous,
        org.opencrx.kernel.base.jmi1.Property[] properties 
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(workflowSegment);
        WfProcess wfProcess = null;
        try {
            wfProcess = (WfProcess)workflowSegment.getWfProcess(id);
        }
        catch(Exception e) {}
        if(wfProcess == null) {
            // Add process
            pm.currentTransaction().begin();
            wfProcess = pm.newInstance(WfProcess.class);
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
                for(int i = 0; i < properties.length; i++) {
                    wfProcess.addProperty(
                        UUIDConversion.toUID(UUIDs.newUUID()),
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
        org.opencrx.kernel.workflow1.jmi1.Segment workflowSegment = this.getWorkflowSegment(
            pm, 
            providerName, 
            segmentName
        );
        // ExportMailWorkflow 
        this.initWorkflow(
            workflowSegment,
            WORKFLOW_EXPORT_MAIL,
            ExportMailWorkflow.class.getName(),
            "Export mails",
            Boolean.FALSE,
            null
        );        
        // SendMailWorkflow
        this.initWorkflow(
            workflowSegment,
            WORKFLOW_SEND_MAIL,
            SendMailWorkflow.class.getName(),
            "Send mails",
            Boolean.FALSE,
            null
        );        
        // SendMailNotificationWorkflow
        WfProcess sendMailNotificationWorkflow = this.initWorkflow(
            workflowSegment,
            WORKFLOW_SEND_MAIL_NOTIFICATION,
            SendMailNotificationWorkflow.class.getName(),
            "Send mail notifications",
            Boolean.FALSE,
            null
        );        
        // SendAlert
        WfProcess sendAlertWorkflow = this.initWorkflow(
            workflowSegment,
            WORKFLOW_SEND_ALERT,
            org.opencrx.kernel.workflow.SendAlert.class.getName(),
            "Send alert",
            Boolean.TRUE,
            null
        );        
        // PrintConsole
        this.initWorkflow(
            workflowSegment,
            WORKFLOW_PRINT_CONSOLE,
            org.opencrx.kernel.workflow.PrintConsole.class.getName(),
            "Print to console",
            Boolean.TRUE,
            null
        );
        // SendDirectMessage (Twitter)
        WfProcess sendMessageToTwitterWorkflow = this.initWorkflow(
            workflowSegment,
            WORKFLOW_SEND_DIRECT_MESSAGE_TWITTER,
            org.opencrx.application.twitter.SendDirectMessageWorkflow.class.getName(),
            "Send direct message to Twitter",
            Boolean.TRUE,
            null
        );
        // SendMessage (Jabber)
        WfProcess sendMessageToJabberWorkflow = this.initWorkflow(
            workflowSegment,
            WORKFLOW_SEND_MESSAGE_JABBER,
            org.opencrx.application.jabber.SendMessageWorkflow.class.getName(),
            "Send message to Jabber / XMPP service",
            Boolean.TRUE,
            null
        );
        // BulkActivityFollowUp
        @SuppressWarnings("unused")
        WfProcess bulkActivityFollowUpWorkflow = this.initWorkflow(
            workflowSegment,
            WORKFLOW_BULK_ACTIVITY_FOLLOWUP,
            org.opencrx.kernel.workflow.BulkActivityFollowUpWorkflow.class.getName(),
            "Perform bulk activity follow up. Parameters are:\n" +
            "* activity: template activity\n" +
            "* transition: process transition\n" + 
            "* followUpTitle: follow up title\n" +
            "* followUpText: follow up text\n" +
            "* assignTo: assign activity to contact",
            Boolean.FALSE,
            null
        );
        WfProcess[] sendAlertActions = new WfProcess[]{
            sendAlertWorkflow
        };
        WfProcess[] sendMailNotificationsActions = new WfProcess[]{
            sendMailNotificationWorkflow
        };
        WfProcess[] sendMessageTwitterActions = new WfProcess[]{
            sendMessageToTwitterWorkflow
        };
        WfProcess[] sendMessageToJabberActions = new WfProcess[]{
            sendMessageToJabberWorkflow
        };
        this.initTopic(
            workflowSegment,
            "AccountModifications",            
            TOPIC_NAME_ACCOUNT_MODIFICATIONS,
            "Send alert for modified accounts",
            "xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*",
            sendAlertActions
        );
        this.initTopic(
            workflowSegment,
            "ActivityFollowUpModifications",            
            TOPIC_NAME_ACTIVITY_FOLLOWUP_MODIFICATIONS,
            "Send alert for modified activity follow ups",
            "xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/followUp/:*",
            sendAlertActions
        );
        this.initTopic(
            workflowSegment,
            "ActivityModifications",            
            TOPIC_NAME_ACTIVITY_MODIFICATIONS,
            "Send alert for modified activities",
            "xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*",
            sendAlertActions
        );
        this.initTopic(
            workflowSegment,
            "BookingModifications",            
            TOPIC_NAME_BOOKING_MODIFICATIONS,
            "Send alert for modified bookings",
            "xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/booking/:*",
            sendAlertActions
        );
        this.initTopic(
            workflowSegment,
            "Competitor Modifications",            
            TOPIC_NAME_COMPETITOR_MODIFICATIONS,
            "Send alert for modified competitors",
            "xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/competitor/:*",
            sendAlertActions
        );
        this.initTopic(
            workflowSegment,
            "CompoundBookingModifications",            
            TOPIC_NAME_COMPOUND_BOOKING_MODIFICATIONS,
            "Send alert for modified compound bookings",
            "xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/cb/:*",
            sendAlertActions
        );
        this.initTopic(
            workflowSegment,
            "InvoiceModifications",            
            TOPIC_NAME_INVOICE_MODIFICATIONS,
            "Send alert for modified invoices",
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*",
            sendAlertActions
        );
        this.initTopic(
            workflowSegment,
            "LeadModifications",            
            TOPIC_NAME_LEAD_MODIFICATIONS,
            "Send alert for modified leads",
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*",
            sendAlertActions
        );
        this.initTopic(
            workflowSegment,
            "OpportunityModifications",            
            TOPIC_NAME_OPPORTUNITY_MODIFICATIONS,
            "Send alert for modified opportunities",
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*",
            sendAlertActions
        );
        this.initTopic(
            workflowSegment,
            "OrganizationModifications",            
            TOPIC_NAME_ORGANIZATION_MODIFICATIONS,
            "Send alert for modified organizations",
            "xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*",
            sendAlertActions
        );
        this.initTopic(
            workflowSegment,
            "ProductModifications",            
            TOPIC_NAME_PRODUCT_MODIFICATIONS,
            "Send alert for modified products",
            "xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*",
            sendAlertActions
        );
        this.initTopic(
            workflowSegment,
            "QuoteModifications",            
            TOPIC_NAME_QUOTE_MODIFICATIONS,
            "Send alert for modified quotes",
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*",
            sendAlertActions
        );
        this.initTopic(
            workflowSegment,
            "SalesOrderModifications",            
            TOPIC_NAME_SALES_ORDER_MODIFICATIONS,
            "Send alert for modified sales orders",
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*",
            sendAlertActions
        );
        this.initTopic(
            workflowSegment,
            "AlertModifications",            
            TOPIC_NAME_ALERT_MODIFICATIONS_EMAIL,
            "Send mail for new alerts",
            "xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/alert/:*",
            sendMailNotificationsActions
        );
        this.initTopic(
            workflowSegment,
            "AlertModificationsTwitter",            
            TOPIC_NAME_ALERT_MODIFICATIONS_TWITTER,
            "Send message for new alerts to Twitter",
            "xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/alert/:*",
            sendMessageTwitterActions
        );
        this.initTopic(
            workflowSegment,
            "AlertModificationsJabber",            
            TOPIC_NAME_ALERT_MODIFICATIONS_JABBER,
            "Send message for new alerts to Jabber / XMPP service",
            "xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/alert/:*",
            sendMessageToJabberActions
        );
        this.initTopic(
            workflowSegment,
            "TimerModifications",            
            TOPIC_NAME_TIMER_MODIFICATIONS,
            "Send alert when timer is triggered",
            "xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/timer/:*",
            sendAlertActions
        );
    }

    //-------------------------------------------------------------------------
    public WfProcessInstance executeWorkflow(
        WorkflowTarget wfTarget,
        WfProcess wfProcess,
        ContextCapable targetObject,
        ContextCapable triggeredBy,
        String triggeredByEventId,
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
        } catch(Exception e) {}
        if(wfInstance == null) {
        	wfInstance = pm.newInstance(WfProcessInstance.class);
            wfInstance.setStepCounter(new Integer(0));
            wfInstance.setProcess(wfProcess);
            wfInstance.setTargetObject(targetObjectIdentity.toXRI());            
            wfInstance.setFailed(Boolean.FALSE);
            if(wfTarget instanceof UserHome) {
	            ((UserHome)wfTarget).addWfProcessInstance(
	            	this.getUidAsString(),
	            	wfInstance
	            );
            }
        }
        // Add parameters of executeWorkflow() operation to property set of WfProcessInstance
        if(triggeredBy != null) {
            UriProperty property = pm.newInstance(UriProperty.class);
            property.setName("triggeredBy");
            property.setUriValue(triggeredBy.refGetPath().toXRI());
            wfInstance.addProperty(
            	this.getUidAsString(),
            	property
            );
        }
        if(triggeredByEventType != null) {
            IntegerProperty property = pm.newInstance(IntegerProperty.class);
            property.setName("triggeredByEventType");
            property.setIntegerValue(triggeredByEventType.intValue());
            wfInstance.addProperty(
            	this.getUidAsString(),
            	property
            );
        }
        // Execute workflow if synchronous  
        if(isSynchronous) {
            SynchronousWorkflow workflow = null;            
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
                workflow = (SynchronousWorkflow)workflowConstructor.newInstance(new Object[]{});
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
            // Execute workflow
            try {
                workflow.execute(
                    wfTarget,
                    targetObject,
                    wfInstance
                );
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

    public enum EventType {
    	
    	NONE((short)0),
    	OBJECT_CREATION((short)1),
    	OBJECT_REPLACEMENT((short)3),
    	OBJECT_REMOVAL((short)4),
    	TIMER((short)5);
    	
		private short value;
    	
		private EventType(
			short value
		) {
			this.value = value;
			
		}
    	
		public short getValue(
		) {
			return this.value;
		}
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
    public static final String WORKFLOW_SEND_MESSAGE_JABBER = "SendMessageJabber";
    public static final String WORKFLOW_BULK_ACTIVITY_FOLLOWUP = "BulkActivityFollowUp";

    public static final String TOPIC_NAME_ACCOUNT_MODIFICATIONS = "Account Modifications";
    public static final String TOPIC_NAME_ACTIVITY_FOLLOWUP_MODIFICATIONS = "Activity Follow Up Modifications";
    public static final String TOPIC_NAME_ACTIVITY_MODIFICATIONS = "Activity Modifications";
    public static final String TOPIC_NAME_ALERT_MODIFICATIONS_EMAIL = "Alert Modifications";
    public static final String TOPIC_NAME_ALERT_MODIFICATIONS_TWITTER = "Alert Modifications (Twitter)";
    public static final String TOPIC_NAME_ALERT_MODIFICATIONS_JABBER = "Alert Modifications (Jabber)";
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
    public static final String TOPIC_NAME_TIMER_MODIFICATIONS = "Timer Modifications (Alert)";
    
    public static final String WORKFLOW_NAME_PRINT_CONSOLE = PrintConsole.class.getName();
    public static final String WORKFLOW_NAME_SEND_ALERT = SendAlert.class.getName();
    public static final String WORKFLOW_NAME_EXPORT_MAIL = ExportMailWorkflow.class.getName();
    public static final String WORKFLOW_NAME_SEND_MAIL_NOTIFICATION = SendMailNotificationWorkflow.class.getName();
    public static final String WORKFLOW_NAME_SEND_MAIL = SendMailWorkflow.class.getName();
    public static final String WORKFLOW_NAME_SEND_MESSAGE_TWITTER = org.opencrx.application.twitter.SendDirectMessageWorkflow.class.getName();
    public static final String WORKFLOW_NAME_SEND_MESSAGE_JABBER = org.opencrx.application.jabber.SendMessageWorkflow.class.getName();
    public static final String WORKFLOW_NAME_BULK_ACTIVITY_FOLLOWUP = org.opencrx.kernel.workflow.BulkActivityFollowUpWorkflow.class.getName();

}

//--- End of File -----------------------------------------------------------
