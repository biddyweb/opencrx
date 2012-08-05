<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: MailMerge.jsp,v 1.8 2008/07/05 11:25:57 cmu Exp $
 * Description: mail merge addresses of group's members --> RTF document
 * Revision:    $Revision: 1.8 $
 * Owner:       CRIXP Corp., Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/07/05 11:25:57 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2008, CRIXP Corp., Switzerland
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
com.itseasy.rtf.*,
com.itseasy.rtf.text.*,
java.util.*,
java.util.zip.*,
java.io.*,
java.text.*,
java.math.*,
java.net.*,
java.sql.*,
org.openmdx.base.accessor.generic.cci.Object_1_0,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.base.exception.*,
org.openmdx.kernel.id.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.texts.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.reports.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.compatibility.base.naming.*,
org.openmdx.compatibility.base.dataprovider.cci.*,
org.openmdx.compatibility.base.query.*,
org.openmdx.application.log.*
" %><%
	request.setCharacterEncoding("UTF-8");
	ApplicationContext app = (ApplicationContext)session.getValue("ObjectInspectorServlet.ApplicationContext");
	ViewsCache viewsCache = (ViewsCache)session.getValue(WebKeys.VIEW_CACHE_KEY_SHOW);
	String requestId =  request.getParameter(Action.PARAMETER_REQUEST_ID);
    String objectXri = request.getParameter(Action.PARAMETER_OBJECTXRI);
	if(objectXri == null || app == null) {
		response.sendRedirect(
			request.getContextPath() + "/" + WebKeys.SERVLET_NAME
		);
		return;
	}
	Texts_1_0 texts = app.getTexts();
	javax.jdo.PersistenceManager pm = app.getPmData();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
  <title>Mail Merge</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <meta name="UNUSEDlabel" content="Mail Merge --&gt; RTF">
  <meta name="UNUSEDtoolTip" content="Mail Merge --&gt; RTF">
  <meta name="targetType" content="_self">
  <meta name="forClass" content="org:opencrx:kernel:account1:Group">
  <meta name="forClass" content="org:opencrx:kernel:account1:Contact">
  <meta name="forClass" content="org:opencrx:kernel:account1:LegalEntity">
  <meta name="forClass" content="org:opencrx:kernel:account1:UnspecifiedAccount">
  <meta name="order" content="org:opencrx:kernel:account1:Group:mailMerge">
  <meta name="order" content="org:opencrx:kernel:account1:Contact:mailMerge">
  <meta name="order" content="org:opencrx:kernel:account1:LegalEntity:mailMerge">
  <meta name="order" content="org:opencrx:kernel:account1:UnspecifiedAccount:mailMerge">
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <link rel='shortcut icon' href='../../images/favicon.ico' />
</head>

