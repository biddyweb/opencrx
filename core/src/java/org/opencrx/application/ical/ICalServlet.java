/*
 * ====================================================================
 * Project:     openCRX/CalDAV, http://www.opencrx.org/
 * Name:        $Id: ICalServlet.java,v 1.26 2009/06/09 14:10:35 wfro Exp $
 * Description: ICalServlet
 * Revision:    $Revision: 1.26 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/06/09 14:10:35 $
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
package org.opencrx.application.ical;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.Activity1Package;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.ActivityGroup;
import org.opencrx.kernel.activity1.jmi1.NewActivityParams;
import org.opencrx.kernel.activity1.jmi1.NewActivityResult;
import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.backend.ICalendar;
import org.opencrx.kernel.base.jmi1.ImportParams;
import org.opencrx.kernel.utils.ActivitiesHelper;
import org.opencrx.kernel.utils.ComponentConfigHelper;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.conversion.XMLEncoder;
import org.openmdx.portal.servlet.Action;
import org.openmdx.portal.servlet.WebKeys;

public class ICalServlet extends FreeBusyServlet {

    //-----------------------------------------------------------------------
    protected Activity findActivity(
        PersistenceManager pm,
        ActivitiesHelper activitiesHelper,
        String calUid
    ) {
        Activity1Package activityPkg = Utils.getActivityPackage(pm);
        ActivityQuery query = activityPkg.createActivityQuery();
        query.thereExistsExternalLink().equalTo(ICalendar.ICAL_SCHEMA + calUid);
        List<Activity> activities = activitiesHelper.getActivitySegment().getActivity(query);
        if(activities.isEmpty()) {
            query = activityPkg.createActivityQuery();
            query.thereExistsExternalLink().equalTo(ICalendar.ICAL_SCHEMA + calUid.replace('.', '+'));
            activities = activitiesHelper.getActivitySegment().getActivity(query);
            if(activities.isEmpty()) {
                return null;
            }
            else {
                if(activities.size() > 1) {
                    AppLog.warning("Duplicate activities. Will not update", activities.iterator().next().refMofId());
                    return null;
                }
                else {
                    return activities.iterator().next();
                }
            }
        }
        else {
            if(activities.size() > 1) {
                AppLog.warning("Duplicate activities. Will not update", activities.iterator().next().refMofId());
                return null;
            }
            else {
                return activities.iterator().next();
            }
        }
    }
        
    //-----------------------------------------------------------------------
    protected String getActivityUrl(        
        HttpServletRequest req,
        Activity activity,
        boolean htmlEncoded
    ) {
        Action selectActivityAction = 
            new Action(
                Action.EVENT_SELECT_OBJECT, 
                new Action.Parameter[]{
                    new Action.Parameter(Action.PARAMETER_OBJECTXRI, activity.refMofId())
                },
                "",
                true
            );        
        return htmlEncoded ? 
            req.getContextPath().replace("-ical-", "-core-") +  "/" + WebKeys.SERVLET_NAME + "?event=" + Action.EVENT_SELECT_OBJECT + "&amp;parameter=" + selectActivityAction.getParameterEncoded() : 
            req.getContextPath().replace("-ical-", "-core-") +  "/" + WebKeys.SERVLET_NAME + "?event=" + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectActivityAction.getParameter();
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
        String filterId = req.getParameter(PARAMETER_NAME_ID);
        String isDisabledFilter = req.getParameter(PARAMETER_NAME_DISABLED);
        ActivitiesHelper activitiesHelper = this.getActivitiesHelper(
            pm, 
            filterId,
            isDisabledFilter
        );
        if(activitiesHelper.getActivitySegment() != null) {
            // Locale
            String loc = req.getParameter(PARAMETER_NAME_USER_LOCALE);
            Locale locale = loc == null ? 
                Locale.getDefault() : 
                new Locale(loc.substring(0, 2), loc.substring(3, 5));       
            // Time zone
            String tz = req.getParameter(PARAMETER_NAME_USER_TZ);
            tz = tz == null ? TimeZone.getDefault().getID() : tz;
            TimeZone timeZone = TimeZone.getTimeZone(tz);
            // Date formatter
            SimpleDateFormat dateFormatLocale = (SimpleDateFormat)SimpleDateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, 
                DateFormat.LONG, 
                locale
            );
            dateFormatLocale.applyPattern("MMM dd yyyy HH:mm:ss '" + tz + "'");
            dateFormatLocale.setTimeZone(timeZone);        
            SimpleDateFormat dateFormatEnUs = (SimpleDateFormat)SimpleDateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, 
                DateFormat.LONG, 
                new Locale("en", "US")
            );
            int offsetHours = ((timeZone.getRawOffset() + timeZone.getDSTSavings()) / 3600000);                
            dateFormatEnUs.applyLocalizedPattern("MMM dd, yyyy hh:mm:ss a 'GMT" + (offsetHours >= 0 ? "+" : "-") + offsetHours + ":00'");
            dateFormatEnUs.setTimeZone(timeZone);
            org.opencrx.kernel.admin1.jmi1.ComponentConfiguration componentConfiguration = 
                this.getComponentConfiguration(
                    activitiesHelper.getActivitySegment().refGetPath().get(2)
                );
            String maxActivitiesValue = componentConfiguration == null ? 
                null : 
                ComponentConfigHelper.getComponentConfigProperty("maxActivities", componentConfiguration).getStringValue();
            int maxActivities = Integer.valueOf(
                maxActivitiesValue == null ? 
                    "500" : 
                    maxActivitiesValue
            ).intValue();
            // Return all activities in ICS format
            if(
                RESOURCE_TYPE_ICS.equals(req.getParameter(PARAMETER_NAME_TYPE)) ||
                RESOURCE_NAME_ACTIVITIES_ICS.equals(req.getParameter(PARAMETER_NAME_RESOURCE))
            ) {
                resp.setCharacterEncoding("UTF-8");
                resp.setStatus(HttpServletResponse.SC_OK);
                ActivityQuery activityQuery = Utils.getActivityPackage(pm).createActivityQuery();
                if(activitiesHelper.isDisabledFilter()) {
                    activityQuery.thereExistsDisabled().isTrue();                    
                }
                else {
                    activityQuery.forAllDisabled().isFalse();                    
                }
                activityQuery.ical().isNonNull();
                PrintWriter p = resp.getWriter();
                p.write("BEGIN:VCALENDAR\n");
                p.write("VERSION:2.0\n");
                p.write("PRODID:" + ICalendar.PROD_ID + "\n");
                p.write("CALSCALE:GREGORIAN\n");
                p.write("METHOD:PUBLISH\n");
                
                // Event serving as calendar guard. Required to allow
                // creation of events in doPut
                p.write("BEGIN:VEVENT\n");
                p.write("UID:" + activitiesHelper.getFilteredActivitiesParentId() + "\n");
                p.write("CLASS:PUBLIC\n");
                p.write("DTSTART:19000101T000000Z\n");
                p.write("DTEND:19000101T000000Z\n");
                p.write("LAST-MODIFIED:19000101T000000Z\n");
                p.write("DTSTAMP:19000101T000000Z\n");
                p.write("SUMMARY:" + filterId + "\n");
                p.write("END:VEVENT\n");
                int n = 0;
                for(Activity activity: activitiesHelper.getFilteredActivities(activityQuery)) {
                    String ical = activity.getIcal();
                    if(ical.indexOf("BEGIN:VEVENT") >= 0) {
                        int start = ical.indexOf("BEGIN:VEVENT");
                        int end = ical.indexOf("END:VEVENT");
                        String vevent = ical.substring(start, end).replace("BEGIN:VEVENTBEGIN:VCALENDAR", "BEGIN:VEVENT");
                        p.write(vevent);
                        if(vevent.indexOf("URL:") < 0) {
                            p.write("URL:" + req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + this.getActivityUrl(req, activity, false) + "\n");
                        }
                        p.write("END:VEVENT\n");
                    }
                    else if(ical.indexOf("BEGIN:VTODO") >= 0) {
                        int start = ical.indexOf("BEGIN:VTODO");
                        int end = ical.indexOf("END:VTODO");
                        String vtodo = ical.substring(start, end).replace("BEGIN:VTODOBEGIN:VCALENDAR", "BEGIN:VTODO");
                        p.write(vtodo);
                        if(vtodo.indexOf("URL:") < 0) {
                            p.write("URL:" + req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + this.getActivityUrl(req, activity, false) + "\n");
                        }
                        p.write("END:VTODO\n");                        
                    }
                    n++;
                    if(n % 50 == 0) pm.evictAll();                
                    if(n > maxActivities) break;
                }
                p.write("END:VCALENDAR\n");
                p.flush();
            }
            // Return all activities in XML format
            else if(
                RESOURCE_TYPE_XML.equals(req.getParameter(PARAMETER_NAME_TYPE)) ||
                RESOURCE_NAME_ACTIVITIES_XML.equals(req.getParameter(PARAMETER_NAME_RESOURCE))
            ) {
                resp.setCharacterEncoding("UTF-8");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("text/xml");
                ActivityQuery activityQuery = Utils.getActivityPackage(pm).createActivityQuery();
                if(activitiesHelper.isDisabledFilter()) {
                    activityQuery.thereExistsDisabled().isTrue();                    
                }
                else {
                    activityQuery.forAllDisabled().isFalse();                    
                }
                activityQuery.scheduledStart().isNonNull();
                PrintWriter p = resp.getWriter();
                p.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                p.write("<data>\n");
                int n = 0;
                for(Activity activity: activitiesHelper.getFilteredActivities(activityQuery)) {
                    p.write("  <event start=\"" + dateFormatEnUs.format(activity.getScheduledStart()) + "\" end=\"" + dateFormatEnUs.format(activity.getScheduledEnd() == null ? activity.getScheduledStart() : activity.getScheduledEnd()) + "\" link=\"" + this.getActivityUrl(req, activity, true) + "\" title=\"" + XMLEncoder.encode((activity.getActivityNumber() == null ? "" : activity.getActivityNumber().trim() + ": " ) + activity.getName()) + "\">\n");
                    String description = (activity.getDescription() == null) || (activity.getDescription().trim().length() == 0) ? 
                        activity.getName() : 
                        activity.getDescription();
                    p.write(XMLEncoder.encode(
                        description == null ? "" : description
                    ));
                    p.write("  </event>\n");
                    n++;
                    if(n % 50 == 0) pm.evictAll();
                    if(n > maxActivities) break;
                }
                p.write("</data>\n");
                p.flush();
            }
            else if(
                RESOURCE_TYPE_HTML.equals(req.getParameter(PARAMETER_NAME_TYPE)) ||
                RESOURCE_NAME_ACTIVITIES_HTML.equals(req.getParameter(PARAMETER_NAME_RESOURCE))
            ) {        
                int height = req.getParameter(PARAMETER_NAME_HEIGHT) == null
                    ? 500
                    : Integer.valueOf(req.getParameter(PARAMETER_NAME_HEIGHT));
                resp.setCharacterEncoding("UTF-8");
                resp.setStatus(HttpServletResponse.SC_OK);
                PrintWriter p = resp.getWriter();
                p.write("<!--[if IE]><!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><![endif]-->\n");
                p.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
                p.write("<html>\n");
                p.write("<head>\n");
                p.write("       <style>\n");
                p.write("           .timeline-band {\n");
                p.write("               font-family: Trebuchet MS, Helvetica, Arial, sans serif;\n");
                p.write("               font-size: 9pt;\n");
                p.write("               border: 1px solid #aaa;\n");
                p.write("           }\n");
                p.write("       </style>\n");
                p.write("       <script type=\"text/javascript\" src=\"javascript/timeline/api/timeline-api.js\"></script>\n");
                p.write("       <script language=\"javascript\" type=\"text/javascript\">\n");               
                p.write("           var tl;\n");
                p.write("           function pageinit() {\n");
                p.write("             var eventSource = new Timeline.DefaultEventSource();\n");          
                p.write("             var bandInfos = [\n");
                p.write("               Timeline.createHotZoneBandInfo({\n");
                p.write("                   zones: [\n");
                p.write("                       {   start:    \"Jan 01 1960 00:00:00\",\n");
                p.write("                           end:      \"Dec 31 2050 00:00:00\",\n");
                p.write("                           magnify:  30,\n");
                p.write("                           unit:     Timeline.DateTime.DAY\n");
                p.write("                       }\n");
                p.write("                   ],\n");
                p.write("                   timeZone: " + offsetHours + ",\n");
                p.write("                   eventSource: eventSource,\n");
                p.write("                   date: \"" + dateFormatEnUs.format(new Date()) + "\",\n");               
                p.write("                   width: \"70%\",\n");
                p.write("                   intervalUnit: Timeline.DateTime.MONTH,\n"); 
                p.write("                   intervalPixels: 100\n");
                p.write("               }),\n");
                p.write("               Timeline.createHotZoneBandInfo({\n");
                p.write("                   zones: [\n");
                p.write("                       {   start:    \"Jan 01 1960 00:00:00\",\n");
                p.write("                           end:      \"Dec 31 2050 00:00:00\",\n");
                p.write("                           magnify:  10,\n");
                p.write("                           unit:     Timeline.DateTime.MONTH\n");
                p.write("                       }\n");
                p.write("                   ],\n");
                p.write("                   timeZone: " + offsetHours + ",\n");
                p.write("                   showEventText: false,\n");
                p.write("                   trackHeight: 0.5,\n");
                p.write("                   trackGap: 0.2,\n");        
                p.write("                   eventSource: eventSource,\n");
                p.write("                   date: \"" + dateFormatEnUs.format(new Date()) + "\",\n");               
                p.write("                   width: \"30%\",\n"); 
                p.write("                   intervalUnit: Timeline.DateTime.YEAR,\n"); 
                p.write("                   intervalPixels: 200\n");
                p.write("               })\n");
                p.write("             ];\n");
                p.write("           bandInfos[1].syncWith = 0;\n");
                p.write("           bandInfos[1].highlight = true;\n");                    
                p.write("           bandInfos[1].eventPainter.setLayout(bandInfos[0].eventPainter.getLayout());\n");           
                p.write("\n");
                p.write("           var theme = Timeline.ClassicTheme.create();\n");
                p.write("           theme.event.label.width = 250; // px\n");
                p.write("           theme.event.bubble.width = 250;\n");
                p.write("           theme.event.bubble.height = 200;\n");
                p.write("\n");
                p.write("           tl = Timeline.create(document.getElementById(\"my-timeline\"), bandInfos);\n");
                String dataUrl = req.getContextPath() + req.getServletPath() + "?" + req.getQueryString().replace("html", "xml");
                p.write("           Timeline.loadXML(\"" + dataUrl + "\", function(xml, url) { eventSource.loadXML(xml, url); });\n");            
                p.write("       }\n");
                p.write("\n");
                p.write("           var resizeTimerID = null;\n");
                p.write("           function pageresize() {\n");
                p.write("               if (resizeTimerID == null) {\n");
                p.write("                   resizeTimerID = window.setTimeout(function() {\n");
                p.write("                       resizeTimerID = null;\n");
                p.write("                       tl.layout();\n");
                p.write("                   }, 500);\n");
                p.write("               }\n");
                p.write("           }\n");
                p.write("       </script>\n");       
                p.write("   </head>\n");
                String actionUrl = req.getContextPath() + req.getServletPath();
                p.write("   <body onload=\"javascript:pageinit();\" onresize=\"javascript:pageresize();\">\n");
                p.write("     <form name=\"Timeline\" id=\"Timeline\" action=\"" + actionUrl + "\" width=\"100%\" style=\"margin-bottom:5px;\">\n");
                p.write("       <table>\n");
                p.write("         <tr>\n");
                p.write("           <td width=\"95%\">\n");
                p.write("             <input type=\"hidden\" name=\"id\" value=\"" + req.getParameter(PARAMETER_NAME_ID) + "\">\n");
                if(req.getParameter(PARAMETER_NAME_RESOURCE) != null) {
                    p.write("             <input type=\"hidden\" name=\"resource\" value=\"" + req.getParameter(PARAMETER_NAME_RESOURCE) + "\">\n");
                }
                if(req.getParameter(PARAMETER_NAME_TYPE) != null) {
                    p.write("             <input type=\"hidden\" name=\"type\" value=\"" + req.getParameter(PARAMETER_NAME_TYPE) + "\">\n");
                }
                p.write("             <input type=\"hidden\" name=\"user.tz\" id=\"user.tz\" value=\"" + tz + "\">\n");
                p.write("             <input type=\"hidden\" name=\"user.locale\" id=\"user.locale\" value=\"" + (locale.getLanguage() + "_" + locale.getCountry()) + "\">\n");
                p.write("             <input type=\"hidden\" name=\"height\" id=\"height\" value=\"" + (Math.max(500, (height + 200) % 1300)) + "\">\n");
                p.write("             <input type=\"image\" src=\"images/magnify.gif\" alt=\"\" border=\"0\" align=\"absbottom\" />");
                p.write("           </td>\n");
                p.write("           <td>" + dateFormatLocale.format(new Date()).replace(" ", "&nbsp;") + "</td>\n");
                p.write("         </tr>\n");
                p.write("       </table>\n");
                p.write("     </form>\n");
                p.write("     <div id=\"my-timeline\" style=\"height: " + height + "px; border: 1px solid #aaa\" />\n");
                p.write("   </body>\n");
                p.write("</html>\n");
                p.flush();
            }            
            else {
                super.doGet(req, resp);
            }
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
    protected ActivityCreator findActivityCreator(
        Collection<ActivityCreator> activityCreators,
        short activityClass
    ) {
        for(ActivityCreator creator: activityCreators) {
            if(
                (creator.getActivityType() != null) && 
                (creator.getActivityType().getActivityClass() == activityClass)
            ) {
                return creator;
            }
        }
        return null;
    }

    //-----------------------------------------------------------------------
    @Override
    protected void doPut(
        HttpServletRequest req, 
        HttpServletResponse resp
    ) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        PersistenceManager pm = this.getPersistenceManager(req);
        if(pm == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }        
        String filterId = req.getParameter(PARAMETER_NAME_ID);
        String isDisabledFilter = req.getParameter(PARAMETER_NAME_DISABLED);        
        ActivitiesHelper activitiesHelper = this.getActivitiesHelper(
            pm, 
            filterId,
            isDisabledFilter
        );
        if(
            RESOURCE_NAME_ACTIVITIES_ICS.equals(req.getParameter(PARAMETER_NAME_RESOURCE)) ||
            RESOURCE_TYPE_ICS.equals(req.getParameter(PARAMETER_NAME_TYPE))
        ) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setCharacterEncoding("UTF-8");
            // Required by Sunbird 0.9+
            resp.setHeader("ETag", Long.toString(System.currentTimeMillis()));
            BufferedReader reader = new BufferedReader(req.getReader());
            boolean allowCreation = false;
            String l = null;
            while((l = reader.readLine()) != null) {
                boolean isEvent = l.startsWith("BEGIN:VEVENT");
                boolean isTodo = l.startsWith("BEGIN:VTODO");
                if(isEvent || isTodo) {
                    StringBuilder calendar = new StringBuilder();
                    calendar.append("BEGIN:VCALENDAR\n");
                    calendar.append("VERSION:2.0\n");
                    calendar.append("PRODID:-" + ICalendar.PROD_ID + "\n");
                    calendar.append(
                        isEvent ? 
                            new StringBuilder("BEGIN:VEVENT\n") : 
                            new StringBuilder("BEGIN:VTODO\n")
                    );
                    String uid = null;
                    String lastModified = null;
                    while((l = reader.readLine()) != null) {
                        calendar.append(l).append("\n");
                        if(l.startsWith("UID:")) {
                            uid = l.substring(4);
                        }
                        else if(l.startsWith("LAST-MODIFIED:")) {
                            lastModified = l.substring(14);
                        }
                        else if(l.startsWith("END:VEVENT") || l.startsWith("END:VTODO")) {
                            break;
                        }
                    }
                    calendar.append("END:VCALENDAR\n");
                    AppLog.trace("VCALENDAR", calendar);
                    if((uid != null) && (lastModified != null)) {
                        // Calendar contains guard VEVENT. Allow creation of new activities
                        if(uid.equals(activitiesHelper.getFilteredActivitiesParentId())) {
                            allowCreation = true;
                        }                        
                        else {
                            AppLog.detail("Lookup activity", uid);
                            Activity activity = this.findActivity(
                                pm,
                                activitiesHelper, 
                                uid
                            );
                            StringBuilder dummy = new StringBuilder();
                            Map<String,String> newICal = new HashMap<String,String>();
                            try {
                            	newICal = ICalendar.getInstance().parseICal(
	                                new BufferedReader(new StringReader(calendar.toString())),
	                                dummy 
	                            );
                            } catch(Exception e) {}
                            newICal.remove("LAST-MODIFIED");
                            newICal.remove("DTSTAMP");                               
                            newICal.remove("CREATED");                               
                            dummy.setLength(0);
                            Map<String,String> oldICal = null;
                            if(activity != null) {
                            	try {
	                                oldICal = ICalendar.getInstance().parseICal(
	                                    new BufferedReader(new StringReader(activity.getIcal())),
	                                    dummy
	                                );
                            	} 
                            	catch(Exception e) {}
                                oldICal.remove("LAST-MODIFIED");
                                oldICal.remove("DTSTAMP");                                   
                                oldICal.remove("CREATED");                                   
                                oldICal.keySet().retainAll(newICal.keySet());
                            }
                            ActivityGroup activityGroup = activitiesHelper.getActivityGroup();                            
                            // Update existing activity
                            if(
                                (activity != null) &&
                                !newICal.equals(oldICal)
                            ) {
                                try {
                                    pm.currentTransaction().begin();
                                    ImportParams importItemParams = Utils.getBasePackage(pm).createImportParams(
                                        calendar.toString().getBytes("UTF-8"), 
                                        ICalendar.MIME_TYPE, 
                                        "import.ics", 
                                        (short)0
                                    );
                                    activity.importItem(importItemParams);
                                    pm.currentTransaction().commit();
                                    pm.refresh(activity);
                                    pm.currentTransaction().begin();
                                    activity.setDisabled(
                                        Boolean.valueOf(activitiesHelper.isDisabledFilter())
                                    );
                                    pm.currentTransaction().commit();
                                }
                                catch(Exception e) {
                                    try {
                                        pm.currentTransaction().rollback();
                                    } 
                                    catch(Exception e0) {}                                    
                                }
                            }
                            // Create new activity
                            else if(
                                allowCreation && 
                                (activity == null) &&
                                (activityGroup != null)
                            ) {
                                Collection<ActivityCreator> activityCreators = activityGroup.getActivityCreator();
                                // Priority 1
                                ActivityCreator activityCreator = this.findActivityCreator(
                                    activityCreators,
                                    isTodo ? 
                                        Activities.ACTIVITY_CLASS_TASK : 
                                        isEvent ? 
                                            Activities.ACTIVITY_CLASS_MEETING : 
                                            Activities.ACTIVITY_CLASS_INCIDENT
                                );
                                // Priority 2
                                if(activityCreator == null) {
                                    activityCreator = this.findActivityCreator(
                                        activityCreators,
                                        isTodo ? 
                                            Activities.ACTIVITY_CLASS_MEETING : 
                                            isEvent ? 
                                                Activities.ACTIVITY_CLASS_INCIDENT : 
                                                Activities.ACTIVITY_CLASS_INCIDENT
                                    );                                    
                                }
                                // Priority 3
                                if(activityCreator == null) {
                                    activityCreator = this.findActivityCreator(
                                        activityCreators,
                                        isTodo ? 
                                            Activities.ACTIVITY_CLASS_INCIDENT : 
                                            isEvent ? 
                                                Activities.ACTIVITY_CLASS_INCIDENT : 
                                                Activities.ACTIVITY_CLASS_INCIDENT
                                    );                                    
                                }
                                if(activityCreator == null) {
                                    activityCreator = activitiesHelper.getActivityGroup().getDefaultCreator();
                                }
                                if(activityCreator != null) {
                                    try {
                                        String name = "NA";
                                        int posSummary;
                                        if((posSummary = calendar.indexOf("SUMMARY:")) > 0) {
                                            if(calendar.indexOf("\n", posSummary) > 0) {
                                                name = calendar.substring(posSummary + 8, calendar.indexOf("\n", posSummary));
                                            }
                                        }
                                        pm.currentTransaction().begin();
                                        NewActivityParams newActivityParams = Utils.getActivityPackage(pm).createNewActivityParams(
                                            null, // description
                                            null, // detailedDescription
                                            null, // dueBy
                                            isEvent ? 
                                                ICalendar.ICAL_TYPE_VEVENT : 
                                                isTodo ? 
                                                    ICalendar.ICAL_TYPE_VTODO : 
                                                    ICalendar.ICAL_TYPE_NA,
                                            name, // name
                                            (short)0, // priority
                                            null, // reportingContact
                                            null, // scheduledEnd
                                            null // scheduledStart
                                        );
                                        NewActivityResult result = activityCreator.newActivity(newActivityParams);
                                        pm.currentTransaction().commit();
                                        try {
                                            activity = (Activity)pm.getObjectById(result.getActivity().refGetPath());
                                            pm.currentTransaction().begin();
                                            ImportParams importItemParams = Utils.getBasePackage(pm).createImportParams(
                                                calendar.toString().getBytes("UTF-8"), 
                                                ICalendar.MIME_TYPE, 
                                                "import.ics", 
                                                (short)0
                                            );
                                            activity.importItem(importItemParams);
                                            pm.currentTransaction().commit();
                                            pm.refresh(activity);
                                            if(
                                                activitiesHelper.isDisabledFilter() &&
                                                ((activity.isDisabled() == null) || !activity.isDisabled().booleanValue())
                                            ) {
                                                pm.currentTransaction().begin();
                                                activity.setDisabled(Boolean.TRUE);
                                                pm.currentTransaction().commit();
                                            }                                            
                                        }
                                        catch(Exception e) {
                                            AppLog.warning("Error importing calendar. Reason is", new String[]{calendar.toString(), e.getMessage()});
                                            new ServiceException(e).log();
                                            try {
                                                pm.currentTransaction().rollback();
                                            } 
                                            catch(Exception e0) {}
                                        }
                                    }
                                    catch(Exception e) {
                                        AppLog.warning("Can not create activity. Reason is", e.getMessage());
                                        new ServiceException(e).log();
                                        try {
                                            pm.currentTransaction().rollback();
                                        } 
                                        catch(Exception e0) {}
                                    }
                                }
                                else {
                                    AppLog.detail("Skipping calendar. No activity creator found", calendar); 
                                }
                            }
                            else {
                                AppLog.detail(
                                    "Skipping ", 
                                    new String[]{
                                        "UID: " + uid, 
                                        "LAST-MODIFIED: " + lastModified, 
                                        "Activity.number: " + (activity == null ? null : activity.refMofId()),
                                        "Activity.modifiedAt:" + (activity == null ? null : activity.getModifiedAt())
                                    }
                                );
                            }
                        }
                    }
                    else {
                        AppLog.detail("Skipping", calendar); 
                    }
                }                    
            }
        }            
        else {
            super.doPut(req, resp);
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
    
    protected final static String RESOURCE_NAME_ACTIVITIES_ICS = "activities.ics";
    protected final static String RESOURCE_NAME_ACTIVITIES_HTML = "activities.html";
    protected final static String RESOURCE_NAME_ACTIVITIES_XML = "activities.xml";
    protected final static String RESOURCE_TYPE_ICS = "ics";
    protected final static String RESOURCE_TYPE_HTML = "html";
    protected final static String RESOURCE_TYPE_XML = "xml";
    
    protected static final int MAX_ACTIVITIES = 500;
    
    protected PersistenceManagerFactory persistenceManagerFactory = null;
    
}
