<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: AddressBook.jsp,v 1.40 2008/06/26 00:34:33 wfro Exp $
 * Description: AddressBook
 * Revision:    $Revision: 1.40 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/06/26 00:34:33 $
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
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
org.openmdx.base.exception.*
" %><%

	final String WIZARD_NAME = "AddressBook.jsp";

	// Init
	request.setCharacterEncoding("UTF-8");
	ApplicationContext app = (ApplicationContext)session.getValue(WebKeys.APPLICATION_KEY);
	ViewsCache viewsCache = (ViewsCache)session.getValue(WebKeys.VIEW_CACHE_KEY_SHOW);
	String requestId =  request.getParameter(Action.PARAMETER_REQUEST_ID);
	String requestIdParam = Action.PARAMETER_REQUEST_ID + "=" + requestId;
	String objectXri = request.getParameter(Action.PARAMETER_OBJECTXRI);
	String xriParam = Action.PARAMETER_OBJECTXRI + "=" + objectXri;
	if(objectXri == null || app == null) {
		session.setAttribute(WIZARD_NAME, null);
		response.sendRedirect(
			request.getContextPath() + "/" + WebKeys.SERVLET_NAME
		);
		return;
	}
	javax.jdo.PersistenceManager pm = app.getPmData();
	Texts_1_0 texts = app.getTexts();
	org.openmdx.portal.servlet.Codes codes = app.getCodes();

	// Get Parameters
	boolean actionSave = request.getParameter("Save.Button") != null;
	boolean actionCancel = request.getParameter("Cancel.Button") != null;
	String startWith = request.getParameter("startWith");
	String command = request.getParameter("command");
	String accountXri = request.getParameter("accountXri");
	String maxAsString = request.getParameter("max");
	int max = 10;
	if((maxAsString != null) && (maxAsString.length() > 0)) {
		max = Integer.valueOf(maxAsString).intValue();
	}
	RefObject_1_0 obj = (RefObject_1_0)pm.getObjectById(new Path(objectXri));

	// Get account1 package
	org.opencrx.kernel.account1.jmi1.Account1Package accountPkg = org.opencrx.kernel.utils.Utils.getAccountPackage(pm);
	// Get account segment
	String providerName = obj.refGetPath().get(2);
	String segmentName = obj.refGetPath().get(4);
	org.opencrx.kernel.account1.jmi1.Segment accountSegment =
	  (org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(
		new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/" + providerName + "/segment/" + segmentName)
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
	// If launched from account go by default to edit mode
	if(obj instanceof org.opencrx.kernel.account1.jmi1.Account) {
		if(command == null) {
			command = "edit";
			session.setAttribute(this.getClass().getName() + ".lastQuery", null);
		}
		accountXri = obj.refMofId();
	}

	// Export VCard
	if(
		"vcard".equalsIgnoreCase(command) &&
		(accountXri != null) &&
		(accountXri.length() > 0)
	) {
		response.setCharacterEncoding("UTF-8");
		short locale =  app.getCurrentLocaleAsIndex();
		org.opencrx.kernel.account1.jmi1.Account account = (org.opencrx.kernel.account1.jmi1.Account)pm.getObjectById(new Path(accountXri));
		if(account instanceof org.opencrx.kernel.account1.jmi1.Contact) {
			org.opencrx.kernel.account1.jmi1.Contact contact = (org.opencrx.kernel.account1.jmi1.Contact)account;
			byte[] item = contact.getVcard() == null ? null :  contact.getVcard().getBytes("UTF-8");
			if(item != null) {
				String location = UUIDs.getGenerator().next().toString();
				File f = new File(
				   app.getTempFileName(location, "")
				);
				FileOutputStream os = new FileOutputStream(f);
				for(int i = 0; i < item.length; i++) {
					os.write(item[i]);
				}
				os.flush();
				os.close();
				String filename = org.opencrx.kernel.utils.Utils.toFilename(contact.getFullName()) + ".vcf";
				Action downloadAction =
					new Action(
						Action.EVENT_DOWNLOAD_FROM_LOCATION,
						new Action.Parameter[]{
							new Action.Parameter(Action.PARAMETER_LOCATION, location),
							new Action.Parameter(Action.PARAMETER_NAME, filename),
							new Action.Parameter(Action.PARAMETER_MIME_TYPE, org.opencrx.kernel.backend.VCard.MIME_TYPE)
						},
						app.getTexts().getClickToDownloadText() + " " + filename,
						true
					);
				response.sendRedirect(
					request.getContextPath() + "/" + downloadAction.getEncodedHRef(requestId)
				);
			}
			else {
				response.setStatus(HttpServletResponse.SC_NO_CONTENT );
			}
		}
		else {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT );
		}
	}
	// Export as CSV
	else if(
		"export".equalsIgnoreCase(command) ||
		"export*".equalsIgnoreCase(command)
	) {
		response.setCharacterEncoding("UTF-8");
		if((startWith == null) || (startWith.length() == 0)) {
			startWith = "A";
		}
		short locale =  app.getCurrentLocaleAsIndex();
		String localeAsString =  app.getCurrentLocaleAsString();
		String location = UUIDs.getGenerator().next().toString();
		File f = new File(
		   app.getTempFileName(location, "")
		);
		PrintStream os = new PrintStream(f, "UTF-8");
		org.opencrx.kernel.account1.cci2.AccountQuery query = accountPkg.createAccountQuery();
		// Export all starting from startFrom up to a maximum of 500 accounts
		if("export*".equals(command)) {
			query.thereExistsFullName().greaterThanOrEqualTo(startWith);
			max = 500;
		}
		else {
			query.thereExistsFullName().like("(?i)" + startWith + ".*");
		}
		query.orderByFullName().ascending();
		int n = 0;
		os.println("group0;group1;group2;group3;group4;fullName;salutation;lastName;firstName;middleName;company;jobTitle;description;workPostal;homePostal;workPhone;homePhone;workFax;homeFax;mobile;otherPhone;workEmail;homeEmail;workWeb;homeWeb;cat0;cat1;cat2;cat3;cat4;cat5;cat6;cat7;cat8;cat9");
		for(Iterator i = accountSegment.getAccount(query).iterator(); i.hasNext(); ) {
			org.opencrx.kernel.account1.jmi1.Account account = (org.opencrx.kernel.account1.jmi1.Account)i.next();
			org.opencrx.kernel.account1.jmi1.Contact contact = account instanceof org.opencrx.kernel.account1.jmi1.Contact
				? (org.opencrx.kernel.account1.jmi1.Contact)account
				: null;
			org.opencrx.kernel.account1.jmi1.AccountAddress[] addresses = Accounts.getMainAddresses(account);
			// The groups0..4 allow to sort according to their membership
			int nGroups = 0;
			for(Iterator j = account.getAccountMembership().iterator(); j.hasNext(); ) {
				org.opencrx.kernel.account1.jmi1.AccountMembership membership = (org.opencrx.kernel.account1.jmi1.AccountMembership)j.next();
				if(membership.getAccountFrom() != null) {
					org.opencrx.kernel.account1.jmi1.Account group = membership.getAccountFrom();
					os.print(group.getFullName() == null ? "" : "\"" + group.getFullName() + "\"");
					os.print(";");
					nGroups++;
				}
			}
			for(int j = nGroups; j < 5; j++) {
				// If account does not have membership make it member
				if(j == 0) {
					os.print(account.getFullName() == null ? "" : "\"" + account.getFullName() + "\"");
				}
				os.print(";");
			}
			os.print(account.getFullName() == null ? "" : "\"" + account.getFullName() + "\"");
			os.print(";");
			short salutationCode = (short)0;
			try {
				salutationCode = contact == null ? (short)0 : contact.getSalutationCode();
			} catch(Exception e) {}
			os.print(contact == null ? "" : "\"" + salutationCode + "\"");
			os.print(";");
			os.print(contact == null || contact.getLastName() == null ? "" : "\"" + contact.getLastName() + "\"");
			os.print(";");
			os.print(contact == null || contact.getFirstName() == null ? "" : "\"" + contact.getFirstName() + "\"");
			os.print(";");
			os.print(contact == null || contact.getMiddleName() == null ? "" : "\"" + contact.getMiddleName() + "\"");
			os.print(";");
			os.print(contact == null || contact.getOrganization() == null ? "" : "\"" + contact.getOrganization() + "\"");
			os.print(";");
			os.print(contact == null || contact.getJobTitle() == null ? "" : "\"" + contact.getJobTitle() + "\"");
			os.print(";");
			os.print(account == null || account.getDescription() == null ? "" : "\"" + (account.getDescription().indexOf(".") > 0 ? account.getDescription().substring(0,  account.getDescription().indexOf(".")) : account.getDescription()) + "\"");
			os.print(";");
			os.print(addresses[Accounts.POSTAL_BUSINESS] == null ? "" : "\"" + app.getPortalExtension().getTitle(addresses[Accounts.POSTAL_BUSINESS], locale, localeAsString, app).replace("<br />", "\r\n") + "\"");
			os.print(";");
			os.print(addresses[Accounts.POSTAL_HOME] == null ? "" : "\"" + app.getPortalExtension().getTitle(addresses[Accounts.POSTAL_HOME], locale, localeAsString, app).replace("<br />", "\r\n") + "\"");
			os.print(";");
			os.print(addresses[Accounts.PHONE_BUSINESS] == null ? "" : "\"" + app.getPortalExtension().getTitle(addresses[Accounts.PHONE_BUSINESS], locale, localeAsString, app).replace("<br />", "\r\n") + "\"");
			os.print(";");
			os.print(addresses[Accounts.PHONE_HOME] == null ? "" : "\"" + app.getPortalExtension().getTitle(addresses[Accounts.PHONE_HOME], locale, localeAsString, app).replace("<br />", "\r\n") + "\"");
			os.print(";");
			os.print(addresses[Accounts.FAX_BUSINESS] == null ? "" : "\"" + app.getPortalExtension().getTitle(addresses[Accounts.FAX_HOME], locale, localeAsString, app).replace("<br />", "\r\n") + "\"");
			os.print(";");
			os.print(addresses[Accounts.FAX_HOME] == null ? "" : "\"" + app.getPortalExtension().getTitle(addresses[Accounts.FAX_HOME], locale, localeAsString, app).replace("<br />", "\r\n") + "\"");
			os.print(";");
			os.print(addresses[Accounts.MOBILE] == null ? "" : "\"" + app.getPortalExtension().getTitle(addresses[Accounts.MOBILE], locale, localeAsString, app).replace("<br />", "\r\n") + "\"");
			os.print(";");
			os.print(addresses[Accounts.PHONE_OTHER] == null ? "" : "\"" + app.getPortalExtension().getTitle(addresses[Accounts.PHONE_OTHER], locale, localeAsString, app).replace("<br />", "\r\n") + "\"");
			os.print(";");
			os.print(addresses[Accounts.MAIL_BUSINESS] == null ? "" : "\"" + ((org.opencrx.kernel.address1.jmi1.EmailAddressable)addresses[Accounts.MAIL_BUSINESS]).getEmailAddress() + "\"");
			os.print(";");
			os.print(addresses[Accounts.MAIL_HOME] == null ? "" : "\"" + ((org.opencrx.kernel.address1.jmi1.EmailAddressable)addresses[Accounts.MAIL_HOME]).getEmailAddress() + "\"");
			os.print(";");
			os.print(addresses[Accounts.WEB_BUSINESS] == null ? "" : "\"" + ((org.opencrx.kernel.address1.jmi1.WebAddressable)addresses[Accounts.WEB_BUSINESS]).getWebUrl() + "\"");
			os.print(";");
			os.print(addresses[Accounts.WEB_HOME] == null ? "" : "\"" + ((org.opencrx.kernel.address1.jmi1.WebAddressable)addresses[Accounts.WEB_HOME]).getWebUrl() + "\"");
			os.print(";");
			int iCat = 0;
			for(Iterator j = account.getCategory().iterator(); j.hasNext(); iCat++) {
				os.print(j.next());
				os.print(";");
			}
			while(iCat < 10) {
				os.print(";");
				iCat++;
			}
			// Record delimiter and new line
			os.print("|\r\n");
			n++;
			if(n > max) break;
		}
		os.flush();
		os.close();
		Action downloadAction =
			new Action(
				Action.EVENT_DOWNLOAD_FROM_LOCATION,
				new Action.Parameter[]{
					new Action.Parameter(Action.PARAMETER_LOCATION, location),
					new Action.Parameter(Action.PARAMETER_NAME, "Accounts.txt"),
					new Action.Parameter(Action.PARAMETER_MIME_TYPE, "text/plain")
				},
				app.getTexts().getClickToDownloadText() + " Accounts.txt",
				true
			);
		response.sendRedirect(
			request.getContextPath() + "/" + downloadAction.getEncodedHRef(requestId)
		);
	}
	// Other commands
	else {
%>
<!--[if IE]><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><![endif]-->
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

		fieldset{
			margin: 1%;
			padding: 1%;
			-moz-border-radius: 10px;
			border: 1.5px solid #DDD;
			background-color: #EEE;}
		legend{
			border: 1px solid #CCC;
			-moz-border-radius: 10px;
			padding: 0 1em;
			background-color: #CCC;
		}
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
	<title>openCRX - Address Book</title>
	<meta name="UNUSEDlabel" content="Address Book">
	<meta name="UNUSEDtoolTip" content="Address Book">
	<meta name="targetType" content="_self">
	<meta name="forClass" content="org:opencrx:kernel:account1:Segment">
	<meta name="forClass" content="org:opencrx:kernel:account1:Contact">
	<meta name="forClass" content="org:opencrx:kernel:account1:LegalEntity">
	<meta name="forClass" content="org:opencrx:kernel:account1:Group">
	<meta name="forClass" content="org:opencrx:kernel:account1:UnspecifiedAccount">
  <meta name="order" content="org:opencrx:kernel:account1:Segment:addressBook">
  <meta name="order" content="org:opencrx:kernel:account1:Contact:addressBook">
  <meta name="order" content="org:opencrx:kernel:account1:LegalEntity:addressBook">
  <meta name="order" content="org:opencrx:kernel:account1:Group:addressBook">
  <meta name="order" content="org:opencrx:kernel:account1:UnspecifiedAccount:addressBook">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link href="../../_style/n2default.css" rel="stylesheet" type="text/css">
</head>
<body>
	<div id="header" style="padding:10px 0px 10px 0px;">
		<table dir="ltr" id="headerlayout" style="position:relative;">
		  <tr id="headRow">
			<td id="head" colspan="2">
			  <table id="info">
				<tr>
				  <td id="headerCellLeft"><img id="logoLeft" src="../../images/logoLeft.gif" alt="openCRX - limitless relationship management" title="" /></td>
				  <td id="headerCellMiddle"><h1>Address Book</h1></td>
				  <td id="headerCellRight"><img id="logoRight" src="../../images/logoRight.gif" alt="" title="" /></td>
				</tr>
			  </table>
			</td>
		  </tr>
		</table>
	</div>
<%
		// Search
		if(
			(command == null) || (command.length() == 0) ||
			(accountXri == null) || (accountXri.length() == 0) ||
			"search".equalsIgnoreCase(command)
		) {
			session.setAttribute(
				this.getClass().getName() + ".lastQuery",
				request.getQueryString()
			);
			if((startWith == null) || (startWith.length() == 0)) {
				startWith = "A";
			}
%>
			<ul class="nav">
				<li><a href="<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=edit&accountXri=newContact" %>">New Contact</a></li>
				<li><a href="<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=edit&accountXri=newLegalEntity" %>">New Legal Entity</a></li>
				<li><a href="<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=edit&accountXri=newGroup" %>">New Group</a></li>
				<li><a href="<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=edit&accountXri=newUnspecifiedAccount" %>">New Unspecified Account</a></li>
				<li><a href="<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=export&startWith=" + URLEncoder.encode(startWith) + "&max=" + max  %>">Export</a></li>
				<li><a href="<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=export*" %>">Export 500</a></li>
				<li><a href="<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=exit"  %>">Exit</a></li>
			</ul>
			<div class="letterBar">
<%
			for(int i = 0; i < 26; i++) {
				char letter =  (char)('A' + i) ;
%>
				<a href="<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=search&startWith=" + letter %>" style="font-size:17px;" class="current"><%= "" + letter %></a>
<%
			}
%>
			</div>
			<p />
			<div class="letterBar">
<%
			for(int i = 0; i < 26; i++) {
				char letter =  (char)('A' + i) ;
%>
				<a href="<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=search&startWith=" + startWith.substring(0,1) + letter %>" style="font-size:11px;" class="current"><%= startWith.substring(0,1) + letter %></a>
<%
			}
%>
			</div>
			<fieldset style="background-color: white;">
				<legend><%= startWith %></legend>
				<table class="listview">
					<tr>
						<td>Full name</td>
						<td>Postal Address Work</td>
						<td>Postal Address Home</td>
						<td>Phone Work / Home</td>
						<td>Email Work / Home</td>
						<td>&nbsp;<br />&nbsp;</td>
						<td></td>
					</tr>
<%
					app.resetPmData();
					org.opencrx.kernel.account1.cci2.AccountQuery query = accountPkg.createAccountQuery();
					short locale =  app.getCurrentLocaleAsIndex();
					String localeAsString =  app.getCurrentLocaleAsString();
					query.thereExistsFullName().like("(?i)" + startWith + ".*");
					query.orderByFullName().ascending();
					int n = 0;
					for(Iterator ai = accountSegment.getAccount(query).iterator(); ai.hasNext(); ) {
						org.opencrx.kernel.account1.jmi1.Account account = (org.opencrx.kernel.account1.jmi1.Account)ai.next();
						org.opencrx.kernel.account1.jmi1.AccountAddress[] addresses = Accounts.getMainAddresses(account);
						String businessEmail = addresses[Accounts.MAIL_BUSINESS] == null ? null : ((org.opencrx.kernel.address1.jmi1.EmailAddressable)addresses[Accounts.MAIL_BUSINESS]).getEmailAddress();
						String homeEmail = addresses[Accounts.MAIL_HOME] == null ? null : ((org.opencrx.kernel.address1.jmi1.EmailAddressable)addresses[Accounts.MAIL_HOME]).getEmailAddress();
%>
						<tr onclick="javascript:location.href='<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=edit&accountXri=" + URLEncoder.encode(account.refMofId(), "UTF-8") %>';">
							<td><%= account.getFullName() %></td>
							<td><%= addresses[Accounts.POSTAL_BUSINESS] == null ? "-" : app.getPortalExtension().getTitle(addresses[Accounts.POSTAL_BUSINESS], locale, localeAsString, app) %></td>
							<td><%= addresses[Accounts.POSTAL_HOME] == null ? "-" : app.getPortalExtension().getTitle(addresses[Accounts.POSTAL_HOME], locale, localeAsString, app) %></td>
							<td><%= (addresses[Accounts.PHONE_BUSINESS] == null ? "- / " : app.getPortalExtension().getTitle(addresses[Accounts.PHONE_BUSINESS], locale, localeAsString, app) + " / <br />" ) + (addresses[Accounts.PHONE_HOME] == null ? "-" : app.getPortalExtension().getTitle(addresses[Accounts.PHONE_HOME], locale, localeAsString, app)) %></td>
							<td><%= (businessEmail == null ? "- / " : "<a href='mailto:" + businessEmail + "'>" + businessEmail + "</a> /  <br />") + (homeEmail == null ? "-" : "<a href='mailto:" + homeEmail + "'>" + homeEmail + "</a>") %></td>
							<td>
<%
                if(account instanceof org.opencrx.kernel.account1.jmi1.Contact) {
%>
							    <a href='<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=vcard&accountXri=" + URLEncoder.encode(account.refMofId(), "UTF-8") %>'><img src='../../images/vcard.gif' alt='VCard' /></a>
<%
                }
%>
							</td>
						</tr>
<%
						n++;
						if(n > max)  break;
					}
%>
					<tr>
						<td colspan="6">
							<div class="letterBar">
								<a href="<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=search&startWith=" + URLEncoder.encode(startWith) + "&max=50"  %>">50</a>
								<a href="<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=search&startWith=" + URLEncoder.encode(startWith) + "&max=100"  %>">100</a>
								<a href="<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=search&startWith=" + URLEncoder.encode(startWith) + "&max=200"  %>">200</a>
								<a href="<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=search&startWith=" + URLEncoder.encode(startWith) + "&max=500"  %>">500</a>&nbsp;&nbsp;&nbsp;
							</div>
						</td>
					</tr>
			   </table>
			</fieldset>
<%
		}
		// Account details
		else {
			short locale =  app.getCurrentLocaleAsIndex();
			String localeAsString =  app.getCurrentLocaleAsString();
			org.opencrx.kernel.account1.jmi1.Account account = null;
			org.opencrx.kernel.account1.jmi1.AccountAddress[] addresses = new org.opencrx.kernel.account1.jmi1.AccountAddress[12];
			if(!accountXri.startsWith("new")) {
				account = (org.opencrx.kernel.account1.jmi1.Account)pm.getObjectById(new Path(accountXri));
				addresses = Accounts.getMainAddresses(account);
			}
			String fSalutation = request.getParameter("salutation");
			String fName = request.getParameter("name");
			String fFirstName = request.getParameter("firstName");
			String fMiddleName = request.getParameter("middleName");
			String fLastName = request.getParameter("lastName");
			String fWorkPhone = request.getParameter("workPhone");
			String fHomePhone = request.getParameter("homePhone");
			String fMobile = request.getParameter("mobile");
			String fOtherPhone = request.getParameter("otherPhone");
			String fJobTitle = request.getParameter("jobTitle");
			String fCompanyName = request.getParameter("companyName");
			String fNotes = request.getParameter("notes");
			String fBusinessEmail = request.getParameter("businessEmail");
			String fHomeEmail = request.getParameter("homeEmail");
			String fBusinessWeb = request.getParameter("businessWeb");
			String fHomeWeb = request.getParameter("homeWeb");
			String fBusinessPostalAddressLines = request.getParameter("businessPostalAddressLines");
			String fHomePostalAddressLines = request.getParameter("homePostalAddressLines");
			String fBusinessPostalStreet = request.getParameter("businessPostalStreet");
			String fHomePostalStreet = request.getParameter("homePostalStreet");
			String fBusinessLocation = request.getParameter("businessPostalLocation");
			String fHomeLocation = request.getParameter("homePostalLocation");
			String fCategories = request.getParameter("categories");

			// Save account
			if("apply".equalsIgnoreCase(command)) {
				try {
					UUIDGenerator uuids = UUIDs.getGenerator();
					if(account == null) {
						if("newContact".equals(accountXri)) {
							account = accountPkg.getContact().createContact();
						}
						else if("newLegalEntity".startsWith(accountXri)) {
							account = accountPkg.getLegalEntity().createLegalEntity();
						}
						else if("newUnspecifiedAccount".equals(accountXri)) {
							account = accountPkg.getUnspecifiedAccount().createUnspecifiedAccount();
						}
						else if("newGroup".equals(accountXri)) {
							account = accountPkg.getGroup().createGroup();
						}
						else {
							account = accountPkg.getContact().createContact();
						}
						account.refInitialize(false, false);
						try {
							pm.currentTransaction().begin();
							accountSegment.addAccount(
								false,
								uuids.next().toString(),
								account
							);
							pm.currentTransaction().commit();
						}
						catch(Exception e) {
							try {
								pm.currentTransaction().rollback();
							} catch(Exception e0) {}
						}
						accountXri = account.refMofId();
						account = (org.opencrx.kernel.account1.jmi1.Account)pm.getObjectById(new Path(accountXri));
					}
					pm.currentTransaction().begin();
					if(account instanceof org.opencrx.kernel.account1.jmi1.Contact) {
						org.opencrx.kernel.account1.jmi1.Contact contact = (org.opencrx.kernel.account1.jmi1.Contact)account;
						contact.setSalutationCode(fSalutation == null ? (short)0 : Short.valueOf(fSalutation).shortValue());
						contact.setFirstName(fFirstName);
						contact.setMiddleName(fMiddleName);
						contact.setLastName(fLastName);
						contact.setJobTitle(fJobTitle);
						contact.setOrganization(fCompanyName);
					}
					else if(account instanceof org.opencrx.kernel.account1.jmi1.AbstractGroup) {
						org.opencrx.kernel.account1.jmi1.AbstractGroup group = (org.opencrx.kernel.account1.jmi1.AbstractGroup)account;
						group.setName(fName);
					}
					account.setDescription(fNotes);
					if(fCategories != null) {
						String[] categories = fCategories.split("\r\n");
						account.getCategory().clear();
						account.getCategory().addAll(Arrays.asList(categories));
					}
					// workPhone
					if((fWorkPhone == null) || (fWorkPhone.length() == 0)) {
						if(addresses[Accounts.PHONE_BUSINESS] != null) {
							addresses[Accounts.PHONE_BUSINESS].refDelete();
						}
					}
					else {
						if(addresses[Accounts.PHONE_BUSINESS] == null) {
							org.opencrx.kernel.account1.jmi1.PhoneNumber newAddress = accountPkg.getPhoneNumber().createPhoneNumber();
							newAddress.refInitialize(false, false);
							newAddress.setAutomaticParsing(true);
							newAddress.getUsage().add(Addresses.USAGE_BUSINESS);
							account.addAddress(
								false,
								uuids.next().toString(),
								newAddress
							);
							addresses[Accounts.PHONE_BUSINESS] = newAddress;
						}
						((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.PHONE_BUSINESS]).setPhoneNumberFull(fWorkPhone);
					}
					// homePhone
					if((fHomePhone == null) || (fHomePhone.length() == 0)) {
						if(addresses[Accounts.PHONE_HOME] != null) {
							addresses[Accounts.PHONE_HOME].refDelete();
						}
					}
					else {
						if(addresses[Accounts.PHONE_HOME] == null) {
							org.opencrx.kernel.account1.jmi1.PhoneNumber newAddress = accountPkg.getPhoneNumber().createPhoneNumber();
							newAddress.refInitialize(false, false);
							newAddress.setAutomaticParsing(true);
							newAddress.getUsage().add(Addresses.USAGE_HOME);
							account.addAddress(
								false,
								uuids.next().toString(),
								newAddress
							);
							addresses[Accounts.PHONE_HOME] = newAddress;
						}
						((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.PHONE_HOME]).setPhoneNumberFull(fHomePhone);
					}
					// mobile
					if((fMobile == null) || (fMobile.length() == 0)) {
						if(addresses[Accounts.MOBILE] != null) {
							addresses[Accounts.MOBILE].refDelete();
						}
					}
					else {
						if(addresses[Accounts.MOBILE] == null) {
							org.opencrx.kernel.account1.jmi1.PhoneNumber newAddress = accountPkg.getPhoneNumber().createPhoneNumber();
							newAddress.refInitialize(false, false);
							newAddress.setAutomaticParsing(true);
							newAddress.getUsage().add(Addresses.USAGE_MOBILE);
							account.addAddress(
								false,
								uuids.next().toString(),
								newAddress
							);
							addresses[Accounts.MOBILE] = newAddress;
						}
						((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.MOBILE]).setPhoneNumberFull(fMobile);
					}
					// otherPhone
					if((fOtherPhone == null) || (fOtherPhone.length() == 0)) {
						if(addresses[Accounts.PHONE_OTHER] != null) {
							addresses[Accounts.PHONE_OTHER].refDelete();
						}
					}
					else {
						if(addresses[Accounts.PHONE_OTHER] == null) {
							org.opencrx.kernel.account1.jmi1.PhoneNumber newAddress = accountPkg.getPhoneNumber().createPhoneNumber();
							newAddress.refInitialize(false, false);
							newAddress.setAutomaticParsing(true);
							newAddress.getUsage().add(Addresses.USAGE_OTHER);
							account.addAddress(
								false,
								uuids.next().toString(),
								newAddress
							);
							addresses[Accounts.PHONE_OTHER] = newAddress;
						}
						((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.PHONE_OTHER]).setPhoneNumberFull(fOtherPhone);
					}
					// businessEmail
					if((fBusinessEmail == null) || (fBusinessEmail.length() == 0)) {
						if(addresses[Accounts.MAIL_BUSINESS] != null) {
							addresses[Accounts.MAIL_BUSINESS].refDelete();
						}
					}
					else {
						if(addresses[Accounts.MAIL_BUSINESS] == null) {
							org.opencrx.kernel.account1.jmi1.EmailAddress newAddress = accountPkg.getEmailAddress().createEmailAddress();
							newAddress.refInitialize(false, false);
							newAddress.getUsage().add(Addresses.USAGE_BUSINESS);
							account.addAddress(
								false,
								uuids.next().toString(),
								newAddress
							);
							addresses[Accounts.MAIL_BUSINESS] = newAddress;
						}
						((org.opencrx.kernel.account1.jmi1.EmailAddress)addresses[Accounts.MAIL_BUSINESS]).setEmailAddress(fBusinessEmail);
					}
					// homeEmail
					if((fHomeEmail == null) || (fHomeEmail.length() == 0)) {
						if(addresses[Accounts.MAIL_HOME] != null) {
							addresses[Accounts.MAIL_HOME].refDelete();
						}
					}
					else {
						if(addresses[Accounts.MAIL_HOME] == null) {
							org.opencrx.kernel.account1.jmi1.EmailAddress newAddress = accountPkg.getEmailAddress().createEmailAddress();
							newAddress.refInitialize(false, false);
							newAddress.getUsage().add(Addresses.USAGE_HOME);
							account.addAddress(
								false,
								uuids.next().toString(),
								newAddress
							);
							addresses[Accounts.MAIL_HOME] = newAddress;
						}
						((org.opencrx.kernel.account1.jmi1.EmailAddress)addresses[Accounts.MAIL_HOME]).setEmailAddress(fHomeEmail);
					}
					// businessWeb
					if((fBusinessWeb == null) || (fBusinessWeb.length() == 0)) {
						if(addresses[Accounts.WEB_BUSINESS] != null) {
							addresses[Accounts.WEB_BUSINESS].refDelete();
						}
					}
					else {
						if(addresses[Accounts.WEB_BUSINESS] == null) {
							org.opencrx.kernel.account1.jmi1.WebAddress newAddress = accountPkg.getWebAddress().createWebAddress();
							newAddress.refInitialize(false, false);
							newAddress.getUsage().add(Addresses.USAGE_BUSINESS);
							account.addAddress(
								false,
								uuids.next().toString(),
								newAddress
							);
							addresses[Accounts.WEB_BUSINESS] = newAddress;
						}
						((org.opencrx.kernel.account1.jmi1.WebAddress)addresses[Accounts.WEB_BUSINESS]).setWebUrl(fBusinessWeb);
					}
					// homeWeb
					if((fHomeWeb == null) || (fHomeWeb.length() == 0)) {
						if(addresses[Accounts.WEB_HOME] != null) {
							addresses[Accounts.WEB_HOME].refDelete();
						}
					}
					else {
						if(addresses[Accounts.WEB_HOME] == null) {
							org.opencrx.kernel.account1.jmi1.WebAddress newAddress = accountPkg.getWebAddress().createWebAddress();
							newAddress.refInitialize(false, false);
							newAddress.getUsage().add(Addresses.USAGE_HOME);
							account.addAddress(
								false,
								uuids.next().toString(),
								newAddress
							);
							addresses[Accounts.WEB_HOME] = newAddress;
						}
						((org.opencrx.kernel.account1.jmi1.WebAddress)addresses[Accounts.WEB_HOME]).setWebUrl(fHomeWeb);
					}
					// businessPostal
					if(
						((fBusinessPostalAddressLines == null) || (fBusinessPostalAddressLines.length() == 0)) &&
						((fBusinessPostalStreet == null) || (fBusinessPostalStreet.length() == 0)) &&
						((fBusinessLocation == null) || (fBusinessLocation.length() == 0))
					) {
						if(addresses[Accounts.POSTAL_BUSINESS] != null) {
							addresses[Accounts.POSTAL_BUSINESS].refDelete();
						}
					}
					else {
						if(addresses[Accounts.POSTAL_BUSINESS] == null) {
							org.opencrx.kernel.account1.jmi1.PostalAddress newAddress = accountPkg.getPostalAddress().createPostalAddress();
							newAddress.refInitialize(false, false);
							newAddress.getUsage().add(Addresses.USAGE_BUSINESS);
							account.addAddress(
								false,
								uuids.next().toString(),
								newAddress
							);
							addresses[Accounts.POSTAL_BUSINESS] = newAddress;
						}
						String[] addressLines = fBusinessPostalAddressLines == null ? new String[0] : fBusinessPostalAddressLines.split("\r\n");
						String[] street = fBusinessPostalStreet == null ? new String[0] : fBusinessPostalStreet.split("\r\n");
						String[] location = fBusinessLocation == null ? new String[0] : fBusinessLocation.split("\r\n");
						((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalAddressLine().clear();
						((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalAddressLine().addAll(Arrays.asList(addressLines));
						((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalStreet().clear();
						((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalStreet().addAll(Arrays.asList(street));
						if(location.length > 0) {
							String postalCode = null;
							String city = null;
							int pos = location[0].indexOf(" ");
							if(pos > 0) {
								postalCode = location[0].substring(0, pos).trim();
								city = location[0].substring(pos+1).trim();
							}
							else if(location[0].length() > 0) {
								city = location[0].trim();
							}
							((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).setPostalCode(postalCode);
							((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).setPostalCity(city);
						}
						if(location.length > 1) {
							Map countries = codes.getLongText("org:opencrx:kernel:address1:PostalAddressable:postalCountry", locale, true, true);
							for(Iterator i = countries.entrySet().iterator(); i.hasNext(); ) {
								Map.Entry entry = (Map.Entry)i.next();
								Number countryCode = (Number)entry.getKey();
								String countryName = (String)entry.getValue();
								if(countryName.startsWith(location[1])) {
									((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).setPostalCountry(countryCode.shortValue());
									break;
								}
							}
						}
					}
					// homePostal
					if(
						((fHomePostalAddressLines == null) || (fHomePostalAddressLines.length() == 0)) &&
						((fHomePostalStreet == null) || (fHomePostalStreet.length() == 0)) &&
						((fHomeLocation == null) || (fHomeLocation.length() == 0))
					) {
						if(addresses[Accounts.POSTAL_HOME] != null) {
							addresses[Accounts.POSTAL_HOME].refDelete();
						}
					}
					else {
						if(addresses[Accounts.POSTAL_HOME] == null) {
							org.opencrx.kernel.account1.jmi1.PostalAddress newAddress = accountPkg.getPostalAddress().createPostalAddress();
							newAddress.refInitialize(false, false);
							newAddress.getUsage().add(Addresses.USAGE_HOME);
							account.addAddress(
								false,
								uuids.next().toString(),
								newAddress
							);
							addresses[Accounts.POSTAL_HOME] = newAddress;
						}
						String[] addressLines = fHomePostalAddressLines == null ? new String[0] : fHomePostalAddressLines.split("\r\n");
						String[] street = fHomePostalStreet == null ? new String[0] : fHomePostalStreet.split("\r\n");
						String[] location = fHomeLocation == null ? new String[0] : fHomeLocation.split("\r\n");
						((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalAddressLine().clear();
						((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalAddressLine().addAll(Arrays.asList(addressLines));
						((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalStreet().clear();
						((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalStreet().addAll(Arrays.asList(street));
						if(location.length > 0) {
							String postalCode = null;
							String city = null;
							int pos = location[0].indexOf(" ");
							if(pos > 0) {
								postalCode = location[0].substring(0, pos).trim();
								city = location[0].substring(pos+1).trim();
							}
							else if(location[0].length() > 0) {
								city = location[0].trim();
							}
							((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).setPostalCode(postalCode);
							((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).setPostalCity(city);
						}
						if(location.length > 1) {
							Map countries = codes.getLongText("org:opencrx:kernel:address1:PostalAddressable:postalCountry", locale, true, true);
							for(Iterator i = countries.entrySet().iterator(); i.hasNext(); ) {
								Map.Entry entry = (Map.Entry)i.next();
								Number countryCode = (Number)entry.getKey();
								String countryName = (String)entry.getValue();
								if(countryName.startsWith(location[1])) {
									((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).setPostalCountry(countryCode.shortValue());
									break;
								}
							}
						}
					}
					pm.currentTransaction().commit();
				}
				catch(Exception e) {
					new ServiceException(e).log();
					try {
						pm.currentTransaction().rollback();
					} catch(Exception e0) {}
				}
				account = (org.opencrx.kernel.account1.jmi1.Account)pm.getObjectById(new Path(accountXri));
				addresses = Accounts.getMainAddresses(account);
			}
%>
		<ul class="nav">
		</ul>
		<form method="post" action="<%= WIZARD_NAME %>">
			<input type="hidden" name="command" value="apply"/>
			<input type="hidden" name="accountXri" value="<%= accountXri %>"/>
			<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= requestId %>" />
			<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= objectXri %>" />
			<div class="col1">
				<fieldset>
<%
					if(
						"newContact".equals(accountXri) ||
						account instanceof org.opencrx.kernel.account1.jmi1.Contact
					) {
						org.opencrx.kernel.account1.jmi1.Contact contact = (org.opencrx.kernel.account1.jmi1.Contact)account;
%>
						<legend>Person</legend>
						<table>
							<tr>
								<td><label for="salutation">Salutation:</label></td>
								<td>
									<select id="salutation" name="salutation">
<%
										short salutationCode = (short)0;
										try {
											salutationCode = contact == null ? (short)0 : contact.getSalutationCode();
										} catch(Exception e) {}
										SortedMap salutations = codes.getLongText("org:opencrx:kernel:account1:Contact:salutationCode", locale, false, false);
										for(Iterator options = salutations.entrySet().iterator(); options.hasNext(); ) {
											Map.Entry option = (Map.Entry)options.next();
											short optionValue = ((Number)option.getValue()).shortValue();
											String selectedModifier = optionValue == salutationCode
												? "selected"
												: "";
%>
											<option <%= selectedModifier %> value="<%= optionValue %>"><%= option.getKey() %>
<%
										}
%>
									</select>
								</td>
							</tr>
							<tr><td><label for="firstName">First:</label></td>
							<td><input type="text" id="firstName" name="firstName"  value="<%= contact == null || contact.getFirstName() == null ? "" : contact.getFirstName() %>"/></td></tr>
							<tr><td><label for="lastName">Middle:</label></td>
							<td><input type="text" id="middleName" name="middleName"  value="<%= contact == null || contact.getMiddleName() == null ? "" : contact.getMiddleName() %>"/></td></tr>
							<tr><td><label for="lastName">Last:</label></td>
							<td><input type="text" id="lastName" name="lastName"  value="<%= contact == null || contact.getLastName() == null ? "" : contact.getLastName() %>"/>
							<tr><td><label for="jobTitle">Title:</label></td><td><input type="text" id="jobTitle" name="jobTitle"  value="<%= contact == null || contact.getJobTitle() == null ? "" : contact.getJobTitle() %>"/></td></tr>
							<tr><td><label for="companyName">Company:</label></td><td><input type="text" id="companyName" name="companyName"  value="<%= contact == null || contact.getOrganization() == null ? "" : contact.getOrganization() %>"/></td></tr>
						</table>
<%
					}
					else {
						org.opencrx.kernel.account1.jmi1.AbstractGroup group = (org.opencrx.kernel.account1.jmi1.AbstractGroup)account;
%>
						<legend>Name</legend>
						<table>
							<tr><td><label for="name">Name:</label></td>
							<td><input type="text" id="name" name="name"  value="<%= group == null || group.getName() == null ? "" : group.getName() %>"/></td></tr>
						</table>
<%
					}
%>
				</fieldset>
				<fieldset>
					<legend>Phone</legend>
					<table>
						<tr><td><label for="workPhone">Work Phone:</label></td>
						<td><input type="text" id="workPhone" name="workPhone"  value="<%= addresses[Accounts.PHONE_BUSINESS] == null ? "" : app.getPortalExtension().getTitle(addresses[Accounts.PHONE_BUSINESS], locale, localeAsString, app) %>"/></td></tr>
						<tr><td><label for="homePhone">Home Phone:</label></td>
						<td><input type="text" id="homePhone" name="homePhone"  value="<%= addresses[Accounts.PHONE_HOME] == null ? "" : app.getPortalExtension().getTitle(addresses[Accounts.PHONE_HOME], locale, localeAsString, app) %>"/></td></tr>
						<tr><td><label for="mobile">Mobile:</label></td>
						<td><input type="text" id="mobile" name="mobile"  value="<%= addresses[Accounts.MOBILE] == null ? "" : app.getPortalExtension().getTitle(addresses[Accounts.MOBILE], locale, localeAsString, app) %>"/></td></tr>
						<tr><td><label for="otherPhone">Other Phone:</label></td>
						<td><input type="text" id="otherPhone" name="otherPhone"  value="<%= addresses[Accounts.PHONE_OTHER] == null ? "" : app.getPortalExtension().getTitle(addresses[Accounts.PHONE_OTHER], locale, localeAsString, app) %>"/></td></tr>
					</table>
				</fieldset>
				<fieldset>
					<legend>Other</legend>
					<label for="notes">Notes:</label>
					<textarea name="notes" id="notes" rows="10" cols="25"><%= account == null || account.getDescription() == null ? "" : account.getDescription() %></textarea>
				</fieldset>
			</div>
			<div class="col2">
				<fieldset>
					<legend>Email</legend>
					<table>
						<tr><td><label for="businessEmail">Work email:</label></td><td><input type="text" id="businessEmail" name="businessEmail"  value="<%= addresses[Accounts.MAIL_BUSINESS] == null ? "" : ((org.opencrx.kernel.address1.jmi1.EmailAddressable)addresses[Accounts.MAIL_BUSINESS]).getEmailAddress() %>"/></td></tr>
						<tr><td><label for="homeEmail">Home email:</label></td><td><input type="text" id="homeEmail" name="homeEmail"  value="<%= addresses[Accounts.MAIL_HOME] == null ? "" : ((org.opencrx.kernel.address1.jmi1.EmailAddressable)addresses[Accounts.MAIL_HOME]).getEmailAddress() %>"/></td></tr>
					</table>
				</fieldset>
				<fieldset>
					<legend>Web</legend>
					<table>
						<tr><td><label for="businessWeb">Work web:</label></td><td><input type="text" id="businessWeb" name="businessWeb"  value="<%= addresses[Accounts.WEB_BUSINESS] == null ? "" : ((org.opencrx.kernel.address1.jmi1.WebAddressable)addresses[Accounts.WEB_BUSINESS]).getWebUrl() %>"/></td></tr>
						<tr><td><label for="homeWeb">Home web:</label></td><td><input type="text" id="homeWeb" name="homeWeb"  value="<%= addresses[Accounts.WEB_HOME] == null ? "" : ((org.opencrx.kernel.address1.jmi1.WebAddressable)addresses[Accounts.WEB_HOME]).getWebUrl() %>"/></td></tr>
					</table>
				</fieldset>
				<fieldset>
					<legend>Address</legend>
					<table>
						<tr>
<%
							// Business Postal
							StringBuilder businessPostalAddressLines = new StringBuilder();
							StringBuilder businessPostalStreet = new StringBuilder();
							StringBuilder businessLocation = new StringBuilder();
							if(addresses[Accounts.POSTAL_BUSINESS] != null) {
								org.opencrx.kernel.account1.jmi1.PostalAddress postalAddress = (org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS];
								for(Iterator i = postalAddress.getPostalAddressLine().iterator(); i.hasNext(); ) {
									String postalAddressLine = (String)i.next();
									if(businessPostalAddressLines.length() > 0) businessPostalAddressLines.append("\r\n");
									businessPostalAddressLines.append(postalAddressLine);
								}
								for(Iterator i = postalAddress.getPostalStreet().iterator(); i.hasNext(); ) {
									String postalStreet = (String)i.next();
									if(businessPostalStreet.length() > 0) businessPostalStreet.append("\n");
									businessPostalStreet.append(postalStreet);
								}
								businessLocation.append(postalAddress.getPostalCode());
								businessLocation.append(" ");
								businessLocation.append(postalAddress.getPostalCity());
								businessLocation.append("\n");
								try {
									businessLocation.append(app.getCodes().getLongText("org:opencrx:kernel:address1:PostalAddressable:postalCountry", locale, true, true).get(new Short(postalAddress.getPostalCountry())));
								} catch(Exception e) {}
							}
							// Home Postal
							StringBuilder homePostalAddressLines = new StringBuilder();
							StringBuilder homePostalStreet = new StringBuilder();
							StringBuilder homeLocation = new StringBuilder();
							if(addresses[Accounts.POSTAL_HOME] != null) {
								org.opencrx.kernel.account1.jmi1.PostalAddress postalAddress = (org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME];
								for(Iterator i = postalAddress.getPostalAddressLine().iterator(); i.hasNext(); ) {
									String postalAddressLine = (String)i.next();
									if(homePostalAddressLines.length() > 0) homePostalAddressLines.append("\n");
									homePostalAddressLines.append(postalAddressLine);
								}
								for(Iterator i = postalAddress.getPostalStreet().iterator(); i.hasNext(); ) {
									String postalStreet = (String)i.next();
									if(homePostalStreet.length() > 0) homePostalStreet.append("\n");
									homePostalStreet.append(postalStreet);
								}
								homeLocation.append(postalAddress.getPostalCode());
								homeLocation.append(" ");
								homeLocation.append(postalAddress.getPostalCity());
								homeLocation.append("\n");
								try {
									homeLocation.append(app.getCodes().getLongText("org:opencrx:kernel:address1:PostalAddressable:postalCountry", locale, true, true).get(new Short(postalAddress.getPostalCountry())));
								} catch(Exception e) {}
							}
%>
							<td><label for="businessPostalAddressLines">Work:</label><textarea name="businessPostalAddressLines" id="businessPostalAddressLines" rows="2" cols="25"><%= businessPostalAddressLines %></textarea></td>
							<td><label for="homePostalAddressLines">Home:</label><textarea name="homePostalAddressLines" id="homePostalAddressLines" rows="2" cols="25"><%= homePostalAddressLines %></textarea></td>
						</tr>
						<tr>
							<td><textarea name="businessPostalStreet" id="businessPostalStreet" rows="2" cols="25"><%= businessPostalStreet %></textarea></td>
							<td><textarea name="homePostalStreet" id="homePostalStreet" rows="2" cols="25"><%= homePostalStreet %></textarea></td>
						</tr>
						<tr>
							<td><textarea name="businessPostalLocation" id="businessPostalLocation" rows="2" cols="25"><%= businessLocation %></textarea></td>
							<td><textarea name="homePostalLocation" id="homePostalLocation" rows="2" cols="25"><%= homeLocation %></textarea></td>
						</tr>
					</table>
				</fieldset>
				<fieldset>
					<legend>Categories</legend>
<%
						StringBuilder categories = new StringBuilder();
						if(account != null) {
							for(Iterator i = account.getCategory().iterator(); i.hasNext(); ) {
								String category = (String)i.next();
								if(categories.length() > 0) categories.append("\n");
								categories.append(category);
							}
						}
%>
					<textarea name="categories" id="categories" rows="6" cols="25"><%= categories %></textarea>
				</fieldset>
			</div>
			<div class="buttons">
				<input type="submit" value="Apply"  class="button" />
<%
				String lastQuery = (String)session.getAttribute(this.getClass().getName() + ".lastQuery");
%>
				<input type="button" value="Cancel" onclick="javascript:location.href='<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&" + (lastQuery == null ? "command=exit" : lastQuery) %>';" class="button" />
			</div>
		</form>
<%
		}
%>
		</body>
		</html>
<%
	}
%>
