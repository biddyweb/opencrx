<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.openmdx.org/
 * Name:        $Id: UploadMedia.jsp,v 1.14 2008/02/25 10:14:26 cmu Exp $
 * Description: UploadMedia
 * Revision:    $Revision: 1.14 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/02/25 10:14:26 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2004-2006, OMEX AG, Switzerland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * * Neither the name of the openMDX team nor the names of its
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
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
 * This product includes software developed by Mihai Bazon
 * (http://dynarch.com/mishoo/calendar.epl) published with an LGPL
 * license.
 */
%><%@ page session="true" import="
java.util.*,
java.io.*,
java.text.*,
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.base.exception.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.texts.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.reports.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.compatibility.base.naming.*,
org.openmdx.compatibility.base.dataprovider.cci.*,
org.openmdx.application.log.*,
org.openmdx.kernel.exception.BasicException,
org.openmdx.uses.org.apache.commons.fileupload.*,
org.openmdx.kernel.id.*
" %><%
  request.setCharacterEncoding("UTF-8");
  ApplicationContext app = (ApplicationContext)session.getValue("ObjectInspectorServlet.ApplicationContext");
  ShowObjectView view = (ShowObjectView)session.getValue("ObjectInspectorServlet.View");
  Texts_1_0 texts = app.getTexts();
  UUIDGenerator uuids = UUIDs.getGenerator();

%>
<!--[if IE]><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><![endif]-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html dir="<%= texts.getDir() %>">
<head>
  <title><%= app.getApplicationName() + " - " + view.getObjectReference().getTitle() + (view.getObjectReference().getTitle().length() == 0 ? "" : " - ") + view.getObjectReference().getLabel() %></title>
  <meta name="UNUSEDlabel" content="Upload Media">
  <meta name="UNUSEDtoolTip" content="Upload Media">
  <meta name="targetType" content="_self">
  <meta name="forClass" content="org:opencrx:kernel:generic:CrxObject">
  <meta name="order" content="org:opencrx:kernel:generic:CrxObject:uploadMedia">
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <link href="../../_style/colors.css" rel="stylesheet" type="text/css">
  <link href="../../_style/n2default.css" rel="stylesheet" type="text/css">
  <link rel='shortcut icon' href='../../images/favicon.ico' />
</head>

<body>
  <%@ include file="../../show-header.html" %>
  <div id="header" style="padding:10px 0px 10px 0px;">
    <table id="headerlayout" style="position:relative;">
      <tr id="headRow">
        <td id="head" colspan="2">
          <table id="info">
            <tr>
              <td id="headerCellLeft"><img id="logoLeft" src="../../images/logoLeft.gif" alt="openCRX - limitless relationship management" title="" /></td>
              <td id="headerCellMiddle"></td>
              <td id="headerCellRight"><img id="logoRight" src="../../images/logoRight.gif" alt="" title="" /></td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </div>

<%
		final String UPLOAD_FILE_FIELD_NAME = "uploadFile";
		try {

			Map parameterMap = request.getParameterMap();
	    	if(FileUpload.isMultipartContent(request)) {
				parameterMap = new HashMap();
				DiskFileUpload upload = new DiskFileUpload();
				upload.setHeaderEncoding("UTF-8");
				try {
					List items = upload.parseRequest(
					  request,
					  100000, // request size threshold
					  4000000, // max request size
					  app.getTempDirectory().getPath()
					);
					for(Iterator i = items.iterator(); i.hasNext(); ) {
					  FileItem item = (FileItem)i.next();
					  if(item.isFormField()) {
						parameterMap.put(
						  item.getFieldName(),
						  new String[]{item.getString("UTF-8")}
						);
					  }
					  else {
						// reset binary
						if("#NULL".equals(item.getName())) {
						  parameterMap.put(
							item.getFieldName(),
							new String[]{item.getName()}
						  );
						}
						// add to parameter map if file received
						else if(item.getSize() > 0) {
						  parameterMap.put(
							item.getFieldName(),
							new String[]{item.getName()}
						  );
						  String location = app.getTempFileName(item.getFieldName(), "");

						  // bytes
						  File outFile = new File(location);
						  item.write(outFile);

						  // type
						  PrintWriter pw = new PrintWriter(
							new FileOutputStream(location + ".INFO")
						  );
						  pw.println(item.getContentType());
						  int sep = item.getName().lastIndexOf("/");
						  if(sep < 0) {
							sep = item.getName().lastIndexOf("\\");
						  }
						  pw.println(item.getName().substring(sep + 1));
						  pw.close();
						}
					  }
					}
				}
				catch(FileUploadException e) {
					AppLog.warning("can not upload file", e.getMessage());
				}
			}

			boolean actionOk = parameterMap.get("OK.Button") != null;
			boolean actionCancel = parameterMap.get("Cancel.Button") != null;

			String[] descriptions = (String[])parameterMap.get("description");
			String description = (descriptions == null) || (descriptions.length == 0) ? "" : descriptions[0];

			String[] objectXris = (String[])parameterMap.get("xri");
			String objectXri = (objectXris == null) || (objectXris.length == 0) ? "" : objectXris[0];
			System.out.println("XRI=" + objectXri);
			String location = app.getTempFileName(UPLOAD_FILE_FIELD_NAME, "");

			if(actionCancel || (objectXri == null)) {
				Action nextAction = view.getObjectReference().getSelectObjectAction();
				response.sendRedirect(
					request.getContextPath() + "/" + view.getEncodedHRef(nextAction)
				);
			}
			else if(actionOk) {
				// Get data package. This is the JMI root package to handle
				// openCRX object requests
				RefPackage_1_0 dataPkg = app.getDataPackage();

				if(
					new File(location + ".INFO").exists() &&
					new File(location).exists() &&
					(new File(location).length() > 0)
				) {
					 // mimeType and name
					 BufferedReader r = new BufferedReader(
					   new FileReader(location + ".INFO")
					 );
					 String contentMimeType = r.readLine();
					 String contentName = r.readLine();
					 r.close();
					new File(location + ".INFO").delete();

					if(
						(contentName != null) &&
						(contentName.length() > 0) &&
						(contentMimeType != null) &&
						(contentMimeType.length() > 0)
					) {
						try {
							dataPkg.refBegin();
							org.opencrx.kernel.generic.jmi1.GenericPackage genericPkg =
								(org.opencrx.kernel.generic.jmi1.GenericPackage)dataPkg.refPackage(
									org.opencrx.kernel.generic.jmi1.GenericPackage.class.getName()
								);
							org.opencrx.kernel.generic.jmi1.CrxObject crxObject =
								(org.opencrx.kernel.generic.jmi1.CrxObject)dataPkg.refObject(objectXri);

							// Add media to crx object
							org.opencrx.kernel.generic.jmi1.Media media = genericPkg.getMedia().createMedia();
							media.setDescription(description.length() > 0 ? description : contentName);
							media.setContentName(contentName);
							media.setContentMimeType(contentMimeType);
							media.setContent(
								org.w3c.cci2.BinaryLargeObjects.valueOf(new File(location))
							);
							crxObject.addMedia(
								false,
								uuids.next().toString(),
								media
							);
							dataPkg.refCommit();

							new File(location).delete();

							// Go to created document
							Action nextAction =
							new Action(
								Action.EVENT_SELECT_OBJECT,
								new Action.Parameter[]{
									new Action.Parameter(Action.PARAMETER_OBJECTXRI, crxObject.refMofId())
								},
								"",
								true
							);
							response.sendRedirect(
								request.getContextPath() + "/" + view.getEncodedHRef(nextAction)
							);
						}
						catch(Exception e) {
							try {
								dataPkg.refRollback();
							} catch(Exception e0) {}
						}
					}
				}
			}
			else {
				File uploadFile = new File(location);
				System.out.println("UploadMedia: file " + location + " either does not exist or has size 0: exists=" + uploadFile.exists() + "; length=" + uploadFile.length());
			}
%>
<form name="UploadMedia" enctype="multipart/form-data" accept-charset="UTF-8" method="POST" action="UploadMedia.jsp">
<input type="hidden" class="valueL" name="xri" value="<%= objectXri %>" />
<table cellspacing="8" class="tableLayout">
  <tr>
    <td class="cellObject">
      <noscript>
        <div class="panelJSWarning" style="display: block;">
          <a href="../../helpJsCookie.html" target="_blank"><img class="popUpButton" src="../../images/help.gif" width="16" height="16" border="0" onclick="javascript:void(window.open('helpJsCookie.html', 'Help', 'fullscreen=no,toolbar=no,status=no,menubar=no,scrollbars=yes,resizable=yes,directories=no,location=no,width=400'));" alt="" /></a> <%= texts.getPageRequiresScriptText() %>
        </div>
      </noscript>
      <table class="objectTitle">
        <tr>
          <td>
            <div style="padding-left:5px; padding-bottom: 3px;">
              Upload a media file
            </div>
          </td>
        </tr>
      </table>
      <br />

      <div class="panel" id="panelObj0" style="display: block">
       <div class="fieldGroupName">&nbsp;</div>
	      <table class="fieldGroup">
	        <tr>
	          <td class="label"><span class="nw">Description <font color="red">*</font></span></td>
	          <td>
	            <input type="text" class="valueL" name="description" maxlength="50" tabindex="100" value="<%= description %>" />
	          </td>
	          <td class="addon"></td>
	          <td class="label"></td>
	          <td></td>
	          <td class="addon"></td>
	        </tr>
    			<tr>
    				<td class="label"><span class="nw">File:</span></td>
    				<td >
    					<input type="file" class="valueL" name="<%= UPLOAD_FILE_FIELD_NAME %>" tabindex="500" />
    				</td>
    				<td class="addon" >
    			</tr>
    			<tr>
	          <td class="label">
	          	<INPUT type="Submit" name="OK.Button" tabindex="1000" value="Upload" />
      			<INPUT type="Submit" name="Cancel.Button" tabindex="1010" value="Cancel" />
	          </td>
	          <td>&nbsp;</td>
	          <td class="addon"></td>
	          <td></td>
	          <td></td>
	          <td></td>
	        </tr>
	      </table>
      </div>
  	</td>
  </tr>
</table>
</form>
<%
    }
    catch (Exception ex) {
   		Action nextAction = view.getObjectReference().getSelectObjectAction();
%>
      <br />
      <br />
      <span style="color:red;"><b><u>Warning:</u> cannot create media attachment (no permission?)</b></span>
      <br />
      <br />
      <INPUT type="Submit" name="Continue.Button" tabindex="1" value="Continue" onClick="javascript:location='<%= request.getContextPath() + "/" + view.getEncodedHRef(nextAction) %>';" />
      <br />
      <br />
      <hr>
<%
	    ServiceException e0 = new ServiceException(ex);
	    out.println("<p><b>The following exception occurred:</b><br><br><pre>");
      PrintWriter pw = new PrintWriter(out);
      pw.println(e0.getMessage());
      pw.println(e0.getCause());
      out.println("</pre></p>");
    }
%>
  <%@ include file="../../show-footer.html" %>
</body>
</html>
