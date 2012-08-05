<?xml version="1.0" encoding="UTF-8"?>
<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     openCRX/Sample, http://www.openmdx.org/
 * Name:        $Id: MonthlyWorkReport.jsp,v 1.14 2009/06/12 04:38:45 cmu Exp $
 * Description: MonthlyReport
 * Revision:    $Revision: 1.14 $
 * Owner:       OMEX AG, Switzerland, http://www.omex.ch
 * Date:        $Date: 2009/06/12 04:38:45 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2004-2008, OMEX AG, Switzerland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * * Neither the name of the openMDX team nor the names of its
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
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
 * This product includes software developed by Mihai Bazon
 * (http://dynarch.com/mishoo/calendar.epl) published with an LGPL
 * license.
 */
%><%@ page session="true" import="
java.util.*,
java.io.*,
java.text.*,
java.math.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.base.exception.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.texts.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.reports.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*,
org.openmdx.base.query.*,
org.openmdx.application.log.*,
org.openmdx.kernel.id.*,
org.openmdx.uses.org.apache.commons.collections.*,
org.openmdx.application.log.*
" %>

<%!

	private static boolean includeActivity(
		org.opencrx.kernel.activity1.jmi1.Activity activity,
		org.opencrx.kernel.activity1.jmi1.ActivityGroup selectedActivityGroup,
		int selectedPriority
	) {
		// Only report activity if it is member of the selected activity tracker
		boolean isMemberOfSelectedActivityGroup = false;
		if(selectedActivityGroup == null) {
			isMemberOfSelectedActivityGroup = true;
		}
		else {
			for(Iterator j = activity.getAssignedGroup().iterator(); j.hasNext(); ) {
				org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment assignment = (org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment)j.next();
				if(assignment.getActivityGroup().refMofId().equals(selectedActivityGroup.refMofId())) {
					isMemberOfSelectedActivityGroup = true;
					break;
				}
			}
		}
		// Only report if activity if it matches selectedPriority
		boolean matchesSelectedPriority = false;
		if(selectedPriority == 0) {
			matchesSelectedPriority = true;
		}
		else {
			matchesSelectedPriority = selectedPriority == 1
				? activity.getPriority() <= 2
				: activity.getPriority() > 2;
		}
		return isMemberOfSelectedActivityGroup && matchesSelectedPriority;
	}

	private static String decimalMinutesToHhMm(
		double decimalMinutes
	) {
		NumberFormat hhFormatter = new DecimalFormat("#,##0");
		NumberFormat mmFormatter = new DecimalFormat("#,#00");
		int hours = (int)(decimalMinutes / 60.0);
		int minutes = (int)java.lang.Math.rint(decimalMinutes % 60.0);
		if (minutes == 60) {
				hours += 1;
				minutes = 0;
		}
		return hhFormatter.format(hours) + ":" + mmFormatter.format(minutes);
	}
%>

<%
  request.setCharacterEncoding("UTF-8");
	ApplicationContext app = (ApplicationContext)session.getValue(WebKeys.APPLICATION_KEY);
	ViewsCache viewsCache = (ViewsCache)session.getValue(WebKeys.VIEW_CACHE_KEY_SHOW);
	String requestId =  request.getParameter(Action.PARAMETER_REQUEST_ID);
	String requestIdParam = Action.PARAMETER_REQUEST_ID + "=" + requestId;
  String objectXri = request.getParameter("xri");
	if(app==null || objectXri==null || viewsCache.getViews().isEmpty()) {
    response.sendRedirect(
       request.getContextPath() + "/" + WebKeys.SERVLET_NAME
    );
    return;
  }
  javax.jdo.PersistenceManager pm = app.getPmData();
  Texts_1_0 texts = app.getTexts();
  Path objectPath = new Path(objectXri);
  RefObject_1_0 obj = (RefObject_1_0)pm.getObjectById(objectPath);
  String providerName = objectPath.get(2);
  String segmentName = objectPath.get(4);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html dir="<%= texts.getDir() %>">
<head>
  <title><%= app.getApplicationName() + " - Work Report" %></title>
  <meta name="label" content="Monthly Work Report">
  <meta name="toolTip" content="Monthly Work Report">
  <meta name="targetType" content="_self">
  <meta name="forClass" content="org:opencrx:kernel:activity1:Segment">
  <meta name="order" content="1">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link href="../../_style/n2default.css" rel="stylesheet" type="text/css">
  <link href="../../_style/colors.css" rel="stylesheet" type="text/css">
  <script language="javascript" type="text/javascript" src="../../javascript/guicontrol.js"></script>
  <link rel='shortcut icon' href='../../images/favicon.ico' />
</head>
<style type="text/css">
  .resultTableFull {
    table-layout:fixed;
  }
	.resultTableFull TD {
		font-size: 10px;
	}
	.gridTableHeaderFull TD {
		padding: 3px 5px 1px 5px; /* top right bottom left */
		line-height: normal;
		text-align: right;
		font-weight: bold;
	}
	.gridTableRowFull TD {
		padding: 2px 5px 2px 5px; /* top right bottom left */
		line-height: normal;
	}
  * TD STRONG, A {
		font-size: 10px;
	}
</style>
<body onload="initPage();">

<%
		int currentTableId = 0;

		try {

			boolean actionOk = request.getParameter("OK.Button") != null;
			boolean actionCancel = request.getParameter("Cancel.Button") != null;

			if(actionCancel) {
    		Action nextAction = new ObjectReference(obj, app).getSelectObjectAction();
    		response.sendRedirect(
    			request.getContextPath() + "/" + nextAction.getEncodedHRef()
      	);
			}
			else {
				// Selected year/month
				String yearmonth = request.getParameter("yearmonth");
				boolean isActivityReport = request.getParameter("DepotReport.Button") == null;
				Calendar calendar = new GregorianCalendar();
				if(yearmonth == null) {
					int month = calendar.get(Calendar.MONTH) + 1;
					yearmonth = (month < 10 ? "0" : "") + month + "-" + calendar.get(Calendar.YEAR);
					calendar.set(Calendar.DAY_OF_MONTH, 1);
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
				}
				else {
					Date startDate = new SimpleDateFormat("MM-yyyy").parse(yearmonth);
					calendar.setTime(startDate);
				}
//				System.out.println("calendar=" + calendar);
				Locale currentLocale = app.getCurrentLocale();
				SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("E'<br />'dd.MM", currentLocale);
				SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", currentLocale);
				DecimalFormat decimalFormat = (DecimalFormat)DecimalFormat.getInstance(currentLocale);
				decimalFormat.setMaximumFractionDigits(2);
				decimalFormat.setMinimumFractionDigits(2);
        NumberFormat weekNumberFormatter = new DecimalFormat("#,##0");
        NumberFormat formatter5 = new DecimalFormat("00000");

    		final int RECORDTYPE_WORK_MAX = 99; // <=99 --> WorkRecord, >= 100 --> ExpenseRecord

    		final String DEFAULT_TIMESTAMP = "0000-01-01T00:00:00.000Z";
        DateFormat crxDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        crxDateFormat.setTimeZone(TimeZone.getTimeZone(app.getCurrentTimeZone()));
        DateFormat activityDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        activityDateFormat.setTimeZone(TimeZone.getTimeZone(app.getCurrentTimeZone()));

				// Get packages and segments
				org.opencrx.kernel.activity1.jmi1.Activity1Package activityPkg = org.opencrx.kernel.utils.Utils.getActivityPackage(pm);
        org.opencrx.kernel.activity1.jmi1.Segment activitySegment =
          (org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(
            new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName)
           );

				// Selected resource
				String selectedResourceXri = request.getParameter("resourceXri");
				org.opencrx.kernel.activity1.jmi1.Resource selectedResource = null;
				if((selectedResourceXri != null) && (selectedResourceXri.length() > 0)) {
					selectedResource = (org.opencrx.kernel.activity1.jmi1.Resource)pm.getObjectById(new Path(selectedResourceXri));
				}
				// Selected activity tracker
				String selectedActivityGroupXri = request.getParameter("activityGroupXri");
				org.opencrx.kernel.activity1.jmi1.ActivityGroup selectedActivityGroup = null;
				if((selectedActivityGroupXri != null) && (selectedActivityGroupXri.length() > 0)) {
					selectedActivityGroup = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)pm.getObjectById(new Path(selectedActivityGroupXri));
				}
				// Selected priority
				String selectedPriorityAsString = request.getParameter("activityPriority");
				int selectedPriority = (selectedPriorityAsString == null) || (selectedPriorityAsString.length() == 0)
					? 0
					: Integer.valueOf(request.getParameter("activityPriority")).intValue();
%>

<div id="container">
	<div id="wrap">
		<div id="fixheader" style="height:90px;">
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
    	<div id="content" style="padding:0px 0.5em 0px 0.5em;">

<form name="MonthlyReport" accept-charset="UTF-8" method="post" action="MonthlyWorkReport.jsp">
<input type="hidden" name="xri" value="<%= objectXri %>" />
<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= requestId %>" />
<table cellspacing="8" class="tableLayout">
  <tr>
    <td class="cellObject">
      <br />
			<table>
			  <tr>
			    <td>
			    	<table>
						<tr>
              <td><input type="Submit" name="ActivityReport.Button" tabindex="1000" value="Activities Report" /></td>
							<td><input type="Submit" name="DepotReport.Button" tabindex="1010" value="Depots Report" /></td>
							<td><input type="Submit" name="Print.Button" tabindex="1020" value="Print" onClick="javascript:window.print();" /></td>
							<td align="right"><input type="Submit" name="Cancel.Button" tabindex="1030" value="Close" /></td>
						</tr>
						<tr>
							<td colspan="3"><h1 style="font-size:20px;">Work Report for <%= activitySegment.refGetPath().get(2) + ":" + activitySegment.refGetPath().get(4) %></h1></td>
						</tr>
						<tr>
							<td>Month/Year:</td>
							<td colspan="2">
								<select class="valueL" name="yearmonth" tabindex="1020">
<%
									for(int i = 1; i < 13; i++) {
										String ym = (i < 10 ? "0" : "") + i + "-" + (calendar.get(Calendar.YEAR) - 1);
%>
						       		<option <%= (yearmonth != null) && ym.equals(yearmonth) ? "selected" : "" %> value="<%= ym %>"><%= ym.replace('-', '/') %>
<%
									}
									for(int i = 1; i < 13; i++) {
										String ym = (i < 10 ? "0" : "") + i + "-" + calendar.get(Calendar.YEAR);
%>
						       		<option <%= (yearmonth != null) && ym.equals(yearmonth) ? "selected" : "" %> value="<%= ym %>"><%= ym.replace('-', '/') %>
<%
									}
									for(int i = 1; i < 13; i++) {
										String ym = (i < 10 ? "0" : "") + i + "-" + (calendar.get(Calendar.YEAR) + 1);
%>
						       		<option <%= (yearmonth != null) && ym.equals(yearmonth) ? "selected" : "" %> value="<%= ym %>"><%= ym.replace('-', '/') %>
<%
									}
%>
								</select>
							 </td>
						</tr>
						<tr>
							<td>Resource:</td>
							<td colspan="2">
								<select class="valueL" name="resourceXri" tabindex="1030">
						       		<option value="">All
<%
									org.opencrx.kernel.activity1.cci2.ResourceQuery resourceFilter = activityPkg.createResourceQuery();
									resourceFilter.orderByName().ascending();
									for(Iterator i = activitySegment.getResource(resourceFilter).iterator(); i.hasNext(); ) {
										org.opencrx.kernel.activity1.jmi1.Resource resource = (org.opencrx.kernel.activity1.jmi1.Resource)i.next();
%>
						       		<option <%= (selectedResource != null) && selectedResource.refMofId().equals(resource.refMofId()) ? "selected" : "" %> value="<%= resource.refMofId() %>"><%= resource.getName() %>
<%
									}
%>
								</select>
							 </td>
						</tr>
						<tr>
							<td>Activity group:</td>
							<td colspan="2">
								<select class="valueL" name="activityGroupXri" tabindex="1040">
						      <option value="">All
<%
									Map orderedActivityGroups = new TreeMap();
						      // get ActivityTrackers
									org.opencrx.kernel.activity1.cci2.ActivityTrackerQuery trackerFilter = activityPkg.createActivityTrackerQuery();
									trackerFilter.forAllDisabled().isFalse();
									int gCounter = 0;
									for(Iterator i = activitySegment.getActivityTracker(trackerFilter).iterator(); i.hasNext(); ) {
											org.opencrx.kernel.activity1.jmi1.ActivityGroup activityGroup = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)i.next();
											orderedActivityGroups.put(
													(activityGroup.getName() != null ? activityGroup.getName() : "_?") + "    " + gCounter++,
													activityGroup
												);
									}
									// get ActivityCategories
									org.opencrx.kernel.activity1.cci2.ActivityCategoryQuery categoryFilter = activityPkg.createActivityCategoryQuery();
									categoryFilter.forAllDisabled().isFalse();
									for(Iterator i = activitySegment.getActivityCategory(categoryFilter).iterator(); i.hasNext(); ) {
											org.opencrx.kernel.activity1.jmi1.ActivityGroup activityGroup = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)i.next();
											orderedActivityGroups.put(
													(activityGroup.getName() != null ? activityGroup.getName() : "_?") + "    " + gCounter++,
													activityGroup
												);
									}
									// get ActivityMilestones
									org.opencrx.kernel.activity1.cci2.ActivityMilestoneQuery milestoneFilter = activityPkg.createActivityMilestoneQuery();
									milestoneFilter.forAllDisabled().isFalse();
									for(Iterator i = activitySegment.getActivityMilestone(milestoneFilter).iterator(); i.hasNext(); ) {
											org.opencrx.kernel.activity1.jmi1.ActivityGroup activityGroup = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)i.next();
											orderedActivityGroups.put(
													(activityGroup.getName() != null ? activityGroup.getName() : "_?") + "    " + gCounter++,
													activityGroup
												);
									}

									for (
											Iterator i = orderedActivityGroups.keySet().iterator();
											i.hasNext();
										) {
												org.opencrx.kernel.activity1.jmi1.ActivityGroup activityGroup =
													(org.opencrx.kernel.activity1.jmi1.ActivityGroup)orderedActivityGroups.get(i.next());
%>
							       		<option <%= (selectedActivityGroup != null) && selectedActivityGroup.refMofId().equals(activityGroup.refMofId()) ? "selected" : "" %> value="<%= activityGroup.refMofId() %>"><%= activityGroup.getName() != null ? activityGroup.getName() : "_?" %>
<%
									}
