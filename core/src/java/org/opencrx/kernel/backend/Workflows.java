/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Workflows.java,v 1.9 2008/05/30 16:43:27 wfro Exp $
 * Description: Workflows
 * Revision:    $Revision: 1.9 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/05/30 16:43:27 $
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.opencrx.kernel.base.jmi1.Subscription;
import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.home1.jmi1.WfProcessInstance;
import org.opencrx.kernel.utils.Utils;
import org.opencrx.kernel.workflow1.jmi1.WfProcess;
import org.opencrx.kernel.workflow1.jmi1.Workflow1Package;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSelectors;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSpecifier;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.Orders;
import org.openmdx.compatibility.base.dataprovider.cci.RequestCollection;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.kernel.application.cci.Classes;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;

public class Workflows {

    //-----------------------------------------------------------------------
    public Workflows(
        Backend backend
    ) throws ServiceException {
        this.delegation = new RequestCollection(
            backend.context.header, 
            backend.context.router
        );
        this.backend = backend;
    }
                
    //-----------------------------------------------------------------------
    public static org.opencrx.kernel.workflow1.jmi1.Segment getWorkflowSegment(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        return (org.opencrx.kernel.workflow1.jmi1.Segment)pm.getObjectById(
            new Path("xri:@openmdx:org.opencrx.kernel.workflow1/provider/" + providerName + "/segment/" + segmentName)
        );
    }
    
