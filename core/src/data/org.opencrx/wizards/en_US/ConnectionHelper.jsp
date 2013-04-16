<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Description: ConnectionHelper: Generate Adapter URLs
 * Owner:       CRIXP Corp., Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2012-2013 CRIXP Corp., Switzerland
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
%>
<%@page session="true" import="
java.util.*,
java.util.zip.*,
java.io.*,
java.text.*,
java.math.*,
java.net.*,
java.sql.*,
org.opencrx.kernel.portal.wizard.*,
org.opencrx.kernel.portal.wizard.ConnectionHelperController.SelectorType,
org.opencrx.kernel.portal.wizard.ConnectionHelperController.ResourceType,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.base.exception.*,
org.openmdx.kernel.id.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*,
org.openmdx.base.query.*,
org.openmdx.kernel.log.*
" %>
<%
	final String WIZARD_NAME = "ConnectionHelper.jsp";
	ConnectionHelperController wc = new ConnectionHelperController();
%>
	<t:wizardHandleCommand controller='<%= wc %>' defaultCommand='Reload' />
<%
	if(response.getStatus() != HttpServletResponse.SC_OK) {
		wc.close();		
		return;
	}
	ApplicationContext app = wc.getApp();
	javax.jdo.PersistenceManager pm = wc.getPm();
	RefObject_1_0 obj = wc.getObject();
	int tabIndex = 0;
	boolean mustReload = false;
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
         <form name="ConnectionHelper" accept-charset="UTF-8" method="POST" action="<%= WIZARD_NAME %>">
           <input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= wc.getObjectIdentity().toXRI() %>" />
           <input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= wc.getRequestId() %>" />
           <input type="checkbox" style="display:none;" id="isInitialized" name="isInitialized" checked />
           <div class="fieldGroupName">Resource</div>
			<div>
               <table class="fieldGroup">
                 <tr>
                   <td class="abel"><span class="nw"><%= wc.getFieldLabel(ConnectionHelperController.RESOURCEASSIGNMENT_CLASS, "resourceRole", app.getCurrentLocaleAsIndex()) %>:</span></td>
                   <td>
                       <select class="valueL" id="type" name="type" class="valueL" tabindex="<%= tabIndex + 10 %>" onchange="javascript:$('Reload.button').click();">
                           <option <%= wc.getResourceType() == ResourceType.PROFILE ? "selected" : "" %> value="<%= ResourceType.PROFILE.toString()  %>"><%= ResourceType.PROFILE.getLabel() %></option>
                           <option <%= wc.getResourceType() == ResourceType.EVENTS_AND_TASKS ? "selected" : "" %> value="<%= ResourceType.EVENTS_AND_TASKS.toString() %>"><%= ResourceType.EVENTS_AND_TASKS.getLabel() %></option>
                           <option <%= wc.getResourceType() == ResourceType.CONTACT ? "selected" : "" %> value="<%= ResourceType.CONTACT.toString() %>"><%= ResourceType.CONTACT.getLabel() %></option>
                       </select>
                   </td>
                   <td class="addon"></td>
                 </tr>
                 <tr>
