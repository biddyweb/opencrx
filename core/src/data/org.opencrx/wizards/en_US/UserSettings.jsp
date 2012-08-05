<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: UserSettings.jsp,v 1.37 2008/10/01 14:24:31 wfro Exp $
 * Description: UserSettings
 * Revision:    $Revision: 1.37 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/10/01 14:24:31 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2005-2008, CRIXP Corp., Switzerland
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
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.texts.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.reports.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.compatibility.base.naming.*,
org.openmdx.compatibility.base.dataprovider.cci.*,
org.openmdx.application.log.*,
org.opencrx.kernel.backend.*,
org.opencrx.kernel.layer.application.*,
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
org.openmdx.base.exception.*
" %>

<%!

	//-----------------------------------------------------------------------
	private static org.opencrx.kernel.activity1.jmi1.Resource findResource(
		javax.jdo.PersistenceManager pm,
		org.opencrx.kernel.activity1.jmi1.Activity1Package activityPkg,
		org.opencrx.kernel.activity1.jmi1.Segment activitySegment,
		org.opencrx.kernel.home1.jmi1.UserHome userHome,
		String providerName,
		String segmentName
	) {
		// Resource
		org.opencrx.kernel.activity1.jmi1.Resource resource = null;
		try {
			resource = (org.opencrx.kernel.activity1.jmi1.Resource)pm.getObjectById(
				new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName + "/resource/" + userHome.refGetPath().getBase())
			);
		}
		catch(Exception e) {}
		if(resource == null) {
			try {
				org.opencrx.kernel.activity1.cci2.ResourceQuery query = activityPkg.createResourceQuery();
				query.thereExistsContact().equalTo(userHome.getContact());
				query.orderByName().ascending();
				Collection resources = activitySegment.getResource(query);
				for(Iterator i = resources.iterator(); i.hasNext(); ) {
					org.opencrx.kernel.activity1.jmi1.Resource candidate = (org.opencrx.kernel.activity1.jmi1.Resource)i.next();
					if(
						candidate.getName().equals(userHome.refGetPath().getBase()) ||
						candidate.getName().equals(userHome.getContact().getFullName())
					) {
						resource = candidate;
						break;
					}
				}
				if(resource == null) {
					resource = (org.opencrx.kernel.activity1.jmi1.Resource)resources.iterator().next();
				}
			}
			catch(Exception e) {}
		}
		return resource;
	}
%>

