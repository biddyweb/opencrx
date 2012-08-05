/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: ICalendar.java,v 1.63 2009/06/05 09:20:12 wfro Exp $
 * Description: ICalendar
 * Revision:    $Revision: 1.63 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/06/05 09:20:12 $
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
package org.opencrx.kernel.backend;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.AccountAddress;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.account1.jmi1.EMailAddress;
import org.opencrx.kernel.activity1.jmi1.Absence;
import org.opencrx.kernel.activity1.jmi1.AbstractActivityParty;
import org.opencrx.kernel.activity1.jmi1.AbstractEMailRecipient;
import org.opencrx.kernel.activity1.jmi1.AbstractPhoneCallRecipient;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.EMail;
import org.opencrx.kernel.activity1.jmi1.EMailRecipient;
import org.opencrx.kernel.activity1.jmi1.ExternalActivity;
import org.opencrx.kernel.activity1.jmi1.Incident;
import org.opencrx.kernel.activity1.jmi1.IncidentParty;
import org.opencrx.kernel.activity1.jmi1.Mailing;
import org.opencrx.kernel.activity1.jmi1.MailingRecipient;
import org.opencrx.kernel.activity1.jmi1.Meeting;
import org.opencrx.kernel.activity1.jmi1.MeetingParty;
import org.opencrx.kernel.activity1.jmi1.PhoneCall;
import org.opencrx.kernel.activity1.jmi1.Task;
import org.opencrx.kernel.activity1.jmi1.TaskParty;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasicObject;
import org.openmdx.base.naming.Path;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;