<%
  final String featurePostalCountryCode = "org:opencrx:kernel:address1:PostalAddressable:postalCountry";
  final String documentName = "MailMerge";
  final String TEMPLATE_NAME = "MailMerge_Template.rtf";

	try {
    Codes codes = app.getCodes();

    // Timezone is reusable
    final TimeZone tz = TimeZone.getTimeZone(app.getCurrentTimeZone());
    // DateFormat is not multi-thread-safe!
    DateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    timestamp.setLenient(false);
    timestamp.setTimeZone(tz);

    NumberFormat formatter6 = new DecimalFormat("000000");

    // Get account1 package
    org.opencrx.kernel.account1.jmi1.Account1Package accountPkg = org.opencrx.kernel.utils.Utils.getAccountPackage(pm);

    RefObject_1_0 obj = (RefObject_1_0)pm.getObjectById(new Path(objectXri));

    Path objPath = new Path(obj.refMofId());
    String providerName = objPath.get(2);
    String segmentName = objPath.get(4);

    org.opencrx.kernel.account1.jmi1.Account account = null;
    String location = null;
    String filename = null;
    ZipOutputStream zipos = null;

    boolean createSingleFile = false;
    boolean zipMultilpleFiles = false;
    Iterator i = null;
    if (obj instanceof org.opencrx.kernel.account1.jmi1.Group) {
      i = ((org.opencrx.kernel.account1.jmi1.Group)obj).getMember().iterator();
      // prepare zip file to be sent to browser
      location = UUIDs.getGenerator().next().toString();
      zipos = new ZipOutputStream(new FileOutputStream(app.getTempFileName(location, "")));
      zipMultilpleFiles = true;
    }
    if (
        (obj instanceof org.opencrx.kernel.account1.jmi1.Contact) ||
        (obj instanceof org.opencrx.kernel.account1.jmi1.LegalEntity) ||
        (obj instanceof org.opencrx.kernel.account1.jmi1.UnspecifiedAccount)
    ) {
      createSingleFile = true;
    }

    int counter = 0;
    while (
      ((i != null) && i.hasNext()) ||
      createSingleFile
    ) {
			if (createSingleFile) {
        account = (org.opencrx.kernel.account1.jmi1.Account)obj;
		  } else {
  			org.opencrx.kernel.account1.jmi1.Member member =
  				(org.opencrx.kernel.account1.jmi1.Member)i.next();
        if ((member.isDisabled() != null) && (member.isDisabled().booleanValue())) {
          // skip disabled members
          continue;
        } else {
          account = member.getAccount();
        }
      }
      if (account != null) {
        counter += 1;

        // Generate document
        RTFTemplate document = new RTFTemplate(new File(getServletContext().getRealPath("/documents/" + TEMPLATE_NAME)));
        MultiTextParts mtpWarning = new MultiTextParts();
        mtpWarning.addText(TextPart.NEWLINE);

        // get postalAddress of Account (main account if any, otherwise any postalAddress)
        MultiTextParts mtpMailingAddress = new MultiTextParts();
        boolean needsNewLine = false;
        org.opencrx.kernel.account1.jmi1.PostalAddress mailingAddress = null;
        boolean searchingMainMailingAddress = true;
  			for(
  			    Iterator addr = account.getAddress().iterator();
  			    addr.hasNext() && searchingMainMailingAddress;
  			) {
  			  org.opencrx.kernel.account1.jmi1.AccountAddress address =
  			    (org.opencrx.kernel.account1.jmi1.AccountAddress)addr.next();
  				if (!(address instanceof org.opencrx.kernel.account1.jmi1.PostalAddress)) {continue;}

  				mailingAddress = (org.opencrx.kernel.account1.jmi1.PostalAddress)address;
  				searchingMainMailingAddress = !mailingAddress.isMain();
  		  }

  		  if (mailingAddress != null) {
  		    try {
            for(Iterator m = ((java.util.List)mailingAddress.getPostalAddressLine()).iterator(); m.hasNext();) {
              String lineToAdd = (m.next()).toString();
              if (lineToAdd != null) {
                lineToAdd = lineToAdd.trim();
                if (lineToAdd.length() > 0) {
                  if(needsNewLine) mtpMailingAddress.addText(TextPart.NEWLINE);
                  mtpMailingAddress.addText(new TextPart(lineToAdd));
                  needsNewLine = true;
                }
              }
            }
            for(Iterator m = ((java.util.List)mailingAddress.getPostalStreet()).iterator(); m.hasNext();) {
              String lineToAdd = (m.next()).toString();
              if (lineToAdd != null) {
                lineToAdd = lineToAdd.trim();
                if (lineToAdd.length() > 0) {
                  if(needsNewLine) mtpMailingAddress.addText(TextPart.NEWLINE);
                  mtpMailingAddress.addText(new TextPart(lineToAdd));
                  needsNewLine = true;
                }
              }
            }
            if(needsNewLine) mtpMailingAddress.addText(TextPart.NEWLINE);
            mtpMailingAddress.addText(new TextPart(
              (mailingAddress.getPostalCountry() == (short)0
                  ? ""
                  : (String)(codes.getShortText(featurePostalCountryCode, app.getCurrentLocaleAsIndex(), true, true).get(new Short(mailingAddress.getPostalCountry()))) + "-")
              + (mailingAddress.getPostalCode() == null
                  ? ""
                  : (mailingAddress.getPostalCode().length() > 0 ? mailingAddress.getPostalCode() + " " : ""))
              + (mailingAddress.getPostalCity() == null ? "" : mailingAddress.getPostalCity())
            ));
          } catch (Exception e) {}
  		  } else {
          mtpMailingAddress.addText(new TextPart("WARNING: no postal address available"));
          mtpMailingAddress.addText(TextPart.NEWLINE);
        }

        filename =
          providerName + "_"
          + segmentName + "_"
          + (formatter6.format(counter)) + "_"
          + ((account.getFullName() != null ? account.getFullName() : "---NoName") + ".rtf").replaceAll(" +", "_");

        // note: zip encode cannot handle file names with special chars
        filename = filename.replaceAll("ü", "u");
        filename = filename.replaceAll("Ü", "U");
        filename = filename.replaceAll("ä", "a");
        filename = filename.replaceAll("Ä", "A");
        filename = filename.replaceAll("ö", "o");
        filename = filename.replaceAll("Ö", "O");
        filename = filename.replaceAll("é", "e");
        filename = filename.replaceAll("è", "e");
        filename = filename.replaceAll("ê", "e");
        filename = filename.replaceAll("á", "a");
        filename = filename.replaceAll("à", "a");
        filename = filename.replaceAll("â", "a");
        filename = filename.replaceAll("ô", "o");
        filename = filename.replaceAll("&", "_");
        filename = URLEncoder.encode(filename, "UTF-8");

        // replace template bookmarks with actual values
        try {document.setBookmarkContent("filename",       filename);} catch (Exception e) {};
        try {document.setBookmarkContent("qualifier",      ((new Path(account.refMofId())).getLastComponent()).toString());} catch (Exception e) {};
        try {document.setBookmarkContent("fullName",       account.getFullName());} catch (Exception e) {};
        try {document.setBookmarkContent("counter",        formatter6.format(counter));} catch (Exception e) {};
        try {document.setBookmarkContent("timestamp",      timestamp.format(new java.util.Date()));} catch (Exception e) {};
        try {document.setBookmarkContent("mailingAddress", mtpMailingAddress);} catch (Exception e) {};


        if (createSingleFile) {
          // send document to browser
          location = UUIDs.getGenerator().next().toString();
          File f = new File(
             app.getTempFileName(location, "")
          );
          FileOutputStream os = new FileOutputStream(f);
          os.write(document.getDocument());
          os.flush();
          os.close();
          createSingleFile = false;
        } else {
          if (zipMultilpleFiles) {
      			// add document to zip file
      			try {
        			zipos.putNextEntry(new ZipEntry(filename));
              zipos.write(document.getDocument());
              zipos.closeEntry();
            } catch (Exception e){
              try {
                zipos.closeEntry();
              } catch (Exception ex) {}
              AppLog.error("Error adding to zip file", documentName);
              AppLog.error(e.getMessage(), e.getCause());
            }
          }
		  else {
			// save documents to generated directory
      		try{
				File f = new File(getServletContext().getRealPath("/documents/generated/" + filename));
				FileOutputStream os = new FileOutputStream(f);
				os.write(document.getDocument());
				os.flush();
				os.close();
			}
      		catch (Exception ex){
				AppLog.warning("Error creating rtf document", documentName);
				AppLog.warning(ex.getMessage(), ex.getCause());
				// Go back to previous view
				Action nextAction = new ObjectReference(
					(RefObject_1_0)pm.getObjectById(new Path(objectXri)),
					app
				).getSelectObjectAction();
				response.sendRedirect(
					request.getContextPath() + "/" + nextAction.getEncodedHRef()
				);
      		}
      	 }
    	}
      }
    }

%>
    <body>
<%
      if (location != null) {
        // determine user-agent because IE doesn't handle application/zip properly
        String userAgent = request.getHeader("User-Agent");
        String mimeType = "application/zip";
        if ((userAgent != null) && (userAgent.indexOf("IE") >=0)) {
          mimeType = "text/plain";
        }
        Action downloadAction = null;
        if (zipMultilpleFiles) {
          zipos.finish();
          zipos.close();
          downloadAction =
            new Action(
                Action.EVENT_DOWNLOAD_FROM_LOCATION,
                new Action.Parameter[]{
                    new Action.Parameter(Action.PARAMETER_LOCATION, location),
                    new Action.Parameter(Action.PARAMETER_NAME, documentName + ".zip"),
                    new Action.Parameter(Action.PARAMETER_MIME_TYPE, mimeType)
                },
                app.getTexts().getClickToDownloadText() + " " + documentName + ".zip",
                true
            );
        } else {
          downloadAction =
            new Action(
                Action.EVENT_DOWNLOAD_FROM_LOCATION,
                new Action.Parameter[]{
                    new Action.Parameter(Action.PARAMETER_LOCATION, location),
                    new Action.Parameter(Action.PARAMETER_NAME, filename),
                    new Action.Parameter(Action.PARAMETER_MIME_TYPE, "text/rtf")
                },
                app.getTexts().getClickToDownloadText() + " " + filename,
                true
            );
        }
%>
        <a href="../../<%= downloadAction.getEncodedHRef() %>">Download</a> Mail Merge Document.
<%
        response.sendRedirect(
           request.getContextPath() + "/" + downloadAction.getEncodedHRef(requestId)
        );
      } else {
%>
        Done
<%
        // Go back to previous view
        Action nextAction = new ObjectReference(
    			(RefObject_1_0)pm.getObjectById(new Path(objectXri)),
    			app
    		).getSelectObjectAction();
        response.sendRedirect(
           request.getContextPath() + "/" + nextAction.getEncodedHRef()
        );
      }
%>
    </body>
  </html>
<%
  }
  catch(Exception e) {
		AppLog.warning("Error creating rtf document", documentName);
		AppLog.warning(e.getMessage(), e.getCause());
		// Go back to previous view
        Action nextAction = new ObjectReference(
			(RefObject_1_0)pm.getObjectById(new Path(objectXri)),
			app
		).getSelectObjectAction();
		response.sendRedirect(
			request.getContextPath() + "/" + nextAction.getEncodedHRef()
		);
  }
%>
