/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: ICalendar.java,v 1.18 2008/02/19 13:38:40 wfro Exp $
 * Description: ICalendar
 * Revision:    $Revision: 1.18 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/02/19 13:38:40 $
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.opencrx.kernel.activity1.jmi1.Activity;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasicObject;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSelectors;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.Directions;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;

public class ICalendar {

    //-------------------------------------------------------------------------
    public ICalendar(
        Backend backend
    ) {
        this.backend = backend;
        this.partyMetadata = new HashMap<String, String[]>();
        this.partyMetadata.put(
            "org:opencrx:kernel:activity1:Absence", null
        );
        this.partyMetadata.put(
            "org:opencrx:kernel:activity1:EMail", new String[]{"org:opencrx:kernel:activity1:EMailRecipient", "emailRecipient"}
        );
        this.partyMetadata.put(
            "org:opencrx:kernel:activity1:ExternalActivity", null
        );
        this.partyMetadata.put(
            "org:opencrx:kernel:activity1:Fax", new String[]{"org:opencrx:kernel:activity1:FaxRecipient", "faxRecipient"}
        );
        this.partyMetadata.put(
            "org:opencrx:kernel:activity1:Incident", new String[]{"org:opencrx:kernel:activity1:IncidentParty", "incidentParty"}
        );
        this.partyMetadata.put(
            "org:opencrx:kernel:activity1:Mailing", new String[]{"org:opencrx:kernel:activity1:MailingRecipient", "mailingRecipient"}
        );
        this.partyMetadata.put(
            "org:opencrx:kernel:activity1:Meeting", new String[]{"org:opencrx:kernel:activity1:MeetingParty", "meetingParty"}
        );
        this.partyMetadata.put(
            "org:opencrx:kernel:activity1:SalesVisit", new String[]{"org:opencrx:kernel:activity1:MeetingParty", "meetingParty"}
        );
        this.partyMetadata.put(
            "org:opencrx:kernel:activity1:Mms", new String[]{"org:opencrx:kernel:activity1:MmsRecipient", "mmsRecipient"}
        );
        this.partyMetadata.put(
            "org:opencrx:kernel:activity1:PhoneCall", new String[]{"org:opencrx:kernel:activity1:PhoneCallRecipient", "phoneCallRecipient"}
        );
        this.partyMetadata.put(
            "org:opencrx:kernel:activity1:Sms", new String[]{"org:opencrx:kernel:activity1:SmsRecipient", "smsRecipient"}
        );
        this.partyMetadata.put(
            "org:opencrx:kernel:activity1:Task", new String[]{"org:opencrx:kernel:activity1:TaskParty", "taskParty"}
        );        
    }

    //-------------------------------------------------------------------------
    private Short numberAsShort(
        Object number
    ) {
        return new Short(((Number)number).shortValue());
    }

