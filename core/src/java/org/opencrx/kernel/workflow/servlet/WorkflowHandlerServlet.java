/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: WorkflowHandlerServlet.java,v 1.26 2008/09/02 15:41:59 wfro Exp $
 * Description: WorkflowHandlerServlet
 * Revision:    $Revision: 1.26 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/09/02 15:41:59 $
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
package org.opencrx.kernel.workflow.servlet;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.kernel.backend.Workflows;
import org.opencrx.kernel.base.jmi1.ExecuteWorkflowParams;
import org.opencrx.kernel.home1.cci2.WfProcessInstanceQuery;
import org.opencrx.kernel.home1.jmi1.Home1Package;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.home1.jmi1.WfProcessInstance;
import org.opencrx.kernel.utils.Utils;
import org.opencrx.kernel.workflow.ASynchWorkflow_1_0;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.kernel.application.cci.Classes;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.model1.accessor.basic.cci.Model_1_3;

/**
 * The WorkflowHandlerServlet scans for non-executed 
 * asynchronous workflows. Non-executed workflows are executed and 
 * marked as executed. The workflows to be executed must implement 
 * the interface org.opencrx.kernel.workflow.AsynchWorkflow_1_0.
 */
public class WorkflowHandlerServlet
    extends HttpServlet {

    //-----------------------------------------------------------------------
    public void init(
        ServletConfig config
    ) throws ServletException {

        super.init(config);
        this.model = Utils.createModel();
        // data connection
        try {
            this.persistenceManagerFactory = Utils.getPersistenceManagerFactory();
        }
        catch(Exception e) {
            throw new ServletException("Can not get connection to data provider", e);
        }
    }

    //-----------------------------------------------------------------------
    private boolean executeWorkflow(
        PersistenceManager pm,        
        WfProcessInstance wfInstance
    ) {      
        AppLog.info("Execute", wfInstance.getProcess().getName());        
        try {
            String workflowName = wfInstance.getProcess().getName();
            Boolean isSynchronous = wfInstance.getProcess().isSynchronous();        
            // Synchronous workflow
            if(
                (isSynchronous != null) &&
                isSynchronous.booleanValue()
            ) {
                org.opencrx.kernel.base.jmi1.BasePackage basePackage = Utils.getBasePackage(pm);
                UserHome userHome = (UserHome)pm.getObjectById(
                    wfInstance.refGetPath().getParent().getParent()
                );
                ExecuteWorkflowParams params = basePackage.createExecuteWorkflowParams(
                    null,
                    null,
                    wfInstance.getTargetObject(),
                    // execute workflow with same eventId. This way the workflow
                    // is executed in the same context, i.e. no new workflow instance is created
                    wfInstance.refGetPath().getBase(),
                    null,
                    null,
                    wfInstance.getProcess()
                );
                try {
                    pm.currentTransaction().begin();
                    userHome.executeWorkflow(params);
                    pm.currentTransaction().commit();
                    wfInstance.refRefresh();
                    // Successful execution if workflow was started (and completed)
                    return wfInstance.getStartedOn() != null;
                }
                catch(Exception e) {
                    AppLog.warning("Can not execute workflow", wfInstance.getProcess().getName() + "; home=" + userHome.refMofId());
                    AppLog.warning(e.getMessage(), e.getCause());
                    try {
                        pm.currentTransaction().rollback();
                    } catch(Exception e0) {}
                    return false;
                }                
            }            
            // Asynchronous workflow
            else {
                ASynchWorkflow_1_0 workflow = null;            
                Class workflowClass = null;
                try {
                    workflowClass = Classes.getApplicationClass(
                        workflowName
                    );
                }
                catch(ClassNotFoundException e) {
                    AppLog.error("Implementation for workflow " + workflowName + " not found");
                    return false;          
                }
                // Look up constructor
                Constructor workflowConstructor = null;
                try {
                    workflowConstructor = workflowClass.getConstructor(new Class[]{});
                }
                catch(NoSuchMethodException e) {
                    AppLog.error("No constructor found for workflow " + workflowName);
                }
                // Instantiate workflow
                try {
                    workflow = (ASynchWorkflow_1_0)workflowConstructor.newInstance(new Object[]{});
                }
                catch(InstantiationException e) {
                    AppLog.error("Can not create workflow (can not instantiate)", workflowName);
                    return false;
                }
                catch(IllegalAccessException e) {
                    AppLog.error("Can not create workflow (illegal access)", workflowName);
                    return false;
                }
                catch(IllegalArgumentException e) {
                    AppLog.error("Can not create workflow (illegal argument)", workflowName);
                    return false;
                }
                catch(InvocationTargetException e) {
                    AppLog.error("Can not create workflow (can not invoke target)", workflowName + "(" + e.getTargetException().getMessage() + ")");
                    return false;         
                }  
                workflow.execute(
                    wfInstance,
                    pm
                );
                AppLog.info("SUCCESS");
                return true;
            }
        }
        catch(Exception e) {
            AppLog.warning("FAILED", e.getMessage());
            new ServiceException(e).log();
            return false;
        }            
    }
    
    //-----------------------------------------------------------------------
    private void processPendingWorklows(
        String providerName,
        String segmentName,
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws IOException {
            
        System.out.println(new Date().toString() + ": " + WORKFLOW_NAME + " " + providerName + "/" + segmentName);
        AppLog.detail(WORKFLOW_NAME + " " + providerName + "/" + segmentName);        
        try {
            PersistenceManager pm = this.persistenceManagerFactory.getPersistenceManager(
                "admin-" + segmentName,
                UUIDs.getGenerator().next().toString()
            );
            Workflows.initWorkflows(
                pm,
                providerName,
                segmentName                
            );            
            
            // Get activity segment
            Home1Package userHomePackage = Utils.getHomePackage(pm);
            org.opencrx.kernel.home1.jmi1.Segment userHomeSegment = 
                (org.opencrx.kernel.home1.jmi1.Segment)pm.getObjectById(
                    new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName)
                );
    
            WfProcessInstanceQuery query = userHomePackage.createWfProcessInstanceQuery();
            query.identity().like(
                Utils.xriAsIdentityPattern(userHomeSegment.refMofId() + "/userHome/:*/wfProcessInstance/:*")
            );
            query.startedOn().isNull();
            query.orderByStepCounter().ascending();
            List<WfProcessInstance> wfInstances = userHomeSegment.getExtent(query);
            AppLog.info("Executing workflows");
            for(WfProcessInstance wfInstance: wfInstances) {
                int stepCounter = wfInstance.getStepCounter() == null
                    ? 0
                    : wfInstance.getStepCounter().intValue();
                boolean maxRetriesReached =
                    (wfInstance.getProcess().getMaxRetries() != null) &&
                    (stepCounter >= wfInstance.getProcess().getMaxRetries().intValue());
                // Double retry delay after each unsuccessful execution 
                int retryDelayMillis = (1 << stepCounter) * 1000;
                boolean maxRetryDelayReached =
                    retryDelayMillis > MAX_RETRY_DELAY_MILLIS;
                if(
                    !maxRetriesReached &&
                    !maxRetryDelayReached &&
                    ((wfInstance.getLastActivityOn() == null ? 0L : wfInstance.getLastActivityOn().getTime()) + retryDelayMillis < new Date().getTime())
                ) {
                    boolean success = this.executeWorkflow(
                        pm,
                        wfInstance
                    );
                    if(success) {
                        try {
                            pm.currentTransaction().begin();
                            wfInstance.setStartedOn(new Date());
                            wfInstance.setFailed(Boolean.FALSE);
                            wfInstance.setLastActivityOn(new Date());
                            wfInstance.setStepCounter(
                                new Integer(wfInstance.getStepCounter().intValue() + 1)
                            );
                            pm.currentTransaction().commit();
                        }
                        catch(Exception e) {
                            AppLog.info(e.getMessage(), e.getCause());
                            try {
                                pm.currentTransaction().rollback();
                            } catch(Exception e0) {}
                        }
                    }
                    else {
                        try {
                            pm.currentTransaction().begin();
                            wfInstance.setLastActivityOn(new Date());
                            wfInstance.setStepCounter(
                                new Integer(wfInstance.getStepCounter().intValue() + 1)
                            );
                            pm.currentTransaction().commit();
                        }
                        catch(Exception e) {
                            AppLog.info(e.getMessage(), e.getCause());
                            try {
                                pm.currentTransaction().rollback();
                            } catch(Exception e0) {}
                        }
                    }
                }
                // Execution fails if maxRetryDelayReached || maxRetriesReached 
                else if(maxRetryDelayReached || maxRetriesReached) {
                    try {
                        pm.currentTransaction().begin();
                        wfInstance.setStartedOn(new Date());
                        wfInstance.setFailed(Boolean.TRUE);                    
                        pm.currentTransaction().commit();
                    }
                    catch(Exception e) {
                        AppLog.info(e.getMessage(), e.getCause());
                        try {
                            pm.currentTransaction().rollback();
                        } catch(Exception e0) {}                        
                    }
                }
                // Wait
                else {
                    // wait
                }
            }
            try {
                pm.close();
            } catch(Exception e) {}
        }
        catch(Exception e) {
            System.out.println("Exception occured " + e.getMessage() + ". Continuing");
            AppLog.warning("Exception occured " + e.getMessage() + ". Continuing");
            AppLog.warning(e.getMessage(), e.getCause());
        }
    }    

    //-----------------------------------------------------------------------
    protected void handleRequest(
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws ServletException, IOException {
        if(System.currentTimeMillis() > this.startedAt + 180000L) {
            String segmentName = req.getParameter("segment");
            String providerName = req.getParameter("provider");
            String id = providerName + "/" + segmentName;
            // run
            if(
                COMMAND_EXECUTE.equals(req.getPathInfo()) &&
                !this.runningSegments.contains(id)
            ) {
                try {
                    this.runningSegments.add(id);
                    this.processPendingWorklows(
                        providerName,
                        segmentName,
                        req,
                        res
                    );
                } 
                catch(Exception e) {
                    AppLog.warning(e.getMessage(), e.getCause());
                }
                finally {
                    this.runningSegments.remove(id);
                }            
            }
            else {
                AppLog.warning(WORKFLOW_NAME + " " + providerName + "/" + segmentName + ". Ignoring command. Running segments are", this.runningSegments);                
            }
        }
    }

    //-----------------------------------------------------------------------
    protected void doGet(
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws ServletException, IOException {
        res.setStatus(HttpServletResponse.SC_OK);
        res.flushBuffer();
        this.handleRequest(
            req,
            res
        );
    }
        
    //-----------------------------------------------------------------------
    protected void doPost(
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws ServletException, IOException {
        res.setStatus(HttpServletResponse.SC_OK);
        res.flushBuffer();
        this.handleRequest(
            req,
            res
        );
    }
        
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final long serialVersionUID = 1260441904508971604L;

    private static final String WORKFLOW_NAME = "WorkflowHandler";
    private static final String COMMAND_EXECUTE = "/execute";
    private static final long MAX_RETRY_DELAY_MILLIS = 604800000L; // 7 days
    
    private PersistenceManagerFactory persistenceManagerFactory = null;
    private final List<String> runningSegments = new ArrayList<String>();
    private long startedAt = System.currentTimeMillis();
    private Model_1_3 model = null;
}

//--- End of File -----------------------------------------------------------
