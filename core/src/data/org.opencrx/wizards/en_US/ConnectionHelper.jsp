<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: ConnectionHelper.jsp,v 1.35 2012/07/08 13:30:30 wfro Exp $
 * Description: ConnectionHelper: Generate Adapter URLs
 * Revision:    $Revision: 1.35 $
 * Owner:       CRIXP Corp., Switzerland, http://www.crixp.com
 * Date:        $Date: 2012/07/08 13:30:30 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2012 CRIXP Corp., Switzerland
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
%><%@ page session="true" import="
java.util.*,
java.util.zip.*,
java.io.*,
java.text.*,
java.math.*,
java.net.*,
java.sql.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.base.exception.*,
org.openmdx.kernel.id.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.reports.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*,
org.openmdx.base.query.*,
org.openmdx.kernel.log.*
" %>
<%!
	enum ResourceType {
    	PROFILE("Profiles"),
    	EVENTS_AND_TASKS("Events / Tasks"),
    	CONTACT("Contacts");
    	
    	private ResourceType(
    		String label
    	) {
    		this.label = label;
    	}
    	public String getLabel(
    	) {
    		return this.label;
    	}
    	private final String label;
	}

    enum SelectorType {
    	TRACKER,
    	TRACKER_FILTER,
    	CATEGORY,
    	CATEGORY_FILTER,
    	MILESTONE,
    	MILESTONE_FILTER,
    	GLOBAL_FILTER,
    	USERHOME,
    	AIRSYNCPROFILE,
    	CALENDARPROFILE,
    	RESOURCE,
    	BDAY,
    	VCARD,
    	DOCUMENTPROFILE,
    	CARDPROFILE
    }

%>
<%
	request.setCharacterEncoding("UTF-8");
	ApplicationContext app = (ApplicationContext)session.getValue("ObjectInspectorServlet.ApplicationContext");
	ViewsCache viewsCache = (ViewsCache)session.getValue(WebKeys.VIEW_CACHE_KEY_SHOW);
	String requestId =  request.getParameter(Action.PARAMETER_REQUEST_ID);
	String objectXri = request.getParameter(Action.PARAMETER_OBJECTXRI);
	if(app == null || objectXri == null || viewsCache.getView(requestId) == null) {
      response.sendRedirect(
         request.getContextPath() + "/" + WebKeys.SERVLET_NAME
      );
      return;
   }
   Texts_1_0 texts = app.getTexts();
   javax.jdo.PersistenceManager pm = app.getNewPmData();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
  <title><%= app.getApplicationName() %> - Connection Helper: AirSync / Calendar / vCard / WebDAV</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <meta name="label" content="Connection Helper: AirSync/Calendar/vCard/WebDAV">
  <meta name="toolTip" content="Connection Helper: AirSync/Calendar/vCard/WebDAV">
  <meta name="targetType" content="_blank">
  <!-- calendars based on activities -->
  <meta name="forClass" content="org:opencrx:kernel:activity1:ActivityTracker">
  <meta name="forClass" content="org:opencrx:kernel:activity1:ActivityCategory">
  <meta name="forClass" content="org:opencrx:kernel:activity1:ActivityMilestone">
  <meta name="forClass" content="org:opencrx:kernel:activity1:ActivityFilterGlobal">
  <meta name="forClass" content="org:opencrx:kernel:activity1:ActivityFilterGroup">
  <meta name="forClass" content="org:opencrx:kernel:activity1:Resource">
  <meta name="forClass" content="org:opencrx:kernel:home1:UserHome">
  <meta name="forClass" content="org:opencrx:kernel:home1:AirSyncProfile">
  <meta name="forClass" content="org:opencrx:kernel:home1:CalendarProfile">
  <!-- calendars based on contacts -->
  <meta name="forClass" content="org:opencrx:kernel:account1:AccountFilterGlobal"> <!-- bday -->
  <!-- webdav -->
  <meta name="forClass" content="org:opencrx:kernel:home1:DocumentProfile">
  <!-- carddav -->
  <meta name="forClass" content="org:opencrx:kernel:home1:CardProfile">

  <meta name="order" content="5998">
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <script language="javascript" type="text/javascript" src="../../javascript/prototype.js"></script>
  <link href="../../_style/n2default.css" rel="stylesheet" type="text/css">
  <link href="../../_style/colors.css" rel="stylesheet" type="text/css">
  <link rel='shortcut icon' href='../../images/favicon.ico' />

  <style type="text/css" media="all">
    TABLE.fieldGroup TD {
      vertical-align:middle;
    }
    .label {
      width:190px;
    }
  </style>
</head>

