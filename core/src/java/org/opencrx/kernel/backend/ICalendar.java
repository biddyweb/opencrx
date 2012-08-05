/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: ICalendar.java,v 1.120 2011/12/18 22:15:34 wfro Exp $
 * Description: ICalendar
 * Revision:    $Revision: 1.120 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2011/12/18 22:15:34 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2011, CRIXP Corp., Switzerland
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.account1.jmi1.EMailAddress;
import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.cci2.EMailRecipientQuery;
import org.opencrx.kernel.activity1.cci2.IncidentPartyQuery;
import org.opencrx.kernel.activity1.cci2.MeetingPartyQuery;
import org.opencrx.kernel.activity1.cci2.TaskPartyQuery;
import org.opencrx.kernel.activity1.jmi1.Absence;
import org.opencrx.kernel.activity1.jmi1.AbstractActivityParty;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.ActivityGroup;
import org.opencrx.kernel.activity1.jmi1.EMail;
import org.opencrx.kernel.activity1.jmi1.EMailRecipient;
import org.opencrx.kernel.activity1.jmi1.ExternalActivity;
import org.opencrx.kernel.activity1.jmi1.Incident;
import org.opencrx.kernel.activity1.jmi1.IncidentParty;
import org.opencrx.kernel.activity1.jmi1.Mailing;
import org.opencrx.kernel.activity1.jmi1.Meeting;
import org.opencrx.kernel.activity1.jmi1.MeetingParty;
import org.opencrx.kernel.activity1.jmi1.NewActivityParams;
import org.opencrx.kernel.activity1.jmi1.NewActivityResult;
import org.opencrx.kernel.activity1.jmi1.PhoneCall;
import org.opencrx.kernel.activity1.jmi1.Task;
import org.opencrx.kernel.activity1.jmi1.TaskParty;
import org.opencrx.kernel.backend.Activities.ActivityClass;
import org.opencrx.kernel.backend.Activities.ActivityState;
import org.opencrx.kernel.backend.Activities.PartyStatus;
import org.opencrx.kernel.backend.Activities.PartyType;
import org.opencrx.kernel.base.jmi1.ImportParams;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.utils.ActivityQueryHelper;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.io.QuotaByteArrayOutputStream;
import org.openmdx.base.jmi1.BasicObject;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.log.SysLog;
import org.w3c.format.DateTimeFormat;

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
	public static class ICalField {
		
		public ICalField(
			String name,
			String value,
			Map<String,List<String>> parameters
		) {
			this.name = name;
			this.value = value;
			this.parameters = parameters;
		}

		public static ICalField getField(
			String name,
			String parameterName,
			List<String> parameterValues,
			Map<String,ICalField> ical
		) {
			List<ICalField> matchingFields = ICalField.findFields(name, parameterName, parameterValues, ical);
			return matchingFields.isEmpty() ?
				null :
					matchingFields.get(0);
		}

		public static List<ICalField> findFields(
			String name,
			String parameterName,
			List<String> parameterValues,			
			Map<String,ICalField> ical
		) {
			List<ICalField> matchingFields = new ArrayList<ICalField>();
			for(ICalField field: ical.values()) {
				if(
					name.equals(field.getName()) && 
					(parameterName == null || (field.getParameters().get(parameterName) != null &&
					field.getParameters().get(parameterName).containsAll(parameterValues)))
				) {
					matchingFields.add(field);
				}
			}
			return matchingFields;
		}
		
		public static String getFieldValue(
			String name,
			String parameterName,
			List<String> parameterValues,
			Map<String,ICalField> ical			
		) {
			ICalField field = getField(
				name,
				parameterName,
				parameterValues,
				ical
			);
			return field == null ? null : field.getValue();
		}
		
		public static String getFieldValue(
			String name,
			Map<String,ICalField> ical			
		) {
			return getFieldValue(
				name,
				null,
				null,
				ical
			);
		}
		
	    public TimeZone getTimeZone(
	    ) {
	        List<String> tzids = this.getParameters().get("TZID");
	        TimeZone tz = TimeZone.getDefault();
	        if(tzids != null && !tzids.isEmpty()) {
	        	String tzid = tzids.get(0).toUpperCase();
	            String[] availableIds = TimeZone.getAvailableIDs();
	            for(int i = 0; i < availableIds.length; i++) {
	                if(tzid.endsWith(availableIds[i].toUpperCase())) {
	                    tz = TimeZone.getTimeZone(availableIds[i]);
	                    break;
	                }
	            }
	        }
	        return tz;
	    }
		
		public String getName(
		) {
			return this.name;
		}
		
		public String getValue(
		) {
			return this.value;
		}
		
		public void setValue(
			String value
		) {
			this.value = value;
		}
		
		public Map<String,List<String>> getParameters(
		) {
			return this.parameters;
		}

		@Override
        public boolean equals(
        	Object obj
        ) {
			if(obj instanceof ICalField) {
				ICalField that = (ICalField)obj;
				return 
					this.name.equals(that.name) && 
					this.parameters.equals(that.parameters) &&
					this.value.equals(that.value);
			} else {
				return super.equals(obj);
			}
        }
		
		@Override
        public String toString(
        ) {
			return this.name + this.parameters.toString() + ":" + this.value;
        }

		private final String name;
		private String value;
		private final Map<String,List<String>> parameters;
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
            dtStart = DateTimeFormat.BASIC_UTC_FORMAT.format(activity.getScheduledStart());
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
            dtEnd = DateTimeFormat.BASIC_UTC_FORMAT.format(activity.getScheduledEnd());
        }
        // DUE
        String dueBy = null;
        if(activity.getDueBy() != null) {
            dueBy = DateTimeFormat.BASIC_UTC_FORMAT.format(activity.getDueBy());
        }
        // COMPLETED
        String completed = null;
        if(activity.getActualEnd() != null) {
            completed = DateTimeFormat.BASIC_UTC_FORMAT.format(activity.getActualEnd());
        }
        // LAST-MODIFIED
        String lastModified = DateTimeFormat.BASIC_UTC_FORMAT.format(new Date());
        // PRIORITY
        Number priority = activity.getPriority();
        // SUMMARY (append activity number)
        String summary = activity.getName() == null ? 
            "" : 
            activity.getName();
        summary += activity.getActivityNumber() == null ? 
            "" : 
            	Base.COMMENT_SEPARATOR_EOT + " #" + activity.getActivityNumber();            
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
            if(Boolean.TRUE.equals(activity.isDisabled())) {
                status = "CANCELLED";
            }
            else if(percentComplete == null || percentComplete.intValue() == 0) {
            	status = "NEEDS-ACTION";
            }
            else if(percentComplete != null && percentComplete.intValue() >= 100) {
            	status = activity.getActivityState() == ActivityState.CANCELLED.getValue() ?
            		"CANCELLED" :
            			"COMPLETED";
            }
            else {
                status = "IN-PROCESS";
            }
        }
        // VEVENT
        else {
            Number percentComplete = activity.getPercentComplete();
            if(Boolean.TRUE.equals(activity.isDisabled())) {
                status = "CANCELLED";
            }
            else if(percentComplete == null || percentComplete.intValue() == 0) {
                status = "TENTATIVE";
            }
            else if(percentComplete != null && percentComplete.intValue() < 100) {
                status = "CONFIRMED";
            }
            else {
            	status = activity.getActivityState() == ActivityState.CANCELLED.getValue() ?
            		"CANCELLED" :
            			"COMPLETED";                	
            }
        }
        // Attendees
        List<String> attendees = new ArrayList<String>();
        List<AbstractActivityParty> parties = Activities.getInstance().getActivityParties(activity); 
        String organizerEMail = null;
        for(AbstractActivityParty party: parties) {
        	RefObject_1_0 partyHolder = null;
        	try {
        		partyHolder = (RefObject_1_0)party.refGetValue("party");
        	} catch(Exception e) {}
            if(partyHolder != null) {
                try {
                    Account account = null;
                    String emailAddress = null;
                    // Party is Contact
                    if(partyHolder instanceof Account) {
                        account = (Account)partyHolder;
                        emailAddress = Accounts.getInstance().getPrimaryBusinessEMail(
                            account,
                            party.getEmailHint()
                        );
                    }
                    // Party is address
                    else if(partyHolder instanceof EMailAddress) {
                        account = (Account)pm.getObjectById(
                            partyHolder.refGetPath().getParent().getParent()
                        );
                        emailAddress = ((EMailAddress)partyHolder).getEmailAddress();
                    }
                    if(emailAddress != null) {
                    	if(
                    		party.getPartyType() == PartyType.ORGANIZER.getValue() ||
                    		party.getPartyType() == PartyType.EMAIL_FROM.getValue()
                    	) {
                    		organizerEMail = emailAddress;
                    	}
                    	else {
		                    String partyType = null;
		                    if(
		                    	party.getPartyType() == PartyType.OPTIONAL.getValue() ||
		                    	party.getPartyType() == PartyType.EMAIL_CC.getValue()
		                    ) {
	                    		partyType = "OPT-PARTICIPANT";
		                    }
		                    else if(
		                    	party.getPartyType() == PartyType.REQUIRED.getValue() || 
		                    	party.getPartyType() == PartyType.EMAIL_TO.getValue()		                    	
		                    ) {
	                    		partyType = "REQ-PARTICIPANT";
		                    }
		                    else if(party.getPartyType() == PartyType.EMAIL_BCC.getValue()) {
		                    	// do not add as attendee
		                    }
		                    else {
			                    partyType = "NON-PARTICIPANT";
		                    }
		                    if(partyType != null) {
			                    String partyStatus = null;
			                    if(party.getPartyStatus() == PartyStatus.NEEDS_ACTION.getValue()) {
			                    	partyStatus = "NEEDS-ACTION";
			                    } else if(party.getPartyStatus() == PartyStatus.ACCEPTED.getValue()) {
		                    		partyStatus = "ACCEPTED";
			                    } else if(party.getPartyStatus() == PartyStatus.DECLINED.getValue()) {
		                    		partyStatus = "DECLINED";
			                    } else if(party.getPartyStatus() == PartyStatus.TENTATIVE.getValue()) {
		                    		partyStatus = "TENTATIVE";
			                    } else if(party.getPartyStatus() == PartyStatus.DELEGATED.getValue()) {
		                    		partyStatus = "DELEGATED";
			                    } else if(party.getPartyStatus() == PartyStatus.COMPLETED.getValue()) {
		                    		partyStatus = "COMPLETED";
			                    }
		                        String fullName = account == null ?
		                        	null :
		                        	account.getFullName();
		                        if(fullName == null || fullName.startsWith(SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR)) {
		                            attendees.add(
		                                ";CN=" + emailAddress + (partyStatus == null ? "" : ";PARTSTAT=" + partyStatus) + ";ROLE=" + partyType + ";RSVP=TRUE:MAILTO:" + emailAddress
		                            );          
		                        }
		                        else {
		                            attendees.add(
		                                ";CN=\"" + fullName + " (" + emailAddress + ")\"" + (partyStatus == null ? "" : ";PARTSTAT=" + partyStatus) + ";ROLE=" + partyType + ";RSVP=TRUE:MAILTO:" + emailAddress
		                            );            
		                        }
		                    }
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
        if(organizerEMail == null) {
        	if(activity.getAssignedTo() != null) {
	            organizerEMail = Accounts.getInstance().getPrimaryBusinessEMail(
	                activity.getAssignedTo(),
	                null
	            );
        	}
        	else if(activity.getReportingContact() != null) {
                organizerEMail = Accounts.getInstance().getPrimaryBusinessEMail(
                	activity.getReportingContact(),
                    null
                );
        	}
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
                "UID:" + UUIDConversion.toUID(uid) + "\n" +
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
                "CLASS:PRIVATE\n" +
                (icalType == ICalendar.ICAL_TYPE_VTODO ? "END:VTODO\n" : "END:VEVENT\n") +
                "END:VCALENDAR";            
        }
        try {
            QuotaByteArrayOutputStream targetIcalBos = new QuotaByteArrayOutputStream(ICalendar.class.getName());
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
                	boolean isAllDay =
                		((dtStart != null) && (dtStart.endsWith("T000000.000Z") || dtStart.endsWith("T000000Z"))) &&
                		((dtEnd != null) && (dtEnd.endsWith("T000000.000Z") || dtEnd.endsWith("T000000Z")));
                	// DTSTART
                	if(dtStart != null) {
                		if(isAllDay) {
                			targetIcal.println("DTSTART;VALUE=DATE:" + dtStart.substring(0, 8));                            
                        }
                        else {
                            targetIcal.println("DTSTART:" + dtStart.substring(0, 15) + "Z");
                        }
                	}
                	// DTEND
                	if(dtEnd != null) {
                		if(isAllDay) {
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
                    targetIcal.println("DTSTAMP:" + DateTimeFormat.BASIC_UTC_FORMAT.format(new Date()).substring(0, 15) + "Z");
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
                    if(organizerEMail != null) {
                        targetIcal.println("ORGANIZER:MAILTO:" + organizerEMail);
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
        Map<String,ICalField> ical
    ) {
        for(Iterator<String> i = ical.keySet().iterator(); i.hasNext(); ) {
            String prop = i.next();
            if(prop.startsWith("X-")) {
           		i.remove();
            }
        }
    }

    //-------------------------------------------------------------------------
    public Map<String,ICalField> parseICal(
        BufferedReader reader,
        StringBuilder icalAsString
    ) throws ServiceException {
        Map<String,ICalField> ical = new HashMap<String,ICalField>();
        List<String> lines = new ArrayList<String>();
        try {
	        String s = null;
	        while((s = reader.readLine()) != null) {
	        	lines.add(s);
	        }
        } catch(Exception e) {}
        /**
         * Calendars with at most one event can be imported
         * The event is mapped to an activity
         */
        boolean isEvent = false;
        int nEvents = 0;
        try {
        	int l = 0;
        	parse: while(l < lines.size()) {
        		String line = lines.get(l++);
        		while(l < lines.size() && lines.get(l).startsWith(" ")) {
        			line += lines.get(l++).substring(1);
        		}
	            // Skip URLs AND ATTACHs if they reference an internal object.
	            if(
	                (line.startsWith("URL:") || line.startsWith("url:") || line.startsWith("ATTACH:") || line.startsWith("attach:")) &&
	                Base.getInstance().isAccessUrl(line.substring(line.indexOf(":") + 1))
	            ) {
	            	continue parse;
	            }
                icalAsString.append(line).append("\n");
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
                	if((pos = line.indexOf(":")) >= 0) {
                    	String qualifiedFieldName = line.substring(0, pos).toUpperCase();
                        String[] fieldNameParts = qualifiedFieldName.split(";");
                        Map<String,List<String>> parameters = new HashMap<String,List<String>>();
                        for(int i = 1; i < fieldNameParts.length; i++) {
                        	String fieldNamePart = fieldNameParts[i];
                        	String parameterName = "TYPE";
                        	String[] parameterValues = new String[]{};
                        	if(fieldNamePart.indexOf("=") > 0) {
                        		int index = fieldNamePart.indexOf("=");
                        		parameterName = fieldNamePart.substring(0, index);
                        		parameterValues = fieldNamePart.substring(index + 1).split(",");
                        	} else {
                        		parameterName = "TYPE";
                        		parameterValues = fieldNamePart.split(",");
                        	}
                        	if(parameters.get(parameterName) != null) {
                        		parameters.get(parameterName).addAll(
                        			Arrays.asList(parameterValues)
                        		);
                        	} else {
                        		parameters.put(
                        			parameterName, 
                        			new ArrayList<String>(Arrays.asList(parameterValues))
                        		);
                        	}
                        }
                        ical.put(
                            qualifiedFieldName,
                            new ICalField(
                            	fieldNameParts[0],
                            	line.substring(pos + 1, line.length()).replace("\\n", "\n").replace("\\", ""),
                            	parameters
                            )
                        );
                	}
	            }                 
	        }
        } catch(Exception e) {
        	throw new ServiceException(e);
        }
        this.removeProprietaryProperties(ical);
        return ical;
    }
    
    //-------------------------------------------------------------------------
    public BasicObject importItem(
        byte[] item,
        Activity activity,
        short locale,
        List<String> errors,
        List<String> report
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activity);    	
    	org.opencrx.kernel.account1.jmi1.Segment accountSegment = Accounts.getInstance().getAccountSegment(pm, activity.refGetPath().get(2), activity.refGetPath().get(4));
        InputStream is = new ByteArrayInputStream(item);
        BufferedReader reader = null;
        try {
        	reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch(UnsupportedEncodingException e) {}
        StringBuilder ical = new StringBuilder();
        Map<String,ICalField> icalFields = this.parseICal(
            reader,
            ical
        );
        SysLog.trace("ICalendar", icalFields);
        return this.importItem(
            ical.toString(),
            icalFields,
            activity,
            accountSegment,
            locale,
            errors,
            report
        );
    }

    //-------------------------------------------------------------------------
    protected Account getAttendeeAsContact(
        String attendeeAsString,
        org.opencrx.kernel.account1.jmi1.Segment accountSegment,
        Contact existingContact,
        short locale,
        List<String> report
    ) throws ServiceException {
        int pos = attendeeAsString.indexOf("MAILTO:");
        if(pos < 0) {
            pos = attendeeAsString.indexOf("mailto:");
        }
        String emailInternet = attendeeAsString.substring(pos + 7);
        PersistenceManager pm = JDOHelper.getPersistenceManager(accountSegment);
        String providerName = accountSegment.refGetPath().get(2);
        String segmentName = accountSegment.refGetPath().get(4);
        List<EMailAddress> emailAddresses = Accounts.getInstance().lookupEmailAddress(
        	pm, 
        	providerName, 
        	segmentName, 
        	emailInternet
        );
        if(!emailAddresses.isEmpty()) {
        	if(existingContact != null) {
        		for(EMailAddress emailAddress: emailAddresses) {
        			if(emailAddress.refGetPath().startsWith(existingContact.refGetPath())) {
        				return existingContact;
        			}
        		}
        	}
        	EMailAddress emailAddress = emailAddresses.iterator().next();        	
        	return (Account)pm.getObjectById(emailAddress.refGetPath().getParent().getParent());
        } else {
        	return null;
        }
    }

    //-------------------------------------------------------------------------
    protected Date getUtcDate(
        String dateTime,
        TimeZone tz
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
        else {
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATETIME_FORMAT);
            dateTimeFormatter.setLenient(false);
            dateTimeFormatter.setTimeZone(tz);        	
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
        String icalAsString,
        Map<String,ICalField> ical,
        Activity activity,
        org.opencrx.kernel.account1.jmi1.Segment accountSegment,
        short locale,
        List<String> errors,
        List<String> report
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(accountSegment);
		String providerName = activity.refGetPath().get(2);
		String segmentName = activity.refGetPath().get(4);
        // ATTENDEEs
        List<EMailAddress> partyEmails = new ArrayList<EMailAddress>();
        List<PartyType> partyTypes = new ArrayList<PartyType>();
        List<PartyStatus> partyStatuses = new ArrayList<PartyStatus>();
        for(ICalField attendee: ICalField.findFields("ATTENDEE", null, null, ical)) {
            if(
                (attendee.getValue().indexOf("MAILTO:") >= 0) ||
                (attendee.getValue().indexOf("mailto:") >= 0)
            ) {
            	int pos = attendee.getValue().indexOf("MAILTO:");
            	if(pos < 0) {
            		pos = attendee.getValue().indexOf("mailto:");
            	}
            	if(pos >= 0) {
	                String email = attendee.getValue().substring(pos + 7);
	            	List<EMailAddress> emailAddresses = Accounts.getInstance().lookupEmailAddress(
	            		pm, 
	            		providerName, 
	            		segmentName, 
	            		email,
	            		true // forceCreate
	            	);
	            	if(!emailAddresses.isEmpty()) {
	            		partyEmails.add(
	            			emailAddresses.iterator().next()
	            		);
	            		if(attendee.getParameters().get("ROLE") != null && !attendee.getParameters().get("ROLE").isEmpty()) {
	            			if("NON-PARTICIPANT".equals(attendee.getParameters().get("ROLE").get(0))) {
	            				partyTypes.add(PartyType.NA);
	            			} else if("OPT-PARTICIPANT".equals(attendee.getParameters().get("ROLE").get(0))) {
	            				partyTypes.add(PartyType.OPTIONAL);	            				
	            			} else {
		            			partyTypes.add(PartyType.REQUIRED);	            				
	            			}
	            		} else {
	            			partyTypes.add(PartyType.REQUIRED);
	            		}
	                    if(attendee.getParameters().get("PARTSTAT") != null && !attendee.getParameters().get("PARTSTAT").isEmpty()) {
		                    if("NEEDS-ACTION".equals(attendee.getParameters().get("PARTSTAT").get(0))) {
		                    	partyStatuses.add(PartyStatus.NEEDS_ACTION);
		                    } else if("ACCEPTED".equals(attendee.getParameters().get("PARTSTAT").get(0))) {
		                    	partyStatuses.add(PartyStatus.ACCEPTED);
		                    } else if("DECLINED".equals(attendee.getParameters().get("PARTSTAT").get(0))) {
		                    	partyStatuses.add(PartyStatus.DECLINED);
		                    } else if("TENTATIVE".equals(attendee.getParameters().get("PARTSTAT").get(0))) {
		                    	partyStatuses.add(PartyStatus.TENTATIVE);
		                    } else if("DELEGATED".equals(attendee.getParameters().get("PARTSTAT").get(0))) {
		                    	partyStatuses.add(PartyStatus.DELEGATED);
		                    } else if("COMPLETED".equals(attendee.getParameters().get("PARTSTAT").get(0))) {
		                    	partyStatuses.add(PartyStatus.COMPLETED);
		                    } else {
		                    	partyStatuses.add(PartyStatus.NA);
		                    }
	                    } else {
	                    	partyStatuses.add(PartyStatus.NA);
	                    }
	            	}
            	}
            }
        }
        if(!errors.isEmpty()) {
            return null;
        }
        // ORGANIZER 
        String s = ICalField.getFieldValue("ORGANIZER", ical);
        if((s != null) && !s.isEmpty()) {
        	int pos = s.indexOf("MAILTO:");
        	if(pos < 0) {
        		pos = s.indexOf("mailto:");
        	}
        	if(pos >= 0) {
                String email = s.substring(pos + 7);
            	List<EMailAddress> emailAddresses = Accounts.getInstance().lookupEmailAddress(
            		pm, 
            		providerName, 
            		segmentName, 
            		email,
            		true // forceCreate
            	);
            	if(!emailAddresses.isEmpty()) {
            		partyEmails.add(
            			emailAddresses.iterator().next()
            		);
            		partyTypes.add(PartyType.ORGANIZER);
            		partyStatuses.add(PartyStatus.ACCEPTED);
            	}
        	}
        }
        // externalLink
        boolean hasIcalUid = false;
        boolean hasIcalRecurrenceId = false;
        List<String> externalLinks = activity.getExternalLink();
        String icalUid = ical.get("UID") == null ? 
        	activity.refGetPath().getBase() : 
        		ICalField.getFieldValue("UID", ical);
        String icalRecurrenceId = ICalField.getFieldValue("RECURRENCE-ID", ical);
        for(int i = 0; i < externalLinks.size(); i++) {
            if((externalLinks.get(i)).startsWith(ICAL_SCHEMA)) {
                externalLinks.set(
                    i,
                    ICAL_SCHEMA + icalUid
                );
                hasIcalUid = true;
            }
            else if((externalLinks.get(i)).startsWith(ICAL_RECURRENCE_ID_SCHEMA)) {
                externalLinks.set(
                    i,
                    ICAL_RECURRENCE_ID_SCHEMA + icalRecurrenceId
                );
                hasIcalRecurrenceId = true;
            }
        }
        if(!hasIcalUid) {
            externalLinks.add(
                ICAL_SCHEMA + icalUid    
            );
        }
        if(!hasIcalRecurrenceId && icalRecurrenceId != null) {
            externalLinks.add(
                ICAL_RECURRENCE_ID_SCHEMA + icalRecurrenceId    
            );
        }
        // DTSTART
        String dtStart = ICalField.getFieldValue("DTSTART", ical);
        if((dtStart != null) && !dtStart.isEmpty()) {
            try {
                activity.setScheduledStart(
                	this.getUtcDate(
                		dtStart,
                        ICalField.getField("DTSTART", null, null, ical).getTimeZone()
                    )
                );
            }
            catch(Exception e) {
                errors.add("DTSTART (" + dtStart + ")");
            }
        }
        // DTEND
        String dtEnd = ICalField.getFieldValue("DTEND", ical);
        if((dtEnd != null) && !dtEnd.isEmpty()) {
            try {
                activity.setScheduledEnd(
                	this.getUtcDate(
                        dtEnd, 
                        ICalField.getField("DTEND", null, null, ical).getTimeZone()
                    )
                );
                // If DTSTART and DTEND are specified as DATETIME prevent that the
                // event is converted to an all-day event. Allow all-day events
                // only if DTSTART and DTEND are specified in as DATE.
                if(
                	activity.getScheduledStart() != null &&
                	(activity.getScheduledEnd().getTime() - activity.getScheduledStart().getTime() == 86400000L) &&
                	(dtStart.length() > 8 || dtEnd.length() > 8)
                ) {
                    activity.setScheduledEnd(
                    	new Date(activity.getScheduledEnd().getTime() - 1000L)
                    );                	
                }
            } 
            catch(Exception e) {
                errors.add("DTEND (" + dtEnd + ")");
            }
        }
        // DUE
        s = ICalField.getFieldValue("DUE", ical);
        if((s != null) && !s.isEmpty()) {
            try {
                activity.setDueBy(
                	this.getUtcDate(
                        s, 
                        ICalField.getField("DUE", null, null, ical).getTimeZone()
                    )
                );
            } 
            catch(Exception e) {
                errors.add("DUE (" + s + ")");
            }
        }
        // COMPLETED
        s = ICalField.getFieldValue("COMPLETED", ical);
        if((s != null) && !s.isEmpty()) {
            try {
                activity.setActualEnd(
                	this.getUtcDate(
                        s, 
                        ICalField.getField("COMPLETED", null, null, ical).getTimeZone()
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
        // PRIORITY
        s = ICalField.getFieldValue("PRIORITY", ical);
        if((s != null) && !s.isEmpty()) {
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
        // SUMMARY
        s = ICalField.getFieldValue("SUMMARY", ical);
        if((s != null) && !s.isEmpty()) {
            int posComment = s.startsWith(Base.COMMENT_SEPARATOR_BOT) ? 0 : s.lastIndexOf(Base.COMMENT_SEPARATOR_EOT);
            String name =  posComment >= 0 ? 
                s.substring(0, posComment) : 
                s;
            name = this.fromICalString(name);
            // Limit name to 1000 chars
            if(name.length() > 1000) {
                name = name.substring(0, 1000);
            }
            activity.setName(name);
        }
        // DESCRIPTION
        s = ICalField.getFieldValue("DESCRIPTION", ical);
        if((s != null) && !s.isEmpty()) {
            int posComment = s.startsWith(Base.COMMENT_SEPARATOR_BOT) ? 0 : s.lastIndexOf(Base.COMMENT_SEPARATOR_EOT);
            s =  posComment >= 0 ? 
                s.substring(0, posComment) : 
                s;        	
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
        // LOCATION
        s = ICalField.getFieldValue("LOCATION", ical);
        if((s != null) && !s.isEmpty()) {
            activity.setLocation(
            	this.fromICalString(s)
            );
        }
        // ical
        activity.setIcal(icalAsString);        
        if(!errors.isEmpty()) {
            return null;
        }        
        report.add("Update activity");
        // Add attendees
        List<Account> accountParties = new ArrayList<Account>();
        for(int i = 0; i < partyEmails.size(); i++) {
        	EMailAddress partyEmail = partyEmails.get(i);
            if(activity instanceof EMail) {
            	EMailRecipientQuery query = (EMailRecipientQuery)pm.newQuery(EMailRecipient.class);
            	query.thereExistsParty().equalTo(partyEmail);
            	List<EMailRecipient> emailRecipients = ((EMail)activity).getEmailRecipient(query);
            	EMailRecipient emailRecipient = emailRecipients.isEmpty() ?
            		null :
            			emailRecipients.iterator().next();
            	if(emailRecipient == null) {
                	emailRecipient = pm.newInstance(EMailRecipient.class);
                	emailRecipient.refInitialize(false, false);
                    ((EMail)activity).addEmailRecipient(
                    	this.getUidAsString(),
                    	emailRecipient
                    );
            	}
            	emailRecipient.setParty(partyEmail);
            	switch(partyTypes.get(i)) {
            		case REQUIRED:
            			emailRecipient.setPartyType(PartyType.EMAIL_TO.getValue());
            			break;
            		case OPTIONAL:
                		emailRecipient.setPartyType(PartyType.EMAIL_CC.getValue());
                		break;
            		case ORGANIZER:
            			emailRecipient.setPartyType(PartyType.EMAIL_FROM.getValue());
            			break;
                	default:
                    	emailRecipient.setPartyType(partyTypes.get(i).getValue());
                    	break;
            	}
            	// Do not overwrite party status in case the supplied party status is NA
            	if(partyStatuses.get(i) != PartyStatus.NA) {
            		emailRecipient.setPartyStatus(partyStatuses.get(i).getValue());
            	}
            	emailRecipient.setEmailHint(partyEmail.getEmailAddress());
            }
            else if(activity instanceof Incident) {
            	Account partyHolder = (Account)pm.getObjectById(partyEmail.refGetPath().getParent().getParent());
            	IncidentPartyQuery query = (IncidentPartyQuery)pm.newQuery(IncidentParty.class);
            	// At most one participant with role ORGANIZER
            	if(partyTypes.get(i) == PartyType.ORGANIZER) {
            		query.partyType().equalTo(PartyType.ORGANIZER.getValue()); 
            	} else {
            		query.thereExistsParty().equalTo(partyHolder);
	            	// Need hint if account is duplicate
	            	if(accountParties.contains(partyHolder)) {
	            		query.thereExistsEmailHint().equalTo(partyEmail.getEmailAddress());
	            	}
            	}
            	accountParties.add(partyHolder);
            	List<IncidentParty> incidentParties = ((Incident)activity).getIncidentParty(query);
            	IncidentParty incidentParty = incidentParties.isEmpty() ? null : incidentParties.iterator().next();
            	if(incidentParty == null) {
            		incidentParty = pm.newInstance(IncidentParty.class);
	                incidentParty.refInitialize(false, false);
	                ((Incident)activity).addIncidentParty(
	                	this.getUidAsString(),
	                	incidentParty
	                );            		
            	}
            	incidentParty.setParty(partyHolder);
            	incidentParty.setPartyType(partyTypes.get(i).getValue());
            	// Do not overwrite party status in case the supplied party status is NA            	
            	if(partyStatuses.get(i) != PartyStatus.NA) {
            		incidentParty.setPartyStatus(partyStatuses.get(i).getValue());
            	}
                incidentParty.setEmailHint(partyEmail.getEmailAddress());
            }
            else if(activity instanceof Mailing) {
            	// Can not map to party. Mailings need postal addresses 
            	// whereas ICal attendees are E-mail addresses
            }
            else if(activity instanceof Meeting) {
            	Account partyHolder = (Account)pm.getObjectById(partyEmail.refGetPath().getParent().getParent());
            	MeetingPartyQuery query = (MeetingPartyQuery)pm.newQuery(MeetingParty.class);
            	if(partyTypes.get(i) == PartyType.ORGANIZER) {
            		query.partyType().equalTo(PartyType.ORGANIZER.getValue());
            	} else {
	            	query.thereExistsParty().equalTo(partyHolder);
	            	if(accountParties.contains(partyHolder)) {
	            		query.thereExistsEmailHint().equalTo(partyEmail.getEmailAddress());
	            	}
            	}
        		accountParties.add(partyHolder);
            	List<MeetingParty> meetingParties = ((Meeting)activity).getMeetingParty(query);
            	MeetingParty meetingParty = meetingParties.isEmpty() ? null : meetingParties.iterator().next();
            	if(meetingParty == null) {
            		meetingParty = pm.newInstance(MeetingParty.class);
	                meetingParty.refInitialize(false, false);
	                ((Meeting)activity).addMeetingParty(
	                	this.getUidAsString(),
	                	meetingParty
	                );            		
            	}
            	meetingParty.setParty(partyHolder);
            	meetingParty.setPartyType(partyTypes.get(i).getValue());
            	// Do not overwrite party status in case the supplied party status is NA            	
            	if(partyStatuses.get(i) != PartyStatus.NA) {
            		meetingParty.setPartyStatus(partyStatuses.get(i).getValue());
            	}
                meetingParty.setEmailHint(partyEmail.getEmailAddress());
            }
            else if(activity instanceof PhoneCall) {
            	// Can not map to party. PhoneCalls need phone numbers 
            	// whereas ICal attendees are E-mail addresses
            }
            else if(activity instanceof Task) {
            	Account partyHolder = (Account)pm.getObjectById(partyEmail.refGetPath().getParent().getParent());
            	TaskPartyQuery query = (TaskPartyQuery)pm.newQuery(TaskParty.class);
            	if(partyTypes.get(i) == PartyType.ORGANIZER) {
            		query.partyType().equalTo(PartyType.ORGANIZER.getValue());
            	} else {
	            	query.thereExistsParty().equalTo(partyHolder);
	            	if(accountParties.contains(partyHolder)) {
	            		query.thereExistsEmailHint().equalTo(partyEmail.getEmailAddress());
	            	}
            	}
        		accountParties.add(partyHolder);
            	List<TaskParty> taskParties = ((Task)activity).getTaskParty(query);
            	TaskParty taskParty = taskParties.isEmpty() ? null : taskParties.iterator().next();
            	if(taskParty == null) {
            		taskParty = pm.newInstance(TaskParty.class);
	                taskParty.refInitialize(false, false);
	                ((Task)activity).addTaskParty(
	                	this.getUidAsString(),
	                	taskParty
	                );
            	}
            	taskParty.setParty(partyHolder);
            	taskParty.setPartyType(partyTypes.get(i).getValue());
            	// Do not overwrite party status in case the supplied party status is NA            	
            	if(partyStatuses.get(i) != PartyStatus.NA) {
            		taskParty.setPartyStatus(partyStatuses.get(i).getValue());
            	}
                taskParty.setEmailHint(partyEmail.getEmailAddress());
            }
        }
        return activity;
    }

    //-----------------------------------------------------------------------
    protected Activity findActivity(
        ActivityQueryHelper activitiesHelper,
        String icalUid,
        String icalRecurrenceId 
    ) {
    	PersistenceManager pm = activitiesHelper.getPersistenceManager();
        ActivityQuery query = (ActivityQuery)pm.newQuery(Activity.class);
        query.thereExistsExternalLink().equalTo(ICalendar.ICAL_SCHEMA + icalUid);
        if(icalRecurrenceId == null) {
        	query.forAllExternalLink().startsNotWith(ICalendar.ICAL_RECURRENCE_ID_SCHEMA);
        }
        else {
            query.thereExistsExternalLink().equalTo(ICalendar.ICAL_RECURRENCE_ID_SCHEMA + icalRecurrenceId);       
        }
        Collection<Activity> activities = activitiesHelper.getFilteredActivities(query);
        if(activities.isEmpty()) {
            query = (ActivityQuery)pm.newQuery(Activity.class);
            query.thereExistsExternalLink().equalTo(ICalendar.ICAL_SCHEMA + icalUid.replace('.', '+'));
            if(icalRecurrenceId == null) {
            	query.forAllExternalLink().startsNotWith(ICalendar.ICAL_RECURRENCE_ID_SCHEMA);
            }            
            else {
            	query.thereExistsExternalLink().equalTo(ICalendar.ICAL_RECURRENCE_ID_SCHEMA + icalRecurrenceId);    
            }
            activities = activitiesHelper.getFilteredActivities(query);
            if(activities.isEmpty()) {
                return null;
            }
            else {
                if(activities.size() > 1) {
                	SysLog.warning("Duplicate activities. Will be handled as not found", activities);
                    return null;
                }
                else {
                    return activities.iterator().next();
                }
            }
        }
        else {
            if(activities.size() > 1) {
            	SysLog.warning("Duplicate activities. Will not update", activities.iterator().next().refMofId());
                return null;
            }
            else {
                return activities.iterator().next();
            }
        }
    }
            
    //-------------------------------------------------------------------------
    public static class PutICalResult {
    	
    	public enum Status { CREATED, UPDATED }
    	
    	public PutICalResult(
    		Status status,
    		String oldUID,
    		String newUID,
    		Activity activity
    	) {
    		this.status = status;
    		this.oldUID = oldUID;
    		this.newUID = newUID;
    		this.activity = activity;
    	}
    	public Status getStatus() {
        	return status;
        }
		public String getOldUID() {
        	return oldUID;
        }
		public String getNewUID() {
        	return newUID;
        }
		public Activity getActivity() {
        	return activity;
        }
		private final Status status;
    	private final String oldUID;
    	private final String newUID;
    	private final Activity activity;    	
    }
    
    public PutICalResult putICal(
    	BufferedReader reader,
    	ActivityQueryHelper activitiesHelper,
    	boolean allowCreation
    ) throws ServiceException {    
    	PersistenceManager pm = activitiesHelper.getPersistenceManager();
    	PutICalResult.Status status = PutICalResult.Status.UPDATED;
    	Activity activity = null;
    	String calUID = null;
    	String newUID = null;
    	String l;
        try {
	        while((l = reader.readLine()) != null) {
	            boolean isEvent = l.startsWith("BEGIN:VEVENT");
	            boolean isTodo = l.startsWith("BEGIN:VTODO");
	            if(isEvent || isTodo) {
	                String calendar = "";
	                calendar += "BEGIN:VCALENDAR\n";
	                calendar += "VERSION:2.0\n";
	                calendar += "PRODID:-" + ICalendar.PROD_ID + "\n";
	                calendar += isEvent ? "BEGIN:VEVENT\n" : "BEGIN:VTODO\n";
	                String recurrenceId = null;
	                boolean isAlarm = false;
	                boolean hasClass = false;
	                while((l = reader.readLine()) != null) {
	                    if(l.startsWith("BEGIN:VALARM")) {
	                    	isAlarm = true;
	                    }
	                	if(!isAlarm) {
                			calendar += l;
                			calendar += "\n";
	                	}
	                    if(l.startsWith("UID:")) {
	                        calUID = l.substring(4);
	                    }
	                    else if(l.startsWith("CLASS:")) {
	                        hasClass = true;
	                    }
	                    else if(l.startsWith("RECURRENCE-ID:")) {
	                        recurrenceId = l.substring(14);
	                    }
	                    else if(l.startsWith("END:VALARM")) {
	                    	isAlarm = false;
	                    }
	                    else if(l.startsWith("END:VEVENT") || l.startsWith("END:VTODO")) {
	                        break;
	                    }
	                }
	                // Default class is confidential
	                if(!hasClass && !isAlarm) {
	                	if(isEvent) {
	                		calendar = calendar.replace("BEGIN:VEVENT", "BEGIN:VEVENT\nCLASS:CONFIDENTIAL");
	                	} else {
	                		calendar = calendar.replace("BEGIN:VEVENT", "BEGIN:VTODO\nCLASS:CONFIDENTIAL");	                		
	                	}
	                }
	                calendar += "END:VCALENDAR\n";
	                SysLog.trace("VCALENDAR", calendar);
	                newUID = calUID;
	                if(calUID != null) {
                        // Calendar contains guard VEVENT. Allow creation of new activities
	                	BasicObject source = activitiesHelper.getSource();
                        if(calUID.equals(source == null ? null : source.refGetPath().getBase())) {
                            allowCreation = true;
                        }
                        else {	                	
		                	SysLog.detail("Lookup activity", calUID);
		                    activity = this.findActivity(
		                        activitiesHelper, 
		                        calUID,
		                        recurrenceId
		                    );
		                    StringBuilder dummy = new StringBuilder();
		                    Map<String,ICalField> newICal = new HashMap<String,ICalField>();
	                    	newICal = this.parseICal(
	                            new BufferedReader(new StringReader(calendar.toString())),
	                            dummy 
	                        );
		                    newICal.remove("LAST-MODIFIED");
		                    newICal.remove("DTSTAMP");                               
		                    newICal.remove("CREATED");                               
		                    dummy.setLength(0);
		                    Map<String,ICalField> oldICal = null;
		                    if(activity == null) {
		                    	boolean rewriteUID = true;
		                    	if(recurrenceId != null && !recurrenceId.isEmpty()) {
		                    		// Try to find activity with supplied UID. If not found
		                    		// rewrite the supplied UID. This way the recurrence event
		                    		// can be correlated with the series event
				                    rewriteUID = this.findActivity(
				                        activitiesHelper, 
				                        calUID,
				                        null // recurrenceId
				                    ) == null;
		                    	}
		                    	if(rewriteUID) {
			                    	int pos1 = calendar.indexOf("UID:");
			                    	if(pos1 > 0) {
			                    		int pos2 = calendar.indexOf("\n", pos1);
			                    		if(pos2 > pos1) {
			                    			newUID = Base.getInstance().getUidAsString();
			                    			calendar =
			                    				calendar.substring(0, pos1) + 
			                    				"UID:" + newUID + "\n" +
			                    				calendar.substring(pos2 + 1);		                    		
			                    		}
			                    	}
		                    	}
		                    }
		                    else {
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
		                    boolean disabledIsModified = activity == null ?
		                    	false : 
		                    	!Utils.areEqual(
		                    		activity.isDisabled(), 
		                    		Boolean.valueOf(activitiesHelper.isDisabledFilter()
		                    	)
                            );
		                    // Update existing activity
		                    if(activity != null) {
		                    	if(!newICal.equals(oldICal) || disabledIsModified) {
			                        try {
			                        	if(!newICal.equals(oldICal)) {
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
			                        	}
			                        	if(disabledIsModified) {
				                            pm.currentTransaction().begin();
				                            // Handle as creation if activity is moved from folder non-disabled to disabled or vice-versa
				                            status = PutICalResult.Status.CREATED;
				                            activity.setDisabled(
				                                Boolean.valueOf(activitiesHelper.isDisabledFilter())
				                            );
				                            pm.currentTransaction().commit();
			                        	}
			                        }
			                        catch(Exception e) {
			                        	new ServiceException(e).log();
			                            try {
			                                pm.currentTransaction().rollback();
			                            } 
			                            catch(Exception e0) {}                                    
			                        }
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
		                        ActivityCreator activityCreator = Activities.getInstance().findActivityCreator(
		                            activityCreators,
		                            isTodo ? 
		                                ActivityClass.TASK.getValue() : 
		                                isEvent ? 
		                                	ActivityClass.MEETING.getValue() : 
		                                		ActivityClass.INCIDENT.getValue()
		                        );
		                        // Priority 2
		                        if(activityCreator == null) {
		                            activityCreator = Activities.getInstance().findActivityCreator(
		                                activityCreators,
		                                isTodo ? 
		                                	ActivityClass.MEETING.getValue() : 
		                                    isEvent ? 
		                                    	ActivityClass.INCIDENT.getValue() : 
		                                    		ActivityClass.INCIDENT.getValue()
		                            );                                    
		                        }
		                        // Priority 3
		                        if(activityCreator == null) {
		                            activityCreator = Activities.getInstance().findActivityCreator(
		                                activityCreators,
		                                isTodo ? 
		                                	ActivityClass.INCIDENT.getValue() : 
		                                    isEvent ? 
		                                    	ActivityClass.INCIDENT.getValue() : 
		                                    		ActivityClass.INCIDENT.getValue()
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
		                                	null, // creationContext
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
		                                    status = PutICalResult.Status.CREATED;
		                                }
		                                catch(Exception e) {
		                                	SysLog.warning("Error importing calendar. Reason is", new String[]{calendar.toString(), e.getMessage()});
		                                    new ServiceException(e).log();
		                                    try {
		                                        pm.currentTransaction().rollback();
		                                    } 
		                                    catch(Exception e0) {}
		                                }
		                            }
		                            catch(Exception e) {
		                            	SysLog.warning("Can not create activity. Reason is", e.getMessage());
		                                new ServiceException(e).log();
		                                try {
		                                    pm.currentTransaction().rollback();
		                                } 
		                                catch(Exception e0) {}
		                            }
		                        }
		                        else {
		                        	SysLog.detail("Skipping calendar. No activity creator found", calendar); 
		                        }
		                    }                            
		                    else {
		                    	SysLog.detail(
		                            "Skipping ", 
		                            new String[]{
		                                "UID: " + calUID, 
		                                "Activity.number: " + (activity == null ? null : activity.refMofId()),
		                                "Activity.modifiedAt:" + (activity == null ? null : activity.getModifiedAt())
		                            }
		                        );
		                    }
                        }
	                }
	            }    	
	        }
        }
        catch (IOException e) {
        	throw new ServiceException(e);
        }       
        return new PutICalResult(
        	status,
        	calUID,
        	newUID,
        	activity
        );
    }
    
	public enum AlarmAction {
		
    	AUDIO((short)10),
		DISPLAY((short)20),
		EMAIL((short)30),
		PROCEDURE((short)40);
		
		private short value;
		
		private AlarmAction(
			short value
		) {
			this.value = value;
		}
		
		public short getValue(
		) {
			return this.value;
		}
		
		static public AlarmAction valueOf(
			short value
		) {
			switch(value) {
				case 10: return AUDIO;
				case 20: return DISPLAY;
				case 30: return EMAIL;
				case 40: return PROCEDURE;
				default: return DISPLAY;
			}
		}
		
	}

    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final String DATETIME_FORMAT =  "yyyyMMdd'T'HHmmss";
    public static final String DATE_FORMAT =  "yyyyMMdd";
    public static final String PROD_ID = "//OPENCRX//NONSGML Version 1//EN";
    public static final String MIME_TYPE = "text/calendar";
    public static final String FILE_EXTENSION = ".ics";
    public static final int MIME_TYPE_CODE = 4;
    public final static String ICAL_SCHEMA = "ICAL:";    
    public final static String ICAL_RECURRENCE_ID_SCHEMA = "ICAL-RECURRENCE-ID:";    
    public static final Short USAGE_EMAIL_PRIMARY = new Short((short)300);
    
    public static final short ICAL_TYPE_VTODO = 2;
    public static final short ICAL_TYPE_VEVENT = 1;
    public static final short ICAL_TYPE_NA = 0;
    
}

//--- End of File -----------------------------------------------------------