<%

	//-----------------------------------------------------------------------
	final String WIZARD_NAME = "UserSettings.jsp";

	//-----------------------------------------------------------------------
	// Init
	request.setCharacterEncoding("UTF-8");
	ApplicationContext app = (ApplicationContext)session.getValue(WebKeys.APPLICATION_KEY);
	String requestId =  request.getParameter(Action.PARAMETER_REQUEST_ID);
	String objectXri = request.getParameter(Action.PARAMETER_OBJECTXRI);
	if(app == null || objectXri == null) {
		response.sendRedirect(
			request.getContextPath() + "/" + WebKeys.SERVLET_NAME
		);
		return;
	}
	javax.jdo.PersistenceManager pm = app.getPmData();
	String requestIdParam = Action.PARAMETER_REQUEST_ID + "=" + requestId;
	String xriParam = Action.PARAMETER_OBJECTXRI + "=" + objectXri;
	Texts_1_0 texts = app.getTexts();
	org.openmdx.portal.servlet.Codes codes = app.getCodes();

	// Get Parameters
	boolean actionSave = request.getParameter("Save.Button") != null;
	boolean actionCancel = request.getParameter("Cancel.Button") != null;
	String command = request.getParameter("command");

	RefObject_1_0 obj = (RefObject_1_0)pm.getObjectById(new Path(objectXri));
	org.opencrx.kernel.home1.jmi1.Home1Package homePkg = org.opencrx.kernel.utils.Utils.getHomePackage(pm);
	org.opencrx.kernel.activity1.jmi1.Activity1Package activityPkg = org.opencrx.kernel.utils.Utils.getActivityPackage(pm);
	org.opencrx.security.realm1.jmi1.Realm1Package realmPkg = org.opencrx.kernel.utils.Utils.getRealmPackage(pm);
	org.opencrx.kernel.base.jmi1.BasePackage basePkg = org.opencrx.kernel.utils.Utils.getBasePackage(pm);
	// Get user home segment
	String providerName = obj.refGetPath().get(2);
	String segmentName = obj.refGetPath().get(4);
	org.opencrx.kernel.home1.jmi1.Segment userHomeSegment =
	  (org.opencrx.kernel.home1.jmi1.Segment)pm.getObjectById(
		new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName)
	   );
	org.opencrx.kernel.activity1.jmi1.Segment activitySegment =
	  (org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(
		new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName)
	   );
	org.opencrx.kernel.workflow1.jmi1.Segment workflowSegment =
	  (org.opencrx.kernel.workflow1.jmi1.Segment)pm.getObjectById(
		new Path("xri:@openmdx:org.opencrx.kernel.workflow1/provider/" + providerName + "/segment/" + segmentName)
	   );

	// Exit
	if("exit".equalsIgnoreCase(command)) {
		session.setAttribute(WIZARD_NAME, null);
		Action nextAction = new ObjectReference(obj, app).getSelectObjectAction();
		response.sendRedirect(
			request.getContextPath() + "/" + nextAction.getEncodedHRef()
		);
		return;
	}

	if(false) {
	}
	// Other commands
	else {
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html dir="<%= texts.getDir() %>">
<head>
	<style type="text/css" media="all">
		body{font-family: Arial, Helvetica, sans-serif; padding: 0; margin:0;}
		h1{  margin: 0; padding: 0 1em; font-size: 150%;}
		h2{ font-size: 130%; margin: 0; text-align: center;}

		a{text-decoration: none;}
		img{border: none;}

		/* Main Navigation across the top */
		.nav{padding: 0; margin: 0 0 1em 0; }
		.nav li{display: inline; }
		.nav a{padding: 0 0.5em; border: 1px solid silver;}
		.nav a:hover,
		.nav a:focus{background-color: silver; border: 1px solid gray;}
		.nav.secondary {float: right;}

		#content{width: 80%; margin: 0 auto; font-size: 90%;}

    textarea,
    input[type='text'],
    input[type='password']{
    	width: 100%;
    	margin: 0; border: 1px solid silver;
    	padding: 0;
    	font-size: 100%;
    	font-family: Arial, Helvetica, sans-serif;
    }
    input.button{
    	-moz-border-radius: 4px;
    	width: 120px;
    	border: 1px solid silver;
    }

		/* Add/Edit page specific settings */
		.col1,
		.col2{float: left; width: 49.5%;}

		.buttons{clear: both; text-align: right;}
		table{border-collapse: collapse; width: 100%; clear: both;}
		tr{}

		/* List page specific settings */
		table.listview tr{
			border: 1px solid #36c;
			border-style: solid none;
		}
		table.listview tr:hover{
			background-color: #F0F0F0;
		}

		div.letterBar {
			padding: 0.2em 0;
			text-align: center;
		}
		div.letterBar a,
		div.letterBar a:link,
		div.letterBar a:visited{
			padding: 0em 0.3em;
			border: 1px solid gray;
			-moz-border-radius: 6px;
			margin: 0 2px;
		}
		div.letterBar a:hover,
		div.letterBar a:focus{
			background-color: yellow;
		}

		div.letterBar a.current {
			background-color: #F0F0F0;
		}
	</style>
	<title>openCRX - User Settings</title>
	<meta name="UNUSEDlabel" content="User Settings">
	<meta name="UNUSEDtoolTip" content="User Settings">
	<meta name="targetType" content="_self">
	<meta name="forClass" content="org:opencrx:kernel:home1:UserHome">
  <meta name="order" content="org:opencrx:kernel:home1:UserHome:userSettings">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link href="../../_style/n2default.css" rel="stylesheet" type="text/css">
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
<%
		// Account details
		if(true) {
			short locale =  app.getCurrentLocaleAsIndex();
			String localeAsString = app.getCurrentLocaleAsString();
			org.opencrx.kernel.home1.jmi1.UserHome userHome =  (org.opencrx.kernel.home1.jmi1.UserHome)pm.getObjectById(new Path(objectXri));
			boolean currentUserOwnsHome =
				app.getCurrentUserRole().equals(userHome.refGetPath().getBase() + "@" + segmentName);
			boolean currentUserIsAdmin =
				app.getCurrentUserRole().equals(org.opencrx.kernel.generic.SecurityKeys.ADMIN_PRINCIPAL + org.opencrx.kernel.generic.SecurityKeys.ID_SEPARATOR + segmentName + "@" + segmentName);
			Properties userSettings =  new Properties();
			if(currentUserOwnsHome) {
				userSettings = app.getSettings();
			}
			else if(userHome.getSettings() != null) {
				userSettings.load(
					new ByteArrayInputStream(
						userHome.getSettings().getBytes("UTF-8")
					)
				);
			}

			String fTimezone = request.getParameter("timezone");
			String fStoreSettingsOnLogoff = request.getParameter("storeSettingsOnLogoff");
			String fEmailAccount = request.getParameter("emailAccount");
			String fSendmailSubjectPrefix = request.getParameter("sendmailSubjectPrefix");
			String fWebAccessUrl = request.getParameter("webAccessUrl");
			String fPrivateTracker = request.getParameter("privateTracker");
			String fResource = request.getParameter("resource");

			// Apply
			if("apply".equalsIgnoreCase(command)) {
				try {
					UUIDGenerator uuids = UUIDs.getGenerator();
					pm.currentTransaction().begin();

					userSettings.setProperty("TimeZone.Name", fTimezone);
					userHome.setStoreSettingsOnLogoff(
						Boolean.valueOf(fStoreSettingsOnLogoff == null ? "false" :"true")
					);
					userHome.setWebAccessUrl(fWebAccessUrl);
					userHome.setSendMailSubjectPrefix(fSendmailSubjectPrefix);
					// Email account
					org.opencrx.kernel.home1.jmi1.EmailAccount defaultEmailAccount = null;
					for(Iterator i = userHome.getEmailAccount().iterator(); i.hasNext(); ) {
						org.opencrx.kernel.home1.jmi1.EmailAccount emailAccount = (org.opencrx.kernel.home1.jmi1.EmailAccount)i.next();
						if((emailAccount.isDefault() != null) && emailAccount.isDefault().booleanValue()) {
							defaultEmailAccount = emailAccount;
							break;
						}
					}
					if(
						(defaultEmailAccount == null) &&
						(fEmailAccount != null) &&
						(fEmailAccount.length() > 0)
					) {
						defaultEmailAccount = homePkg.getEmailAccount().createEmailAccount();
						defaultEmailAccount.refInitialize(false, false);
						defaultEmailAccount.setDefault(Boolean.TRUE);
						defaultEmailAccount.setEmailAddress(fEmailAccount);
						userHome.addEmailAccount(
							false,
							uuids.next().toString(),
							defaultEmailAccount
						);
					}
					else if(
						(defaultEmailAccount != null) &&
						((fEmailAccount == null) ||
						(fEmailAccount.length() == 0))
					) {
						defaultEmailAccount.refDelete();
					}
					else if(defaultEmailAccount != null) {
						defaultEmailAccount.setEmailAddress(fEmailAccount);
					}
					// Root objects
					for(int i = 1; i < 20; i++) {
						String state = request.getParameter("rootObject" + i);
						userSettings.setProperty(
							"RootObject." + i + ".State",
							state == null ? "0" : "1"
						);
					}
					// Show max items in top navigation					
					String topNavigationShowMax = request.getParameter("topNavigationShowMax");
					userSettings.setProperty(
						"TopNavigation.ShowMax",
						topNavigationShowMax
					);
					// History
					String state = request.getParameter("history");
					userSettings.setProperty(
						"History.State",
						state == null ? "0" : "1"
					);
					// If running as segment admin set ACLs of created objects
					org.opencrx.security.realm1.jmi1.PrincipalGroup privatePrincipalGroup = null;
					if(currentUserIsAdmin) {
						// Get principal group with name <principal>.Group. This is the private group of the owner of the user home page
						org.openmdx.security.realm1.jmi1.Realm realm = org.opencrx.kernel.backend.SecureObject.getRealm(
							pm,
							providerName,
							segmentName
						);
						privatePrincipalGroup = (org.opencrx.security.realm1.jmi1.PrincipalGroup)org.opencrx.kernel.backend.SecureObject.findPrincipal(
							userHome.refGetPath().getBase() + "." + org.opencrx.kernel.generic.SecurityKeys.GROUP_SUFFIX,
							realm,
							pm
						);
						if(
							(privatePrincipalGroup == null)
						) {
							privatePrincipalGroup = realmPkg.getPrincipalGroup().createPrincipalGroup();
							privatePrincipalGroup.refInitialize(false, false);
							privatePrincipalGroup.setDescription(segmentName + "\\\\" + userHome.refGetPath().getBase() + "." + org.opencrx.kernel.generic.SecurityKeys.GROUP_SUFFIX);
							realm.addPrincipal(
								false,
								userHome.refGetPath().getBase() + "." + org.opencrx.kernel.generic.SecurityKeys.GROUP_SUFFIX,
								privatePrincipalGroup
							);
						}
						// Set UserHome's primary group
						userHome.setPrimaryGroup(privatePrincipalGroup);
						org.openmdx.security.realm1.jmi1.Principal principal = null;
						try {
							principal = org.opencrx.kernel.backend.SecureObject.findPrincipal(
								userHome.refGetPath().getBase(),
								realm,
								pm
							);
							// Validate that user is member of <principal>.Group
							if(!principal.getIsMemberOf().contains(privatePrincipalGroup)) {
								principal.getIsMemberOf().add(privatePrincipalGroup);
							}
							// Validate that user is member of group 'Public'
							org.opencrx.security.realm1.jmi1.PrincipalGroup publicGroup = (org.opencrx.security.realm1.jmi1.PrincipalGroup)org.opencrx.kernel.backend.SecureObject.findPrincipal(
								"Public",
								realm,
								pm
							);
							if(!principal.getIsMemberOf().contains(publicGroup)) {
								principal.getIsMemberOf().add(publicGroup);
							}
						} catch(Exception e) {}
						// Validate that subject of <principal>.Group is the same as of <principal>
						if((principal != null) && (privatePrincipalGroup != null)) {
							privatePrincipalGroup.setSubject(principal.getSubject());
						}
					}

					// Private activity tracker
					org.opencrx.kernel.activity1.jmi1.ActivityTracker privateTracker = null;
					try {
						privateTracker = (org.opencrx.kernel.activity1.jmi1.ActivityTracker)pm.getObjectById(
							new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName + "/activityTracker/" + userHome.refGetPath().getBase())
						);
					}
					catch(Exception e) {}
					if(privateTracker == null) {
						privateTracker = activityPkg.getActivityTracker().createActivityTracker();
						privateTracker.refInitialize(false, false);
						privateTracker.setName(userHome.refGetPath().getBase() + "~Private");
						if(privatePrincipalGroup != null) {
							privateTracker.getOwningGroup().clear();
							privateTracker.getOwningGroup().add(privatePrincipalGroup);
						}
						activitySegment.addActivityTracker(
							false,
							userHome.refGetPath().getBase(),
							privateTracker
						);
					}
					if((fPrivateTracker != null) && (fPrivateTracker.length() > 0)) {
						privateTracker.setName(fPrivateTracker);
					}
					// Private activity creator
					org.opencrx.kernel.activity1.jmi1.ActivityCreator privateCreator = null;
					try {
						privateCreator = (org.opencrx.kernel.activity1.jmi1.ActivityCreator)pm.getObjectById(
							new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName + "/activityCreator/" + userHome.refGetPath().getBase())
						);
					}
					catch(Exception e) {}
					if(privateCreator == null) {
						privateCreator = activityPkg.getActivityCreator().createActivityCreator();
						privateCreator.refInitialize(false, false);
						privateCreator.setName(userHome.refGetPath().getBase() + "~Private");
						privateCreator.getActivityGroup().add(privateTracker);
						privateCreator.setActivityType(
							org.opencrx.kernel.backend.Activities.findActivityType("Bugs + Features", activitySegment, pm)
						);
						if(privatePrincipalGroup != null) {
							privateCreator.getOwningGroup().clear();
							privateCreator.getOwningGroup().add(privatePrincipalGroup);
						}
						activitySegment.addActivityCreator(
							false,
							userHome.refGetPath().getBase(),
							privateCreator
						);
					}
					if((fPrivateTracker != null) && (fPrivateTracker.length() > 0)) {
						privateCreator.setName(fPrivateTracker);
					}
					// Set default creator on tracker
					privateTracker.setDefaultCreator(privateCreator);

					// Resource
					org.opencrx.kernel.activity1.jmi1.Resource resource = findResource(
						pm,
						activityPkg,
						activitySegment,
						userHome,
						providerName,
						segmentName
					);
					if(resource == null) {
						resource = activityPkg.getResource().createResource();
						resource.refInitialize(false, false);
						if(userHome.getContact() != null) {
							resource.setName(userHome.getContact().getFullName());
							resource.setContact(userHome.getContact());
						}
						else {
							resource.setName(userHome.refGetPath().getBase());
						}
						activitySegment.addResource(
							false,
							userHome.refGetPath().getBase(),
							resource
						);
					}
					if((fResource != null) && (fResource.length() > 0)) {
						resource.setName(fResource);
					}
					// Subscriptions
					for(Iterator i = workflowSegment.getTopic().iterator(); i.hasNext(); ) {
						org.opencrx.kernel.workflow1.jmi1.Topic topic = (org.opencrx.kernel.workflow1.jmi1.Topic)i.next();
						org.opencrx.kernel.home1.cci2.SubscriptionQuery query = homePkg.createSubscriptionQuery();
						query.thereExistsTopic().equalTo(topic);
						Collection subscriptions = userHome.getSubscription(query);
						org.opencrx.kernel.home1.jmi1.Subscription subscription = null;
						if(subscriptions.isEmpty()) {
							subscription = homePkg.getSubscription().createSubscription();
							subscription.refInitialize(false, false);
							subscription.setName(topic.getName());
							subscription.setTopic(topic);
							userHome.addSubscription(
								false,
								uuids.next().toString(),
								subscription
							);
						}
						else {
							subscription = (org.opencrx.kernel.home1.jmi1.Subscription)subscriptions.iterator().next();
						}
						subscription.getEventType().clear();
						String topicId = topic.refGetPath().getBase();
						subscription.setActive(
							request.getParameter("topicIsActive-" + topicId) != null
						);
						if(request.getParameter("topicCreation-" + topicId) != null) {
							subscription.getEventType().add(new Short((short)1));
						}
						if(request.getParameter("topicReplacement-" + topicId) != null) {
							subscription.getEventType().add(new Short((short)3));
						}
						if(request.getParameter("topicRemoval-" + topicId) != null) {
							subscription.getEventType().add(new Short((short)4));
						}
					}
					// Store settings
					if(!currentUserOwnsHome) {
						ByteArrayOutputStream bsSettings = new ByteArrayOutputStream();
						userSettings.store(
							bsSettings,
							"settings of user " + userHome.refMofId()
						);
						bsSettings.close();
						userHome.setSettings(
							bsSettings.toString("UTF-8")
						);
					}
					pm.currentTransaction().commit();
					// Assert owningUser of Resource
					if(!currentUserOwnsHome) {
					    pm.currentTransaction().begin();
					    org.opencrx.kernel.base.jmi1.SetOwningUserParams setOwningUserParams = basePkg.createSetOwningUserParams(
					        (short)SecureObject.MODE_RECURSIVE,
					        userHome.getOwningUser()
					    );
						resource.setOwningUser(
						    setOwningUserParams
						);					    
					    pm.currentTransaction().commit();
					}
   					if(currentUserOwnsHome) {
						app.saveSettings(false);
					}
				}
				catch(Exception e) {
					new ServiceException(e).log();
					try {
						pm.currentTransaction().rollback();
					} catch(Exception e0) {}
				}
			}
