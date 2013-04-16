<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="portal" uri="http://www.openmdx.org/tags/openmdx-portal"%>
<%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: BulkActivityFollowUpWizard.jsp
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2011-2013, CRIXP Corp., Switzerland
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
org.opencrx.kernel.generic.*,
org.opencrx.kernel.portal.wizard.*,
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
org.openmdx.base.exception.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*
" %>
<!--
	<meta name="label" content="Bulk - Activity Follow up">
	<meta name="toolTip" content="Bulk - Activity Follow up">
	<meta name="targetType" content="_inplace">
	<meta name="forClass" content="org:opencrx:kernel:activity1:Activity">
	<meta name="order" content="5555">
-->
<%
	final String FORM_NAME = "bulkActivityFollowUpForm";
	BulkActivityFollowUpWizardController wc = new BulkActivityFollowUpWizardController();
%>
	<t:wizardHandleCommand controller='<%= wc %>' defaultCommand='Refresh' />
<%
	if(response.getStatus() != HttpServletResponse.SC_OK) {
		wc.close();		
		return;
	}
	final GregorianCalendar NOW = wc.getLocalizedCalendar();
	ApplicationContext app = wc.getApp();
	javax.jdo.PersistenceManager pm = wc.getPm();	
	int tabIndex = 100;
%>
<br />
<div class="OperationDialogTitle"><%= wc.getToolTip() %></div>
<div style="border:1px solid black;padding:10px;margin:2px;background-color:#FF9900;">
	<strong>WARNING:</strong> Follow up will be performed on <strong><%= wc.getActivityCount() %> activities</strong> assigned to '<%= wc.getActivityGroup() != null ? wc.getActivityGroup().getName() : "--" %>'<br>
	<img id='detailsHidden' style="display:block;" src="images/expand_down.gif" border="0" align="top" title="<%= app.getTexts().getShowDetailsTitle() %>" alt="<%= app.getTexts().getShowDetailsTitle() %>" onclick="javascript:$('BulkActivities-00').style.display='block';$('detailsShown').style.display='block';$('detailsHidden').style.display='none';" />
	<img id='detailsShown'  style="display:none;"  src="images/shrink_up.gif"   border="0" align="top" alt="^"  onclick="javascript:$('BulkActivities-00').style.display='none'; $('detailsShown').style.display='none';$('detailsHidden').style.display='block';" />
<%
	if (wc.getActivityGroup() != null) {
%>		
		<div id="BulkActivities-00" style="border:1px solid black;padding:2x;background-color:#fff;display:none;">
			<portal:showobject id='BulkActivityFollowUpWizard-Activities' object="<%= wc.getActivityGroup().refGetPath() %>" navigationTarget="_blank" resourcePathPrefix="./" showAttributes="false" grids="filteredActivity">
				<portal:query name="filteredActivity" query="<%= wc.getActivityQuery() %>" />
			</portal:showobject>
		</div>
<%
	}
%>
</div>
<form id="<%= FORM_NAME %>" name="<%= FORM_NAME %>" accept-charset="UTF-8" method="POST" action="<%= wc.getServletPath() %>">
	<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= wc.getRequestId() %>" />
	<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= wc.getObjectIdentity().toXRI() %>" />
	<input type="hidden" id="Command" name="Command" value="" />
	<table class="tableLayout">
		<tr>
			<td class="cellObject">
				<table class="fieldGroup">
					<tbody>
						<tr>
							<td class="label" title=""><span class="nw"><%= wc.getFieldLabel("org:opencrx:kernel:activity1:ActivityGroupAssignment", "activityGroup", app.getCurrentLocaleAsIndex()) %>:</span></td>
							<td>
								<select id="activityGroupXri" name="activityGroupXri" class="valueL" tabindex="<%= tabIndex++ %>" onchange="javascript:$('Refresh.Button').click();" >
