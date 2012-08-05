/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: WorkflowControllerServlet.java,v 1.49 2008/05/29 23:12:38 wfro Exp $
 * Description: WorkflowControllerServlet
 * Revision:    $Revision: 1.49 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/05/29 23:12:38 $
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
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.kernel.admin1.jmi1.Admin1Package;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.utils.Utils;
import org.opencrx.kernel.workflow1.jmi1.WfProcess;
import org.opencrx.kernel.workflow1.jmi1.Workflow1Package;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;
import org.openmdx.model1.accessor.basic.cci.Model_1_3;

/**
 * The WorkflowControllerServlet periodically pings the configured URLs 
 */  
public class WorkflowControllerServlet 
    extends HttpServlet {

    //-----------------------------------------------------------------------
    public static class PingRate {
        
        public PingRate(
            long initial
        ) {
            this.rate = initial;
        }
        
        public void setRate(
            long newValue
        ) {
            this.rate = newValue;
        }
        
        public long getRate(
        ) {
            return this.rate;
        }
        
        public void increment(
        ) {
            this.rate++;
        }
        
        public void decrement(
        ) {
            if(this.rate > 1) {
                this.rate--;
            }
        }
        
        private long rate = 1L;
    }
     
    //-----------------------------------------------------------------------
    public class WorkflowMonitor
       implements Runnable {
        
        public WorkflowMonitor(
            List monitoredWorkflowServlets
        ) {
            this.monitoredWorkflowServlets = monitoredWorkflowServlets;
        }
        
        public void run(
        ) {
            // List all monitored paths
            List paths = new ArrayList();
            for(
                Iterator i = this.monitoredWorkflowServlets.iterator();
                i.hasNext();
            ) {
                WorkflowServletConfig monitoredWorkflowServlet = (WorkflowServletConfig)i.next();
                paths.add(monitoredWorkflowServlet.getPath());
            }                
            System.out.println(new Date().toString() + ": WorkflowController: monitoring " + paths);
            // Monitor forever
            while(true) {                
                try {
                    for(
                        Iterator i = this.monitoredWorkflowServlets.iterator();
                        i.hasNext();
                    ) {
                        WorkflowServletConfig monitoredWorkflowServlet = (WorkflowServletConfig)i.next();
                        URL monitoredURL = WorkflowControllerServlet.this.getWorkflowServletURL(
                            monitoredWorkflowServlet.getPath()
                        );
                        AppLog.detail("Next execution", Arrays.asList(new Object[]{monitoredURL, monitoredWorkflowServlet.getNextExecutionAt()}));
                        if(new Date().compareTo(monitoredWorkflowServlet.getNextExecutionAt()) > 0) {
                            if(monitoredURL != null) {
                                try {
                                    HttpURLConnection connection = (HttpURLConnection)monitoredURL.openConnection();
                                    connection.setInstanceFollowRedirects(false);
                                    connection.setDoInput(true);
                                    connection.setDoOutput(true);
                                    connection.setRequestMethod("POST");
                                    int rc = connection.getResponseCode();
                                    if(rc != HttpURLConnection.HTTP_OK) {
                                        System.out.println(new Date().toString() + ": WorkflowController: response code for " + monitoredURL + " " + rc);
                                    }
                                    monitoredWorkflowServlet.scheduleNextExecution();
                                }
                                catch(IOException e0) {
                                    new ServiceException(e0).log();
                                }
                            }                            
                        }
                    }    
                    // Wait 1 min between monitoring cycles
                    try {
                        Thread.sleep(60000L);
                    }
                    catch (InterruptedException e) {}
                }
                catch(Exception e) {
                    System.out.println(new Date().toString() + ": WorkflowController: catched exception (for more information see log) " + e.getMessage());
                    ServiceException e0 = new ServiceException(e);
                    AppLog.error(e0.getMessage(), e0.getCause());
                }
                catch(Error e) {
                    System.out.println(new Date().toString() + ": WorkflowController: catched error (for more information see log) " + e.getMessage());
                    AppLog.error(e.getMessage(), e.getCause());                    
                }
            }            
        }
        
        private final List monitoredWorkflowServlets;
        
    }
    
    //-----------------------------------------------------------------------
    public class WorkflowServletConfig {
        
        public WorkflowServletConfig(
            String path,
            boolean autostart,
            PingRate pingRate
        ) {
            this.path = path;
            this.isAutostart = autostart;
            this.pingRate = pingRate;
            this.nextExecutionAt = new GregorianCalendar();
            this.nextExecutionAt.setTimeInMillis(
                System.currentTimeMillis() + 60000L
            );
            this.scheduleNextExecution();
        }
    
        public String getPath(
        ) {
            return this.path;
        }
        
        public boolean isAutostart(
        ) {
            return this.isAutostart;
        }
                
        public Date getNextExecutionAt(
        ) {
            return this.nextExecutionAt.getTime();         
        }
        
        public void scheduleNextExecution(
        ) {
            GregorianCalendar nextExecutionAt = new GregorianCalendar();
            nextExecutionAt.setTime(this.nextExecutionAt.getTime());
            while(nextExecutionAt.getTime().compareTo(new Date()) < 0) {
                nextExecutionAt.setTimeInMillis(
                    nextExecutionAt.getTimeInMillis() + this.pingRate.getRate() * 60000L
                );
            }
            this.nextExecutionAt.setTime(nextExecutionAt.getTime());
        }

        private final String path;
        private final boolean isAutostart;
        private final PingRate pingRate;
        private GregorianCalendar nextExecutionAt;
    }
    
    //-----------------------------------------------------------------------
    public void init(
        ServletConfig config
    ) throws ServletException {

        super.init(config);
        this.model = Utils.createModel();
        String providerName = new Path(this.getInitParameter("realmSegment")).get(2);
        PersistenceManager pm = null;
        try {
            pm = Utils.getPersistenceManagerFactory().getPersistenceManager(
                SecurityKeys.ROOT_PRINCIPAL,
                UUIDs.getGenerator().next().toString()
            );
        }
        catch(Exception e) {
            throw new ServletException("Can not get connection to provider", e);
        }

        // Get component configuration
        try {
            Admin1Package adminPackage = Utils.getAdminPackage(pm);            
            org.opencrx.kernel.base.jmi1.BasePackage basePackage = Utils.getBasePackage(pm); 
            org.opencrx.kernel.admin1.jmi1.Segment adminSegment = 
                (org.opencrx.kernel.admin1.jmi1.Segment)pm.getObjectById(
                    new Path("xri:@openmdx:org.opencrx.kernel.admin1/provider/" + providerName + "/segment/Root")
                );
            try {
                this.componentConfiguration = adminSegment.getConfiguration(
                    COMPONENT_CONFIGURATION_ID
                );
            }
            catch(Exception e) {}
            if(this.componentConfiguration == null) {
                org.opencrx.kernel.admin1.jmi1.ComponentConfiguration componentConfiguration = 
                    adminPackage.getComponentConfiguration().createComponentConfiguration();
                componentConfiguration.setName("WorkflowController");
                pm.currentTransaction().begin();
                adminSegment.addConfiguration(
                    false, 
                    COMPONENT_CONFIGURATION_ID,
                    componentConfiguration
                );
                // Default serverURL
                UUIDGenerator uuids = UUIDs.getGenerator();
                org.opencrx.kernel.base.jmi1.StringProperty sp = basePackage.getStringProperty().createStringProperty();
                sp.setName(WorkflowControllerServlet.OPTION_SERVER_URL);
                sp.setDescription("Server URL");
                sp.setStringValue("http://127.0.0.1:8080/opencrx-core-" + providerName);
                componentConfiguration.addProperty(
                    false,                         
                    uuids.next().toString(),
                    sp
                );
                // SubscriptionHandler.<provider>.Standard.autostart
                sp = basePackage.getStringProperty().createStringProperty();
                sp.setName(MONITORED_WORKFLOW_SUBSCRIPTIONHANDLER + "." + providerName + ".Standard." + OPTION_AUTOSTART);
                sp.setDescription(MONITORED_WORKFLOW_SUBSCRIPTIONHANDLER + " autostart");
                sp.setStringValue("false");
                componentConfiguration.addProperty(
                    false,                         
                    uuids.next().toString(),
                    sp
                );
                // SubscriptionHandler.<provider>.Standard.pingrate
                sp = basePackage.getStringProperty().createStringProperty();
                sp.setName(MONITORED_WORKFLOW_SUBSCRIPTIONHANDLER + "." + providerName + ".Standard." + OPTION_PINGRATE);
                sp.setDescription(MONITORED_WORKFLOW_SUBSCRIPTIONHANDLER + " pingrate");
                sp.setStringValue("2");
                componentConfiguration.addProperty(
                    false,                         
                    uuids.next().toString(),
                    sp
                );
                // IndexerServlet.<provider>.Standard.autostart
                sp = basePackage.getStringProperty().createStringProperty();
                sp.setName(MONITORED_WORKFLOW_INDEXER + "." + providerName + ".Standard." + OPTION_AUTOSTART);
                sp.setDescription(MONITORED_WORKFLOW_INDEXER + " autostart");
                sp.setStringValue("false");
                componentConfiguration.addProperty(
                    false, 
                    uuids.next().toString(),
                    sp
                );
                // IndexerServlet.<provider>.Standard.pingrate
                sp = basePackage.getStringProperty().createStringProperty();
                sp.setName(MONITORED_WORKFLOW_INDEXER + "." + providerName + ".Standard." + OPTION_PINGRATE);
                sp.setDescription(MONITORED_WORKFLOW_INDEXER + " pingrate");
                sp.setStringValue("2");
                componentConfiguration.addProperty(
                    false, 
                    uuids.next().toString(),
                    sp                   
                );
                // WorkflowHandler.<provider>.Standard.autostart
                sp = basePackage.getStringProperty().createStringProperty();
                sp.setName(MONITORED_WORKFLOW_WORKFLOWHANDLER + "." + providerName + ".Standard." + OPTION_AUTOSTART);
                sp.setDescription(MONITORED_WORKFLOW_WORKFLOWHANDLER + " autostart");
                sp.setStringValue("false");
                componentConfiguration.addProperty(
                    false, 
                    uuids.next().toString(),
                    sp
                );
                // WorkflowHandler.<provider>.Standard.pingrate
                sp = basePackage.getStringProperty().createStringProperty();
                sp.setName(MONITORED_WORKFLOW_WORKFLOWHANDLER + "." + providerName + ".Standard." + OPTION_PINGRATE);
                sp.setDescription(MONITORED_WORKFLOW_WORKFLOWHANDLER + " pingrate");
                sp.setStringValue("2");
                componentConfiguration.addProperty(
                    false, 
                    uuids.next().toString(),
                    sp
                );
                pm.currentTransaction().commit();
                this.componentConfiguration = adminSegment.getConfiguration(
                    COMPONENT_CONFIGURATION_ID
                );
            }
        }
        catch(Exception e) {
            throw new ServletException("Can not get component configuration", e);
        }
        
        // Get set of segments to be monitored
        Set segmentNames = new HashSet();
        try {
            Set excludeRealms = new HashSet();
            for(int ii = 0; ii < 100; ii++) {
                if(this.getInitParameter("excludeRealm[" + ii + "]") != null) {
                    excludeRealms.add(
                        this.getInitParameter("excludeRealm[" + ii + "]")
                    );
                }
            }
            org.openmdx.security.realm1.jmi1.Segment realmSegment = 
                (org.openmdx.security.realm1.jmi1.Segment)pm.getObjectById(
                    new Path(this.getInitParameter("realmSegment"))
                );
            for(Iterator i = realmSegment.getRealm().iterator(); i.hasNext(); ) {
                org.openmdx.security.realm1.jmi1.Realm realm = (org.openmdx.security.realm1.jmi1.Realm)i.next();
                if(!excludeRealms.contains(realm.getName())) {
                    segmentNames.add(realm.getName());
                }
            }
        }
        catch(Exception e) {
            throw new ServletException("Error occured while retrieving realms", e);
        }
        
        // Create a path to be monitored from each configured path and provider/segment
        try {
            this.workflowServlets = new ArrayList();
            this.monitoredWorkflowServlets = new ArrayList();
            for(Iterator j = segmentNames.iterator(); j.hasNext(); ) {
                String segmentName = (String)j.next();
                int ii = 0;
                while(this.getInitParameter("path[" + ii + "]") != null) {
                    String path = this.getInitParameter("path[" + ii + "]");
                    String autostart = this.getComponentConfigProperty(path.substring(1) + "." + providerName + "." + segmentName + "." + OPTION_AUTOSTART);
                    String pingrate = this.getComponentConfigProperty(path.substring(1) + "." + providerName + "." + segmentName + "." + OPTION_PINGRATE);
                    WorkflowServletConfig servletConfig =
                        new WorkflowServletConfig(
                            path + "/execute?provider=" + providerName + "&segment=" + segmentName,
                            autostart != null
                                ? Boolean.valueOf(autostart).booleanValue()
                                : false,
                            pingrate != null
                                ? new PingRate(Long.valueOf(pingrate).longValue())
                                : new PingRate(1L)
                        );                        
                    this.workflowServlets.add(
                        servletConfig
                    );
                    if(servletConfig.isAutostart()) {
                        this.monitoredWorkflowServlets.add(servletConfig);
                    }
                    ii++;
                }
            }
            // Start monitor
            new Thread(
                new WorkflowMonitor(
                    this.monitoredWorkflowServlets
                )
            ).start();
        }
        catch(Exception e) {
            throw new ServletException("Can not start workflow monitor", e);
        }
    }

    //-----------------------------------------------------------------------
    private String getComponentConfigProperty(
        String name
    ) {
        String value = null;
        for(int i = 0; i < 1; i++) {
            for(Iterator j = this.componentConfiguration.getProperty().iterator(); j.hasNext(); ) {
                org.opencrx.kernel.base.jmi1.Property p = (org.opencrx.kernel.base.jmi1.Property)j.next();
                if(
                    p.getName().equals(name) &&
                    (p instanceof org.opencrx.kernel.base.jmi1.StringProperty)
                ) {
                    value = ((org.opencrx.kernel.base.jmi1.StringProperty)p).getStringValue();
                    break;
                }
            }
            if(value == null) {
                this.componentConfiguration.refRefresh();
            }
            else {
                break;
            }
        }
        return value;
    }
    
    //-----------------------------------------------------------------------
    public URL getWorkflowServletURL(
        String path
    ) throws MalformedURLException {
        String servlerURL = this.getComponentConfigProperty(OPTION_SERVER_URL);
        return servlerURL == null
            ? null
            : new URL(servlerURL + path);
    }
    
    //-----------------------------------------------------------------------
    protected void handleRequest(
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws ServletException, IOException {
        
        // Add/remove to activeURLs
        if(COMMAND_START.equals(req.getPathInfo())) {
            for(
                Iterator i = this.workflowServlets.iterator();
                i.hasNext();
            ) {
                WorkflowServletConfig servletConfig = (WorkflowServletConfig)i.next();
                if(URLEncoder.encode(servletConfig.getPath(), "UTF-8").equals(req.getQueryString())) {
                    this.monitoredWorkflowServlets.add(servletConfig);
                    break;
                }
            }
        }
        else if(COMMAND_STOP.equals(req.getPathInfo())) {
            URL monitoredURL = this.getWorkflowServletURL(
                URLDecoder.decode(req.getQueryString(), "UTF-8")
            );
            for(
                Iterator i = this.monitoredWorkflowServlets.iterator();
                i.hasNext();
            ) {
                WorkflowServletConfig controlledWorkflow = (WorkflowServletConfig)i.next();
                URL url = this.getWorkflowServletURL(
                    controlledWorkflow.getPath()
                );
                if(url.equals(monitoredURL)) {
                    i.remove();
                    break;
                }
            }
        }
        
        // List status
        PrintWriter out = res.getWriter();
        out.println("<h2>openCRX Workflow Controller</h2>");
        out.println("<table>");
        for(
            Iterator i = this.workflowServlets.iterator();
            i.hasNext();
        ) {
            WorkflowServletConfig servletConfig = (WorkflowServletConfig)i.next();
            boolean active = this.monitoredWorkflowServlets.contains(servletConfig);
            out.println("<tr>");
            out.println("<td>" + servletConfig.getPath() + "</td><td><a href=\"" + req.getContextPath() + req.getServletPath() + (active ? "/stop" : "/start") + "?" + URLEncoder.encode(servletConfig.getPath(), "UTF-8") + "\">" + (active ? "Turn Off" : "Turn On") + "</a></td>");
            out.println("</tr>");
        }
        out.println("</table>");
    }

    //-----------------------------------------------------------------------
    protected void doGet(
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws ServletException, IOException {
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
        this.handleRequest(
            req,
            res
        );
    }
        
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final long serialVersionUID = -2397456308895573603L;
    
    private static final String COMPONENT_CONFIGURATION_ID = "WorkflowController";
    
    private static final String COMMAND_START = "/start";
    private static final String COMMAND_STOP = "/stop";

    private static final String MONITORED_WORKFLOW_INDEXER = "IndexerServlet";
    private static final String MONITORED_WORKFLOW_WORKFLOWHANDLER = "WorkflowHandler";
    private static final String MONITORED_WORKFLOW_SUBSCRIPTIONHANDLER = "SubscriptionHandler";
    
    public static final String OPTION_SERVER_URL = "serverURL";
    public static final String OPTION_AUTOSTART = "autostart";
    public static final String OPTION_PINGRATE = "pingrate";
    public static final String OPTION_EXECUTION_PERIOD = "executionPeriod";
    
    private List workflowServlets = null;
    private List monitoredWorkflowServlets = null;
    private org.opencrx.kernel.admin1.jmi1.ComponentConfiguration componentConfiguration = null;
    private Model_1_3 model = null;
}

//--- End of File -----------------------------------------------------------