%>
								</select>
							 </td>
						</tr>
						<tr>
							<td>Priority:</td>
							<td colspan="2">
								<select class="valueL" name="activityPriority" tabindex="1050">
						       		<option value="">All
									<option <%= selectedPriority == 1 ? "selected" : "" %> value="1">&lt;= normal</option>
									<option <%= selectedPriority == 2 ? "selected" : "" %> value="2">&gt; normal</option>
								</select>
							 </td>
						</tr>
					</table>
			    </td>
			</tr>
<%
			if((selectedResource != null) || (selectedActivityGroup != null)) {
				GregorianCalendar startAt = (GregorianCalendar)calendar.clone();
				GregorianCalendar endAt = (GregorianCalendar)calendar.clone();
				endAt.add(Calendar.MONTH, 1);
				org.opencrx.kernel.activity1.cci2.WorkAndExpenseRecordQuery filter = activityPkg.createWorkAndExpenseRecordQuery();
				filter.thereExistsStartedAt().between(
					startAt.getTime(),
					endAt.getTime()
				);
				filter.recordType().between(new Short((short)1), new Short((short)RECORDTYPE_WORK_MAX)); // work records only, i.e. no expense records
				//filter.recordType().elementOf(
				//	org.opencrx.kernel.backend.Activities.WORKRECORD_TYPE_WORK_STANDARD,
				//	org.opencrx.kernel.backend.Activities.WORKRECORD_TYPE_WORK_OVERTIME
				//);
				Map activities = new TreeMap();
				Map depots = new TreeMap();
				Map workRecords = new TreeMap(); // sorted by Map startedAt
				int counter = 0;
				List workReportEntries = selectedResource == null
					? selectedActivityGroup.getWorkReportEntry(filter)
					: selectedResource.getWorkReportEntry(filter);
				// Prepare involved activities
				for(Iterator i = workReportEntries.iterator(); i.hasNext(); ) {
					org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord workReportEntry = (org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord)i.next();
					org.opencrx.kernel.activity1.jmi1.Activity activity = workReportEntry.getActivity();
					activities.put(
						activity.getActivityNumber(),
						activity
					);
					// Get depot of debit booking of work compound booking
					org.opencrx.kernel.depot1.jmi1.CompoundBooking cb = workReportEntry.getWorkCb();
					if(cb != null) {
						for(Iterator j = cb.getBooking().iterator(); j.hasNext(); ) {
							org.opencrx.kernel.depot1.jmi1.Booking booking = (org.opencrx.kernel.depot1.jmi1.Booking)j.next();
							if(booking instanceof org.opencrx.kernel.depot1.jmi1.DebitBooking) {
								org.opencrx.kernel.depot1.jmi1.Depot depot = booking.getPosition().getDepot();
								depots.put(
									depot.getDepotNumber(),
									depot
								);
							}
						}
					}
				}
//				System.out.println("< prepare WorkReportEntries");

				int currentMonth = calendar.get(Calendar.MONTH);
				double[] totalEffort = new double[isActivityReport ? activities.size() : depots.size()];
				for(int i = 0; i < totalEffort.length; i++) {
					totalEffort[i] = 0.0;
				}
				while(calendar.get(Calendar.MONTH) == currentMonth) {
%>
					<tr>
						<td style="padding-top: 0px;">
							<table id="table-<%= currentTableId++ %>" class="resultTableFull">
							  <tr>
							    <td width="70">&nbsp;</td>
							    <td width="300">&nbsp;</td>
							    <td width="300" colspan="8">&nbsp;</td>
							  </tr>
								<tr class="gridTableHeaderFull">
									<td style="text-align: left;">#</td>
									<td style="text-align: left;"><%= isActivityReport ? "Activity" : "Depot"%></td>
<%
									GregorianCalendar cal = (GregorianCalendar)calendar.clone();
									for(int j = 0; j < 7; j++) {
									  if (cal.get(Calendar.DAY_OF_WEEK) > j+2) {
%>
                      <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
<%
									    continue;
									  }
%>
										<td><%= dayOfWeekFormat.format(cal.getTime()) %></td>
<%
										cal.add(Calendar.DAY_OF_MONTH, 1);
										if((cal.get(Calendar.MONTH) != currentMonth) || (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)) {
    									for(int cd = j+1; cd < 7; cd++) {
%>
    										<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
<%
										  }
%>
                      <td>&nbsp;</td>
<%
										  break;
										}
									}
%>
									<td >Total<br />Week</td>
									<td >Total<br />hh:mm</td>
								</tr>
<%
								double[] sumDays = new double[7];
								for(int i = 0; i < sumDays.length; i++) {
									sumDays[i] = 0.0;
								}
								if(isActivityReport) {
									int ii = 0;
									for(Iterator i = activities.values().iterator(); i.hasNext(); ii++) {
										org.opencrx.kernel.activity1.jmi1.Activity activity = (org.opencrx.kernel.activity1.jmi1.Activity)i.next();

										boolean includeActivity = includeActivity(
											activity,
											selectedActivityGroup,
											selectedPriority
										);

										if(includeActivity) {
//											System.out.println("> activity " + activity.getName());
											double sumWeek = 0;
%>
											<tr class="gridTableRowFull">
												<td style="text-align: left;"><%= activity.getActivityNumber() %></td>
												<td style="text-align: left;"><%= activity.getName() %></td>
<%
												cal = (GregorianCalendar)calendar.clone();
												for(int j = 0; j < 7; j++) {
      									  if (cal.get(Calendar.DAY_OF_WEEK) > j+2) {
%>
                            <td>&nbsp;</td>
<%
      									    continue;
      									  }
													double sumDay = 0.0;

													// Get work records of current day assigned to current activity
													org.opencrx.kernel.activity1.cci2.WorkAndExpenseRecordQuery currentDayFilter = activityPkg.createWorkAndExpenseRecordQuery();
													GregorianCalendar tomorrow = (GregorianCalendar)cal.clone();
													tomorrow.add(Calendar.DAY_OF_MONTH, 1);
													currentDayFilter.thereExistsStartedAt().between(
														cal.getTime(),
														tomorrow.getTime()
													);
													currentDayFilter.recordType().between(new Short((short)1), new Short((short)RECORDTYPE_WORK_MAX)); // work records only, i.e. no expense records
													//currentDayFilter.recordType().elementOf(
													//	org.opencrx.kernel.backend.Activities.WORKRECORD_TYPE_WORK_STANDARD,
													//	org.opencrx.kernel.backend.Activities.WORKRECORD_TYPE_WORK_OVERTIME
													//);
													Collection entries = selectedResource == null ?
														selectedActivityGroup.getWorkReportEntry(currentDayFilter) :
														selectedResource.getWorkReportEntry(currentDayFilter);
													for(Iterator k = entries.iterator(); k.hasNext(); ) {
														org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord workReportEntry = (org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord)k.next();
														GregorianCalendar calEntryStartedAt = new GregorianCalendar();
														calEntryStartedAt.setTime(workReportEntry.getStartedAt());
														if(
															activity.equals(workReportEntry.getActivity()) &&
															(calEntryStartedAt.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH))
														) {
															GregorianCalendar calEntryModifiedAt = new GregorianCalendar();
															calEntryModifiedAt.setTime(workReportEntry.getModifiedAt());
															sumDay += workReportEntry.getQuantity().doubleValue() * 60.0;
															// add this work record to map with key [startedAt(TimeStamp)][Counter(00000)] = yyyy-MM-ddTHH:mm:ss.SSSZ00000
															workRecords.put((workReportEntry.getStartedAt() != null ?
																crxDateFormat.format(workReportEntry.getStartedAt()) :
																DEFAULT_TIMESTAMP) + formatter5.format(counter),
															  workReportEntry
															);
															counter += 1;
														}
													}
%>
													<td align="right"><%= decimalFormat.format(1.0 * (sumDay) / 60.0) %></td>
<%
													sumWeek += sumDay;
													sumDays[j] += sumDay;
													totalEffort[ii] += sumDay;
													cal.add(Calendar.DAY_OF_MONTH, 1);
													if((cal.get(Calendar.MONTH) != currentMonth) || (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)) {
          									for(int cd = j; cd < 7; cd++) {
%>
          										<td>&nbsp;</td>
<%
      										  }
      										  break;
      									  }
												}
%>
												<td align="right"><%= decimalFormat.format(1.0 * sumWeek / 60.0) %></td>
												<td align="right"><strong><%= decimalMinutesToHhMm(sumWeek) %></strong></td>
											</tr>
<%
//											System.out.println("< activity");
										}
									}
								}
								// Depot Report
								else {
									int ii = 0;
//									System.out.println("> week " + calendar.get(Calendar.DAY_OF_MONTH));
									for(Iterator i = depots.values().iterator(); i.hasNext(); ii++) {
										org.opencrx.kernel.depot1.jmi1.Depot depot = (org.opencrx.kernel.depot1.jmi1.Depot)i.next();
										double sumWeek = 0.0;
%>
										<tr class="gridTableRowFull">
											<td style="text-align: left;"><%= depot.getDepotNumber() %></td>
											<td style="text-align: left;"><%= depot.getName() %></td>
<%
											cal = (GregorianCalendar)calendar.clone();
//											System.out.println("> depot " + depot.getDepotNumber());
											for(int j = 0; j < 7; j++) {
												if (cal.get(Calendar.DAY_OF_WEEK) > j+2) {
%>
													<td>&nbsp;</td>
<%
													continue;
												}
												double sumDay = 0.0;
												// Get work records of current day
												org.opencrx.kernel.activity1.cci2.WorkAndExpenseRecordQuery currentDayFilter = activityPkg.createWorkAndExpenseRecordQuery();
												GregorianCalendar tomorrow = (GregorianCalendar)cal.clone();
												tomorrow.add(Calendar.DAY_OF_MONTH, 1);
												currentDayFilter.thereExistsStartedAt().between(
													cal.getTime(),
													tomorrow.getTime()
												);
												filter.recordType().between(new Short((short)1), new Short((short)RECORDTYPE_WORK_MAX)); // work records only, i.e. no expense records
												//filter.recordType().elementOf(
												//	org.opencrx.kernel.backend.Activities.WORKRECORD_TYPE_WORK_STANDARD,
												//	org.opencrx.kernel.backend.Activities.WORKRECORD_TYPE_WORK_OVERTIME
												//);
												Collection entries = selectedResource == null ?
													selectedActivityGroup.getWorkReportEntry(currentDayFilter) :
													selectedResource.getWorkReportEntry(currentDayFilter);
												for(Iterator k = entries.iterator(); k.hasNext(); ) {
													org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord workReportEntry = (org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord)k.next();
													// Only report activity if it is member of the selected activity tracker
													boolean isMemberOfSelectedActivityGroup = false;
													if(selectedActivityGroup == null) {
														isMemberOfSelectedActivityGroup = true;
													}
													else {
														for(Iterator l = workReportEntry.getActivity().getAssignedGroup().iterator(); l.hasNext(); ) {
															org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment assignment = (org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment)l.next();
															if(assignment.getActivityGroup().refMofId().equals(selectedActivityGroup.refMofId())) {
																isMemberOfSelectedActivityGroup = true;
																break;
															}
														}
													}
													// Only report activity if it is assigned to selected resource
													boolean isAssignedToSelectedResource = false;
													if(selectedResource == null) {
														isAssignedToSelectedResource = true;
													}
													else {
														org.opencrx.kernel.activity1.jmi1.Resource assignment = workReportEntry.getResource();
														if(assignment.refMofId().equals(selectedResource.refMofId())) {
															isAssignedToSelectedResource = true;
															break;
														}
													}
													// Only report if activity matches selectedPriority
													boolean matchesSelectedPriority = false;
													if(selectedPriority == 0) {
														matchesSelectedPriority = true;
													}
													else {
														matchesSelectedPriority = selectedPriority == 1 ?
															workReportEntry.getActivity().getPriority() <= 2 :
															workReportEntry.getActivity().getPriority() > 2;
													}

													if(isMemberOfSelectedActivityGroup && isAssignedToSelectedResource && matchesSelectedPriority) {
														org.opencrx.kernel.depot1.jmi1.CompoundBooking workCb = workReportEntry.getWorkCb();
														boolean workCbContainsDepot = false;
														if(workCb != null) {
															for(Iterator l = workCb.getBooking().iterator(); l.hasNext(); ) {
																org.opencrx.kernel.depot1.jmi1.Booking booking = (org.opencrx.kernel.depot1.jmi1.Booking)l.next();
																if(booking.getPosition().getDepot().refMofId().equals(depot.refMofId())) {
																	workCbContainsDepot = true;
																	break;
																}
															}
														}
														GregorianCalendar calEntryStartedAt = new GregorianCalendar();
														calEntryStartedAt.setTime(workReportEntry.getStartedAt());
														if(
															workCbContainsDepot &&
															(calEntryStartedAt.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH))
														) {
															GregorianCalendar calEntryModifiedAt = new GregorianCalendar();
															calEntryModifiedAt.setTime(workReportEntry.getModifiedAt());
															sumDay += workReportEntry.getQuantity().doubleValue() * 60.0;
															// add this work record to map with key [startedAt(TimeStamp)][Counter(00000)] = yyyy-MM-ddTHH:mm:ss.SSSZ00000
															workRecords.put((workReportEntry.getStartedAt() != null ?
																crxDateFormat.format(workReportEntry.getStartedAt()) :
																DEFAULT_TIMESTAMP) + formatter5.format(counter),
															  workReportEntry
															);
															counter += 1;
														}
													}
												}
%>
												<td align="right"><%= decimalFormat.format(1.0 * (sumDay) / 60.0) %></td>
<%
												sumWeek += sumDay;
												sumDays[j] += sumDay;
												totalEffort[ii] += sumDay;
												cal.add(Calendar.DAY_OF_MONTH, 1);
												if((cal.get(Calendar.MONTH) != currentMonth) || (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)) {
        									for(int cd = j; cd < 7; cd++) {
%>
        										<td>&nbsp;</td>
<%
    										  }
    										  break;
    										}
											}
//											System.out.println("< depot");
%>
											<td align="right"><%= decimalFormat.format(1.0 * sumWeek / 60.0) %></td>
											<td align="right"><strong><%= decimalMinutesToHhMm(sumWeek) %></strong></td>
										</tr>
<%
									}
//									System.out.println("< week");
								}