    //-------------------------------------------------------------------------
    public String getPrimaryEMailAddress(
        Path contact
    ) throws ServiceException {
        List addresses = this.backend.getDelegatingRequests().addFindRequest(
            contact.getChild("address"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        String emailAddress = null;
        for(Iterator i = addresses.iterator(); i.hasNext(); ) {
            DataproviderObject_1_0 address = (DataproviderObject_1_0)i.next();
            String addressClass = (String)address.values(SystemAttributes.OBJECT_CLASS).get(0);
            List usage = new ArrayList();
            for(Iterator j = address.values("usage").iterator(); j.hasNext(); ) {
                usage.add(
                    this.numberAsShort(j.next())
                );
            }
            if("org:opencrx:kernel:account1:EMailAddress".equals(addressClass)) {
                List adr = address.values("emailAddress");
                if((emailAddress == null) && (adr.size() > 0)) {
                    emailAddress = (String)adr.get(0);
                }
                if((adr.size() > 0) && (usage.contains(USAGE_EMAIL_PRIMARY))) {
                    emailAddress = (String)adr.get(0);
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
    /**
     * Update existing ical (activity.ical) with new activity values and return 
     * merged ical. 
     */
    public String mergeIcal(
        DataproviderObject_1_0 activity,
        String sourceIcal,
        List<String> statusMessage
    ) throws ServiceException {
        // DTSTART
        String dtStart = null;
        if(!activity.values("scheduledStart").isEmpty()) {
            dtStart = (String)activity.values("scheduledStart").get(0);
        }
        else {
            // ical requires DTSTART. Set DTSTART to beginning of year 
            // if scheduledStart is not defined for activity
            GregorianCalendar cal = new GregorianCalendar();
            dtStart = cal.get(Calendar.YEAR) + "0101T000000.000Z";
        }
        // DTEND
        String dtEnd = null;
        if(!activity.values("scheduledEnd").isEmpty()) {
            dtEnd = (String)activity.values("scheduledEnd").get(0);
        }
        // DUE
        String dueBy = null;
        if(!activity.values("dueBy").isEmpty()) {
            dueBy = (String)activity.values("dueBy").get(0);
        }
        // LAST-MODIFIED
        String lastModified = DateFormat.getInstance().format(new Date());
        // PRIORITY
        Number priority = null;
        if(!activity.values("priority").isEmpty()) {
            priority = (Number)activity.values("priority").get(0);
        }
        // SUMMARY (append activity number)
        String summary = activity.values("name").isEmpty() 
            ? "" 
            : (String)activity.values("name").get(0);
        summary += activity.values("activityNumber").isEmpty()
            ? ""
            : LINE_COMMENT_INDICATOR + " #" + activity.values("activityNumber").get(0);            
        // DESCRIPTION
        String description = activity.values("description").isEmpty() 
            ? "" 
            : (String)activity.values("description").get(0);
        // LOCATION
        String location = activity.values("location").isEmpty() 
            ? "" 
            : (String)activity.values("location").get(0);

        // attendees
        List<String> attendees = new ArrayList<String>();
        String activityClass = (String)activity.values(SystemAttributes.OBJECT_CLASS).get(0);
        String[] partyMetadata = this.partyMetadata.get(activityClass);
        if(partyMetadata != null) {
            List participants = this.backend.getDelegatingRequests().addFindRequest(
                activity.path().getChild(partyMetadata[1]),
                null,
                AttributeSelectors.ALL_ATTRIBUTES,
                0,
                Integer.MAX_VALUE,
                Directions.ASCENDING
            );
            for(Iterator i = participants.iterator(); i.hasNext(); ) {
                DataproviderObject_1_0 participant = (DataproviderObject_1_0)i.next();
                if(!participant.values("party").isEmpty()) {
                    try {
                        DataproviderObject_1_0 party = this.backend.retrieveObject(
                            (Path)participant.values("party").get(0)
                        );
                        DataproviderObject_1_0 contact = null;
                        String emailAddress = null;
                        // Party is Contact
                        if(this.backend.isContact(party)) {
                            contact = party;
                            emailAddress = this.getPrimaryEMailAddress(
                                party.path()
                            );
                        }
                        // Party is address
                        else {
                            contact = this.backend.retrieveObject(
                                party.path().getPrefix((party.path().size() - 2))
                            );
                            emailAddress = (String)party.values("emailAddress").get(0);
                        }
                        String partyType = !participant.values("partyType").isEmpty() && ((Number)participant.values("partyType").get(0)).intValue() == PARTY_TYPE_OPTIONAL 
                            ? "OPT-PARTICIPANT"
                            : "REQ-PARTICIPANT";
                        String fullName = contact.values("fullName").size() == 0 ? null : (String)contact.values("fullName").get(0);
                        if(emailAddress != null) {
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
        }
        
        // ORGANIZER
        String organizerEmailAddress = null;
        if(!activity.values("assignedTo").isEmpty()) {
            organizerEmailAddress = this.getPrimaryEMailAddress(
                (Path)activity.values("assignedTo").get(0)
            );
        }

        // return if data is missing
        if(!statusMessage.isEmpty()) {
            return null;
        }
        
        if((sourceIcal == null) || (sourceIcal.length() == 0)) {
            // Template
            UUID uid = null;
            try {
                uid = UUIDConversion.fromString(activity.path().getBase());
            }
            catch(Exception e) {
                uid = UUIDs.getGenerator().next();
            }
            sourceIcal =
                "BEGIN:VCALENDAR\n" +
                "PRODID:" + PROD_ID + "\n" +
                "VERSION:2.0\n" +
                "BEGIN:VEVENT\n" +
                "UID:" + uid.toString() + "\n" +
                "LAST-MODIFIED:" + lastModified.substring(0, 15) + "Z\n" +
                "DTSTART:\n" +
                "DTEND:\n" +
                "DUE:\n" +
                "LOCATION:\n" +
                "DTSTAMP:\n" +
                "SUMMARY:\n" +
                "DESCRIPTION:\n" +
                "PRIORITY:\n" +
                "ATTENDEE:\n" +
                "CLASS:PUBLIC\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR";            
        }
        try {
            ByteArrayOutputStream targetIcalBos = new ByteArrayOutputStream();
            PrintWriter targetIcal = new PrintWriter(new OutputStreamWriter(targetIcalBos, "UTF-8"));
            String line = null;
            BufferedReader readerSourceIcal = new BufferedReader(new StringReader(sourceIcal));
            boolean isEvent = false;
            boolean isAlarm = false;
            boolean isTimezone = false;
            String tagStart = null;
            int nEvents = 0;
            while((line = readerSourceIcal.readLine()) != null) {
                if(!line.startsWith(" ")) {
                    tagStart = line;
                }
                // PRODID
                if(line.startsWith("PRODID") || line.startsWith("prodid")) {                
                    targetIcal.println("PRODID:" + PROD_ID);
                }                
                else if(line.startsWith("TZID") || line.startsWith("tzid")) {                
                }                
                else if(
                    line.startsWith("BEGIN:VTIMEZONE") || 
                    line.startsWith("begin:vtimezone") 
                ) {
                    isTimezone = true;
                }
                else if(
                    line.startsWith("END:VTIMEZONE") || 
                    line.startsWith("end:vtimezone") 
                ) {
                    isTimezone = false;
                }
                else if(
                    line.startsWith("BEGIN:VALARM") || 
                    line.startsWith("begin:valarm") 
                ) {
                    targetIcal.println("BEGIN:VALARM");                    
                    isAlarm = true;
                }
                else if(
                    line.startsWith("END:VALARM") || 
                    line.startsWith("end:valarm") 
                ) {           
                    targetIcal.println("END:VALARM");                    
                    isAlarm = false;                    
                }
                else if(
                    line.startsWith("BEGIN:VEVENT") || 
                    line.startsWith("begin:vevent") 
                ) {
                    targetIcal.println("BEGIN:VEVENT");                    
                    isEvent = true;
                }
                else if(
                    line.startsWith("BEGIN:VTODO") || 
                    line.startsWith("begin:vtodo") 
                ) {
                    targetIcal.println("BEGIN:VTODO");                    
                    isEvent = true;
                }
                // Dump updated event fields only for first event
                else if(
                    (nEvents == 0) &&
                    line.startsWith("END:VEVENT") || 
                    line.startsWith("end:vevent") || 
                    line.startsWith("END:VTODO") || 
                    line.startsWith("end:vtodo") 
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
                    // ATTENDEE
                    for(int i = 0; i < attendees.size(); i++) {
                        targetIcal.println("ATTENDEE" + attendees.get(i));
                    }
                    // ORGANIZER
                    if(organizerEmailAddress != null) {
                        targetIcal.println("ORGANIZER:MAILTO:" + organizerEmailAddress);
                    }
                    if(
                        line.startsWith("END:VEVENT") || 
                        line.startsWith("end:vevent")
                    ) {                        
                        targetIcal.println("END:VEVENT");                    
                        
                    }
                    else {                        
                        targetIcal.println("END:VTODO");                                            
                    }
                    isEvent = false;
                    nEvents++;
                }
                else if(isTimezone) {
                    // Skip all timezone fields. All datetime fields are converted to UTC
                }
                else if(
                    isEvent && 
                    !isAlarm && 
                    (nEvents == 0)
                ) {
                    boolean isUpdatableEventTag = 
                        tagStart.startsWith("DTSTART") || tagStart.startsWith("dtstart");
                    isUpdatableEventTag |=
                        tagStart.startsWith("DTEND") || tagStart.startsWith("dtend");
                    isUpdatableEventTag |=
                        tagStart.startsWith("DUE") || tagStart.startsWith("due");
                    isUpdatableEventTag |=
                        tagStart.startsWith("LOCATION") || tagStart.startsWith("location");
                    isUpdatableEventTag |= 
                        tagStart.startsWith("DTSTAMP") || tagStart.startsWith("dtstamp");
                    isUpdatableEventTag |=
                        tagStart.startsWith("DESCRIPTION") || tagStart.startsWith("description");
                    isUpdatableEventTag |=
                        tagStart.startsWith("LAST-MODIFIED") || tagStart.startsWith("last-modified");
                    isUpdatableEventTag |=
                        tagStart.startsWith("SUMMARY") || tagStart.startsWith("summary");
                    isUpdatableEventTag |=
                        tagStart.startsWith("PRIORITY") || tagStart.startsWith("priority");
                    isUpdatableEventTag |=
                        tagStart.startsWith("ATTENDEE") || tagStart.startsWith("attendee");
                    isUpdatableEventTag |=
                        tagStart.startsWith("ORGANIZER") || tagStart.startsWith("organizer");
                    if(!isUpdatableEventTag) {
                        targetIcal.println(line);
                    }
                }
                else {
                    targetIcal.println(line);                    
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

    //-------------------------------------------------------------------------
    public BasicObject importItem(
        byte[] item,
        Path activityIdentity,
        short locale,
        List<String> errors,
        List<String> report
    ) throws ServiceException {
        Path accountSegmentIdentity =
            new Path(new String[]{"org:opencrx:kernel:account1", "provider", activityIdentity.get(2), "segment", activityIdentity.get(4)});        
        try {
            InputStream is = new ByteArrayInputStream(item);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            Map<String,String> fields = new HashMap<String,String>();
            StringBuilder ical = new StringBuilder();
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
                            if(fields.containsKey(currentName)) {
                                fields.put(
                                    currentName,
                                    fields.get(currentName) + line.substring(1)
                                );
                            }
                            else if((pos = line.indexOf(":")) >= 0) {
                                currentName += line.substring(0, pos).toUpperCase();
                                if(currentName.indexOf(";") > 0) {
                                    currentName = currentName.substring(0, currentName.indexOf(";"));
                                }
                                fields.put(
                                    currentName,
                                    line.substring(pos + 1)
                                );                            
                            }
                        }
                        else if(line.startsWith("ATTENDEE") || line.startsWith("attendee")) {
                            currentName = "ATTENDEE[" + nAttendees + "]";
                            fields.put(
                                currentName,
                                line.substring("ATTENDEE".length())
                            );
                            nAttendees++;
                        }
                        else if((pos = line.indexOf(":")) >= 0) {
                            currentName = line.substring(0, pos).toUpperCase();
                            if(currentName.indexOf(";") > 0) {
                                currentName = currentName.substring(0, currentName.indexOf(";"));
                            }
                            fields.put(
                                currentName,
                                line.substring(pos + 1)
                            );
                        }
                        else {
                            currentName = line;
                        }
                    }
                }                 
            }
            // Do not import URLs
            AppLog.trace("ICalendar", fields);
            return this.importItem(
                ical.toString(),
                fields,
                activityIdentity,
                accountSegmentIdentity,
                locale,
                errors,
                report
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
        VCard vcardImporter,
        Path contactSegmentPath,
        short locale,
        List report
    ) {
        Map vcard = new HashMap();
        int pos = attendeeAsString.indexOf("MAILTO:");
        if(pos < 0) {
            pos = attendeeAsString.indexOf("mailto:");
        }
        String emailPrefInternet = attendeeAsString.substring(pos + 7);
        vcard.put("EMAIL;PREF;INTERNET", emailPrefInternet);
        try {
            return vcardImporter.importItem(
                vcard,
                contactSegmentPath,
                locale,
                report,
                true
            );
        }
        catch(Exception e) {
            return null;
        }    
    }

    //-------------------------------------------------------------------------
    protected String getUtcDateTime(
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
        return DateFormat.getInstance().format(date);
    }
    
    //-------------------------------------------------------------------------
    protected String fromICalString(
        String s
    ) {
        String t = s.replace("\\\\", "\\");
        t = t.replace("\\;", ";");
        t = t.replace("\\,", ",");
        return t;
    }
    
    //-------------------------------------------------------------------------
    public Activity importItem(
        String ical,
        Map<String,String> fields,
        Path activityIdentity,
        Path accountSegmentIdentity,
        short locale,
        List<String> errors,
        List<String> report
    ) throws ServiceException {

        // Prepare attendees
        VCard vcardImporter = new VCard(
            this.backend
        );
        List<DataproviderObject_1_0> attendees = new ArrayList<DataproviderObject_1_0>();
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
                    vcardImporter,
                    accountSegmentIdentity,
                    locale,
                    report
                );
                if(attendee != null) {
                    attendees.add(
                        this.backend.retrieveObject(
                            attendee.refGetPath()
                        )
                    );
                    attendeeRoles.add(
                        (attendeeAsString.indexOf("ROLE=OPT-PARTICIPANT") >= 0) || (attendeeAsString.indexOf("role=opt-participant") >= 0)
                            ? new Short((short)PARTY_TYPE_OPTIONAL)
                            : new Short((short)PARTY_TYPE_REQUIRED)
                    );
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
        
        // Retrieve activity for modification
        DataproviderObject activity = this.backend.retrieveObjectForModification(
            activityIdentity
        );
        // ICAL:uid
        boolean hasIcalUid = false;
        List<String> externalLinks = (List<String>)activity.values("externalLink");
        String icalUid = fields.get("UID") == null
            ? activity.path().getBase()
            : fields.get("UID");
        for(int i = 0; i < externalLinks.size(); i++) {
            if(externalLinks.get(i).startsWith(ICAL_SCHEMA)) {
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
                activity.clearValues("scheduledStart").add(
                    this.getUtcDateTime(
                        s,
                        dateTimeFormatter
                    )
                );
            } catch(Exception e) {
                errors.add("DTSTART (" + s + ")");
            }
        }
        s = fields.get("DTEND");
        if((s != null) && (s.length() > 0)) {
            try {
                activity.clearValues("scheduledEnd").add(
                    this.getUtcDateTime(
                        s, 
                        dateTimeFormatter
                    )
                );
            } catch(Exception e) {
                errors.add("DTEND (" + s + ")");
            }
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
                activity.clearValues("priority").add(new Integer(priority));
            } catch(Exception e) {
                errors.add("PRIORITY (" + s + ")");
            }
        }
        s = fields.get("SUMMARY");
        if((s != null) && (s.length() > 0)) {
            int posComment = s.indexOf(LINE_COMMENT_INDICATOR);
            String name =  posComment > 0 
                ? s.substring(0, posComment) 
                : s;
            activity.clearValues("name").add(
                this.fromICalString(name)
            );
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
            activity.clearValues("description").add(
               this.fromICalString(temp)
           );
        }
        s = fields.get("LOCATION");
        if((s != null) && (s.length() > 0)) {
            activity.clearValues("location").add(
                this.fromICalString(s)
            );
        }
        s = fields.get("ORGANIZER");
        if((s != null) && (s.length() > 0)) {
            BasicObject organizer = this.getAttendeeAsContact(
                s.startsWith("MAILTO:") 
                    ?  s 
                    : "MAILTO:" + s,
                vcardImporter,
                accountSegmentIdentity,
                locale,
                report
            );
            if(organizer != null) {
                activity.clearValues("assignedTo").add(
                    organizer.refGetPath()
                );
            }
        }
        // ical
        activity.clearValues("ical").add(ical);
        
        if(!errors.isEmpty()) {
            return null;
        }        
        report.add("Update activity");

        // Update parties
        String activityClass = (String)activity.values(SystemAttributes.OBJECT_CLASS).get(0);
        String[] partyMetadata = this.partyMetadata.get(activityClass);
        if(partyMetadata != null) {
            List<DataproviderObject_1_0> parties = this.backend.getDelegatingRequests().addFindRequest(
                activity.path().getChild(partyMetadata[1]),
                null,
                AttributeSelectors.ALL_ATTRIBUTES,
                0,
                Integer.MAX_VALUE,
                Directions.ASCENDING      
            );
            List<Path> existingParties = new ArrayList<Path>();
            for(DataproviderObject_1_0 party : parties) {
                existingParties.addAll(
                    party.values("party")
                );
            }
            // Create new parties
            for(int i = 0; i < attendees.size(); i++) {
                DataproviderObject_1_0 attendee = attendees.get(i);
                if(!existingParties.contains(attendee.path())) {
                    DataproviderObject party = new DataproviderObject(
                        activity.path().getDescendant(new String[]{partyMetadata[1], this.backend.getUidAsString()})
                    );
                    party.values(SystemAttributes.OBJECT_CLASS).add(partyMetadata[0]);
                    if(
                        "org:opencrx:kernel:activity1:Meeting".equals(activityClass) ||
                        "org:opencrx:kernel:activity1:SalesVisit".equals(activityClass)
                    ) {
                        party.clearValues("partyType").add(
                            attendeeRoles.get(i)                            
                        );                        
                    }
                    else {
                        party.clearValues("partyType").add(
                            new Short((short)PARTY_TYPE_NA)
                        );
                    }
                    party.clearValues("party").add(attendee.path());
                    this.backend.getDelegatingRequests().addCreateRequest(
                        party
                    );
                    report.add("Create party");
                }
            }
        }
        return activity == null
            ? null
            : (Activity)this.backend.getDelegatingPkg().refObject(activity.path().toXri());
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
    
    protected final Backend backend;
    protected final Map<String, String[]> partyMetadata;
}

//--- End of File -----------------------------------------------------------

