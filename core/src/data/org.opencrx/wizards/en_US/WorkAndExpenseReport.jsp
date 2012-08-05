<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/**
 * ====================================================================
 * Project:         openCRX/Core, http://www.opencrx.org/
 * Name:            $Id: WorkAndExpenseReport.jsp,v 1.18 2009/06/14 21:10:41 cmu Exp $
 * Description:     Create Work Record
 * Revision:        $Revision: 1.18 $
 * Owner:           CRIXP Corp., Switzerland, http://www.crixp.com
 * Date:            $Date: 2009/06/14 21:10:41 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2009, CRIXP Corp., Switzerland
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
java.io.*,
java.net.*,
java.text.*,
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
org.opencrx.kernel.backend.*,
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
org.openmdx.base.exception.*,
org.openmdx.base.text.conversion.*
" %>
<%!

	public static String getDateAsString(
		GregorianCalendar date
	) {
		return getDateAsString(
			date.get(GregorianCalendar.YEAR),
			date.get(GregorianCalendar.MONTH) + 1,
			date.get(GregorianCalendar.DAY_OF_MONTH)
		);
	}

	public static String getDateAsString(
		int year,
		int month,
		int dayOfMonth
	) {
		return // YYYYMMDD
			Integer.toString(year) +
			((month < 10 ? "0" : "") + Integer.toString(month)) +
			((dayOfMonth < 10 ? "0" : "") + Integer.toString(dayOfMonth));
	}

	public static GregorianCalendar getDateAsCalendar(
		String dateAsString, // YYYYMMDD
		                     // 01234567
		ApplicationContext app
	) {
		GregorianCalendar date = new GregorianCalendar(app.getCurrentLocale());
		date.set(GregorianCalendar.YEAR, Integer.valueOf(dateAsString.substring(0, 4)));
		date.set(GregorianCalendar.MONTH, Integer.valueOf(dateAsString.substring(4, 6)) - 1);
		date.set(GregorianCalendar.DAY_OF_MONTH, Integer.valueOf(dateAsString.substring(6)));
		date.set(GregorianCalendar.HOUR_OF_DAY, 0);
		date.set(GregorianCalendar.MINUTE, 0);
		date.set(GregorianCalendar.SECOND, 0);
		date.set(GregorianCalendar.MILLISECOND, 0);
		return date;
	}

	public static GregorianCalendar getDateTimeAsCalendar(
		String dateAsString, // dd-MM-YYYY HH:mm
		                     // 0123456789012345
		ApplicationContext app
	) {
		GregorianCalendar date = new GregorianCalendar(app.getCurrentLocale());
		try {
			date.set(GregorianCalendar.YEAR, Integer.valueOf(dateAsString.substring(6, 10)));
			date.set(GregorianCalendar.MONTH, Integer.valueOf(dateAsString.substring(3, 5)) - 1);
			date.set(GregorianCalendar.DAY_OF_MONTH, Integer.valueOf(dateAsString.substring(0, 2)));
			date.set(GregorianCalendar.HOUR_OF_DAY, Integer.valueOf(dateAsString.substring(11, 13)));
			date.set(GregorianCalendar.MINUTE, Integer.valueOf(dateAsString.substring(14)));
			date.set(GregorianCalendar.SECOND, 0);
			date.set(GregorianCalendar.MILLISECOND, 0);
		} catch (Exception e) {
				date = null;
		}
		return date;
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
	final int MAX_ACTIVITY_SHOWN_INITIALLY = 50;
	final int MAX_ACTIVITY_SHOWN = 500;
	final String FORM_NAME = "WorkAndExpenseReport";
	final String WIZARD_NAME = FORM_NAME + ".jsp";
	final String SUBMIT_HANDLER = "javascript:$('command').value=this.name;";
	final String CAUTION = "<img border='0=' alt='' height='16px' src='../../images/caution.gif' />";

	final String ACTIVITY_FILTER_SEGMENT = "Segment";
	final String ACTIVITY_FILTER_ANYGROUP = "AnyGroup";
	final String ACTIVITY_FILTER_TRACKER = "Tracker";
	final String ACTIVITY_FILTER_PROJECT = "Project";
	final String ACTIVITY_FILTER_CATEGORY = "Category";
	final String ACTIVITY_FILTER_MILESTONE = "Milestone";


	final String ACTIVITY_CLASS = "org:opencrx:kernel:activity1:Activity";
	final String ACTIVITYFILTER_CLASS = "org:opencrx:kernel:activity1:ActivityFilterGlobal";
	final String ACTIVITYSEGMENT_CLASS = "org:opencrx:kernel:activity1:Segment";
	final String ACTIVITYGROUPASSIGNMENT_CLASS = "org:opencrx:kernel:activity1:ActivityGroupAssignment";
	final String ACTIVITYTRACKER_CLASS = "org:opencrx:kernel:activity1:ActivityTracker";
	final String ACTIVITYCATEGORY_CLASS = "org:opencrx:kernel:activity1:ActivityCategory";
	final String ACTIVITYMILESTONE_CLASS = "org:opencrx:kernel:activity1:ActivityMilestone";
	final String RESOURCE_CLASS = "org:opencrx:kernel:activity1:Resource";
	final String ACCOUNT_CLASS = "org:opencrx:kernel:account1:Account";
	final String CONTACT_CLASS = "org:opencrx:kernel:account1:Contact";
	final String GROUP_CLASS = "org:opencrx:kernel:account1:Group";
	final String WORKANDEXPENSERECORD_CLASS = "org:opencrx:kernel:activity1:WorkAndExpenseRecord";
	final String CALENDAR_CLASS = "org:opencrx:kernel:activity1:Calendar";
	final int RECORDTYPE_WORK_MAX = 99; // <=99 --> WorkRecord, >= 100 --> ExpenseRecord
	final String[] UOM_NAMES = {
			"s", "min", "hour", "day",
			"m", "km", "mile", "feet", "inch",
			"kg",
			"Piece(s)", "Unit(s)"
	};

	final String featureRecordType = "org:opencrx:kernel:activity1:WorkAndExpenseRecord:recordType";
	final String featureRecordTypeWork = "org:opencrx:kernel:activity1:ActivityAddWorkRecordParams:recordType";
	final String featureRecordTypeExpense = "org:opencrx:kernel:activity1:ActivityAddExpenseRecordParams:recordType";
	final String featureBillingCurrency = "org:opencrx:kernel:activity1:WorkAndExpenseRecord:billingCurrency";
	final String featurePaymentType = "org:opencrx:kernel:activity1:WorkAndExpenseRecord:paymentType";

	final String contactTargetFinder = "org:opencrx:kernel:activity1:Resource:contact";

	final String errorStyle = "style='background-color:#FFF0CC;'";
	final String errorStyleInline = "background-color:#FFF0CC;";
	// Init
	request.setCharacterEncoding("UTF-8");
	ApplicationContext app = (ApplicationContext)session.getValue(WebKeys.APPLICATION_KEY);
	ViewsCache viewsCache = (ViewsCache)session.getValue(WebKeys.VIEW_CACHE_KEY_SHOW);
	String requestId =  request.getParameter(Action.PARAMETER_REQUEST_ID);
	String objectXri = request.getParameter(Action.PARAMETER_OBJECTXRI);
	javax.jdo.PersistenceManager pm = app.getPmData();
	String requestIdParam = Action.PARAMETER_REQUEST_ID + "=" + requestId;
	String xriParam = Action.PARAMETER_OBJECTXRI + "=" + objectXri;
	if((app == null) || (objectXri == null)) {
		session.setAttribute(WIZARD_NAME, null);
		response.sendRedirect(
			request.getContextPath() + "/" + WebKeys.SERVLET_NAME
		);
		return;
	}
	Texts_1_0 texts = app.getTexts();
	org.openmdx.portal.servlet.Codes codes = app.getCodes();

	RefObject_1_0 obj = (RefObject_1_0)pm.getObjectById(new Path(objectXri));
	String providerName = obj.refGetPath().get(2);
	String segmentName = obj.refGetPath().get(4);

	String errorMsg = "";

	// Format DateTimes
	TimeZone timezone = TimeZone.getTimeZone(app.getCurrentTimeZone());
	SimpleDateFormat dtf = new SimpleDateFormat("EEEE", app.getCurrentLocale());										dtf.setTimeZone(timezone);
	SimpleDateFormat monthFormat = new java.text.SimpleDateFormat("MMMM", app.getCurrentLocale());	monthFormat.setTimeZone(timezone);
	SimpleDateFormat dayInWeekFormat = new java.text.SimpleDateFormat("E", app.getCurrentLocale()); dayInWeekFormat.setTimeZone(timezone);
	SimpleDateFormat weekdayf = new SimpleDateFormat("EE", app.getCurrentLocale());									weekdayf.setTimeZone(timezone);
	SimpleDateFormat yyyyf = new SimpleDateFormat("yyyy", app.getCurrentLocale());										yyyyf.setTimeZone(timezone);
	SimpleDateFormat datetimef = new SimpleDateFormat("dd-MMM-yyyy HH:mm", app.getCurrentLocale());	datetimef.setTimeZone(timezone);
	SimpleDateFormat jsCalenderf = new SimpleDateFormat("dd-MM-yyyy HH:mm", app.getCurrentLocale());	jsCalenderf.setTimeZone(timezone);
	SimpleDateFormat datef = new SimpleDateFormat("EE d-MMMM-yyyy", app.getCurrentLocale());				datef.setTimeZone(timezone);
	SimpleDateFormat dtsortf = new SimpleDateFormat("yyyyMMddHHmmss", app.getCurrentLocale());			dtsortf.setTimeZone(timezone);
	SimpleDateFormat selectorf = new java.text.SimpleDateFormat("MM/yyyy", app.getCurrentLocale());	selectorf.setTimeZone(timezone);
	NumberFormat formatter2 = new DecimalFormat("00");
	NumberFormat formatter3 = new DecimalFormat("000");
	NumberFormat formatter = new DecimalFormat("00000");
	NumberFormat quantityf = new DecimalFormat("0.000");
	NumberFormat ratesepf = new DecimalFormat("#,##0.00");
	DecimalFormat decimalFormat = (DecimalFormat)DecimalFormat.getInstance(app.getCurrentLocale());

	org.opencrx.kernel.activity1.jmi1.Activity1Package activityPkg = org.opencrx.kernel.utils.Utils.getActivityPackage(pm);
	org.opencrx.kernel.activity1.jmi1.Segment activitySegment = (org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(
			new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName)
		);

  // Dates and Times
	Map formValues = new HashMap();

	UserDefinedView userView = new UserDefinedView(
		(RefObject_1_0)pm.getObjectById(new Path(objectXri)),
		app,
		(View)viewsCache.getViews().values().iterator().next()
	);
	int tabIndex = 1;

	boolean isFirstCall = request.getParameter("isFirstCall") == null; // used to properly initialize various options

	String command = request.getParameter("command");
	//System.out.println("command=" + command);
	boolean actionNextMonth = "NextMonth".equals(command);
	boolean actionPrevMonth = "PrevMonth".equals(command);
	boolean actionNextYear = "NextYear".equals(command);
 	boolean actionPrevYear = "PrevYear".equals(command);
	boolean actionSelectDate = command != null && command.startsWith("SelectDate.");
	boolean actionSelectDateP = command != null && command.startsWith("SelectDateP.");
	boolean actionSelectDateN = command != null && command.startsWith("SelectDateN.");
	boolean actionCancel = command != null && command.startsWith("cancel.");
	boolean actionEvictAndReload = command != null && command.startsWith("EVICT_RELOAD");

	if (actionEvictAndReload) {
			app.resetPmData();
	}

	boolean isWorkRecord = ((request.getParameter("isExpenseRecord") == null) || (request.getParameter("isExpenseRecord").length() == 0));
	boolean hasProjects = ((request.getParameter("hasProjects") != null) && (request.getParameter("hasProjects").length() > 0));

	String contactXri = null;
	String resourceXri = null;
	String activityFilter = null;
	String activityFilterXri = null;
	String activityXri = null;
	String selector  = request.getParameter("selector") == null ? "" : request.getParameter("selector");
  boolean isSelectorChange = ((request.getParameter("isSelectorChange") != null) && (request.getParameter("isSelectorChange").length() > 0));

	if (isFirstCall) {
		try {
			// try to derive initial settings from calling object
			if (obj instanceof org.opencrx.kernel.account1.jmi1.Contact) {
					// called from Contact
					contactXri = ((org.opencrx.kernel.account1.jmi1.Contact)obj).refMofId();
			} else if (obj instanceof org.opencrx.kernel.activity1.jmi1.ActivityTracker) {
					// called from ActivityTracker
					activityFilterXri = ((org.opencrx.kernel.activity1.jmi1.ActivityTracker)obj).refMofId();
					activityFilter = ACTIVITY_FILTER_TRACKER;
			} else if (obj instanceof org.opencrx.kernel.activity1.jmi1.ActivityCategory) {
				// called from ActivityCategory
				activityFilterXri = ((org.opencrx.kernel.activity1.jmi1.ActivityCategory)obj).refMofId();
				activityFilter = ACTIVITY_FILTER_CATEGORY;
			} else if (obj instanceof org.opencrx.kernel.activity1.jmi1.ActivityMilestone) {
				// called from ActivityMilestone
				activityFilterXri = ((org.opencrx.kernel.activity1.jmi1.ActivityMilestone)obj).refMofId();
				activityFilter = ACTIVITY_FILTER_MILESTONE;
			} else if ((obj instanceof org.opencrx.kernel.activity1.jmi1.Activity) || (obj instanceof org.opencrx.kernel.activity1.jmi1.ResourceAssignment)) {
					org.opencrx.kernel.activity1.jmi1.Activity activity = null;
					if (obj instanceof org.opencrx.kernel.activity1.jmi1.ResourceAssignment) {
							if (((org.opencrx.kernel.activity1.jmi1.ResourceAssignment)obj).getResource() != null) {
									resourceXri = ((org.opencrx.kernel.activity1.jmi1.ResourceAssignment)obj).getResource().refMofId();
							}
							activityXri = new Path(obj.refMofId()).getParent().getParent().toXri();
					} else {
							// called from Activity
							activityXri = ((org.opencrx.kernel.activity1.jmi1.Activity)obj).refMofId();
					}
					if (activityXri != null) {
							org.opencrx.kernel.activity1.jmi1.ActivityTracker tracker = null;
							org.opencrx.kernel.activity1.jmi1.ActivityCategory category = null;
							org.opencrx.kernel.activity1.jmi1.ActivityMilestone milestone = null;
							// hint: choose any of the assigned activity groups (preference: tracker > category > milestone), otherwise segment
							for(Iterator i = ((org.opencrx.kernel.activity1.jmi1.Activity)pm.getObjectById(new Path(activityXri))).getAssignedGroup().iterator(); i.hasNext(); ) {
									org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment ass = (org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment)i.next();
									if (ass.getActivityGroup() != null) {
											org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = ass.getActivityGroup();
											if (
													ag instanceof org.opencrx.kernel.activity1.jmi1.ActivityTracker &&
													(
															((org.opencrx.kernel.activity1.jmi1.ActivityTracker)ag).isDisabled() == null ||
															!((org.opencrx.kernel.activity1.jmi1.ActivityTracker)ag).isDisabled().booleanValue()
													)
											) {
													tracker = (org.opencrx.kernel.activity1.jmi1.ActivityTracker)ag;
											} else if (
													ag instanceof org.opencrx.kernel.activity1.jmi1.ActivityCategory &&
													(
															((org.opencrx.kernel.activity1.jmi1.ActivityCategory)ag).isDisabled() == null ||
															!((org.opencrx.kernel.activity1.jmi1.ActivityCategory)ag).isDisabled().booleanValue()
													)
											) {
													category = (org.opencrx.kernel.activity1.jmi1.ActivityCategory)ag;
											} else if (
													ag instanceof org.opencrx.kernel.activity1.jmi1.ActivityMilestone &&
													(
															((org.opencrx.kernel.activity1.jmi1.ActivityMilestone)ag).isDisabled() == null ||
															!((org.opencrx.kernel.activity1.jmi1.ActivityMilestone)ag).isDisabled().booleanValue()
													)
											) {
													milestone = (org.opencrx.kernel.activity1.jmi1.ActivityMilestone)ag;
											}
									}
									if (tracker != null) {
											activityFilterXri = tracker.refMofId();
											activityFilter = ACTIVITY_FILTER_TRACKER;
									} else if (category != null) {
											activityFilterXri = category.refMofId();
											activityFilter = ACTIVITY_FILTER_CATEGORY;
									} else if (milestone != null) {
										activityFilterXri = milestone.refMofId();
										activityFilter = ACTIVITY_FILTER_MILESTONE;
									} else {
										activityFilterXri = "";
										activityFilter = ACTIVITY_FILTER_SEGMENT;
									}
							}
					} else {
							activityXri = "*";
					}
			} else if (obj instanceof org.opencrx.kernel.activity1.jmi1.Resource) {
				// called from Resource
				resourceXri = ((org.opencrx.kernel.activity1.jmi1.Resource)obj).refMofId();
				if (((org.opencrx.kernel.activity1.jmi1.Resource)obj).getContact() != null) {
						contactXri = ((org.opencrx.kernel.activity1.jmi1.Resource)obj).getContact().refMofId();
				}
			}
		} catch (Exception e) {
				new ServiceException(e).log();
		}
		if (activityFilter == null) {activityFilter = ACTIVITY_FILTER_SEGMENT;}

		// determine wheter there are ActivityTrackers with userString0 != null
		org.opencrx.kernel.activity1.cci2.ActivityTrackerQuery trackerFilter = activityPkg.createActivityTrackerQuery();
		trackerFilter.forAllDisabled().isFalse();
		trackerFilter.thereExistsUserBoolean0().isTrue();
		hasProjects = !activitySegment.getActivityTracker(trackerFilter).isEmpty();

		// determine initial setting of selector
		GregorianCalendar selectorDate = new GregorianCalendar(app.getCurrentLocale());
		selectorDate.set(GregorianCalendar.DAY_OF_MONTH, 1);
		selectorDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
		selectorDate.set(GregorianCalendar.MINUTE, 0);
		selectorDate.set(GregorianCalendar.SECOND, 0);
		selectorDate.set(GregorianCalendar.MILLISECOND, 0);
		selector = selectorf.format(selectorDate.getTime());
		isSelectorChange = true;
	}

	// Parameter contact
	if (contactXri == null) {contactXri = request.getParameter("contactXri");}
	org.opencrx.kernel.account1.jmi1.Contact contact = null;
	String contactXriTitle = request.getParameter("contactXri.Title") == null ? "" : request.getParameter("contactXri.Title");
  boolean showAllResources = false;
  boolean showAllResourcesOfContact = false;
  boolean isResourceChange = ((request.getParameter("isResourceChange") != null) && (request.getParameter("isResourceChange").length() > 0));
  boolean isContactChange = ((request.getParameter("isContactChange") != null) && (request.getParameter("isContactChange").length() > 0));
	try {
			if ((contactXri != null) && (contactXri.length() > 0)) {
					if (contactXri.compareTo("*") == 0) {
							showAllResources = true;
							resourceXri = "*";
					} else {
							contact = (org.opencrx.kernel.account1.jmi1.Contact)pm.getObjectById(new Path(contactXri));
					}
			} else if (obj instanceof org.opencrx.kernel.account1.jmi1.Contact) {
					contact = (org.opencrx.kernel.account1.jmi1.Contact)obj;
			} else {
					// default is current users Contact (as defined in current user's UserHome
					// get UserHome
					org.opencrx.kernel.home1.jmi1.UserHome myUserHome =
						(org.opencrx.kernel.home1.jmi1.UserHome)pm.getObjectById(
							new Path("xri://@openmdx*org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName + "/userHome/" + app.getLoginPrincipalId())
						);
					if (myUserHome.getContact() != null) {
						contact = myUserHome.getContact();
					}
			}
	} catch (Exception e) {}

	org.opencrx.kernel.activity1.jmi1.Resource resource = null;
	if ((resourceXri == null) && (!isContactChange)) {
			resourceXri = request.getParameter("resourceXri");
			if ((resourceXri != null) && (resourceXri.length() > 0)) {
					if (resourceXri.compareTo("*") == 0) {
							showAllResourcesOfContact = true;
					} else if (isResourceChange) {
							try {
									resource = (org.opencrx.kernel.activity1.jmi1.Resource)pm.getObjectById(new Path(resourceXri));
									contact = resource.getContact();
									showAllResources = false;
							} catch (Exception e) {}
					}
			}
	}

	if (contact == null) {
			showAllResources = true;
			contactXri = "*";
			contactXriTitle = "*";
	} else {
			contactXri = contact.refMofId();
			contactXriTitle = (new ObjectReference(contact, app)).getTitle();
	}

	String projectMain = request.getParameter("projectMain") == null ? "" : request.getParameter("projectMain");

	if (activityFilterXri == null) {
			activityFilterXri = request.getParameter("activityFilterXri");
			if (activityFilterXri == null) {activityFilterXri = "";}
	}

	if (activityFilter == null) {
			activityFilter = request.getParameter("activityFilter");
			if (activityFilter == null) {activityFilter = "";}
	}

	org.opencrx.kernel.activity1.jmi1.ActivityGroup activityGroup = null;
	try {
		if ((activityFilterXri != null) && (activityFilterXri.length() > 0)) {
				activityGroup = (org.opencrx.kernel.activity1.jmi1.ActivityTracker)pm.getObjectById(new Path(activityFilterXri));
		}
	} catch (Exception e) {}

	if (activityXri == null) {
			activityXri = request.getParameter("activityXri")  == null ? "*" : request.getParameter("activityXri");
	}
	String recordType  = request.getParameter("recordType")  == null ? "0" : request.getParameter("recordType");  // Parameter recordType [default "0 - N/A"]
	String isBillable  = isFirstCall ? "" : request.getParameter("isBillable");
	String isReimbursable = isFirstCall ? "" : request.getParameter("isReimbursable");

	// Cancel
	if(actionCancel) {
		session.setAttribute(WIZARD_NAME, null);
		Action nextAction = new ObjectReference(obj, app).getSelectObjectAction();
		response.sendRedirect(
			request.getContextPath() + "/" + nextAction.getEncodedHRef()
		);
		return;
	}

	String scheduledStart = request.getParameter("scheduledStart") == null ? jsCalenderf.format(new java.util.Date()) : request.getParameter("scheduledStart").trim();
	String scheduledEnd = request.getParameter("scheduledEnd") == null ? jsCalenderf.format(new java.util.Date()) : request.getParameter("scheduledEnd").trim();

	GregorianCalendar scheduledStartDate = getDateTimeAsCalendar(scheduledStart, app);
	boolean scheduledStartDateOK = scheduledStartDate != null;

	GregorianCalendar scheduledEndDate = getDateTimeAsCalendar(scheduledEnd, app);
	boolean scheduledEndDateOK = scheduledEndDate != null;

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html dir="<%= texts.getDir() %>">
<head>
	<title>openCRX - Work/Expense Report</title>
	<meta name="label" content="Work/Expense Report">
	<meta name="toolTip" content="Work/Expense Report">
	<meta name="targetType" content="_self">
	<meta name="forClass" content="org:opencrx:kernel:activity1:Segment">
	<meta name="forClass" content="org:opencrx:kernel:activity1:Resource">
	<meta name="forClass" content="org:opencrx:kernel:activity1:ResourceAssignment">
	<meta name="forClass" content="org:opencrx:kernel:activity1:ActivityTracker">
	<meta name="forClass" content="org:opencrx:kernel:activity1:ActivityCategory">
	<meta name="forClass" content="org:opencrx:kernel:activity1:ActivityMilestone">
	<meta name="forClass" content="org:opencrx:kernel:activity1:Absence">
	<meta name="forClass" content="org:opencrx:kernel:activity1:EMail">
	<meta name="forClass" content="org:opencrx:kernel:activity1:Incident">
	<meta name="forClass" content="org:opencrx:kernel:activity1:Mailing">
	<meta name="forClass" content="org:opencrx:kernel:activity1:Meeting">
	<meta name="forClass" content="org:opencrx:kernel:activity1:SalesVisit">
	<meta name="forClass" content="org:opencrx:kernel:activity1:PhoneCall">
	<meta name="forClass" content="org:opencrx:kernel:activity1:Task">
	<meta name="forClass" content="org:opencrx:kernel:activity1:ExternalActivity">
	<meta name="forClass" content="org:opencrx:kernel:account1:Contact">
	<meta name="forClass" content="org:opencrx:kernel:home1:UserHome">
	<meta name="order" content="9999">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link href="../../_style/colors.css" rel="stylesheet" type="text/css">
	<link href="../../_style/n2default.css" rel="stylesheet" type="text/css">
	<link href="../../_style/ssf.css" rel="stylesheet" type="text/css">
  <link href="../../_style/calendar-small.css" rel="stylesheet" type="text/css">
	<link rel='shortcut icon' href='../../images/favicon.ico' />
  <script language="javascript" type="text/javascript" src="../../javascript/portal-all.js"></script>
  <script language="javascript" type="text/javascript" src="../../javascript/calendar/lang/calendar-<%= app.getCurrentLocaleAsString() %>.js"></script> <!-- calendar language -->
	<script language="javascript" type="text/javascript">
		var OF = null;
		try {
			OF = self.opener.OF;
		}
		catch(e) {
			OF = null;
		}
		if(!OF) {
			OF = new ObjectFinder();
		}

    function timeTick(hh_mm, upMins) {
      var right_now = new Date();
      var hrs = right_now.getHours();
      var mins = right_now.getMinutes();
      try {
        timeStr = hh_mm.split(":");
        hrsStr = timeStr[0];
        minsStr = timeStr[1];
      } catch (e) {}
      try {
        hrs = parseInt(hrsStr, 10);
      } catch (e) {}
      if (isNaN(hrs)) {hrs=12;}
      try {
        mins = parseInt(minsStr, 10);
        mins = parseInt(mins/15, 10)*15;
      } catch (e) {}
      if (isNaN(mins)) {mins=00;}
      mins = hrs*60 + mins + upMins;
      while (mins <      0) {mins += 24*60;}
      while (mins >= 24*60) {mins -= 24*60;}
      hrs = parseInt(mins/60, 10);
      if (hrs < 10) {
        hrsStr = "0" + hrs;
      } else {
        hrsStr = hrs;
      }
      mins -= hrs*60;
      if (mins < 10) {
        minsStr = "0" + mins;
      } else {
        minsStr = mins;
      }
      return hrsStr + ":" + minsStr;
    }

    var oldValue = "";
    function positiveDecimalsVerify(caller){
      var newValue = caller.value;
      var isOK = true;
      var i = 0;
      while ((isOK) && (i < newValue.length)) {
        var char = newValue.substring(i,i+1);
        if ((char!='.') && ((char<'0') || (char>'9'))) {isOK = false;}
        i++;
      }
      if (!isOK) {
        caller.value = oldValue;
      }
    }

	</script>

	<style type="text/css" media="all">
		fieldset{
			margin: 0px 10px 20px 0px;
			padding: 5px 0px 5px 15px;
			-moz-border-radius: 10px;
			border: 1.5px solid #DDD;
			background-color: #EEE;
		}
		.small{font-size:8pt;}
		#wizMonth {
			text-align:center;
			white-space:nowrap;
		}
		input.error{background-color:red;}
		#scheduleTable, .fieldGroup {
	    border-collapse: collapse;
	    border-spacing:0;
	    width:100%;
		}
		.fieldGroup TR TD {padding:2px 0px;}
		#scheduleTable td {
			vertical-align:top;
		}
		#scheduleTable TD.timelabel {
			background-color:#FFFE70;
			vertical-align:middle;
			border-top:1px solid #B3D7C3;
			border-bottom:1px solid #B3D7C3;
			border-left:1px solid #B3D7C3;
			white-space:nowrap;
			padding:5px;
		}
		#scheduleTable TD.time {
			background-color:#FFFE70;
			vertical-align:middle;
			border-top:1px solid #B3D7C3;
			border-right:1px solid #B3D7C3;
			border-bottom:1px solid #B3D7C3;
			white-space:nowrap;
			padding:5px;
			overflow:hidden;
		}
		TD.smallheader{border-bottom:1px solid black;padding:0px 8px 0px 0px;font-weight:bold;}
		TD.total{border-top:1px solid black;border-bottom:1px solid black;padding:0px 8px 0px 0px;font-weight:bold;}
		TD.totalR{border-top:1px solid black;border-bottom:1px solid black;padding:0px 16px 0px 0px;font-weight:bold;text-align:right;}
		TD.smallheaderR{border-bottom:1px solid black;padding:0px 16px 0px 0px;font-weight:bold;text-align:right;}
		TD.miniheader{font-size:7pt;}
		TD.padded{padding:0px 15px 0px 0px;}
		TD.padded_r{padding:0px 15px 0px 0px;text-align:right;}
		TR.centered TD {text-align:center;}
		TR.even TD {background-color:#EEEEFF;}
		TR.match TD {background-color:#FFFE70;}
		TR.created TD {font-weight:bold;}
		input.disabled {
			background-color:transparent;
		}
		input.disabled:hover {
			background-color:transparent;
		}
		.hidden {
			display:none;
		}
		.outofperiod {
			background-color:#F3F3F3;
		}
	</style>

</head>
<body>
<div id="container">
	<div id="wrap">
		<div id="scrollheader" style="height:90px;">
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
				<div id="aPanel">
					<div id="inspector">
						<div class="inspTabPanel" style="z-index: 201;">
							<a class="<%= isWorkRecord ? "selected" : "" %>" onclick="$('isExpenseRecord').value='';$('reload.button').click();" href="#">Work Report</a>
							<a class="<%= isWorkRecord ? "" : "selected" %>" onclick="$('isExpenseRecord').value='isExpenseRecord';$('reload.button').click();" href="#">Expense Report</a>
						</div>
						<div id="inspContent" class="inspContent" style="z-index: 200;">
							<div id="inspPanel0" class="selected" style="padding-top: 10px;">


				<form name="<%= FORM_NAME %>" accept-charset="UTF-8" method="POST" action="<%= WIZARD_NAME %>">
					<input type="hidden" name="command" id="command" value="none"/>
					<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= requestId %>" />
					<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= objectXri %>" />
					<input type="hidden" name="isExpenseRecord" id="isExpenseRecord" value="<%= isWorkRecord ? "" : "isExpenseRecord"  %>" />
					<input type="hidden" name="hasProjects" id="hasProjects" value="<%= hasProjects ? "hasProjects" : ""  %>" />
					<input type="hidden" name="isSelectorChange" id="isSelectorChange" value="" />
					<input type="hidden" name="isContactChange" id="isContactChange" value="" />
					<input type="hidden" name="isResourceChange" id="isResourceChange" value="" />
					<input type="checkbox" style="display:none;" id="isFirstCall" name="isFirstCall" checked />

					<table id="scheduleTable">
						<tr>
							<td style="width:100%;">
								<fieldset>
								<table class="fieldGroup">
									<tr>
										<td class="label"><span class="nw"><%= app.getTexts().getSelectAllText() %></span></td>
										<td nowrap>
<%
											boolean isManualEntry = "*".compareTo(selector) == 0;
											GregorianCalendar reportBeginOfPeriod = null;
											GregorianCalendar reportEndOfPeriod = null;
%>
											<select id="selector" name="selector" class="valueL" tabindex="<%= tabIndex++ %>" onchange="javascript:$('isSelectorChange').value='true';$('reload.button').click();" >
												<option <%= isManualEntry ? "selected" : ""  %> value="*">&mdash;&mdash;&mdash;&gt;</option>
<%
												GregorianCalendar now = new GregorianCalendar(app.getCurrentLocale());
												if (isManualEntry) {
														if (scheduledStartDateOK) {
																reportBeginOfPeriod = (GregorianCalendar)scheduledStartDate.clone();
														}
														if (scheduledEndDateOK) {
																reportEndOfPeriod = (GregorianCalendar)scheduledEndDate.clone();
														}
												}

												// full months
												for (int i=-7; i <= 7; i++) {
														GregorianCalendar beginOfPeriod = new GregorianCalendar(app.getCurrentLocale());
														beginOfPeriod.set(GregorianCalendar.DAY_OF_MONTH, 1);
														beginOfPeriod.add(GregorianCalendar.MONTH, i);
														beginOfPeriod.set(GregorianCalendar.HOUR_OF_DAY, 0);
														beginOfPeriod.set(GregorianCalendar.MINUTE, 0);
														beginOfPeriod.set(GregorianCalendar.SECOND, 0);
														beginOfPeriod.set(GregorianCalendar.MILLISECOND, 0);
														GregorianCalendar endOfPeriod = (GregorianCalendar)beginOfPeriod.clone();
														endOfPeriod.add(GregorianCalendar.MONTH, 1);
														endOfPeriod.add(GregorianCalendar.MINUTE, -1);
														String value = selectorf.format(beginOfPeriod.getTime());
														boolean selected = value.compareTo(selector) == 0;
														if (selected) {
																reportBeginOfPeriod = (GregorianCalendar)beginOfPeriod.clone();
																reportEndOfPeriod = (GregorianCalendar)endOfPeriod.clone();
																if (isSelectorChange) {
																		scheduledStartDate = (GregorianCalendar)beginOfPeriod.clone();
																		scheduledStart = jsCalenderf.format(beginOfPeriod.getTime());
																		scheduledEnd   = jsCalenderf.format(endOfPeriod.getTime());
																		scheduledEndDate = (GregorianCalendar)endOfPeriod.clone();
																}
														}
%>
														<option <%= selected ? "selected" : ""  %> value="<%= value %>"><%= value %></option>
<%
												}
%>
											</select>
										</td>
										<td class="addon"></td>

										<td class="label"><span class="nw"><%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "startedAt", app.getCurrentLocaleAsIndex()) %>:</span></td>
										<td style="padding-top:2px;">
												<input type="text" class="valueL" <%= scheduledStartDateOK ? "" : errorStyle %> name="scheduledStart" id="scheduledStart" maxlength="16" tabindex="<%= tabIndex++ %>" value="<%= scheduledStartDateOK ?  jsCalenderf.format(scheduledStartDate.getTime()) : scheduledStart %>" <%= isManualEntry ? "" : "readonly style='background-color:#F3F3F3;'" %> />
										</td>
										<td class="addon">
												<a><img class="popUpButton" id="cal_trigger_scheduledStart" border="0" alt="Click to open Calendar" src="../../images/cal.gif" <%= isManualEntry ? "" : "style='display:none;'" %> /></a>
												<script language="javascript" type="text/javascript">
														Calendar.setup({
																inputField   : "scheduledStart",
																ifFormat     : "%d-%m-%Y %H:%M",
																timeFormat   : "24",
																button       : "cal_trigger_scheduledStart",
																align        : "Tr",
																singleClick  : true,
																showsTime    : true
														});
												</script>
										</td>
									</tr>

									<tr>
										<td class="label"><span class="nw"></span></td>
										<td nowrap></td>
										<td class="addon"></td>

										<td class="label"><span class="nw"><%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "endedAt", app.getCurrentLocaleAsIndex()) %>:</span></td>
										<td style="padding-top:2px;">
												<input type="text" class="valueL" <%= scheduledEndDateOK ? "" : errorStyle %> name="scheduledEnd" id="scheduledEnd" maxlength="16" tabindex="<%= tabIndex++ %>" value="<%= scheduledEndDateOK ?  jsCalenderf.format(scheduledEndDate.getTime()) : scheduledEnd %>" <%= isManualEntry ? "" : "readonly style='background-color:#F3F3F3;'" %> />
										</td>
										<td class="addon">
												<a><img class="popUpButton" id="cal_trigger_scheduledEnd" border="0" alt="Click to open Calendar" src="../../images/cal.gif" <%= isManualEntry ? "" : "style='display:none;'" %> /></a>
												<script language="javascript" type="text/javascript">
														Calendar.setup({
																inputField   : "scheduledEnd",
																ifFormat     : "%d-%m-%Y %H:%M",
																timeFormat   : "24",
																button       : "cal_trigger_scheduledEnd",
																align        : "Tr",
																singleClick  : true,
																showsTime    : true
														});
												</script>
										</td>
									</tr>
								</table>
								</fieldset>

								<fieldset>
								<table class="fieldGroup">
									<tr>
										<td class="label"><span class="nw"><%= app.getLabel(CONTACT_CLASS) %>:</span></td>
<%
										String lookupId = org.opencrx.kernel.backend.Accounts.getInstance().getUidAsString();
										Action findContactTargetObjectAction = Action.getFindObjectAction(contactTargetFinder, lookupId);
										String accountName = app.getLabel(CONTACT_CLASS);
%>
										<td nowrap>
											<div class="autocompleterMenu">
												<ul id="nav" class="nav" onmouseover="sfinit(this);" >
													<li><a href="#"><img border="0" alt="" src="../../images/autocomplete_select.png" /></a>
														<ul onclick="this.style.left='-999em';" onmouseout="this.style.left='';">
															<li class="selected"><a href="#" onclick="javascript:navSelect(this);ac_addObject0.url= './'+getEncodedHRef(['../../ObjectInspectorServlet', 'event', '40', 'parameter', 'xri*(xri:@openmdx:org.opencrx.kernel.account1/provider/<%= providerName %>/segment/<%= segmentName %>)*referenceName*(account)*filterByType*(org:opencrx:kernel:account1:Contact)*filterByFeature*(fullName)*filterOperator*(IS_LIKE)*orderByFeature*(fullName)*position*(0)*size*(20)']);return false;"><span>&nbsp;&nbsp;&nbsp;</span><%= accountName %> / <%= userView.getFieldLabel(ACCOUNT_CLASS, "fullName", app.getCurrentLocaleAsIndex()) %></a></li>
															<li <%= userView.getFieldLabel(ACCOUNT_CLASS, "description", app.getCurrentLocaleAsIndex()) == null ? "style='display:none;" : "" %>><a href="#" onclick="javascript:navSelect(this);ac_addObject0.url= './'+getEncodedHRef(['../../ObjectInspectorServlet', 'event', '40', 'parameter', 'xri*(xri:@openmdx:org.opencrx.kernel.account1/provider/<%= providerName %>/segment/<%= segmentName %>)*referenceName*(account)*filterByType*(org:opencrx:kernel:account1:Contact)*filterByFeature*(description)*filterOperator*(IS_LIKE)*orderByFeature*(description)*position*(0)*size*(20)']);return false;"><span>&nbsp;&nbsp;&nbsp;</span><%= accountName %> / <%= userView.getFieldLabel(ACCOUNT_CLASS, "description", app.getCurrentLocaleAsIndex()) %></a></li>
															<li <%= userView.getFieldLabel(ACCOUNT_CLASS, "aliasName", app.getCurrentLocaleAsIndex()) == null ? "style='display:none;" : "" %>><a href="#" onclick="javascript:navSelect(this);ac_addObject0.url= './'+getEncodedHRef(['../../ObjectInspectorServlet', 'event', '40', 'parameter', 'xri*(xri:@openmdx:org.opencrx.kernel.account1/provider/<%= providerName %>/segment/<%= segmentName %>)*referenceName*(account)*filterByType*(org:opencrx:kernel:account1:Contact)*filterByFeature*(aliasName)*filterOperator*(IS_LIKE)*orderByFeature*(aliasName)*position*(0)*size*(20)']);return false;"><span>&nbsp;&nbsp;&nbsp;</span><%= accountName %> / <%= userView.getFieldLabel(ACCOUNT_CLASS, "aliasName", app.getCurrentLocaleAsIndex()) %></a></li>
															<li><a href="#" onclick="javascript:navSelect(this);ac_addObject0.url= './'+getEncodedHRef(['../../ObjectInspectorServlet', 'event', '40', 'parameter', 'xri*(xri:@openmdx:org.opencrx.kernel.account1/provider/<%= providerName %>/segment/<%= segmentName %>)*referenceName*(account)*filterByType*(org:opencrx:kernel:account1:Contact)*filterByFeature*(firstName)*filterOperator*(IS_LIKE)*orderByFeature*(firstName)*position*(0)*size*(20)']);return false;"><span>&nbsp;&nbsp;&nbsp;</span><%= accountName %> / <%= userView.getFieldLabel(CONTACT_CLASS, "firstName", app.getCurrentLocaleAsIndex()) %></a></li>
															<li><a href="#" onclick="javascript:navSelect(this);ac_addObject0.url= './'+getEncodedHRef(['../../ObjectInspectorServlet', 'event', '40', 'parameter', 'xri*(xri:@openmdx:org.opencrx.kernel.account1/provider/<%= providerName %>/segment/<%= segmentName %>)*referenceName*(account)*filterByType*(org:opencrx:kernel:account1:Contact)*filterByFeature*(middleName)*filterOperator*(IS_LIKE)*orderByFeature*(middleName)*position*(0)*size*(20)']);return false;"><span>&nbsp;&nbsp;&nbsp;</span><%= accountName %> / <%= userView.getFieldLabel(CONTACT_CLASS, "middleName", app.getCurrentLocaleAsIndex()) %></a></li>
															<li><a href="#" onclick="javascript:navSelect(this);ac_addObject0.url= './'+getEncodedHRef(['../../ObjectInspectorServlet', 'event', '40', 'parameter', 'xri*(xri:@openmdx:org.opencrx.kernel.account1/provider/<%= providerName %>/segment/<%= segmentName %>)*referenceName*(account)*filterByType*(org:opencrx:kernel:account1:Contact)*filterByFeature*(lastName)*filterOperator*(IS_LIKE)*orderByFeature*(lastName)*position*(0)*size*(20)']);return false;"><span>&nbsp;&nbsp;&nbsp;</span><%= accountName %> / <%= userView.getFieldLabel(CONTACT_CLASS, "lastName", app.getCurrentLocaleAsIndex()) %></a></li>
															<li <%= userView.getFieldLabel(CONTACT_CLASS, "nickName", app.getCurrentLocaleAsIndex()) == null ? "style='display:none;" : "" %>><a href="#" onclick="javascript:navSelect(this);ac_addObject0.url= './'+getEncodedHRef(['../../ObjectInspectorServlet', 'event', '40', 'parameter', 'xri*(xri:@openmdx:org.opencrx.kernel.account1/provider/<%= providerName %>/segment/<%= segmentName %>)*referenceName*(account)*filterByType*(org:opencrx:kernel:account1:Contact)*filterByFeature*(nickName)*filterOperator*(IS_LIKE)*orderByFeature*(nickName)*position*(0)*size*(20)']);return false;"><span>&nbsp;&nbsp;&nbsp;</span><%= accountName %> / <%= userView.getFieldLabel(CONTACT_CLASS, "nickName", app.getCurrentLocaleAsIndex()) %></a></li>
														</ul>
													</li>
												</ul>
											</div>
											<div class="autocompleterInput"><input type="text" class="valueL mandatory valueAC <%= contact == null ? "inputError" : "" %>" id="contactXri.Title" name="contactXri.Title" tabindex="<%= tabIndex++ %>" value="<%= contactXriTitle != null ? contactXriTitle : "" %>" /></div>
											<input type="hidden" class="valueLLocked" id="contactXri" readonly name="contactXri" value="<%= contactXri != null ? contactXri : "" %>" />
											<div class="autocomplete" id="contact.Update" style="display:none;z-index:500;"></div>
											<script type="text/javascript" language="javascript" charset="utf-8">
												function afterUpdateReload(titleField, selectedItem) {
														updateXriField(titleField, selectedItem);
														$('isContactChange').value="true";
													  $('reload.button').click();
												}
												ac_addObject0 = new Ajax.Autocompleter(
													'contactXri.Title',
													'contact.Update',
													'../../ObjectInspectorServlet?event=40&parameter=xri*%28xri%3A%40openmdx%3Aorg.opencrx.kernel.account1%2Fprovider%2F<%= providerName %>%2Fsegment%2F<%= segmentName %>%29*referenceName*%28account%29*filterByType*%28org%3Aopencrx%3Akernel%3Aaccount1%3AContact%29*filterByFeature*%28fullName%29*filterOperator*%28IS_LIKE%29*orderByFeature*%28fullName%29*position*%280%29*size*%2820%29',
													{
														paramName: 'filtervalues',
														minChars: 0,
														afterUpdateElement: afterUpdateReload
													}
												);
											</script>
										</td>
										<td class="addon">
											<img class="popUpButton" border="0" alt="" src="../../images/closeInsp.gif" style="float:right;" onclick="javascript:$('contactXri').value='*';$('isContactChange').value='true';$('contactXri.Title').value='*';$('reload.button').click();" />
											<img class="popUpButton" border="0" align="bottom" alt="Click to open ObjectFinder" src="../../images/lookup.gif" onclick="OF.findObject('../../<%= findContactTargetObjectAction.getEncodedHRef() %>', $('contactXri.Title'), $('contactXri'), '<%= lookupId %>');$('isContactChange').value='true';" />
										</td>
									</tr>

									<tr>
										<td class="label">
											<span class="nw"><%= app.getLabel(RESOURCE_CLASS) %>:</span>
										</td>
										<td>
<%
											boolean noResourcesFound = false;
											org.opencrx.kernel.activity1.cci2.ResourceQuery resourceFilter = activityPkg.createResourceQuery();
											if (!showAllResources) {
													resourceFilter.thereExistsContact().equalTo(contact);
											}
											resourceFilter.forAllDisabled().isFalse();
											resourceFilter.orderByName().ascending();
											resourceFilter.orderByDescription().ascending();
											List resources = activitySegment.getResource(resourceFilter);
										  if (resources.isEmpty()) {
											  	errorMsg += "no matching resource found!<br>";
											  	noResourcesFound = true;
											  	resourceXri = "";
%>
													<select id="resourceXri" name="resourceXri" class="valueL" <%= errorStyle %> tabindex="<%= tabIndex++ %>">
														<option value="">--</option>
													</select>
<%
										  } else {
%>
												<select id="resourceXri" name="resourceXri" class="valueL" tabindex="<%= tabIndex++ %>" onchange="javascript:$('isResourceChange').value='true';$('reload.button').click();" >
													<option <%= showAllResourcesOfContact ? "selected" : "" %> value="*">*</option>
<%
													for (
															Iterator i = resources.iterator();
															i.hasNext();
													) {
															org.opencrx.kernel.activity1.jmi1.Resource res = (org.opencrx.kernel.activity1.jmi1.Resource)i.next();
															String contactTitle = "--";
															try {
																	contactTitle = (new ObjectReference(res.getContact(), app)).getTitle();
															} catch (Exception e) {}
															if (((resourceXri == null) || (resourceXri.length() == 0)) && (!showAllResourcesOfContact)) {
																	resourceXri = res.refMofId();
															}
%>
															<option <%= (resourceXri != null) && (resourceXri.compareTo(res.refMofId()) == 0) ? "selected" : "" %> value="<%= res.refMofId() %>"><%= res.getName() %><%= showAllResources ? " [" + contactTitle + "]" : "" %></option>
<%
													}
%>
												</select>
<%
										  }
%>
											<input type="hidden" name="previousResourceXri" value="<%= resourceXri %>" />
										</td>
										<td class="addon">
												<%= noResourcesFound ? CAUTION : "" %>
										</td>
									</tr>

									<tr>
										<td class="label">
											<span class="nw"><%= app.getLabel(ACTIVITYFILTER_CLASS) %>:</span>
										</td>
										<td nowrap>
<%

											Map projectNames = new TreeMap();
											Iterator projectMainIterator = null;
											Iterator activityFilterIterator = null;
											Map orderedActivityGroups = new TreeMap();

											List activitiesList = null;
											org.opencrx.kernel.activity1.cci2.ActivityQuery activityQuery = activityPkg.createActivityQuery();
											activityQuery.forAllDisabled().isFalse();
											activityQuery.orderByName().ascending();
											activityQuery.orderByDescription().ascending();

											if (ACTIVITY_FILTER_SEGMENT.compareTo(activityFilter) == 0) {
													activitiesList = activitySegment.getActivity(activityQuery);
													activityGroup = null; // ensure that all activities are shown
											} else {
													int gCounter = 0;
													if (ACTIVITY_FILTER_ANYGROUP.compareTo(activityFilter) == 0) {
															// get ActivityTrackers
															org.opencrx.kernel.activity1.cci2.ActivityTrackerQuery trackerFilter = activityPkg.createActivityTrackerQuery();
															trackerFilter.forAllDisabled().isFalse();
															for(Iterator i = activitySegment.getActivityTracker(trackerFilter).iterator(); i.hasNext(); ) {
																	org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)i.next();
																	orderedActivityGroups.put(
																			(ag.getName() != null ? ag.getName() : "_?") + "    " + gCounter++,
																			ag
																		);
															}
															// get ActivityCategories
															org.opencrx.kernel.activity1.cci2.ActivityCategoryQuery categoryFilter = activityPkg.createActivityCategoryQuery();
															categoryFilter.forAllDisabled().isFalse();
															for(Iterator i = activitySegment.getActivityCategory(categoryFilter).iterator(); i.hasNext(); ) {
																	org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)i.next();
																	orderedActivityGroups.put(
																			(ag.getName() != null ? ag.getName() : "_?") + "    " + gCounter++,
																			ag
																		);
															}
															// get ActivityMilestones
															org.opencrx.kernel.activity1.cci2.ActivityMilestoneQuery milestoneFilter = activityPkg.createActivityMilestoneQuery();
															milestoneFilter.forAllDisabled().isFalse();
															for(Iterator i = activitySegment.getActivityMilestone(milestoneFilter).iterator(); i.hasNext(); ) {
																	org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)i.next();
																	orderedActivityGroups.put(
																			(ag.getName() != null ? ag.getName() : "_?") + "    " + gCounter++,
																			ag
																		);
															}
															activityFilterIterator = orderedActivityGroups.values().iterator();
													} else if (ACTIVITY_FILTER_PROJECT.compareTo(activityFilter) == 0) {
															// get projects, i.e. ActivityTrackers with userString0 != null
															org.opencrx.kernel.activity1.cci2.ActivityTrackerQuery trackerFilter = activityPkg.createActivityTrackerQuery();
															trackerFilter.forAllDisabled().isFalse();
															trackerFilter.thereExistsUserBoolean0().isTrue();
															trackerFilter.orderByUserString0().ascending();
															for(Iterator i = activitySegment.getActivityTracker(trackerFilter).iterator(); i.hasNext(); ) {
																	org.opencrx.kernel.activity1.jmi1.ActivityTracker at = (org.opencrx.kernel.activity1.jmi1.ActivityTracker)i.next();
																	if ((at.getUserString1() == null) || (at.getUserString1().length() == 0)) {
																		if ((projectMain.length() == 0) && (at.getUserString0() != null)) {
																				// set initial value of projectName
																				projectMain = at.getUserString0().trim();
																		}
																		projectNames.put(
																				(at.getUserString0() != null ? at.getUserString0().trim() : "_?"),
																				at
																			);
																	}
															}
															projectMainIterator = projectNames.values().iterator(); // all distinct project names

															trackerFilter = activityPkg.createActivityTrackerQuery();
															trackerFilter.forAllDisabled().isFalse();
															trackerFilter.userString1().isNonNull();
															trackerFilter.thereExistsUserString0().equalTo(projectMain);
															for(Iterator i = activitySegment.getActivityTracker(trackerFilter).iterator(); i.hasNext(); ) {
																	org.opencrx.kernel.activity1.jmi1.ActivityTracker at = (org.opencrx.kernel.activity1.jmi1.ActivityTracker)i.next();
																	if ((at.getUserString1() != null) && (at.getUserString1().length() > 0)) {
																		orderedActivityGroups.put(
																				(at.getUserString1() != null ? at.getUserString1().trim() : "_?") + "    " + gCounter++,
																				at
																			);
																	}
															}
															activityFilterIterator = orderedActivityGroups.values().iterator();
													} else if (ACTIVITY_FILTER_TRACKER.compareTo(activityFilter) == 0) {
															// get ActivityTrackers
															org.opencrx.kernel.activity1.cci2.ActivityTrackerQuery trackerFilter = activityPkg.createActivityTrackerQuery();
															trackerFilter.forAllDisabled().isFalse();
															for(Iterator i = activitySegment.getActivityTracker(trackerFilter).iterator(); i.hasNext(); ) {
																	org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)i.next();
																		orderedActivityGroups.put(
																				(ag.getName() != null ? ag.getName() : "_?") + "    " + gCounter++,
																				ag
																			);
															}
															activityFilterIterator = orderedActivityGroups.values().iterator();
													} else if (ACTIVITY_FILTER_CATEGORY.compareTo(activityFilter) == 0) {
															// get ActivityCategories
															org.opencrx.kernel.activity1.cci2.ActivityCategoryQuery categoryFilter = activityPkg.createActivityCategoryQuery();
															categoryFilter.forAllDisabled().isFalse();
															for(Iterator i = activitySegment.getActivityCategory(categoryFilter).iterator(); i.hasNext(); ) {
																	org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)i.next();
																	orderedActivityGroups.put(
																			(ag.getName() != null ? ag.getName() : "_?") + "    " + gCounter++,
																			ag
																		);
															}
															activityFilterIterator = orderedActivityGroups.values().iterator();
													} else if (ACTIVITY_FILTER_MILESTONE.compareTo(activityFilter) == 0) {
															// get ActivityMilestones
															org.opencrx.kernel.activity1.cci2.ActivityMilestoneQuery milestoneFilter = activityPkg.createActivityMilestoneQuery();
															milestoneFilter.forAllDisabled().isFalse();
															for(Iterator i = activitySegment.getActivityMilestone(milestoneFilter).iterator(); i.hasNext(); ) {
																	org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)i.next();
																	orderedActivityGroups.put(
																			(ag.getName() != null ? ag.getName() : "_?") + "    " + gCounter++,
																			ag
																		);
															}
															activityFilterIterator = orderedActivityGroups.values().iterator();
													}

													tabIndex += 10;
													if (ACTIVITY_FILTER_PROJECT.compareTo(activityFilter) != 0) {
														  if (activityFilterIterator == null || !activityFilterIterator.hasNext()) {
															  errorMsg += "no activity groups found!<br>";
%>
																<select class="valueL" style="width:50%;float:right;" id="activityFilterXri" name="activityFilterXri" class="valueL" <%= errorStyle %> tabindex="<%= tabIndex+5 %>">
																	<option value="">--</option>
																</select>
<%
														  } else {
%>
																<select class="valueL" style="width:50%;float:right;" id="activityFilterXri" name="activityFilterXri" tabindex="<%= tabIndex+5 %>" onchange="javascript:$('reload.button').click();" >
<%
																	boolean hasSelection = false;
																	org.opencrx.kernel.activity1.jmi1.ActivityGroup firstAg = null; 
																	while (activityFilterIterator != null && activityFilterIterator.hasNext()) {
																			org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)activityFilterIterator.next();
																			boolean selected = false;
																			if ((activityFilterXri != null) && (activityFilterXri.compareTo(ag.refMofId()) == 0)) {
																					activityGroup = ag;
																					selected = true;
																					hasSelection = true;
																			}
																			if (firstAg == null) {
																					firstAg = ag;
																			}
%>
																			<option <%= selected ? "selected" : "" %> value="<%= ag.refMofId() %>"><%= ag.getName() %></option>
<%
																	}
																	if (!hasSelection) {
																			activityGroup = firstAg; // to ensure proper location of activities
																			activityFilterXri = firstAg.refMofId();
																	}
%>
																</select>
<%
															}
													}
											}
%>
											<select class="valueL" style="width:<%= ACTIVITY_FILTER_SEGMENT.compareTo(activityFilter) == 0 || ACTIVITY_FILTER_PROJECT.compareTo(activityFilter) == 0 ? "100" : "49" %>%;float:left;" id="activityGroupType" name="activityFilter" tabindex="<%= tabIndex++ %>" onchange="javascript:$('reload.button').click();" >
												<option <%= ACTIVITY_FILTER_SEGMENT.compareTo(activityFilter)   == 0 ? "selected" : "" %> value="<%= ACTIVITY_FILTER_SEGMENT %>"  >*</option>
												<option <%= ACTIVITY_FILTER_ANYGROUP.compareTo(activityFilter) 	== 0 ? "selected" : "" %> value="<%= ACTIVITY_FILTER_ANYGROUP %>" ><%= app.getLabel(ACTIVITYTRACKER_CLASS) %> / <%= app.getLabel(ACTIVITYCATEGORY_CLASS) %> / <%= app.getLabel(ACTIVITYMILESTONE_CLASS) %></option>
<%
												if (hasProjects) {
%>
														<option <%= ACTIVITY_FILTER_PROJECT.compareTo(activityFilter)   == 0 ? "selected" : "" %> value="<%= ACTIVITY_FILTER_PROJECT %>"  ><%= app.getLabel(ACTIVITYTRACKER_CLASS) %> [<%= userView.getFieldLabel(ACTIVITYTRACKER_CLASS, "userBoolean0", app.getCurrentLocaleAsIndex()) %>]</option>
<%
												}
%>
												<option <%= ACTIVITY_FILTER_TRACKER.compareTo(activityFilter)   == 0 ? "selected" : "" %> value="<%= ACTIVITY_FILTER_TRACKER %>"  ><%= app.getLabel(ACTIVITYTRACKER_CLASS) %></option>
												<option <%= ACTIVITY_FILTER_CATEGORY.compareTo(activityFilter)  == 0 ? "selected" : "" %> value="<%= ACTIVITY_FILTER_CATEGORY %>" ><%= app.getLabel(ACTIVITYCATEGORY_CLASS) %></option>
												<option <%= ACTIVITY_FILTER_MILESTONE.compareTo(activityFilter) == 0 ? "selected" : "" %> value="<%= ACTIVITY_FILTER_MILESTONE %>"><%= app.getLabel(ACTIVITYMILESTONE_CLASS) %></option>
											</select>
										</td>
										<td class="addon"></td>
									</tr>
<%
									if (hasProjects) {
%>
										<tr <%= ACTIVITY_FILTER_PROJECT.compareTo(activityFilter) == 0 ? "" : "style='display:none;'" %>>
											<td class="label">
												<span class="nw"><%= userView.getFieldLabel(ACTIVITYTRACKER_CLASS, "userBoolean0", app.getCurrentLocaleAsIndex()) %> - <%= userView.getFieldLabel(ACTIVITYTRACKER_CLASS, "userString1", app.getCurrentLocaleAsIndex()) %>:</span>
											</td>
											<td nowrap>
<%
												if (activityFilterIterator == null || !activityFilterIterator.hasNext()) {
													  errorMsg += "no activity groups found!<br>";
%>
														<select class="valueL" style="width:50%;float:right;" id="activityFilterXri" name="activityFilterXri" class="valueL" <%= errorStyle %> tabindex="<%= tabIndex+5 %>">
															<option value="">--</option>
														</select>
<%
												} else {
%>
														<select class="valueL" style="width:50%;float:right;" id="activityFilterXri" name="activityFilterXri" tabindex="<%= tabIndex+5 %>" onchange="javascript:$('reload.button').click();" >
<%
															boolean hasSelection = false;
															org.opencrx.kernel.activity1.jmi1.ActivityGroup firstAg = null; 
															while (activityFilterIterator.hasNext()) {
																	org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)activityFilterIterator.next();
																	boolean selected = false;
																	if ((activityFilterXri != null) && (activityFilterXri.compareTo(ag.refMofId()) == 0)) {
																			activityGroup = ag;
																			selected = true;
																			hasSelection = true;
																	}
																	if (firstAg == null) {
																			firstAg = ag;
																	}
%>
																	<option <%= (activityFilterXri != null) && (activityFilterXri.compareTo(ag.refMofId()) == 0) ? "selected" : "" %> value="<%= ag.refMofId() %>"><%= ag.getName() %></option>
<%
															}
															if (!hasSelection) {
																	activityGroup = firstAg; // to ensure proper location of activities
																	activityFilterXri = firstAg.refMofId();
															}
%>
														</select>
<%
												}

												if (projectMainIterator == null || !projectMainIterator.hasNext()) {
														errorMsg += "no main topics!<br>";
%>
														<select id="projectMain" name="projectMain" class="valueL" style="width:49%;float:left;<%= errorStyleInline %>" tabindex="<%= tabIndex++ %>">
															<option value="">--</option>
														</select>
<%
												} else {
%>
														<select class="valueL" style="width:49%;float:left;" id="projectMain" name="projectMain" tabindex="<%= tabIndex++ %>" onchange="javascript:$('reload.button').click();" >
<%
															while (projectMainIterator.hasNext()) {
																org.opencrx.kernel.activity1.jmi1.ActivityTracker at = (org.opencrx.kernel.activity1.jmi1.ActivityTracker)projectMainIterator.next();
																if (at.getUserString0() != null) {
%>
																	<option <%= (projectMain != null) && (projectMain.compareTo(at.getUserString0().trim()) == 0) ? "selected" : "" %> value="<%= at.getUserString0().trim() %>"><%= at.getUserString0().trim() %></option>
<%
																}
															}
%>
														</select>
<%
												}
%>
											</td>
											<td class="addon"></td>
										</tr>
<%
									}
%>
									<tr>
										<td class="label">
											<span class="nw"><%= app.getLabel(ACTIVITYSEGMENT_CLASS) %>:</span>
										</td>
										<td>
<%
												tabIndex += 10;
												if (activityGroup != null) {
														activitiesList = activityGroup.getFilteredActivity(activityQuery);
												}
											  int activityCounter = 0;
											  int maxToShow = MAX_ACTIVITY_SHOWN_INITIALLY;
											  if (activityXri != null && "MAX".compareTo(activityXri) == 0) {
												  	maxToShow = MAX_ACTIVITY_SHOWN;
											  };
											  boolean allFilteredActivities = "*".compareTo(activityXri) == 0;
%>
												<select id="activityXri" name="activityXri" class="valueL" tabindex="<%= tabIndex++ %>" onchange="javascript:$('reload.button').click();" >
													<option <%= allFilteredActivities ? "selected" : ""  %> value="*">*</option>
<%
													if (activitiesList != null) {
															for (
																	Iterator i = activitiesList.iterator();
																	i.hasNext() && (activityCounter < maxToShow);
																	activityCounter++
															) {
																	org.opencrx.kernel.activity1.jmi1.Activity activity = (org.opencrx.kernel.activity1.jmi1.Activity)i.next();
%>
																	<option <%= (activityXri != null) && (activityXri.compareTo(activity.refMofId()) == 0) ? "selected" : "" %> value="<%= activity.refMofId() %>">#<%= activity.getActivityNumber() %>: <%= activity.getName() %></option>
<%
															}
													}
											  	if (activityCounter >= maxToShow) {
%>
														<option value="MAX"><%= activityCounter < MAX_ACTIVITY_SHOWN ? "&mdash;&mdash;&gt;" : "..." %></option>
<%
											  	}
%>
											</select>
										</td>
										<td class="addon"></td>
									</tr>

									<tr>
										<td class="label">
											<span class="nw"><%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "isBillable", app.getCurrentLocaleAsIndex()) %>:</span>
										</td>
										<td>
											<input type="checkbox" name="isBillable" <%= (isBillable != null) && (isBillable.length() > 0) ? "checked" : "" %> tabindex="<%= tabIndex++ %>" value="isBillable" onchange="javascript:$('reload.button').click();" />
										</td>
										<td class="addon"></td>
									</tr>

									<tr>
										<td class="label">
											<span class="nw"><%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "isReimbursable", app.getCurrentLocaleAsIndex()) %>:</span>
										</td>
										<td>
											<input type="checkbox" name="isReimbursable" <%= (isReimbursable != null) && (isReimbursable.length() > 0) ? "checked" : "" %> tabindex="<%= tabIndex++ %>" value="isReimbursable" onchange="javascript:$('reload.button').click();" />
										</td>
										<td class="addon"></td>
									</tr>

								</table>
								</fieldset>

							</td>
						</tr>
						<tr>
							<td>
									<input type="submit" id="EVICT_RELOAD" name="EVICT_RELOAD" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getReloadText() %>" onclick="<%= SUBMIT_HANDLER %>" />
									<input type="submit" id="reload.button" name="reload.button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getReloadText() %>" onclick="<%= SUBMIT_HANDLER %>" style="display:none;" />
									<input type="submit" id="cancel.button" name="cancel.button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getCancelTitle() %>" onclick="<%= SUBMIT_HANDLER %>" />
							</td>
						</tr>
					</table>

					<br>

<!-- REPORT -->
<%
					Map selectedActivities = null;
					if (allFilteredActivities) {
							if ((ACTIVITY_FILTER_SEGMENT.compareTo(activityFilter) != 0) && (activitiesList != null)) {
									// hint: instead of adding all activities of the segment to the Map selectedActivities
									//       is left at null; this ensures that all activities are included
									selectedActivities = new TreeMap();
									for (
											Iterator i = activitiesList.iterator();
											i.hasNext();
									) {
											org.opencrx.kernel.activity1.jmi1.Activity activity = (org.opencrx.kernel.activity1.jmi1.Activity)i.next();
											selectedActivities.put(activity.getActivityNumber() == null ? "-" : activity.getActivityNumber(), activity);
									}
							}
					} else if (activityXri != null && activityXri.length() > 1 && "MAX".compareTo(activityXri) != 0) {
							selectedActivities = new TreeMap();
							org.opencrx.kernel.activity1.jmi1.Activity activity = (org.opencrx.kernel.activity1.jmi1.Activity)pm.getObjectById(new Path(activityXri));
							selectedActivities.put(activity.getActivityNumber() == null ? "-" : activity.getActivityNumber(), activity);
					}

					Map selectedResources = null;
					boolean hasMultipleResources = true;
					if (
							!showAllResources &&
							showAllResourcesOfContact &&
							(resourceXri != null && "*".compareTo(resourceXri) == 0) &&
							contact != null
					) {
							selectedResources = new TreeMap();
							org.opencrx.kernel.activity1.cci2.ResourceQuery resFilter = activityPkg.createResourceQuery();
							resFilter.thereExistsContact().equalTo(contact);
							resFilter.forAllDisabled().isFalse();
							for (
									Iterator i = activitySegment.getResource(resFilter).iterator();
									i.hasNext();
							) {
									org.opencrx.kernel.activity1.jmi1.Resource res = (org.opencrx.kernel.activity1.jmi1.Resource)i.next();
									selectedResources.put(res.refMofId(), res);
							}
					}

					org.opencrx.kernel.activity1.jmi1.Resource selectedResource = null;
					//System.out.println("allResources= " + showAllResources);
					//System.out.println("allResOfCont= " + showAllResourcesOfContact);
					//System.out.println("resourceXri = " + resourceXri);
					//System.out.println("resource    = " + resource);
					//System.out.println("selectedRes = " + selectedResources);

					boolean doReportCalculation = true;
					org.opencrx.kernel.activity1.cci2.WorkAndExpenseRecordQuery workAndExpenseRecordFilter = activityPkg.createWorkAndExpenseRecordQuery();
					if (reportBeginOfPeriod == null && reportEndOfPeriod == null) {
							doReportCalculation = false; // do NOT calculate work/expense report
					} else {
							if (reportBeginOfPeriod != null) {
									workAndExpenseRecordFilter.thereExistsStartedAt().greaterThanOrEqualTo(reportBeginOfPeriod.getTime());
							}
							if (reportEndOfPeriod != null) {
									workAndExpenseRecordFilter.thereExistsStartedAt().lessThanOrEqualTo(reportEndOfPeriod.getTime());
							}
					}
					//workAndExpenseRecordFilter.thereExistsStartedAt().between(
					//		reportBeginOfPeriod.getTime(),
					//		reportEndOfPeriod.getTime()
					//);
					if (isWorkRecord) {
							workAndExpenseRecordFilter.recordType().between(new Short((short)1), new Short((short)RECORDTYPE_WORK_MAX)); // work records only, i.e. no expense records
					} else {
							workAndExpenseRecordFilter.recordType().greaterThan(new Short((short)RECORDTYPE_WORK_MAX));
					}
					if ((isBillable != null) && (isBillable.length() > 0)) {
							workAndExpenseRecordFilter.forAllIsBillable().isTrue();
					}
					if ((isReimbursable != null) && (isReimbursable.length() > 0)) {
							workAndExpenseRecordFilter.forAllIsReimbursable().isTrue();
					}

					Iterator w = null;
					if (
							!showAllResourcesOfContact &&
							(resourceXri != null && (resourceXri.length() > 0) && "*".compareTo(resourceXri) != 0)
					) {
							resource = (org.opencrx.kernel.activity1.jmi1.Resource)pm.getObjectById(new Path(resourceXri));
							w = resource.getWorkReportEntry(workAndExpenseRecordFilter).iterator();
							hasMultipleResources = false;
					} else {
							w = activitySegment.getWorkReportEntry(workAndExpenseRecordFilter).iterator();
					}

					// init totalizers
					Map totals 							= new TreeMap();	// (currency, double)
					Map totalsBillable 			= new TreeMap();	// (currency, double)
					Map totalsReimbursable	= new TreeMap();	// (currency, double)
					Map activities          = new TreeMap();	// (activityNumber, XRI)
					Map totalsPerActivity		= new TreeMap();	// (activityNumber_currency, double)
					Map totalsPerWeekDay		= new TreeMap();	// (yyyyNNDD_currency, double)	[NN = WEEK_OF_YEAR, EE = DAY_OF_WEEK]

					String timeKey = " hh:mm";
					totals.put(timeKey, (Double)0.0);
					totalsBillable.put(timeKey, (Double)0.0);
					totalsReimbursable.put(timeKey, (Double)0.0);
					boolean totalsError = false;
					
					java.util.Date earliestDate = null;
					java.util.Date latestDate = null;

					Map workReportEntries = new TreeMap(); // (startedAt[YYYYMMDD]-counter[00000], WorkEndExpenseRecord)
					int counter = 0;

					while (doReportCalculation && w.hasNext()) {
							org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord workAndExpenseRecord = (org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord)w.next();
							String activityNumber = null;
							try {
								activityNumber = workAndExpenseRecord.getActivity().getActivityNumber() == null
								? "-"
								: workAndExpenseRecord.getActivity().getActivityNumber();
							} catch (Exception e) {
									activityNumber = "-";
							}
							if (
									((selectedActivities == null) || (selectedActivities.containsKey(activityNumber))) &&
									((selectedResources  == null) || (selectedResources.containsKey(workAndExpenseRecord.getResource().refMofId())))
							) {
									workReportEntries.put(
											(workAndExpenseRecord.getStartedAt() != null ? dtsortf.format(workAndExpenseRecord.getStartedAt()) : "yyyyMMddHHmmss") + "-" + formatter.format(counter++),
											workAndExpenseRecord
									);
									//System.out.println("added workRecord: " + dtsortf.format(workAndExpenseRecord.getStartedAt()) + "-" + formatter.format(counter++));

									if (earliestDate == null || (workAndExpenseRecord.getStartedAt() != null && earliestDate.compareTo(workAndExpenseRecord.getStartedAt()) > 0)) {
											earliestDate = workAndExpenseRecord.getStartedAt();
									}
									if (latestDate == null || (workAndExpenseRecord.getStartedAt() != null && latestDate.compareTo(workAndExpenseRecord.getStartedAt()) < 0)) {
											latestDate = workAndExpenseRecord.getStartedAt();
									}
									
									double recordTotal = 0.0;
									double timeTotal = 0.0;
									boolean quantityError = false;
									try {
											timeTotal = workAndExpenseRecord.getQuantity().doubleValue();
											recordTotal = workAndExpenseRecord.getQuantity().doubleValue() * workAndExpenseRecord.getRate().doubleValue();
									} catch (Exception e) {
											quantityError = true;
											totalsError = true;
									}
									if (workAndExpenseRecord.getBillingCurrency() == 0) {
											quantityError = true;
											totalsError = true;
									}

									String currency = (String)(codes.getShortText(featureBillingCurrency, app.getCurrentLocaleAsIndex(), true, true).get(new Short(workAndExpenseRecord.getBillingCurrency())));
									if (totals.get(currency) == null) {
											// init this currency
											totals.put(currency, (Double)0.0);
											totalsBillable.put(currency, (Double)0.0);
											totalsReimbursable.put(currency, (Double)0.0);
									}

									String activityKey = null; // activityNumber
									String activityTimeKey = null;
									try {
											try {
													activities.put(activityNumber, workAndExpenseRecord.getActivity().refMofId());
											} catch (Exception ea) {
													// probably a work record with missing activity
													new ServiceException(ea).log();
											}
											activityKey = activityNumber + "_" + currency;
											activityTimeKey = activityNumber + "_" + timeKey;
									} catch (Exception e) {
											totalsError = true;
											activityKey = "?_" + currency;
											activityTimeKey = "?_" + timeKey;
									}
									if (totalsPerActivity.get(activityKey) == null) {
											totalsPerActivity.put(activityKey, (Double)0.0);
									}
									if (totalsPerActivity.get(activityTimeKey) == null) {
											totalsPerActivity.put(activityTimeKey, (Double)0.0);
									}

									String WWDDKey = null; 		// YYYYWWDD where WW week of year and DD day of week
									String WWDDsumKey = null; // YYYYWW99 (sum of week)
									String WWDDTimeKey = null;
									String WWDDsumTimeKey = null;
									try {
											GregorianCalendar cal = new GregorianCalendar(app.getCurrentLocale());
											cal.setTime(workAndExpenseRecord.getStartedAt());
											WWDDKey = 			yyyyf.format(cal.getTime()) + 
																			formatter2.format(cal.get(GregorianCalendar.WEEK_OF_YEAR)) +
																			formatter2.format(cal.get(GregorianCalendar.DAY_OF_WEEK) % 7) + "_" + currency;
											WWDDsumKey =		yyyyf.format(cal.getTime()) + 
																			formatter2.format(cal.get(GregorianCalendar.WEEK_OF_YEAR)) +
																			"99_" + currency;
											WWDDTimeKey = 	yyyyf.format(cal.getTime()) + 
																			formatter2.format(cal.get(GregorianCalendar.WEEK_OF_YEAR)) +
																			formatter2.format(cal.get(GregorianCalendar.DAY_OF_WEEK) % 7) + "_" + timeKey;
											WWDDsumTimeKey = yyyyf.format(cal.getTime()) + 
																			formatter2.format(cal.get(GregorianCalendar.WEEK_OF_YEAR)) +
																			"99_" + timeKey;
									} catch (Exception e) {
											totalsError = true;
											WWDDKey = "?_" + currency;
											WWDDsumKey = "9_" + currency;
											WWDDTimeKey = "?_" + timeKey;
											WWDDsumTimeKey = "9_" + timeKey;
									}
									if (totalsPerWeekDay.get(WWDDKey) == null) {
											totalsPerWeekDay.put(WWDDKey, (Double)0.0);
									}
									if (totalsPerWeekDay.get(WWDDsumKey) == null) {
											totalsPerWeekDay.put(WWDDsumKey, (Double)0.0);
									}
									if (totalsPerWeekDay.get(WWDDTimeKey) == null) {
											totalsPerWeekDay.put(WWDDTimeKey, (Double)0.0);
									}
									if (totalsPerWeekDay.get(WWDDsumTimeKey) == null) {
											totalsPerWeekDay.put(WWDDsumTimeKey, (Double)0.0);
									}

									// add totals of current record to overall totals
									totals.put(currency, ((Double)totals.get(currency)) + recordTotal);
									if (workAndExpenseRecord.isBillable() != null && workAndExpenseRecord.isBillable().booleanValue()) {
											totalsBillable.put(currency, ((Double)totalsBillable.get(currency)) + recordTotal);
									}
									if (workAndExpenseRecord.isReimbursable() != null && workAndExpenseRecord.isReimbursable().booleanValue()) {
											totalsReimbursable.put(currency, ((Double)totalsReimbursable.get(currency)) + recordTotal);
									}
									if (isWorkRecord && (timeTotal != 0.0)) {
											totals.put(timeKey, ((Double)totals.get(timeKey)) + timeTotal);
											if (workAndExpenseRecord.isBillable() != null && workAndExpenseRecord.isBillable().booleanValue()) {
													totalsBillable.put(timeKey, ((Double)totalsBillable.get(timeKey)) + timeTotal);
											}
											if (workAndExpenseRecord.isReimbursable() != null && workAndExpenseRecord.isReimbursable().booleanValue()) {
													totalsReimbursable.put(timeKey, ((Double)totalsReimbursable.get(timeKey)) + timeTotal);
											}
											totalsPerActivity.put(activityTimeKey, ((Double)totalsPerActivity.get(activityTimeKey)) + timeTotal);
											totalsPerWeekDay.put(WWDDTimeKey, ((Double)totalsPerWeekDay.get(WWDDTimeKey)) + timeTotal);
											totalsPerWeekDay.put(WWDDsumTimeKey, ((Double)totalsPerWeekDay.get(WWDDsumTimeKey)) + timeTotal);
									}
									totalsPerActivity.put(activityKey, ((Double)totalsPerActivity.get(activityKey)) + recordTotal);
									totalsPerWeekDay.put(WWDDKey, ((Double)totalsPerWeekDay.get(WWDDKey)) + recordTotal);
									totalsPerWeekDay.put(WWDDsumKey, ((Double)totalsPerWeekDay.get(WWDDsumKey)) + recordTotal);
							}
					}
					String contactTitle = "--";
					try {
							if (resource != null) {
									contactTitle = (new ObjectReference(resource.getContact(), app)).getTitle();
							}
					} catch (Exception e) {}

					String resHref = "";
					if (resource != null) {
						Action action = new Action(
								Action.EVENT_SELECT_OBJECT,
								new Action.Parameter[]{
								    new Action.Parameter(Action.PARAMETER_OBJECTXRI, resource.refMofId())
								},
								"",
								true // enabled
							);
						resHref = "../../" + action.getEncodedHRef();
						resHref = "<a href='" + resHref + "' target='_blank'>" + (new ObjectReference(resource, app)).getTitle() + "</a>";
					}

					String contactHref = "";
					if (contact != null) {
						Action action = new Action(
								Action.EVENT_SELECT_OBJECT,
								new Action.Parameter[]{
								    new Action.Parameter(Action.PARAMETER_OBJECTXRI, contact.refMofId())
								},
								"",
								true // enabled
							);
						contactHref = "../../" + action.getEncodedHRef();
						contactHref = "<a href='" + contactHref + "' target='_blank'>" + (new ObjectReference(contact, app)).getTitle() + "</a>";
					}

%>
					<hr>
					<h2 style="padding-left:5px;">
						<%= app.getLabel(WORKANDEXPENSERECORD_CLASS) %>	<%= reportBeginOfPeriod != null ? datef.format(reportBeginOfPeriod.getTime()) : "--" %> &mdash; <%= reportEndOfPeriod != null ? datef.format(reportEndOfPeriod.getTime()) : "--" %><br>
					</h2>
					<p style="padding-left:5px;">
						<%= hasMultipleResources
						? (contact != null
								? app.getLabel(CONTACT_CLASS) + ": " + contactHref
								: app.getLabel(RESOURCE_CLASS) + ": *"
							)
						: app.getLabel(RESOURCE_CLASS) + ": " + resHref + "<br>" + app.getLabel(CONTACT_CLASS) + ": " + contactHref %>
					</p>
<%
/*---------------------------------------------------------------------------------------------------------------------
 N O T E :   It is assumed that ALL work records (i.e. WorkAndExpenseRecords with recordType <= 99 have hours as UOM!!!
---------------------------------------------------------------------------------------------------------------------*/

					if (doReportCalculation) {
%>
							<table><tr><td style="padding-left:5px;">
							<table class="gridTable">
								<tr class="gridTableHeader">
									<td class="smallheaderR" colspan="2"><%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "startedAt", app.getCurrentLocaleAsIndex()) %></td>
									<td class="smallheaderR" colspan="2"><%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "endedAt", app.getCurrentLocaleAsIndex()) %></td>
									<td class="smallheaderR"><%= isWorkRecord ? "hh:mm" : userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "quantity", app.getCurrentLocaleAsIndex()) %></td>