<%
                    if(wc.getResourceType() == ResourceType.EVENTS_AND_TASKS) {
%>
                        <td class="label"><span class="nw"><%= wc.getFieldLabel(ConnectionHelperController.ABSTRACTPRICELEVEL_CLASS, "basedOn", app.getCurrentLocaleAsIndex()) %>:</span></td>
                        <td>
                            <select class="valueL" id="selectorType" name="selectorType" class="valueL" tabindex="<%= tabIndex + 10 %>" onchange="javascript:$('Reload.button').click();">
                                <option <%= wc.getSelectorType() == SelectorType.TRACKER ?          "selected" : "" %> value="<%= SelectorType.TRACKER.toString() %>"><%= app.getLabel(ConnectionHelperController.ACTIVITYTRACKER_CLASS)      %></option>
                                <option <%= wc.getSelectorType() == SelectorType.TRACKER_FILTER ?   "selected" : "" %> value="<%= SelectorType.TRACKER_FILTER.toString() %>"><%= app.getLabel(ConnectionHelperController.ACTIVITYTRACKER_CLASS)      %> &lt;<%= app.getLabel(ConnectionHelperController.ACTIVITYFILTERGROUP_CLASS) %>&gt;</option>
                                <option <%= wc.getSelectorType() == SelectorType.CATEGORY?          "selected" : "" %> value="<%= SelectorType.CATEGORY %>"><%= app.getLabel(ConnectionHelperController.ACTIVITYCATEGORY_CLASS)     %></option>
                                <option <%= wc.getSelectorType() == SelectorType.CATEGORY_FILTER ?  "selected" : "" %> value="<%= SelectorType.CATEGORY_FILTER.toString() %>"><%= app.getLabel(ConnectionHelperController.ACTIVITYCATEGORY_CLASS)     %> &lt;<%= app.getLabel(ConnectionHelperController.ACTIVITYFILTERGROUP_CLASS) %>&gt;</option>
                                <option <%= wc.getSelectorType() == SelectorType.MILESTONE ?        "selected" : "" %> value="<%= SelectorType.MILESTONE.toString() %>"><%= app.getLabel(ConnectionHelperController.ACTIVITYMILESTONE_CLASS)    %></option>
                                <option <%= wc.getSelectorType() == SelectorType.MILESTONE_FILTER ? "selected" : "" %> value="<%= SelectorType.MILESTONE_FILTER.toString() %>"><%= app.getLabel(ConnectionHelperController.ACTIVITYMILESTONE_CLASS)    %> &lt;<%= app.getLabel(ConnectionHelperController.ACTIVITYFILTERGROUP_CLASS) %>&gt;</option>
                                <option <%= wc.getSelectorType() == SelectorType.GLOBAL_FILTER ?    "selected" : "" %> value="<%= SelectorType.GLOBAL_FILTER.toString() %>"><%= app.getLabel(ConnectionHelperController.ACTIVITYFILTERGLOBAL_CLASS) %></option>
                                <option <%= wc.getSelectorType() == SelectorType.USERHOME ?         "selected" : "" %> value="<%= SelectorType.USERHOME.toString() %>"><%= app.getLabel(ConnectionHelperController.USERHOME_CLASS)             %></option>
                                <option <%= wc.getSelectorType() == SelectorType.RESOURCE ?         "selected" : "" %> value="<%= SelectorType.RESOURCE.toString() %>"><%= app.getLabel(ConnectionHelperController.RESOURCE_CLASS)             %></option>
                                <option <%= wc.getSelectorType() == SelectorType.BDAY ?             "selected" : "" %> value="<%= SelectorType.BDAY.toString() %>"><%= app.getLabel(ConnectionHelperController.ACCOUNTFILTERGLOBAL_CLASS)  %> / <%= wc.getFieldLabel(ConnectionHelperController.CONTACT_CLASS, "birthdate", app.getCurrentLocaleAsIndex()) %></option>
                            </select>
                        </td>
                        <td class="addon"></td>
<%
                    } 
                    else if(wc.getResourceType() == ResourceType.PROFILE) {
%>
                        <td class="label"><span class="nw">Selector type:</span></td>
                        <td>
                            <select class="valueL" id="selectorType" name="selectorType" class="valueL" tabindex="<%= tabIndex + 10 %>" onchange="javascript:$('Reload.button').click();">
                                <option <%= wc.getSelectorType() == SelectorType.AIRSYNCPROFILE ? "selected" : "" %> value="<%= SelectorType.AIRSYNCPROFILE %>"><%= app.getLabel(ConnectionHelperController.AIRSYNCPROFILE_CLASS) %></option>
                                <option <%= wc.getSelectorType() == SelectorType.CALENDARPROFILE ? "selected" : "" %> value="<%= SelectorType.CALENDARPROFILE %>"><%= app.getLabel(ConnectionHelperController.CALENDARPROFILE_CLASS) %></option>
                                <option <%= wc.getSelectorType() == SelectorType.CARDPROFILE ? "selected" : "" %> value="<%= SelectorType.CARDPROFILE %>"><%= app.getLabel(ConnectionHelperController.CARDPROFILE_CLASS) %></option>
                                <option <%= wc.getSelectorType() == SelectorType.DOCUMENTPROFILE ? "selected" : "" %> value="<%= SelectorType.DOCUMENTPROFILE %>"><%= app.getLabel(ConnectionHelperController.DOCUMENTPROFILE_CLASS) %></option>
                            </select>
                        </td>
                        <td class="addon"></td>
<%
                    } 
                    else if(wc.getResourceType() == ResourceType.CONTACT) {
%>
                        <td class="label"><span class="nw">Selector type:</span></td>
                        <td>
                            <select class="valueL" id="selectorType" name="selectorType" class="valueL" tabindex="<%= tabIndex + 10 %>" onchange="javascript:$('Reload.button').click();">
                                <option <%= wc.getSelectorType().equals(SelectorType.VCARD) ? "selected" : "" %> value="<%= SelectorType.VCARD %>"><%= app.getLabel(ConnectionHelperController.ACCOUNTFILTERGLOBAL_CLASS)  %></option>
                            </select>
                        </td>
                        <td class="addon"></td>
<%
                    }
