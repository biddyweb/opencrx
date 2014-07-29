<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:	 openCRX/Core, http://www.opencrx.org/
 * Name:		$Id: VoteForEvent.jsp,v 1.20 2012/07/13 10:08:40 wfro Exp $
 * Description: VoteForEvent
 * Revision:	$Revision: 1.20 $
 * Owner:	   CRIXP Corp., Switzerland, http://www.crixp.com
 * Date:		$Date: 2012/07/13 10:08:40 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2008-2011, CRIXP Corp., Switzerland
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
javax.naming.Context,
org.opencrx.kernel.generic.*,
org.openmdx.base.naming.*,
org.openmdx.portal.servlet.*,
org.openmdx.kernel.id.*,
org.openmdx.kernel.id.cci.*,
org.openmdx.base.text.conversion.*,
java.io.*,
java.util.*,
java.text.*"
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
	public static final int NUM_SLOTS = 8;
	public static final String TIME_MISSING = "0000.000Z@<";
	public static final String SCHEDULE_EVENT_WIZARD_NAME = "ScheduleEventWizard.jsp";
	public static final String WIZARD_NAME = "VoteForEvent.jsp";
	public static final Map resourceBundles = new HashMap();

	public static Properties getResourceBundle(
		Locale locale,
		ServletContext servletContext
	) {
		String bundleId = locale.getLanguage() + "_" + locale.getCountry();
		Properties resourceBundle = (Properties)resourceBundles.get(bundleId);
		if(resourceBundle == null) {
			try {
				java.net.URL bundleURL = servletContext.getResource("/wizards/" + bundleId + "/ScheduleEventWizard.properties");
				InputStream in = bundleURL.openStream();
				resourceBundle = new Properties();
				resourceBundle.loadFromXML(in);
				in.close();
				resourceBundles.put(
					bundleId,
					resourceBundle
				);
			}
			catch(Exception e) {
				try {
					java.net.URL bundleURL = servletContext.getResource("/wizards/en_US/ScheduleEventWizard.properties");
					InputStream in = bundleURL.openStream();
					resourceBundle = new Properties();
					resourceBundle.loadFromXML(in);
					in.close();
					resourceBundles.put(
						bundleId,
						resourceBundle
					);
				} catch(Exception e0) {}
			}
		}
		return resourceBundle;
	}

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
		return
			Integer.toString(year) +
			((month < 10 ? "0" : "") + Integer.toString(month)) +
			((dayOfMonth < 10 ? "0" : "") + Integer.toString(dayOfMonth));
	}

	public static String getSlotId(
		String dateAsString,
		int slotIndex
	) {
		return "calendar.slot." + dateAsString + "." + slotIndex;
	}

	public static GregorianCalendar getDateAsCalendar(
		String dateAsString
	) {
		GregorianCalendar date = new GregorianCalendar();
		date.set(GregorianCalendar.YEAR, Integer.valueOf(dateAsString.substring(0, 4)));
		date.set(GregorianCalendar.MONTH, Integer.valueOf(dateAsString.substring(4, 6)) - 1);
		date.set(GregorianCalendar.DAY_OF_MONTH, Integer.valueOf(dateAsString.substring(6, 8)));
		return date;
	}

	// Slot value is of the form datetimeFrom@location or datetimeFrom-datetimeTo@location
	public static String formatEvent(
		String event,
		SimpleDateFormat timeFormat
	) {
    if(event == null) return "";
    if(event.indexOf(TIME_MISSING)>0) return event.substring(22);
    if (event.length() < 20) return "";
		Date dateFrom = org.w3c.spi2.Datatypes.create(Date.class, event.substring(0, 20));
		Date dateTo = org.w3c.spi2.Datatypes.create(Date.class, event.length() < 41 ? event.substring(0, 20) : event.substring(21, 41));
		return timeFormat.format(dateFrom) + (dateFrom.compareTo(dateTo) == 0 ? "" : "-" + timeFormat.format(dateTo)) + (event.length() < 41 ? event.substring(20) : event.substring(41));
	}

