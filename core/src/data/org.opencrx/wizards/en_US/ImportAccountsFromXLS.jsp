<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: ImportAccountsFromXLS.jsp,v 1.3 2009/06/08 10:48:46 cmu Exp $
 * Description: import accounts from Excel Sheet
 * Revision:    $Revision: 1.3 $
 * Owner:       CRIXP Corp., Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/06/08 10:48:46 $
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
java.util.*,
java.io.*,
java.text.*,
java.math.*,
java.net.URL,
java.net.URLEncoder,
java.net.MalformedURLException,
java.io.UnsupportedEncodingException,
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
org.opencrx.kernel.portal.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.base.exception.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.texts.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.reports.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*,
org.openmdx.base.query.*,
org.openmdx.base.exception.*,
org.openmdx.application.log.*,
org.openmdx.uses.org.apache.commons.fileupload.*,
org.apache.poi.hssf.usermodel.*,
org.apache.poi.hssf.util.*,
org.apache.poi.poifs.filesystem.POIFSFileSystem
" %>
<%!

	public List<org.opencrx.kernel.account1.jmi1.Contact> findContact(
			String firstName,
			String lastName,
			String extString0,
			org.opencrx.kernel.account1.jmi1.Segment accountSegment,
			javax.jdo.PersistenceManager pm
	) {
			List<org.opencrx.kernel.account1.jmi1.Contact> matchingContacts = null;
			try {
			    boolean hasQueryProperty = false;
			    org.opencrx.kernel.account1.cci2.ContactQuery contactFilter =
			        (org.opencrx.kernel.account1.cci2.ContactQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.Contact.class);
			    if(extString0 != null) {
			        hasQueryProperty = true;
			        contactFilter.thereExistsExtString0().equalTo(extString0); // exact match required
			    } else {
					    contactFilter.forAllDisabled().isFalse();
					    if(firstName != null) {
					        hasQueryProperty = true;
					        contactFilter.thereExistsFirstName().like("(?i).*" + firstName + ".*");
					    }
					    if(lastName != null) {
					        hasQueryProperty = true;
					        contactFilter.thereExistsLastName().like("(?i).*" + lastName + ".*");
					    }
			    }
			    if (hasQueryProperty) {
					    Collection contacts = accountSegment.getAccount(contactFilter);
					    if (!contacts.isEmpty()) {
									for(Iterator c = contacts.iterator(); c.hasNext(); ) {
										  if (matchingContacts == null) {
											  	matchingContacts = new ArrayList();
										  }
											matchingContacts.add((org.opencrx.kernel.account1.jmi1.Contact)c.next());
									}
					    }
			    }
			} catch (Exception e) {
				new ServiceException(e).log();
			}
			return matchingContacts;
	}

	public List<org.opencrx.kernel.account1.jmi1.AbstractGroup> findAbstractGroup(
			String name,
			String extString0,
			boolean allowDtypeGroup,
			boolean allowDtypeLegalEntity,
			boolean allowDtypeUnspecifiedAccount,
			org.opencrx.kernel.account1.jmi1.Segment accountSegment,
			javax.jdo.PersistenceManager pm
	) {
			List<org.opencrx.kernel.account1.jmi1.AbstractGroup> matchingAbstractGroups = null;
			try {
			    boolean hasQueryProperty = false;
			    org.opencrx.kernel.account1.cci2.AbstractGroupQuery abstractGroupFilter =
			        (org.opencrx.kernel.account1.cci2.AbstractGroupQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.AbstractGroup.class);
			    if(extString0 != null) {
			        hasQueryProperty = true;
			        abstractGroupFilter.thereExistsExtString0().equalTo(extString0); // exact match required
			    } else {
					    abstractGroupFilter.forAllDisabled().isFalse();
					    if(name != null) {
					        hasQueryProperty = true;
					        abstractGroupFilter.thereExistsFullName().like("(?i).*" + name + ".*");
					    }
			    }
			    if (hasQueryProperty) {
					    Collection abstractGroups = accountSegment.getAccount(abstractGroupFilter);
					    if (!abstractGroups.isEmpty()) {
									for(Iterator c = abstractGroups.iterator(); c.hasNext(); ) {
											org.opencrx.kernel.account1.jmi1.AbstractGroup abstractGroup = (org.opencrx.kernel.account1.jmi1.AbstractGroup)c.next();
											if (
													(allowDtypeGroup && abstractGroup instanceof org.opencrx.kernel.account1.jmi1.Group) ||
													(allowDtypeLegalEntity && abstractGroup instanceof org.opencrx.kernel.account1.jmi1.LegalEntity) ||
													(allowDtypeUnspecifiedAccount && abstractGroup instanceof org.opencrx.kernel.account1.jmi1.UnspecifiedAccount)
											) {
												  if (matchingAbstractGroups == null) {
													  	matchingAbstractGroups = new ArrayList();
												  }
												  matchingAbstractGroups.add(abstractGroup);
											}
									}
					    }
			    }
			} catch (Exception e) {
				new ServiceException(e).log();
			}
			return matchingAbstractGroups;
	}

	public org.opencrx.kernel.account1.jmi1.Account findUniqueTargetAccount(
			String valueToMatch,
			org.opencrx.kernel.account1.jmi1.Segment accountSegment,
			javax.jdo.PersistenceManager pm
	) {
		org.opencrx.kernel.account1.jmi1.Account targetAccount = null;
	    if(valueToMatch != null) {
					// try to locate account based on fullName
					try {
					    org.opencrx.kernel.account1.cci2.AccountQuery accountFilter =
					        (org.opencrx.kernel.account1.cci2.AccountQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.Account.class);
					    accountFilter.forAllDisabled().isFalse(); // exlude disabled accounts in search
					    accountFilter.thereExistsFullName().like("(?i).*" + valueToMatch + ".*");
					    Iterator accounts = accountSegment.getAccount(accountFilter).iterator();
							if (accounts.hasNext()) {
									targetAccount = (org.opencrx.kernel.account1.jmi1.Account)accounts.next();
									if (accounts.hasNext()) {
											// match must be unique
											targetAccount = null;
									}
							}
					} catch (Exception e) {
						new ServiceException(e).log();
					}

					if (targetAccount == null) {
							// try to locate account based on extString0
							try {
							    org.opencrx.kernel.account1.cci2.AccountQuery accountFilter =
							        (org.opencrx.kernel.account1.cci2.AccountQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.Account.class);
							    //accountFilter.forAllDisabled().isFalse(); // include disabled accounts in search
							    accountFilter.thereExistsExtString0().like("(?i).*" + valueToMatch + ".*");
							    Iterator accounts = accountSegment.getAccount(accountFilter).iterator();
									if (accounts.hasNext()) {
											targetAccount = (org.opencrx.kernel.account1.jmi1.Account)accounts.next();
											if (accounts.hasNext()) {
													// match must be unique
													targetAccount = null;
											}
									}
							} catch (Exception e) {
								new ServiceException(e).log();
							}
					}
			}
			return targetAccount;
	}

	public org.opencrx.kernel.account1.jmi1.Member createOrUpdateMember(
			org.opencrx.kernel.account1.jmi1.Account parentAccount,
			org.opencrx.kernel.account1.jmi1.Account memberAccount,
			String keyMemberRole,
			String feature,
			Codes codes,
			List<Short> activeLocales,
			org.opencrx.kernel.account1.jmi1.Account1Package accountPkg,
			org.opencrx.kernel.account1.jmi1.Segment accountSegment,
			javax.jdo.PersistenceManager pm
	) {
			org.opencrx.kernel.account1.jmi1.Member member = null;
	    short memberRole = 0;
			if ((parentAccount != null) && (memberAccount != null)) {
					memberRole = findCodeTableCodeFromValue(
						keyMemberRole,
						feature,
		    		codes,
		    		activeLocales
		    	);
					if (memberRole == 0) {
							try {
									memberRole = Short.parseShort(keyMemberRole);
							} catch (Exception e) {}
					}

					// try to locate member with same parent, account and memberRole (or create new member)
					org.opencrx.kernel.account1.cci2.MemberQuery memberFilter = accountPkg.createMemberQuery();
					memberFilter.forAllDisabled().isFalse();
					memberFilter.thereExistsAccount().equalTo(memberAccount);
					memberFilter.thereExistsMemberRole().equalTo(memberRole);
					try {
							Iterator m = parentAccount.getMember(memberFilter).iterator();
							if (m.hasNext()) {
									member = (org.opencrx.kernel.account1.jmi1.Member)m.next();
							} else {
									// create new member
									member = accountPkg.getMember().createMember();
									member.refInitialize(false, false);
									parentAccount.addMember(
											false,
											org.opencrx.kernel.backend.Accounts.getInstance().getUidAsString(),
											member
									);
							}
					} catch (Exception e) {
							new ServiceException(e).log();
					}
					if (member != null) {
							member.setMemberRole(new short[]{memberRole});
							//newMember.setValidFrom(new java.util.Date());
							member.setAccount(memberAccount);
							member.setQuality((short)5); // normal
							//System.out.println("member parent: " + memberAccount.getFullName());
							//System.out.println("member child:  " + parentAccount.getFullName());
							//System.out.println("member role:   " + memberRole);
					}
			}
			return member;
	}

  public String getObjectHref(
		  org.opencrx.kernel.generic.jmi1.CrxObject crxObject
	) {
	  	String href = "";
	  	if (crxObject != null) {
					Action parentAction = new Action(
							Action.EVENT_SELECT_OBJECT,
							new Action.Parameter[]{
							    new Action.Parameter(Action.PARAMETER_OBJECTXRI, crxObject.refMofId())
							},
							"",
							true // enabled
						);
					href = "../../" + parentAction.getEncodedHRef();
	  	}
	  	return href;
  }

	public short findCodeTableCodeFromValue(
			String value,
			String feature,
			Codes codes,
			List<Short> activeLocales
	) {
			short code = 0;
			if ((value != null) && (value.length() > 0)) {
					value = value.toUpperCase();
					try {
							boolean found = false;
							for(Iterator l = activeLocales.iterator(); l.hasNext() && !found; ) {
									short locale = ((Short)l.next()).shortValue();
									SortedMap code_longTextsC = codes.getLongText(feature, locale, false, false);
									for(Iterator i = code_longTextsC.keySet().iterator(); i.hasNext() && !found; ) {
											String longText = i.next().toString();
											if ((longText != null) && (longText.toUpperCase().startsWith(value))) {
													found = true;
													code = ((Short)code_longTextsC.get(longText)).shortValue();
											}
									}
							}
					} catch (Exception e) {
							new ServiceException(e).log();
					}
			}
			return code;
	}