    //-----------------------------------------------------------------------
    public static org.opencrx.kernel.workflow1.jmi1.Topic findTopic(
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
    public static org.opencrx.kernel.workflow1.jmi1.WfProcess findWfProcess(
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
    public static org.opencrx.kernel.workflow1.jmi1.Topic initTopic(
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
        } catch(Exception e) {}
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
    public static org.opencrx.kernel.workflow1.jmi1.WfProcess initWorkflow(
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
    public static void initWorkflows(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) throws ServiceException {
        Workflow1Package workflowPackage = Utils.getWorkflowPackage(pm);
        org.opencrx.kernel.workflow1.jmi1.Segment workflowSegment = getWorkflowSegment(
            pm, 
            providerName, 
            segmentName
        );
        // ExportMailWorkflow 
        initWorkflow(
            pm,
            workflowPackage,
            workflowSegment,
            WORKFLOW_EXPORT_MAIL,
            org.opencrx.mail.workflow.ExportMailWorkflow.class.getName(),
            "Export mails",
            Boolean.FALSE,
            null
        );        
        // SendMailWorkflow
        initWorkflow(
            pm,
            workflowPackage,
            workflowSegment,
            WORKFLOW_SEND_MAIL,
            org.opencrx.mail.workflow.SendMailWorkflow.class.getName(),
            "Send mails",
            Boolean.FALSE,
            null
        );        
        // SendMailNotificationWorkflow
        WfProcess sendMailNotificationWorkflow = initWorkflow(
            pm,
            workflowPackage,
            workflowSegment,
            WORKFLOW_SEND_MAIL_NOTIFICATION,
            org.opencrx.mail.workflow.SendMailNotificationWorkflow.class.getName(),
            "Send mail notifications",
            Boolean.FALSE,
            null
        );        
        // SendAlert
        WfProcess sendAlertWorkflow = initWorkflow(
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
        initWorkflow(
            pm,
            workflowPackage,
            workflowSegment,
            WORKFLOW_PRINT_CONSOLE,
            org.opencrx.kernel.workflow.PrintConsole.class.getName(),
            "Print to console",
            Boolean.TRUE,
            null
        );
        WfProcess[] sendAlertActions = new WfProcess[]{
            sendAlertWorkflow
        };
        WfProcess[] sendMailNotificationsActions = new WfProcess[]{
            sendMailNotificationWorkflow
        };
        initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "AccountModifications",            
            TOPIC_NAME_ACCOUNT_MODIFICATIONS,
            "Send alert for modified accounts",
            "xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*",
            sendAlertActions
        );
        initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "ActivityFollowUpModifications",            
            TOPIC_NAME_ACTIVITY_FOLLOWUP_MODIFICATIONS,
            "Send alert for modified activity follow ups",
            "xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/followUp/:*",
            sendAlertActions
        );
        initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "ActivityModifications",            
            TOPIC_NAME_ACTIVITY_MODIFICATIONS,
            "Send alert for modified activities",
            "xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*",
            sendAlertActions
        );
        initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "BookingModifications",            
            TOPIC_NAME_BOOKING_MODIFICATIONS,
            "Send alert for modified bookings",
            "xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/booking/:*",
            sendAlertActions
        );
        initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "Competitor Modifications",            
            TOPIC_NAME_COMPETITOR_MODIFICATIONS,
            "Send alert for modified competitors",
            "xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/competitor/:*",
            sendAlertActions
        );
        initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "CompoundBookingModifications",            
            TOPIC_NAME_COMPOUND_BOOKING_MODIFICATIONS,
            "Send alert for modified compound bookings",
            "xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/cb/:*",
            sendAlertActions
        );
        initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "InvoiceModifications",            
            TOPIC_NAME_INVOICE_MODIFICATIONS,
            "Send alert for modified invoices",
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*",
            sendAlertActions
        );
        initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "LeadModifications",            
            TOPIC_NAME_LEAD_MODIFICATIONS,
            "Send alert for modified leads",
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*",
            sendAlertActions
        );
        initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "OpportunityModifications",            
            TOPIC_NAME_OPPORTUNITY_MODIFICATIONS,
            "Send alert for modified opportunities",
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*",
            sendAlertActions
        );
        initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "OrganizationModifications",            
            TOPIC_NAME_ORGANIZATION_MODIFICATIONS,
            "Send alert for modified organizations",
            "xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*",
            sendAlertActions
        );
        initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "ProductModifications",            
            TOPIC_NAME_PRODUCT_MODIFICATIONS,
            "Send alert for modified products",
            "xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*",
            sendAlertActions
        );
        initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "QuoteModifications",            
            TOPIC_NAME_QUOTE_MODIFICATIONS,
            "Send alert for modified quotes",
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*",
            sendAlertActions
        );
        initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "SalesOrderModifications",            
            TOPIC_NAME_SALES_ORDER_MODIFICATIONS,
            "Send alert for modified sales orders",
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*",
            sendAlertActions
        );
        initTopic(
            pm,
            workflowPackage,
            workflowSegment,
            "AlertModifications",            
            TOPIC_NAME_ALERT_MODIFICATIONS,
            "Send mail for new alerts",
            "xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/alert/:*",
            sendMailNotificationsActions
        );
    }
        
    //-------------------------------------------------------------------------
    public WfProcessInstance executeWorkflow(
        Path workflowTargetIdentity,
        Path wfProcessIdentity,
        Path targetObjectIdentity,
        String targetObjectXri,
        String triggeredByEventId,
        Subscription triggeredBySubscription,
        Number triggeredByEventType
    ) throws ServiceException {
        DataproviderObject_1_0 wfProcessInstance = this.executeWorkflow(
            this.backend.retrieveObject(
                workflowTargetIdentity
            ),
            wfProcessIdentity, 
            targetObjectIdentity, 
            targetObjectXri, 
            triggeredByEventId, 
            triggeredBySubscription == null ? null : triggeredBySubscription.refGetPath(), 
            triggeredByEventType
        );
        return wfProcessInstance == null
            ? null
            : (WfProcessInstance)this.backend.getDelegatingPkg().refObject(wfProcessInstance.path().toXri());
    }

    //-------------------------------------------------------------------------
    /**
     * Execute workflow and execute workflow instance identity.
     * 
     * @return the updated / created process instance.
     */
    public DataproviderObject_1_0 executeWorkflow(
        DataproviderObject_1_0 userHome,
        Path wfProcessIdentity,
        Path targetObjectPath,
        String targetObjectXri,
        String triggeredByEventId,
        Path triggeredBySubscription,
        Number triggeredByEventType
    ) throws ServiceException {

        // Workflow
        if(wfProcessIdentity == null) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.WORKFLOW_MISSING_WORKFLOW,
                null,
                "Missing workflow"
            );                                                                
        }
        DataproviderObject_1_0 wfProcess = this.delegation.addGetRequest(
            wfProcessIdentity,
            AttributeSelectors.ALL_ATTRIBUTES,
            new AttributeSpecifier[]{}
        );
        String workflowName = (String)wfProcess.values("name").get(0);
        boolean isSynchronous = 
            (wfProcess.values("isSynchronous").get(0) != null) && 
            ((Boolean)wfProcess.values("isSynchronous").get(0)).booleanValue();        

        // Target
        if((targetObjectPath == null) && (targetObjectXri == null)) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.WORKFLOW_MISSING_TARGET,
                null,
                "Missing target object"
            );                                                                
        }
        Path targetObjectIdentity = targetObjectPath != null
            ? targetObjectPath
            : new Path(targetObjectXri);
        
        // Create workflow instance
        Path wfInstanceIdentity = 
            userHome.path().getDescendant(
                new String[]{
                    "wfProcessInstance", 
                    triggeredByEventId == null
                        ? this.backend.getUidAsString()
                        : triggeredByEventId
                }
            );            
        DataproviderObject_1_0 wfInstance = null;
        // Try to execute workflow in context of existing workflow instance
        try {
            try {
                wfInstance = this.backend.retrieveObjectFromDelegation(
                    wfInstanceIdentity
                );
            }
            catch(ServiceException e) {
                wfInstance = new DataproviderObject(wfInstanceIdentity);
                wfInstance.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:home1:WfProcessInstance");
                wfInstance.values("stepCounter").add(new Integer(0));
                wfInstance.values("process").add(wfProcess.path());
                wfInstance.values("targetObject").add(targetObjectIdentity.toXri());            
                wfInstance.values("failed").add(Boolean.FALSE);
                this.delegation.addCreateRequest(
                    (DataproviderObject)wfInstance
                );
            }
        }
        catch(ServiceException e) {
            new ServiceException(e).log();
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.WORKFLOW_CAN_NOT_CREATE_PROCESS_INSTANCE,
                new BasicException.Parameter[]{
                    new BasicException.Parameter("param0", workflowName),
                    new BasicException.Parameter("param1", e.getMessage())
                },
                "Can not get or create process instance"
            );
        }        
        // Add parameters of executeWorkflow() operation to property set of WfProcessInstance
        if(triggeredBySubscription != null) {
            DataproviderObject property = new DataproviderObject(
                wfInstance.path().getDescendant(new String[]{"property", this.backend.getUidAsString()})
            );
            property.values(SystemAttributes.OBJECT_CLASS).add(
                "org:opencrx:kernel:base:UriProperty"
            );
            property.values("name").add(
                "triggeredBySubscription"
            );            
            property.values("uriValue").add(
                triggeredBySubscription.toXri()
            );
            this.delegation.addCreateRequest(
                property
            );
        }
        if(triggeredByEventType != null) {
            DataproviderObject property = new DataproviderObject(
                wfInstance.path().getDescendant(new String[]{"property", this.backend.getUidAsString()})
            );
            property.values(SystemAttributes.OBJECT_CLASS).add(
                "org:opencrx:kernel:base:IntegerProperty"
            );
            property.values("name").add(
                "triggeredByEventType"
            );            
            property.values("integerValue").add(
                triggeredByEventType
            );
            this.delegation.addCreateRequest(
                property
            );
        }                
        // Execute workflow if synchronous  
        if(isSynchronous) {
            SynchWorkflow_1_0 workflow = null;            
            Class workflowClass = null;
            try {
                workflowClass = Classes.getApplicationClass(
                    workflowName
                );
            }
            catch(ClassNotFoundException e) {
                new ServiceException(e).log();
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.WORKFLOW_NO_IMPLEMENTATION,
                    new BasicException.Parameter[]{
                        new BasicException.Parameter("param0", workflowName),
                        new BasicException.Parameter("param1", e.getMessage())
                    },
                    "implementation not found"
                );                                                                                        
            }
            // Look up constructor
            Constructor workflowConstructor = null;
            try {
                workflowConstructor = workflowClass.getConstructor(new Class[]{});
            }
            catch(NoSuchMethodException e) {
                new ServiceException(e).log();
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.WORKFLOW_MISSING_CONSTRUCTOR,
                    new BasicException.Parameter[]{
                        new BasicException.Parameter("param0", workflowName),
                        new BasicException.Parameter("param1", e.getMessage())
                    },
                    "missing constructor"
                );                                                                                        
            }
            // Instantiate workflow
            try {
                workflow = (SynchWorkflow_1_0)workflowConstructor.newInstance(new Object[]{});
            }
            catch(InstantiationException e) {
                new ServiceException(e).log();
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.WORKFLOW_CAN_NOT_INSTANTIATE,
                    new BasicException.Parameter[]{
                        new BasicException.Parameter("param0", workflowName),
                        new BasicException.Parameter("param1", e.getMessage())
                    },
                    "can not instantiate"
                );                                                                                        
            }
            catch(IllegalAccessException e) {
                new ServiceException(e).log();
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.WORKFLOW_ILLEGAL_ACCESS,
                    new BasicException.Parameter[]{
                        new BasicException.Parameter("param0", workflowName),
                        new BasicException.Parameter("param1", e.getMessage())
                    },
                    "illegal access"
                );                                                                            
            }
            catch(IllegalArgumentException e) {
                new ServiceException(e).log();
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.WORKFLOW_ILLEGAL_ARGUMENT,
                    new BasicException.Parameter[]{
                        new BasicException.Parameter("param0", workflowName),
                        new BasicException.Parameter("param1", e.getMessage())
                    },
                    "illegal argument"
                );                                                                                        
            }
            catch(InvocationTargetException e) {
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.WORKFLOW_CAN_NOT_INVOKE,
                    new BasicException.Parameter[]{
                        new BasicException.Parameter("param0", workflowName),
                        new BasicException.Parameter("param1", e.getTargetException().getMessage())
                    },
                    "can not invoke"
                );                                                                                        
            }
            // Get workflow parameters
            List parameters = this.delegation.addFindRequest(
                wfProcess.path().getChild("property"),
                null,
                AttributeSelectors.ALL_ATTRIBUTES,
                null,
                0,
                Integer.MAX_VALUE,
                Orders.ASCENDING            
            );
            Map params = new HashMap();
            // Add parameters of executeWorkflow operation to params
            if(triggeredBySubscription != null) {
                params.put("triggeredBySubscription", triggeredBySubscription);
            }
            if(triggeredByEventType != null) {
                params.put("triggeredByEventType", triggeredByEventType);            
            }
            for(
                Iterator i = parameters.iterator(); 
                i.hasNext(); 
            ) {
                DataproviderObject_1_0 parameter = (DataproviderObject_1_0)i.next();
                String parameterType = (String)parameter.values(SystemAttributes.OBJECT_CLASS).get(0);
                Object val = null;
                if("org:opencrx:kernel:base:BooleanProperty".equals(parameterType)) {
                    val = parameter.values("booleanValue");
                }
                else if("org:opencrx:kernel:base:IntegerProperty".equals(parameterType)) {
                    val = parameter.values("integerValue");
                }
                else if("org:opencrx:kernel:base:DecimalProperty".equals(parameterType)) {
                    val = parameter.values("decimalValue");
                }
                else if("org:opencrx:kernel:base:UriProperty".equals(parameterType)) {
                    val = parameter.values("uriValue");                
                }
                else {
                    val = parameter.values("stringValue");
                }
                params.put(
                    parameter.values("name").get(0),
                    val
                );
            }
            // Execute workflow
            try {
                workflow.execute(
                    userHome,
                    targetObjectIdentity,
                    params,
                    wfInstance.path(),
                    this.backend
                );
                // Update workflow instance after successful execution
                wfInstance = this.backend.retrieveObjectForModification(
                    wfInstanceIdentity
                );
                wfInstance.clearValues("startedOn").add(
                    DateFormat.getInstance().format(new Date())
                );
                wfInstance.clearValues("lastActivityOn").add(
                    DateFormat.getInstance().format(new Date())
                );
                wfInstance.clearValues("failed").add(Boolean.FALSE);
            }
            catch(Exception e) {
                ServiceException e0 = new ServiceException(e);
                AppLog.warning(e0.getMessage(), e0.getCause());
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
                wfInstance = this.backend.retrieveObjectForModification(
                    wfInstanceIdentity
                );
                wfInstance.clearValues("lastActivityOn").add(
                    DateFormat.getInstance().format(new Date())
                );
                Number maxRetries = (Number)wfProcess.values("maxRetries").get(0);
                if(
                    (maxRetries == null) ||
                    (maxRetries.intValue() == 0)
                ) {
                    wfInstance.clearValues("startedOn").add(
                        DateFormat.getInstance().format(new Date())
                    );
                    wfInstance.clearValues("failed").add(Boolean.TRUE);
                }
                // Create log entry
                DataproviderObject logEntry = new DataproviderObject(
                    wfInstanceIdentity.getDescendant(new String[]{"actionLog", this.backend.getUidAsString()})
                );
                logEntry.values(SystemAttributes.OBJECT_CLASS).add(
                    "org:opencrx:kernel:home1:WfActionLogEntry"
                );
                logEntry.values("name").add(e0.getMessage());
                logEntry.values("correlation").add(targetObjectPath);
                this.delegation.addCreateRequest(
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
        
    public static final String TOPIC_NAME_ACCOUNT_MODIFICATIONS = "Account Modifications";
    public static final String TOPIC_NAME_ACTIVITY_FOLLOWUP_MODIFICATIONS = "Activity Follow Up Modifications";
    public static final String TOPIC_NAME_ACTIVITY_MODIFICATIONS = "Activity Modifications";
    public static final String TOPIC_NAME_ALERT_MODIFICATIONS = "Alert Modifications";
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
    
    public static final String WORKFLOW_NAME_EXPORT_MAIL = "org.opencrx.mail.workflow.ExportMailWorkflow";
    public static final String WORKFLOW_NAME_PRINT_CONSOLE = "org.opencrx.kernel.workflow.PrintConsole";
    public static final String WORKFLOW_NAME_SEND_ALERT = "org.opencrx.kernel.workflow.SendAlert";
    public static final String WORKFLOW_NAME_SEND_MAIL = "org.opencrx.mail.workflow.SendMailWorkflow";
    public static final String WORKFLOW_NAME_SEND_MAIL_NOTIFICATION = "org.opencrx.mail.workflow.SendMailNotificationWorkflow";
    
    private final Backend backend;
    private final RequestCollection delegation;
    
}

//--- End of File -----------------------------------------------------------
