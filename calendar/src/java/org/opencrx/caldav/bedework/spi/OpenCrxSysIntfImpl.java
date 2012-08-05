/*
 * ====================================================================
 * Project:     openCRX/CalDAV, http://www.opencrx.org/
 * Name:        $Id: OpenCrxSysIntfImpl.java,v 1.1 2007/09/11 23:57:45 wfro Exp $
 * Description: OpenCrxSysIntfImpl
 * Revision:    $Revision: 1.1 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2007/09/11 23:57:45 $
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
package org.opencrx.caldav.bedework.spi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryImpl;

import org.bedework.caldav.server.SysIntf;
import org.bedework.calfacade.BwCalendar;
import org.bedework.calfacade.BwDateTime;
import org.bedework.calfacade.BwEvent;
import org.bedework.calfacade.BwEventObj;
import org.bedework.calfacade.BwEventProxy;
import org.bedework.calfacade.BwFreeBusy;
import org.bedework.calfacade.BwLocation;
import org.bedework.calfacade.BwString;
import org.bedework.calfacade.BwUser;
import org.bedework.calfacade.RecurringRetrievalMode;
import org.bedework.calfacade.ScheduleResult;
import org.bedework.calfacade.base.BwShareableDbentity;
import org.bedework.calfacade.exc.CalFacadeException;
import org.bedework.calfacade.filter.BwFilter;
import org.bedework.calfacade.svc.EventInfo;
import org.bedework.calfacade.timezones.SATimezonesImpl;
import org.bedework.calfacade.util.ChangeTable;
import org.bedework.icalendar.IcalMalformedException;
import org.bedework.icalendar.IcalTranslator;
import org.bedework.icalendar.Icalendar;
import org.bedework.icalendar.SAICalCallback;
import org.opencrx.kernel.activity1.cci.ActivityTracker;
import org.opencrx.kernel.activity1.jmi.Activity1Package;
import org.opencrx.kernel.activity1.query.ActivityTrackerQuery;
import org.openmdx.base.accessor.jmi.spi.PersistenceManagerFactory_1;
import org.openmdx.base.cci.Authority;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.object.jdo.ConfigurableProperties_2_0;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.compatibility.application.dataprovider.transport.ejb.cci.Dataprovider_1ConnectionFactoryImpl;
import org.openmdx.compatibility.base.dataprovider.transport.cci.Dataprovider_1ConnectionFactory;
import org.openmdx.model1.accessor.basic.cci.Model_1_0;
import org.openmdx.model1.accessor.basic.spi.Model_1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.rpi.cct.webdav.servlet.common.WebdavUtils;
import edu.rpi.cct.webdav.servlet.shared.PrincipalPropertySearch;
import edu.rpi.cct.webdav.servlet.shared.WebdavBadRequest;
import edu.rpi.cct.webdav.servlet.shared.WebdavException;
import edu.rpi.cct.webdav.servlet.shared.WebdavNotFound;
import edu.rpi.cmt.access.Acl.CurrentAccess;

public class OpenCrxSysIntfImpl implements SysIntf {

    //-----------------------------------------------------------------------
    public static String formatDate(
        Date date
    ) {
        return DateFormat.getInstance().format(date).substring(0, 15) + "Z";
    }
    
    //-----------------------------------------------------------------------
    public static class OpenCrxCalendar extends BwCalendar {
    
        public OpenCrxCalendar(
            String account,
            String name,
            String path,
            int calType,
            BwCalendar parent,
            Activity1Package activityPackage,
            org.opencrx.kernel.activity1.cci.Segment activitySegment,                
            ActivityTracker activityTracker
        ) {            
            super(
                new BwUser(account),
                false,
                new BwUser(account),
                null,
                name,
                path,
                name,
                null,
                null,
                true,
                parent,
                Collections.EMPTY_LIST,
                calType,
                null,
                formatDate(activityTracker.getModifiedAt()),
                180,
                Collections.EMPTY_LIST                
            );
            this.activityPackage = activityPackage;
            this.activitySegment = activitySegment;
            this.activityTracker = activityTracker;
        }
        
        public Activity1Package getActivityPackage(
        ) {
            return this.activityPackage;
        }
        
        public org.opencrx.kernel.activity1.cci.Segment getActivitySegment(
        ) {
            return this.activitySegment;
        }
        
        public ActivityTracker getActivityTracker(
        ) {
            return this.activityTracker;
        }
        
        private static final long serialVersionUID = 1012308320085708890L;
        private final Activity1Package activityPackage;
        private final org.opencrx.kernel.activity1.cci.Segment activitySegment;
        private final ActivityTracker activityTracker;
    }

    //-----------------------------------------------------------------------
    public static class CalTimezones extends SATimezonesImpl {

        public CalTimezones(
            boolean debug, 
            BwUser user
        ) throws CalFacadeException {
            super(
                debug, 
                user
            );
        }
        
        @Override
        protected void initSystemTimeZones() throws CalFacadeException {
            // TODO Auto-generated method stub            
        }

        @Override
        protected void initUserTimeZones() throws CalFacadeException {
            this.timezones.put(
                DEFAULT_TIMEZONE,
                new CalTimezones.TimezoneInfo(
                   timezoneRegistry.getTimeZone(DEFAULT_TIMEZONE)
                )
            );
        }

        @Override
        public void storeTimeZone(String id, BwUser owner) throws CalFacadeException {            
            String[] tzids = java.util.TimeZone.getAvailableIDs();
            for(int i = 0; i < tzids.length; i++) {
                if(id.endsWith(tzids[i])) {
                    this.timezones.put(
                        id,
                        new CalTimezones.TimezoneInfo(
                           timezoneRegistry.getTimeZone(tzids[i])
                        )
                    );
                    break;
                }
            }
        }

        private static final TimeZoneRegistry timezoneRegistry = new TimeZoneRegistryImpl();
        private static final long serialVersionUID = -2681254267864202518L;
        
    }
    
    //-----------------------------------------------------------------------
    public class CalCallback extends SAICalCallback {
        
        public CalCallback(
            CalTimezones timezones,
            String account
        ) {
            super(
                timezones,
                account
            );
        }

        @Override
        public Collection getEvent(
            BwCalendar cal, 
            String guid, 
            String rid, 
            RecurringRetrievalMode recurRetrieval
        ) throws CalFacadeException {
            if(OpenCrxSysIntfImpl.icals.keySet().contains(guid)) {
                EventInfo evinfo = new EventInfo();
                BwEventObj ev = new BwEventObj();
                ev.setName(guid + ".ics");
                ev.setDtstamps();
                ev.setCreator(this.getUser());
                ev.setUid(guid);
                ev.setCalendar(cal);
                evinfo.setEvent(ev);
                evinfo.setNewEvent(false);
                List<EventInfo> events = new ArrayList<EventInfo>();
                events.add(evinfo);
                return events;
            }
            else {
                return null;
            }
        }
        
        private static final long serialVersionUID = -169553153047544640L;
    }
    
    //-----------------------------------------------------------------------
    protected BwEvent mapToEvent(
        org.opencrx.kernel.activity1.cci.Activity activity,
        BwCalendar cal
    ) {
        BwEvent event = new BwEventObj();
//        event.setAttendees(val);
//        event.setCalendar(cal);
//        event.setComments(val);
//        event.setCompleted(val);
//        event.setCreated(val);
        if(!activity.getCreatedBy().isEmpty()) {
            event.setCreator(
                new BwUser(
                    (String)activity.getCreatedBy().iterator().next()
                )
            );
        }
        event.setDeleted(
            activity.isDisabled()
        );
        if(activity.getDescription() != null) {
            event.setDescription(activity.getDescription());
        }
//        event.setDtend(val);
//        event.setDtstamp(val);
//        event.setDtstart(val);
//        event.setDuration(val);
//        event.setEndType(val);
//        event.setEntityType(val);
//        event.setExdates(val);
//        event.setExpanded(val);
//        event.setExrules(val);
//        event.setFreeBusyPeriods(val);
//        event.setGeo(val);
//        event.setId(val);
//        event.setLastmod(val);
//        event.setLatestDate(val);
//        event.setLink(val);
        if(activity.getName() != null) {
            event.setName(activity.getName());
        }
//        event.setNoStart(val);
//        event.setOrganizer(val);
//        event.setOriginator(val);
//        event.setOwner(val);
//        event.setPercentComplete(val);
//        event.setPriority(val);
//        event.setPublick(val);
//        event.setRdates(val);
//        event.setRecipients(val);
//        event.setRecurrenceId(val);
//        event.setRecurring(val);
//        event.setRelatedTo(val);
//        event.setRequestStatuses(val);
//        event.setResources(val);
//        event.setRrules(val);
//        event.setScheduleMethod(val);
//        event.setScheduleState(val);
//        event.setStatus(val);
        if(activity.getDetailedDescription() != null) {
            event.setSummary(activity.getDetailedDescription());
        }
        event.setUid(activity.refMofId());
        return event;
    }
    
    //-----------------------------------------------------------------------
    protected BwEvent mapToEvent(
        org.opencrx.kernel.activity1.cci.Task task,
        BwCalendar cal
    ) {
        BwEvent event = this.mapToEvent(
            (org.opencrx.kernel.activity1.cci.Activity)task,
            cal
        );
        return event;
    }
    
    //-----------------------------------------------------------------------
    protected BwEvent mapToEvent(
        org.opencrx.kernel.activity1.cci.Meeting meeting,
        BwCalendar cal        
    ) {
        BwEvent event = this.mapToEvent(
            (org.opencrx.kernel.activity1.cci.Activity)meeting,
            cal
        );
        if(meeting.getLocation() != null) {
            BwLocation location = new BwLocation();
            location.setAddress(
                new BwString(
                    DEFAULT_LANGUAGE,
                    meeting.getLocation()
                )
            );
            event.setLocation(location);
        }
        return event;
    }
    
    //-----------------------------------------------------------------------
    public void init(
        HttpServletRequest request,
        String envPrefix,
        String account,
        boolean debug
    ) throws WebdavException {
        try {
            this.account = account;
            this.timezones = new CalTimezones(
                debug,
                new BwUser(account)
            );
            this.timezones.setDefaultTimeZoneId(DEFAULT_TIMEZONE);
            this.icalTranslator = new IcalTranslator(
                new CalCallback(
                    this.timezones, 
                    account
                ),
                debug
            );
            this.urlPrefix = WebdavUtils.getUrlPrefix(request);
            
            // Init model repository
            if(model == null) {    
                try {
                    model = new Model_1();
                    model.addModels(MODEL_PACKAGES);
                }
                catch(Exception e) {
                    System.out.println("Can not initialize model repository " + e.getMessage());
                    System.out.println(new ServiceException(e).getCause());
                }
            }                
            // Get openMDX connection to data provider
            // One connection per HttpSession
            PersistenceManagerFactory persistenceManagerFactory = null;
            try {
                Context initialContext;
                try {
                    initialContext = new InitialContext();
                } 
                catch (NamingException e) {
                    throw new WebdavException("Can not get the initial context" + e.getMessage());
                }
                persistenceManagerFactory = (PersistenceManagerFactory)request.getSession().getAttribute(
                    PersistenceManagerFactory.class.getName()
                );
                if(persistenceManagerFactory == null) {
                    Map<String, Object> configuration = new HashMap<String, Object>();
                    configuration.put(
                        Dataprovider_1ConnectionFactory.class.getName(),
                        new Dataprovider_1ConnectionFactoryImpl(
                            initialContext,
                            "data",
                            new String[]{"java:comp/env/ejb"}
                        )
                    );
                    configuration.put(
                        ConfigurableProperties_2_0.FACTORY_CLASS,
                        PersistenceManagerFactory_1.class.getName()
                    );
                    configuration.put(
                        ConfigurableProperties_2_0.OPTIMISTIC,
                        Boolean.TRUE.toString()
                    );
                    request.getSession().setAttribute(
                        PersistenceManagerFactory.class.getName(),
                        persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory(configuration)
                    );            
                }
            }
            catch(Exception e) {
                throw new WebdavException("Can not get connection to data provider" + e.getMessage());
            }        
            // Get JMI root package
            this.pm = persistenceManagerFactory.getPersistenceManager(
                account, 
                request.getSession().getId()
            );  
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    //-----------------------------------------------------------------------
    public String getUrlPrefix(
    ) {
        return this.urlPrefix;
    }

    //-----------------------------------------------------------------------
    public String getPrincipalRoot(
    ) {
        return "/principals";
    }

    //-----------------------------------------------------------------------
    public String getUserPrincipalRoot(
    ) {
        return "/principals/users";
    }

    //-----------------------------------------------------------------------
    public String getGroupPrincipalRoot(
    ) {
        return "/principals/groups";
    }

    //-----------------------------------------------------------------------
    public String makeUserHref(
        String id
    ) throws WebdavException {
        return this.getUrlPrefix() + "/" + getUserPrincipalRoot() + "/" + id;
    }

    //-----------------------------------------------------------------------
    public String makeGroupHref(
        String id
    ) throws WebdavException {
        return this.getUrlPrefix() + "/" + getGroupPrincipalRoot() + "/" + id;
    }

    //-----------------------------------------------------------------------
    public boolean getDirectoryBrowsingDisallowed(
    ) throws WebdavException {
        return false;
    }

    //-----------------------------------------------------------------------
    public String caladdrToUser(
        String caladdr
    ) throws WebdavException {
        return caladdr;
    }

    //-----------------------------------------------------------------------
    public String userToCaladdr(
        String account
    ) throws WebdavException {
        return account;
    }

    //-----------------------------------------------------------------------
    public CalUserInfo getCalUserInfo(
        String account,
        boolean getDirInfo
    ) throws WebdavException {
        return new CalUserInfo(account, null, null, null, null, null);
    }

    //-----------------------------------------------------------------------
    public Collection<String> getPrincipalCollectionSet(
        String resourceUri
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public Collection<CalUserInfo> getPrincipals(
        String resourceUri,
        PrincipalPropertySearch pps
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public boolean validUser(
        String account
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public boolean validGroup(
        String account
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public ScheduleResult schedule(
        BwEvent event
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public Collection<BwEventProxy> addEvent(
        BwCalendar cal,
        BwEvent event,
        Collection<BwEventProxy> overrides,
        boolean rollbackOnError
    ) throws WebdavException {
        String calendar = this.pendingIcals.get(event.getUid());
        if(calendar != null) {       
            icals.put(
                event.getUid(), 
                calendar
            );
        }
        return Collections.EMPTY_LIST;
    }

    //-----------------------------------------------------------------------
    public void updateEvent(
        BwEvent event,
        Collection overrides,
        ChangeTable changes
    ) throws WebdavException {
        String calendar = this.pendingIcals.get(event.getUid());
        if(calendar != null) {       
            icals.put(
                event.getUid(), 
                calendar
            );
        }
    }

    //-----------------------------------------------------------------------
    public Collection<EventInfo> getEvents(
        BwCalendar cal,
        BwFilter filter,
        RecurringRetrievalMode recurRetrieval
    ) throws WebdavException {        
        if(cal instanceof OpenCrxCalendar) {
            List<EventInfo> events = new ArrayList<EventInfo>();        
            List<String> splitUri = this.splitUri(cal.getPath(), true);
            if(splitUri.size() == 3) {
                for(String id : icals.keySet()) {
                    events.add(
                        this.getEvent(
                            cal, 
                            id, 
                            recurRetrieval
                        )
                    );
                }
    //            OpenCrxCalendar calendar = (OpenCrxCalendar)cal;
    //            Activity1Package activityPackage = calendar.getActivityPackage();
    //            ActivityTracker tracker = calendar.getActivityTracker();
    //            // Tasks
    //            TaskQuery taskQuery = activityPackage.createTaskQuery();
    //            for(Task task : (Collection<Task>)tracker.getFilteredActivity(taskQuery)) {
    //                EventInfo eventInfo = new EventInfo();
    //                eventInfo.setEvent(
    //                    this.mapToEvent(
    //                        task,
    //                        cal
    //                    )
    //                );
    //                events.add(eventInfo);
    //            }
    //            // Meetings
    //            MeetingQuery meetingQuery = activityPackage.createMeetingQuery();
    //            for(Meeting meeting : (Collection<Meeting>)tracker.getFilteredActivity(meetingQuery)) {
    //                EventInfo eventInfo = new EventInfo();
    //                eventInfo.setEvent(
    //                    this.mapToEvent(
    //                        meeting,
    //                        cal
    //                    )
    //                );
    //                events.add(eventInfo);
    //            }
            }
            return events;
        }
        else {
            throw new WebdavException("Unsupported operation");
        }
    }

    //-----------------------------------------------------------------------
    public EventInfo getEvent(
        BwCalendar cal, 
        String val,
        RecurringRetrievalMode recurRetrieval
    ) throws WebdavException {
        int pos = val.lastIndexOf(".");
        val = pos > 0 ? val.substring(0, pos) : val;
        String calendar = icals.get(val);
        if(calendar != null) {
            Icalendar ical = this.fromIcal(
                cal, 
                new StringReader(calendar)
            );
            EventInfo ev = ical.getEventInfo();
            return ev;
        }
        return null;
    }

    //-----------------------------------------------------------------------
    public void deleteEvent(
        BwEvent ev
    ) throws WebdavException {
        icals.remove(ev.getUid());
    }

    //-----------------------------------------------------------------------
    public void deleteCalendar(
        BwCalendar cal
    ) throws WebdavException {      
        List<String> l = splitUri(cal.getPath(), true);
        String calendarName = l.get(l.size()-1);
        if((calendarName != null) && (calendarName.indexOf(".") > 0)) {
            String uid = calendarName.substring(0, calendarName.indexOf("."));
            icals.remove(uid);
        }
    }

    //-----------------------------------------------------------------------
    public ScheduleResult requestFreeBusy(
        BwEvent val
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public BwFreeBusy getFreeBusy(
        BwCalendar cal,
        String account,
        BwDateTime start,
        BwDateTime end
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public CurrentAccess checkAccess(
        BwShareableDbentity ent,
        int desiredAccess,
        boolean returnResult
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public void updateAccess(
        BwCalendar cal,
        Collection aces
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public void updateAccess(
        BwEvent ev,
        Collection aces
    ) throws WebdavException{
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public int makeCollection(
        String name, 
        boolean calendarCollection,
        String parentPath
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public void copyMove(
        BwCalendar from,
        BwCalendar to,
        boolean copy,
        boolean overwrite
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public boolean copyMove(
        BwEvent from, Collection<BwEventProxy>overrides,
        BwCalendar to,
        String name,
        boolean copy,
        boolean overwrite
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public BwCalendar getCalendar(
        String path
    ) throws WebdavException {
        List<String> l = splitUri(path, true);
        if(l.size() >= 3) {
            String providerName = l.get(0);
            String segmentName = l.get(1);
            String calendarName = l.get(l.size()-1);
            if("null".equals(calendarName)) {
                return null;
            }
            Activity1Package activityPackage = 
                (org.opencrx.kernel.activity1.jmi.Activity1Package)((Authority)this.pm.getObjectById(
                    Authority.class,
                    org.opencrx.kernel.activity1.jmi.Activity1Package.AUTHORITY_XRI
                )).getPackage();
            org.opencrx.kernel.activity1.cci.Segment activitySegment = 
                (org.opencrx.kernel.activity1.cci.Segment)this.pm.getObjectById(
                    "xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName
                );
            ActivityTrackerQuery query = activityPackage.createActivityTrackerQuery();
            query.name().equalTo(calendarName);
            List trackers = activitySegment.getActivityTracker(query);
            if(trackers.isEmpty()) {
                return null;
            }
            BwCalendar cal = new OpenCrxCalendar(
                this.account,
                calendarName,
                path,
                1,
                null,
                activityPackage,
                activitySegment,
                (ActivityTracker)trackers.iterator().next()
            );
            cal.setId(81);
            return cal;
        }
        else {
            throw new WebdavNotFound(path);            
        }
    }

    //-----------------------------------------------------------------------
    public Collection<BwCalendar> getCalendars(
        BwCalendar cal
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public Calendar toCalendar(
        EventInfo ev
    ) throws WebdavException {
        try {
            return this.icalTranslator.toIcal(ev, Icalendar.methodTypeNone);
        } catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    //-----------------------------------------------------------------------
    public Icalendar fromIcal(
        BwCalendar cal, 
        Reader ical
    ) throws WebdavException {
        try {
            // Get a copy of the ics. The original ics is attached as media 
            // to the calendar activity. This way only a subset of the ics
            // fields have to be mapped to an activity object.
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int b;
            while((b = ical.read()) >= 0) {
                bos.write(b);
            }
            String calendar = new String(bos.toByteArray());
            int posUid = calendar.indexOf("UID:");
            StringBuilder uid = new StringBuilder();
            int i = posUid + 4;
            while((calendar.charAt(i) != '\r') && (calendar.charAt(i) != '\n')) {
                uid.append(calendar.charAt(i));
                i++;
            }
            this.pendingIcals.put(
                uid.toString(),
                calendar
            );
            // Convert to Icalendar
            ical = new InputStreamReader(
                new ByteArrayInputStream(bos.toByteArray())
            );
            return this.icalTranslator.fromIcal(cal, ical);
        } catch (IcalMalformedException ime) {
            throw new WebdavBadRequest(ime.getMessage());
        } catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    //-----------------------------------------------------------------------
    public CalTimezones getTimezones(
    ) {
        return this.timezones;
    }

    //-----------------------------------------------------------------------
    public TimeZone getDefaultTimeZone(
    ) throws WebdavException {
        try {
            return getTimezones().getDefaultTimeZone();
        } catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    //-----------------------------------------------------------------------
    public String toStringTzCalendar(
        String tzid
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public int getMaxUserEntitySize(
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public void close() throws WebdavException {
    }

//    private BwFreeBusy makeFb(BwDateTime start,
//                              BwDateTime end,
//                              String val,
//                              int cellSize) throws WebdavException{
//      BwFreeBusy fb = new BwFreeBusy();
//
//      fb.setStart(start);
//      fb.setEnd(end);
//
//      char lastDigit = 0;
//
//      SimpleTimeZone utctz = new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC");
//      java.util.Calendar startCal = java.util.Calendar.getInstance(utctz);
//
//      java.util.Calendar endCal = java.util.Calendar.getInstance(utctz);
//
//      try {
//        startCal.setTime(start.makeDate());
//
//        endCal.setTime(start.makeDate());
//
//        DateTime startDt = null;
//
//        char[] digits = val.toCharArray();
//
//        for (int i = 0; i < digits.length; i++) {
//          char digit = digits[i];
//          endCal.add(java.util.Calendar.MINUTE, cellSize);
//
//          if ((lastDigit != digit) || (i == digits.length)) {
//            // End of period or end of freebusy
//
//            DateTime endDt = new DateTime(endCal.getTime());
//            endDt.setUtc(true);
//
//            if (startDt != null) {
//              if (lastDigit != exchangeFBFree) {
//                /* Just finished a non-free period * /
//                BwFreeBusyComponent fbcomp = new BwFreeBusyComponent();
//
//                fb.addTime(fbcomp);
//
//                int type = -1;
//                if (lastDigit == exchangeFBBusy) {
//                  type = BwFreeBusyComponent.typeBusy;
//                } else if (lastDigit == exchangeFBBusyTentative) {
//                  type = BwFreeBusyComponent.typeBusyTentative;
//                } else if (lastDigit == exchangeFBOutOfOffice) {
//                  type = BwFreeBusyComponent.typeBusyUnavailable;
//                }
//
//                fbcomp.setType(type);
//
//                fbcomp.addPeriod(new Period(startDt, endDt));
//              }
//            }
//
//            startDt = endDt;
//          }
//
//          lastDigit = digit;
//        }
//
//        return fb;
//      } catch (Throwable t) {
//        if (debug) {
//          getLogger().error(this, t);
//        }
//
//        throw new WebdavException(t);
//      }
//    }*/

    //-----------------------------------------------------------------------
    private List<String> splitUri(
        String uri, 
        boolean decoded
    ) throws WebdavException {
        try {
            /*Remove all "." and ".." components */
            if (decoded) {
                uri = new URI(null, null, uri, null).toString();
            }
            uri = new URI(uri).normalize().getPath();
            this.log.debug("Normalized uri {}", uri);
            uri = URLDecoder.decode(uri, "UTF-8");
            if (!uri.startsWith("/")) {
                return null;
            }
            if (uri.endsWith("/")) {
                uri = uri.substring(0, uri.length() - 1);
            }
            String[] ss = uri.split("/");
            int pathLength = ss.length - 1;  // First element is empty string
            if (pathLength < 2) {
                throw new WebdavBadRequest("Bad uri: " + uri);
            }
            List<String> l = Arrays.asList(ss);
            return l.subList(1, l.size());
        } 
        catch (Exception e) {
            this.log.error("Bad uri {}", e.getMessage());
            throw new WebdavBadRequest("Bad uri: " + uri);
        }
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private final static String DEFAULT_TIMEZONE = "America/Los_Angeles";
    private final static String DEFAULT_LANGUAGE = "en";
    
    private static List MODEL_PACKAGES = Arrays.asList(
        new String[]{
            "org:w3c",
            "org:openmdx:base",
            "org:openmdx:datastore1",
            "org:openmdx:filter1",
            "org:opencrx",
            "org:opencrx:kernel:base",
            "org:opencrx:kernel:generic",
            "org:opencrx:kernel:document1",
            "org:opencrx:kernel:workflow1",
            "org:opencrx:kernel:building1",
            "org:opencrx:kernel:address1",
            "org:opencrx:kernel:account1",
            "org:opencrx:kernel:product1",
            "org:opencrx:kernel:contract1",
            "org:opencrx:kernel:activity1",
            "org:opencrx:kernel:forecast1",
            "org:opencrx:kernel:code1",
            "org:opencrx:kernel:uom1",
            "org:oasis_open",
            "org:openmdx:generic1",
            "org:openmdx:compatibility:view1",
            "org:opencrx:kernel:home1",
            "org:openmdx:security:realm1",
            "org:openmdx:security:authorization1",
            "org:openmdx:security:authentication1",
            "org:opencrx:security:identity1",
            "org:opencrx:security:realm1",
            "org:opencrx:kernel:reservation1",
            "org:opencrx:kernel:admin1",
            "org:openmdx:compatibility:document1",
            "org:opencrx:kernel:model1",
            "org:opencrx:kernel:ras1",
            "org:opencrx:kernel:depot1",
            "org:openmdx:compatibility:state1",
            "org:opencrx:kernel",
            "org:opencrx:security"
        }
    );
    private static Model_1_0 model;
    
    private final Map<String,String> pendingIcals = new HashMap<String,String>();
    private static final Map<String,String> icals = new HashMap<String,String>();
    private String account;
    private CalTimezones timezones;
    private Logger log = LoggerFactory.getLogger(OpenCrxSysIntfImpl.class);
    private IcalTranslator icalTranslator;
    private String urlPrefix;
    private PersistenceManager pm;
    
}
