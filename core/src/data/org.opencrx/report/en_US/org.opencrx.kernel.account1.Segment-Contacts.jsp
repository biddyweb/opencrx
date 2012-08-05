<%/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: org.opencrx.kernel.account1.Segment-Contacts.jsp,v 1.11 2008/04/13 13:43:11 wfro Exp $
 * Description: openCRX Contacts Report
 * Revision:    $Revision: 1.11 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/04/13 13:43:11 $
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
org.openmdx.compatibility.base.naming.*,
org.openmdx.compatibility.base.dataprovider.cci.*,
org.openmdx.compatibility.base.query.*,
org.openmdx.application.log.*,
org.apache.poi.hssf.usermodel.*,
org.apache.poi.hssf.util.*
" %>
<!--
<report>
	<property name="label">Contacts Report</property>
	<property name="description">Contacts Report</property>
	<parameters>
		<scalar-parameter name="nameFrom">
			<property name="displayName">Start with name</property>
			<property name="dataType">String</property>
			<property name="defaultValue"> </property>
		</scalar-parameter>
		<scalar-parameter name="nameTo">
			<property name="displayName">End with name</property>
			<property name="dataType">String</property>
			<property name="defaultValue"> </property>
		</scalar-parameter>
		<scalar-parameter name="exportMax">
			<property name="displayName">Export max.</property>
			<property name="dataType">String</property>
			<property name="defaultValue">100</property>
		</scalar-parameter>
	</parameters>
