/*
 * ====================================================================
 * Project:     openCRX/CalDAV, http://www.opencrx.org/
 * Name:        $Id: ActivitiesHelper.java,v 1.6 2009/07/13 14:27:36 wfro Exp $
 * Description: ActivitiesHelper
 * Revision:    $Revision: 1.6 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/07/13 14:27:36 $
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
package org.opencrx.kernel.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.opencrx.kernel.activity1.cci2.ActivityCategoryQuery;
import org.opencrx.kernel.activity1.cci2.ActivityFilterGlobalQuery;
import org.opencrx.kernel.activity1.cci2.ActivityFilterGroupQuery;
import org.opencrx.kernel.activity1.cci2.ActivityMilestoneQuery;
import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.cci2.ActivityTrackerQuery;
import org.opencrx.kernel.activity1.cci2.ResourceQuery;
import org.opencrx.kernel.activity1.jmi1.AbstractFilterActivity;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.Activity1Package;
import org.opencrx.kernel.activity1.jmi1.ActivityCategory;
import org.opencrx.kernel.activity1.jmi1.ActivityGroup;
import org.opencrx.kernel.activity1.jmi1.ActivityMilestone;
import org.opencrx.kernel.activity1.jmi1.ActivityTracker;
import org.opencrx.kernel.activity1.jmi1.Resource;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.openmdx.base.naming.Path;
import org.openmdx.base.text.format.DateFormat;

public class ActivitiesHelper {

    //-----------------------------------------------------------------------
    public ActivitiesHelper(
       PersistenceManager pm
    ) {
        this.pm = pm;
    }
    
    //-----------------------------------------------------------------------
    public int parseFilteredActivitiesUri(
       String uri
    ) throws IllegalArgumentException  {
        List<String> l = ActivitiesHelper.splitUri(uri);
        if(l.size() >= 3) {
            // URL pattern is
            // ./provider.name/segment.name/tracker|milestone|category|home/calendar.name[/filter/filter.name]
            String providerName = l.get(0);
            String segmentName = l.get(1);
            String calendarType = null;
            this.calendarName = null;
            this.filterName = null;
            if("filter".equals(l.get(l.size()-2))) {
                calendarType = l.get(l.size()-4);
                this.calendarName = l.get(l.size()-3);
                this.filterName = l.get(l.size()-1);
            }
            else {
                calendarType = l.get(l.size()-2);
                this.calendarName = l.get(l.size()-1);
                this.filterName = null;                
            }
            if(".chandler".equals(this.calendarName)) {
                calendarType = l.get(l.size()-3);
                this.calendarName = l.get(l.size()-2);
            }
            else if("null".equals(this.calendarName)) {
                return l.size();
            }
            this.activityPackage = Utils.getActivityPackage(this.pm); 
            this.activitySegment = 
                (org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(
                    new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName)
                );
            this.activityGroup = null;
            this.userHome = null;
            this.activityFilter = null;
            this.resource = null;
            if(
                "milestone".equals(calendarType) ||
                "category".equals(calendarType) ||
                "tracker".equals(calendarType)
            ) {
                List<? extends ActivityGroup> activityGroups = Collections.emptyList();
                if("milestone".equals(calendarType)) {
                    ActivityMilestoneQuery query = this.activityPackage.createActivityMilestoneQuery();
                    query.name().equalTo(this.calendarName);
                    List<org.opencrx.kernel.activity1.jmi1.ActivityMilestone> milestones = this.activitySegment.getActivityMilestone(query);
                    activityGroups = milestones;
                }
                else if("category".equals(calendarType)) {
                    ActivityCategoryQuery query = this.activityPackage.createActivityCategoryQuery();
                    query.name().equalTo(this.calendarName);
                    List<org.opencrx.kernel.activity1.jmi1.ActivityCategory> categories = this.activitySegment.getActivityCategory(query);
                    activityGroups = categories;
                }
                else if("tracker".equals(calendarType)) {
                    ActivityTrackerQuery query = this.activityPackage.createActivityTrackerQuery();
                    query.name().equalTo(this.calendarName);
                    List<org.opencrx.kernel.activity1.jmi1.ActivityTracker> trackers = this.activitySegment.getActivityTracker(query);
                    activityGroups = trackers;
                }
                if(!activityGroups.isEmpty()) {
                    this.activityGroup = activityGroups.iterator().next();
                    if(this.filterName != null) {
                        ActivityFilterGroupQuery query = this.activityPackage.createActivityFilterGroupQuery();
                        query.name().equalTo(this.filterName);
                        List<org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup> activityFilters = this.activityGroup.getActivityFilter(query);
                        if(!activityFilters.isEmpty()) {
                            this.activityFilter = activityFilters.iterator().next();
                        }
                    }
                }                
            }
            else if("globalfilter".equals(calendarType)) {
                ActivityFilterGlobalQuery query = this.activityPackage.createActivityFilterGlobalQuery();
                query.name().equalTo(this.calendarName);
                List<org.opencrx.kernel.activity1.jmi1.ActivityFilterGlobal> globalFilters = this.activitySegment.getActivityFilter(query);
                this.activityFilter = globalFilters.iterator().next();
            }
            else if("userhome".equals(calendarType)) {
                this.userHome = (org.opencrx.kernel.home1.jmi1.UserHome)this.pm.getObjectById(
                    new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName + "/userHome/" + this.calendarName)
                );
            }
            else if("resource".equals(calendarType)) {
                ResourceQuery query = this.activityPackage.createResourceQuery();
                query.name().equalTo(this.calendarName);
                List<Resource> resources = this.activitySegment.getResource(query);
                this.resource = resources.iterator().next();
            }
        }
        return l.size();
    }
        
    //-----------------------------------------------------------------------
    public void parseDisabledFilter(
        String isDisabledFilter
    ) {
        this.isDisabledFilter = isDisabledFilter == null
            ? false
            : Boolean.valueOf(isDisabledFilter); 
    }
    
    //-----------------------------------------------------------------------
    public boolean isDisabledFilter(
    ) {
        return this.isDisabledFilter;
    }
    
    //-----------------------------------------------------------------------
    public ActivityGroup getActivityGroup(
    ) {
        return this.activityGroup;
    }
    
    //-----------------------------------------------------------------------
    public UserHome getUserHome(
    ) {
        return this.userHome;
    }
    
    //-----------------------------------------------------------------------
    public Resource getResource(
    ) {
        return this.resource;
    }
    
    //-----------------------------------------------------------------------
    public AbstractFilterActivity getActivityFilter(
    ) {
        return this.activityFilter;
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.activity1.jmi1.Segment getActivitySegment(
    ) {
        return this.activitySegment;
    }
    
    //-----------------------------------------------------------------------
    public String getCalendarName(
    ) {
        return this.calendarName;
    }
    
    //-----------------------------------------------------------------------
    public String getFilterName(
    ) {
        return this.filterName;
    }

    //-----------------------------------------------------------------------
    public Collection<Activity> getFilteredActivities(
        ActivityQuery activityQuery            
    ) {
        if(this.activityFilter != null) {
            return this.activityFilter.getFilteredActivity(activityQuery);
        }
        else if(this.activityGroup != null) {
            return this.activityGroup.getFilteredActivity(activityQuery);
        }
        else if(this.userHome != null) {
            return this.userHome.getAssignedActivity(activityQuery);
        }
        else if(this.resource != null) {
            return this.resource.getAssignedActivity(activityQuery);
        }
        else {
            return Collections.emptyList();
        }
    }
    
    //-----------------------------------------------------------------------
    public String getFilteredActivitiesParentId(
    ) {
        if(this.activityFilter != null) {
            return this.activityFilter.refGetPath().getBase();
        }
        else if(this.activityGroup != null) {
            return this.activityGroup.refGetPath().getBase();
        }
        else if(this.userHome != null) {
            return this.userHome.refGetPath().getBase();
        }
        else if(this.resource != null) {
            return this.resource.refGetPath().getBase();
        }
        else {
            return "-";
        }        
    }
    
    //-----------------------------------------------------------------------
    public static String formatDate(
        Date date
    ) {
        return DateFormat.getInstance().format(date).substring(0, 15) + "Z";
    }
    
    //-----------------------------------------------------------------------
    public static Date getActivityGroupModifiedAt(
        ActivityGroup activityGroup
    ) {
        if(activityGroup instanceof ActivityTracker) {
            return ((ActivityTracker)activityGroup).getModifiedAt();
        }
        else if(activityGroup instanceof ActivityMilestone) {
            return ((ActivityMilestone)activityGroup).getModifiedAt();
        }
        else if(activityGroup instanceof ActivityCategory) {
            return ((ActivityCategory)activityGroup).getModifiedAt();
        }
        else {
            return new Date();
        }
    }

    //-----------------------------------------------------------------------
    public static Date getActivityGroupCreatedAt(
        ActivityGroup activityGroup
    ) {
        if(activityGroup instanceof ActivityTracker) {
            return ((ActivityTracker)activityGroup).getCreatedAt();
        }
        else if(activityGroup instanceof ActivityMilestone) {
            return ((ActivityMilestone)activityGroup).getCreatedAt();
        }
        else if(activityGroup instanceof ActivityCategory) {
            return ((ActivityCategory)activityGroup).getCreatedAt();
        }
        else {
            return new Date();
        }
    }
    
    //-----------------------------------------------------------------------
    public static List<String> splitUri(
        String uri 
    ) throws IllegalArgumentException  {
        try {
            String[] ss = uri.split("/");
            int pathLength = ss.length - 1;  // First element is empty string
            if (pathLength < 2) {
                throw new IllegalArgumentException ("Bad uri: " + uri);
            }
            List<String> l = Arrays.asList(ss);
            return l.subList(1, l.size());
        } 
        catch (Exception e) {
            throw new IllegalArgumentException ("Bad uri: " + uri);
        }
    }
            
    //-----------------------------------------------------------------------
    public PersistenceManager getPersistenceManager(
    ) {
        return this.pm;
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final PersistenceManager pm;
    protected ActivityGroup activityGroup = null;
    protected UserHome userHome = null;
    protected Resource resource = null;
    protected AbstractFilterActivity activityFilter = null;
    protected Activity1Package activityPackage = null;    
    protected org.opencrx.kernel.activity1.jmi1.Segment activitySegment = null;
    protected String calendarName = null;
    protected String filterName = null;
    protected boolean isDisabledFilter;
    
}
