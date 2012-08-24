<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: BulkActivityFollowUpWizard.jsp,v 1.10 2012/07/08 13:30:03 wfro Exp $
 * Description: BulkActivityFollowUpWizard.jsp
 * Revision:    $Revision: 1.10 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2012/07/08 13:30:03 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2011-2012, CRIXP Corp., Switzerland
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
<%@ taglib uri="http://www.openmdx.org/tags/openmdx-portal" prefix="portal" %>
<%@ page session="true" import="
java.util.*,
java.io.*,
java.text.*,
org.opencrx.kernel.portal.*,
org.opencrx.kernel.backend.*,
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.base.exception.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.reports.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*
" %>
<%!

	public static java.util.Date incDate(
		java.util.Date date,
		int numberOfMinutes,
		ApplicationContext app
	) {
		GregorianCalendar cal = new GregorianCalendar(app.getCurrentLocale());
		cal.setTimeZone(TimeZone.getTimeZone(app.getCurrentTimeZone()));
		cal.setMinimalDaysInFirstWeek(4); // this conforms to DIN 1355/ISO 8601
		cal.setFirstDayOfWeek(GregorianCalendar.MONDAY);
		cal.setTime(date);
		cal.add(GregorianCalendar.MINUTE, numberOfMinutes);
		return cal.getTime();
	}