%>
                  </tr>
                  <tr>
                    <td class="label"><span class="nw"><%= wc.getAnchorObjectLabel() %>:</span></td>
                    <td>
<%
                        if (wc.getAnchorObjects().isEmpty()) {
%>
                            <select class="valueL" id="anchorObjectXri" name="anchorObjectXri" class="valueL" tabindex="<%= tabIndex + 10 %>" onchange="javascript:$('Reload.button').click();">
                                <option value="">--</option>
                            </select>
<%
                        } else {
%>
                            <select class="valueL" id="anchorObjectXri" name="anchorObjectXri" class="valueL" tabindex="<%= tabIndex + 10 %>" onchange="javascript:$('Reload.button').click();">
<%
                                boolean hasSelection = false;
                                for (Iterator<String> i = wc.getAnchorObjects().keySet().iterator(); i.hasNext();) {
                                    String key = i.next();
                                    String value = wc.getAnchorObjects().get(key);
                                    boolean selected = 
                                    	((wc.getAnchorObjectXri() != null) && (value != null) && (wc.getAnchorObjectXri().equals(value))) ||                                    	
                                        (!Boolean.TRUE.equals(wc.getIsInitialized()) && value.equals(wc.getAnchorObjectXriFromInitialObject()));  
                                    if (selected) {
                                        hasSelection = true;
                                    }
%>
	                                  <option <%= selected ? "selected" : "" %> value="<%= value != null ? value : "" %>"><%= key %></option>
<%
	                              }
%>
	                          </select>
<%
                            if ((wc.getAnchorObjectXri() != null) && !wc.getAnchorObjectXri().isEmpty() && (!hasSelection)) {
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
			if(wc.getAnchorObjectXriFromInitialObject() != null && !wc.getAnchorObjectXriFromInitialObject().isEmpty()) {
				anchorObject = (RefObject_1_0)pm.getObjectById(new Path(wc.getAnchorObjectXriFromInitialObject()));
			}
			boolean showOptionIsDisabled = false;
			boolean showOptionMax = false;
			boolean showOptionUser = false;
			boolean showOptionSummaryPrefix = false;
			boolean showOptionCategories = false;
			boolean showOptionYear = false;
			boolean showOptionAlarm = false;
			boolean showOptionTimelineHeight = false;
			
			List<URL> urls = org.opencrx.application.utils.AdapterConnectionHelper.getCalDavCollectionSetURLs(wc.getUrlBase(), anchorObject);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">CalDAV Calendar Home (use with CalDAV clients only)</div>
				<br />
<%				
	            for(URL url: urls) {
%>
    	            <a href="<%= url.toString() %>" target="_blank"><%= url.toString() %></a>
    	            <br />
<%
        	    }
			}
            urls = org.opencrx.application.utils.AdapterConnectionHelper.getCalDavEventCollectionURLs(wc.getUrlBase(), anchorObject);
            if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">CalDAV Event Collections</div>
				<br />
<%				
				for(URL url: urls) {
					String stringifiedURL = url.toString();
					stringifiedURL = stringifiedURL.endsWith("/") ? stringifiedURL : stringifiedURL + "/";
%>
                	<a href="<%= stringifiedURL %>" target="_blank"><%= stringifiedURL %></a>
                	<br />
<%
				}
            }
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getCalDavTaskCollectionURLs(wc.getUrlBase(), anchorObject);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">CalDAV Task Collections</div>
				<br />