<%
  final String FORM_ACTION = "ConnectionHelper.jsp";
  NumberFormat formatter = new DecimalFormat("00000");
  final Integer MAX_ENTRY_SELECT = 200;

  try {
	  boolean isFirstCall = request.getParameter("isFirstCall") == null; // used to properly initialize various options
    boolean mustReload = request.getParameter("mustReload") != null;

    //System.out.println("must reload = " + mustReload);

    RefObject_1_0 obj = (RefObject_1_0)pm.getObjectById(new Path(objectXri));
    Path objectPath = obj.refGetPath();
    String providerName = objectPath.get(2);
    String segmentName = objectPath.get(4);
    UserDefinedView userView = new UserDefinedView(
        pm.getObjectById(new Path(objectXri)),
        app,
        viewsCache.getView(requestId)
    );

    String anchorObjectXri = request.getParameter("anchorObjectXri");
    if (!isFirstCall && (anchorObjectXri != null)) {
        try {
            obj = (RefObject_1_0)pm.getObjectById(new Path(anchorObjectXri));
        } catch (Exception e) {}
    }
    ResourceType type = request.getParameter("type") != null ? ResourceType.valueOf(request.getParameter("type")) : null;
    SelectorType selectorType = request.getParameter("selectorType") != null ? SelectorType.valueOf(request.getParameter("selectorType")) : null;
	String optionMax = request.getParameter("optionMax") == null ? "500" : request.getParameter("optionMax");	
	String optionUser = request.getParameter("optionUser") == null ? app.getLoginPrincipal() : request.getParameter("optionUser");	
	String optionIsDisabled = request.getParameter("optionIsDisabled") == null ? "false" : request.getParameter("optionIsDisabled");
	String optionSummaryPrefix = request.getParameter("optionSummaryPrefix") == null ? "Birthdays" : request.getParameter("optionSummaryPrefix");
	String optionCategories = request.getParameter("optionCategories") == null ? "Birthday" : request.getParameter("optionCategories");
	String optionYear = request.getParameter("optionYear") == null ? "2012" : request.getParameter("optionYear");
	String optionTimelineHeight = request.getParameter("optionTimelineHeight") == null ? "500" : request.getParameter("optionTimelineHeight");
	String optionAlarm = request.getParameter("optionAlarm") == null ? "false" : request.getParameter("optionAlarm");

    final String ACTIVITYTRACKER_CLASS = "org:opencrx:kernel:activity1:ActivityTracker";
    final String ACTIVITYCATEGORY_CLASS = "org:opencrx:kernel:activity1:ActivityCategory";
    final String ACTIVITYMILESTONE_CLASS = "org:opencrx:kernel:activity1:ActivityMilestone";
    final String ACTIVITYFILTERGROUP_CLASS = "org:opencrx:kernel:activity1:ActivityFilterGroup";
    final String ACTIVITYFILTERGLOBAL_CLASS = "org:opencrx:kernel:activity1:ActivityFilterGlobal";
    final String RESOURCE_CLASS = "org:opencrx:kernel:activity1:Resource";
    final String RESOURCEASSIGNMENT_CLASS = "org:opencrx:kernel:activity1:ResourceAssignment";
    final String USERHOME_CLASS = "org:opencrx:kernel:home1:UserHome";
    final String AIRSYNCPROFILE_CLASS = "org:opencrx:kernel:home1:AirSyncProfile";
    final String CALENDARPROFILE_CLASS = "org:opencrx:kernel:home1:CalendarProfile";
    final String ACCOUNTFILTERGLOBAL_CLASS = "org:opencrx:kernel:account1:AccountFilterGlobal";
    final String CONTACT_CLASS = "org:opencrx:kernel:account1:Contact";
    final String DOCUMENTPROFILE_CLASS = "org:opencrx:kernel:home1:DocumentProfile";
    final String CARDPROFILE_CLASS = "org:opencrx:kernel:home1:CardProfile";
    final String ABSTRACTPRICELEVEL_CLASS = "org:opencrx:kernel:product1:AbstractPriceLevel";

    final String HTML_COMMENT_BEGIN = "<!-- ";
    final String HTML_COMMENT_END = " -->";
    final String PROTOCOL_SPECIFIER_HTTP = "http:";
    final String PROTOCOL_SPECIFIER_HTTPS = "https:";
    final String UNKNOWN = "_?_";

    String urlBase = (request.getRequestURL().toString()).substring(0, (request.getRequestURL().toString()).indexOf(request.getServletPath().toString()));
    String anchorObjectXriFromInitialObject = "";
    String anchorObjectFilteredXriFromInitialObject = null;

    String server = "";
    String path = "";
                 
    String selectedAnchorObjectXRI = "";

    int tabIndex = 0;

    // get current userHome
    org.opencrx.kernel.home1.jmi1.UserHome currentUserHome = (org.opencrx.kernel.home1.jmi1.UserHome)pm.getObjectById(app.getUserHomeIdentityAsPath());
    org.opencrx.kernel.account1.jmi1.Segment accountSegment = org.opencrx.kernel.backend.Accounts.getInstance().getAccountSegment(pm, providerName, segmentName);
    org.opencrx.kernel.activity1.jmi1.Segment activitySegment = org.opencrx.kernel.backend.Activities.getInstance().getActivitySegment(pm, providerName, segmentName);
    org.opencrx.kernel.home1.jmi1.Segment homeSegment = org.opencrx.kernel.backend.UserHomes.getInstance().getUserHomeSegment(pm, providerName, segmentName);

    // Option activity filter at group level
    boolean isGroupFilter = false;
    if(obj instanceof org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup) {
    	isGroupFilter = true;
		org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup activityFilterGroup =
        	(org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup)obj;
      	if((activityFilterGroup.getName() != null) && !activityFilterGroup.getName().isEmpty()) {
        	anchorObjectXriFromInitialObject = activityFilterGroup.refMofId();
      	}
      	obj = (RefObject_1_0)pm.getObjectById(new Path(activityFilterGroup.refMofId()).getParent().getParent());
    }
    if(obj instanceof org.opencrx.kernel.activity1.jmi1.ActivityFilterGlobal) {
    	if(type == null) {
	    	type = ResourceType.EVENTS_AND_TASKS;
	    	selectorType = SelectorType.GLOBAL_FILTER;
    	}
      	org.opencrx.kernel.activity1.jmi1.ActivityFilterGlobal activityFilterGlobal =
        	(org.opencrx.kernel.activity1.jmi1.ActivityFilterGlobal)obj;
      	if((activityFilterGlobal.getName() != null) && !activityFilterGlobal.getName().isEmpty()) {
        	anchorObjectFilteredXriFromInitialObject = activityFilterGlobal.refMofId();
      	}
    }
    else if(obj instanceof org.opencrx.kernel.activity1.jmi1.ActivityTracker) {
		org.opencrx.kernel.activity1.jmi1.ActivityTracker activityTracker =
			(org.opencrx.kernel.activity1.jmi1.ActivityTracker)obj;
		if((activityTracker.getName() != null) && !activityTracker.getName().isEmpty()) {
	        if(!isGroupFilter) {
				anchorObjectXriFromInitialObject = activityTracker.refMofId();
	        }
      	}
		if(type == null) {
			type = ResourceType.EVENTS_AND_TASKS;
			selectorType = isGroupFilter ? SelectorType.TRACKER_FILTER : SelectorType.TRACKER;
		}
    }
    else if(obj instanceof org.opencrx.kernel.activity1.jmi1.ActivityCategory) {
		org.opencrx.kernel.activity1.jmi1.ActivityCategory activityCategory =
			(org.opencrx.kernel.activity1.jmi1.ActivityCategory)obj;
		if((activityCategory.getName() != null) && !activityCategory.getName().isEmpty()) {
	        if (!isGroupFilter) {
	          anchorObjectXriFromInitialObject = activityCategory.refMofId();
	        }
		}
		if(type == null) {
			type = ResourceType.EVENTS_AND_TASKS;
			selectorType = isGroupFilter ? SelectorType.CATEGORY_FILTER : SelectorType.CATEGORY;
		}
    }
    else if(obj instanceof org.opencrx.kernel.activity1.jmi1.ActivityMilestone) {
		org.opencrx.kernel.activity1.jmi1.ActivityMilestone activityMilestone =
			(org.opencrx.kernel.activity1.jmi1.ActivityMilestone)obj;
		if ((activityMilestone.getName() != null) && !activityMilestone.getName().isEmpty()) {
	        if(!isGroupFilter) {
	          anchorObjectXriFromInitialObject = activityMilestone.refMofId();
	        }
		}
		if(type == null) {
			type = ResourceType.EVENTS_AND_TASKS;
			selectorType = isGroupFilter ? SelectorType.MILESTONE_FILTER : SelectorType.MILESTONE;
		}
    }
    else if(obj instanceof org.opencrx.kernel.home1.jmi1.UserHome) {
    	if(type == null) {
	    	type = ResourceType.EVENTS_AND_TASKS;
	    	selectorType = SelectorType.USERHOME;
    	}
        if(isFirstCall) {
            anchorObjectXriFromInitialObject = ((org.opencrx.kernel.home1.jmi1.UserHome)obj).refMofId();
        }
    }
    else if(obj instanceof org.opencrx.kernel.home1.jmi1.SyncProfile) {
    	if(type == null) {
    		type = ResourceType.PROFILE;
    	}
    	org.opencrx.kernel.home1.jmi1.SyncProfile syncProfile = (org.opencrx.kernel.home1.jmi1.SyncProfile)obj;
		if((syncProfile.getName() != null) && !syncProfile.getName().isEmpty()) {
	        if (!isGroupFilter) {
	          anchorObjectXriFromInitialObject = syncProfile.refMofId();
	        }
		}
    }
    else if(obj instanceof org.opencrx.kernel.activity1.jmi1.Resource) {
    	if(type == null) {
	    	type = ResourceType.EVENTS_AND_TASKS;
	    	selectorType = SelectorType.RESOURCE;
    	}
    	org.opencrx.kernel.activity1.jmi1.Resource resource = (org.opencrx.kernel.activity1.jmi1.Resource)obj;
		if((resource.getName() != null) && !resource.getName().isEmpty()) {
	        if (!isGroupFilter) {
	          anchorObjectXriFromInitialObject = resource.refMofId();
	        }
		}
    }
    else if(obj instanceof org.opencrx.kernel.account1.jmi1.AccountFilterGlobal) {
        if(type == ResourceType.CONTACT) {
        	selectorType = SelectorType.VCARD;
        } else if(type == null) {
        	type = ResourceType.EVENTS_AND_TASKS;
        	selectorType = SelectorType.BDAY;
        }
        org.opencrx.kernel.account1.jmi1.AccountFilterGlobal accountFilterGlobal =
          (org.opencrx.kernel.account1.jmi1.AccountFilterGlobal)obj;
        if ((accountFilterGlobal.getName() != null) && (accountFilterGlobal.getName().length() > 0)) {
          anchorObjectFilteredXriFromInitialObject = accountFilterGlobal.refMofId();
        }
    }
    if(anchorObjectFilteredXriFromInitialObject != null) {
        anchorObjectXriFromInitialObject = anchorObjectFilteredXriFromInitialObject;
    }
    String anchorObjectLabel = "Anchor object";

    Map<String,String> anchorObjects = new TreeMap<String,String>();

    if(selectorType == SelectorType.TRACKER) {
        anchorObjectLabel = app.getLabel(ACTIVITYTRACKER_CLASS);
        // get ActivityTrackers (not disabled)
        org.opencrx.kernel.activity1.cci2.ActivityTrackerQuery trackerQuery = 
        	(org.opencrx.kernel.activity1.cci2.ActivityTrackerQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityTracker.class);
        trackerQuery.forAllDisabled().isFalse();
        int index = 0;
        for(org.opencrx.kernel.activity1.jmi1.ActivityGroup ag: activitySegment.getActivityTracker(trackerQuery)) {
            String display = (ag.getName() != null ? ag.getName() : UNKNOWN);
            String sortKey = display.toUpperCase() + formatter.format(index++);
            anchorObjects.put(
                HTML_COMMENT_BEGIN + sortKey + HTML_COMMENT_END + display,
                ag.refMofId()
            );
        }
    } else if(selectorType == SelectorType.TRACKER_FILTER) {
        anchorObjectLabel = app.getLabel(ACTIVITYTRACKER_CLASS);
        // get ActivityFilters of ActivityTrackers (not disabled)
        org.opencrx.kernel.activity1.cci2.ActivityTrackerQuery trackerQuery = 
        	(org.opencrx.kernel.activity1.cci2.ActivityTrackerQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityTracker.class);
        trackerQuery.forAllDisabled().isFalse();
        int index = 0;
        for(Iterator<org.opencrx.kernel.activity1.jmi1.ActivityTracker> i = activitySegment.getActivityTracker(trackerQuery).iterator(); i.hasNext() && index < MAX_ENTRY_SELECT; ) {
            org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = i.next();
            for(Iterator<org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup> j = ag.<org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup>getActivityFilter().iterator(); j.hasNext() && index < MAX_ENTRY_SELECT; ) {
                org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup afg = j.next();
                String display = (ag.getName() != null ? ag.getName() : UNKNOWN) + " &lt;" + (afg.getName() != null ? afg.getName() : UNKNOWN) + "&gt;";
                String sortKey = display.toUpperCase() + formatter.format(index++);
                anchorObjects.put(
                    HTML_COMMENT_BEGIN + sortKey + HTML_COMMENT_END + display,
                    afg.refMofId()
                );
            }
        }
    } else if(selectorType == SelectorType.CATEGORY) {
        anchorObjectLabel = app.getLabel(ACTIVITYCATEGORY_CLASS);
        // get ActivityCategories (not disabled)
        org.opencrx.kernel.activity1.cci2.ActivityCategoryQuery categoryQuery = 
        	(org.opencrx.kernel.activity1.cci2.ActivityCategoryQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityCategory.class);
        categoryQuery.forAllDisabled().isFalse();
        int index = 0;
        for(Iterator<org.opencrx.kernel.activity1.jmi1.ActivityCategory> i = activitySegment.getActivityCategory(categoryQuery).iterator(); i.hasNext() && index < MAX_ENTRY_SELECT; ) {
            org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = i.next();
            String display = (ag.getName() != null ? ag.getName() : UNKNOWN);
            String sortKey = display.toUpperCase() + formatter.format(index++);
            anchorObjects.put(
                HTML_COMMENT_BEGIN + sortKey + HTML_COMMENT_END + display,
                ag.refMofId()
            );
        }
    } else if(selectorType == SelectorType.CATEGORY_FILTER) {
        anchorObjectLabel = app.getLabel(ACTIVITYCATEGORY_CLASS);
        // get ActivityFilters of ActivityCategories (not disabled)
        org.opencrx.kernel.activity1.cci2.ActivityCategoryQuery categoryQuery = 
        	(org.opencrx.kernel.activity1.cci2.ActivityCategoryQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityCategory.class);
        categoryQuery.forAllDisabled().isFalse();
        int index = 0;
        for(Iterator<org.opencrx.kernel.activity1.jmi1.ActivityCategory> i = activitySegment.getActivityCategory(categoryQuery).iterator(); i.hasNext() && index < MAX_ENTRY_SELECT; ) {
            org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = i.next();
            for(Iterator<org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup> j = ag.<org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup>getActivityFilter().iterator(); j.hasNext() && index < MAX_ENTRY_SELECT; ) {
                org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup afg = j.next();
                String display = (ag.getName() != null ? ag.getName() : UNKNOWN) + " &lt;" + (afg.getName() != null ? afg.getName() : UNKNOWN) + "&gt;";
                String sortKey = display.toUpperCase() + formatter.format(index++);
                anchorObjects.put(
                    HTML_COMMENT_BEGIN + sortKey + HTML_COMMENT_END + display,
                    afg.refMofId()
                );
            }
        }
    } else if(selectorType == SelectorType.MILESTONE) {
        anchorObjectLabel = app.getLabel(ACTIVITYMILESTONE_CLASS);
        // get ActivityMilestones (not disabled)
        org.opencrx.kernel.activity1.cci2.ActivityMilestoneQuery milestoneQuery = 
        	(org.opencrx.kernel.activity1.cci2.ActivityMilestoneQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityMilestone.class);
        milestoneQuery.forAllDisabled().isFalse();
        int index = 0;
        for(Iterator<org.opencrx.kernel.activity1.jmi1.ActivityMilestone> i = activitySegment.getActivityMilestone(milestoneQuery).iterator(); i.hasNext() && index < MAX_ENTRY_SELECT; ) {
            org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = i.next();
            String display = (ag.getName() != null ? ag.getName() : UNKNOWN);
            String sortKey = display.toUpperCase() + formatter.format(index++);
            anchorObjects.put(
                HTML_COMMENT_BEGIN + sortKey + HTML_COMMENT_END + display,
                ag.refMofId()
            );
        }
    } else if(selectorType == SelectorType.MILESTONE_FILTER) {
        anchorObjectLabel = app.getLabel(ACTIVITYMILESTONE_CLASS);
        // get ActivityFilters of ActivityMilestones (not disabled)
        org.opencrx.kernel.activity1.cci2.ActivityMilestoneQuery milestoneQuery = 
        	(org.opencrx.kernel.activity1.cci2.ActivityMilestoneQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityMilestone.class);
        milestoneQuery.forAllDisabled().isFalse();
        int index = 0;
        for(Iterator<org.opencrx.kernel.activity1.jmi1.ActivityMilestone> i = activitySegment.getActivityMilestone(milestoneQuery).iterator(); i.hasNext() && index < MAX_ENTRY_SELECT; ) {
            org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = i.next();
            for(Iterator<org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup> j = ag.<org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup>getActivityFilter().iterator(); j.hasNext() && index < MAX_ENTRY_SELECT; ) {
                org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup afg = j.next();
                String display = (ag.getName() != null ? ag.getName() : UNKNOWN) + " &lt;" + (afg.getName() != null ? afg.getName() : UNKNOWN) + "&gt;";
                String sortKey = display.toUpperCase() + formatter.format(index++);
                anchorObjects.put(
                    HTML_COMMENT_BEGIN + sortKey + HTML_COMMENT_END + display,
                    afg.refMofId()
                );
            }
        }
    } else if(selectorType == SelectorType.GLOBAL_FILTER) {
        anchorObjectLabel = app.getLabel(ACTIVITYFILTERGLOBAL_CLASS);
        // get ActivityTrackers (not disabled)
        org.opencrx.kernel.activity1.cci2.ActivityFilterGlobalQuery activityQuery = 
        	(org.opencrx.kernel.activity1.cci2.ActivityFilterGlobalQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityFilterGlobal.class);
        activityQuery.forAllDisabled().isFalse();
        int index = 0;
        for(Iterator<org.opencrx.kernel.activity1.jmi1.ActivityFilterGlobal> i = activitySegment.getActivityFilter(activityQuery).iterator(); i.hasNext() && index < MAX_ENTRY_SELECT; ) {
            org.opencrx.kernel.activity1.jmi1.ActivityFilterGlobal af = i.next();
            String display = (af.getName() != null ? af.getName() : UNKNOWN);
            String sortKey = display.toUpperCase() + formatter.format(index++);
            anchorObjects.put(
                HTML_COMMENT_BEGIN + sortKey + HTML_COMMENT_END + display,
                af.refMofId()
            );
        }
    } else if(selectorType == SelectorType.USERHOME) {
        anchorObjectLabel = app.getLabel(USERHOME_CLASS);
        // get UserHomes
        int index = 0;
        for(Iterator<org.opencrx.kernel.home1.jmi1.UserHome> i = homeSegment.<org.opencrx.kernel.home1.jmi1.UserHome>getUserHome().iterator(); i.hasNext() && index < MAX_ENTRY_SELECT; ) {
            org.opencrx.kernel.home1.jmi1.UserHome userHome = i.next();
            org.opencrx.kernel.account1.jmi1.Contact contact = null;
            try {
                contact = userHome.getContact();
            } catch (Exception e) {}
            String principal = userHome.refGetPath().getBase();
            String display = (contact != null && contact.getFullName() != null ? contact.getFullName() : UNKNOWN) + " [" + principal + "]";
            String sortKey = display.toUpperCase() + formatter.format(index++);
            anchorObjects.put(
                HTML_COMMENT_BEGIN + sortKey + HTML_COMMENT_END + display,
                userHome.refMofId()
            );
        }
    } else if(selectorType == SelectorType.CALENDARPROFILE) {
        anchorObjectLabel = app.getLabel(CALENDARPROFILE_CLASS);
        org.opencrx.kernel.account1.jmi1.Contact contact = null;
        try {
            contact = currentUserHome.getContact();
        } catch (Exception e) {}
        String principal = currentUserHome.refGetPath().getBase();
        int index = 0;
        for(Iterator<org.opencrx.kernel.home1.jmi1.SyncProfile> i = currentUserHome.<org.opencrx.kernel.home1.jmi1.SyncProfile>getSyncProfile().iterator(); i.hasNext() && index < MAX_ENTRY_SELECT; ) {
            org.opencrx.kernel.home1.jmi1.SyncProfile syncProfile = i.next();
            if(syncProfile instanceof org.opencrx.kernel.home1.jmi1.CalendarProfile) {
	            String display = (syncProfile.getName() != null ? syncProfile.getName() : "?");
	            String sortKey = display.toUpperCase() + formatter.format(index++);
	            anchorObjects.put(
	                HTML_COMMENT_BEGIN + sortKey + HTML_COMMENT_END + display,
	                syncProfile.refMofId()
	            );
        	}
        }
    } else if(selectorType == SelectorType.AIRSYNCPROFILE) {
        int index = 0;
        for(Iterator<org.opencrx.kernel.home1.jmi1.SyncProfile> i = currentUserHome.<org.opencrx.kernel.home1.jmi1.SyncProfile>getSyncProfile().iterator(); i.hasNext() && index < MAX_ENTRY_SELECT; ) {
            org.opencrx.kernel.home1.jmi1.SyncProfile syncProfile = i.next();
            if(syncProfile instanceof org.opencrx.kernel.home1.jmi1.AirSyncProfile) {
                String display = (syncProfile.getName() != null ? syncProfile.getName() : "?");
                String sortKey = display.toUpperCase() + formatter.format(index++);
                anchorObjects.put(
                    HTML_COMMENT_BEGIN + sortKey + HTML_COMMENT_END + display,
                    syncProfile.refMofId()
                );
            }
        }
    } else if(selectorType == SelectorType.CARDPROFILE) {
        anchorObjectLabel = app.getLabel(CARDPROFILE_CLASS);
        org.opencrx.kernel.account1.jmi1.Contact contact = null;
        try {
            contact = currentUserHome.getContact();
        } catch (Exception e) {}
        String principal = currentUserHome.refGetPath().getBase();
        int index = 0;
        for(Iterator<org.opencrx.kernel.home1.jmi1.SyncProfile> i = currentUserHome.<org.opencrx.kernel.home1.jmi1.SyncProfile>getSyncProfile().iterator(); i.hasNext() && index < MAX_ENTRY_SELECT; ) {
            org.opencrx.kernel.home1.jmi1.SyncProfile syncProfile = i.next();
            if (syncProfile instanceof org.opencrx.kernel.home1.jmi1.CardProfile) {
                String display = (syncProfile.getName() != null ? syncProfile.getName() : "?");
                String sortKey = display.toUpperCase() + formatter.format(index++);
                anchorObjects.put(
                    HTML_COMMENT_BEGIN + sortKey + HTML_COMMENT_END + display,
                    syncProfile.refMofId()
                );
            }
        }
    } else if(selectorType == SelectorType.DOCUMENTPROFILE) {
        anchorObjectLabel = app.getLabel(DOCUMENTPROFILE_CLASS);
        org.opencrx.kernel.account1.jmi1.Contact contact = null;
        try {
            contact = currentUserHome.getContact();
        } catch (Exception e) {}
        String principal = currentUserHome.refGetPath().getBase();
        int index = 0;
        for(Iterator<org.opencrx.kernel.home1.jmi1.SyncProfile> i = currentUserHome.<org.opencrx.kernel.home1.jmi1.SyncProfile>getSyncProfile().iterator(); i.hasNext() && index < MAX_ENTRY_SELECT; ) {
            org.opencrx.kernel.home1.jmi1.SyncProfile syncProfile = i.next();
            if (syncProfile instanceof org.opencrx.kernel.home1.jmi1.DocumentProfile) {
                String display = (syncProfile.getName() != null ? syncProfile.getName() : "?");
                String sortKey = display.toUpperCase() + formatter.format(index++);
                anchorObjects.put(
                    HTML_COMMENT_BEGIN + sortKey + HTML_COMMENT_END + display,
                    syncProfile.refMofId()
                );
            }
        }
    } else if(selectorType == SelectorType.RESOURCE) {
        anchorObjectLabel = app.getLabel(RESOURCE_CLASS);
        // get Resources (not disabled)
        org.opencrx.kernel.activity1.cci2.ResourceQuery resourceQuery = 
        	(org.opencrx.kernel.activity1.cci2.ResourceQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.Resource.class);
        resourceQuery.forAllDisabled().isFalse();
        int index = 0;
        for(Iterator<org.opencrx.kernel.activity1.jmi1.Resource> i = activitySegment.getResource(resourceQuery).iterator(); i.hasNext() && index < MAX_ENTRY_SELECT; ) {
            org.opencrx.kernel.activity1.jmi1.Resource resource = i.next();
            org.opencrx.kernel.account1.jmi1.Contact contact = resource.getContact();
            String display = (resource.getName() != null ? resource.getName() : UNKNOWN) + " [" + (contact != null && contact.getFullName() != null ? contact.getFullName() : UNKNOWN) + "]";
            String sortKey = display.toUpperCase() + formatter.format(index++);
            anchorObjects.put(
                HTML_COMMENT_BEGIN + sortKey + HTML_COMMENT_END + display,
                resource.refMofId()
            );
        }
    } else if((selectorType == SelectorType.BDAY) || (selectorType == SelectorType.VCARD)) {
        anchorObjectLabel = app.getLabel(ACCOUNTFILTERGLOBAL_CLASS);
        // get AccountFilterGlobals (not disabled)
        org.opencrx.kernel.account1.cci2.AccountFilterGlobalQuery accountQuery = 
        	(org.opencrx.kernel.account1.cci2.AccountFilterGlobalQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.AccountFilterGlobal.class);
        accountQuery.forAllDisabled().isFalse();
        int index = 0;
        for(Iterator<org.opencrx.kernel.account1.jmi1.AccountFilterGlobal> i = accountSegment.getAccountFilter(accountQuery).iterator(); i.hasNext() && index < MAX_ENTRY_SELECT; ) {
            org.opencrx.kernel.account1.jmi1.AccountFilterGlobal af = i.next();
            String display = (af.getName() != null ? af.getName() : UNKNOWN);
            String sortKey = display.toUpperCase() + formatter.format(index++);
            anchorObjects.put(
                HTML_COMMENT_BEGIN + sortKey + HTML_COMMENT_END + display,
                af.refMofId()
            );
        }
    }
%>
    <body onload="initPage();">
    <div id="container">
      <div id="wrap">
        <div id="header" style="height:90px;">
        <div id="logoTable">
          <table id="headerlayout">
            <tr id="headRow">
              <td id="head" colspan="2">
                <table id="info">
                  <tr>
                    <td id="headerCellLeft"><img id="logoLeft" src="../../images/logoLeft.gif" alt="openCRX" title="" /></td>
                    <td id="headerCellSpacerLeft"></td>
                    <td id="headerCellMiddle">&nbsp;</td>
                    <td id="headerCellRight"><img id="logoRight" src="../../images/logoRight.gif" alt="" title="" /></td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </div>
      </div>

      <div id="content-wrap">
        <div id="content" style="padding:100px 0.5em 0px 0.5em;">
          <form name="ConnectionHelper" accept-charset="UTF-8" method="POST" action="<%= FORM_ACTION %>">
            <input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= objectXri %>" />
            <input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= requestId %>" />
            <input type="checkbox" style="display:none;" id="isFirstCall" name="isFirstCall" checked />
            <input type="checkbox" style="display:none;" id="mustReload" name="mustReload" />

            <div class="fieldGroupName">Resource</div>
			<div>
                <table class="fieldGroup">
                  <tr>
                    <td class="label"><span class="nw"><%= userView.getFieldLabel(RESOURCEASSIGNMENT_CLASS, "resourceRole", app.getCurrentLocaleAsIndex()) %>:</span></td>
                    <td>
                        <select class="valueL" id="type" name="type" class="valueL" tabindex="<%= tabIndex + 10 %>" onchange="javascript:$('mustReload').checked = true;$('reload.button').click();">
                            <option <%= type == ResourceType.PROFILE ? "selected" : "" %> value="<%= ResourceType.PROFILE.toString()  %>"><%= ResourceType.PROFILE.getLabel() %></option>
                            <option <%= type == ResourceType.EVENTS_AND_TASKS ? "selected" : "" %> value="<%= ResourceType.EVENTS_AND_TASKS.toString() %>"><%= ResourceType.EVENTS_AND_TASKS.getLabel() %></option>
                            <option <%= type == ResourceType.CONTACT ? "selected" : "" %> value="<%= ResourceType.CONTACT.toString() %>"><%= ResourceType.CONTACT.getLabel() %></option>
                        </select>
                    </td>
                    <td class="addon"></td>
                  </tr>

                  <tr>
<%
                    if(type == ResourceType.EVENTS_AND_TASKS) {
%>
                        <td class="label"><span class="nw"><%= userView.getFieldLabel(ABSTRACTPRICELEVEL_CLASS, "basedOn", app.getCurrentLocaleAsIndex()) %>:</span></td>
                        <td>
                            <select class="valueL" id="selectorType" name="selectorType" class="valueL" tabindex="<%= tabIndex + 10 %>" onchange="javascript:$('reload.button').click();">
                                <option <%= selectorType == SelectorType.TRACKER ?          "selected" : "" %> value="<%= SelectorType.TRACKER.toString() %>"><%= app.getLabel(ACTIVITYTRACKER_CLASS)      %></option>
                                <option <%= selectorType == SelectorType.TRACKER_FILTER ?   "selected" : "" %> value="<%= SelectorType.TRACKER_FILTER.toString() %>"><%= app.getLabel(ACTIVITYTRACKER_CLASS)      %> &lt;<%= app.getLabel(ACTIVITYFILTERGROUP_CLASS) %>&gt;</option>
                                <option <%= selectorType == SelectorType.CATEGORY?          "selected" : "" %> value="<%= SelectorType.CATEGORY %>"><%= app.getLabel(ACTIVITYCATEGORY_CLASS)     %></option>
                                <option <%= selectorType == SelectorType.CATEGORY_FILTER ?  "selected" : "" %> value="<%= SelectorType.CATEGORY_FILTER.toString() %>"><%= app.getLabel(ACTIVITYCATEGORY_CLASS)     %> &lt;<%= app.getLabel(ACTIVITYFILTERGROUP_CLASS) %>&gt;</option>
                                <option <%= selectorType == SelectorType.MILESTONE ?        "selected" : "" %> value="<%= SelectorType.MILESTONE.toString() %>"><%= app.getLabel(ACTIVITYMILESTONE_CLASS)    %></option>
                                <option <%= selectorType == SelectorType.MILESTONE_FILTER ? "selected" : "" %> value="<%= SelectorType.MILESTONE_FILTER.toString() %>"><%= app.getLabel(ACTIVITYMILESTONE_CLASS)    %> &lt;<%= app.getLabel(ACTIVITYFILTERGROUP_CLASS) %>&gt;</option>
                                <option <%= selectorType == SelectorType.GLOBAL_FILTER ?    "selected" : "" %> value="<%= SelectorType.GLOBAL_FILTER.toString() %>"><%= app.getLabel(ACTIVITYFILTERGLOBAL_CLASS) %></option>
                                <option <%= selectorType == SelectorType.USERHOME ?         "selected" : "" %> value="<%= SelectorType.USERHOME.toString() %>"><%= app.getLabel(USERHOME_CLASS)             %></option>
                                <option <%= selectorType == SelectorType.RESOURCE ?         "selected" : "" %> value="<%= SelectorType.RESOURCE.toString() %>"><%= app.getLabel(RESOURCE_CLASS)             %></option>
                                <option <%= selectorType == SelectorType.BDAY ?             "selected" : "" %> value="<%= SelectorType.BDAY.toString() %>"><%= app.getLabel(ACCOUNTFILTERGLOBAL_CLASS)  %> / <%= userView.getFieldLabel(CONTACT_CLASS, "birthdate", app.getCurrentLocaleAsIndex()) %></option>
                            </select>
                        </td>
                        <td class="addon"></td>
<%
                    } 
                    else if(type == ResourceType.PROFILE) {
%>
                        <td class="label"><span class="nw">Selector type:</span></td>
                        <td>
                            <select class="valueL" id="selectorType" name="selectorType" class="valueL" tabindex="<%= tabIndex + 10 %>" onchange="javascript:$('reload.button').click();">
                                <option <%= selectorType == SelectorType.AIRSYNCPROFILE ? "selected" : "" %> value="<%= SelectorType.AIRSYNCPROFILE %>"><%= app.getLabel(AIRSYNCPROFILE_CLASS) %></option>
                                <option <%= selectorType == SelectorType.CALENDARPROFILE ? "selected" : "" %> value="<%= SelectorType.CALENDARPROFILE %>"><%= app.getLabel(CALENDARPROFILE_CLASS) %></option>
                                <option <%= selectorType == SelectorType.CARDPROFILE ? "selected" : "" %> value="<%= SelectorType.CARDPROFILE %>"><%= app.getLabel(CARDPROFILE_CLASS) %></option>
                                <option <%= selectorType == SelectorType.DOCUMENTPROFILE ? "selected" : "" %> value="<%= SelectorType.DOCUMENTPROFILE %>"><%= app.getLabel(DOCUMENTPROFILE_CLASS) %></option>
                            </select>
                        </td>
                        <td class="addon"></td>
<%
                    } 
                    else if(type == ResourceType.CONTACT) {
%>
                        <td class="label"><span class="nw">Selector type:</span></td>
                        <td>
                            <select class="valueL" id="selectorType" name="selectorType" class="valueL" tabindex="<%= tabIndex + 10 %>" onchange="javascript:$('reload.button').click();">
                                <option <%= selectorType.equals(SelectorType.VCARD) ? "selected" : "" %> value="<%= SelectorType.VCARD %>"><%= app.getLabel(ACCOUNTFILTERGLOBAL_CLASS)  %></option>
                            </select>
                        </td>
                        <td class="addon"></td>
<%
                    }
%>
                  </tr>

                  <tr>
                    <td class="label"><span class="nw"><%= anchorObjectLabel %>:</span></td>
                    <td>
<%
                        if (anchorObjects.isEmpty()) {
%>
                            <select class="valueL" id="anchorObjectXri" name="anchorObjectXri" class="valueL" tabindex="<%= tabIndex + 10 %>" onchange="javascript:$('reload.button').click();">
                                <option value="">--</option>
                            </select>
<%
                        } else {
%>
                            <select class="valueL" id="anchorObjectXri" name="anchorObjectXri" class="valueL" tabindex="<%= tabIndex + 10 %>" onchange="javascript:$('reload.button').click();">
<%
                                boolean hasSelection = false;
                                for (Iterator<String> i = anchorObjects.keySet().iterator(); i.hasNext();) {
                                    String key = i.next();
                                    String value = anchorObjects.get(key);
                                    boolean selected = ((anchorObjectXri != null) && (value != null) && (anchorObjectXri.equals(value))) ||
                                                       (isFirstCall && value.equals(anchorObjectXriFromInitialObject));
                                    if (selected) {
                                        hasSelection = true;
                                        selectedAnchorObjectXRI = value;
                                    }
%>
	                                  <option <%= selected ? "selected" : "" %> value="<%= value != null ? value : "" %>"><%= key %></option>
<%
	                              }
%>
	                          </select>
<%
                            if ((anchorObjectXri != null) && (anchorObjectXri.length() > 0) && (!hasSelection)) {
                                mustReload = true;
                            }
                        }
%>
                    </td>
                    <td class="addon"></td>
                  </tr>
                </table>
            </div>
<%
			RefObject_1_0 anchorObject = obj;
			if(anchorObjectXriFromInitialObject != null && !anchorObjectXriFromInitialObject.isEmpty()) {
				anchorObject = (RefObject_1_0)pm.getObjectById(new Path(anchorObjectXriFromInitialObject));
			}
			boolean showOptionIsDisabled = false;
			boolean showOptionMax = false;
			boolean showOptionUser = false;
			boolean showOptionSummaryPrefix = false;
			boolean showOptionCategories = false;
			boolean showOptionYear = false;
			boolean showOptionAlarm = false;
			boolean showOptionTimelineHeight = false;
			
			List<URL> urls = org.opencrx.application.utils.AdapterConnectionHelper.getCalDavCollectionSetURLs(urlBase, anchorObject);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">CalDAV Collection Sets</div>
				<br />
<%				
	            for(URL url: urls) {
%>
    	            <a href="<%= url.toString() %>" target="_blank"><%= url.toString() %></a>
    	            <br />
<%
        	    }
			}
            urls = org.opencrx.application.utils.AdapterConnectionHelper.getCalDavEventCollectionURLs(urlBase, anchorObject);
            if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">CalDAV Event Collections</div>
				<br />