public class ICalendar extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new ICalendar());
	}
	
    //-------------------------------------------------------------------------
	public static ICalendar getInstance(		
	) throws ServiceException {
		return getInstance(ICalendar.class);
	}

	//-------------------------------------------------------------------------
	protected ICalendar(
	) {
		
	}
	
    //-------------------------------------------------------------------------
    public String getPrimaryEMailAddress(
        Account account
    ) throws ServiceException {
        Collection<AccountAddress> addresses = account.getAddress();
        String emailAddress = null;
        for(AccountAddress address: addresses) {
            if(address instanceof EMailAddress) {
                String adr = ((EMailAddress)address).getEmailAddress();
                if((emailAddress == null) && (adr != null)) {
                    emailAddress = adr;
                }
                if((adr != null) && (address.getUsage().contains(USAGE_EMAIL_PRIMARY))) {
                    emailAddress = adr;
                }
            }
        }
        return emailAddress;
    }

    //-------------------------------------------------------------------------
    private String escapeNewlines(
        String from
    ) {
        String to = "";
        for(int i = 0; i < from.length(); i++) {
            if(from.charAt(i) == '\n') {
                to += "\\n";
            }
            else {
                to += from.charAt(i);
            }
        }
        return to;
    }

    //-------------------------------------------------------------------------
    public String mergeIcal(
        Activity activity,
        String sourceIcal,
        List<String> statusMessage
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activity);    	
        short icalType = activity.getIcalType();
        if(icalType == ICAL_TYPE_NA) {
        	// Try to derive get icalType from sourceICal
        	if((sourceIcal != null) && (sourceIcal.indexOf("BEGIN:VEVENT") > 0)) {
        		icalType = ICAL_TYPE_VEVENT;
        	}
        	else if((sourceIcal != null) && (sourceIcal.indexOf("BEGIN:VTODO") > 0)) {
        		icalType = ICAL_TYPE_VTODO;
        	}
        	// If still undefined, try to derive from last applied creator
        	else {
	            ActivityCreator activityCreator = null;
	            try {
	            	activityCreator = activity.getLastAppliedCreator();
	            }
	            catch(Exception e) {} // don't care if creator can not be accessed
	            if(
	            	(activityCreator != null) && 
	            	(activityCreator.getIcalType() != ICAL_TYPE_NA)
	            ) {
	                icalType = activityCreator.getIcalType();
	            }
	            else {
	            	if(activity instanceof Absence) {
	            		icalType = ICAL_TYPE_VEVENT;
	            	}
	            	else if(activity instanceof EMail) {
	            		icalType = ICAL_TYPE_VEVENT;
	            	}
	            	else if(activity instanceof ExternalActivity) {
	            		icalType = ICAL_TYPE_VEVENT;
	            	}
	            	else if(activity instanceof Incident) {
	            		icalType = ICAL_TYPE_VTODO;
	            	}
	            	else if(activity instanceof Mailing) {
	            		icalType = ICAL_TYPE_VEVENT;
	            	}
	            	else if(activity instanceof Meeting) {
	            		icalType = ICAL_TYPE_VEVENT;
	            	}
	            	else if(activity instanceof PhoneCall) {
	            		icalType = ICAL_TYPE_VEVENT;
	            	}
	            	else if(activity instanceof Task) {
	            		icalType = ICAL_TYPE_VTODO;
	            	}
	            }
        	}
        }
        // DTSTART
        String dtStart = null;
        if(activity.getScheduledStart() != null) {
            dtStart = DateFormat.getInstance().format(activity.getScheduledStart());
        }
        else {
            // ical requires DTSTART. Set DTSTART to beginning of year 
            // if scheduledStart is not defined for activity
            GregorianCalendar cal = new GregorianCalendar();
            dtStart = cal.get(Calendar.YEAR) + "0101T000000.000Z";
        }
        // DTEND
        String dtEnd = null;
        if(activity.getScheduledEnd() != null) {
            dtEnd = DateFormat.getInstance().format(activity.getScheduledEnd());
        }
        // DUE
        String dueBy = null;
        if(activity.getDueBy() != null) {
            dueBy = DateFormat.getInstance().format(activity.getDueBy());
        }
        // COMPLETED
        String completed = null;
        if(activity.getActualEnd() != null) {
            completed = DateFormat.getInstance().format(activity.getActualEnd());
        }
        // LAST-MODIFIED
        String lastModified = DateFormat.getInstance().format(new Date());
        // PRIORITY
        Number priority = activity.getPriority();
        // SUMMARY (append activity number)
        String summary = activity.getName() == null ? 
            "" : 
            activity.getName();
        summary += activity.getActivityNumber() == null ? 
            "" : 
            LINE_COMMENT_INDICATOR + " #" + activity.getActivityNumber();            
        // DESCRIPTION
        String description = activity.getDescription() == null ? 
            "" : 
            activity.getDescription();
        // LOCATION
        String location = activity.getLocation() == null ? 
            "" : 
            activity.getLocation();
        // STATUS
        String status = null;
        // VTODO
        if(icalType == ICAL_TYPE_VTODO) {
            Number percentComplete = activity.getPercentComplete();
            Boolean isDisabled = activity.isDisabled();
            if(isDisabled != null && isDisabled.booleanValue()) {
                status = "CANCELLED";
            }
            else {
                if(percentComplete == null || percentComplete.intValue() == 0) {
                    status = "NEEDS-ACTION";
                }
                else if(percentComplete != null && percentComplete.intValue() >= 100) {
                    status = "COMPLETED";
                }
                else {
                    status = "IN-PROCESS";
                }
            }
        }
        // VEVENT
        else {
            Number percentComplete = activity.getPercentComplete();
            Boolean isDisabled = activity.isDisabled();
            if(isDisabled != null && isDisabled.booleanValue()) {
                status = "CANCELLED";
            }
            else {
                if(percentComplete == null || percentComplete.intValue() == 0) {
                    status = "TENTATIVE";
                }
                else {
                    status = "CONFIRMED";
                }
            }
        }
        // Attendees
        List<String> attendees = new ArrayList<String>();
        List<AbstractActivityParty> participants = new ArrayList<AbstractActivityParty>();
        if(activity instanceof EMail) {
        	Collection<AbstractEMailRecipient> c = ((EMail)activity).getEmailRecipient();
        	participants.addAll(c);
        }
        else if(activity instanceof Incident) {
        	Collection<IncidentParty> c = ((Incident)activity).getIncidentParty();
        	participants.addAll(c);
        }
        else if(activity instanceof Mailing) {
        	Collection<MailingRecipient> c = ((Mailing)activity).getMailingRecipient();
        	participants.addAll(c);
        }
        else if(activity instanceof Meeting) {
        	Collection<MeetingParty> c = ((Meeting)activity).getMeetingParty();
        	participants.addAll(c);
        }
        else if(activity instanceof PhoneCall) {
        	Collection<AbstractPhoneCallRecipient> c = ((PhoneCall)activity).getPhoneCallRecipient();
        	participants.addAll(c);
        }
        else if(activity instanceof Task) {
        	Collection<TaskParty> c = ((Task)activity).getTaskParty();
        	participants.addAll(c);
        }
        for(AbstractActivityParty participant: participants) {
        	RefObject_1_0 party = null;
        	try {
        		party = (RefObject_1_0)participant.refGetValue("party");
        	}
        	catch(Exception e) {}
            if(party != null) {
                try {
                    Account account = null;
                    String emailAddress = null;
                    // Party is Contact
                    if(party instanceof Account) {
                        account = (Account)party;
                        emailAddress = this.getPrimaryEMailAddress(
                            account
                        );
                    }
                    // Party is address
                    else if(party instanceof EMailAddress) {
                        account = (Account)pm.getObjectById(
                            party.refGetPath().getParent().getParent()
                        );
                        emailAddress = ((EMailAddress)party).getEmailAddress();
                    }
                    String partyType = participant.getPartyType() == PARTY_TYPE_OPTIONAL ? 
                    	"OPT-PARTICIPANT" : 
                    	"REQ-PARTICIPANT";
                    if(emailAddress != null) {
                        String fullName = account == null ?
                        	null :
                        	account.getFullName();
                        if(fullName == null) {
                            attendees.add(
                                ";CN=" + emailAddress + ";ROLE=" + partyType + ";RSVP=TRUE:MAILTO:" + emailAddress
                            );          
                        }
                        else {
                            attendees.add(
                                ";CN=\"" + fullName + " (" + emailAddress + ")\";ROLE=" + partyType + ";RSVP=TRUE:MAILTO:" + emailAddress
                            );            
                        }
                    }
                }
                catch(ServiceException e) {
                    if(e.getExceptionCode() != BasicException.Code.AUTHORIZATION_FAILURE) {
                        throw e;
                    }
                }
            }
        }
        
        // ORGANIZER
        String organizerEmailAddress = null;
        if(activity.getAssignedTo() != null) {
            organizerEmailAddress = this.getPrimaryEMailAddress(
                activity.getAssignedTo()
            );
        }
        // Return if data is missing
        if(!statusMessage.isEmpty()) {
            return null;
        }        
        if((sourceIcal == null) || (sourceIcal.length() == 0)) {
            // Empty template
            UUID uid = null;
            try {
                uid = UUIDConversion.fromString(activity.refGetPath().getBase());
            }
            catch(Exception e) {
                uid = UUIDs.getGenerator().next();
            }
            sourceIcal =
                "BEGIN:VCALENDAR\n" +
                "PRODID:" + PROD_ID + "\n" +
                "VERSION:2.0\n" +
                (icalType == ICalendar.ICAL_TYPE_VTODO ? "BEGIN:VTODO\n" : "BEGIN:VEVENT\n") +
                "UID:" + uid.toString() + "\n" +
                "LAST-MODIFIED:" + lastModified.substring(0, 15) + "Z\n" +
                "DTSTART:\n" +
                "DTEND:\n" +
                "DUE:\n" +
                "COMPLETED:\n" +
                "LOCATION:\n" +
                "DTSTAMP:\n" +
                "SUMMARY:\n" +
                "DESCRIPTION:\n" +
                "PRIORITY:\n" +
                "STATUS:\n" +
                "ATTENDEE:\n" +
                "CLASS:PUBLIC\n" +
                (icalType == ICalendar.ICAL_TYPE_VTODO ? "END:VTODO\n" : "END:VEVENT\n") +
                "END:VCALENDAR";            
        }
        try {
            ByteArrayOutputStream targetIcalBos = new ByteArrayOutputStream();
            PrintWriter targetIcal = new PrintWriter(new OutputStreamWriter(targetIcalBos, "UTF-8"));
            String lSourceIcal = null;
            BufferedReader readerSourceIcal = new BufferedReader(new StringReader(sourceIcal));
            boolean isEventOrTodo = false;
            boolean isAlarm = false;
            boolean isTimezone = false;
            String tagStart = null;
            int nEvents = 0;
            while((lSourceIcal = readerSourceIcal.readLine()) != null) {
                if(!lSourceIcal.startsWith(" ")) {
                    tagStart = lSourceIcal;
                }
                // PRODID
                if(lSourceIcal.startsWith("PRODID") || lSourceIcal.startsWith("prodid")) {                
                    targetIcal.println("PRODID:" + PROD_ID);
                }                
                else if(lSourceIcal.startsWith("TZID") || lSourceIcal.startsWith("tzid")) {                
                }                
                else if(
                    lSourceIcal.toUpperCase().startsWith("BEGIN:VTIMEZONE") 
                ) {
                    isTimezone = true;
                }
                else if(
                    lSourceIcal.toUpperCase().startsWith("END:VTIMEZONE")
                ) {
                    isTimezone = false;
                }
                else if(
                    lSourceIcal.toUpperCase().startsWith("BEGIN:VALARM")
                ) {
                    targetIcal.println("BEGIN:VALARM");                    
                    isAlarm = true;
                }
                else if(
                    lSourceIcal.toUpperCase().startsWith("END:VALARM")
                ) {           
                    targetIcal.println("END:VALARM");                    
                    isAlarm = false;                    
                }
                else if(
                    lSourceIcal.toUpperCase().startsWith("BEGIN:VEVENT") ||
                    lSourceIcal.toUpperCase().startsWith("BEGIN:VTODO") 
                ) {
                    targetIcal.println(
                        icalType == ICAL_TYPE_VTODO ? "BEGIN:VTODO" : "BEGIN:VEVENT"
                    );
                    isEventOrTodo = true;
                }
                // Dump updated event fields only for first event
                else if(
                    (nEvents == 0) &&
                    lSourceIcal.toUpperCase().startsWith("END:VEVENT") || 
                    lSourceIcal.toUpperCase().startsWith("END:VTODO")
                ) {                    
                    // DTSTART
                    if(dtStart != null) {
                        if(dtStart.endsWith("T000000.000Z")) {
                            targetIcal.println("DTSTART;VALUE=DATE:" + dtStart.substring(0, 8));                            
                        }
                        else {
                            targetIcal.println("DTSTART:" + dtStart.substring(0, 15) + "Z");
                        }
                    }
                    // DTEND
                    if(dtEnd != null) {
                        if(dtEnd.endsWith("T000000.000Z")) {
                            targetIcal.println("DTEND;VALUE=DATE:" + dtEnd.substring(0, 8));                            
                        }
                        else {
                            targetIcal.println("DTEND:" + dtEnd.substring(0, 15) + "Z");
                        }
                    }
                    // DUE
                    if(dueBy != null) {
                        if(dueBy.endsWith("T000000.000Z")) {
                            targetIcal.println("DUE;VALUE=DATE:" + dueBy.substring(0, 8));                            
                        }
                        else {
                            targetIcal.println("DUE:" + dueBy.substring(0, 15) + "Z");
                        }
                    }
                    // COMPLETED
                    if(completed != null) {
                        if(completed.endsWith("T000000.000Z")) {
                            targetIcal.println("COMPLETED;VALUE=DATE:" + completed.substring(0, 8));                            
                        }
                        else {
                            targetIcal.println("COMPLETED:" + completed.substring(0, 15) + "Z");
                        }
                    }
                    // LAST-MODIFIED
                    if(lastModified != null) {
                        targetIcal.println("LAST-MODIFIED:" + lastModified.substring(0, 15) + "Z");
                    }
                    // LOCATION
                    if((location != null) && (location.length() > 0)) {
                        targetIcal.println("LOCATION:" + location);
                    }
                    // DTSTAMP
                    targetIcal.println("DTSTAMP:" + DateFormat.getInstance().format(new Date()).substring(0, 15) + "Z");
                    // DESCRIPTION
                    if((description != null) && (description.length() > 0)) {
                        targetIcal.println("DESCRIPTION:" + this.escapeNewlines(description));
                    }
                    // SUMMARY
                    if((summary != null) && (summary.length() > 0)) {
                        targetIcal.println("SUMMARY:" + summary);
                    }
                    // PRIORITY
                    if(priority != null) {
                        int icalPriority = 9;
                        switch(priority.intValue()) {
                            case 1: icalPriority = 9; break; // low                            
                            case 2: icalPriority = 5; break; // normal
                            case 3: icalPriority = 3; break; // high
                            case 4: icalPriority = 2; break; // urgent
                            case 5: icalPriority = 1; break; // immediate
                        }
                        targetIcal.println("PRIORITY:" + icalPriority);
                    }
                    // STATUS
                    if(status != null) {
                        targetIcal.println("STATUS:" + status);
                    }
                    // ATTENDEE
                    for(int i = 0; i < attendees.size(); i++) {
                        targetIcal.println("ATTENDEE" + attendees.get(i));
                    }
                    // ORGANIZER
                    if(organizerEmailAddress != null) {
                        targetIcal.println("ORGANIZER:MAILTO:" + organizerEmailAddress);
                    }
                    if(
                        lSourceIcal.toUpperCase().startsWith("END:VEVENT") ||
                        lSourceIcal.toUpperCase().startsWith("END:VTODO")
                    ) {
                        targetIcal.println(
                            icalType == ICAL_TYPE_VTODO ? "END:VTODO" : "END:VEVENT"
                        );                                            
                    }
                    isEventOrTodo = false;
                    nEvents++;
                }
                else if(isTimezone) {
                    // Skip all timezone fields. All datetime fields are converted to UTC
                }
                else if(
                    isEventOrTodo && 
                    !isAlarm && 
                    (nEvents == 0)
                ) {
                    boolean isUpdatableTag = 
                        tagStart.toUpperCase().startsWith("DTSTART");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("DTEND");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("DUE");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("COMPLETED");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("LOCATION");
                    isUpdatableTag |= 
                        tagStart.toUpperCase().startsWith("DTSTAMP");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("DESCRIPTION");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("LAST-MODIFIED");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("SUMMARY");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("PRIORITY");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("STATUS");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("ATTENDEE");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("ORGANIZER");
                    if(!isUpdatableTag) {
                        targetIcal.println(lSourceIcal);
                    }
                }
                else {
                    targetIcal.println(lSourceIcal);                    
                }
            }
            targetIcal.flush();
            targetIcalBos.close();
            try {
                return targetIcalBos.toString("UTF-8");
            }
            catch(Exception e) {
                return null;
            }
        }
        catch(Exception e) {
            return null;
        }
    }

    //-----------------------------------------------------------------------
    public void removeProprietaryProperties(
        Map<String,String> ical
    ) {
        for(Iterator<String> i = ical.keySet().iterator(); i.hasNext(); ) {
            String prop = i.next();
            if(prop.startsWith("X-")) {
                i.remove();
            }
            else if(prop.startsWith("ATTENDEE") || prop.startsWith("ORGANIZER")) {
                String[] components = ical.get(prop).replaceFirst(":", ";").split(";");
                String role = null;
                String rsvp = null;
                String mailto = null;
                for(String component: components) {
                    if(component.startsWith("ROLE=")) {
                        role = component;
                    }
                    else if(component.startsWith("RSVP=")) {
                        rsvp = component;
                    }
                    else if(component.startsWith("MAILTO:") || component.startsWith("mailto:")) {
                        mailto = component;
                    }
                }
                String normalizedValue = "";
                if(role != null) {
                    normalizedValue += role;
                }
                if(rsvp != null) {
                    if(normalizedValue.length() > 0) normalizedValue += ";";
                    normalizedValue += rsvp;
                }
                if(mailto != null) {
                    if(normalizedValue.length() > 0) normalizedValue += ":";
                    normalizedValue += mailto.replace("mailto", "MAILTO");
                }                
                ical.put(
                    prop,
                    normalizedValue
                );
            }
        }
    }
        
    //-------------------------------------------------------------------------
    public Map<String,String> parseICal(
        BufferedReader reader,
        StringBuilder ical
    ) throws IOException {
        Map<String,String> icalFields = new HashMap<String,String>();
        String line = null;
        int nAttendees = 0;
        String currentName = null;
        /**
         * Calendars with at most one event can be imported
         * The event is mapped to an activity
         */
        boolean isEvent = false;
        int nEvents = 0;
        while((line = reader.readLine()) != null) {
            // Skip tags: URL
            if(
                line.startsWith("URL") || 
                line.startsWith("url") 
            ) {
                while(
                    ((line = reader.readLine()) != null) &&
                    line.startsWith(" ")
                ) {}
            }
            if(line == null) {
                break;
            }
            else {
                ical.append(line).append("\n");
                boolean addProperty = isEvent && (nEvents == 0);
                if(
                    line.startsWith("BEGIN:VEVENT") || 
                    line.startsWith("begin:vevent") || 
                    line.startsWith("BEGIN:VTODO") || 
                    line.startsWith("begin:vtodo") 
                ) {
                    isEvent = true;
                }
                else if(
                    line.startsWith("TZID") || 
                    line.startsWith("tzid") 
                ) {
                    addProperty = true;
                }
                else if(
                    line.startsWith("END:VEVENT") || 
                    line.startsWith("end:vevent") || 
                    line.startsWith("END:VTODO") || 
                    line.startsWith("end:vtodo") 
                ) {
                    nEvents++;
                    isEvent = false;
                    addProperty = false;
                }
                if(addProperty) {
                    int pos;
                    if(line.startsWith(" ")) {
                        if(icalFields.containsKey(currentName)) {
                            icalFields.put(
                                currentName,
                                icalFields.get(currentName) + line.substring(1).replace("\\n", "\n").replace("\\", "")
                            );
                        }
                        else if((pos = line.indexOf(":")) >= 0) {
                            currentName += line.substring(0, pos).toUpperCase();
                            if(currentName.indexOf(";") > 0) {
                                currentName = currentName.substring(0, currentName.indexOf(";"));
                            }
                            icalFields.put(
                                currentName,
                                line.substring(pos + 1).replace("\\n", "\n").replace("\\", "")
                            );                            
                        }
                    }
                    else if(line.startsWith("ATTENDEE") || line.startsWith("attendee")) {
                        currentName = "ATTENDEE[" + nAttendees + "]";
                        icalFields.put(
                            currentName,
                            line.substring("ATTENDEE".length()).replace("\\n", "\n").replace("\\", "")
                        );
                        nAttendees++;
                    }
                    else if((pos = line.indexOf(":")) >= 0) {
                        currentName = line.substring(0, pos).toUpperCase();
                        if(currentName.indexOf(";") > 0) {
                            currentName = currentName.substring(0, currentName.indexOf(";"));
                        }
                        icalFields.put(
                            currentName,
                            line.substring(pos + 1).replace("\\n", "\n").replace("\\", "")
                        );
                    }
                    else {
                        currentName = line;
                    }
                }
            }                 
        }      
        this.removeProprietaryProperties(icalFields);
        return icalFields;
    }
    
    //-------------------------------------------------------------------------
    public BasicObject importItem(
        byte[] item,
        Activity activity,
        short locale,
        List<String> errors,
        List<String> report,
        boolean isEMailAddressLookupCaseInsensitive,
        boolean isEMailAddressLookupIgnoreDisabled
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activity);    	
    	org.opencrx.kernel.account1.jmi1.Segment accountSegment =
    		(org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(
    			new Path(new String[]{
    				"org:opencrx:kernel:account1", 
    				"provider", 
    				activity.refGetPath().get(2), 
    				"segment", 
    				activity.refGetPath().get(4)
    			})
    		);
        try {
            InputStream is = new ByteArrayInputStream(item);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder ical = new StringBuilder();
            Map<String,String> icalFields = this.parseICal(
                reader,
                ical
            );
            AppLog.trace("ICalendar", icalFields);
            return this.importItem(
                ical.toString(),
                icalFields,
                activity,
                accountSegment,
                locale,
                errors,
                report,
                isEMailAddressLookupCaseInsensitive,
                isEMailAddressLookupIgnoreDisabled                
            );
        }
        catch(IOException e) {
            AppLog.warning("Can not read item", e.getMessage());
        }
        return null;
    }

    //-------------------------------------------------------------------------
    private BasicObject getAttendeeAsContact(
        String attendeeAsString,
        org.opencrx.kernel.account1.jmi1.Segment accountSegment,
        short locale,
        List<String> report
    ) {
        Map<String,String> vcard = new HashMap<String,String>();
        int pos = attendeeAsString.indexOf("MAILTO:");
        if(pos < 0) {
            pos = attendeeAsString.indexOf("mailto:");
        }
        String emailPrefInternet = attendeeAsString.substring(pos + 7);
        vcard.put("EMAIL;PREF;INTERNET", emailPrefInternet);
        try {
            return VCard.getInstance().updateAccount(
                vcard,
                accountSegment,
                locale,
                report
            );
        }
        catch(Exception e) {
            return null;
        }    
    }

    //-------------------------------------------------------------------------
    protected Date getUtcDate(
        String dateTime,
        SimpleDateFormat dateTimeFormatter
    ) throws ParseException {
        Date date = null;
        if(dateTime.endsWith("Z")) {
            if(dateTime.length() == 16) {
                date = DateFormat.getInstance().parse(dateTime.substring(0, 15) + ".000Z");
            }
            else {
                date = DateFormat.getInstance().parse(dateTime);
            }
        }
        else if(dateTime.length() == 8) {
            date = DateFormat.getInstance().parse(dateTime + "T000000.000Z");
        }
        else {
            date = dateTimeFormatter.parse(dateTime);
        }
        return date;
    }
    
    //-------------------------------------------------------------------------
    protected String fromICalString(
        String s
    ) {
        String t = s.replace("\\\\", "\\");
        t = t.replace("\\;", ";");
        t = t.replace("\\,", ",");
        t = t.replace("\\\"", "\"");
        return t;
    }
    
    //-------------------------------------------------------------------------
    public Activity importItem(
        String ical,
        Map<String,String> fields,
        Activity activity,
        org.opencrx.kernel.account1.jmi1.Segment accountSegment,
        short locale,
        List<String> errors,
        List<String> report,
        boolean isEMailAddressLookupCaseInsensitive,
        boolean isEMailAddressLookupIgnoreDisabled
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(accountSegment);
        // Prepare attendees
        List<EMailAddress> attendees = new ArrayList<EMailAddress>();
        List<Short> attendeeRoles = new ArrayList<Short>();
        int count = 0;
        while(fields.get("ATTENDEE[" + count + "]") != null) {
            String attendeeAsString = fields.get("ATTENDEE[" + count + "]");
            if(
                (attendeeAsString.indexOf("MAILTO:") >= 0) ||
                (attendeeAsString.indexOf("mailto:") >= 0)
            ) {
                BasicObject attendee = this.getAttendeeAsContact(
                    attendeeAsString,
                    accountSegment,
                    locale,
                    report
                );
                if(attendee != null) {
                	int pos = attendeeAsString.indexOf("MAILTO:");
                    String emailPrefInternet = attendeeAsString.substring(pos + 7);                	
                	List<EMailAddress> emailAddresses = Accounts.getInstance().lookupEmailAddress(
                		pm, 
                		activity.refGetPath().get(2), 
                		activity.refGetPath().get(4), 
                		emailPrefInternet,
                        isEMailAddressLookupCaseInsensitive,
                        isEMailAddressLookupIgnoreDisabled
                	);
                	if(!emailAddresses.isEmpty()) {
                		attendees.add(
                			emailAddresses.iterator().next()
                		);
                        attendeeRoles.add(
                            (attendeeAsString.indexOf("ROLE=OPT-PARTICIPANT") >= 0) || (attendeeAsString.indexOf("role=opt-participant") >= 0) ? 
                            	new Short((short)PARTY_TYPE_OPTIONAL) : 
                            	new Short((short)PARTY_TYPE_REQUIRED)
                        );
                	}
                }
            }
            count++;
        }
        if(!errors.isEmpty()) {
            return null;
        }
        AppLog.trace("attendees=", attendees);
        // Timezone and date/time formatter
        String tzid = fields.get("TZID");
        TimeZone tz = TimeZone.getDefault();
        if(tzid != null) {
            String[] tzids = TimeZone.getAvailableIDs();
            for(int i = 0; i < tzids.length; i++) {
                if(tzid.endsWith(tzids[i])) {
                    tz = TimeZone.getTimeZone(tzids[i]);
                    break;
                }
            }
        }
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATETIME_FORMAT);
        dateTimeFormatter.setLenient(false);
        dateTimeFormatter.setTimeZone(tz);        
        // externalLink
        boolean hasIcalUid = false;
        List<String> externalLinks = activity.getExternalLink();
        String icalUid = fields.get("UID") == null ? 
        	activity.refGetPath().getBase() : 
        	fields.get("UID");
        for(int i = 0; i < externalLinks.size(); i++) {
            if((externalLinks.get(i)).startsWith(ICAL_SCHEMA)) {
                externalLinks.set(
                    i,
                    ICAL_SCHEMA + icalUid
                );
                hasIcalUid = true;
                break;
            }
        }
        if(!hasIcalUid) {
            externalLinks.add(
                ICAL_SCHEMA + icalUid    
            );
        }
        // Update activity according to ical fields
        String s = fields.get("DTSTART");
        if((s != null) && (s.length() > 0)) {
            try {
                activity.setScheduledStart(
                	this.getUtcDate(
                        s,
                        dateTimeFormatter
                    )
                );
            } 
            catch(Exception e) {
                errors.add("DTSTART (" + s + ")");
            }
        }
        s = fields.get("DTEND");
        if((s != null) && (s.length() > 0)) {
            try {
                activity.setScheduledEnd(
                	this.getUtcDate(
                        s, 
                        dateTimeFormatter
                    )
                );
            } 
            catch(Exception e) {
                errors.add("DTEND (" + s + ")");
            }
        }
        s = fields.get("DUE");
        if((s != null) && (s.length() > 0)) {
            try {
                activity.setDueBy(
                	this.getUtcDate(
                        s, 
                        dateTimeFormatter
                    )
                );
            } 
            catch(Exception e) {
                errors.add("DUE (" + s + ")");
            }
        }
        s = fields.get("COMPLETED");
        if((s != null) && (s.length() > 0)) {
            try {
                activity.setActualEnd(
                	this.getUtcDate(
                        s, 
                        dateTimeFormatter
                    )
                );
            } 
            catch(Exception e) {
                errors.add("COMPLETED (" + s + ")");
            }
        }
        else {
            activity.setActualEnd(null);            
        }
        s = fields.get("PRIORITY");
        if((s != null) && (s.length() > 0)) {
            try {
                int priority = 1;
                switch(Integer.valueOf(s).intValue()) {
                    case 1: priority = 5; break; // immediate
                    case 2: priority = 4; break; // urgent
                    case 3:
                    case 4: priority = 3; break; // high
                    case 5: 
                    case 6:
                    case 7:
                    case 8: priority = 2; break; // normal
                    case 9: priority = 1; break; // low
                }
                activity.setPriority(new Short((short)priority));
            } 
            catch(Exception e) {
                errors.add("PRIORITY (" + s + ")");
            }
        }
        s = fields.get("SUMMARY");
        if((s != null) && (s.length() > 0)) {
            int posComment = s.indexOf(LINE_COMMENT_INDICATOR);
            String name =  posComment > 0 ? 
                s.substring(0, posComment) : 
                s;
            name = this.fromICalString(name);
            // Limit name to 1000 chars
            if(name.length() > 1000) {
                name = name.substring(0, 1000);
            }
            activity.setName(name);
        }
        s = fields.get("DESCRIPTION");
        if((s != null) && (s.length() > 0)) {
            String temp = "";
            int pos = 0;
            while((pos = s.indexOf("\\n")) >= 0) {
                temp += temp.length() == 0 ? "" : "\n";
                temp += s.substring(0, pos);
                s = s.substring(pos + 2);
            }
            temp += temp.length() == 0 ? "" : "\n";
            temp += s;
            // Limit description to 1000 chars
            String description = this.fromICalString(temp);
            if(description.length() > 1000) {
                description = description.substring(0, 1000);
            }
            activity.setDescription(description);
        }
        s = fields.get("LOCATION");
        if((s != null) && (s.length() > 0)) {
            activity.setLocation(
            	this.fromICalString(s)
            );
        }
        s = fields.get("ORGANIZER");
        if((s != null) && (s.length() > 0)) {
            BasicObject organizer = this.getAttendeeAsContact(
                s.startsWith("MAILTO:") ?  
                	s : 
                	"MAILTO:" + s,
                accountSegment,
                locale,
                report
            );
            if(organizer != null) {
                activity.setAssignedTo(
                    (Contact)organizer
                );
            }
        }
        // ical
        activity.setIcal(ical);        
        if(!errors.isEmpty()) {
            return null;
        }        
        report.add("Update activity");
        // Get activity's parties
        List<AbstractActivityParty> participants = new ArrayList<AbstractActivityParty>();
        if(activity instanceof EMail) {
        	Collection<AbstractEMailRecipient> c = ((EMail)activity).getEmailRecipient();
        	participants.addAll(c);
        }
        else if(activity instanceof Incident) {
        	Collection<IncidentParty> c = ((Incident)activity).getIncidentParty();
        	participants.addAll(c);
        }
        else if(activity instanceof Mailing) {
        	Collection<MailingRecipient> c = ((Mailing)activity).getMailingRecipient();
        	participants.addAll(c);
        }
        else if(activity instanceof Meeting) {
        	Collection<MeetingParty> c = ((Meeting)activity).getMeetingParty();
        	participants.addAll(c);
        }
        else if(activity instanceof PhoneCall) {
        	Collection<AbstractPhoneCallRecipient> c = ((PhoneCall)activity).getPhoneCallRecipient();
        	participants.addAll(c);
        }
        else if(activity instanceof Task) {
        	Collection<TaskParty> c = ((Task)activity).getTaskParty();
        	participants.addAll(c);
        }
        List<Path> existingParties = new ArrayList<Path>();
        for(AbstractActivityParty participant: participants) {
        	try {
        		if(participant.refGetValue("party") != null) {
	                existingParties.add(
	                    ((RefObject_1_0)participant.refGetValue("party")).refGetPath()
	                );        			
        		}
        	}
        	catch(Exception e) {}
        }
        // Add attendees
        int ii = 0;
        for(EMailAddress attendee: attendees) {
            if(activity instanceof EMail) {
            	Path partyIdentity = attendee.refGetPath();
                if(!existingParties.contains(partyIdentity)) {
                	EMailRecipient participant = pm.newInstance(EMailRecipient.class);
                    participant.refInitialize(false, false);
                	participant.setPartyType((short)PARTY_TYPE_NA);
                    participant.setParty(attendee);
                    ((EMail)activity).addEmailRecipient(
                    	false,
                    	this.getUidAsString(),
                    	participant
                    );
                }
            }
            else if(activity instanceof Incident) {
            	Path partyIdentity = attendee.refGetPath().getParent().getParent();
                if(!existingParties.contains(partyIdentity)) {
	            	IncidentParty participant = pm.newInstance(IncidentParty.class);
	                participant.refInitialize(false, false);
	            	participant.setPartyType((short)PARTY_TYPE_NA);
	                participant.setParty((Account)pm.getObjectById(partyIdentity));
	                ((Incident)activity).addIncidentParty(
	                	false,
	                	this.getUidAsString(),
	                	participant
	                );
                }
            }
            else if(activity instanceof Mailing) {
            	// Can not map to party. Mailings need postal addresses 
            	// whereas ICal attendees are E-mail addresses
            }
            else if(activity instanceof Meeting) {
            	Path partyIdentity = attendee.refGetPath().getParent().getParent();
                if(!existingParties.contains(partyIdentity)) {
	            	MeetingParty participant = pm.newInstance(MeetingParty.class);
	                participant.refInitialize(false, false);
	                participant.setPartyType(attendeeRoles.get(ii));                            
	                participant.setParty((Account)pm.getObjectById(partyIdentity));
	                ((Meeting)activity).addMeetingParty(
	                	false,
	                	this.getUidAsString(),
	                	participant
	                );
                }
            }
            else if(activity instanceof PhoneCall) {
            	// Can not map to party. PhoneCalls need phone numbers 
            	// whereas ICal attendees are E-mail addresses
            }
            else if(activity instanceof Task) {
            	Path partyIdentity = attendee.refGetPath().getParent().getParent();
                if(!existingParties.contains(partyIdentity)) {
	            	TaskParty participant = pm.newInstance(TaskParty.class);
	                participant.refInitialize(false, false);
	            	participant.setPartyType((short)PARTY_TYPE_NA);
	                participant.setParty((Account)pm.getObjectById(partyIdentity));
	                ((Task)activity).addTaskParty(
	                	false,
	                	this.getUidAsString(),
	                	participant
	                );
                }
            }
            ii++;
            report.add("Create party");
        }
        return activity;
    }

    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final String DATETIME_FORMAT =  "yyyyMMdd'T'HHmmss";
    public static final String DATE_FORMAT =  "yyyyMMdd";
    public static final String PROD_ID = "//OPENCRX//NONSGML Version 1//EN";
    public static final String MIME_TYPE = "text/calendar";
    public static final int MIME_TYPE_CODE = 4;
    public final static String ICAL_SCHEMA = "ICAL:";    
    public static final Short USAGE_EMAIL_PRIMARY = new Short((short)300);
    public static final String LINE_COMMENT_INDICATOR = " //";
    public static final int PARTY_TYPE_NA = 0;
    public static final int PARTY_TYPE_REQUIRED = 410;
    public static final int PARTY_TYPE_OPTIONAL = 420;

    public static final short ICAL_TYPE_VTODO = 2;
    public static final short ICAL_TYPE_VEVENT = 1;
    public static final short ICAL_TYPE_NA = 0;
    
}

//--- End of File -----------------------------------------------------------