%>
		<ul class="nav">
		</ul>
		<form method="post" action="<%= WIZARD_NAME %>">
			<input type="hidden" name="command" value="apply"/>
			<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= objectXri %>"/>
			<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= requestId %>" />
			<div class="col1">
				<fieldset>
					<legend>User Home</legend>
					<table>
						<tr><td><label for="timezone">Timezone:</label></td>
						<td>
							<select id="timezone" name="timezone">
<%
								Map timezoneIDs = codes.getShortText("org:opencrx:kernel:address1:PostalAddressable:postalUtcOffset", locale, true, true);
								for(Iterator i = timezoneIDs.values().iterator(); i.hasNext(); ) {
									String timezoneID = (String)i.next();
									timezoneID = timezoneID.trim();
									timezoneID = timezoneID.replace(":", "");
									timezoneID = timezoneID.replace(" Greenwich Mean Time", "-0000");
									if(!"NA".equals(timezoneID)) {
										String selectedModifier = timezoneID.equals(userSettings.getProperty("TimeZone.Name"))
											? "selected"
											: "";
%>
										<option  <%= selectedModifier %> value="<%= timezoneID %>"><%= timezoneID %>
<%
									}
								}
                String[] timezones = java.util.TimeZone.getAvailableIDs();
                for(int i = 0; i < timezones.length; i++) {
                  String timezoneID = timezones[i].trim();
                  String selectedModifier = timezoneID.equals(userSettings.getProperty("TimeZone.Name"))
                    ? "selected"
                    : "";
%>
										<option  <%= selectedModifier %> value="<%= timezoneID %>"><%= timezoneID %>
<%
                }