<%
									if (!isWorkRecord) {
%>
										<td class="smallheader"><%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "quantityUom", app.getCurrentLocaleAsIndex()) %>&nbsp;</td>
<%
									}
%>
									<td class="smallheader">&nbsp;</td>
									<td class="smallheader">&nbsp;</td>
									<td class="smallheaderR">&sum;</td>
									<td class="smallheader" title="<%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "isBillable", app.getCurrentLocaleAsIndex()) %>">$$&nbsp;</td>
									<td class="smallheader"><%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "name", app.getCurrentLocaleAsIndex()) %>&nbsp;</td>
									<td class="smallheader"><%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "description", app.getCurrentLocaleAsIndex()) %>&nbsp;</td>
									<td class="smallheader"><%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "activity", app.getCurrentLocaleAsIndex()) %>&nbsp;</td>
									<td class="smallheader" title="<%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "isReimbursable", app.getCurrentLocaleAsIndex()) %>">~~&nbsp;</td>
									<td class="smallheader"><%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "recordType", app.getCurrentLocaleAsIndex()) %>&nbsp;</td>
									<td class="smallheader" <%= hasMultipleResources ? "" : "style='display:none;'" %>><%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "resource", app.getCurrentLocaleAsIndex()) %>&nbsp;</td>
								</tr>
