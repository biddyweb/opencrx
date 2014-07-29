<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: CreateActivityWizard
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2004-2014, CRIXP Corp., Switzerland
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
java.text.*,
org.opencrx.kernel.portal.*,
org.openmdx.kernel.id.cci.*,
org.opencrx.kernel.activity1.jmi1.*,
org.opencrx.kernel.backend.*,
org.opencrx.kernel.portal.wizard.*,
org.openmdx.kernel.id.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.base.exception.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.component.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*,
org.openmdx.kernel.log.*,
org.openmdx.kernel.exception.*
" %>
<!--
	<meta name="UNUSEDlabel" content="Create Activity">
	<meta name="UNUSEDtoolTip" content="Create Activity">
	<meta name="targetType" content="_inplace">
	<meta name="forClass" content="org:opencrx:kernel:activity1:Segment">
	<meta name="forClass" content="org:opencrx:kernel:activity1:ActivityTracker">
	<meta name="forClass" content="org:opencrx:kernel:activity1:ActivityCreator">
	<meta name="forClass" content="org:opencrx:kernel:activity1:Activity">
	<meta name="order" content="org:opencrx:kernel:activity1:Segment:createActivity">
	<meta name="order" content="org:opencrx:kernel:activity1:ActivityTracker:createActivity">
	<meta name="order" content="org:opencrx:kernel:activity1:ActivityCreator:createActivity">
	<meta name="order" content="org:opencrx:kernel:activity1:Activity:createActivity">
-->
<%
	final String FORM_NAME = "CreateActivityForm";
	CreateActivityWizardController wc = new CreateActivityWizardController();
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
%>
<div class="OperationDialogTitle"><%= wc.getToolTip() %></div>
<form id="<%= FORM_NAME %>" name="<%= FORM_NAME %>" accept-charset="UTF-8" method="POST" action="<%= wc.getServletPath() %>">
<%
	if(!wc.getErrorMsg().isEmpty()) {
%>
		<div id="errorPane" style="padding:10px 10px 10px 10px;background-color:#FF0000;">
			<table>
				<tr style="vertical-align:top;">
					<td style="padding:5px;font-weight:bold;color:#FFFFFF;"><img src='images/Alert.gif'/></td>
					<td style="padding:5px;color:#FFFFFF;"><%= wc.getErrorMsg() %></td>
				</tr>
			</table>
		</div>
<%
	}
%>
	<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= wc.getRequestId() %>" />
	<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= wc.getObjectIdentity().toXRI() %>" />
	<input type="checkbox" style="display:none;" id="isInitialized" name="isInitialized" checked value="true" />
	<input type="hidden" id="Command" name="Command" value="" />
	<table class="tableLayout">
		<tr>
			<td class="cellObject">
				<div class="panel" id="panel<%= FORM_NAME %>" style="display:block;overflow:visible;">
<%
					wc.getForms().get(FORM_NAME).paint(viewPort, null, true);
					viewPort.flush();
%>					
					<script type="text/javascript">
						try {
					      var els = document.getElementsByTagName('INPUT');
					      for (i=0; i<els.length; i++) {
						        if (els[i].id && (els[i].id.indexOf('org:opencrx:kernel:activity1:Activity:reportingAccount') >= 0) && (els[i].id.indexOf('.Title') >= 0)) {
						          var eltINPUT = els[i];
						        }
						    }
						    if (eltINPUT) {
						    	eltINPUT.setAttribute('onFocus', 'this.previousValue = this.value;');
						    	eltINPUT.setAttribute('onBlur','if (this.previousValue != this.value) {this.previousValue=this.value;setTimeout("$(\'Refresh.Button\').click()", 500);}');
						    }
						} catch(e){}
					</script>
