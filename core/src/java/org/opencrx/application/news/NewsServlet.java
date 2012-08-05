/*
 * ====================================================================
 * Project:     openCRX/Application, http://www.opencrx.org/
 * Name:        $Id: NewsServlet.java,v 1.13 2010/04/23 13:25:00 wfro Exp $
 * Description: NewsServlet
 * Revision:    $Revision: 1.13 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/04/23 13:25:00 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2009, CRIXP Corp., Switzerland
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
package org.opencrx.application.news;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.kernel.backend.Notifications;
import org.opencrx.kernel.backend.UserHomes;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.home1.cci2.AlertQuery;
import org.opencrx.kernel.utils.ComponentConfigHelper;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.ContextCapable;
import org.openmdx.base.naming.Path;
import org.openmdx.base.text.conversion.XMLEncoder;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.portal.servlet.Action;

public class NewsServlet extends HttpServlet {

    //-----------------------------------------------------------------------
    @Override
    public void init(
        ServletConfig config            
    ) throws ServletException {
        super.init();        
        if(this.persistenceManagerFactory == null) {                    
            try {
                Utils.getModel();
                this.persistenceManagerFactory = Utils.getPersistenceManagerFactory();
                this.rootPm = this.persistenceManagerFactory.getPersistenceManager(
                    SecurityKeys.ROOT_PRINCIPAL,
                    UUIDs.getGenerator().next().toString()
                );            
            }
            catch (NamingException e) {
                throw new ServletException( 
                    "Can not get the initial context", 
                    e
                );                
            }
            catch(ServiceException e) {
                throw new ServletException( 
                    "Can not get persistence manager", 
                    e
                );                
            }        
        }            
    }
    
    //-----------------------------------------------------------------------
    protected PersistenceManager getPersistenceManager(
        HttpServletRequest req
    ) {
        return req.getUserPrincipal() == null ?
            null :
            this.persistenceManagerFactory.getPersistenceManager(
                req.getUserPrincipal().getName(),
                UUIDs.getGenerator().next().toString()
            );
    }

    //-----------------------------------------------------------------------
    protected org.opencrx.kernel.admin1.jmi1.ComponentConfiguration getComponentConfiguration(
        String providerName
    ) {
        if(this.componentConfiguration == null) {
            this.componentConfiguration = ComponentConfigHelper.getComponentConfiguration(
                CONFIGURATION_ID,
                providerName,
                this.rootPm,
                false,
                null
            );
        }
        return this.componentConfiguration;
    }
    
    //-----------------------------------------------------------------------
    protected String[] splitUri(
        String uri
    ) {
        return uri == null ?
            new String[]{} :
            uri.split("/");
    }

    //-----------------------------------------------------------------------
    @Override
    protected void doGet(
        HttpServletRequest req, 
        HttpServletResponse resp
    ) throws ServletException, IOException {
        PersistenceManager pm = this.getPersistenceManager(req);
        if(pm == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }        
        // id
        String[] ids = this.splitUri(req.getParameter("id"));
        String providerName = ids.length > 0 ? ids[0] : null;
        String segmentName = ids.length > 1 ? ids[1] : null;
        // max
        int max = MAX_NEWS;
        if(req.getParameter("max") != null) {
            max = Integer.valueOf(req.getParameter("max")).intValue();
        }        
        if(
            (providerName != null) &&
            (segmentName != null) &&
            RESOURCE_TYPE_RSS.equals(req.getParameter(PARAMETER_NAME_TYPE))
        ) {
            try {
                resp.setCharacterEncoding("UTF-8");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("application/rss+xml");
                org.opencrx.kernel.home1.jmi1.UserHome userHome = (org.opencrx.kernel.home1.jmi1.UserHome)pm.getObjectById(
                    new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName + "/userHome/" + req.getUserPrincipal().getName())
                );
                if(userHome != null) {
                	// Sample date: Fri, 23 Apr 2010 12:49:00 +0200
                	DateFormat dateTimeFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                    AlertQuery alertQuery = (AlertQuery)pm.newQuery(org.opencrx.kernel.home1.jmi1.Alert.class);
                    alertQuery.alertState().lessThanOrEqualTo((short)1);
                    alertQuery.reference().isNonNull();
                    alertQuery.orderByCreatedAt().descending();
                    List<org.opencrx.kernel.home1.jmi1.Alert> alerts = userHome.getAlert(alertQuery);
                    PrintWriter p = resp.getWriter();
                    // Header
                    p.write(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<rss version=\"2.0\">\n" +
                        "  <channel>\n" +
                        "    <generator>" + NewsServlet.class.getName() + "</generator>\n" +
                        "    <title>Alerts " + req.getUserPrincipal().getName() + "@" + providerName + ":" + segmentName + "</title>\n" +
                        "    <description>Alerts " + req.getUserPrincipal().getName() + "@" + providerName + ":" + segmentName + "</description>\n" +
                        "    <link>" + XMLEncoder.encode(UserHomes.getInstance().getWebAccessUrl(userHome)) + "</link>\n" +
                        "    <pubDate>" + dateTimeFormat.format(new Date()) + "</pubDate>\n" +
                        "    <lastBuildDate>" + (alerts.isEmpty() ? new Date() : alerts.iterator().next().getModifiedAt()) + "</lastBuildDate>\n"
                    );     
                    int count = 0;
                    for(org.opencrx.kernel.home1.jmi1.Alert alert: alerts) {
                    	ContextCapable reference = null;
                    	try {
                    		reference = alert.getReference();
                    	}
                    	catch(Exception e) {}
                    	if(reference != null) {
	                        String title = Notifications.getInstance().getNotificationSubject(
	                            pm, 
	                            alert, 
	                            userHome, 
	                            new HashMap<String,Object>(),
	                            true // useSendMailSubjectPrefix
	                        );
	                        String text = Notifications.getInstance().getNotificationText(
	                            pm, 
	                            alert, 
	                            null, 
	                            userHome, 
	                            new HashMap<String,Object>()
	                        );
	                        Action selectReferencedObjectAction = 
	                            new Action(
	                                Action.EVENT_SELECT_OBJECT, 
	                                new Action.Parameter[]{
	                                    new Action.Parameter(Action.PARAMETER_OBJECTXRI, reference.refGetPath().toXri())
	                                },
	                                "",
	                                true
	                            );         
	                        String linkReferencedObject = 
	                            UserHomes.getInstance().getWebAccessUrl(userHome) + 
	                            "?event=" + Action.EVENT_SELECT_OBJECT + 
	                            "&parameter=" + selectReferencedObjectAction.getParameter();
	                        Action selectAlertAction = 
	                            new Action(
	                                Action.EVENT_SELECT_OBJECT, 
	                                new Action.Parameter[]{
	                                    new Action.Parameter(Action.PARAMETER_OBJECTXRI, alert.refGetPath().toXri())
	                                },
	                                "",
	                                true
	                            );         
	                        String linkAlert = 
	                            UserHomes.getInstance().getWebAccessUrl(userHome) + 
	                            "?event=" + Action.EVENT_SELECT_OBJECT + 
	                            "&parameter=" + selectAlertAction.getParameter();
	                        p.write(
	                            "    <item>\n" +
	                            "      <title>" + XMLEncoder.encode(title) + "</title>\n" +
	                            "      <link>" + XMLEncoder.encode(linkReferencedObject) + "</link>\n" +
	                            "      <guid>" + XMLEncoder.encode(linkAlert) + "</guid>\n" +
	                            "      <category>Alerts</category>\n" +
	                            "      <pubDate>" + dateTimeFormat.format(alert.getModifiedAt()) + "</pubDate>\n" +
	                            "      <description>" + XMLEncoder.encode(text.replace("\n", "<br />")) + "</description>\n" +
	                            "    </item>\n"
	                        );
	                        count++;
	                        if(count > max) break;
                    	}
                    }
                    p.write("  </channel>\n");
                    p.write("</rss>\n");
                    p.flush();
                }
                else {
                    super.doGet(req, resp);                    
                }
            }
            catch(Exception e) {
                new ServiceException(e).log();
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);                
            }
        }    
        else {
            super.doGet(req, resp);
        }
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final long serialVersionUID = -7180734151023143956L;
    
    protected final static String CONFIGURATION_ID = "NewsServlet";    
    protected final static String RESOURCE_TYPE_RSS = "rss";
    protected final static String PARAMETER_NAME_TYPE = "type";
    
    protected static final int MAX_NEWS = 20;
    
    protected PersistenceManagerFactory persistenceManagerFactory = null;
    protected PersistenceManager rootPm = null;
    protected org.opencrx.kernel.admin1.jmi1.ComponentConfiguration componentConfiguration = null;    
        
}
