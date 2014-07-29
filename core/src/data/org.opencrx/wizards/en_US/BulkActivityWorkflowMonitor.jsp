<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
/*
 * ====================================================================
 * Project:		openCRX/Core, http://www.opencrx.org/
 * Description: Bulk Activity Workflow Monitor
 * Owner:		CRIXP AG, Switzerland, http://www.crixp.com
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
<%
	final String FORM_NAME = "BulkActivityWorkflowMonitor";
	final String WIZARD_NAME = "BulkActivityWorkflowMonitor.jsp";
	
	BulkActivityWorkflowMonitorController wc = new BulkActivityWorkflowMonitorController();
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
	int tabIndex = 1;
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><%=app.getApplicationName()%> - Bulk Activity Workflow Monitor</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta name="label" content="Bulk Activity Workflow Monitor">
	<meta name="toolTip" content="Bulk Activity Workflow Monitor">
	<meta name="targetType" content="_blank">
	<meta name="forClass" content="org:opencrx:kernel:activity1:ActivityTracker">
	<meta name="order" content="100">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" href="../../javascript/bootstrap/css/bootstrap.min.css">	
	<link rel="stylesheet" href="../../_style/colors.css">
	<link rel="stylesheet" href="../../_style/n2default.css">
	<link rel="stylesheet" href="../../_style/ssf.css">
	<script language="javascript" type="text/javascript" src="../../javascript/prototype.js"></script>
	<link rel='shortcut icon' href='../../images/favicon.ico' />

	<style type="text/css" media="all">
		.processTable {
			border-collapse: collapse;
			border-spacing:0;
		}
		.processTable td {
			vertical-align:top;
			white-space: nowrap;
		}
		.processTable TR.header TD {
			background-color:black;
			color: white;
			font-weight: bold;
			vertical-align:middle;
			white-space:nowrap;
			padding:5px;
		}
		.processTable TR.process TD {
			background-color:#DDDDDD;
			border-top:1px solid black;
			vertical-align:middle;
			font-weight: bold;
			white-space:nowrap;
			padding:5px;
		}
		.processTable TR.log {
			background-color:white;
		}
		.processTable TD.log {
			background-color:#EEEEEE;
			vertical-align:middle;
			white-space:nowrap;
			padding:5px;
			overflow:hidden;
		}
		.processTable TR.child {
			background-color:white;
		}
		.processTable TD.child {
			background-color:#EEEEEE;
			vertical-align:middle;
			white-space:nowrap;
			padding:5px;
			overflow:hidden;
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
       <div id="content" style="padding:0px 0.5em 0px 0.5em;">
        <form name="<%= FORM_NAME %>" accept-charset="UTF-8" method="POST" action="<%= WIZARD_NAME %>">
          <div style="background-color:#F4F4F4;border:1px solid #EBEBEB;padding:10px;margin-top:15px;">
			<input type="Submit" name="Refresh" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getReloadText() %>" />
			<input type="Submit" name="Close" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getCloseText() %>" onClick="javascript:window.close();" />
			<br />          
            <h1>Workflows for: <%= (new ObjectReference(obj, app)).getTitle() %> - <%= (new ObjectReference(obj, app)).getLabel() %></h1>
            <input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI%>" value="<%= wc.getObjectIdentity().toXRI() %>" />
            <input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID%>" value="<%= wc.getRequestId() %>" />
			<table class="processTable">
				<tr class="header">
				    <td />
					<td><%= wc.getFieldLabel(BulkActivityWorkflowMonitorController.WFPROCESSINSTANCE_CLASS, "name", app.getCurrentLocaleAsIndex()) %></td>
					<td><%= wc.getFieldLabel(BulkActivityWorkflowMonitorController.WFPROCESSINSTANCE_CLASS, "lastActivityOn", app.getCurrentLocaleAsIndex()) %></td>
					<td><%= wc.getFieldLabel(BulkActivityWorkflowMonitorController.WFPROCESSINSTANCE_CLASS, "startedOn", app.getCurrentLocaleAsIndex()) %></td>
					<td><%= wc.getFieldLabel(BulkActivityWorkflowMonitorController.WFPROCESSINSTANCE_CLASS, "createdAt", app.getCurrentLocaleAsIndex()) %></td>
					<td><%= wc.getFieldLabel(BulkActivityWorkflowMonitorController.WFPROCESSINSTANCE_CLASS, "process", app.getCurrentLocaleAsIndex()) %></td>
				</tr>
<%
				for(org.opencrx.kernel.home1.jmi1.WfProcessInstance wfProcessInstance: wc.getProcessInstances().values()) {
			    	try {
						Action action = new ObjectReference(
							wfProcessInstance,
							app
						).getSelectObjectAction();
						String wfProcessInstanceHref = "../../" + action.getEncodedHRef();
						String wfProcessHref = "";
						if (wfProcessInstance.getProcess() != null) {
							action = new ObjectReference(
									wfProcessInstance.getProcess(),
								app
							).getSelectObjectAction();
							wfProcessHref = "../../" + action.getEncodedHRef();
						}
						SimpleDateFormat dtf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", app.getCurrentLocale());
						dtf.setTimeZone(TimeZone.getTimeZone(app.getCurrentTimeZone()));							
						String processName = (wfProcessInstance.getProcess() != null ? wc.getLastElementOfName(wfProcessInstance.getProcess().getName()) : "?");
						BulkActivityWorkflowMonitorController.ProcessState processState = wc.getState(wfProcessInstance);
						String imageProcessState = null;
						switch(processState) {
							case NA:
								imageProcessState = null;
								break;
							case PENDING_NOTYETSTARTED:
								imageProcessState = "filter_all";
								break;
							case PENDING_STARTED:
								imageProcessState = "filter_pending";
								break;
							case COMPLETED_FAILURE:
								imageProcessState = "filter_notok";
								break;
							case COMPLETED_SUCCESS:
								imageProcessState = "filter_ok";
								break;
						}
%>
						<tr class="process">
						    <td width="40px" style="background-color:white;text-align:center;"><img src="../../images/<%= imageProcessState %>.gif"/></td>
							<td><a href="<%= wfProcessInstanceHref %>" target="_blank"><%= wfProcessInstance.getName() == null ? "N/A" : wfProcessInstance.getName() %></a></td>
							<td><a href="<%= wfProcessInstanceHref %>" target="_blank"><%= wfProcessInstance.getLastActivityOn() != null ? dtf.format(wfProcessInstance.getLastActivityOn()) : "" %></a></td>
							<td><a href="<%= wfProcessInstanceHref %>" target="_blank"><%= wfProcessInstance.getStartedOn() != null ? dtf.format(wfProcessInstance.getStartedOn()) : "" %></a></td>
							<td><a href="<%= wfProcessInstanceHref %>" target="_blank"><%= wfProcessInstance.getCreatedAt() != null ? dtf.format(wfProcessInstance.getCreatedAt()) : "" %></a></td>
							<td><a href="<%= wfProcessHref %>" target="_blank"><%= processName %></a></td>
						</tr>
<%
						try {
							org.opencrx.kernel.home1.cci2.WfActionLogEntryQuery wfActionLogEntryQuery = (org.opencrx.kernel.home1.cci2.WfActionLogEntryQuery)wc.getPm().newQuery(org.opencrx.kernel.home1.jmi1.WfActionLogEntry.class);
							wfActionLogEntryQuery.orderByModifiedAt().descending();								
							// ActionLogEntries
							int counter = (wc.getState(wfProcessInstance) == BulkActivityWorkflowMonitorController.ProcessState.COMPLETED_SUCCESS ? 1 : 3);
							for(org.opencrx.kernel.home1.jmi1.WfActionLogEntry entry: wfProcessInstance.getActionLog(wfActionLogEntryQuery)) {
%>
								<tr class="log">
									<td/>
									<td/>
									<td class="log"><%= dtf.format(entry.getCreatedAt()) %></td>
									<td class="log"><%= entry.getName() %></td>
									<td colspan="3" class="log"><%= entry.getDescription() %></td>
								</tr>
<%
								counter--;
								if(counter <= 0) break;
							}
						} catch(Exception e) {
				    		new ServiceException(e).log();
				    	}
						try {
							if (!wfProcessInstance.getChildProcessInstance().isEmpty()) {
								// child processes	
								int countPending = wc.getCountChildrenPending(wfProcessInstance);
								int countSuccess = wc.getCountChildrenSuccess(wfProcessInstance);
								int countFailed = wc.getCountChildrenFailed(wfProcessInstance);
%>
								<tr class="child">
									<td/>
									<td/>
									<td class="child">Child Processes</td>
									<td class="child"><%= countPending == 0 ? "Complete" : "in progress..." %></td>
									<td colspan="3" class="child">{Success: <%= countSuccess %>, Failed: <%= countFailed %>, Pending: <%= countPending %>, Total: <%= countSuccess+countFailed+countPending %>}</td>
								</tr>
<%
							}
						} catch(Exception e) {
							new ServiceException(e).log();
						}
			    	} catch(Exception e) {
			    		new ServiceException(e).log();
			    	}
		    	}
%>
			</table>
			<br />
			<input type="Submit" name="Refresh" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getReloadText() %>" />
			<input type="Submit" name="Close" class="<%= CssClass.btn.toString() %> <%= CssClass.btnDefault.toString() %>" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getCloseText() %>" onClick="javascript:window.close();" /><br>
		  </div>
        </form>
      </div> <!-- content -->
    </div> <!-- content-wrap -->
   </div> <!-- wrap -->
</div> <!-- container -->
</body>
</html>
<t:wizardClose controller="<%= wc %>" />