%>
								<tr class="gridTableRowFull">
									<td></td>
									<td>Total:</td>
<%
									cal = (GregorianCalendar)calendar.clone();
									double sumWeekTotal = 0;
									for(int j = 0; j < 7; j++) {
									  if (cal.get(Calendar.DAY_OF_WEEK) > j+2) {
%>
                      <td>&nbsp;</td>
<%
									    continue;
									  }
%>
										<td align="right"><%= decimalFormat.format(1.0 * sumDays[j] / 60.0) %></td>
<%
										sumWeekTotal += sumDays[j];
										cal.add(Calendar.DAY_OF_MONTH, 1);
										if((cal.get(Calendar.MONTH) != currentMonth) || (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)) {
    									for(int cd = j; cd < 7; cd++) {
%>
    										<td>&nbsp;</td>
<%
										  }
										  break;
										}
									}
%>
									<td align="right"><%= decimalFormat.format(1.0 * sumWeekTotal / 60.0) %></td>
									<td align="right"><strong><%= decimalMinutesToHhMm(sumWeekTotal) %></strong></td>
								</tr>
							</table>
						</td>
					</tr>
<%
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
						calendar.add(Calendar.DAY_OF_MONTH, 1);
					}
				}
%>
				<tr>
					<td style="padding-top: 25px;">&nbsp;
					</td>
				</tr>
				<tr>
					<td style="padding:5px; border:1px solid #ddd;">
						<table id="table-<%= currentTableId++ %>" class="resultTableFull">
							<tr class="gridTableHeaderFull">
								<td style="text-align: left;">#</td>
								<td style="text-align: left;"><%= isActivityReport ? "Activity" : "Depot"%></td>
								<td>Total</td>
								<td>Total [hh:mm]</td>
							</tr>