<%				
				for(URL url: urls) {
%>
                	<a href="<%= url.toString() %>" target="_blank"><%= url.toString() %></a>
                	<br />
<%
				}
            }
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getCalDavTaskCollectionURLs(urlBase, anchorObject);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">CalDAV Task Collections</div>
				<br />
<%				
				for(URL url: urls) {
%>
                	<a href="<%= url.toString() %>" target="_blank"><%= url.toString() %></a>
                	<br />
<%
				}
			}
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getWebDavCollectionURLs(urlBase, anchorObject);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">WebDAV Collections</div>
				<br />
<%				
				for(URL url: urls) {
%>
                	<a href="<%= url.toString() %>" target="_blank"><%= url.toString() %></a>
                	<br />
<%
				}
			}
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getCardDavCollectionSetURLs(urlBase, anchorObject);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">CardDAV Collection Sets</div>
				<br />
<%				
				for(URL url: urls) {
%>
                	<a href="<%= url.toString() %>" target="_blank"><%= url.toString() %></a>
                	<br />
<%
				}
			}
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getCardDavCollectionURLs(urlBase, anchorObject);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">CardDAV Collections</div>
				<br />
<%				
				for(URL url: urls) {
%>
                	<a href="<%= url.toString() %>" target="_blank"><%= url.toString() %></a>
                	<br />
<%
				}
			}
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getICalURLs(
				urlBase, 
				anchorObject,
				optionMax,
				optionIsDisabled
			);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">ICAL Calendars</div>
				<br />