%>

<%
	final String ATTR_EXTSTRING0 = "extString0";
	final String ATTR_FIRSTNAME = "FirstName";
	final String ATTR_LASTNAME = "LastName";
	final String ATTR_COMPANY = "Company";
	final String ATTR_DTYPE = "Dtype";
	final String DTYPE_CONTACT = "Contact";
	final String DTYPE_GROUP = "Group";
	final String DTYPE_LEGALENTITY = "LegalEntity";
	final String DTYPE_UNSPECIFIEDACCOUNT = "UnspecifiedAccount";
	final String FEATURE_POSTALCOUNTRY_CODE = "org:opencrx:kernel:address1:PostalAddressable:postalCountry";
	final String FEATURE_SALUTATION_CODE = "org:opencrx:kernel:account1:Contact:salutationCode";
	final String FEATURE_ACADEMICTITLE = "org:opencrx:kernel:account1:Contact:userCode1";
	final String FEATURE_MEMBERROLE = "org:opencrx:kernel:account1:Member:memberRole";

	final String EOL_HTML = "<br>";

  request.setCharacterEncoding("UTF-8");
	ApplicationContext app = (ApplicationContext)session.getValue(WebKeys.APPLICATION_KEY);
	if (app == null) {
		System.out.println("aborting... (ApplicationContext == null)");
    response.sendRedirect(
       request.getContextPath() + "/" + WebKeys.SERVLET_NAME
    );
    return;
  }
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
			AppLog.warning("cannot upload file", e.getMessage());
%>
      <div style="padding:10px 10px 10px 10px;background-color:#FF0000;color:#FFFFFF;">
        <table>
          <tr>
            <td style="padding:5px;"><b>ERROR</b>:</td>
            <td>cannot upload file - <%= e.getMessage() %></td>
          </tr>
        </table>
      </div>
<%
		}
  }
	String[] requestIds = (String[])parameterMap.get(Action.PARAMETER_REQUEST_ID);
	String requestId = (requestIds == null) || (requestIds.length == 0) ? request.getParameter(Action.PARAMETER_REQUEST_ID) : requestIds[0];
	String requestIdParam = Action.PARAMETER_REQUEST_ID + "=" + requestId;
	String[] objectXris = (String[])parameterMap.get("xri");
	String objectXri = (objectXris == null) || (objectXris.length == 0) ? null : objectXris[0];
  if (
    (app == null) || (objectXri == null)
  ) {
    response.sendRedirect(
       request.getContextPath() + "/" + WebKeys.SERVLET_NAME
    );
    return;
  }
  javax.jdo.PersistenceManager pm = app.getPmData();
  Texts_1_0 texts = app.getTexts();
  Codes codes = app.getCodes();
  Texts_1_0[] textsAllAvailableLocales = app.getTextsFactory().getTexts();
  List<Short> activeLocales = new ArrayList();
  for(int t = 0; t < textsAllAvailableLocales.length; t++) {
	  	activeLocales.add(textsAllAvailableLocales[t].getLocaleIndex());
  }

  final String formAction = "ImportAccountsFromXLS.jsp";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="org.opencrx.kernel.generic.jmi1.CrxObject"%><html>