%>
<%
	request.setCharacterEncoding("UTF-8");
	String servletPath = "." + request.getServletPath();
	String servletPathPrefix = servletPath.substring(0, servletPath.lastIndexOf("/") + 1);
	ApplicationContext app = (ApplicationContext)session.getValue(WebKeys.APPLICATION_KEY);
	ViewsCache viewsCache = (ViewsCache)session.getValue(WebKeys.VIEW_CACHE_KEY_SHOW);
	String requestId =  request.getParameter(Action.PARAMETER_REQUEST_ID);
	String objectXri = request.getParameter(Action.PARAMETER_OBJECTXRI);
	if(objectXri == null || app == null || viewsCache.getView(requestId) == null) {
		response.sendRedirect(
			request.getContextPath() + "/" + WebKeys.SERVLET_NAME
		);
		return;
	}
	javax.jdo.PersistenceManager pm = app.getNewPmData();
	org.openmdx.base.persistence.cci.UserObjects.setBulkLoad(pm, true);
	RefObject_1_0 obj = (RefObject_1_0)pm.getObjectById(new Path(objectXri));
	Texts_1_0 texts = app.getTexts();
	Codes codes = app.getCodes();
	final String FORM_NAME = "bulkActivityFollowUpForm";
	final String wizardName = "BulkActivityFollowUpWizard.jsp";
	final String TIMER_CLASS = "org:opencrx:kernel:home1:Timer";

	try {
    	// Get Parameters
		String command = request.getParameter("Command");
		if(command == null) command = "";
		boolean actionCreate = "OK".equals(command);
		boolean actionCancel = "Cancel".equals(command);
		if(actionCancel || (!(obj instanceof org.opencrx.kernel.activity1.jmi1.Activity))) {
			session.setAttribute(wizardName, null);
			Action nextAction = new ObjectReference(obj, app).getSelectObjectAction();
			response.sendRedirect(
				request.getContextPath() + "/" + nextAction.getEncodedHRef()
			);
			return;
		}
		String activityGroupXri = request.getParameter("activityGroupXri");
		org.opencrx.kernel.activity1.jmi1.Activity activity = (org.opencrx.kernel.activity1.jmi1.Activity)obj;
		String providerName = activity.refGetPath().get(2);
		String segmentName = activity.refGetPath().get(4);
		String name = request.getParameter("name") == null ? (activity != null && activity.getName() != null ? activity.getName() : "--") + " (" + app.getLoginPrincipal() + ")" : request.getParameter("name");
		boolean triggerAtDateOk = false;
		SimpleDateFormat activityDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", app.getCurrentLocale());
		java.util.Date proposedTriggerAt = incDate(new java.util.Date(), 15, app);
		String triggerAt = request.getParameter("triggerAt") == null ? activityDateFormat.format(proposedTriggerAt)	: request.getParameter("triggerAt");
		java.util.Date triggerAtDate = null;
		try {
	  		if ((triggerAt != null) && (triggerAt.length() == 16)) {
				triggerAtDate = new java.util.Date();
				triggerAtDate.setYear(Integer.parseInt(triggerAt.substring(6,10))-1900);
				triggerAtDate.setMonth(Integer.parseInt(triggerAt.substring(3,5))-1);
				triggerAtDate.setDate(Integer.parseInt(triggerAt.substring(0,2)));
				triggerAtDate.setHours(Integer.parseInt(triggerAt.substring(11,13)));
				triggerAtDate.setMinutes(Integer.parseInt(triggerAt.substring(14,16)));
				triggerAtDate.setSeconds(0);
				triggerAtDateOk = true;
			}
		} catch (Exception e) {}		
		boolean triggerAtIsInTheFuture = triggerAtDateOk && triggerAtDate.compareTo(new java.util.Date()) > 0;
		boolean useTimer = (request.getParameter("useTimer") != null) && (request.getParameter("useTimer").length() > 0);
%>
      <!--
      	<meta name="label" content="Bulk - Activity Follow up">
      	<meta name="toolTip" content="Bulk - Activity Follow up">
      	<meta name="targetType" content="_inplace">
      	<meta name="forClass" content="org:opencrx:kernel:activity1:Activity">
      	<meta name="order" content="5555">
      -->
<%
		org.openmdx.ui1.jmi1.FormDefinition formDefinition = app.getUiFormDefinition(FORM_NAME);
		org.openmdx.portal.servlet.control.FormControl bulkActivityFollowUpForm = new org.openmdx.portal.servlet.control.FormControl(
			formDefinition.refGetPath().getBase(),
			app.getCurrentLocaleAsString(),
			app.getCurrentLocaleAsIndex(),
			app.getUiContext(),
			formDefinition
		);
		Map formValues = new HashMap();
		bulkActivityFollowUpForm.updateObject(
    		request.getParameterMap(),
    		formValues,
    		app,
    		pm
    	);
		boolean isFirstCall = request.getParameter("isFirstCall") == null;
		// Initialize formValues on first call
		if(isFirstCall) {
			// Nothing todo
		}

		// Get identity of activities to be updated
		org.opencrx.kernel.activity1.jmi1.ActivityProcessState processState = activity.getProcessState();
		org.opencrx.kernel.activity1.cci2.ActivityQuery activityQuery = (org.opencrx.kernel.activity1.cci2.ActivityQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.Activity.class);
		activityQuery.thereExistsProcessState().equalTo(processState);
		List<Path> activityIdentities = new ArrayList<Path>();
		if (activityGroupXri == null || activityGroupXri.length() == 0) {
			activityGroupXri = null;
			for(Iterator i = activity.getAssignedGroup().iterator(); i.hasNext() && activityGroupXri == null;) {
				try {
					org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment activityGroupAssignment = (org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment)i.next();
					if (activityGroupAssignment.getActivityGroup() != null) {
						activityGroupXri = activityGroupAssignment.getActivityGroup().refMofId();
					}
				} catch (Exception e) {
					new ServiceException(e).log();
				}
			}
		}
		org.opencrx.kernel.activity1.jmi1.ActivityGroup activityGroup = null;
		if (activityGroupXri != null) {
			try {
				activityGroup = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)pm.getObjectById(
					new Path(activityGroupXri)
				);
				Collection<org.opencrx.kernel.activity1.jmi1.Activity> activities = activityGroup.getFilteredActivity(activityQuery);
				for(org.opencrx.kernel.activity1.jmi1.Activity a: activities) {
					activityIdentities.add(a.refGetPath());
				}
			} catch (Exception e) {
				new ServiceException(e).log();
			}
		}
		if(actionCreate && activityGroup != null) {
		    org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition processTransition =
	        	(org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition)pm.getObjectById(
	        		formValues.get("org:opencrx:kernel:activity1:ActivityDoFollowUpParams:transition")
	        	);
		    String transitionName = processTransition != null && processTransition.getName() != null ? processTransition.getName() : "?";
    	    String followUpTitle = (String)formValues.get("org:opencrx:kernel:activity1:ActivityDoFollowUpParams:followUpTitle");
    	    String followUpText = (String)formValues.get("org:opencrx:kernel:activity1:ActivityDoFollowUpParams:followUpText");
			org.opencrx.kernel.account1.jmi1.Contact assignTo = formValues.get("org:opencrx:kernel:activity1:Activity:assignedTo") != null ?
    	    	(org.opencrx.kernel.account1.jmi1.Contact)pm.getObjectById(
    	    		formValues.get("org:opencrx:kernel:activity1:Activity:assignedTo")
    	    	) : null;
    	    
    	    if (useTimer) {
    	    	// asynchronous execution, i.e. create timer
				if(
					triggerAtDateOk &&
					name != null && !name.isEmpty() && 
					triggerAtIsInTheFuture
				) {
					try {
		  				pm.currentTransaction().begin();
		  				org.opencrx.kernel.home1.jmi1.UserHome currentUserHome = org.opencrx.kernel.backend.UserHomes.getInstance().getUserHome(new Path(objectXri), pm, true);		  
							org.opencrx.kernel.home1.jmi1.Timer timer = org.opencrx.kernel.utils.Utils.getHomePackage(pm).getTimer().createTimer();
							timer.refInitialize(false, false);
							timer.setName(transitionName + ": " + name);
							timer.setTimerStartAt(triggerAtDate);
							try {
								org.opencrx.kernel.activity1.jmi1.WfAction action = null;
								org.opencrx.kernel.workflow1.jmi1.WfProcess wfProcess = null;
								org.opencrx.kernel.workflow1.jmi1.Segment workflowSegment = Workflows.getInstance().getWorkflowSegment(pm, providerName, segmentName);
								wfProcess = org.opencrx.kernel.backend.Workflows.getInstance().findWfProcess(org.opencrx.kernel.backend.Workflows.WORKFLOW_NAME_BULK_ACTIVITY_FOLLOWUP, workflowSegment, pm);
								if (wfProcess != null) {
									timer.getAction().add(wfProcess);
								}
							} catch (Exception e) {
								new ServiceException(e).log();
							}
							
							timer.setTriggerRepeat(1);
							timer.setTriggerIntervalMinutes(1);
							timer.setDisabled(false);
							timer.setTimerState(new Short((short)10)); // open
							timer.setTimerEndAt(incDate(triggerAtDate, 60, app));
							timer.setTarget((org.openmdx.base.jmi1.BasicObject)activityGroup);
							currentUserHome.addTimer(
								false,
								org.opencrx.kernel.backend.Base.getInstance().getUidAsString(),
								timer
							);

						// set additional properties required for delayed execution
						org.opencrx.kernel.base.jmi1.ReferenceProperty refprop = pm.newInstance(org.opencrx.kernel.base.jmi1.ReferenceProperty.class);
						refprop.refInitialize(false, false);
						refprop.setName("activity" /* org.opencrx.kernel.workflow.BulkActivityFollowUpWorkflow.OPTION_ACTIVITY */);
						refprop.setReferenceValue((org.openmdx.base.jmi1.BasicObject)activity);
						timer.addProperty(
								false,
								org.opencrx.kernel.backend.Base.getInstance().getUidAsString(),
								refprop
							);
						
						refprop = pm.newInstance(org.opencrx.kernel.base.jmi1.ReferenceProperty.class);
						refprop.refInitialize(false, false);
						refprop.setName("transition" /* org.opencrx.kernel.workflow.BulkActivityFollowUpWorkflow.OPTION_TRANSITION */);
						refprop.setReferenceValue((org.openmdx.base.jmi1.BasicObject)processTransition);
						timer.addProperty(
								false,
								org.opencrx.kernel.backend.Base.getInstance().getUidAsString(),
								refprop
							);
						
						refprop = pm.newInstance(org.opencrx.kernel.base.jmi1.ReferenceProperty.class);
						refprop.refInitialize(false, false);
						refprop.setName("assignTo" /* org.opencrx.kernel.workflow.BulkActivityFollowUpWorkflow.OPTION_ASSIGN_TO */);
						refprop.setReferenceValue((org.openmdx.base.jmi1.BasicObject)assignTo);
						timer.addProperty(
								false,
								org.opencrx.kernel.backend.Base.getInstance().getUidAsString(),
								refprop
							);
						
						org.opencrx.kernel.base.jmi1.StringProperty strprop = pm.newInstance(org.opencrx.kernel.base.jmi1.StringProperty.class);
						strprop.refInitialize(false, false);
						strprop.setName("followUpTitle" /* org.opencrx.kernel.workflow.BulkActivityFollowUpWorkflow.OPTION_FOLLOW_UP_TITLE */);
						strprop.setStringValue(followUpTitle);
						timer.addProperty(
								false,
								org.opencrx.kernel.backend.Base.getInstance().getUidAsString(),
								strprop
							);

						strprop = pm.newInstance(org.opencrx.kernel.base.jmi1.StringProperty.class);
						strprop.refInitialize(false, false);
						strprop.setName("followUpText" /* org.opencrx.kernel.workflow.BulkActivityFollowUpWorkflow.OPTION_FOLLOW_UP_TEXT */);
						strprop.setStringValue(followUpText);
						timer.addProperty(
								false,
								org.opencrx.kernel.backend.Base.getInstance().getUidAsString(),
								strprop
							);
						
						pm.currentTransaction().commit();

						// Go back to previous view
						Action action = new ObjectReference(obj, app).getSelectObjectAction();
						response.sendRedirect(
							request.getContextPath() + "/" + action.getEncodedHRef()
						);
						return;
					}
					catch(Exception e) {
						try {
							pm.currentTransaction().rollback();
						} catch(Exception e1) {}
						new ServiceException(e).log();
					}
				}
    	    } else {
	    	    // synchronous execution, i.e. create follow ups
	    	    if(processState != null && processTransition != null) {
	    	    	for(Path activityIdentity: activityIdentities) {
	    	    		try {
							org.opencrx.kernel.activity1.jmi1.ActivityDoFollowUpParams params = org.opencrx.kernel.utils.Utils.getActivityPackage(pm).createActivityDoFollowUpParams(
								assignTo,
								followUpText,
								followUpTitle,
								processTransition
							);					
							pm.currentTransaction().begin();
							org.opencrx.kernel.activity1.jmi1.Activity a = (org.opencrx.kernel.activity1.jmi1.Activity)pm.getObjectById(activityIdentity);
							org.opencrx.kernel.activity1.jmi1.ActivityDoFollowUpResult result = a.doFollowUp(params);
							pm.currentTransaction().commit();
							if(assignTo != null) {
								pm.currentTransaction().begin();
	    	          			a.setAssignedTo(assignTo);
	    	          			pm.currentTransaction().commit();
							}
	    	    		}
	    	    		catch(Exception e) {
		    				new ServiceException(e).log();
	    	    			try {
	    	    				pm.currentTransaction().rollback();
	    	    			} catch(Exception e0) {}
	    	    		}
	    	    	}
					// Go back to previous view
					Action action = new ObjectReference(obj, app).getSelectObjectAction();
					response.sendRedirect(
						request.getContextPath() + "/" + action.getEncodedHRef()
					);
					return;
				}
    	    }
		}
    	TransientObjectView view = new TransientObjectView(
    		formValues,
    		app,
    		obj,
    		pm
    	);
    	ViewPort p = ViewPortFactory.openPage(
    		view,
    		request,
    		out
    	);
		UserDefinedView userView = new UserDefinedView(
			pm.getObjectById(new Path(objectXri)),
			app,
			viewsCache.getView(requestId)
		);