<%
								boolean isEvenRow = false;
								int wr = 0;
								for(Iterator i = workReportEntries.values().iterator(); i.hasNext(); wr++) {
										org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord workAndExpenseRecord = (org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord)i.next();

										String recordHref = "";
										Action action = new Action(
												Action.EVENT_SELECT_OBJECT,
												new Action.Parameter[]{
												    new Action.Parameter(Action.PARAMETER_OBJECTXRI, workAndExpenseRecord.refMofId())
												},
												"",
												true // enabled
											);
										recordHref = "../../" + action.getEncodedHRef();

										org.opencrx.kernel.activity1.jmi1.Activity activity = null;
										try {
												activity = workAndExpenseRecord.getActivity();
										} catch (Exception e) {}
										String activityHref = "";
										if (activity != null) {
												action = new Action(
														Action.EVENT_SELECT_OBJECT,
														new Action.Parameter[]{
														    new Action.Parameter(Action.PARAMETER_OBJECTXRI, activity.refMofId())
														},
														"",
														true // enabled
													);
												activityHref = "../../" + action.getEncodedHRef();
										}

										String resourceHref = "";
										if (workAndExpenseRecord.getResource() != null) {
											action = new Action(
													Action.EVENT_SELECT_OBJECT,
													new Action.Parameter[]{
													    new Action.Parameter(Action.PARAMETER_OBJECTXRI, workAndExpenseRecord.getResource().refMofId())
													},
													"",
													true // enabled
												);
											resourceHref = "../../" + action.getEncodedHRef();
										}

										double recordTotal = 0.0;
										boolean quantityError = false;
										try {
												recordTotal = workAndExpenseRecord.getQuantity().doubleValue() * workAndExpenseRecord.getRate().doubleValue();
										} catch (Exception e) {
												quantityError = true;
												totalsError = true;
										}
										if (workAndExpenseRecord.getBillingCurrency() == 0) {
												quantityError = true;
												totalsError = true;
										}

										String currency = (String)(codes.getShortText(featureBillingCurrency, app.getCurrentLocaleAsIndex(), true, true).get(new Short(workAndExpenseRecord.getBillingCurrency())));
%>
										<tr <%=isEvenRow ? "class='even'" : "" %>>
											<td><a href='<%= recordHref %>' target='_blank'><%= workAndExpenseRecord.getStartedAt() != null ? weekdayf.format(workAndExpenseRecord.getStartedAt()) : "--" %>&nbsp;</a></td>
											<td class="padded_r"><a href='<%= recordHref %>' target='_blank'><%= workAndExpenseRecord.getStartedAt() != null ? datetimef.format(workAndExpenseRecord.getStartedAt()) : "--" %></a></td>
											<td><a href='<%= recordHref %>' target='_blank'><%= workAndExpenseRecord.getEndedAt() != null ? weekdayf.format(workAndExpenseRecord.getEndedAt()) : "--" %>&nbsp;</a></td>
											<td class="padded_r"><a href='<%= recordHref %>' target='_blank'><%= workAndExpenseRecord.getEndedAt() != null ? datetimef.format(workAndExpenseRecord.getEndedAt()) : "--" %></a></td>
											<td class="padded_r"><a href='<%= recordHref %>' target='_blank'><%= isWorkRecord ? decimalMinutesToHhMm(workAndExpenseRecord.getQuantity().doubleValue() * 60.0) : quantityf.format(workAndExpenseRecord.getQuantity()) %></a></td>
<%
											if (!isWorkRecord) {
%>
												<td class="padded"><a href='<%= recordHref %>' target='_blank'><%= workAndExpenseRecord.getQuantityUom() != null && workAndExpenseRecord.getQuantityUom().getName() != null ? workAndExpenseRecord.getQuantityUom().getName() : "?" %>&nbsp;</a></td>
<%
											}
%>
											<td class="padded_r"><a href='<%= recordHref %>' target='_blank'>[<%= workAndExpenseRecord.getRate() != null ? ratesepf.format(workAndExpenseRecord.getRate()) : "--" %>]&nbsp;</a></td>
											<td class="padded_r" 		<%= quantityError ? errorStyle : "" %>><a href='<%= recordHref %>' target='_blank'><%= currency %></a></td>
											<td class="padded_r" <%= quantityError ? errorStyle : "" %>><a href='<%= recordHref %>' target='_blank'><%= ratesepf.format(recordTotal) %></a></td>
											<td class="padded"<a href='<%= recordHref %>' target='_blank'><img src="../../images/<%= workAndExpenseRecord.isBillable() != null && workAndExpenseRecord.isBillable().booleanValue() ? "" : "not" %>checked_r.gif" /></a></td>
											<td class="padded"><a href='<%= recordHref %>' target='_blank'><%= workAndExpenseRecord.getName() != null ? workAndExpenseRecord.getName() : "" %></a></td>
											<td class="padded"><a href='<%= recordHref %>' target='_blank'><%= workAndExpenseRecord.getDescription() != null ? workAndExpenseRecord.getDescription() : "" %></a></td>
											<td class="padded"><a href='<%= activityHref %>' target='_blank'>#<%= activity != null ? (new ObjectReference(activity, app)).getTitle() : "--" %>&nbsp;</a></td>
											<td class="padded"<a href='<%= recordHref %>' target='_blank'><img src="../../images/<%= workAndExpenseRecord.isReimbursable() != null && workAndExpenseRecord.isReimbursable().booleanValue() ? "" : "not" %>checked_r.gif" /></a></td>
											<td class="padded"><a href='<%= recordHref %>' target='_blank'><%= (String)(codes.getLongText(featureRecordType, app.getCurrentLocaleAsIndex(), true, true).get(new Short(workAndExpenseRecord.getRecordType()))) %></a></td>
											<td class="padded" <%= hasMultipleResources ? "" : "style='display:none;'" %>><a href='<%= resourceHref %>' target='_blank'><%= workAndExpenseRecord.getResource() != null ? (new ObjectReference(workAndExpenseRecord.getResource(), app)).getTitle() : "" %>&nbsp;</a></td>
										</tr>
<%
										isEvenRow = !isEvenRow;
								}