<head>
  <title>Import Accounts from Excel Sheet (XLS)</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <meta name="UNUSEDlabel" content="Import Accounts from Excel Sheet (XLS)">
  <meta name="UNUSEDtoolTip" content="Import Accounts from Excel Sheet (XLS)">
  <meta name="targetType" content="_self">
  <meta name="forClass" content="org:opencrx:kernel:account1:Segment">
  <meta name="order" content="org:opencrx:kernel:account1:Segment:importAccountsFromXLS">
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <link href="../../_style/colors.css" rel="stylesheet" type="text/css">
  <link href="../../_style/n2default.css" rel="stylesheet" type="text/css" />
  <script language="javascript" type="text/javascript" src="../../javascript/prototype.js"></script>

  <link rel='shortcut icon' href='../../images/favicon.ico' />

  <style type="text/css">
  	.gridTableHeaderFull TD {
  		padding: 1px 5px 1px 5px; /* top right bottom left */
  		white-space: nowrap;
  	}
  	.err {background-color:red;color:white;}
  	.ImportTable {border-collapse:collapse;border:1px solid grey;}
  	.ImportTable .attributes TD {font-weight:bold;background-color:orange;}
  	.ImportTable .sheetInfo TD {background-color:yellow;padding:5px;}
  	.ImportTable .importHeader TD {font-weight:bold;background-color:#ddd;}
  	.ImportTable TD {white-space:nowrap;border:1px solid grey;padding:1px;}
  	.ImportTable TD.empty {background-color:grey;}
  	.ImportTable TD.searchAttr {background-color:#CFE9FE;}
  	.ImportTable TD.match {background-color:#D2FFD2;}
  	.ImportTable TD.create {background-color:#00FF00;}
  	.ImportTable TD.ok {background-color:#00FF00;}
  	.ImportTable TD.nok {background-color:#FF0000;}
  </style>
</head>

<body>
<div id="container">
	<div id="wrap">
		<div id="fixheader" style="height:90px;">
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

<%
    NumberFormat formatter = new DecimalFormat("0000");
		final String UPLOAD_FILE_FIELD_NAME = "uploadFile";
		try {
			boolean actionOk = parameterMap.get("OK.Button") != null;
			boolean actionCancel = parameterMap.get("Cancel.Button") != null;
			boolean continueToExit = false;

			String[] descriptions = (String[])parameterMap.get("description");
			String description = (descriptions == null) || (descriptions.length == 0) ? "" : descriptions[0];

			//System.out.println("XRI=" + objectXri);
			String location = app.getTempFileName(UPLOAD_FILE_FIELD_NAME, "");

			// Get data package. This is the JMI root package to handle
			// openCRX object requests

      RefObject_1_0 obj = (RefObject_1_0)pm.getObjectById(new Path(objectXri));

      Path objectPath = new Path(objectXri);
      String providerName = objectPath.get(2);
      String segmentName = objectPath.get(4);

      //Set userRoles = app.getUserRoles();
      String currentUserRole = app.getCurrentUserRole();
      String adminRole = "admin-" + segmentName + "@" + segmentName;
      boolean permissionOk = currentUserRole.compareTo(adminRole) == 0;
      permissionOk = true;

			if(actionCancel || (objectXri == null) || (!permissionOk)) {
        Action nextAction = new ObjectReference(
          (RefObject_1_0)pm.getObjectById(new Path(objectXri)),
          app
        ).getSelectObjectAction();
 				continueToExit = true;
			  if (actionCancel) {
  				response.sendRedirect(
  					request.getContextPath() + "/" + nextAction.getEncodedHRef()
  				);
  		  } else {
  		    String errorMessage = "Cannot upload Excel File!)";
  		    if (!permissionOk) {errorMessage = "no permission to run this wizard";}
%>
          <br />
          <br />
          <span style="color:red;"><b><u>ERROR:</u> <%= errorMessage %></b></span>
          <br />
          <br />
          <INPUT type="Submit" name="Cancel.Button" tabindex="1" value="Weiter" onClick="javascript:location='<%= request.getContextPath() + "/" + nextAction.getEncodedHRef() %>';" />
          <br />
          <br />
          <hr>
<%
  		  }
			}
			else if(actionOk) {
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
              java.util.Date today = new java.util.Date();

              // Get acount1 package
              org.opencrx.kernel.account1.jmi1.Account1Package accountPkg = org.opencrx.kernel.utils.Utils.getAccountPackage(pm);

              // Get account segment
              org.opencrx.kernel.account1.jmi1.Segment accountSegment =
                (org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(
                  new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/" + providerName + "/segment/" + segmentName)
                 );

							int idxExtString0 = -1;
							int idxFirstName = -1;
							int idxLastName = -1;
							int idxCompany = -1;
							int idxDtype = -1;

              // verify whether File exists
              POIFSFileSystem fs = null;
              try {
                fs = new POIFSFileSystem(new FileInputStream(location));
              } catch (Exception e) {}

          		if(permissionOk && actionOk && (fs != null)) {
         				continueToExit = true;

                try {
                  HSSFWorkbook workbook = new HSSFWorkbook(fs);
                  //for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                  // read first sheet only!!!
                  for (int i = 0; i < 1; i++) {
											HSSFSheet sheet = workbook.getSheetAt(i);
										  int linesRead = 0;
											int contactsUpdated = 0;
											int contactsCreated = 0;
											int groupsUpdated = 0;
											int groupsCreated = 0;
											int legalEntitiesUpdated = 0;
											int legalEntitiesCreated = 0;
											int unspecifiedAccountsUpdated = 0;
											int unspecifiedAccountsCreated = 0;

%>
											<table class="ImportTable">
<%
	                    Iterator rows = sheet.rowIterator();
	                    int nRow = 0;
	                    int maxCell = 0;
	                    HSSFRow row = null;

	                    Map attributeMap = new TreeMap();
											if (rows.hasNext()) {
	                      nRow += 1;
	                   		// read first row with attribute names
%>
													<tr class="attributes">
														<td>Row<br><%= formatter.format(nRow) %></td>
<%
			                    	row = (HSSFRow) rows.next();
		                        try {
		                            Iterator cells = row.cellIterator();
		                            int nCell = 0;
		                            while (cells.hasNext()) {
		                                HSSFCell cell = (HSSFCell)cells.next();
		                            		nCell = cell.getCellNum();
		                            		if (nCell > maxCell) {
		                            				maxCell = nCell;
		                            		}
		                                try {
		    		                            if (
		    		                            		(cell.getCellType() == HSSFCell.CELL_TYPE_STRING) &&
		    		                            		(cell.getStringCellValue() != null)
		    		                            ) {
						                            		boolean isSearchAttribute = false;
						                            		String cellValue = (cell.getStringCellValue().trim()).toUpperCase();
		    		                            		attributeMap.put(formatter.format(nCell), cellValue);
		    		                            		// get idx of select attributes
		    		                            		if (ATTR_EXTSTRING0.compareToIgnoreCase(cellValue) == 0) {
		    		                            				idxExtString0 = nCell;
		    		                            				isSearchAttribute = true;
		    		                            		} else if (ATTR_FIRSTNAME.compareToIgnoreCase(cellValue) == 0) {
		    		                            				idxFirstName = nCell;
		    		                            				isSearchAttribute = true;
		    		                            		} else if (ATTR_LASTNAME.compareToIgnoreCase(cellValue) == 0) {
		    		                            				idxLastName = nCell;
		    		                            				isSearchAttribute = true;
		    		                            		} else if (ATTR_COMPANY.compareToIgnoreCase(cellValue) == 0) {
		    		                            				idxCompany = nCell;
		    		                            				isSearchAttribute = true;
		    		                            		} else if (ATTR_DTYPE.compareToIgnoreCase(cellValue) == 0) {
		    		                            				idxDtype = nCell;
	    		                            			}
%>
																						<td <%= isSearchAttribute ? "class='searchAttr' title='attribute used for matching'" : "" %>>Col-<%= formatter.format(nCell) + EOL_HTML + cellValue %></td>
<%
		    		                            } else {
%>
	  	  		                            		<td class="err">c<%= formatter.format(nCell) %>[not a string cell]<br><%= cell.getCellFormula() %></td>
<%
		    		                            }
		    		                        } catch (Exception ec) {
			    		                        	new ServiceException(ec).log();
%>
																				<td class="err">c<%= formatter.format(nCell) %> [UNKNOWN ERROR]<br><%= cell.getCellFormula() %></td>
<%
	  	    	                        }
	    	                        }
	      	                  } catch (Exception e) {
	      	                			new ServiceException(e).log();
%>
																<td class="err">ERROR in Attribute Row!</td>
<%
	              	          }
%>
													</tr>
<%
                    	}
	                    while (rows.hasNext()) {
	                      nRow += 1;
	                      linesRead += 1;

	                      org.opencrx.kernel.account1.jmi1.Contact contact = null;
	                 	    org.opencrx.kernel.account1.jmi1.LegalEntity legalEntity = null;
	                 	    org.opencrx.kernel.account1.jmi1.Group group = null;
	                 	    org.opencrx.kernel.account1.jmi1.UnspecifiedAccount unspecifiedAccount = null;
	                 	    boolean isDtypeContact = true; // default
	                 	    boolean isDtypeGroup = false;
	                 	    boolean isDtypeLegalEntity = false;
	                 	    boolean isDtypeUnspecifiedAccount = false;
	                 	    String className = DTYPE_CONTACT; // default

	                 	    row = (HSSFRow) rows.next();
	                 	    String extString0 = null;
	                      String firstName = null;
	                      String lastName = null;
	                      String company = null;

	                      String cellId = null;
	                      String multiMatchList = "";
		                    Map valueMap = new TreeMap();
		                    boolean isCreation = false;
		                    boolean isUpdate = false;
%>
												<tr>
													<td><b>Row<br><%= formatter.format(nRow) %></b></td>
<%
													String jsBuffer = "";
													try {
		                        Iterator cells = row.cellIterator();
		                        int nCell = 0;
		                        int currentCell = 0;
		                        while (cells.hasNext()) {
		                            //HSSFCell cell = (HSSFCell)row.getCell((short)0);
		                            HSSFCell cell = (HSSFCell)cells.next();
		                        		nCell = cell.getCellNum();
		                        		if (nCell > currentCell) {
%>
																	<td colspan="<%= nCell-currentCell %>" class="empty">&nbsp;</td>
<%
		                        		}
		                        		currentCell = nCell+1;
		                            try {
		                            		cellId =  "id='r" + nRow + (String)attributeMap.get(formatter.format(nCell)) + "'";
			                            	if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
				                            		String cellValue = cell.getStringCellValue().trim();
		                            				valueMap.put((String)attributeMap.get(formatter.format(nCell)), cellValue);
                            						if (nCell == idxDtype) {
		                            						if (DTYPE_GROUP.compareToIgnoreCase(cellValue) == 0) {
		                            								isDtypeGroup = true;
		                            								isDtypeContact = false;
																								className = DTYPE_GROUP;
		                            						} else if (DTYPE_LEGALENTITY.compareToIgnoreCase(cellValue) == 0) {
		                            								isDtypeLegalEntity = true;
		                            								isDtypeContact = false;
																								className = DTYPE_LEGALENTITY;
	                            							} else if (DTYPE_UNSPECIFIEDACCOUNT.compareToIgnoreCase(cellValue) == 0) {
		                            								isDtypeUnspecifiedAccount = true;
		                            								isDtypeContact = false;
																								className = DTYPE_UNSPECIFIEDACCOUNT;
	                            							}
				                            		} else if (nCell == idxExtString0) {
			                            				extString0 = cellValue;
			                            			} else if (nCell == idxFirstName) {
				                            				firstName = cellValue;
				                            		} else if (nCell == idxLastName) {
				                            				lastName = cellValue;
				                            		} else if (nCell == idxCompany) {
			                            					company = cellValue;
				                            		}
%>
																			  <td <%= cellId %>><%= cellValue != null ? (cellValue.replace("\r\n", EOL_HTML)).replace("\n", EOL_HTML) : "" %></td>
<%
			                            	} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				                                BigDecimal cellValue = new BigDecimal(cell.getNumericCellValue());
		                            				valueMap.put((String)attributeMap.get(formatter.format(nCell)), cellValue);
%>
																				<td <%= cellId %>><%= cellValue %></td>
<%
				                            } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
				                                boolean cellValue = cell.getBooleanCellValue();
		                            				valueMap.put((String)attributeMap.get(formatter.format(nCell)), cellValue);
%>
																				<td <%= cellId %>><%= cellValue ? "TRUE" : "FALSE" %></td>
<%
			                            	} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
%>
																				<td <%= cellId %> class="empty">&nbsp;</td>
<%
			                            	}	else {
%>
			                            		<td class="err">r<%= formatter.format(nRow) %>-c<%= formatter.format(nCell) %>[cell-type (<%= cell.getCellType() %>) not supported]<br><%= cell.getCellFormula() %></td>
<%
		  		                          }
			                          } catch (Exception ec) {
			                        	  	new ServiceException(ec).log();
%>
																		<td class="err">r<%= formatter.format(nRow) %>-c<%= formatter.format(nCell) %> [UNKNOWN ERROR]<br><%= cell.getCellFormula() %></td>
<%
		  	                        }
		                        }
	                       		if (nCell < maxCell) {
%>
																<td colspan="<%= maxCell-nCell %>" class="empty"></td>
<%
														}
		                      } catch (Exception e) {
															new ServiceException(e).log();
%>
															<td class="err" colspan="<%= maxCell+2 %>">ERROR in Attribute Row!</td>
<%
		                      }
													boolean createNew = true;
													boolean updateExisting = false;

													List<org.opencrx.kernel.account1.jmi1.Contact> matchingContacts = null;
													List<org.opencrx.kernel.account1.jmi1.AbstractGroup> matchingAbstractGroups = null;
													String accountHref = "";
													org.opencrx.kernel.account1.jmi1.Account account = null;

													if (isDtypeContact) {
															matchingContacts = findContact(
																		firstName,
																		lastName,
																		extString0,
																		accountSegment,
																		pm
																	);
															if (matchingContacts != null) {
																// at least 1 match with existing contacts
																updateExisting = true;
																createNew = false;
																for(Iterator c = matchingContacts.iterator(); c.hasNext(); ) {
																		org.opencrx.kernel.account1.jmi1.Contact matchingContact = (org.opencrx.kernel.account1.jmi1.Contact)c.next();
																		if (c.hasNext()) {
																				// more than 1 match
																				updateExisting = false;;
																				Action action = new Action(
																						Action.EVENT_SELECT_OBJECT,
																						new Action.Parameter[]{
																						    new Action.Parameter(Action.PARAMETER_OBJECTXRI, matchingContact.refMofId())
																						},
																						"",
																						true // enabled
																					);
																				accountHref = "../../" + action.getEncodedHRef();
																				multiMatchList += "<br><a href='" + accountHref + " target='_blank'><b>" + (new ObjectReference(matchingContact, app)).getTitle() + "</b> [" + matchingContact.refMofId() + "]</a>";
																		} else if (updateExisting) {
																				contactsUpdated += 1;
																				isUpdate = true;
																				contact = matchingContact;
																		}
																}
															} else {
																// no match with existing contacts
																if (
																		// minimum requirements to creat contact
																		((firstName != null) || (lastName != null))
																) {
																		try {
																		    pm.currentTransaction().begin();
																		    contact = pm.newInstance(org.opencrx.kernel.account1.jmi1.Contact.class);
																	    	contact.refInitialize(false, false);
															    			contact.setFirstName(firstName);
															    			contact.setLastName(lastName);
															    			contact.setExtString0(extString0);
																		    accountSegment.addAccount(
																			        false,
																			        org.opencrx.kernel.backend.Accounts.getInstance().getUidAsString(),
																			        contact
																			    );
																		    pm.currentTransaction().commit();
																		} catch (Exception e) {
																				new ServiceException(e).log();
																				contact = null;
																				try {
																					pm.currentTransaction().rollback();
																				} catch(Exception e1) {}
																		}
																}
																if (contact != null) {
																		contactsCreated += 1;
																		isCreation = true;
																} else {
																		// creation failed
%>
																		<tr>
																			<td class="err" colspan="<%= maxCell+2 %>">CREATION FAILED [<b><%= className %></b>]</td>
																		</tr>
<%
																}
															}

															if (contact != null) {
																// update new or existing contact
																Action action = new Action(
																		Action.EVENT_SELECT_OBJECT,
																		new Action.Parameter[]{
																		    new Action.Parameter(Action.PARAMETER_OBJECTXRI, contact.refMofId())
																		},
																		"",
																		true // enabled
																	);
																accountHref = "../../" + action.getEncodedHRef();
																account = (org.opencrx.kernel.account1.jmi1.Account)contact;
																try {
																    pm.currentTransaction().begin();
																		for (
																				Iterator c = valueMap.keySet().iterator();
																				c.hasNext();
																		) {
																				String key = (String)c.next(); // key is equal to name of attribute
				                            		cellId =  "r" + nRow + key;

																				/*--------------------------------------------------------------*\
																				| BEGIN   M a p p i n g   C o n t a c t   t o   o p e n C R X    |
																				\---------------------------------------------------------------*/

																				boolean isOk = false;
																				boolean isNok = false;
																				try {
																				    DataBinding_1_0 postalHomeDataBinding = new PostalAddressDataBinding("[isMain=(boolean)true];usage=(short)400?zeroAsNull=true");

																				    if (key.equalsIgnoreCase("TITLE")) {
																						    // salutationCode
																						    short salutationCode = findCodeTableCodeFromValue(
																						    		(String)valueMap.get(key),
																						    		FEATURE_SALUTATION_CODE,
																						    		codes,
																						    		activeLocales
																						    	);
																						    contact.setSalutationCode(salutationCode);
																						    if (salutationCode != 0) {
																						    		isOk = true;
																						    } else {
																						    		isNok = true;
																						    }
																				    } else if (key.equalsIgnoreCase("ACADEMICTITLE")) {
																						    // academic Title (
																						    short academicTitle = findCodeTableCodeFromValue(
																						    		(String)valueMap.get(key),
																						    		FEATURE_ACADEMICTITLE,
																						    		codes,
																						    		activeLocales
																						    	);
																						    contact.setUserCode1(academicTitle);
																						    if (academicTitle != 0) {
																						    		isOk = true;
																						    } else {
																						    		isNok = true;
																						    }
																						} else if (key.equalsIgnoreCase("FIRSTNAME")) {
																						    contact.setFirstName((String)valueMap.get(key)); isOk = true;
																						} else if (key.equalsIgnoreCase("MIDDLENAME")) {
																						    contact.setMiddleName((String)valueMap.get(key)); isOk = true;
																						} else if (key.equalsIgnoreCase("LASTNAME")) {
																						    contact.setLastName((String)valueMap.get(key)); isOk = true;
																						} else if (key.equalsIgnoreCase("SUFFIX")) {
																						    contact.setSuffix((String)valueMap.get(key)); isOk = true;
																						} else if (key.equalsIgnoreCase("COMPANY")) {
																								String memberRole = null;
																								if (valueMap.containsKey("COMPANYROLE")) {
																										memberRole = (String)valueMap.get("COMPANYROLE");
																								}
																								org.opencrx.kernel.account1.jmi1.Account parentAccount = findUniqueTargetAccount((String)valueMap.get(key), accountSegment, pm);
																								org.opencrx.kernel.account1.jmi1.Member member = createOrUpdateMember(
																										parentAccount,
																										account,
																										memberRole,
																						    		FEATURE_MEMBERROLE,
																						    		codes,
																						    		activeLocales,
																						    		accountPkg,
																						    		accountSegment,
																						    		pm
																						    );
																								if (member != null) {
																										if (valueMap.containsKey("JOBTITLE")) {
																												member.setDescription((String)valueMap.get("JOBTITLE"));
																										}
																										isOk = true;
																										if (memberRole != null) {
																											jsBuffer += "$('r" + nRow +  "COMPANYROLE').className += ' ok';";
																										}
																										// add clickable links
																										jsBuffer += "$('r" + nRow + "COMPANY').innerHTML += '<br>&lt;Parent: <a href=\""
																											+ getObjectHref(parentAccount) + "\" target=\"_blank\"><b>" + (new ObjectReference(parentAccount, app)).getTitle() + "</b></a>&gt;<br>&lt;Member: <a href=\""
																											+ getObjectHref(account) + "\" target=\"_blank\"><b>" + (new ObjectReference(account, app)).getTitle() + "</b></a>&gt;';";
																										contact.setOrganization(parentAccount.getFullName()); isOk = true;
																								} else {
																										contact.setOrganization((String)valueMap.get(key)); isOk = true;
																								}
																						} else if (key.equalsIgnoreCase("DEPARTMENT")) {
																						    contact.setDepartment((String)valueMap.get(key)); isOk = true;
																						} else if (key.equalsIgnoreCase("JOBTITLE")) {
																						    contact.setJobTitle((String)valueMap.get(key)); isOk = true;
																						} else if (key.equalsIgnoreCase("BIRTHDAY")) {
																								String value = (String)valueMap.get(key);
																								if (!value.startsWith("00") && !value.startsWith("0.") && !value.startsWith("0-")) {
																										java.util.Date birthdate = null;
																										try {
																												SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy");
																												birthdate = sd.parse((String)valueMap.get(key));
																										} catch (Exception e) {}
																										if (birthdate == null) {
																												try {
																														SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yy");
																														birthdate = sd.parse((String)valueMap.get(key));
																												} catch (Exception e) {}
																										}
																										if (birthdate == null) {
																												try {
																														SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
																														birthdate = sd.parse((String)valueMap.get(key));
																												} catch (Exception e) {}
																										}
																										if (birthdate == null) {
																												try {
																														SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yy");
																														birthdate = sd.parse((String)valueMap.get(key));
																												} catch (Exception e) {}
																										}
																										if (birthdate != null) {
																								    		contact.setBirthdate(birthdate); isOk = true;
																										}
																								}
																						} else if (key.equalsIgnoreCase("HomePhone")) {
																						    // Phone Home
																						    DataBinding_1_0 phoneHomeDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)400;automaticParsing=(boolean)true");
																						    phoneHomeDataBinding.setValue(
																						        contact,
																						        "org:opencrx:kernel:account1:Contact:address!phoneNumberFull",
																						        (String)valueMap.get(key)
																						    );
																						    isOk = true;
																						} else if (key.equalsIgnoreCase("HomePhone2")) {
																						    // Phone other
																						    DataBinding_1_0 phoneOtherDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)1800;automaticParsing=(boolean)true");
																						    phoneOtherDataBinding.setValue(
																						        contact,
																						        "org:opencrx:kernel:account1:Account:address*Other!phoneNumberFull",
																						        (String)valueMap.get(key)
																						    );
																						    isOk = true;
																						} else if (key.equalsIgnoreCase("HomeFax")) {
																						    // Fax Home
																						    DataBinding_1_0 faxHomeDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)430;automaticParsing=(boolean)true");
																						    faxHomeDataBinding.setValue(
																						        contact,
																						        "org:opencrx:kernel:account1:Contact:address*Fax!phoneNumberFull",
																						        (String)valueMap.get(key)
																						    );
																						    isOk = true;
/*
																						} else if (key.equalsIgnoreCase("WebPage")) {
																						    // Web page
																						    org.openmdx.portal.servlet.databinding.CompositeObjectDataBinding webPageHomeDataBinding =
																						    	new org.openmdx.portal.servlet.databinding.CompositeObjectDataBinding("type=org:opencrx:kernel:account1:WebAddress;disabled=(boolean)false;[isMain=(boolean)true];usage=(short)400");
																						    webPageHomeDataBinding.setValue(
																						        contact,
																						        "org:opencrx:kernel:account1:Contact:address!webUrl",
																						        (String)valueMap.get(key)
																						    );
																						    isOk = true;
*/
																						} else if (key.equalsIgnoreCase("HomeAddressLine")) {
																						    // Postal Address Business / addressLine
																						    List<String> postalAddressLines = new ArrayList<String>();
																						    StringTokenizer tokenizer = new StringTokenizer(valueMap.get(key).toString(), "\r\n", false);
																						    while(tokenizer.hasMoreTokens()) {
																						    	postalAddressLines.add(tokenizer.nextToken());
																						    }
																						    postalHomeDataBinding.setValue(
																						        contact,
																						        "org:opencrx:kernel:account1:Contact:address!postalAddressLine",
																						        postalAddressLines
																						    );
																						    isOk = true;
																						} else if (key.equalsIgnoreCase("HomeStreet")) {
																						    // Postal Address Business / postalStreet
																						    List<String> postalStreetLines = new ArrayList<String>();
																						    StringTokenizer tokenizer = new StringTokenizer(valueMap.get(key).toString(), "\r\n", false);
																						    while(tokenizer.hasMoreTokens()) {
																						    	postalStreetLines.add(tokenizer.nextToken());
																						    }
																						    postalHomeDataBinding.setValue(
																						        contact,
																						        "org:opencrx:kernel:account1:Contact:address!postalStreet",
																						        postalStreetLines
																						    );
																						    isOk = true;
																						} else if (key.equalsIgnoreCase("HomeCity")) {
																						    // Postal Address Business / postalCity
																						    postalHomeDataBinding.setValue(
																						        contact,
																						        "org:opencrx:kernel:account1:Contact:address!postalCity",
																						        (String)valueMap.get(key)
																						    );
																						    isOk = true;
																						} else if (key.equalsIgnoreCase("HomePostalCode")) {
																						    // Postal Address Business / postalCode
																						    postalHomeDataBinding.setValue(
																						        contact,
																						        "org:opencrx:kernel:account1:Contact:address!postalCode",
																						        (String)valueMap.get(key)
																						    );
																						    isOk = true;
																						} else if (key.equalsIgnoreCase("HomeState")) {
																						    // Postal Address Business / postalState
																						    postalHomeDataBinding.setValue(
																						        contact,
																						        "org:opencrx:kernel:account1:Contact:address!postalState",
																						        (String)valueMap.get(key)
																						    );
																						    isOk = true;
																						} else if (key.equalsIgnoreCase("HomeCountry") || key.equalsIgnoreCase("HomeCountryRegion")) {
																						    // Postal Address Business / postalCountry
																						    short postalCountry = findCodeTableCodeFromValue(
																						    		(String)valueMap.get(key),
																						    		FEATURE_POSTALCOUNTRY_CODE,
																						    		codes,
																						    		activeLocales
																						    	);
																						    postalHomeDataBinding.setValue(
																						        contact,
																						        "org:opencrx:kernel:account1:Contact:address!postalCountry",
																						        postalCountry
																						    );
																						    if (postalCountry != 0) {
																						    		isOk = true;
																						    } else {
																						    		isNok = true;
																						    }
																						}
																				} catch (Exception e) {
																						new ServiceException(e).log();
																						isNok = true;
																				}
																				if (isOk) {
																				    jsBuffer += "$('" + cellId + "').className += ' ok';";
																				}
																				if (isNok) {
																						jsBuffer += "$('" + cellId + "').className += ' nok';";
																				}
																				/*--------------------------------------------------------------*\
																				| END   M a p p i n g   C o n t a c t   t o   o p e n C R X      |
																				\---------------------------------------------------------------*/
																		}
													    			pm.currentTransaction().commit();
																} catch (Exception e) {
																		new ServiceException(e).log();
																		contact = null;
																		try {
																			pm.currentTransaction().rollback();
																		} catch(Exception e1) {}
																}
															}
													} else if (
															isDtypeGroup ||
															isDtypeLegalEntity ||
															isDtypeUnspecifiedAccount
													) {
															org.opencrx.kernel.account1.jmi1.AbstractGroup abstractGroup = null;
															matchingAbstractGroups = findAbstractGroup(
																	company,
																	extString0,
																	isDtypeGroup,
																	isDtypeLegalEntity,
																	isDtypeUnspecifiedAccount,
																	accountSegment,
																	pm
																);
															if (matchingAbstractGroups != null) {
																// at least 1 match with existing AbstractGroups
																updateExisting = true;
																createNew = false;
																for(Iterator c = matchingAbstractGroups.iterator(); c.hasNext(); ) {
																		org.opencrx.kernel.account1.jmi1.AbstractGroup matchingAbstractGroup = (org.opencrx.kernel.account1.jmi1.AbstractGroup)c.next();
																		if (c.hasNext()) {
																				// more than 1 match
																				updateExisting = false;;
																				Action action = new Action(
																						Action.EVENT_SELECT_OBJECT,
																						new Action.Parameter[]{
																						    new Action.Parameter(Action.PARAMETER_OBJECTXRI, matchingAbstractGroup.refMofId())
																						},
																						"",
																						true // enabled
																					);
																				accountHref = "../../" + action.getEncodedHRef();
																				multiMatchList += "<br><a href='" + accountHref + " target='_blank'><b>" + (new ObjectReference(matchingAbstractGroup, app)).getTitle() + "</b> [" + matchingAbstractGroup.refMofId() + "]</a>";
																		} else if (updateExisting) {
																				isUpdate = true;
																				if (isDtypeGroup) {
																						groupsUpdated += 1;
																						group = (org.opencrx.kernel.account1.jmi1.Group)matchingAbstractGroup;
																						abstractGroup = matchingAbstractGroup;
																				} else if (isDtypeLegalEntity) {
																						legalEntitiesUpdated += 1;
																						legalEntity = (org.opencrx.kernel.account1.jmi1.LegalEntity)matchingAbstractGroup;
																						abstractGroup = matchingAbstractGroup;
																				} else if (isDtypeUnspecifiedAccount) {
																						unspecifiedAccountsUpdated += 1;
																						unspecifiedAccount = (org.opencrx.kernel.account1.jmi1.UnspecifiedAccount)matchingAbstractGroup;
																						abstractGroup = matchingAbstractGroup;
																				}
																		}
																}
															} else {
																// no match with existing AbstractGroups
																if (
																		// minimum requirements to creat AbstractGroup
																		(company != null)
																) {
																		try {
																		    pm.currentTransaction().begin();
																				if (isDtypeGroup) {
																				    group = pm.newInstance(org.opencrx.kernel.account1.jmi1.Group.class);
																				    group.refInitialize(false, false);
																				    group.setName(company);
																				    group.setExtString0(extString0);
																				    accountSegment.addAccount(
																					        false,
																					        org.opencrx.kernel.backend.Accounts.getInstance().getUidAsString(),
																					        group
																					    );
																				} else if (isDtypeLegalEntity) {
																				    legalEntity = pm.newInstance(org.opencrx.kernel.account1.jmi1.LegalEntity.class);
																				    legalEntity.refInitialize(false, false);
																				    legalEntity.setName(company);
																				    legalEntity.setExtString0(extString0);
																				    accountSegment.addAccount(
																					        false,
																					        org.opencrx.kernel.backend.Accounts.getInstance().getUidAsString(),
																					        legalEntity
																					    );
																				} else if (isDtypeUnspecifiedAccount) {
																				    unspecifiedAccount = pm.newInstance(org.opencrx.kernel.account1.jmi1.UnspecifiedAccount.class);
																				    unspecifiedAccount.refInitialize(false, false);
																				    unspecifiedAccount.setName(company);
																				    unspecifiedAccount.setExtString0(extString0);
																				    accountSegment.addAccount(
																					        false,
																					        org.opencrx.kernel.backend.Accounts.getInstance().getUidAsString(),
																					        unspecifiedAccount
																					    );
																				}
																		    pm.currentTransaction().commit();
																		} catch (Exception e) {
																				new ServiceException(e).log();
																				contact = null;
																				try {
																					pm.currentTransaction().rollback();
																				} catch(Exception e1) {}
																		}
																}

																if (isDtypeGroup && group != null) {
																		groupsCreated += 1;
																		isCreation = true;
																		abstractGroup = (org.opencrx.kernel.account1.jmi1.AbstractGroup)group;
																} else if (isDtypeLegalEntity && legalEntity != null) {
																		legalEntitiesCreated += 1;
																		isCreation = true;
																		abstractGroup = (org.opencrx.kernel.account1.jmi1.AbstractGroup)legalEntity;
																} else if (isDtypeUnspecifiedAccount && unspecifiedAccount != null) {
																		unspecifiedAccountsCreated += 1;
																		isCreation = true;
																		abstractGroup = (org.opencrx.kernel.account1.jmi1.AbstractGroup)unspecifiedAccount;
																} else {
																		// creation failed
%>
																		<tr>
																			<td class="err" colspan="<%= maxCell+2 %>">CREATION FAILED [<b><%= className %></b>]</td>
																		</tr>
<%
																}
															}

															if (abstractGroup != null) {
																// update new or existing abstractGroup
																Action action = new Action(
																		Action.EVENT_SELECT_OBJECT,
																		new Action.Parameter[]{
																		    new Action.Parameter(Action.PARAMETER_OBJECTXRI, abstractGroup.refMofId())
																		},
																		"",
																		true // enabled
																	);
																accountHref = "../../" + action.getEncodedHRef();
																account = (org.opencrx.kernel.account1.jmi1.Account)abstractGroup;
																try {
																    pm.currentTransaction().begin();
																		for (
																				Iterator c = valueMap.keySet().iterator();
																				c.hasNext();
																		) {
																				String key = (String)c.next(); // key is equal to name of attribute
				                            		cellId =  "r" + nRow + key;

				                            		boolean isOk = false;
																				try {
						                            		if (isDtypeGroup) {
																								/*----------------------------------------------------------*\
																								| BEGIN   M a p p i n g   G r o u p   t o   o p e n C R X    |
																								\-----------------------------------------------------------*/
																								if (key.equalsIgnoreCase("COMPANY")) {
																										group.setName((String)valueMap.get(key)); isOk = true;
																								}
																								/*----------------------------------------------------------*\
																								| END     M a p p i n g   G r o u p   t o   o p e n C R X    |
																								\-----------------------------------------------------------*/
																						} else if (isDtypeLegalEntity) {
																								/*----------------------------------------------------------------------*\
																								| BEGIN   M a p p i n g   L e g a l E n t i t y   t o   o p e n C R X    |
																								\-----------------------------------------------------------------------*/
																								if (key.equalsIgnoreCase("COMPANY")) {
																										legalEntity.setName((String)valueMap.get(key)); isOk = true;
																								}
																								/*----------------------------------------------------------------------*\
																								| END     M a p p i n g   L e g a l E n t i t y   t o   o p e n C R X    |
																								\-----------------------------------------------------------------------*/
																						} else if (isDtypeUnspecifiedAccount) {
																								/*------------------------------------------------------------------------------------*\
																								| BEGIN   M a p p i n g   U n s p e c i f i e d A c c o u n t   t o   o p e n C R X    |
																								\-------------------------------------------------------------------------------------*/
																								if (key.equalsIgnoreCase("COMPANY")) {
																										unspecifiedAccount.setName((String)valueMap.get(key)); isOk = true;
																								}
																								/*------------------------------------------------------------------------------------*\
																								| END     M a p p i n g   U n s p e c i f i e d A c c o u n t   t o   o p e n C R X    |
																								\-------------------------------------------------------------------------------------*/
																						}
																				} catch (Exception e) {
																				    jsBuffer += "$('" + cellId + "').className += ' nok';";
																				}
																				if (isOk) {
																				    jsBuffer += "$('" + cellId + "').className += ' ok';";
																				}
																		}
													    			pm.currentTransaction().commit();
																} catch (Exception e) {
																		new ServiceException(e).log();
																		try {
																			pm.currentTransaction().rollback();
																		} catch(Exception e1) {}
																		abstractGroup = null;
																		group = null;
																		legalEntity = null;
																		unspecifiedAccount = null;
																}
															}
													}

													if (account != null) {
															// update attributes common to all subclasses of Account
														try {
														    pm.currentTransaction().begin();
																for (
																		Iterator c = valueMap.keySet().iterator();
																		c.hasNext();
																) {
																		String key = (String)c.next(); // key is equal to name of attribute
		                            		cellId =  "r" + nRow + key;

																		/*--------------------------------------------------------------*\
																		| BEGIN   M a p p i n g   A c c o u n t   t o   o p e n C R X    |
																		\---------------------------------------------------------------*/

																		boolean isOk = false;
																		boolean isNok = false;
																		try {
																		    DataBinding_1_0 postalBusinesDataBinding = new PostalAddressDataBinding("[isMain=(boolean)true];usage=(short)500?zeroAsNull=true");

																		    if (key.equalsIgnoreCase(ATTR_EXTSTRING0)) {
																						// verify, but do NOT set extString0 (may only be set during creation of new contact!!!)
																						isOk = (valueMap.get(key) != null) && (account.getExtString0() != null) && (valueMap.get(key).toString().compareTo(account.getExtString0()) == 0);
																				} else if (key.equalsIgnoreCase("Notes")) {
																				    // description
																				    account.setDescription((String)valueMap.get(key));
																				    isOk = true;
																				} else if (key.equalsIgnoreCase("BusinessPhone")) {
																				    // Phone Business
																				    DataBinding_1_0 phoneBusinessDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)500;automaticParsing=(boolean)true");
																				    phoneBusinessDataBinding.setValue(
																				        account,
																				        "org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull",
																				        (String)valueMap.get(key)
																				    );
																				    isOk = true;
																				} else if (key.equalsIgnoreCase("BusinessPhone2")) {
																				    // Phone other
																				    DataBinding_1_0 phoneOtherDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)1800;automaticParsing=(boolean)true");
																				    phoneOtherDataBinding.setValue(
																				    		account,
																				        "org:opencrx:kernel:account1:Account:address*Other!phoneNumberFull",
																				        (String)valueMap.get(key)
																				    );
																				    isOk = true;
																				} else if (key.equalsIgnoreCase("BusinessFax")) {
																				    // Fax Business
																				    DataBinding_1_0 faxBusinessDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)530;automaticParsing=(boolean)true");
																				    faxBusinessDataBinding.setValue(
																				    		account,
																				        "org:opencrx:kernel:account1:Account:address*BusinessFax!phoneNumberFull",
																				        (String)valueMap.get(key)
																				    );
																				    isOk = true;
																				} else if (key.equalsIgnoreCase("MobilePhone")) {
																				    // Phone Mobile
																				    DataBinding_1_0 phoneMobileDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)200;automaticParsing=(boolean)true");
																				    phoneMobileDataBinding.setValue(
																				    		account,
																				        "org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull",
																				        (String)valueMap.get(key)
																				    );
																				    isOk = true;
																				} else if (key.equalsIgnoreCase("EmailAddress")) {
																				    // Mail Business
																				    DataBinding_1_0 mailBusinessDataBinding = new EmailAddressDataBinding("[isMain=(boolean)true];usage=(short)500;[emailType=(short)1]");
																				    mailBusinessDataBinding.setValue(
																				        account,
																				        "org:opencrx:kernel:account1:Account:address*Business!emailAddress",
																				        (String)valueMap.get(key)
																				    );
																				    isOk = true;
																				} else if (key.equalsIgnoreCase("Email2Address")) {
																				    // Mail Home
																				    DataBinding_1_0 mailHomeDataBinding = new EmailAddressDataBinding("[isMain=(boolean)true];usage=(short)400;[emailType=(short)1]");
																				    mailHomeDataBinding.setValue(
																				        account,
																				        "org:opencrx:kernel:account1:Contact:address!emailAddress",
																				        (String)valueMap.get(key)
																				    );
																				    isOk = true;
																				} else if (key.equalsIgnoreCase("Email3Address")) {
																				    // Mail Other
																				    DataBinding_1_0 mailOtherDataBinding = new EmailAddressDataBinding("[isMain=(boolean)true];usage=(short)1800;[emailType=(short)1]");
																				    mailOtherDataBinding.setValue(
																				        account,
																				        "org:opencrx:kernel:account1:Account:address*Other!emailAddress",
																				        (String)valueMap.get(key)
																				    );
																				    isOk = true;
																				} else if (key.equalsIgnoreCase("WebPage")) {
																				    // Web page
																				    org.openmdx.portal.servlet.databinding.CompositeObjectDataBinding webPageBusinessDataBinding =
																				    	new org.openmdx.portal.servlet.databinding.CompositeObjectDataBinding("type=org:opencrx:kernel:account1:WebAddress;disabled=(boolean)false;[isMain=(boolean)true];usage=(short)500");
																				    webPageBusinessDataBinding.setValue(
																				        account,
																				        "org:opencrx:kernel:account1:LegalEntity:address!webUrl",
																				        (String)valueMap.get(key)
																				    );
																				    isOk = true;
																				} else if (key.equalsIgnoreCase("BusinessAddressLine")) {
																				    // Postal Address Business / addressLine
																				    List<String> postalAddressLines = new ArrayList<String>();
																				    StringTokenizer tokenizer = new StringTokenizer(valueMap.get(key).toString(), "\r\n", false);
																				    while(tokenizer.hasMoreTokens()) {
																				    	postalAddressLines.add(tokenizer.nextToken());
																				    }
																				    postalBusinesDataBinding.setValue(
																				    	account,
																				        "org:opencrx:kernel:account1:Account:address*Business!postalAddressLine",
																				        postalAddressLines
																				    );
																				    isOk = true;
																				} else if (key.equalsIgnoreCase("BusinessStreet")) {
																				    // Postal Address Business / postalStreet
																				    List<String> postalStreetLines = new ArrayList<String>();
																				    StringTokenizer tokenizer = new StringTokenizer(valueMap.get(key).toString(), "\r\n", false);
																				    while(tokenizer.hasMoreTokens()) {
																				    	postalStreetLines.add(tokenizer.nextToken());
																				    }
																				    postalBusinesDataBinding.setValue(
																				    	account,
																				        "org:opencrx:kernel:account1:Account:address*Business!postalStreet",
																				        postalStreetLines
																				    );
																				    isOk = true;
																				} else if (key.equalsIgnoreCase("BusinessCity")) {
																				    // Postal Address Business / postalCity
																				    postalBusinesDataBinding.setValue(
																				    		account,
																				        "org:opencrx:kernel:account1:Account:address*Business!postalCity",
																				        (String)valueMap.get(key)
																				    );
																				    isOk = true;
																				} else if (key.equalsIgnoreCase("BusinessPostalCode")) {
																				    // Postal Address Business / postalCode
																				    postalBusinesDataBinding.setValue(
																				    		account,
																				        "org:opencrx:kernel:account1:Account:address*Business!postalCode",
																				        (String)valueMap.get(key)
																				    );
																				    isOk = true;
																				} else if (key.equalsIgnoreCase("BusinessState")) {
																				    // Postal Address Business / postalState
																				    postalBusinesDataBinding.setValue(
																				    		account,
																				        "org:opencrx:kernel:account1:Account:address*Business!postalState",
																				        (String)valueMap.get(key)
																				    );
																				    isOk = true;
																				} else if (key.equalsIgnoreCase("BusinessCountry") || key.equalsIgnoreCase("BusinessCountryRegion")) {
																				    // Postal Address Business / postalCountry
																				    short postalCountry = findCodeTableCodeFromValue(
																				    		(String)valueMap.get(key),
																				    		FEATURE_POSTALCOUNTRY_CODE,
																				    		codes,
																				    		activeLocales
																				    	);
																				    postalBusinesDataBinding.setValue(
																				    		account,
																				        "org:opencrx:kernel:account1:Account:address*Business!postalCountry",
																				        postalCountry
																				    );
																				    if (postalCountry != 0) {
																				    		isOk = true;
																				    } else {
																				    		isNok = true;
																				    }
																				} else if (key.equalsIgnoreCase("ASSISTANTSNAME")) {
																						String memberRole = null;
																						if (valueMap.containsKey("ASSISTANTSNAMEROLE")) {
																								memberRole = (String)valueMap.get("ASSISTANTSNAMEROLE");
																						}
																						org.opencrx.kernel.account1.jmi1.Account parentAccount = findUniqueTargetAccount((String)valueMap.get(key), accountSegment, pm);
																						org.opencrx.kernel.account1.jmi1.Member member = createOrUpdateMember(
																								parentAccount,
																								account,
																								memberRole,
																				    		FEATURE_MEMBERROLE,
																				    		codes,
																				    		activeLocales,
																				    		accountPkg,
																				    		accountSegment,
																				    		pm
																				    );
																						if (member != null) {
																								isOk = true;
																								if (memberRole != null) {
																									jsBuffer += "$('r" + nRow +  "ASSISTANTSNAMEROLE').className += ' ok';";
																								}
																								// add clickable links
																								jsBuffer += "$('r" + nRow + "ASSISTANTSNAME').innerHTML += '<br>&lt;Parent: <a href=\""
																									+ getObjectHref(parentAccount) + "\" target=\"_blank\"><b>" + (new ObjectReference(parentAccount, app)).getTitle() + "</b></a>&gt;<br>&lt;Member: <a href=\""
																									+ getObjectHref(account) + "\" target=\"_blank\"><b>" + (new ObjectReference(account, app)).getTitle() + "</b></a>&gt;';";
																						}
																				} else if (key.equalsIgnoreCase("MANAGERSNAME")) {
																						String memberRole = null;
																						if (valueMap.containsKey("MANAGERSROLE")) {
																								memberRole = (String)valueMap.get("MANAGERSROLE");
																						}
																						org.opencrx.kernel.account1.jmi1.Account manager = findUniqueTargetAccount((String)valueMap.get(key), accountSegment, pm);
																						org.opencrx.kernel.account1.jmi1.Member member = createOrUpdateMember(
																								account,
																								manager,
																								memberRole,
																				    		FEATURE_MEMBERROLE,
																				    		codes,
																				    		activeLocales,
																				    		accountPkg,
																				    		accountSegment,
																				    		pm
																				    );
																						if (member != null) {
																								isOk = true;
																								if (memberRole != null) {
																									jsBuffer += "$('r" + nRow +  "MANAGERSROLE').className += ' ok';";
																								}
																								// add clickable links
																								jsBuffer += "$('r" + nRow + "MANAGERSNAME').innerHTML += '<br>&lt;Parent: <a href=\""
																									+ getObjectHref(account) + "\" target=\"_blank\"><b>" + (new ObjectReference(account, app)).getTitle() + "</b></a>&gt;<br>&lt;Member: <a href=\""
																									+ getObjectHref(manager) + "\" target=\"_blank\"><b>" + (new ObjectReference(manager, app)).getTitle() + "</b></a>&gt;';";
																						}
																				}
																		} catch (Exception e) {
																				new ServiceException(e).log();
																				isNok = true;
																		}
																		if (isOk) {
																		    jsBuffer += "$('" + cellId + "').className += ' ok';";
																		}
																		if (isNok) {
																				jsBuffer += "$('" + cellId + "').className += ' nok';";
																		}
																		/*--------------------------------------------------------------*\
																		| END   M a p p i n g   A c c o u n t   t o   o p e n C R X      |
																		\---------------------------------------------------------------*/
																}
											    			pm.currentTransaction().commit();
														} catch (Exception e) {
																new ServiceException(e).log();
																contact = null;
																try {
																	pm.currentTransaction().rollback();
																} catch(Exception e1) {}
														}
													}

													valueMap = null;
													if (isCreation) {
%>
															<tr>
																<td class="create" colspan="<%= maxCell+2 %>">
																	CREATED [<b><%= className %></b>]: <a href="<%= accountHref %>" target="_blank"><b><%=  (new ObjectReference(account, app)).getTitle() %></b> [<%= account.refMofId() %>]</a>
																	<%= jsBuffer.length() > 0 ? "<script language='javascript' type='text/javascript'>" + jsBuffer + "</script>" : "" %>
																</td>
															</tr>
<%
													}
													if (isUpdate) {
															if (multiMatchList.length() > 0) {
%>
																	<tr>
																		<td class="match" colspan="<%= maxCell+2 %>">
																			NO UPDATE [<b><%= className %></b>] - Multiple Matches:<%= multiMatchList %>
																		</td>
																	</tr>
<%

															} else {
%>
																	<tr>
																		<td class="match" colspan="<%= maxCell+2 %>">
																			UPDATED [<b><%= className %></b>]: <a href="<%= accountHref %>" target="_blank"><b><%=  (new ObjectReference(account, app)).getTitle() %></b> [<%= account.refMofId() %>]</a>
																			<%= jsBuffer.length() > 0 ? "<script language='javascript' type='text/javascript'>" + jsBuffer + "</script>" : "" %>
																		</td>
																	</tr>
<%
															}
													}
%>
												</tr>
<%
	                    } /* while */
%>
											<tr class="sheetInfo">
												<td colspan="<%= maxCell+2 %>">
													Sheet: <b><%= workbook.getSheetName(i) %></b> |
													data lines <b>read: <%= linesRead %></b><br>
												</td>
											</tr>
											<tr class="importHeader">
												<td><%= ATTR_DTYPE %></td>
												<td>created</td>
												<td colspan="<%= maxCell %>">updated</td>
											</tr>
											<tr>
												<td><%= DTYPE_CONTACT %></td>
												<td><%= contactsCreated %></td>
												<td colspan="<%= maxCell %>"><%= contactsUpdated %></td>
											</tr>
											<tr>
												<td><%= DTYPE_GROUP %></td>
												<td><%= groupsCreated %></td>
												<td colspan="<%= maxCell %>"><%= groupsUpdated %></td>
											</tr>
											<tr>
												<td><%= DTYPE_LEGALENTITY %></td>
												<td><%= legalEntitiesCreated %></td>
												<td colspan="<%= maxCell %>"><%= legalEntitiesUpdated %></td>
											</tr>
											<tr>
												<td><%= DTYPE_UNSPECIFIEDACCOUNT %></td>
												<td><%= unspecifiedAccountsCreated %></td>
												<td colspan="<%= maxCell %>"><%= unspecifiedAccountsUpdated %></td>
											</tr>
<%
				              if (linesRead != contactsCreated + contactsUpdated +
				            		  						 groupsCreated + groupsUpdated +
				            		  						 legalEntitiesCreated + legalEntitiesUpdated +
				            		  						 unspecifiedAccountsCreated + unspecifiedAccountsUpdated
				            	) {
%>
												<tr>
													<td class="err" colspan="<%= maxCell+2 %>">WARNING: some data lines were not processed due to data errors (e.g. multiple matches, missing first/last name, etc.)</td>
												</tr>
<%
				              }
%>
											</table>
<%
                  } /* for */