<%
									boolean hasEntries = false;
									for(org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment activityGroupAssignment: wc.getActivity().<org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment>getAssignedGroup()) {
										try {
											if(activityGroupAssignment.getActivityGroup() != null) {
												org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = activityGroupAssignment.getActivityGroup();
												String activityGroupTitle = "--";
												try {
													activityGroupTitle = app.getHtmlEncoder().encode(new ObjectReference(ag, app).getTitle(), false);
												} catch (Exception e) {}
												hasEntries = true;
%>
												<option <%= wc.getActivityGroup() != null && wc.getActivityGroup().refGetPath().equals(ag.refGetPath()) ? "selected" : "" %> value="<%= ag.refGetPath().toXRI() %>"><%= activityGroupTitle %></option>
<%
											}
										} catch (Exception e) {
											new ServiceException(e).log();
										}
									}
									if (!hasEntries) {
										// create empty entry
%>
										<option value="">--</option>
<%
									}
%>
								</select>
							</td>
							<td class="addon">
							</td>
						</tr>
					</tbody>
				</table>
				<div class="fieldGroupName">FollowUp 1</div>
				<table class="fieldGroup">
					<tr>
						<td class="label">
							<span class="nw"><%= wc.getFieldLabel("org:opencrx:kernel:activity1:ActivityFollowUp", "transition", app.getCurrentLocaleAsIndex()) %>:</span>
						</td>
						<td>
							<select id="processTransitionXri0" name="processTransitionXri0" class="valueL" tabindex="<%= tabIndex++ %>" onchange="javascript:$('Refresh.Button').click();" >
<%
								if(wc.getSelectableProcessTransitions1() != null) {
									for(org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition processTransition: wc.getSelectableProcessTransitions1()) {
%>
										<option <%=wc.getFormFields().getProcessTransitionXri0() != null && wc.getFormFields().getProcessTransitionXri0().equals(processTransition.refGetPath().toXRI()) ? "selected" : ""%> value="<%= processTransition.refGetPath().toXRI() %>"><%= processTransition.getName() %></option>
<%
									}
								}
%>
							</select>
						</td>
						<td class="addon">
						</td>														
					</tr>
					<tr>
						<td class="label">
							<span class="nw"><%= wc.getFieldLabel("org:opencrx:kernel:activity1:ActivityFollowUp", "title", app.getCurrentLocaleAsIndex()) %>:</span>
						</td>
						<td>
							<input id="followUpTitle0" name="followUpTitle0" type="text" class="valueL" tabindex="<%= tabIndex++ %>" value="<%=wc.getFormFields().getFollowUpTitle0() == null ? "" : wc.getFormFields().getFollowUpTitle0()%>">
						</td>
						<td class="addon">
						</td>														
					</tr>
					<tr>
						<td class="label">
							<span class="nw"><%= wc.getFieldLabel("org:opencrx:kernel:activity1:ActivityFollowUp", "text", app.getCurrentLocaleAsIndex()) %>:</span>
						</td>
						<td rowspan="10">
						  <table style="width:100%;"><tbody><tr>
						    <td style="width:100%;"><div onclick="javascript:loadWIKYedit('followUpText1','./');" onmouseover="javascript: this.style.backgroundColor='#FF9900';this.style.cursor='pointer';" onmouseout="javascript: this.style.backgroundColor='';"><img src="./images/wiki.gif" border="0" alt="wiki" title=""></div></td>
						    <td><div onclick="javascript:$('followUpText1').value=Wiky.toWiki($('followUpText1').value);" onmouseover="javascript: this.style.backgroundColor='#FF9900';this.style.cursor='pointer';" onmouseout="javascript: this.style.backgroundColor='';"><img src="./images/htmltowiki.gif" border="0" alt="html &gt; wiki" title=""></div></td>
						  </tr></tbody></table>
						  <textarea id="followUpText0" name="followUpText0" class="" rows="10" cols="30" style="width:100%;" tabindex="<%= tabIndex++ %>"><%=wc.getFormFields().getFollowUpText0() == null ? "" : wc.getFormFields().getFollowUpText0()%></textarea>
						</td>	
						<td class="addon">
						</td>
					</tr>
				</table>
				<div class="fieldGroupName">FollowUp 2 (optional)</div>
				<table class="fieldGroup">
					<tr>
						<td class="label">
							<span class="nw"><%= wc.getFieldLabel("org:opencrx:kernel:activity1:ActivityFollowUp", "transition", app.getCurrentLocaleAsIndex()) %>:</span>
						</td>
						<td>
							<select id="processTransitionXri1" name="processTransitionXri1" class="valueL" tabindex="<%= tabIndex++ %>" onchange="javascript:$('Refresh.Button').click();" >
