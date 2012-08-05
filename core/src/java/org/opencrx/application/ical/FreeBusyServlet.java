/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: FreeBusyServlet.java,v 1.17 2010/08/27 08:56:46 wfro Exp $
 * Description: FreeBusyServlet
 * Revision:    $Revision: 1.17 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/08/27 08:56:46 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2010, CRIXP Corp., Switzerland
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
import java.text.ParseException;
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
import org.opencrx.kernel.activity1.jmi1.Absence;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ExternalActivity;
import org.opencrx.kernel.activity1.jmi1.Meeting;
import org.opencrx.kernel.activity1.jmi1.PhoneCall;
import org.opencrx.kernel.backend.ICalendar;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.utils.ActivitiesFilterHelper;
import org.opencrx.kernel.utils.ComponentConfigHelper;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.persistence.cci.PersistenceHelper;
import org.openmdx.kernel.id.UUIDs;
import org.w3c.format.DateTimeFormat;

public class FreeBusyServlet extends HttpServlet {

    //-----------------------------------------------------------------------
    protected static class RRule {
    
        //-------------------------------------------------------------------------
        protected Date getUtcDate(
            String dateTime
        ) throws ParseException {
            Date date = null;
            if(dateTime.endsWith("Z")) {
                if(dateTime.length() == 16) {
                    date = DateTimeFormat.BASIC_UTC_FORMAT.parse(dateTime.substring(0, 15) + ".000Z");
                }
                else {
                    date = DateTimeFormat.BASIC_UTC_FORMAT.parse(dateTime);
                }
            }
            else if(dateTime.length() == 8) {
                date = DateTimeFormat.BASIC_UTC_FORMAT.parse(dateTime + "T000000.000Z");
            }
            return date;
        }
                	
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
                    else if(attrs[i].startsWith("UNTIL=")) {
                    	try {
                    		this.until = getUtcDate(attrs[i].substring(6));
                    	} catch(Exception e) {}
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
        
        public Date getUntil(
        ) {
        	return this.until;
        }
        
        private String freq = "DAILY";
        private int count = 1;
        private int interval = 1;
        private Date until = null;
    }
    
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
    protected ActivitiesFilterHelper getActivitiesHelper(
        PersistenceManager pm,
        String filterId,
        String isDisabledFilter
    ) {
        ActivitiesFilterHelper activitiesHelper = new ActivitiesFilterHelper(pm);
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
        PersistenceManager pm = this.persistenceManagerFactory.getPersistenceManager(
            "guest",
            null
        );
        String filterId = req.getParameter(PARAMETER_NAME_ID);
        String isDisabledFilter = req.getParameter(PARAMETER_NAME_DISABLED);
        ActivitiesFilterHelper activitiesHelper = this.getActivitiesHelper(
            pm, 
            filterId,
            isDisabledFilter
        );
        if(activitiesHelper.getUserHome() != null) {
        	// Switch to user home's principal
        	pm = this.persistenceManagerFactory.getPersistenceManager(
        		activitiesHelper.getUserHome().refGetPath().getBase(),
        		null
        	);
        	activitiesHelper = this.getActivitiesHelper(
                pm, 
                filterId,
                isDisabledFilter
            );        	
        }
        // Return all activities in FreeBusy format
        if((req.getRequestURI().endsWith("/freebusy"))) {        	            
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setCharacterEncoding("UTF-8");    
            resp.setContentType("text/plain");
            PrintWriter p = resp.getWriter();
            p.write("BEGIN:VCALENDAR\n");
            p.write("PRODID:-" + ICalendar.PROD_ID + "\n");
            p.write("VERSION:1.1\n");
            p.write("METHOD:PUBLISH\n");
            p.write("BEGIN:VFREEBUSY\n");
            p.write("ORGANIZER:" + activitiesHelper.getCalendarName() + "\n");
            ActivityQuery activityQuery = (ActivityQuery)pm.newQuery(Activity.class);
            PersistenceHelper.setClasses(activityQuery, Meeting.class, PhoneCall.class, Absence.class, ExternalActivity.class);
            Date dtStart = new Date(System.currentTimeMillis() - 7*86400000L);
            Date dtEnd = new Date(System.currentTimeMillis() + 60*86400000L);
            if(activitiesHelper.isDisabledFilter()) {
                activityQuery.thereExistsDisabled().isTrue();                    
            }
            else {
                activityQuery.forAllDisabled().isFalse();                    
            }
            activityQuery.ical().isNonNull();
            activityQuery.thereExistsScheduledStart().lessThanOrEqualTo(dtEnd);
            activityQuery.thereExistsScheduledEnd().greaterThanOrEqualTo(dtStart);
            activityQuery.orderByScheduledStart().ascending();       
            p.write("DTSTAMP:" + ActivitiesFilterHelper.formatDate(ActivitiesFilterHelper.getActivityGroupModifiedAt(activitiesHelper.getActivityGroup())) + "\n");
            p.write("DTSTART:" + ActivitiesFilterHelper.formatDate(dtStart) + "\n");
            p.write("DTEND:" + ActivitiesFilterHelper.formatDate(dtEnd) + "\n");
            for(Activity activity: activitiesHelper.getFilteredActivities(activityQuery)) {
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
                	(rrule.getUntil() != null && scheduledStart.getTime().compareTo(rrule.getUntil()) <= 0) ||
                    (i < rrule.getCount())
                ) {
                    p.write("FREEBUSY:" + ActivitiesFilterHelper.formatDate(scheduledStart.getTime()) + "/" + ActivitiesFilterHelper.formatDate(scheduledEnd.getTime()) + "\n");
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
            p.write("END:VFREEBUSY\n");
            p.write("END:VCALENDAR\n");
            p.flush();
        }
        else {
            super.doGet(req, resp);                
        }
        try {
            pm.close();            
        } 
        catch(Exception e) {}
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final long serialVersionUID = 4746783518992145105L;
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
