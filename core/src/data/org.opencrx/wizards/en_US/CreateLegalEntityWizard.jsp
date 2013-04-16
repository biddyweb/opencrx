<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.openmdx.org/
 * Description: CreateLegalEntity wizard
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2005-2013, CRIXP Corp., Switzerland
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
<%@ page session="true" import="
java.util.*,
java.io.*,
java.text.*,
org.opencrx.kernel.backend.*,
org.opencrx.kernel.portal.*,
org.opencrx.kernel.portal.wizard.*,
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
org.openmdx.base.text.conversion.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.databinding.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.exception.*,
org.openmdx.base.naming.*
" %>
<!--
	<meta name="UNUSEDlabel" content="Create Legal Entity">
	<meta name="UNUSEDtoolTip" content="Create Legal Entity">
	<meta name="targetType" content="_inplace">
	<meta name="forClass" content="org:opencrx:kernel:account1:Segment">
	<meta name="forClass" content="org:opencrx:kernel:account1:LegalEntity">
	<meta name="order" content="org:opencrx:kernel:account1:Segment:createLegalEntity">
	<meta name="order" content="org:opencrx:kernel:account1:LegalEntity:createLegalEntity">
-->
<%
	final String FORM_NAME = "LegalEntityForm";
	final String FORM_NAME_MEMBER = "MemberForm";
	
	CreateLegalEntityWizardController wc = new CreateLegalEntityWizardController();
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
	ViewPort viewPort = wc.getViewPort(out);
	int tabIndex = 100;
	org.opencrx.kernel.account1.jmi1.Segment accountSegment = Accounts.getInstance().getAccountSegment(pm, wc.getProviderName(), wc.getSegmentName());
	org.opencrx.kernel.activity1.jmi1.Segment activitySegment = Activities.getInstance().getActivitySegment(pm,  wc.getProviderName(), wc.getSegmentName());
%>
<br />
<div class="OperationDialogTitle"><%= wc.getToolTip() %></div>
<form id="<%= FORM_NAME %>" name="<%= FORM_NAME %>" accept-charset="UTF-8" action="<%= wc.getServletPath() %>">
	<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= wc.getRequestId() %>" />
	<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= wc.getObjectIdentity().toXRI() %>" />
	<input type="hidden" id="Command" name="Command" value="" />
	<input type="hidden" id="Para0" name="Para0" value="" />
	<input type="hidden" name="memberXri" id="memberXri" value="<%= wc.getMemberXri() == null ? "" : wc.getMemberXri() %>" />
	<input type="checkbox" style="display:none;" name="isAddMemberMode" id="isAddMemberMode" <%= Boolean.TRUE.equals(wc.getIsAddMemberMode()) ? "checked" : "" %>/>
	<table class="tableLayout">
		<tr>
			<td class="cellObject">
				<div class="panel" id="panel<%= FORM_NAME %>" style="display:block">