</report>
-->
<%
  ApplicationContext app = (ApplicationContext)session.getValue("ObjectInspectorServlet.ApplicationContext");
  ShowObjectView view = (ShowObjectView)session.getValue("ObjectInspectorServlet.View");
  javax.jdo.PersistenceManager pm = view.getPersistenceManager();
  Texts_1_0 texts = app.getTexts();
  Codes codes = app.getCodes();

	// Get parameters
	String nameFrom = request.getParameter("nameFrom");
	String nameTo = request.getParameter("nameTo");
	int exportMax = request.getParameter("exportMax") == null
		? 100
		: new Integer(request.getParameter("exportMax")).intValue();
	
  // Generate report  
   String location = UUIDs.getGenerator().next().toString();
   File f = new File(
       app.getTempFileName(location, "")
   );
   FileOutputStream os = new FileOutputStream(f);
   
	// TODO: generate contacts report
	HSSFWorkbook wb = new HSSFWorkbook();
	HSSFSheet contactSheet = wb.createSheet("Contacts");
	
	org.opencrx.kernel.account1.jmi1.Account1Package accountPkg = org.opencrx.kernel.utils.Utils.getAccountPackage(pm);
	org.opencrx.kernel.account1.jmi1.Segment accountSegment = 
		(org.opencrx.kernel.account1.jmi1.Segment)view.getObjectReference().getObject();
	org.opencrx.kernel.account1.cci2.ContactQuery contactFilter = accountPkg.createContactQuery();
	if((nameFrom != null) && (nameFrom.trim().length() > 0)) {
		contactFilter.thereExistsFullName().greaterThanOrEqualTo(
			nameFrom.trim()
		);
	}
	if((nameTo != null) && (nameTo.trim().length() > 0)) {
		contactFilter.thereExistsFullName().lessThanOrEqualTo(
			nameTo.trim()
		);
	}
	int ii = 0;
	int nRow = 0;
	for(
		Iterator i = accountSegment.getAccount(contactFilter).iterator();
		i.hasNext() && (ii < exportMax);
		ii++
	) {
		org.opencrx.kernel.account1.jmi1.Contact contact = 
			(org.opencrx.kernel.account1.jmi1.Contact)i.next();
		// Fill contact row
	 	HSSFRow contactRow = contactSheet.createRow((short)nRow++);
	 	// Class
	 	HSSFCell cell = contactRow.createCell((short)0);
	  	cell.setCellValue(contact.refClass().refMofId());
	 	// Full name
	 	cell = contactRow.createCell((short)1);
	  	cell.setCellValue(contact.getFullName());
	 	// Last name
	 	cell = contactRow.createCell((short)2);
	  	cell.setCellValue(contact.getLastName());
	 	// Given name
	 	cell = contactRow.createCell((short)3);
	  	cell.setCellValue(contact.getFirstName());
	 	// Alias
	 	cell = contactRow.createCell((short)4);
	  	cell.setCellValue(contact.getAliasName());
	 	// Department
	 	cell = contactRow.createCell((short)5);
	  	cell.setCellValue(contact.getDepartment());
		int addressStartAtRow = nRow;
	  	for(
	  		Iterator j = contact.getAddress().iterator();
	  		j.hasNext();
	  	) {
			org.opencrx.kernel.account1.jmi1.AccountAddress address = 
				(org.opencrx.kernel.account1.jmi1.AccountAddress)j.next();
	  		// Fill address row
		 	HSSFRow addressRow = contactSheet.createRow((short)nRow++);
		 	// Class
		 	cell = addressRow.createCell((short)0);
		  	cell.setCellValue(address.refClass().refMofId());
		  	// PhoneNumber
		  	if(address instanceof org.opencrx.kernel.account1.jmi1.PhoneNumber) {
		  		org.opencrx.kernel.account1.jmi1.PhoneNumber phoneNumber = 
		  			(org.opencrx.kernel.account1.jmi1.PhoneNumber)address;
		  		// Phone number full
			 	cell = addressRow.createCell((short)2);
			  	cell.setCellValue(phoneNumber.getPhoneNumberFull());
		  		// Country prefix
			 	cell = addressRow.createCell((short)3);
			 	try {
				  	cell.setCellValue(
				  		(String)codes.getShortText(
				  			"org:opencrx:kernel:address1:PhoneNumberAddressable:phoneCountryPrefix",
				  			app.getCurrentLocaleAsIndex(),
				  			true,
				  			true
				  		).get(
				  			new Short(phoneNumber.getPhoneCountryPrefix())
					  	)
				  	);
				} catch(Exception e) {}
		  		// City area
			 	cell = addressRow.createCell((short)4);
			  	cell.setCellValue(phoneNumber.getPhoneCityArea());
		  		// Local number
			 	cell = addressRow.createCell((short)5);
			  	cell.setCellValue(phoneNumber.getPhoneLocalNumber());
		  		// Extension
			 	cell = addressRow.createCell((short)6);
			  	cell.setCellValue(phoneNumber.getPhoneExtension());
		  	}
		  	// EMailAddress
		  	else if(address instanceof org.opencrx.kernel.account1.jmi1.EmailAddress) {
		  		org.opencrx.kernel.account1.jmi1.EmailAddress email = 
		  			(org.opencrx.kernel.account1.jmi1.EmailAddress)address;
		  		// EMail address
			 	cell = addressRow.createCell((short)2);
			  	cell.setCellValue(email.getEmailAddress());
		  	}
		  	// WebAddress
		  	else if(address instanceof org.opencrx.kernel.account1.jmi1.WebAddress) {
		  		org.opencrx.kernel.account1.jmi1.WebAddress web = 
		  			(org.opencrx.kernel.account1.jmi1.WebAddress)address;
		  		// Web Url
			 	cell = addressRow.createCell((short)2);
			  	cell.setCellValue(web.getWebUrl());
		  	}
		  	// PostalAddress
		  	else if(address instanceof org.opencrx.kernel.account1.jmi1.PostalAddress) {
		  		org.opencrx.kernel.account1.jmi1.PostalAddress postal = 
		  			(org.opencrx.kernel.account1.jmi1.PostalAddress )address;
		  		// Address lines
			 	cell = addressRow.createCell((short)2);
			  	cell.setCellValue(postal.getPostalAddressLine().toString());
		  		// Street
			 	cell = addressRow.createCell((short)3);
			  	cell.setCellValue(postal.getPostalStreet().toString());
		  		// Postal code
			 	cell = addressRow.createCell((short)4);
			  	cell.setCellValue(postal.getPostalCode());
		  		// Postal city
			 	cell = addressRow.createCell((short)5);
			  	cell.setCellValue(postal.getPostalCity());
		  		// Postal state
			 	cell = addressRow.createCell((short)6);
			  	cell.setCellValue(postal.getPostalState());
		  		// Postal country
			 	cell = addressRow.createCell((short)7);
			  	cell.setCellValue(
			  		(String)codes.getShortText(
			  			"org:opencrx:kernel:address1:PostalAddressable:postalCountry",
			  			app.getCurrentLocaleAsIndex(),
			  			true,
			  			true
			  		).get(
			  			new Short(postal.getPostalCountry())
				  	)
			  	);
		  	}
		  	// Room
		  	else if(address instanceof org.opencrx.kernel.account1.jmi1.Room) {
		  		org.opencrx.kernel.account1.jmi1.Room room = 
		  			(org.opencrx.kernel.account1.jmi1.Room)address;
		  		// Address lines
			 	cell = addressRow.createCell((short)2);
			  	cell.setCellValue(room.getRoomNumber());
		  	}
		}
		if(nRow > addressStartAtRow) {
			System.out.println("groupRow from " + addressStartAtRow + " to " + (nRow-1));
			contactSheet.groupRow(
				addressStartAtRow,
				nRow-1
		 	);
		 	contactSheet.setRowGroupCollapsed(
		 		addressStartAtRow,
		 		true
		 	);
		}
	}
	
	wb.write(os);
   
   os.flush();
   os.close();
   
   Action downloadAction = 
      new Action(
          Action.EVENT_DOWNLOAD_FROM_LOCATION,
          new Action.Parameter[]{
              new Action.Parameter(Action.PARAMETER_LOCATION, location),
              new Action.Parameter(Action.PARAMETER_NAME, "Contacts.xls"),
              new Action.Parameter(Action.PARAMETER_MIME_TYPE, "application/vnd.ms-excel")
          },
          app.getTexts().getClickToDownloadText() + " " + "Contacts.xls",
          true
      );
	  response.sendRedirect(
		"../" + view.getEncodedHRef(downloadAction)
	  );
%>
