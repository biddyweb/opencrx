<%@  page contentType="text/html" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:	 openCRX/Core, http://www.opencrx.org/
 * Name:		$Id: ValidateCaptcha.jsp,v 1.1 2009/11/21 00:11:32 wfro Exp $
 * Description: ValidateCaptcha
 * Revision:	$Revision: 1.1 $
 * Owner:	   CRIXP Corp., Switzerland, http://www.crixp.com
 * Date:		$Date: 2009/11/21 00:11:32 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2008-2009, CRIXP Corp., Switzerland
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
javax.naming.Context,
javax.servlet.http.*,
com.octo.captcha.service.*,
com.octo.captcha.service.image.*,
com.sun.image.codec.jpeg.*,
java.awt.image.*,
java.io.*,
java.util.*,
java.text.*"
%>
<%!

	public static boolean captchaIsValid(
		HttpSession session,
		HttpServletRequest request
	) {
		boolean captchaIsValid = false;
    	// remember that we need an id to validate!
    	String captchaId = session.getId();
    	// retrieve the response
    	String captacheResponse = request.getParameter("j_captcha_response");
    	// Call the Service method
     	try {
     		captchaIsValid = org.opencrx.application.captcha.CaptchaService.getInstance().validateResponseForID(
         		captchaId,
                captacheResponse
            );
     	} 
    	catch (CaptchaServiceException e) {
          // should not happen, may be thrown if the id is not valid
     	}
    	return captchaIsValid; 
	}

%>
<%
	final String WIZARD_NAME = "ValidateCaptcha.jsp";
	final String COMMAND_VALIDATE = "validate";
	
	request.setCharacterEncoding("UTF-8");
	String servletPath = request.getServletPath();
	String contextPath = request.getContextPath();
	String command = request.getParameter("command");
	boolean isValidate = COMMAND_VALIDATE.equals(command);
		
	Boolean captchaIsValid = null;
	if(isValidate) {
		captchaIsValid = captchaIsValid(session, request);
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
</head>
<body>
	<form id="<%= WIZARD_NAME %>" name="<%= WIZARD_NAME %>" method="post" accept-charset="UTF-8" action="<%= contextPath + servletPath %>">
		<input type="hidden" id="command" name="command" value="<%= COMMAND_VALIDATE %>" />
		<img src="<%= contextPath %>/GetCaptchaServlet" />
		<input type="text" id="j_captcha_response" name="j_captcha_response" value="" />
		<input type="submit" id="doValidate" name="doValidate" value="Validate" />
		<%= captchaIsValid == null ? "" : (captchaIsValid.booleanValue() ? "Validation OK" : "Validation FAILED") %>
	</form>
</body>
</html>