<%				
				for(URL url: urls) {
%>
                	<a href="<%= url.toString() %>" target="_blank"><%= url.toString() %></a>
                	<br />
<%
				}
				showOptionIsDisabled = true;
				showOptionMax = true;
			}
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getTimelineURLs(
				urlBase, 
				anchorObject,
				optionMax,
				optionIsDisabled,
				optionTimelineHeight
			);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">Timeline URLs</div>
				<br />
<%				
				for(URL url: urls) {
%>
                	<a href="<%= url.toString() %>" target="_blank"><%= url.toString() %></a>
                	<br />
<%
				}
				showOptionIsDisabled = true;
				showOptionMax = true;
				showOptionTimelineHeight = true;
			}
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getVCardURLs(urlBase, anchorObject);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">VCARD Collections</div>
				<br />
<%				
				for(URL url: urls) {
%>
                	<a href="<%= url.toString() %>" target="_blank"><%= url.toString() %></a>
                	<br />
<%
				}
			}
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getAirSyncURLs(urlBase, anchorObject);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">AirSync URLs</div>
				<br />
<%				
				for(URL url: urls) {
%>
                	<a href="<%= url.toString() %>" target="_blank"><%= url.toString() %></a>
                	<br />                	
<%
				}
			}
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getBirthdayCalendarURLs(
				urlBase, 
				anchorObject,
				optionMax,
				optionSummaryPrefix,
				optionCategories,
				optionYear,
				optionAlarm
			);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">Birthday Calendars</div>
				<br />
