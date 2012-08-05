<%/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: org.opencrx.kernel.activity1.ActivityTracker-ExportMsProject.jsp,v 1.9 2009/08/26 12:12:27 wfro Exp $
 * Description: openCRX MS Project Export
 * Revision:    $Revision: 1.9 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/08/26 12:12:27 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2006, CRIXP Corp., Switzerland
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
 */%>

<%@  page contentType= "text/html;charset=UTF-8" language="java" pageEncoding= "UTF-8" %>
<%@ page session="true" import="
java.util.*,
java.io.*,
java.text.*,
org.openmdx.kernel.id.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.texts.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.reports.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.base.naming.*,
org.openmdx.base.query.*,
org.apache.poi.hssf.usermodel.*,
org.apache.poi.hssf.util.*
" %>
<!--
<report>
	<property name="label">MS Project Export</property>
	<property name="description">MS Project Export</property>
</report>
-->
<head>
	  <meta name="forClass" content="org:opencrx:kernel:activity1:ActivityTracker"> 
	  <meta name="forClass" content="org:opencrx:kernel:activity1:ActivityMilestone"> 
	  <meta name="forClass" content="org:opencrx:kernel:activity1:ActivityCategory"> 
</head>
<%
  ApplicationContext app = (ApplicationContext)session.getValue("ObjectInspectorServlet.ApplicationContext");
  ShowObjectView view = (ShowObjectView)session.getValue("ObjectInspectorServlet.View");
  javax.jdo.PersistenceManager pm = view.getPersistenceManager();
  Texts_1_0 texts = app.getTexts();
  Codes codes = app.getCodes();

	// Get parameters
	int exportMax = request.getParameter("exportMax") == null
		? 100
		: new Integer(request.getParameter("exportMax")).intValue();
	
  // Generate report  
   String location = UUIDs.getGenerator().next().toString();
   File f = new File(
       app.getTempFileName(location, "")
   );
   FileOutputStream os = new FileOutputStream(f);
   
	// Generate MS Project export
	org.opencrx.kernel.activity1.jmi1.Activity1Package activityPkg = org.opencrx.kernel.utils.Utils.getActivityPackage(pm);
	org.opencrx.kernel.activity1.jmi1.ActivityGroup activityGroup = 
		(org.opencrx.kernel.activity1.jmi1.ActivityGroup)view.getObjectReference().getObject();

	org.opencrx.application.msproject.ProjectExporter exporter = 
		new org.opencrx.application.msproject.ProjectExporter(
			os,
			activityGroup,
			pm
		);
	exporter.setExportActivities(Boolean.TRUE);
	exporter.setExportResources(Boolean.TRUE);
	exporter.setExportAssignments(Boolean.TRUE);
    exporter.setMspHyperlinkRootAddress(
		request.getContextPath()
	);
	exporter.export();
	exporter.close();
   
   Action downloadAction = 
      new Action(
          Action.EVENT_DOWNLOAD_FROM_LOCATION,
          new Action.Parameter[]{
              new Action.Parameter(Action.PARAMETER_LOCATION, location),
              new Action.Parameter(Action.PARAMETER_NAME, "Activities.xml"),
              new Action.Parameter(Action.PARAMETER_MIME_TYPE, "test/xml")
          },
          app.getTexts().getClickToDownloadText() + " " + "Activities.xml",
          true
      );
	  response.sendRedirect(
		"../" + view.getEncodedHRef(downloadAction)
	  );
%>
