<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="portal" uri="http://www.openmdx.org/tags/openmdx-portal"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
/*
 * ====================================================================
 * Project:	    openCRX/Core, http://www.opencrx.org/
 * Description: BulkCreateActivityWizard.jsp
 * Owner:	    CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2011-2014, CRIXP Corp., Switzerland
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
org.opencrx.kernel.portal.*,
org.opencrx.kernel.backend.*,
org.opencrx.kernel.portal.wizard.*,
org.opencrx.kernel.workflow.BulkCreateActivityWorkflow.CreationType,
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.base.exception.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.component.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*
" %>
<!--
	<meta name="label" content="Bulk - Create Activity">
	<meta name="toolTip" content="Bulk - Create Activity">
	<meta name="targetType" content="_inplace">
	<meta name="forClass" content="org:opencrx:kernel:account1:AccountFilterGlobal">
	<meta name="forClass" content="org:opencrx:kernel:account1:Group">
	<meta name="forClass" content="org:opencrx:kernel:account1:AddressFilterGlobal">
	<meta name="forClass" content="org:opencrx:kernel:activity1:AddressGroup">
	<meta name="forClass" content="org:opencrx:kernel:activity1:ActivityGroup">
	<meta name="order" content="5555">
-->
<%
	final String FORM_NAME = "BulkCreateActivityForm";
	final String FORM_NAME_CREATOR = FORM_NAME + "Creator";
	final String FORM_NAME_PLACEHOLDERS = FORM_NAME + "PlaceHolders";
	final String FORM_NAME_ACTIVITY = FORM_NAME + "Activity";
	final String FORM_NAME_EMAIL = FORM_NAME + "EMail";
	final String FORM_NAME_EMAILTO = FORM_NAME + "EMailTo";
	final String FORM_NAME_RECIPIENT = FORM_NAME + "Recipient";
	final String WIZARD_NAME = "BulkCreateActivityWizard.jsp";

	BulkCreateActivityWizardController wc = new BulkCreateActivityWizardController();
%>
	<t:wizardHandleCommand controller='<%= wc %>' defaultCommand='Reload' />
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
	if(wc.getErrorMessage() != null && !wc.getErrorMessage().isEmpty()) {
%>
		<div class="alert alert-danger" role="alert">
		  <table>
		    <tr>
		    	<td style="vertical-align:top;padding:10px;"><span class="glyphicon glyphicon-exclamation-sign"></span></td>
		    	<td><%= wc.getErrorMessage() %></td>
		    </tr>
		  </table>
		</div>
<%
	}
%>
	<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= wc.getRequestId() %>" />
	<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= wc.getObjectIdentity().toXRI() %>" />
	<input type="hidden" id="Command" name="Command" value="" />
	<input type="hidden" id="activityGroupXri" name="activityGroupXri" value="" />
	<input type="checkbox" style="display:none;" id="isInitialized" name="isInitialized" checked />
	<input type="checkbox" style="display:none;" id="excludeNoBulkEMail" name="excludeNoBulkEMail" <%= Boolean.TRUE.equals(wc.getExcludeNoBulkEMail()) ? "checked" : "" %> />
	<table class="tableLayout">
		<tr>
			<td class="cellObject">
				<div class="panel" id="panel<%= FORM_NAME_CREATOR %>" style="display:block;overflow:visible;">
<%
					wc.getForms().get(FORM_NAME_CREATOR).paint(viewPort, null, true);
					viewPort.flush();
					if(wc.getSelectedLocale() != null) {
%>
						<div class="container-fluid">
							<div class="row">
								<div class="col-lg-12">
									<table class="fieldGroup">
										<tr>
											<td title="" class="<%= CssClass.fieldLabel %>"><span class="nw">Locale:</span></td>
											<td>
											    <select name="selectedLocale" tabindex="2100" class="valueL" onchange="javascript:$('Refresh.Button').click();">
<%
													for(Short locale: wc.getSelectableLocales()) {
%>
														<option value="<%= locale %>" <%= wc.getSelectedLocale() != null && locale.equals(wc.getSelectedLocale()) ? "selected" : "" %>> <%= wc.getCodes().getLongTextByCode("locale", app.getCurrentLocaleAsIndex(), true).get(locale) %></option>
<%
													}
%>
											    </select>
											</td>
											<td class="addon"></td>
										</tr>
										<tr>
											<td title="" class="<%= CssClass.fieldLabel %>"><span class="nw"><%= wc.getFieldLabel("org:opencrx:kernel:activity1:ActivityGroup", "targetGroupAccounts", app.getCurrentLocaleAsIndex()) %>:</span></td>
											<td>
											    <select tabindex="2110" name="selectedTargetGroupXri" class="valueL" onchange="javascript:$('Refresh.Button').click();">
													<option value=""></option>
<%
													List<org.openmdx.base.jmi1.BasicObject> selectableTargetGroups = wc.getSelectableTargetGroups();
													for(org.openmdx.base.jmi1.BasicObject targetGroup: selectableTargetGroups) {
%>
														<option value="<%= targetGroup.refGetPath().toXRI() %>" <%= wc.getSelectedTargetGroup() != null && wc.getSelectedTargetGroup().refGetPath().equals(targetGroup.refGetPath()) ? "selected" : ""%>><%= wc.getApp().getPortalExtension().getTitle(targetGroup, app.getCurrentLocaleAsIndex(), app.getCurrentLocaleAsString(), false, app) %></option>
<%
													}
%>
											    </select>
											</td>
											<td class="addon"></td>
										</tr>
									</table>
								</div>
							</div>
						</div>						
<%
					}
%>						
				</div>
				<p>
				<div id="SubmitArea1">
					<input type="submit" name="Refresh" id="Refresh.Button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="8001" value="<%= wc.getTexts().getReloadText() %>" style="display:none;" onclick="javascript:if($('Command').value==''){$('Command').value=this.name;this.name='--'};$('SubmitArea1').style.visibility='hidden';" />
				</div>
<%
				if(wc.getActivityCreator() != null) {
%>
					<p>
					<div id="WaitIndicator" style="display:none;float:left;">
						<img border="0" src='./images/progress_bar.gif' alt='please wait...' />
					</div>
					<div id="SubmitArea" style="float:left;">
<%
						if(Boolean.TRUE.equals(wc.getCanCreate())) {
%>
							<input type="submit" name="CreateTest" id="CreateTest.Button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="9010" value="Create/Update <%= BulkCreateActivityWizardController.NUM_OF_TEST_ACTIVITIES %> Test Activities" <%= wc.getCreationType() == CreationType.CREATE_TEST ? "disabled" : "" %> onclick="javascript:$('Command').value=this.name;this.name='--';$('WaitIndicator').style.display='block';$('SubmitArea').style.visibility='hidden';" />
							<input type="submit" name="Create" id="Create.Button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="9030" value="Create/Update Activities" <%= wc.getCreationType() == CreationType.CREATE ? "disabled" : "" %> onclick="javascript:$('Command').value=this.name;this.name='--';$('WaitIndicator').style.display='block';$('SubmitArea').style.visibility='hidden';" />
<%
						}
%>
						<input type="submit" name="Save" id="Save.Button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="9000" value="<%= app.getTexts().getSaveTitle() %>" onclick="javascript:$('Command').value=this.name;this.name='--';$('WaitIndicator').style.display='block';$('SubmitArea').style.visibility='hidden';" />
						<input type="submit" name="Cancel" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="9040" value="<%= app.getTexts().getCancelTitle() %>" onclick="javascript:$('Command').value=this.name;" />
						<p>
<%						
						if(!Boolean.TRUE.equals(wc.getCanCreate())) {							
%>
							<div class="alert alert-danger">
								Unable to proceed because some data is missing. Please check:
								<ul>
<%
									if(wc.getActivityCreator() == null) {
%>								
										<li>Activity creator</li>
<%
									}
									if(!wc.hasTargetGroupAccounts()) {
%>										
										<li><%= wc.getFieldLabel("org:opencrx:kernel:activity1:ActivityGroup", "targetGroupAccounts", app.getCurrentLocaleAsIndex()) %></li>
<%
									}
									if(!wc.hasEMailSender()) {
%>										
										<li>E-Mail sender</li>
<%
									}
%>										
								</ul>
							</div>
<%
						}
%>
					</div>
					<div style="clear:both;"></div>
<%
					if(wc.getExecutionReport() != null && !wc.getExecutionReport().isEmpty()) {
%>
						<p>
						<div class="fieldGroupName">Report</div>
						<div class="alert alert-info">
							<pre>
<%
								for(String line: wc.getExecutionReport()) {
%><%= line %><br /><%
								}
%>
							</pre>
						</div>
<%
					}
					org.opencrx.kernel.activity1.jmi1.ActivityType actType = null;
					try {
						actType = wc.getActivityCreator().getActivityType();						
					} catch(Exception e) {
						new ServiceException(e).log();
					}
					boolean isEMailType = actType != null && actType.getActivityClass() == org.opencrx.kernel.backend.Activities.ActivityClass.EMAIL.getValue();
					if(Boolean.TRUE.equals(wc.getCanCreate())) {
						try {
							if(
								isEMailType &&
								!Boolean.TRUE.equals(wc.getSelectedAccountsOnly()) &&
								wc.getCreationType() == CreationType.CREATE_TEST
							) {
%>							
								<div class="panel" id="panel<%= FORM_NAME_EMAILTO %>" style="overflow:visible;">
<%
									wc.getForms().get(FORM_NAME_EMAIL).paint(viewPort, null, true);
									viewPort.flush();
%>
								</div>
<%
							} else if (Boolean.TRUE.equals(wc.getSelectedAccountsOnly())) {
%>							
								<div class="panel" id="panel<%= FORM_NAME_RECIPIENT %>" style="display:<%= wc.getCreationType() == CreationType.CREATE_TEST ? "block" : "none" %>;overflow:visible;">
<%
									wc.getForms().get(FORM_NAME_RECIPIENT).paint(viewPort, null, true);
									viewPort.flush();
%>
								</div>
<%
								}
							} catch (Exception e) {
								new ServiceException(e).log();
							}
							if(
								wc.getCreationType() == CreationType.CREATE || 
								wc.getCreationType() == CreationType.CREATE_TEST
							) {
%>
							<div class="alert alert-warning">
<%
								if (
									wc.getActivityType().getActivityClass() == Activities.ActivityClass.EMAIL.getValue()
								) {
%>
									<input type="checkbox" id="excludeNoBulkEMail2" name="excludeNoBulkEMail2" <%= Boolean.TRUE.equals(wc.getExcludeNoBulkEMail()) ? "checked" : "" %> onchange="javascript:$('excludeNoBulkEMail').checked=this.checked;" /> skip e-mail addresses of contacts marked "<%= wc.getFieldLabel(BulkCreateActivityWizardController.CONTACT_CLASS, "doNotEMail", app.getCurrentLocaleAsIndex()) %>"<br>
<%
								}
%>
								Do you really want to create/update the activities listed in the report?&nbsp;&nbsp;
								<input type="submit" name="CreateTestConfirmed" id="CreateTestConfirmed.Button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="9050" value="OK" <%= wc.getCreationType() == CreationType.CREATE_TEST ? "" : "style='display:none;'" %> onclick="javascript:$('Command').value=this.name;this.name='--';$('WaitIndicator').style.display='block';$('SubmitArea').style.visibility='hidden';" />
								<input type="submit" name="CreateConfirmed" id="CreateConfirmed.Button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="9060" value="OK" <%= wc.getCreationType() == CreationType.CREATE ? "" : "style='display:none;'" %> onclick="javascript:$('Command').value=this.name;this.name='--';$('WaitIndicator').style.display='block';$('SubmitArea').style.visibility='hidden';" />
								<input type="button" name="CancelCreate" id="CancelCreate.Button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="9070" value="<%= app.getTexts().getCancelTitle() %>" onclick="javascript:$('Refresh.Button').click();" />
							</div>
<%
						}
					}
					if(isEMailType) {
%>
						<p>
						<ul style="z-index:201;" class="nav nav-tabs nav-condensed">
    						<li class="active"><a data-toggle="tab" href="#panel<%= FORM_NAME_EMAIL %>"><%= app.getLabel("org:opencrx:kernel:activity1:EMail") %></a></li>
    						<li class=""><a data-toggle="tab" href="#panel<%= FORM_NAME_ACTIVITY %>"><%= app.getLabel("org:opencrx:kernel:activity1:Activity") %></a></li>
  						</ul>
  						<div class="tab-content" style="display:block">
							<div class="panel tab-pane active" id="panel<%= FORM_NAME_EMAIL %>" style="overflow:visible;">
<%
								wc.getForms().get(FORM_NAME_EMAIL).paint(viewPort, null, true);
								viewPort.flush();
								wc.getForms().get(FORM_NAME_PLACEHOLDERS).paint(viewPort, null, true);
								viewPort.flush();								
%>
							</div>
<%
					}
%>
					<div class="panel tab-pane" id="panel<%= FORM_NAME_ACTIVITY %>" style="overflow:visible;">
<%
						wc.getForms().get(FORM_NAME_ACTIVITY).paint(viewPort, null, true);
						viewPort.flush();
%>
					</div>
<%
					if(isEMailType) {
%>
						</div>
<%
					}
				}
%>							
			</td>
		</tr>
	</table>
</form>
<p>		
<%
if(wc.getActivityCreator() != null) {
	org.opencrx.kernel.activity1.cci2.ActivityQuery activityQuery = 
		(org.opencrx.kernel.activity1.cci2.ActivityQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.Activity.class);
	activityQuery.thereExistsLastAppliedCreator().equalTo(wc.getActivityCreator());
	int ii = 0;
	int tabIdx = 9050;
	for(org.opencrx.kernel.activity1.jmi1.ActivityGroup activityGroup: wc.getActivityCreator().<org.opencrx.kernel.activity1.jmi1.ActivityGroup>getActivityGroup()) {
		boolean isSelectedActivityGroup = wc.getActivityGroup() != null && activityGroup.refGetPath().equals(wc.getActivityGroup().refGetPath());
%>
		<div class="fieldGroupName"><%= (new ObjectReference(activityGroup, app)).getLabel() + " " + activityGroup.getName() %></div>
		<div style='padding:4px;'>
			<h2>
				<input type="button" name="CountActivities" id="CountActivities<%= ii %>.Button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="<%= tabIdx++ %>" value='Count Activities<%= isSelectedActivityGroup && wc.getNumberOfFilteredActivities() != null ? " (=" + wc.getNumberOfFilteredActivities() + ")" : "" %>' onclick="javascript:$('Command').value=this.name;this.name='--';$('activityGroupXri').value='<%= activityGroup.refGetPath().toXRI() %>';$('WaitIndicator').style.display='block';$('SubmitArea').style.visibility='hidden';$('Refresh.Button').click();" />
				<input type="button" name="ConfirmDeleteActivities" id="ConfirmDeleteActivities<%= ii %>.Button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="<%= tabIdx++ %>" value="<%= app.getTexts().getDeleteTitle() %> Activities" <%= isSelectedActivityGroup && Boolean.TRUE.equals(wc.getConfirmDeleteActivities()) ? "disabled" : "" %> onclick="javascript:$('Command').value=this.name;this.name='--';$('activityGroupXri').value='<%= activityGroup.refGetPath().toXRI() %>';$('WaitIndicator').style.display='block';$('SubmitArea').style.visibility='hidden';$('Refresh.Button').click();" />
			</h2>
<%
			if(isSelectedActivityGroup && Boolean.TRUE.equals(wc.getConfirmDeleteActivities())) {
%>
				<div style="border:1px solid black;padding:10px;margin:2px;background-color:#FF9900;">
					Do you really want to delete all activities assigned to this tracker?
					<input type="button" name="DeleteActivities" id="DeleteActivities<%= ii %>.Button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="<%= tabIdx++ %>" value="<%= app.getTexts().getDeleteTitle() %>" onclick="javascript:$('Command').value=this.name;this.name='--';$('activityGroupXri').value='<%= activityGroup.refGetPath().toXRI() %>';$('WaitIndicator').style.display='block';$('SubmitArea').style.visibility='hidden';$('Refresh.Button').click();" />
					<input type="button" name="CancelDelete.Button" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="<%= tabIdx++ %>" value="<%= app.getTexts().getCancelTitle() %>" onclick="javascript:$('Refresh.Button').click();" />
				</div>
<%
			}
			if(!(wc.getObject() instanceof org.opencrx.kernel.activity1.jmi1.ActivityGroup)) {
%>
				<portal:showobject id='<%= "A" + ii %>' object="<%= activityGroup.refGetPath() %>" navigationTarget="_blank" resourcePathPrefix="./" showAttributes="true" grids="filteredActivity">
					<portal:query name="filteredActivity" query="<%= activityQuery %>" />
				</portal:showobject>
<%
			}
%>			
		</div>
<%
		ii++;
	}
}
%>
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
  	try {
  		$('org:opencrx:kernel:activity1:Activity:lastAppliedCreator[1100]').onchange = function() {$('Reload.Button').click();};  		
  	} catch(e){};
</script>
<t:wizardClose controller="<%= wc %>" />