<%
					if(wc.getReportingAccount() != null) {
							boolean hasContacts = false;
%>
							<table style="display:none;">
								<tr><td/><td/><td/></tr>
								<tr id="toBeInserted">
									<td class="<%= CssClass.fieldLabel %>">
										<span class="nw"><%= app.getLabel(CreateActivityWizardController.MEMBER_CLASS) %>:</span>
									</td>
									<td>
										<input type="hidden" id="fetchMemberContact" name="fetchMemberContact" value="" /> 
										<select id="memberContact" name="memberContact" class="valueL" onchange="javascript:$('fetchMemberContact').value='true';$('Refresh.Button').click();" >
											<option value="NA">^</option>
<%
											// Show members of reporting account
								            org.opencrx.kernel.account1.cci2.MemberQuery memberQuery = (org.opencrx.kernel.account1.cci2.MemberQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.Member.class);
								            memberQuery.orderByName().ascending();
								            memberQuery.forAllDisabled().isFalse();
											int maxMemberToShow = 200;
								            for (
												Iterator<org.opencrx.kernel.account1.jmi1.Member> k = wc.getReportingAccount().getMember(memberQuery).iterator();
												k.hasNext() && maxMemberToShow > 0;
												maxMemberToShow--
								            ) {
								            	try {
									         	    org.opencrx.kernel.account1.jmi1.Member member = k.next();
									         	    org.opencrx.kernel.account1.jmi1.Account account = member.getAccount();
									         	    org.opencrx.kernel.account1.jmi1.Contact contact = null;
									         	    if (account instanceof org.opencrx.kernel.account1.jmi1.Contact) {
									         	    	contact = (org.opencrx.kernel.account1.jmi1.Contact)account;
									         	    }
									         	    if(contact != null) {
									         	    	hasContacts = true;
									                	String selectedModifier = ((contact != null ) && (wc.getReportingContact() != null) && (wc.getReportingContact().refGetPath().equals(contact.refGetPath()))) ? "selected" : "";
%>
														<option <%= selectedModifier %> value="<%= contact.refGetPath().toXRI() %>"><%= (member.getName() == null ? contact.getFullName() : member.getName()) + (contact != null ? " (" + contact.getFirstName() + " " + contact.getLastName() + ")": "") %></option>
<%
													}
												} catch (Exception ignore) {}
											}
%>
										</select>
									</td>
									<td class="addon"/>
								</tr>
							</table>
							<script type="text/javascript">
								try {
							      var els = document.getElementsByTagName('DIV');
							      for (i=0; i<els.length && <%= hasContacts ? "true" : "false" %>; i++) {
							        if (els[i].className=="fieldGroupName" && els[i].innerHTML && (els[i].innerHTML.indexOf('Activity') >= 0)) {
							          var TABLE = els[i].nextSibling;
							          while (TABLE.nodeName != "TABLE" && TABLE.nextSibling) {
								          	TABLE = TABLE.nextSibling;
							          };
							          if (TABLE.nodeName == "TABLE") {
									          var TBODY = TABLE.firstChild;
									          while (TBODY.nodeName != "TBODY" && TBODY.nextSibling) {
									        	  	TBODY = TBODY.nextSibling;
									          };
									          if (TBODY.nodeName == "TBODY") {
										          	// append row with id "toBeInserted"
										          	var tr = document.getElementById('toBeInserted');
										          	TBODY.appendChild(tr);
									          }
							          }
							        }
							      }
								} catch(e){}
							</script>					
<%
					}
					if (obj instanceof org.opencrx.kernel.activity1.jmi1.Activity) {
						// offer to create link from calling activity to new activity
						String desc = app.getLabel(CreateActivityWizardController.ACTIVITY_CLASS);
						try {
							desc = (new ObjectReference(obj, app)).getTitle();
						} catch (Exception e) {}
%>
						<table>
							<tr><td/><td/><td/></tr>
							<tr id="activityLink">
								<td class="<%= CssClass.fieldLabel %>">
									<span class="nw"><%= wc.getFieldLabel(CreateActivityWizardController.ACTIVITYLINKTO_CLASS, "linkTo", app.getCurrentLocaleAsIndex()) %>:</span>
								</td>
								<td>
									<input type="checkbox" id="linkToActivity" name="linkToActivity" <%= Boolean.TRUE.equals(wc.getLinkToActivity()) ? "checked" : "" %> tabindex="8000" value="true" /> <%= desc %>
								</td>
								<td class="addon"></td>
							</tr>
						</table>
<%
					}
%>
				</div>
				<table class="fieldGroup">
					<tr>
						<td class="<%= CssClass.fieldLabel %>" />
						<td>
<%
							if (wc.getLastAppliedActivityCreator() != null) {
								// list activity groups that will be assigned automatically
								for (Iterator agi = wc.getLastAppliedActivityCreator().getActivityGroup().iterator(); agi.hasNext();) {
									try {
										org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)agi.next();
%>
										<div style="font-weight:bold;"><%= new ObjectReference(ag, app).getTitle() %></div>
<%
									} catch (Exception e) {
										new ServiceException(e).log();
									}
								}
							}
%>
						</td>
						<td class="addon" />
					</tr>
				</table>
				<div id="WaitIndicator" style="float:left;width:50px;height:24px;" class="wait">&nbsp;</div>
				<div id="SubmitArea" style="float:left;display:none;">
	 				<input type="submit" name="Refresh" id="Refresh.Button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="9000" value="<%= app.getTexts().getReloadText() %>" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; $('Command').value=this.name;" />
					<input type="submit" name="OK" id="OK.Button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="9010" value="<%= app.getTexts().getNewText() %>" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; $('Command').value=this.name;this.name='---';" />
					<input type="submit" name="Cancel" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="9020" value="<%= app.getTexts().getCancelTitle() %>" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; $('Command').value=this.name;" />
				</div>
			</td>
		</tr>
	</table>
</form>
<br />
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
