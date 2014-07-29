<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.openmdx.org/
 * Description: CustomerCareWizard
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2012-2013, CRIXP Corp., Switzerland
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
java.io.*,
java.text.*,
org.opencrx.kernel.backend.*,
org.opencrx.kernel.portal.wizard.*,
org.opencrx.kernel.generic.*,
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
org.openmdx.base.exception.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.component.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*
" %>
<%
	final String WIZARD_NAME = "CustomerCareWizard.jsp";
	CustomerCareWizardController wc = new CustomerCareWizardController();
%>
	<t:wizardHandleCommand controller='<%= wc %>' defaultCommand='Refresh' />
<%
	if(response.getStatus() != HttpServletResponse.SC_OK) {
		wc.close();		
		return;
	}
	ApplicationContext app = wc.getApp();
	javax.jdo.PersistenceManager pm = wc.getPm();
	RefObject_1_0 obj = wc.getObject();
	Texts_1_0 texts = wc.getTexts();
	ViewPort viewPort = wc.getViewPort(out);	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html dir="<%= texts.getDir() %>">
<head>
	<title><%= app.getApplicationName() + " - Customer Care (" + wc.getProviderName() + "/" + wc.getSegmentName() + ")" %></title>
	<meta name="label" content="Customer Care">
	<meta name="toolTip" content="Customer Care">
	<meta name="targetType" content="_self">
	<meta name="forClass" content="org:opencrx:kernel:home1:UserHome">
	<meta name="forClass" content="org:opencrx:kernel:account1:Account">
	<meta name="forClass" content="org:opencrx:kernel:activity1:ActivityGroup">
	<meta name="order" content="5555">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link href="../../_style/calendar-small.css" rel="stylesheet" type="text/css">	
	<link rel="stylesheet" href="../../javascript/bootstrap/css/bootstrap.min.css">	
	<link rel="stylesheet" href="../../_style/colors.css">
	<link rel="stylesheet" href="../../_style/n2default.css">
	<link rel="stylesheet" href="../../_style/ssf.css">
	<script type="text/javascript" src="../../javascript/portal-all.js"></script>
	<script type="text/javascript" src="../../javascript/calendar/lang/calendar-en_US.js"></script>
	<link rel='shortcut icon' href='../../images/favicon.ico' />
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
									<td id="headerCellMiddle" style="text-align:right;"><a class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" href="#" onclick="javascript:new Effect.Pulsate(this,{pulses:1,duration:0.5});window.location.href='./'+getEncodedHRef(['../../ObjectInspectorServlet', 'requestId', '<%= wc.getRequestId() %>', 'event', '<%= org.openmdx.portal.servlet.action.LogoffAction.EVENT_ID %>']);">Logoff</a></td>
									<td id="headerCellRight"><img id="logoRight" src="../../images/logoRight.gif" alt="" title="" /></td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</div>
		</div>
		<div id="content-wrap">
			<div id="content" style="padding:10px 0.5em 0px 0.5em;">
				<form id="<%= WIZARD_NAME %>" name="<%= WIZARD_NAME %>" method="post" accept-charset="UTF-8" action="<%= WIZARD_NAME %>">
					<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= wc.getRequestId() %>" />
					<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= wc.getObjectIdentity().toXRI() %>" />
					<input type="hidden" id="Command" name="Command" value="" />
					<input type="hidden" id="<%= CustomerCareWizardController.PARAMETER_SELECTED_OBJECT_XRI %>" name="<%= CustomerCareWizardController.PARAMETER_SELECTED_OBJECT_XRI %>" value="" />
					<table cellspacing="8" class="tableLayout">
						<tr>
							<td class="cellObject">
								<div class="panel" id="panelMain" style="display:block;overflow:visible;">
