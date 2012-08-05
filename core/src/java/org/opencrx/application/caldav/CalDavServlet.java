/*
 * ====================================================================
 * Project:     openCRX/core, http://www.opencrx.org/
 * Name:        $Id: CalDavServlet.java,v 1.24 2009/09/19 16:58:16 wfro Exp $
 * Description: CalDavServlet
 * Revision:    $Revision: 1.24 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/09/19 16:58:16 $
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
package org.opencrx.application.caldav;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.jmi1.AbstractFilterActivity;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityCategory;
import org.opencrx.kernel.activity1.jmi1.ActivityFilterGlobal;
import org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup;
import org.opencrx.kernel.activity1.jmi1.ActivityGroup;
import org.opencrx.kernel.activity1.jmi1.ActivityMilestone;
import org.opencrx.kernel.activity1.jmi1.ActivityTracker;
import org.opencrx.kernel.backend.ICalendar;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.home1.jmi1.ActivityFilterCalendarFeed;
import org.opencrx.kernel.home1.jmi1.ActivityGroupCalendarFeed;
import org.opencrx.kernel.home1.jmi1.CalendarFeed;
import org.opencrx.kernel.home1.jmi1.CalendarProfile;
import org.opencrx.kernel.home1.jmi1.EMailAccount;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.utils.ActivitiesHelper;
import org.opencrx.kernel.utils.ComponentConfigHelper;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.base.text.conversion.XMLEncoder;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.log.SysLog;
import org.openmdx.portal.servlet.Action;
import org.openmdx.portal.servlet.WebKeys;

public class CalDavServlet extends HttpServlet  {

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
            req.getContextPath().replace("-caldav-", "-core-") +  "/" + WebKeys.SERVLET_NAME + "?event=" + Action.EVENT_SELECT_OBJECT + "&amp;parameter=" + selectActivityAction.getParameterEncoded() : 
            req.getContextPath().replace("-caldav-", "-core-") +  "/" + WebKeys.SERVLET_NAME + "?event=" + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectActivityAction.getParameter();
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
    
//    //-----------------------------------------------------------------------
//    static class CaldavRequest extends HttpPost {
//    
//    	public CaldavRequest(
//    		String method,
//    		String url
//    	) {
//    		super(url);
//    		this.method = method;
//    	}
//
//		@Override
//        public String getMethod(
//        ) {
//			return this.method;
//        }
//		
//		private final String method;
//    	
//    }
//    
//    //-----------------------------------------------------------------------
//    private void delegateRequest(
//    	String content,
//    	HttpServletRequest req,
//        HttpServletResponse resp
//    ) throws IOException {
//    	DefaultHttpClient httpClient = new DefaultHttpClient();
//    	CaldavRequest caldavRequest = new CaldavRequest(
//    		req.getMethod(),
//    		"https://www.google.com:443/calendar" + req.getServletPath()
//    	);
//    	Enumeration<String> headerNames = req.getHeaderNames();
//    	while(headerNames.hasMoreElements()) {
//    		String headerName = headerNames.nextElement();
//    		if(
//    			(headerName != null) &&
//    			!"content-length".equalsIgnoreCase(headerName)
//    		) {
//	    		Enumeration<String> headerValues = req.getHeaders(headerName);
//	    		while(headerValues.hasMoreElements()) {
//	    			String headerValue = headerValues.nextElement();    			
//	    			caldavRequest.addHeader(headerName, headerValue);
//	    		}
//    		}
//    	}
//    	String userPassword = "user:password";
//    	caldavRequest.setHeader("Authorization", "Basic " + Base64.encode (userPassword.getBytes()));
//    	HttpEntity requestEntity = new ByteArrayEntity(content.getBytes());
//    	caldavRequest.setEntity(requestEntity);
//    	HttpResponse response = httpClient.execute(caldavRequest);    	    	
//        resp.setStatus(response.getStatusLine().getStatusCode());
//        HttpEntity responseEntity = response.getEntity();
//        if(responseEntity != null) {
//	        if(responseEntity.getContentEncoding() != null) {
//	        	resp.setCharacterEncoding(responseEntity.getContentEncoding().getValue());
//	        }
//	        if(responseEntity.getContentType() != null) {
//	        	resp.setContentType(responseEntity.getContentType().getValue());
//	        }
//	        Header[] responseHeaders = response.getAllHeaders();
//	        for(Header header: responseHeaders) {
//	        	for(HeaderElement element: header.getElements()) {
//		        	resp.addHeader(
//		        		header.getName(), 
//		        		element.getValue()
//		        	);
//	        	}
//	        }
//	    	InputStream in = responseEntity.getContent();
//	    	OutputStream out = resp.getOutputStream();
//	    	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//	    	int b;
//	    	while((b = in.read()) != -1) {
//	    		bytes.write(b);
//	    		out.write(b);
//	    	}
//	    	bytes.close();
//	    	String responseContent = bytes.toString("ISO-8859-1");
//	    	SysLog.warning("Response", responseContent);
//        }
//        else {
//	    	SysLog.warning("Response", "NA");        	
//        }
//    }
    
    protected void printICal(
    	Activity activity,
    	PrintWriter p,
    	HttpServletRequest req,
    	int index
    ) {
        String ical = activity.getIcal();
        ical = ical.replace("\r\n", "\n"); // Remove \r just in case
		String userAgent = req.getHeader("user-agent");
        boolean iPhone = userAgent != null && userAgent.indexOf("iPhone") > 0;
    	p.println("BEGIN:VCALENDAR");
        p.println("PRODID:" + ICalendar.PROD_ID);
        p.println("VERSION:2.0");
        p.println("CALSCALE:GREGORIAN");
        if(ical.indexOf("BEGIN:VEVENT") >= 0) {
            int start = ical.indexOf("BEGIN:VEVENT");
            int end = ical.indexOf("END:VEVENT");
            String vevent = ical.substring(start, end);
    		String url = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + this.getActivityUrl(req, activity, false);
            // The attribute ORGANIZER (and ATTENDE and maybe other) attribute
            // puts the event into read-only mode in case of iPhone.
        	if(iPhone) {
        		if((vevent.indexOf("ORGANIZER:") > 0) && (vevent.indexOf("ATTENDEE:") < 0)) {
        			start = vevent.indexOf("ORGANIZER:");
        			end = vevent.indexOf("\n", start);
        			vevent = vevent.substring(0, start) + vevent.substring(end + 1); 
        		}
        		if(vevent.indexOf("DESCRIPTION:") > 0) {
        			start = vevent.indexOf("DESCRIPTION:");
        			end = vevent.indexOf("\n", start);
        			if(end > start) {
        				vevent = 
        					vevent.substring(0, end) + 
        					ICalendar.LINE_COMMENT_INDICATOR + " " + url + " " +
        					vevent.substring(end);    					        					
        			}
        		}
        		else {
        			vevent += "DESCRIPTION:" + ICalendar.LINE_COMMENT_INDICATOR + " " + url + "\n";
        		}
        	}
        	String encVevent = XMLEncoder.encode(vevent); 
            p.print(encVevent);            
            SysLog.detail("VEVENT #", index);
            SysLog.detail(encVevent);
            if(vevent.indexOf("URL:") < 0) {
                p.println("URL:" + XMLEncoder.encode(url));
            }
            p.println("END:VEVENT");
        }
        else if(ical.indexOf("BEGIN:VTODO") >= 0) {
            int start = ical.indexOf("BEGIN:VTODO");
            int end = ical.indexOf("END:VTODO");
            String vtodo = ical.substring(start, end);
            String url = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + this.getActivityUrl(req, activity, false);
            if(iPhone) {
        		if(vtodo.indexOf("DESCRIPTION:") > 0) {
        			start = vtodo.indexOf("DESCRIPTION:");
        			end = vtodo.indexOf("\n", start);
        			if(end > start) {
        				vtodo = 
        					vtodo.substring(0, end) + 
        					ICalendar.LINE_COMMENT_INDICATOR + " " + url + " " +
        					vtodo.substring(end);        					        					
        			}
        		}            	
	    		else {
	    			vtodo += "DESCRIPTION:" + ICalendar.LINE_COMMENT_INDICATOR + " " + url + "\n";
	    		}
            }
            String encVTodo = XMLEncoder.encode(vtodo); 
            p.print(encVTodo);
            SysLog.detail("VTODO #", index);
            SysLog.detail(encVTodo);
            if(vtodo.indexOf("URL:") < 0) {
                p.println("URL:" + XMLEncoder.encode(url));
            }
            p.println("END:VTODO");                        
        }
        p.print("END:VCALENDAR");    	
    }
    
    //-----------------------------------------------------------------------
    private String urlEncodePath(
    	String path
    ) throws UnsupportedEncodingException {
    	path = path.replace("/", "\t");
    	path = URLEncoder.encode(path, "UTF-8");
    	path = path.replace("%09", "/");
    	return path;
    }
  
    //-----------------------------------------------------------------------
    @Override
    protected void service(
        HttpServletRequest req, 
        HttpServletResponse resp
    ) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        PersistenceManager pm = this.getPersistenceManager(req);
        if(pm == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        // Path
    	String path = req.getServletPath();
    	String contextPath = req.getContextPath();
        // Method
        String method = req.getMethod();
        // Parse path
        String[] components = path.startsWith("/") ? path.substring(1).split("/") : path.split("/"); 
        String providerName = null;
        String segmentName = null;
        String userName = null;
        String profileName = null;
        boolean isFeedsQuery = false;
        int icalType = ICalendar.ICAL_TYPE_VEVENT;
        ActivitiesHelper activitiesHelper = null;
        if(components.length > 0) {
        	providerName = components[0];
        }
        if(components.length > 1) {
        	segmentName = components[1];
        }
        // User
        if(components.length >= 4 && "user".equals(components[2])) {
        	userName = components[3];
        }
        // Profile
        if(components.length >= 6 && "profile".equals(components[4])) {
        	profileName = components[5];
        }
        // Feeds
        if(components.length == 7 && "feeds".equals(components[6])) {
        	isFeedsQuery = true;
        }
        // Calendar
        if((userName == null) && !isFeedsQuery) {
	        // Filter
	        String filterId = path;
	        if(filterId.endsWith(".ics")) {
	        	filterId = filterId.substring(0, filterId.lastIndexOf("/"));
	        }
	        if(filterId.endsWith("/VTODO") || filterId.endsWith("/VTODO/")) {
	        	icalType = ICalendar.ICAL_TYPE_VTODO;
	        	filterId = filterId.substring(0, filterId.indexOf("/VTODO"));
	        }
	        activitiesHelper = this.getActivitiesHelper(
	            pm, 
	            filterId,
	            "false"
	        );
        }
        // Request
        BufferedReader reader = new BufferedReader(req.getReader());
        StringBuilder request = new StringBuilder();
        String l = null;
        while((l = reader.readLine()) != null) {
        	request.append(l).append("\n");
        }
        SysLog.detail("Request", Arrays.asList(method, req.getServletPath()));
        SysLog.detail("Request.body", request);
        if("PROPFIND".equalsIgnoreCase(method)) {
        	resp.setStatus(SC_MULTI_STATUS);
        	resp.setCharacterEncoding("UTF-8");
        	resp.setContentType("application/xml");
        	resp.setHeader("DAV", "1, 2, calendar-access");
      	  	PrintWriter p = resp.getWriter();      	  	
        	// User properties
        	if(userName != null && profileName != null && !isFeedsQuery) {
        		UserHome userHome = (UserHome)pm.getObjectById(
        			new Path("xri://@openmdx*org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName + "/userHome/" + userName)
        		);
        		EMailAccount emailAccount = null;
        		Collection<EMailAccount> emailAccounts = userHome.getEMailAccount();
        		for(EMailAccount account: emailAccounts) {
        			if(account.isDefault()) {
        				emailAccount = account; 
        				break;
        			}
        		}   		
        		// Sample request
        		// <x0:propfind xmlns:x2="http://calendarserver.org/ns/" xmlns:x1="urn:ietf:params:xml:ns:caldav" xmlns:x0="DAV:">
        		//  <x0:prop>
        		//   <x1:calendar-home-set/>
        		//   <x1:calendar-user-address-set/>
        		//   <x1:schedule-inbox-URL/>
        		//   <x1:schedule-outbox-URL/>
        		//   <x2:dropbox-home-URL/>
        		//   <x2:notifications-URL/>
        		//   <x0:displayname/>
        		//  </x0:prop>
        		// </x0:propfind>
        		p.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        		p.println("<D:multistatus xmlns:D=\"DAV:\">");
        		p.println("  <D:response>");
        		p.println("    <D:href>" + contextPath + this.urlEncodePath(path) + "</D:href>");
        		p.println("    <D:propstat>");
        		p.println("      <D:status>HTTP/1.1 200 OK</D:status>");
        		p.println("      <D:prop>");
        		p.println("        <C:calendar-home-set xmlns:C=\"urn:ietf:params:xml:ns:caldav\">");
        		p.println("          <D:href>" + contextPath + "/" + providerName + "/" + segmentName + "/user/" + userName + "/profile/" + profileName + "/feeds/</D:href>");
        		p.println("        </C:calendar-home-set>");
        		p.println("        <C:calendar-user-address-set xmlns:C=\"urn:ietf:params:xml:ns:caldav\">");
        		p.println("          <D:href>mailto:" + (emailAccount == null ? userName + "@" + req.getServerName() : emailAccount.getEMailAddress()) + "</D:href>");
        		p.println("          <D:href>" + contextPath + this.urlEncodePath(path) + "/</D:href>");
        		p.println("        </C:calendar-user-address-set>");
        		p.println("        <D:displayname>" + userHome.getContact().getFullName() + "</D:displayname>");
        		p.println("      </D:prop>");
        		p.println("    </D:propstat>");
        		p.println("    <D:propstat>");
        		p.println("      <D:status>HTTP/1.1 404 Not Found</D:status>");
        		p.println("      <D:prop>");
        		p.println("        <x1:schedule-inbox xmlns:x1=\"urn:ietf:params:xml:ns:caldav\" />");
        		p.println("        <x1:schedule-outbox-URL xmlns:x1=\"urn:ietf:params:xml:ns:caldav\" />");
        		p.println("        <x2:dropbox-home-URL xmlns:x2=\"http://calendarserver.org/ns/\" />");
        		p.println("        <x2:notifications-URL xmlns:x2=\"http://calendarserver.org/ns/\" />");
        		p.println("      </D:prop>");
        		p.println("    </D:propstat>");
        		p.println("  </D:response>");
        		p.println("</D:multistatus>");
        	}
        	// Calendar feed properties
        	else if(isFeedsQuery) {
        		// Sample request
        		// <x0:propfind xmlns:x1="urn:ietf:params:xml:ns:caldav" xmlns:x0="DAV:" xmlns:x2="http://apple.com/ns/ical/">
        		//  <x0:prop>
        		//   <x0:displayname/>
        		//   <x1:calendar-description/>
        		//   <x1:supported-calendar-component-set/>
        		//   <x2:calendar-color/>
        		//   <x0:resourcetype/>
        		//  </x0:prop>
        		// </x0:propfind>
        		UserHome userHome = (UserHome)pm.getObjectById(
        			new Path("xri://@openmdx*org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName + "/userHome/" + userName)
        		);        		
        		p.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	        	p.println("<D:multistatus xmlns:D=\"DAV:\" xmlns:C=\"urn:ietf:params:xml:ns:caldav\" xmlns:CS=\"http://calendarserver.org/ns/\">");
	        	p.println("  <D:response>");
	        	p.println("    <D:href>" + contextPath + this.urlEncodePath(path) + "/</D:href>");
	        	p.println("    <D:propstat>");
	        	p.println("      <D:status>HTTP/1.1 200 OK</D:status>");
	        	p.println("      <D:prop>");
	        	p.println("        <D:resourcetype>");
	        	p.println("          <D:collection />");
	        	p.println("        </D:resourcetype>");
	        	p.println("      </D:prop>");
	        	p.println("    </D:propstat>");
	        	p.println("    <D:propstat>");
	        	p.println("      <D:status>HTTP/1.1 404 Not Found</D:status>");
	        	p.println("      <D:prop>");
	        	p.println("        <x0:displayname xmlns:x0=\"DAV:\" />");
	        	p.println("        <x1:calendar-description xmlns:x1=\"urn:ietf:params:xml:ns:caldav\" />");
	        	p.println("        <x1:supported-calendar-component-set xmlns:x1=\"urn:ietf:params:xml:ns:caldav\" />");
	        	p.println("        <x2:calendar-color xmlns:x2=\"http://apple.com/ns/ical/\" />");
	        	p.println("      </D:prop>");
	        	p.println("    </D:propstat>");
	        	p.println("  </D:response>");
	        	p.println("  <D:response>");
	        	p.println("    <D:href>" + contextPath + "/" + providerName + "/" + segmentName + "/user/" + userName + "/profile/" + profileName + "/</D:href>");
	        	p.println("    <D:propstat>");
	        	p.println("      <D:status>HTTP/1.1 200 OK</D:status>");
	        	p.println("      <D:prop>");
	        	p.println("        <D:displayname>" + userHome.getContact().getFullName() + "</D:displayname>");
	        	p.println("        <D:resourcetype>");
	        	p.println("          <D:principal />");
	        	p.println("          <D:collection />");
	        	p.println("        </D:resourcetype>");
	        	p.println("      </D:prop>");
	        	p.println("    </D:propstat>");
	        	p.println("    <D:propstat>");
	        	p.println("      <D:status>HTTP/1.1 404 Not Found</D:status>");
	        	p.println("      <D:prop>");
	        	p.println("        <x1:calendar-description xmlns:x1=\"urn:ietf:params:xml:ns:caldav\" />");
	        	p.println("        <x1:supported-calendar-component-set xmlns:x1=\"urn:ietf:params:xml:ns:caldav\" />");
	        	p.println("        <x2:calendar-color xmlns:x2=\"http://apple.com/ns/ical/\" />");
	        	p.println("      </D:prop>");
	        	p.println("    </D:propstat>");
	        	p.println("  </D:response>");
	        	if(profileName != null) {
		        	CalendarProfile calendarProfile = null;
		        	Collection<CalendarProfile> calendarProfiles = userHome.getCalendarProfile();
		        	for(CalendarProfile profile: calendarProfiles) {
		        		if(profileName.equals(profile.getName())) {
		        			calendarProfile = profile;
		        			break;
		        		}
		        	}
		        	if(calendarProfile != null) {
		        		Collection<CalendarFeed> feeds = calendarProfile.getCalendarFeed();
		        		for(CalendarFeed feed: feeds) {
		        			if(feed.isActive() != null && feed.isActive()) {
			        			String href = contextPath + "/" + providerName + "/" + segmentName;
			        			if(feed instanceof ActivityGroupCalendarFeed) {
			        				ActivityGroup activityGroup = ((ActivityGroupCalendarFeed)feed).getActivityGroup();
			        				if(activityGroup instanceof ActivityTracker) {
			        					href += "/tracker/" + activityGroup.getName();
			        				}
			        				else if(activityGroup instanceof ActivityMilestone) {
			        					href += "/milestone/" + activityGroup.getName();
			        				}
			        				else if(activityGroup instanceof ActivityCategory) {
			        					href += "/category/" + activityGroup.getName();
			        				}
			        			}
			        			else if(feed instanceof ActivityFilterCalendarFeed) {
			        				AbstractFilterActivity activityFilter = ((ActivityFilterCalendarFeed)feed).getActivityFilter();
			        				if(activityFilter instanceof ActivityFilterGlobal) {
			        					href += "/globalfilter/" + ((ActivityFilterGlobal)activityFilter).getName();
			        				}
			        				else if(activityFilter instanceof ActivityFilterGroup){
			        					ActivityGroup activityGroup = (ActivityGroup)pm.getObjectById(activityFilter.refGetPath().getParent().getParent());
				        				if(activityGroup instanceof ActivityTracker) {
				        					href += "/tracker/" + activityGroup.getName();
				        				}
				        				else if(activityGroup instanceof ActivityMilestone) {
				        					href += "/milestone/" + activityGroup.getName();
				        				}
				        				else if(activityGroup instanceof ActivityCategory) {
				        					href += "/category/" + activityGroup.getName();
				        				}		
				        				href += "/filter/" + ((ActivityFilterGroup)activityFilter).getName();
			        				}
			        			}
			        			// Calendar for VEVENTs
				        		p.println("  <D:response>");
				        		p.println("    <D:href>" + href + "/</D:href>");
				        		p.println("    <D:propstat>");
				        		p.println("      <D:status>HTTP/1.1 200 OK</D:status>");
				        		p.println("      <D:prop>");
				        		p.println("        <D:displayname>" + feed.getName() + " - Events</D:displayname>");
				        		p.println("        <C:calendar-description xmlns:C=\"urn:ietf:params:xml:ns:caldav\">" + (feed.getDescription() == null ? feed.getName() : feed.getDescription()) + " - Events</C:calendar-description>");
				        		p.println("        <C:supported-calendar-component-set xmlns:C=\"urn:ietf:params:xml:ns:caldav\">");
				        		p.println("          <C:comp name=\"VEVENT\" />");
				        		p.println("        </C:supported-calendar-component-set>");
				        		p.println("        <A:calendar-color xmlns:A=\"http://apple.com/ns/ical/\">" + (feed.getBackColor() == null ? "#2952A3" : feed.getBackColor()) + "</A:calendar-color>");
				        		p.println("        <D:resourcetype>");
				        		p.println("          <D:collection />");
				        		p.println("          <C:calendar xmlns:C=\"urn:ietf:params:xml:ns:caldav\" />");
				        		p.println("        </D:resourcetype>");
				        		p.println("      </D:prop>");
				        		p.println("    </D:propstat>");
				        		p.println("  </D:response>");
				        		// Calendar for VTODOs
				        		p.println("  <D:response>");
				        		p.println("    <D:href>" + href + "/VTODO/</D:href>");
				        		p.println("    <D:propstat>");
				        		p.println("      <D:status>HTTP/1.1 200 OK</D:status>");
				        		p.println("      <D:prop>");
				        		p.println("        <D:displayname>" + feed.getName() + " - Todos</D:displayname>");
				        		p.println("        <C:calendar-description xmlns:C=\"urn:ietf:params:xml:ns:caldav\">" + (feed.getDescription() == null ? feed.getName() : feed.getDescription()) + " - Todos</C:calendar-description>");
				        		p.println("        <C:supported-calendar-component-set xmlns:C=\"urn:ietf:params:xml:ns:caldav\">");
				        		p.println("          <C:comp name=\"VTODO\" />");
				        		p.println("        </C:supported-calendar-component-set>");
				        		p.println("        <A:calendar-color xmlns:A=\"http://apple.com/ns/ical/\">" + (feed.getBackColor() == null ? "#2952A3" : feed.getBackColor()) + "</A:calendar-color>");
				        		p.println("        <D:resourcetype>");
				        		p.println("          <D:collection />");
				        		p.println("          <C:calendar xmlns:C=\"urn:ietf:params:xml:ns:caldav\" />");
				        		p.println("        </D:resourcetype>");
				        		p.println("      </D:prop>");
				        		p.println("    </D:propstat>");
				        		p.println("  </D:response>");				        		
		        			}
		        		}
		        	}
	        	}	        	  
	            p.println("</D:multistatus>");       		        		
        	}
        	// Properties of calendar and its content
        	else {
        		p.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	        	p.println("<D:multistatus xmlns:D=\"DAV:\" xmlns:C=\"urn:ietf:params:xml:ns:caldav\" xmlns:CS=\"http://calendarserver.org/ns/\">");
	        	// Properties for calendar
	            p.println("  <D:response>");
	            p.println("    <D:href>" + contextPath + this.urlEncodePath(path) + "</D:href>");
	            p.println("    <D:propstat>");
	            p.println("      <D:status>HTTP/1.1 200 OK</D:status>");
	            p.println("      <D:prop>");
	            // displayname
	            if(request.indexOf("displayname") > 0) {
	            	p.println("        <D:displayname>" + activitiesHelper.getCalendarName() + "</D:displayname>");
	            }            
	            // getctag
	            if(request.indexOf("getctag") > 0) {
	            	p.println("        <CS:getctag xmlns:CS=\"http://calendarserver.org/ns/\">" + System.currentTimeMillis() + "</CS:getctag>");
	            }
	            // getetag
	            if(request.indexOf("getetag") > 0) {
	            	p.println("        <D:getetag>&quot;" + activitiesHelper.getFilteredActivitiesParentId() + "&quot;</D:getetag>");
	            }
	            // calendar-description
	            if(request.indexOf("calendar-description") > 0) {
	            	p.println("        <C:calendar-description xmlns:C=\"urn:ietf:params:xml:ns:caldav\">" + activitiesHelper.getCalendarName() + "</C:calendar-description>");
	            }
	            // getcontenttype
	            if(request.indexOf("getcontenttype") > 0) {
	            	p.println("        <D:getcontenttype>text/calendar</D:getcontenttype>");
	            }
	            if(request.indexOf("supported-calendar-component-set") > 0) {
	            	p.println("        <C:supported-calendar-component-set xmlns:C=\"urn:ietf:params:xml:ns:caldav\">");
		            p.println("          <C:comp name=\"" + (icalType == ICalendar.ICAL_TYPE_VTODO ? "VTODO" : "VEVENT") + "\" />");
		            p.println("        </C:supported-calendar-component-set>");
	            }	            
	            // resourcetype
	            p.println("        <D:resourcetype>");
	            p.println("          <D:collection />");
	            p.println("          <C:calendar xmlns:C=\"urn:ietf:params:xml:ns:caldav\" />");
	            p.println("        </D:resourcetype>");
	            p.println("      </D:prop>");
	            p.println("    </D:propstat>");     
	            p.println("  </D:response>");
	            // Calendar events
                if(activitiesHelper.getActivitySegment() != null) {                
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
	                ActivityQuery activityQuery = Utils.getActivityPackage(pm).createActivityQuery();
	                if(activitiesHelper.isDisabledFilter()) {
	                    activityQuery.thereExistsDisabled().isTrue();                    
	                }
	                else {
	                    activityQuery.forAllDisabled().isFalse();                    
	                }
	                if(icalType == ICalendar.ICAL_TYPE_VEVENT) {
	                	activityQuery.icalType().elementOf(ICalendar.ICAL_TYPE_NA, ICalendar.ICAL_TYPE_VEVENT);
	                }
	                else if(icalType == ICalendar.ICAL_TYPE_VTODO) {
	                	activityQuery.icalType().equalTo(ICalendar.ICAL_TYPE_VTODO);
	                }
	                activityQuery.ical().isNonNull();
	                int n = 0;
	                for(Activity activity: activitiesHelper.getFilteredActivities(activityQuery)) {
	                    p.println("  <D:response>");
	                    p.println("    <D:href>" + contextPath + this.urlEncodePath(path) + "activity/" + activity.refGetPath().getBase() + ".ics</D:href>");
	                    p.println("    <D:propstat>");
	                    p.println("      <D:status>HTTP/1.1 200 OK</D:status>");
	                    p.println("      <D:prop>");
	                    p.println("        <D:getetag>&quot;" + activity.getModifiedAt().getTime() + "&quot;</D:getetag>");
	                    p.println("        <D:resourcetype />");
	                    p.println("      </D:prop>");
	                    p.println("    </D:propstat>");
	                    p.println("  </D:response>");
	                    n++;
	                    if(n % 50 == 0) pm.evictAll();                
	                    if(n > maxActivities) break;
	                }
                }
	            p.println("</D:multistatus>");
        	}
            p.flush();
        }
        else if("OPTIONS".equalsIgnoreCase(method)) {   
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/xml");
            resp.setHeader("Allow:", "OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, COPY, MOVE");
            resp.setHeader("Allow:", "PROPFIND, PROPPATCH, REPORT");
            resp.setHeader("DAV", "1, 2, calendar-access");        	
        }
        else if("REPORT".equalsIgnoreCase(method)) {
        	if(
        		(activitiesHelper != null) &&
        		((request.indexOf("calendar-multiget") > 0) || 
        		(request.indexOf("calendar-query") > 0))
        	) {
                if(activitiesHelper.getActivitySegment() != null) {                
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
	                ActivityQuery activityQuery = Utils.getActivityPackage(pm).createActivityQuery();
	                if(activitiesHelper.isDisabledFilter()) {
	                    activityQuery.thereExistsDisabled().isTrue();                    
	                }
	                else {
	                    activityQuery.forAllDisabled().isFalse();                    
	                }
	                if(icalType == ICalendar.ICAL_TYPE_VEVENT) {
	                	activityQuery.icalType().elementOf(ICalendar.ICAL_TYPE_NA, ICalendar.ICAL_TYPE_VEVENT);
	                }
	                else if(icalType == ICalendar.ICAL_TYPE_VTODO) {
	                	activityQuery.icalType().equalTo(ICalendar.ICAL_TYPE_VTODO);
	                }	                
	                activityQuery.ical().isNonNull();
		            resp.setStatus(SC_MULTI_STATUS);
		            resp.setCharacterEncoding("UTF-8");
		            resp.setContentType("application/xml");
		            resp.setHeader("DAV", "1, 2, calendar-access");
		        	PrintWriter p = resp.getWriter();
		        	p.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		        	SysLog.detail("<D:multistatus>");
		        	p.println("<D:multistatus xmlns:D=\"DAV:\" xmlns:C=\"urn:ietf:params:xml:ns:caldav\">");
	                int n = 0;
	                for(Activity activity: activitiesHelper.getFilteredActivities(activityQuery)) {
	    	        	p.println("  <D:response>");
	    	        	p.println("    <D:href>" + contextPath + this.urlEncodePath(path) + "activity/" + activity.refGetPath().getBase() + ".ics</D:href>");
	    	        	p.println("    <D:propstat>");
	    	        	p.println("      <D:prop>");
	    	        	p.println("        <D:getetag>&quot;" + activity.getModifiedAt().getTime() + "&quot;</D:getetag>");
	    	        	p.print  ("        <C:calendar-data xmlns:C=\"urn:ietf:params:xml:ns:caldav\">");
	    	        	this.printICal(
	    	        		activity, 
	    	        		p, 
	    	        		req, 
	    	        		n
	    	        	);
	                    p.println("</C:calendar-data>");
	    	        	p.println("      </D:prop>");
	    	        	p.println("      <D:status>HTTP/1.1 200 OK</D:status>");
	    	        	p.println("    </D:propstat>");
	    	        	p.println("  </D:response>");
	                    n++;
	                    if(n % 50 == 0) pm.evictAll();                
	                    if(n > maxActivities) break;
	                }
		        	p.println("</D:multistatus>");
		        	SysLog.detail("</D:multistatus>");
		        	p.flush();
                }
            	else {
                    super.service(req, resp);                
            	}
        	}
        	else {
                super.service(req, resp);                
        	}
        }
        else if("GET".equalsIgnoreCase(method)) {
        	if(path.endsWith(".ics")) {
        		try {
	        		String activityId = path.substring(path.lastIndexOf("/") + 1, path.length() - 4);
	        		Path activityIdentity = new Path(
	        			"xri://@openmdx*org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName + "/activity/" + activityId
	        		);
        			Activity activity = (Activity)pm.getObjectById(activityIdentity);
    	            resp.setCharacterEncoding("UTF-8");
    	            resp.setContentType(ICalendar.MIME_TYPE);
		            resp.setHeader("DAV", "1, 2, calendar-access");
		            resp.setHeader("ETag", DateFormat.getInstance().format(activity.getModifiedAt()));		            
    	        	PrintWriter p = resp.getWriter();
    	        	this.printICal(
    	        		activity, 
    	        		p, 
    	        		req, 
    	        		0
    	        	);
    	        	p.println();
    	        	p.flush();
        		}
        		catch(Exception e) {
        			try {
        				pm.currentTransaction().rollback();
        			} catch(Exception e0) {}
        		}
        	}
	        resp.setStatus(HttpServletResponse.SC_OK);        		
        }
        else if("PUT".equalsIgnoreCase(method)) {
            resp.setCharacterEncoding("UTF-8");
        	reader = new BufferedReader(new StringReader(request.toString()));
        	try {
	        	boolean created = ICalendar.getInstance().putICal(
	        		reader, 
	        		activitiesHelper,
	        		true
	        	);
	            resp.setHeader("ETag", Long.toString(System.currentTimeMillis()));            
	            resp.setStatus(created ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_OK);
        	}
        	catch(ServiceException e) {
        		e.log();
        	}        	
        }        
        else if("DELETE".equalsIgnoreCase(method)) {        	
        	if(path.endsWith(".ics")) {
        		try {
	        		String activityId = path.substring(path.lastIndexOf("/") + 1, path.length() - 4);
	        		Path activityIdentity = new Path(
	        			"xri://@openmdx*org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName + "/activity/" + activityId
	        		);
        			pm.currentTransaction().begin();
        			Activity activity = (Activity)pm.getObjectById(activityIdentity);
        			activity.setDisabled(true);
        			pm.currentTransaction().commit();
        		}
        		catch(Exception e) {
        			try {
        				pm.currentTransaction().rollback();
        			} catch(Exception e0) {}
        		}        		
        	}
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        try {
            pm.close();            
        } 
        catch(Exception e) {}
    }

	//-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------    
    private static final long serialVersionUID = 307158420450161147L;

    protected final static String CONFIGURATION_ID = "CalDavServlet";

    protected final int SC_MULTI_STATUS = 207;
    
    protected PersistenceManagerFactory persistenceManagerFactory = null;
    protected PersistenceManager rootPm = null;
    protected org.opencrx.kernel.admin1.jmi1.ComponentConfiguration componentConfiguration = null;    
    
}