<%				
				for(URL url: urls) {
					String stringifiedURL = url.toString();
					stringifiedURL = stringifiedURL.endsWith("/") ? stringifiedURL : stringifiedURL + "/";
%>
                	<a href="<%= stringifiedURL %>" target="_blank"><%= stringifiedURL %></a>
                	<br />
<%
				}
			}
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getWebDavCollectionURLs(wc.getUrlBase(), anchorObject);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">WebDAV Collections</div>
				<br />
<%				
				for(URL url: urls) {
					String stringifiedURL = url.toString();
					stringifiedURL = stringifiedURL.endsWith("/") ? stringifiedURL : stringifiedURL + "/";
%>
                	<a href="<%= stringifiedURL %>" target="_blank"><%= stringifiedURL %></a>
                	<br />
<%
				}
			}
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getCardDavCollectionSetURLs(wc.getUrlBase(), anchorObject);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">CardDAV Addressbook Home (use with CardDAV clients only)</div>
				<br />
<%				
				for(URL url: urls) {
%>
                	<a href="<%= url.toString() %>" target="_blank"><%= url.toString() %></a>
                	<br />
<%
				}
			}
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getCardDavCollectionURLs(wc.getUrlBase(), anchorObject);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">CardDAV Collections</div>
				<br />
<%				
				for(URL url: urls) {
					String stringifiedURL = url.toString();
					stringifiedURL = stringifiedURL.endsWith("/") ? stringifiedURL : stringifiedURL + "/";
%>
                	<a href="<%= stringifiedURL %>" target="_blank"><%= stringifiedURL %></a>
                	<br />
<%
				}
			}
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getICalURLs(
				wc.getUrlBase(), 
				anchorObject,
				Integer.toString(wc.getOptionMax()),
				Boolean.toString(wc.getOptionIsDisabled())
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
				wc.getUrlBase(), 
				anchorObject,
				Integer.toString(wc.getOptionMax()),
				Boolean.toString(wc.getOptionIsDisabled()),
				Integer.toString(wc.getOptionTimelineHeight())
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
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getVCardURLs(wc.getUrlBase(), anchorObject);
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
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getAirSyncURLs(wc.getUrlBase(), anchorObject);
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
			urls = org.opencrx.application.utils.AdapterConnectionHelper.getOtherCalendarURLs(
				wc.getUrlBase(), 
				anchorObject,
				Integer.toString(wc.getOptionMax()),
				wc.getOptionSummaryPrefix(),
				wc.getOptionCategories(),
				Integer.toString(wc.getOptionYear()),
				Boolean.toString(wc.getOptionAlarm())
			);
			if(!urls.isEmpty()) {
%>
				<br />
				<div class="fieldGroupName">Other Calendars (Birthdays, Anniversaries, Dates of Death, ...)</div>
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
				wc.getUrlBase(), 
				anchorObject,
				wc.getOptionUser(),
				Integer.toString(wc.getOptionMax()),
				Boolean.toString(wc.getOptionIsDisabled())
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
					    <td><input type="text" class="valueL" name="optionUser" value="<%= wc.getOptionUser() %>" onchange="javascript:$('Reload.button').click();"></input></td>
					    <td class="addon"></td>
					</tr>
<%
				}
				if(showOptionMax) {
%>					  
					<tr title="maximum number of accounts - default is '500'">
					    <td class="label"><span class="nw">Max:</span></td>
					    <td><input type="text" class="valueL" name="optionMax" value="<%= Integer.toString(wc.getOptionMax()) %>" onchange="javascript:$('Reload.button').click();"></input></td>
					    <td class="addon"></td>
					</tr>
<%
				}
				if(showOptionIsDisabled) {
%>					  
					<tr title="activate filter 'disabled' to process disabled activities only">
						<td class="label"><span class="nw">Disabled:</span></td>
					    <td>
							<select class="valueL" name="optionIsDisabled" onchange="javascript:$('Reload.button').click();">
								<option <%= Boolean.TRUE.equals(wc.getOptionIsDisabled()) ? "selected" : "" %> value="true">true</option>						
								<option <%= !Boolean.TRUE.equals(wc.getOptionIsDisabled()) ? "selected" : "" %> value="false">false</option>						
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
					    <td><input type="text" class="valueL" name="optionSummaryPrefix" value="<%= wc.getOptionSummaryPrefix() %>" onchange="javascript:$('Reload.button').click();"></input></td>
					    <td class="addon"></td>
					</tr>
<%
				}
				if(showOptionCategories) {
%>					  
					<tr>
					    <td class="label"><span class="nw">Categories:</span></td>
					    <td><input type="text" class="valueL" name="optionCategories" value="<%= wc.getOptionCategories() %>" onchange="javascript:$('Reload.button').click();"></input></td>
					    <td class="addon"></td>
					</tr>
<%
				}
				if(showOptionYear) {
%>					  
					<tr title="generate data for year-1, year, year+1 - default is current year">
					    <td class="label"><span class="nw">Year:</span></td>
					    <td><input type="text" class="valueL" name="optionYear" value="<%= Integer.toString(wc.getOptionYear()) %>" onchange="javascript:$('Reload.button').click();"></input></td>
					    <td class="addon"></td>
					</tr>
<%
				}
				if(showOptionTimelineHeight) {
%>					  
					<tr>
					    <td class="label"><span class="nw">Timeline height (in pixels):</span></td>
					    <td><input type="text" class="valueL" name="optionTimelineHeight" value="<%= Integer.toString(wc.getOptionTimelineHeight()) %>" onchange="javascript:$('Reload.button').click();"></input></td>
					    <td class="addon"></td>
					</tr>
<%
				}
				if(showOptionAlarm) {
%>					  
					<tr>
						<td class="label"><span class="nw">Alarm:</span></td>
					    <td>
							<select class="valueL" name="optionAlarm" onchange="javascript:$('Reload.button').click();">
								<option <%= Boolean.TRUE.equals(wc.getOptionAlarm()) ? "selected" : "" %> value="true">true</option>						
								<option <%= !Boolean.TRUE.equals(wc.getOptionAlarm()) ? "selected" : "" %> value="false">false</option>						
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
			<div>
				<p>
				See the <a href="http://www.opencrx.org/documents.htm" target="_blank"><strong>openCRX Admin Guide</strong></a> for 
				the component configuration of the CalDAV, CardDAV, ICAL and VCARD adapters.
				<p>
				The properties <strong>maxActivities</strong> and <strong>maxAccounts</strong> allow
				to configure the maximum number of items returned by the adapters (default is 500).
			</div>
			<br />
			<div class="fieldGroupName">&nbsp;</div>
            <input type="submit" id="Reload.button" name="Reload" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getReloadText() %>" />
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
			$('Reload.button').click();
<%
		}
%>
	}
  </script>
</body>
</html>
<t:wizardClose controller="<%= wc %>" />
    