<%
									if(wc.getForms().containsKey(CustomerCareWizardController.Form.CustomerCareSearchContactForm.name())) {
%>										
										<div>
											<input type="submit" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" name="<%= CustomerCareWizardController.Command.SearchContact.name() %>" id="<%= CustomerCareWizardController.Command.SearchContact.name() %>.Button" tabindex="9010" value="<%= texts.getSearchText() %>" onclick="javascript:$('Command').value=this.name;" />
<%										
											for(Path activityCreatorIdentity: wc.getWizardState().getAllowedActivityCreators()) {
												org.opencrx.kernel.activity1.jmi1.ActivityCreator activityCreator = (org.opencrx.kernel.activity1.jmi1.ActivityCreator)pm.getObjectById(activityCreatorIdentity);
%>										
												<input type="submit" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="9050" value="<%= texts.getNewText() + ": " + activityCreator.getName() %>" onclick="javascript:$('<%= CustomerCareWizardController.PARAMETER_SELECTED_OBJECT_XRI %>').value='<%= activityCreatorIdentity %>';$('Command').value='<%= CustomerCareWizardController.Command.NewActivity.name() %>';" />
<%
											}										
%>
											<input type="submit" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" name="<%= CustomerCareWizardController.Command.Cancel.name() %>" tabindex="9080" value="<%= app.getTexts().getCancelTitle() %>" onclick="javascript:$('Command').value=this.name;"/>
										</div>
<%
									} else if(wc.getForms().containsKey(CustomerCareWizardController.Form.ContactForm.name())) {
%>
										<div>
<%																				
											for(Path activityCreatorIdentity: wc.getWizardState().getAllowedActivityCreatorsContact()) {
												org.opencrx.kernel.activity1.jmi1.ActivityCreator activityCreator = (org.opencrx.kernel.activity1.jmi1.ActivityCreator)pm.getObjectById(activityCreatorIdentity);
%>										
												<input type="submit" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="9050" value="<%= texts.getNewText() + ": " + activityCreator.getName() %>" onclick="javascript:$('<%= CustomerCareWizardController.PARAMETER_SELECTED_OBJECT_XRI %>').value='<%= activityCreatorIdentity %>';$('Command').value='<%= CustomerCareWizardController.Command.NewActivityContact.name() %>';" />
<%
											}
%>
											<input type="submit" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabIndex="9050" value="<%= texts.getCancelTitle() %>" onclick="javascript:$('Command').value='<%= CustomerCareWizardController.Command.BackToSearchContact.name() %>';"></a>
										</div>
										<div>&nbsp;</div>
										<div>
<%
											org.opencrx.kernel.account1.jmi1.Contact selectedContact = (org.opencrx.kernel.account1.jmi1.Contact)pm.getObjectById(wc.getWizardState().getSelectedContactIdentity());
											if(wc.contactIsLocked(selectedContact)) {
%>
												<img src="../../images/Lock.gif">
<%												
											}
											else {
%>
												<input type="image" name="<%= CustomerCareWizardController.Command.LockContact.name() %>" id="<%= CustomerCareWizardController.Command.LockContact.name() %>.Button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabIndex="9060" src="../../images/PhoneCall.gif" onclick="javascript:$('Command').value=this.name;" /> 
<%
											}
%>										
										</div>
<%
									} else if(wc.getForms().containsKey(CustomerCareWizardController.Form.CustomerCareCreateActivityForm.name())) {
										if(wc.getCommand().equals(CustomerCareWizardController.Command.NewActivityContact.name())) {
%>
											<div>
												<input type="submit" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" name="<%= CustomerCareWizardController.Command.SaveAsNewActivityContact.name() %>" id="<%= CustomerCareWizardController.Command.SaveAsNewActivityContact.name() %>.Button" tabindex="9070" value="<%= texts.getSaveTitle() %>" onclick="javascript:$('Command').value=this.name;" />
												<input type="submit" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabIndex="9050" value="<%= texts.getCancelTitle() %>" onclick="javascript:$('Command').value='<%= CustomerCareWizardController.Command.BackToContact.name() %>';"></a>
											</div>
<%
										} else if(wc.getCommand().equals(CustomerCareWizardController.Command.NewActivity.name())) {
%>
											<div>
												<input type="submit" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" name="<%= CustomerCareWizardController.Command.SaveAsNewActivity.name() %>" id="<%= CustomerCareWizardController.Command.SaveAsNewActivity.name() %>.Button" tabindex="9070" value="<%= texts.getSaveTitle() %>" onclick="javascript:$('Command').value=this.name;" />
												<input type="submit" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabIndex="9050" value="<%= texts.getCancelTitle() %>" onclick="javascript:$('Command').value='<%= CustomerCareWizardController.Command.BackToSearchContact.name() %>';"></a>
											</div>
<%											
										}
									} else if(wc.getForms().containsKey(CustomerCareWizardController.Form.CustomerCareShowActivityForm.name())) {
										if(wc.getWizardState().getSelectedActivityIdentity() != null) {
											org.opencrx.kernel.activity1.jmi1.Activity selectedActivity = (org.opencrx.kernel.activity1.jmi1.Activity)pm.getObjectById(wc.getWizardState().getSelectedActivityIdentity());
											if(selectedActivity.getActivityType() == null) {
%>
												<div id="transitions">
													<input type="submit" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabIndex="9080" value="<%= texts.getCancelTitle() %>" onclick="javascript:$('Command').value='<%= CustomerCareWizardController.Command.BackToContact.name() %>';"></a>
												</div>
<%												
											} else {
												org.opencrx.kernel.activity1.jmi1.ActivityProcess activityProcess = selectedActivity.getActivityType().getControlledBy();
												org.opencrx.kernel.activity1.jmi1.ActivityProcessState processState = selectedActivity.getProcessState();
												org.opencrx.kernel.activity1.cci2.ActivityProcessTransitionQuery transitionQuery = (org.opencrx.kernel.activity1.cci2.ActivityProcessTransitionQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition.class);
												transitionQuery.thereExistsPrevState().equalTo(selectedActivity.getProcessState());
												transitionQuery.orderByName().ascending();
												List<org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition> transitions = activityProcess.getTransition(transitionQuery);
												// One button for each transition
%>
												<div id="transitions">
<%																						
													for(org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition transition: transitions) {
														if(!transition.getNextState().equals(activityProcess.getStartState())) {
															String transitionId = transition.refGetPath().getBase();
%>
															<input type="button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="9070" value="<%= transition.getName() %>..." onclick="javascript:$('<%= transitionId %>').style.display='block';$('<%= transitionId %>.Title').style.display='block';$('followUp').style.display='block';$('transitions').style.display='none';" />
<%
														}
													}
%>								
													<input type="submit" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabIndex="9080" value="<%= texts.getCancelTitle() %>" onclick="javascript:$('Command').value='<%= CustomerCareWizardController.Command.BackToContact.name() %>';"></a>
												</div>
<%											
												// One title div for each transition
												for(org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition transition: transitions) {
													if(!transition.getNextState().equals(activityProcess.getStartState())) {
%>
														<div id='<%= transition.refGetPath().getBase() %>.Title' style='display:none;'>
															<div style="font-size:2em;font-weight:bold;"><%= transition.getName() %></div>
														</div>
<%
													}
												}
%>
												<div>&nbsp;</div>
												<div id="followUp" style="display:none;">
<%
													org.openmdx.ui1.jmi1.FormDefinition followUpFormDefinition = app.getUiFormDefinition(CustomerCareWizardController.Form.CustomerCareCreateFollowUpForm.name());
													org.openmdx.portal.servlet.control.FormControl followUpForm = new org.openmdx.portal.servlet.control.FormControl(
														followUpFormDefinition.refGetPath().getBase(),
														app.getCurrentLocaleAsString(),
														app.getCurrentLocaleAsIndex(),
														app.getUiContext(),
														followUpFormDefinition
													);
													followUpForm.paint(
														viewPort,
														null, // frame
														true // forEditing
													);
													viewPort.flush();
%>
												</div>
<%																					
												// One div for each transition
												for(org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition transition: transitions) {
													if(!transition.getNextState().equals(activityProcess.getStartState())) {
														String transitionId = transition.refGetPath().getBase();
%>
														<div id='<%= transitionId %>' style='display:none;'>
															<input type="submit" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="9070" value="<%= texts.getOkTitle() %>" onclick="javascript:$('<%= CustomerCareWizardController.PARAMETER_SELECTED_OBJECT_XRI %>').value='<%= transition.refGetPath() %>';$('Command').value='<%= CustomerCareWizardController.Command.DoFollowUp.name() %>';" />
															<input type="button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabIndex="9080" value="<%= texts.getCancelTitle() %>" onclick="javascript:$('<%= transitionId %>').style.display='none';$('<%= transitionId %>.Title').style.display='none';$('followUp').style.display='none';$('transitions').style.display='block';"></a>
														</div>
<%
													}
												}
											}
										}
									}