<%				
				for(URL url: urls) {
%>
                	<a href="<%= url.toString() %>" target="_blank"><%= url.toString() %></a>
                	<br />
<%
				}
				showOptionMax = true;
				showOptionSummaryPrefix = true;
				showOptionCategories = true;
				showOptionYear = true;
				showOptionAlarm = true;
			}
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getFreeBusyURLs(
				urlBase, 
				anchorObject,
				optionUser,
				optionMax,
				optionIsDisabled
			);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">FreeBusy Calendars</div>
				<br />
<%				
				for(URL url: urls) {
%>
                	<a href="<%= url.toString() %>" target="_blank"><%= url.toString() %></a>
                	<br />
<%
				}
				showOptionIsDisabled = true;
				showOptionMax = true;
				showOptionUser = true;
			}
%>
			<br />
			<div class="fieldGroupName">Options</div>
			<br />
			<table class="fieldGroup">
<%
				if(showOptionUser) {	
%>				
					<tr>
					    <td class="label"><span class="nw">User:</span></td>
					    <td><input type="text" class="valueL" name="optionUser" value="<%= optionUser %>" onchange="javascript:$('reload.button').click();"></input></td>
					    <td class="addon"></td>
					</tr>
<%
				}
				if(showOptionMax) {
%>					  
					<tr title="maximum number of accounts - default is '500'">
					    <td class="label"><span class="nw">Max:</span></td>
					    <td><input type="text" class="valueL" name="optionMax" value="<%= optionMax %>" onchange="javascript:$('reload.button').click();"></input></td>
					    <td class="addon"></td>
					</tr>
<%
				}
				if(showOptionIsDisabled) {
%>					  
					<tr title="activate filter 'disabled' to process disabled activities only">
						<td class="label"><span class="nw">Disabled:</span></td>
					    <td>
							<select class="valueL" name="optionIsDisabled" onchange="javascript:$('reload.button').click();">
								<option <%= "true".equals(optionIsDisabled) ? "selected" : "" %> value="true">true</option>						
								<option <%= "false".equals(optionIsDisabled) ? "selected" : "" %> value="false">false</option>						
							</select>						
					    </td>
					    <td class="addon"></td>
					</tr>
<%
				}
				if(showOptionSummaryPrefix) {
%>					  
					<tr title="Summary prefix - default is ''">
					    <td class="label"><span class="nw">Summary prefix:</span></td>
					    <td><input type="text" class="valueL" name="optionSummaryPrefix" value="<%= optionSummaryPrefix %>" onchange="javascript:$('reload.button').click();"></input></td>
					    <td class="addon"></td>
					</tr>
<%
				}
				if(showOptionCategories) {
%>					  
					<tr>
					    <td class="label"><span class="nw">Categories:</span></td>
					    <td><input type="text" class="valueL" name="optionCategories" value="<%= optionCategories %>" onchange="javascript:$('reload.button').click();"></input></td>
					    <td class="addon"></td>
					</tr>
<%
				}
				if(showOptionYear) {
%>					  
					<tr title="generate data for year-1, year, year+1 - default is current year">
					    <td class="label"><span class="nw">Year:</span></td>
					    <td><input type="text" class="valueL" name="optionYear" value="<%= optionYear %>" onchange="javascript:$('reload.button').click();"></input></td>
					    <td class="addon"></td>
					</tr>
<%
				}
				if(showOptionTimelineHeight) {
%>					  
					<tr>
					    <td class="label"><span class="nw">Timeline height (in pixels):</span></td>
					    <td><input type="text" class="valueL" name="optionTimelineHeight" value="<%= optionTimelineHeight %>" onchange="javascript:$('reload.button').click();"></input></td>
					    <td class="addon"></td>
					</tr>
<%
				}
				if(showOptionAlarm) {
%>					  
					<tr>
						<td class="label"><span class="nw">Alarm:</span></td>
					    <td>
							<select class="valueL" name="optionAlarm" onchange="javascript:$('reload.button').click();">
								<option <%= "true".equals(optionAlarm) ? "selected" : "" %> value="true">true</option>						
								<option <%= "false".equals(optionAlarm) ? "selected" : "" %> value="false">false</option>						
							</select>						
					    </td>
					    <td class="addon"></td>
					</tr>
<%
				}