%>
							</table>
							</td></tr></table>

							<br>
							<table><tr><td style="padding-left:5px;">
							<table class="gridTable">
<!-- totals per week -->
<%
								GregorianCalendar calendarBeginOfWeek = null;
								if (earliestDate != null && latestDate != null) {
									calendarBeginOfWeek = new GregorianCalendar(app.getCurrentLocale());
									calendarBeginOfWeek.setTime(earliestDate);
									while (calendarBeginOfWeek.get(GregorianCalendar.DAY_OF_WEEK) != calendarBeginOfWeek.getFirstDayOfWeek()) {
											calendarBeginOfWeek.add(GregorianCalendar.DAY_OF_MONTH, -1);
									}
								}
%>
								<tr class="gridTableHeader">
									<td class="smallheader"><%= app.getLabel(CALENDAR_CLASS) %></td>
<%
									int dayCounter = 0;
									if (isWorkRecord && calendarBeginOfWeek != null) {
											GregorianCalendar wd = (GregorianCalendar)calendarBeginOfWeek.clone();
											for (int i = wd.get(GregorianCalendar.DAY_OF_WEEK); dayCounter < 7; dayCounter++) {
%>
												<td class="smallheaderR"><%= weekdayf.format(wd.getTime()) %></td>
<%
												wd.add(GregorianCalendar.DAY_OF_MONTH, 1);
											}
									} else {
%>
										<td class="smallheaderR" colspan="7"></td>
<%
									}
									for (Iterator i = totals.keySet().iterator(); i.hasNext();) {
										String key = (String)i.next();
										if (!isWorkRecord && key.compareTo(timeKey) == 0) {continue;}
%>
										<td class="smallheaderR"><%= key %></td>
<%
									}