%>
							</select>
						</td></tr>
						<tr><td><label for="storeSettingsOnLogoff">Store settings on logoff:</label></td>
						<td><input type="checkbox" <%= userHome.isStoreSettingsOnLogoff() != null && userHome.isStoreSettingsOnLogoff().booleanValue() ? "checked" : "" %> id="storeSettingsOnLogoff" name="storeSettingsOnLogoff"/></td></tr>
<%
						org.opencrx.kernel.home1.jmi1.EmailAccount defaultEmailAccount = null;
						for(Iterator i = userHome.getEmailAccount().iterator(); i.hasNext(); ) {
							org.opencrx.kernel.home1.jmi1.EmailAccount emailAccount = (org.opencrx.kernel.home1.jmi1.EmailAccount)i.next();
							if((emailAccount.isDefault() != null) && emailAccount.isDefault().booleanValue()) {
								defaultEmailAccount = emailAccount;
								 break;
							}
						}
%>
						<tr><td><label for="emailAccount">Email:</label></td>
						<td><input type="text" id="emailAccount" name="emailAccount"  value="<%= defaultEmailAccount == null || defaultEmailAccount.getEmailAddress() == null ? "" :  defaultEmailAccount.getEmailAddress() %>"/></td></tr>
						<tr><td><label for="sendmailSubjectPrefix">Sendmail subject prefix:</label></td>
						<td><input type="text" id="sendmailSubjectPrefix" name="sendmailSubjectPrefix"  value="<%= userHome.getSendMailSubjectPrefix() == null ? "[" + providerName + ":" + segmentName + "]" : userHome.getSendMailSubjectPrefix() %>"/>
						<tr><td><label for="webAccessUrl">Web access URL:</label></td>
						<td><input type="text" id="webAccessUrl" name="webAccessUrl"  value="<%= userHome.getWebAccessUrl() == null ? request.getRequestURL().substring(0, request.getRequestURL().indexOf("/wizards")) :  userHome.getWebAccessUrl()  %>"/>
					</table>
				</fieldset>
				<fieldset>
					<legend>Root Menu</legend>
					<table>
						<tr><td></td><td>Is&nbsp;Active:</td></tr>