<%
							int ii = 0;
							double sumMonth = 0.0;
							if(isActivityReport) {
								for(Iterator i = activities.values().iterator(); i.hasNext(); ii++) {
									org.opencrx.kernel.activity1.jmi1.Activity activity = (org.opencrx.kernel.activity1.jmi1.Activity)i.next();
									boolean includeActivity = includeActivity(
										activity,
										selectedActivityGroup,
										selectedPriority
									);
									sumMonth += totalEffort[ii];
									if(includeActivity) {
%>
										<tr class="gridTableRowFull">
											<td style="text-align: left;"><%= activity.getActivityNumber() %></td>
											<td style="text-align: left;"><%= activity.getName() %></td>
											<td align="right"><%= decimalFormat.format(1.0 * totalEffort[ii] / 60.0) %></td>
											<td align="right"><strong><%= decimalMinutesToHhMm(totalEffort[ii]) %></strong></td>
										</tr>
<%
									}
								}
							}
							else {
								for(Iterator i = depots.values().iterator(); i.hasNext(); ii++) {
									org.opencrx.kernel.depot1.jmi1.Depot depot = (org.opencrx.kernel.depot1.jmi1.Depot)i.next();
									sumMonth += totalEffort[ii];
%>
									<tr class="gridTableRowFull">
										<td style="text-align: left;"><%= depot.getDepotNumber() %></td>
										<td style="text-align: left;"><%= depot.getName() %></td>
										<td align="right"><%= decimalFormat.format(1.0 * totalEffort[ii] / 60.0) %></td>
										<td align="right"><strong><%= decimalMinutesToHhMm(totalEffort[ii]) %></strong></td>
									</tr>
<%
								}
							}
