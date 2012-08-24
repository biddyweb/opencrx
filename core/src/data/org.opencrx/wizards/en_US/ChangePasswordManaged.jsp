<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: ChangePasswordManaged.jsp,v 1.5 2012/07/08 13:30:31 wfro Exp $
 * Description: Change Password
 * Revision:    $Revision: 1.5 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2012/07/08 13:30:31 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2012, CRIXP Corp., Switzerland
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
java.net.*,
java.text.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.reports.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*,
org.openmdx.kernel.log.*,
org.opencrx.kernel.backend.*,
org.opencrx.kernel.layer.application.*,
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
org.openmdx.base.exception.*
" %>
<%

	//-----------------------------------------------------------------------
	final String WIZARD_NAME = "ChangePasswordManaged.jsp";

	//-----------------------------------------------------------------------
	// Init
	request.setCharacterEncoding("UTF-8");
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
	String requestIdParam = Action.PARAMETER_REQUEST_ID + "=" + requestId;
	String xriParam = Action.PARAMETER_OBJECTXRI + "=" + URLEncoder.encode(objectXri);
	Texts_1_0 texts = app.getTexts();
	org.openmdx.portal.servlet.Codes codes = app.getCodes();
	org.opencrx.kernel.portal.wizard.ChangePasswordWizardExtension wizardExtension = 
		(org.opencrx.kernel.portal.wizard.ChangePasswordWizardExtension)app.getPortalExtension().getExtension(org.opencrx.kernel.portal.wizard.ChangePasswordWizardExtension.class.getName());
	// Get Parameters
	boolean actionSave = request.getParameter("Save.Button") != null;
	boolean actionCancel = request.getParameter("Cancel.Button") != null;
	String command = request.getParameter("command");
	boolean isFirstCall = request.getParameter("isFirstCall") == null; // used to properly initialize various options
	String pw_old  = request.getParameter("pw_old"); if(pw_old ==null){pw_old  = "";}
	String pw_new1 = request.getParameter("pw_new1");if(pw_new1==null){pw_new1 = "";}
	String pw_new2 = request.getParameter("pw_new2");if(pw_new2==null){pw_new2 = "";}
	boolean showPasswords = ((request.getParameter("showPasswords") != null) && (request.getParameter("showPasswords").length() > 0));
	
	RefObject_1_0 obj = (RefObject_1_0)pm.getObjectById(new Path(objectXri));
	// Get user home segment
	String providerName = obj.refGetPath().get(2);
	String segmentName = obj.refGetPath().get(4);

	// Exit
	if("exit".equalsIgnoreCase(command)) {
		session.setAttribute(WIZARD_NAME, null);
		Action nextAction = new ObjectReference(obj, app).getSelectObjectAction();
		response.sendRedirect(
			request.getContextPath() + "/" + nextAction.getEncodedHRef()
		);
		return;
	}

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html dir="<%= texts.getDir() %>">
<head>
	<style type="text/css" media="all">
		body{
		  font-family: "Open Sans", "DejaVu Sans Condensed", "lucida sans", tahoma, verdana, arial, sans-serif;
			padding: 0; margin:0;}
		h1{  margin: 0; padding: 0 1em; font-size: 150%;}
		h2{ font-size: 130%; margin: 0; text-align: center;}

		a{text-decoration: none;}
		img{border: none;}

		table tr td{vertical-align:top;}
		
		/* Main Navigation across the top */
		.nav{padding: 0; margin: 0 0 1em 0; }
		.nav li{display: inline; }
		.nav a{padding: 0 0.5em; border: 1px solid silver;}
		.nav a:hover,
		.nav a:focus{background-color: silver; border: 1px solid gray;}
		.nav.secondary {float: right;}

		#content{width: 80%; margin: 0 auto; font-size: 90%;}

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
    	width: 140px;
    	border: 1px solid silver;
    }

		/* Add/Edit page specific settings */
		.col1,
		.col2{float: left; width: 80%;}

		.buttons{clear: both; text-align: left;}
		table{border-collapse: collapse; width: 100%; clear: both;}
		tr{}

		/* List page specific settings */
		table.listview tr{
			border: 1px solid #36c;
			border-style: solid none;
		}
		table.listview tr:hover{
			background-color: #F0F0F0;
		}

		div.letterBar {
			padding: 0.2em 0;
			text-align: center;
		}
		div.letterBar a,
		div.letterBar a:link,
		div.letterBar a:visited{
			padding: 0em 0.3em;
			border: 1px solid gray;
			-moz-border-radius: 6px;
			margin: 0 2px;
		}
		div.letterBar a:hover,
		div.letterBar a:focus{
			background-color: yellow;
		}

		div.letterBar a.current {
			background-color: #F0F0F0;
		}
	</style>
	<title>openCRX - Change Password</title>
	<meta name="UNUSEDlabel" content="Change Password">
	<meta name="UNUSEDtoolTip" content="Change Password">
	<meta name="targetType" content="_self">
	<meta name="forClass" content="org:opencrx:kernel:home1:UserHome">
  <meta name="order" content="org:opencrx:kernel:home1:UserHome:changePasswordManaged">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link href="../../_style/n2default.css" rel="stylesheet" type="text/css">
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
    	<div id="content" style="padding:100px 0.5em 0px 0.5em;">