<%
						Action[] rootObjectActions = app.getRootObjectActions();
						// Always show root object 0
						int n = 1;
						for(int i = 1; i < rootObjectActions.length; i++) {
							Action action = rootObjectActions[i];
							if(action.getParameter(Action.PARAMETER_REFERENCE).length() == 0) {
%>
								<tr><td><label for="rootObject<%= n %>"><%= action.getTitle() %>:</label></td><td>
								<input type="checkbox" <%= userSettings.getProperty("RootObject." + n + ".State", "1").equals("1") ? "checked" : "" %> id="rootObject<%= n %>" name="rootObject<%= n %>"/></td></tr>
<%
								n++;
							}
						}
%>
						<tr><td><label for="history">History:</label></td><td>
						<input type="checkbox" <%= userSettings.getProperty("History.State", "1").equals("1") ? "checked" : "" %> id="history" name="history"/></td></tr>
						<tr><td><label for="topNavigationShowMax">Show max items in top navigation:</label></td><td>
						<input type="text" id="topNavigationShowMax" name="topNavigationShowMax" value="<%= userSettings.getProperty("TopNavigation.ShowMax", "6") %>"/></td></tr>
					</table>
				</fieldset>
			</div>
			<div class="col2">
				<fieldset>
					<legend>Activities</legend>
					<table>