%>
							<tr class="gridTableRowFull">
								<td></td>
								<td><strong>Total:</strong></td>
								<td align="right"><strong><%= decimalFormat.format(1.0 * sumMonth / 60.0) %></strong></td>
								<td align="right"><strong><%= decimalMinutesToHhMm(sumMonth) %></strong></td>
							</tr>
						</table>
					</td>
				</tr>

				<tr>
					<td style="padding-top: 40px;">&nbsp;</td>
				</tr>
				<tr>
				  <td><strong><p style="page-break-before: always">Work Reports</p></strong></td>
				</tr>
				<tr>
					<td style="padding:5px; border:1px solid #ddd;">
						<table id="table-<%= currentTableId++ %>" class="resultTableFull">
							<tr class="gridTableHeaderFull">
								<td align="right">#</td>
								<td style="text-align: left;">Name</td>
								<td style="text-align: left;">Description</td>
								<td align="right">Activity</td>
								<td align="right">Started at</td>
								<td align="right">Ended at</td>
								<td style="text-align: left;">Resource</td>
								<td align="right">Effort<br>hh.mm</td>
								<td align="right">Effort<br>hh:mm</td>
							</tr>
<%
							int wr = 0;
							for(Iterator i = workRecords.values().iterator(); i.hasNext(); wr++) {
								org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord workReportEntry = (org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord)i.next();
								String wrHref = "";
								Action action = new Action(
								   Action.EVENT_SELECT_OBJECT,
								   new Action.Parameter[]{
								       new Action.Parameter(Action.PARAMETER_OBJECTXRI, workReportEntry.refMofId())
								   },
								   "",
								   true // enabled
								);
								wrHref = "../../" + action.getEncodedHRef();
								String actHref = "";
								if (workReportEntry.getActivity() != null) {
								  action = new Action(
								     Action.EVENT_SELECT_OBJECT,
								     new Action.Parameter[]{
								         new Action.Parameter(Action.PARAMETER_OBJECTXRI, workReportEntry.getActivity().refMofId())
								     },
								     "",
								     true // enabled
								  );
								  actHref = "../../" + action.getEncodedHRef();
								}
								String resHref = "";
								if (workReportEntry.getResource() != null) {
								  action = new Action(
								     Action.EVENT_SELECT_OBJECT,
								     new Action.Parameter[]{
								         new Action.Parameter(Action.PARAMETER_OBJECTXRI, workReportEntry.getResource().refMofId())
								     },
								     "",
								     true // enabled
								  );
								  resHref = "../../" + action.getEncodedHRef();
								}
%>
  							<tr class="gridTableRowFull">
  								<td align="right"><a href="<%= wrHref %>" target="_blank" title="click to open work report"><%= weekNumberFormatter.format(wr) %></a></td>
  								<td style="text-align: left;"><a href="<%= wrHref %>" target="_blank" title="click to open work report"><%= workReportEntry.getName() != null ? workReportEntry.getName() : "" %></a></td>
  								<td style="text-align: left;"><a href="<%= wrHref %>" target="_blank" title="click to open work report"><%= workReportEntry.getDescription() != null ? workReportEntry.getDescription() : "" %></a></td>
  								<td align="right"><a href="<%= actHref %>" target="_blank" title="click to open activity"><%= workReportEntry.getActivity().getActivityNumber() != null ? workReportEntry.getActivity().getActivityNumber() : "" %></a></td>
  								<td align="right"><a href="<%= wrHref %>" target="_blank" title="click to open work report"><%= workReportEntry.getStartedAt() != null ? activityDateFormat.format(workReportEntry.getStartedAt()) : "" %></a></td>
  								<td align="right"><a href="<%= wrHref %>" target="_blank" title="click to open work report"><%= workReportEntry.getEndedAt() != null ? activityDateFormat.format(workReportEntry.getEndedAt()) : "" %></a></td>
  								<td align="left"><a href="<%= resHref %>" target="_blank" title="click to open resource"><%= (workReportEntry.getResource() != null) && (workReportEntry.getResource().getName() != null) ? workReportEntry.getResource().getName() : "--" %></a></td>
  								<td align="right"><a href="<%= wrHref %>" target="_blank" title="click to open work report"><%= decimalFormat.format(workReportEntry.getQuantity().doubleValue()) %></a></td>
  								<td align="right"><a href="<%= wrHref %>" target="_blank" title="click to open work report"><%= decimalMinutesToHhMm(workReportEntry.getQuantity().doubleValue() * 60.0) %></a></td>
  							</tr>
<%
              }