<%
								if(wc.getSelectableProcessTransitions2() != null) {
									for(org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition processTransition: wc.getSelectableProcessTransitions2()) {
%>
										<option <%=wc.getFormFields().getProcessTransitionXri1() != null && wc.getFormFields().getProcessTransitionXri1().equals(processTransition.refGetPath().toXRI()) ? "selected" : ""%> value="<%= processTransition.refGetPath().toXRI() %>"><%= processTransition.getName() %></option>
<%
									}
								}
%>
							</select>
						</td>
						<td class="addon">
						</td>														
					</tr>
					<tr>
						<td class="label">
							<span class="nw"><%= wc.getFieldLabel("org:opencrx:kernel:activity1:ActivityFollowUp", "title", app.getCurrentLocaleAsIndex()) %>:</span>
						</td>
						<td>
							<input id="followUpTitle1" name="followUpTitle1" type="text" class="valueL" tabindex="<%= tabIndex++ %>" value="<%=wc.getFormFields().getFollowUpTitle1() == null ? "" : wc.getFormFields().getFollowUpTitle1()%>">
						</td>								
						<td class="addon">
						</td>														
					</tr>
					<tr>
						<td class="label">
							<span class="nw"><%= wc.getFieldLabel("org:opencrx:kernel:activity1:ActivityFollowUp", "text", app.getCurrentLocaleAsIndex()) %>:</span>
						</td>
						<td rowspan="10">
						  <table style="width:100%;"><tbody><tr>
						    <td style="width:100%;"><div onclick="javascript:loadWIKYedit('followUpText2','./');" onmouseover="javascript: this.style.backgroundColor='#FF9900';this.style.cursor='pointer';" onmouseout="javascript: this.style.backgroundColor='';"><img src="./images/wiki.gif" border="0" alt="wiki" title=""></div></td>
						    <td><div onclick="javascript:$('followUpText2').value=Wiky.toWiki($('followUpText2').value);" onmouseover="javascript: this.style.backgroundColor='#FF9900';this.style.cursor='pointer';" onmouseout="javascript: this.style.backgroundColor='';"><img src="./images/htmltowiki.gif" border="0" alt="html &gt; wiki" title=""></div></td>
						  </tr></tbody></table>
						  <textarea id="followUpText1" name="followUpText1" class="" rows="10" cols="30" style="width:100%;" tabindex="<%= tabIndex++ %>"><%=wc.getFormFields().getFollowUpText1() == null ? "" : wc.getFormFields().getFollowUpText1()%></textarea>
						</td>								
						<td class="addon">
						</td>								
					</tr>
				</table>
				<div class="fieldGroupName">&nbsp;</div>
				<table class="fieldGroup">
					<tr>
						<td class="label">
							<span class="nw"><%= app.getLabel("org:opencrx:kernel:activity1:Resource") %>:</span>
						</td>
						<td>
							<select id="assignToXri" name="assignToXri" class="valueL" >
								<option value=""></option>
<%
								org.opencrx.kernel.activity1.jmi1.Segment activitySegment = Activities.getInstance().getActivitySegment(wc.getPm(), wc.getProviderName(), wc.getSegmentName());
				                org.opencrx.kernel.activity1.cci2.ResourceQuery recourceQuery = (org.opencrx.kernel.activity1.cci2.ResourceQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.Resource.class);
				                recourceQuery.orderByName().ascending();
				                recourceQuery.forAllDisabled().isFalse();
								int maxResourceToShow = 0;
				                for(org.opencrx.kernel.activity1.jmi1.Resource resource: activitySegment.getResource(recourceQuery)) {
				                	try {
										org.opencrx.kernel.account1.jmi1.Contact contact = resource.getContact();
										if (contact != null) {
%>
											<option <%= wc.getFormFields().getAssignToXri() != null && wc.getFormFields().getAssignToXri().equals(contact.refGetPath().toXRI()) ? "selected" : "" %> value="<%= contact.refGetPath().toXRI() %>"><%= resource.getName() + " (" + contact.getFullName() + ")" %></option>
<%
										}
									} catch (Exception e) {}
				                	maxResourceToShow++;
				                	if(maxResourceToShow > 200) break;
								}