<%
			String errorMsg = "";
			String resultText = null;

			org.opencrx.kernel.home1.jmi1.UserHome userHome =  (org.opencrx.kernel.home1.jmi1.UserHome)pm.getObjectById(new Path(objectXri));
			String principalName = ((new Path(userHome.refMofId())).getLastComponent()).toString();

			// Apply
			if("apply".equalsIgnoreCase(command)) {
				try {
					wizardExtension.validatePassword(pw_new1, principalName, app);
					try {
						org.opencrx.kernel.home1.jmi1.ChangePasswordParams params = org.opencrx.kernel.utils.Utils.getHomePackage(pm).createChangePasswordParams(
							pw_new1, // new password
							pw_new2, // new password again
							pw_old   // old password
						);
						pm.currentTransaction().begin();
						org.opencrx.kernel.home1.jmi1.ChangePasswordResult result = userHome.changePassword(params);
						pm.currentTransaction().commit();
						short resultCode = result.getStatus();
						try {
							resultText = "[" + resultCode + "]: " + (String)(codes.getLongText("org:opencrx:kernel:home1:ChangePasswordResult:status", app.getCurrentLocaleAsIndex(), true, true).get(new Short(resultCode)));
						} catch (Exception e) {
							errorMsg = "unknown error";
						}
						if (resultCode > 0) {
							errorMsg = resultText;
							resultText = null;
							if (errorMsg == null || errorMsg.length() == 0) {
								errorMsg = "unknown error";
							}
						}
					}
					catch(Exception e) {
						try {
							pm.currentTransaction().rollback();
						} catch(Exception e0) {}
						new ServiceException(e).log();
					}
				}
				catch(Exception e) {
					errorMsg = e.getMessage();
				}
			}
%>
		<ul class="nav">
		</ul>
		<form method="post" action="<%= WIZARD_NAME %>">
			<input type="hidden" name="command" value="apply"/>
			<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= objectXri %>"/>
			<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= requestId %>" />
			<input type="checkbox" style="display:none;" id="isFirstCall" name="isFirstCall" checked="true" />
			<div class="col1">
				<fieldset>
					<legend>Change Password of principal <b><%= principalName %></b></legend><br>
					<table>
						<tr>
							<td class="label"><label for="pw_old">Password <b>old</b>:&nbsp;</label><br>&nbsp;</td>
							<td><input type="<%= showPasswords ? "text" : "password" %>" id="pw_old" name="pw_old"  value="<%= pw_old == null ? "" :  pw_old %>"/></td>
						</tr>
						<tr>
							<td class="label"><label for="pw_new1">Password <b>new</b>:&nbsp;</label></td>
							<td><input type="<%= showPasswords ? "text" : "password" %>" id="pw_new1" name="pw_new1"  value="<%= pw_new1 == null ? "" :  pw_new1 %>"/></td>
						</tr>
						<tr>
							<td class="label"><label for="pw_new2">Password <b>new</b> (again):&nbsp;</label><br>&nbsp;</td>
							<td><input type="<%= showPasswords ? "text" : "password" %>" id="pw_new2" name="pw_new2"  value="<%= pw_new2 == null ? "" :  pw_new2 %>"/></td>
						</tr>
						<tr>
							<td class="label"><label for="showPasswords">Show password:&nbsp;</label><br>&nbsp;</td>
							<td><input type="checkbox" name="showPasswords" id="showPasswords" <%= showPasswords ? "checked" : ""	%> onclick="javascript:
								str = 'text';
								if(document.getElementById('pw_old').type==str){str='password';};
								document.getElementById('pw_old').type=str;
								document.getElementById('pw_new1').type=str;
								document.getElementById('pw_new2').type=str;" />
							</td>
						</tr>
					</table>
<%
					if (errorMsg.length() > 0) {
%>
						<div style="background-color:red;color:white;border:1px solid black;padding:10px;font-weight:bold;margin-top:10px;">
								<%= errorMsg %>
						</div>
<%
					}
%>
<%
					if (resultText != null && resultText.length() > 0) {
%>
						<div style="background-color:#CEFFBB;border:1px solid black;padding:10px;font-weight:bold;margin-top:10px;">
								<%= resultText %>
						</div>
<%
					}
%>
				</fieldset>
			</div><br>
			<div class="buttons">
				<input type="submit" value="<%= app.getTexts().getSaveTitle() %>" />
				<input type="button" value="<%= app.getTexts().getCloseText() %>" onclick="javascript:location.href='<%= WIZARD_NAME + "?" + requestIdParam + "&" + xriParam + "&command=exit" %>';" />
			</div>
		</form>

            </div> <!-- content -->
          </div> <!-- content-wrap -->
        </div> <!-- wrap -->
      </div> <!-- container -->
  	</body>
  	</html>
<%
    if(pm != null) {
    	pm.close();
    }
%>
