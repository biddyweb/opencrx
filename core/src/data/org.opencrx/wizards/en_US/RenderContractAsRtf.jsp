<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: RenderContractAsRtf.jsp,v 1.14 2010/12/06 18:28:23 wfro Exp $
 * Description: RenderContractAsRtf
 * Revision:    $Revision: 1.14 $
 * Owner:       CRIXP Corp., Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/12/06 18:28:23 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2004-2009, CRIXP Corp., Switzerland
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
java.net.*,
java.sql.*,
javax.naming.Context,
javax.naming.InitialContext,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.base.exception.*,
org.openmdx.kernel.id.*,
org.opencrx.kernel.utils.rtf.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.texts.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.reports.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*,
org.openmdx.base.query.*,
org.openmdx.kernel.log.*
" %>
<%!

   public org.opencrx.kernel.document1.jmi1.DocumentFolder findDocumentFolder(
      String documentFolderName,
      org.opencrx.kernel.document1.jmi1.Segment segment,
      javax.jdo.PersistenceManager pm
   ) {
      org.opencrx.kernel.document1.cci2.DocumentFolderQuery query =
        org.opencrx.kernel.utils.Utils.getDocumentPackage(pm).createDocumentFolderQuery();
      query.name().equalTo(documentFolderName);
      Collection documentFolders = segment.getFolder(query);
      if(!documentFolders.isEmpty()) {
         return (org.opencrx.kernel.document1.jmi1.DocumentFolder)documentFolders.iterator().next();
      }
      return null;
   }

%>
<%
	request.setCharacterEncoding("UTF-8");
	ApplicationContext app = (ApplicationContext)session.getValue(WebKeys.APPLICATION_KEY);
	ViewsCache viewsCache = (ViewsCache)session.getValue(WebKeys.VIEW_CACHE_KEY_SHOW);
	String requestId =  request.getParameter(Action.PARAMETER_REQUEST_ID);
	String requestIdParam = Action.PARAMETER_REQUEST_ID + "=" + requestId;
  	String objectXri = request.getParameter(Action.PARAMETER_OBJECTXRI);
	if(app==null || objectXri==null || viewsCache.getView(requestId) == null) {
	    response.sendRedirect(
	       request.getContextPath() + "/" + WebKeys.SERVLET_NAME
	    );
    	return;
  	}
	String templateName = request.getParameter("template");
  	javax.jdo.PersistenceManager pm = app.getNewPmData();
  	Texts_1_0 texts = app.getTexts();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
  <title>openCRX - Render Contract as RTF</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <meta name="UNUSEDlabel" content="Render Contract as RTF">
  <meta name="UNUSEDtoolTip" content="Render Contract as RTF">
  <meta name="targetType" content="_self">
  <meta name="forClass" content="org:opencrx:kernel:contract1:Opportunity">
  <meta name="forClass" content="org:opencrx:kernel:contract1:Quote">
  <meta name="forClass" content="org:opencrx:kernel:contract1:SalesOrder">
  <meta name="forClass" content="org:opencrx:kernel:contract1:Invoice">
  <meta name="order" content="org:opencrx:kernel:contract1:Opportunity:renderRtf">
  <meta name="order" content="org:opencrx:kernel:contract1:Quote:renderRtf">
  <meta name="order" content="org:opencrx:kernel:contract1:SalesOrder:renderRtf">
  <meta name="order" content="org:opencrx:kernel:contract1:Invoice:renderRtf">
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <link rel='shortcut icon' href='../../images/favicon.ico' />
</head>