%>									
									<div>&nbsp;</div>
<%									
									wc.getForm().paint(viewPort, null, wc.isForEditing());
									viewPort.flush();
									if(wc.getCommand().equals(CustomerCareWizardController.Command.SearchContact.name())) {
										if(wc.getMatchingContacts() != null) {
%>
											<div>&nbsp;</div>
											<table class="gridTableFull">
												<tr class="gridTableHeaderFull">
													<td class="gridColTypeIcon"/>
													<td><%= wc.getShortLabel("org:opencrx:kernel:account1:Account", "fullName", app.getCurrentLocaleAsIndex()) %></td>
													<td />
													<td><%= wc.getShortLabel("org:opencrx:kernel:account1:Account", "aliasName", app.getCurrentLocaleAsIndex()) %></td>
													<td><%= wc.getShortLabel("org:opencrx:kernel:account1:Account", "address*Business!phoneNumberFull", app.getCurrentLocaleAsIndex()) %></td>
													<td><%= wc.getShortLabel("org:opencrx:kernel:account1:Account", "address*Business!emailAddress", app.getCurrentLocaleAsIndex()) %></td>
													<td><%= wc.getFieldLabel("org:opencrx:kernel:activity1:Activity", "lastTransition", app.getCurrentLocaleAsIndex()) %></td>
													<td />
													<td />
													<td />
													<td class="addon"/>
												</tr>
<%
												int count = 0;
												try {
													List<org.opencrx.kernel.account1.jmi1.Account> contacts = new ArrayList<org.opencrx.kernel.account1.jmi1.Account>();
													for(Iterator<?> i = wc.getMatchingContacts().iterator(); i.hasNext(); ) {
													    Object object = i.next();
													    org.opencrx.kernel.account1.jmi1.Account contact = null;
													    if(object instanceof org.opencrx.kernel.account1.jmi1.Member) {
													    	contact = ((org.opencrx.kernel.account1.jmi1.Member)object).getAccount();
													    } else if(object instanceof org.opencrx.kernel.account1.jmi1.Contact) {
													    	contact = (org.opencrx.kernel.account1.jmi1.Contact)object;
													    } else if(object instanceof org.opencrx.kernel.activity1.jmi1.Activity) {
													    	contact = ((org.opencrx.kernel.activity1.jmi1.Activity)object).getReportingContact();
													    }
													    if(contact != null && !contacts.contains(contact)) {
													    	contacts.add(contact);
													    	org.opencrx.kernel.account1.jmi1.AccountAddress[] addresses = Accounts.getInstance().getMainAddresses(contact);
%>
															<tr class="gridTableRowFull <%= count % 2 == 0 ? "" : "gridTableFullhover" %>" onclick="javascript:$('<%= CustomerCareWizardController.PARAMETER_SELECTED_OBJECT_XRI %>').value='<%= contact.refMofId() %>';$('Command').value='<%= CustomerCareWizardController.Command.SelectContact.name() %>';$('<%= WIZARD_NAME %>').submit();">
																<td class="gridColTypeIcon"><img src="../../images/Contact.gif"/></td>
																<td><%= contact.getFullName() == null ? "" : contact.getFullName() %></td>
																<td><%= wc.contactIsLocked(contact) ? "<img src='../../images/Lock.gif'>" : "" %></td>
																<td><%= contact.getAliasName() == null ? "" : contact.getAliasName() %></td>
																<td style="white-space: nowrap;"><%= new org.openmdx.portal.servlet.ObjectReference(addresses[Accounts.PHONE_BUSINESS], app).getTitle() %></td>
																<td style="white-space: nowrap;"><%= new org.openmdx.portal.servlet.ObjectReference(addresses[Accounts.MAIL_BUSINESS], app).getTitle() %></td>
<%
																if(object instanceof org.opencrx.kernel.activity1.jmi1.Activity) {
																	org.opencrx.kernel.activity1.jmi1.Activity activity = (org.opencrx.kernel.activity1.jmi1.Activity)object;
																	org.opencrx.kernel.activity1.cci2.ActivityFollowUpQuery followUpQuery = (org.opencrx.kernel.activity1.cci2.ActivityFollowUpQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityFollowUp.class);
																	followUpQuery.orderByModifiedAt().descending();
																	List<org.opencrx.kernel.activity1.jmi1.ActivityFollowUp> followUps = activity.getFollowUp(followUpQuery);
																	String lastFollowUpTitle = followUps.isEmpty() ? null : followUps.iterator().next().getTitle();
%>															
																	<td style="white-space: nowrap;"><%= DateValue.getLocalizedDateTimeFormatter(null, false, app).format(activity.getModifiedAt()) %></td>
																	<td><%= new org.openmdx.portal.servlet.ObjectReference(activity.getProcessState(), app).getTitle() %></td>
																	<td><%= lastFollowUpTitle == null ? "" : lastFollowUpTitle %></td>
																	<td>#<%= activity.getActivityNumber() %></td>
<%
																} else {
%>
																	<td />
																	<td />
																	<td />
																	<td />
<%
																}
%>
																<td class="addon"/>
															</tr>
<%
															count++;
															if(count > wc.getShowMaxContacts()) break;
													    }
													}
												} catch(Exception e) {
													new ServiceException(e).log();
												}
%>
											</table>
<%
										}
									} else if(
										wc.getCommand().equals(CustomerCareWizardController.Command.SelectContact.name()) &&
										wc.getWizardState().getSelectedContactIdentity() != null
									) {
										org.opencrx.kernel.account1.jmi1.Contact selectedContact = (org.opencrx.kernel.account1.jmi1.Contact)pm.getObjectById(wc.getWizardState().getSelectedContactIdentity());
										// Show assigned activities
										List<org.opencrx.kernel.activity1.jmi1.ActivityProcessState> allowedProcessStates = null;
										if(wc.getWizardState().getAllowedProcessStates() != null) {
											allowedProcessStates = new ArrayList<org.opencrx.kernel.activity1.jmi1.ActivityProcessState>();
											for(Path allowedProcessStateIdentity: wc.getWizardState().getAllowedProcessStates()) {
												allowedProcessStates.add(
													(org.opencrx.kernel.activity1.jmi1.ActivityProcessState)pm.getObjectById(allowedProcessStateIdentity)
												);
											}
										}
										for(Activities.ActivityState activityState: Arrays.asList(Activities.ActivityState.OPEN, Activities.ActivityState.CLOSED, Activities.ActivityState.CANCELLED)) {
											SimpleDateFormat scheduledStartFormat = DateValue.getLocalizedDateTimeFormatter(
												"org:opencrx:kernel:activity1:Activity:scheduledStart",
												false,
												app
											);
											SimpleDateFormat dueByFormat = DateValue.getLocalizedDateTimeFormatter(
												"org:opencrx:kernel:activity1:Activity:dueBy",
												false,
												app
											);
											org.opencrx.kernel.activity1.cci2.ActivityQuery activityQuery = (org.opencrx.kernel.activity1.cci2.ActivityQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.Activity.class);
											activityQuery.activityState().equalTo(activityState.getValue());
											activityQuery.thereExistsProcessState().elementOf(allowedProcessStates);
											activityQuery.orderByModifiedAt().descending();
											List<org.opencrx.kernel.activity1.jmi1.Activity> activities = selectedContact.getAssignedActivity(activityQuery);
											if(!activities.isEmpty()) {
%>
												<div>&nbsp;</div>
												<div><%= activityState.toString() %></div>
												<table class="gridTableFull">
													<tr class="gridTableHeaderFull">
														<td class="gridColTypeIcon"/>
														<td><%= wc.getShortLabel("org:opencrx:kernel:activity1:Activity", "activityNumber", app.getCurrentLocaleAsIndex()) %></td>
														<td><%= wc.getShortLabel("org:opencrx:kernel:activity1:Activity", "name", app.getCurrentLocaleAsIndex()) %></td>
														<td><%= wc.getShortLabel("org:opencrx:kernel:activity1:Activity", "assignedTo", app.getCurrentLocaleAsIndex()) %></td>
														<td><%= wc.getShortLabel("org:opencrx:kernel:activity1:Activity", "reportingContact", app.getCurrentLocaleAsIndex()) %></td>
														<td><%= wc.getShortLabel("org:opencrx:kernel:activity1:Activity", "processState", app.getCurrentLocaleAsIndex()) %></td>
														<td><%= wc.getShortLabel("org:opencrx:kernel:activity1:Activity", "scheduledStart", app.getCurrentLocaleAsIndex()) %></td>
														<td><%= wc.getShortLabel("org:opencrx:kernel:activity1:Activity", "dueBy", app.getCurrentLocaleAsIndex()) %></td>
														<td class="addon"/>
													</tr>
<%
													int count = 0;
													for(org.opencrx.kernel.activity1.jmi1.Activity activity: activities) {
														String assignedTo = "N/A";
														try {
															assignedTo = new org.openmdx.portal.servlet.ObjectReference(activity.getAssignedTo(), app).getTitle();
														} catch(Exception e) {}
														String reportingContact = "N/A";
														try {
															reportingContact = new org.openmdx.portal.servlet.ObjectReference(activity.getReportingContact(), app).getTitle();
														} catch(Exception e) {}
														
%>
														<tr class="gridTableRowFull <%= count % 2 == 0 ? "" : "gridTableFullhover" %>" onclick="javascript:$('<%= CustomerCareWizardController.PARAMETER_SELECTED_OBJECT_XRI %>').value='<%= activity.refMofId() %>';$('Command').value='<%= CustomerCareWizardController.Command.SelectActivity.name() %>';$('<%= WIZARD_NAME %>').submit();">
															<td class="gridColTypeIcon"><img src="../../images/Incident.gif"/></td>
															<td><%= activity.getActivityNumber() == null ? "" : activity.getActivityNumber() %></td>
															<td><%= activity.getName() == null ? "" : activity.getName() %></td>
															<td><%= assignedTo %></td>
															<td><%= reportingContact %></td>
															<td><%= new org.openmdx.portal.servlet.ObjectReference(activity.getProcessState(), app).getTitle() %></td>
															<td style="white-space: nowrap;"><%= activity.getScheduledStart() == null ? "" : scheduledStartFormat.format(activity.getScheduledStart()) %></td>
															<td style="white-space: nowrap;"><%= activity.getDueBy() == null ? "" : dueByFormat.format(activity.getDueBy()) %></td>
															<td class="addon"/>
														</tr>
<%
														count++;
														if(count > 20) break;
													}
%>
												</table>
<%
											}
										}
									} else if(
										wc.getCommand().equals(CustomerCareWizardController.Command.SelectActivity.name()) &&
										wc.getWizardState().getSelectedActivityIdentity() != null
									) {
										org.opencrx.kernel.activity1.jmi1.Activity selectedActivity = (org.opencrx.kernel.activity1.jmi1.Activity)pm.getObjectById(wc.getWizardState().getSelectedActivityIdentity());
%>										
										<div>&nbsp;</div>
										<table class="gridTableFull">
											<tr class="gridTableHeaderFull">
												<td class="gridColTypeIcon"/>
												<td><%= wc.getShortLabel("org:opencrx:kernel:activity1:ActivityFollowUp", "title", app.getCurrentLocaleAsIndex()) %></td>
												<td><%= wc.getShortLabel("org:opencrx:kernel:activity1:ActivityFollowUp", "text", app.getCurrentLocaleAsIndex()) %></td>
												<td class="addon"/>
											</tr>
<%										
											org.opencrx.kernel.activity1.cci2.ActivityFollowUpQuery followUpQuery = (org.opencrx.kernel.activity1.cci2.ActivityFollowUpQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityFollowUp.class);
											followUpQuery.orderByCreatedAt().descending();
											List<org.opencrx.kernel.activity1.jmi1.ActivityFollowUp> followUps = selectedActivity.getFollowUp(followUpQuery);
											int count = 0;
											for(org.opencrx.kernel.activity1.jmi1.ActivityFollowUp followUp: followUps) {
												%>
												<tr class="gridTableRowFull <%= count % 2 == 0 ? "" : "gridTableFullhover" %>">
													<td class="gridColTypeIcon"><img src="../../images/ActivityFollowUp.gif" /></td>
													<td><%= new org.opencrx.kernel.portal.FormattedFollowUpDataBinding().getValue(followUp, null, app) %></td>
													<td><%= followUp.getTitle() == null ? "" : new org.opencrx.kernel.portal.FormattedNoteDataBinding().getValue(followUp, null, app) %></td>
													<td class="addon"/>
												</tr>
<%
												count++;
											}
%>
										</table>
<%
									}
%>
									<div class="fieldGroupName">&nbsp;</div>
								</div>
							</td>
						</tr>
					</table>
				</form>
			</div> <!-- content -->
		</div> <!-- content-wrap -->
	</div> <!-- wrap -->
</div> <!-- container -->			
<t:wizardClose controller="<%= wc %>" />