%>
								</tr>
<%
								double[] sumDays = new double[7 + totals.keySet().size()];
								for(int i = 0; i < sumDays.length; i++) {
										sumDays[i] = 0.0;
								}
								if (calendarBeginOfWeek != null) {
									GregorianCalendar currentDate = (GregorianCalendar)calendarBeginOfWeek.clone();
									while (currentDate.getTime().compareTo(latestDate) < 0) {
%>
										<tr>
											<td class="padded"><%= yyyyf.format(currentDate.getTime()) %> #<%= formatter2.format(currentDate.get(GregorianCalendar.WEEK_OF_YEAR)) %> [<%= datef.format(currentDate.getTime()) %>]</td>
<%
											dayCounter = 0;
											GregorianCalendar beginOfCurrentWeek = (GregorianCalendar)currentDate.clone();
											if (isWorkRecord) {
												for(int i = currentDate.getFirstDayOfWeek(); dayCounter < 7; dayCounter++) {
														String WWDDKey = null; 		// YYYYWWDD where WW week of year and DD day of week
														String WWDDsumKey = null; // YYYYWW99 (sum of week)
														String WWDDTimeKey = null;
														String WWDDsumTimeKey = null;
														try {
																WWDDTimeKey = 	yyyyf.format(currentDate.getTime()) + 
																								formatter2.format(currentDate.get(GregorianCalendar.WEEK_OF_YEAR)) +
																								formatter2.format(currentDate.get(GregorianCalendar.DAY_OF_WEEK) % 7) + "_" + timeKey;
														} catch (Exception e) {
																WWDDTimeKey = "?_" + timeKey;
														}
														boolean outOfPeriod = (reportBeginOfPeriod != null && currentDate.compareTo(reportBeginOfPeriod) < 0) ||
																									(reportEndOfPeriod != null && currentDate.compareTo(reportEndOfPeriod) > 0);
%>
														<td class="padded_r <%= outOfPeriod ? "outofperiod" : "" %>"><%= totalsPerWeekDay.get(WWDDTimeKey) == null ? (outOfPeriod ? "" : "--") : decimalMinutesToHhMm(((Double)totalsPerWeekDay.get(WWDDTimeKey)) * 60.0) %></td>
<%
														sumDays[currentDate.get(GregorianCalendar.DAY_OF_WEEK) % 7] += (totalsPerWeekDay.get(WWDDTimeKey) == null ? 0.0 : (Double)totalsPerWeekDay.get(WWDDTimeKey));
														currentDate.add(GregorianCalendar.DAY_OF_MONTH, 1);
												}
											} else {
%>
												<td class="padded_r" colspan="7">&nbsp;</td>
<%
												currentDate.add(GregorianCalendar.DAY_OF_MONTH, 7);
											}
											int sumIdx = 6;
											for (Iterator i = totals.keySet().iterator(); i.hasNext();) {
													String key = (String)i.next();
													sumIdx++;
													if (!isWorkRecord && key.compareTo(timeKey) == 0) {continue;}
													String sumKey = null; // YYYYWW99 (sum of week)
													try {
															sumKey = yyyyf.format(beginOfCurrentWeek.getTime()) + 
																			formatter2.format(beginOfCurrentWeek.get(GregorianCalendar.WEEK_OF_YEAR)) +
																			"99_" + key;
													} catch (Exception e) {
															sumKey = "9_" + key;
													}
%>
													<td class="padded_r"><%= totalsPerWeekDay.get(sumKey) == null ? "--" : (key.compareTo(timeKey) == 0 ? decimalMinutesToHhMm(60.0 * (Double)totalsPerWeekDay.get(sumKey)) : ratesepf.format((Double)totalsPerWeekDay.get(sumKey))) %></td>
<%
													sumDays[sumIdx] += (totalsPerWeekDay.get(sumKey) == null ? 0.0 : (Double)totalsPerWeekDay.get(sumKey));
											}
%>
										</tr>
<%
									}
								}