<%
					wc.getForms().get(FORM_NAME).paint(viewPort, null, true);
					viewPort.flush();

					if(wc.getLegalEntity() != null) {
						// list existing members

%>
						<div class="fieldGroupName">&nbsp;</div>
						<table class="fieldGroup">
							<tr>
								<td class="label">
									<%= app.getLabel("org:opencrx:kernel:account1:Member") %><br>
									<input type="submit" name="AddMember" id="AddMember.Button" tabindex="<%= tabIndex++ %>" title="<%= app.getTexts().getNewText() + " " + app.getLabel("org:opencrx:kernel:account1:Member") %>" value="+" onclick="javascript:$('Command').value=this.name;$('isAddMemberMode').checked=true;" />
								</td>
								<td>
									<table class="gridTableFull">
										<tr class="gridTableHeaderFull">
											<td/>
											<td><strong><%= wc.getFieldLabel("org:opencrx:kernel:account1:Member", "account", app.getCurrentLocaleAsIndex()) %></strong></td>
											<td><strong><%= wc.getFieldLabel("org:opencrx:kernel:account1:Member", "name", app.getCurrentLocaleAsIndex()) %></strong></td>
											<td><strong><%= wc.getFieldLabel("org:opencrx:kernel:account1:Member", "description", app.getCurrentLocaleAsIndex()) %></strong></td>
											<td><strong><%= wc.getFieldLabel("org:opencrx:kernel:account1:Member", "memberRole", app.getCurrentLocaleAsIndex()) %></strong></td>
											<td/>
										</tr>
<%
										try {
											org.opencrx.kernel.account1.cci2.MemberQuery memberFilter = (org.opencrx.kernel.account1.cci2.MemberQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.Member.class);
											memberFilter.forAllDisabled().isFalse();
											for(org.opencrx.kernel.account1.jmi1.Member member: wc.getLegalEntity().<org.opencrx.kernel.account1.jmi1.Member>getMember(memberFilter)) {
												try {
													org.opencrx.kernel.account1.jmi1.Account accountTo = member.getAccount();
						                  			String accountHref = "";
						                  			Action action = new ObjectReference(accountTo, app).getSelectObjectAction();
						                  			accountHref = action.getEncodedHRef();
													String rolesText = "";
													for(Iterator roles = member.getMemberRole().iterator(); roles.hasNext(); ) {
														if (rolesText.length() > 0) {
															rolesText += ";";
														}
														rolesText += wc.getCodes().getLongTextByCode("memberRole", app.getCurrentLocaleAsIndex(), true).get(new Short(((Short)roles.next()).shortValue()));
													}
%>
													<tr class="gridTableRow" <%= wc.getMemberXri() != null && wc.getMemberXri().equals(member.refGetPath().toXRI()) ? "style='background-color:#E4FF79;'" : "" %>>
														<td class="addon">
															<button type="submit" name="EditMember" tabindex="<%= tabIndex++ %>" value="&mdash;" title="<%= app.getTexts().getEditTitle() %>" style="border:0;background:transparent;font-size:10px;font-weight:bold;cursor:pointer;" onclick="javascript:$('Command').value=this.name;$('memberXri').value='<%= member.refGetPath().toXRI() %>';" ><img src="images/edit.gif" /></button>
														</td>
														<td><a href="<%= accountHref %>" target="_blank"><%= app.getHtmlEncoder().encode(new ObjectReference(member.getAccount(), app).getTitle(), false) %></a></td>
														<td><%= member.getName() != null ? app.getHtmlEncoder().encode(member.getName(), false) : "" %></td>
														<td><%= member.getDescription() != null ?  app.getHtmlEncoder().encode(member.getDescription(), false) : "" %></td>
														<td style="overflow:hidden;text-overflow:ellipsis;"><%= rolesText %></td>
														<td class="addon">
															<button type="submit" name="DisableMember" tabindex="<%= tabIndex++ %>" value="&mdash;" title="<%= app.getTexts().getDeleteTitle() %>" style="border:0;background:transparent;font-size:10px;font-weight:bold;cursor:pointer;" onclick="javascript:$('memberXri').value='';$('Command').value=this.name;$('Para0').value='<%= member.refGetPath().toXRI() %>';" ><img src="images/deletesmall.gif" /></button>
														</td>
													</tr>
<%
												} catch(Exception em) {
													new ServiceException(em).log();
												}
											}
										} catch(Exception e) {
											new ServiceException(e).log();
										}
%>
									</table>
								</td>
								<td class="addon"/>
							</tr>
						</table>
<%
						if(Boolean.TRUE.equals(wc.getIsAddMemberMode()) || (wc.getMemberXri() != null)) {
							wc.getForms().get(FORM_NAME_MEMBER).paint(viewPort, null, true);
							viewPort.flush();
						}
					}
%>
					<div class="fieldGroupName">&nbsp;</div>				
				</div>
				<div id="WaitIndicator" style="float:left;width:50px;height:24px;" class="wait">&nbsp;</div>
<%
				if(!wc.getErrorMsg().isEmpty()) {
%>
					<div title="<%= wc.getErrorTitle().replace("\"", "'") %>"  style="background-color:red;color:white;border:1px solid black;padding:10px;font-weight:bold;margin-top:10px;">
						<%= wc.getErrorMsg() %>
					</div>