%>
		
<%@page import="org.opencrx.kernel.workflow.BulkActivityFollowUpWorkflow"%><br />
		<h1>Bulk activity follow up wizard</h1>
		<div style="border:1px solid black;padding:10px;margin:2px;background-color:#FF9900;">
			<strong>WARNING:</strong> Follow up will be performed on <strong><%= activityIdentities.size() %> activities</strong> assigned to '<%= activityGroup != null ? activityGroup.getName() : "--" %>'<br>
			<img id='detailsHidden' style="display:block;" src="images/expand_down.gif" border="0" align="top" title="<%= app.getTexts().getShowDetailsTitle() %>" alt="<%= app.getTexts().getShowDetailsTitle() %>" onclick="javascript:$('BulkActivities-00').style.display='block';$('detailsShown').style.display='block';$('detailsHidden').style.display='none';" />
			<img id='detailsShown'  style="display:none;"  src="images/shrink_up.gif"   border="0" align="top" alt="^"  onclick="javascript:$('BulkActivities-00').style.display='none'; $('detailsShown').style.display='none';$('detailsHidden').style.display='block';" />
<%
			if (activityGroup != null) {
%>		
				<div id="BulkActivities-00" style="border:1px solid black;padding:2x;background-color:#fff;display:none;">
					<portal:showobject id='BulkActivityFollowUpWizard-Activities' object="<%= activityGroup.refGetPath() %>" navigationTarget="_blank" resourcePathPrefix="./" showAttributes="false" grids="filteredActivity">
						<portal:query name="filteredActivity" query="<%= activityQuery %>" />
					</portal:showobject>
				</div>
<%
			}