%>
								<tr>
<%
									if (isWorkRecord && calendarBeginOfWeek != null) {
%>
										<td class="total">&sum;</td>
<%
											dayCounter = 0;
											for(int i = calendarBeginOfWeek.getFirstDayOfWeek(); dayCounter < 7; dayCounter++) {
%>
												<td class="totalR"><%= decimalMinutesToHhMm(sumDays[i % 7] * 60.0) %></td>
<%
												i++;
											}
									} else {
%>
											<td class="total" colspan="8">&sum;</td>
<%
									}
									int sumIdx = 6;
									for (Iterator i = totals.keySet().iterator(); i.hasNext();) {
											String key = (String)i.next();
											sumIdx++;
											if (!isWorkRecord && key.compareTo(timeKey) == 0) {continue;}
%>
											<td class="totalR"><%= key.compareTo(timeKey) == 0 ? decimalMinutesToHhMm(60.0 * sumDays[sumIdx]) : ratesepf.format(sumDays[sumIdx]) %></td>
<%
									}
%>
								</tr>
<!-- end totals per week -->

<!-- simple totals -->
								<tr>
									<td class="padded" colspan="8">&sum; (<%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "isBillable", app.getCurrentLocaleAsIndex()) %>)</td>
<%
									for (Iterator i = totalsBillable.keySet().iterator(); i.hasNext();) {
										String key = (String)i.next();
										if (!isWorkRecord && key.compareTo(timeKey) == 0) {continue;}
%>
										<td class="padded_r"><%= key.compareTo(timeKey) == 0 ? decimalMinutesToHhMm(60.0 * (Double)totalsBillable.get(key)) : ratesepf.format((Double)totalsBillable.get(key)) %></td>
<%
									}