<%
				}
%>
				<div id="SubmitArea" style="float:left;display:none;">
				<input type="submit" name="Search" id="Search.Button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getSearchText() %>" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; $('Command').value=this.name;" />
				<input type="button" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; new Ajax.Updater('UserDialog', '<%= wc.getServletPath() + "?" + Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(accountSegment.refMofId(), "UTF-8") + "&" + Action.PARAMETER_REQUEST_ID + "=" + wc.getRequestId() %>', {evalScripts: true});" value="<%= app.getTexts().getNewText() %> <%= app.getTexts().getSearchText() %>" />
<%
				if(wc.getLegalEntity() != null) {
					org.opencrx.kernel.account1.jmi1.LegalEntity legalEntity = wc.getLegalEntity();
%>
					<input type="button" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; new Ajax.Updater('UserDialog', '<%= wc.getServletPathPrefix() + "CreateLeadWizard.jsp?" + Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(legalEntity.refMofId(), "UTF-8") + "&" + Action.PARAMETER_REQUEST_ID + "=" + wc.getRequestId() %>', {evalScripts: true});" value="<%= app.getTexts().getNewText() %> <%= app.getLabel("org:opencrx:kernel:contract1:Lead") %>" />
					<input type="button" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; new Ajax.Updater('UserDialog', '<%= wc.getServletPathPrefix() + "CreateContractWizard.jsp?" + Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(legalEntity.refMofId(), "UTF-8") + "&" + Action.PARAMETER_REQUEST_ID + "=" + wc.getRequestId() %>', {evalScripts: true});" value="<%= app.getTexts().getNewText() %> <%= wc.getFieldLabel("org:opencrx:kernel:contract1:ContractRole", "contract", app.getCurrentLocaleAsIndex()) %>" />
					<input style='display:none;' type="button" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; new Ajax.Updater('UserDialog', '<%= wc.getServletPathPrefix() + "CreateActivityWizard.jsp?" + Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(legalEntity.refMofId(), "UTF-8") + "&" + Action.PARAMETER_REQUEST_ID + "=" + wc.getRequestId() %>', {evalScripts: true});" value="<%= app.getTexts().getNewText() %> <%= wc.getFieldLabel("org:opencrx:kernel:activity1:ActivityFollowUp", "activity", app.getCurrentLocaleAsIndex()) %>" />
<%
					// prepare href to open new tab with activity segment and then call inline wizard to create new activity
					String providerName = obj.refGetPath().get(2);
					String segmentName = obj.refGetPath().get(4);
					String createActivityScript = "$('UserDialogWait').className='loading udwait';new Ajax.Updater('UserDialog', '" + wc.getServletPathPrefix() + "CreateActivityWizard.jsp?" + Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(legalEntity.refMofId(), "UTF-8") + "&" + Action.PARAMETER_REQUEST_ID + "=" + wc.getRequestId() + "&reportingAccount=" + java.net.URLEncoder.encode(legalEntity.refMofId(), "UTF-8") + "', {evalScripts: true});";
					
					QuickAccessor createActivityAccessor = new QuickAccessor(
					    activitySegment.refGetPath(), // target
					    "New Activity", // name
					    "New Activity", // description
					    "Task.gif", // iconKey
					    Action.MACRO_TYPE_JAVASCRIPT, // actionType
					    createActivityScript,
					    Collections.<String>emptyList() // actionParams
					);
					Action newActivityAction =	createActivityAccessor.getAction(
					    accountSegment.refGetPath()
					  );
					String newActivityHref = newActivityAction.getEncodedHRef(wc.getRequestId());
%>
					<a href="<%= newActivityHref %>" target="_blank"><button type="button" name="newActivity" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getNewText() %> <%= wc.getFieldLabel("org:opencrx:kernel:activity1:ActivityFollowUp", "activity", app.getCurrentLocaleAsIndex()) %>"><%= app.getTexts().getNewText() %> <%= wc.getFieldLabel("org:opencrx:kernel:activity1:ActivityFollowUp", "activity", app.getCurrentLocaleAsIndex()) %></button></a>
					<input type="submit" name="OK" id="OK.Button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getSaveTitle() %>" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; $('Command').value=this.name;"/>
<%
				}
				else {
				    if(wc.getMachingLegalEntities() != null) {
%>
						<input type="submit" name="Create" id="Create.Button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getNewText() %> <%= app.getLabel("org:opencrx:kernel:account1:LegalEntity") %>" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; $('Command').value=this.name;"/>
<%
				    }
				}