%>

		</div>
		<form id="<%= FORM_NAME %>" name="<%= FORM_NAME %>" accept-charset="UTF-8" method="POST" action="<%= servletPath %>">
			<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= requestId %>" />
			<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= objectXri %>" />
			<input type="hidden" id="Command" name="Command" value="" />
			<input type="checkbox" style="display:none;" id="isFirstCall" name="isFirstCall" checked="true" />
			<table cellspacing="8" class="tableLayout">
				<tr>
					<td class="cellObject">

						<table class="fieldGroup">
							<tbody>
								<tr>
									<td class="label" title=""><span class="nw"><%= userView.getFieldLabel("org:opencrx:kernel:activity1:ActivityGroupAssignment", "activityGroup", app.getCurrentLocaleAsIndex()) %>:</span></td>
									<td>
										<select id="activityGroupXri" name="activityGroupXri" class="valueL" tabindex="100" onchange="javascript:$('Refresh.Button').click();" >
<%
											boolean hasEntries = false;
											for(Iterator i = activity.getAssignedGroup().iterator(); i.hasNext();) {
												try {
													org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment activityGroupAssignment = (org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment)i.next();
													if (activityGroupAssignment.getActivityGroup() != null) {
														org.opencrx.kernel.activity1.jmi1.ActivityGroup ag = activityGroupAssignment.getActivityGroup();
														String activityGroupTitle = "--";
														try {
															activityGroupTitle = app.getHtmlEncoder().encode(new ObjectReference(ag, app).getTitle(), false);
														} catch (Exception e) {}
														hasEntries = true;
%>
														<option <%= (activityGroupXri != null) && (activityGroupXri.compareTo(ag.refMofId()) == 0) ? "selected" : "" %> value="<%= ag.refMofId() %>"><%= activityGroupTitle %></option>
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
						<div />
<%

%>					
						<div class="panel" id="panel<%= FORM_NAME %>" style="display: block">
<%
							bulkActivityFollowUpForm.paint(
								p,
								null, // frame
								true // forEditing
							);
							p.flush();
%>
						</div>

				  		<fieldset style="margin:15px 0px 10px 0px;">
				  			<legend nowrap>
				  				Optional <%= app.getLabel(TIMER_CLASS) %>
								<input type="Checkbox" name="useTimer" id="useTimer" <%= useTimer ? "checked" : "" %> tabindex="1000" value="useTimer" onchange="javascript:
									if(this.checked) {
										$('timerDIV').style.visibility='visible';
										$('timerDIV').style.height='';
									} else {
										$('timerDIV').style.visibility='hidden';
										$('timerDIV').style.height='0px';
									};" />
				  			</legend>
<%
							if (!triggerAtIsInTheFuture) {
%>
								<div style="background-color:red;color:white;border:1px solid black;padding:10px;font-weight:bold;margin-top:10px;">
									Timer is in the past
								</div>
<%
							}
%>
					  		<div id='timerDIV' <%= useTimer ? "" : "style='visibility:hidden;height:0px;'" %>>
					  		<table class="fieldGroup">
								<tr>
						  			<td class="label">
										<span class="nw"><%= userView.getFieldLabel(TIMER_CLASS, "name", app.getCurrentLocaleAsIndex()) %>:</span>
									</td>
									<td>
										<input type="text" class="valueL lightUp" name="name" id="name" maxlength="50" tabindex="1200" value="<%= name.replaceAll("\"", "&quot;") %>" />
									</td>
									<td class="addon"></td>
								</tr>
								<tr>
									<td class="label">
										<span class="nw"><%= userView.getFieldLabel(TIMER_CLASS, "timerStartAt", app.getCurrentLocaleAsIndex()) %>:</span>
									</td>
									<td style="padding-top:2px;">
										<input type="text" class="valueL <%= triggerAtDateOk ? "lightUp" : "valueError" %>" name="triggerAt" id="triggerAt" maxlength="16" tabindex="1800" value="<%= triggerAt %>" />
									</td>
									<td class="addon">
										<a><img class="popUpButton" id="cal_trigger_triggerAt" border="0" alt="Click to open Calendar" src="images/cal.gif" /></a>
										<script language="javascript" type="text/javascript">
											  Calendar.setup({
												inputField   : "triggerAt",
												ifFormat	 : "%d-%m-%Y %H:%M",
												timeFormat   : "24",
												button	   : "cal_trigger_triggerAt",
												align		: "Tr",
												singleClick  : true,
												showsTime	: true
											  });
										</script>
									</td>
								</tr>
							</table>
							</div>
						</fieldset>

						<div id="waitMsg" style="display:none;">
							Processing request - please wait...<br>
							<img border="0" src='./images/progress_bar.gif' alt='please wait...' />
						</div>						
						<div id="submitButtons">
							<input type="submit" style="display:none;" name="Refresh" id="Refresh.Button" tabindex="9000" value="<%= app.getTexts().getReloadText() %>" onclick="javascript:$('Command').value=this.name;" />
							<input type="submit" name="OK" id="OK.Button" tabindex="9000" value="<%= app.getTexts().getOkTitle() %>" onclick="javascript:$('Command').value=this.name;this.name='--';$('waitMsg').style.display='block';$('submitButtons').style.visibility='hidden';" />
							<input type="submit" name="Cancel" tabindex="9010" value="<%= app.getTexts().getCancelTitle() %>" onclick="javascript:$('Command').value=this.name;" />
						</div>
					</td>
				</tr>
			</table>
		</form>
		<br>&nbsp;

		<script language="javascript" type="text/javascript">
	      	Event.observe('<%= FORM_NAME %>', 'submit', function(event) {
	      		$('<%= FORM_NAME %>').request({
	      			onFailure: function() { },
	      			onSuccess: function(t) {
	      				$('UserDialog').update(t.responseText);
	      			}
	      		});
	      		Event.stop(event);
	      	});
		</script>
<%
		p.close(false);
	}
	catch (Exception e) {
		new ServiceException(e).log();
	}
	finally {
		if(pm != null) {
			pm.close();
		}
	}
%>
