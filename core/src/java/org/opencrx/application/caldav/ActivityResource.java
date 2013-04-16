/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: openCRX application plugin
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2012, CRIXP Corp., Switzerland
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

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;

import org.opencrx.application.uses.net.sf.webdav.RequestContext;
import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.backend.Base;
import org.opencrx.kernel.backend.ICalendar;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.log.SysLog;
import org.w3c.cci2.BinaryLargeObject;
import org.w3c.cci2.BinaryLargeObjects;

/**
 * WebDAV resource of type Activity.
 *
 */
class ActivityResource extends CalDavResource {

	public ActivityResource(
		RequestContext requestContext,
		Activity activity,
		ActivityCollectionResource parent
	) {
		super(requestContext, activity);
		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see org.opencrx.application.caldav.CalDavResource#getObject()
	 */
	@Override
    public Activity getObject(
    ) {
        return (Activity)super.getObject();
    }

	/* (non-Javadoc)
	 * @see org.opencrx.application.uses.net.sf.webdav.Resource#isCollection()
	 */
	@Override
    public boolean isCollection(
    ) {
		return false;
    }
	
	/* (non-Javadoc)
	 * @see org.opencrx.application.caldav.CalDavResource#getMimeType()
	 */
	@Override 
	public String getMimeType(
	) {
		return "text/calendar; component=" + (this.parent.getType() == ActivityCollectionResource.Type.VTODO ? "vtodo" : "vevent");			
	}
	
    /* (non-Javadoc)
     * @see org.opencrx.application.caldav.CalDavResource#getName()
     */
    @Override
    public String getName(
    ) {
        return super.getName() + ".ics";
    }

    /* (non-Javadoc)
     * @see org.opencrx.application.uses.net.sf.webdav.Resource#getDisplayName()
     */
    @Override
    public String getDisplayName(
    ) {
    	return this.getObject().getName();
    }
    
    /**
     * Get parent collection of this activity resource.
     * @return
     */
    public ActivityCollectionResource getSyncFeedResource(
    ) {
    	return this.parent;
    }
    
	/**
	 * Get UID of this activity resource.
	 * @param event
	 * @return
	 */
	private String getUid(
    	String event
    ) {
    	String uid = null;
    	if(event.indexOf("UID:") > 0) {
    		int start = event.indexOf("UID:");
    		int end = event.indexOf("\n", start);
    		if(end > start) {
    			uid = event.substring(start + 4, end).trim();
    		}
    	}    	
    	return uid;
    }

	/* (non-Javadoc)
	 * @see org.opencrx.application.caldav.CalDavResource#getContent()
	 */
	@Override
    public BinaryLargeObject getContent(
    ) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter p = null;
		try {
			p = new PrintWriter(
				new OutputStreamWriter(out, "UTF-8")
			);
		} catch(Exception e) {
			p = new PrintWriter(out);
		}
		Activity activity = this.getObject();
		HttpServletRequest req = this.getRequestContext().getHttpServletRequest();
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activity);
    	String ical = activity.getIcal();
    	// In case of recurring activities collect activities 
    	// which are member of the same recurrence
    	List<Activity> events = new ArrayList<Activity>();
    	events.add(activity);
    	String uid = this.getUid(ical);
    	if((uid != null) && (ical.indexOf("RRULE:") > 0)) {
        	ActivityQuery memberQuery = (ActivityQuery)pm.newQuery(Activity.class);
        	memberQuery.thereExistsExternalLink().equalTo(
        		ICalendar.ICAL_SCHEMA + uid 
        	);
        	memberQuery.thereExistsExternalLink().startsWith(
        		ICalendar.ICAL_RECURRENCE_ID_SCHEMA
        	);
        	Collection<Activity> members = this.parent.getQueryHelper().getFilteredActivities(memberQuery);
        	events.addAll(members);
    	}
		String userAgent = req.getHeader("user-agent");
        boolean iPhone = userAgent != null && userAgent.indexOf("iPhone") > 0;
    	p.println("BEGIN:VCALENDAR");
        p.println("PRODID:" + ICalendar.PROD_ID);
        p.println("VERSION:2.0");
        p.println("CALSCALE:GREGORIAN");
    	for(Activity event: events) {
	        ical = event.getIcal();
	        // Obfuscate event if we are in impersonate mode and event is not PUBLIC
	        if(this.parent.getRunAs() != null && ical.indexOf("CLASS:PUBLIC") < 0) {
	        	// UID
	        	String oUID = null;
	        	int pos1 = ical.indexOf("UID");
	        	if(pos1 >= 0) {
		        	int pos2 = ical.indexOf("\n", pos1);
		        	if(pos2 > pos1) {
		        		oUID = ical.substring(pos1, pos2);
		        	}
	        	}
	        	// DTSTAMP
	        	String oDTSTAMP = null;
	        	pos1 = ical.indexOf("DTSTAMP");
	        	if(pos1 >= 0) {
		        	int pos2 = ical.indexOf("\n", pos1);
		        	if(pos2 > pos1) {
		        		oDTSTAMP = ical.substring(pos1, pos2);
		        	}
	        	}
	        	// ORGANIZER
	        	String oORGANIZER = null;
	        	pos1 = ical.indexOf("ORGANIZER");
	        	if(pos1 >= 0) {
		        	int pos2 = ical.indexOf("\n", pos1);
		        	if(pos2 > pos1) {
		        		oORGANIZER = ical.substring(pos1, pos2);
		        	}
	        	}
	        	// DTSTART
	        	String oDTSTART = null;
	        	pos1 = ical.indexOf("DTSTART");
	        	if(pos1 >= 0) {
		        	int pos2 = ical.indexOf("\n", pos1);
		        	if(pos2 > pos1) {
		        		oDTSTART = ical.substring(pos1, pos2);
		        	}
	        	}
	        	// DTEND
	        	String oDTEND = null;
	        	pos1 = ical.indexOf("DTEND");
	        	if(pos1 >= 0) {
		        	int pos2 = ical.indexOf("\n", pos1);
		        	if(pos2 > pos1) {
		        		oDTEND = ical.substring(pos1, pos2);
		        	}
	        	}
	        	// RRULE
	        	String oRRULE = null;
	        	pos1 = ical.indexOf("RRULE");
	        	if(pos1 >= 0) {
		        	int pos2 = ical.indexOf("\n", pos1);
		        	if(pos2 > pos1) {
		        		oRRULE = ical.substring(pos1, pos2);
		        	}
	        	}
	        	ical = 
        			"BEGIN:VEVENT\n" +        			
        			(oUID == null ? "" : oUID + "\n") +
        			"CLASS:CONFIDENTIAL\n" +
        			(oDTSTAMP == null ? "" : oDTSTAMP + "\n") +
        			(oORGANIZER == null ? "" : oORGANIZER + "\n") +
        			"SUMMARY:*** (" + this.parent.getRunAs() + ")\n" +
        			(oDTSTART == null ? "" : oDTSTART + "\n") +                		
                    (oDTEND == null ? "" : oDTEND + "\n") +          	
                    (oRRULE == null ? "" : oRRULE + "\n") +          	
                    "END:VEVENT\n";
	        }
	        uid = this.getUid(ical);
	        boolean externalLinkMatchesUid = false;
	        if(uid != null) {
		        for(String externalLink: event.getExternalLink()) {
		        	if(externalLink.startsWith(ICalendar.ICAL_SCHEMA) && externalLink.endsWith(uid)) {
		        		externalLinkMatchesUid = true;
		        		break;
		        	}
		        }
	        }
	        if(!externalLinkMatchesUid) {
	        	ServiceException e0 = new ServiceException(
	        		BasicException.Code.DEFAULT_DOMAIN,
	        		BasicException.Code.ASSERTION_FAILURE,
	        		"Mismatch of activity's external link and ical's UID. Use updateIcal() to fix event.",
	        		new BasicException.Parameter("activity", activity.refGetPath().toXRI()),
	        		new BasicException.Parameter("externalLink", activity.getExternalLink()),
	        		new BasicException.Parameter("uid", uid),
	        		new BasicException.Parameter("ical", ical)
	        	);
	        	SysLog.warning("Mismatch of activity's external link and ical's UID. Use updateIcal() to fix event.", Arrays.asList(activity.refGetPath().toString(), activity.getActivityNumber()));
	        	SysLog.detail(e0.getMessage(), e0.getCause());
	        }
	        ical = ical.replace("\r\n", "\n"); // Remove \r just in case
	        // VEVENT
	        if(ical.indexOf("BEGIN:VEVENT") >= 0) {
	            int start = ical.indexOf("BEGIN:VEVENT");
	            int end = ical.indexOf("END:VEVENT");
	            if(end < 0 || start < 0 || end < start) {
	            	SysLog.log(Level.WARNING, "ICAL {0} of activity {1} has bad format and will be ignored", ical, event.refGetPath().toXRI());
	            } else {
		            String vevent = ical.substring(start, end);
		    		String url = null;
		    		try {
		    			url = Base.getInstance().getAccessUrl(req, "-caldav-", event);
		    		} catch(Exception e) {}
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
		        					Base.COMMENT_SEPARATOR_EOT + " " + url + " " +
		        					vevent.substring(end);    					        					
		        			}
		        		}
		        		else {
		        			vevent += "DESCRIPTION:" + Base.COMMENT_SEPARATOR_BOT + " " + url + "\n";
		        		}
		        	}
		            p.print(vevent);            
		            SysLog.detail(vevent);
		            if(vevent.indexOf("TRANSP:") < 0) {
			        	try {
			        		String transp = "OPAQUE";
			        		if(transp != null) {
			        			p.println("TRANSP:" + transp);
			        		}
			        	} catch(Exception e) {}
		            }
		            if(vevent.indexOf("URL:") < 0) {
		            	if(url != null) {
		            		p.println("URL:" + url);
		            	}
		            }
		            try {
		            	Activities.getInstance().printAlarms(p, event);
		            } catch(Exception ignore) {}
		            p.println("END:VEVENT");
	            }
	        }
	        // VTODO
	        else if(ical.indexOf("BEGIN:VTODO") >= 0) {
	            int start = ical.indexOf("BEGIN:VTODO");
	            int end = ical.indexOf("END:VTODO");
	            if(end < 0 || start < 0 || end < start) {
	            	SysLog.log(Level.WARNING, "ICAL {0} of activity {1} has bad format and will be ignored", ical, event.refGetPath().toXRI());
	            } else {	            
		            String vtodo = ical.substring(start, end);
		            String url = null;
		            try {
		            	url = Base.getInstance().getAccessUrl(req, "-caldav-", event);
		            } catch(Exception e) {}
		            if(iPhone) {
		        		if(vtodo.indexOf("DESCRIPTION:") > 0) {
		        			start = vtodo.indexOf("DESCRIPTION:");
		        			end = vtodo.indexOf("\n", start);
		        			if(end > start) {
		        				vtodo = 
		        					vtodo.substring(0, end) + 
		        					Base.COMMENT_SEPARATOR_EOT + " " + url + " " +
		        					vtodo.substring(end);        					        					
		        			}
		        		} else {
			    			vtodo += "DESCRIPTION:" + Base.COMMENT_SEPARATOR_BOT + " " + url + "\n";
			    		}
		            }
		            p.print(vtodo);
		            SysLog.detail(vtodo);
		            if(vtodo.indexOf("URL:") < 0) {
		            	if(url != null) {
		            		p.println("URL:" + url);
		            	}
		            }
		            try {
		            	Activities.getInstance().printAlarms(p, event);
		            } catch(Exception ignore) {}
		            p.println("END:VTODO");
	            }
	        }
    	}
        p.print("END:VCALENDAR");			
		p.close();
		return BinaryLargeObjects.valueOf(out.toByteArray());
    }

	private final ActivityCollectionResource parent;
	
}