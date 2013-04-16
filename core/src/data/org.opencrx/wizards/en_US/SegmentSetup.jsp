<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: SegmentSetup
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
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*
" %>
<%
	final String WIZARD_NAME = "SegmentSetup.jsp";
	SegmentSetupController wc = new SegmentSetupController();
%>
	<t:wizardHandleCommand controller='<%= wc %>' defaultCommand='Refresh' />
<%
	if(response.getStatus() != HttpServletResponse.SC_OK) {
		wc.close();
		return;
	}
	org.openmdx.portal.servlet.ApplicationContext app = wc.getApp();
	javax.jdo.PersistenceManager pm = wc.getPm();
	String requestIdParam = Action.PARAMETER_REQUEST_ID + "=" + wc.getRequestId();
	String xriParam = Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(wc.getObjectIdentity().toXRI(), "UTF-8");
%>
<head>
	<style type="text/css" media="all">
		body{
		  font-family: "Open Sans", "DejaVu Sans Condensed", "lucida sans", tahoma, verdana, arial, sans-serif;
			padding: 0; margin:0;}
		h1{ margin: 0.5em 0em; font-size: 150%;}
		h2{ font-size: 130%; margin: 0.5em 0em; text-align: left;}
    textarea,
    input[type='text'],
    input[type='password']{
    	width: 100%;
    	margin: 0; border: 1px solid silver;
    	padding: 0;
    	font-size: 100%;
		  font-family: "Open Sans", "DejaVu Sans Condensed", "lucida sans", tahoma, verdana, arial, sans-serif;
    }
    input.button{
    	-moz-border-radius: 4px;
    	-webkit-border-radius: 4px;
    	width: 120px;
    	border: 1px solid silver;
    }
		.col1,
		.col2{float: left; width: 49.5%;}
	</style>
	<title>openCRX - Segment Setup Wizard</title>
	<meta name="label" content="Segment Setup">
	<meta name="toolTip" content="Segment Setup">
	<meta name="targetType" content="_self">
	<meta name="forClass" content="org:opencrx:kernel:home1:UserHome">
	<meta name="order" content="2100">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link href="../../_style/n2default.css" rel="stylesheet" type="text/css">
	<link rel='shortcut icon' href='../../images/favicon.ico' />
	<script type="text/javascript" src="../../javascript/portal-all.js"></script>	
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
    	<div id="content" style="padding:10px 0.5em 0px 0.5em;">
			<form method="post" action="<%= WIZARD_NAME %>">
				<% wc.renderSetupReport(out); %>
				<br />
				<div class="buttons">
					<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= wc.getRequestId() %>" />
					<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= wc.getObjectIdentity().toXRI() %>" />
					<input type="hidden" id="Command" name="Command" value="" /> 
<%
					if(wc.isCurrentUserIsAdmin()) {
%>
						<input type="Submit" name="Setup" value="<%= app.getTexts().getSaveTitle() %>" onclick="javascript:$('Command').value=this.name;" />
<%
					}
%>
					<input type="Submit" name="Cancel" value="<%= app.getTexts().getCloseText() %>" onclick="javascript:$('Command').value=this.name;" />
					<%= wc.isCurrentUserIsAdmin() ? "" : "<h2>This wizard requires admin permissions.</h2>" %>
					<br />
				</div>
			</form>
			<br />
      </div> <!-- content -->
    </div> <!-- content-wrap -->
  </div> <!-- wrap -->
</div> <!-- container -->
</body>
</html>
<t:wizardClose controller="<%= wc %>" />