/*
	public static String formatEvent(
		String event,
		SimpleDateFormat timeFormat
	) {
		if((event == null) || (event.length() < 21)) return "";
		Date dateFrom = org.w3c.spi2.Datatypes.create(Date.class, event.substring(0, 20));
		Date dateTo = org.w3c.spi2.Datatypes.create(Date.class, event.length() < 41 ? event.substring(0, 20) : event.substring(21, 41));
		return timeFormat.format(dateFrom) + (dateFrom.compareTo(dateTo) == 0 ? "" : "-" + timeFormat.format(dateTo)) + (event.length() < 41 ? event.substring(20) : event.substring(41));
	}
*/

	public static boolean userIsOwnerOfEMailAddress(
		javax.jdo.PersistenceManager pm,
		org.opencrx.kernel.home1.jmi1.UserHome userHome,
		String emailAddress
	) {
		boolean isOwner = false;
		if(userHome != null) {
		   	org.opencrx.kernel.home1.cci2.EMailAccountQuery emailAccountQuery =
		   		(org.opencrx.kernel.home1.cci2.EMailAccountQuery)pm.newQuery(org.opencrx.kernel.home1.jmi1.EMailAccount.class);
			emailAccountQuery.name().equalTo(emailAddress);
			isOwner = !userHome.getEMailAccount(emailAccountQuery).isEmpty();
			if(!isOwner && userHome.getContact() != null) {
				org.opencrx.kernel.account1.cci2.EMailAddressQuery emailAddressQuery =
			   		(org.opencrx.kernel.account1.cci2.EMailAddressQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.EMailAddress.class);
				emailAddressQuery.thereExistsEmailAddress().equalTo(emailAddress);
				isOwner = !userHome.getContact().getAddress(emailAddressQuery).isEmpty();
			}
		}
		return isOwner;
	}

	public static boolean isMemberOfRecipients(
		javax.jdo.PersistenceManager pm,
		org.opencrx.kernel.activity1.jmi1.EMail email,
		String emailAddress
	) {
		boolean isMember = false;
		for(Iterator i = email.getEmailRecipient().iterator(); i.hasNext(); ) {
			org.opencrx.kernel.activity1.jmi1.EMailRecipient recipient = ( org.opencrx.kernel.activity1.jmi1.EMailRecipient)i.next();
			if(
				(recipient.getParty() instanceof org.opencrx.kernel.account1.jmi1.EMailAddress) &&
				((org.opencrx.kernel.account1.jmi1.EMailAddress)recipient.getParty()).getEmailAddress().equalsIgnoreCase(emailAddress)
			) {
				isMember = true;
				break;
			}
		}
		return isMember;
	}

%>
<%
	// Return redirect page
	if(request.getParameter("tzoffset") == null) {
%>
		<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
		<html>
		<head>
		</head>
		<body>
			<script>
				window.location.href = '<%= request.getRequestURL() + "?" + request.getQueryString() %>' + '&tzoffset=' + -1*new Date().getTimezoneOffset();
			</script
		</body>
		</html>
<%

		return;
	}

	request.setCharacterEncoding("UTF-8");
	String servletPath = "." + request.getServletPath();
	String servletPathPrefix = servletPath.substring(0, servletPath.lastIndexOf("/") + 1);

	// Timezone
	int tzoffset = Integer.valueOf(request.getParameter("tzoffset"));
	ApplicationContext app = (ApplicationContext)session.getValue(WebKeys.APPLICATION_KEY);
	TimeZone timezone = app == null ?
		TimeZone.getTimeZone(
			"GMT" +
			(tzoffset >= 0 ? "+" : "-") +
			(Math.abs(tzoffset/60) < 10 ? "0" + Math.abs(tzoffset)/60 : "" + Math.abs(tzoffset)/60) +
			(Math.abs(tzoffset)%60 < 10 ? "0" + Math.abs(tzoffset)%60 : "" + Math.abs(tzoffset)%60)
		) :
		TimeZone.getTimeZone(app.getCurrentTimeZone());
	tzoffset = (int)(timezone.getRawOffset() / 60000L);

	// try to get locale from browser header or - if authenticated - from application context
	String localeStr = (app == null
		?	((request.getHeader("accept-language") == null || request.getHeader("accept-language").length()<5)
				? null
				: request.getHeader("accept-language").substring(0,2) + "_" + request.getHeader("accept-language").substring(3)
			)
		: app.getCurrentLocaleAsString()
	);

	// override locale if submitted as parameter
	if ((request.getParameter("locale") != null) && (request.getParameter("locale").length() > 4)) {
		localeStr = request.getParameter("locale");
	}

	// verify whether chosen locale is supported (set to default locale if necessary)
	String defaultLocale = "en_US";
	List activeLocales = new ArrayList();