%>
							</select>
						</td>
						<td class="addon"/>
					</tr>
				</table>
		  		<fieldset style="margin:15px 0px 10px 0px;">
		  			<legend>
		  				Optional <%= app.getLabel(BulkActivityFollowUpWizardController.TIMER_CLASS) %>
						<input type="Checkbox" name="useTimer" id="useTimer" <%= Boolean.TRUE.equals(wc.getFormFields().getUseTimer()) ? "checked" : "" %> tabindex="<%= tabIndex++ %>" value="true" onchange="javascript:
							if(this.checked) {
								$('timerDIV').style.visibility='visible';
								$('timerDIV').style.height='';
							} else {
								$('timerDIV').style.visibility='hidden';
								$('timerDIV').style.height='0px';
							};" />
		  			</legend>
<%
					if(Boolean.TRUE.equals(wc.getFormFields().getUseTimer()) && (wc.getTriggerAtDate() != null && wc.getTriggerAtDate().compareTo(NOW) < 0)) {
%>
						<div style="background-color:red;color:white;border:1px solid black;padding:10px;font-weight:bold;margin-top:10px;">
							Timer is in the past
						</div>
<%
					}
%>
			  		<div id='timerDIV' <%= Boolean.TRUE.equals(wc.getFormFields().getUseTimer()) ? "" : "style='visibility:hidden;height:0px;'" %>>
			  		<table class="fieldGroup">
						<tr>
				  			<td class="label">
								<span class="nw"><%= wc.getFieldLabel(BulkActivityFollowUpWizardController.TIMER_CLASS, "name", app.getCurrentLocaleAsIndex()) %>:</span>
							</td>
							<td>
								<input type="text" class="valueL lightUp" name="timerName" id="timerName" maxlength="50" tabindex="<%= tabIndex++ %>" value="<%= wc.getTimerName().replaceAll("\"", "&quot;") %>" />
							</td>
							<td class="addon"></td>
						</tr>
						<tr>
							<td class="label">
								<span class="nw"><%= wc.getFieldLabel(BulkActivityFollowUpWizardController.TIMER_CLASS, "timerStartAt", app.getCurrentLocaleAsIndex()) %>:</span>
							</td>
							<td style="padding-top:2px;">
								<input type="text" class="valueL <%= wc.getTriggerAtDate() != null ? "lightUp" : "valueError" %>" name="triggerAt" id="triggerAt" maxlength="16" tabindex="<%= tabIndex++ %>" value="<%= wc.getFormFields().getTriggerAt() %>" />
							</td>
							<td class="addon">
								<a><img class="popUpButton" id="cal_trigger_triggerAt" border="0" alt="Click to open Calendar" src="images/cal.gif" /></a>
								<script type="text/javascript">
									  Calendar.setup({
										inputField: "triggerAt",
										ifFormat: "%d-%m-%Y %H:%M",
										timeFormat: "24",
										button: "cal_trigger_triggerAt",
										align: "Tr",
										singleClick: true,
										showsTime: true
									  });
								</script>
							</td>
						</tr>
					</table>
					</div>
				</fieldset>
				<div id="WaitIndicator" style="display:none;">
					Processing request - please wait...<br>
					<img border="0" src='./images/progress_bar.gif' alt='please wait...' />
				</div>						
				<div id="SubmitArea">
					<input type="submit" name="Refresh" id="Refresh.Button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getReloadText() %>" onclick="javascript:$('Command').value=this.name;" />
					<input type="submit" name="OK" id="OK.Button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getOkTitle() %>" onclick="javascript:$('Command').value=this.name;this.name='--';$('WaitIndicator').style.display='block';$('SubmitArea').style.visibility='hidden';" />
					<input type="submit" name="Cancel" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getCancelTitle() %>" onclick="javascript:$('Command').value=this.name;" />
				</div>
			</td>
		</tr>
	</table>
</form>
<br>&nbsp;
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