<%
						org.opencrx.kernel.activity1.jmi1.ActivityTracker privateTracker = null;
						try {
							privateTracker = (org.opencrx.kernel.activity1.jmi1.ActivityTracker)pm.getObjectById(
								new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName + "/activityTracker/" + userHome.refGetPath().getBase())
							);
						} catch(Exception e) {}
%>
						<tr><td><label for="privateTracker">Private activity tracker:</label></td>
						<td><input type="text" id="privateTracker" name="privateTracker"  value="<%= privateTracker == null ? userHome.refGetPath().getBase() + "~Private" : privateTracker.getName() %>"/></td></tr>
<%
						org.opencrx.kernel.activity1.jmi1.Resource resource = findResource(
							pm,
							activityPkg,
							activitySegment,
							userHome,
							providerName,
							segmentName
						);
%>
						<tr><td><label for="resource">Resource:</label></td>
						<td><input type="text" id="resource" name="resource"  value="<%= resource == null ? (userHome.getContact() == null ? "" :  userHome.getContact().getFullName()) : resource.getName() %>"/></td></tr>
					</table>
				</fieldset>
				<fieldset>
					<legend>Subscriptions</legend>
					<table>
						<tr><td></td><td></td><td colspan="3" style="background-color:#DDDDDD;text-align:center;">Notify on</td></tr>
						<tr><td></td><td>&nbsp;Is&nbsp;Active&nbsp;</td><td style="">&nbsp;Creation&nbsp;</td><td style="">&nbsp;Replacement&nbsp;</td><td style="">&nbsp;Removal&nbsp;</td></tr>