%>
								</tr>

								<tr>
									<td class="padded" colspan="8">&sum; (<%= userView.getFieldLabel(WORKANDEXPENSERECORD_CLASS, "isReimbursable", app.getCurrentLocaleAsIndex()) %>)</td>
<%
									for (Iterator i = totalsReimbursable.keySet().iterator(); i.hasNext();) {
										String key = (String)i.next();
										if (!isWorkRecord && key.compareTo(timeKey) == 0) {continue;}
%>
										<td class="padded_r"><%= key.compareTo(timeKey) == 0 ? decimalMinutesToHhMm(60.0 * (Double)totalsReimbursable.get(key)) : ratesepf.format((Double)totalsReimbursable.get(key)) %></td>
<%
									}
%>
								</tr>
<!-- end simple totals -->

								<tr>
									<td colspan="<%= totals.keySet().size() + 9 %>" style="padding:5px;">&nbsp;</td>
								</tr>

<!-- totals per activity -->
								<tr class="gridTableHeader">
									<td class="smallheader" colspan="8"><%= app.getLabel(ACTIVITYSEGMENT_CLASS) %></td>
<%
									for (Iterator i = totals.keySet().iterator(); i.hasNext();) {
										String key = (String)i.next();
										if (!isWorkRecord && key.compareTo(timeKey) == 0) {continue;}
%>
										<td class="smallheaderR"><%= key %></td>
<%
									}
