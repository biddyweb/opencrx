/*
 * ====================================================================
 * Project:     openCRX/CalDAV, http://www.opencrx.org/
 * Name:        $Id: ICalServlet.java,v 1.39 2009/09/18 12:25:56 wfro Exp $
 * Description: ICalServlet
 * Revision:    $Revision: 1.39 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/09/18 12:25:56 $
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.backend.ICalendar;
import org.opencrx.kernel.utils.AccountsHelper;
import org.opencrx.kernel.utils.ActivitiesHelper;
import org.opencrx.kernel.utils.ComponentConfigHelper;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.base.text.conversion.XMLEncoder;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.log.SysLog;
import org.openmdx.portal.servlet.Action;
import org.openmdx.portal.servlet.WebKeys;

public class ICalServlet extends FreeBusyServlet {

	//-----------------------------------------------------------------------
    @SuppressWarnings("deprecation")
    protected boolean hasBirthday(
    	org.opencrx.kernel.account1.jmi1.Contact contact,
		int year
    ) {
		return
			((contact.isDisabled() == null) || (!contact.isDisabled().booleanValue())) && // contact must not be disabled
			((contact.getBirthdate() != null) && (contact.getBirthdate().getYear() <= year)) && // born no later than year
			(true // not deceased
			/*
				(contact.getDeceasedDate() == null) ||
				(contact.getDeceasedDate().getYear() > year)) ||
				(	(contact.getDeceasedDate().getYear() == year) &&
					((contact.getDeceasedDate().getMonth() > contact.getBirthdate().getMonth()) ||
					(	(contact.getDeceasedDate().getMonth() == contact.getBirthdate().getMonth()) &&
						(contact.getDeceasedDate().getDate() >= contact.getBirthdate().getDate())
					)
					)
				)
			*/
			);
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
    protected String getAccountUrl(
	    HttpServletRequest req,
	    org.opencrx.kernel.account1.jmi1.Account account,
	    boolean htmlEncoded
 	) {
	    Action selectAccountAction =
	        new Action(
	            Action.EVENT_SELECT_OBJECT,
	            new Action.Parameter[]{
	                new Action.Parameter(Action.PARAMETER_OBJECTXRI, account.refMofId())
	            },
	            "",
	            true
	        );
	    return htmlEncoded ?
	        req.getContextPath().replace("-ical-", "-core-") +  "/" + WebKeys.SERVLET_NAME + "?event=" + Action.EVENT_SELECT_OBJECT + "&amp;parameter=" + selectAccountAction.getParameterEncoded() :
	        req.getContextPath().replace("-ical-", "-core-") +  "/" + WebKeys.SERVLET_NAME + "?event=" + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectAccountAction.getParameter();
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
    protected AccountsHelper getAccountsHelper(
        PersistenceManager pm,
        String filterId,
        String isDisabledFilter
    ) {
    	AccountsHelper accountsHelper = new AccountsHelper(pm);
        if(filterId != null) {
            try {
            	accountsHelper.parseFilteredAccountsUri(                        
                    (filterId.startsWith("/") ? "" : "/") + filterId
                );
            }
            catch(Exception  e) {}
        }        
        return accountsHelper;
    }
    
    //-----------------------------------------------------------------------
    protected void printICal(
    	Activity activity,
    	PrintWriter p,
    	HttpServletRequest req,
    	int index
    ) {
        String ical = activity.getIcal();
        ical = ical.replace("\r\n", "\n"); // Remove \r just in case
        if(ical.indexOf("BEGIN:VEVENT") >= 0) {
            int start = ical.indexOf("BEGIN:VEVENT");
            int end = ical.indexOf("END:VEVENT");
            String vevent = ical.substring(start, end).replace("BEGIN:VEVENTBEGIN:VCALENDAR", "BEGIN:VEVENT");
            p.write(vevent);
            SysLog.detail("VEVENT #", index);
            SysLog.detail(vevent);            
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
            SysLog.detail("VTODO #", index);
            SysLog.detail(vtodo);            
            if(vtodo.indexOf("URL:") < 0) {
                p.write("URL:" + req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + this.getActivityUrl(req, activity, false) + "\n");
            }
            p.write("END:VTODO\n");                        
        }        
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
        if(req.getRequestURI().endsWith("/ical") || req.getRequestURI().endsWith("/activities")) {        
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
	                	this.printICal(
	                		activity, 
	                		p, 
	                		req, 
	                		n
	                	);
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
	                p.write("       <script>");
	                p.write("         Timeline_ajax_url='javascript/timeline_ajax/simile-ajax-api.js';");
	                p.write("         Timeline_urlPrefix='javascript/timeline_js/';");   
	                p.write("         Timeline_parameters='bundle=true';");
	                p.write("       </script>");
	                p.write("       <script type=\"text/javascript\" src=\"javascript/timeline_js/timeline-api.js\"></script>\n");
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
	        }
            else {
                super.doGet(req, resp);
            }
        }
        else if(req.getRequestURI().endsWith("/bdays")) {
        	final int YEARS_BEFORE_SELECTED_YEAR = 1;
        	final int YEARS_AFTER_SELECTED_YEAR = 1;        	
	        AccountsHelper accountsHelper = this.getAccountsHelper(
	            pm, 
	            filterId,
	            isDisabledFilter
	        );
			// year
			GregorianCalendar now = new GregorianCalendar();
			int year = now.get(GregorianCalendar.YEAR);
			if(req.getParameter("year") != null) {
				try {
					year = Integer.parseInt(req.getParameter("year"));
				} catch (Exception e) {}
			}
			// alarm
			boolean createAlarm = req.getParameter("alarm") != null ?
				Boolean.valueOf(req.getParameter("alarm")) :
				req.getParameter("ALARM") != null ?
					Boolean.valueOf(req.getParameter("ALARM")) :
					false;
			// icalType
			short icalType = ICalendar.ICAL_TYPE_VEVENT;
			if ((req.getParameter("icalType") != null) && (req.getParameter("icalType").compareTo("VTODO") == 0)) {
				icalType = ICalendar.ICAL_TYPE_VTODO;
			}		
			// max
            int max = req.getParameter("max") != null ?
            	Integer.valueOf(req.getParameter("max")) :
            	500;
            // categories
            String categories = req.getParameter("categories") != null ?
            	req.getParameter("categories") :
            	"Birthday";
            // summaryPrefix
            String summaryPrefix = req.getParameter("summaryPrefix") != null ?
            	req.getParameter("summaryPrefix") :
            	"";
	        if(accountsHelper.getAccountSegment() != null) {
				SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(ICalendar.DATETIME_FORMAT);
				dateTimeFormatter.setLenient(false);
				GregorianCalendar latestBirthdate = new GregorianCalendar();
				latestBirthdate.add(GregorianCalendar.YEAR, YEARS_AFTER_SELECTED_YEAR + 1);
				latestBirthdate.set(GregorianCalendar.MONTH, 0);
				latestBirthdate.set(GregorianCalendar.DAY_OF_MONTH, 1);
				latestBirthdate.set(GregorianCalendar.HOUR_OF_DAY, 0);
				latestBirthdate.set(GregorianCalendar.MINUTE, 0);
				latestBirthdate.set(GregorianCalendar.SECOND, 0);
				latestBirthdate.set(GregorianCalendar.MILLISECOND, 0);
				GregorianCalendar earliestDeceasedDate = (GregorianCalendar)latestBirthdate.clone();
				earliestDeceasedDate.add(GregorianCalendar.YEAR, -(YEARS_BEFORE_SELECTED_YEAR + YEARS_AFTER_SELECTED_YEAR + 1));	        	
				org.opencrx.kernel.account1.cci2.ContactQuery contactFilter =
			        (org.opencrx.kernel.account1.cci2.ContactQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.Contact.class);
				contactFilter.forAllDisabled().isFalse();
				contactFilter.thereExistsBirthdate().lessThanOrEqualTo(latestBirthdate.getTime());	        
				//contactFilter.forAllDeceasedDate().greaterThanOrEqualTo(earliestDeceasedDate.getTime());				
	        	Collection<Account> contacts = accountsHelper.getFilteredAccounts(contactFilter);
                resp.setCharacterEncoding("UTF-8");
                resp.setStatus(HttpServletResponse.SC_OK);
                PrintWriter p = resp.getWriter();	    
                p.write("BEGIN:VCALENDAR\n");
                p.write("VERSION:2.0\n");
                p.write("PRODID:" + ICalendar.PROD_ID + "\n");
                p.write("CALSCALE:GREGORIAN\n");
                p.write("METHOD:PUBLISH\n");    
                int n = 0;
	        	for(Account account: contacts) {
	        		if(account instanceof Contact) {
	        			Contact contact = (Contact)account;
						for(
							int i = -YEARS_BEFORE_SELECTED_YEAR; 
							i <= YEARS_AFTER_SELECTED_YEAR; 
							i++
						) {			
							int currentYear = year + i;
							if(this.hasBirthday(contact, currentYear)) {
							    if(contact.getBirthdate() != null) {
								    UUID uid = null;
								    try {
								        uid = UUIDConversion.fromString(contact.refGetPath().getBase());
								    }
								    catch(Exception e) {
								        uid = UUIDs.getGenerator().next();
								    }
								    String lastModified = dateTimeFormatter.format(new Date());
								    String dtStart = null;
								    String dtEnd = null;
								    String dtDue = null;
								    String valarm = null;
								    String age = "";
								    String name = "";
								    try {
							  			name = contact.getFullName();
							    		GregorianCalendar date = new GregorianCalendar();
							    		date.setTime(contact.getBirthdate());
							    		age = Integer.toString(currentYear - date.get(GregorianCalendar.YEAR));
							    		//rrule = "RRULE:FREQ=YEARLY;COUNT=3;BYMONTHDAY=" + birthdate.get(GregorianCalendar.DAY_OF_MONTH) + ";BYMONTH=" + (birthdate.get(GregorianCalendar.MONTH) + 1);
							    		date.set(GregorianCalendar.YEAR, currentYear);
							    		dtStart = "DTSTART;VALUE=DATE:" + (dateTimeFormatter.format(date.getTime())).substring(0, 8);
							    		if (createAlarm) {
											GregorianCalendar yesterday = new GregorianCalendar();
									        yesterday.add(GregorianCalendar.DAY_OF_MONTH, -1);
											if(yesterday.compareTo(date) <= 0) {
												// generate VALARM
												valarm = "BEGIN:VALARM\n" +
										        "ACTION:DISPLAY\n" +
										        "DESCRIPTION:Birthday " + age + " coming up " + name + "\n" +
										        "TRIGGER;VALUE=DURATION:-P1D\n" +
										        "END:VALARM\n";
											}
							    		}
								        date.add(GregorianCalendar.DAY_OF_MONTH, 1);
								        dtEnd = "DTEND;VALUE=DATE:" + (dateTimeFormatter.format(date.getTime())).substring(0, 8);
								        dtDue = "DUE;VALUE=DATE:" + (dateTimeFormatter.format(date.getTime())).substring(0, 8);
								        String emailAddress = ICalendar.getInstance().getPrimaryEMailAddress(account);
								        String attendee = null;
								        if(emailAddress != null) {
											String fullName = contact.getFullName();
											if(fullName == null) {
												attendee = "ATTENDEE;CN=" + emailAddress + ";ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:" + emailAddress + "\n";
											}
											else {
												attendee = "ATTENDEE;CN=\"" + fullName + " (" + emailAddress + ")\";ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:" + emailAddress + "\n";
											}
								        }
								        p.write((icalType == ICalendar.ICAL_TYPE_VTODO ? "BEGIN:VTODO\n" : "BEGIN:VEVENT\n"));
								        p.write("UID:" + uid.toString() + "-" + age + "\n");
								        p.write("LAST-MODIFIED:" + lastModified.substring(0, 15) + "Z\n");
								        p.write(dtStart + "\n");
								        p.write(dtEnd + "\n");
								        p.write(dtDue + "\n");
								        p.write(valarm != null ? valarm : "");
								        p.write("CATEGORIES:" + categories + "\n");
								        p.write("DTSTAMP:" + (dateTimeFormatter.format(contact.getModifiedAt())).substring(0, 15) + "Z\n");
								        p.write("SUMMARY:" + (summaryPrefix.length() == 0 ? "" : summaryPrefix + " ") + name + " (" + age + ")\n");
							    		date.setTime(contact.getBirthdate());								        
								        p.write("DESCRIPTION:" + name + " *" + date.get(GregorianCalendar.YEAR) + "\n");
								        p.write(attendee != null ? attendee : "");
								        p.write("PRIORITY:6\n");
								        p.write("STATUS:CONFIRMED\n");
								        p.write("CLASS:PUBLIC\n");
								        p.write("URL:" + req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + this.getAccountUrl(req, contact, false) + "\n");
								        p.write(icalType == ICalendar.ICAL_TYPE_VTODO ? "END:VTODO\n" : "END:VEVENT\n");
							 		} 
								    catch (Exception e) {
							 			new ServiceException(e).log();
							 		}
							    }
							}
						}
					    n++;
	                    if(n % 50 == 0) pm.evictAll();
	                    if(n > max) break;								    						
	        		}
	        	}
	        	p.write("END:VCALENDAR\n");	        	
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
        if(req.getRequestURI().endsWith("/ical") || req.getRequestURI().endsWith("/activities")) {        
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setCharacterEncoding("UTF-8");
            // Required by Sunbird 0.9+
            resp.setHeader("ETag", Long.toString(System.currentTimeMillis()));
            BufferedReader reader = new BufferedReader(req.getReader());
            boolean allowCreation = false;
            try {
	            ICalendar.getInstance().putICal(
	            	reader, 
	            	activitiesHelper, 
	            	allowCreation
	            );
            }
            catch(ServiceException e) {
            	e.log();
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
        
}