<%
						for(Iterator i = workflowSegment.getTopic().iterator(); i.hasNext(); ) {
							org.opencrx.kernel.workflow1.jmi1.Topic topic = (org.opencrx.kernel.workflow1.jmi1.Topic)i.next();
							ObjectReference objRefTopic = new ObjectReference(topic, app);
							org.opencrx.kernel.home1.cci2.SubscriptionQuery query = homePkg.createSubscriptionQuery();
							query.thereExistsTopic().equalTo(topic);
							Collection subscriptions = userHome.getSubscription(query);
							org.opencrx.kernel.home1.jmi1.Subscription subscription = subscriptions.isEmpty()
								? null
								: (org.opencrx.kernel.home1.jmi1.Subscription)subscriptions.iterator().next();
							Set eventTypes = new HashSet();
							if(subscription != null) {
								for(Iterator j = subscription.getEventType().iterator(); j.hasNext(); ) {
									eventTypes.add(
										Integer.valueOf(((Number)j.next()).intValue())
									);
								}
							}
							String topicId = topic.refGetPath().getBase();
%>
							<tr><td><label><%= objRefTopic.getTitle() %>:</label></td><td style="text-align:center;">
							<input type="checkbox" <%= subscription == null || !subscription.isActive() ? "" : "checked" %> id="topicIsActive-<%= topicId %>" name="topicIsActive-<%= topicId %>" /></td><td style="text-align:center;">
							<input type="checkbox" <%= eventTypes.contains(Integer.valueOf(1)) ? "checked" : "" %> id="topicCreation-<%= topicId %>" name="topicCreation-<%= topicId %>" /></td><td style="text-align:center;">
							<input type="checkbox" <%= eventTypes.contains(Integer.valueOf(3)) ? "checked" : "" %> id="topicReplacement-<%= topicId %>" name="topicReplacement-<%= topicId %>" /></td><td style="text-align:center;">
							<input type="checkbox" <%= eventTypes.contains(Integer.valueOf(4)) ? "checked" : "" %> id="topicRemoval-<%= topicId %>" name="topicRemoval-<%= topicId %>" /></td></tr>
<%
						}
%>
					</table>
				</fieldset>
			</div>
			<div class="buttons">
<%
				boolean allowApply = currentUserIsAdmin || (currentUserOwnsHome && (privateTracker != null) && (resource != null));
%>
				<input <%= allowApply ? "" : "disabled" %> type="submit" value="Apply"  class="button" />
				<input type="button" value="Cancel" onclick="javascript:location.href='<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=exit" %>';" class="button" />
				<%= allowApply ? "" : "<h2>Apply not allowed! Non-ownership of user home and first time usage of wizard requires admin permissions.</h2>" %>
			</div>
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
<%
	}
%>
