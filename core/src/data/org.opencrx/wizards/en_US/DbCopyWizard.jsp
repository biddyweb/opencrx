<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: DbCopyWizard
 * Owner:       CRIXP Corp., Switzerland, http://www.crixp.com
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
<%@ page session="true" import="
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
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*
"%>
<%
	final String FORM_NAME = "DbCopyForm";
	DbCopyWizardController wc = new DbCopyWizardController();
%>
	<t:wizardHandleCommand controller='<%= wc %>' defaultCommand='Refresh' />
<%
	if(response.getStatus() != HttpServletResponse.SC_OK) {
		wc.close();		
		return;
	}
	ApplicationContext app = wc.getApp();
	RefObject_1_0 obj = wc.getObject();
	Texts_1_0 texts = wc.getTexts();
	boolean isRefreshReport = "RefreshReport".equals(wc.getCommand());
%>
<!--
	<meta name="label" content="Database Copy wizard">
	<meta name="toolTip" content="Database Copy wizard">
	<meta name="targetType" content="_inplace">
	<meta name="forClass" content="org:opencrx:kernel:admin1:Segment">
	<meta name="order" content="9999">
-->
<br />
<div class="OperationDialogTitle"><%= wc.getToolTip() %></div>
<form id="<%= FORM_NAME %>" name="<%= FORM_NAME %>" accept-charset="UTF-8" method="POST" action="<%= wc.getServletPath() %>">
	<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= wc.getRequestId() %>" />
	<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= wc.getObjectIdentity().toXRI() %>" />
	<input type="hidden" id="Command" name="Command" value="" />
	<table class="tableLayout">
		<tr>
			<td class="cellObject">
				<div id="contentArea">
					<div id="SubmitArea" style="display:<%= "Copy".equals(wc.getCommand()) || isRefreshReport || Boolean.TRUE.equals(wc.isRunning()) ? "none" : "block" %>">
						<h1>WARNING: all data in the TARGET database will be LOST.</h1>										
						<table>
							<tr>
								<td colspan="2">
									<h1>Source database:</h1>
								</td>
							</tr>
							<tr>
								<td>URL:</td>
								<td>
									<input type="text" name="jdbcUrlSource" id="connectionUrl" tabIndex="9000" style="width:50em;" value="<%= wc.getFormFields().getJdbcUrlSource() == null ? "" : wc.getFormFields().getJdbcUrlSource() %>" />