%>
								</tr>
<%
								for (Iterator a = activities.keySet().iterator(); a.hasNext();) {
										String activityNumber = (String)a.next();
										org.opencrx.kernel.activity1.jmi1.Activity activity = (org.opencrx.kernel.activity1.jmi1.Activity)pm.getObjectById(new Path((String)activities.get(activityNumber)));
										String activityHref = "";
										Action action = new Action(
												Action.EVENT_SELECT_OBJECT,
												new Action.Parameter[]{
												    new Action.Parameter(Action.PARAMETER_OBJECTXRI, activity.refMofId())
												},
												"",
												true // enabled
											);
										activityHref = "../../" + action.getEncodedHRef();
%>

										<tr>
											<td class="padded" colspan="8"><a href='<%= activityHref %>' target='_blank'>#<%= (new ObjectReference(activity, app)).getTitle() %></a></td>
<%
											for (Iterator i = totals.keySet().iterator(); i.hasNext();) {
												String key = activityNumber + "_" + (String)i.next();
												String activityKey = activityNumber + "_" + timeKey;
												if (!isWorkRecord && key.compareTo(activityKey) == 0) {continue;}
%>
												<td class="padded_r"><%= totalsPerActivity.get(key) == null ? "--" : (key.compareTo(activityKey) == 0 ? decimalMinutesToHhMm(60.0 * (Double)totalsPerActivity.get(key)) : ratesepf.format((Double)totalsPerActivity.get(key))) %></td>
<%
											}
%>
										</tr>
<%
								}
%>
								<tr>
									<td class="total" colspan="8">&sum;</td>
<%
									for (Iterator i = totals.keySet().iterator(); i.hasNext();) {
										String key = (String)i.next();
										if (!isWorkRecord && key.compareTo(timeKey) == 0) {continue;}
%>
										<td class="totalR"><%= key.compareTo(timeKey) == 0 ? decimalMinutesToHhMm(60.0 * (Double)totals.get(key)) : ratesepf.format((Double)totals.get(key)) %></td>
<%
									}
%>
								</tr>
<!--  end totals per activity -->
							</table>
							</td></tr></table>
<%
					} /* doReportCalculation */
%>
				</form>
				<br>
				<script language="javascript" type="text/javascript">
						function setFocus(id) {
							try {
								$(id).focus();
							} catch(e){}
						}
<%
						if (isContactChange)		{ %>setFocus('contactXri.Title');	<% }
						if (isResourceChange)		{ %>setFocus('resourceXri');			<% }
						if (isResourceChange)		{ %>setFocus('resourceXri');			<% }

						if (isFirstCall)			{ %>setFocus('contactXri.Title');	<% }
%>
				</script>



							</div> <!-- inspPanel0 -->
						</div> <!-- inspContent -->
					</div> <!-- inspector -->
				</div> <!-- aPanel -->

      </div> <!-- content -->
    </div> <!-- content-wrap -->
	<div> <!-- wrap -->
</div> <!-- container -->
</body>
</html>
