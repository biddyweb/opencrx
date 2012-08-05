/*
 * ====================================================================
 * Project:     openCRX/Groupware, http://www.opencrx.org/
 * Name:        $Id: FreeBusyServlet.java,v 1.6 2008/09/04 21:53:22 wfro Exp $
 * Description: FreeBusyServlet
 * Revision:    $Revision: 1.6 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/09/04 21:53:22 $
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
package org.opencrx.application.ical;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.backend.ICalendar;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.utils.ActivitiesHelper;
import org.opencrx.kernel.utils.ComponentConfigHelper;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.id.UUIDs;

public class FreeBusyServlet extends HttpServlet {

    //-----------------------------------------------------------------------
    protected static class RRule {
    
        public void parse(
            String rrule
        ) {
            if(rrule.startsWith("RRULE:")) {
                int end = 6;
                while((end < rrule.length()) && !Character.isWhitespace(rrule.charAt(end))) {
                    end++;                    
                }
                String[] attrs = rrule.substring(6, end).split(";");
                for(int i = 0; i < attrs.length; i++) {
                    if(attrs[i].startsWith("FREQ=")) {                        
                        this.freq = attrs[i].substring(5);
                    }
                    else if(attrs[i].startsWith("COUNT=")) {                        
                        this.count = Integer.valueOf(attrs[i].substring(6));
                    }
                    else if(attrs[i].startsWith("INTERVAL=")) {          
                        this.interval = Integer.valueOf(attrs[i].substring(9));                        
                    }
                }
            }
        }
        
        public String getFreq(
        ) {
            return this.freq;
        }
        
        public int getCount(
        ) {
            return this.count;
        }
        
        public int getInterval(
        ) {
            return this.interval;            
        }
        
        private String freq = "DAILY";
        private int count = 1;
        private int interval = 1;
    }
    
    //-----------------------------------------------------------------------
    @Override
    public void init(
        ServletConfig config            
    ) throws ServletException {
        super.init();        
        if(this.persistenceManagerFactory == null) {                    
            try {
                Utils.createModel();
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
        return this.persistenceManagerFactory.getPersistenceManager(
            req.getUserPrincipal() == null ? "guest" : req.getUserPrincipal().getName(),
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
    protected ActivitiesHelper getActivitiesHelper(
        PersistenceManager pm,
        String filterId,
        String isDisabledFilter
    ) {
        ActivitiesHelper activitiesHelper = new ActivitiesHelper(pm);
        if(filterId != null) {
            try {
                activitiesHelper.parseFilteredActivitiesUri(                        
                    (filterId.startsWith("/") ? "" : "/") + filterId
                );
                activitiesHelper.parseDisabledFilter(
                   isDisabledFilter
                );
            }
            catch(Exception  e) {}
        }        
        return activitiesHelper;
    }
    
    //-----------------------------------------------------------------------
    @Override
    protected void doGet(
        HttpServletRequest req, 
        HttpServletResponse resp
    ) throws ServletException, IOException {
        PersistenceManager pm = this.getPersistenceManager(req);
        String filterId = req.getParameter(PARAMETER_NAME_ID);
        String isDisabledFilter = req.getParameter(PARAMETER_NAME_DISABLED);
        ActivitiesHelper activitiesHelper = this.getActivitiesHelper(
            pm, 
            filterId,
            isDisabledFilter
        );
        // Return all activities in FreeBusy format
        if(RESOURCE_NAME_FREEBUSY.equals(req.getParameter(PARAMETER_NAME_RESOURCE))) {            
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setCharacterEncoding("UTF-8");                
            PrintWriter p = resp.getWriter();
            p.write("BEGIN:VCALENDAR\n");
            p.write("VERSION:2.0\n");
            p.write("PRODID:-" + ICalendar.PROD_ID + "\n");
            p.write("METHOD:PUBLISH\n");
            p.write("BEGIN:VFREEBUSY\n");
            p.write("DTSTAMP:" + ActivitiesHelper.formatDate(ActivitiesHelper.getActivityGroupModifiedAt(activitiesHelper.getActivityGroup())) + "\n");
            ActivityQuery activityQuery = Utils.getActivityPackage(pm).createActivityQuery();
            Date dtStart = new Date(System.currentTimeMillis() - 7*86400000L);
            Date dtEnd = new Date(System.currentTimeMillis() + 60*86400000L);
            if(activitiesHelper.isDisabledFilter()) {
                activityQuery.thereExistsDisabled().isTrue();                    
            }
            else {
                activityQuery.forAllDisabled().isFalse();                    
            }
            activityQuery.ical().isNonNull();
            activityQuery.orderByScheduledStart();
            p.write("DTSTART:" + ActivitiesHelper.formatDate(dtStart) + "\n");
            p.write("DTEND:" + ActivitiesHelper.formatDate(dtEnd) + "\n");
            for(Activity activity: activitiesHelper.getFilteredActivities(activityQuery)) {
                if((activity.getScheduledStart() != null) && (activity.getScheduledEnd() != null)) {
                    if(
                        (activity.getScheduledEnd().compareTo(dtStart) >= 0) && 
                        (activity.getScheduledStart().compareTo(dtEnd) <= 0)
                    ) {
                        String ical = activity.getIcal();
                        RRule rrule = new RRule();
                        if((ical != null) && (ical.indexOf("RRULE:") > 0)) {
                            rrule.parse(ical.substring(ical.indexOf("RRULE:")));
                        }
                        GregorianCalendar scheduledStart = new GregorianCalendar();
                        scheduledStart.setTime(activity.getScheduledStart());
                        GregorianCalendar scheduledEnd = new GregorianCalendar();
                        scheduledEnd.setTime(activity.getScheduledEnd());
                        int i = 0;
                        while(
                            (i < rrule.getCount()) &&
                            (scheduledEnd.getTime().compareTo(dtStart) >= 0) &&
                            (scheduledStart.getTime().compareTo(dtEnd) <= 0)                            
                        ) {
                            p.write("FREEBUSY:" + ActivitiesHelper.formatDate(scheduledStart.getTime()) + "/" + ActivitiesHelper.formatDate(scheduledEnd.getTime()) + "\n");
                            if("DAILY".equals(rrule.getFreq())) {
                                scheduledStart.add(GregorianCalendar.DAY_OF_MONTH, rrule.getInterval());
                                scheduledEnd.add(GregorianCalendar.DAY_OF_MONTH, rrule.getInterval());
                            }
                            else if("WEEKLY".equals(rrule.getFreq())) {
                                scheduledStart.add(GregorianCalendar.WEEK_OF_YEAR, rrule.getInterval());
                                scheduledEnd.add(GregorianCalendar.WEEK_OF_YEAR, rrule.getInterval());                                    
                            }
                            else if("MONTHLY".equals(rrule.getFreq())) {
                                scheduledStart.add(GregorianCalendar.MONTH, rrule.getInterval());
                                scheduledEnd.add(GregorianCalendar.MONTH, rrule.getInterval());                                                                        
                            }
                            else if("YEARLY".equals(rrule.getFreq())) {
                                scheduledStart.add(GregorianCalendar.YEAR, rrule.getInterval());
                                scheduledEnd.add(GregorianCalendar.YEAR, rrule.getInterval());                                                                                                            
                            }
                            i++;
                        }
                    }
                }
            }
            p.write("END:VFREEBUSY\n");
            p.write("END:VCALENDAR\n");
            p.flush();
        }
        else {
            super.doGet(req, resp);                
        }
        try {
            pm.close();            
        } catch(Exception e) {}
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final long serialVersionUID = 4746783518992145105L;
    protected final static String RESOURCE_NAME_FREEBUSY = "freebusy.ics";
    protected final static String CONFIGURATION_ID = "ICalServlet";

    protected final static String PARAMETER_NAME_ID = "id";
    protected final static String PARAMETER_NAME_DISABLED = "disabled";
    protected final static String PARAMETER_NAME_USER_LOCALE = "user.locale";
    protected final static String PARAMETER_NAME_USER_TZ = "user.tz";
    protected final static String PARAMETER_NAME_TYPE = "type";
    protected final static String PARAMETER_NAME_RESOURCE = "resource";
    protected final static String PARAMETER_NAME_HEIGHT = "height";
    
    protected PersistenceManagerFactory persistenceManagerFactory = null;
    protected PersistenceManager rootPm = null;
    protected org.opencrx.kernel.admin1.jmi1.ComponentConfiguration componentConfiguration = null;    
    
}
