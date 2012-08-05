<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.openmdx.org/
 * Name:        $Id: FetchEMail.jsp,v 1.2 2012/01/20 12:50:55 cmu Exp $
 * Description: UploadEMail
 * Revision:    $Revision: 1.2 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2012/01/20 12:50:55 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2010, CRIXP AG, Switzerland
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
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
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
org.openmdx.kernel.log.*,
org.openmdx.kernel.exception.BasicException,
org.openmdx.uses.org.apache.commons.fileupload.*,
org.openmdx.kernel.id.*,
org.opencrx.kernel.backend.*,
java.io.InputStream,
java.util.Calendar,
java.util.GregorianCalendar,
java.util.Properties,
javax.mail.*,
javax.mail.internet.*,
javax.mail.search.*
" %>
<%!

%>
<%
	request.setCharacterEncoding("UTF-8");
	String servletPath = "." + request.getServletPath();
	String servletPathPrefix = servletPath.substring(0, servletPath.lastIndexOf("/") + 1);
	ApplicationContext app = (ApplicationContext)session.getValue(WebKeys.APPLICATION_KEY);
	ViewsCache viewsCache = (ViewsCache)session.getValue(WebKeys.VIEW_CACHE_KEY_SHOW);
	String objectXri = request.getParameter(Action.PARAMETER_OBJECTXRI);
	// May be invoked by WorkflowController --> requestId is optional in this wizard
	if(app == null || objectXri == null) {
		System.out.println("aborting... (ApplicationContext == null)");
	  response.sendRedirect(
	       request.getContextPath() + "/" + WebKeys.SERVLET_NAME
	    );
		return;
	}
	javax.jdo.PersistenceManager pm = app.getNewPmData();
	RefObject_1_0 obj = (RefObject_1_0)pm.getObjectById(new Path(objectXri));
	String providerName = new Path(objectXri).get(2);
	String segmentName = new Path(objectXri).get(4);
	Texts_1_0 texts = app.getTexts();
	Codes codes = app.getCodes();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html dir="<%= texts.getDir() %>">