<pre>Examples:
* jdbc:postgresql://127.0.0.1/CRX
* jdbc:mysql://127.0.0.1/CRX
* jdbc:hsqldb:hsql://127.0.0.1/CRX
* jdbc:db2://127.0.0.1:50000/CRX
* jdbc:oracle:thin:@127.0.0.1:1521:XE
* jdbc:sqlserver://127.0.0.1:1433;databaseName=CRX;selectMethod=cursor</pre>
								</td>
							</tr>
							<tr>
								<td>User:</td>
								<td><input type="text" name="usernameSource" id="usernameSource" tabIndex="9001" style="width:20em;" value="<%= wc.getFormFields().getUsernameSource() == null ? "" : wc.getFormFields().getUsernameSource() %>" /></td>
							</tr>
							<tr>
								<td>Password:</td>
								<td><input type="password" name="passwordSource" id="passwordSource" tabIndex="9002" style="width:20em;" value="<%= wc.getFormFields().getPasswordSource() == null ? "" : wc.getFormFields().getPasswordSource() %>" /></td>
							</tr>
							<tr>
								<td colspan="2">
									<h1>Target database:</h1>
								</td>
							</tr>
							<tr>
								<td>URL:</td>
								<td><input type="text" name="jdbcUrlTarget" id="jdbcUrlTarget" tabIndex="9010" style="width:50em;" value="<%=  wc.getFormFields().getJdbcUrlTarget() == null ? "" : wc.getFormFields().getJdbcUrlTarget() %>" /></td>
							</tr>
							<tr>						
								<td>User:</td>
								<td><input type="text" name="usernameTarget" id="usernameTarget" tabIndex="9011" style="width:20em;" value="<%= wc.getFormFields().getUsernameTarget() == null ? "" :  wc.getFormFields().getUsernameTarget() %>" /></td>
							</tr>
							<tr>
								<td>Password:</td>
								<td><input type="password" name="passwordTarget" id="passwordTarget" tabIndex="9012" style="width:20em;" value="<%= wc.getFormFields().getPasswordTarget() == null ? "" :  wc.getFormFields().getPasswordTarget() %>" /></td>
							</tr>
							<tr>
								<td colspan="2">
									<h1>Options:</h1>
								</td>
							</tr>
							<tr>
								<td>Start from (OOCKE1) [Default=0]:</td>
								<td><input type="text" name="kernelStartFromDbObject" id="kernelStartFromDbObject" tabIndex="9020" style="width:5em;" value="<%=  wc.getFormFields().getKernelStartFromDbObject() == null ? "" :  wc.getFormFields().getKernelStartFromDbObject() %>" /></td>
							</tr>
							<tr>
								<td>End with (OOCKE1) [Default=9999]:</td>
								<td><input type="text" name="kernelEndWithDbObject" id="kernelEndWithDbObject" tabIndex="9021" style="width:5em;" value="<%=  wc.getFormFields().getKernelEndWithDbObject() == null ? "" :  wc.getFormFields().getKernelEndWithDbObject() %>" /></td>
							</tr>
							<tr>
								<td>Start from (OOMSE2) [Default=0]:</td>
								<td><input type="text" name="securityStartFromDbObject" id="securityStartFromDbObject" tabIndex="9022" style="width:5em;" value="<%=  wc.getFormFields().getSecurityStartFromDbObject() == null ? "" :  wc.getFormFields().getSecurityStartFromDbObject() %>" /></td>
							</tr>
							<tr>
								<td>End with (OOMSE2) [Default=9999]:</td>
								<td><input type="text" name="securityEndWithDbObject" id="securityEndWithDbObject" tabIndex="9023" style="width:5em;" value="<%=  wc.getFormFields().getSecurityEndWithDbObject() == null ? "" :  wc.getFormFields().getSecurityEndWithDbObject() %>" /></td>
							</tr>
						</table>
						<div style="float:left;">															
							<input type="submit" name="Copy" id="Copy.Button" tabindex="9030" value="Copy" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none';setTimeout('javascript:$(\'RefreshReportButton\').click();',2000);$('Command').value=this.name;" />
							<input type="submit" name="Cancel" tabindex="9031" value="<%= app.getTexts().getCancelTitle() %>" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none';$('Command').value=this.name;" />
						</div>
					</div>
					<div id="WaitIndicator" style="display:<%= isRefreshReport ? "block" : "none" %>" class="<%= Boolean.FALSE.equals(wc.isRunning()) ? "" : "wait" %>">
						<br />
<%
						if(Boolean.TRUE.equals(wc.isRunning())) {
%>							
							<div>
								<br />
								<b>HINT: </b>Scroll to bottom to see progress...
							</div>	
<%											
						}
%>						
						<div id="SubmitArea2">
							<input type="submit" id="RefreshReportButton" name="RefreshReport" style="display:none;" tabindex="9032" value="<%= wc.getTexts().getReloadText() %>" onclick="javascript:$('Command').value=this.name;" />
<%
							if(Boolean.FALSE.equals(wc.isRunning())) {
%>						
								<input type="submit" name="Clear" tabindex="9033" value="Clear" onclick="javascript:$('Command').value=this.name;" />
								<br />	
<%
							}
%>
						</div>
<%
						if(Boolean.TRUE.equals(wc.isRunning())) {
%>						
							<script type="text/javascript">
								setTimeout('javascript:$(\'RefreshReportButton\').click();',3000);						
							</script>
<%
						}
						if(wc.getProgressMeter() != null && wc.getProgressMeter().getReport() != null) {
%>
							<div>						
								<pre><%= new Date() %>
<%= wc.getProgressMeter().getReport().toString("UTF-8") %></pre>
							</div>
<%
						}
%>						
					</div>
				</div>
			</td>
		</tr>
	</table>
</form>
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
</script>
<t:wizardClose controller="<%= wc %>" />
