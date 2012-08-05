/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: IndexerServlet.java,v 1.30 2011/01/23 22:16:15 wfro Exp $
 * Description: IndexerServlet
 * Revision:    $Revision: 1.30 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2011/01/23 22:16:15 $
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.kernel.backend.Workflows;
import org.opencrx.kernel.base.jmi1.UpdateIndexResult;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;

/**
 * The IndexerServlet 'listens' for object modifications on incoming
 * audit entries. Modified objects are indexed.
 */  
public class IndexerServlet 
    extends HttpServlet {

    //-----------------------------------------------------------------------
    public void init(
        ServletConfig config
    ) throws ServletException {

        super.init(config);        
        // persistenceManagerFactory
        try {
            this.pmf = Utils.getPersistenceManagerFactory();
        }
        catch (Exception e) {
            throw new ServletException("Can not get connection to data provider", e);
        }

    }

    //-----------------------------------------------------------------------    
    public void updateIndex(
        String id,
        String providerName,
        String segmentName,
        HttpServletRequest req, 
        HttpServletResponse res        
    ) throws IOException {
        
        System.out.println(new Date().toString() + ": " + WORKFLOW_NAME + " " + providerName + "/" + segmentName);

        try {
            PersistenceManager pm = this.pmf.getPersistenceManager(
                "admin-" + segmentName,
                UUIDs.getGenerator().next().toString()
            );        
            Workflows.getInstance().initWorkflows(
                pm, 
                providerName, 
                segmentName
            );
            List<org.opencrx.kernel.base.jmi1.Indexed> indexedSegments = new ArrayList<org.opencrx.kernel.base.jmi1.Indexed>();
            indexedSegments.add(
                (org.opencrx.kernel.base.jmi1.Indexed)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/" + providerName + "/segment/" + segmentName))
            );
            indexedSegments.add(
                (org.opencrx.kernel.base.jmi1.Indexed)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName))
            );
            indexedSegments.add(
                (org.opencrx.kernel.base.jmi1.Indexed)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/" + providerName + "/segment/" + segmentName))
            );
            indexedSegments.add(
                (org.opencrx.kernel.base.jmi1.Indexed)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/" + providerName + "/segment/" + segmentName))
            );
            indexedSegments.add(
                (org.opencrx.kernel.base.jmi1.Indexed)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/" + providerName + "/segment/" + segmentName))
            );
            indexedSegments.add(
                (org.opencrx.kernel.base.jmi1.Indexed)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/" + providerName + "/segment/" + segmentName))
            );
            indexedSegments.add(
                (org.opencrx.kernel.base.jmi1.Indexed)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.forecast1/provider/" + providerName + "/segment/" + segmentName))
            );
            indexedSegments.add(
                (org.opencrx.kernel.base.jmi1.Indexed)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.model1/provider/" + providerName + "/segment/" + segmentName))
            );
            indexedSegments.add(
                (org.opencrx.kernel.base.jmi1.Indexed)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/" + providerName + "/segment/" + segmentName))
            );
            indexedSegments.add(
                (org.opencrx.kernel.base.jmi1.Indexed)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName))
            );
            for(org.opencrx.kernel.base.jmi1.Indexed indexedSegment: indexedSegments) {
                long startedAt = System.currentTimeMillis();
                try {
                    UpdateIndexResult result = indexedSegment.updateIndex();
                    if(result.getNumberOfIndexedObjects() > 0) {
                        long duration = System.currentTimeMillis() - startedAt;
                        System.out.println(new Date().toString() + ": " + WORKFLOW_NAME + " " + providerName + "/" + segmentName + ": Indexed " + indexedSegment.refMofId() + " (#" + result.getNumberOfIndexedObjects() + " objects in " + duration + " ms)");
                    }
                }
                catch(Exception e) {
                    ServiceException e0 = new ServiceException(
                        e,
                        BasicException.Code.DEFAULT_DOMAIN,
                        BasicException.Code.PROCESSING_FAILURE,
                        "Unable to update index",
                        new BasicException.Parameter("segment.xri", indexedSegment.refMofId())
                    );
                    e0.log();
                    System.out.println(new Date() + ": " + WORKFLOW_NAME + " " + providerName + "/" + segmentName + ": exception occured " + e.getMessage() + ". Continuing");
                }        
            }
            try {
                pm.close();
            } 
            catch(Exception e) {}
        }
        catch(Exception e) {
            new ServiceException(e).log();
            System.out.println(new Date() + ": " + WORKFLOW_NAME + " " + providerName + "/" + segmentName + ": exception occured " + e.getMessage() + ". Continuing");
        }        
    }
    
    //-----------------------------------------------------------------------
    protected void handleRequest(
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws ServletException, IOException {
        if(System.currentTimeMillis() > this.startedAt + STARTUP_DELAY) {
            String segmentName = req.getParameter("segment");
            String providerName = req.getParameter("provider");
            String id = providerName + "/" + segmentName;
            if(COMMAND_EXECUTE.equals(req.getPathInfo())) {
                if(!runningSegments.containsKey(id)) {
	                try {
	                    runningSegments.put(
	                    	id,
	                    	Thread.currentThread()
	                    );
	                    this.updateIndex(
	                        id,
	                        providerName,
	                        segmentName,
	                        req,
	                        res
	                    );
	                } 
	                catch(Exception e) {
	                    new ServiceException(e).log();
	                }
	                finally {
	                    runningSegments.remove(id);
	                }
                }
	        	else if(
	        		!runningSegments.get(id).isAlive() || 
	        		runningSegments.get(id).isInterrupted()
	        	) {
	            	Thread t = runningSegments.get(id);
	        		System.out.println(new Date() + ": " + WORKFLOW_NAME + " " + providerName + "/" + segmentName + ": workflow " + t.getId() + " is alive=" + t.isAlive() + "; interrupted=" + t.isInterrupted() + ". Skipping execution.");
	        	}
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
    private static final long serialVersionUID = 4441731357561757549L;

    private static final String COMMAND_EXECUTE = "/execute";
    private static final String WORKFLOW_NAME = "Indexer";
    private static final long STARTUP_DELAY = 180000L;
    private static final Map<String,Thread> runningSegments = new ConcurrentHashMap<String,Thread>();
    
    private PersistenceManagerFactory pmf = null;
    private long startedAt = System.currentTimeMillis();
        
}

//--- End of File -----------------------------------------------------------