%>
		              <hr>
<%
                } catch (Exception e) {
										ServiceException e0 = new ServiceException(e);
										e0.log();
										out.println("<div style='color:red;padding:5px;margin:10px;'><b><u>Warning:</u> Error reading/processing Excel file!<br><br>The following exception(s) occured:</b><br><br><pre>");
										PrintWriter pw = new PrintWriter(out);
										e0.printStackTrace(pw);
										out.println("</pre></div>");
                }
              }
							new File(location).delete();

              // Go back to previous view
              Action nextAction =
                new Action(
            			Action.EVENT_SELECT_OBJECT,
                  new Action.Parameter[]{
                    new Action.Parameter(Action.PARAMETER_OBJECTXRI, objectXri)
                    },
                  "", true
          	  );
%>
              <br />
              <br />
              <INPUT type="Submit" name="Cancel.Button" tabindex="1" value="Continue" onClick="javascript:location='<%= request.getContextPath() + "/" + nextAction.getEncodedHRef() %>';" />
              <br />
              <br />
<%
						}
						catch(Exception e) {
								new ServiceException(e).log();
						}
					}
				}
			  else {

			  }
			}
			else {
				File uploadFile = new File(location);
				System.out.println("Import: file " + location + " either does not exist or has size 0: exists=" + uploadFile.exists() + "; length=" + uploadFile.length());
			}
			if (!continueToExit) {
%>
<form name="UploadMedia" enctype="multipart/form-data" accept-charset="UTF-8" method="POST" action="<%= formAction %>">
<input type="hidden" class="valueL" name="xri" value="<%= objectXri %>" />
<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= requestId %>" />
<table cellspacing="8" class="tableLayout">
  <tr>
    <td class="cellObject">
      <noscript>
        <div class="panelJSWarning" style="display: block;">
          <a href="../../helpJsCookie.html" target="_blank"><img class="popUpButton" src="../../images/help.gif" width="16" height="16" border="0" onclick="javascript:void(window.open('helpJsCookie.html', 'Help', 'fullscreen=no,toolbar=no,status=no,menubar=no,scrollbars=yes,resizable=yes,directories=no,location=no,width=400'));" alt="" /></a> <%= texts.getPageRequiresScriptText() %>
        </div>
      </noscript>
      <div id="etitle" style="height:20px;">
         Import Contacts from Excel Sheet (XLS) - 1 account per row<br>
      </div>

      <div class="panel" id="panelObj0" style="display: block">
        <div class="fieldGroupName">
          <span style="font-size:9px;">(Hint: row 1 contains field names, data starts at row 2)</span>
        </div>
        <br>
	      <table class="fieldGroup">
          <tr id="waitMsg" style="display:none;">
            <td colspan="3">
              <table class="objectTitle">
                <tr>
                  <td>
                    <div style="padding-left:5px; padding-bottom: 3px;">
                      Processing request - please wait...<br>
                      <img border=0 src='../../images/progress_bar.gif' alt='please wait...' />
                    </div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
					<tr id="submitFilename">
    				<td class="label"><span class="nw">File:</span></td>
    				<td >
    					<input type="file" class="valueL" size="100" name="<%= UPLOAD_FILE_FIELD_NAME %>" tabindex="500" />
    				</td>
    				<td class="addon" >&nbsp;<br>&nbsp;</td>
    			</tr>
					<tr id="submitButtons">
	          <td class="label" colspan="3">
	          	<INPUT type="Submit" name="OK.Button" tabindex="1000" value="Importieren" onclick="javascript:$('waitMsg').style.display='block';$('submitButtons').style.visibility='hidden';$('submitFilename').style.visibility='hidden';" />
      			  <INPUT type="Submit" name="Cancel.Button" tabindex="1010" value="Abbrechen" />
	          </td>
	          <td></td>
    				<td class="addon" >&nbsp;<br>&nbsp;</td>
	        </tr>
	      </table>
      </div>
  	</td>
  </tr>
</table>
</form>
<%
      }
    }
    catch (Exception ex) {
      Action nextAction = new ObjectReference(
        (RefObject_1_0)pm.getObjectById(new Path(objectXri)),
        app
      ).getSelectObjectAction();
%>
      <br />
      <br />
      <span style="color:red;"><b><u>Warning:</u> cannot upload file (no permission?)</b></span>
      <br />
      <br />
      <INPUT type="Submit" name="Continue.Button" tabindex="1" value="Continue" onClick="javascript:location='<%= request.getContextPath() + "/" + nextAction.getEncodedHRef() %>';" />
      <br />
      <br />
      <hr>
<%
	      ServiceException e0 = new ServiceException(ex);
	      e0.log();
	      out.println("<p><b>!! Failed !!<br><br>The following exception(s) occured:</b><br><br><pre>");
	      PrintWriter pw = new PrintWriter(out);
	      e0.printStackTrace(pw);
	      out.println("</pre></p>");
    }
%>
      </div> <!-- content -->
    </div> <!-- content-wrap -->
	<div> <!-- wrap -->
</div> <!-- container -->
</body>
</html>
