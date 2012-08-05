/*
 * ====================================================================
 * Project:     openCRX/CalDAV, http://www.opencrx.org/
 * Name:        $Id: OpenCrxSysIntfImpl.java,v 1.13 2008/01/27 23:42:03 wfro Exp $
 * Description: OpenCrxSysIntfImpl
 * Revision:    $Revision: 1.13 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/01/27 23:42:03 $
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
package org.opencrx.groupware.bedework.spi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryImpl;

import org.bedework.caldav.server.PropertyHandler;
import org.bedework.caldav.server.SysIntf;
import org.bedework.caldav.server.PropertyHandler.PropertyType;
import org.bedework.calfacade.BwCalendar;
import org.bedework.calfacade.BwDateTime;
import org.bedework.calfacade.BwEvent;
import org.bedework.calfacade.BwEventObj;
import org.bedework.calfacade.BwEventProxy;
import org.bedework.calfacade.BwFreeBusy;
import org.bedework.calfacade.BwUser;
import org.bedework.calfacade.CalFacadeDefs;
import org.bedework.calfacade.RecurringRetrievalMode;
import org.bedework.calfacade.ScheduleResult;
import org.bedework.calfacade.base.BwShareableDbentity;
import org.bedework.calfacade.exc.CalFacadeException;
import org.bedework.calfacade.filter.BwFilter;
import org.bedework.calfacade.svc.EventInfo;
import org.bedework.calfacade.timezones.SATimezonesImpl;
import org.bedework.calfacade.util.ChangeTable;
import org.bedework.icalendar.IcalTranslator;
import org.bedework.icalendar.Icalendar;
import org.bedework.icalendar.SAICalCallback;
import org.opencrx.groupware.generic.ActivitiesHelper;
import org.opencrx.groupware.generic.Util;
import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.Activity1Package;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.NewActivityResult;
import org.opencrx.kernel.backend.ICalendar;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.model1.accessor.basic.cci.Model_1_0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.rpi.cct.webdav.servlet.common.WebdavUtils;
import edu.rpi.cct.webdav.servlet.shared.PrincipalPropertySearch;
import edu.rpi.cct.webdav.servlet.shared.WebdavBadRequest;
import edu.rpi.cct.webdav.servlet.shared.WebdavException;
import edu.rpi.cct.webdav.servlet.shared.WebdavNotFound;
import edu.rpi.cct.webdav.servlet.shared.WebdavNsNode.PropertyTagEntry;
import edu.rpi.cct.webdav.servlet.shared.WebdavNsNode.UrlHandler;
import edu.rpi.cmt.access.Ace;
import edu.rpi.cmt.access.Acl;
import edu.rpi.cmt.access.PrincipalInfo;
import edu.rpi.cmt.access.Acl.CurrentAccess;
import edu.rpi.sss.util.xml.QName;

public class OpenCrxSysIntfImpl implements SysIntf {

    //-----------------------------------------------------------------------
    static {
        fromWho = new HashMap<Integer, String>();
        toWho = new HashMap<String, Integer>();
        initWhoMaps("/principals/users", Ace.whoTypeUser);
        initWhoMaps("/principals/groups", Ace.whoTypeGroup);
        initWhoMaps("/principals/tickets", Ace.whoTypeTicket);
        initWhoMaps("/principals/resources", Ace.whoTypeResource);
        initWhoMaps("/principals/venues", Ace.whoTypeVenue);
        initWhoMaps("/principals/hosts", Ace.whoTypeHost);
    };

    //-----------------------------------------------------------------------
    private static void initWhoMaps(
        String prefix, 
        int whoType
    ) {
        toWho.put(prefix, whoType);
        fromWho.put(whoType, prefix);
    }
    
    //-----------------------------------------------------------------------
    protected class OpenCrxBwCalendar extends BwCalendar {
    
        public OpenCrxBwCalendar(
            String account,
            String name,
            String path,
            int calType,
            BwCalendar parent,
            ActivitiesHelper activitiesHelper
        ) {   
            super(
                new BwUser(account), // owner
                false, // public
                new BwUser(account), // creator
                null, // access
                name, // name
                path, // path
                name, // summary
                null, // description
                null, // mailListId
                true, // calendarCollection
                parent, // parent
                Collections.EMPTY_LIST, // children
                calType, // calType
                ActivitiesHelper.formatDate(
                    ActivitiesHelper.getActivityGroupCreatedAt(activitiesHelper.getActivityGroup())
                ), // created
                ActivitiesHelper.formatDate(
                    ActivitiesHelper.getActivityGroupModifiedAt(activitiesHelper.getActivityGroup())
                ), // lastmod
                180, // sequence
                Collections.EMPTY_LIST, // categories                
                Collections.EMPTY_LIST // properties 
            );
            this.activitiesHelper = activitiesHelper;
        }
        
        public ActivitiesHelper getActivitiesHelper(
        ) {
            return this.activitiesHelper;
        }

        private static final long serialVersionUID = 1012308320085708890L;
        private final ActivitiesHelper activitiesHelper;
    }

    //-----------------------------------------------------------------------
    public static class MyPropertyHandler extends PropertyHandler {
        
        public Map<QName, PropertyTagEntry> getPropertyNames(
        ) {
            return propertyNames;
        }

        private static final long serialVersionUID = -5614711638441349226L;
        private final static HashMap<QName, PropertyTagEntry> propertyNames =
            new HashMap<QName, PropertyTagEntry>();
        
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
            Activity activity = OpenCrxSysIntfImpl.this.findActivity(guid);
            if((activity != null) && (activity.getIcal() != null)) {
                String ical = activity.getIcal();
                EventInfo evinfo = new EventInfo();
                BwEventObj ev = new BwEventObj();
                ev.setName(guid + ".ics");
                ev.setDtstamps();
                ev.setCreator(this.getUser());
                ev.setUid(guid);
                ev.setCalendar(cal);
                if(ical.indexOf("BEGIN:VTODO") > 0) {
                    ev.setEntityType(CalFacadeDefs.entityTypeTodo);
                }
                else {
                    ev.setEntityType(CalFacadeDefs.entityTypeEvent);                    
                }
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
                model = Util.createModel();
            }                
            // Get openMDX connection to openCRX provider
            // One connection per CalDAV session
            PersistenceManagerFactory persistenceManagerFactory = 
                (PersistenceManagerFactory)request.getSession().getAttribute(
                    PersistenceManagerFactory.class.getName()
                );
            if(persistenceManagerFactory == null) {                    
                try {
                    persistenceManagerFactory = Util.getPersistenceManagerFactory();
                    request.getSession().setAttribute(
                        PersistenceManagerFactory.class.getName(),
                        persistenceManagerFactory
                    );            
                }
                catch (NamingException e) {
                    throw new WebdavException("Can not get the initial context" + e.getMessage());
                }
                catch(ServiceException e) {
                    throw new WebdavException("Can not get connection to data provider" + e.getMessage());
                }        
            }
            // Get JMI root package
            this.pm = persistenceManagerFactory.getPersistenceManager(
                account, 
                request.getSession().getId()
            );  
            this.urlHandler = new UrlHandler(
                request, 
                true
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
    public boolean getDirectoryBrowsingDisallowed(
    ) throws WebdavException {
        return false;
    }

    //-----------------------------------------------------------------------
    public Collection<String>getGroups(
        String rootUrl,
        String principalUrl
    ) throws WebdavException {
        return Collections.emptySet();
    }
    
    //-----------------------------------------------------------------------
    public String getAccount(
    ) throws WebdavException {
        return this.account;
    }

    //-----------------------------------------------------------------------
    public boolean isPrincipal(
        String val
    ) throws WebdavException {
        return val.startsWith("/principals");
    }
    
    //-----------------------------------------------------------------------
    public String makeHref(
        String id, 
        int whoType
    ) throws WebdavException {
      String root = fromWho.get(whoType);
      if (root == null) {
        throw new WebdavException("unknown who type " + whoType);
      }
      return root + "/" + id;
    }

    //-----------------------------------------------------------------------
    public PropertyHandler getPropertyHandler(
        PropertyType ptype
    ) throws WebdavException {
      return new MyPropertyHandler();
    }

    //-----------------------------------------------------------------------
    public UrlHandler getUrlHandler(
    ) {
        return this.urlHandler;
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
        return new CalUserInfo(
            account, 
            "/principals/users", 
            null, 
            null, 
            null, 
            null, 
            null
        );
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
    public PrincipalInfo getPrincipalInfo(
        String href
    ) throws WebdavException {
        PrincipalInfo pi = new PrincipalInfo();
        try {
            String uri = new URI(href).getPath();
            if (!isPrincipal(uri)) {
                return null;
            }
            int start;
            int end = uri.length();
            if (uri.endsWith("/")) {
                end--;
            }
            String groupRoot = "/principals/groups";
            String userRoot = "/principals/users";
            if (uri.startsWith(userRoot)) {
                start = userRoot.length();
                pi.prefix = userRoot;
                pi.whoType = Ace.whoTypeUser;
            } 
            else if (uri.startsWith(groupRoot)) {
                start = groupRoot.length();
                pi.prefix = groupRoot;
                pi.whoType = Ace.whoTypeGroup;
            } 
            else {
                throw new WebdavNotFound(uri);
            }
            if (start == end) {
                // Trying to browse user principals?
                pi.who = null;
            } 
            else if (uri.charAt(start) != '/') {
                throw new WebdavNotFound(uri);
            } 
            else {
                pi.who = uri.substring(start + 1, end);
            }
            return pi;
        } 
        catch (Throwable t) {
            throw new WebdavException(t);
        }
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
        EventInfo event
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    protected Activity findActivity(
        String calUid
    ) {
        ActivityQuery query = this.activityPackage.createActivityQuery();
        query.thereExistsExternalLink().equalTo(ICalendar.ICAL_SCHEMA + calUid);
        List activities = this.activitySegment.getActivity(query);
        if(activities.isEmpty()) {
            query = this.activityPackage.createActivityQuery();
            query.thereExistsExternalLink().equalTo(ICalendar.ICAL_SCHEMA + calUid.replace('.', '+'));
            activities = this.activitySegment.getActivity(query);
            if(activities.isEmpty()) {
                return null;
            }
            else {
                return (Activity)activities.iterator().next();
            }
        }
        else {
            return (Activity)activities.iterator().next();
        }
    }
    
    //-----------------------------------------------------------------------
    public Collection<BwEventProxy> addEvent(
        BwCalendar cal,
        EventInfo eventInfo,
        boolean rollbackOnError
    ) throws WebdavException {
        String calendar = this.pendingIcals.get(eventInfo.getEvent().getUid());
        if((calendar != null) && (cal instanceof OpenCrxBwCalendar)) {      
            ActivityCreator activityCreator = ((OpenCrxBwCalendar)cal).getActivitiesHelper().getActivityGroup().getDefaultCreator();
            if(activityCreator != null) {
                try {
                    String name = "NA";
                    int posSummary;
                    if((posSummary = calendar.indexOf("SUMMARY:")) > 0) {
                        if(calendar.indexOf("\n", posSummary) > 0) {
                            name = calendar.substring(posSummary + 8, calendar.indexOf("\n", posSummary));
                        }
                    }
                    this.pm.currentTransaction().begin();
                    NewActivityResult result = activityCreator.newActivity(
                        null,
                        null,
                        null,
                        name,
                        (short)0,
                        null,
                        null,
                        null
                    );
                    this.pm.currentTransaction().commit();
                    try {
                        Activity activity = (Activity)this.pm.getObjectById(result.getActivity().refMofId());
                        this.pm.currentTransaction().begin();
                        activity.importItem(
                            calendar.getBytes("UTF-8"), 
                            "text/calendar", 
                            "import.ics", 
                            (short)0
                        );
                        this.pm.currentTransaction().commit();
                    }
                    catch(Exception e) {
                        this.log.error("Error importing calendar {}. Reason is {}", calendar, e.getMessage());
                        new ServiceException(e).log();
                        try {
                            this.pm.currentTransaction().rollback();
                        } catch(Exception e0) {}
                    }
                }
                catch(Exception e) {
                    this.log.error("Can not create activity. Reason is {}", e.getMessage());
                    new ServiceException(e).log();
                    try {
                        this.pm.currentTransaction().rollback();
                    } catch(Exception e0) {}
                }
            }
        }
        return Collections.EMPTY_LIST;
    }

    //-----------------------------------------------------------------------
    public void updateEvent(
        BwEvent event,
        Collection<BwEventProxy> overrides,
        ChangeTable changes
    ) throws WebdavException {
        String calendar = this.pendingIcals.get(event.getUid());
        if(calendar != null) {       
            try {
                Activity activity = this.findActivity(event.getUid());
                if(activity != null) {
                    this.pm.currentTransaction().begin();
                    activity.importItem(
                        calendar.getBytes("UTF-8"), 
                        "text/calendar", 
                        "tmp.ics", 
                        (short)0
                    );
                    this.pm.currentTransaction().commit();
                }
            }
            catch(Exception e) {
                this.log.error("Error importing calendar {}. Reason is {}", calendar, e.getMessage());
                try {
                    this.pm.currentTransaction().rollback();
                } catch(Exception e0) {}
            }
        }
    }

    //-----------------------------------------------------------------------
    public Collection<EventInfo> getEvents(
        BwCalendar cal,
        BwFilter filter,
        RecurringRetrievalMode recurRetrieval
    ) throws WebdavException {        
        if(cal instanceof OpenCrxBwCalendar) {
            List<EventInfo> events = new ArrayList<EventInfo>();        
            ActivityQuery activityQuery = this.activityPackage.createActivityQuery();
            activityQuery.forAllDisabled().isFalse();
            activityQuery.ical().isNonNull();
            Collection<Activity> filteredActivities = ((OpenCrxBwCalendar)cal).getActivitiesHelper().getFilteredActivities(activityQuery);
            for(Activity activity : filteredActivities) {
                Icalendar ical = this.fromIcal(
                    cal, 
                    new StringReader(activity.getIcal())
                );
                if(!ical.getComponents().isEmpty()) {
                   EventInfo ei = (EventInfo)ical.getComponents().iterator().next();
                   if(ei.getEvent().getCalendar() != null) {
                       events.add(ei);
                   }
                   else {
                       AppLog.info("Can not get calendar for activity", activity.refGetPath());
                   }
                }
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
        String calUid,
        RecurringRetrievalMode recurRetrieval
    ) throws WebdavException {
        int pos = calUid.lastIndexOf(".");
        calUid = pos > 0 ? calUid.substring(0, pos) : calUid;
        Activity activity = this.findActivity(calUid);
        if((activity != null) && (activity.getIcal() != null)) {
            Icalendar ical = this.fromIcal(
                cal, 
                new StringReader(activity.getIcal())
            );
            if(!ical.getComponents().isEmpty()) {
                EventInfo ev = (EventInfo)ical.getComponents().iterator().next();
                return ev;
            }
        }
        return null;
    }

    //-----------------------------------------------------------------------
    public void deleteEvent(
        BwEvent ev
    ) throws WebdavException {
        Activity activity = this.findActivity(ev.getUid());
        if(activity != null) {
            try {
                this.pm.currentTransaction().begin();
                activity.setDisabled(true);
                this.pm.currentTransaction().commit();
            }
            catch(Exception e) {
                this.log.error("Can not delete event {}. Reason is {}", ev.getUid(), e.getMessage());
                new ServiceException(e).log();
                try {
                    this.pm.currentTransaction().rollback();
                } catch(Exception e0) {}
            }
        }
    }

    //-----------------------------------------------------------------------
    public void deleteCalendar(
        BwCalendar cal
    ) throws WebdavException {      
        List<String> l = null;
        try {
            l = ActivitiesHelper.splitUri(cal.getPath(), true);
            String calendarName = l.get(l.size()-1);
            if((calendarName != null) && (calendarName.indexOf(".") > 0)) {
                String uid = calendarName.substring(0, calendarName.indexOf("."));
                Activity activity = this.findActivity(uid);
                if(activity != null) {
                    try{
                        this.pm.currentTransaction().begin();
                        activity.setDisabled(true);
                        this.pm.currentTransaction().commit();
                    }
                    catch(Exception e) {
                        this.log.error("Can not delete event {}. Reason is {}", uid, e.getMessage());
                        new ServiceException(e).log();
                        try {
                            this.pm.currentTransaction().rollback();
                        } catch(Exception e0) {}
                    }
                }            
            }
        }
        catch(IllegalArgumentException  e) {
            throw new WebdavBadRequest("Bad uri: " + cal.getPath());
        }
    }

    //-----------------------------------------------------------------------
    public ScheduleResult requestFreeBusy(
        EventInfo val
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
    public Collection<String> getFreebusySet(
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }
    
    //-----------------------------------------------------------------------
    public CurrentAccess checkAccess(
        BwShareableDbentity ent,
        int desiredAccess,
        boolean returnResult
    ) throws WebdavException {
        return null;
    }

    //-----------------------------------------------------------------------
    public void updateAccess(
        BwCalendar cal,
        Acl acl
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
    }

    //-----------------------------------------------------------------------
    public void updateAccess(
        BwEvent ev,
        Acl acl
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
        try {
            ActivitiesHelper helper = new ActivitiesHelper(pm);
            int pathLength = helper.parseFilteredActivitiesUri(path);
            if(pathLength >= 3) {
                this.activityPackage = helper.getActivityPackage();
                this.activitySegment = helper.getActivitySegment();
                // No calendar without a corresponding activity group
                if(helper.getActivityGroup() == null) {
                    return null;
                }
                BwCalendar cal = new OpenCrxBwCalendar(
                    this.account,
                    helper.getCalendarName() + (helper.getActivityFilter() == null ? "" : "*" + helper.getFilterName()),
                    path,
                    1,
                    null,
                    helper
                );
                return cal;
            }
            else {
                throw new WebdavNotFound(path);            
            }
        }
        catch(IllegalArgumentException  e) {
            throw new WebdavBadRequest("Bad uri: " + path);
        }
    }

    //-----------------------------------------------------------------------
    public void updateCalendar(
        BwCalendar val
    ) throws WebdavException {
        throw new WebdavException("Unsupported operation");
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
        String calendar = null;
        try {
            // Get a copy of the ical. The original ical is attached as media 
            // to the calendar activity. This way only a subset of the ical
            // fields have to be mapped to an activity object.
            // PROD-ID decides whether to use ISO-8859-1 or UTF-8 encoding.
            ByteArrayOutputStream isoBos = new ByteArrayOutputStream();
            OutputStreamWriter isoWriter = new OutputStreamWriter(isoBos, "ISO-8859-1");
            ByteArrayOutputStream utf8Bos = new ByteArrayOutputStream();
            OutputStreamWriter utf8Writer = new OutputStreamWriter(utf8Bos, "UTF-8");
            int b;
            while((b = ical.read()) >= 0) {
                isoWriter.write(b);
                utf8Writer.write(b);
            }
            isoWriter.close();
            utf8Writer.close();            
            calendar = new String(utf8Bos.toByteArray(), "UTF-8");
            if(calendar.indexOf(PROD_ID_CHANDLER) > 0) {
                calendar = new String(isoBos.toByteArray(), "UTF-8");
            }
            // Some WebDAV clients do not always send icals!
            // In case we do not have a VCALENDAR ignore and return a dummy
            if(!calendar.startsWith("BEGIN:VCALENDAR") && !calendar.startsWith("begin:vcalendar")) {
                calendar = 
                    "BEGIN:VCALENDAR\n" +
                    "PRODID:" + ICalendar.PROD_ID + "\n" +
                    "UID:" + UUIDConversion.toUID(UUIDs.getGenerator().next()) + "\n" +
                    "VERSION:2.0\n" +
                    "END:VCALENDAR\n";
            }
            // Eliminate redundant DTSTART / DURATION tags
            BufferedReader source = new BufferedReader(new StringReader(calendar));
            StringBuilder target = new StringBuilder();
            boolean isEvent = false;
            boolean hasDtStart = false;
            String duration = null;
            String line = null;
            while((line = source.readLine()) != null) {
                if(
                    line.startsWith("BEGIN:VEVENT") ||
                    line.startsWith("begin:vevent") ||
                    line.startsWith("BEGIN:VTODO") ||
                    line.startsWith("begin:vtodo")
                ) {
                    isEvent = true;
                    target.append(line).append("\n");
                }
                else if(
                    line.startsWith("END:VEVENT") ||
                    line.startsWith("end:vevent") ||
                    line.startsWith("END:VTODO") ||
                    line.startsWith("end:vtodo")
                ) {
                    if(!hasDtStart && (duration != null)) {
                        target.append(duration).append("\n");                        
                    }
                    hasDtStart = false;
                    duration = null;
                    isEvent = false;
                    target.append(line).append("\n");
                }
                else if(
                    isEvent &&
                    line.startsWith("DTSTART") ||
                    line.startsWith("dtstart")
                ) {
                    hasDtStart = true;
                    target.append(line).append("\n");
                }
                else if(
                    isEvent &&
                    line.startsWith("DURATION") ||
                    line.startsWith("duration")
                ) {
                    duration = line;
                }
                else {
                    target.append(line).append("\n");                    
                }
            }
            calendar = target.toString();
            // Get UID
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
            // Ignore non iCal requests. Return a dummy calendar
            return this.icalTranslator.fromIcal(
                cal, 
                new StringReader(calendar)
            );
        } 
        catch (Exception e) {
            this.log.warn("Error converting calendar {}", calendar);
            new ServiceException(e).log();
            throw new WebdavBadRequest(e.getMessage());
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

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private final static String DEFAULT_TIMEZONE = "America/Los_Angeles";
    private final static String PROD_ID_CHANDLER = "PRODID:-//PYVOBJECT//NONSGML Version 1//EN";
    
    private static Model_1_0 model;
    private static HashMap<Integer, String> fromWho;
    private static HashMap<String, Integer> toWho;
    private final Map<String,String> pendingIcals = new HashMap<String,String>();
    private String account;
    private CalTimezones timezones;
    private Logger log = LoggerFactory.getLogger(OpenCrxSysIntfImpl.class);
    private IcalTranslator icalTranslator;
    private String urlPrefix;
    private PersistenceManager pm;
    private Activity1Package activityPackage = null;    
    private org.opencrx.kernel.activity1.jmi1.Segment activitySegment = null;
    private UrlHandler urlHandler;
    
}