<head>
	<title>openCRX - Fetch E-Mail</title>
	<meta name="label" content="Fetch E-Mail">
	<meta name="toolTip" content="Fetch E-Mail">
	<meta name="targetType" content="_blank">
	<meta name="forClass" content="org:opencrx:kernel:activity1:Segment">
	<meta name="forClass" content="org:opencrx:kernel:activity1:ActivityCreator">
	<meta name="order" content="8000">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link href="../../_style/colors.css" rel="stylesheet" type="text/css">
	<link href="../../_style/n2default.css" rel="stylesheet" type="text/css">
	<link rel='shortcut icon' href='../../images/favicon.ico' />
	<style type="text/css" media="all">
		.resultTable {
			border-collapse: separate;
			border-spacing:2px;
			border:1px solid grey;
			width:100%;
		}
		.resultTable TD {
			border-spacing:1px;
			border:1px solid grey;
			text-align:left;
			padding:0px 2px;
		}
		.error {background-color:red;color:white;}
		.ok{background-color:#C0FF00;}
		fieldset{width:500px;}
  </style>
</head>
<body>
<div id="container">
	<div id="wrap">
		<div id="eheader">
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
			<div id="econtent">
<%
				try{
					final String formName = "FetchEMail";
					final String wizardName = formName + ".jsp";
					final String SUBMIT_HANDLER = "javascript:document.getElementById('command').value=this.name;";
					final String ACTIVITYCREATOR_CLASS = "org:opencrx:kernel:activity1:ActivityCreator";

					TimeZone timezone = TimeZone.getTimeZone(app.getCurrentTimeZone());
					SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm", app.getCurrentLocale());
					timeFormat.setTimeZone(timezone);

					// Get Parameters
					String command = request.getParameter("command");
					if(command == null) command = "";
					boolean actionOk = "ok.button".equals(command);
					boolean actionCancel = "cancel.button".equals(command);
					//System.out.println("command = " + command);
					boolean isFirstCall = request.getParameter("isFirstCall") == null; // used to properly initialize various options
					String host         = request.getParameter("host");
					String protocol     = request.getParameter("protocol");
					String port         = request.getParameter("port");
					String user         = request.getParameter("user");
					String password     = request.getParameter("password");
					String messageCount = request.getParameter("messageCount");
					String activityCreatorXri = request.getParameter("activityCreatorXri");
					if (isFirstCall) {
						if (obj instanceof org.opencrx.kernel.activity1.jmi1.ActivityCreator){
							activityCreatorXri = ((org.opencrx.kernel.activity1.jmi1.ActivityCreator)obj).refMofId();
						}
					}
					
					if (host     == null)     {host         = "server.mail.com";}
					if (protocol == null)     {protocol     = "imap";}
					if (port     == null)     {port         = "143";}
					if (user     == null)     {user         = "username";}
					if (password == null)     {password     = "password";}
					if (messageCount == null) {messageCount = "10";}

					int tabIndex = 100;
%>
					<form id="<%= formName %>" name="<%= formName %>" accept-charset="UTF-8" method="POST" action="<%= wizardName %>">
						<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= objectXri %>" />
						<input type="checkbox" style="display:none;" name="isFirstCall" checked />
						<input type="hidden" id="command" name="command" value="" />
						<fieldset>
								<table class="fieldGroup">

									<tr>
										<td class="label"><span class="nw">Host</span></td>
										<td nowrap><input type="text" class="valueL" name="host" tabindex="<%= tabIndex++ %>" value="<%= host %>" /></td>
										<td class="addon" />
									</tr>

									<tr>
										<td class="label"><span class="nw">Protocol</span></td>
										<td nowrap>
											<select class="valueL" name="protocol" id="protocol" tabindex="<%= tabIndex++ %>" onChange="javascript:
												if(this.value=='imap')  {document.getElementById('port').value='143';}
												if(this.value=='imaps') {document.getElementById('port').value='993';}
												if(this.value=='pop3')  {document.getElementById('port').value='110';}
												if(this.value=='pop3s') {document.getElementById('port').value='993';}
												document.getElementById('reload.button').click();
											  ">
	                      <option value="imap"  <%= protocol != null &&  "imap".compareTo(protocol) == 0 ? "selected" : "" %>>imap</option>
	                      <option value="imaps" <%= protocol != null && "imaps".compareTo(protocol) == 0 ? "selected" : "" %>>imaps</option>
	                      <option value="pop3"  <%= protocol != null &&  "pop3".compareTo(protocol) == 0 ? "selected" : "" %>>pop3</option>
	                      <option value="pop3s" <%= protocol != null && "pop3s".compareTo(protocol) == 0 ? "selected" : "" %>>pop3s</option>
										</td>
										<td class="addon" />
									</tr>

									<tr>
										<td class="label"><span class="nw">Port</span></td>
										<td nowrap><input type="text" class="valueL" name="port" id="port" tabindex="<%= tabIndex++ %>" value="<%= port %>" /></td>
										<td class="addon" />
									</tr>

									<tr>
										<td class="label"><span class="nw">User</span></td>
										<td nowrap><input type="text" class="valueL" name="user" tabindex="<%= tabIndex++ %>" value="<%= user %>" /></td>
										<td class="addon" />
									</tr>

									<tr>
										<td class="label"><span class="nw">Password</span></td>
										<td nowrap><input type="text" class="valueL" name="password" tabindex="<%= tabIndex++ %>" value="<%= password %>" /></td>
										<td class="addon" />
									</tr>

									<tr>
										<td class="label"><span class="nw">max messages to import</span></td>
										<td nowrap><input type="text" class="valueL" name="messageCount" tabindex="<%= tabIndex++ %>" value="<%= messageCount %>" /></td>
										<td class="addon" />
									</tr>

                  <tr>
	                  <td class="label"><span class="nw"><%= app.getLabel(ACTIVITYCREATOR_CLASS) %>:</span></td>
	                  <td>
	                    <select class="valueL" name="activityCreatorXri" tabindex="<%= tabIndex++ %>">
	                      <option value="0">Default</option>
<%
	                      // get ActivityCreators sorted by name (asc) for SalesRep responsible for customer
	                      org.opencrx.kernel.activity1.cci2.ActivityCreatorQuery activityCreatorFilter = org.opencrx.kernel.utils.Utils.getActivityPackage(pm).createActivityCreatorQuery();
	                      activityCreatorFilter.orderByName().ascending();
	                      activityCreatorFilter.forAllActivityType().activityClass().equalTo(new Short((short)0));
	                      /*  0 = E-mail
			                      1 = DEPRECATED (was Fax)
			                      2 = Incident
			                      3 = Mailing
			                      4 = Meeting
			                      5 = DEPRECATED (was MMS)
			                      6 = Phone Call
			                      7 = DEPRECATED (was SMS)
			                      8 = Task
			                      9 = Absense
			                     10 = External Activity
			                     11 = Sales Visit
			                  */
	
	                      int maxCreator = 200;
	                      int counter = 0;
	                      org.opencrx.kernel.activity1.jmi1.Segment activitySegment = Activities.getInstance().getActivitySegment(pm, providerName, segmentName);
	                      for (
	                        Iterator j = activitySegment.getActivityCreator(activityCreatorFilter).iterator();
	                        j.hasNext() && counter < maxCreator;
	                      ) {
	
	                        String selectedModifier = "";
	                        // get ActivityCreator
	                  	  	org.opencrx.kernel.activity1.jmi1.ActivityCreator activityCreator =
	                          (org.opencrx.kernel.activity1.jmi1.ActivityCreator)j.next();
	
	                        counter++;
	                        selectedModifier = (activityCreatorXri != null) && (activityCreatorXri.compareTo((activityCreator.refMofId()).toString()) == 0) ? "selected" : "";
%>
	                        <option <%= selectedModifier %> value="<%= activityCreator.refMofId() %>"><%= activityCreator.getName() != null ? activityCreator.getName() : "???" %><%= activityCreator.getDescription() != null ? " / " + activityCreator.getDescription() : "" %></option>
<%
  	                    }
%>
    	                </select>
      		          </td>
       		          <td class="addon">&nbsp;</td>
       		        </tr>

									<tr>
										<td colspan="3">
											<input type="Submit" id="reload.button" name="reload.button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getReloadText() %>" style="display:none;" />
											<input type="submit" id="ok.button" name="ok.button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getOkTitle() %>" onclick="<%= SUBMIT_HANDLER %>" />
											<input type="submit" id="cancel.button" name="cancel.button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getCloseText() %>" onClick="javascript:window.close();" />
										</td>
									</tr>

								</table>
					</fieldset>
					</form>
					<br>
<%
					if (actionOk) {
							//System.out.println("importing e-mails...");
							int maxMessages = 10;
							try {
								maxMessages = Integer.parseInt(messageCount);
							} catch (Exception e) {
									new ServiceException(e).log();
							}
%>
							<table><tr><td><img id="email" src="../../images/EMail.gif" alt="openCRX" title="" /> openCRX FetchMail (importing at most <%= maxMessages %> messages at <%= timeFormat.format(new java.util.Date()) %>)<table class="resultTable">
							<tr>
								<td>Sent</td>
								<td>From</td>
								<td>Subject</td>
								<td>Message ID</td>
								<td>openCRX ID</td>
							</tr>
<%
							try {
									// connect to my pop3 inbox in read-only mode
									Properties properties = System.getProperties();
									// try to set port
									try {
											properties.setProperty("mail." + protocol + ".port", port);
									} catch (Exception e) {
											new ServiceException(e).log();
									}
		
									Session msession = Session.getDefaultInstance(properties);
									Store store = msession.getStore(protocol);
									store.connect(host, user, password);
									Folder inbox = store.getFolder("inbox");
									inbox.open(Folder.READ_WRITE);
									
									// search for all "unseen" messages
									Flags seen = new Flags(Flags.Flag.SEEN);
									FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
									Message messages[] = inbox.search(unseenFlagTerm);
									
									if (messages.length == 0) {
											//System.out.println("No unread messages found.");
%>
											<tr><td colspan="5">No unread messages found</td></tr>
<%
									} else {
											//System.out.println(messages.length + " unread Messages");
											for (int i = 0; i < messages.length && i < maxMessages; i++) {
%>
												<tr>							
<%
													try {
															MimeMessage mimeMessage = (MimeMessage)messages[i];
															String messageId = "NA";
															if (mimeMessage.getMessageID() != null) {
																	messageId = mimeMessage.getMessageID();
															}
%>
															<td><%= messages[i].getSentDate() %></td>
															<td><%= app.getHtmlEncoder().encode(messages[i].getFrom()[0].toString(), false) %></td>
															<td><%= app.getHtmlEncoder().encode(messages[i].getSubject(),            false) %></td>
															<td><%= app.getHtmlEncoder().encode(messageId,                           false) %></td>
<%
															/*
															System.out.println("Message " + (i + 1));
															System.out.println("From : " + messages[i].getFrom()[0]);
															System.out.println("Subject : " + messages[i].getSubject());
															System.out.println("Sent Date : " + messages[i].getSentDate());
															System.out.println();
															*/
															//System.out.println("importing msg-id: " + messageId);
															
															org.opencrx.kernel.activity1.jmi1.ActivityCreator activityCreator = null;
															if (activityCreatorXri != null && activityCreatorXri.length() > 0) {
																	try {
																		activityCreator = (org.opencrx.kernel.activity1.jmi1.ActivityCreator)pm.getObjectById(new Path(activityCreatorXri));
																	} catch (Exception e) {}
															}
						
															try {
																	List<org.opencrx.kernel.activity1.jmi1.EMail> emails = Activities.getInstance().importMimeMessage(
																		pm,
																		providerName,
																		segmentName,
																		mimeMessage,
																		activityCreator // maybe null --> default e-mail creator will be used
																	);
																	
																	try {
																		// mark as read following hint from http://www.jguru.com/faq/view.jsp?EID=305942
																		MimeMessage copy = new MimeMessage(mimeMessage);
																	} catch (Exception e) {}
%>
																	<td class="ok">
<%
																			for(int idx = 0; idx < emails.size(); idx++){
																					org.opencrx.kernel.activity1.jmi1.EMail email = emails.get(idx);
																					String emailHref = "";
																					Action action = new ObjectReference(
																							email,
																							app
																						).getSelectObjectAction();
																					emailHref = "../../" + action.getEncodedHRef();
																					String opencrxId = ((new Path(email.refMofId())).getLastComponent()).toString();
%>
																					<a href="<%= emailHref %>" target="_blank"><%= opencrxId %></a>
<%																	
																			}
%>
																	</td>
<%
															} catch (Exception e) {
																	new ServiceException(e).log();
%>
																	<td class="error">failed</td>
<%
															}
													} catch (Exception e) {
															new ServiceException(e).log();
													}
%>
												<tr>							
<%
											}
									}
									inbox.close(true);
									store.close();
							} catch (Exception e) {
									ServiceException e0 = new ServiceException(e);
									e0.log();
									out.println("<p><b>!! Failed !!<br><br>The following exception(s) occured:</b><br><br><pre>");
									PrintWriter pw = new PrintWriter(out);
									e0.printStackTrace(pw);
									out.println("</pre></p>");
							}
%>
							</table></td></tr></table>
<%
					}
				} catch (Exception e) {
						new ServiceException(e).log();
				}
%>
			</div> <!-- content -->
		</div> <!-- content-wrap -->
	</div> <!-- wrap -->
</div> <!-- container -->
</body>
</html>