%><%@ include file="login-locales.jsp" %><%
	if(!activeLocales.contains(defaultLocale)) {
		activeLocales.add(defaultLocale);
	}
	if((localeStr == null) || !activeLocales.contains(localeStr)) {
		localeStr = defaultLocale;
	}
	Locale locale = new Locale(
		localeStr.substring(0,2),
		localeStr.substring(3,5)
	);

	Properties bundle = getResourceBundle(
		locale,
		this.getServletContext()
	);

	// Date/Time formatters
	SimpleDateFormat timeFormat = app == null ?
		new SimpleDateFormat("HH:mm") :
		new SimpleDateFormat("HH:mm", locale);
	timeFormat.setTimeZone(timezone);

	// Get parameters
	boolean actionVote = request.getParameter("Vote") != null;
	boolean actionRemoveVote = request.getParameter("RemoveVote") != null;
	boolean hasVoted = actionVote || ((request.getParameter("hasVoted") != null) && (request.getParameter("hasVoted").length() > 0));
	String id = request.getParameter("id");
	String[] ids = id == null ?
 		new String[]{} :
		id.split("/");
	String providerName = ids.length > 0 ? ids[0] : null;
	String segmentName = ids.length > 1 ? ids[1] : null;
	String activityId = ids.length > 2 ? ids[2] : null;
	String voteId = ids.length > 3 ? ids[3] : null;
	javax.jdo.PersistenceManagerFactory pmf = org.opencrx.kernel.utils.Utils.getPersistenceManagerFactory();
	javax.jdo.PersistenceManager pm = pmf.getPersistenceManager(
		app == null ?
			org.opencrx.kernel.generic.SecurityKeys.ADMIN_PRINCIPAL + org.opencrx.kernel.generic.SecurityKeys.ID_SEPARATOR + segmentName :
			app.getLoginPrincipal(),
		null
	);
	// Get user home if user is authenticated
	org.opencrx.kernel.home1.jmi1.UserHome userHome = null;
	try {
		if(app != null) {
			userHome = (org.opencrx.kernel.home1.jmi1.UserHome)pm.getObjectById(
				app.getUserHomeIdentityAsPath()
			);
		}
	} catch(Exception e) {}
	org.opencrx.kernel.account1.jmi1.EMailAddress userEMailAddress = null;
	if((userHome != null) && (userHome.getContact() != null)) {
		org.opencrx.kernel.account1.jmi1.AccountAddress[] addresses = org.opencrx.kernel.backend.Accounts.getInstance().getMainAddresses(
			userHome.getContact()
		);
		userEMailAddress = (org.opencrx.kernel.account1.jmi1.EMailAddress)addresses[org.opencrx.kernel.backend.Accounts.MAIL_HOME];
		if (userEMailAddress == null) {
			userEMailAddress = (org.opencrx.kernel.account1.jmi1.EMailAddress)addresses[org.opencrx.kernel.backend.Accounts.MAIL_BUSINESS];
		}
	}
	org.opencrx.kernel.activity1.jmi1.Activity activity = null;
	try {
		if(activityId != null) {
			activity = (org.opencrx.kernel.activity1.jmi1.Activity)pm.getObjectById(
				new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName + "/activity/" + activityId)
			);
		}
	} catch(Exception e) {}

	// Try to get voter name from existing vote
	String retrievedVoter = null;
	if(voteId != null && activityId != null && providerName != null && segmentName != null) {
		try {
			org.opencrx.kernel.activity1.jmi1.ActivityVote activityVote =
				(org.opencrx.kernel.activity1.jmi1.ActivityVote)pm.getObjectById(
					new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName + "/activity/" + activityId + "/vote/" + voteId)
				);
			retrievedVoter = activityVote.getName();
		} catch(Exception e) {}
	}

	boolean allowVoting = false;
	boolean allowEdit = false;

	// Get schedule
	Map formValues = new HashMap();
	List selectedDates = new ArrayList();
	if(activity != null && activity instanceof org.opencrx.kernel.activity1.jmi1.EMail) {
		org.opencrx.kernel.activity1.jmi1.EMail email = (org.opencrx.kernel.activity1.jmi1.EMail)activity;
		for(Iterator i = email.getNote().iterator(); i.hasNext(); ) {
			org.opencrx.kernel.generic.jmi1.Note note = (org.opencrx.kernel.generic.jmi1.Note)i.next();
			if(SCHEDULE_EVENT_WIZARD_NAME.equals(note.getTitle())) {
				Properties eventAndPollInfo = new Properties();
				ByteArrayInputStream in = new ByteArrayInputStream(note.getText().getBytes("UTF-8"));
				eventAndPollInfo.loadFromXML(in);
				in.close();
				formValues.put(
					"isClosedGroupPoll",
					Boolean.valueOf(eventAndPollInfo.getProperty("isClosedGroupPoll", "false"))
				);
				formValues.put(
					"isHiddenPoll",
					Boolean.valueOf(eventAndPollInfo.getProperty("isHiddenPoll", "false"))
				);
				formValues.put(
					"isYesNoPoll",
					Boolean.valueOf(eventAndPollInfo.getProperty("isYesNoPoll", "true"))
				);
				formValues.put(
						"isLimitTo1Poll",
						Boolean.valueOf(eventAndPollInfo.getProperty("isLimitTo1Poll", "false"))
					);
				// Get sorted events
				Map slots = new TreeMap();
				Enumeration propertyNames = eventAndPollInfo.propertyNames();
				GregorianCalendar date = new GregorianCalendar();
				date.setTimeZone(timezone);
				org.w3c.format.DateTimeFormat dateFormat = org.w3c.format.DateTimeFormat.BASIC_UTC_FORMAT;
				while(propertyNames.hasMoreElements()) {
					String propertyName = (String)propertyNames.nextElement();
					if(propertyName.startsWith("event.")) {
						String event = eventAndPollInfo.getProperty(propertyName);
						date.setTime(dateFormat.parse(event.substring(0, 20)));
						String dateAsString = getDateAsString(date);
						if(slots.get(dateAsString) == null) {
							slots.put(
								dateAsString,
								new TreeSet()
							);
						}
						((Set)slots.get(dateAsString)).add(event);
					}
				}
				selectedDates = new ArrayList(slots.keySet());
				formValues.put(
					"calendar.selectedDates",
					selectedDates
				);
				for(Iterator j = selectedDates.iterator(); j.hasNext(); ) {
					String dateAsString = (String)j.next();
					int kk = 0;
					for(Iterator k = ((Set)slots.get(dateAsString)).iterator(); k.hasNext(); kk++) {
						String event = (String)k.next();
						formValues.put(
							getSlotId(dateAsString, kk),
							event
						);
					}
				}
			}
		}

		String voter = request.getParameter("voter") != null
			? request.getParameter("voter")
			: retrievedVoter;
		org.opencrx.kernel.activity1.jmi1.ActivityVote vote = null;

		if(
			(voter != null) &&
			(voter.length() > 0)
		) {
			voter = voter.trim();
			Boolean isClosedGroupPoll = (Boolean)formValues.get("isClosedGroupPoll");
			allowVoting =
				(isClosedGroupPoll == null) ||
				!isClosedGroupPoll ||
				isMemberOfRecipients(pm, email, voter);
			if(allowVoting) {
				org.opencrx.kernel.activity1.cci2.ActivityVoteQuery voteQuery =
					(org.opencrx.kernel.activity1.cci2.ActivityVoteQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityVote.class);
				voteQuery.name().equalTo(voter);
				if(!email.getVote(voteQuery).isEmpty()) {
					vote = (org.opencrx.kernel.activity1.jmi1.ActivityVote)email.getVote(voteQuery).iterator().next();
				}
				allowEdit =
					(voteId != null && vote != null && voteId.equals(vote.refGetPath().getBase()))  ||
					userIsOwnerOfEMailAddress(pm, userHome, voter);
			}
		}

		if(actionVote || actionRemoveVote) {
			if (
				actionRemoveVote &&
				allowEdit
			) {
				// remove existing vote
				pm.currentTransaction().begin();
				vote.refDelete();
				try {
					pm.currentTransaction().commit();
				}
				catch(Exception e) {
					try {
						pm.currentTransaction().rollback();
					} catch(Exception e1) {}
					new org.openmdx.base.exception.ServiceException(e).log();
				}
				voteId = null;
				hasVoted = false;
				allowEdit = false;
				retrievedVoter = null;
				/*
				String providerName = ids.length > 0 ? ids[0] : null;
				String segmentName = ids.length > 1 ? ids[1] : null;
				String activityId = ids.length > 2 ? ids[2] : null;
				String voteId = ids.length > 3 ? ids[3] : null;
				*/

			}

			if(
				actionVote &&
				allowVoting &&
				(allowEdit || (vote == null))
			) {
				pm.currentTransaction().begin();
				if(vote == null) {
					// create new vote
					vote = pm.newInstance(org.opencrx.kernel.activity1.jmi1.ActivityVote.class);
					vote.refInitialize(false, false);
					email.addVote(
						UUIDConversion.toUID(UUIDs.newUUID()),
						vote
					);
					vote.getOwningGroup().addAll(
						email.getOwningGroup()
					);
				}
				// update existing (or newly created) vote
				vote.setName(voter);
				Properties votes = new Properties();
				Enumeration parameterNames = request.getParameterNames();
				Boolean isLimitTo1Poll = (Boolean)formValues.get("isLimitTo1Poll");
				Integer maxVotesPerParticipant = (isLimitTo1Poll != null && isLimitTo1Poll)
					? 1
					: null;

				int voteCounter = 0;
				while(parameterNames.hasMoreElements()) {
					String parameterName = (String)parameterNames.nextElement();
					if(parameterName.startsWith("vote.")) {
						String value = request.getParameter(parameterName);
						if(/* (!"0".equals(value)) && */ (maxVotesPerParticipant == null || voteCounter < maxVotesPerParticipant)) {
							votes.put(
								parameterName.substring(5),
								value
							);
							voteCounter++;
						}
					}
				}
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				votes.storeToXML(os, voter);
				os.close();
				vote.setDescription(
					new String(os.toByteArray(), "UTF-8")
				);
				try {
					pm.currentTransaction().commit();
					voteId = vote.refGetPath().getBase();
					id = providerName + "/" + segmentName + "/" + activityId + "/" + voteId;
					allowEdit = true;
				}
				catch(Exception e) {
					try {
						pm.currentTransaction().rollback();
					} catch(Exception e1) {}
					new org.openmdx.base.exception.ServiceException(e).log();
				}
			}
		}
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>openCRX - Vote for Event</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link href="<%=request.getContextPath()%>/_style/colors.css" rel="stylesheet" type="text/css">
	<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/javascript/portal-all.js"></script>
	<script language="javascript" type="text/javascript" src="javascript/tristate/tristate.js"></script>
	<!--[if lt IE 7]><script type="text/javascript" src="<%=request.getContextPath()%>/javascript/iehover-fix.js"></script><![endif]-->
	<link href="<%=request.getContextPath()%>/_style/ssf.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath()%>/_style/n2default.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath()%>/javascript/yui/build/assets/skins/sam/container.css" rel="stylesheet" type="text/css">
	<link rel='shortcut icon' href='<%=request.getContextPath()%>/images/favicon.ico' />

	<style>
	fieldset {background-color:#F2F2F2;;margin:10px 0px 10px 0px;}
	legend span {font-size:14px;vertical-align:baseline;}
		td.ticks {
			vertical-align:middle;
			text-align:right;
			padding-right:20px;
		}
		td.ticks img {
			vertical-align:top;
		}
		td.ownVote {
			background-color:#F9FB95;
			text-align:center;
		}
		td.results {
			background-color:#F9FB95;
			border-top:1px solid grey;
			padding-top:5px;
			padding-bottom:5px;
		}
		td.vote {
			background-color:#80FF80;
			padding-top:5px;
			padding-bottom:5px;
		}
		#resultRowTop TD, #resultRowTop TH{
			border-bottom:1px solid #ccc;
		}
	</style>
</head>
<body>
<div id="container">
<div id="wrap">
	<div id="header" style="height:90px;">
		<div id="logoTable">
			<table id="headerlayout">
			  <tr id="headRow">
				<td id="head" colspan="2">
				  <table id="info">
					<tr>
					  <td id="headerCellLeft"><img id="logoLeft" src="images/logoLeft.gif" alt="openCRX" title="" /></td>
					  <td id="headerCellSpacerLeft"></td>
					  <td id="headerCellMiddle">&nbsp;</td>
					  <td id="headerCellRight"><img id="logoRight" src="images/logoRight.gif" alt="" title="" /></td>
					</tr>
				  </table>
				</td>
			  </tr>
			</table>
		</div>
	</div>
	<div id="content-wrap">
		<div id="content" style="padding:100px 0.5em 0px 0.5em;">
			<div id="etitle" style="height:20px;">
					<div id="printButton" style="background-color:transparent;" onClick="javascript:yuiPrint();">&nbsp;</div>
					<div style="float:right;">
						<ul dir="ltr" id="<%=CssClass.ssfNav %>" class="<%=CssClass.ssfNav %>" style="width:220px;" onmouseover="sfinit(this);">
							<li id="flyout" style="border-top: solid 1px #DDDDDD;border-bottom: solid 1px #DDDDDD;"><a href="#"><img src="<%=request.getContextPath()%>/images/panel_down.gif" alt="" style="border:none 0px white;float:right;top:-20px;" /><%= localeStr %> - <%= textsLocale.get(localeStr) %>&nbsp;</a>
								<ul onclick="this.style.left='-999em';" onmouseout="this.style.left='';">
<%
									for (int i = 0; i < activeLocales.size(); i++) {
%>
										<li><a href="#" onclick="javascript:$('locale').value='<%= activeLocales.get(i).toString() %>';$('Refresh').click();"><span style="font-family:courier;"><%= activeLocales.get(i).toString() %>&nbsp;&nbsp;</span><%= textsLocale.get(activeLocales.get(i)).toString() %></a></li>
<%
									}
%>
								</ul>
							</li>
						</ul>
					</div>
			  #<%= activity == null ? "" : activity.getActivityNumber() %> / <%= bundle.get("VoteForLabel") %>: <%= activity == null ? "NA" : activity.getName() %>
			</div>
<%
  			if(activity instanceof org.opencrx.kernel.activity1.jmi1.EMail) {
				org.opencrx.kernel.activity1.jmi1.EMail email = (org.opencrx.kernel.activity1.jmi1.EMail)activity;
				if((email.getSender() != null) && (email.getSender() instanceof org.opencrx.kernel.account1.jmi1.EMailAddress)) {
%>
					  <p><%= bundle.get("PollCreatedByLabel") %> <b><%= ((org.opencrx.kernel.account1.jmi1.EMailAddress)email.getSender()).getEmailAddress() %></b></p>
<%
				}
				if(app != null) {
%>
					  <p><%= bundle.get("YouAreLoggedInAsLabel") %> <b><%= app.getLoginPrincipal() %></b></p>
<%
				}
				int tabIndex = 0;
				final int MAXLEN_EMAILADDRESS = 50;
%>
				  <form id="<%= WIZARD_NAME %>" name="<%= WIZARD_NAME %>" method="post" accept-charset="UTF-8" action="<%= servletPath %>">
						<input type="hidden" id="id" name="id" value="<%= id %>" />
						<input type="hidden" id="tzoffset" name="tzoffset" value="<%= tzoffset %>" />
						<input type="hidden" id="locale" name="locale" value="<%= localeStr %>" style="display:none;" />
						<input type="text" id="hasVoted" name="hasVoted" value="<%= hasVoted ? "hasVoted" : "" %>" style="display:none;" />
						<!-- Votes -->
						<fieldset>
							<legend><span><%= bundle.get("YourVoteLabel") %></span></legend>
 							<table cellspacing="1">
 								<tr>
 									<td />
 									<th />
<%
 									// Table head for Dates
 									for(int i = 0; i < selectedDates.size(); i++) {
										String dateAsString = (String)selectedDates.get(i);
										int colspan = 0;
 										for(int j = 0; j < NUM_SLOTS; j++) {
 											String slotId = getSlotId(dateAsString, j);
 											String event = (String)formValues.get(slotId);
 											if((event != null) && (event.length() > 0)) {
 												colspan++;
 											}
 										}
 										GregorianCalendar date = getDateAsCalendar(dateAsString);
%>
										<th style="background-color: lightblue" colspan="<%= colspan %>">&nbsp;<%= new SimpleDateFormat("EEE, MMMM d, yyyy", locale).format(date.getTime()) %>&nbsp;</th>
<%
 									}
%>
 								</tr>
 								<tr>
 									<th />
 									<th />
<%
 									// Table head for Slots
 									int nSlots = 0;
 									for(int i = 0; i < selectedDates.size(); i++) {
 										String dateAsString = (String)selectedDates.get(i);
 										for(int j = 0; j < NUM_SLOTS; j++) {
 											String slotId = getSlotId(dateAsString, j);
 											String event = (String)formValues.get(slotId);
 											if(event != null && event.length() > 0) {
%>
												<th><input type="button" disabled value="<%= formatEvent((String)formValues.get(slotId), timeFormat) %>"</th>
<%
												nSlots++;
											}
										}
									}
%>
 								</tr>
<%
								if (hasVoted && (request.getParameter("voter") != null && request.getParameter("voter").length() > 0)) {
	 								org.opencrx.kernel.activity1.cci2.ActivityVoteQuery voteQuery =
	 									(org.opencrx.kernel.activity1.cci2.ActivityVoteQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityVote.class);
	 								voteQuery.name().equalTo(request.getParameter("voter"));
	 								voteQuery.orderByName().ascending();
	 								for(Iterator i = email.getVote(voteQuery).iterator(); i.hasNext();) {
	 									org.opencrx.kernel.activity1.jmi1.ActivityVote vote = (org.opencrx.kernel.activity1.jmi1.ActivityVote)i.next();
	 									ByteArrayInputStream in = new ByteArrayInputStream(vote.getDescription().getBytes("UTF-8"));
	 									Properties votes = new Properties();
	 									votes.loadFromXML(in);
	 									in.close();
%>
 										<tr>
 											<td><img src="images/spacer.gif" height="30" width="0"/></td>
		 									<td align="left" nowrap><b><%= bundle.get("YourVoteLabel") %>:&nbsp;</b></td>
<%
 											for(int j = 0; j < selectedDates.size(); j++) {
	 											String dateAsString = (String)selectedDates.get(j);
	 											for(int k = 0; k < NUM_SLOTS; k++) {
	 												String slotId = getSlotId(dateAsString, k);
	 												String event = (String)formValues.get(slotId);
	 												  if((event != null && (event.length() > 0))) {
%>
	 													  <td class="ownVote">
<%
	   														if (votes.get(event) != null) {
%>
	   															<img src="images/<%= ("2".equals(votes.get(event)) || "true".equals(votes.get(event)) || "on".equals(votes.get(event))) ? "checked" : (("1".equals(votes.get(event))) ? "ifneedbe" : "notchecked") %>.gif" />
<%
   															}
%>
	   												 	</td>
<%
													}
												}
											}
%>
 										</tr>
<%
 									}
								}

								String jsResetAll = "";
								Boolean isYesNoPoll = (Boolean)formValues.get("isYesNoPoll");
								if (!hasVoted) {
%>
	 								<!-- Vote input -->
	 								<tr>
	 									<td />
	 									<td align="left" nowrap><b><%= bundle.get("YourVoteLabel") %>:&nbsp;</b></td>
<%
										Boolean isLimitTo1Poll = (Boolean)formValues.get("isLimitTo1Poll");
	 									for(int j = 0; j < selectedDates.size(); j++) {
	 										String dateAsString = (String)selectedDates.get(j);
	 										for(int k = 0; k < NUM_SLOTS; k++) {
	 											String slotId = getSlotId(dateAsString, k);
	 											String event = (String)formValues.get(slotId);
	 											if((event != null && (event.length() > 0))) {
	 												if((isYesNoPoll != null && isYesNoPoll) || (isLimitTo1Poll != null && isLimitTo1Poll)) {
	 													jsResetAll += "$('vote." + event + "').checked = false;";
%>
	 													<td class="vote" align="center"><input <%= (isLimitTo1Poll != null && isLimitTo1Poll) ? "type=\"radio\" onClick=\"javascript:resetAll();this.checked=true;\"" : "type=\"checkbox\"" %>" name="<%= "vote." + event %>" id="<%= "vote." + event %>"/></td>
<%
	 												}
	 												else {
%>
														<td class="vote" align="center"><span id="<%= "vote." + event %>.tristateBox" style="cursor:default;"><input type="hidden" name="<%= "vote." + event %>" id="<%= "vote." + event %>" value="0"/></span></td>
														<script type="text/javascript">
															initTriStateCheckBox('<%= "vote." + event %>.tristateBox', '<%= "vote." + event %>', true);
														</script>
<%
	 												}
	 											}
	 										}
	 									}
%>
									</tr>
<%
								}
%>
 								<!-- Spacer -->
 								<tr>
 									<td />
 									<td align="left" nowrap title="<%= (app != null) ? " [" + app.getLoginPrincipal() + "]" : "" %>"><b><%= bundle.get("VoterLabel") %>:&nbsp;</b></td>
 									<td colspan="<%= nSlots %>">
										<input type="text" size="40" <%= hasVoted ? "readonly" : "" %> maxlength="<%= MAXLEN_EMAILADDRESS %>" name="voter" id="voter" value="<%= request.getParameter("voter") == null ? (retrievedVoter == null ? ((userEMailAddress != null) && (userEMailAddress.getEmailAddress() != null) ? userEMailAddress.getEmailAddress() : "") : retrievedVoter) : request.getParameter("voter") %>" />
										<input id="Vote.Button" name="Vote" type="submit" <%= hasVoted ? "disabled" : "class=\"<%= CssClass.btn.toString() + " " + CssClass.btnDefault.toString() %>\"" %> tabindex="<%= tabIndex++ %>" value="<%= bundle.get("VoteLabel") %>" />
										<input id="RemoveVote.Button" name="RemoveVote" type="submit" <%= hasVoted && allowEdit ? "" : "style='display:none;'" %> class="<%= CssClass.btn.toString() + " " + CssClass.btnDefault.toString() %>" tabindex="<%= tabIndex++ %>" value="<%= bundle.get("RemoveVoteLabel") %>" />
 									</td>
 								</tr>
 							</table>
							<script language="javascript" type="text/javascript">
							  function resetAll(){
								  <%= jsResetAll %>
							  }
							</script>
 						</fieldset>

						<div>&nbsp;</div>
<%
						if(voteId != null) {
							String link = request.getRequestURL() + "?id=" + id + "&locale=" + localeStr;
							String emailAddress = request.getParameter("voter") == null ? (retrievedVoter == null ? ((userEMailAddress != null) && (userEMailAddress.getEmailAddress() != null) ? userEMailAddress.getEmailAddress() : "") : retrievedVoter) : request.getParameter("voter");
							String subject = java.net.URLEncoder.encode(bundle.get("VoteForLabel") + ": " + (activity == null || activity.getName() == null ? "NA" : activity.getName()), "UTF-8").replace("+", "%20");
							String body = java.net.URLEncoder.encode(link, "UTF-8").replace("+", "%20");
%>
							<fieldset>
								<legend><span><%= bundle.get("VoteInformationLabel") %></span></legend>
								<p><%= bundle.get("EditVoteLabel") %>:<br>
								<a href="<%= link %>"><b><%= link %></b></a></p>
								<p><%= bundle.get("MailVoteLabel") %>:
								<a href="mailto:<%= emailAddress + "?subject=" + subject + "&body=" + body %>" title="<%= bundle.get("MailVoteLabel") %>"><b><%= emailAddress %></b></a></p>
							</fieldset>
							<div>&nbsp;</div>
<%
						}
%>
 						<fieldset>
							<legend><span><%= bundle.get("VotesLabel") %></span></legend>
 							<table cellspacing="1">
 								<tr>
 									<td />
 									<th align="left"><input type="submit" id="Refresh" name="Refresh" class="<%= CssClass.btn.toString() + " " + CssClass.btnDefault.toString() %>" value="<%= bundle.get("RefreshLabel") %>"/></th>
<%
 									// Table head for Dates
 									for(int i = 0; i < selectedDates.size(); i++) {
										String dateAsString = (String)selectedDates.get(i);
										int colspan = 0;
 										for(int j = 0; j < NUM_SLOTS; j++) {
 											String slotId = getSlotId(dateAsString, j);
 											String event = (String)formValues.get(slotId);
 											if((event != null) && (event.length() > 0)) {
 												colspan++;
 											}
 										}
 										GregorianCalendar date = getDateAsCalendar(dateAsString);
%>
										<th style="background-color: lightblue" colspan="<%= colspan %>">&nbsp;<%= new SimpleDateFormat("EEE, MMMM d, yyyy", locale).format(date.getTime()) %>&nbsp;</th>
<%
 									}
%>
 								</tr>
 								<tr>
 									<th />
 									<th />
<%
 									// Table head for Slots
 									for(int i = 0; i < selectedDates.size(); i++) {
 										String dateAsString = (String)selectedDates.get(i);
 										for(int j = 0; j < NUM_SLOTS; j++) {
 											String slotId = getSlotId(dateAsString, j);
 											String event = (String)formValues.get(slotId);
 											if(event != null && event.length() > 0) {
%>
												<th><input type="button" disabled value="<%= formatEvent((String)formValues.get(slotId), timeFormat) %>"</th>
<%
										  }
									  }
								  }
%>
 								</tr>
 								<tr id="resultRowTop">
 									<th />
									<th align="right">
										<%= bundle.get("yesLabel") %>&nbsp;<br>
										<%= ((Boolean)formValues.get("isYesNoPoll")).booleanValue() ? "" : bundle.get("ifneedbeLabel") + "&nbsp;<br>" %>
										<%= bundle.get("noLabel") %>&nbsp;
									</th>
<%
									List yesVotes = new ArrayList();
									List noVotes = new ArrayList();
									List ifneedbeVotes = new ArrayList();
									// Place holders for total votes
									for(int i = 0; i < nSlots; i++) {
										yesVotes.add(new Integer(0));
										noVotes.add(new Integer(0));
										ifneedbeVotes.add(new Integer(0));
%>
										<td class="results ticks"><span id="slot<%= i %>Votes"></span></td>
<%
									}
%>
								</tr>
<%
 								Boolean isHiddenPoll = (Boolean)formValues.get("isHiddenPoll");
 								boolean showVote = false;
 								int ii = 0;
 								org.opencrx.kernel.activity1.cci2.ActivityVoteQuery voteQuery =
 									(org.opencrx.kernel.activity1.cci2.ActivityVoteQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityVote.class);
 								voteQuery.orderByName().ascending();
 								for(Iterator i = email.getVote(voteQuery).iterator(); i.hasNext(); ii++) {
 									org.opencrx.kernel.activity1.jmi1.ActivityVote vote = (org.opencrx.kernel.activity1.jmi1.ActivityVote)i.next();
 									ByteArrayInputStream in = new ByteArrayInputStream(vote.getDescription().getBytes("UTF-8"));
 									Properties votes = new Properties();
 									votes.loadFromXML(in);
 									in.close();
 									showVote =
 										(isHiddenPoll == null) ||
 										!isHiddenPoll ||
 										(voteId != null && voteId.equals(vote.refGetPath().getBase())) ||
 										userIsOwnerOfEMailAddress(pm, userHome, vote.getName());
 									if(showVote) {
%>
 										<tr <%= ii % 2 == 0 ? "style='background-color:#F4F4F4;'" : "" %>>
 											<td><img src="images/spacer.gif" height="20" width="0"/></td>
 											<td style="vertical-align:middle;"><%= vote.getName() %></td>
<%
 											int nSlot = 0;
 											for(int j = 0; j < selectedDates.size(); j++) {
 											String dateAsString = (String)selectedDates.get(j);
 											for(int k = 0; k < NUM_SLOTS; k++) {
 												String slotId = getSlotId(dateAsString, k);
 												String event = (String)formValues.get(slotId);
 												  if((event != null && (event.length() > 0))) {
%>
 													  <td class="ticks">
<%
   														if (votes.get(event) != null) {
   															if ("2".equals(votes.get(event)) || "true".equals(votes.get(event)) || "on".equals(votes.get(event))) {
   																yesVotes.set(nSlot, ((Integer)yesVotes.get(nSlot))+1);
%>
   																<%= "<img src=\"images/checked.gif\"/>" %>
<%
   															}
   															else if ("1".equals(votes.get(event))) {
   																ifneedbeVotes.set(nSlot, ((Integer)ifneedbeVotes.get(nSlot))+1);
%>
   																<%= "<img src=\"images/ifneedbe.gif\"/>" %>
<%
   															}
   															else {
   																noVotes.set(nSlot, ((Integer)noVotes.get(nSlot))+1);
%>
   																<%= "<img src=\"images/notchecked.gif\"/>" %>
<%
   															}
   														}
%>
   												 	</td>
<%
   													nSlot++;
												}
											}
										}
%>
 										</tr>
<%
 									}
 								}
 								if(showVote) {
%>
 									<tr id="resultRowBottom" style="display:none;">
 										<td />
										<th align="right">
											<%= bundle.get("yesLabel") %>&nbsp;<br>
											<%= ((Boolean)formValues.get("isYesNoPoll")).booleanValue() ? "" : bundle.get("ifneedbeLabel") + "&nbsp;<br>" %>
											<%= bundle.get("noLabel") %>&nbsp;
										</th>
<%
 											// Place holders for total votes
 											for(int i = 0; i < nSlots; i++) {
 												String results =
 													"<div><b>" + ((Integer)yesVotes.get(i) == 0 ? "&nbsp;" : (Integer)yesVotes.get(i)) + " </b><img src=\"images/checked.gif\"/></div>" +
 													(((Boolean)formValues.get("isYesNoPoll")).booleanValue()
 															? ""
 															: "<div><b>" + ((Integer)ifneedbeVotes.get(i) == 0 ? "&nbsp;" : (Integer)ifneedbeVotes.get(i)) + " </b><img src=\"images/ifneedbe.gif\"/></div>"
 														) +
 													"<div><b>" + ((Integer)noVotes.get(i) == 0 ? "&nbsp;" : (Integer)noVotes.get(i)) + " </b><img src=\"images/notchecked.gif\"/></div>";
%>
 												<td class="results ticks">
 													<script language="javascript" type="text/javascript">
 														$('slot<%= i %>Votes').replace('<%= results %>');
 													</script>
 													<%= results %>
 												</td>
<%
 											}
%>
 									</tr>
<%
 								}
%>
							</table>
							<div>&nbsp;</div>
						</fieldset>
						<div>&nbsp;</div>
					</form>
<%
				}
%>

			</div> <!-- content -->
		</div> <!-- content-wrap -->
	<div> <!-- wrap -->
</div> <!-- container -->
</body>
</html>