%>					  
			</table>
			<br />
			<div class="fieldGroupName">Hints</div>
				<p>
				See the <a href="http://www.opencrx.org/documents.htm" target="_blank"><strong>openCRX Admin Guide</strong></a> for 
				the component configuration of the CalDAV, CardDAV, ICAL and VCARD adapters.
				<p>
				The properties <strong>maxActivities</strong> and <strong>maxAccounts</strong> allow
				to configure the maximum number of items returned by the adapters (default is 500).
			</div>			
			<br />
			<div class="fieldGroupName">&nbsp;</div>
            <input type="submit" id="reload.button" name="reload.button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getReloadText() %>" />
            <input type="submit" id="Cancel" name="Cancel" tabindex="30" value="<%= app.getTexts().getCancelTitle() %>"  onClick="javascript:window.close();" />
            <br />
          </form>
        </div> <!-- content -->
      </div> <!-- content-wrap -->
     </div> <!-- wrap -->
    </div> <!-- container -->
    <script language="javascript" type="text/javascript">
        function initPage() {
<%
            if (mustReload) {
%>
                $('reload.button').click();
<%
		}
%>
}
    </script>
    </body>
    </html>
<%
  }
  catch(Exception e) {
    new ServiceException(e).log();
      // Go back to previous view
    Action nextAction = new ObjectReference(
         (RefObject_1_0)pm.getObjectById(new Path(objectXri)),
         app
    ).getSelectObjectAction();
    response.sendRedirect(
      request.getContextPath() + "/" + nextAction.getEncodedHRef()
    );
  } finally {
	  if(pm != null) {
		  pm.close();
	  }
  }
%>