%>
				<input type="submit" name="Cancel" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getCloseText() %>" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; $('Command').value=this.name;"/>
				</div>
<%
				if(wc.getMachingLegalEntities() != null) {
%>
					<div>&nbsp;</div>
					<table class="gridTableFull">
						<tr class="gridTableHeaderFull">
							<td/>
							<td><%= wc.getFieldLabel("org:opencrx:kernel:account1:LegalEntity", "name", app.getCurrentLocaleAsIndex()) %></td>
							<td><%= wc.getFieldLabel("org:opencrx:kernel:account1:LegalEntity", "aliasName", app.getCurrentLocaleAsIndex()) %></td>
							<td><%= app.getLabel("org:opencrx:kernel:account1:PostalAddress") %></td>
							<td><%= wc.getFieldLabel("org:opencrx:kernel:account1:Account", "address*Business!phoneNumberFull", app.getCurrentLocaleAsIndex()) %></td>
							<td><%= wc.getFieldLabel("org:opencrx:kernel:account1:Account", "address*Mobile!phoneNumberFull", app.getCurrentLocaleAsIndex()) %></td>
							<td><%= wc.getFieldLabel("org:opencrx:kernel:account1:Account", "address*Business!emailAddress", app.getCurrentLocaleAsIndex()) %></td>
							<td class="addon"/>
						</tr>
<%
						int count = 0;
						for(Iterator i = wc.getMachingLegalEntities().iterator(); i.hasNext(); ) {
						    org.opencrx.kernel.account1.jmi1.LegalEntity legalEntity = ( org.opencrx.kernel.account1.jmi1.LegalEntity)i.next();
						    org.opencrx.kernel.account1.jmi1.AccountAddress[] addresses = Accounts.getInstance().getMainAddresses(legalEntity);
%>
							<tr class="gridTableRowFull">
								<td><img style="cursor: pointer;" src="images/LegalEntity.gif" onclick="javascript:new Ajax.Updater('UserDialog', '<%= wc.getServletPath() + "?" + Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(legalEntity.refMofId(), "UTF-8") + "&" + Action.PARAMETER_REQUEST_ID + "=" + wc.getRequestId() %>', {evalScripts: true});"/></td>
								<td><%= legalEntity.getFullName() == null ? "" : legalEntity.getFullName() %></td>
								<td><%= legalEntity.getAliasName() == null ? "" : legalEntity.getAliasName() %></td>
								<td><%= new org.openmdx.portal.servlet.ObjectReference(addresses[Accounts.POSTAL_BUSINESS], app).getTitle() %></td>
								<td><%= new org.openmdx.portal.servlet.ObjectReference(addresses[Accounts.PHONE_BUSINESS], app).getTitle() %></td>
								<td><%= new org.openmdx.portal.servlet.ObjectReference(addresses[Accounts.MOBILE], app).getTitle() %></td>
								<td><%= new org.openmdx.portal.servlet.ObjectReference(addresses[Accounts.MAIL_BUSINESS], app).getTitle() %></td>
								<td class="addon"/>
							</tr>
<%
							count++;
							if(count > 50) break;
						}
					}
%>
				</table>
			</td>
		</tr>
	</table>
</form>
<br>
<script type="text/javascript">
	Event.observe('<%= FORM_NAME %>', 'submit', function(event) {
		$('<%= FORM_NAME %>').request({
			onFailure: function() { },
			onSuccess: function(t) {
				$('UserDialog').update(t.responseText);
			}
		});
		Event.stop(event);
	});
	$('WaitIndicator').style.display='none';
	$('SubmitArea').style.display='block';
</script>
<t:wizardClose controller="<%= wc %>" />

