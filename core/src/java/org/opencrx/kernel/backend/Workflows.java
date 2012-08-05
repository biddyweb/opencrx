/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Workflows.java,v 1.6 2007/12/25 17:19:11 wfro Exp $
 * Description: Workflows
 * Revision:    $Revision: 1.6 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2007/12/25 17:19:11 $
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.opencrx.kernel.base.jmi1.Subscription;
import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.home1.jmi1.WfProcessInstance;
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
                AppLog.warning(e0.getMessage(), e0.getCause(), 1);
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

    private final Backend backend;
    private final RequestCollection delegation;
    
}

//--- End of File -----------------------------------------------------------