<%
	try {
    Codes codes = app.getCodes();

    DateFormat dateFormat = DateValue.getLocalizedDateFormatter(null, false, app);
    NumberFormat decimalFormatter = new DecimalFormat("0.00");

    org.opencrx.kernel.contract1.jmi1.AbstractContract contract = (org.opencrx.kernel.contract1.jmi1.AbstractContract)pm.getObjectById(new Path(objectXri));

    Path contractIdentity = new Path(contract.refMofId());
    String providerName = contractIdentity.get(2);
    String segmentName = contractIdentity.get(4);

    String templateFolderName = null;
    List positions = null;
    if(contract instanceof org.opencrx.kernel.contract1.jmi1.Opportunity) {
        org.opencrx.kernel.contract1.cci2.OpportunityPositionQuery query = (org.opencrx.kernel.contract1.cci2.OpportunityPositionQuery)pm.newQuery(org.opencrx.kernel.contract1.jmi1.OpportunityPosition.class);
        query.orderByLineItemNumber().ascending();
        positions = ((org.opencrx.kernel.contract1.jmi1.Opportunity)contract).getPosition(query);
        templateFolderName = "Opportunity Templates";
        if(templateName == null) {
        	templateName = "Default";
        }
    }
    else if(contract instanceof org.opencrx.kernel.contract1.jmi1.Quote) {
        org.opencrx.kernel.contract1.cci2.QuotePositionQuery query = (org.opencrx.kernel.contract1.cci2.QuotePositionQuery)pm.newQuery(org.opencrx.kernel.contract1.jmi1.QuotePosition.class);
        query.orderByLineItemNumber().ascending();
        positions = ((org.opencrx.kernel.contract1.jmi1.Quote)contract).getPosition(query);
        templateFolderName = "Quote Templates";
        if(templateName == null) {
        	templateName = "Default";
        }
    }
    else if(contract instanceof org.opencrx.kernel.contract1.jmi1.SalesOrder) {
        org.opencrx.kernel.contract1.cci2.SalesOrderPositionQuery query = (org.opencrx.kernel.contract1.cci2.SalesOrderPositionQuery)pm.newQuery(org.opencrx.kernel.contract1.jmi1.SalesOrderPosition.class);
        query.orderByLineItemNumber().ascending();
        positions = ((org.opencrx.kernel.contract1.jmi1.SalesOrder)contract).getPosition(query);
        templateFolderName = "Sales Order Templates";
        if(templateName == null) {
        	templateName = "Default";
        }
    }
    else if(contract instanceof org.opencrx.kernel.contract1.jmi1.Invoice) {
        org.opencrx.kernel.contract1.cci2.InvoicePositionQuery query = (org.opencrx.kernel.contract1.cci2.InvoicePositionQuery)pm.newQuery(org.opencrx.kernel.contract1.jmi1.InvoicePosition.class);
        query.orderByLineItemNumber().ascending();
        positions = ((org.opencrx.kernel.contract1.jmi1.Invoice)contract).getPosition(query);
        templateFolderName = "Invoice Templates";
        if(templateName == null) {
        	templateName = "Default";
        }
    }
    org.opencrx.kernel.document1.jmi1.Segment documentSegment = (org.opencrx.kernel.document1.jmi1.Segment)pm.getObjectById(
		new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/" + providerName + "/segment/" + segmentName)
	);
    org.opencrx.kernel.document1.jmi1.DocumentFolder templateFolder = findDocumentFolder(
    	templateFolderName,
		documentSegment,
		pm
	);
    org.opencrx.kernel.document1.jmi1.MediaContent template = null;
    if (templateFolder != null) {
        for(Iterator i = templateFolder.getFolderEntry().iterator(); i.hasNext(); ) {
            org.opencrx.kernel.document1.jmi1.DocumentFolderEntry entry = (org.opencrx.kernel.document1.jmi1.DocumentFolderEntry)i.next();
            org.opencrx.kernel.document1.jmi1.Document document = (org.opencrx.kernel.document1.jmi1.Document)entry.getDocument();
            if(
            	templateName.equals(document.getName()) &&
            	(document.getHeadRevision() instanceof org.opencrx.kernel.document1.jmi1.MediaContent)
            ) {
				org.opencrx.kernel.document1.jmi1.MediaContent mediaContent =
                   (org.opencrx.kernel.document1.jmi1.MediaContent)document.getHeadRevision();
				if(mediaContent.getContent() != null) {
				    template = mediaContent;
				    break;
                }
            }
        }
    }
    if(template == null) {
        Action nextAction = new ObjectReference(
			(RefObject_1_0)pm.getObjectById(new Path(objectXri)),
            app
		).getSelectObjectAction();
		response.sendRedirect(
			request.getContextPath() + "/" + nextAction.getEncodedHRef()
		);
     	return;
    }
    org.opencrx.kernel.utils.rtf.RTFTemplate document = new org.opencrx.kernel.utils.rtf.RTFTemplate();
    pm.refresh(template);
    document.readFrom(
    	new InputStreamReader(template.getContent().getContent()),
    	true
   	);
	// Positions
    int positionCount = 0;
	int nPositions = positions.size();
    for (
    	Iterator j = positions.iterator();
    	j.hasNext();
	) {
		org.opencrx.kernel.contract1.jmi1.AbstractContractPosition position = (org.opencrx.kernel.contract1.jmi1.AbstractContractPosition)j.next();
		if(positionCount < nPositions - 1) {
	       	document.appendTableRow("ContractPositions");
		}
		document.setBookmarkContent("quantity", decimalFormatter.format(position.getQuantity()), true);
  	    document.setBookmarkContent("product", app.getPortalExtension().getTitle(((org.opencrx.kernel.product1.jmi1.ConfiguredProduct)position).getProduct(), app.getCurrentLocaleAsIndex(), app.getCurrentLocaleAsString(), true, app), true);
		document.setBookmarkContent("uom", app.getPortalExtension().getTitle(position.getUom(), app.getCurrentLocaleAsIndex(), app.getCurrentLocaleAsString(), true, app), true);
		document.setBookmarkContent("pricePerUnit", decimalFormatter.format(position.getPricePerUnit()), true);
		document.setBookmarkContent("discountAmount", decimalFormatter.format(position.getDiscountAmount()), true);
		document.setBookmarkContent("salesTaxType", decimalFormatter.format(position.getSalesTaxType() == null ? BigDecimal.ZERO : position.getSalesTaxType().getRate()), true);
		document.setBookmarkContent("positionAmount", decimalFormatter.format(position.getAmount()), true);
   	    positionCount++;
    }
    org.opencrx.kernel.address1.jmi1.PostalAddressable billingAddress = null;
    org.opencrx.kernel.address1.jmi1.PostalAddressable shippingAddress = null;
    for(Iterator i = contract.getAddress().iterator(); i.hasNext(); ) {
        org.opencrx.kernel.contract1.jmi1.ContractAddress address = (org.opencrx.kernel.contract1.jmi1.ContractAddress)i.next();
        if(
            address.getUsage().contains(Short.valueOf((short)10200)) &&
            (address instanceof org.opencrx.kernel.address1.jmi1.PostalAddressable)
        ) {
            shippingAddress = (org.opencrx.kernel.address1.jmi1.PostalAddressable)address;
        }
        if(
            address.getUsage().contains(Short.valueOf((short)10000)) &&
            (address instanceof org.opencrx.kernel.address1.jmi1.PostalAddressable)
        ) {
            billingAddress = (org.opencrx.kernel.address1.jmi1.PostalAddressable)address;
        }
    }
    if(contract.getCustomer() != null) {
	    for(Iterator i = contract.getCustomer().getAddress().iterator(); i.hasNext(); ) {
	        org.opencrx.kernel.account1.jmi1.AccountAddress address = (org.opencrx.kernel.account1.jmi1.AccountAddress)i.next();
	        if(
	            address.getUsage().contains(Short.valueOf((short)10200)) &&
	            (address instanceof org.opencrx.kernel.address1.jmi1.PostalAddressable)
	        ) {
	            if(shippingAddress == null) {
	            	shippingAddress = (org.opencrx.kernel.address1.jmi1.PostalAddressable)address;
	            }
	        }
	        if(
	            address.getUsage().contains(Short.valueOf((short)10000)) &&
	            (address instanceof org.opencrx.kernel.address1.jmi1.PostalAddressable)
	        ) {
	            if(billingAddress == null) {
	            	billingAddress = (org.opencrx.kernel.address1.jmi1.PostalAddressable)address;
	            }
	        }
	    }
    }
    String billingAddressTitle = app.getPortalExtension().getTitle(billingAddress, app.getCurrentLocaleAsIndex(), app.getCurrentLocaleAsString(), true, app);
    String[] addressLines = billingAddressTitle.split("<br />");
    for(int i = 0; i < 8; i++) {
	    document.setBookmarkContent(
	   		"billingAddress_" + i,
	   		i < addressLines.length ? addressLines[i] : ""
	   	);
    }
    String shippingAddressTitle = app.getPortalExtension().getTitle(shippingAddress, app.getCurrentLocaleAsIndex(), app.getCurrentLocaleAsString(), true, app);
    addressLines = shippingAddressTitle.split("<br />");
    for(int i = 0; i < 8; i++) {
	    document.setBookmarkContent(
	   		"shippingAddress_" + i,
	   		i < addressLines.length ? addressLines[i] : ""
	   	);
    }
    document.setBookmarkContent(
       	"contractNumber",
       	contract.getContractNumber() == null ? "" : contract.getContractNumber()
   	);
	document.setBookmarkContent(
		"activeOn",
		contract.getActiveOn() == null ? "" : dateFormat.format(contract.getActiveOn())
	);
    document.setBookmarkContent(
    	"expiresOn",
    	contract.getExpiresOn() == null ? "" : dateFormat.format(contract.getExpiresOn())
   	);
    document.setBookmarkContent(
      	"description",
       	contract.getDescription() == null ? "" : contract.getDescription()
    );
    document.setBookmarkContent(
    	"contractCurrency",
    	(String)(codes.getShortText("org:opencrx:kernel:contract1:AbstractContract:contractCurrency", app.getCurrentLocaleAsIndex(), true, true).get(new Short(contract.getContractCurrency())))
    );
    document.setBookmarkContent(
    	"paymentTerms",
    	(String)(codes.getShortText("org:opencrx:kernel:contract1:AbstractContract:paymentTerms", app.getCurrentLocaleAsIndex(), true, true).get(new Short(contract.getPaymentTerms())))
    );
    document.setBookmarkContent(
		"shippingMethod",
		(String)(codes.getShortText("org:opencrx:kernel:contract1:ShippingDetail:shippingMethod", app.getCurrentLocaleAsIndex(), true, true).get(new Short(contract.getShippingMethod())))
	);
    document.setBookmarkContent(
       	"totalAmountExcludingTax",
       	contract.getTotalAmount() == null ? "" : decimalFormatter.format(contract.getTotalAmount())
    );
    document.setBookmarkContent(
           "totalSalesTax",
           contract.getTotalTaxAmount() == null ? "" : decimalFormatter.format(contract.getTotalTaxAmount())
       );
    document.setBookmarkContent(
        "totalAmount",
        contract.getTotalAmountIncludingTax() == null ? "" : decimalFormatter.format(contract.getTotalAmountIncludingTax())
    );
    // SalesRep
    if((contract.getSalesRep() != null) && (contract.getSalesRep() instanceof org.opencrx.kernel.account1.jmi1.Contact)) {
		org.opencrx.kernel.account1.jmi1.Contact salesRep = (org.opencrx.kernel.account1.jmi1.Contact)contract.getSalesRep();
		document.setBookmarkContent("salesRep", salesRep.getFullName());
		org.opencrx.kernel.account1.jmi1.EMailAddress salesRepEMailAddress = null;
		org.opencrx.kernel.account1.jmi1.PhoneNumber salesRepPhoneNumber = null;
		org.opencrx.kernel.account1.cci2.AccountAddressQuery addressFilter =
		    (org.opencrx.kernel.account1.cci2.AccountAddressQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.AccountAddress.class);
        addressFilter.thereExistsUsage().equalTo(
			new Short((short)500)
        );
        addressFilter.forAllDisabled().isFalse();
        for (
			Iterator i = salesRep.getAddress(addressFilter).iterator();
			i.hasNext();
        ) {
			org.opencrx.kernel.account1.jmi1.AccountAddress address = (org.opencrx.kernel.account1.jmi1.AccountAddress)i.next();
			if (address instanceof org.opencrx.kernel.account1.jmi1.EMailAddress) {
			    salesRepEMailAddress = (org.opencrx.kernel.account1.jmi1.EMailAddress)address;
          	}
          	if(address instanceof org.opencrx.kernel.account1.jmi1.PhoneNumber) {
          	    salesRepPhoneNumber = (org.opencrx.kernel.account1.jmi1.PhoneNumber)address;
	        }
      	}
        document.setBookmarkContent(
        	"salesRepEMail",
        	salesRepEMailAddress == null ? "" : salesRepEMailAddress.getEmailAddress()
       	);
    	document.setBookmarkContent(
    	    "salesRepTel",
    	    salesRepPhoneNumber == null ? "" : salesRepPhoneNumber.getPhoneNumberFull()
    	);
    }
    // Prepare for download
    String location = UUIDs.getGenerator().next().toString();
    String documentName = contract.getContractNumber() == null ?
        templateName :
        contract.getContractNumber();
    documentName = org.opencrx.kernel.utils.Utils.toFilename(documentName) + ".rtf";
    File f = new File(
       app.getTempFileName(location, "")
    );
    FileOutputStream os = new FileOutputStream(f);
    document.writeTo(os);
    os.flush();
    os.close();
    Action downloadAction =
      new Action(
          Action.EVENT_DOWNLOAD_FROM_LOCATION,
          new Action.Parameter[]{
              new Action.Parameter(Action.PARAMETER_LOCATION, location),
              new Action.Parameter(Action.PARAMETER_NAME, documentName),
              new Action.Parameter(Action.PARAMETER_MIME_TYPE, "text/rtf")
          },
          app.getTexts().getClickToDownloadText() + " " + documentName,
          true
      );
      response.sendRedirect(
         request.getContextPath() + "/" + downloadAction.getEncodedHRef(requestId)
      );
%>
    <body>
      Download report from <a href="../../<%= downloadAction.getEncodedHRef(requestId) %>">here</a>
    </body>
  </html>
<%
  }
  catch(Exception e) {
    SysLog.warning(e.getMessage(), e.getCause());
    // Go back to previous view
    Action nextAction = new ObjectReference(
      (RefObject_1_0)pm.getObjectById(new Path(objectXri)),
      app
    ).getSelectObjectAction();
    response.sendRedirect(
       request.getContextPath() + "/" + nextAction.getEncodedHRef()
    );
  } finally {
	  if(pm != null) {
		  pm.close();
	  }
  }
%>