%>
							<tr class="gridTableRowFull">
								<td colspan=7>&nbsp;</td>
								<td align="right"><strong><%= decimalFormat.format(1.0 * sumMonth / 60.0) %></strong></td>
								<td align="right"><strong><%= decimalMinutesToHhMm(sumMonth) %></strong></td>
							</tr>
						</table>
					</td>
				</tr>
<%
			}
%>
			</table>
		</td>
	 </tr>
	</table>
</form>
<%
	    }
    }
    catch (Exception ex) {
	      ServiceException e0 = new ServiceException(ex);
	      e0.log();
	      out.println("<p><b>!! Failed !!<br><br>The following exception(s) occured:</b><br><br><pre>");
	      PrintWriter pw = new PrintWriter(out);
	      e0.printStackTrace(pw);
	      out.println("</pre></p>");
	    // Go back to previous view
  		Action nextAction = new ObjectReference(obj, app).getSelectObjectAction();
  		response.sendRedirect(
  			request.getContextPath() + "/" + nextAction.getEncodedHRef()
    	);


    }
%>

<br>
<script language="javascript" type="text/javascript">

  function initPage() {
<%
		for(int i = 0; i < currentTableId; i++) {
%>
			makeZebraTable('table-<%= i %>', 1);
<%
		}
%>
  }
</script>

      </div> <!-- content -->
    </div> <!-- content-wrap -->
	<div> <!-- wrap -->
</div> <!-- container -->
</body>
</html>
