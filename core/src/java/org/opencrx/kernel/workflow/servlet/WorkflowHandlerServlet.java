/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: WorkflowHandlerServlet.java,v 1.42 2010/03/25 10:09:36 wfro Exp $
 * Description: WorkflowHandlerServlet
 * Revision:    $Revision: 1.42 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/03/25 10:09:36 $
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jdo.JDOHelper;
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
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.ContextCapable;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.loading.Classes;
import org.openmdx.kernel.log.SysLog;

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
        WfProcessInstance wfInstance
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(wfInstance);
    	SysLog.info("Execute", wfInstance.getProcess().getName());        
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
                ContextCapable targetObject = null;
                try {
                	targetObject = (ContextCapable)pm.getObjectById(
	                	wfInstance.getTargetObject()
	                );
                } catch(Exception e) {}
                if(targetObject != null) {
	                ExecuteWorkflowParams params = basePackage.createExecuteWorkflowParams(
	                    null,
	                    targetObject,
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
	                    pm.refresh(wfInstance);
	                    // Successful execution if workflow was started (and completed)
	                    return wfInstance.getStartedOn() != null;
	                }
	                catch(Exception e) {
	                	SysLog.warning("Can not execute workflow. Exception occurred", "Workflow instance=" + wfInstance + "; home=" + userHome.refGetPath());
	                	SysLog.warning(e.getMessage(), e.getCause());
	                    try {
	                        pm.currentTransaction().rollback();
	                    } 
	                    catch(Exception e0) {}
	                    return false;
	                }
                }
                else {
                	SysLog.warning("Can not execute workflow. Target object not accessible", "Workflow instance=" + wfInstance + "; home=" + userHome.refGetPath());
                    return false;                	
                }
            }            
            // Asynchronous workflow
            else {
                ASynchWorkflow_1_0 workflow = null;            
                Class<?> workflowClass = null;
                try {
                    workflowClass = Classes.getApplicationClass(
                        workflowName
                    );
                }
                catch(ClassNotFoundException e) {
                	SysLog.error("Implementation for workflow " + workflowName + " not found");
                    return false;          
                }
                // Look up constructor
                Constructor<?> workflowConstructor = null;
                try {
                    workflowConstructor = workflowClass.getConstructor(new Class[]{});
                }
                catch(NoSuchMethodException e) {
                	SysLog.error("No constructor found for workflow " + workflowName);
                }
                // Instantiate workflow
                try {
                    workflow = (ASynchWorkflow_1_0)workflowConstructor.newInstance(new Object[]{});
                }
                catch(InstantiationException e) {
                	SysLog.error("Can not create workflow (can not instantiate)", workflowName);
                    return false;
                }
                catch(IllegalAccessException e) {
                	SysLog.error("Can not create workflow (illegal access)", workflowName);
                    return false;
                }
                catch(IllegalArgumentException e) {
                	SysLog.error("Can not create workflow (illegal argument)", workflowName);
                    return false;
                }
                catch(InvocationTargetException e) {
                	SysLog.error("Can not create workflow (can not invoke target)", workflowName + "(" + e.getTargetException().getMessage() + ")");
                    return false;         
                }  
                workflow.execute(
                    wfInstance
                );
                SysLog.info("SUCCESS");
                return true;
            }
        }
        catch(Exception e) {
        	SysLog.warning("FAILED", e.getMessage());
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
        SysLog.detail(WORKFLOW_NAME + " " + providerName + "/" + segmentName);        
        try {
            PersistenceManager pm = this.persistenceManagerFactory.getPersistenceManager(
                "admin-" + segmentName,
                UUIDs.getGenerator().next().toString()
            );
            Workflows.getInstance().initWorkflows(
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
                userHomeSegment.refGetPath().getDescendant("userHome", ":*", "wfProcessInstance", ":*").toResourcePattern()
            );
            query.startedOn().isNull();
            query.orderByStepCounter().ascending();
            List<WfProcessInstance> wfInstances = userHomeSegment.getExtent(query);
            SysLog.info("Executing workflows");
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
                        wfInstance
                    );
                    if(success) {
                        try {
                        	pm.refresh(wfInstance);
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
                        	SysLog.info(e.getMessage(), e.getCause());
                            try {
                                pm.currentTransaction().rollback();
                            } 
                            catch(Exception e0) {}
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
                        	SysLog.info(e.getMessage(), e.getCause());
                            try {
                                pm.currentTransaction().rollback();
                            } 
                            catch(Exception e0) {}
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
                    	SysLog.info(e.getMessage(), e.getCause());
                        try {
                            pm.currentTransaction().rollback();
                        } 
                        catch(Exception e0) {}                        
                    }
                }
                // Wait
                else {
                    // wait
                }
            }
            try {
                pm.close();
            } 
            catch(Exception e) {}
        }
        catch(Exception e) {
            ServiceException e0 = new ServiceException(e);
            System.out.println("Exception occured " + e0.getMessage() + ". Continuing");
            SysLog.warning("Exception occured " + e0.getMessage() + ". Continuing");
            SysLog.detail(e0.getMessage(), e0.getCause());
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
            if(COMMAND_EXECUTE.equals(req.getPathInfo())) {
            	if(!this.runningSegments.containsKey(id)) {
            		try {
	                    this.runningSegments.put(
	                    	id,
	                    	Thread.currentThread()
	                    );
	                    this.processPendingWorklows(
	                        providerName,
	                        segmentName,
	                        req,
	                        res
	                    );
	                } 
	                catch(Exception e) {
	                	SysLog.warning(e.getMessage(), e.getCause());
	                }
	                finally {
	                    this.runningSegments.remove(id);
	                }
            	}
            	else if(
            		!this.runningSegments.get(id).isAlive() || 
            		this.runningSegments.get(id).isInterrupted()
            	) {
	            	Thread t = this.runningSegments.get(id);
            		System.out.println(new Date() + ": " + WORKFLOW_NAME + " " + providerName + "/" + segmentName + ": workflow " + t.getId() + " is alive=" + t.isAlive() + "; interrupted=" + t.isInterrupted() + ". Skipping execution.");
            	}            	
            }
            else {
            	SysLog.warning(WORKFLOW_NAME + " " + providerName + "/" + segmentName + ". Ignoring command. Running segments are", this.runningSegments);                
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
    private final Map<String,Thread> runningSegments = new ConcurrentHashMap<String,Thread>();
    private long startedAt = System.currentTimeMillis();
}

//--- End of File -----------------------------------------------------------
